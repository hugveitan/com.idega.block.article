/*
 * $Id: ArticleItemViewerTag.java,v 1.7 2008/04/29 09:19:40 valdas Exp $
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
 *  Last modified: $Date: 2008/04/29 09:19:40 $ by $Author: valdas $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.7 $
 */
public class ArticleItemViewerTag extends ContentItemViewerTag {

	private boolean headlineAsLink = false;
	private boolean showAuthor = true;
	private boolean showCreationDate = true;
	private boolean showHeadline = true;
	private boolean showTeaser = true;
	private boolean showBody = true;
	private boolean showDate = true;
	private boolean showTime = true;
	private boolean showComments = false;
	
	private Boolean showDetailsCommand = null;
	
	private String datePattern = null;
	private String articleItemViewerFilter = null;
	
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
			article.setShowHeadline(isShowHeadline());
			article.setShowTeaser(isShowTeaser());
			article.setShowBody(isShowBody());
			article.setDatePattern(getDatePattern());
			article.setShowDate(isShowDate());
			article.setShowTime(isShowTime());
			if (isShowDetailsCommand() != null) {
				article.setShowDetailsCommand(isShowDetailsCommand().booleanValue());
			}
			article.setAddCommentsViewer(isShowComments());
			article.setArticleItemViewerFilter(getArticleItemViewerFilter());
		}
	}
	
	@Override
	public void release() {   
		super.release();
		showAuthor = true;
		showCreationDate = true;
		datePattern = null;
		this.articleItemViewerFilter = null;
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
	
	public boolean isShowHeadline() {
		return showHeadline;
	}

	public void setShowHeadline(boolean showHeadline) {
		this.showHeadline = showHeadline;
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

	public Boolean isShowDetailsCommand() {
		return showDetailsCommand;
	}

	public void setShowDetailsCommand(boolean showDetailsCommand) {
		this.showDetailsCommand = Boolean.valueOf(showDetailsCommand);
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

	public void setShowComments(boolean showComments) {
		this.showComments = showComments;
	}

	public boolean isShowComments() {
		return this.showComments;
	}

	public String getArticleItemViewerFilter() {
		return articleItemViewerFilter;
	}

	public void setArticleItemViewerFilter(String articleItemViewerFilter) {
		this.articleItemViewerFilter = articleItemViewerFilter;
	}

}