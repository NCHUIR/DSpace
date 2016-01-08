package org.dspace.analytics;

import org.apache.log4j.Logger;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dspace.core.ConfigurationManager;

/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
/**
 * Created by libuser on 2016/1/8.
 */
public class googleAnalytics implements Runnable{{
    private static Logger log = Logger.getLogger(googleAnalytics.class);

    private static final String APPLICATION_NAME = "Hello Analytics";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String KEY_FILE_LOCATION = "dspace-f4e08c603517.p12";
    private static final String SERVICE_ACCOUNT_EMAIL = "nchuir@dspace-1068.iam.gserviceaccount.com";
    private static String Sessions="";
    private static long lastUpdate ;
    private static long updateInterval = 43200000;

    static {
        lastUpdate = 0;
        updateInterval = ConfigurationManager.getIntProperty("ItemWithBitstreamVsTotalCounter.updateInterval",(int) updateInterval);
        try{
            log.info("update at init ... ");
            update();
        }catch(Exception e){
            log.error("Error at service startup");
            log.error(e.toString());
        }
    }

    public static synchronized void update(){
        log.debug("checking if update is required ...");
        long current = System.currentTimeMillis();
        log.debug(String.format(" current = %d, lastUpdate = %d, inteval = %d, updateInterval = %d", current, lastUpdate, current - lastUpdate, updateInterval));
        if((current - lastUpdate) > updateInterval){
            lastUpdate = current;
            (new Thread(new ItemWithBitstreamVsTotalCounter(context))).run();
        }
    }

    @Override
    public void run() {
        try {
            Analytics analytics = initializeAnalytics();
            String profile = getFirstProfileId(analytics);
            System.out.println("First Profile Id: "+ profile);
            printResults(getResults(analytics, profile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String GetSessions(){
        return Sessions;
    }



    private static Analytics initializeAnalytics() throws Exception {
        // Initializes an authorized analytics service object.

        // Construct a GoogleCredential object with the service account email
        // and p12 file downloaded from the developer console.
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountPrivateKeyFromP12File(new File(KEY_FILE_LOCATION))
                .setServiceAccountScopes(AnalyticsScopes.all())
                .build();

        // Construct the Analytics service object.
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }


    private static String getFirstProfileId(Analytics analytics) throws IOException {
        // Get the first view (profile) ID for the authorized user.
        String profileId = null;

        // Query for the list of all accounts associated with the service account.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            System.err.println("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query for the list of properties associated with the first account.
            Webproperties properties = analytics.management().webproperties()
                    .list(firstAccountId).execute();

            if (properties.getItems().isEmpty()) {
                System.err.println("No Webproperties found");
            } else {
                String firstWebpropertyId = properties.getItems().get(2).getId();

                // Query for the list views (profiles) associated with the property.
                Profiles profiles = analytics.management().profiles()
                        .list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    System.err.println("No views (profiles) found");
                } else {
                    // Return the first (view) profile associated with the property.
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }

    private static GaData getResults(Analytics analytics, String profileId) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.
        return analytics.data().ga()
                .get("ga:" + profileId, "2014-10-08", "today", "ga:sessions")
                .execute();
    }

    private static void printResults(GaData results) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        if (results != null && !results.getRows().isEmpty()) {
            System.out.println("View (Profile) Name: "
                    + results.getProfileInfo().getProfileName());
            Sessions = results.getRows().get(0).get(0);
            System.out.println("Total Sessions: " + Sessions);
        } else {
            System.out.println("No results found");
        }
    }
}
