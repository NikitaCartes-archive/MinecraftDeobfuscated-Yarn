package net.minecraft.client.render.model;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BlockStatesLoader {
	private static final Logger LOGGER = LogUtils.getLogger();
	static final int field_52259 = -1;
	private static final int field_52262 = 0;
	public static final ResourceFinder FINDER = ResourceFinder.json("blockstates");
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');
	private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);
	private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_MANAGER = new StateManager.Builder<Block, BlockState>(Blocks.AIR)
		.add(BooleanProperty.of("map"))
		.build(Block::getDefaultState, BlockState::new);
	private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = Map.of(
		Identifier.ofVanilla("item_frame"), ITEM_FRAME_STATE_MANAGER, Identifier.ofVanilla("glow_item_frame"), ITEM_FRAME_STATE_MANAGER
	);
	private final Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates;
	private final Profiler profiler;
	private final BlockColors blockColors;
	private final BiConsumer<ModelIdentifier, UnbakedModel> onLoad;
	private int lookupId = 1;
	private final Object2IntMap<BlockState> stateLookup = Util.make(new Object2IntOpenHashMap<>(), map -> map.defaultReturnValue(-1));
	private final BlockStatesLoader.BlockModel missingModel;
	private final ModelVariantMap.DeserializationContext context = new ModelVariantMap.DeserializationContext();

	public BlockStatesLoader(
		Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates,
		Profiler profiler,
		UnbakedModel missingModel,
		BlockColors blockColors,
		BiConsumer<ModelIdentifier, UnbakedModel> onLoad
	) {
		this.blockStates = blockStates;
		this.profiler = profiler;
		this.blockColors = blockColors;
		this.onLoad = onLoad;
		BlockStatesLoader.ModelDefinition modelDefinition = new BlockStatesLoader.ModelDefinition(List.of(missingModel), List.of());
		this.missingModel = new BlockStatesLoader.BlockModel(missingModel, () -> modelDefinition);
	}

	public void load() {
		this.profiler.push("static_definitions");
		STATIC_DEFINITIONS.forEach(this::loadBlockStates);
		this.profiler.swap("blocks");

		for (Block block : Registries.BLOCK) {
			this.loadBlockStates(block.getRegistryEntry().registryKey().getValue(), block.getStateManager());
		}

		this.profiler.pop();
	}

	private void loadBlockStates(Identifier id, StateManager<Block, BlockState> stateManager) {
		this.context.setStateFactory(stateManager);
		List<Property<?>> list = List.copyOf(this.blockColors.getProperties(stateManager.getOwner()));
		List<BlockState> list2 = stateManager.getStates();
		Map<ModelIdentifier, BlockState> map = new HashMap();
		list2.forEach(state -> map.put(BlockModels.getModelId(id, state), state));
		Map<BlockState, BlockStatesLoader.BlockModel> map2 = new HashMap();
		Identifier identifier = FINDER.toResourcePath(id);

		try {
			for (BlockStatesLoader.SourceTrackedData sourceTrackedData : (List)this.blockStates.getOrDefault(identifier, List.of())) {
				ModelVariantMap modelVariantMap = sourceTrackedData.readVariantMap(id, this.context);
				Map<BlockState, BlockStatesLoader.BlockModel> map3 = new IdentityHashMap();
				MultipartUnbakedModel multipartUnbakedModel;
				if (modelVariantMap.hasMultipartModel()) {
					multipartUnbakedModel = modelVariantMap.getMultipartModel();
					list2.forEach(
						state -> map3.put(
								state, new BlockStatesLoader.BlockModel(multipartUnbakedModel, () -> BlockStatesLoader.ModelDefinition.create(state, multipartUnbakedModel, list))
							)
					);
				} else {
					multipartUnbakedModel = null;
				}

				modelVariantMap.getVariantMap()
					.forEach(
						(variant, model) -> {
							try {
								list2.stream()
									.filter(toStatePredicate(stateManager, variant))
									.forEach(
										state -> {
											BlockStatesLoader.BlockModel blockModel = (BlockStatesLoader.BlockModel)map3.put(
												state, new BlockStatesLoader.BlockModel(model, () -> BlockStatesLoader.ModelDefinition.create(state, model, list))
											);
											if (blockModel != null && blockModel.model != multipartUnbakedModel) {
												map3.put(state, this.missingModel);
												throw new RuntimeException(
													"Overlapping definition with: "
														+ (String)((Entry)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == blockModel.model).findFirst().get())
															.getKey()
												);
											}
										}
									);
							} catch (Exception var12x) {
								LOGGER.warn(
									"Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}",
									identifier,
									sourceTrackedData.source,
									variant,
									var12x.getMessage()
								);
							}
						}
					);
				map2.putAll(map3);
			}
		} catch (BlockStatesLoader.ModelLoaderException var18) {
			LOGGER.warn("{}", var18.getMessage());
		} catch (Exception var19) {
			LOGGER.warn("Exception loading blockstate definition: '{}'", identifier, var19);
		} finally {
			Map<BlockStatesLoader.ModelDefinition, Set<BlockState>> map5 = new HashMap();
			map.forEach((modelId, state) -> {
				BlockStatesLoader.BlockModel blockModel = (BlockStatesLoader.BlockModel)map2.get(state);
				if (blockModel == null) {
					LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", identifier, modelId);
					blockModel = this.missingModel;
				}

				this.onLoad.accept(modelId, blockModel.model);

				try {
					BlockStatesLoader.ModelDefinition modelDefinition = (BlockStatesLoader.ModelDefinition)blockModel.key().get();
					((Set)map5.computeIfAbsent(modelDefinition, definition -> Sets.newIdentityHashSet())).add(state);
				} catch (Exception var8) {
					LOGGER.warn("Exception evaluating model definition: '{}'", modelId, var8);
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

	private static Predicate<BlockState> toStatePredicate(StateManager<Block, BlockState> stateManager, String predicate) {
		Map<Property<?>, Comparable<?>> map = new HashMap();

		for (String string : COMMA_SPLITTER.split(predicate)) {
			Iterator<String> iterator = EQUAL_SIGN_SPLITTER.split(string).iterator();
			if (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				Property<?> property = stateManager.getProperty(string2);
				if (property != null && iterator.hasNext()) {
					String string3 = (String)iterator.next();
					Comparable<?> comparable = parseProperty((Property<Comparable<?>>)property, string3);
					if (comparable == null) {
						throw new RuntimeException("Unknown value: '" + string3 + "' for blockstate property: '" + string2 + "' " + property.getValues());
					}

					map.put(property, comparable);
				} else if (!string2.isEmpty()) {
					throw new RuntimeException("Unknown blockstate property: '" + string2 + "'");
				}
			}
		}

		Block block = stateManager.getOwner();
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
	static <T extends Comparable<T>> T parseProperty(Property<T> property, String value) {
		return (T)property.parse(value).orElse(null);
	}

	private void addStates(Iterable<BlockState> states) {
		int i = this.lookupId++;
		states.forEach(state -> this.stateLookup.put(state, i));
	}

	public Object2IntMap<BlockState> getStateLookup() {
		return this.stateLookup;
	}

	@Environment(EnvType.CLIENT)
	static record BlockModel(UnbakedModel model, Supplier<BlockStatesLoader.ModelDefinition> key) {
	}

	@Environment(EnvType.CLIENT)
	static record ModelDefinition(List<UnbakedModel> components, List<Object> values) {
		public static BlockStatesLoader.ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
			StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
			List<UnbakedModel> list = (List<UnbakedModel>)rawModel.getComponents()
				.stream()
				.filter(component -> component.getPredicate(stateManager).test(state))
				.map(MultipartModelComponent::getModel)
				.collect(Collectors.toUnmodifiableList());
			List<Object> list2 = getStateValues(state, properties);
			return new BlockStatesLoader.ModelDefinition(list, list2);
		}

		public static BlockStatesLoader.ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
			List<Object> list = getStateValues(state, properties);
			return new BlockStatesLoader.ModelDefinition(List.of(rawModel), list);
		}

		private static List<Object> getStateValues(BlockState state, Collection<Property<?>> properties) {
			return (List<Object>)properties.stream().map(state::get).collect(Collectors.toUnmodifiableList());
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

		ModelVariantMap readVariantMap(Identifier id, ModelVariantMap.DeserializationContext context) {
			try {
				return ModelVariantMap.fromJson(context, this.data);
			} catch (Exception var4) {
				throw new BlockStatesLoader.ModelLoaderException(
					String.format(Locale.ROOT, "Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", id, this.source, var4.getMessage())
				);
			}
		}
	}
}
