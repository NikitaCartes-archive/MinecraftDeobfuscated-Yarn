package net.minecraft.tag;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class ItemTags {
	protected static final RequiredTagList<Item> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("item"), TagManager::getItems);
	public static final Tag.Identified<Item> field_15544 = register("wool");
	public static final Tag.Identified<Item> field_15537 = register("planks");
	public static final Tag.Identified<Item> field_15531 = register("stone_bricks");
	public static final Tag.Identified<Item> field_15555 = register("wooden_buttons");
	public static final Tag.Identified<Item> field_15551 = register("buttons");
	public static final Tag.Identified<Item> field_15542 = register("carpets");
	public static final Tag.Identified<Item> field_15552 = register("wooden_doors");
	public static final Tag.Identified<Item> field_15557 = register("wooden_stairs");
	public static final Tag.Identified<Item> field_15534 = register("wooden_slabs");
	public static final Tag.Identified<Item> field_17620 = register("wooden_fences");
	public static final Tag.Identified<Item> field_15540 = register("wooden_pressure_plates");
	public static final Tag.Identified<Item> field_15550 = register("wooden_trapdoors");
	public static final Tag.Identified<Item> field_15553 = register("doors");
	public static final Tag.Identified<Item> field_15528 = register("saplings");
	public static final Tag.Identified<Item> field_23212 = register("logs_that_burn");
	public static final Tag.Identified<Item> field_15539 = register("logs");
	public static final Tag.Identified<Item> field_15546 = register("dark_oak_logs");
	public static final Tag.Identified<Item> field_15545 = register("oak_logs");
	public static final Tag.Identified<Item> field_15554 = register("birch_logs");
	public static final Tag.Identified<Item> field_15525 = register("acacia_logs");
	public static final Tag.Identified<Item> field_15538 = register("jungle_logs");
	public static final Tag.Identified<Item> field_15549 = register("spruce_logs");
	public static final Tag.Identified<Item> field_21957 = register("crimson_stems");
	public static final Tag.Identified<Item> field_21958 = register("warped_stems");
	public static final Tag.Identified<Item> field_15556 = register("banners");
	public static final Tag.Identified<Item> field_15532 = register("sand");
	public static final Tag.Identified<Item> field_15526 = register("stairs");
	public static final Tag.Identified<Item> field_15535 = register("slabs");
	public static final Tag.Identified<Item> field_15560 = register("walls");
	public static final Tag.Identified<Item> field_15547 = register("anvil");
	public static final Tag.Identified<Item> field_15529 = register("rails");
	public static final Tag.Identified<Item> field_15558 = register("leaves");
	public static final Tag.Identified<Item> field_15548 = register("trapdoors");
	public static final Tag.Identified<Item> field_15543 = register("small_flowers");
	public static final Tag.Identified<Item> field_16444 = register("beds");
	public static final Tag.Identified<Item> field_16585 = register("fences");
	public static final Tag.Identified<Item> field_20343 = register("tall_flowers");
	public static final Tag.Identified<Item> field_20344 = register("flowers");
	public static final Tag.Identified<Item> field_23064 = register("piglin_repellents");
	public static final Tag.Identified<Item> field_24481 = register("piglin_loved");
	public static final Tag.Identified<Item> field_23065 = register("gold_ores");
	public static final Tag.Identified<Item> field_23211 = register("non_flammable_wood");
	public static final Tag.Identified<Item> field_23801 = register("soul_fire_base_blocks");
	public static final Tag.Identified<Item> field_15536 = register("boats");
	public static final Tag.Identified<Item> field_15527 = register("fishes");
	public static final Tag.Identified<Item> field_15533 = register("signs");
	public static final Tag.Identified<Item> field_15541 = register("music_discs");
	public static final Tag.Identified<Item> field_23969 = register("creeper_drop_music_discs");
	public static final Tag.Identified<Item> field_17487 = register("coals");
	public static final Tag.Identified<Item> field_18317 = register("arrows");
	public static final Tag.Identified<Item> field_21465 = register("lectern_books");
	public static final Tag.Identified<Item> field_22277 = register("beacon_payment_items");
	public static final Tag.Identified<Item> field_23802 = register("stone_tool_materials");
	public static final Tag.Identified<Item> field_25808 = register("stone_crafting_materials");

	private static Tag.Identified<Item> register(String id) {
		return REQUIRED_TAGS.add(id);
	}

	public static TagGroup<Item> getTagGroup() {
		return REQUIRED_TAGS.getGroup();
	}

	public static List<? extends Tag.Identified<Item>> method_31074() {
		return REQUIRED_TAGS.getTags();
	}
}
