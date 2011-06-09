/*
 * $Id: ArticleListManagedBean.java,v 1.28 2008/06/06 16:22:46 valdas Exp $
 * Created on 27.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.article.bean;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.component.html.HtmlOutputLink;

import org.apache.webdav.lib.search.CompareOperator;
import org.apache.webdav.lib.search.SearchException;
import org.apache.webdav.lib.search.SearchExpression;
import org.apache.webdav.lib.search.SearchRequest;
import org.apache.webdav.lib.search.SearchScope;
import org.apache.webdav.lib.search.expressions.CompareExpression;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.idega.block.article.business.ArticleActionURIHandler;
import com.idega.block.article.business.ArticleUtil;
import com.idega.block.article.component.ArticleItemViewer;
import com.idega.block.article.data.dao.ArticleDao;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.content.bean.ContentItemBeanComparator;
import com.idega.content.bean.ContentListViewerManagedBean;
import com.idega.content.business.ContentConstants;
import com.idega.content.business.ContentSearch;
import com.idega.content.presentation.ContentItemViewer;
import com.idega.content.presentation.ContentViewer;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.search.business.Search;
import com.idega.core.search.business.SearchResult;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideSession;
import com.idega.slide.util.IWSlideConstants;
import com.idega.user.data.User;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.expression.ELUtil;

/**
 *
 *  Last modified: $Date: 2008/06/06 16:22:46 $ by $Author: valdas $
 *
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.28 $
 */
public class ArticleListManagedBean implements ContentListViewerManagedBean {

	private List<String> categories = null;

	private String LOCALIZEDKEY_MORE = "itemviewer.more";

	private String detailsViewerPath = null;
	private String datePattern = null;
	private String resourcePath;
	private String articleItemViewerFilter = null;

	private int numberOfDaysDisplayed = 0;
	private int maxNumberOfDisplayed = -1;

	private boolean headlineAsLink=false;
	private boolean showDate = true;
	private boolean showTime = true;
	private boolean showAuthor = true;
	private boolean showCreationDate = true;
	private boolean showHeadline = true;
	private boolean showTeaser = true;
	private boolean showBody = true;
	private boolean showAllItems = false;
	private boolean identifierAutoGenerated = false;
	private Boolean showDetailsCommand = null;

	@Autowired
	private ArticleDao articleDao;

