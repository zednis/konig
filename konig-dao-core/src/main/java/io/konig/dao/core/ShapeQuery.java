package io.konig.dao.core;

/*
 * #%L
 * Konig DAO Core
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



public class ShapeQuery {

	private String shapeId;
	private DataFilter filter;
	
	public ShapeQuery(String shapeId, DataFilter filter) {
		this.shapeId = shapeId;
		this.filter = filter;
	}

	public String getShapeId() {
		return shapeId;
	}

	public DataFilter getFilter() {
		return filter;
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {
		private String shapeId;
		private DataFilter shapeFilter;
		
		public Builder setShapeId(String shapeId) {
			this.shapeId = shapeId;
			return this;
		}
		public DataFilter getShapeFilter() {
			return shapeFilter;
		}
		
		public ShapeQuery build() {
			return new ShapeQuery(shapeId, shapeFilter);
		}
		
		public PredicateConstraint.Builder beginPredicateConstraint() {
			return new PredicateConstraint.Builder(this);
		}
		
		public void addFilter(DataFilter filter) {
			if (shapeFilter == null) {
				shapeFilter = filter;
			} else if (shapeFilter instanceof CompositeDataFilter) {
				CompositeDataFilter composite = (CompositeDataFilter) shapeFilter;
				composite.add(filter);
			} else {
				CompositeDataFilter composite = new CompositeDataFilter(CompositeOperator.AND);
				composite.add(shapeFilter);
				composite.add(filter);
				shapeFilter = composite;
			}
		}
	}
}