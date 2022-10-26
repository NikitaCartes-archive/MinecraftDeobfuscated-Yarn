/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
implements ResourceReloader,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, SpriteAtlasManager.SpriteResourceLoader> LAYERS_TO_LOADERS = Map.of(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, resourceManager -> {
        HashMap map = new HashMap();
        SpriteLoader.addResource(resourceManager, ModelLoader.BANNER_BASE.getTextureId(), map::put);
        SpriteLoader.addResources(resourceManager, "entity/banner", map::put);
        return map;
    }, TexturedRenderLayers.BEDS_ATLAS_TEXTURE, resourceManager -> SpriteLoader.findAllResources(resourceManager, "entity/bed"), TexturedRenderLayers.CHEST_ATLAS_TEXTURE, resourceManager -> SpriteLoader.findAllResources(resourceManager, "entity/chest"), TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, resourceManager -> {
        HashMap map = new HashMap();
        SpriteLoader.addResource(resourceManager, ModelLoader.SHIELD_BASE.getTextureId(), map::put);
        SpriteLoader.addResource(resourceManager, ModelLoader.SHIELD_BASE_NO_PATTERN.getTextureId(), map::put);
        SpriteLoader.addResources(resourceManager, "entity/shield", map::put);
        return map;
    }, TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, resourceManager -> SpriteLoader.findAllResources(resourceManager, "entity/signs"), TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, resourceManager -> SpriteLoader.findAllResources(resourceManager, "entity/shulker"), SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, resourceManager -> {
        HashMap map = new HashMap();
        SpriteLoader.addResources(resourceManager, "block", map::put);
        SpriteLoader.addResources(resourceManager, "item", map::put);
        SpriteLoader.addResources(resourceManager, "entity/conduit", map::put);
        SpriteLoader.addResource(resourceManager, BellBlockEntityRenderer.BELL_BODY_TEXTURE.getTextureId(), map::put);
        SpriteLoader.addResource(resourceManager, EnchantingTableBlockEntityRenderer.BOOK_TEXTURE.getTextureId(), map::put);
        return map;
    }, TexturedRenderLayers.HANGING_SIGNS_ATLAS_TEXTURE, resourceManager -> SpriteLoader.findAllResources(resourceManager, "entity/signs/hanging"));
    private Map<Identifier, BakedModel> models;
    private final SpriteAtlasManager atlasManager;
    private final BlockModels blockModelCache;
    private final BlockColors colorMap;
    private int mipmapLevels;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> stateLookup;

    public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmap) {
        this.colorMap = colorMap;
        this.mipmapLevels = mipmap;
        this.blockModelCache = new BlockModels(this);
        this.atlasManager = new SpriteAtlasManager(LAYERS_TO_LOADERS, textureManager);
    }

    public BakedModel getModel(ModelIdentifier id) {
        return this.models.getOrDefault(id, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        prepareProfiler.startTick();
        CompletableFuture<Map<Identifier, JsonUnbakedModel>> completableFuture = BakedModelManager.reloadModels(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, List<ModelLoader.SourceTrackedData>>> completableFuture2 = BakedModelManager.reloadBlockStates(manager, prepareExecutor);
        CompletionStage completableFuture3 = completableFuture.thenCombineAsync(completableFuture2, (map, map2) -> new ModelLoader(this.colorMap, prepareProfiler, (Map<Identifier, JsonUnbakedModel>)map, (Map<Identifier, List<ModelLoader.SourceTrackedData>>)map2), prepareExecutor);
        Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> map3 = this.atlasManager.reload(manager, this.mipmapLevels, prepareExecutor);
        return ((CompletableFuture)((CompletableFuture)((CompletableFuture)CompletableFuture.allOf((CompletableFuture[])Stream.concat(map3.values().stream(), Stream.of(completableFuture3)).toArray(CompletableFuture[]::new)).thenApplyAsync(arg_0 -> this.method_45885(prepareProfiler, map3, (CompletableFuture)completableFuture3, arg_0), prepareExecutor)).thenCompose(bakingResult -> bakingResult.readyForUpload.thenApply(void_ -> bakingResult))).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(bakingResult -> this.upload((BakingResult)bakingResult, applyProfiler), applyExecutor);
    }

    private static CompletableFuture<Map<Identifier, JsonUnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> ModelLoader.MODELS_FINDER.findResources(resourceManager), executor).thenCompose(models2 -> {
            ArrayList<CompletableFuture<Pair>> list = new ArrayList<CompletableFuture<Pair>>(models2.size());
            for (Map.Entry entry : models2.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Pair<Identifier, JsonUnbakedModel> pair;
                    block8: {
                        BufferedReader reader = ((Resource)entry.getValue()).getReader();
                        try {
                            pair = Pair.of((Identifier)entry.getKey(), JsonUnbakedModel.deserialize(reader));
                            if (reader == null) break block8;
                        } catch (Throwable throwable) {
                            try {
                                if (reader != null) {
                                    try {
                                        ((Reader)reader).close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            } catch (IOException iOException) {
                                LOGGER.error("Failed to load model {}", entry.getKey(), (Object)iOException);
                                return null;
                            }
                        }
                        ((Reader)reader).close();
                    }
                    return pair;
                }, executor));
            }
            return Util.combineSafe(list).thenApply(models -> models.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static CompletableFuture<Map<Identifier, List<ModelLoader.SourceTrackedData>>> reloadBlockStates(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> ModelLoader.BLOCK_STATES_FINDER.findAllResources(resourceManager), executor).thenCompose(blockStates2 -> {
            ArrayList<CompletableFuture<Pair>> list = new ArrayList<CompletableFuture<Pair>>(blockStates2.size());
            for (Map.Entry entry : blockStates2.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    List list = (List)entry.getValue();
                    ArrayList<ModelLoader.SourceTrackedData> list2 = new ArrayList<ModelLoader.SourceTrackedData>(list.size());
                    for (Resource resource : list) {
                        try {
                            BufferedReader reader = resource.getReader();
                            try {
                                JsonObject jsonObject = JsonHelper.deserialize(reader);
                                list2.add(new ModelLoader.SourceTrackedData(resource.getResourcePackName(), jsonObject));
                            } finally {
                                if (reader == null) continue;
                                ((Reader)reader).close();
                            }
                        } catch (IOException iOException) {
                            LOGGER.error("Failed to load blockstate {} from pack {}", entry.getKey(), resource.getResourcePackName(), iOException);
                        }
                    }
                    return Pair.of((Identifier)entry.getKey(), list2);
                }, executor));
            }
            return Util.combineSafe(list).thenApply(blockStates -> blockStates.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private BakingResult bake(Profiler profiler, Map<Identifier, SpriteAtlasManager.AtlasPreparation> preparations, ModelLoader modelLoader) {
        profiler.push("load");
        profiler.swap("baking");
        HashMultimap multimap = HashMultimap.create();
        modelLoader.bake((id, spriteId) -> {
            SpriteAtlasManager.AtlasPreparation atlasPreparation = (SpriteAtlasManager.AtlasPreparation)preparations.get(spriteId.getAtlasId());
            Sprite sprite = atlasPreparation.getSprite(spriteId.getTextureId());
            if (sprite != null) {
                return sprite;
            }
            multimap.put(id, spriteId);
            return atlasPreparation.getMissingSprite();
        });
        multimap.asMap().forEach((modelId, spriteIds) -> LOGGER.warn("Missing textures in model {}:\n{}", modelId, (Object)spriteIds.stream().sorted(SpriteIdentifier.COMPARATOR).map(spriteIdentifier -> "    " + spriteIdentifier.getAtlasId() + ":" + spriteIdentifier.getTextureId()).collect(Collectors.joining("\n"))));
        profiler.swap("dispatch");
        Map<Identifier, BakedModel> map = modelLoader.getBakedModelMap();
        BakedModel bakedModel = map.get(ModelLoader.MISSING_ID);
        IdentityHashMap<BlockState, BakedModel> map2 = new IdentityHashMap<BlockState, BakedModel>();
        for (Block block : Registry.BLOCK) {
            block.getStateManager().getStates().forEach(state -> {
                Identifier identifier = state.getBlock().getRegistryEntry().registryKey().getValue();
                BakedModel bakedModel2 = map.getOrDefault(BlockModels.getModelId(identifier, state), bakedModel);
                map2.put((BlockState)state, bakedModel2);
            });
        }
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])preparations.values().stream().map(SpriteAtlasManager.AtlasPreparation::whenComplete).toArray(CompletableFuture[]::new));
        profiler.pop();
        profiler.endTick();
        return new BakingResult(modelLoader, bakedModel, map2, preparations, completableFuture);
    }

    private void upload(BakingResult bakingResult, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        bakingResult.atlasPreparations.values().forEach(SpriteAtlasManager.AtlasPreparation::upload);
        ModelLoader modelLoader = bakingResult.modelLoader;
        this.models = modelLoader.getBakedModelMap();
        this.stateLookup = modelLoader.getStateLookup();
        this.missingModel = bakingResult.missingModel;
        profiler.swap("cache");
        this.blockModelCache.setModels(bakingResult.modelCache);
        profiler.pop();
        profiler.endTick();
    }

    public boolean shouldRerender(BlockState from, BlockState to) {
        int j;
        if (from == to) {
            return false;
        }
        int i = this.stateLookup.getInt(from);
        if (i != -1 && i == (j = this.stateLookup.getInt(to))) {
            FluidState fluidState2;
            FluidState fluidState = from.getFluidState();
            return fluidState != (fluidState2 = to.getFluidState());
        }
        return true;
    }

    public SpriteAtlasTexture getAtlas(Identifier id) {
        return this.atlasManager.getAtlas(id);
    }

    @Override
    public void close() {
        this.atlasManager.close();
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.mipmapLevels = mipmapLevels;
    }

    private /* synthetic */ BakingResult method_45885(Profiler void_, Map map, CompletableFuture completableFuture, Void void1) {
        return this.bake(void_, map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> (SpriteAtlasManager.AtlasPreparation)((CompletableFuture)entry.getValue()).join())), (ModelLoader)completableFuture.join());
    }

    @Environment(value=EnvType.CLIENT)
    record BakingResult(ModelLoader modelLoader, BakedModel missingModel, Map<BlockState, BakedModel> modelCache, Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations, CompletableFuture<Void> readyForUpload) {
    }
}

