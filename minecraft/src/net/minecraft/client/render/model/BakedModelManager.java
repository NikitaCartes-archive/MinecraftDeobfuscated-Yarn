package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BakedModelManager implements ResourceReloader, AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
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
		CompletableFuture<Map<Identifier, JsonUnbakedModel>> completableFuture = reloadModels(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, List<BlockStatesLoader.SourceTrackedData>>> completableFuture2 = reloadBlockStates(manager, prepareExecutor);
		CompletableFuture<ModelLoader> completableFuture3 = completableFuture.thenCombineAsync(
			completableFuture2, (jsonUnbakedModels, blockStates) -> new ModelLoader(this.colorMap, prepareProfiler, jsonUnbakedModels, blockStates), prepareExecutor
		);
		Map<Identifier, CompletableFuture<SpriteAtlasManager.AtlasPreparation>> map = this.atlasManager.reload(manager, this.mipmapLevels, prepareExecutor);
		return CompletableFuture.allOf((CompletableFuture[])Stream.concat(map.values().stream(), Stream.of(completableFuture3)).toArray(CompletableFuture[]::new))
			.thenApplyAsync(
				void1 -> this.bake(
						prepareProfiler,
						(Map<Identifier, SpriteAtlasManager.AtlasPreparation>)map.entrySet()
							.stream()
							.collect(Collectors.toMap(Entry::getKey, entry -> (SpriteAtlasManager.AtlasPreparation)((CompletableFuture)entry.getValue()).join())),
						(ModelLoader)completableFuture3.join()
					),
				prepareExecutor
			)
			.thenCompose(result -> result.readyForUpload.thenApply(void_ -> result))
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(result -> this.upload(result, applyProfiler), applyExecutor);
	}

	private static CompletableFuture<Map<Identifier, JsonUnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
		return CompletableFuture.supplyAsync(() -> ModelLoader.MODELS_FINDER.findResources(resourceManager), executor)
			.thenCompose(
				models -> {
					List<CompletableFuture<Pair<Identifier, JsonUnbakedModel>>> list = new ArrayList(models.size());

					for (Entry<Identifier, Resource> entry : models.entrySet()) {
						list.add(CompletableFuture.supplyAsync(() -> {
							try {
								Reader reader = ((Resource)entry.getValue()).getReader();

								Pair var2x;
								try {
									var2x = Pair.of((Identifier)entry.getKey(), JsonUnbakedModel.deserialize(reader));
								} catch (Throwable var5) {
									if (reader != null) {
										try {
											reader.close();
										} catch (Throwable var4x) {
											var5.addSuppressed(var4x);
										}
									}

									throw var5;
								}

								if (reader != null) {
									reader.close();
								}

								return var2x;
							} catch (Exception var6) {
								LOGGER.error("Failed to load model {}", entry.getKey(), var6);
								return null;
							}
						}, executor));
					}

					return Util.combineSafe(list)
						.thenApply(modelsx -> (Map)modelsx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
				}
			);
	}

	private static CompletableFuture<Map<Identifier, List<BlockStatesLoader.SourceTrackedData>>> reloadBlockStates(
		ResourceManager resourceManager, Executor executor
	) {
		return CompletableFuture.supplyAsync(() -> BlockStatesLoader.FINDER.findAllResources(resourceManager), executor)
			.thenCompose(
				blockStates -> {
					List<CompletableFuture<Pair<Identifier, List<BlockStatesLoader.SourceTrackedData>>>> list = new ArrayList(blockStates.size());

					for (Entry<Identifier, List<Resource>> entry : blockStates.entrySet()) {
						list.add(CompletableFuture.supplyAsync(() -> {
							List<Resource> listx = (List<Resource>)entry.getValue();
							List<BlockStatesLoader.SourceTrackedData> list2 = new ArrayList(listx.size());

							for (Resource resource : listx) {
								try {
									Reader reader = resource.getReader();

									try {
										JsonObject jsonObject = JsonHelper.deserialize(reader);
										list2.add(new BlockStatesLoader.SourceTrackedData(resource.getPackId(), jsonObject));
									} catch (Throwable var9) {
										if (reader != null) {
											try {
												reader.close();
											} catch (Throwable var8) {
												var9.addSuppressed(var8);
											}
										}

										throw var9;
									}

									if (reader != null) {
										reader.close();
									}
								} catch (Exception var10) {
									LOGGER.error("Failed to load blockstate {} from pack {}", entry.getKey(), resource.getPackId(), var10);
								}
							}

							return Pair.of((Identifier)entry.getKey(), list2);
						}, executor));
					}

					return Util.combineSafe(list)
						.thenApply(blockStatesx -> (Map)blockStatesx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
				}
			);
	}

	private BakedModelManager.BakingResult bake(Profiler profiler, Map<Identifier, SpriteAtlasManager.AtlasPreparation> preparations, ModelLoader modelLoader) {
		profiler.push("load");
		profiler.swap("baking");
		Multimap<ModelIdentifier, SpriteIdentifier> multimap = HashMultimap.create();
		modelLoader.bake((modelIdentifier, spriteId) -> {
			SpriteAtlasManager.AtlasPreparation atlasPreparation = (SpriteAtlasManager.AtlasPreparation)preparations.get(spriteId.getAtlasId());
			Sprite sprite = atlasPreparation.getSprite(spriteId.getTextureId());
			if (sprite != null) {
				return sprite;
			} else {
				multimap.put(modelIdentifier, spriteId);
				return atlasPreparation.getMissingSprite();
			}
		});
		multimap.asMap()
			.forEach(
				(modelIdentifier, spriteIds) -> LOGGER.warn(
						"Missing textures in model {}:\n{}",
						modelIdentifier,
						spriteIds.stream()
							.sorted(SpriteIdentifier.COMPARATOR)
							.map(spriteIdentifier -> "    " + spriteIdentifier.getAtlasId() + ":" + spriteIdentifier.getTextureId())
							.collect(Collectors.joining("\n"))
					)
			);
		profiler.swap("dispatch");
		Map<ModelIdentifier, BakedModel> map = modelLoader.getBakedModelMap();
		BakedModel bakedModel = (BakedModel)map.get(ModelLoader.MISSING_MODEL_ID);
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
		return new BakedModelManager.BakingResult(modelLoader, bakedModel, map2, preparations, completableFuture);
	}

	private void upload(BakedModelManager.BakingResult bakingResult, Profiler profiler) {
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
		ModelLoader modelLoader,
		BakedModel missingModel,
		Map<BlockState, BakedModel> modelCache,
		Map<Identifier, SpriteAtlasManager.AtlasPreparation> atlasPreparations,
		CompletableFuture<Void> readyForUpload
	) {
	}
}
