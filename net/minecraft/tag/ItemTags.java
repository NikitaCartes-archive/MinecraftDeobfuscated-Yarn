/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.item.Item;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public final class ItemTags {
    protected static final RequiredTagList<Item> REQUIRED_TAGS = RequiredTagListRegistry.register(new Identifier("item"), TagManager::getItems);
    public static final Tag.Identified<Item> WOOL = ItemTags.register("wool");
    public static final Tag.Identified<Item> PLANKS = ItemTags.register("planks");
    public static final Tag.Identified<Item> STONE_BRICKS = ItemTags.register("stone_bricks");
    public static final Tag.Identified<Item> WOODEN_BUTTONS = ItemTags.register("wooden_buttons");
    public static final Tag.Identified<Item> BUTTONS = ItemTags.register("buttons");
    public static final Tag.Identified<Item> CARPETS = ItemTags.register("carpets");
    public static final Tag.Identified<Item> WOODEN_DOORS = ItemTags.register("wooden_doors");
    public static final Tag.Identified<Item> WOODEN_STAIRS = ItemTags.register("wooden_stairs");
    public static final Tag.Identified<Item> WOODEN_SLABS = ItemTags.register("wooden_slabs");
    public static final Tag.Identified<Item> WOODEN_FENCES = ItemTags.register("wooden_fences");
    public static final Tag.Identified<Item> WOODEN_PRESSURE_PLATES = ItemTags.register("wooden_pressure_plates");
    public static final Tag.Identified<Item> WOODEN_TRAPDOORS = ItemTags.register("wooden_trapdoors");
    public static final Tag.Identified<Item> DOORS = ItemTags.register("doors");
    public static final Tag.Identified<Item> SAPLINGS = ItemTags.register("saplings");
    public static final Tag.Identified<Item> LOGS_THAT_BURN = ItemTags.register("logs_that_burn");
    public static final Tag.Identified<Item> LOGS = ItemTags.register("logs");
    public static final Tag.Identified<Item> DARK_OAK_LOGS = ItemTags.register("dark_oak_logs");
    public static final Tag.Identified<Item> OAK_LOGS = ItemTags.register("oak_logs");
    public static final Tag.Identified<Item> BIRCH_LOGS = ItemTags.register("birch_logs");
    public static final Tag.Identified<Item> ACACIA_LOGS = ItemTags.register("acacia_logs");
    public static final Tag.Identified<Item> JUNGLE_LOGS = ItemTags.register("jungle_logs");
    public static final Tag.Identified<Item> SPRUCE_LOGS = ItemTags.register("spruce_logs");
    public static final Tag.Identified<Item> CRIMSON_STEMS = ItemTags.register("crimson_stems");
    public static final Tag.Identified<Item> WARPED_STEMS = ItemTags.register("warped_stems");
    public static final Tag.Identified<Item> BANNERS = ItemTags.register("banners");
    public static final Tag.Identified<Item> SAND = ItemTags.register("sand");
    public static final Tag.Identified<Item> STAIRS = ItemTags.register("stairs");
    public static final Tag.Identified<Item> SLABS = ItemTags.register("slabs");
    public static final Tag.Identified<Item> WALLS = ItemTags.register("walls");
    public static final Tag.Identified<Item> ANVIL = ItemTags.register("anvil");
    public static final Tag.Identified<Item> RAILS = ItemTags.register("rails");
    public static final Tag.Identified<Item> LEAVES = ItemTags.register("leaves");
    public static final Tag.Identified<Item> TRAPDOORS = ItemTags.register("trapdoors");
    public static final Tag.Identified<Item> SMALL_FLOWERS = ItemTags.register("small_flowers");
    public static final Tag.Identified<Item> BEDS = ItemTags.register("beds");
    public static final Tag.Identified<Item> FENCES = ItemTags.register("fences");
    public static final Tag.Identified<Item> TALL_FLOWERS = ItemTags.register("tall_flowers");
    public static final Tag.Identified<Item> FLOWERS = ItemTags.register("flowers");
    public static final Tag.Identified<Item> PIGLIN_REPELLENTS = ItemTags.register("piglin_repellents");
    public static final Tag.Identified<Item> PIGLIN_LOVED = ItemTags.register("piglin_loved");
    public static final Tag.Identified<Item> GOLD_ORES = ItemTags.register("gold_ores");
    public static final Tag.Identified<Item> NON_FLAMMABLE_WOOD = ItemTags.register("non_flammable_wood");
    public static final Tag.Identified<Item> SOUL_FIRE_BASE_BLOCKS = ItemTags.register("soul_fire_base_blocks");
    public static final Tag.Identified<Item> BOATS = ItemTags.register("boats");
    public static final Tag.Identified<Item> FISHES = ItemTags.register("fishes");
    public static final Tag.Identified<Item> SIGNS = ItemTags.register("signs");
    public static final Tag.Identified<Item> MUSIC_DISCS = ItemTags.register("music_discs");
    public static final Tag.Identified<Item> CREEPER_DROP_MUSIC_DISCS = ItemTags.register("creeper_drop_music_discs");
    public static final Tag.Identified<Item> COALS = ItemTags.register("coals");
    public static final Tag.Identified<Item> ARROWS = ItemTags.register("arrows");
    public static final Tag.Identified<Item> LECTERN_BOOKS = ItemTags.register("lectern_books");
    public static final Tag.Identified<Item> BEACON_PAYMENT_ITEMS = ItemTags.register("beacon_payment_items");
    public static final Tag.Identified<Item> STONE_TOOL_MATERIALS = ItemTags.register("stone_tool_materials");
    public static final Tag.Identified<Item> STONE_CRAFTING_MATERIALS = ItemTags.register("stone_crafting_materials");

    private static Tag.Identified<Item> register(String id) {
        return REQUIRED_TAGS.add(id);
    }

    public static TagGroup<Item> getTagGroup() {
        return REQUIRED_TAGS.getGroup();
    }
}

