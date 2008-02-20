<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:jsf="http://java.sun.com/jsf/core"
        xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
        xmlns:wf="http://xmlns.idega.com/com.idega.webface"
        xmlns:article="http://xmlns.idega.com/com.idega.block.article"
        xmlns:co="http://xmlns.idega.com/com.idega.content"  
version="1.2">
<jsp:directive.page contentType="text/html" pageEncoding="UTF-8"/>
	<jsf:view>
		<ws:page id="listarticles1">
			<h:form id="listarticlesform1">
				<wf:wfblock id="article_list_block" title="#{localizedStrings['com.idega.block.article']['list_articles']}" maximizedVertically="true">
					<article:ArticleListViewer id="article_list" resourcePath="/files/cms/article" detailsViewerPath="/workspace/content/article/preview" headlineAsLink="true" datePattern="dd.MM.yy" showComments="false" />
				</wf:wfblock>
			</h:form>
		</ws:page>
	</jsf:view>
</jsp:root>