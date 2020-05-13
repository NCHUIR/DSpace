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
<dspace:layout locbar="nolink" titlekey="jsp.about.title">
<div id="content" width="95%">
    <div class="dropdown  pull-right">
      <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-expanded="true">
        快速移動
        <span class="caret"></span>
      </button>
      <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1"></ul>
    </div>
</div>
<script language="JavaScript" src="<%=request.getContextPath()%>/jscript/ajax.js" ></script>
<script language="JavaScript">
	var url_base = "<%=request.getContextPath()%>";
	var frontCoverNotFoundSrc= '/image/journals/coverNotFound.jpg';
	var journal=[
		{
			name:'興大工程學刊',
			href:'/handle/11455/82038',
			frontCoverSrc:'/image/journals/01.jpg',
			description:'原興大工程學報',
			category:'工學院'
		},
		{
			name:'中興史學',
			href:'/handle/11455/82100',
			frontCoverSrc:'/image/journals/02.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'興大中文學報',
			href:'/handle/11455/82120',
			frontCoverSrc:'/image/journals/03.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'興大人文學報',
			href:'/handle/11455/82155',
			frontCoverSrc:'/image/journals/04.jpg',
			description:'原文史學報',
			category:'文學院'
		},
		{
			name:'興大歷史學報',
			href:'/handle/11455/82208',
			frontCoverSrc:'/image/journals/05.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'中興文苑',
			href:'/handle/11455/82234',
			frontCoverSrc:'/image/journals/19.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'興史風',
			href:'/handle/11455/82242',
			frontCoverSrc:'/image/journals/20.jpg',
			description:'興大歷史學系刊',
			category:'文學院'
		},
		{
			name:'興大文風',
			href:'/handle/11455/82677',
			frontCoverSrc:'/image/journals/21.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'興大系友通訊',
			href:'/handle/11455/82271',
			frontCoverSrc:'/image/journals/22.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'中興湖文學獎',
			href:'/handle/11455/85348',
			frontCoverSrc:'/image/journals/23.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'興大人文學報*',
			href:'/handle/11455/82155',
			frontCoverSrc:'/image/journals/04.jpg',
			description:'原文史學報',
			category:'文學院'
		},
		{
			name:'第三十七屆中區中文研究所碩博士生論文發表會論文集 ',
			href:'/handle/11455/83244',
			frontCoverSrc:'/image/journals/25.jpg',
			description:'',
			category:'文學院'
		},
		{
			name:'全球政治評論',
			href:'/handle/11455/82275',
			frontCoverSrc:'/image/journals/06.jpg',
			description:'',
			category:'法政學院'
		},
		{
			name:'International Journal of Sport and Exercise Science',
			href:'/handle/11455/82321',
			frontCoverSrc:'/image/journals/07.jpg',
			description:'',
			category:'管理學院'
		},
		{
			name:'臺灣管理學刊',
			href:'/handle/11455/82328',
			frontCoverSrc:'/image/journals/08.jpg',
			description:'',
			category:'管理學院'
		},
		{
			name:'興大資訊管理學系刊',
			href:'/handle/11455/82355',
			frontCoverSrc:'',
			description:'',
			category:'管理學院'
		},
		{
			name:'圖書館館訊',
			href:'/handle/11455/82357',
			frontCoverSrc:'/image/journals/09.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'教育科學期刊',
			href:'/handle/11455/82517',
			frontCoverSrc:'/image/journals/10.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大校友',
			href:'/handle/11455/82540',
			frontCoverSrc:'/image/journals/40.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大體育',
			href:'/handle/11455/82567',
			frontCoverSrc:'/image/journals/39.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大體育學刊',
			href:'/handle/11455/82575',
			frontCoverSrc:'/image/journals/12.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大七十年',
			href:'/handle/11455/82615',
			frontCoverSrc:'/image/journals/28.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大實錄：國立中興大學九十週年校史（1919～2009）',
			href:'/handle/11455/82616',
			frontCoverSrc:'/image/journals/29.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'興大檔案中的校園變遷',
			href:'/11455/82617',
			frontCoverSrc:'/image/journals/30.jpg',
			description:'',
			category:'行政單位'
		},
		{
			name:'中興大學畜牧學報',
			href:'/handle/11455/82618',
			frontCoverSrc:'/image/journals/31.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'中興畜牧',
			href:'/handle/11455/82620',
			frontCoverSrc:'/image/journals/32.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'中華水土保持學報',
			href:'/handle/11455/82622',
			frontCoverSrc:'/image/journals/15.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'應用經濟論叢',
			href:'/handle/11455/82745',
			frontCoverSrc:'/image/journals/18.jpg',
			description:'原農業經濟半年刊',
			category:'農資單位'
		},
		{
			name:'林業研究季刊',
			href:'/handle/11455/82839',
			frontCoverSrc:'/image/journals/13.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'水土保持學報',
			href:'/handle/11455/82900',
			frontCoverSrc:'/image/journals/16.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'興大園藝學報',
			href:'/handle/11455/82996',
			frontCoverSrc:'/image/journals/38.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'農林學報',
			href:'/handle/11455/83079',
			frontCoverSrc:'/image/journals/14.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'農產運銷論叢',
			href:'/handle/11455/83209',
			frontCoverSrc:'/image/journals/33.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'動物科學系系刊',
			href:'/handle/11455/83216',
			frontCoverSrc:'/image/journals/34.jpg',
			description:'原畜牧系刊/畜產系刊',
			category:'農資單位'
		},
		{
			name:'園馥',
			href:'/handle/11455/83222',
			frontCoverSrc:'/image/journals/35.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'興大昆蟲學系系學會會刊',
			href:'/handle/11455/83235',
			frontCoverSrc:'/image/journals/36.jpg',
			description:'',
			category:'農資單位'
		},
		{
			name:'興大植病',
			href:'/handle/11455/83239',
			frontCoverSrc:'/image/journals/37.jpg',
			description:'',
			category:'農資單位'
		},
		
	];

	journalPageGenerate=function(){
		$('head title').html('NCHU Institutional Repository CRIS: 校內出版品');

		//anchor目錄放置的地方
		$('div#content').append(
			$('<ul></ul>')
				.addClass('cateMenu')
				.attr('id','cateMenu')
		);

		//如果有出現錯誤，到時候在console輸入ExceptionArray可以查詢詳細資訊
		ExceptionArray=new Array();

		//把journal一個一個塞進網頁，並進行分類
		for (var i = 0; i < journal.length; i++) {
			aJournal=$('<div></div>');
			aJournal.addClass('aJournal');
			if((journal[i].frontCoverSrc.length==0)&&(typeof(journal[i].frontCoverSrc)=='string')){
				aJournal.append($('<img/>').attr('src',url_base + frontCoverNotFoundSrc));
			}
			else
				aJournal.append('<img src="'+url_base+journal[i].frontCoverSrc+'"/>');
			aJournal.append('<h3>'+journal[i].name+'</h3>');
			aJournal.append('<p>'+journal[i].description+'</p>');
			aJournal=$('<a href="'+url_base+journal[i].href+'"></a>').prepend(aJournal);

			try{
				isNew=true;
				$('#content div.category').each(function(){
					thisCate=$(this);
					if(journal[i].category==thisCate.attr('id')){
						thisCate.append(aJournal);
						isNew=false;
					}
				});


				if(isNew){
					$('div#content')
						.append($('<div></div>')
							.addClass('category')
							.attr('id',journal[i].category)
							.prepend(
								$('<h3></h3>')
								.html(journal[i].category)
						)
						.append(aJournal)
					);

					$('div.dropdown ul.dropdown-menu').append(
						$('<li></li>')
							.addClass('cateItem')
							.append(
								$('<a></a>')
								.html(journal[i].category)
								.attr('href','#'+journal[i].category)
							)
					);

					//console.log("類別:"+journal[i].category+" added!");
				}


			}catch(e){
				console.log('Exception caught at:');
				console.log(aJournal);
				console.log('Problem:');
				console.log(e);
				console.log('see more detail in ExceptionArray');
				ExceptionArray.push(e);
				$('div#content').prepend(aJournal);
			}

		};

	}

	$(document).ready(journalPageGenerate);

</script>

<style>
	#content div.category{
		margin: 7px;
		padding: 5px;
	}

	div.category h3{
		text-align: left;
		color: brown;
		font-size: 25px;
	}

	#content div.aJournal{
		width:150px;
		display:inline-block;
		margin: 7px;
		padding: 5px;
		vertical-align: top;
	}
	#content div.aJournal:hover{
		background-color: beige;
	}
	div.aJournal h3{
		text-align: center;
		color: blueviolet;
		font-size: 15px;
	}
	div.aJournal p{
		text-align: center;
		color:black;
		font-size: 12px;
	}
	div.aJournal img{
		width:100%;

		-moz-box-shadow: 0 0 15px 1px rgba(20%,20%,40%,0.6);
		-webkit-box-shadow: 0 0 15px 1px rgba(20%,20%,40%,0.6);
		box-shadow: 0 0 15px 1px rgba(20%,20%,40%,0.6);
	}

</style>

</dspace:layout>
