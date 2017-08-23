package io.konig.sql.runtime;

/*
 * #%L
 * Konig DAO SQL Runtime
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


import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryRequest;
import com.google.cloud.bigquery.QueryResponse;
import com.google.cloud.bigquery.QueryResult;

import io.konig.dao.core.ChartSeries;
import io.konig.dao.core.ChartSeriesFactory;
import io.konig.dao.core.ChartSeriesRequest;
import io.konig.dao.core.DaoException;
import io.konig.dao.core.DataFilter;
import io.konig.dao.core.FieldPath;
import io.konig.dao.core.ShapeQuery;

public class BigQueryChartSeriesFactory extends SqlGenerator implements ChartSeriesFactory {
	
	private BigQuery bigQuery;
	

	public BigQueryChartSeriesFactory(BigQuery bigQuery) {
		this.bigQuery = bigQuery;
	}


	@Override
	public ChartSeries createChartSeries(ChartSeriesRequest seriesRequest) throws DaoException {
		EntityStructure struct=seriesRequest.getStruct();
		FieldPath dimension = seriesRequest.getDimension();
		FieldPath measure = seriesRequest.getDimension();
		ShapeQuery query = seriesRequest.getQuery();
		
		String sql = sql(query, struct, dimension, measure);
		QueryRequest request = QueryRequest.newBuilder(sql).setUseLegacySql(false).build();
		QueryResponse response = bigQuery.query(request);
		while (!response.jobCompleted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			response = bigQuery.getQueryResults(response.getJobId());
		}
		// TODO: look for execution errors
		QueryResult result = response.getResult();
		
		String title = struct.getComment();
		return new BigQueryChartSeries(title, result, dimension.lastField(), measure.lastField());
	}


	protected String sql(ShapeQuery query, EntityStructure struct, FieldPath dimension, FieldPath measure) {
		StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append(dimension.stringValue());
		builder.append(", ");
		builder.append(measure.stringValue());
		builder.append(" FROM ");
		builder.append(struct.getName());
		
		DataFilter filter = query.getFilter();
		appendFilter(struct,builder, filter);
		return builder.toString();
	}

}