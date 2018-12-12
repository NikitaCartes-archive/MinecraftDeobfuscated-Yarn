package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
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
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelLoader {
	public static final Identifier field_5397 = new Identifier("block/fire_0");
	public static final Identifier field_5370 = new Identifier("block/fire_1");
	public static final Identifier field_5381 = new Identifier("block/lava_flow");
	public static final Identifier field_5391 = new Identifier("block/water_flow");
	public static final Identifier field_5388 = new Identifier("block/water_overlay");
	public static final Identifier field_5377 = new Identifier("block/destroy_stage_0");
	public static final Identifier field_5385 = new Identifier("block/destroy_stage_1");
	public static final Identifier field_5375 = new Identifier("block/destroy_stage_2");
	public static final Identifier field_5403 = new Identifier("block/destroy_stage_3");
	public static final Identifier field_5393 = new Identifier("block/destroy_stage_4");
	public static final Identifier field_5386 = new Identifier("block/destroy_stage_5");
	public static final Identifier field_5369 = new Identifier("block/destroy_stage_6");
	public static final Identifier field_5401 = new Identifier("block/destroy_stage_7");
	public static final Identifier field_5392 = new Identifier("block/destroy_stage_8");
	public static final Identifier field_5382 = new Identifier("block/destroy_stage_9");
	private static final Set<Identifier> DEFAULT_TEXTURES = Sets.<Identifier>newHashSet(
		field_5391,
		field_5381,
		field_5388,
		field_5397,
		field_5370,
		field_5377,
		field_5385,
		field_5375,
		field_5403,
		field_5393,
		field_5386,
		field_5369,
		field_5401,
		field_5392,
		field_5382,
		new Identifier("item/empty_armor_slot_helmet"),
		new Identifier("item/empty_armor_slot_chestplate"),
		new Identifier("item/empty_armor_slot_leggings"),
		new Identifier("item/empty_armor_slot_boots"),
		new Identifier("item/empty_armor_slot_shield")
	);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
	@VisibleForTesting
	public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '"
			+ MissingSprite.getMissingSprite().getId().getPath()
			+ "',       'missingno': '"
			+ MissingSprite.getMissingSprite().getId().getPath()
			+ "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
		.replace('\'', '"');
	private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.<String, String>newHashMap(ImmutableMap.of("missing", MISSING_DEFINITION));
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');
	private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').limit(2);
	public static final JsonUnbakedModel GENERATION_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "block entity marker"
	);
	private static final StateFactory<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateFactory.Builder<Block, BlockState>(Blocks.field_10124)
		.with(BooleanProperty.create("map"))
		.build(BlockState::new);
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateFactory<Block, BlockState>> BLOCK_STATE_FACTORY_OVERRIDES = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final ResourceManager resourceManager;
	private final SpriteAtlasTexture spriteAtlas;
	private final Set<Identifier> modelsToLoad = Sets.<Identifier>newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> modelRotationCache = Maps.<Triple<Identifier, ModelRotation, Boolean>, BakedModel>newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.<Identifier, BakedModel>newHashMap();

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture) {
		this.resourceManager = resourceManager;
		this.spriteAtlas = spriteAtlasTexture;

		try {
			this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
			this.addModel(MISSING);
		} catch (IOException var5) {
			LOGGER.error("Error loading missing model, should never happen :(", (Throwable)var5);
			throw new RuntimeException(var5);
		}

		BLOCK_STATE_FACTORY_OVERRIDES.forEach(
			(identifier, stateFactory) -> stateFactory.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState)))
		);

		for (Block block : Registry.BLOCK) {
			block.getStateFactory().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
		}

		for (Identifier identifier : Registry.ITEM.keys()) {
			this.addModel(new ModelIdentifier(identifier, "inventory"));
		}

		this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		Set<String> set = Sets.<String>newLinkedHashSet();
		Set<Identifier> set2 = (Set<Identifier>)this.modelsToBake
			.values()
			.stream()
			.flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream())
			.collect(Collectors.toSet());
		set2.addAll(DEFAULT_TEXTURES);
		set.forEach(string -> LOGGER.warn("Unable to resolve texture reference: {}", string));
		this.spriteAtlas.build(this.resourceManager, set2);
		this.modelsToBake.keySet().forEach(identifier -> {
			BakedModel bakedModel = null;

			try {
				bakedModel = this.bake(identifier, ModelRotation.X0_Y0);
			} catch (Exception var4x) {
				LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4x);
			}

			if (bakedModel != null) {
				this.bakedModels.put(identifier, bakedModel);
			}
		});
	}

	private static Predicate<BlockState> stateKeyToPredicate(StateFactory<Block, BlockState> stateFactory, String string) {
		Map<Property<?>, Comparable<?>> map = Maps.<Property<?>, Comparable<?>>newHashMap();

		for (String string2 : COMMA_SPLITTER.split(string)) {
			Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateFactory.getProperty(string3);
				if (property != null && iterator.hasNext()) {
					String string4 = (String)iterator.next();
					Comparable<?> comparable = getPropertyValue((Property<Comparable<?>>)property, string4);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string4 + "' for blockstate property: '" + string3 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string3.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string3 + "'");
				}
			}
		}

		Block block = stateFactory.getBaseObject();
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
		return (T)property.getValue(string).orElse(null);
	}

	public UnbakedModel getOrLoadModel(Identifier identifier) {
		if (this.unbakedModels.containsKey(identifier)) {
			return (UnbakedModel)this.unbakedModels.get(identifier);
		} else if (this.modelsToLoad.contains(identifier)) {
			throw new IllegalStateException("Circular reference while loading " + identifier);
		} else {
			this.modelsToLoad.add(identifier);
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(MISSING);

			while (!this.modelsToLoad.isEmpty()) {
				Identifier identifier2 = (Identifier)this.modelsToLoad.iterator().next();

				try {
					if (!this.unbakedModels.containsKey(identifier2)) {
						this.loadModel(identifier2);
					}
				} catch (ModelLoader.ModelLoaderException var9) {
					LOGGER.warn(var9.getMessage());
					this.unbakedModels.put(identifier2, unbakedModel);
				} catch (Exception var10) {
					LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", identifier2, identifier, var10);
					this.unbakedModels.put(identifier2, unbakedModel);
				} finally {
					this.modelsToLoad.remove(identifier2);
				}
			}

			return (UnbakedModel)this.unbakedModels.getOrDefault(identifier, unbakedModel);
		}
	}

	private void loadModel(Identifier identifier) throws Exception {
		if (!(identifier instanceof ModelIdentifier)) {
			this.putModel(identifier, this.loadModelFromJson(identifier));
		} else {
			ModelIdentifier modelIdentifier = (ModelIdentifier)identifier;
			if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
				JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
				this.putModel(modelIdentifier, jsonUnbakedModel);
				this.unbakedModels.put(identifier2, jsonUnbakedModel);
			} else {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), identifier.getPath());
				StateFactory<Block, BlockState> stateFactory = (StateFactory<Block, BlockState>)Optional.ofNullable(BLOCK_STATE_FACTORY_OVERRIDES.get(identifier2))
					.orElseGet(() -> Registry.BLOCK.get(identifier2).getStateFactory());
				this.variantMapDeserializationContext.setStateFactory(stateFactory);
				ImmutableList<BlockState> immutableList = stateFactory.getStates();
				Map<ModelIdentifier, BlockState> map = Maps.<ModelIdentifier, BlockState>newHashMap();
				immutableList.forEach(blockState -> {
					BlockState var10000 = (BlockState)map.put(BlockModels.getModelId(identifier2, blockState), blockState);
				});
				Map<BlockState, UnbakedModel> map2 = Maps.<BlockState, UnbakedModel>newHashMap();
				Identifier identifier3 = new Identifier(identifier.getNamespace(), "blockstates/" + identifier.getPath() + ".json");

				try {
					List<Pair<String, ModelVariantMap>> list;
					try {
						list = (List<Pair<String, ModelVariantMap>>)this.resourceManager
							.getAllResources(identifier3)
							.stream()
							.map(
								resource -> {
									try {
										InputStream inputStream = resource.getInputStream();
										Throwable var3x = null;

										Pair var4x;
										try {
											var4x = Pair.of(
												resource.getPackName(),
												ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8))
											);
										} catch (Throwable var14x) {
											var3x = var14x;
											throw var14x;
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
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getPackName(), var16x.getMessage()
											)
										);
									}
								}
							)
							.collect(Collectors.toList());
					} catch (IOException var23) {
						LOGGER.warn("Exception loading blockstate definition: {}: {}", identifier3, var23);
						return;
					}

					for (Pair<String, ModelVariantMap> pair : list) {
						ModelVariantMap modelVariantMap = pair.getSecond();
						Map<BlockState, UnbakedModel> map3 = Maps.<BlockState, UnbakedModel>newIdentityHashMap();
						UnbakedModel unbakedModel2;
						if (modelVariantMap.hasMultipartModel()) {
							unbakedModel2 = modelVariantMap.getMultipartMdoel();
							immutableList.forEach(blockState -> {
								UnbakedModel var10000 = (UnbakedModel)map3.put(blockState, unbakedModel2);
							});
						} else {
							unbakedModel2 = null;
						}

						modelVariantMap.getVariantMap()
							.forEach(
								(string, weightedUnbakedModel) -> {
									try {
										immutableList.stream()
											.filter(stateKeyToPredicate(stateFactory, string))
											.forEach(
												blockState -> {
													UnbakedModel unbakedModel2x = (UnbakedModel)map3.put(blockState, weightedUnbakedModel);
													if (unbakedModel2x != null && unbakedModel2x != unbakedModel2) {
														map3.put(blockState, this.unbakedModels.get(MISSING));
														throw new RuntimeException(
															"Overlapping definition with: "
																+ (String)((Entry)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == unbakedModel2x).findFirst().get())
																	.getKey()
														);
													}
												}
											);
									} catch (Exception var11x) {
										LOGGER.warn(
											"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
											identifier3,
											pair.getFirst(),
											string,
											var11x.getMessage()
										);
									}
								}
							);
						map2.putAll(map3);
					}
				} catch (ModelLoader.ModelLoaderException var24) {
					throw var24;
				} catch (Exception var25) {
					throw new ModelLoader.ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier3, var25));
				} finally {
					Iterator var16 = map.entrySet().iterator();

					while (true) {
						if (!var16.hasNext()) {
							;
						} else {
							Entry<ModelIdentifier, BlockState> entry3 = (Entry<ModelIdentifier, BlockState>)var16.next();
							UnbakedModel unbakedModel4 = (UnbakedModel)map2.get(entry3.getValue());
							if (unbakedModel4 == null) {
								LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier3, entry3.getKey());
								unbakedModel4 = (UnbakedModel)this.unbakedModels.get(MISSING);
							}

							this.putModel((Identifier)entry3.getKey(), unbakedModel4);
						}
					}
				}
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

	@Nullable
	public BakedModel bake(Identifier identifier, ModelRotationContainer modelRotationContainer) {
		Triple<Identifier, ModelRotation, Boolean> triple = Triple.of(identifier, modelRotationContainer.getRotation(), modelRotationContainer.isUvLocked());
		if (this.modelRotationCache.containsKey(triple)) {
			return (BakedModel)this.modelRotationCache.get(triple);
		} else {
			UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
			if (unbakedModel instanceof JsonUnbakedModel) {
				JsonUnbakedModel jsonUnbakedModel = (JsonUnbakedModel)unbakedModel;
				if (jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
					return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel)
						.bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, modelRotationContainer);
				}
			}

			BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, modelRotationContainer);
			this.modelRotationCache.put(triple, bakedModel);
			return bakedModel;
		}
	}

	private JsonUnbakedModel loadModelFromJson(Identifier identifier) throws IOException {
		Reader reader = null;
		Resource resource = null;

		JsonUnbakedModel jsonUnbakedModel;
		try {
			String string = identifier.getPath();
			if ("builtin/generated".equals(string)) {
				return GENERATION_MARKER;
			}

			if (!"builtin/entity".equals(string)) {
				if (string.startsWith("builtin/")) {
					String string2 = string.substring("builtin/".length());
					String string3 = (String)BUILTIN_MODEL_DEFINITIONS.get(string2);
					if (string3 == null) {
						throw new FileNotFoundException(identifier.toString());
					}

					reader = new StringReader(string3);
				} else {
					resource = this.resourceManager.getResource(new Identifier(identifier.getNamespace(), "models/" + identifier.getPath() + ".json"));
					reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
				}

				jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
				jsonUnbakedModel.id = identifier.toString();
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

	@Environment(EnvType.CLIENT)
	static class ModelLoaderException extends RuntimeException {
		public ModelLoaderException(String string) {
			super(string);
		}
	}
}
