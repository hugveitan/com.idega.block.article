/*
 * $Id: ArticleItemViewer.java,v 1.7 2005/11/03 16:06:26 tryggvil Exp $
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 */
package com.idega.block.article.component;

import java.sql.Timestamp;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import com.idega.block.article.bean.ArticleItemBean;
import com.idega.block.article.bean.ArticleListManagedBean;
import com.idega.content.bean.ContentItem;
import com.idega.content.presentation.ContentItemViewer;
import com.idega.webface.WFHtml;
import com.idega.webface.convert.WFTimestampConverter;

/**
 * Last modified: $Date: 2005/11/03 16:06:26 $ by $Author: tryggvil $
 *
 * Displays the article item
 *
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.7 $
 */
public class ArticleItemViewer extends ContentItemViewer {
	
	//constants:
	private final static String ATTRIBUTE_AUTHOR = "author";
	private final static String ATTRIBUTE_CREATION_DATE = "creation_date";
	private final static String ATTRIBUTE_HEADLINE = "headline";
	private final static String ATTRIBUTE_TEASER = "teaser";
	private final static String ATTRIBUTE_BODY = "body";
	private final static String[] ATTRIBUTE_ARRAY = new String[] {ATTRIBUTE_AUTHOR,ATTRIBUTE_CREATION_DATE,ATTRIBUTE_HEADLINE,ATTRIBUTE_TEASER,ATTRIBUTE_BODY};
	private final static String facetIdPrefix = "article_";
	private final static String styleClassPrefix = "article_";
	public final static String DEFAULT_STYLE_CLASS = styleClassPrefix + "item";
	//instance variables:
	private boolean headlineAsLink;
	
	
	public ArticleItemViewer() {
		super();
		this.setStyleClass(DEFAULT_STYLE_CLASS);
	}
	
	
	
	public String[] getViewerFieldNames(){
		return ATTRIBUTE_ARRAY;
	}
	
	/**
	 * @return Returns the facetIdPrefix.
	 */
	protected String getFacetIdPrefix() {
		return facetIdPrefix;
	}

	/**
	 * @return Returns the styleClassPrefix.
	 */
	protected String getDefaultStyleClassPrefix() {
		return styleClassPrefix;
	}
	
	protected UIComponent createFieldComponent(String attribute){
		if(ATTRIBUTE_BODY.equals(attribute)){
			return new WFHtml();
		} 
		else if(attribute.equals(ATTRIBUTE_HEADLINE)&&this.getHeadlineAsLink()){
			UIComponent link = getEmptyMoreLink();
			HtmlOutputText text = new HtmlOutputText();
			link.getChildren().add(text);
			return link;
		}
		else {
			return new HtmlOutputText();
		}
	}
	
	protected void initializeComponent(FacesContext context) {	
		super.initializeComponent(context);
		((HtmlOutputText)getFieldViewerComponent(ATTRIBUTE_CREATION_DATE)).setConverter(new WFTimestampConverter());
	}


	/**
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return (String)getValue(ATTRIBUTE_AUTHOR);
	}	
	/**
	 * @param author The author to set.
	 */
	public void setAuthor(String author) {
		setFieldLocalValue(ATTRIBUTE_AUTHOR,author);
	}

	/**
	 * @return Returns the body.
	 */
	public String getBody() {
		return (String)getValue(ATTRIBUTE_BODY);
	}
	/**
	 * @param body The body to set.
	 */
	public void setBody(String body) {
		setFieldLocalValue(ATTRIBUTE_BODY,body);
	}

	/**
	 * @return Returns the creationDate.
	 */
	public Timestamp getCreationDate() {
		return (Timestamp)getValue(ATTRIBUTE_CREATION_DATE);
	}
	/**
	 * @param creationDate The creationDate to set.
	 */
	public void setCreationDate(Timestamp creationDate) {
		setFieldLocalValue(ATTRIBUTE_CREATION_DATE,creationDate);
	}

	/**
	 * @return Returns the headline.
	 */
	public String getHeadline() {
		return (String)getValue(ATTRIBUTE_HEADLINE);
	}
	/**
	 * @param headline The headline to set.
	 */
	public void setHeadline(String headline) {
		setFieldLocalValue(ATTRIBUTE_HEADLINE,headline);
	}

	/**
	 * @return Returns the teaser.
	 */
	public String getTeaser() {
		return (String)getValue(ATTRIBUTE_TEASER);
	}
	/**
	 * @param teaser The teaser to set.
	 */
	public void setTeaser(String teaser) {
		setFieldLocalValue(ATTRIBUTE_TEASER,teaser);
	}
	
	
	public ContentItem loadContentItem(String itemResourcePath) {
		try {
			ArticleItemBean bean = new ArticleItemBean();
			bean.load(itemResourcePath);
			return bean;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public void setHeadlineAsLink(boolean asLink) {
		this.headlineAsLink = asLink;
	}

	public boolean getHeadlineAsLink() {
		return this.headlineAsLink;
	}
	
	
	/**
	 * @see javax.faces.component.UIComponentBase#saveState(javax.faces.context.FacesContext)
	 */
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = Boolean.valueOf(headlineAsLink);
		return values;
	}

	/**
	 * @see javax.faces.component.UIComponentBase#restoreState(javax.faces.context.FacesContext,
	 *      java.lang.Object)
	 */
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		headlineAsLink=((Boolean)values[1]).booleanValue();
	}
	
}
