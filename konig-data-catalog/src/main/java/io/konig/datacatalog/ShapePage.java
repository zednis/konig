package io.konig.datacatalog;

/*
 * #%L
 * Konig Data Catalog
 * %%
 * Copyright (C) 2015 - 2017 Gregory McFall
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.konig.core.NamespaceManager;
import io.konig.core.OwlReasoner;
import io.konig.core.json.SampleJsonGenerator;
import io.konig.core.util.IOUtil;
import io.konig.core.util.StringUtil;
import io.konig.shacl.PropertyConstraint;
import io.konig.shacl.Shape;

public class ShapePage {
	private static final Logger logger = LoggerFactory.getLogger(ShapePage.class);
	private static final String SHAPE_TEMPLATE = "data-catalog/velocity/shape.vm";

	public void render(ShapeRequest request, PageResponse response) throws DataCatalogException {

		DataCatalogUtil.setSiteName(request);
		VelocityEngine engine = request.getEngine();
		VelocityContext context = request.getContext();
		Shape shape = request.getShape();
		Resource shapeId = shape.getId();
		URI shapeURI = null;
		if (shapeId instanceof URI) {
			shapeURI = (URI) shapeId;
			context.put("ShapeId", shapeURI.stringValue());
			context.put("ShapeName", shapeURI.getLocalName());
		} else {
			return;
		}
		URI targetClass = shape.getTargetClass();
		if (targetClass == null) {
			logger.warn("sh:targetClass not defined for shape <{}>", shape.getId().stringValue());
			return;
		}
		context.put("TargetClass", new Link(targetClass.getLocalName(), targetClass.stringValue()));
		request.setPageId(shapeURI);
		request.setActiveLink(null);
		
		List<PropertyInfo> propertyList = new ArrayList<>();
		context.put("PropertyList", propertyList);
		for (PropertyConstraint p : shape.getProperty()) {
			propertyList.add(new PropertyInfo(shapeURI, p, request));
		}
		DataCatalogUtil.sortProperties(propertyList);
		addJsonSamples(request);
		
		Template template = engine.getTemplate(SHAPE_TEMPLATE);
		
		PrintWriter out = response.getWriter();
		template.merge(context, out);
		out.flush();
	}

	private void addJsonSamples(ShapeRequest request) throws DataCatalogException {
		List<NamedText> list = new ArrayList<>();
		

		Shape shape = request.getShape();
		
		Resource shapeId = shape.getId();
		if (shapeId instanceof URI) {
			URI shapeURI = (URI) shapeId;
			NamespaceManager nsManager = request.getNamespaceManager();
			Namespace ns = nsManager.findByName(shapeURI.getNamespace());
			if (ns != null) {
				String prefix = ns.getPrefix();
				File srcDir = new File(request.getExamplesDir(), prefix + '/' + shapeURI.getLocalName());
				if (srcDir.exists()) {
					try {
						addCustomJson(list, srcDir);
					} catch (IOException e) {
						throw new DataCatalogException(e);
					}
					if (!list.isEmpty()) {
						request.getContext().put("JsonExamples", list);
					}
					return;
				}
			}
			
		}
		
		
		OwlReasoner reasoner = new OwlReasoner(request.getGraph());
		SampleJsonGenerator generator = new SampleJsonGenerator(reasoner);
		StringWriter out = new StringWriter();
		try {
			generator.generate(shape, out);
			String text = out.toString();
			list.add(new NamedText("Example", text));
		} catch (IOException e) {
			throw new DataCatalogException(e);
		}
		
		request.getContext().put("JsonExamples", list);
		
	}

	private void addCustomJson(List<NamedText> list, File srcDir) throws IOException {
		File[] files = srcDir.listFiles();
		for (File file : files) {
			String fileName = file.getName();
			int dot = fileName.lastIndexOf('.');
			if (dot > 0) {
				String suffix = fileName.substring(dot+1);
				if ("json".equals(suffix) || "jsonld".equals(suffix)) {
					String name = fileName.substring(0, dot);
					name = name.replace('_', ' ');
					name = StringUtil.capitalize(name);
					
					String text = IOUtil.stringContent(file);
					list.add(new NamedText(name, text));
				}
			}
		}
		
	}
	
	
}
