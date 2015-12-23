package io.konig.core;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public interface Traversal {
	
	/**
	 * Filter this traversal to those elements that have the specified property.
	 * @param property The property used to filter the elements.
	 * @return A new traversal that is a filtered copy of this one.
	 */
	Traversal has(URI property);
	
	/**
	 * Get the number of elements in this traversal
	 * @return the number of elements in this traversal
	 */
	int size();
	
	
	
	/**
	 * Filter this traversal to those elements that have the specified property value
	 * @param property The property used to filter the elements
	 * @param value The target value of the property
	 * @return A new Traversal that is a filtered copy of this one.
	 */
	Traversal hasValue(URI property, Value value);
	
	/**
	 * Filter this traversal to those elements that have the specified property value
	 * @param property The property used to filter the elements
	 * @param value The string value of the property
	 * @return A new Traversal that is a filtered copy of this one.
	 */
	Traversal hasValue(URI property, String value);
	
	/**
	 * Add a property to all vertices in this traversal
	 * @param property The IRI for the property being added
	 * @param value The value of the property being added.
	 * @return This traversal
	 */
	Traversal addProperty(URI property, Value value);

	/**
	 * Add an object property to all vertices in this traversal.
	 * This is a convenience method that converts the supplied strings to URI values,
	 * and then invokes the addProperty method.
	 * @param property The IRI for the property that is being added.
	 * @param iri The IRI for the resource that is the value of the property
	 * @return This traversal
	 */
	Traversal addObject(String property, String iri);
	
	
	/**
	 * Add a literal property value to all vertices in this traversal.
	 * @param property The IRI for the property that is being added.
	 * @param value The value of the property
	 * @return This traversal
	 */
	Traversal addLiteral(String property, String value);
	

	/**
	 * Add a literal property value to all vertices in this traversal.
	 * @param property The IRI for the property that is being added.
	 * @param value The value of the property
	 * @return This traversal
	 */
	Traversal addLiteral(URI property, String value);
	
	/**
	 * Add one or more vertices to the underlying graph
	 * @param iri The vertex (or vertices) to add.
	 * @return A new Traversal containing the newly added vertices.
	 */
	Traversal addV(Resource...iri);
	
	/**
	 * Iterate over the vertices in this traversal and get the value from the first edge having the specified predicate.
	 * @param predicate The predicate for the value requested.
	 * @return The Value requested, or null if no such value exists.
	 */
	Value firstValue(URI predicate);
	
	/**
	 * Iterate over the vertices in this traversal, get the object of the first outgoing edge having the specified predicate,
	 * and return it as a Vertex.
	 * @param predicate The predicate for the Vertex requested
	 * @return The Vertex requested, or null if no such Vertex exists.
	 */
	Vertex firstVertex(URI predicate);
	
	URI firstIRI(URI predicate);
	
	/**
	 * Move from the current set of vertices in this traversal to a new set of vertices by following outgoing edges
	 * labeled by a given predicate.
	 * 
	 * @param predicate  The label for the edge to be traversed
	 * @return A new Traversal encapsulating the vertices at the other end of the outgoing edges with the specified predicate.
	 */
	Traversal out(URI predicate);
	
	/**
	 * Move from the current set of vertices in this traversal to a new set of vertices by
	 * following incoming edges labeled by a given predicate.
	 * 
	 * @param predicate The label for the edges to be traversed.
	 * @return A new Traversal encapsulating the vertices at the other end of the incoming edges.
	 */
	Traversal in(URI predicate);
	
	/**
	 * From the current set of vertices in this traversal, get the first one.
	 * @return The first vertex in this traversal.
	 */
	Vertex firstVertex();
	

}
