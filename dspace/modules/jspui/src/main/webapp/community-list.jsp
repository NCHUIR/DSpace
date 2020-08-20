<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>

<%--
  - Display hierarchical list of communities and collections
  -
  - Attributes to be passed in:
  -    communities         - array of communities
  -    collections.map  - Map where a keys is a community IDs (Integers) and
  -                      the value is the array of collections in that community
  -    subcommunities.map  - Map where a keys is a community IDs (Integers) and
  -                      the value is the array of subcommunities in that community
  -    admin_button - Boolean, show admin 'Create Top-Level Community' button
  --%>

<%@page import="org.dspace.content.Bitstream"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="org.dspace.app.webui.servlet.admin.EditCommunitiesServlet" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.browse.ItemCountException" %>
<%@ page import="org.dspace.browse.ItemCounter" %>
<%@ page import="org.dspace.content.Collection" %>
<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Map" %>

<%@ page import="org.dspace.statistics.ItemWithBitstreamVsTotalCounter" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%
    Community[] communities = (Community[]) request.getAttribute("communities");
    Map collectionMap = (Map) request.getAttribute("collections.map");
    Map subcommunityMap = (Map) request.getAttribute("subcommunities.map");
    Boolean admin_b = (Boolean)request.getAttribute("admin_button");
    boolean admin_button = (admin_b == null ? false : admin_b.booleanValue());
    ItemCounter ic = new ItemCounter(UIUtil.obtainContext(request));
%>

<style type="text/css">
    td{
        width: 33.3%
    }

    img.media-object{
        margin-right: 30px;
        float: left;
        height: 44px;
        width: auto;
    }

    .item{
        margin-top: 20px;
        margin-bottom: 50px;
    }

    .media-header{
        margin-bottom: 15px;
    }
</style>

