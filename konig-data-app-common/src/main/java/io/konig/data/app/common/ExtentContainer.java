package io.konig.data.app.common;

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


import java.io.Writer;

import org.openrdf.model.URI;

import io.konig.dao.core.ConstraintOperator;
import io.konig.dao.core.DaoException;
import io.konig.dao.core.Format;
import io.konig.dao.core.ShapeQuery;
import io.konig.dao.core.ShapeReadService;

/**
 * A container that holds all instances of a given type.
 * @author Greg McFall
 *
 */
public class ExtentContainer extends AbstractContainer {


	private ShapeReadService shapeReadService;
	private URI extentClass;
	
	public ShapeReadService getShapeReadService() {
		return shapeReadService;
	}

	public void setShapeReadService(ShapeReadService shapeReadService) {
		this.shapeReadService = shapeReadService;
	}

	public URI getExtentClass() {
		return extentClass;
	}

	public void setExtentClass(URI extentClass) {
		this.extentClass = extentClass;
	}

	@Override
	public void get(GetRequest request, DataResponse response) throws DataAppException {
		URI shapeId = request.getShapeId();
		URI individualId = request.getIndividualId();
		Writer out = response.getWriter();
		Format format = request.getFormat();
		
		ShapeQuery query = ShapeQuery.newBuilder()
				.setShapeId(shapeId.toString())
				.beginPredicateConstraint()
					.setPropertyName(DataAppConstants.ID)
					.setOperator(ConstraintOperator.EQUAL)
					.setValue(individualId.stringValue())
				.endPredicateConstraint()
				.build();
			
			try {
				shapeReadService.execute(query, out, format);
			} catch (DaoException e) {
				throw new DataAppException(e);
			}
		
	}

}