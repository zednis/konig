package io.konig.maven.project.generator;

import java.io.File;

/*
 * #%L
 * Konig Maven Project Generator
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

import io.konig.schemagen.maven.GoogleCloudPlatformConfig;
import io.konig.schemagen.maven.RdfConfig;

public class MultiProject extends MavenProjectConfig {
	
	private RdfModelGenerator rdfModel;
	private JavaModelGenerator javaModel;
	private GoogleCloudPlatformConfig googleCloudPlatform;

	public RdfModelGenerator getRdfModel() {
		return rdfModel;
	}

	public void setRdfModel(RdfModelGenerator rdfModel) {
		this.rdfModel = rdfModel;
	}
	
	public JavaModelGenerator getJavaModel() {
		return javaModel;
	}

	public void setJavaModel(JavaModelGenerator javaModel) {
		this.javaModel = javaModel;
	}

	
	public GoogleCloudPlatformConfig getGoogleCloudPlatform() {
		return googleCloudPlatform;
	}

	public void setGoogleCloudPlatform(GoogleCloudPlatformConfig googleCloudPlatform) {
		this.googleCloudPlatform = googleCloudPlatform;
	}

	public void run() throws MavenProjectGeneratorException, IOException {
		ParentProjectGenerator parent = prepare();
		parent.run();
	}
	
	public ParentProjectGenerator prepare() throws MavenProjectGeneratorException {
		ParentProjectGenerator parent = new ParentProjectGenerator();
		if (rdfModel != null) {
			rdfModel.init(this);
			setRdfSourceDir(new File(rdfModel.baseDir(), "target/generated/rdf"));
		}
		parent.add(rdfModel);
		if (googleCloudPlatform != null) {
			GoogleCloudPlatformModelGenerator gcp = 
				new GoogleCloudPlatformModelGenerator(googleCloudPlatform);
			
			parent.add(gcp);
		}
		parent.add(javaModel);
		parent.init(this);
		return parent;
	}
}
