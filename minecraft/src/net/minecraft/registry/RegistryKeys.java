package net.minecraft.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.Block;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Instrument;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.network.message.MessageType;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.EntitySubPredicate;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.structure.pool.alias.StructurePoolAliasBinding;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.structure.rule.RuleTestType;
import net.minecraft.structure.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.heightprovider.HeightProviderType;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import net.minecraft.world.gen.root.RootPlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraft.world.poi.PointOfInterestType;

public class RegistryKeys {
	public static final Identifier ROOT = Identifier.ofVanilla("root");
	public static final RegistryKey<Registry<Activity>> ACTIVITY = of("activity");
	public static final RegistryKey<Registry<EntityAttribute>> ATTRIBUTE = of("attribute");
	public static final RegistryKey<Registry<BannerPattern>> BANNER_PATTERN = of("banner_pattern");
	public static final RegistryKey<Registry<MapCodec<? extends BiomeSource>>> BIOME_SOURCE = of("worldgen/biome_source");
	public static final RegistryKey<Registry<Block>> BLOCK = of("block");
	public static final RegistryKey<Registry<MapCodec<? extends Block>>> BLOCK_TYPE = of("block_type");
	public static final RegistryKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE = of("block_entity_type");
	public static final RegistryKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE = of("block_predicate_type");
	public static final RegistryKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE = of("worldgen/block_state_provider_type");
	public static final RegistryKey<Registry<Carver<?>>> CARVER = of("worldgen/carver");
	public static final RegistryKey<Registry<CatVariant>> CAT_VARIANT = of("cat_variant");
	public static final RegistryKey<Registry<WolfVariant>> WOLF_VARIANT = of("wolf_variant");
	public static final RegistryKey<Registry<MapCodec<? extends ChunkGenerator>>> CHUNK_GENERATOR = of("worldgen/chunk_generator");
	public static final RegistryKey<Registry<ChunkStatus>> CHUNK_STATUS = of("chunk_status");
	public static final RegistryKey<Registry<ArgumentSerializer<?, ?>>> COMMAND_ARGUMENT_TYPE = of("command_argument_type");
	public static final RegistryKey<Registry<ItemGroup>> ITEM_GROUP = of("creative_mode_tab");
	public static final RegistryKey<Registry<Identifier>> CUSTOM_STAT = of("custom_stat");
	public static final RegistryKey<Registry<DamageType>> DAMAGE_TYPE = of("damage_type");
	public static final RegistryKey<Registry<MapCodec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE = of("worldgen/density_function_type");
	public static final RegistryKey<Registry<MapCodec<? extends EnchantmentEntityEffect>>> ENCHANTMENT_ENTITY_EFFECT_TYPE = of("enchantment_entity_effect_type");
	public static final RegistryKey<Registry<MapCodec<? extends EnchantmentLevelBasedValue>>> ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = of(
		"enchantment_level_based_value_type"
	);
	public static final RegistryKey<Registry<MapCodec<? extends EnchantmentLocationBasedEffect>>> ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = of(
		"enchantment_location_based_effect_type"
	);
	public static final RegistryKey<Registry<MapCodec<? extends EnchantmentProvider>>> ENCHANTMENT_PROVIDER_TYPE = of("enchantment_provider_type");
	public static final RegistryKey<Registry<MapCodec<? extends EnchantmentValueEffect>>> ENCHANTMENT_VALUE_EFFECT_TYPE = of("enchantment_value_effect_type");
	public static final RegistryKey<Registry<EntityType<?>>> ENTITY_TYPE = of("entity_type");
	public static final RegistryKey<Registry<Feature<?>>> FEATURE = of("worldgen/feature");
	public static final RegistryKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE = of("worldgen/feature_size_type");
	public static final RegistryKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE = of("float_provider_type");
	public static final RegistryKey<Registry<Fluid>> FLUID = of("fluid");
	public static final RegistryKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE = of("worldgen/foliage_placer_type");
	public static final RegistryKey<Registry<FrogVariant>> FROG_VARIANT = of("frog_variant");
	public static final RegistryKey<Registry<GameEvent>> GAME_EVENT = of("game_event");
	public static final RegistryKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE = of("height_provider_type");
	public static final RegistryKey<Registry<Instrument>> INSTRUMENT = of("instrument");
	public static final RegistryKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE = of("int_provider_type");
	public static final RegistryKey<Registry<Item>> ITEM = of("item");
	public static final RegistryKey<Registry<JukeboxSong>> JUKEBOX_SONG = of("jukebox_song");
	public static final RegistryKey<Registry<LootConditionType>> LOOT_CONDITION_TYPE = of("loot_condition_type");
	public static final RegistryKey<Registry<LootFunctionType<?>>> LOOT_FUNCTION_TYPE = of("loot_function_type");
	public static final RegistryKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_TYPE = of("loot_nbt_provider_type");
	public static final RegistryKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_TYPE = of("loot_number_provider_type");
	public static final RegistryKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE = of("loot_pool_entry_type");
	public static final RegistryKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_TYPE = of("loot_score_provider_type");
	public static final RegistryKey<Registry<MapCodec<? extends MaterialRules.MaterialCondition>>> MATERIAL_CONDITION = of("worldgen/material_condition");
	public static final RegistryKey<Registry<MapCodec<? extends MaterialRules.MaterialRule>>> MATERIAL_RULE = of("worldgen/material_rule");
	public static final RegistryKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE = of("memory_module_type");
	public static final RegistryKey<Registry<ScreenHandlerType<?>>> SCREEN_HANDLER = of("menu");
	public static final RegistryKey<Registry<StatusEffect>> STATUS_EFFECT = of("mob_effect");
	public static final RegistryKey<Registry<PaintingVariant>> PAINTING_VARIANT = of("painting_variant");
	public static final RegistryKey<Registry<ParticleType<?>>> PARTICLE_TYPE = of("particle_type");
	public static final RegistryKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_TYPE = of("worldgen/placement_modifier_type");
	public static final RegistryKey<Registry<PointOfInterestType>> POINT_OF_INTEREST_TYPE = of("point_of_interest_type");
	public static final RegistryKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE = of("position_source_type");
	public static final RegistryKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST = of("pos_rule_test");
	public static final RegistryKey<Registry<Potion>> POTION = of("potion");
	public static final RegistryKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER = of("recipe_serializer");
	public static final RegistryKey<Registry<RecipeType<?>>> RECIPE_TYPE = of("recipe_type");
	public static final RegistryKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE = of("worldgen/root_placer_type");
	public static final RegistryKey<Registry<RuleTestType<?>>> RULE_TEST = of("rule_test");
	public static final RegistryKey<Registry<RuleBlockEntityModifierType<?>>> RULE_BLOCK_ENTITY_MODIFIER = of("rule_block_entity_modifier");
	public static final RegistryKey<Registry<Schedule>> SCHEDULE = of("schedule");
	public static final RegistryKey<Registry<SensorType<?>>> SENSOR_TYPE = of("sensor_type");
	public static final RegistryKey<Registry<SoundEvent>> SOUND_EVENT = of("sound_event");
	public static final RegistryKey<Registry<StatType<?>>> STAT_TYPE = of("stat_type");
	public static final RegistryKey<Registry<StructurePieceType>> STRUCTURE_PIECE = of("worldgen/structure_piece");
	public static final RegistryKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT = of("worldgen/structure_placement");
	public static final RegistryKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT = of("worldgen/structure_pool_element");
	public static final RegistryKey<Registry<MapCodec<? extends StructurePoolAliasBinding>>> POOL_ALIAS_BINDING = of("worldgen/pool_alias_binding");
	public static final RegistryKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR = of("worldgen/structure_processor");
	public static final RegistryKey<Registry<StructureType<?>>> STRUCTURE_TYPE = of("worldgen/structure_type");
	public static final RegistryKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE = of("worldgen/tree_decorator_type");
	public static final RegistryKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE = of("worldgen/trunk_placer_type");
	public static final RegistryKey<Registry<VillagerProfession>> VILLAGER_PROFESSION = of("villager_profession");
	public static final RegistryKey<Registry<VillagerType>> VILLAGER_TYPE = of("villager_type");
	public static final RegistryKey<Registry<DecoratedPotPattern>> DECORATED_POT_PATTERN = of("decorated_pot_pattern");
	public static final RegistryKey<Registry<NumberFormatType<?>>> NUMBER_FORMAT_TYPE = of("number_format_type");
	public static final RegistryKey<Registry<ArmorMaterial>> ARMOR_MATERIAL = of("armor_material");
	public static final RegistryKey<Registry<ComponentType<?>>> DATA_COMPONENT_TYPE = of("data_component_type");
	public static final RegistryKey<Registry<MapCodec<? extends EntitySubPredicate>>> ENTITY_SUB_PREDICATE_TYPE = of("entity_sub_predicate_type");
	public static final RegistryKey<Registry<ItemSubPredicate.Type<?>>> ITEM_SUB_PREDICATE_TYPE = of("item_sub_predicate_type");
	public static final RegistryKey<Registry<MapDecorationType>> MAP_DECORATION_TYPE = of("map_decoration_type");
	public static final RegistryKey<Registry<ComponentType<?>>> ENCHANTMENT_EFFECT_COMPONENT_TYPE = of("enchantment_effect_component_type");
	public static final RegistryKey<Registry<ConsumeEffect.Type<?>>> CONSUME_EFFECT_TYPE = of("consume_effect_type");
	public static final RegistryKey<Registry<TrialSpawnerConfig>> TRIAL_SPAWNER = of("trial_spawner");
	public static final RegistryKey<Registry<Biome>> BIOME = of("worldgen/biome");
	public static final RegistryKey<Registry<MessageType>> MESSAGE_TYPE = of("chat_type");
	public static final RegistryKey<Registry<ConfiguredCarver<?>>> CONFIGURED_CARVER = of("worldgen/configured_carver");
	public static final RegistryKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE = of("worldgen/configured_feature");
	public static final RegistryKey<Registry<DensityFunction>> DENSITY_FUNCTION = of("worldgen/density_function");
	public static final RegistryKey<Registry<DimensionType>> DIMENSION_TYPE = of("dimension_type");
	public static final RegistryKey<Registry<Enchantment>> ENCHANTMENT = of("enchantment");
	public static final RegistryKey<Registry<EnchantmentProvider>> ENCHANTMENT_PROVIDER = of("enchantment_provider");
	public static final RegistryKey<Registry<FlatLevelGeneratorPreset>> FLAT_LEVEL_GENERATOR_PRESET = of("worldgen/flat_level_generator_preset");
	public static final RegistryKey<Registry<ChunkGeneratorSettings>> CHUNK_GENERATOR_SETTINGS = of("worldgen/noise_settings");
	public static final RegistryKey<Registry<DoublePerlinNoiseSampler.NoiseParameters>> NOISE_PARAMETERS = of("worldgen/noise");
	public static final RegistryKey<Registry<PlacedFeature>> PLACED_FEATURE = of("worldgen/placed_feature");
	public static final RegistryKey<Registry<Structure>> STRUCTURE = of("worldgen/structure");
	public static final RegistryKey<Registry<StructureProcessorList>> PROCESSOR_LIST = of("worldgen/processor_list");
	public static final RegistryKey<Registry<StructureSet>> STRUCTURE_SET = of("worldgen/structure_set");
	public static final RegistryKey<Registry<StructurePool>> TEMPLATE_POOL = of("worldgen/template_pool");
	public static final RegistryKey<Registry<Criterion<?>>> CRITERION = of("trigger_type");
	public static final RegistryKey<Registry<ArmorTrimMaterial>> TRIM_MATERIAL = of("trim_material");
	public static final RegistryKey<Registry<ArmorTrimPattern>> TRIM_PATTERN = of("trim_pattern");
	public static final RegistryKey<Registry<WorldPreset>> WORLD_PRESET = of("worldgen/world_preset");
	public static final RegistryKey<Registry<MultiNoiseBiomeSourceParameterList>> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = of(
		"worldgen/multi_noise_biome_source_parameter_list"
	);
	public static final RegistryKey<Registry<World>> WORLD = of("dimension");
	public static final RegistryKey<Registry<DimensionOptions>> DIMENSION = of("dimension");
	public static final RegistryKey<Registry<LootTable>> LOOT_TABLE = of("loot_table");
	public static final RegistryKey<Registry<LootFunction>> ITEM_MODIFIER = of("item_modifier");
	public static final RegistryKey<Registry<LootCondition>> PREDICATE = of("predicate");
	public static final RegistryKey<Registry<Advancement>> ADVANCEMENT = of("advancement");
	public static final RegistryKey<Registry<Recipe<?>>> RECIPE = of("recipe");

	public static RegistryKey<World> toWorldKey(RegistryKey<DimensionOptions> key) {
		return RegistryKey.of(WORLD, key.getValue());
	}

	public static RegistryKey<DimensionOptions> toDimensionKey(RegistryKey<World> key) {
		return RegistryKey.of(DIMENSION, key.getValue());
	}

	private static <T> RegistryKey<Registry<T>> of(String id) {
		return RegistryKey.ofRegistry(Identifier.ofVanilla(id));
	}

	public static String getPath(RegistryKey<? extends Registry<?>> registryRef) {
		return registryRef.getValue().getPath();
	}

	public static String getTagPath(RegistryKey<? extends Registry<?>> registryRef) {
		return "tags/" + registryRef.getValue().getPath();
	}
}
