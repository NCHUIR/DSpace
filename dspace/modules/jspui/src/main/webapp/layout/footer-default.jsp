<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Footer for home page
  --%>

<%@page import="org.dspace.core.ConfigurationManager"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.dspace.eperson.EPerson"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.core.NewsManager" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.app.webui.util.LocaleUIHelper" %>
<%@ page import="org.dspace.statistics.ItemWithBitstreamVsTotalCounter" %>

<%
	String footerNews = NewsManager.readNewsFile(LocaleSupport.getLocalizedMessage(pageContext, "news-footer.html"));
    String sidebar = (String) request.getAttribute("dspace.layout.sidebar");
	String[] mlinks = new String[0];
	String mlinksConf = ConfigurationManager.getProperty("cris","navbar.cris-entities");
	if (StringUtils.isNotBlank(mlinksConf)) {
		mlinks = StringUtils.split(mlinksConf, ",");
	}
	
	boolean showCommList = ConfigurationManager.getBooleanProperty("community-list.show.all",true);
	boolean isRtl = StringUtils.isNotBlank(LocaleUIHelper.ifLtr(request, "","rtl"));
	ItemWithBitstreamVsTotalCounter siteCount = ItemWithBitstreamVsTotalCounter.getSiteCount();
%>

            <%-- Right-hand side bar if appropriate --%>
<%
    if (sidebar != null)
    {
%>
	</div>
	<div class="col-md-3">
                    <%= sidebar %>
    </div>
    </div>       
<%
    }
%>
</div>
<br/>
</main>
            <%-- Page footer --%>
            <footer class="navbar navbar-inverse navbar-bottom navbar-square" style="margin-bottom: 0 !important;">
				<style type="text/css">
					.footer-link-list {}
					.footer-link-list > a {
						color: white
					}
				</style>
             <div class="container">
	             <div class="row">
					 <div class="col-md-3 col-sm-3 footer-link-list">
						 <a style="font-size: large" href="/community-list">各單位分類列表</a><br>
						 <a style="font-size: large" href="/about/journals.jsp">校內出版品</a><br>
						 <a style="font-size: large" href="/password-login">系統登入</a><br>
					 </div>
	<div class="col-md-3 col-sm-3 footer-link-list">
	<a href="http://etds.lib.nchu.edu.tw/" target="_blank">興大電子學位論文服務</a><br>
	<a href="http://ndltd.ncl.edu.tw/" target="_blank">臺灣碩博士論文系統</a><br>
	<a href="http://www.grb.gov.tw/" target="_blank">政府研究資訊系統</a><br>
	<a href="http://twpat.tipo.gov.tw/" target="_blank">中華民國專利資訊檢索系統</a><br>
	<a href="http://www.ndltd.org/" target="_blank">NDLTD</a>
	</div>

	<div class="col-md-3 col-sm-3 footer-link-list">
	<a href="http://tair.org.tw/" target="_blank">臺灣學術機構典藏</a><br>
	<a href="http://ir.org.tw/" target="_blank">機構典藏計畫網站</a><br>
	<a href="http://www.opendoar.org/" target="_blank">OpenDOAR</a><br>
	<a href="http://roar.eprints.org/" target="_blank">ROAR</a><br>
	<a href="http://www.oclc.org/oaister/" target="_blank">OAIster</a><br>
	<a href="http://repositories.webometrics.info/" target="_blank">RWWR</a><br>
	<a href="http://www.sherpa.ac.uk/index.html" target="_blank">SHERPA</a>
	</div>
					 <div class="col-md-3 col-sm-3 footer-link-list">
					 <a href="http://www.nchu.edu.tw/" target="_blank">興大首頁</a><br>
					 <a href="http://www.lib.nchu.edu.tw/" target="_blank">興大圖書館</a><br>
					 <a href="mailto:nchuir@gmail.com" target="_blank">聯絡管理員</a>
					 </div>
	             	<div class="col-md-3 col-sm-3 footer-link-list">
						<a href="<%=request.getContextPath()%>/copyright.jsp">著作權相關文件</a><br>
						<p class="text-muted">
                    		<fmt:message key="jsp.ItemWithBitstreamVsTotalCounter.prefix" /><%= siteCount.toString() %>
                		</p>
						<br>
	             	</div>
	            </div>
				 <div class="row">
					 <%= footerNews %>
				 </div>
            </div>
			<div class="container-fluid extra-footer row">
      			<div id="footer_feedback" class="col-sm-4 pull-<%= isRtl ? "right":"left" %>">                                    
                     <a href="<%= request.getContextPath() %>/feedback"><fmt:message key="jsp.layout.footer-default.feedback"/></a>
                </div>
	           	<div id="designedby" class="col-sm-8 text-<%= isRtl ? "left": "right" %>">
            	 	<fmt:message key="jsp.layout.footer-default.text"/> - 
            	 	<fmt:message key="jsp.layout.footer-default.version-by"/> 
            	 	<a href="http://www.4science.it/en/dspace-and-dspace-cris-services/">
            	 		<img src="<%= request.getContextPath() %>/image/logo-4science-small.png"
                                    alt="Logo 4SCIENCE" height="32px"/></a>
				</div>
			</div>
	    </footer>
    </body>
</html>
