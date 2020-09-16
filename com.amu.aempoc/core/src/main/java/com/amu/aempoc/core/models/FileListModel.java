package com.amu.aempoc.core.models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

import com.day.cq.dam.api.Asset;

@Model(adaptables = Resource.class)
public class FileListModel {

	@Inject
	@Default(values = "/content/dam")
	protected String assetsRoot;

	@Inject
	private ResourceResolver resolver;

	private char[] alphabets = "abcdefghijklmnopqrstuvwxyz#".toCharArray();

	private Map<Character, SortedSet<Asset>> assetList = new HashMap<>();

	@PostConstruct
	protected void init() {
		Resource parentRes = resolver.getResource(assetsRoot);
		resolver.getChildren(parentRes).forEach(resource -> {
			if (resource.isResourceType("dam:Asset")) {
				Asset currentAsset = resource.adaptTo(Asset.class);
				String title = currentAsset.getMetadataValue("dc:title");
				char assetStartChar;
				if (title != null) {
					assetStartChar = title.toLowerCase().charAt(0);
				} else {
					assetStartChar = resource.getName().toLowerCase().charAt(0);
				}
				if (Character.isAlphabetic(assetStartChar)) {
					assetList.put(assetStartChar, getOrCreateList(assetStartChar, currentAsset));
				} else {
					assetList.put('#', getOrCreateList(assetStartChar, currentAsset));
				}
			}
		});
	}

	private SortedSet<Asset> getOrCreateList(char assetStartChar, Asset currentAsset) {
		SortedSet<Asset> list;
		if (assetList.get(assetStartChar) != null) {
			list = assetList.get(assetStartChar);
		} else {
			Comparator<Asset> assetNameComparator = Comparator.comparing(Asset::getName);
			list = new TreeSet<>(assetNameComparator);
		}
		list.add(currentAsset);
		return list;
	}

	public char[] getAlphabets() {
		return alphabets;
	}

	public Map<Character, SortedSet<Asset>> getAssetList() {
		return assetList;
	}

}
