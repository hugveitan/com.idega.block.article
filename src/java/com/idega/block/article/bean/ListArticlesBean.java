/*
 * $Id: ListArticlesBean.java,v 1.7 2005/01/04 15:18:18 joakim Exp $
 *
 * Copyright (C) 2004 Idega. All Rights Reserved.
 *
 * This software is the proprietary information of Idega.
 * Use is subject to license terms.
 *
 */
package com.idega.block.article.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;
import org.apache.xmlbeans.XmlException;
import com.idega.content.business.ContentUtil;
import com.idega.webface.WFPage;
import com.idega.webface.WFUtil;
import com.idega.webface.bean.WFListBean;
import com.idega.webface.model.WFDataModel;

/**
 * Bean for listing articles.   
 * <p>
 * Last modified: $Date: 2005/01/04 15:18:18 $ by $Author: joakim $
 *
 * @author Anders Lindman
 * @version $Revision: 1.7 $
 */

public class ListArticlesBean implements WFListBean, Serializable {
	
	public final static String ARTICLE_ID = "article_id";
	
	private WFDataModel _dataModel = null;
	private ActionListener _articleLinkListener = null;

	private String _id = null;
	private String _headline = null;
	private String _published = null;
	
	private Date _searchPublishedFrom = null;
	private Date _searchPublishedTo = null;
	private int _searchCategoryId = 0;
	
	private Map _allCategories = null;

	/**
	 * Default constructor.
	 */
	public ListArticlesBean() {}

	/**
	 * Constructs a new list articles bean with the specified article link listener.
	 */
	public ListArticlesBean(ActionListener l) {
		this();
		setArticleLinkListener(l);
	}
	
	/**
	 * Constructs a new list articles bean with the specified parameters. 
	 */
	public ListArticlesBean(String id, String headline, String published) {
		_id = id;
		_headline = headline;
		_published = published;
	}
		
	public String getId() { return _id; }
	public String getHeadline() { return _headline; }
	public String getPublished() { return _published; }

	public Date getSearchPublishedFrom() { return _searchPublishedFrom; }
	public Date getSearchPublishedTo() { return _searchPublishedTo; }
	public int getSearchCategoryId() { return _searchCategoryId; }

	public void setId(String s) { _id = s; }
	public void setHeadline(String s) { _headline = s; }
	public void setPublished(String s) { _published = s; }

	public void setSearchPublishedFrom(Date d) { _searchPublishedFrom = d; }
	public void setSearchPublishedTo(Date d) { _searchPublishedTo = d; }
	public void setSearchCategoryId(int id) { _searchCategoryId = id; }
	
	public ActionListener getArticleLinkListener() { return _articleLinkListener; }
	public void setArticleLinkListener(ActionListener l) { _articleLinkListener = l; }
	
	/**
	 * Returns all categories available for articles.
	 */
	public Map getCategories() {
		String bref = WFPage.CONTENT_BUNDLE + ".";
		if (_allCategories == null) {
			_allCategories = new LinkedHashMap();
			_allCategories.put(WFUtil.getValue(bref + "all_categories"),  new Integer(-1));
			_allCategories.put(WFUtil.getValue(bref + "category_public_news"), new Integer(1));
			_allCategories.put(WFUtil.getValue(bref + "category_business_news"), new Integer(2));
			_allCategories.put(WFUtil.getValue(bref + "category_company_info"), new Integer(3));
			_allCategories.put(WFUtil.getValue(bref + "category_general_info"), new Integer(4));
			_allCategories.put(WFUtil.getValue(bref + "category_it_stuff"), new Integer(5));
			_allCategories.put(WFUtil.getValue(bref + "category_press_releases"), new Integer(6));
			_allCategories.put(WFUtil.getValue(bref + "category_internal_info"), new Integer(7));
		}
		return _allCategories;
	}

	/**
	 * @see com.idega.webface.bean.WFListBean#getDataModel() 
	 */
	public DataModel getDataModel() {
		return _dataModel;
	}
	
	/**
	 * @see com.idega.webface.bean.WFListBean#setDataModel() 
	 */
	public void setDataModel(DataModel dataModel) {
		_dataModel = (WFDataModel) dataModel;
	}
	
	/**
	 * @see com.idega.webface.bean.WFListBean#updateDataModel() 
	 */
	public void updateDataModel(Integer start, Integer rows) {
		if (_dataModel == null) {
			_dataModel = new WFDataModel();
		}
		try {
//			ArticleItemBean[] articleItemBean = (ArticleItemBean[]) ArticleListBean.loadAllArticlesInFolder("/files/content").toArray(new ArticleItemBean[0]);
			ArticleItemBean[] articleItemBean = (ArticleItemBean[]) ArticleListBean.loadAllArticlesInFolder(ContentUtil.CONTENT_PATH).toArray(new ArticleItemBean[0]);
			int availableRows = articleItemBean.length;
			
			int nrOfRows = rows.intValue();
			if (nrOfRows == 0) {
				nrOfRows = availableRows;
			}
			int maxRow = Math.min(start.intValue() + nrOfRows,availableRows);
			for (int i = start.intValue(); i < maxRow; i++) {
				//TODO we don't have published in the article item bean
				String id = articleItemBean[i].getMainCategory()+"/"+articleItemBean[i].getHeadline()+".xml";
				ListArticlesBean bean = new ListArticlesBean(id, articleItemBean[i].getHeadline(), "");
				_dataModel.set(bean, i);
			}
			_dataModel.setRowCount(availableRows);
		}
		catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @see com.idega.webface.bean.WFListBean#createColumns() 
	 */
	public UIColumn[] createColumns(String var) {
		UIColumn col = new UIColumn();
		col.getChildren().add(WFUtil.getTextVB(var + ".published"));
		col.getChildren().add(WFUtil.getBreak());
		HtmlCommandLink l = WFUtil.getLinkVB(ARTICLE_ID, var + ".headline", _articleLinkListener);
		WFUtil.addParameterVB(l, "id", var + ".id");
		col.getChildren().add(l);
		col.getChildren().add(WFUtil.getBreak(2));
		
		return new UIColumn[] { col };
	}
	
	/**
	 * Generates a list result from the current bean search values. 
	 */
	public void list() {
		ListArticlesBean bean = new ListArticlesBean("100", "Headline", "--");
		_dataModel.set(bean, _dataModel.getRowCount());
	}
}
