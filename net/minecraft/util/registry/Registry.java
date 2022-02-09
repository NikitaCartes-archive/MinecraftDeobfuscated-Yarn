/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
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
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Registry<T>
implements Keyable,
IndexedIterable<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<Identifier, Supplier<?>> DEFAULT_ENTRIES = Maps.newLinkedHashMap();
    public static final Identifier ROOT_KEY = new Identifier("root");
    protected static final MutableRegistry<MutableRegistry<?>> ROOT = new SimpleRegistry(Registry.createRegistryKey("root"), Lifecycle.experimental(), null);
    public static final Registry<? extends Registry<?>> REGISTRIES = ROOT;
    public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENT_KEY = Registry.createRegistryKey("sound_event");
    public static final RegistryKey<Registry<Fluid>> FLUID_KEY = Registry.createRegistryKey("fluid");
    public static final RegistryKey<Registry<StatusEffect>> MOB_EFFECT_KEY = Registry.createRegistryKey("mob_effect");
    public static final RegistryKey<Registry<Block>> BLOCK_KEY = Registry.createRegistryKey("block");
    public static final RegistryKey<Registry<Enchantment>> ENCHANTMENT_KEY = Registry.createRegistryKey("enchantment");
    public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPE_KEY = Registry.createRegistryKey("entity_type");
    public static final RegistryKey<Registry<Item>> ITEM_KEY = Registry.createRegistryKey("item");
    public static final RegistryKey<Registry<Potion>> POTION_KEY = Registry.createRegistryKey("potion");
    public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPE_KEY = Registry.createRegistryKey("particle_type");
    public static final RegistryKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_KEY = Registry.createRegistryKey("block_entity_type");
    public static final RegistryKey<Registry<PaintingMotive>> MOTIVE_KEY = Registry.createRegistryKey("motive");
    public static final RegistryKey<Registry<Identifier>> CUSTOM_STAT_KEY = Registry.createRegistryKey("custom_stat");
    public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS_KEY = Registry.createRegistryKey("chunk_status");
    public static final RegistryKey<Registry<RuleTestType<?>>> RULE_TEST_KEY = Registry.createRegistryKey("rule_test");
    public static final RegistryKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST_KEY = Registry.createRegistryKey("pos_rule_test");
    public static final RegistryKey<Registry<ScreenHandlerType<?>>> MENU_KEY = Registry.createRegistryKey("menu");
    public static final RegistryKey<Registry<RecipeType<?>>> RECIPE_TYPE_KEY = Registry.createRegistryKey("recipe_type");
    public static final RegistryKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER_KEY = Registry.createRegistryKey("recipe_serializer");
    public static final RegistryKey<Registry<EntityAttribute>> ATTRIBUTE_KEY = Registry.createRegistryKey("attribute");
    public static final RegistryKey<Registry<GameEvent>> GAME_EVENT_KEY = Registry.createRegistryKey("game_event");
    public static final RegistryKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_KEY = Registry.createRegistryKey("position_source_type");
    public static final RegistryKey<Registry<StatType<?>>> STAT_TYPE_KEY = Registry.createRegistryKey("stat_type");
    public static final RegistryKey<Registry<VillagerType>> VILLAGER_TYPE_KEY = Registry.createRegistryKey("villager_type");
    public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_KEY = Registry.createRegistryKey("villager_profession");
    public static final RegistryKey<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE_KEY = Registry.createRegistryKey("point_of_interest_type");
    public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_KEY = Registry.createRegistryKey("memory_module_type");
    public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPE_KEY = Registry.createRegistryKey("sensor_type");
    public static final RegistryKey<Registry<Schedule>> SCHEDULE_KEY = Registry.createRegistryKey("schedule");
    public static final RegistryKey<Registry<Activity>> ACTIVITY_KEY = Registry.createRegistryKey("activity");
    public static final RegistryKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE_KEY = Registry.createRegistryKey("loot_pool_entry_type");
    public static final RegistryKey<Registry<LootFunctionType>> LOOT_FUNCTION_TYPE_KEY = Registry.createRegistryKey("loot_function_type");
    public static final RegistryKey<Registry<LootConditionType>> LOOT_CONDITION_TYPE_KEY = Registry.createRegistryKey("loot_condition_type");
    public static final RegistryKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_TYPE_KEY = Registry.createRegistryKey("loot_number_provider_type");
    public static final RegistryKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_TYPE_KEY = Registry.createRegistryKey("loot_nbt_provider_type");
    public static final RegistryKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_TYPE_KEY = Registry.createRegistryKey("loot_score_provider_type");
    public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE_KEY = Registry.createRegistryKey("dimension_type");
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
    public static final RegistryKey<Registry<World>> WORLD_KEY = Registry.createRegistryKey("dimension");
    /**
     * A registry key representing the {@link DimensionOptions} type.
     * 
     * <p>Notice that {@code this == Registry.WORLD_KEY}.
     * 
     * @see #WORLD_KEY
     */
    public static final RegistryKey<Registry<DimensionOptions>> DIMENSION_KEY = Registry.createRegistryKey("dimension");
    public static final DefaultedRegistry<GameEvent> GAME_EVENT = Registry.create(GAME_EVENT_KEY, "step", GameEvent::getRegistryEntry, (Registry<T> registry) -> GameEvent.STEP);
    public static final Registry<SoundEvent> SOUND_EVENT = Registry.create(SOUND_EVENT_KEY, registry -> SoundEvents.ENTITY_ITEM_PICKUP);
    public static final DefaultedRegistry<Fluid> FLUID = Registry.create(FLUID_KEY, "empty", Fluid::getRegistryEntry, (Registry<T> registry) -> Fluids.EMPTY);
    public static final Registry<StatusEffect> STATUS_EFFECT = Registry.create(MOB_EFFECT_KEY, registry -> StatusEffects.LUCK);
    public static final DefaultedRegistry<Block> BLOCK = Registry.create(BLOCK_KEY, "air", Block::getRegistryEntry, (Registry<T> registry) -> Blocks.AIR);
    public static final Registry<Enchantment> ENCHANTMENT = Registry.create(ENCHANTMENT_KEY, registry -> Enchantments.FORTUNE);
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = Registry.create(ENTITY_TYPE_KEY, "pig", EntityType::getRegistryEntry, (Registry<T> registry) -> EntityType.PIG);
    public static final DefaultedRegistry<Item> ITEM = Registry.create(ITEM_KEY, "air", Item::getRegistryEntry, (Registry<T> registry) -> Items.AIR);
    public static final DefaultedRegistry<Potion> POTION = Registry.create(POTION_KEY, "empty", (Registry<T> registry) -> Potions.EMPTY);
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = Registry.create(PARTICLE_TYPE_KEY, registry -> ParticleTypes.BLOCK);
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = Registry.create(BLOCK_ENTITY_TYPE_KEY, registry -> BlockEntityType.FURNACE);
    public static final DefaultedRegistry<PaintingMotive> PAINTING_MOTIVE = Registry.create(MOTIVE_KEY, "kebab", (Registry<T> registry) -> PaintingMotive.KEBAB);
    public static final Registry<Identifier> CUSTOM_STAT = Registry.create(CUSTOM_STAT_KEY, registry -> Stats.JUMP);
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = Registry.create(CHUNK_STATUS_KEY, "empty", (Registry<T> registry) -> ChunkStatus.EMPTY);
    public static final Registry<RuleTestType<?>> RULE_TEST = Registry.create(RULE_TEST_KEY, registry -> RuleTestType.ALWAYS_TRUE);
    public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = Registry.create(POS_RULE_TEST_KEY, registry -> PosRuleTestType.ALWAYS_TRUE);
    public static final Registry<ScreenHandlerType<?>> SCREEN_HANDLER = Registry.create(MENU_KEY, registry -> ScreenHandlerType.ANVIL);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = Registry.create(RECIPE_TYPE_KEY, registry -> RecipeType.CRAFTING);
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = Registry.create(RECIPE_SERIALIZER_KEY, registry -> RecipeSerializer.SHAPELESS);
    public static final Registry<EntityAttribute> ATTRIBUTE = Registry.create(ATTRIBUTE_KEY, registry -> EntityAttributes.GENERIC_LUCK);
    public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = Registry.create(POSITION_SOURCE_TYPE_KEY, registry -> PositionSourceType.BLOCK);
    public static final Registry<StatType<?>> STAT_TYPE = Registry.create(STAT_TYPE_KEY, registry -> Stats.USED);
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = Registry.create(VILLAGER_TYPE_KEY, "plains", (Registry<T> registry) -> VillagerType.PLAINS);
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = Registry.create(VILLAGER_PROFESSION_KEY, "none", (Registry<T> registry) -> VillagerProfession.NONE);
    public static final DefaultedRegistry<PointOfInterestType> POINT_OF_INTEREST_TYPE = Registry.create(POINT_OF_INTEREST_TYPE_KEY, "unemployed", (Registry<T> registry) -> PointOfInterestType.UNEMPLOYED);
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = Registry.create(MEMORY_MODULE_TYPE_KEY, "dummy", (Registry<T> registry) -> MemoryModuleType.DUMMY);
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = Registry.create(SENSOR_TYPE_KEY, "dummy", (Registry<T> registry) -> SensorType.DUMMY);
    public static final Registry<Schedule> SCHEDULE = Registry.create(SCHEDULE_KEY, registry -> Schedule.EMPTY);
    public static final Registry<Activity> ACTIVITY = Registry.create(ACTIVITY_KEY, registry -> Activity.IDLE);
    public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = Registry.create(LOOT_POOL_ENTRY_TYPE_KEY, registry -> LootPoolEntryTypes.EMPTY);
    public static final Registry<LootFunctionType> LOOT_FUNCTION_TYPE = Registry.create(LOOT_FUNCTION_TYPE_KEY, registry -> LootFunctionTypes.SET_COUNT);
    public static final Registry<LootConditionType> LOOT_CONDITION_TYPE = Registry.create(LOOT_CONDITION_TYPE_KEY, registry -> LootConditionTypes.INVERTED);
    public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = Registry.create(LOOT_NUMBER_PROVIDER_TYPE_KEY, registry -> LootNumberProviderTypes.CONSTANT);
    public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = Registry.create(LOOT_NBT_PROVIDER_TYPE_KEY, registry -> LootNbtProviderTypes.CONTEXT);
    public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = Registry.create(LOOT_SCORE_PROVIDER_TYPE_KEY, registry -> LootScoreProviderTypes.CONTEXT);
    public static final RegistryKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_KEY = Registry.createRegistryKey("float_provider_type");
    public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = Registry.create(FLOAT_PROVIDER_TYPE_KEY, registry -> FloatProviderType.CONSTANT);
    public static final RegistryKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_KEY = Registry.createRegistryKey("int_provider_type");
    public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = Registry.create(INT_PROVIDER_TYPE_KEY, tegistry -> IntProviderType.CONSTANT);
    public static final RegistryKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_KEY = Registry.createRegistryKey("height_provider_type");
    public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = Registry.create(HEIGHT_PROVIDER_TYPE_KEY, registry -> HeightProviderType.CONSTANT);
    public static final RegistryKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE_KEY = Registry.createRegistryKey("block_predicate_type");
    public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = Registry.create(BLOCK_PREDICATE_TYPE_KEY, registry -> BlockPredicateType.NOT);
    public static final RegistryKey<Registry<ChunkGeneratorSettings>> CHUNK_GENERATOR_SETTINGS_KEY = Registry.createRegistryKey("worldgen/noise_settings");
    public static final RegistryKey<Registry<ConfiguredCarver<?>>> CONFIGURED_CARVER_KEY = Registry.createRegistryKey("worldgen/configured_carver");
    public static final RegistryKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_KEY = Registry.createRegistryKey("worldgen/configured_feature");
    public static final RegistryKey<Registry<PlacedFeature>> PLACED_FEATURE_KEY = Registry.createRegistryKey("worldgen/placed_feature");
    public static final RegistryKey<Registry<ConfiguredStructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_KEY = Registry.createRegistryKey("worldgen/configured_structure_feature");
    public static final RegistryKey<Registry<StructureProcessorList>> STRUCTURE_PROCESSOR_LIST_KEY = Registry.createRegistryKey("worldgen/processor_list");
    public static final RegistryKey<Registry<StructurePool>> STRUCTURE_POOL_KEY = Registry.createRegistryKey("worldgen/template_pool");
    public static final RegistryKey<Registry<Biome>> BIOME_KEY = Registry.createRegistryKey("worldgen/biome");
    public static final RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_WORLDGEN = Registry.createRegistryKey("worldgen/noise");
    public static final RegistryKey<Registry<Carver<?>>> CARVER_KEY = Registry.createRegistryKey("worldgen/carver");
    public static final Registry<Carver<?>> CARVER = Registry.create(CARVER_KEY, registry -> Carver.CAVE);
    public static final RegistryKey<Registry<Feature<?>>> FEATURE_KEY = Registry.createRegistryKey("worldgen/feature");
    public static final Registry<Feature<?>> FEATURE = Registry.create(FEATURE_KEY, registry -> Feature.ORE);
    public static final RegistryKey<Registry<StructureFeature<?>>> STRUCTURE_FEATURE_KEY = Registry.createRegistryKey("worldgen/structure_feature");
    public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = Registry.create(STRUCTURE_FEATURE_KEY, registry -> StructureFeature.MINESHAFT);
    public static final RegistryKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT_KEY = Registry.createRegistryKey("worldgen/structure_placement");
    public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = Registry.create(STRUCTURE_PLACEMENT_KEY, registry -> StructurePlacementType.RANDOM_SPREAD);
    public static final RegistryKey<Registry<StructurePieceType>> STRUCTURE_PIECE_KEY = Registry.createRegistryKey("worldgen/structure_piece");
    public static final Registry<StructurePieceType> STRUCTURE_PIECE = Registry.create(STRUCTURE_PIECE_KEY, registry -> StructurePieceType.MINESHAFT_ROOM);
    public static final RegistryKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_TYPE_KEY = Registry.createRegistryKey("worldgen/placement_modifier_type");
    public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = Registry.create(PLACEMENT_MODIFIER_TYPE_KEY, registry -> PlacementModifierType.COUNT);
    public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_KEY = Registry.createRegistryKey("worldgen/block_state_provider_type");
    public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_KEY = Registry.createRegistryKey("worldgen/foliage_placer_type");
    public static final RegistryKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_KEY = Registry.createRegistryKey("worldgen/trunk_placer_type");
    public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_KEY = Registry.createRegistryKey("worldgen/tree_decorator_type");
    public static final RegistryKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_KEY = Registry.createRegistryKey("worldgen/feature_size_type");
    public static final RegistryKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_KEY = Registry.createRegistryKey("worldgen/biome_source");
    public static final RegistryKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_KEY = Registry.createRegistryKey("worldgen/chunk_generator");
    public static final RegistryKey<Registry<Codec<? extends MaterialRules.MaterialCondition>>> MATERIAL_CONDITION_KEY = Registry.createRegistryKey("worldgen/material_condition");
    public static final RegistryKey<Registry<Codec<? extends MaterialRules.MaterialRule>>> MATERIAL_RULE_KEY = Registry.createRegistryKey("worldgen/material_rule");
    public static final RegistryKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_KEY = Registry.createRegistryKey("worldgen/structure_processor");
    public static final RegistryKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_KEY = Registry.createRegistryKey("worldgen/structure_pool_element");
    public static final Registry<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = Registry.create(BLOCK_STATE_PROVIDER_TYPE_KEY, registry -> BlockStateProviderType.SIMPLE_STATE_PROVIDER);
    public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = Registry.create(FOLIAGE_PLACER_TYPE_KEY, registry -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = Registry.create(TRUNK_PLACER_TYPE_KEY, registry -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
    public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = Registry.create(TREE_DECORATOR_TYPE_KEY, registry -> TreeDecoratorType.LEAVE_VINE);
    public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = Registry.create(FEATURE_SIZE_TYPE_KEY, registry -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
    public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = Registry.create(BIOME_SOURCE_KEY, Lifecycle.stable(), (Registry<T> registry) -> BiomeSource.CODEC);
    public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = Registry.create(CHUNK_GENERATOR_KEY, Lifecycle.stable(), (Registry<T> registry) -> ChunkGenerator.CODEC);
    public static final Registry<Codec<? extends MaterialRules.MaterialCondition>> MATERIAL_CONDITION = Registry.create(MATERIAL_CONDITION_KEY, MaterialRules.MaterialCondition::registerAndGetDefault);
    public static final Registry<Codec<? extends MaterialRules.MaterialRule>> MATERIAL_RULE = Registry.create(MATERIAL_RULE_KEY, MaterialRules.MaterialRule::registerAndGetDefault);
    public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = Registry.create(STRUCTURE_PROCESSOR_KEY, registry -> StructureProcessorType.BLOCK_IGNORE);
    public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = Registry.create(STRUCTURE_POOL_ELEMENT_KEY, registry -> StructurePoolElementType.EMPTY_POOL_ELEMENT);
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
                Util.error("Registry '" + registries.getId((Registry)registry) + "' was empty after loading");
            }
            if (registry instanceof DefaultedRegistry) {
                Identifier identifier = ((DefaultedRegistry)registry).getDefaultId();
                Validate.notNull(registry.get(identifier), "Missing default of DefaultedMappedRegistry: " + identifier, new Object[0]);
            }
        });
    }

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, Lifecycle.experimental(), defaultEntryGetter);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, defaultId, Lifecycle.experimental(), defaultEntryGetter);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Function<T, RegistryEntry.Reference<T>> valueToEntryFunction, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, defaultId, Lifecycle.experimental(), valueToEntryFunction, defaultEntryGetter);
    }

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, new SimpleRegistry(key, lifecycle, null), defaultEntryGetter, lifecycle);
    }

    private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, Function<T, RegistryEntry.Reference<T>> valueToEntryFunction, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, new SimpleRegistry<T>(key, lifecycle, valueToEntryFunction), defaultEntryGetter, lifecycle);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, new DefaultedRegistry(defaultId, key, lifecycle, null), defaultEntryGetter, lifecycle);
    }

    private static <T> DefaultedRegistry<T> create(RegistryKey<? extends Registry<T>> key, String defaultId, Lifecycle lifecycle, Function<T, RegistryEntry.Reference<T>> valueToEntryFunction, DefaultEntryGetter<T> defaultEntryGetter) {
        return Registry.create(key, new DefaultedRegistry<T>(defaultId, key, lifecycle, valueToEntryFunction), defaultEntryGetter, lifecycle);
    }

    private static <T, R extends MutableRegistry<T>> R create(RegistryKey<? extends Registry<T>> key, R registry, DefaultEntryGetter<T> defaultEntryGetter, Lifecycle lifecycle) {
        Identifier identifier = key.getValue();
        DEFAULT_ENTRIES.put(identifier, () -> defaultEntryGetter.run(registry));
        ROOT.add(key, registry, lifecycle);
        return registry;
    }

    protected Registry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        Bootstrap.ensureBootstrapped(() -> "registry " + key);
        this.registryKey = key;
        this.lifecycle = lifecycle;
    }

    public static void freezeRegistries() {
        for (Registry registry : REGISTRIES) {
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
        Codec<Object> codec = Identifier.CODEC.flatXmap(id -> Optional.ofNullable(this.get((Identifier)id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry key in " + this.registryKey + ": " + id)), value -> this.getKey(value).map(RegistryKey::getValue).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry element in " + this.registryKey + ":" + value)));
        Codec<Object> codec2 = Codecs.rawIdChecked(value -> this.getKey(value).isPresent() ? this.getRawId(value) : -1, this::get, -1);
        return Codecs.withLifecycle(Codecs.orCompressed(codec, codec2), this::getEntryLifecycle, value -> this.lifecycle);
    }

    public Codec<RegistryEntry<T>> createEntryCodec() {
        Codec<RegistryEntry> codec = Identifier.CODEC.flatXmap(id -> this.getEntry(RegistryKey.of(this.registryKey, id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry key in " + this.registryKey + ": " + id)), entry -> entry.getKey().map(RegistryKey::getValue).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown registry element in " + this.registryKey + ":" + entry)));
        return Codecs.withLifecycle(codec, entry -> this.getEntryLifecycle(entry.value()), entry -> this.lifecycle);
    }

    public <U> Stream<U> keys(DynamicOps<U> ops) {
        return this.getIds().stream().map(id -> ops.createString(id.toString()));
    }

    @Nullable
    public abstract Identifier getId(T var1);

    public abstract Optional<RegistryKey<T>> getKey(T var1);

    @Override
    public abstract int getRawId(@Nullable T var1);

    @Nullable
    public abstract T get(@Nullable RegistryKey<T> var1);

    @Nullable
    public abstract T get(@Nullable Identifier var1);

    /**
     * Gets the lifecycle of a registry entry.
     */
    public abstract Lifecycle getEntryLifecycle(T var1);

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
        }
        return object;
    }

    public abstract Set<Identifier> getIds();

    public abstract Set<Map.Entry<RegistryKey<T>, T>> getEntries();

    public abstract Optional<RegistryEntry<T>> getRandom(Random var1);

    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public abstract boolean containsId(Identifier var1);

    public abstract boolean contains(RegistryKey<T> var1);

    public static <T> T register(Registry<? super T> registry, String id, T entry) {
        return Registry.register(registry, new Identifier(id), entry);
    }

    public static <V, T extends V> T register(Registry<V> registry, Identifier id, T entry) {
        return Registry.register(registry, RegistryKey.of(registry.registryKey, id), entry);
    }

    public static <V, T extends V> T register(Registry<V> registry, RegistryKey<V> key, T entry) {
        ((MutableRegistry)registry).add(key, entry, Lifecycle.stable());
        return entry;
    }

    public static <V, T extends V> T register(Registry<V> registry, int rawId, String id, T entry) {
        ((MutableRegistry)registry).set(rawId, RegistryKey.of(registry.registryKey, new Identifier(id)), entry, Lifecycle.stable());
        return entry;
    }

    public abstract Registry<T> freeze();

    public abstract RegistryEntry<T> getOrCreateEntry(RegistryKey<T> var1);

    public abstract RegistryEntry.Reference<T> createEntry(T var1);

    public abstract Optional<RegistryEntry<T>> getEntry(int var1);

    public abstract Optional<RegistryEntry<T>> getEntry(RegistryKey<T> var1);

    public RegistryEntry<T> entryOf(RegistryKey<T> key) {
        return this.getEntry(key).orElseThrow(() -> new IllegalStateException("Missing key in " + this.registryKey + ": " + key));
    }

    public abstract Stream<RegistryEntry.Reference<T>> streamEntries();

    public abstract Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> var1);

    public Iterable<RegistryEntry<T>> iterateEntries(TagKey<T> tag) {
        return DataFixUtils.orElse(this.getEntryList(tag), List.of());
    }

    public abstract RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> var1);

    public abstract Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries();

    public abstract Stream<TagKey<T>> streamTags();

    public abstract boolean containsTag(TagKey<T> var1);

    public abstract void clearTags();

    public abstract void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> var1);

    public IndexedIterable<RegistryEntry<T>> getIndexedEntries() {
        return new IndexedIterable<RegistryEntry<T>>(){

            @Override
            public int getRawId(RegistryEntry<T> registryEntry) {
                return Registry.this.getRawId(registryEntry.value());
            }

            @Override
            @Nullable
            public RegistryEntry<T> get(int i) {
                return Registry.this.getEntry(i).orElse(null);
            }

            @Override
            public int size() {
                return Registry.this.size();
            }

            @Override
            public Iterator<RegistryEntry<T>> iterator() {
                return Registry.this.streamEntries().map(entry -> entry).iterator();
            }

            @Override
            @Nullable
            public /* synthetic */ Object get(int index) {
                return this.get(index);
            }
        };
    }

    static {
        BuiltinRegistries.init();
        DEFAULT_ENTRIES.forEach((? super K id, ? super V defaultEntry) -> {
            if (defaultEntry.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", id);
            }
        });
        Registry.validate(ROOT);
    }

    @FunctionalInterface
    static interface DefaultEntryGetter<T> {
        public T run(Registry<T> var1);
    }
}

