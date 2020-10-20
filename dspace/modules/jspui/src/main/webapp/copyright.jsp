<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Home page JSP
  -
  - Attributes:
  -    communities - Community[] all communities in DSpace
  -    recent.submissions - RecetSubmissions
  --%>

<%@page import="org.dspace.content.Bitstream"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="java.io.File" %>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Locale"%>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>
<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>
<%@ page import="org.dspace.core.I18nUtil" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.app.webui.components.RecentSubmissions" %>
<%@ page import="org.dspace.content.Community" %>
<%@ page import="org.dspace.core.ConfigurationManager" %>
<%@ page import="org.dspace.core.NewsManager" %>
<%@ page import="org.dspace.browse.ItemCounter" %>
<%@ page import="org.dspace.content.Item" %>

<%
    boolean feedEnabled = ConfigurationManager.getBooleanProperty("webui.feed.enable");
    String feedData = "NONE";
    if (feedEnabled)
    {
        feedData = "ALL:" + ConfigurationManager.getProperty("webui.feed.formats");
    }
    
%>

<dspace:layout locbar="nolink" titlekey="jsp.home.title" feedData="<%= feedData %>">

<h2>著作權相關文件</h2>
<div class="panel panel-primary">
    
    <div class="panel-heading text-center">總共 6 筆資料</div>
    <table align="center" class="table" summary="This table browses all dspace content">
        <tbody>
            <tr>
              <td>2013</td>
              <td>
                  <a href="/handle/11455/87132">電子全文下載著作權責任聲明</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-file" target="_blank" href="/bitstream/11455/87132/1/電子全文下載著作權責任聲明.pdf"></a>
              </td>
            </tr>
            <tr>
              <td>2013</td>
              <td>
                  <a href="/handle/11455/87133">國立中興大學機構典藏系統（NCHUIR）著作權聲明</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-file" target="_blank" href="/bitstream/11455/87133/1/國立中興大學機構典藏系統（NCHUIR）著作權聲明.pdf"></a>
              </td>
            </tr>
            <tr>
              <td>2013</td>
              <td>
                  <a href="/handle/11455/87134">國立中興大學圖書館對於NCHUIR著作權責任聲明</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-file" target="_blank" href="/bitstream/11455/87134/1/國立中興大學圖書館對於NCHUIR著作權責任聲明.pdf"></a>
              </td>
            </tr>
            <tr>
              <td>2008</td>
              <td>
                  <a href="/handle/11455/87135">機構典藏西文出版社授權表(清大版本)</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-link" target="_blank" href="http://nthur.lib.nthu.edu.tw/web/copyright/copyright_nthur3.pdf"></a>
              </td>
            </tr>
            <tr>
              <td>2009-06</td>
              <td>
                  <a href="/handle/11455/87136">機構典藏西文出版社授權表(台大版本)</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-link" target="_blank" href="http://ntur.lib.ntu.edu.tw/retrieve/125460"></a>
              </td>
            </tr>
            <tr>
              <td>2013</td>
              <td>
                  <a href="/handle/11455/87137">經濟部智慧財產局 智慧財產局專區</a>
              </td>
              <td>
                  <a href="/browse?type=author&amp;value=%E7%B3%BB%E7%B5%B1%E7%AE%A1%E7%90%86%E5%93%A1&amp;value_lang=zh_TW">系統管理員</a>
              </td>
              <td>
                  <a class="glyphicon glyphicon-link" target="_blank" href="http://www.tipo.gov.tw/"></a>
              </td>
            </tr>
        </tbody>
    </table>

    
    
    <div class="panel-footer text-center">
        總共 6 筆資料
    </div>
</div>

</div>

<script type="text/javascript">
  $(document).ready(function(){
    $('head title').html('NCHU Institutional Repository CRIS: 著作權相關文件')
  });
</script>
</dspace:layout>
