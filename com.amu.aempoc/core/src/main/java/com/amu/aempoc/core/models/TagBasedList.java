package com.amu.aempoc.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.query.Query;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = Resource.class)
public class TagBasedList {

	@Inject
	private ResourceResolver resourceResolver;

	@Inject
	private PageManager pageManager;

	@Inject
	@Default(values = { "" })
	private String[] tags;

	@Inject
	@Default(values = { "" })
	private String[] ignoreTags;

	@Inject
	@Default(values = "")
	private String rootPath;

	@Inject
	@Default(values = "")
	private String sortBy;

	@Inject
	@Default(values = "")
	private String sortDirection;

	private List<Page> pageList = new ArrayList<>();

	@PostConstruct
	protected void init() {
		final StringBuffer queryTag = new StringBuffer();
		for (String item : tags) {
			queryTag.append(" OR s.[cq:tags] like '" + item + "'");
		}

		final StringBuffer ignoreQueryTag = new StringBuffer();
		for (String item : ignoreTags) {
			ignoreQueryTag.append(" AND NOT s.[cq:tags] like '" + item + "'");
		}

		if (!"".equals(sortDirection)) {
			sortDirection = " ORDER BY s.[" + sortBy + "] " + sortDirection;
		}

		String query = "SELECT * FROM [cq:PageContent] AS s WHERE ISDESCENDANTNODE([" + rootPath + "]) AND ("
				+ queryTag.substring(4) + ")" + ignoreQueryTag + sortDirection;
		Iterator<Resource> resourceList = resourceResolver.findResources(query, Query.JCR_SQL2);
		resourceList.forEachRemaining(item -> {
			pageList.add(pageManager.getContainingPage(item));
		});
	}

	public List<Page> getPageList() {
		return pageList;
	}
}
