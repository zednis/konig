package io.konig.maven.project.generator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;

import org.junit.Test;

import io.konig.schemagen.maven.DataServicesConfig;
import io.konig.schemagen.maven.GoogleCloudPlatformConfig;

public class XmlSerializerTest {

	@Test
	public void test() {
		
		DataServicesConfig dataServices = new DataServicesConfig();
		dataServices.setBasedir(new File("base/foo"));
		dataServices.setInfoFile(new File("config/info.yaml"));
		
		GoogleCloudPlatformConfig config = new GoogleCloudPlatformConfig();
		
		config.setBigQueryDatasetId("datasetId");
		config.setCredentials(new File("auth/credentials.json"));
		config.setDataServices(dataServices);
		
		StringWriter buffer = new StringWriter();
		buffer.write("\n");
		
		XmlSerializer serializer = new XmlSerializer(buffer);
		serializer.setIndent(1);
		serializer.indent();
		
		
		serializer.write(config, "googleCloudPlatform");
		serializer.flush();
		
		String expected = 
			"\n" + 
			"   <googleCloudPlatform>\n" + 
			"      <bigQueryDatasetId>datasetId</bigQueryDatasetId>\n" + 
			"      <credentials>auth/credentials.json</credentials>\n" + 
			"      <dataServices>\n" + 
			"         <basedir>base/foo</basedir>\n" + 
			"         <infoFile>config/info.yaml</infoFile>\n" + 
			"      </dataServices>\n" + 
			"   </googleCloudPlatform>\n" + 
			"";
		String actual = buffer.toString().replace("\r", "");
		
		assertEquals(expected, actual);
	}

}
