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
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class MultipartModelComponent {
	private final MultipartModelSelector selector;
	private final WeightedUnbakedModel model;

	public MultipartModelComponent(MultipartModelSelector selector, WeightedUnbakedModel model) {
		if (selector == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (model == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.selector = selector;
			this.model = model;
		}
	}

	public WeightedUnbakedModel getModel() {
		return this.model;
	}

	public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateFactory) {
		return this.selector.getPredicate(stateFactory);
	}

	public boolean equals(Object o) {
		return this == o;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<MultipartModelComponent> {
		public MultipartModelComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new MultipartModelComponent(
				this.deserializeSelectorOrDefault(jsonObject), jsonDeserializationContext.deserialize(jsonObject.get("apply"), WeightedUnbakedModel.class)
			);
		}

		private MultipartModelSelector deserializeSelectorOrDefault(JsonObject object) {
			return object.has("when") ? deserializeSelector(JsonHelper.getObject(object, "when")) : MultipartModelSelector.TRUE;
		}

		@VisibleForTesting
		static MultipartModelSelector deserializeSelector(JsonObject object) {
			Set<Entry<String, JsonElement>> set = object.entrySet();
			if (set.isEmpty()) {
				throw new JsonParseException("No elements found in selector");
			} else if (set.size() == 1) {
				if (object.has("OR")) {
					List<MultipartModelSelector> list = (List<MultipartModelSelector>)Streams.stream(JsonHelper.getArray(object, "OR"))
						.map(json -> deserializeSelector(json.getAsJsonObject()))
						.collect(Collectors.toList());
					return new OrMultipartModelSelector(list);
				} else if (object.has("AND")) {
					List<MultipartModelSelector> list = (List<MultipartModelSelector>)Streams.stream(JsonHelper.getArray(object, "AND"))
						.map(json -> deserializeSelector(json.getAsJsonObject()))
						.collect(Collectors.toList());
					return new AndMultipartModelSelector(list);
				} else {
					return createStatePropertySelector((Entry<String, JsonElement>)set.iterator().next());
				}
			} else {
				return new AndMultipartModelSelector(
					(Iterable<? extends MultipartModelSelector>)set.stream()
						.map(MultipartModelComponent.Deserializer::createStatePropertySelector)
						.collect(Collectors.toList())
				);
			}
		}

		private static MultipartModelSelector createStatePropertySelector(Entry<String, JsonElement> entry) {
			return new SimpleMultipartModelSelector((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
		}
	}
}
