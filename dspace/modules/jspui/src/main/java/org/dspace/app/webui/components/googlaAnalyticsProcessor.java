package org.dspace.app.webui.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.plugin.CollectionHomeProcessor;
import org.dspace.plugin.CommunityHomeProcessor;
import org.dspace.plugin.PluginException;
import org.dspace.plugin.SiteHomeProcessor;
import org.dspace.analytics.googleAnalytics;
/**
 * Created by libuser on 2016/1/8.
 */
public class googlaAnalyticsProcessor {
    private static Logger log = Logger.getLogger(ItemWithBitstreamVsTotalProcessor.class);

    // Configs and settings
    private static String prefixForNTUR = "總瀏覽人數 : "; // default is the chinese prefix

    // Service startup
    static {
        String prefixForNTURTmp = ConfigurationManager.getProperty("googleAnalyticsProcessor.forNTURCrawler.prefix ");
        if (prefixForNTURTmp != null) prefixForNTUR = prefixForNTURTmp;
    }

    public static String getPrefixForNTUR() {
        return prefixForNTUR;
    }

    @Override
    public void process(Context context, HttpServletRequest request,
                        HttpServletResponse response) throws PluginException,
            AuthorizeException
    {
        googleAnalytics.update();
    }
}
