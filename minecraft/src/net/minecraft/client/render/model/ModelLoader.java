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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.Rotation3;
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

@Environment(EnvType.CLIENT)
public class ModelLoader {
	public static final Identifier FIRE_0 = new Identifier("block/fire_0");
	public static final Identifier FIRE_1 = new Identifier("block/fire_1");
	public static final Identifier LAVA_FLOW = new Identifier("block/lava_flow");
	public static final Identifier WATER_FLOW = new Identifier("block/water_flow");
	public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");
	public static final Identifier field_20845 = new Identifier("entity/shulker/shulker");
	public static final List<Identifier> field_20846 = ImmutableList.of(
		new Identifier("entity/shulker/shulker_white"),
		new Identifier("entity/shulker/shulker_orange"),
		new Identifier("entity/shulker/shulker_magenta"),
		new Identifier("entity/shulker/shulker_light_blue"),
		new Identifier("entity/shulker/shulker_yellow"),
		new Identifier("entity/shulker/shulker_lime"),
		new Identifier("entity/shulker/shulker_pink"),
		new Identifier("entity/shulker/shulker_gray"),
		new Identifier("entity/shulker/shulker_light_gray"),
		new Identifier("entity/shulker/shulker_cyan"),
		new Identifier("entity/shulker/shulker_purple"),
		new Identifier("entity/shulker/shulker_blue"),
		new Identifier("entity/shulker/shulker_brown"),
		new Identifier("entity/shulker/shulker_green"),
		new Identifier("entity/shulker/shulker_red"),
		new Identifier("entity/shulker/shulker_black")
	);
	public static final Identifier field_20847 = new Identifier("entity/banner_base");
	public static final Identifier field_21557 = new Identifier("entity/shield_base");
	public static final Identifier field_21558 = new Identifier("entity/shield_base_nopattern");
	public static final Identifier field_21014 = new Identifier("entity/signs/oak");
	public static final Identifier field_21015 = new Identifier("entity/signs/spruce");
	public static final Identifier field_21016 = new Identifier("entity/signs/birch");
	public static final Identifier field_21017 = new Identifier("entity/signs/acacia");
	public static final Identifier field_21018 = new Identifier("entity/signs/jungle");
	public static final Identifier field_21019 = new Identifier("entity/signs/dark_oak");
	public static final List<Identifier> field_20848 = (List<Identifier>)IntStream.range(0, 10)
		.mapToObj(i -> new Identifier("block/destroy_stage_" + i))
		.collect(Collectors.toList());
	public static final List<Identifier> BLOCK_BREAKING_STAGES = (List<Identifier>)field_20848.stream()
		.map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png"))
		.collect(Collectors.toList());
	private static final Set<Identifier> DEFAULT_TEXTURES = Util.create(Sets.<Identifier>newHashSet(), hashSet -> {
		hashSet.add(WATER_FLOW);
		hashSet.add(LAVA_FLOW);
		hashSet.add(WATER_OVERLAY);
		hashSet.add(FIRE_0);
		hashSet.add(FIRE_1);
		hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
		hashSet.addAll(Arrays.asList(BedBlockEntityRenderer.TEXTURES));
		hashSet.add(ChestBlockEntityRenderer.TRAPPED_TEX);
		hashSet.add(ChestBlockEntityRenderer.field_21473);
		hashSet.add(ChestBlockEntityRenderer.field_21474);
		hashSet.add(ChestBlockEntityRenderer.CHRISTMAS_TEX);
		hashSet.add(ChestBlockEntityRenderer.field_21475);
		hashSet.add(ChestBlockEntityRenderer.field_21476);
		hashSet.add(ChestBlockEntityRenderer.NORMAL_TEX);
		hashSet.add(ChestBlockEntityRenderer.field_21477);
		hashSet.add(ChestBlockEntityRenderer.field_21478);
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
		hashSet.add(field_21557);
		hashSet.add(field_21558);

		for (BannerPattern bannerPattern : BannerPattern.values()) {
			hashSet.add(bannerPattern.getSpriteId(true));
			hashSet.add(bannerPattern.getSpriteId(false));
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
	public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '"
			+ MissingSprite.getMissingSpriteId().getPath()
			+ "',       'missingno': '"
			+ MissingSprite.getMissingSpriteId().getPath()
			+ "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
		.replace('\'', '"');
	private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.<String, String>newHashMap(ImmutableMap.of("missing", MISSING_DEFINITION));
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
	public static final JsonUnbakedModel GENERATION_MARKER = Util.create(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.create(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "block entity marker"
	);
	private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.add(BooleanProperty.of("map"))
		.build(BlockState::new);
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final ResourceManager resourceManager;
	private final SpriteAtlasTexture spriteAtlas;
	private final BlockColors field_20272;
	private final Set<Identifier> modelsToLoad = Sets.<Identifier>newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Triple<Identifier, Rotation3, Boolean>, BakedModel> bakedModelCache = Maps.<Triple<Identifier, Rotation3, Boolean>, BakedModel>newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.<Identifier, BakedModel>newHashMap();
	private final SpriteAtlasTexture.Data spriteAtlasData;
	private int field_20273 = 1;
	private final Object2IntMap<BlockState> field_20274 = Util.create(
		new Object2IntOpenHashMap<>(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1)
	);

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlas, BlockColors blockColors, Profiler profiler) {
		this.resourceManager = resourceManager;
		this.spriteAtlas = spriteAtlas;
		this.field_20272 = blockColors;
		profiler.push("missing_model");

		try {
			this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
			this.addModel(MISSING);
		} catch (IOException var7) {
			LOGGER.error("Error loading missing model, should never happen :(", (Throwable)var7);
			throw new RuntimeException(var7);
		}

		profiler.swap("static_definitions");
		STATIC_DEFINITIONS.forEach(
			(identifier, stateManager) -> stateManager.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState)))
		);
		profiler.swap("blocks");

		for (Block block : Registry.BLOCK) {
			block.getStateFactory().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
		}

		profiler.swap("items");

		for (Identifier identifier : Registry.ITEM.getIds()) {
			this.addModel(new ModelIdentifier(identifier, "inventory"));
		}

		profiler.swap("special");
		this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		profiler.swap("textures");
		Set<String> set = Sets.<String>newLinkedHashSet();
		Set<Identifier> set2 = (Set<Identifier>)this.modelsToBake
			.values()
			.stream()
			.flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream())
			.collect(Collectors.toSet());
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
				bakedModel = this.bake(identifier, ModelRotation.X0_Y0);
			} catch (Exception var4) {
				LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4);
			}

			if (bakedModel != null) {
				this.bakedModels.put(identifier, bakedModel);
			}
		});
		profiler.pop();
	}

	private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> stateFactory, String key) {
		Map<Property<?>, Comparable<?>> map = Maps.<Property<?>, Comparable<?>>newHashMap();

		for (String string : COMMA_SPLITTER.split(key)) {
			Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string).iterator();
			if (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				Property<?> property = stateFactory.getProperty(string2);
				if (property != null && iterator.hasNext()) {
					String string3 = (String)iterator.next();
					Comparable<?> comparable = getPropertyValue((Property<Comparable<?>>)property, string3);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string3 + "' for blockstate property: '" + string2 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string2.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string2 + "'");
				}
			}
		}

		Block block = stateFactory.getOwner();
		return blockState -> {
			if (blockState != null && block == blockState.getBlock()) {
				for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
					if (!Objects.equals(blockState.get((Property)entry.getKey()), entry.getValue())) {
						return false;
					}
				}

				return true;
			} else {
				return false;
			}
		};
	}

	@Nullable
	static <T extends Comparable<T>> T getPropertyValue(Property<T> property, String string) {
		return (T)property.parse(string).orElse(null);
	}

	public UnbakedModel getOrLoadModel(Identifier id) {
		if (this.unbakedModels.containsKey(id)) {
			return (UnbakedModel)this.unbakedModels.get(id);
		} else if (this.modelsToLoad.contains(id)) {
			throw new IllegalStateException("Circular reference while loading " + id);
		} else {
			this.modelsToLoad.add(id);
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING);

			while (!this.modelsToLoad.isEmpty()) {
				Identifier identifier = (Identifier)this.modelsToLoad.iterator().next();

				try {
					if (!this.unbakedModels.containsKey(identifier)) {
						this.loadModel(identifier);
					}
				} catch (ModelLoader.ModelLoaderException var9) {
					LOGGER.warn(var9.getMessage());
					this.unbakedModels.put(identifier, unbakedModel);
				} catch (Exception var10) {
					LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier, id, var10);
					this.unbakedModels.put(identifier, unbakedModel);
				} finally {
					this.modelsToLoad.remove(identifier);
				}
			}

			return (UnbakedModel)this.unbakedModels.getOrDefault(id, unbakedModel);
		}
	}

	private void loadModel(Identifier id) throws Exception {
		if (!(id instanceof ModelIdentifier)) {
			this.putModel(id, this.loadModelFromJson(id));
		} else {
			ModelIdentifier modelIdentifier = (ModelIdentifier)id;
			if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
				Identifier identifier = new Identifier(id.getNamespace(), "item/" + id.getPath());
				JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier);
				this.putModel(modelIdentifier, jsonUnbakedModel);
				this.unbakedModels.put(identifier, jsonUnbakedModel);
			} else {
				Identifier identifier = new Identifier(id.getNamespace(), id.getPath());
				StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>)Optional.ofNullable(STATIC_DEFINITIONS.get(identifier))
					.orElseGet(() -> Registry.BLOCK.get(identifier).getStateFactory());
				this.variantMapDeserializationContext.setStateFactory(stateManager);
				List<Property<?>> list = ImmutableList.copyOf(this.field_20272.getProperties(stateManager.getOwner()));
				ImmutableList<BlockState> immutableList = stateManager.getStates();
				Map<ModelIdentifier, BlockState> map = Maps.<ModelIdentifier, BlockState>newHashMap();
				immutableList.forEach(blockState -> {
					BlockState var10000 = (BlockState)map.put(BlockModels.getModelId(identifier, blockState), blockState);
				});
				Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>> map2 = Maps.<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>>newHashMap();
				Identifier identifier2 = new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
				UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING);
				ModelLoader.ModelValuePair modelValuePair = new ModelLoader.ModelValuePair(ImmutableList.of(unbakedModel), ImmutableList.of());
				Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>> pair = Pair.of(unbakedModel, () -> modelValuePair);

				try {
					List<Pair<String, ModelVariantMap>> list2;
					try {
						list2 = (List<Pair<String, ModelVariantMap>>)this.resourceManager
							.getAllResources(identifier2)
							.stream()
							.map(
								resource -> {
									try {
										InputStream inputStream = resource.getInputStream();
										Throwable var3x = null;

										Pair var4x;
										try {
											var4x = Pair.of(
												resource.getResourcePackName(),
												ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8))
											);
										} catch (Throwable var14) {
											var3x = var14;
											throw var14;
										} finally {
											if (inputStream != null) {
												if (var3x != null) {
													try {
														inputStream.close();
													} catch (Throwable var13x) {
														var3x.addSuppressed(var13x);
													}
												} else {
													inputStream.close();
												}
											}
										}

										return var4x;
									} catch (Exception var16x) {
										throw new ModelLoader.ModelLoaderException(
											String.format(
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), var16x.getMessage()
											)
										);
									}
								}
							)
							.collect(Collectors.toList());
					} catch (IOException var25) {
						LOGGER.warn("Exception loading blockstate definition: {}: {}", identifier2, var25);
						return;
					}

					for (Pair<String, ModelVariantMap> pair2 : list2) {
						ModelVariantMap modelVariantMap = pair2.getSecond();
						Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>> map4 = Maps.<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>>newIdentityHashMap();
						MultipartUnbakedModel multipartUnbakedModel;
						if (modelVariantMap.hasMultipartModel()) {
							multipartUnbakedModel = modelVariantMap.getMultipartModel();
							immutableList.forEach(
								blockState -> {
									Pair var10000 = (Pair)map4.put(
										blockState, Pair.of(multipartUnbakedModel, () -> ModelLoader.ModelValuePair.create(blockState, multipartUnbakedModel, list))
									);
								}
							);
						} else {
							multipartUnbakedModel = null;
						}

						modelVariantMap.getVariantMap()
							.forEach(
								(string, weightedUnbakedModel) -> {
									try {
										immutableList.stream()
											.filter(stateKeyToPredicate(stateManager, string))
											.forEach(
												blockState -> {
													Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>> pair2xx = (Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>)map4.put(
														blockState, Pair.of(weightedUnbakedModel, () -> ModelLoader.ModelValuePair.create(blockState, weightedUnbakedModel, list))
													);
													if (pair2xx != null && pair2xx.getFirst() != multipartUnbakedModel) {
														map4.put(blockState, pair);
														throw new RuntimeException(
															"Overlapping definition with: "
																+ (String)((Entry)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair2xx.getFirst()).findFirst().get())
																	.getKey()
														);
													}
												}
											);
									} catch (Exception var12x) {
										LOGGER.warn(
											"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
											identifier2,
											pair2.getFirst(),
											string,
											var12x.getMessage()
										);
									}
								}
							);
						map2.putAll(map4);
					}
				} catch (ModelLoader.ModelLoaderException var26) {
					throw var26;
				} catch (Exception var27) {
					throw new ModelLoader.ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier2, var27));
				} finally {
					Map<ModelLoader.ModelValuePair, Set<BlockState>> map6 = Maps.<ModelLoader.ModelValuePair, Set<BlockState>>newHashMap();
					map.forEach((modelIdentifierx, blockState) -> {
						Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>> pair2x = (Pair<UnbakedModel, Supplier<ModelLoader.ModelValuePair>>)map2.get(blockState);
						if (pair2x == null) {
							LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier2, modelIdentifierx);
							pair2x = pair;
						}

						this.putModel(modelIdentifierx, pair2x.getFirst());

						try {
							ModelLoader.ModelValuePair modelValuePairx = (ModelLoader.ModelValuePair)pair2x.getSecond().get();
							((Set)map6.computeIfAbsent(modelValuePairx, modelValuePairxx -> Sets.newIdentityHashSet())).add(blockState);
						} catch (Exception var9x) {
							LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifierx, var9x);
						}
					});
					map6.forEach((modelValuePairx, set) -> {
						Iterator<BlockState> iterator = set.iterator();

						while (iterator.hasNext()) {
							BlockState blockState = (BlockState)iterator.next();
							if (blockState.getRenderType() != BlockRenderType.MODEL) {
								iterator.remove();
								this.field_20274.put(blockState, 0);
							}
						}

						if (set.size() > 1) {
							this.method_21603(set);
						}
					});
				}
			}
		}
	}

	private void putModel(Identifier id, UnbakedModel unbakedModel) {
		this.unbakedModels.put(id, unbakedModel);
		this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
	}

	private void addModel(ModelIdentifier modelId) {
		UnbakedModel unbakedModel = this.getOrLoadModel(modelId);
		this.unbakedModels.put(modelId, unbakedModel);
		this.modelsToBake.put(modelId, unbakedModel);
	}

	private void method_21603(Iterable<BlockState> iterable) {
		int i = this.field_20273++;
		iterable.forEach(blockState -> this.field_20274.put(blockState, i));
	}

	@Nullable
	public BakedModel bake(Identifier id, ModelBakeSettings settings) {
		Triple<Identifier, Rotation3, Boolean> triple = Triple.of(id, settings.getRotation(), settings.isUvLocked());
		if (this.bakedModelCache.containsKey(triple)) {
			return (BakedModel)this.bakedModelCache.get(triple);
		} else {
			UnbakedModel unbakedModel = this.getOrLoadModel(id);
			if (unbakedModel instanceof JsonUnbakedModel) {
				JsonUnbakedModel jsonUnbakedModel = (JsonUnbakedModel)unbakedModel;
				if (jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
					return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, settings, id);
				}
			}

			BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, settings, id);
			this.bakedModelCache.put(triple, bakedModel);
			return bakedModel;
		}
	}

	private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
		Reader reader = null;
		Resource resource = null;

		JsonUnbakedModel jsonUnbakedModel;
		try {
			String string = id.getPath();
			if ("builtin/generated".equals(string)) {
				return GENERATION_MARKER;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(id.toString());
					}

					reader = new StringReader(string3);
				} else {
					resource = this.resourceManager.getResource(new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
					reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				}

				jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
				jsonUnbakedModel.id = id.toString();
				return jsonUnbakedModel;
			}

			jsonUnbakedModel = BLOCK_ENTITY_MARKER;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(resource);
		}

		return jsonUnbakedModel;
	}

	public Map<Identifier, BakedModel> getBakedModelMap() {
		return this.bakedModels;
	}

	public Object2IntMap<BlockState> method_21605() {
		return this.field_20274;
	}

	@Environment(EnvType.CLIENT)
	static class ModelLoaderException extends RuntimeException {
		public ModelLoaderException(String string) {
			super(string);
		}
	}

	@Environment(EnvType.CLIENT)
	static class ModelValuePair {
		private final List<UnbakedModel> model;
		private final List<Object> propertyValues;

		public ModelValuePair(List<UnbakedModel> list, List<Object> list2) {
			this.model = list;
			this.propertyValues = list2;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (!(object instanceof ModelLoader.ModelValuePair)) {
				return false;
			} else {
				ModelLoader.ModelValuePair modelValuePair = (ModelLoader.ModelValuePair)object;
				return Objects.equals(this.model, modelValuePair.model) && Objects.equals(this.propertyValues, modelValuePair.propertyValues);
			}
		}

		public int hashCode() {
			return 31 * this.model.hashCode() + this.propertyValues.hashCode();
		}

		public static ModelLoader.ModelValuePair create(BlockState blockState, MultipartUnbakedModel multipartUnbakedModel, Collection<Property<?>> collection) {
			StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateFactory();
			List<UnbakedModel> list = (List<UnbakedModel>)multipartUnbakedModel.getComponents()
				.stream()
				.filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(blockState))
				.map(MultipartModelComponent::getModel)
				.collect(ImmutableList.toImmutableList());
			List<Object> list2 = getValues(blockState, collection);
			return new ModelLoader.ModelValuePair(list, list2);
		}

		public static ModelLoader.ModelValuePair create(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
			List<Object> list = getValues(blockState, collection);
			return new ModelLoader.ModelValuePair(ImmutableList.of(unbakedModel), list);
		}

		private static List<Object> getValues(BlockState blockState, Collection<Property<?>> collection) {
			return (List<Object>)collection.stream().map(blockState::get).collect(ImmutableList.toImmutableList());
		}
	}
}
