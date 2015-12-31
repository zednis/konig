package io.konig.core.io;

/*
 * #%L
 * konig-core
 * %%
 * Copyright (C) 2015 Gregory McFall
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


import java.util.Enumeration;

public interface ResourceFile {
	public static final String CONTENT_LOCATION = "Content-Location";
	public static final String CONTENT_TYPE = "Content-Type";

	String asText();
	
	byte[] getEntityBody();
	String getContentLocation();
	String getContentType();

	Enumeration<String> propertyNames();
	String getProperty(String key);
	void setProperty(String key, String value);
	
	/**
	 * Create a new ResourceFile with the same properties as this one but a different entity body.
	 * @param entityBody The new entity body
	 * @return A new ResourceFile with the same properties as this one and the specified entity body.
	 */
	ResourceFile replaceContent(byte[] entityBody);
	

}
