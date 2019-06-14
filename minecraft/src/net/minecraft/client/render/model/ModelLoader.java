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
	private static final Set<Identifier> DEFAULT_TEXTURES = Sets.<Identifier>newHashSet(
		WATER_FLOW,
		LAVA_FLOW,
		WATER_OVERLAY,
		FIRE_0,
		FIRE_1,
		DESTROY_STAGE_0,
		DESTROY_STAGE_1,
		DESTROY_STAGE_2,
		DESTROY_STAGE_3,
		DESTROY_STAGE_4,
		DESTROY_STAGE_5,
		DESTROY_STAGE_6,
		DESTROY_STAGE_7,
		DESTROY_STAGE_8,
		DESTROY_STAGE_9,
		new Identifier("item/empty_armor_slot_helmet"),
		new Identifier("item/empty_armor_slot_chestplate"),
		new Identifier("item/empty_armor_slot_leggings"),
		new Identifier("item/empty_armor_slot_boots"),
		new Identifier("item/empty_armor_slot_shield")
	);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ModelIdentifier field_5374 = new ModelIdentifier("builtin/missing", "missing");
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
	public static final JsonUnbakedModel GENERATION_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "generation marker"
	);
	public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = SystemUtil.consume(
		JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> jsonUnbakedModel.id = "block entity marker"
	);
	private static final StateFactory<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateFactory.Builder<Block, BlockState>(Blocks.field_10124)
		.method_11667(BooleanProperty.of("map"))
		.build(BlockState::new);
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final Map<Identifier, StateFactory<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of(
		new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY
	);
	private final ResourceManager resourceManager;
	private final SpriteAtlasTexture spriteAtlas;
	private final Set<Identifier> modelsToLoad = Sets.<Identifier>newHashSet();
	private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
	private final Map<Identifier, UnbakedModel> unbakedModels = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> bakedModelCache = Maps.<Triple<Identifier, ModelRotation, Boolean>, BakedModel>newHashMap();
	private final Map<Identifier, UnbakedModel> modelsToBake = Maps.<Identifier, UnbakedModel>newHashMap();
	private final Map<Identifier, BakedModel> bakedModels = Maps.<Identifier, BakedModel>newHashMap();
	private final SpriteAtlasTexture.Data spriteAtlasData;

	public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlasTexture, Profiler profiler) {
		this.resourceManager = resourceManager;
		this.spriteAtlas = spriteAtlasTexture;
		profiler.push("missing_model");

		try {
			this.unbakedModels.put(field_5374, this.loadModelFromJson(field_5374));
			this.method_4727(field_5374);
		} catch (IOException var6) {
			LOGGER.error("Error loading missing model, should never happen :(", (Throwable)var6);
			throw new RuntimeException(var6);
		}

		profiler.swap("static_definitions");
		STATIC_DEFINITIONS.forEach(
			(identifier, stateFactory) -> stateFactory.getStates().forEach(blockState -> this.method_4727(BlockModels.method_3336(identifier, blockState)))
		);
		profiler.swap("blocks");

		for (Block block : Registry.BLOCK) {
			block.method_9595().getStates().forEach(blockState -> this.method_4727(BlockModels.method_3340(blockState)));
		}

		profiler.swap("items");

		for (Identifier identifier : Registry.ITEM.getIds()) {
			this.method_4727(new ModelIdentifier(identifier, "inventory"));
		}

		profiler.swap("special");
		this.method_4727(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		profiler.swap("textures");
		Set<String> set = Sets.<String>newLinkedHashSet();
		Set<Identifier> set2 = (Set<Identifier>)this.modelsToBake
			.values()
			.stream()
			.flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::method_4726, set).stream())
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
				bakedModel = this.method_15878(identifier, ModelRotation.field_5350);
			} catch (Exception var4) {
				LOGGER.warn("Unable to bake model: '{}': {}", identifier, var4);
			}

			if (bakedModel != null) {
				this.bakedModels.put(identifier, bakedModel);
			}
		});
		profiler.pop();
	}

	private static Predicate<BlockState> stateKeyToPredicate(StateFactory<Block, BlockState> stateFactory, String string) {
		Map<Property<?>, Comparable<?>> map = Maps.<Property<?>, Comparable<?>>newHashMap();

		for (String string2 : COMMA_SPLITTER.split(string)) {
			Iterator<String> iterator = KEY_VALUE_SPLITTER.split(string2).iterator();
			if (iterator.hasNext()) {
				String string3 = (String)iterator.next();
				Property<?> property = stateFactory.method_11663(string3);
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
					if (!Objects.equals(blockState.method_11654((Property)entry.getKey()), entry.getValue())) {
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

	public UnbakedModel method_4726(Identifier identifier) {
		if (this.unbakedModels.containsKey(identifier)) {
			return (UnbakedModel)this.unbakedModels.get(identifier);
		} else if (this.modelsToLoad.contains(identifier)) {
			throw new IllegalStateException("Circular reference while loading " + identifier);
		} else {
			this.modelsToLoad.add(identifier);
			UnbakedModel unbakedModel = (UnbakedModel)this.unbakedModels.get(field_5374);

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
			this.method_4729(identifier, this.loadModelFromJson(identifier));
		} else {
			ModelIdentifier modelIdentifier = (ModelIdentifier)identifier;
			if (Objects.equals(modelIdentifier.getVariant(), "inventory")) {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), "item/" + identifier.getPath());
				JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier2);
				this.method_4729(modelIdentifier, jsonUnbakedModel);
				this.unbakedModels.put(identifier2, jsonUnbakedModel);
			} else {
				Identifier identifier2 = new Identifier(identifier.getNamespace(), identifier.getPath());
				StateFactory<Block, BlockState> stateFactory = (StateFactory<Block, BlockState>)Optional.ofNullable(STATIC_DEFINITIONS.get(identifier2))
					.orElseGet(() -> Registry.BLOCK.get(identifier2).method_9595());
				this.variantMapDeserializationContext.setStateFactory(stateFactory);
				ImmutableList<BlockState> immutableList = stateFactory.getStates();
				Map<ModelIdentifier, BlockState> map = Maps.<ModelIdentifier, BlockState>newHashMap();
				immutableList.forEach(blockState -> {
					BlockState var10000 = (BlockState)map.put(BlockModels.method_3336(identifier2, blockState), blockState);
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
												resource.getResourcePackName(),
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
												"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), var16x.getMessage()
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
							unbakedModel2 = modelVariantMap.method_3421();
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
														map3.put(blockState, this.unbakedModels.get(field_5374));
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
								unbakedModel4 = (UnbakedModel)this.unbakedModels.get(field_5374);
							}

							this.method_4729((Identifier)entry3.getKey(), unbakedModel4);
						}
					}
				}
			}
		}
	}

	private void method_4729(Identifier identifier, UnbakedModel unbakedModel) {
		this.unbakedModels.put(identifier, unbakedModel);
		this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
	}

	private void method_4727(ModelIdentifier modelIdentifier) {
		UnbakedModel unbakedModel = this.method_4726(modelIdentifier);
		this.unbakedModels.put(modelIdentifier, unbakedModel);
		this.modelsToBake.put(modelIdentifier, unbakedModel);
	}

	@Nullable
	public BakedModel method_15878(Identifier identifier, ModelBakeSettings modelBakeSettings) {
		Triple<Identifier, ModelRotation, Boolean> triple = Triple.of(identifier, modelBakeSettings.getRotation(), modelBakeSettings.isUvLocked());
		if (this.bakedModelCache.containsKey(triple)) {
			return (BakedModel)this.bakedModelCache.get(triple);
		} else {
			UnbakedModel unbakedModel = this.method_4726(identifier);
			if (unbakedModel instanceof JsonUnbakedModel) {
				JsonUnbakedModel jsonUnbakedModel = (JsonUnbakedModel)unbakedModel;
				if (jsonUnbakedModel.getRootModel() == GENERATION_MARKER) {
					return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::method_4608, jsonUnbakedModel)
						.method_3446(this, jsonUnbakedModel, this.spriteAtlas::method_4608, modelBakeSettings);
				}
			}

			BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::method_4608, modelBakeSettings);
			this.bakedModelCache.put(triple, bakedModel);
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
