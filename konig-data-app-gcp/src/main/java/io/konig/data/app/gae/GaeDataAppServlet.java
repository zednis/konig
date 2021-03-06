package io.konig.data.app.gae;

/*
 * #%L
 * Konig Data App for Google Cloud Platform
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
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

import io.konig.dao.core.ChartGeoLocationMapping;
import io.konig.dao.core.ShapeReadService;
import io.konig.data.app.common.AppEngineUtil;
import io.konig.data.app.common.DataAppServlet;
import io.konig.sql.runtime.BigQueryFusionChartService;
import io.konig.sql.runtime.BigQueryShapeReadService;
import io.konig.sql.runtime.ClasspathEntityStructureService;

public class GaeDataAppServlet extends DataAppServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected ShapeReadService createShapeReadService() throws ServletException {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(AppEngineUtil.CREDENTIALS_FILE)) {
			GoogleCredentials credentials = GoogleCredentials.fromStream(input);
			BigQuery bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
			ClasspathEntityStructureService structureService = ClasspathEntityStructureService.defaultInstance();
			configureFusionIdMappingCache(bigQuery);
			return new BigQueryShapeReadService(structureService, bigQuery);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	private void configureFusionIdMappingCache(BigQuery bigQuery) throws ServletException {
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		try {
			ChartGeoLocationMapping mapping = new BigQueryFusionChartService(bigQuery)
					.getFusionIdMapping();
			syncCache.put("FusionIdMapping", mapping);
		} catch (Exception e) {
			throw new ServletException(e);
		}

	}

}
