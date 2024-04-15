package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
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
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelLoader {
	public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_0"));
	public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_1"));
	public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/lava_flow"));
	public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_flow"));
	public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_overlay"));
	public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(
		TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/banner_base")
	);
	public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(
		TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/shield_base")
	);
	public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(
		TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, new Identifier("entity/shield_base_nopattern")
	);
	public static final int field_32983 = 10;
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = (List<Identifier>)IntStream.range(0, 10)
		.mapToObj(stage -> new Identifier("block/destroy_stage_" + stage))
		.collect(Collectors.toList());
	public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = (List<Identifier>)BLOCK_DESTRUCTION_STAGES.stream()
		.map(id -> new Identifier("textures/" + id.getPath() + ".png"))
		.collect(Collectors.toList());
	public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = (List<RenderLayer>)BLOCK_DESTRUCTION_STAGE_TEXTURES.stream()
		.map(RenderLayer::getBlockBreaking)
		.collect(Collectors.toList());
	static final int field_32984 = -1;
	private static final int field_32985 = 0;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String BUILTIN = "builtin/";
	private static final String BUILTIN_GENERATED = "builtin/generated";
	private static final String BUILTIN_ENTITY = "builtin/entity";
	private static final String MISSING = "missing";
	public static final ModelIdentifier MISSING_ID = ModelIdentifier.ofVanilla("builtin/missing", "missing");
	public static final ResourceFinder BLOCK_STATES_FINDER = ResourceFinder.json("blockstates");
	public static final ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
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
	public static final JsonUnbakedModel GENERATION_MARKER = Util.make(
		JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"), model -> model.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.make(
		JsonUnbakedModel.deserialize("{\"gui_light\": \"side\"}"), model -> model.id = "block entity marker"
	);
	private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.add(BooleanProperty.of("map"))
		.build(Block::getDefaultState, BlockState::new);
	static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY, new Identifier("glow_item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final BlockColors blockColors;
	private final Map<Identifier, JsonUnbakedModel> jsonUnbakedModels;
	private final Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates;
	private final Set<Identifier> modelsToLoad = Sets.<Identifier>newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.<Identifier, UnbakedModel>newHashMap();
	final Map<ModelLoader.BakedModelCacheKey, BakedModel> bakedModelCache = Maps.<ModelLoader.BakedModelCacheKey, BakedModel>newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.<Identifier, BakedModel>newHashMap();
	private int nextStateId = 1;
	private final Object2IntMap<BlockState> stateLookup = Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));

	public ModelLoader(
		BlockColors blockColors,
		Profiler profiler,
		Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
		Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates
	) {
		this.blockColors = blockColors;
		this.jsonUnbakedModels = jsonUnbakedModels;
		this.blockStates = blockStates;
		profiler.push("missing_model");

		try {
			this.unbakedModels.put(MISSING_ID, this.loadModelFromJson(MISSING_ID));
			this.addModel(MISSING_ID);
		} catch (IOException var7) {
			LOGGER.error("Error loading missing model, should never happen :(", (Throwable)var7);
			throw new RuntimeException(var7);
		}

		profiler.swap("static_definitions");
		STATIC_DEFINITIONS.forEach((id, stateManager) -> stateManager.getStates().forEach(state -> this.addModel(BlockModels.getModelId(id, state))));
		profiler.swap("blocks");

		for (Block block : Registries.BLOCK) {
			block.getStateManager().getStates().forEach(state -> this.addModel(BlockModels.getModelId(state)));
		}

		profiler.swap("items");

		for (Identifier identifier : Registries.ITEM.getIds()) {
			this.addModel(new ModelIdentifier(identifier, "inventory"));
		}

		profiler.swap("special");
		this.addModel(ItemRenderer.TRIDENT_IN_HAND);
		this.addModel(ItemRenderer.SPYGLASS_IN_HAND);
		this.modelsToBake.values().forEach(model -> model.setParents(this::getOrLoadModel));
		profiler.pop();
	}

	public void bake(BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader) {
		this.modelsToBake.keySet().forEach(modelId -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = new ModelLoader.BakerImpl(spriteLoader, modelId).bake(modelId, ModelRotation.X0_Y0);
			} catch (Exception var5) {
				LOGGER.warn("Unable to bake model: '{}': {}", modelId, var5);
			}

			if (bakedModel != null) {
				this.bakedModels.put(modelId, bakedModel);
			}
		});
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
		return state -> {
			if (state != null && state.isOf(block)) {
				for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
					if (!Objects.equals(state.get((Property)entry.getKey()), entry.getValue())) {
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
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING_ID);

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
		if (!(id instanceof ModelIdentifier modelIdentifier)) {
			this.putModel(id, this.loadModelFromJson(id));
		} else {
			if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
				Identifier identifier = id.withPrefixedPath("item/");
				JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier);
				this.putModel(modelIdentifier, jsonUnbakedModel);
				this.unbakedModels.put(identifier, jsonUnbakedModel);
			} else {
				Identifier identifier = new Identifier(id.getNamespace(), id.getPath());
				StateManager<Block, BlockState> stateManager = (StateManager<Block, BlockState>)Optional.ofNullable((StateManager)STATIC_DEFINITIONS.get(identifier))
					.orElseGet(() -> Registries.BLOCK.get(identifier).getStateManager());
				this.variantMapDeserializationContext.setStateFactory(stateManager);
				List<Property<?>> list = ImmutableList.copyOf(this.blockColors.getProperties(stateManager.getOwner()));
				ImmutableList<BlockState> immutableList = stateManager.getStates();
				Map<ModelIdentifier, BlockState> map = Maps.<ModelIdentifier, BlockState>newHashMap();
				immutableList.forEach(state -> map.put(BlockModels.getModelId(identifier, state), state));
				Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>> map2 = Maps.<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>>newHashMap(
					
				);
				Identifier identifier2 = BLOCK_STATES_FINDER.toResourcePath(id);
				UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING_ID);
				ModelLoader.ModelDefinition modelDefinition = new ModelLoader.ModelDefinition(ImmutableList.of(unbakedModel), ImmutableList.of());
				Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair = Pair.of(unbakedModel, () -> modelDefinition);

				try {
					for (Pair<String, ModelVariantMap> pair2 : ((List)this.blockStates.getOrDefault(identifier2, List.of()))
						.stream()
						.map(
							blockState -> {
								try {
									return Pair.of(blockState.source, ModelVariantMap.fromJson(this.variantMapDeserializationContext, blockState.data));
								} catch (Exception var4x) {
									throw new ModelLoader.ModelLoaderException(
										String.format(
											Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", identifier2, blockState.source, var4x.getMessage()
										)
									);
								}
							}
						)
						.toList()) {
						ModelVariantMap modelVariantMap = pair2.getSecond();
						Map<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>> map3 = Maps.<BlockState, Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>>newIdentityHashMap(
							
						);
						MultipartUnbakedModel multipartUnbakedModel;
						if (modelVariantMap.hasMultipartModel()) {
							multipartUnbakedModel = modelVariantMap.getMultipartModel();
							immutableList.forEach(
								state -> map3.put(state, Pair.of(multipartUnbakedModel, () -> ModelLoader.ModelDefinition.create(state, multipartUnbakedModel, list)))
							);
						} else {
							multipartUnbakedModel = null;
						}

						modelVariantMap.getVariantMap()
							.forEach(
								(key, model) -> {
									try {
										immutableList.stream()
											.filter(stateKeyToPredicate(stateManager, key))
											.forEach(
												state -> {
													Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair2xx = (Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>)map3.put(
														state, Pair.of(model, () -> ModelLoader.ModelDefinition.create(state, model, list))
													);
													if (pair2xx != null && pair2xx.getFirst() != multipartUnbakedModel) {
														map3.put(state, pair);
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
											"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", identifier2, pair2.getFirst(), key, var12x.getMessage()
										);
									}
								}
							);
						map2.putAll(map3);
					}
				} catch (ModelLoader.ModelLoaderException var24) {
					throw var24;
				} catch (Exception var25) {
					throw new ModelLoader.ModelLoaderException(String.format(Locale.ROOT, "Exception loading blockstate definition: '%s': %s", identifier2, var25));
				} finally {
					Map<ModelLoader.ModelDefinition, Set<BlockState>> map5 = Maps.<ModelLoader.ModelDefinition, Set<BlockState>>newHashMap();
					map.forEach((idx, state) -> {
						Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>> pair2x = (Pair<UnbakedModel, Supplier<ModelLoader.ModelDefinition>>)map2.get(state);
						if (pair2x == null) {
							LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier2, idx);
							pair2x = pair;
						}

						this.putModel(idx, pair2x.getFirst());

						try {
							ModelLoader.ModelDefinition modelDefinitionx = (ModelLoader.ModelDefinition)pair2x.getSecond().get();
							((Set)map5.computeIfAbsent(modelDefinitionx, definition -> Sets.newIdentityHashSet())).add(state);
						} catch (Exception var9x) {
							LOGGER.warn("Exception evaluating model definition: '{}'", idx, var9x);
						}
					});
					map5.forEach((definition, states) -> {
						Iterator<BlockState> iterator = states.iterator();

						while (iterator.hasNext()) {
							BlockState blockState = (BlockState)iterator.next();
							if (blockState.getRenderType() != BlockRenderType.MODEL) {
								iterator.remove();
								this.stateLookup.put(blockState, 0);
							}
						}

						if (states.size() > 1) {
							this.addStates(states);
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

	private void addStates(Iterable<BlockState> states) {
		int i = this.nextStateId++;
		states.forEach(state -> this.stateLookup.put(state, i));
	}

	private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
		String string = id.getPath();
		if ("builtin/generated".equals(string)) {
			return GENERATION_MARKER;
		} else if ("builtin/entity".equals(string)) {
			return BLOCK_ENTITY_MARKER;
		} else if (string.startsWith("builtin/")) {
			String string2 = string.substring("builtin/".length());
			String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
			if (string3 == null) {
				throw new FileNotFoundException(id.toString());
			} else {
				Reader reader = new StringReader(string3);
				JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
				jsonUnbakedModel.id = id.toString();
				return jsonUnbakedModel;
			}
		} else {
			Identifier identifier = MODELS_FINDER.toResourcePath(id);
			JsonUnbakedModel jsonUnbakedModel2 = (JsonUnbakedModel)this.jsonUnbakedModels.get(identifier);
			if (jsonUnbakedModel2 == null) {
				throw new FileNotFoundException(identifier.toString());
			} else {
				jsonUnbakedModel2.id = id.toString();
				return jsonUnbakedModel2;
			}
		}
	}

	public Map<Identifier, BakedModel> getBakedModelMap() {
		return this.bakedModels;
	}

	public Object2IntMap<BlockState> getStateLookup() {
		return this.stateLookup;
	}

	@Environment(EnvType.CLIENT)
	static record BakedModelCacheKey(Identifier id, AffineTransformation transformation, boolean isUvLocked) {
	}

	@Environment(EnvType.CLIENT)
	class BakerImpl implements Baker {
		private final Function<SpriteIdentifier, Sprite> textureGetter;

		BakerImpl(final BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader, final Identifier modelId) {
			this.textureGetter = spriteId -> (Sprite)spriteLoader.apply(modelId, spriteId);
		}

		@Override
		public UnbakedModel getOrLoadModel(Identifier id) {
			return ModelLoader.this.getOrLoadModel(id);
		}

		@Override
		public BakedModel bake(Identifier id, ModelBakeSettings settings) {
			ModelLoader.BakedModelCacheKey bakedModelCacheKey = new ModelLoader.BakedModelCacheKey(id, settings.getRotation(), settings.isUvLocked());
			BakedModel bakedModel = (BakedModel)ModelLoader.this.bakedModelCache.get(bakedModelCacheKey);
			if (bakedModel != null) {
				return bakedModel;
			} else {
				UnbakedModel unbakedModel = this.getOrLoadModel(id);
				if (unbakedModel instanceof JsonUnbakedModel jsonUnbakedModel && jsonUnbakedModel.getRootModel() == ModelLoader.GENERATION_MARKER) {
					return ModelLoader.ITEM_MODEL_GENERATOR.create(this.textureGetter, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.textureGetter, settings, id, false);
				}

				BakedModel bakedModel2 = unbakedModel.bake(this, this.textureGetter, settings, id);
				ModelLoader.this.bakedModelCache.put(bakedModelCacheKey, bakedModel2);
				return bakedModel2;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class ModelDefinition {
		private final List<UnbakedModel> components;
		private final List<Object> values;

		public ModelDefinition(List<UnbakedModel> components, List<Object> values) {
			this.components = components;
			this.values = values;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof ModelLoader.ModelDefinition modelDefinition)
					? false
					: Objects.equals(this.components, modelDefinition.components) && Objects.equals(this.values, modelDefinition.values);
			}
		}

		public int hashCode() {
			return 31 * this.components.hashCode() + this.values.hashCode();
		}

		public static ModelLoader.ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
			StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
			List<UnbakedModel> list = (List<UnbakedModel>)rawModel.getComponents()
				.stream()
				.filter(component -> component.getPredicate(stateManager).test(state))
				.map(MultipartModelComponent::getModel)
				.collect(ImmutableList.toImmutableList());
			List<Object> list2 = getStateValues(state, properties);
			return new ModelLoader.ModelDefinition(list, list2);
		}

		public static ModelLoader.ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
			List<Object> list = getStateValues(state, properties);
			return new ModelLoader.ModelDefinition(ImmutableList.of(rawModel), list);
		}

		private static List<Object> getStateValues(BlockState state, Collection<Property<?>> properties) {
			return (List<Object>)properties.stream().map(state::get).collect(ImmutableList.toImmutableList());
		}
	}

	@Environment(EnvType.CLIENT)
	static class ModelLoaderException extends RuntimeException {
		public ModelLoaderException(String message) {
			super(message);
		}
	}

	@Environment(EnvType.CLIENT)
	public static record SourceTrackedData(String source, JsonElement data) {
	}
}
