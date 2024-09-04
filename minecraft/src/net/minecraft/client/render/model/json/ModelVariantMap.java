package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.GroupableModel;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelVariantMap {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ModelVariantMap.class, new ModelVariantMap.Deserializer())
		.registerTypeAdapter(ModelVariant.class, new ModelVariant.Deserializer())
		.registerTypeAdapter(WeightedUnbakedModel.class, new WeightedUnbakedModel.Deserializer())
		.registerTypeAdapter(MultipartUnbakedModel.Serialized.class, new MultipartUnbakedModel.Deserializer())
		.registerTypeAdapter(MultipartModelComponent.class, new MultipartModelComponent.Deserializer())
		.create();
	private final Map<String, WeightedUnbakedModel> variantMap;
	@Nullable
	private final MultipartUnbakedModel.Serialized multipartModel;

	public static ModelVariantMap fromJson(Reader reader) {
		return JsonHelper.deserialize(GSON, reader, ModelVariantMap.class);
	}

	public static ModelVariantMap fromJson(JsonElement json) {
		return GSON.fromJson(json, ModelVariantMap.class);
	}

	public ModelVariantMap(Map<String, WeightedUnbakedModel> variantMap, @Nullable MultipartUnbakedModel.Serialized multipartModel) {
		this.multipartModel = multipartModel;
		this.variantMap = variantMap;
	}

	/**
	 * Finds and returns the definition of the variant under the {@code key}. If the
	 * {@code key} does not {@linkplain #containsVariant(String) exist}, this throws
	 * an exception.
	 * 
	 * @return the variant definition
	 * @throws VariantAbsentException if no variant with the given {@code key} exists
	 * 
	 * @param key the variant's key
	 */
	@VisibleForTesting
	public WeightedUnbakedModel getVariant(String key) {
		WeightedUnbakedModel weightedUnbakedModel = (WeightedUnbakedModel)this.variantMap.get(key);
		if (weightedUnbakedModel == null) {
			throw new ModelVariantMap.VariantAbsentException();
		} else {
			return weightedUnbakedModel;
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof ModelVariantMap modelVariantMap)
				? false
				: this.variantMap.equals(modelVariantMap.variantMap) && Objects.equals(this.multipartModel, modelVariantMap.multipartModel);
		}
	}

	public int hashCode() {
		return 31 * this.variantMap.hashCode() + (this.multipartModel != null ? this.multipartModel.hashCode() : 0);
	}

	@VisibleForTesting
	public Set<WeightedUnbakedModel> getAllModels() {
		Set<WeightedUnbakedModel> set = Sets.<WeightedUnbakedModel>newHashSet(this.variantMap.values());
		if (this.multipartModel != null) {
			set.addAll(this.multipartModel.getBackingModels());
		}

		return set;
	}

	@Nullable
	public MultipartUnbakedModel.Serialized getMultipartModel() {
		return this.multipartModel;
	}

	public Map<BlockState, GroupableModel> parse(StateManager<Block, BlockState> stateManager, String path) {
		Map<BlockState, GroupableModel> map = new IdentityHashMap();
		List<BlockState> list = stateManager.getStates();
		MultipartUnbakedModel multipartUnbakedModel;
		if (this.multipartModel != null) {
			multipartUnbakedModel = this.multipartModel.toModel(stateManager);
			list.forEach(state -> map.put(state, multipartUnbakedModel));
		} else {
			multipartUnbakedModel = null;
		}

		this.variantMap.forEach((key, model) -> {
			try {
				list.stream().filter(BlockPropertiesPredicate.parse(stateManager, key)).forEach(state -> {
					UnbakedModel unbakedModel = (UnbakedModel)map.put(state, model);
					if (unbakedModel != null && unbakedModel != multipartUnbakedModel) {
						String stringx = (String)((Entry)this.variantMap.entrySet().stream().filter(entry -> entry.getValue() == unbakedModel).findFirst().get()).getKey();
						throw new RuntimeException("Overlapping definition with: " + stringx);
					}
				});
			} catch (Exception var9) {
				LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", path, key, var9.getMessage());
			}
		});
		return map;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<ModelVariantMap> {
		public ModelVariantMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, WeightedUnbakedModel> map = this.variantsFromJson(jsonDeserializationContext, jsonObject);
			MultipartUnbakedModel.Serialized serialized = this.multipartFromJson(jsonDeserializationContext, jsonObject);
			if (map.isEmpty() && serialized == null) {
				throw new JsonParseException("Neither 'variants' nor 'multipart' found");
			} else {
				return new ModelVariantMap(map, serialized);
			}
		}

		protected Map<String, WeightedUnbakedModel> variantsFromJson(JsonDeserializationContext context, JsonObject object) {
			Map<String, WeightedUnbakedModel> map = Maps.<String, WeightedUnbakedModel>newHashMap();
			if (object.has("variants")) {
				JsonObject jsonObject = JsonHelper.getObject(object, "variants");

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					map.put((String)entry.getKey(), (WeightedUnbakedModel)context.deserialize((JsonElement)entry.getValue(), WeightedUnbakedModel.class));
				}
			}

			return map;
		}

		@Nullable
		protected MultipartUnbakedModel.Serialized multipartFromJson(JsonDeserializationContext context, JsonObject object) {
			if (!object.has("multipart")) {
				return null;
			} else {
				JsonArray jsonArray = JsonHelper.getArray(object, "multipart");
				return context.deserialize(jsonArray, MultipartUnbakedModel.Serialized.class);
			}
		}
	}

	/**
	 * An unchecked exception indicating a variant is not found with a string key.
	 */
	@Environment(EnvType.CLIENT)
	protected static class VariantAbsentException extends RuntimeException {
	}
}
