package io.konig.core.impl;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

import io.konig.core.Edge;
import io.konig.core.Graph;
import io.konig.core.Traversal;
import io.konig.core.Vertex;

public class VertexImpl implements Vertex {
	private transient Graph graph;
	private Resource id;
	
	private EdgeMapImpl out = new EdgeMapImpl();
	private EdgeMapImpl in = new EdgeMapImpl();
	
	private static Set<Edge> emptySet = new HashSet<Edge>();
	

	public VertexImpl(Graph graph, Resource id) {
		this.graph = graph;
		this.id = id instanceof URI ? 
			new URIVertex(id.stringValue(), this) : 
			new BNodeVertex(id.stringValue(), this);
	}

	public Resource getId() {
		return id;
	}

	public Graph getGraph() {
		return graph;
	}
	
	public void add(Edge e) {
		if (e.getSubject().equals(id)) {
			out.add(e);
		}
		if (e.getObject().equals(id)) {
			in.add(e);
		}
	}

	public Set<Edge> outProperty(URI predicate) {
		Set<Edge> result = out.get(predicate);
		return result == null ? emptySet : result;
	}

	public Set<Edge> inProperty(URI predicate) {
		Set<Edge> result = in.get(predicate);
		return result == null ? emptySet : result;
	}

	public Set<Entry<URI, Set<Edge>>> outEdges() {
		return out.entries();
	}

	public Traversal asTraversal() {
		return new TraversalImpl(this);
	}

	public void remove(Edge edge) {
		
		if (edge.getSubject().equals(id)) {
			Set<Edge> out = outProperty(edge.getPredicate());
			out.remove(edge);
		}
		if (edge.getObject().equals(id)) {
			Set<Edge> in = inProperty(edge.getPredicate());
			in.remove(edge);
		}
		
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(id.stringValue());
		buffer.append('\n');
		Set<Entry<URI,Set<Edge>>> out = outEdges();
		for (Entry<URI,Set<Edge>> entry : out) {
			URI predicate = entry.getKey();
			Set<Edge> edges = entry.getValue();
			buffer.append("  ");
			buffer.append(predicate.stringValue());
			if (edges.size()==1) {
				buffer.append("  ");
				String value = edges.iterator().next().getObject().stringValue();
				buffer.append(value);
				buffer.append('\n');
			} else {
				buffer.append('\n');
				for (Edge e : edges) {
					String value = e.getObject().stringValue();
					buffer.append("    ");
					buffer.append(value);
					buffer.append('\n');
				}
			}
		}
		
		
		return buffer.toString();
	}


}
