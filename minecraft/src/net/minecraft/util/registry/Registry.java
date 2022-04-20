package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Bootstrap;
import net.minecraft.class_7408;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
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
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
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
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.AbstractRandom;
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
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public abstract class Registry<T> implements Keyable, IndexedIterable<T> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.<Identifier, Supplier<?>>newLinkedHashMap();
	public static final Identifier ROOT_KEY = new Identifier("root");
	protected static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry<>(createRegistryKey("root"), Lifecycle.experimental(), null);
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
	public static final RegistryKey<Registry<PaintingMotive>> MOTIVE_KEY = createRegistryKey("painting_variant");
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
	public static final RegistryKey<Registry<ArgumentSerializer<?, ?>>> COMMAND_ARGUMENT_TYPE_KEY = createRegistryKey("command_argument_type");
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
	public static final DefaultedRegistry<GameEvent> GAME_EVENT = create(GAME_EVENT_KEY, "step", GameEvent::getRegistryEntry, registry -> GameEvent.STEP);
	public static final Registry<SoundEvent> SOUND_EVENT = create(SOUND_EVENT_KEY, registry -> SoundEvents.ENTITY_ITEM_PICKUP);
	public static final DefaultedRegistry<Fluid> FLUID = create(FLUID_KEY, "empty", Fluid::getRegistryEntry, registry -> Fluids.EMPTY);
	public static final Registry<StatusEffect> STATUS_EFFECT = create(MOB_EFFECT_KEY, registry -> StatusEffects.LUCK);
	public static final DefaultedRegistry<Block> BLOCK = create(BLOCK_KEY, "air", Block::getRegistryEntry, registry -> Blocks.AIR);
	public static final Registry<Enchantment> ENCHANTMENT = create(ENCHANTMENT_KEY, registry -> Enchantments.FORTUNE);
	public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = create(ENTITY_TYPE_KEY, "pig", EntityType::getRegistryEntry, registry -> EntityType.PIG);
	public static final DefaultedRegistry<Item> ITEM = create(ITEM_KEY, "air", Item::getRegistryEntry, registry -> Items.AIR);
	public static final DefaultedRegistry<Potion> POTION = create(POTION_KEY, "empty", registry -> Potions.EMPTY);
	public static final Registry<ParticleType<?>> PARTICLE_TYPE = create(PARTICLE_TYPE_KEY, registry -> ParticleTypes.BLOCK);
	public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = create(BLOCK_ENTITY_TYPE_KEY, registry -> BlockEntityType.FURNACE);
	public static final DefaultedRegistry<PaintingMotive> PAINTING_MOTIVE = create(MOTIVE_KEY, "kebab", class_7408::method_43406);
	public static final Registry<Identifier> CUSTOM_STAT = create(CUSTOM_STAT_KEY, registry -> Stats.JUMP);
	public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = create(CHUNK_STATUS_KEY, "empty", registry -> ChunkStatus.EMPTY);
	public static final Registry<RuleTestType<?>> RULE_TEST = create(RULE_TEST_KEY, registry -> RuleTestType.ALWAYS_TRUE);
	public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = create(POS_RULE_TEST_KEY, registry -> PosRuleTestType.ALWAYS_TRUE);
	public static final Registry<ScreenHandlerType<?>> SCREEN_HANDLER = create(MENU_KEY, registry -> ScreenHandlerType.ANVIL);
	public static final Registry<RecipeType<?>> RECIPE_TYPE = create(RECIPE_TYPE_KEY, registry -> RecipeType.CRAFTING);
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = create(RECIPE_SERIALIZER_KEY, registry -> RecipeSerializer.SHAPELESS);
	public static final Registry<EntityAttribute> ATTRIBUTE = create(ATTRIBUTE_KEY, registry -> EntityAttributes.GENERIC_LUCK);
	public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = create(POSITION_SOURCE_TYPE_KEY, registry -> PositionSourceType.BLOCK);
	public static final Registry<ArgumentSerializer<?, ?>> COMMAND_ARGUMENT_TYPE = create(COMMAND_ARGUMENT_TYPE_KEY, ArgumentTypes::register);
	public static final Registry<StatType<?>> STAT_TYPE = create(STAT_TYPE_KEY, registry -> Stats.USED);
	public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = create(VILLAGER_TYPE_KEY, "plains", registry -> VillagerType.PLAINS);
	public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = create(VILLAGER_PROFESSION_KEY, "none", registry -> VillagerProfession.NONE);
	public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = create(
		POINT_OF_INTEREST_TYPE_KEY, "unemployed", registry -> PointOfInterestType.UNEMPLOYED
	);
	public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = create(MEMORY_MODULE_TYPE_KEY, "dummy", registry -> MemoryModuleType.DUMMY);
	public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = create(SENSOR_TYPE_KEY, "dummy", registry -> SensorType.DUMMY);
	public static final Registry<Schedule> SCHEDULE = create(SCHEDULE_KEY, registry -> Schedule.EMPTY);
	public static final Registry<Activity> ACTIVITY = create(ACTIVITY_KEY, registry -> Activity.IDLE);
	public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = create(LOOT_POOL_ENTRY_TYPE_KEY, registry -> LootPoolEntryTypes.EMPTY);
	public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = create(LOOT_FUNCTION_TYPE_KEY, registry -> LootFunctionTypes.SET_COUNT);
	public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = create(LOOT_CONDITION_TYPE_KEY, registry -> LootConditionTypes.INVERTED);
	public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = create(
		LOOT_NUMBER_PROVIDER_TYPE_KEY, registry -> LootNumberProviderTypes.CONSTANT
	);
	public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = create(LOOT_NBT_PROVIDER_TYPE_KEY, registry -> LootNbtProviderTypes.CONTEXT);
	public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = create(LOOT_SCORE_PROVIDER_TYPE_KEY, registry -> LootScoreProviderTypes.CONTEXT);
	public static final RegistryKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_KEY = createRegistryKey("float_provider_type");
	public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = create(FLOAT_PROVIDER_TYPE_KEY, registry -> FloatProviderType.CONSTANT);
	public static final RegistryKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_KEY = createRegistryKey("int_provider_type");
	public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = create(INT_PROVIDER_TYPE_KEY, registry -> IntProviderType.CONSTANT);
	public static final RegistryKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_KEY = createRegistryKey("height_provider_type");
	public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = create(HEIGHT_PROVIDER_TYPE_KEY, registry -> HeightProviderType.CONSTANT);
	public static final RegistryKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE_KEY = createRegistryKey("block_predicate_type");
	public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = create(BLOCK_PREDICATE_TYPE_KEY, registry -> BlockPredicateType.NOT);
	public static final RegistryKey<Registry<ChunkGeneratorSettings>> CHUNK_GENERATOR_SETTINGS_KEY = createRegistryKey("worldgen/noise_settings");
	public static final RegistryKey<Registry<ConfiguredCarver<?>>> CONFIGURED_CARVER_KEY = createRegistryKey("worldgen/configured_carver");
	public static final RegistryKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_KEY = createRegistryKey("worldgen/configured_feature");
	public static final RegistryKey<Registry<PlacedFeature>> PLACED_FEATURE_KEY = createRegistryKey("worldgen/placed_feature");
	public static final RegistryKey<Registry<StructureType>> STRUCTURE_KEY = createRegistryKey("worldgen/structure");
	public static final RegistryKey<Registry<StructureSet>> STRUCTURE_SET_KEY = createRegistryKey("worldgen/structure_set");
	public static final RegistryKey<Registry<StructureProcessorList>> STRUCTURE_PROCESSOR_LIST_KEY = createRegistryKey("worldgen/processor_list");
	public static final RegistryKey<Registry<StructurePool>> STRUCTURE_POOL_KEY = createRegistryKey("worldgen/template_pool");
	public static final RegistryKey<Registry<Biome>> BIOME_KEY = createRegistryKey("worldgen/biome");
	public static final RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_WORLDGEN = createRegistryKey("worldgen/noise");
	public static final RegistryKey<Registry<DensityFunction>> DENSITY_FUNCTION_KEY = createRegistryKey("worldgen/density_function");
	public static final RegistryKey<Registry<WorldPreset>> WORLD_PRESET_WORLDGEN = createRegistryKey("worldgen/world_preset");
	public static final RegistryKey<Registry<FlatLevelGeneratorPreset>> FLAT_LEVEL_GENERATOR_PRESET_WORLDGEN = createRegistryKey(
		"worldgen/flat_level_generator_preset"
	);
	public static final RegistryKey<Registry<Carver<?>>> CARVER_KEY = createRegistryKey("worldgen/carver");
	public static final Registry<Carver<?>> CARVER = create(CARVER_KEY, registry -> Carver.CAVE);
	public static final RegistryKey<Registry<Feature<?>>> FEATURE_KEY = createRegistryKey("worldgen/feature");
	public static final Registry<Feature<?>> FEATURE = create(FEATURE_KEY, registry -> Feature.ORE);
	public static final RegistryKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT_KEY = createRegistryKey("worldgen/structure_placement");
	public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = create(STRUCTURE_PLACEMENT_KEY, registry -> StructurePlacementType.RANDOM_SPREAD);
	public static final RegistryKey<Registry<StructurePieceType>> STRUCTURE_PIECE_KEY = createRegistryKey("worldgen/structure_piece");
	public static final Registry<StructurePieceType> STRUCTURE_PIECE = create(STRUCTURE_PIECE_KEY, registry -> StructurePieceType.MINESHAFT_ROOM);
	public static final RegistryKey<Registry<net.minecraft.structure.StructureType<?>>> STRUCTURE_TYPE_KEY = createRegistryKey("worldgen/structure_type");
	public static final Registry<net.minecraft.structure.StructureType<?>> STRUCTURE_TYPE = create(
		STRUCTURE_TYPE_KEY, registry -> net.minecraft.structure.StructureType.JIGSAW
	);
	public static final RegistryKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_TYPE_KEY = createRegistryKey("worldgen/placement_modifier_type");
	public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = create(PLACEMENT_MODIFIER_TYPE_KEY, registry -> PlacementModifierType.COUNT);
	public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_KEY = createRegistryKey("worldgen/block_state_provider_type");
	public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_KEY = createRegistryKey("worldgen/foliage_placer_type");
	public static final RegistryKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_KEY = createRegistryKey("worldgen/trunk_placer_type");
	public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_KEY = createRegistryKey("worldgen/tree_decorator_type");
	public static final RegistryKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE_KEY = createRegistryKey("worldgen/root_placer_type");
	public static final RegistryKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_KEY = createRegistryKey("worldgen/feature_size_type");
	public static final RegistryKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_KEY = createRegistryKey("worldgen/biome_source");
	public static final RegistryKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_KEY = createRegistryKey("worldgen/chunk_generator");
	public static final RegistryKey<Registry<Codec<? extends MaterialRules.MaterialCondition>>> MATERIAL_CONDITION_KEY = createRegistryKey(
		"worldgen/material_condition"
	);
	public static final RegistryKey<Registry<Codec<? extends MaterialRules.MaterialRule>>> MATERIAL_RULE_KEY = createRegistryKey("worldgen/material_rule");
	public static final RegistryKey<Registry<Codec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE_KEY = createRegistryKey("worldgen/density_function_type");
	public static final RegistryKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_KEY = createRegistryKey("worldgen/structure_processor");
	public static final RegistryKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_KEY = createRegistryKey("worldgen/structure_pool_element");
	public static final Registry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = create(
		BLOCK_STATE_PROVIDER_TYPE_KEY, registry -> BlockStateProviderType.SIMPLE_STATE_PROVIDER
	);
	public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = create(FOLIAGE_PLACER_TYPE_KEY, registry -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
	public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = create(TRUNK_PLACER_TYPE_KEY, registry -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
	public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = create(ROOT_PLACER_TYPE_KEY, registry -> RootPlacerType.MANGROVE_ROOT_PLACER);
	public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = create(TREE_DECORATOR_TYPE_KEY, registry -> TreeDecoratorType.LEAVE_VINE);
	public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = create(FEATURE_SIZE_TYPE_KEY, registry -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
	public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = create(BIOME_SOURCE_KEY, Lifecycle.stable(), registry -> BiomeSource.CODEC);
	public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = create(
		CHUNK_GENERATOR_KEY, Lifecycle.stable(), registry -> ChunkGenerator.CODEC
	);
	public static final Registry<Codec<? extends MaterialRules.MaterialCondition>> MATERIAL_CONDITION = create(
		MATERIAL_CONDITION_KEY, MaterialRules.MaterialCondition::registerAndGetDefault
	);
	public static final Registry<Codec<? extends MaterialRules.MaterialRule>> MATERIAL_RULE = create(
		MATERIAL_RULE_KEY, MaterialRules.MaterialRule::registerAndGetDefault
	);
	public static final Registry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = create(
		DENSITY_FUNCTION_TYPE_KEY, DensityFunctionTypes::registerAndGetDefault
	);
	public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = create(STRUCTURE_PROCESSOR_KEY, registry -> StructureProcessorType.BLOCK_IGNORE);
	public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = create(
		STRUCTURE_POOL_ELEMENT_KEY, registry -> StructurePoolElementType.EMPTY_POOL_ELEMENT
	);
	public static final RegistryKey<Registry<CatVariant>> CAT_VARIANT_KEY = createRegistryKey("cat_variant");
	public static final Registry<CatVariant> CAT_VARIANT = create(CAT_VARIANT_KEY, registry -> CatVariant.BLACK);
	public static final RegistryKey<Registry<FrogVariant>> FROG_VARIANT_KEY = createRegistryKey("frog_variant");
	public static final Registry<FrogVariant> FROG_VARIANT = create(FROG_VARIANT_KEY, registry -> FrogVariant.TEMPERATE);
	/**
	 * The key representing the type of elements held by this registry. It is also the
	 * key of this registry within the root registry.
	 */
	private final RegistryKey<? extends Registry<T>> registryKey;
	private final Lifecycle lifecycle;

	private static <T> RegistryKey<Registry<T>> createRegistryKey(String registryId) {
		return RegistryKey.ofRegistry(new Identifier(registryId));
	}

	public static <T extends Registry<?>> void validate(Registry<T> registries) {
		registries.forEach(registry -> {
			if (registry.getIds().isEmpty()) {
				Util.error("Registry '" + registries.getId((T)registry) + "' was empty after loading");
			}

			if (registry instanceof DefaultedRegistry) {
				Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
				Validate.notNull(registry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier);
			}
		});
	}

	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Registry.DefaultEntryGetter<T> defaultEntryGetter) {
		return create(key, Lifecycle.experimental(), defaultEntryGetter);
	}

	private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Registry.DefaultEntryGetter<T> defaultEntryGetter) {
		return create(key, defaultId, Lifecycle.experimental(), defaultEntryGetter);
	}

	private static <T> DefaultedRegistry<T> create(
		RegistryKey<? extends Registry<T>> key,
		String defaultId,
		Function<T, RegistryEntry.Reference<T>> valueToEntryFunction,
		Registry.DefaultEntryGetter<T> defaultEntryGetter
	) {
		return create(key, defaultId, Lifecycle.experimental(), valueToEntryFunction, defaultEntryGetter);
	}

	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Registry.DefaultEntryGetter<T> defaultEntryGetter) {
		return create(key, new SimpleRegistry<>(key, lifecycle, null), defaultEntryGetter, lifecycle);
	}

	private static <T> Registry<T> create(
		RegistryKey<? extends Registry<T>> key,
		Lifecycle lifecycle,
		Function<T, RegistryEntry.Reference<T>> valueToEntryFunction,
		Registry.DefaultEntryGetter<T> defaultEntryGetter
	) {
		return create(key, new SimpleRegistry<>(key, lifecycle, valueToEntryFunction), defaultEntryGetter, lifecycle);
	}

	private static <T> DefaultedRegistry<T> create(
		RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, Registry.DefaultEntryGetter<T> defaultEntryGetter
	) {
		return create(key, new DefaultedRegistry<>(defaultId, key, lifecycle, null), defaultEntryGetter, lifecycle);
	}

	private static <T> DefaultedRegistry<T> create(
		RegistryKey<? extends Registry<T>> key,
		String defaultId,
		Lifecycle lifecycle,
		Function<T, RegistryEntry.Reference<T>> valueToEntryFunction,
		Registry.DefaultEntryGetter<T> defaultEntryGetter
	) {
		return create(key, new DefaultedRegistry<>(defaultId, key, lifecycle, valueToEntryFunction), defaultEntryGetter, lifecycle);
	}

	private static <T, R extends MutableRegistry<T>> R create(
		RegistryKey<? extends Registry<T>> key, R registry, Registry.DefaultEntryGetter<T> defaultEntryGetter, Lifecycle lifecycle
	) {
		Identifier identifier = key.getValue();
		DEFAULT_ENTRIES.put(identifier, (Supplier)() -> defaultEntryGetter.run(registry));
		ROOT.add((RegistryKey<MutableRegistry<?>>)key, registry, lifecycle);
		return registry;
	}

	protected Registry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		Bootstrap.ensureBootstrapped(() -> "registry " + key);
		this.registryKey = key;
		this.lifecycle = lifecycle;
	}

	public static void freezeRegistries() {
		for (Registry<?> registry : REGISTRIES) {
			registry.freeze();
		}
	}

	public RegistryKey<? extends Registry<T>> getKey() {
		return this.registryKey;
	}

	public Lifecycle method_39198() {
		return this.lifecycle;
	}

	public String toString() {
		return "Registry[" + this.registryKey + " (" + this.lifecycle + ")]";
	}

	public Codec<T> getCodec() {
		Codec<T> codec = Identifier.CODEC
			.flatXmap(
				id -> (DataResult)Optional.ofNullable(this.get(id))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown registry key in " + this.registryKey + ": " + id)),
				value -> (DataResult)this.getKey((T)value)
						.map(RegistryKey::getValue)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown registry element in " + this.registryKey + ":" + value))
			);
		Codec<T> codec2 = Codecs.rawIdChecked(value -> this.getKey((T)value).isPresent() ? this.getRawId((T)value) : -1, this::get, -1);
		return Codecs.withLifecycle(Codecs.orCompressed(codec, codec2), this::getEntryLifecycle, value -> this.lifecycle);
	}

	public Codec<RegistryEntry<T>> createEntryCodec() {
		Codec<RegistryEntry<T>> codec = Identifier.CODEC
			.flatXmap(
				id -> (DataResult)this.getEntry(RegistryKey.of(this.registryKey, id))
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown registry key in " + this.registryKey + ": " + id)),
				entry -> (DataResult)entry.getKey()
						.map(RegistryKey::getValue)
						.map(DataResult::success)
						.orElseGet(() -> DataResult.error("Unknown registry element in " + this.registryKey + ":" + entry))
			);
		return Codecs.withLifecycle(codec, entry -> this.getEntryLifecycle((T)entry.value()), entry -> this.lifecycle);
	}

	@Override
	public <U> Stream<U> keys(DynamicOps<U> ops) {
		return this.getIds().stream().map(id -> ops.createString(id.toString()));
	}

	@Nullable
	public abstract Identifier getId(T value);

	public abstract Optional<RegistryKey<T>> getKey(T entry);

	@Override
	public abstract int getRawId(@Nullable T value);

	@Nullable
	public abstract T get(@Nullable RegistryKey<T> key);

	@Nullable
	public abstract T get(@Nullable Identifier id);

	/**
	 * Gets the lifecycle of a registry entry.
	 */
	public abstract Lifecycle getEntryLifecycle(T entry);

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
			throw new IllegalStateException("Missing key in " + this.registryKey + ": " + key);
		} else {
			return object;
		}
	}

	public abstract Set<Identifier> getIds();

	public abstract Set<Entry<RegistryKey<T>, T>> getEntrySet();

	public abstract Set<RegistryKey<T>> getKeys();

	public abstract Optional<RegistryEntry<T>> getRandom(AbstractRandom random);

	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	public abstract boolean containsId(Identifier id);

	public abstract boolean contains(RegistryKey<T> key);

	public static <T> T register(Registry<? super T> registry, String id, T entry) {
		return register(registry, new Identifier(id), entry);
	}

	public static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry) {
		return register(registry, RegistryKey.of(registry.registryKey, id), entry);
	}

	public static <V, T extends V> T register(Registry<V> registry, RegistryKey<V> key, T entry) {
		((MutableRegistry)registry).add(key, (V)entry, Lifecycle.stable());
		return entry;
	}

	public static <V, T extends V> T register(Registry<V> registry, int rawId, String id, T entry) {
		((MutableRegistry)registry).set(rawId, RegistryKey.of(registry.registryKey, new Identifier(id)), (V)entry, Lifecycle.stable());
		return entry;
	}

	public abstract Registry<T> freeze();

	public abstract RegistryEntry<T> getOrCreateEntry(RegistryKey<T> key);

	public abstract RegistryEntry.Reference<T> createEntry(T value);

	public abstract Optional<RegistryEntry<T>> getEntry(int rawId);

	public abstract Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key);

	public RegistryEntry<T> entryOf(RegistryKey<T> key) {
		return (RegistryEntry<T>)this.getEntry(key).orElseThrow(() -> new IllegalStateException("Missing key in " + this.registryKey + ": " + key));
	}

	public abstract Stream<RegistryEntry.Reference<T>> streamEntries();

	public abstract Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag);

	public Iterable<RegistryEntry<T>> iterateEntries(TagKey<T> tag) {
		return DataFixUtils.orElse(this.getEntryList(tag), List.of());
	}

	public abstract RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag);

	public abstract Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries();

	public abstract Stream<TagKey<T>> streamTags();

	public abstract boolean containsTag(TagKey<T> tag);

	public abstract void clearTags();

	public abstract void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries);

	public IndexedIterable<RegistryEntry<T>> getIndexedEntries() {
		return new IndexedIterable<RegistryEntry<T>>() {
			public int getRawId(RegistryEntry<T> registryEntry) {
				return Registry.this.getRawId(registryEntry.value());
			}

			@Nullable
			public RegistryEntry<T> get(int i) {
				return (RegistryEntry<T>)Registry.this.getEntry(i).orElse(null);
			}

			@Override
			public int size() {
				return Registry.this.size();
			}

			public Iterator<RegistryEntry<T>> iterator() {
				return Registry.this.streamEntries().map(entry -> entry).iterator();
			}
		};
	}

	static {
		BuiltinRegistries.init();
		DEFAULT_ENTRIES.forEach((id, defaultEntry) -> {
			if (defaultEntry.get() == null) {
				LOGGER.error("Unable to bootstrap registry '{}'", id);
			}
		});
		validate(ROOT);
	}

	@FunctionalInterface
	interface DefaultEntryGetter<T> {
		T run(Registry<T> registry);
	}
}
