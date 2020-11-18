package com.amu.aempoc.core.models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class })
class FileListModelTest {

	private final AemContext ctx = new AemContext();

	protected String assetsRoot;

	@Mock
	protected Resource resource;

	@Mock
	private ResourceResolver resolver;

	@BeforeEach
	void setUp() throws Exception {
		ctx.addModelsForClasses(FileListModel.class);
		ctx.load().json("/com/amu/aempoc/core/models/FileListModelTest.json", "/content");
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("dc:title", "hello");
		ctx.create().asset("/content/dam/asset-1", 25, 25, "pdf", metadata);
		metadata.put("dc:title", "harry");
		ctx.create().asset("/content/dam/asset-4", 25, 25, "pdf", metadata);
		metadata.put("dc:title", "123");
		ctx.create().asset("/content/dam/asset-2", 25, 25, "pdf", metadata);
		metadata.put("dc:title", "");
		ctx.create().asset("/content/dam/asset-3", 25, 25, "pdf", metadata);
	}

	@Test
	void testGetAlphabets() {
		char[] expected = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '#' };
		ctx.currentResource("/content/filelist");
		FileListModel fileList = ctx.currentResource().adaptTo(FileListModel.class);
		char[] actual = fileList.getAlphabets();
		assertArrayEquals(expected, actual);
	}

	@Test
	void testGetAssetList() {
		ctx.currentResource("/content/filelist");
		FileListModel fileList = ctx.currentResource().adaptTo(FileListModel.class);
		System.out.println(fileList.getAssetList().size());
		assertEquals(3, fileList.getAssetList().size());
	}

}
