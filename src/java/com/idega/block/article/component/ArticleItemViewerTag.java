/*
 * $Id: ArticleItemViewerTag.java,v 1.4 2008/02/20 15:48:11 laddi Exp $
 * Created on 21.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.article.component;

import javax.faces.component.UIComponent;

import com.idega.content.presentation.ContentItemViewerTag;

/**
 * 
 *  Last modified: $Date: 2008/02/20 15:48:11 $ by $Author: laddi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.4 $
 */
public class ArticleItemViewerTag extends ContentItemViewerTag {

	private boolean headlineAsLink = false;
	private boolean showAuthor = true;
	private boolean showCreationDate = true;
	private boolean showTeaser = true;
	private boolean showBody = true;
	private String datePattern = null;
	private boolean showDate = true;
	private boolean showTime = true;
	
	/**
	 * 
	 */
	public ArticleItemViewerTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	@Override
	public String getComponentType() {
		return "ArticleItemView";
	}

	@Override
	protected void setProperties(UIComponent component) {
		if (component instanceof ArticleItemViewer) {
			super.setProperties(component);
			ArticleItemViewer article = (ArticleItemViewer) component;
			article.setHeadlineAsLink(isHeadlineAsLink());
			article.setShowAuthor(isShowAuthor());
			article.setShowCreationDate(isShowCreationDate());
			article.setShowTeaser(isShowTeaser());
			article.setShowBody(isShowBody());
			article.setDatePattern(getDatePattern());
			article.setShowDate(isShowDate());
			article.setShowTime(isShowTime());
		}
	}
	
	@Override
	public void release() {   
		super.release();
		showAuthor = true;
		showCreationDate = true;
		datePattern = null;
	}
	
	public boolean isHeadlineAsLink() {
		return headlineAsLink;
	}

	public void setHeadlineAsLink(boolean headlineAsLink) {
		this.headlineAsLink = headlineAsLink;
	}

	public boolean isShowAuthor() {
		return showAuthor;
	}

	public void setShowAuthor(boolean showAuthor) {
		this.showAuthor = showAuthor;
	}

	public boolean isShowCreationDate() {
		return showCreationDate;
	}

	public void setShowCreationDate(boolean showCreationDate) {
		this.showCreationDate = showCreationDate;
	}
	
	public boolean isShowTeaser() {
		return showTeaser;
	}

	public void setShowTeaser(boolean showTeaser) {
		this.showTeaser = showTeaser;
	}

	public boolean isShowBody() {
		return showBody;
	}

	public void setShowBody(boolean showBody) {
		this.showBody = showBody;
	}

	public String getDatePattern() {
		return datePattern;
	}
	
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}

	public boolean isShowDate() {
		return this.showDate;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public boolean isShowTime() {
		return this.showTime;
	}
}