	public ArticleDao getArticleDao(){
		return this.articleDao;
	}
	/**
	 *
	 */
	public ArticleListManagedBean() {
		super();

		ELUtil.getInstance().autowire(this);
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getContentItems()
	 */
	@Override
	public List<ArticleItemBean> getContentItems() {
		try {
			List<ArticleItemBean> l = loadAllArticlesInFolder(ArticleUtil.getArticleBaseFolderPath());
			ContentItemBeanComparator c = new ContentItemBeanComparator();
			c.setReverseOrder(true);
			Collections.sort(l,c);
			return l;
		}
		catch (XmlException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<ArticleItemBean>();
	}

	/**
	 * Loads all xml files in the given folder
	 * @param folder
	 * @return List containing ArticleItemBean
	 * @throws XmlException
	 * @throws IOException
	 */
	public List<ArticleItemBean> loadAllArticlesInFolder(String folder) throws XmlException, IOException{
		IWContext iwc = CoreUtil.getIWContext();

		List<String> uris = null;
		if (iwc.getIWMainApplication().getSettings().getBoolean("load_articles_from_db", Boolean.TRUE)){
			String[] urisArray = getArticlesFromDatabase(folder, categories, iwc,getMaxNumberOfDisplayed());
			uris = ArrayUtil.isEmpty(urisArray) ? new ArrayList<String>(0) : Arrays.asList(urisArray);
		}else{
			Collection<SearchResult> results = getArticleSearcResults(folder, this.categories, iwc);
			if (results == null) {
				return new ArrayList<ArticleItemBean>();
			}
			//create uri list
			uris = new ArrayList<String>();
			for (SearchResult result: results) {
				uris.add(result.getSearchResultURI());
			}
		}
		return getArticlesByURIs(uris,iwc);
	}

	//gets articles and loads them
	private List<ArticleItemBean> getArticlesByURIs(List<String> uris, IWContext iwc) {
		List<ArticleItemBean> list = new ArrayList<ArticleItemBean>();
		String resourcePathFromRequest = iwc.getParameter(ContentViewer.PARAMETER_CONTENT_RESOURCE);
		String identifierFromRequest = iwc.getParameter(ContentConstants.CONTENT_ITEM_VIEWER_IDENTIFIER_PARAMETER);

		int count = 0;
		ArticleItemBean article = null;
		for (String uri: uris) {
			try {
				article = new ArticleItemBean();
				article.setResourcePath(uri);
				article.load();
				if (canShowArticle(article, iwc, resourcePathFromRequest, identifierFromRequest)) {
					int maxNumber = getMaxNumberOfDisplayed();
					if (maxNumber < 0 || count < maxNumber) {
						list.add(article);
						count++;
						if (count == maxNumber) {
							break;
						}
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	private boolean canShowArticle(ArticleItemBean article, IWContext iwc, String resourcePathFromRequest, String identifierFromRequest) {
		if (!hasUserRightToViewArticle(article, iwc)) {
			return false;
		}
		if (article.isDummyContentItem()) {
			return false;
		}
		if (!article.getAvailableInRequestedLanguage()) {
			return false;
		}
		if (!isShowAllItems()) {
			List<String> categories = getCategories();
			if (categories == null || categories.isEmpty()) {
				return false;
			}
		}

		if (getArticleItemViewerFilter() == null) {
			//	No identifier set as property. Will check resource paths
			if (identifierFromRequest != null) {
				//	Identifiers do not match
				return true;
			}
			//	Displaying selected article or not displaying at all
			return resourcePathFromRequest == null ? true : article.getResourcePath().equals(resourcePathFromRequest);
		}
		else {
			//	There is set identifier as property or auto generated
			if (identifierFromRequest == null) {
				//	No identifier in request, nothing to match
				return true;
			}

			if (identifierFromRequest.equals(getArticleItemViewerFilter())) {
				//	Identifiers match, checking resource paths

				if (!isIdentifierAutoGenerated()) {
					//	We can not modify current list - identifiers match, but it's NOT AUTO generated, displaying all available (for current list) articles
					article.setPartOfArticleList(true);
					return true;
				}

				//	Identifier is auto generated, proceeding checking
				if (resourcePathFromRequest == null) {
					//	No custom resource path provided, can show article
					return true;
				}
				//	Showing ONLY selected article
				return article.getResourcePath().equals(resourcePathFromRequest);
			}

			//	Identifiers do not match, can show article
			return true;
		}
	}

	private boolean hasUserRightToViewArticle(ArticleItemBean article, IWContext iwc) {
		if (iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			return true;
		}

		//	User has "little" role
		Timestamp publishedDate = article.getPublishedDate();
		if (publishedDate == null) {
			publishedDate = article.getCreationDate();
		}

		if (publishedDate == null) {
			//	Article is not published
			User currentUser = null;
			try {
				currentUser = iwc.getCurrentUser();
			} catch(Exception e) {}
			if (currentUser == null) {
				return false;
			}

			int creatorId = article.getCreatedByUserId();
			if (creatorId < 0) {
				return false;
			}

			Integer userId = null;
			try {
				userId = Integer.valueOf(currentUser.getId());
			} catch(NumberFormatException e) {
				//	Trying to get access rights by user's name and article's author name
				String articleAuthor = article.getAuthor();
				if (articleAuthor == null) {
					return false;
				}
				String userName = currentUser.getName();
				if (userName == null) {
					return false;
				}
				return articleAuthor.equals(userName);
			}

			return userId.intValue() == creatorId;
		}
		else {
			return IWTimestamp.RightNow().isLaterThanOrEquals(new IWTimestamp(publishedDate));
		}
	}

	/**
	 * @param folder
	 * @param localeString
	 * @param oldest
	 * @param categoryList
	 * @throws SearchException
	 */
	public SearchRequest getSearchRequest(String scope, Locale locale, IWTimestamp oldest, List<String> categoryList) throws SearchException {
		SearchRequest s = new SearchRequest();
		s.addSelection(IWSlideConstants.PROPERTY_DISPLAY_NAME);
		s.addSelection(IWSlideConstants.PROPERTY_CREATION_DATE);
		s.addSelection(IWSlideConstants.PROPERTY_CATEGORY);
		s.addScope(new SearchScope(scope));
		SearchExpression expression = null;

		String localeString = CoreConstants.EMPTY;
		SearchExpression namePatternExpression = s.compare(CompareOperator.LIKE, IWSlideConstants.PROPERTY_DISPLAY_NAME,"%"+localeString+".article");
		expression = namePatternExpression;

		SearchExpression creationDateExpression = null;
		if(oldest != null){
			creationDateExpression = s.compare(CompareOperator.GTE, IWSlideConstants.PROPERTY_CREATION_DATE,oldest.getDate());
			expression = s.and(expression,creationDateExpression);
		}

		List<CompareExpression> categoryExpressions = new ArrayList<CompareExpression>();
		if (categoryList != null) {
			for (Iterator<String> iter = categoryList.iterator(); iter.hasNext();) {
				String categoryName = iter.next();
				categoryExpressions.add(s.compare(CompareOperator.LIKE,IWSlideConstants.PROPERTY_CATEGORY,"%,"+categoryName+",%"));
			}
			Iterator<CompareExpression> expr = categoryExpressions.iterator();
			if(expr.hasNext()){
				SearchExpression categoryExpression = expr.next();
				while (expr.hasNext()) {
					categoryExpression = s.or(categoryExpression, expr.next());
				}
				expression = s.and(expression,categoryExpression);
			}
		}

		s.setWhereExpression(expression);
		return s;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getContentViewer()
	 */
	@Override
	public ContentItemViewer getContentViewer() {
		ArticleItemViewer viewer = new ArticleItemViewer();
		viewer.setDatePattern(getDatePattern());
		viewer.setHeadlineAsLink(getHeadlineAsLink());
		viewer.setShowDate(isShowDate());
		viewer.setShowTime(isShowTime());
		viewer.setShowAuthor(isShowAuthor());
		viewer.setShowCreationDate(isShowCreationDate());
		viewer.setShowHeadline(isShowHeadline());
		viewer.setShowTeaser(isShowTeaser());
		viewer.setShowBody(isShowBody());
		viewer.setPartOfArticlesList(true);
		viewer.setArticleItemViewerFilter(getArticleItemViewerFilter());
		viewer.setIdentifierAutoGenerated(isIdentifierAutoGenerated());
		if (isShowDetailsCommand() != null) {
			viewer.setShowDetailsCommand(isShowDetailsCommand().booleanValue());
		}

		if (this.detailsViewerPath != null) {
			viewer.setDetailsViewerPath(this.detailsViewerPath);
			HtmlOutputLink moreLink = viewer.getEmptyMoreLink();
			moreLink.getChildren().add(ArticleUtil.getBundle().getLocalizedText(this.LOCALIZEDKEY_MORE));
			viewer.setDetailsCommand(moreLink);
		}

		return viewer;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getAttachmentViewers()
	 */
	@Override
	public List getAttachmentViewers() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#setDetailsViewerPath(java.lang.String)
	 */
	@Override
	public void setDetailsViewerPath(String path) {
		this.detailsViewerPath = path;
	}

	/**
	 * @return Returns the categories.
	 */
	public List<String> getCategories() {
		return this.categories;
	}
	/**
	 * @param categories The categories to set.
	 */
	@Override
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getIWActionURIHandlerIdentifier()
	 */
	@Override
	public String getIWActionURIHandlerIdentifier() {
		return ArticleActionURIHandler.HANDLER_IDENTIFIER;
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

	/**
	 * @param headlineAsLink
	 */
	public void setHeadlineAsLink(boolean headlineAsLink) {
		this.headlineAsLink=headlineAsLink;
	}

	public boolean getHeadlineAsLink(){
		return this.headlineAsLink;
	}

	public void setDatePattern(String pattern) {
		this.datePattern = pattern;
	}

	public String getDatePattern() {
		return this.datePattern;
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

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#setResourcePath(java.lang.String)
	 */
	@Override
	public void setBaseFolderPath(String path) {
		this.resourcePath=path;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getResourcePath()
	 */
	@Override
	public String getBaseFolderPath() {
		return this.resourcePath;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#setMaxNumberOfDisplayed(int)
	 */
	@Override
	public void setMaxNumberOfDisplayed(int maxItems) {
		this.maxNumberOfDisplayed=maxItems;
	}

	/* (non-Javadoc)
	 * @see com.idega.content.bean.ContentListViewerManagedBean#getMaxNumberOfDisplayed()
	 */
	@Override
	public int getMaxNumberOfDisplayed() {
		return this.maxNumberOfDisplayed;
	}

	/**
	 * Returns Collection<SearchResult> by categories and amount of how many result to returns
	 * @param folder : String  - path to folder
	 * @param categories : List<String>
	 * @param iwc : IWContext
	 * @param firstResult : int
	 * @param maxResults : int
	 * @return Collection<SearchResult> if results were found, null otherwise
	 */
	@Transactional(readOnly = true)
	private String [] getArticlesFromDatabase(String folder,List<String> categories, IWContext iwc, int maxResults){
		String uriFrom = iwc.getParameter(ContentViewer.PARAMETER_CONTENT_RESOURCE);
		return this.getArticleDao().getUrisByCategoriesAndAmount(categories, uriFrom, 10);
	}

	public Collection<SearchResult> getArticleSearcResults(String folder, List<String> categories, IWContext iwc) {

		if (folder == null) {
			return null;
		}
		if (iwc == null) {
			iwc = IWContext.getInstance();
			if (iwc == null) {
				return null;
			}
		}

		IWTimestamp oldest = null;

		if (this.numberOfDaysDisplayed > 0) {
			oldest = IWTimestamp.RightNow();
			oldest.addDays(-this.numberOfDaysDisplayed);
		}

		IWSlideSession session = null;
		try {
			session = (IWSlideSession) IBOLookup.getSessionInstance(iwc,IWSlideSession.class);
		} catch (IBOLookupException e) {
			e.printStackTrace();
			return null;
		}
		String webDavUri = null;
		try {
			webDavUri = session.getWebdavServerURI();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (webDavUri != null) {
			if(folder.startsWith(webDavUri)){
				folder = folder.substring(webDavUri.length());
			}
			if(folder.startsWith("/")){
				folder = folder.substring(1);
			}
		}
		SearchRequest articleSearch = null;
		try {
			articleSearch = getSearchRequest(folder, iwc.getCurrentLocale(), oldest, categories);
		} catch (SearchException e) {
			e.printStackTrace();
			return null;
		}
		ContentSearch searchBusiness = new ContentSearch(iwc.getIWMainApplication());
		searchBusiness.setToUseRootAccessForSearch(true);
		searchBusiness.setToUseDescendingOrder(true);
		Search search = searchBusiness.createSearch(articleSearch);
		return search.getSearchResults();
	}

	public String getArticleItemViewerFilter() {
		return articleItemViewerFilter;
	}

	public void setArticleItemViewerFilter(String articleItemViewerFilter) {
		this.articleItemViewerFilter = articleItemViewerFilter;
	}

	@Override
	public void setViewerIdentifier(String viewerIdentifier) {
		this.setArticleItemViewerFilter(viewerIdentifier);
	}

	public boolean isShowAllItems() {
		return showAllItems;
	}

	public void setShowAllItems(boolean showAllItems) {
		this.showAllItems = showAllItems;
	}

	public boolean isIdentifierAutoGenerated() {
		return identifierAutoGenerated;
	}

	public void setIdentifierAutoGenerated(boolean identifierAutoGenerated) {
		this.identifierAutoGenerated = identifierAutoGenerated;
	}

}

