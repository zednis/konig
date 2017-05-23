package io.konig.transform.rule;

import org.openrdf.model.URI;

import io.konig.core.io.PrettyPrintWriter;
import io.konig.shacl.PropertyConstraint;
import io.konig.shacl.Shape;

public class RenamePropertyRule extends AbstractPropertyRule {
	
	private URI focusPredicate;
	private PropertyConstraint sourceProperty;
	private int pathIndex;

	public RenamePropertyRule(
		URI focusPredicate, 
		DataChannel channel, 
		PropertyConstraint sourceProperty,
		int pathIndex
	) {
		super(channel);
		this.focusPredicate = focusPredicate;
		this.sourceProperty = sourceProperty;
		this.pathIndex = pathIndex;
	}

	public int getPathIndex() {
		return pathIndex;
	}




	public PropertyConstraint getSourceProperty() {
		return sourceProperty;
	}



	@Override
	public URI getPredicate() {
		return focusPredicate;
	}

	@Override
	public DataChannel getDataChannel() {
		return channel;
	}

	@Override
	protected void printLocalFields(PrettyPrintWriter out) {

		out.beginObjectField("sourceProperty", sourceProperty);
		out.field("predicate", sourceProperty.getPredicate());
		out.endObjectField(sourceProperty);
		
	}

}