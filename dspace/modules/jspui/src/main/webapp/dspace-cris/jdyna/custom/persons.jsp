<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    https://github.com/CILEA/dspace-cris/wiki/License

--%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.List"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="java.net.URLEncoder"            %>
<%@ page import="org.dspace.sort.SortOption" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.dspace.browse.BrowseItem" %>
<%@page import="org.dspace.app.webui.cris.dto.ComponentInfoDTO"%>
<%@page import="it.cilea.osd.jdyna.web.Box"%>

<%@page import="org.dspace.eperson.EPerson"%>
<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="jdynatags" prefix="dyna"%>

<style type="text/css">
  [headers="t2"]{min-width: initial;}
</style>

<c:set var="dspace.layout.head" scope="request">
	<link href="<%=request.getContextPath() %>/css/misctable.css" type="text/css" rel="stylesheet" />
</c:set>
<c:set var="root"><%=request.getContextPath()%></c:set>
<c:set var="info" value="${componentinfomap}" scope="page" />
<%
	
	Box holder = (Box)request.getAttribute("holder");
	ComponentInfoDTO info = ((Map<String, ComponentInfoDTO>)(request.getAttribute("componentinfomap"))).get(holder.getShortName());

	if (info.getItems().length > 0) {
%>
	
<div id="${holder.shortName}" class="box ${holder.collapsed?"":"expanded"}">
	<h3>
		<a href="#">${holder.title} <fmt:message
				key="jsp.layout.dspace.detail.fieldset-legend.component.boxtitle.${info[holder.shortName].type}"/>				
		</a>
	</h3>
<div>
	<p>


<!-- prepare pagination controls -->
<%
    // create the URLs accessing the previous and next search result pages
    StringBuilder sb = new StringBuilder();
	sb.append("<div align=\"center\">");
	//sb.append("Result pages:");
	
    String prevURL = info.buildPrevURL(); 
    String nextURL = info.buildNextURL();


if (info.getPagefirst() != info.getPagecurrent()) {
  sb.append(" <a class=\"pagination previous\" href=\"");
  sb.append(prevURL);
  sb.append("\"> <span class=\"glyphicon glyphicon-chevron-left\" aria-hidden=\"true\"></span> </a>");
}else{
  sb.append(" <span class=\"glyphicon glyphicon-chevron-left\" aria-hidden=\"true\"></span> ");
};

for( int q = info.getPagefirst(); q <= info.getPagelast(); q++ )
{
   	String myLink = info.buildMyLink(q);
    sb.append(" " + myLink);
} // for

if (info.getPagetotal() > info.getPagecurrent()) {
  sb.append(" <a class=\"pagination next\" href=\"");
  sb.append(nextURL);
  sb.append("\"> <span class=\"glyphicon glyphicon-chevron-right\" aria-hidden=\"true\"></span> </a>");
}else{
  sb.append(" <span class=\"glyphicon glyphicon-chevron-right\" aria-hidden=\"true\"></span> ");
}

sb.append("</div>");

%>


<div align="center" class="browse_range">

	<p align="center"><fmt:message key="jsp.search.results.results">
        <fmt:param><%=info.getStart()+1%></fmt:param>
        <fmt:param><%=info.getStart()+info.getItems().length%></fmt:param>
        <fmt:param><%=info.getTotal()%></fmt:param>
    </fmt:message></p>

</div>
<%
if (info.getPagetotal() > 1)
{
%>
<%= sb %>
<%
	}
%>
			
<form id="sortform<%= info.getType() %>" action="#<%= info.getType() %>" method="get">
	   <input id="sort_by<%= info.getType() %>" type="hidden" name="sort_by<%= info.getType() %>" value=""/>
       <input id="order<%= info.getType() %>" type="hidden" name="order<%= info.getType() %>" value="<%= info.getOrder() %>" />
	   <input type="hidden" name="open" value="<%= info.getType() %>" />
</form>	
<dspace:browselist items="<%= (BrowseItem[])info.getItems() %>" config="crisrp" sortBy="<%= new Integer(info.getSo().getNumber()).toString() %>" order="<%= info.getOrder() %>"/>

<script type="text/javascript"><!--
	var j = jQuery;
    function sortBy(sort_by, order) {
        j('#sort_by<%= info.getType() %>').val(sort_by);
        j('#order<%= info.getType() %>').val(order);
        j('#sortform<%= info.getType() %>').submit(function(e){e.preventDefault();});
        return j('#sortform<%= info.getType() %>').submit().serialize();
    }

    var url = jQuery("[role='tablist']").find('.active a').attr('href').split('?')[0];
    var page = jQuery('.pagination');

    for(var i=0; i<page.length; i++){
      var href = url+page.eq(i).attr('href');
      page.eq(i).attr('href', href);
    }
--></script>
<%-- show pagniation controls at bottom --%>
<%
	if (info.getPagetotal() > 1)
	{
%>
<%= sb %>
<%
	}
%>


</p>
</div>
</div>

<% } %>