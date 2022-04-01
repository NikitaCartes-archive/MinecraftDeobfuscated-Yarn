package net.minecraft.tag;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ItemTags {
	public static final TagKey<Item> WOOL = register("wool");
	public static final TagKey<Item> PLANKS = register("planks");
	public static final TagKey<Item> STONE_BRICKS = register("stone_bricks");
	public static final TagKey<Item> WOODEN_BUTTONS = register("wooden_buttons");
	public static final TagKey<Item> BUTTONS = register("buttons");
	public static final TagKey<Item> CARPETS = register("carpets");
	public static final TagKey<Item> WOODEN_DOORS = register("wooden_doors");
	public static final TagKey<Item> WOODEN_STAIRS = register("wooden_stairs");
	public static final TagKey<Item> WOODEN_SLABS = register("wooden_slabs");
	public static final TagKey<Item> WOODEN_FENCES = register("wooden_fences");
	public static final TagKey<Item> WOODEN_PRESSURE_PLATES = register("wooden_pressure_plates");
	public static final TagKey<Item> WOODEN_TRAPDOORS = register("wooden_trapdoors");
	public static final TagKey<Item> DOORS = register("doors");
	public static final TagKey<Item> SAPLINGS = register("saplings");
	public static final TagKey<Item> LOGS_THAT_BURN = register("logs_that_burn");
	public static final TagKey<Item> LOGS = register("logs");
	public static final TagKey<Item> DARK_OAK_LOGS = register("dark_oak_logs");
	public static final TagKey<Item> OAK_LOGS = register("oak_logs");
	public static final TagKey<Item> BIRCH_LOGS = register("birch_logs");
	public static final TagKey<Item> ACACIA_LOGS = register("acacia_logs");
	public static final TagKey<Item> JUNGLE_LOGS = register("jungle_logs");
	public static final TagKey<Item> SPRUCE_LOGS = register("spruce_logs");
	public static final TagKey<Item> CRIMSON_STEMS = register("crimson_stems");
	public static final TagKey<Item> WARPED_STEMS = register("warped_stems");
	public static final TagKey<Item> BANNERS = register("banners");
	public static final TagKey<Item> SAND = register("sand");
	public static final TagKey<Item> STAIRS = register("stairs");
	public static final TagKey<Item> SLABS = register("slabs");
	public static final TagKey<Item> WALLS = register("walls");
	public static final TagKey<Item> ANVIL = register("anvil");
	public static final TagKey<Item> RAILS = register("rails");
	public static final TagKey<Item> LEAVES = register("leaves");
	public static final TagKey<Item> TRAPDOORS = register("trapdoors");
	public static final TagKey<Item> SMALL_FLOWERS = register("small_flowers");
	public static final TagKey<Item> BEDS = register("beds");
	public static final TagKey<Item> FENCES = register("fences");
	public static final TagKey<Item> TALL_FLOWERS = register("tall_flowers");
	public static final TagKey<Item> FLOWERS = register("flowers");
	public static final TagKey<Item> PIGLIN_REPELLENTS = register("piglin_repellents");
	public static final TagKey<Item> PIGLIN_LOVED = register("piglin_loved");
	public static final TagKey<Item> IGNORED_BY_PIGLIN_BABIES = register("ignored_by_piglin_babies");
	public static final TagKey<Item> PIGLIN_FOOD = register("piglin_food");
	public static final TagKey<Item> FOX_FOOD = register("fox_food");
	public static final TagKey<Item> GOLD_ORES = register("gold_ores");
	public static final TagKey<Item> IRON_ORES = register("iron_ores");
	public static final TagKey<Item> DIAMOND_ORES = register("diamond_ores");
	public static final TagKey<Item> REDSTONE_ORES = register("redstone_ores");
	public static final TagKey<Item> LAPIS_ORES = register("lapis_ores");
	public static final TagKey<Item> COAL_ORES = register("coal_ores");
	public static final TagKey<Item> EMERALD_ORES = register("emerald_ores");
	public static final TagKey<Item> COPPER_ORES = register("copper_ores");
	public static final TagKey<Item> NON_FLAMMABLE_WOOD = register("non_flammable_wood");
	public static final TagKey<Item> SOUL_FIRE_BASE_BLOCKS = register("soul_fire_base_blocks");
	public static final TagKey<Item> CANDLES = register("candles");
	public static final TagKey<Item> DIRT = register("dirt");
	public static final TagKey<Item> TERRACOTTA = register("terracotta");
	public static final TagKey<Item> BOATS = register("boats");
	public static final TagKey<Item> FISHES = register("fishes");
	public static final TagKey<Item> SIGNS = register("signs");
	public static final TagKey<Item> MUSIC_DISCS = register("music_discs");
	public static final TagKey<Item> CREEPER_DROP_MUSIC_DISCS = register("creeper_drop_music_discs");
	public static final TagKey<Item> COALS = register("coals");
	public static final TagKey<Item> ARROWS = register("arrows");
	public static final TagKey<Item> LECTERN_BOOKS = register("lectern_books");
	public static final TagKey<Item> BEACON_PAYMENT_ITEMS = register("beacon_payment_items");
	public static final TagKey<Item> STONE_TOOL_MATERIALS = register("stone_tool_materials");
	public static final TagKey<Item> STONE_CRAFTING_MATERIALS = register("stone_crafting_materials");
	public static final TagKey<Item> FREEZE_IMMUNE_WEARABLES = register("freeze_immune_wearables");
	public static final TagKey<Item> AXOLOTL_TEMPT_ITEMS = register("axolotl_tempt_items");
	public static final TagKey<Item> OCCLUDES_VIBRATION_SIGNALS = register("occludes_vibration_signals");
	public static final TagKey<Item> CLUSTER_MAX_HARVESTABLES = register("cluster_max_harvestables");

	private ItemTags() {
	}

	private static TagKey<Item> register(String id) {
		return TagKey.of(Registry.ITEM_KEY, new Identifier(id));
	}
}
