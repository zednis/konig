package io.konig.spreadsheet;

/*
 * #%L
 * Konig Spreadsheet
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


import static org.junit.Assert.*;

import org.junit.Test;

import io.konig.core.util.BeanValueMap;
import io.konig.core.util.SimpleValueFormat;

public class BeanValueMapTest {

	@Test
	public void test() {
		
		ShapeBean bean = new ShapeBean();
		bean.setClassLocalName("Person");
		bean.setClassNamespacePrefix("schema");
		bean.setShapeLocalName("ReportingPersonShape");
		bean.setShapeNamespacePrefix("shape");
		
		BeanValueMap map = new BeanValueMap(bean);
		
		SimpleValueFormat format = new SimpleValueFormat("{classNamespacePrefix}/{classLocalName}/{shapeNamespacePrefix}/{shapeLocalName}");
		
		String actual = format.format(map);
		
		String expected = "schema/Person/shape/ReportingPersonShape";
		
		assertEquals(expected, actual);
	}

}
