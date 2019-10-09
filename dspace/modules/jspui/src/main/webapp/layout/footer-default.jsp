<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Footer for home page
  --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>

<%@ page import="org.dspace.statistics.ItemWithBitstreamVsTotalCounter" %>
<%@ page import="org.dspace.app.webui.components.ItemWithBitstreamVsTotalProcessor" %>

<%@ page import="org.dspace.analytics.googleAnalytics" %>
<%@ page import="org.dspace.app.webui.components.googlaAnalyticsProcessor" %>

<%
    String sidebar = (String) request.getAttribute("dspace.layout.sidebar");
    ItemWithBitstreamVsTotalCounter siteCount = ItemWithBitstreamVsTotalCounter.getSiteCount();
    
%>
    <%-- Right-hand side bar if appropriate --%>
<%
    if (sidebar != null)
    {
%>
    </div>
    <div class="col-md-2">
        <%= sidebar %>
    </div>
    </div>
<%
    }
%>
</div>
</main>
    <%-- Page footer --%>
    <dspace:include page="/layout/copyright.jsp" />
    <footer class="navbar-inverse navbar-bottom">
	<div id="designedby" class="container text-muted">
            <div style="float: left; padding-top: 12px;">
                <span>
                    <fmt:message key="jsp.ItemWithBitstreamVsTotalCounter.prefix" /><%= siteCount.toString() %>
                </span>
                <br>
                <span>
                    <fmt:message key="jsp.googleAnalytics.prefix" /><%= googleAnalytics.GetSessions() %>
                </span>

            </div>
            <div id="footer_feedback" class="pull-right">
                <p class="text-muted"><fmt:message key="jsp.layout.footer-default.text"/>&nbsp;-
                    <a target="_blank" href="<%= request.getContextPath() %>/feedback"><fmt:message key="jsp.layout.footer-default.feedback"/></a>
                <a href="<%= request.getContextPath() %>/htmlmap"></a></p>
                </div>
            </div>
    </footer>
    </body>
</html>
