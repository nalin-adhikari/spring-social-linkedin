/*
 * Copyright 2014 the original author or authors.
 *
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
 */
package org.springframework.social.linkedin.api.impl.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.social.linkedin.api.Group.GroupAvailableAction;
import org.springframework.social.linkedin.api.Post.PostAvailableAction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class PostRelationMixin extends LinkedInObjectMixin {
	
	@JsonCreator
	PostRelationMixin(
			@JsonProperty("availableActions") @JsonDeserialize(using=AvailableActionDeserializer.class) List<GroupAvailableAction> availableActions, 
			@JsonProperty("isFollowing") Boolean isFollowing, 
			@JsonProperty("isLiked") Boolean isLiked) {}
	
	private static final class AvailableActionDeserializer extends JsonDeserializer<List<PostAvailableAction>>  {
		public List<PostAvailableAction> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new LinkedInModule());
			jp.setCodec(mapper);
			List<PostAvailableAction> actions = new ArrayList<PostAvailableAction>();
			if(jp.hasCurrentToken()) {
				JsonNode dataNode = jp.readValueAs(JsonNode.class).get("values");
				if (dataNode != null) {
					for (JsonNode d : dataNode) {
						String s = d.path("code").textValue();
						if (s != null) {
							actions.add(PostAvailableAction.valueOf(s.replace('-', '_').toUpperCase()));
						}
					}
				}
			}
			return actions;
		}
	}
	
}
