package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
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
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4730;
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
import net.minecraft.client.texture.SpriteAtlasTexture;
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
	private final ModelTransformation transformations;
	private final List<ModelItemOverride> overrides;
	public String id = "";
	@VisibleForTesting
	protected final Map<String, Either<class_4730, String>> textureMap;
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
		@Nullable Identifier parentId,
		List<ModelElement> elements,
		Map<String, Either<class_4730, String>> textureMap,
		boolean ambientOcclusion,
		boolean depthInGui,
		ModelTransformation transformations,
		List<ModelItemOverride> overrides
	) {
		this.elements = elements;
		this.ambientOcclusion = ambientOcclusion;
		this.depthInGui = depthInGui;
		this.textureMap = textureMap;
		this.parentId = parentId;
		this.transformations = transformations;
		this.overrides = overrides;
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

	private ModelItemPropertyOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel parent) {
		return this.overrides.isEmpty()
			? ModelItemPropertyOverrideList.EMPTY
			: new ModelItemPropertyOverrideList(modelLoader, parent, modelLoader::getOrLoadModel, this.overrides);
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
	public Collection<class_4730> getTextureDependencies(
		Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences
	) {
		Set<UnbakedModel> set = Sets.<UnbakedModel>newLinkedHashSet();

		for (JsonUnbakedModel jsonUnbakedModel = this;
			jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null;
			jsonUnbakedModel = jsonUnbakedModel.parent
		) {
			set.add(jsonUnbakedModel);
			UnbakedModel unbakedModel = (UnbakedModel)unbakedModelGetter.apply(jsonUnbakedModel.parentId);
			if (unbakedModel == null) {
				LOGGER.warn("No parent '{}' while loading model '{}'", this.parentId, jsonUnbakedModel);
			}

			if (set.contains(unbakedModel)) {
				LOGGER.warn(
					"Found 'parent' loop while loading model '{}' in chain: {} -> {}",
					jsonUnbakedModel,
					set.stream().map(Object::toString).collect(Collectors.joining(" -> ")),
					this.parentId
				);
				unbakedModel = null;
			}

			if (unbakedModel == null) {
				jsonUnbakedModel.parentId = ModelLoader.MISSING;
				unbakedModel = (UnbakedModel)unbakedModelGetter.apply(jsonUnbakedModel.parentId);
			}

			if (!(unbakedModel instanceof JsonUnbakedModel)) {
				throw new IllegalStateException("BlockModel parent has to be a block model.");
			}

			jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
		}

		Set<class_4730> set2 = Sets.<class_4730>newHashSet(this.method_24077("particle"));

		for (ModelElement modelElement : this.getElements()) {
			for (ModelElementFace modelElementFace : modelElement.faces.values()) {
				class_4730 lv = this.method_24077(modelElementFace.textureId);
				if (Objects.equals(lv.method_24147(), MissingSprite.getMissingSpriteId())) {
					unresolvedTextureReferences.add(Pair.of(modelElementFace.textureId, this.id));
				}

				set2.add(lv);
			}
		}

		this.overrides.forEach(modelItemOverride -> {
			UnbakedModel unbakedModelx = (UnbakedModel)unbakedModelGetter.apply(modelItemOverride.getModelId());
			if (!Objects.equals(unbakedModelx, this)) {
				set2.addAll(unbakedModelx.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
			}
		});
		if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
			ItemModelGenerator.LAYERS.forEach(string -> set2.add(this.method_24077(string)));
		}

		return set2;
	}

	@Override
	public BakedModel bake(ModelLoader loader, Function<class_4730, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return this.bake(loader, this, textureGetter, rotationContainer, modelId);
	}

	public BakedModel bake(
		ModelLoader loader, JsonUnbakedModel parent, Function<class_4730, Sprite> textureGetter, ModelBakeSettings settings, Identifier identifier
	) {
		Sprite sprite = (Sprite)textureGetter.apply(this.method_24077("particle"));
		if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
			return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(loader, parent), sprite);
		} else {
			BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(loader, parent)).setParticle(sprite);

			for (ModelElement modelElement : this.getElements()) {
				for (Direction direction : modelElement.faces.keySet()) {
					ModelElementFace modelElementFace = (ModelElementFace)modelElement.faces.get(direction);
					Sprite sprite2 = (Sprite)textureGetter.apply(this.method_24077(modelElementFace.textureId));
					if (modelElementFace.cullFace == null) {
						builder.addQuad(createQuad(modelElement, modelElementFace, sprite2, direction, settings, identifier));
					} else {
						builder.addQuad(
							Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace),
							createQuad(modelElement, modelElementFace, sprite2, direction, settings, identifier)
						);
					}
				}
			}

			return builder.build();
		}
	}

	private static BakedQuad createQuad(
		ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier identifier
	) {
		return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade, identifier);
	}

	public boolean textureExists(String name) {
		return !MissingSprite.getMissingSpriteId().equals(this.method_24077(name).method_24147());
	}

	public class_4730 method_24077(String string) {
		if (isTextureReference(string)) {
			string = string.substring(1);
		}

		List<String> list = Lists.<String>newArrayList();

		while (true) {
			Either<class_4730, String> either = this.resolveTexture(string);
			Optional<class_4730> optional = either.left();
			if (optional.isPresent()) {
				return (class_4730)optional.get();
			}

			string = (String)either.right().get();
			if (list.contains(string)) {
				LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), string, this.id);
				return new class_4730(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId());
			}

			list.add(string);
		}
	}

	private Either<class_4730, String> resolveTexture(String name) {
		for (JsonUnbakedModel jsonUnbakedModel = this; jsonUnbakedModel != null; jsonUnbakedModel = jsonUnbakedModel.parent) {
			Either<class_4730, String> either = (Either<class_4730, String>)jsonUnbakedModel.textureMap.get(name);
			if (either != null) {
				return either;
			}
		}

		return Either.left(new class_4730(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId()));
	}

	private static boolean isTextureReference(String string) {
		return string.charAt(0) == '#';
	}

	public JsonUnbakedModel getRootModel() {
		return this.parent == null ? this : this.parent.getRootModel();
	}

	public ModelTransformation getTransformations() {
		Transformation transformation = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_LEFT_HAND);
		Transformation transformation2 = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND);
		Transformation transformation3 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_LEFT_HAND);
		Transformation transformation4 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND);
		Transformation transformation5 = this.getTransformation(ModelTransformation.Type.HEAD);
		Transformation transformation6 = this.getTransformation(ModelTransformation.Type.GUI);
		Transformation transformation7 = this.getTransformation(ModelTransformation.Type.GROUND);
		Transformation transformation8 = this.getTransformation(ModelTransformation.Type.FIXED);
		return new ModelTransformation(
			transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8
		);
	}

	private Transformation getTransformation(ModelTransformation.Type type) {
		return this.parent != null && !this.transformations.isTransformationDefined(type)
			? this.parent.getTransformation(type)
			: this.transformations.getTransformation(type);
	}

	public String toString() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public static class Deserializer implements JsonDeserializer<JsonUnbakedModel> {
		public JsonUnbakedModel deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = element.getAsJsonObject();
			List<ModelElement> list = this.deserializeElements(context, jsonObject);
			String string = this.deserializeParent(jsonObject);
			Map<String, Either<class_4730, String>> map = this.deserializeTextures(jsonObject);
			boolean bl = this.deserializeAmbientOcclusion(jsonObject);
			ModelTransformation modelTransformation = ModelTransformation.NONE;
			if (jsonObject.has("display")) {
				JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
				modelTransformation = context.deserialize(jsonObject2, ModelTransformation.class);
			}

			List<ModelItemOverride> list2 = this.deserializeOverrides(context, jsonObject);
			Identifier identifier = string.isEmpty() ? null : new Identifier(string);
			return new JsonUnbakedModel(identifier, list, map, bl, true, modelTransformation, list2);
		}

		protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext context, JsonObject object) {
			List<ModelItemOverride> list = Lists.<ModelItemOverride>newArrayList();
			if (object.has("overrides")) {
				for (JsonElement jsonElement : JsonHelper.getArray(object, "overrides")) {
					list.add(context.deserialize(jsonElement, ModelItemOverride.class));
				}
			}

			return list;
		}

		private Map<String, Either<class_4730, String>> deserializeTextures(JsonObject object) {
			Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEX;
			Map<String, Either<class_4730, String>> map = Maps.<String, Either<class_4730, String>>newHashMap();
			if (object.has("textures")) {
				JsonObject jsonObject = JsonHelper.getObject(object, "textures");

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					map.put(entry.getKey(), method_24079(identifier, ((JsonElement)entry.getValue()).getAsString()));
				}
			}

			return map;
		}

		private static Either<class_4730, String> method_24079(Identifier identifier, String string) {
			if (JsonUnbakedModel.isTextureReference(string)) {
				return Either.right(string.substring(1));
			} else {
				Identifier identifier2 = Identifier.tryParse(string);
				if (identifier2 == null) {
					throw new JsonParseException(string + " is not valid resource location");
				} else {
					return Either.left(new class_4730(identifier, identifier2));
				}
			}
		}

		private String deserializeParent(JsonObject object) {
			return JsonHelper.getString(object, "parent", "");
		}

		protected boolean deserializeAmbientOcclusion(JsonObject object) {
			return JsonHelper.getBoolean(object, "ambientocclusion", true);
		}

		protected List<ModelElement> deserializeElements(JsonDeserializationContext context, JsonObject object) {
			List<ModelElement> list = Lists.<ModelElement>newArrayList();
			if (object.has("elements")) {
				for (JsonElement jsonElement : JsonHelper.getArray(object, "elements")) {
					list.add(context.deserialize(jsonElement, ModelElement.class));
				}
			}

			return list;
		}
	}
}
