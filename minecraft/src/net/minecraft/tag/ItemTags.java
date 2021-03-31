package net.minecraft.tag;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public final class ItemTags {
	protected static final RequiredTagList<Item> REQUIRED_TAGS = RequiredTagListRegistry.register(Registry.ITEM_KEY, "tags/items");
	public static final Tag.Identified<Item> WOOL = register("wool");
	public static final Tag.Identified<Item> PLANKS = register("planks");
	public static final Tag.Identified<Item> STONE_BRICKS = register("stone_bricks");
	public static final Tag.Identified<Item> WOODEN_BUTTONS = register("wooden_buttons");
	public static final Tag.Identified<Item> BUTTONS = register("buttons");
	public static final Tag.Identified<Item> CARPETS = register("carpets");
	public static final Tag.Identified<Item> WOODEN_DOORS = register("wooden_doors");
	public static final Tag.Identified<Item> WOODEN_STAIRS = register("wooden_stairs");
	public static final Tag.Identified<Item> WOODEN_SLABS = register("wooden_slabs");
	public static final Tag.Identified<Item> WOODEN_FENCES = register("wooden_fences");
	public static final Tag.Identified<Item> WOODEN_PRESSURE_PLATES = register("wooden_pressure_plates");
	public static final Tag.Identified<Item> WOODEN_TRAPDOORS = register("wooden_trapdoors");
	public static final Tag.Identified<Item> DOORS = register("doors");
	public static final Tag.Identified<Item> SAPLINGS = register("saplings");
	public static final Tag.Identified<Item> LOGS_THAT_BURN = register("logs_that_burn");
	public static final Tag.Identified<Item> LOGS = register("logs");
	public static final Tag.Identified<Item> DARK_OAK_LOGS = register("dark_oak_logs");
	public static final Tag.Identified<Item> OAK_LOGS = register("oak_logs");
	public static final Tag.Identified<Item> BIRCH_LOGS = register("birch_logs");
	public static final Tag.Identified<Item> ACACIA_LOGS = register("acacia_logs");
	public static final Tag.Identified<Item> JUNGLE_LOGS = register("jungle_logs");
	public static final Tag.Identified<Item> SPRUCE_LOGS = register("spruce_logs");
	public static final Tag.Identified<Item> CRIMSON_STEMS = register("crimson_stems");
	public static final Tag.Identified<Item> WARPED_STEMS = register("warped_stems");
	public static final Tag.Identified<Item> BANNERS = register("banners");
	public static final Tag.Identified<Item> SAND = register("sand");
	public static final Tag.Identified<Item> STAIRS = register("stairs");
	public static final Tag.Identified<Item> SLABS = register("slabs");
	public static final Tag.Identified<Item> WALLS = register("walls");
	public static final Tag.Identified<Item> ANVIL = register("anvil");
	public static final Tag.Identified<Item> RAILS = register("rails");
	public static final Tag.Identified<Item> LEAVES = register("leaves");
	public static final Tag.Identified<Item> TRAPDOORS = register("trapdoors");
	public static final Tag.Identified<Item> SMALL_FLOWERS = register("small_flowers");
	public static final Tag.Identified<Item> BEDS = register("beds");
	public static final Tag.Identified<Item> FENCES = register("fences");
	public static final Tag.Identified<Item> TALL_FLOWERS = register("tall_flowers");
	public static final Tag.Identified<Item> FLOWERS = register("flowers");
	public static final Tag.Identified<Item> PIGLIN_REPELLENTS = register("piglin_repellents");
	public static final Tag.Identified<Item> PIGLIN_LOVED = register("piglin_loved");
	public static final Tag.Identified<Item> IGNORED_BY_PIGLIN_BABIES = register("ignored_by_piglin_babies");
	public static final Tag.Identified<Item> PIGLIN_FOOD = register("piglin_food");
	public static final Tag.Identified<Item> FOX_FOOD = register("fox_food");
	public static final Tag.Identified<Item> GOLD_ORES = register("gold_ores");
	public static final Tag.Identified<Item> IRON_ORES = register("iron_ores");
	public static final Tag.Identified<Item> DIAMOND_ORES = register("diamond_ores");
	public static final Tag.Identified<Item> REDSTONE_ORES = register("redstone_ores");
	public static final Tag.Identified<Item> LAPIS_ORES = register("lapis_ores");
	public static final Tag.Identified<Item> COAL_ORES = register("coal_ores");
	public static final Tag.Identified<Item> EMERALD_ORES = register("emerald_ores");
	public static final Tag.Identified<Item> COPPER_ORES = register("copper_ores");
	public static final Tag.Identified<Item> NON_FLAMMABLE_WOOD = register("non_flammable_wood");
	public static final Tag.Identified<Item> SOUL_FIRE_BASE_BLOCKS = register("soul_fire_base_blocks");
	public static final Tag.Identified<Item> CANDLES = register("candles");
	public static final Tag.Identified<Item> BOATS = register("boats");
	public static final Tag.Identified<Item> FISHES = register("fishes");
	public static final Tag.Identified<Item> SIGNS = register("signs");
	public static final Tag.Identified<Item> MUSIC_DISCS = register("music_discs");
	public static final Tag.Identified<Item> CREEPER_DROP_MUSIC_DISCS = register("creeper_drop_music_discs");
	public static final Tag.Identified<Item> COALS = register("coals");
	public static final Tag.Identified<Item> ARROWS = register("arrows");
	public static final Tag.Identified<Item> LECTERN_BOOKS = register("lectern_books");
	public static final Tag.Identified<Item> BEACON_PAYMENT_ITEMS = register("beacon_payment_items");
	public static final Tag.Identified<Item> STONE_TOOL_MATERIALS = register("stone_tool_materials");
	public static final Tag.Identified<Item> STONE_CRAFTING_MATERIALS = register("stone_crafting_materials");
	public static final Tag.Identified<Item> FREEZE_IMMUNE_WEARABLES = register("freeze_immune_wearables");
	public static final Tag.Identified<Item> AXOLOTL_TEMPT_ITEMS = register("axolotl_tempt_items");
	public static final Tag.Identified<Item> OCCLUDES_VIBRATION_SIGNALS = register("occludes_vibration_signals");
	public static final Tag.Identified<Item> CLUSTER_MAX_HARVESTABLES = register("cluster_max_harvestables");

	private ItemTags() {
	}

	private static Tag.Identified<Item> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<Item> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}
}
