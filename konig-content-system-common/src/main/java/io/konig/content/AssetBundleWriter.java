package io.konig.content;

/*
 * #%L
 * Konig Content System, Shared Library
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
import java.io.PrintWriter;
import java.util.List;

public class AssetBundleWriter {

	public void writeBundle(AssetBundle bundle, PrintWriter out) throws IOException {
		
		out.print("format=");
		out.print(FormatConstants.BUNDLE_1p0);
		out.print(',');
		out.print("name=");
		out.print(bundle.getKey().getName());
		out.print(',');
		out.print("version=");
		out.print(bundle.getKey().getVersion());
		out.print('\n');
		
		List<AssetMetadata> list = bundle.getMetadataList();
		
		for (AssetMetadata a : list) {
			writeMetadata(a, out);
		}
		
		out.flush();
		
	}

	private void writeMetadata(AssetMetadata a, PrintWriter out) {
		
		out.print(a.getPath());
		out.print(',');
		out.print(a.getEtag());
		out.print('\n');
		
	}
}
