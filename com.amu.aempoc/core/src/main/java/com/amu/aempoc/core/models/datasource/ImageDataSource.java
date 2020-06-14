package com.amu.aempoc.core.models.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(service = { Servlet.class }, property = { "sling.servlet.resourceTypes=" + ImageDataSource.RESOURCE_TYPE,
		"sling.servlet.methods=GET", "sling.servlet.extensions=html" })
public class ImageDataSource extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 401076080096556495L;

	protected final static String RESOURCE_TYPE = "core/wcm/components/image/v2/image/datastore/htmlids";

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
		SimpleDataSource allowedTypesDataSource = new SimpleDataSource(getAllowedTypes(request).iterator());
		request.setAttribute(DataSource.class.getName(), allowedTypesDataSource);
	}

	private List<Resource> getAllowedTypes(SlingHttpServletRequest request) {
		List<Resource> options = new ArrayList<>();
		ResourceResolver resolver = request.getResourceResolver();
		Resource contentResource = resolver.getResource((String) request.getAttribute("granite.ui.form.contentpath"));
		ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
		if (policyMgr != null) {
			ContentPolicy policy = policyMgr.getPolicy(contentResource);
			if (policy != null) {
				Resource allowedTypesRes = policy.adaptTo(Resource.class).getChild("allowedTypes");
				for (Resource type : allowedTypesRes.getChildren()) {
					ValueMap props = type.adaptTo(ValueMap.class);
					if (props != null) {
						String text = props.get("title", String.class);
						String value = props.get("value", String.class);
						options.add(new TextValueResource(text, value, resolver));
					}
				}
			}
		}
		return options;
	}

	private static class TextValueResource extends SyntheticResource {

		private final String text;
		private final String value;

		protected static final String PN_VALUE = "value";

		protected static final String PN_TEXT = "text";

		private ValueMap valueMap;

		TextValueResource(String text, String value, ResourceResolver resourceResolver) {
			super(resourceResolver, "", Resource.RESOURCE_TYPE_NON_EXISTING);
			this.text = text;
			this.value = value;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
			if (type == ValueMap.class) {
				if (valueMap == null) {
					initValueMap();
				}
				return (AdapterType) valueMap;
			} else {
				return super.adaptTo(type);
			}
		}

		private void initValueMap() {
			valueMap = new ValueMapDecorator(new HashMap<String, Object>());
			valueMap.put(PN_VALUE, getValue());
			valueMap.put(PN_TEXT, getText());
		}

		protected String getText() {
			return text;
		}

		protected String getValue() {
			return value;
		}
	}
}
