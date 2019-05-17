package org.dspace.app.webui.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.authorize.AuthorizeException;
import org.dspace.plugin.PluginException;
import org.dspace.plugin.SiteHomeProcessor;
import org.dspace.analytics.googleAnalytics;
/**
 * Created by libuser on 2016/1/8.
 */
public class googlaAnalyticsProcessor implements SiteHomeProcessor{
    private static Logger log = Logger.getLogger(googlaAnalyticsProcessor.class);

	@Override
    public void process(Context context, HttpServletRequest request,HttpServletResponse response) throws PluginException, AuthorizeException{
        googleAnalytics.update();
    }
}
