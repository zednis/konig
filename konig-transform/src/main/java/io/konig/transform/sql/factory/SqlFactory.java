package io.konig.transform.sql.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;

import io.konig.core.util.TurtleElements;
import io.konig.datasource.DataSource;
import io.konig.datasource.TableDataSource;
import io.konig.sql.query.AliasExpression;
import io.konig.sql.query.ColumnExpression;
import io.konig.sql.query.ComparisonOperator;
import io.konig.sql.query.ComparisonPredicate;
import io.konig.sql.query.FromExpression;
import io.konig.sql.query.JoinExpression;
import io.konig.sql.query.OnExpression;
import io.konig.sql.query.SearchCondition;
import io.konig.sql.query.SelectExpression;
import io.konig.sql.query.TableAliasExpression;
import io.konig.sql.query.TableItemExpression;
import io.konig.sql.query.TableNameExpression;
import io.konig.sql.query.ValueExpression;
import io.konig.transform.factory.TransformBuildException;
import io.konig.transform.rule.BinaryBooleanExpression;
import io.konig.transform.rule.BooleanExpression;
import io.konig.transform.rule.DataChannel;
import io.konig.transform.rule.ExactMatchPropertyRule;
import io.konig.transform.rule.JoinStatement;
import io.konig.transform.rule.PropertyRule;
import io.konig.transform.rule.RenamePropertyRule;
import io.konig.transform.rule.ShapeRule;
import io.konig.transform.rule.TransformBinaryOperator;

public class SqlFactory {
	
	public SelectExpression selectExpression(ShapeRule shapeRule) throws TransformBuildException {
		Worker worker = new Worker();
		return worker.selectExpression(shapeRule);
	}
	
	class Worker {
		
		private Map<String, TableItemExpression> tableItemMap = new HashMap<>();

		private SelectExpression selectExpression(ShapeRule shapeRule) throws TransformBuildException {
			SelectExpression select = new SelectExpression();
			addDataChannels(select, shapeRule);
			addColumns(select, shapeRule);
			
			return select;
		}

		private void addColumns(SelectExpression select, ShapeRule shapeRule) throws TransformBuildException {
			
			List<PropertyRule> list = new ArrayList<>( shapeRule.getPropertyRules() );
			Collections.sort(list);
			
			for (PropertyRule p : list) {
				select.add(column(p));
			}
		}

		private ValueExpression column(PropertyRule p) throws TransformBuildException {

			DataChannel channel = p.getDataChannel();
			TableItemExpression tableItem = simpleTableItem(channel);
			URI predicate = p.getPredicate();
			if (p instanceof ExactMatchPropertyRule) {

				return columnExpression(tableItem, predicate);
			}
			
			if (p instanceof RenamePropertyRule) {
				RenamePropertyRule renameRule = (RenamePropertyRule) p;
				URI sourcePredicate = renameRule.getSourceProperty().getPredicate();
				ValueExpression column = columnExpression(tableItem, sourcePredicate);
				return new AliasExpression(column, predicate.getLocalName());
			}
			
			throw new TransformBuildException("Unsupported PropertyRule: " + p.getClass().getName());
		}

		private void addDataChannels(SelectExpression select, ShapeRule shapeRule) throws TransformBuildException {
			List<DataChannel> channelList = shapeRule.getChannels();
			Collections.sort(channelList);

			FromExpression from = select.getFrom();
			for (DataChannel channel : channelList) {
				TableItemExpression item = toTableItemExpression(channel);
				from.add(item);
			}
			
		}
		
		/**
		 * Get the "simple" TableItem associated with a channel.  
		 * A simple TableItem is either a TableNameExpression or a TableAliasExpression.
		 */
		private TableItemExpression simpleTableItem(DataChannel channel) throws TransformBuildException {
			TableItemExpression e = toTableItemExpression(channel);
			if (e instanceof JoinExpression) {
				JoinExpression join = (JoinExpression) e;
				e = join.getRightTable();
			}
			return e;
		}

		private TableItemExpression toTableItemExpression(DataChannel channel) throws TransformBuildException {
			
			TableItemExpression tableItem = tableItemMap.get(channel.getName());
			
			if (tableItem == null) {
				ShapeRule shapeRule = channel.getParent();
				DataSource datasource = channel.getDatasource();
				if (datasource == null) {
					StringBuilder msg = new StringBuilder();
					msg.append("Cannot create table item for ");
					msg.append(TurtleElements.resource(channel.getShape().getId()));
					msg.append(". Datasource is not specified.");
					throw new TransformBuildException(msg.toString());
				}
				if (!(datasource instanceof TableDataSource)) {

					StringBuilder msg = new StringBuilder();
					msg.append("Cannot create table item for ");
					msg.append(TurtleElements.resource(channel.getShape().getId()));
					msg.append(". Specified datasource is not an instance of TableDataSource");
					throw new TransformBuildException(msg.toString());
				}
				boolean useAlias = shapeRule.getChannels().size()>1;
				TableDataSource tableSource = (TableDataSource) datasource;
				String tableName = tableSource.getTableIdentifier();
				tableItem = new TableNameExpression(tableName);
				if (useAlias) {
					tableItem = new TableAliasExpression(tableItem, channel.getName());
				}
				
				JoinStatement join = channel.getJoinStatement();
				if (join != null) {
					TableItemExpression left = simpleTableItem(join.getLeft());
					TableItemExpression right = tableItem;
					OnExpression joinSpecification = onExpression(left, right, join);
					tableItem = new JoinExpression(left, right, joinSpecification);
				}

				tableItemMap.put(channel.getName(), tableItem);
			}
			
			
			return tableItem;
		}

		private OnExpression onExpression(TableItemExpression left, TableItemExpression right, JoinStatement join) throws TransformBuildException {
			SearchCondition searchCondition = null;
			BooleanExpression condition = join.getCondition();
			if (condition instanceof BinaryBooleanExpression) {
				BinaryBooleanExpression binary = (BinaryBooleanExpression) condition;
				
				URI leftPredicate = binary.getLeftPredicate();
				URI rightPredicate = binary.getRightPredicate();
				
				ValueExpression leftValue = columnExpression(left, leftPredicate);
				ValueExpression rightValue = columnExpression(right, rightPredicate);
				ComparisonOperator comparisonOperator = comparisonOperator(binary.getOperator());
				
				
				searchCondition = new ComparisonPredicate(comparisonOperator, leftValue, rightValue);
				
				
			} else {
				throw new TransformBuildException("Unsupported BooleanExpression: " + condition.getClass().getSimpleName() );
			}
			
			
			return new OnExpression(searchCondition);
		}

		private ValueExpression columnExpression(TableItemExpression table, URI predicate) {
			String columnName = columnName(table, predicate);
			return new ColumnExpression(columnName);
		}

		private String columnName(TableItemExpression table, URI predicate) {
			String name = predicate.getLocalName();
			if (table instanceof TableAliasExpression) {
				TableAliasExpression tableName = (TableAliasExpression) table;
				StringBuilder builder = new StringBuilder();
				builder.append(tableName.getAlias());
				builder.append('.');
				builder.append(name);
				name = builder.toString();
			}
			return name;
		}

		private ComparisonOperator comparisonOperator(TransformBinaryOperator operator) throws TransformBuildException {
			switch (operator) {
			case EQUAL :
				return ComparisonOperator.EQUALS;
				
			case NOT_EQUAL :
				return ComparisonOperator.NOT_EQUALS;
			}
			
			throw new TransformBuildException("Unsupported binary operator: " + operator);
		}

	}
	
	

}