package net.minecraft.client.render.model.json;

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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

@Environment(EnvType.CLIENT)
public class ModelVariantMap {
	private final Map<String, WeightedUnbakedModel> variantMap = Maps.<String, WeightedUnbakedModel>newLinkedHashMap();
	private MultipartUnbakedModel multipartModel;

	public static ModelVariantMap deserialize(ModelVariantMap.DeserializationContext context, Reader reader) {
		return JsonHelper.deserialize(context.gson, reader, ModelVariantMap.class);
	}

	public ModelVariantMap(Map<String, WeightedUnbakedModel> variantMap, MultipartUnbakedModel multipartModel) {
		this.multipartModel = multipartModel;
		this.variantMap.putAll(variantMap);
	}

	public ModelVariantMap(List<ModelVariantMap> variantMapList) {
		ModelVariantMap modelVariantMap = null;

		for (ModelVariantMap modelVariantMap2 : variantMapList) {
			if (modelVariantMap2.hasMultipartModel()) {
				this.variantMap.clear();
				modelVariantMap = modelVariantMap2;
			}

			this.variantMap.putAll(modelVariantMap2.variantMap);
		}

		if (modelVariantMap != null) {
			this.multipartModel = modelVariantMap.multipartModel;
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ModelVariantMap) {
				ModelVariantMap modelVariantMap = (ModelVariantMap)o;
				if (this.variantMap.equals(modelVariantMap.variantMap)) {
					return this.hasMultipartModel() ? this.multipartModel.equals(modelVariantMap.multipartModel) : !modelVariantMap.hasMultipartModel();
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.variantMap.hashCode() + (this.hasMultipartModel() ? this.multipartModel.hashCode() : 0);
	}

	public Map<String, WeightedUnbakedModel> getVariantMap() {
		return this.variantMap;
	}

	public boolean hasMultipartModel() {
		return this.multipartModel != null;
	}

	public MultipartUnbakedModel getMultipartModel() {
		return this.multipartModel;
	}

	@Environment(EnvType.CLIENT)
	public static final class DeserializationContext {
		protected final Gson gson = new GsonBuilder()
			.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer())
			.registerTypeAdapter(ModelVariant.class, new ModelVariant.Deserializer())
			.registerTypeAdapter(WeightedUnbakedModel.class, new WeightedUnbakedModel.Deserializer())
			.registerTypeAdapter(MultipartUnbakedModel.class, new MultipartUnbakedModel.Deserializer(this))
			.registerTypeAdapter(MultipartModelComponent.class, new MultipartModelComponent.Deserializer())
			.create();
		private StateManager<Block, BlockState> stateFactory;

		public StateManager<Block, BlockState> getStateFactory() {
			return this.stateFactory;
		}

		public void setStateFactory(StateManager<Block, BlockState> stateFactory) {
			this.stateFactory = stateFactory;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap method_3428(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, WeightedUnbakedModel> map = this.deserializeVariants(jsonDeserializationContext, jsonObject);
			MultipartUnbakedModel multipartUnbakedModel = this.deserializeMultipart(jsonDeserializationContext, jsonObject);
			if (!map.isEmpty() || multipartUnbakedModel != null && !multipartUnbakedModel.getModels().isEmpty()) {
				return new ModelVariantMap(map, multipartUnbakedModel);
			} else {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, WeightedUnbakedModel> deserializeVariants(JsonDeserializationContext context, JsonObject object) {
			Map<String, WeightedUnbakedModel> map = Maps.<String, WeightedUnbakedModel>newHashMap();
			if (object.has("variants")) {
				JsonObject jsonObject = JsonHelper.getObject(object, "variants");

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					map.put(entry.getKey(), context.deserialize((JsonElement)entry.getValue(), WeightedUnbakedModel.class));
				}
			}

			return map;
		}

		@Nullable
		protected MultipartUnbakedModel deserializeMultipart(JsonDeserializationContext context, JsonObject object) {
			if (!object.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(object, "multipart");
				return context.deserialize(jsonArray, MultipartUnbakedModel.class);
			}
		}
	}
}
