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
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
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
	private static final BakedQuadFactory field_4249 = new BakedQuadFactory();
	@VisibleForTesting
	static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(JsonUnbakedModel.class, new JsonUnbakedModel.Deserializer())
		.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
		.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
		.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
		.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
		.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
		.registerTypeAdapter(ModelItemOverride.class, new ModelItemOverride.Deserializer())
		.create();
	private final List<ModelElement> elements;
	private final boolean depthInGui;
	private final boolean ambientOcclusion;
	private final ModelTransformation field_4250;
	private final List<ModelItemOverride> overrides;
	public String id = "";
	@VisibleForTesting
	protected final Map<String, String> textureMap;
	@Nullable
	protected JsonUnbakedModel parent;
	@Nullable
	protected Identifier parentId;

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
		ModelTransformation modelTransformation,
		List<ModelItemOverride> list2
	) {
		this.elements = list;
		this.ambientOcclusion = bl;
		this.depthInGui = bl2;
		this.textureMap = map;
		this.parentId = identifier;
		this.field_4250 = modelTransformation;
		this.overrides = list2;
	}

	public List<ModelElement> getElements() {
		return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
	}

	public boolean useAmbientOcclusion() {
		return this.parent != null ? this.parent.useAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean hasDepthInGui() {
		return this.depthInGui;
	}

	public List<ModelItemOverride> getOverrides() {
		return this.overrides;
	}

	private ModelItemPropertyOverrideList method_3440(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel) {
		return this.overrides.isEmpty()
			? ModelItemPropertyOverrideList.EMPTY
			: new ModelItemPropertyOverrideList(modelLoader, jsonUnbakedModel, modelLoader::method_4726, this.overrides);
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
				jsonUnbakedModel.parentId = ModelLoader.field_5374;
				unbakedModel = (UnbakedModel)function.apply(jsonUnbakedModel.parentId);
			}

			if (!(unbakedModel instanceof JsonUnbakedModel)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
		}

		Set<Identifier> set3 = Sets.<Identifier>newHashSet(new Identifier(this.resolveTexture("particle")));

		for (ModelElement modelElement : this.getElements()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				String string = this.resolveTexture(modelElementFace.textureId);
				if (Objects.equals(string, MissingSprite.getMissingSpriteId().toString())) {
					set.add(String.format("%s in %s", modelElementFace.textureId, this.id));
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
		if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
			ItemModelGenerator.LAYERS.forEach(stringx -> set3.add(new Identifier(this.resolveTexture(stringx))));
		}

		return set3;
	}

	@Override
	public BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> function, ModelBakeSettings modelBakeSettings) {
		return this.method_3446(modelLoader, this, function, modelBakeSettings);
	}

	public BakedModel method_3446(
		ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel, Function<Identifier, Sprite> function, ModelBakeSettings modelBakeSettings
	) {
		Sprite sprite = (Sprite)function.apply(new Identifier(this.resolveTexture("particle")));
		if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
			return new BuiltinBakedModel(this.method_3443(), this.method_3440(modelLoader, jsonUnbakedModel), sprite);
		} else {
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.method_3440(modelLoader, jsonUnbakedModel)).setParticle(sprite);

			for (ModelElement modelElement : this.getElements()) {
				for (Direction direction : modelElement.faces.keySet()) {
					ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
					Sprite sprite2 = (Sprite)function.apply(new Identifier(this.resolveTexture(modelElementFace.textureId)));
					if (modelElementFace.cullFace == null) {
						builder.addQuad(method_3447(modelElement, modelElementFace, sprite2, direction, modelBakeSettings));
					} else {
						builder.addQuad(
							modelBakeSettings.getRotation().apply(modelElementFace.cullFace), method_3447(modelElement, modelElementFace, sprite2, direction, modelBakeSettings)
						);
					}
				}
			}

			return builder.build();
		}
	}

	private static BakedQuad method_3447(
		ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelBakeSettings modelBakeSettings
	) {
		return field_4249.method_3468(
			modelElement.from, modelElement.to, modelElementFace, sprite, direction, modelBakeSettings, modelElement.field_4232, modelElement.shade
		);
	}

	public boolean textureExists(String string) {
		return !MissingSprite.getMissingSpriteId().toString().equals(this.resolveTexture(string));
	}

	public String resolveTexture(String string) {
		if (!this.isTextureReference(string)) {
			string = '#' + string;
		}

		return this.resolveTexture(string, new JsonUnbakedModel.TextureResolutionContext(this));
	}

	private String resolveTexture(String string, JsonUnbakedModel.TextureResolutionContext textureResolutionContext) {
		if (this.isTextureReference(string)) {
			if (this == textureResolutionContext.current) {
				LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", string, this.id);
				return MissingSprite.getMissingSpriteId().toString();
			} else {
				String string2 = (String)this.textureMap.get(string.substring(1));
				if (string2 == null && this.parent != null) {
					string2 = this.parent.resolveTexture(string, textureResolutionContext);
				}

				textureResolutionContext.current = this;
				if (string2 != null && this.isTextureReference(string2)) {
					string2 = textureResolutionContext.root.resolveTexture(string2, textureResolutionContext);
				}

				return string2 != null && !this.isTextureReference(string2) ? string2 : MissingSprite.getMissingSpriteId().toString();
			}
		} else {
			return string;
		}
	}

	private boolean isTextureReference(String string) {
		return string.charAt(0) == '#';
	}

	public JsonUnbakedModel getRootModel() {
		return this.parent == null ? this : this.parent.getRootModel();
	}

	public ModelTransformation method_3443() {
		Transformation transformation = this.method_3438(ModelTransformation.Type.field_4323);
		Transformation transformation2 = this.method_3438(ModelTransformation.Type.field_4320);
		Transformation transformation3 = this.method_3438(ModelTransformation.Type.field_4321);
		Transformation transformation4 = this.method_3438(ModelTransformation.Type.field_4322);
		Transformation transformation5 = this.method_3438(ModelTransformation.Type.field_4316);
		Transformation transformation6 = this.method_3438(ModelTransformation.Type.field_4317);
		Transformation transformation7 = this.method_3438(ModelTransformation.Type.field_4318);
		Transformation transformation8 = this.method_3438(ModelTransformation.Type.field_4319);
		return new ModelTransformation(
			transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8
		);
	}

	private Transformation method_3438(ModelTransformation.Type type) {
		return this.parent != null && !this.field_4250.isTransformationDefined(type) ? this.parent.method_3438(type) : this.field_4250.getTransformation(type);
	}

	public String toString() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<JsonUnbakedModel> {
		public JsonUnbakedModel method_3451(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			List<ModelElement> list = this.deserializeElements(jsonDeserializationContext, jsonObject);
			String string = this.deserializeParent(jsonObject);
			Map<String, String> map = this.deserializeTextures(jsonObject);
			boolean bl = this.deserializeAmbientOcclusion(jsonObject);
			ModelTransformation modelTransformation = ModelTransformation.NONE;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
				modelTransformation = jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
			}

			List<ModelItemOverride> list2 = this.deserializeOverrides(jsonDeserializationContext, jsonObject);
			Identifier identifier = string.isEmpty() ? null : new Identifier(string);
			return new JsonUnbakedModel(identifier, list, map, bl, true, modelTransformation, list2);
		}

		protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelItemOverride> list = Lists.<ModelItemOverride>newArrayList();
			if (jsonObject.has("overrides")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "overrides")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, String> deserializeTextures(JsonObject jsonObject) {
			Map<String, String> map = Maps.<String, String>newHashMap();
			if (jsonObject.has("textures")) {
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
				}
			}

			return map;
		}

		private String deserializeParent(JsonObject jsonObject) {
			return JsonHelper.getString(jsonObject, "parent", "");
		}

		protected boolean deserializeAmbientOcclusion(JsonObject jsonObject) {
			return JsonHelper.getBoolean(jsonObject, "ambientocclusion", true);
		}

		protected List<ModelElement> deserializeElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
			List<ModelElement> list = Lists.<ModelElement>newArrayList();
			if (jsonObject.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "elements")) {
					list.add(jsonDeserializationContext.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}

	@Environment(EnvType.CLIENT)
	static final class TextureResolutionContext {
		public final JsonUnbakedModel root;
		public JsonUnbakedModel current;

		private TextureResolutionContext(JsonUnbakedModel jsonUnbakedModel) {
			this.root = jsonUnbakedModel;
		}
	}
}
