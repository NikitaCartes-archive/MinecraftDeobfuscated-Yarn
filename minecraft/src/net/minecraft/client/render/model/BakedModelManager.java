package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BakedModelManager implements ResourceReloader, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ResourceFinder BLOCK_STATES_FINDER = ResourceFinder.json("blockstates");
	private static final ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
	private static final Map<Identifier, Identifier> LAYERS_TO_LOADERS = Map.of(
		TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE,
		Identifier.ofVanilla("banner_patterns"),
		TexturedRenderLayers.BEDS_ATLAS_TEXTURE,
		Identifier.ofVanilla("beds"),
		TexturedRenderLayers.CHEST_ATLAS_TEXTURE,
		Identifier.ofVanilla("chests"),
		TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE,
		Identifier.ofVanilla("shield_patterns"),
		TexturedRenderLayers.SIGNS_ATLAS_TEXTURE,
		Identifier.ofVanilla("signs"),
		TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE,
		Identifier.ofVanilla("shulker_boxes"),
		TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE,
		Identifier.ofVanilla("armor_trims"),
		TexturedRenderLayers.DECORATED_POT_ATLAS_TEXTURE,
		Identifier.ofVanilla("decorated_pot"),
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
		Identifier.ofVanilla("blocks")
	);
	private Map<ModelIdentifier, BakedModel> models;
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
		return (BakedModel)this.models.getOrDefault(id, this.missingModel);
	}

	public BakedModel getMissingModel() {
		return this.missingModel;
	}

	public BlockModels getBlockModels() {
		return this.blockModelCache;
	}

	@Override
	public final CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		prepareProfiler.startTick();
		UnbakedModel unbakedModel = MissingModel.create();
		BlockStatesLoader blockStatesLoader = new BlockStatesLoader(unbakedModel);
		CompletableFuture<Map<Identifier, UnbakedModel>> completableFuture = reloadModels(manager, prepareExecutor);
		CompletableFuture<BlockStatesLoader.BlockStateDefinition> completableFuture2 = reloadBlockStates(blockStatesLoader, manager, prepareExecutor);
		CompletableFuture<ReferencedModelsCollector> completableFuture3 = completableFuture2.thenCombineAsync(
			completableFuture, (definition, models) -> this.collect(unbakedModel, models, definition), prepareExecutor
		);
		CompletableFuture<Object2IntMap<BlockState>> completableFuture4 = completableFuture2.thenApplyAsync(
			blockStateDefinition -> group(this.colorMap, blockStateDefinition), prepareExecutor
		);
		Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> map = this.atlasManager.reload(manager, this.mipmapLevels, prepareExecutor);
		return CompletableFuture.allOf(
				(CompletableFuture[])Stream.concat(map.values().stream(), Stream.of(completableFuture3, completableFuture4)).toArray(CompletableFuture[]::new)
			)
			.thenApplyAsync(
				void_ -> {
					Map<Identifier, SpriteAtlasManager.AtlasPreparation> map2 = (Map<Identifier, SpriteAtlasManager.AtlasPreparation>)map.entrySet()
						.stream()
						.collect(Collectors.toMap(Entry::getKey, entry -> (SpriteAtlasManager.AtlasPreparation)((CompletableFuture)entry.getValue()).join()));
					ReferencedModelsCollector referencedModelsCollector = (ReferencedModelsCollector)completableFuture3.join();
					Object2IntMap<BlockState> object2IntMap = (Object2IntMap<BlockState>)completableFuture4.join();
					return this.bake(
						prepareProfiler,
						map2,
						new ModelBaker(referencedModelsCollector.getTopLevelModels(), referencedModelsCollector.getResolvedModels(), unbakedModel),
						object2IntMap
					);
				},
				prepareExecutor
			)
			.thenCompose(result -> result.readyForUpload.thenApply(void_ -> result))
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(result -> this.upload(result, applyProfiler), applyExecutor);
	}

	private static CompletableFuture<Map<Identifier, UnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> MODELS_FINDER.findResources(resourceManager), executor)
			.thenCompose(
				models -> {
					List<CompletableFuture<Pair<Identifier, JsonUnbakedModel>>> list = new ArrayList(models.size());

					for (Entry<Identifier, Resource> entry : models.entrySet()) {
						list.add(CompletableFuture.supplyAsync(() -> {
							Identifier identifier = MODELS_FINDER.toResourceId((Identifier)entry.getKey());

							try {
								Reader reader = ((Resource)entry.getValue()).getReader();

								Pair var4x;
								try {
									JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
									jsonUnbakedModel.id = identifier.toString();
									var4x = Pair.of(identifier, jsonUnbakedModel);
								} catch (Throwable var6) {
									if (reader != null) {
										try {
											reader.close();
										} catch (Throwable var5) {
											var6.addSuppressed(var5);
										}
									}

									throw var6;
								}

								if (reader != null) {
									reader.close();
								}

								return var4x;
							} catch (Exception var7) {
								LOGGER.error("Failed to load model {}", entry.getKey(), var7);
								return null;
							}
						}, executor));
					}

					return Util.combineSafe(list)
						.thenApply(modelsx -> (Map)modelsx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
				}
			);
	}

	private ReferencedModelsCollector collect(UnbakedModel missingModel, Map<Identifier, UnbakedModel> inputs, BlockStatesLoader.BlockStateDefinition definition) {
		ReferencedModelsCollector referencedModelsCollector = new ReferencedModelsCollector(inputs, missingModel);
		referencedModelsCollector.addBlockStates(definition);
		referencedModelsCollector.resolveAll();
		return referencedModelsCollector;
	}

	private static CompletableFuture<BlockStatesLoader.BlockStateDefinition> reloadBlockStates(
		BlockStatesLoader blockStatesLoader, ResourceManager resourceManager, Executor executor
	) {
		Function<Identifier, StateManager<Block, BlockState>> function = BlockStatesLoader.getIdToStatesConverter();
		return CompletableFuture.supplyAsync(() -> BLOCK_STATES_FINDER.findAllResources(resourceManager), executor).thenCompose(blockStates -> {
			List<CompletableFuture<BlockStatesLoader.BlockStateDefinition>> list = new ArrayList(blockStates.size());

			for (Entry<Identifier, List<Resource>> entry : blockStates.entrySet()) {
				list.add(CompletableFuture.supplyAsync(() -> {
					Identifier identifier = BLOCK_STATES_FINDER.toResourceId((Identifier)entry.getKey());
					StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>)function.apply(identifier);
					if (stateManager == null) {
						LOGGER.debug("Discovered unknown block state definition {}, ignoring", identifier);
						return null;
					} else {
						List<Resource> listx = (List<Resource>)entry.getValue();
						List<BlockStatesLoader.PackBlockStateDefinition> list2 = new ArrayList(listx.size());

						for (Resource resource : listx) {
							try {
								Reader reader = resource.getReader();

								try {
									JsonObject jsonObject = JsonHelper.deserialize(reader);
									ModelVariantMap modelVariantMap = ModelVariantMap.fromJson(jsonObject);
									list2.add(new BlockStatesLoader.PackBlockStateDefinition(resource.getPackId(), modelVariantMap));
								} catch (Throwable var14) {
									if (reader != null) {
										try {
											reader.close();
										} catch (Throwable var13) {
											var14.addSuppressed(var13);
										}
									}

									throw var14;
								}

								if (reader != null) {
									reader.close();
								}
							} catch (Exception var15) {
								LOGGER.error("Failed to load blockstate definition {} from pack {}", identifier, resource.getPackId(), var15);
							}
						}

						try {
							return blockStatesLoader.combine(identifier, stateManager, list2);
						} catch (Exception var12) {
							LOGGER.error("Failed to load blockstate definition {}", identifier, var12);
							return null;
						}
					}
				}, executor));
			}

			return Util.combineSafe(list).thenApply(blockStatesx -> {
				Map<ModelIdentifier, BlockStatesLoader.BlockModel> map = new HashMap();

				for (BlockStatesLoader.BlockStateDefinition blockStateDefinition : blockStatesx) {
					if (blockStateDefinition != null) {
						map.putAll(blockStateDefinition.models());
					}
				}

				return new BlockStatesLoader.BlockStateDefinition(map);
			});
		});
	}

	private BakedModelManager.BakingResult bake(
		Profiler profiler, Map<Identifier, SpriteAtlasManager.AtlasPreparation> preparations, ModelBaker modelLoader, Object2IntMap<BlockState> modelGroups
	) {
		profiler.push("load");
		profiler.swap("baking");
		Multimap<ModelIdentifier, SpriteIdentifier> multimap = HashMultimap.create();
		modelLoader.bake((modelId, spriteId) -> {
			SpriteAtlasManager.AtlasPreparation atlasPreparation = (SpriteAtlasManager.AtlasPreparation)preparations.get(spriteId.getAtlasId());
			Sprite sprite = atlasPreparation.getSprite(spriteId.getTextureId());
			if (sprite != null) {
				return sprite;
			} else {
				multimap.put(modelId, spriteId);
				return atlasPreparation.getMissingSprite();
			}
		});
		multimap.asMap()
			.forEach(
				(modelId, spriteIds) -> LOGGER.warn(
						"Missing textures in model {}:\n{}",
						modelId,
						spriteIds.stream()
							.sorted(SpriteIdentifier.COMPARATOR)
							.map(spriteId -> "    " + spriteId.getAtlasId() + ":" + spriteId.getTextureId())
							.collect(Collectors.joining("\n"))
					)
			);
		profiler.swap("dispatch");
		Map<ModelIdentifier, BakedModel> map = modelLoader.getBakedModels();
		BakedModel bakedModel = (BakedModel)map.get(MissingModel.MODEL_ID);
		Map<BlockState, BakedModel> map2 = new IdentityHashMap();

		for (Block block : Registries.BLOCK) {
			block.getStateManager().getStates().forEach(state -> {
				Identifier identifier = state.getBlock().getRegistryEntry().registryKey().getValue();
				BakedModel bakedModel2 = (BakedModel)map.getOrDefault(BlockModels.getModelId(identifier, state), bakedModel);
				map2.put(state, bakedModel2);
			});
		}

		CompletableFuture<Void> completableFuture = CompletableFuture.allOf(
			(CompletableFuture[])preparations.values().stream().map(SpriteAtlasManager.AtlasPreparation::whenComplete).toArray(CompletableFuture[]::new)
		);
		profiler.pop();
		profiler.endTick();
		return new BakedModelManager.BakingResult(modelLoader, modelGroups, bakedModel, map2, preparations, completableFuture);
	}

	private static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.BlockStateDefinition definition) {
		return ModelGrouper.group(colors, definition);
	}

	private void upload(BakedModelManager.BakingResult bakingResult, Profiler profiler) {
		profiler.startTick();
		profiler.push("upload");
		bakingResult.atlasPreparations.values().forEach(SpriteAtlasManager.AtlasPreparation::upload);
		ModelBaker modelBaker = bakingResult.modelLoader;
		this.models = modelBaker.getBakedModels();
		this.stateLookup = bakingResult.modelGroups;
		this.missingModel = bakingResult.missingModel;
		profiler.swap("cache");
		this.blockModelCache.setModels(bakingResult.modelCache);
		profiler.pop();
		profiler.endTick();
	}

	public boolean shouldRerender(BlockState from, BlockState to) {
		if (from == to) {
			return false;
		} else {
			int i = this.stateLookup.getInt(from);
			if (i != -1) {
				int j = this.stateLookup.getInt(to);
				if (i == j) {
					FluidState fluidState = from.getFluidState();
					FluidState fluidState2 = to.getFluidState();
					return fluidState != fluidState2;
				}
			}

			return true;
		}
	}

	public SpriteAtlasTexture getAtlas(Identifier id) {
		return this.atlasManager.getAtlas(id);
	}

	public void close() {
		this.atlasManager.close();
	}

	public void setMipmapLevels(int mipmapLevels) {
		this.mipmapLevels = mipmapLevels;
	}

	@Environment(EnvType.CLIENT)
	static record BakingResult(
		ModelBaker modelLoader,
		Object2IntMap<BlockState> modelGroups,
		BakedModel missingModel,
		Map<BlockState, BakedModel> modelCache,
		Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations,
		CompletableFuture<Void> readyForUpload
	) {
	}
}
