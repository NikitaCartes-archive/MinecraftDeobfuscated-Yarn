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
import com.mojang.datafixers.util.Pair;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)JsonUnbakedModel.class), new Deserializer()).registerTypeAdapter((Type)((Object)ModelElement.class), new ModelElement.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementFace.class), new ModelElementFace.Deserializer()).registerTypeAdapter((Type)((Object)ModelElementTexture.class), new ModelElementTexture.Deserializer()).registerTypeAdapter((Type)((Object)Transformation.class), new Transformation.Deserializer()).registerTypeAdapter((Type)((Object)ModelTransformation.class), new ModelTransformation.Deserializer()).registerTypeAdapter((Type)((Object)ModelItemOverride.class), new ModelItemOverride.Deserializer()).create();
    private final List<ModelElement> elements;
    private final boolean depthInGui;
    private final boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelItemOverride> overrides;
    public String id = "";
    @VisibleForTesting
    protected final Map<String, Either<SpriteIdentifier, String>> textureMap;
    @Nullable
    protected JsonUnbakedModel parent;
    @Nullable
    protected Identifier parentId;

    public static JsonUnbakedModel deserialize(Reader reader) {
        return JsonHelper.deserialize(GSON, reader, JsonUnbakedModel.class);
    }

    public static JsonUnbakedModel deserialize(String string) {
        return JsonUnbakedModel.deserialize(new StringReader(string));
    }

    public JsonUnbakedModel(@Nullable Identifier identifier, List<ModelElement> list, Map<String, Either<SpriteIdentifier, String>> map, boolean bl, boolean bl2, ModelTransformation modelTransformation, List<ModelItemOverride> list2) {
        this.elements = list;
        this.ambientOcclusion = bl;
        this.depthInGui = bl2;
        this.textureMap = map;
        this.parentId = identifier;
        this.transformations = modelTransformation;
        this.overrides = list2;
    }

    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public boolean useAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.useAmbientOcclusion();
        }
        return this.ambientOcclusion;
    }

    public boolean hasDepthInGui() {
        return this.depthInGui;
    }

    public List<ModelItemOverride> getOverrides() {
        return this.overrides;
    }

    private ModelItemPropertyOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel) {
        if (this.overrides.isEmpty()) {
            return ModelItemPropertyOverrideList.EMPTY;
        }
        return new ModelItemPropertyOverrideList(modelLoader, jsonUnbakedModel, modelLoader::getOrLoadModel, this.overrides);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        HashSet<Identifier> set = Sets.newHashSet();
        for (ModelItemOverride modelItemOverride : this.overrides) {
            set.add(modelItemOverride.getModelId());
        }
        if (this.parentId != null) {
            set.add(this.parentId);
        }
        return set;
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
        LinkedHashSet<JsonUnbakedModel> set2 = Sets.newLinkedHashSet();
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null) {
            set2.add(jsonUnbakedModel);
            UnbakedModel unbakedModel = function.apply(jsonUnbakedModel.parentId);
            if (unbakedModel == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentId, (Object)jsonUnbakedModel);
            }
            if (set2.contains(unbakedModel)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", (Object)jsonUnbakedModel, (Object)set2.stream().map(Object::toString).collect(Collectors.joining(" -> ")), (Object)this.parentId);
                unbakedModel = null;
            }
            if (unbakedModel == null) {
                jsonUnbakedModel.parentId = ModelLoader.MISSING;
                unbakedModel = function.apply(jsonUnbakedModel.parentId);
            }
            if (!(unbakedModel instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        HashSet<SpriteIdentifier> set3 = Sets.newHashSet(this.method_24077("particle"));
        for (ModelElement modelElement : this.getElements()) {
            for (ModelElementFace modelElementFace : modelElement.faces.values()) {
                SpriteIdentifier spriteIdentifier = this.method_24077(modelElementFace.textureId);
                if (Objects.equals(spriteIdentifier.getTextureId(), MissingSprite.getMissingSpriteId())) {
                    set.add(Pair.of(modelElementFace.textureId, this.id));
                }
                set3.add(spriteIdentifier);
            }
        }
        this.overrides.forEach(modelItemOverride -> {
            UnbakedModel unbakedModel = (UnbakedModel)function.apply(modelItemOverride.getModelId());
            if (Objects.equals(unbakedModel, this)) {
                return;
            }
            set3.addAll(unbakedModel.getTextureDependencies(function, set));
        });
        if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(string -> set3.add(this.method_24077((String)string)));
        }
        return set3;
    }

    @Override
    public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
        return this.bake(modelLoader, this, function, modelBakeSettings, identifier);
    }

    public BakedModel bake(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
        Sprite sprite = function.apply(this.method_24077("particle"));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(modelLoader, jsonUnbakedModel), sprite);
        }
        BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(modelLoader, jsonUnbakedModel)).setParticle(sprite);
        for (ModelElement modelElement : this.getElements()) {
            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                Sprite sprite2 = function.apply(this.method_24077(modelElementFace.textureId));
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, modelBakeSettings, identifier));
                    continue;
                }
                builder.addQuad(Direction.transform(modelBakeSettings.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, modelBakeSettings, identifier));
            }
        }
        return builder.build();
    }

    private static BakedQuad createQuad(ModelElement modelElement, ModelElementFace modelElementFace, Sprite sprite, Direction direction, ModelBakeSettings modelBakeSettings, Identifier identifier) {
        return QUAD_FACTORY.bake(modelElement.from, modelElement.to, modelElementFace, sprite, direction, modelBakeSettings, modelElement.rotation, modelElement.shade, identifier);
    }

    public boolean textureExists(String string) {
        return !MissingSprite.getMissingSpriteId().equals(this.method_24077(string).getTextureId());
    }

    public SpriteIdentifier method_24077(String string) {
        if (JsonUnbakedModel.isTextureReference(string)) {
            string = string.substring(1);
        }
        ArrayList<String> list = Lists.newArrayList();
        Either<SpriteIdentifier, String> either;
        Optional<SpriteIdentifier> optional;
        while (!(optional = (either = this.resolveTexture(string)).left()).isPresent()) {
            string = either.right().get();
            if (list.contains(string)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", (Object)Joiner.on("->").join(list), (Object)string, (Object)this.id);
                return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId());
            }
            list.add(string);
        }
        return optional.get();
    }

    private Either<SpriteIdentifier, String> resolveTexture(String string) {
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel != null) {
            Either<SpriteIdentifier, String> either = jsonUnbakedModel.textureMap.get(string);
            if (either != null) {
                return either;
            }
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        return Either.left(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId()));
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
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    private Transformation getTransformation(ModelTransformation.Type type) {
        if (this.parent != null && !this.transformations.isTransformationDefined(type)) {
            return this.parent.getTransformation(type);
        }
        return this.transformations.getTransformation(type);
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        @Override
        public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.deserializeElements(jsonDeserializationContext, jsonObject);
            String string = this.deserializeParent(jsonObject);
            Map<String, Either<SpriteIdentifier, String>> map = this.deserializeTextures(jsonObject);
            boolean bl = this.deserializeAmbientOcclusion(jsonObject);
            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, (Type)((Object)ModelTransformation.class));
            }
            List<ModelItemOverride> list2 = this.deserializeOverrides(jsonDeserializationContext, jsonObject);
            Identifier identifier = string.isEmpty() ? null : new Identifier(string);
            return new JsonUnbakedModel(identifier, list, map, bl, true, modelTransformation, list2);
        }

        protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            ArrayList<ModelItemOverride> list = Lists.newArrayList();
            if (jsonObject.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add((ModelItemOverride)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)ModelItemOverride.class)));
                }
            }
            return list;
        }

        private Map<String, Either<SpriteIdentifier, String>> deserializeTextures(JsonObject jsonObject) {
            Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEX;
            HashMap<String, Either<SpriteIdentifier, String>> map = Maps.newHashMap();
            if (jsonObject.has("textures")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "textures");
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    map.put(entry.getKey(), Deserializer.method_24079(identifier, entry.getValue().getAsString()));
                }
            }
            return map;
        }

        private static Either<SpriteIdentifier, String> method_24079(Identifier identifier, String string) {
            if (JsonUnbakedModel.isTextureReference(string)) {
                return Either.right(string.substring(1));
            }
            Identifier identifier2 = Identifier.tryParse(string);
            if (identifier2 == null) {
                throw new JsonParseException(string + " is not valid resource location");
            }
            return Either.left(new SpriteIdentifier(identifier, identifier2));
        }

        private String deserializeParent(JsonObject jsonObject) {
            return JsonHelper.getString(jsonObject, "parent", "");
        }

        protected boolean deserializeAmbientOcclusion(JsonObject jsonObject) {
            return JsonHelper.getBoolean(jsonObject, "ambientocclusion", true);
        }

        protected List<ModelElement> deserializeElements(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
            ArrayList<ModelElement> list = Lists.newArrayList();
            if (jsonObject.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "elements")) {
                    list.add((ModelElement)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)ModelElement.class)));
                }
            }
            return list;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

