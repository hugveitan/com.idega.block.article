<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:jsf="http://java.sun.com/jsf/core"
        xmlns:ws="http://xmlns.idega.com/com.idega.workspace"
        xmlns:article="http://xmlns.idega.com/com.idega.block.article"
version="1.2">
<jsp:directive.page contentType="text/html" pageEncoding="UTF-8"/>
<jsf:view>
        <ws:page id="searcharticle1">
                <h:form id="searcharticleform1">
                        <article:ArticleSearch id="article_advanced_search"/>
                </h:form>
        </ws:page>
</jsf:view>
</jsp:root>
