package io.konig.core.util;

/*
 * #%L
 * Konig Core
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


import org.openrdf.model.Namespace;

import io.konig.core.NamespaceManager;

public class NamespaceValueMap implements ValueMap {

	private NamespaceManager nsManager;
	
	public NamespaceValueMap(NamespaceManager nsManager) {
		this.nsManager = nsManager;
	}

	@Override
	public String get(String name) {
		Namespace ns = nsManager.findByPrefix(name);
		if (ns != null) {
			return ns.getName();
		}
		return null;
	}

}
