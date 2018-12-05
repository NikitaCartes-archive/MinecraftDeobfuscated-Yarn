package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotationContainer;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class JsonUnbakedModel implements UnbakedModel {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
	@VisibleForTesting
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(JsonUnbakedModel.class, new JsonUnbakedModel.class_795())
		.registerTypeAdapter(ModelElement.class, new ModelElement.class_786())
		.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.class_784())
		.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.class_788())
		.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.class_805())
		.registerTypeAdapter(ModelTransformations.class, new ModelTransformations.class_810())
		.registerTypeAdapter(ModelItemOverride.class, new ModelItemOverride.class_800())
		.create();
	private final List<ModelElement> elements;
	private final boolean depthInGui;
	private final boolean ambientOcclusion;
	private final ModelTransformations transformations;
	private final List<ModelItemOverride> overrides;
	public String id = "";
	@VisibleForTesting
	protected final Map<String, String> textureMap;
	@Nullable
	@VisibleForTesting
	JsonUnbakedModel parent;
	@Nullable
	@VisibleForTesting
	Identifier parentId;

	public static JsonUnbakedModel deserialize(Reader reader) {
		return JsonHelper.deserialize(GSON, reader, JsonUnbakedModel.class);
	}

	public static JsonUnbakedModel deserialize(String string) {
		return deserialize(new StringReader(string));
	}

	public JsonUnbakedModel(
		@Nullable Identifier identifier,
		List<ModelElement> list,
		Map<String, String> map,
		boolean bl,
		boolean bl2,
		ModelTransformations modelTransformations,
		List<ModelItemOverride> list2
	) {
		this.elements = list;
		this.ambientOcclusion = bl;
		this.depthInGui = bl2;
		this.textureMap = map;
		this.parentId = identifier;
		this.transformations = modelTransformations;
		this.overrides = list2;
	}

	public List<ModelElement> getElements() {
		return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
	}

	public boolean hasAmbientOcclusion() {
		return this.parent != null ? this.parent.hasAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean hasDepthInGui() {
		return this.depthInGui;
	}

	public List<ModelItemOverride> getOverrides() {
		return this.overrides;
	}

	private ModelItemPropertyOverrideList method_3440(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel) {
		return this.overrides.isEmpty()
			? ModelItemPropertyOverrideList.ORIGIN
			: new ModelItemPropertyOverrideList(modelLoader, jsonUnbakedModel, modelLoader::getOrLoadModel, this.overrides);
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (ModelItemOverride modelItemOverride : this.overrides) {
			set.add(modelItemOverride.getModelId());
		}

		if (this.parentId != null) {
			set.add(this.parentId);
		}

		return set;
	}

	@Override
	public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<String> set) {
		Set<UnbakedModel> set2 = Sets.<UnbakedModel>newLinkedHashSet();

		for (JsonUnbakedModel jsonUnbakedModel = this;
			jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null;
			jsonUnbakedModel = jsonUnbakedModel.parent
		) {
			set2.add(jsonUnbakedModel);
			UnbakedModel unbakedModel = (UnbakedModel)function.apply(jsonUnbakedModel.parentId);
			if (unbakedModel == null) {
				LOGGER.warn("No parent '{}' while loading model '{}'", this.parentId, jsonUnbakedModel);
			}

			if (set2.contains(unbakedModel)) {
				LOGGER.warn(
					"Found 'parent' loop while loading model '{}' in chain: {} -> {}",
					jsonUnbakedModel,
					set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")),
					this.parentId
				);
				unbakedModel = null;
			}

			if (unbakedModel == null) {
				jsonUnbakedModel.parentId = ModelLoader.MISSING;
				unbakedModel = (UnbakedModel)function.apply(jsonUnbakedModel.parentId);
			}

			if (!(unbakedModel instanceof JsonUnbakedModel)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
		}

		Set<Identifier> set3 = Sets.<Identifier>newHashSet(new Identifier(this.method_3436("particle")));

		for (ModelElement modelElement : this.getElements()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				String string = this.method_3436(modelElementFace.texture);
				if (Objects.equals(string, MissingSprite.getMissingSprite().getId().toString())) {
					set.add(String.format("%s in %s", modelElementFace.texture, this.id));
				}

				set3.add(new Identifier(string));
			}
		}

		this.overrides.forEach(modelItemOverride -> {
			UnbakedModel unbakedModelx = (UnbakedModel)function.apply(modelItemOverride.getModelId());
			if (!Objects.equals(unbakedModelx, this)) {
				set3.addAll(unbakedModelx.getTextureDependencies(function, set));
			}
		});
		if (this.method_3431() == ModelLoader.GENERATION_MARKER) {
			ItemModelGenerator.LAYERS.forEach(stringx -> set3.add(new Identifier(this.method_3436(stringx))));
		}

		return set3;
	}

	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> function, ModelRotationContainer modelRotationContainer) {
		return this.method_3446(modelLoader, this, function, modelRotationContainer);
	}

	public BakedModel method_3446(
		ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel, Function<Identifier, Sprite> function, ModelRotationContainer modelRotationContainer
	) {
		Sprite sprite = (Sprite)function.apply(new Identifier(this.method_3436("particle")));
		if (this.method_3431() == ModelLoader.BLOCK_ENTITY_MARKER) {
			return new BuiltinBakedModel(this.getTransformations(), this.method_3440(modelLoader, jsonUnbakedModel), sprite);
		} else {
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.method_3440(modelLoader, jsonUnbakedModel)).setParticle(sprite);

			for (ModelElement modelElement : this.getElements()) {
				for (Direction direction : modelElement.faces.keySet()) {
					ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
					Sprite sprite2 = (Sprite)function.apply(new Identifier(this.method_3436(modelElementFace.texture)));
					if (modelElementFace.field_4225 == null) {
						builder.addQuad(method_3447(modelElement, modelElementFace, sprite2, direction, modelRotationContainer));
					} else {
						builder.method_4745(
							modelRotationContainer.getRotation().method_4705(modelElementFace.field_4225),
							method_3447(modelElement, modelElementFace, sprite2, direction, modelRotationContainer)
						);
					}
				}
			}

			return builder.build();
		}
	}

	private static BakedQuad method_3447(
		ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelRotationContainer modelRotationContainer
	) {
		return QUAD_FACTORY.method_3468(
			modelElement.field_4228, modelElement.field_4231, modelElementFace, sprite, direction, modelRotationContainer, modelElement.rotation, modelElement.shade
		);
	}

	public boolean method_3432(String string) {
		return !MissingSprite.getMissingSprite().getId().toString().equals(this.method_3436(string));
	}

	public String method_3436(String string) {
		if (!this.method_3439(string)) {
			string = '#' + string;
		}

		return this.method_3442(string, new JsonUnbakedModel.class_794(this));
	}

	private String method_3442(String string, JsonUnbakedModel.class_794 arg) {
		if (this.method_3439(string)) {
			if (this == arg.field_4256) {
				LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", string, this.id);
				return MissingSprite.getMissingSprite().getId().toString();
			} else {
				String string2 = (String)this.textureMap.get(string.substring(1));
				if (string2 == null && this.parent != null) {
					string2 = this.parent.method_3442(string, arg);
				}

				arg.field_4256 = this;
				if (string2 != null && this.method_3439(string2)) {
					string2 = arg.field_4257.method_3442(string2, arg);
				}

				return string2 != null && !this.method_3439(string2) ? string2 : MissingSprite.getMissingSprite().getId().toString();
			}
		} else {
			return string;
		}
	}

	private boolean method_3439(String string) {
		return string.charAt(0) == '#';
	}

	public JsonUnbakedModel method_3431() {
		return this.parent == null ? this : this.parent.method_3431();
	}

	public ModelTransformations getTransformations() {
		ModelTransformation modelTransformation = this.getTransformation(ModelTransformations.Type.THIRD_PERSON_LEFT_HAND);
		ModelTransformation modelTransformation2 = this.getTransformation(ModelTransformations.Type.THIRD_PERSON_RIGHT_HAND);
		ModelTransformation modelTransformation3 = this.getTransformation(ModelTransformations.Type.FIRST_PERSON_LEFT_HAND);
		ModelTransformation modelTransformation4 = this.getTransformation(ModelTransformations.Type.FIRST_PERSON_RIGHT_HAND);
		ModelTransformation modelTransformation5 = this.getTransformation(ModelTransformations.Type.HEAD);
		ModelTransformation modelTransformation6 = this.getTransformation(ModelTransformations.Type.GUI);
		ModelTransformation modelTransformation7 = this.getTransformation(ModelTransformations.Type.GROUND);
		ModelTransformation modelTransformation8 = this.getTransformation(ModelTransformations.Type.FIXED);
		return new ModelTransformations(
			modelTransformation,
			modelTransformation2,
			modelTransformation3,
			modelTransformation4,
			modelTransformation5,
			modelTransformation6,
			modelTransformation7,
			modelTransformation8
		);
	}

	private ModelTransformation getTransformation(ModelTransformations.Type type) {
		return this.parent != null && !this.transformations.isTransformationDefined(type)
			? this.parent.getTransformation(type)
			: this.transformations.getTransformation(type);
	}

	public String toString() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	static final class class_794 {
		public final JsonUnbakedModel field_4257;
		public JsonUnbakedModel field_4256;

		private class_794(JsonUnbakedModel jsonUnbakedModel) {
			this.field_4257 = jsonUnbakedModel;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_795 implements JsonDeserializer<JsonUnbakedModel> {
		public JsonUnbakedModel method_3451(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelElement> list = this.method_3449(jsonDeserializationContext, jsonObject);
			String string = this.method_3450(jsonObject);
			Map<String, String> map = this.method_3448(jsonObject);
			boolean bl = this.method_3453(jsonObject);
			ModelTransformations modelTransformations = ModelTransformations.ORIGIN;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
				modelTransformations = jsonDeserializationContext.deserialize(jsonObject2, ModelTransformations.class);
			}

			List<ModelItemOverride> list2 = this.method_3452(jsonDeserializationContext, jsonObject);
			Identifier identifier = string.isEmpty() ? null : new Identifier(string);
			return new JsonUnbakedModel(identifier, list, map, bl, true, modelTransformations, list2);
		}

		protected List<ModelItemOverride> method_3452(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelItemOverride> list = Lists.<ModelItemOverride>newArrayList();
			if (jsonObject.has("overrides")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "overrides")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, String> method_3448(JsonObject jsonObject) {
			Map<String, String> map = Maps.<String, String>newHashMap();
			if (jsonObject.has("textures")) {
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
				}
			}

			return map;
		}

		private String method_3450(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "parent", "");
		}

		protected boolean method_3453(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "ambientocclusion", true);
		}

		protected List<ModelElement> method_3449(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelElement> list = Lists.<ModelElement>newArrayList();
			if (jsonObject.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "elements")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}
}
