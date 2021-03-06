package io.konig.content.gae;

/*
 * #%L
 * Konig Content System, Google App Engine implementation
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



import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.konig.content.ContentAccessException;
import io.konig.content.ZipArchive;
import io.konig.content.ZipItem;

public class MemoryZipArchive implements ZipArchive {
	
	private ZipInputStream zipInput;

	public MemoryZipArchive(ZipInputStream zipInput) {
		this.zipInput = zipInput;
	}

	@Override
	public ZipItem nextItem() throws ContentAccessException {
		ZipEntry entry;
		try {
			entry = zipInput.getNextEntry();
			return entry==null ? null : new MemoryZipItem(zipInput, entry);
			
		} catch (IOException e) {
			throw new ContentAccessException(e);
		}
	}

}
