package net.minecraft.registry.tag;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ItemTags {
	public static final TagKey<Item> WOOL = of("wool");
	public static final TagKey<Item> PLANKS = of("planks");
	public static final TagKey<Item> STONE_BRICKS = of("stone_bricks");
	public static final TagKey<Item> WOODEN_BUTTONS = of("wooden_buttons");
	public static final TagKey<Item> BUTTONS = of("buttons");
	public static final TagKey<Item> WOOL_CARPETS = of("wool_carpets");
	public static final TagKey<Item> WOODEN_DOORS = of("wooden_doors");
	public static final TagKey<Item> WOODEN_STAIRS = of("wooden_stairs");
	public static final TagKey<Item> WOODEN_SLABS = of("wooden_slabs");
	public static final TagKey<Item> WOODEN_FENCES = of("wooden_fences");
	public static final TagKey<Item> FENCE_GATES = of("fence_gates");
	public static final TagKey<Item> WOODEN_PRESSURE_PLATES = of("wooden_pressure_plates");
	public static final TagKey<Item> WOODEN_TRAPDOORS = of("wooden_trapdoors");
	public static final TagKey<Item> DOORS = of("doors");
	public static final TagKey<Item> SAPLINGS = of("saplings");
	public static final TagKey<Item> LOGS_THAT_BURN = of("logs_that_burn");
	public static final TagKey<Item> LOGS = of("logs");
	public static final TagKey<Item> DARK_OAK_LOGS = of("dark_oak_logs");
	public static final TagKey<Item> OAK_LOGS = of("oak_logs");
	public static final TagKey<Item> BIRCH_LOGS = of("birch_logs");
	public static final TagKey<Item> ACACIA_LOGS = of("acacia_logs");
	public static final TagKey<Item> JUNGLE_LOGS = of("jungle_logs");
	public static final TagKey<Item> SPRUCE_LOGS = of("spruce_logs");
	public static final TagKey<Item> MANGROVE_LOGS = of("mangrove_logs");
	public static final TagKey<Item> CRIMSON_STEMS = of("crimson_stems");
	public static final TagKey<Item> WARPED_STEMS = of("warped_stems");
	public static final TagKey<Item> BAMBOO_BLOCKS = of("bamboo_blocks");
	public static final TagKey<Item> WART_BLOCKS = of("wart_blocks");
	public static final TagKey<Item> BANNERS = of("banners");
	public static final TagKey<Item> SAND = of("sand");
	public static final TagKey<Item> STAIRS = of("stairs");
	public static final TagKey<Item> SLABS = of("slabs");
	public static final TagKey<Item> WALLS = of("walls");
	public static final TagKey<Item> ANVIL = of("anvil");
	public static final TagKey<Item> RAILS = of("rails");
	public static final TagKey<Item> LEAVES = of("leaves");
	public static final TagKey<Item> TRAPDOORS = of("trapdoors");
	public static final TagKey<Item> SMALL_FLOWERS = of("small_flowers");
	public static final TagKey<Item> BEDS = of("beds");
	public static final TagKey<Item> FENCES = of("fences");
	public static final TagKey<Item> TALL_FLOWERS = of("tall_flowers");
	public static final TagKey<Item> FLOWERS = of("flowers");
	public static final TagKey<Item> PIGLIN_REPELLENTS = of("piglin_repellents");
	public static final TagKey<Item> PIGLIN_LOVED = of("piglin_loved");
	public static final TagKey<Item> IGNORED_BY_PIGLIN_BABIES = of("ignored_by_piglin_babies");
	public static final TagKey<Item> PIGLIN_FOOD = of("piglin_food");
	public static final TagKey<Item> FOX_FOOD = of("fox_food");
	public static final TagKey<Item> GOLD_ORES = of("gold_ores");
	public static final TagKey<Item> IRON_ORES = of("iron_ores");
	public static final TagKey<Item> DIAMOND_ORES = of("diamond_ores");
	public static final TagKey<Item> REDSTONE_ORES = of("redstone_ores");
	public static final TagKey<Item> LAPIS_ORES = of("lapis_ores");
	public static final TagKey<Item> COAL_ORES = of("coal_ores");
	public static final TagKey<Item> EMERALD_ORES = of("emerald_ores");
	public static final TagKey<Item> COPPER_ORES = of("copper_ores");
	public static final TagKey<Item> NON_FLAMMABLE_WOOD = of("non_flammable_wood");
	public static final TagKey<Item> SOUL_FIRE_BASE_BLOCKS = of("soul_fire_base_blocks");
	public static final TagKey<Item> CANDLES = of("candles");
	public static final TagKey<Item> DIRT = of("dirt");
	public static final TagKey<Item> TERRACOTTA = of("terracotta");
	public static final TagKey<Item> COMPLETES_FIND_TREE_TUTORIAL = of("completes_find_tree_tutorial");
	public static final TagKey<Item> BOATS = of("boats");
	public static final TagKey<Item> CHEST_BOATS = of("chest_boats");
	public static final TagKey<Item> FISHES = of("fishes");
	public static final TagKey<Item> SIGNS = of("signs");
	public static final TagKey<Item> MUSIC_DISCS = of("music_discs");
	public static final TagKey<Item> CREEPER_DROP_MUSIC_DISCS = of("creeper_drop_music_discs");
	public static final TagKey<Item> COALS = of("coals");
	public static final TagKey<Item> ARROWS = of("arrows");
	public static final TagKey<Item> LECTERN_BOOKS = of("lectern_books");
	public static final TagKey<Item> BOOKSHELF_BOOKS = of("bookshelf_books");
	public static final TagKey<Item> BEACON_PAYMENT_ITEMS = of("beacon_payment_items");
	public static final TagKey<Item> STONE_TOOL_MATERIALS = of("stone_tool_materials");
	public static final TagKey<Item> STONE_CRAFTING_MATERIALS = of("stone_crafting_materials");
	public static final TagKey<Item> FREEZE_IMMUNE_WEARABLES = of("freeze_immune_wearables");
	public static final TagKey<Item> AXOLOTL_TEMPT_ITEMS = of("axolotl_tempt_items");
	public static final TagKey<Item> DAMPENS_VIBRATIONS = of("dampens_vibrations");
	public static final TagKey<Item> CLUSTER_MAX_HARVESTABLES = of("cluster_max_harvestables");
	public static final TagKey<Item> COMPASSES = of("compasses");
	public static final TagKey<Item> HANGING_SIGNS = of("hanging_signs");

	private ItemTags() {
	}

	private static TagKey<Item> of(String id) {
		return TagKey.of(RegistryKeys.ITEM, new Identifier(id));
	}
}
