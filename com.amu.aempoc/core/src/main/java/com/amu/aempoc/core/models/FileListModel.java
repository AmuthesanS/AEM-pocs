package com.amu.aempoc.core.models;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;

import com.day.cq.dam.api.Asset;

@Model(adaptables = Resource.class)
public class FileListModel {

	char[] alphabets = "abcdefghijklmnopqrstuvwxyz#".toCharArray();

	@Inject
	@Default(values = "/content/dam")
	protected String assetsRoot;

	@Inject
	private ResourceResolver resolver;

	public char[] getAlphabets() {
		return alphabets;
	}

	public Map<Character, Set<Asset>> getAssets() {
		Map<Character, Set<Asset>> answer = new LinkedHashMap<>();
		Resource parentRes = resolver.getResource(assetsRoot);
		for (char alphabet : alphabets) {
			SortedSet<Asset> assetsList = new TreeSet<>(new Comparator<Asset>() {
				@Override
				public int compare(Asset o1, Asset o2) {
					int ans;
					String title1 = o1.getMetadataValue("dc:title");
					String title2 = o1.getMetadataValue("dc:title");
					if (title1 != null && title2 != null) {
						ans = title1.compareTo(title2);
					} else {
						ans = o1.getName().compareTo(o2.getName());
					}
					return ans;
				}
			});
			answer.put(alphabet, assetsList);
		}
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

				Set<Asset> currentSet = answer.get(assetStartChar);
				if (currentSet != null) {
					currentSet.add(currentAsset);
				} else {
					answer.get('#').add(currentAsset);
				}
			}
		});
		return answer;
	}
}
