/**
 * 
 */
package com.idega.block.article;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.block.article.component.ArticleItemViewer;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.cache.UIComponentCacher;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * TODO tryggvil Describe Type ArticleCacher
 * </p>
 *  Last modified: $Date: 2009/05/27 16:10:48 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.8 $
 */
public class ArticleCacher extends UIComponentCacher {
	
	public static final String BEAN_ID="articleCacher";
	protected static final String CACHE_ENABLE_KEY="article.cache.enabled";
	private IWMainApplication iwma;
	//sets the time-to-live, set to 24 hours
	protected static final int defaultTTLSeconds = 24*60*60;
	protected static final int defaultCacheSize = 10000;
	protected static final boolean eternal=false;
	protected static final boolean overFlowToDisk=true;
	
	public static ArticleCacher getInstance(IWMainApplication iwma){
		ArticleCacher instance = (ArticleCacher) iwma.getAttribute(BEAN_ID);
		if(instance==null){
			instance = new ArticleCacher();
			iwma.setAttribute(BEAN_ID,instance);
			instance.iwma=iwma;
		}
		return instance;
	}
	
	@Override
	public Map<String, String> getCacheMap() {
		IWCacheManager2 iwcm = IWCacheManager2.getInstance(this.iwma);
		
		return iwcm.getCache("article",defaultCacheSize,overFlowToDisk,eternal,defaultTTLSeconds,defaultTTLSeconds);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.UIComponentCacher#getCacheKey(javax.faces.component.UIComponent, javax.faces.context.FacesContext)
	 */
	@Override
	protected StringBuffer getCacheKeyStringBuffer(UIComponent component, FacesContext context) {
		StringBuffer keyBuffer = super.getCacheKeyStringBuffer(component, context);
		/*if(component instanceof ArticleItemViewer){
			ArticleItemViewer itemViewer = (ArticleItemViewer)component;
			String resourcePath = itemViewer.getResourcePath();
			if(resourcePath!=null){
				keyBuffer.append(resourcePath);
			}
		}
		else if(component instanceof ArticleListViewer){
			ArticleListViewer listViewer = (ArticleListViewer)component;
			String categories = listViewer.getCategories();
			if(categories!=null){
				keyBuffer.append(categories);
			}
		}*/
		return keyBuffer;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.cache.UIComponentCacher#isCacheEnbled(javax.faces.component.UIComponent, javax.faces.context.FacesContext)
	 */
	@Override
	public boolean isCacheEnbled(UIComponent component, FacesContext context) {
		
		IWMainApplication iwma = IWMainApplication.getIWMainApplication(context);
		String prop = iwma.getSettings().getProperty(CACHE_ENABLE_KEY);
		boolean defaultEnabled = true;
		if(prop!=null){
			defaultEnabled = Boolean.valueOf(prop).booleanValue();
		}
		if(defaultEnabled){
			if(component.getId()==null){
				return false;
			}
			
			if(component instanceof ArticleItemViewer){
				ArticleItemViewer viewer = (ArticleItemViewer)component;
				return viewer.isCacheEnabled();
			}
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
}
