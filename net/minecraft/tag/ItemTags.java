/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ItemTags {
    public static final TagKey<Item> WOOL = ItemTags.register("wool");
    public static final TagKey<Item> PLANKS = ItemTags.register("planks");
    public static final TagKey<Item> STONE_BRICKS = ItemTags.register("stone_bricks");
    public static final TagKey<Item> WOODEN_BUTTONS = ItemTags.register("wooden_buttons");
    public static final TagKey<Item> BUTTONS = ItemTags.register("buttons");
    public static final TagKey<Item> CARPETS = ItemTags.register("carpets");
    public static final TagKey<Item> WOODEN_DOORS = ItemTags.register("wooden_doors");
    public static final TagKey<Item> WOODEN_STAIRS = ItemTags.register("wooden_stairs");
    public static final TagKey<Item> WOODEN_SLABS = ItemTags.register("wooden_slabs");
    public static final TagKey<Item> WOODEN_FENCES = ItemTags.register("wooden_fences");
    public static final TagKey<Item> WOODEN_PRESSURE_PLATES = ItemTags.register("wooden_pressure_plates");
    public static final TagKey<Item> WOODEN_TRAPDOORS = ItemTags.register("wooden_trapdoors");
    public static final TagKey<Item> DOORS = ItemTags.register("doors");
    public static final TagKey<Item> SAPLINGS = ItemTags.register("saplings");
    public static final TagKey<Item> LOGS_THAT_BURN = ItemTags.register("logs_that_burn");
    public static final TagKey<Item> LOGS = ItemTags.register("logs");
    public static final TagKey<Item> DARK_OAK_LOGS = ItemTags.register("dark_oak_logs");
    public static final TagKey<Item> OAK_LOGS = ItemTags.register("oak_logs");
    public static final TagKey<Item> BIRCH_LOGS = ItemTags.register("birch_logs");
    public static final TagKey<Item> ACACIA_LOGS = ItemTags.register("acacia_logs");
    public static final TagKey<Item> JUNGLE_LOGS = ItemTags.register("jungle_logs");
    public static final TagKey<Item> SPRUCE_LOGS = ItemTags.register("spruce_logs");
    public static final TagKey<Item> CRIMSON_STEMS = ItemTags.register("crimson_stems");
    public static final TagKey<Item> WARPED_STEMS = ItemTags.register("warped_stems");
    public static final TagKey<Item> BANNERS = ItemTags.register("banners");
    public static final TagKey<Item> SAND = ItemTags.register("sand");
    public static final TagKey<Item> STAIRS = ItemTags.register("stairs");
    public static final TagKey<Item> SLABS = ItemTags.register("slabs");
    public static final TagKey<Item> WALLS = ItemTags.register("walls");
    public static final TagKey<Item> ANVIL = ItemTags.register("anvil");
    public static final TagKey<Item> RAILS = ItemTags.register("rails");
    public static final TagKey<Item> LEAVES = ItemTags.register("leaves");
    public static final TagKey<Item> TRAPDOORS = ItemTags.register("trapdoors");
    public static final TagKey<Item> SMALL_FLOWERS = ItemTags.register("small_flowers");
    public static final TagKey<Item> BEDS = ItemTags.register("beds");
    public static final TagKey<Item> FENCES = ItemTags.register("fences");
    public static final TagKey<Item> TALL_FLOWERS = ItemTags.register("tall_flowers");
    public static final TagKey<Item> FLOWERS = ItemTags.register("flowers");
    public static final TagKey<Item> PIGLIN_REPELLENTS = ItemTags.register("piglin_repellents");
    public static final TagKey<Item> PIGLIN_LOVED = ItemTags.register("piglin_loved");
    public static final TagKey<Item> IGNORED_BY_PIGLIN_BABIES = ItemTags.register("ignored_by_piglin_babies");
    public static final TagKey<Item> PIGLIN_FOOD = ItemTags.register("piglin_food");
    public static final TagKey<Item> FOX_FOOD = ItemTags.register("fox_food");
    public static final TagKey<Item> GOLD_ORES = ItemTags.register("gold_ores");
    public static final TagKey<Item> IRON_ORES = ItemTags.register("iron_ores");
    public static final TagKey<Item> DIAMOND_ORES = ItemTags.register("diamond_ores");
    public static final TagKey<Item> REDSTONE_ORES = ItemTags.register("redstone_ores");
    public static final TagKey<Item> LAPIS_ORES = ItemTags.register("lapis_ores");
    public static final TagKey<Item> COAL_ORES = ItemTags.register("coal_ores");
    public static final TagKey<Item> EMERALD_ORES = ItemTags.register("emerald_ores");
    public static final TagKey<Item> COPPER_ORES = ItemTags.register("copper_ores");
    public static final TagKey<Item> NON_FLAMMABLE_WOOD = ItemTags.register("non_flammable_wood");
    public static final TagKey<Item> SOUL_FIRE_BASE_BLOCKS = ItemTags.register("soul_fire_base_blocks");
    public static final TagKey<Item> CANDLES = ItemTags.register("candles");
    public static final TagKey<Item> DIRT = ItemTags.register("dirt");
    public static final TagKey<Item> TERRACOTTA = ItemTags.register("terracotta");
    public static final TagKey<Item> BOATS = ItemTags.register("boats");
    public static final TagKey<Item> FISHES = ItemTags.register("fishes");
    public static final TagKey<Item> SIGNS = ItemTags.register("signs");
    public static final TagKey<Item> MUSIC_DISCS = ItemTags.register("music_discs");
    public static final TagKey<Item> CREEPER_DROP_MUSIC_DISCS = ItemTags.register("creeper_drop_music_discs");
    public static final TagKey<Item> COALS = ItemTags.register("coals");
    public static final TagKey<Item> ARROWS = ItemTags.register("arrows");
    public static final TagKey<Item> LECTERN_BOOKS = ItemTags.register("lectern_books");
    public static final TagKey<Item> BEACON_PAYMENT_ITEMS = ItemTags.register("beacon_payment_items");
    public static final TagKey<Item> STONE_TOOL_MATERIALS = ItemTags.register("stone_tool_materials");
    public static final TagKey<Item> STONE_CRAFTING_MATERIALS = ItemTags.register("stone_crafting_materials");
    public static final TagKey<Item> FREEZE_IMMUNE_WEARABLES = ItemTags.register("freeze_immune_wearables");
    public static final TagKey<Item> AXOLOTL_TEMPT_ITEMS = ItemTags.register("axolotl_tempt_items");
    public static final TagKey<Item> OCCLUDES_VIBRATION_SIGNALS = ItemTags.register("occludes_vibration_signals");
    public static final TagKey<Item> CLUSTER_MAX_HARVESTABLES = ItemTags.register("cluster_max_harvestables");

    private ItemTags() {
    }

    private static TagKey<Item> register(String id) {
        return TagKey.intern(Registry.ITEM_KEY, new Identifier(id));
    }
}

