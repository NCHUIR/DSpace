/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
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

import org.dspace.statistics.ItemWithBitstreamVsTotalCounter;

public class ItemWithBitstreamVsTotalProcessor implements CollectionHomeProcessor, CommunityHomeProcessor, SiteHomeProcessor
{
    /** log4j category */
    private static Logger log = Logger.getLogger(ItemWithBitstreamVsTotalProcessor.class);

    @Override
    public void process(Context context, HttpServletRequest request,
            HttpServletResponse response, Community community)
            throws PluginException, AuthorizeException
    {
        process(context, request, response, (DSpaceObject) community);
    }

    @Override
    public void process(Context context, HttpServletRequest request,
            HttpServletResponse response, Collection collection)
            throws PluginException, AuthorizeException
    {
        process(context, request, response, (DSpaceObject) collection);
    }

    @Override
    public void process(Context context, HttpServletRequest request,
            HttpServletResponse response) throws PluginException,
            AuthorizeException
    {
        process(context, request, response, (DSpaceObject) null);
    }

    private void process(Context context, HttpServletRequest request,
            HttpServletResponse response, DSpaceObject scope)
    {
        ItemWithBitstreamVsTotalCounter.update(context);
    }

}
