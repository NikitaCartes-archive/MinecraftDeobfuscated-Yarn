package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placer.BlockPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements Codec<T>, Keyable, IndexedIterable<T> {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final Identifier ROOT_KEY = new Identifier("root");
	protected static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry<>(createRegistryKey("root"), Lifecycle.experimental());
	public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
	public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENT_KEY = createRegistryKey("sound_event");
	public static final RegistryKey<Registry<Fluid>> FLUID_KEY = createRegistryKey("fluid");
	public static final RegistryKey<Registry<StatusEffect>> MOB_EFFECT_KEY = createRegistryKey("mob_effect");
	public static final RegistryKey<Registry<Block>> BLOCK_KEY = createRegistryKey("block");
	public static final RegistryKey<Registry<Enchantment>> ENCHANTMENT_KEY = createRegistryKey("enchantment");
	public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPE_KEY = createRegistryKey("entity_type");
	public static final RegistryKey<Registry<Item>> ITEM_KEY = createRegistryKey("item");
	public static final RegistryKey<Registry<Potion>> POTION_KEY = createRegistryKey("potion");
	public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPE_KEY = createRegistryKey("particle_type");
	public static final RegistryKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_KEY = createRegistryKey("block_entity_type");
	public static final RegistryKey<Registry<PaintingMotive>> MOTIVE_KEY = createRegistryKey("motive");
	public static final RegistryKey<Registry<Identifier>> CUSTOM_STAT_KEY = createRegistryKey("custom_stat");
	public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS_KEY = createRegistryKey("chunk_status");
	public static final RegistryKey<Registry<RuleTestType<?>>> RULE_TEST_KEY = createRegistryKey("rule_test");
	public static final RegistryKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST_KEY = createRegistryKey("pos_rule_test");
	public static final RegistryKey<Registry<ScreenHandlerType<?>>> MENU_KEY = createRegistryKey("menu");
	public static final RegistryKey<Registry<RecipeType<?>>> RECIPE_TYPE_KEY = createRegistryKey("recipe_type");
	public static final RegistryKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER_KEY = createRegistryKey("recipe_serializer");
	public static final RegistryKey<Registry<EntityAttribute>> ATTRIBUTE_KEY = createRegistryKey("attribute");
	public static final RegistryKey<Registry<GameEvent>> GAME_EVENT_KEY = createRegistryKey("game_event");
	public static final RegistryKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_KEY = createRegistryKey("position_source_type");
	public static final RegistryKey<Registry<StatType<?>>> STAT_TYPE_KEY = createRegistryKey("stat_type");
	public static final RegistryKey<Registry<VillagerType>> VILLAGER_TYPE_KEY = createRegistryKey("villager_type");
	public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_KEY = createRegistryKey("villager_profession");
	public static final RegistryKey<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE_KEY = createRegistryKey("point_of_interest_type");
	public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_KEY = createRegistryKey("memory_module_type");
	public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPE_KEY = createRegistryKey("sensor_type");
	public static final RegistryKey<Registry<Schedule>> SCHEDULE_KEY = createRegistryKey("schedule");
	public static final RegistryKey<Registry<Activity>> ACTIVITY_KEY = createRegistryKey("activity");
	public static final RegistryKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE_KEY = createRegistryKey("loot_pool_entry_type");
	public static final RegistryKey<Registry<LootFunctionType>> LOOT_FUNCTION_TYPE_KEY = createRegistryKey("loot_function_type");
	public static final RegistryKey<Registry<LootConditionType>> LOOT_CONDITION_TYPE_KEY = createRegistryKey("loot_condition_type");
	public static final RegistryKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_TYPE_KEY = createRegistryKey("loot_number_provider_type");
	public static final RegistryKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_TYPE_KEY = createRegistryKey("loot_nbt_provider_type");
	public static final RegistryKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_TYPE_KEY = createRegistryKey("loot_score_provider_type");
	public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_KEY = createRegistryKey("dimension_type");
	/**
	 * A registry key representing the {@link World} type. Can be used to obtain
	 * registry keys with the {@link World} type, such as that for the overworld.
	 * 
	 * <p>Notice that {@code this == Registry.DIMENSION_KEY}.
	 * 
	 * @see #DIMENSION_KEY
	 * @see World#OVERWORLD
	 * @see net.minecraft.server.MinecraftServer#worlds
	 */
	public static final RegistryKey<Registry<World>> WORLD_KEY = createRegistryKey("dimension");
	/**
	 * A registry key representing the {@link DimensionOptions} type.
	 * 
	 * <p>Notice that {@code this == Registry.WORLD_KEY}.
	 * 
	 * @see #WORLD_KEY
	 */
	public static final RegistryKey<Registry<DimensionOptions>> DIMENSION_KEY = createRegistryKey("dimension");
	public static final DefaultedRegistry<GameEvent> GAME_EVENT = create(GAME_EVENT_KEY, "step", () -> GameEvent.STEP);
	public static final Registry<SoundEvent> SOUND_EVENT = create(SOUND_EVENT_KEY, () -> SoundEvents.ENTITY_ITEM_PICKUP);
	public static final DefaultedRegistry<Fluid> FLUID = create(FLUID_KEY, "empty", () -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = create(MOB_EFFECT_KEY, () -> StatusEffects.LUCK);
	public static final DefaultedRegistry<Block> BLOCK = create(BLOCK_KEY, "air", () -> Blocks.AIR);
	public static final Registry<Enchantment> ENCHANTMENT = create(ENCHANTMENT_KEY, () -> Enchantments.FORTUNE);
	public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = create(ENTITY_TYPE_KEY, "pig", () -> EntityType.PIG);
	public static final DefaultedRegistry<Item> ITEM = create(ITEM_KEY, "air", () -> Items.AIR);
	public static final DefaultedRegistry<Potion> POTION = create(POTION_KEY, "empty", () -> Potions.EMPTY);
	public static final Registry<ParticleType<?>> PARTICLE_TYPE = create(PARTICLE_TYPE_KEY, () -> ParticleTypes.BLOCK);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = create(BLOCK_ENTITY_TYPE_KEY, () -> BlockEntityType.FURNACE);
	public static final DefaultedRegistry<PaintingMotive> PAINTING_MOTIVE = create(MOTIVE_KEY, "kebab", () -> PaintingMotive.KEBAB);
	public static final Registry<Identifier> CUSTOM_STAT = create(CUSTOM_STAT_KEY, () -> Stats.JUMP);
	public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = create(CHUNK_STATUS_KEY, "empty", () -> ChunkStatus.EMPTY);
	public static final Registry<RuleTestType<?>> RULE_TEST = create(RULE_TEST_KEY, () -> RuleTestType.ALWAYS_TRUE);
	public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = create(POS_RULE_TEST_KEY, () -> PosRuleTestType.ALWAYS_TRUE);
	public static final Registry<ScreenHandlerType<?>> SCREEN_HANDLER = create(MENU_KEY, () -> ScreenHandlerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = create(RECIPE_TYPE_KEY, () -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = create(RECIPE_SERIALIZER_KEY, () -> RecipeSerializer.SHAPELESS);
	public static final Registry<EntityAttribute> ATTRIBUTE = create(ATTRIBUTE_KEY, () -> EntityAttributes.GENERIC_LUCK);
	public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = create(POSITION_SOURCE_TYPE_KEY, () -> PositionSourceType.BLOCK);
	public static final Registry<StatType<?>> STAT_TYPE = create(STAT_TYPE_KEY, () -> Stats.USED);
	public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = create(VILLAGER_TYPE_KEY, "plains", () -> VillagerType.PLAINS);
	public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = create(VILLAGER_PROFESSION_KEY, "none", () -> VillagerProfession.NONE);
	public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = create(
		POINT_OF_INTEREST_TYPE_KEY, "unemployed", () -> PointOfInterestType.UNEMPLOYED
	);
	public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = create(MEMORY_MODULE_TYPE_KEY, "dummy", () -> MemoryModuleType.DUMMY);
	public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = create(SENSOR_TYPE_KEY, "dummy", () -> SensorType.DUMMY);
	public static final Registry<Schedule> SCHEDULE = create(SCHEDULE_KEY, () -> Schedule.EMPTY);
	public static final Registry<Activity> ACTIVITY = create(ACTIVITY_KEY, () -> Activity.IDLE);
	public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = create(LOOT_POOL_ENTRY_TYPE_KEY, () -> LootPoolEntryTypes.EMPTY);
	public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = create(LOOT_FUNCTION_TYPE_KEY, () -> LootFunctionTypes.SET_COUNT);
	public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = create(LOOT_CONDITION_TYPE_KEY, () -> LootConditionTypes.INVERTED);
	public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = create(LOOT_NUMBER_PROVIDER_TYPE_KEY, () -> LootNumberProviderTypes.CONSTANT);
	public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = create(LOOT_NBT_PROVIDER_TYPE_KEY, () -> LootNbtProviderTypes.CONTEXT);
	public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = create(LOOT_SCORE_PROVIDER_TYPE_KEY, () -> LootScoreProviderTypes.CONTEXT);
	public static final RegistryKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_KEY = createRegistryKey("float_provider_type");
	public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = create(FLOAT_PROVIDER_TYPE_KEY, () -> FloatProviderType.CONSTANT);
	public static final RegistryKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_KEY = createRegistryKey("int_provider_type");
	public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = create(INT_PROVIDER_TYPE_KEY, () -> IntProviderType.CONSTANT);
	public static final RegistryKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_KEY = createRegistryKey("height_provider_type");
	public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = create(HEIGHT_PROVIDER_TYPE_KEY, () -> HeightProviderType.CONSTANT);
	public static final RegistryKey<Registry<ChunkGeneratorSettings>> CHUNK_GENERATOR_SETTINGS_KEY = createRegistryKey("worldgen/noise_settings");
	public static final RegistryKey<Registry<ConfiguredSurfaceBuilder<?>>> CONFIGURED_SURFACE_BUILDER_KEY = createRegistryKey(
		"worldgen/configured_surface_builder"
	);
	public static final RegistryKey<Registry<ConfiguredCarver<?>>> CONFIGURED_CARVER_KEY = createRegistryKey("worldgen/configured_carver");
	public static final RegistryKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_KEY = createRegistryKey("worldgen/configured_feature");
	public static final RegistryKey<Registry<ConfiguredStructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_KEY = createRegistryKey(
		"worldgen/configured_structure_feature"
	);
	public static final RegistryKey<Registry<StructureProcessorList>> STRUCTURE_PROCESSOR_LIST_KEY = createRegistryKey("worldgen/processor_list");
	public static final RegistryKey<Registry<StructurePool>> STRUCTURE_POOL_KEY = createRegistryKey("worldgen/template_pool");
	public static final RegistryKey<Registry<Biome>> BIOME_KEY = createRegistryKey("worldgen/biome");
	public static final RegistryKey<Registry<SurfaceBuilder<?>>> SURFACE_BUILD_KEY = createRegistryKey("worldgen/surface_builder");
	public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = create(SURFACE_BUILD_KEY, () -> SurfaceBuilder.DEFAULT);
	public static final RegistryKey<Registry<Carver<?>>> CARVER_KEY = createRegistryKey("worldgen/carver");
	public static final Registry<Carver<?>> CARVER = create(CARVER_KEY, () -> Carver.CAVE);
	public static final RegistryKey<Registry<Feature<?>>> FEATURE_KEY = createRegistryKey("worldgen/feature");
	public static final Registry<Feature<?>> FEATURE = create(FEATURE_KEY, () -> Feature.ORE);
	public static final RegistryKey<Registry<StructureFeature<?>>> STRUCTURE_FEATURE_KEY = createRegistryKey("worldgen/structure_feature");
	public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = create(STRUCTURE_FEATURE_KEY, () -> StructureFeature.MINESHAFT);
	public static final RegistryKey<Registry<StructurePieceType>> STRUCTURE_PIECE_KEY = createRegistryKey("worldgen/structure_piece");
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = create(STRUCTURE_PIECE_KEY, () -> StructurePieceType.MINESHAFT_ROOM);
	public static final RegistryKey<Registry<Decorator<?>>> DECORATOR_KEY = createRegistryKey("worldgen/decorator");
	public static final Registry<Decorator<?>> DECORATOR = create(DECORATOR_KEY, () -> Decorator.NOPE);
	public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_KEY = createRegistryKey("worldgen/block_state_provider_type");
	public static final RegistryKey<Registry<BlockPlacerType<?>>> BLOCK_PLACER_TYPE_KEY = createRegistryKey("worldgen/block_placer_type");
	public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_KEY = createRegistryKey("worldgen/foliage_placer_type");
	public static final RegistryKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_KEY = createRegistryKey("worldgen/trunk_placer_type");
	public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_KEY = createRegistryKey("worldgen/tree_decorator_type");
	public static final RegistryKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_KEY = createRegistryKey("worldgen/feature_size_type");
	public static final RegistryKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_KEY = createRegistryKey("worldgen/biome_source");
	public static final RegistryKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_KEY = createRegistryKey("worldgen/chunk_generator");
	public static final RegistryKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_KEY = createRegistryKey("worldgen/structure_processor");
	public static final RegistryKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_KEY = createRegistryKey("worldgen/structure_pool_element");
	public static final Registry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = create(
		BLOCK_STATE_PROVIDER_TYPE_KEY, () -> BlockStateProviderType.SIMPLE_STATE_PROVIDER
	);
	public static final Registry<BlockPlacerType<?>> BLOCK_PLACER_TYPE = create(BLOCK_PLACER_TYPE_KEY, () -> BlockPlacerType.SIMPLE_BLOCK_PLACER);
	public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = create(FOLIAGE_PLACER_TYPE_KEY, () -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = create(TRUNK_PLACER_TYPE_KEY, () -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
	public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = create(TREE_DECORATOR_TYPE_KEY, () -> TreeDecoratorType.LEAVE_VINE);
	public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = create(FEATURE_SIZE_TYPE_KEY, () -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
	public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = create(BIOME_SOURCE_KEY, Lifecycle.stable(), () -> BiomeSource.CODEC);
	public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = create(CHUNK_GENERATOR_KEY, Lifecycle.stable(), () -> ChunkGenerator.CODEC);
	public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = create(STRUCTURE_PROCESSOR_KEY, () -> StructureProcessorType.BLOCK_IGNORE);
	public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = create(
		STRUCTURE_POOL_ELEMENT_KEY, () -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	/**
	 * The key representing the type of elements held by this registry. It is also the
	 * key of this registry within the root registry.
	 */
	private final RegistryKey<? extends Registry<T>> registryKey;
	private final Lifecycle lifecycle;

	private static <T> RegistryKey<Registry<T>> createRegistryKey(String registryId) {
		return RegistryKey.ofRegistry(new Identifier(registryId));
	}

	public static <T extends MutableRegistry<?>> void validate(MutableRegistry<T> registry) {
		registry.forEach(mutableRegistry2 -> {
			if (mutableRegistry2.getIds().isEmpty()) {
				Util.error("Registry '" + registry.getId((T)mutableRegistry2) + "' was empty after loading");
			}

			if (mutableRegistry2 instanceof DefaultedRegistry) {
				Identifier identifier = ((DefaultedRegistry)mutableRegistry2).getDefaultId();
				Validate.notNull(mutableRegistry2.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier);
			}
		});
	}

	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Supplier<T> defaultEntry) {
		return create(key, Lifecycle.experimental(), defaultEntry);
	}

	private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Supplier<T> defaultEntry) {
		return create(key, defaultId, Lifecycle.experimental(), defaultEntry);
	}

	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Supplier<T> defaultEntry) {
		return create(key, new SimpleRegistry<>(key, lifecycle), defaultEntry, lifecycle);
	}

	private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, Supplier<T> defaultEntry) {
		return create(key, new DefaultedRegistry<>(defaultId, key, lifecycle), defaultEntry, lifecycle);
	}

	private static <T, R extends MutableRegistry<T>> R create(RegistryKey<? extends Registry<T>> key, R registry, Supplier<T> defaultEntry, Lifecycle lifecycle) {
		Identifier identifier = key.getValue();
		DEFAULT_ENTRIES.put(identifier, defaultEntry);
		MutableRegistry<R> mutableRegistry = ROOT;
		return mutableRegistry.add((RegistryKey<R>)key, registry, lifecycle);
	}

	protected Registry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		Bootstrap.method_36235(() -> "registry " + key);
		this.registryKey = key;
		this.lifecycle = lifecycle;
	}

	public RegistryKey<? extends Registry<T>> getKey() {
		return this.registryKey;
	}

	public String toString() {
		return "Registry[" + this.registryKey + " (" + this.lifecycle + ")]";
	}

	@Override
	public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> dynamicOps, U object) {
		return dynamicOps.compressMaps()
			? dynamicOps.getNumberValue(object).flatMap(number -> {
				T objectx = this.get(number.intValue());
				return objectx == null ? DataResult.error("Unknown registry id: " + number) : DataResult.success(objectx, this.getEntryLifecycle(objectx));
			}).map(objectx -> Pair.of(objectx, dynamicOps.empty()))
			: Identifier.CODEC
				.decode(dynamicOps, object)
				.flatMap(
					pair -> {
						T objectx = this.get((Identifier)pair.getFirst());
						return objectx == null
							? DataResult.error("Unknown registry key: " + pair.getFirst())
							: DataResult.success(Pair.of(objectx, pair.getSecond()), this.getEntryLifecycle(objectx));
					}
				);
	}

	@Override
	public <U> DataResult<U> encode(T object, DynamicOps<U> dynamicOps, U object2) {
		Identifier identifier = this.getId(object);
		if (identifier == null) {
			return DataResult.error("Unknown registry element " + object);
		} else {
			return dynamicOps.compressMaps()
				? dynamicOps.mergeToPrimitive(object2, dynamicOps.createInt(this.getRawId(object))).setLifecycle(this.lifecycle)
				: dynamicOps.mergeToPrimitive(object2, dynamicOps.createString(identifier.toString())).setLifecycle(this.lifecycle);
		}
	}

	@Override
	public <U> Stream<U> keys(DynamicOps<U> dynamicOps) {
		return this.getIds().stream().map(identifier -> dynamicOps.createString(identifier.toString()));
	}

	@Nullable
	public abstract Identifier getId(T entry);

	public abstract Optional<RegistryKey<T>> getKey(T entry);

	@Override
	public abstract int getRawId(@Nullable T entry);

	@Nullable
	public abstract T get(@Nullable RegistryKey<T> key);

	@Nullable
	public abstract T get(@Nullable Identifier id);

	/**
	 * Gets the lifecycle of a registry entry.
	 */
	protected abstract Lifecycle getEntryLifecycle(T entry);

	public abstract Lifecycle getLifecycle();

	public Optional<T> getOrEmpty(@Nullable Identifier id) {
		return Optional.ofNullable(this.get(id));
	}

	public Optional<T> getOrEmpty(@Nullable RegistryKey<T> key) {
		return Optional.ofNullable(this.get(key));
	}

	/**
	 * Gets an entry from the registry.
	 * 
	 * @throws IllegalStateException if the entry was not present in the registry
	 */
	public T getOrThrow(RegistryKey<T> key) {
		T object = this.get(key);
		if (object == null) {
			throw new IllegalStateException("Missing: " + key);
		} else {
			return object;
		}
	}

	public abstract Set<Identifier> getIds();

	public abstract Set<Entry<RegistryKey<T>, T>> getEntries();

	@Nullable
	public abstract T getRandom(Random random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public abstract boolean containsId(Identifier id);

	public abstract boolean contains(RegistryKey<T> key);

	public static <T> T register(Registry<? super T> registry, String id, T entry) {
		return register(registry, new Identifier(id), entry);
	}

	public static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry) {
		return ((MutableRegistry)registry).add(RegistryKey.of(registry.registryKey, id), entry, Lifecycle.stable());
	}

	public static <V, T extends V> T register(Registry<V> registry, int rawId, String id, T entry) {
		return ((MutableRegistry)registry).set(rawId, RegistryKey.of(registry.registryKey, new Identifier(id)), entry, Lifecycle.stable());
	}

	static {
		BuiltinRegistries.init();
		DEFAULT_ENTRIES.forEach((identifier, supplier) -> {
			if (supplier.get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", identifier);
			}
		});
		validate(ROOT);
	}
}
