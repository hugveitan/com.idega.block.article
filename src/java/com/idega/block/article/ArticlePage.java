/*
 * Created on Dec 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.idega.block.article;

import javax.faces.component.UIComponent;
import com.idega.block.article.component.ArticleBarBlock;
import com.idega.webface.WFPage;
import com.idega.webface.WFUtil;


/**
 * @author Joakim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArticlePage extends CMSPage {
	
	
	public ArticlePage(){
		super();
	}

	/**
	 * Creates the page content. 
	 */
	protected void createContent() {
		add(getEditPerspective());
		WFUtil.invoke(ARTICLE_ITEM_BEAN_ID, "clear");
	}
	
	/**
	 * Returns the main task bar selector. 
	 */
	protected UIComponent getMainTaskbar() {
		return getEditPerspective();
	}
	
	/**
	 * Returns the content edit perspective.
	 */
	protected UIComponent getEditPerspective() {
		//TODO (JJ) This should be changed to ARTICLE_BUNDLE
		String bref = WFPage.CONTENT_BUNDLE + ".";
		ArticleBarBlock abb = new ArticleBarBlock(bref + "article");
		abb.setId("article_block");
		return abb;
	}
}