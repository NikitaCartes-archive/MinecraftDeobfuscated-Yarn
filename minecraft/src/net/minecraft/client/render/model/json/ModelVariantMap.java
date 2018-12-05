package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_816;
import net.minecraft.class_819;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariantMap {
	private final Map<String, WeightedUnbakedModel> variantMap = Maps.<String, WeightedUnbakedModel>newLinkedHashMap();
	private class_816 field_4240;

	public static ModelVariantMap method_3424(ModelVariantMap.class_791 arg, Reader reader) {
		return JsonHelper.deserialize(arg.field_4243, reader, ModelVariantMap.class);
	}

	public ModelVariantMap(Map<String, WeightedUnbakedModel> map, class_816 arg) {
		this.field_4240 = arg;
		this.variantMap.putAll(map);
	}

	public ModelVariantMap(List<ModelVariantMap> list) {
		ModelVariantMap modelVariantMap = null;

		for (ModelVariantMap modelVariantMap2 : list) {
			if (modelVariantMap2.method_3422()) {
				this.variantMap.clear();
				modelVariantMap = modelVariantMap2;
			}

			this.variantMap.putAll(modelVariantMap2.variantMap);
		}

		if (modelVariantMap != null) {
			this.field_4240 = modelVariantMap.field_4240;
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof ModelVariantMap) {
				ModelVariantMap modelVariantMap = (ModelVariantMap)object;
				if (this.variantMap.equals(modelVariantMap.variantMap)) {
					return this.method_3422() ? this.field_4240.equals(modelVariantMap.field_4240) : !modelVariantMap.method_3422();
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.variantMap.hashCode() + (this.method_3422() ? this.field_4240.hashCode() : 0);
	}

	public Map<String, WeightedUnbakedModel> method_3423() {
		return this.variantMap;
	}

	public boolean method_3422() {
		return this.field_4240 != null;
	}

	public class_816 method_3421() {
		return this.field_4240;
	}

	@Environment(EnvType.CLIENT)
	public static final class class_791 {
		@VisibleForTesting
		final Gson field_4243 = new GsonBuilder()
			.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.class_792())
			.registerTypeAdapter(ModelVariant.class, new ModelVariant.class_814())
			.registerTypeAdapter(WeightedUnbakedModel.class, new WeightedUnbakedModel.class_808())
			.registerTypeAdapter(class_816.class, new class_816.class_817(this))
			.registerTypeAdapter(class_819.class, new class_819.class_820())
			.create();
		private StateFactory<Block, BlockState> field_4242;

		public StateFactory<Block, BlockState> method_3425() {
			return this.field_4242;
		}

		public void method_3426(StateFactory<Block, BlockState> stateFactory) {
			this.field_4242 = stateFactory;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_792 implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap method_3428(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, WeightedUnbakedModel> map = this.method_3429(jsonDeserializationContext, jsonObject);
			class_816 lv = this.method_3427(jsonDeserializationContext, jsonObject);
			if (!map.isEmpty() || lv != null && !lv.method_3520().isEmpty()) {
				return new ModelVariantMap(map, lv);
			} else {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, WeightedUnbakedModel> method_3429(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			Map<String, WeightedUnbakedModel> map = Maps.<String, WeightedUnbakedModel>newHashMap();
			if (jsonObject.has("variants")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "variants");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), jsonDeserializationContext.deserialize((JsonElement)entry.getValue(), WeightedUnbakedModel.class));
				}
			}

			return map;
		}

		@Nullable
		protected class_816 method_3427(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			if (!jsonObject.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(jsonObject, "multipart");
				return jsonDeserializationContext.deserialize(jsonArray, class_816.class);
			}
		}
	}
}