<%!
    void showCommunity(String listType, String listNum, Community c, JspWriter out, HttpServletRequest request, ItemCounter ic, PageContext pageContext,
                       Map collectionMap, Map subcommunityMap) throws ItemCountException, IOException, SQLException
    {
        boolean showLogos = ConfigurationManager.getBooleanProperty("jspui.community-list.logos", true);
        out.println( "<td>" );
        out.println( "<div class=\"media-header col-md-12\">");
        Bitstream logo = c.getLogo();
        if (showLogos && logo != null)
        {
            out.println("<a href=\"" + request.getContextPath() + "/handle/"
                    + c.getHandle() + "\"><img class=\"media-object img-responsive\" src=\"" +
                    request.getContextPath() + "/retrieve/" + logo.getID() + "\" alt=\"community logo\"></a>");
        }
        out.println( "<div><h4 class=\"media-heading\"><a href=\"" + request.getContextPath() + "/handle/"
                + c.getHandle() + "\">" + c.getMetadata("name") + "</a>");
        if(ConfigurationManager.getBooleanProperty("webui.strengths.show"))
        {
            out.println(" <span class=\"badge\">" + ic.getCount(c) + "</span>");
        }
        out.println("</h4>");
        if (StringUtils.isNotBlank(c.getMetadata("short_description")))
        {
            out.println(c.getMetadata("short_description"));
            out.println(" | ");
        }
        String csCount = null;
        try {
            csCount = ItemWithBitstreamVsTotalCounter.getCommunityCount(c).toString();
        } catch (Exception e) {
            out.println("Indexing...");
        }
        if (csCount != null) {
            out.println(LocaleSupport.getLocalizedMessage(pageContext, "jsp.ItemWithBitstreamVsTotalCounter.prefix"));
            out.println(csCount);
        }
        out.println("<br></div>");
        out.println( "</div>");

        // Variables of collections and subcommunities
        Collection[] cols = (Collection[]) collectionMap.get(c.getID());
        Community[] comms = (Community[]) subcommunityMap.get(c.getID());

        int index=0, flag=0;
        if ( (cols != null && cols.length > 0) || (comms != null && comms.length > 0)) out.println("<div><table class=\"table table-bordered\"><tbody>");
        // Get the collections in this community
        if (cols != null && cols.length > 0)
        {
            //out.println("<ul class=\"" + listType + "\" id=\"" + listNum + "\">");
            for (int j = 0; j < cols.length; j++)
            {
                if(index%3==0 && flag!=0) out.println("</tr>");
                if(index%3==0) out.println("<tr>");
                out.println("<td>");
                Bitstream logoCol = cols[j].getLogo();
                if (showLogos && logoCol != null)
                {
                    out.println("<a class=\"pull-left col-md-2\" href=\"" + request.getContextPath() + "/handle/"
                            + cols[j].getHandle() + "\"><img class=\"media-object img-responsive\" src=\"" +
                            request.getContextPath() + "/retrieve/" + logoCol.getID() + "\" alt=\"collection logo\"></a>");
                }
                String cCsCount = "Indexing...";
                try {
                    cCsCount = ItemWithBitstreamVsTotalCounter.getCollectionCount(cols[j]).toString();
                } catch (Exception e) {
                    // ignore
                }
                out.println("<a href=\"" + request.getContextPath() + "/handle/" + cols[j].getHandle() + "\">" + cols[j].getMetadata("name") + " [" + cCsCount +"]</a>");
                if(ConfigurationManager.getBooleanProperty("webui.strengths.show"))
                {
                    out.println(" [" + ic.getCount(cols[j]) + "]");
                }
                out.println("</td>");
                index++;
                flag=1;
            }
        }

        // Get the sub-communities in this community
        if (comms != null && comms.length > 0)
        {
            //out.println("<ul class=\"media-list\" id=\"" + listNum + "\">");
            for (int k = 0; k < comms.length; k++)
            {
                String subListNum=listNum+"-"+k;
                String subListType="sub"+listType;
                subcommunity(subListType, subListNum, comms[k], out, request, ic, collectionMap, subcommunityMap, index);
                index++;
            }
        }
        if ( (cols != null && cols.length > 0) || (comms != null && comms.length > 0)){
            for(int i=0; i<(3-index%3)%3; i++){
                out.println("<td></td>");
            }
            out.println("</tr>");
        }
        if ( (cols != null && cols.length > 0) || (comms != null && comms.length > 0)) out.println("</tbody></table>");
        out.println("</div>");
    }

    void subcommunity(String listType, String listNum, Community c, JspWriter out, HttpServletRequest request, ItemCounter ic,
                      Map collectionMap, Map subcommunityMap, int index) throws ItemCountException, IOException, SQLException
    {
        boolean showLogos = ConfigurationManager.getBooleanProperty("jspui.community-list.logos", true);
        if(index%3==0) out.println("</tr>");
        if(index%3==0) out.println("<tr>");
        out.println("<td>");
        out.println("<li>");
        out.println("<a href=\"" + request.getContextPath() + "/handle/"
                + c.getHandle() + "\">" + c.getMetadata("name") + " [" + ItemWithBitstreamVsTotalCounter.getCommunityCount(c).toString() + "]</a>");
        if(ConfigurationManager.getBooleanProperty("webui.strengths.show"))
        {
            out.println(" <span class=\"badge\">" + ic.getCount(c) + "</span>");
        }
        if (StringUtils.isNotBlank(c.getMetadata("short_description")))
        {
            out.println(c.getMetadata("short_description"));
        }
        out.println("</li>");
        out.println("</td>");
    }
%>

<dspace:layout titlekey="jsp.community-list.title">

    <%
        if (admin_button)
        {
    %>
    <dspace:sidebar>
        <div class="panel panel-warning">
        <div class="panel-heading">
            <fmt:message key="jsp.admintools"/>
            <span class="pull-right">
                    <dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.site-admin\")%>"><fmt:message key="jsp.adminhelp"/></dspace:popup>
                </span>
        </div>
        <div class="panel-body">
            <form method="post" action="<%=request.getContextPath()%>/dspace-admin/edit-communities">
                <input type="hidden" name="action" value="<%=EditCommunitiesServlet.START_CREATE_COMMUNITY%>" />
                <input class="btn btn-default col-md-12" type="submit" name="submit" value="<fmt:message key="jsp.community-list.create.button"/>" title="<fmt:message key="jsp.community-list.create.button"/>" />
            </form>
        </div>
    </dspace:sidebar>
    <%
        }
    %>
    <h1><fmt:message key="jsp.community-list.title"/></h1>
    <p><fmt:message key="jsp.community-list.text1"/></p>

    <% if (communities.length != 0)
    {
    %>
    <%
        for (int i = 0; i < communities.length; i++)
        {
            out.println("<div class=\"item\">");
            String listNum="list"+i;
            String listType="media-list";
            showCommunity(listType, "list"+i, communities[i], out, request, ic, pageContext, collectionMap, subcommunityMap);
            out.println("</div>");
        }
    %>
    <% }
    %>

</dspace:layout>
