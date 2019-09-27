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
import java.util.Arrays;
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
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.class_4590;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
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
    public static final Identifier field_20845 = new Identifier("entity/shulker/shulker");
    public static final List<Identifier> field_20846 = ImmutableList.of(new Identifier("entity/shulker/shulker_white"), new Identifier("entity/shulker/shulker_orange"), new Identifier("entity/shulker/shulker_magenta"), new Identifier("entity/shulker/shulker_light_blue"), new Identifier("entity/shulker/shulker_yellow"), new Identifier("entity/shulker/shulker_lime"), new Identifier("entity/shulker/shulker_pink"), new Identifier("entity/shulker/shulker_gray"), new Identifier("entity/shulker/shulker_light_gray"), new Identifier("entity/shulker/shulker_cyan"), new Identifier("entity/shulker/shulker_purple"), new Identifier("entity/shulker/shulker_blue"), new Identifier[]{new Identifier("entity/shulker/shulker_brown"), new Identifier("entity/shulker/shulker_green"), new Identifier("entity/shulker/shulker_red"), new Identifier("entity/shulker/shulker_black")});
    public static final Identifier field_20847 = new Identifier("entity/banner_base");
    public static final Identifier field_21014 = new Identifier("entity/signs/oak");
    public static final Identifier field_21015 = new Identifier("entity/signs/spruce");
    public static final Identifier field_21016 = new Identifier("entity/signs/birch");
    public static final Identifier field_21017 = new Identifier("entity/signs/acacia");
    public static final Identifier field_21018 = new Identifier("entity/signs/jungle");
    public static final Identifier field_21019 = new Identifier("entity/signs/dark_oak");
    public static final List<Identifier> field_20848 = IntStream.range(0, 10).mapToObj(i -> new Identifier("block/destroy_stage_" + i)).collect(Collectors.toList());
    public static final List<Identifier> field_21020 = field_20848.stream().map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png")).collect(Collectors.toList());
    private static final Set<Identifier> DEFAULT_TEXTURES = SystemUtil.consume(Sets.newHashSet(), hashSet -> {
        hashSet.add(WATER_FLOW);
        hashSet.add(LAVA_FLOW);
        hashSet.add(WATER_OVERLAY);
        hashSet.add(FIRE_0);
        hashSet.add(FIRE_1);
        hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
        hashSet.addAll(Arrays.asList(BedBlockEntityRenderer.TEXTURES));
        hashSet.add(ChestBlockEntityRenderer.TRAPPED_DOUBLE_TEX);
        hashSet.add(ChestBlockEntityRenderer.CHRISTMAS_DOUBLE_TEX);
        hashSet.add(ChestBlockEntityRenderer.NORMAL_DOUBLE_TEX);
        hashSet.add(ChestBlockEntityRenderer.TRAPPED_TEX);
        hashSet.add(ChestBlockEntityRenderer.CHRISTMAS_TEX);
        hashSet.add(ChestBlockEntityRenderer.NORMAL_TEX);
        hashSet.add(ChestBlockEntityRenderer.ENDER_TEX);
        hashSet.add(ConduitBlockEntityRenderer.BASE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.CAGE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.WIND_TEX);
        hashSet.add(ConduitBlockEntityRenderer.WIND_VERTICAL_TEX);
        hashSet.add(ConduitBlockEntityRenderer.OPEN_EYE_TEX);
        hashSet.add(ConduitBlockEntityRenderer.CLOSED_EYE_TEX);
        hashSet.add(EnchantingTableBlockEntityRenderer.BOOK_TEX);
        hashSet.add(field_20845);
        hashSet.addAll(field_20846);
        hashSet.add(field_20847);
        for (BannerPattern bannerPattern : BannerPattern.values()) {
            hashSet.add(bannerPattern.method_22536());
        }
        hashSet.add(field_21014);
        hashSet.add(field_21015);
        hashSet.add(field_21016);
        hashSet.add(field_21017);
        hashSet.add(field_21018);
        hashSet.add(field_21019);
        hashSet.addAll(field_20848);
        hashSet.add(new Identifier("item/empty_armor_slot_helmet"));
        hashSet.add(new Identifier("item/empty_armor_slot_chestplate"));
        hashSet.add(new Identifier("item/empty_armor_slot_leggings"));
        hashSet.add(new Identifier("item/empty_armor_slot_boots"));
        hashSet.add(new Identifier("item/empty_armor_slot_shield"));
    });
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
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    private final SpriteAtlasTexture spriteAtlas;
    private final BlockColors field_20272;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, class_4590, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final SpriteAtlasTexture.Data spriteAtlasData;
    private int field_20273 = 1;
    private final Object2IntMap<BlockState> field_20274 = SystemUtil.consume(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, BlockColors blockColors, Profiler profiler) {
        this.resourceManager = resourceManager;
        this.spriteAtlas = spriteAtlasTexture;
        this.field_20272 = blockColors;
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
            StateManager stateManager = Optional.ofNullable(STATIC_DEFINITIONS.get(identifier2)).orElseGet(() -> Registry.BLOCK.get(identifier2).getStateFactory());
            this.variantMapDeserializationContext.setStateFactory(stateManager);
            ImmutableList<Property<?>> list = ImmutableList.copyOf(this.field_20272.getProperties((Block)stateManager.getOwner()));
            ImmutableList immutableList = stateManager.getStates();
            HashMap<ModelIdentifier, BlockState> map = Maps.newHashMap();
            immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier2, blockState), (BlockState)blockState));
            HashMap map2 = Maps.newHashMap();
            Identifier identifier3 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);
            class_4455 lv = new class_4455(ImmutableList.of(unbakedModel), ImmutableList.of());
            Pair<UnbakedModel, Supplier<class_4455>> pair = Pair.of(unbakedModel, () -> lv);
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
                    HashMap<class_4455, Set> map3 = Maps.newHashMap();
                    map.forEach((modelIdentifier, blockState) -> {
                        Pair pair2 = (Pair)map2.get(blockState);
                        if (pair2 == null) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, modelIdentifier);
                            pair2 = pair;
                        }
                        this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                        try {
                            class_4455 lv = (class_4455)((Supplier)pair2.getSecond()).get();
                            map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(blockState);
                        } catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                        }
                    });
                    map3.forEach((arg, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState blockState = (BlockState)iterator.next();
                            if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                            iterator.remove();
                            this.field_20274.put(blockState, 0);
                        }
                        if (set.size() > 1) {
                            this.method_21603((Iterable<BlockState>)set);
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
                        immutableList.forEach(blockState -> map4.put(blockState, Pair.of(multipartUnbakedModel, () -> class_4455.method_21607(blockState, multipartUnbakedModel, list))));
                    } else {
                        multipartUnbakedModel = null;
                    }
                    modelVariantMap.getVariantMap().forEach((string, weightedUnbakedModel) -> {
                        try {
                            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateManager, string)).forEach(blockState -> {
                                Pair<WeightedUnbakedModel, Supplier<class_4455>> pair2 = map4.put(blockState, Pair.of(weightedUnbakedModel, () -> class_4455.method_21608(blockState, weightedUnbakedModel, list)));
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
                HashMap<class_4455, Set> map6 = Maps.newHashMap();
                map.forEach((modelIdentifier, blockState) -> {
                    Pair pair2 = (Pair)map2.get(blockState);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier3, modelIdentifier);
                        pair2 = pair;
                    }
                    this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                    try {
                        class_4455 lv = (class_4455)((Supplier)pair2.getSecond()).get();
                        map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(blockState);
                    } catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                    }
                });
                map6.forEach((arg, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState blockState = (BlockState)iterator.next();
                        if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                        iterator.remove();
                        this.field_20274.put(blockState, 0);
                    }
                    if (set.size() > 1) {
                        this.method_21603((Iterable<BlockState>)set);
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

    private void method_21603(Iterable<BlockState> iterable) {
        int i = this.field_20273++;
        iterable.forEach(blockState -> this.field_20274.put((BlockState)blockState, i));
    }

    @Nullable
    public BakedModel bake(Identifier identifier, ModelBakeSettings modelBakeSettings) {
        JsonUnbakedModel jsonUnbakedModel;
        Triple<Identifier, class_4590, Boolean> triple = Triple.of(identifier, modelBakeSettings.getRotation(), modelBakeSettings.isUvLocked());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        }
        UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
        if (unbakedModel instanceof JsonUnbakedModel && (jsonUnbakedModel = (JsonUnbakedModel)unbakedModel).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, modelBakeSettings, identifier);
        }
        BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, modelBakeSettings, identifier);
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

    public Object2IntMap<BlockState> method_21605() {
        return this.field_20274;
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4455 {
        private final List<UnbakedModel> field_20275;
        private final List<Object> field_20276;

        public class_4455(List<UnbakedModel> list, List<Object> list2) {
            this.field_20275 = list;
            this.field_20276 = list2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof class_4455) {
                class_4455 lv = (class_4455)object;
                return Objects.equals(this.field_20275, lv.field_20275) && Objects.equals(this.field_20276, lv.field_20276);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.field_20275.hashCode() + this.field_20276.hashCode();
        }

        public static class_4455 method_21607(BlockState blockState, MultipartUnbakedModel multipartUnbakedModel, Collection<Property<?>> collection) {
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateFactory();
            List list = multipartUnbakedModel.getComponents().stream().filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(blockState)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            List<Object> list2 = class_4455.method_21609(blockState, collection);
            return new class_4455(list, list2);
        }

        public static class_4455 method_21608(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
            List<Object> list = class_4455.method_21609(blockState, collection);
            return new class_4455(ImmutableList.of(unbakedModel), list);
        }

        private static List<Object> method_21609(BlockState blockState, Collection<Property<?>> collection) {
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

