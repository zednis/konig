package io.konig.schemagen.gcp;

/*
 * #%L
 * Konig Schema Generator
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


import io.konig.core.Vertex;
import io.konig.spreadsheet.IdMapper;

/**
 * An interface that maps a given OWL class to the Google Cloud Platform Dataset
 * that contains that class.
 * @author Greg McFall
 *
 */
public interface DatasetMapper extends IdMapper {

	/**
	 * Get the Id of the Dataset that contains the given owlClass.
	 * @param owlClass The OWL class for which a dataset will be computed.
	 * @return The datasetId for the Dataset
	 */
	String datasetForClass(Vertex owlClass);
	
}
