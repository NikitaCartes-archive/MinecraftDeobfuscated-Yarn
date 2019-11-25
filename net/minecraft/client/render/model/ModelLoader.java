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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.container.PlayerContainer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelLoader {
    public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/fire_0"));
    public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/fire_1"));
    public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/lava_flow"));
    public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/water_flow"));
    public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("block/water_overlay"));
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier("entity/shield_base_nopattern"));
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(i -> new Identifier("block/destroy_stage_" + i)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getCrumbling).collect(Collectors.toList());
    private static final Set<SpriteIdentifier> DEFAULT_TEXTURES = Util.create(Sets.newHashSet(), hashSet -> {
        hashSet.add(WATER_FLOW);
        hashSet.add(LAVA_FLOW);
        hashSet.add(WATER_OVERLAY);
        hashSet.add(FIRE_0);
        hashSet.add(FIRE_1);
        hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.BASE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.CAGE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.WIND_TEX);
        hashSet.add(ConduitBlockEntityRenderer.WIND_VERTICAL_TEX);
        hashSet.add(ConduitBlockEntityRenderer.OPEN_EYE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.CLOSED_EYE_TEX);
        hashSet.add(EnchantingTableBlockEntityRenderer.BOOK_TEX);
        hashSet.add(BANNER_BASE);
        hashSet.add(SHIELD_BASE);
        hashSet.add(SHIELD_BASE_NO_PATTERN);
        for (Identifier identifier : BLOCK_DESTRUCTION_STAGES) {
            hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, identifier));
        }
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerContainer.field_21669));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerContainer.field_21670));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerContainer.field_21671));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerContainer.field_21672));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEX, PlayerContainer.field_21673));
        TexturedRenderLayers.addDefaultTextures(hashSet::add);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
    private static final String field_21773 = MISSING.toString();
    @VisibleForTesting
    public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().getPath() + "',       'missingno': '" + MissingSprite.getMissingSpriteId().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_DEFINITION));
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
    public static final JsonUnbakedModel GENERATION_MARKER = Util.create(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "generation marker";
    });
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.create(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "block entity marker";
    });
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    @Nullable
    private SpriteAtlasManager spriteAtlasManager;
    private final BlockColors colorationManager;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, Rotation3, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;
    private int nextStateId = 1;
    private final Object2IntMap<BlockState> stateLookup = Util.create(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i) {
        this.resourceManager = resourceManager;
        this.colorationManager = blockColors;
        profiler.push("missing_model");
        try {
            this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
            this.addModel(MISSING);
        } catch (IOException iOException) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException);
            throw new RuntimeException(iOException);
        }
        profiler.swap("static_definitions");
        STATIC_DEFINITIONS.forEach((identifier, stateManager) -> stateManager.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState))));
        profiler.swap("blocks");
        for (Block block : Registry.BLOCK) {
            block.getStateManager().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
        }
        profiler.swap("items");
        for (Identifier identifier2 : Registry.ITEM.getIds()) {
            this.addModel(new ModelIdentifier(identifier2, "inventory"));
        }
        profiler.swap("special");
        this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        profiler.swap("textures");
        LinkedHashSet set = Sets.newLinkedHashSet();
        Set set2 = this.modelsToBake.values().stream().flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(DEFAULT_TEXTURES);
        set.stream().filter(pair -> !((String)pair.getSecond()).equals(field_21773)).forEach(pair -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
        Map<Identifier, List<SpriteIdentifier>> map = set2.stream().collect(Collectors.groupingBy(SpriteIdentifier::getAtlasId));
        profiler.swap("stitching");
        this.spriteAtlasData = Maps.newHashMap();
        for (Map.Entry<Identifier, List<SpriteIdentifier>> entry : map.entrySet()) {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture(entry.getKey());
            SpriteAtlasTexture.Data data = spriteAtlasTexture.stitch(this.resourceManager, entry.getValue().stream().map(SpriteIdentifier::getTextureId), profiler, i);
            this.spriteAtlasData.put(entry.getKey(), Pair.of(spriteAtlasTexture, data));
        }
        profiler.pop();
    }

    public SpriteAtlasManager upload(TextureManager textureManager, Profiler profiler) {
        profiler.push("atlas");
        for (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data> pair : this.spriteAtlasData.values()) {
            SpriteAtlasTexture spriteAtlasTexture = pair.getFirst();
            SpriteAtlasTexture.Data data = pair.getSecond();
            spriteAtlasTexture.upload(data);
            textureManager.registerTexture(spriteAtlasTexture.getId(), spriteAtlasTexture);
            textureManager.bindTexture(spriteAtlasTexture.getId());
            spriteAtlasTexture.method_24198(data);
        }
        this.spriteAtlasManager = new SpriteAtlasManager(this.spriteAtlasData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
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
        return this.spriteAtlasManager;
    }

    private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> stateManager, String string) {
        HashMap<Property<?>, ?> map = Maps.newHashMap();
        for (String string2 : COMMA_SPLITTER.split(string)) {
            Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string2).iterator();
            if (!iterator.hasNext()) continue;
            String string3 = iterator.next();
            Property<?> property = stateManager.getProperty(string3);
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
        Block block = stateManager.getOwner();
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
        return (T)((Comparable)property.parse(string).orElse(null));
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

    private void loadModel(Identifier identifier) throws Exception {
        if (!(identifier instanceof ModelIdentifier)) {
            this.putModel(identifier, this.loadModelFromJson(identifier));
            return;
        }
        ModelIdentifier modelIdentifier2 = (ModelIdentifier)identifier;
        if (Objects.equals(modelIdentifier2.getVariant(), "inventory")) {
            Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
            JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
            this.putModel(modelIdentifier2, jsonUnbakedModel);
            this.unbakedModels.put(identifier2, jsonUnbakedModel);
        } else {
            Identifier identifier2 = new Identifier(identifier.getNamespace(), identifier.getPath());
            StateManager stateManager = Optional.ofNullable(STATIC_DEFINITIONS.get(identifier2)).orElseGet(() -> Registry.BLOCK.get(identifier2).getStateManager());
            this.variantMapDeserializationContext.setStateFactory(stateManager);
            ImmutableList<Property<?>> list = ImmutableList.copyOf(this.colorationManager.getProperties((Block)stateManager.getOwner()));
            ImmutableList immutableList = stateManager.getStates();
            HashMap<ModelIdentifier, BlockState> map = Maps.newHashMap();
            immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier2, blockState), (BlockState)blockState));
            HashMap map2 = Maps.newHashMap();
            Identifier identifier3 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);
            ModelDefinition modelDefinition2 = new ModelDefinition(ImmutableList.of(unbakedModel), ImmutableList.of());
            Pair<UnbakedModel, Supplier<ModelDefinition>> pair = Pair.of(unbakedModel, () -> modelDefinition2);
            try {
                List list2;
                try {
                    list2 = this.resourceManager.getAllResources(identifier3).stream().map(resource -> {
                        try (InputStream inputStream = resource.getInputStream();){
                            Pair<String, ModelVariantMap> pair = Pair.of(resource.getResourcePackName(), ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                            return pair;
                        } catch (Exception exception) {
                            throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), exception.getMessage()));
                        }
                    }).collect(Collectors.toList());
                } catch (IOException iOException) {
                    LOGGER.warn("Exception loading blockstate definition: {}: {}", (Object)identifier3, (Object)iOException);
                    HashMap<ModelDefinition, Set> map3 = Maps.newHashMap();
                    map.forEach((modelIdentifier, blockState) -> {
                        Pair pair2 = (Pair)map2.get(blockState);
                        if (pair2 == null) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, modelIdentifier);
                            pair2 = pair;
                        }
                        this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                        try {
                            ModelDefinition modelDefinition2 = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                            map3.computeIfAbsent(modelDefinition2, modelDefinition -> Sets.newIdentityHashSet()).add(blockState);
                        } catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                        }
                    });
                    map3.forEach((modelDefinition, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState blockState = (BlockState)iterator.next();
                            if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                            iterator.remove();
                            this.stateLookup.put(blockState, 0);
                        }
                        if (set.size() > 1) {
                            this.addStates((Iterable<BlockState>)set);
                        }
                    });
                    return;
                }
                for (Pair pair2 : list2) {
                    MultipartUnbakedModel multipartUnbakedModel;
                    ModelVariantMap modelVariantMap = (ModelVariantMap)pair2.getSecond();
                    IdentityHashMap map4 = Maps.newIdentityHashMap();
                    if (modelVariantMap.hasMultipartModel()) {
                        multipartUnbakedModel = modelVariantMap.getMultipartModel();
                        immutableList.forEach(blockState -> map4.put(blockState, Pair.of(multipartUnbakedModel, () -> ModelDefinition.create(blockState, multipartUnbakedModel, list))));
                    } else {
                        multipartUnbakedModel = null;
                    }
                    modelVariantMap.getVariantMap().forEach((string, weightedUnbakedModel) -> {
                        try {
                            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateManager, string)).forEach(blockState -> {
                                Pair<WeightedUnbakedModel, Supplier<ModelDefinition>> pair2 = map4.put(blockState, Pair.of(weightedUnbakedModel, () -> ModelDefinition.create(blockState, weightedUnbakedModel, list)));
                                if (pair2 != null && pair2.getFirst() != multipartUnbakedModel) {
                                    map4.put(blockState, pair);
                                    throw new RuntimeException("Overlapping definition with: " + (String)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair2.getFirst()).findFirst().get().getKey());
                                }
                            });
                        } catch (Exception exception) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", (Object)identifier3, pair2.getFirst(), string, (Object)exception.getMessage());
                        }
                    });
                    map2.putAll(map4);
                }
            } catch (ModelLoaderException modelLoaderException) {
                throw modelLoaderException;
            } catch (Exception exception) {
                throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier3, exception));
            } finally {
                HashMap<ModelDefinition, Set> map6 = Maps.newHashMap();
                map.forEach((modelIdentifier, blockState) -> {
                    Pair pair2 = (Pair)map2.get(blockState);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, modelIdentifier);
                        pair2 = pair;
                    }
                    this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                    try {
                        ModelDefinition modelDefinition2 = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                        map3.computeIfAbsent(modelDefinition2, modelDefinition -> Sets.newIdentityHashSet()).add(blockState);
                    } catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                    }
                });
                map6.forEach((modelDefinition, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState blockState = (BlockState)iterator.next();
                        if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                        iterator.remove();
                        this.stateLookup.put(blockState, 0);
                    }
                    if (set.size() > 1) {
                        this.addStates((Iterable<BlockState>)set);
                    }
                });
            }
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

    private void addStates(Iterable<BlockState> iterable) {
        int i = this.nextStateId++;
        iterable.forEach(blockState -> this.stateLookup.put((BlockState)blockState, i));
    }

    @Nullable
    public BakedModel bake(Identifier identifier, ModelBakeSettings modelBakeSettings) {
        JsonUnbakedModel jsonUnbakedModel;
        Triple<Identifier, Rotation3, Boolean> triple = Triple.of(identifier, modelBakeSettings.getRotation(), modelBakeSettings.isShaded());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        }
        if (this.spriteAtlasManager == null) {
            throw new IllegalStateException("bake called too early");
        }
        UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
        if (unbakedModel instanceof JsonUnbakedModel && (jsonUnbakedModel = (JsonUnbakedModel)unbakedModel).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlasManager::getSprite, modelBakeSettings, identifier);
        }
        BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlasManager::getSprite, modelBakeSettings, identifier);
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

    public Object2IntMap<BlockState> getStateLookup() {
        return this.stateLookup;
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelDefinition {
        private final List<UnbakedModel> components;
        private final List<Object> values;

        public ModelDefinition(List<UnbakedModel> list, List<Object> list2) {
            this.components = list;
            this.values = list2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof ModelDefinition) {
                ModelDefinition modelDefinition = (ModelDefinition)object;
                return Objects.equals(this.components, modelDefinition.components) && Objects.equals(this.values, modelDefinition.values);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.components.hashCode() + this.values.hashCode();
        }

        public static ModelDefinition create(BlockState blockState, MultipartUnbakedModel multipartUnbakedModel, Collection<Property<?>> collection) {
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            List list = multipartUnbakedModel.getComponents().stream().filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(blockState)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            List<Object> list2 = ModelDefinition.getStateValues(blockState, collection);
            return new ModelDefinition(list, list2);
        }

        public static ModelDefinition create(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
            List<Object> list = ModelDefinition.getStateValues(blockState, collection);
            return new ModelDefinition(ImmutableList.of(unbakedModel), list);
        }

        private static List<Object> getStateValues(BlockState blockState, Collection<Property<?>> collection) {
            return collection.stream().map(blockState::get).collect(ImmutableList.toImmutableList());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelLoaderException
    extends RuntimeException {
        public ModelLoaderException(String string) {
            super(string);
        }
    }
}

