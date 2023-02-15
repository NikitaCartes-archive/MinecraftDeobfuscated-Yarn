/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)JsonUnbakedModel.class), new Deserializer()).registerTypeAdapter((Type)((Object)ModelElement.class), new ModelElement.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementFace.class), new ModelElementFace.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementTexture.class), new ModelElementTexture.Deserializer()).registerTypeAdapter((Type)((Object)Transformation.class), new Transformation.Deserializer()).registerTypeAdapter((Type)((Object)ModelTransformation.class), new ModelTransformation.Deserializer()).registerTypeAdapter((Type)((Object)ModelOverride.class), new ModelOverride.Deserializer()).create();
    /**
     * The initial character ({@value}) of a texture reference in JSON; used to
     * distinguish texture references from other references.
     */
    private static final char TEXTURE_REFERENCE_INITIAL = '#';
    public static final String PARTICLE_KEY = "particle";
    private static final boolean field_42912 = true;
    private final List<ModelElement> elements;
    @Nullable
    private final GuiLight guiLight;
    @Nullable
    private final Boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelOverride> overrides;
    public String id = "";
    @VisibleForTesting
    protected final Map<String, Either<SpriteIdentifier, String>> textureMap;
    @Nullable
    protected JsonUnbakedModel parent;
    @Nullable
    protected Identifier parentId;

    public static JsonUnbakedModel deserialize(Reader input) {
        return JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
    }

    public static JsonUnbakedModel deserialize(String json) {
        return JsonUnbakedModel.deserialize(new StringReader(json));
    }

    public JsonUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, @Nullable Boolean ambientOcclusion, @Nullable GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight = guiLight;
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.transformations = transformations;
        this.overrides = overrides;
    }

    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public boolean useAmbientOcclusion() {
        if (this.ambientOcclusion != null) {
            return this.ambientOcclusion;
        }
        if (this.parent != null) {
            return this.parent.useAmbientOcclusion();
        }
        return true;
    }

    public GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        }
        if (this.parent != null) {
            return this.parent.getGuiLight();
        }
        return GuiLight.BLOCK;
    }

    public boolean needsResolution() {
        return this.parentId == null || this.parent != null && this.parent.needsResolution();
    }

    public List<ModelOverride> getOverrides() {
        return this.overrides;
    }

    private ModelOverrideList compileOverrides(Baker baker, JsonUnbakedModel parent) {
        if (this.overrides.isEmpty()) {
            return ModelOverrideList.EMPTY;
        }
        return new ModelOverrideList(baker, parent, this.overrides);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        HashSet<Identifier> set = Sets.newHashSet();
        for (ModelOverride modelOverride : this.overrides) {
            set.add(modelOverride.getModelId());
        }
        if (this.parentId != null) {
            set.add(this.parentId);
        }
        return set;
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        LinkedHashSet<JsonUnbakedModel> set = Sets.newLinkedHashSet();
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null) {
            set.add(jsonUnbakedModel);
            UnbakedModel unbakedModel = modelLoader.apply(jsonUnbakedModel.parentId);
            if (unbakedModel == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentId, (Object)jsonUnbakedModel);
            }
            if (set.contains(unbakedModel)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", jsonUnbakedModel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentId);
                unbakedModel = null;
            }
            if (unbakedModel == null) {
                jsonUnbakedModel.parentId = ModelLoader.MISSING_ID;
                unbakedModel = modelLoader.apply(jsonUnbakedModel.parentId);
            }
            if (!(unbakedModel instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        this.overrides.forEach(override -> {
            UnbakedModel unbakedModel = (UnbakedModel)modelLoader.apply(override.getModelId());
            if (Objects.equals(unbakedModel, this)) {
                return;
            }
            unbakedModel.setParents(modelLoader);
        });
    }

    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return this.bake(baker, this, textureGetter, rotationContainer, modelId, true);
    }

    public BakedModel bake(Baker baker, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        Sprite sprite = textureGetter.apply(this.resolveSprite(PARTICLE_KEY));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(baker, parent), sprite, this.getGuiLight().isSide());
        }
        BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(baker, parent), hasDepth).setParticle(sprite);
        for (ModelElement modelElement : this.getElements()) {
            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                Sprite sprite2 = textureGetter.apply(this.resolveSprite(modelElementFace.textureId));
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
                    continue;
                }
                builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
            }
        }
        return builder.build();
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
        return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade, id);
    }

    public boolean textureExists(String name) {
        return !MissingSprite.getMissingSpriteId().equals(this.resolveSprite(name).getTextureId());
    }

    public SpriteIdentifier resolveSprite(String spriteName) {
        if (JsonUnbakedModel.isTextureReference(spriteName)) {
            spriteName = spriteName.substring(1);
        }
        ArrayList<String> list = Lists.newArrayList();
        Either<SpriteIdentifier, String> either;
        Optional<SpriteIdentifier> optional;
        while (!(optional = (either = this.resolveTexture(spriteName)).left()).isPresent()) {
            spriteName = either.right().get();
            if (list.contains(spriteName)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), spriteName, this.id);
                return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId());
            }
            list.add(spriteName);
        }
        return optional.get();
    }

    private Either<SpriteIdentifier, String> resolveTexture(String name) {
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel != null) {
            Either<SpriteIdentifier, String> either = jsonUnbakedModel.textureMap.get(name);
            if (either != null) {
                return either;
            }
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId()));
    }

    static boolean isTextureReference(String reference) {
        return reference.charAt(0) == '#';
    }

    public JsonUnbakedModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ModelTransformation getTransformations() {
        Transformation transformation = this.getTransformation(ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = this.getTransformation(ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = this.getTransformation(ModelTransformationMode.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = this.getTransformation(ModelTransformationMode.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = this.getTransformation(ModelTransformationMode.HEAD);
        Transformation transformation6 = this.getTransformation(ModelTransformationMode.GUI);
        Transformation transformation7 = this.getTransformation(ModelTransformationMode.GROUND);
        Transformation transformation8 = this.getTransformation(ModelTransformationMode.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    private Transformation getTransformation(ModelTransformationMode renderMode) {
        if (this.parent != null && !this.transformations.isTransformationDefined(renderMode)) {
            return this.parent.getTransformation(renderMode);
        }
        return this.transformations.getTransformation(renderMode);
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum GuiLight {
        ITEM("front"),
        BLOCK("side");

        private final String name;

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight byName(String value) {
            for (GuiLight guiLight : GuiLight.values()) {
                if (!guiLight.name.equals(value)) continue;
                return guiLight;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == BLOCK;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        @Override
        public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.elementsFromJson(jsonDeserializationContext, jsonObject);
            String string = this.parentFromJson(jsonObject);
            Map<String, Either<SpriteIdentifier, String>> map = this.texturesFromJson(jsonObject);
            Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, (Type)((Object)ModelTransformation.class));
            }
            List<ModelOverride> list2 = this.overridesFromJson(jsonDeserializationContext, jsonObject);
            GuiLight guiLight = null;
            if (jsonObject.has("gui_light")) {
                guiLight = GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier identifier = string.isEmpty() ? null : new Identifier(string);
            return new JsonUnbakedModel(identifier, list, map, boolean_, guiLight, modelTransformation, list2);
        }

        protected List<ModelOverride> overridesFromJson(JsonDeserializationContext context, JsonObject object) {
            ArrayList<ModelOverride> list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add((ModelOverride)context.deserialize(jsonElement, (Type)((Object)ModelOverride.class)));
                }
            }
            return list;
        }

        private Map<String, Either<SpriteIdentifier, String>> texturesFromJson(JsonObject object) {
            Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
            HashMap<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    map.put(entry.getKey(), Deserializer.resolveReference(identifier, entry.getValue().getAsString()));
                }
            }
            return map;
        }

        private static Either<SpriteIdentifier, String> resolveReference(Identifier id, String name) {
            if (JsonUnbakedModel.isTextureReference(name)) {
                return Either.right(name.substring(1));
            }
            Identifier identifier = Identifier.tryParse(name);
            if (identifier == null) {
                throw new JsonParseException(name + " is not valid resource location");
            }
            return Either.left(new SpriteIdentifier(id, identifier));
        }

        private String parentFromJson(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        @Nullable
        protected Boolean ambientOcclusionFromJson(JsonObject json) {
            if (json.has("ambientocclusion")) {
                return JsonHelper.getBoolean(json, "ambientocclusion");
            }
            return null;
        }

        protected List<ModelElement> elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            ArrayList<ModelElement> list = Lists.newArrayList();
            if (json.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add((ModelElement)context.deserialize(jsonElement, (Type)((Object)ModelElement.class)));
                }
            }
            return list;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
            return this.deserialize(element, unused, ctx);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class UncheckedModelException
    extends RuntimeException {
        public UncheckedModelException(String message) {
            super(message);
        }
    }
}

