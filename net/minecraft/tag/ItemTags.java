/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class ItemTags {
    private static TagContainer<Item> container = new TagContainer(identifier -> Optional.empty(), "", false, "");
    private static int latestVersion;
    public static final Tag<Item> WOOL;
    public static final Tag<Item> PLANKS;
    public static final Tag<Item> STONE_BRICKS;
    public static final Tag<Item> WOODEN_BUTTONS;
    public static final Tag<Item> BUTTONS;
    public static final Tag<Item> CARPETS;
    public static final Tag<Item> WOODEN_DOORS;
    public static final Tag<Item> WOODEN_STAIRS;
    public static final Tag<Item> WOODEN_SLABS;
    public static final Tag<Item> WOODEN_FENCES;
    public static final Tag<Item> WOODEN_PRESSURE_PLATES;
    public static final Tag<Item> WOODEN_TRAPDOORS;
    public static final Tag<Item> DOORS;
    public static final Tag<Item> SAPLINGS;
    public static final Tag<Item> LOGS_THAT_BURN;
    public static final Tag<Item> LOGS;
    public static final Tag<Item> DARK_OAK_LOGS;
    public static final Tag<Item> OAK_LOGS;
    public static final Tag<Item> BIRCH_LOGS;
    public static final Tag<Item> ACACIA_LOGS;
    public static final Tag<Item> JUNGLE_LOGS;
    public static final Tag<Item> SPRUCE_LOGS;
    public static final Tag<Item> CRIMSON_STEMS;
    public static final Tag<Item> WARPED_STEMS;
    public static final Tag<Item> BANNERS;
    public static final Tag<Item> SAND;
    public static final Tag<Item> STAIRS;
    public static final Tag<Item> SLABS;
    public static final Tag<Item> WALLS;
    public static final Tag<Item> ANVIL;
    public static final Tag<Item> RAILS;
    public static final Tag<Item> LEAVES;
    public static final Tag<Item> TRAPDOORS;
    public static final Tag<Item> SMALL_FLOWERS;
    public static final Tag<Item> BEDS;
    public static final Tag<Item> FENCES;
    public static final Tag<Item> TALL_FLOWERS;
    public static final Tag<Item> FLOWERS;
    public static final Tag<Item> PIGLIN_REPELLENTS;
    public static final Tag<Item> GOLD_ORES;
    public static final Tag<Item> NON_FLAMMABLE_WOOD;
    public static final Tag<Item> BOATS;
    public static final Tag<Item> FISHES;
    public static final Tag<Item> SIGNS;
    public static final Tag<Item> MUSIC_DISCS;
    public static final Tag<Item> COALS;
    public static final Tag<Item> ARROWS;
    public static final Tag<Item> LECTERN_BOOKS;
    public static final Tag<Item> BEACON_PAYMENT_ITEMS;

    public static void setContainer(TagContainer<Item> container) {
        ItemTags.container = container;
        ++latestVersion;
    }

    public static TagContainer<Item> getContainer() {
        return container;
    }

    private static Tag<Item> register(String id) {
        return new CachingTag(new Identifier(id));
    }

    static {
        WOOL = ItemTags.register("wool");
        PLANKS = ItemTags.register("planks");
        STONE_BRICKS = ItemTags.register("stone_bricks");
        WOODEN_BUTTONS = ItemTags.register("wooden_buttons");
        BUTTONS = ItemTags.register("buttons");
        CARPETS = ItemTags.register("carpets");
        WOODEN_DOORS = ItemTags.register("wooden_doors");
        WOODEN_STAIRS = ItemTags.register("wooden_stairs");
        WOODEN_SLABS = ItemTags.register("wooden_slabs");
        WOODEN_FENCES = ItemTags.register("wooden_fences");
        WOODEN_PRESSURE_PLATES = ItemTags.register("wooden_pressure_plates");
        WOODEN_TRAPDOORS = ItemTags.register("wooden_trapdoors");
        DOORS = ItemTags.register("doors");
        SAPLINGS = ItemTags.register("saplings");
        LOGS_THAT_BURN = ItemTags.register("logs_that_burn");
        LOGS = ItemTags.register("logs");
        DARK_OAK_LOGS = ItemTags.register("dark_oak_logs");
        OAK_LOGS = ItemTags.register("oak_logs");
        BIRCH_LOGS = ItemTags.register("birch_logs");
        ACACIA_LOGS = ItemTags.register("acacia_logs");
        JUNGLE_LOGS = ItemTags.register("jungle_logs");
        SPRUCE_LOGS = ItemTags.register("spruce_logs");
        CRIMSON_STEMS = ItemTags.register("crimson_stems");
        WARPED_STEMS = ItemTags.register("warped_stems");
        BANNERS = ItemTags.register("banners");
        SAND = ItemTags.register("sand");
        STAIRS = ItemTags.register("stairs");
        SLABS = ItemTags.register("slabs");
        WALLS = ItemTags.register("walls");
        ANVIL = ItemTags.register("anvil");
        RAILS = ItemTags.register("rails");
        LEAVES = ItemTags.register("leaves");
        TRAPDOORS = ItemTags.register("trapdoors");
        SMALL_FLOWERS = ItemTags.register("small_flowers");
        BEDS = ItemTags.register("beds");
        FENCES = ItemTags.register("fences");
        TALL_FLOWERS = ItemTags.register("tall_flowers");
        FLOWERS = ItemTags.register("flowers");
        PIGLIN_REPELLENTS = ItemTags.register("piglin_repellents");
        GOLD_ORES = ItemTags.register("gold_ores");
        NON_FLAMMABLE_WOOD = ItemTags.register("non_flammable_wood");
        BOATS = ItemTags.register("boats");
        FISHES = ItemTags.register("fishes");
        SIGNS = ItemTags.register("signs");
        MUSIC_DISCS = ItemTags.register("music_discs");
        COALS = ItemTags.register("coals");
        ARROWS = ItemTags.register("arrows");
        LECTERN_BOOKS = ItemTags.register("lectern_books");
        BEACON_PAYMENT_ITEMS = ItemTags.register("beacon_payment_items");
    }

    public static class CachingTag
    extends Tag<Item> {
        private int version = -1;
        private Tag<Item> delegate;

        public CachingTag(Identifier identifier) {
            super(identifier);
        }

        @Override
        public boolean contains(Item item) {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.contains(item);
        }

        @Override
        public Collection<Item> values() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.values();
        }

        @Override
        public Collection<Tag.Entry<Item>> entries() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.entries();
        }
    }
}

