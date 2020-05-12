package com.amu.aempoc.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class FileListModel {

	char[] alphabets = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	@Inject
	@Default(values = "/content/dam")
	protected String assetsRoot;

	@Inject
	private ResourceResolver resolver;

	@PostConstruct
	protected void init() {
		//
	}

	public char[] getAlphabets() {
		return alphabets;
	}

	public List<Resource> getAssets() {
		Resource parentRes = resolver.getResource(assetsRoot);
		List<Resource> assetsList = new ArrayList<>();
		resolver.getChildren(parentRes).forEach(resource -> {
			if (resource.isResourceType("dam:Asset")) {
				assetsList.add(resource);
			}
		});
		Collections.sort(assetsList, (Resource o1, Resource o2) -> o1.getName().compareTo(o2.getName()));
		return assetsList;
	}
}
