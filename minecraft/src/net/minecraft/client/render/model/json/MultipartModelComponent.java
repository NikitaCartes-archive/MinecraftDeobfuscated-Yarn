package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.sortme.BlockStatePropertyValuePredicateFactory;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class MultipartModelComponent {
	private final MultipartModelSelector selector;
	private final WeightedUnbakedModel model;

	public MultipartModelComponent(MultipartModelSelector multipartModelSelector, WeightedUnbakedModel weightedUnbakedModel) {
		if (multipartModelSelector == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (weightedUnbakedModel == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.selector = multipartModelSelector;
			this.model = weightedUnbakedModel;
		}
	}

	public WeightedUnbakedModel getModel() {
		return this.model;
	}

	public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory) {
		return this.selector.getPredicate(stateFactory);
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<MultipartModelComponent> {
		public MultipartModelComponent method_3535(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new MultipartModelComponent(
				this.deserializeSelectorOrDefault(jsonObject), jsonDeserializationContext.deserialize(jsonObject.get("apply"), WeightedUnbakedModel.class)
			);
		}

		private MultipartModelSelector deserializeSelectorOrDefault(JsonObject jsonObject) {
			return jsonObject.has("when") ? deserializeSelector(JsonHelper.getObject(jsonObject, "when")) : MultipartModelSelector.TRUE;
		}

		@VisibleForTesting
		static MultipartModelSelector deserializeSelector(JsonObject jsonObject) {
			Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
			if (set.isEmpty()) {
				throw new JsonParseException("No elements found in selector");
			} else if (set.size() == 1) {
				if (jsonObject.has("OR")) {
					List<MultipartModelSelector> list = (List<MultipartModelSelector>)Streams.stream(JsonHelper.getArray(jsonObject, "OR"))
						.map(jsonElement -> deserializeSelector(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new OrMultipartModelSelector(list);
				} else if (jsonObject.has("AND")) {
					List<MultipartModelSelector> list = (List<MultipartModelSelector>)Streams.stream(JsonHelper.getArray(jsonObject, "AND"))
						.map(jsonElement -> deserializeSelector(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new AndMultipartModelSelector(list);
				} else {
					return createStatePropertySelector((Entry<String, JsonElement>)set.iterator().next());
				}
			} else {
				return new AndMultipartModelSelector(
					(Iterable<? extends MultipartModelSelector>)set.stream().map(entry -> createStatePropertySelector(entry)).collect(Collectors.toList())
				);
			}
		}

		private static MultipartModelSelector createStatePropertySelector(Entry<String, JsonElement> entry) {
			return new BlockStatePropertyValuePredicateFactory((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
		}
	}
}
