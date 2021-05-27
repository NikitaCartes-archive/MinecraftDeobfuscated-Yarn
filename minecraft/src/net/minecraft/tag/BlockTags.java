package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

public final class BlockTags {
	protected static final RequiredTagList<Block> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.BLOCK_KEY, "tags/blocks");
	public static final Tag.Identified<Block> WOOL = register("wool");
	public static final Tag.Identified<Block> PLANKS = register("planks");
	public static final Tag.Identified<Block> STONE_BRICKS = register("stone_bricks");
	public static final Tag.Identified<Block> WOODEN_BUTTONS = register("wooden_buttons");
	public static final Tag.Identified<Block> BUTTONS = register("buttons");
	public static final Tag.Identified<Block> CARPETS = register("carpets");
	public static final Tag.Identified<Block> WOODEN_DOORS = register("wooden_doors");
	public static final Tag.Identified<Block> WOODEN_STAIRS = register("wooden_stairs");
	public static final Tag.Identified<Block> WOODEN_SLABS = register("wooden_slabs");
	public static final Tag.Identified<Block> WOODEN_FENCES = register("wooden_fences");
	public static final Tag.Identified<Block> PRESSURE_PLATES = register("pressure_plates");
	public static final Tag.Identified<Block> WOODEN_PRESSURE_PLATES = register("wooden_pressure_plates");
	public static final Tag.Identified<Block> STONE_PRESSURE_PLATES = register("stone_pressure_plates");
	public static final Tag.Identified<Block> WOODEN_TRAPDOORS = register("wooden_trapdoors");
	public static final Tag.Identified<Block> DOORS = register("doors");
	public static final Tag.Identified<Block> SAPLINGS = register("saplings");
	public static final Tag.Identified<Block> LOGS_THAT_BURN = register("logs_that_burn");
	public static final Tag.Identified<Block> LOGS = register("logs");
	public static final Tag.Identified<Block> DARK_OAK_LOGS = register("dark_oak_logs");
	public static final Tag.Identified<Block> OAK_LOGS = register("oak_logs");
	public static final Tag.Identified<Block> BIRCH_LOGS = register("birch_logs");
	public static final Tag.Identified<Block> ACACIA_LOGS = register("acacia_logs");
	public static final Tag.Identified<Block> JUNGLE_LOGS = register("jungle_logs");
	public static final Tag.Identified<Block> SPRUCE_LOGS = register("spruce_logs");
	public static final Tag.Identified<Block> CRIMSON_STEMS = register("crimson_stems");
	public static final Tag.Identified<Block> WARPED_STEMS = register("warped_stems");
	public static final Tag.Identified<Block> BANNERS = register("banners");
	public static final Tag.Identified<Block> SAND = register("sand");
	public static final Tag.Identified<Block> STAIRS = register("stairs");
	public static final Tag.Identified<Block> SLABS = register("slabs");
	public static final Tag.Identified<Block> WALLS = register("walls");
	public static final Tag.Identified<Block> ANVIL = register("anvil");
	public static final Tag.Identified<Block> RAILS = register("rails");
	public static final Tag.Identified<Block> LEAVES = register("leaves");
	public static final Tag.Identified<Block> TRAPDOORS = register("trapdoors");
	public static final Tag.Identified<Block> SMALL_FLOWERS = register("small_flowers");
	public static final Tag.Identified<Block> BEDS = register("beds");
	public static final Tag.Identified<Block> FENCES = register("fences");
	public static final Tag.Identified<Block> TALL_FLOWERS = register("tall_flowers");
	public static final Tag.Identified<Block> FLOWERS = register("flowers");
	public static final Tag.Identified<Block> PIGLIN_REPELLENTS = register("piglin_repellents");
	public static final Tag.Identified<Block> GOLD_ORES = register("gold_ores");
	public static final Tag.Identified<Block> IRON_ORES = register("iron_ores");
	public static final Tag.Identified<Block> DIAMOND_ORES = register("diamond_ores");
	public static final Tag.Identified<Block> REDSTONE_ORES = register("redstone_ores");
	public static final Tag.Identified<Block> LAPIS_ORES = register("lapis_ores");
	public static final Tag.Identified<Block> COAL_ORES = register("coal_ores");
	public static final Tag.Identified<Block> EMERALD_ORES = register("emerald_ores");
	public static final Tag.Identified<Block> COPPER_ORES = register("copper_ores");
	public static final Tag.Identified<Block> NON_FLAMMABLE_WOOD = register("non_flammable_wood");
	public static final Tag.Identified<Block> CANDLES = register("candles");
	public static final Tag.Identified<Block> DIRT = register("dirt");
	public static final Tag.Identified<Block> FLOWER_POTS = register("flower_pots");
	public static final Tag.Identified<Block> ENDERMAN_HOLDABLE = register("enderman_holdable");
	public static final Tag.Identified<Block> ICE = register("ice");
	public static final Tag.Identified<Block> VALID_SPAWN = register("valid_spawn");
	public static final Tag.Identified<Block> IMPERMEABLE = register("impermeable");
	public static final Tag.Identified<Block> UNDERWATER_BONEMEALS = register("underwater_bonemeals");
	public static final Tag.Identified<Block> CORAL_BLOCKS = register("coral_blocks");
	public static final Tag.Identified<Block> WALL_CORALS = register("wall_corals");
	public static final Tag.Identified<Block> CORAL_PLANTS = register("coral_plants");
	public static final Tag.Identified<Block> CORALS = register("corals");
	public static final Tag.Identified<Block> BAMBOO_PLANTABLE_ON = register("bamboo_plantable_on");
	public static final Tag.Identified<Block> STANDING_SIGNS = register("standing_signs");
	public static final Tag.Identified<Block> WALL_SIGNS = register("wall_signs");
	public static final Tag.Identified<Block> SIGNS = register("signs");
	public static final Tag.Identified<Block> DRAGON_IMMUNE = register("dragon_immune");
	public static final Tag.Identified<Block> WITHER_IMMUNE = register("wither_immune");
	public static final Tag.Identified<Block> WITHER_SUMMON_BASE_BLOCKS = register("wither_summon_base_blocks");
	public static final Tag.Identified<Block> BEEHIVES = register("beehives");
	public static final Tag.Identified<Block> CROPS = register("crops");
	public static final Tag.Identified<Block> BEE_GROWABLES = register("bee_growables");
	public static final Tag.Identified<Block> PORTALS = register("portals");
	public static final Tag.Identified<Block> FIRE = register("fire");
	public static final Tag.Identified<Block> NYLIUM = register("nylium");
	public static final Tag.Identified<Block> WART_BLOCKS = register("wart_blocks");
	public static final Tag.Identified<Block> BEACON_BASE_BLOCKS = register("beacon_base_blocks");
	public static final Tag.Identified<Block> SOUL_SPEED_BLOCKS = register("soul_speed_blocks");
	public static final Tag.Identified<Block> WALL_POST_OVERRIDE = register("wall_post_override");
	public static final Tag.Identified<Block> CLIMBABLE = register("climbable");
	public static final Tag.Identified<Block> SHULKER_BOXES = register("shulker_boxes");
	public static final Tag.Identified<Block> HOGLIN_REPELLENTS = register("hoglin_repellents");
	public static final Tag.Identified<Block> SOUL_FIRE_BASE_BLOCKS = register("soul_fire_base_blocks");
	public static final Tag.Identified<Block> STRIDER_WARM_BLOCKS = register("strider_warm_blocks");
	public static final Tag.Identified<Block> CAMPFIRES = register("campfires");
	public static final Tag.Identified<Block> GUARDED_BY_PIGLINS = register("guarded_by_piglins");
	public static final Tag.Identified<Block> PREVENT_MOB_SPAWNING_INSIDE = register("prevent_mob_spawning_inside");
	public static final Tag.Identified<Block> FENCE_GATES = register("fence_gates");
	public static final Tag.Identified<Block> UNSTABLE_BOTTOM_CENTER = register("unstable_bottom_center");
	public static final Tag.Identified<Block> MUSHROOM_GROW_BLOCK = register("mushroom_grow_block");
	public static final Tag.Identified<Block> INFINIBURN_OVERWORLD = register("infiniburn_overworld");
	public static final Tag.Identified<Block> INFINIBURN_NETHER = register("infiniburn_nether");
	public static final Tag.Identified<Block> INFINIBURN_END = register("infiniburn_end");
	public static final Tag.Identified<Block> BASE_STONE_OVERWORLD = register("base_stone_overworld");
	public static final Tag.Identified<Block> STONE_ORE_REPLACEABLES = register("stone_ore_replaceables");
	public static final Tag.Identified<Block> DEEPSLATE_ORE_REPLACEABLES = register("deepslate_ore_replaceables");
	public static final Tag.Identified<Block> BASE_STONE_NETHER = register("base_stone_nether");
	public static final Tag.Identified<Block> CANDLE_CAKES = register("candle_cakes");
	public static final Tag.Identified<Block> CAULDRONS = register("cauldrons");
	public static final Tag.Identified<Block> CRYSTAL_SOUND_BLOCKS = register("crystal_sound_blocks");
	public static final Tag.Identified<Block> INSIDE_STEP_SOUND_BLOCKS = register("inside_step_sound_blocks");
	public static final Tag.Identified<Block> OCCLUDES_VIBRATION_SIGNALS = register("occludes_vibration_signals");
	public static final Tag.Identified<Block> DRIPSTONE_REPLACEABLE_BLOCKS = register("dripstone_replaceable_blocks");
	public static final Tag.Identified<Block> CAVE_VINES = register("cave_vines");
	public static final Tag.Identified<Block> MOSS_REPLACEABLE = register("moss_replaceable");
	public static final Tag.Identified<Block> LUSH_GROUND_REPLACEABLE = register("lush_ground_replaceable");
	public static final Tag.Identified<Block> SMALL_DRIPLEAF_PLACEABLE = register("small_dripleaf_placeable");
	public static final Tag.Identified<Block> SNOW = register("snow");
	public static final Tag.Identified<Block> AXE_MINEABLE = register("mineable/axe");
	public static final Tag.Identified<Block> HOE_MINEABLE = register("mineable/hoe");
	public static final Tag.Identified<Block> PICKAXE_MINEABLE = register("mineable/pickaxe");
	public static final Tag.Identified<Block> SHOVEL_MINEABLE = register("mineable/shovel");
	public static final Tag.Identified<Block> NEEDS_DIAMOND_TOOL = register("needs_diamond_tool");
	public static final Tag.Identified<Block> NEEDS_IRON_TOOL = register("needs_iron_tool");
	public static final Tag.Identified<Block> NEEDS_STONE_TOOL = register("needs_stone_tool");
	public static final Tag.Identified<Block> FEATURES_CANNOT_REPLACE = register("features_cannot_replace");
	public static final Tag.Identified<Block> LAVA_POOL_STONE_REPLACEABLES = register("lava_pool_stone_replaceables");
	public static final Tag.Identified<Block> GEODE_INVALID_BLOCKS = register("geode_invalid_blocks");

	private BlockTags() {
	}

	private static Tag.Identified<Block> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<Block> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}
}
