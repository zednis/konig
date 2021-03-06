package io.konig.transform.rule;

/*
 * #%L
 * Konig Transform
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


import io.konig.core.io.AbstractPrettyPrintable;
import io.konig.core.io.PrettyPrintWriter;

public abstract class AbstractPropertyRule extends AbstractPrettyPrintable implements PropertyRule {
	
	protected ShapeRule container;
	protected DataChannel channel;
	protected ShapeRule nestedRule;

	public AbstractPropertyRule(DataChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public ShapeRule getContainer() {
		return container;
	}
	
	@Override
	public void setContainer(ShapeRule container) {
		this.container = container;
	}

	@Override
	public DataChannel getDataChannel() {
		return channel;
	}

	@Override
	public ShapeRule getNestedRule() {
		return nestedRule;
	}

	public void setNestedRule(ShapeRule nestedRule) {
		nestedRule.setAccessor(this);
		this.nestedRule = nestedRule;
	}


	@Override
	public void print(PrettyPrintWriter out) {
		out.beginObject(this);
		out.field("predicate", getPredicate());
		if (channel != null) {
			out.beginObjectField("channel", channel);
			out.field("name", channel.getName());
			out.endObjectField(channel);
		}
		out.field("nestedRule", nestedRule);
		printLocalFields(out);
		out.endObject();
		
	}


	@Override
	public int compareTo(PropertyRule other) {
		if (other == this) {
			return 0;
		}
		
		String channelName = channel==null ? "" : channel.getName();
		DataChannel otherChannel = other.getDataChannel();
		String otherChannelName = otherChannel == null ? "" : otherChannel.getName();
		
		int result = channelName.compareTo(otherChannelName);
		if (result == 0) {
			result = getPredicate().getLocalName().compareTo(other.getPredicate().getLocalName());
			if (result == 0) {
				result = getPredicate().stringValue().compareTo(other.getPredicate().stringValue());
			}
		}
		return result;
	}

	abstract protected void printLocalFields(PrettyPrintWriter out);


}
