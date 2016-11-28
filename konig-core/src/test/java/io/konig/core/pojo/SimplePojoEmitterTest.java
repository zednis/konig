package io.konig.core.pojo;

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


import static org.junit.Assert.*;

import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;

import io.konig.core.Graph;
import io.konig.core.Vertex;
import io.konig.core.impl.MemoryGraph;
import io.konig.core.impl.MemoryNamespaceManager;
import io.konig.core.io.GraphLoadHandler;
import io.konig.core.vocab.Schema;

public class SimplePojoEmitterTest {

	@Test
	public void test()  throws Exception {
		
		Graph graph = new MemoryGraph();

		graph.setNamespaceManager(new MemoryNamespaceManager());
		graph.getNamespaceManager().add("schema", "http://schema.org/");
		
		graph.edge(Schema.name, RDF.TYPE, OWL.DATATYPEPROPERTY);
		graph.edge(Schema.address, RDF.TYPE, OWL.OBJECTPROPERTY);
		graph.edge(Schema.contactType, RDF.TYPE, RDF.PROPERTY);
		graph.edge(Schema.addressLocality, RDF.TYPE, RDF.PROPERTY);
		graph.edge(Schema.addressRegion, RDF.TYPE, RDF.PROPERTY);
		graph.edge(Schema.streetAddress, RDF.TYPE, OWL.DATATYPEPROPERTY);
		
		
		
		EmitContext context = new EmitContext(graph);
		SimplePojoEmitter emitter = new SimplePojoEmitter();
		URI personId = uri("http://example.com/alice");
		TestPerson person = new TestPerson();
		person.setId(personId);
		person.setName("Alice Jones");
		TestAddress addressPojo = new TestAddress();
		person.setAddress(addressPojo);
		addressPojo.setStreetAddress("101 Main St.");
		addressPojo.setAddressLocality("Boston");
		addressPojo.setAddressRegion("MA");
		
		Graph sink = new MemoryGraph();
		sink.setNamespaceManager(new MemoryNamespaceManager());
		emitter.emit(context, person, sink);
		
		Vertex alice = sink.getVertex(personId);
		assertTrue(alice!=null);
		
		assertEquals(alice.getValue(Schema.name), literal("Alice Jones"));
		
		Vertex address = alice.getVertex(Schema.address);
		assertEquals(address.getValue(Schema.streetAddress), literal(addressPojo.getStreetAddress()));
		
	}
	
	private Literal literal(String value) {
		return new LiteralImpl(value);
	}
	
	private URI uri(String value) {
		return new URIImpl(value);
	}

}
