package io.konig.schemagen.avro;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;

import io.konig.schemagen.avro.impl.SimpleAvroNamer;
import io.konig.shacl.NodeKind;
import io.konig.shacl.Shape;
import io.konig.shacl.ShapeBuilder;
import io.konig.shacl.ShapeManager;

public class AvroIdlGeneratorTest {

	@Test
	public void test() {
		
		URI personShapeId = uri("http://example.com/shapes/v1/Person");
		
		
		ShapeBuilder builder = new ShapeBuilder();
		builder.beginShape(personShapeId)
			.targetClass(uri("http://schema.org/Person"))
			.beginProperty(uri("http://schema.org/givenName"))
				.documentation("The person's given name")
				.datatype(XMLSchema.STRING)
				.maxCount(1)
				.minCount(1)
			.endProperty()
			.beginProperty(uri("http://schema.org/contactPoint"))
				.documentation("The contact point for this Person")
				.valueShape(uri("http://example.com/shapes/v1/ContactPoint"))
				.maxCount(1)
				.minCount(1)
			.endProperty()
			.beginProperty(uri("http://schema.org/birthDate"))
				.datatype(XMLSchema.DATE)
				.minCount(0)
				.maxCount(1)
			.endProperty()
			.beginProperty(uri("http://schema.org/makesOffer"))
				.valueShape(uri("http://example.com/shapes/v1/Offer"))
			.endProperty()
			.beginProperty(uri("http://schema.org/parent"))
				.nodeKind(NodeKind.IRI)
				.minCount(1)
			.endProperty()
		.endShape();
		
		ShapeManager shapeManager = builder.getShapeManager();
		Shape personShape = shapeManager.getShapeById(personShapeId);
	
		
		SimpleAvroNamer namer = new SimpleAvroNamer();
		AvroIdlGenerator generator = new AvroIdlGenerator(namer, null);
		
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		generator.generateIDL(personShape, out);
		
		String text = buffer.toString();
		System.out.println(text);
		
		assertTrue(text.contains("@namespace(\"com.example.shapes.v1\")"));
		assertTrue(text.contains("  string givenName;"));
		assertTrue(text.contains("  ContactPoint contactPoint;"));
		assertTrue(text.contains("  union {null, int} birthDate;"));
		assertTrue(text.contains("  union {null, array<Offer>} makesOffer;"));
		assertTrue(text.contains("  array<string> parent;"));
		
	}
	
	private URI uri(String value) {
		return new URIImpl(value);
	}

}
