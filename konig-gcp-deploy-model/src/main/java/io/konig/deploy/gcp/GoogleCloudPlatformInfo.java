package io.konig.deploy.gcp;

import java.io.File;


import io.konig.yaml.Yaml;

public class GoogleCloudPlatformInfo  {
	
	@Parameter(property="google.credentials")
	private String credentials;
	
	@Parameter(property="konig.deploy.gcp.projectId")
	private String projectId;
	
	@Parameter(property="konig.deploy.gcp.directory")
	private File directory;
	
	@Parameter(property="konig.deploy.gcp.bigquery")
	private BigQueryInfo bigquery = new BigQueryInfo();
	
	@Parameter(property="konig.deploy.gcp.cloudstorage")
	private CloudStorageInfo cloudstorage = new CloudStorageInfo();
	
	/**
	 * Get the identifier for the Google Cloud Project.
	 */
	public String getProjectId() {
		return projectId;
	}
	
	/**
	 * Set the identifier for the Google Cloud Project.
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * Get the top-level directory in the local file system where Google Cloud resources
	 * are stored.  This directory will typically contain the following sub-directories:
	 * <ul>
	 * 	<li> bigquery/data
	 * 	<li> bigquery/dataset
	 * 	<li> bigquery/schema
	 *  <li> bigquery/scripts
	 * </ul>
	 * @return
	 */
	public File getDirectory() {
		return directory;
	}
	
	

	public BigQueryInfo getBigQuery() {
		return bigquery;
	}

	public void setBigQuery(BigQueryInfo bigQuery) {
		this.bigquery = bigQuery;
	}
	
	

	/**
	 * Specify the location of the top-level directory in the local file system where
	 * Google Cloud Platform resources are stored.
	 * @param gcpDir
	 */
	public void setDirectory(File gcpDir) {
		this.directory = gcpDir;
	}

	public String toString() {
		return Yaml.toString(this);
	}

	public CloudStorageInfo getCloudstorage() {
		return cloudstorage;
	}

	public void setCloudstorage(CloudStorageInfo cloudstorage) {
		this.cloudstorage = cloudstorage;
	}
	
}