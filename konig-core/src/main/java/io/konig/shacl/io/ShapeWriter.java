package io.konig.shacl.io;

/*
 * #%L
 * Konig Core
 * %%
 * Copyright (C) 2015 - 2016 Gregory McFall
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
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;

import io.konig.activity.Activity;
import io.konig.core.Graph;
import io.konig.core.NamespaceManager;
import io.konig.core.impl.CompositeLocalNameService;
import io.konig.core.impl.MemoryGraph;
import io.konig.core.impl.RdfUtil;
import io.konig.core.impl.SimpleLocalNameService;
import io.konig.core.io.FileGetter;
import io.konig.core.pojo.EmitContext;
import io.konig.core.pojo.SimplePojoEmitter;
import io.konig.shacl.Shape;

public class ShapeWriter {

	public void emitShape(Shape shape, Graph graph) {
		SimplePojoEmitter emitter = SimplePojoEmitter.getInstance();
		CompositeLocalNameService nameService = new CompositeLocalNameService(
			SimpleLocalNameService.getDefaultInstance(), graph);
		
		EmitContext context = new EmitContext(graph);
		context.setLocalNameService(nameService);
		emitter.emit(context, shape, graph);
	}
	
	public void writeTurtle(NamespaceManager nsManager, Shape shape, Writer writer) throws RDFHandlerException, IOException {
		MemoryGraph graph = new MemoryGraph();
		graph.setNamespaceManager(nsManager);
		
		emitShape(shape, graph);
		
		RdfUtil.prettyPrintTurtle(nsManager, graph, writer);
		
	}
	
	public void writeTurtle(NamespaceManager nsManager, Shape shape, File file) throws RDFHandlerException, IOException {

		MemoryGraph graph = new MemoryGraph();
		graph.setNamespaceManager(nsManager);
		
		emitShape(shape, graph);
		
		RdfUtil.prettyPrintTurtle(nsManager, graph, file);
	}
	
	/**
	 * Write shapes that were generated by specific processes. 
	 * @param shapeList  The collection of shapes from which generated shapes will be selected
	 * @param fileGetter The FileGetter that provides the File to which any given Shape should be written.
	 * @param activityWhiteList The specific processes for which shapes will be written.
	 * @throws IOException 
	 * @throws RDFHandlerException 
	 */
	public void writeGeneratedShapes(
		NamespaceManager nsManager, 
		Collection<Shape> shapeList, 
		FileGetter fileGetter,
		Set<URI> activityWhitelist) throws RDFHandlerException, IOException {
		for (Shape shape : shapeList) {
			Resource resource = shape.getId();
			if (resource instanceof URI) {

				Activity prov = shape.getWasGeneratedBy();
				if (prov!=null && activityWhitelist.contains(prov.getType())) {
					URI uri = (URI) resource;
					File file = fileGetter.getFile(uri);
					if (file != null) {
						writeTurtle(nsManager, shape, file);
					}
				}
				
				
			}
		}
	}

}
