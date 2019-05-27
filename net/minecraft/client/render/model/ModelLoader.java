/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelLoader {
    public static final Identifier FIRE_0 = new Identifier("block/fire_0");
    public static final Identifier FIRE_1 = new Identifier("block/fire_1");
    public static final Identifier LAVA_FLOW = new Identifier("block/lava_flow");
    public static final Identifier WATER_FLOW = new Identifier("block/water_flow");
    public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");
    public static final Identifier DESTROY_STAGE_0 = new Identifier("block/destroy_stage_0");
    public static final Identifier DESTROY_STAGE_1 = new Identifier("block/destroy_stage_1");
    public static final Identifier DESTROY_STAGE_2 = new Identifier("block/destroy_stage_2");
    public static final Identifier DESTROY_STAGE_3 = new Identifier("block/destroy_stage_3");
    public static final Identifier DESTROY_STAGE_4 = new Identifier("block/destroy_stage_4");
    public static final Identifier DESTROY_STAGE_5 = new Identifier("block/destroy_stage_5");
    public static final Identifier DESTROY_STAGE_6 = new Identifier("block/destroy_stage_6");
    public static final Identifier DESTROY_STAGE_7 = new Identifier("block/destroy_stage_7");
    public static final Identifier DESTROY_STAGE_8 = new Identifier("block/destroy_stage_8");
    public static final Identifier DESTROY_STAGE_9 = new Identifier("block/destroy_stage_9");
    private static final Set<Identifier> DEFAULT_TEXTURES = Sets.newHashSet(WATER_FLOW, LAVA_FLOW, WATER_OVERLAY, FIRE_0, FIRE_1, DESTROY_STAGE_0, DESTROY_STAGE_1, DESTROY_STAGE_2, DESTROY_STAGE_3, DESTROY_STAGE_4, DESTROY_STAGE_5, DESTROY_STAGE_6, DESTROY_STAGE_7, DESTROY_STAGE_8, DESTROY_STAGE_9, new Identifier("item/empty_armor_slot_helmet"), new Identifier("item/empty_armor_slot_chestplate"), new Identifier("item/empty_armor_slot_leggings"), new Identifier("item/empty_armor_slot_boots"), new Identifier("item/empty_armor_slot_shield"));
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
    @VisibleForTesting
    public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().getPath() + "',       'missingno': '" + MissingSprite.getMissingSpriteId().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_DEFINITION));
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
    public static final JsonUnbakedModel GENERATION_MARKER = SystemUtil.consume(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "generation marker";
    });
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = SystemUtil.consume(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "block entity marker";
    });
    private static final StateFactory<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateFactory.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateFactory<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    private final SpriteAtlasTexture spriteAtlas;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final SpriteAtlasTexture.Data spriteAtlasData;

    public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, Profiler profiler) {
        this.resourceManager = resourceManager;
        this.spriteAtlas = spriteAtlasTexture;
        profiler.push("missing_model");
        try {
            this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
            this.addModel(MISSING);
        } catch (IOException iOException) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException);
            throw new RuntimeException(iOException);
        }
        profiler.swap("static_definitions");
        STATIC_DEFINITIONS.forEach((identifier, stateFactory) -> stateFactory.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState))));
        profiler.swap("blocks");
        for (Block block : Registry.BLOCK) {
            block.getStateFactory().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
        }
        profiler.swap("items");
        for (Identifier identifier2 : Registry.ITEM.getIds()) {
            this.addModel(new ModelIdentifier(identifier2, "inventory"));
        }
        profiler.swap("special");
        this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        profiler.swap("textures");
        LinkedHashSet set = Sets.newLinkedHashSet();
        Set<Identifier> set2 = this.modelsToBake.values().stream().flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(DEFAULT_TEXTURES);
        set.forEach(string -> LOGGER.warn("Unable to resolve texture reference: {}", string));
        profiler.swap("stitching");
        this.spriteAtlasData = this.spriteAtlas.stitch(this.resourceManager, set2, profiler);
        profiler.pop();
    }

    public void upload(Profiler profiler) {
        profiler.push("atlas");
        this.spriteAtlas.upload(this.spriteAtlasData);
        profiler.swap("baking");
        this.modelsToBake.keySet().forEach(identifier -> {
            BakedModel bakedModel = null;
            try {
                bakedModel = this.bake((Identifier)identifier, ModelRotation.X0_Y0);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", identifier, (Object)exception);
            }
            if (bakedModel != null) {
                this.bakedModels.put((Identifier)identifier, bakedModel);
            }
        });
        profiler.pop();
    }

    private static Predicate<BlockState> stateKeyToPredicate(StateFactory<Block, BlockState> stateFactory, String string) {
        HashMap<Property<?>, ?> map = Maps.newHashMap();
        for (String string2 : COMMA_SPLITTER.split(string)) {
            Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string2).iterator();
            if (!iterator.hasNext()) continue;
            String string3 = iterator.next();
            Property<?> property = stateFactory.getProperty(string3);
            if (property != null && iterator.hasNext()) {
                String string4 = iterator.next();
                Object comparable = ModelLoader.getPropertyValue(property, string4);
                if (comparable != null) {
                    map.put(property, comparable);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getValues());
            }
            if (string3.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
        }
        Block block = stateFactory.getBaseObject();
        return blockState -> {
            if (blockState == null || block != blockState.getBlock()) {
                return false;
            }
            for (Map.Entry entry : map.entrySet()) {
                if (Objects.equals(blockState.get((Property)entry.getKey()), entry.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    static <T extends Comparable<T>> T getPropertyValue(Property<T> property, String string) {
        return (T)((Comparable)property.getValue(string).orElse(null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UnbakedModel getOrLoadModel(Identifier identifier) {
        if (this.unbakedModels.containsKey(identifier)) {
            return this.unbakedModels.get(identifier);
        }
        if (this.modelsToLoad.contains(identifier)) {
            throw new IllegalStateException("Circular reference while loading " + identifier);
        }
        this.modelsToLoad.add(identifier);
        UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);
        while (!this.modelsToLoad.isEmpty()) {
            Identifier identifier2 = this.modelsToLoad.iterator().next();
            try {
                if (this.unbakedModels.containsKey(identifier2)) continue;
                this.loadModel(identifier2);
            } catch (ModelLoaderException modelLoaderException) {
                LOGGER.warn(modelLoaderException.getMessage());
                this.unbakedModels.put(identifier2, unbakedModel);
            } catch (Exception exception) {
                LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", (Object)identifier2, (Object)identifier, (Object)exception);
                this.unbakedModels.put(identifier2, unbakedModel);
            } finally {
                this.modelsToLoad.remove(identifier2);
            }
        }
        return this.unbakedModels.getOrDefault(identifier, unbakedModel);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void loadModel(Identifier identifier) throws Exception {
        Object list;
        if (!(identifier instanceof ModelIdentifier)) {
            this.putModel(identifier, this.loadModelFromJson(identifier));
            return;
        }
        ModelIdentifier modelIdentifier = (ModelIdentifier)identifier;
        if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
            Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
            JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
            this.putModel(modelIdentifier, jsonUnbakedModel);
            this.unbakedModels.put(identifier2, jsonUnbakedModel);
            return;
        }
        Identifier identifier2 = new Identifier(identifier.getNamespace(), identifier.getPath());
        StateFactory stateFactory = Optional.ofNullable(STATIC_DEFINITIONS.get(identifier2)).orElseGet(() -> Registry.BLOCK.get(identifier2).getStateFactory());
        this.variantMapDeserializationContext.setStateFactory(stateFactory);
        ImmutableList immutableList = stateFactory.getStates();
        HashMap map = Maps.newHashMap();
        immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier2, blockState), blockState));
        HashMap map2 = Maps.newHashMap();
        Identifier identifier3 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
        try {
            try {
                list = this.resourceManager.getAllResources(identifier3).stream().map(resource -> {
                    try (InputStream inputStream = resource.getInputStream();){
                        Pair<String, ModelVariantMap> pair = Pair.of(resource.getResourcePackName(), ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                        return pair;
                    } catch (Exception exception) {
                        throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), exception.getMessage()));
                    }
                }).collect(Collectors.toList());
            } catch (IOException iOException) {
                LOGGER.warn("Exception loading blockstate definition: {}: {}", (Object)identifier3, (Object)iOException);
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();
                    UnbakedModel unbakedModel = (UnbakedModel)map2.get(entry.getValue());
                    if (unbakedModel == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, entry.getKey());
                        unbakedModel = this.unbakedModels.get(MISSING);
                    }
                    this.putModel((Identifier)entry.getKey(), unbakedModel);
                }
                return;
            }
        } catch (ModelLoaderException modelLoaderException) {
            try {
                throw modelLoaderException;
                catch (Exception exception) {
                    throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier3, exception));
                }
            } catch (Throwable throwable) {
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry3 = iterator.next();
                    UnbakedModel unbakedModel4 = (UnbakedModel)map2.get(entry3.getValue());
                    if (unbakedModel4 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, entry3.getKey());
                        unbakedModel4 = this.unbakedModels.get(MISSING);
                    }
                    this.putModel((Identifier)entry3.getKey(), unbakedModel4);
                }
                throw throwable;
            }
        }
        {
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                MultipartUnbakedModel unbakedModel2;
                Pair pair = (Pair)iterator.next();
                ModelVariantMap modelVariantMap = (ModelVariantMap)pair.getSecond();
                IdentityHashMap map3 = Maps.newIdentityHashMap();
                if (modelVariantMap.hasMultipartModel()) {
                    unbakedModel2 = modelVariantMap.getMultipartModel();
                    immutableList.forEach(blockState -> map3.put(blockState, unbakedModel2));
                } else {
                    unbakedModel2 = null;
                }
                modelVariantMap.getVariantMap().forEach((string, weightedUnbakedModel) -> {
                    try {
                        immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateFactory, string)).forEach(blockState -> {
                            UnbakedModel unbakedModel2 = map3.put(blockState, weightedUnbakedModel);
                            if (unbakedModel2 != null && unbakedModel2 != unbakedModel2) {
                                map3.put(blockState, this.unbakedModels.get(MISSING));
                                throw new RuntimeException("Overlapping definition with: " + (String)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == unbakedModel2).findFirst().get().getKey());
                            }
                        });
                    } catch (Exception exception) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", (Object)identifier3, pair.getFirst(), string, (Object)exception.getMessage());
                    }
                });
                map2.putAll(map3);
            }
        }
        list = map.entrySet().iterator();
        while (list.hasNext()) {
            Map.Entry entry = (Map.Entry)list.next();
            UnbakedModel unbakedModel3 = (UnbakedModel)map2.get(entry.getValue());
            if (unbakedModel3 == null) {
                LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, entry.getKey());
                unbakedModel3 = this.unbakedModels.get(MISSING);
            }
            this.putModel((Identifier)entry.getKey(), unbakedModel3);
        }
    }

    private void putModel(Identifier identifier, UnbakedModel unbakedModel) {
        this.unbakedModels.put(identifier, unbakedModel);
        this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
    }

    private void addModel(ModelIdentifier modelIdentifier) {
        UnbakedModel unbakedModel = this.getOrLoadModel(modelIdentifier);
        this.unbakedModels.put(modelIdentifier, unbakedModel);
        this.modelsToBake.put(modelIdentifier, unbakedModel);
    }

    @Nullable
    public BakedModel bake(Identifier identifier, ModelBakeSettings modelBakeSettings) {
        JsonUnbakedModel jsonUnbakedModel;
        Triple<Identifier, ModelRotation, Boolean> triple = Triple.of(identifier, modelBakeSettings.getRotation(), modelBakeSettings.isUvLocked());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        }
        UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
        if (unbakedModel instanceof JsonUnbakedModel && (jsonUnbakedModel = (JsonUnbakedModel)unbakedModel).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, modelBakeSettings);
        }
        BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, modelBakeSettings);
        this.bakedModelCache.put(triple, bakedModel);
        return bakedModel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonUnbakedModel loadModelFromJson(Identifier identifier) throws IOException {
        String string;
        Resource resource;
        Reader reader;
        block8: {
            block7: {
                JsonUnbakedModel jsonUnbakedModel;
                reader = null;
                resource = null;
                try {
                    string = identifier.getPath();
                    if (!"builtin/generated".equals(string)) break block7;
                    jsonUnbakedModel = GENERATION_MARKER;
                } catch (Throwable throwable) {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);
                return jsonUnbakedModel;
            }
            if (!"builtin/entity".equals(string)) break block8;
            JsonUnbakedModel jsonUnbakedModel = BLOCK_ENTITY_MARKER;
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(resource);
            return jsonUnbakedModel;
        }
        if (string.startsWith("builtin/")) {
            String string2 = string.substring("builtin/".length());
            String string3 = BUILTIN_MODEL_DEFINITIONS.get(string2);
            if (string3 == null) {
                throw new FileNotFoundException(identifier.toString());
            }
            reader = new StringReader(string3);
        } else {
            resource = this.resourceManager.getResource(new Identifier(identifier.getNamespace(), "models/" + identifier.getPath() + ".json"));
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        }
        JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
        jsonUnbakedModel.id = identifier.toString();
        JsonUnbakedModel jsonUnbakedModel2 = jsonUnbakedModel;
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly((Closeable)resource);
        return jsonUnbakedModel2;
    }

    public Map<Identifier, BakedModel> getBakedModelMap() {
        return this.bakedModels;
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelLoaderException
    extends RuntimeException {
        public ModelLoaderException(String string) {
            super(string);
        }
    }
}

