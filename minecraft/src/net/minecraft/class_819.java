package net.minecraft;

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
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.sortme.BlockStatePropertyValuePredicateFactory;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class class_819 {
	private final class_815 field_4335;
	private final WeightedUnbakedModel field_4336;

	public class_819(class_815 arg, WeightedUnbakedModel weightedUnbakedModel) {
		if (arg == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (weightedUnbakedModel == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.field_4335 = arg;
			this.field_4336 = weightedUnbakedModel;
		}
	}

	public WeightedUnbakedModel method_3529() {
		return this.field_4336;
	}

	public Predicate<BlockState> method_3530(StateFactory<Block, BlockState> stateFactory) {
		return this.field_4335.getPredicate(stateFactory);
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Environment(EnvType.CLIENT)
	public static class class_820 implements JsonDeserializer<class_819> {
		public class_819 method_3535(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new class_819(this.method_3531(jsonObject), jsonDeserializationContext.deserialize(jsonObject.get("apply"), WeightedUnbakedModel.class));
		}

		private class_815 method_3531(JsonObject jsonObject) {
			return jsonObject.has("when") ? method_3536(JsonHelper.getObject(jsonObject, "when")) : class_815.TRUE;
		}

		@VisibleForTesting
		static class_815 method_3536(JsonObject jsonObject) {
			Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
			if (set.isEmpty()) {
				throw new JsonParseException("No elements found in selector");
			} else if (set.size() == 1) {
				if (jsonObject.has("OR")) {
					List<class_815> list = (List<class_815>)Streams.stream(JsonHelper.getArray(jsonObject, "OR"))
						.map(jsonElement -> method_3536(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new class_821(list);
				} else if (jsonObject.has("AND")) {
					List<class_815> list = (List<class_815>)Streams.stream(JsonHelper.getArray(jsonObject, "AND"))
						.map(jsonElement -> method_3536(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new class_812(list);
				} else {
					return method_3533((Entry<String, JsonElement>)set.iterator().next());
				}
			} else {
				return new class_812((Iterable<? extends class_815>)set.stream().map(entry -> method_3533(entry)).collect(Collectors.toList()));
			}
		}

		private static class_815 method_3533(Entry<String, JsonElement> entry) {
			return new BlockStatePropertyValuePredicateFactory((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
		}
	}
}
