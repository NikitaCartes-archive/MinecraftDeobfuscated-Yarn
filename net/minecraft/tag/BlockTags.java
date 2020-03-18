/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class BlockTags {
    private static TagContainer<Block> container = new TagContainer(identifier -> Optional.empty(), "", false, "");
    private static int latestVersion;
    public static final Tag<Block> WOOL;
    public static final Tag<Block> PLANKS;
    public static final Tag<Block> STONE_BRICKS;
    public static final Tag<Block> WOODEN_BUTTONS;
    public static final Tag<Block> BUTTONS;
    public static final Tag<Block> CARPETS;
    public static final Tag<Block> WOODEN_DOORS;
    public static final Tag<Block> WOODEN_STAIRS;
    public static final Tag<Block> WOODEN_SLABS;
    public static final Tag<Block> WOODEN_FENCES;
    public static final Tag<Block> WOODEN_PRESSURE_PLATES;
    public static final Tag<Block> WOODEN_TRAPDOORS;
    public static final Tag<Block> DOORS;
    public static final Tag<Block> SAPLINGS;
    public static final Tag<Block> LOGS;
    public static final Tag<Block> DARK_OAK_LOGS;
    public static final Tag<Block> OAK_LOGS;
    public static final Tag<Block> BIRCH_LOGS;
    public static final Tag<Block> ACACIA_LOGS;
    public static final Tag<Block> JUNGLE_LOGS;
    public static final Tag<Block> SPRUCE_LOGS;
    public static final Tag<Block> CRIMSON_STEMS;
    public static final Tag<Block> WARPED_STEMS;
    public static final Tag<Block> BANNERS;
    public static final Tag<Block> SAND;
    public static final Tag<Block> STAIRS;
    public static final Tag<Block> SLABS;
    public static final Tag<Block> WALLS;
    public static final Tag<Block> ANVIL;
    public static final Tag<Block> RAILS;
    public static final Tag<Block> LEAVES;
    public static final Tag<Block> TRAPDOORS;
    public static final Tag<Block> SMALL_FLOWERS;
    public static final Tag<Block> BEDS;
    public static final Tag<Block> FENCES;
    public static final Tag<Block> TALL_FLOWERS;
    public static final Tag<Block> FLOWERS;
    public static final Tag<Block> PIGLIN_REPELLENTS;
    public static final Tag<Block> GOLD_ORES;
    public static final Tag<Block> FLOWER_POTS;
    public static final Tag<Block> ENDERMAN_HOLDABLE;
    public static final Tag<Block> ICE;
    public static final Tag<Block> VALID_SPAWN;
    public static final Tag<Block> IMPERMEABLE;
    public static final Tag<Block> UNDERWATER_BONEMEALS;
    public static final Tag<Block> CORAL_BLOCKS;
    public static final Tag<Block> WALL_CORALS;
    public static final Tag<Block> CORAL_PLANTS;
    public static final Tag<Block> CORALS;
    public static final Tag<Block> BAMBOO_PLANTABLE_ON;
    public static final Tag<Block> STANDING_SIGNS;
    public static final Tag<Block> WALL_SIGNS;
    public static final Tag<Block> SIGNS;
    public static final Tag<Block> DRAGON_IMMUNE;
    public static final Tag<Block> WITHER_IMMUNE;
    public static final Tag<Block> WITHER_SUMMON_BASE_BLOCKS;
    public static final Tag<Block> BEEHIVES;
    public static final Tag<Block> CROPS;
    public static final Tag<Block> BEE_GROWABLES;
    public static final Tag<Block> PORTALS;
    public static final Tag<Block> FIRE;
    public static final Tag<Block> NYLIUM;
    public static final Tag<Block> WART_BLOCKS;
    public static final Tag<Block> BEACON_BASE_BLOCKS;
    public static final Tag<Block> SOUL_SPEED_BLOCKS;
    public static final Tag<Block> WALL_POST_OVERRIDE;
    public static final Tag<Block> CLIMBABLE;
    public static final Tag<Block> SHULKER_BOXES;
    public static final Tag<Block> HOGLIN_REPELLENTS;
    public static final Tag<Block> SOUL_FIRE_BASE_BLOCKS;

    public static void setContainer(TagContainer<Block> container) {
        BlockTags.container = container;
        ++latestVersion;
    }

    public static TagContainer<Block> getContainer() {
        return container;
    }

    private static Tag<Block> register(String id) {
        return new CachingTag(new Identifier(id));
    }

    static {
        WOOL = BlockTags.register("wool");
        PLANKS = BlockTags.register("planks");
        STONE_BRICKS = BlockTags.register("stone_bricks");
        WOODEN_BUTTONS = BlockTags.register("wooden_buttons");
        BUTTONS = BlockTags.register("buttons");
        CARPETS = BlockTags.register("carpets");
        WOODEN_DOORS = BlockTags.register("wooden_doors");
        WOODEN_STAIRS = BlockTags.register("wooden_stairs");
        WOODEN_SLABS = BlockTags.register("wooden_slabs");
        WOODEN_FENCES = BlockTags.register("wooden_fences");
        WOODEN_PRESSURE_PLATES = BlockTags.register("wooden_pressure_plates");
        WOODEN_TRAPDOORS = BlockTags.register("wooden_trapdoors");
        DOORS = BlockTags.register("doors");
        SAPLINGS = BlockTags.register("saplings");
        LOGS = BlockTags.register("logs");
        DARK_OAK_LOGS = BlockTags.register("dark_oak_logs");
        OAK_LOGS = BlockTags.register("oak_logs");
        BIRCH_LOGS = BlockTags.register("birch_logs");
        ACACIA_LOGS = BlockTags.register("acacia_logs");
        JUNGLE_LOGS = BlockTags.register("jungle_logs");
        SPRUCE_LOGS = BlockTags.register("spruce_logs");
        CRIMSON_STEMS = BlockTags.register("crimson_stems");
        WARPED_STEMS = BlockTags.register("warped_stems");
        BANNERS = BlockTags.register("banners");
        SAND = BlockTags.register("sand");
        STAIRS = BlockTags.register("stairs");
        SLABS = BlockTags.register("slabs");
        WALLS = BlockTags.register("walls");
        ANVIL = BlockTags.register("anvil");
        RAILS = BlockTags.register("rails");
        LEAVES = BlockTags.register("leaves");
        TRAPDOORS = BlockTags.register("trapdoors");
        SMALL_FLOWERS = BlockTags.register("small_flowers");
        BEDS = BlockTags.register("beds");
        FENCES = BlockTags.register("fences");
        TALL_FLOWERS = BlockTags.register("tall_flowers");
        FLOWERS = BlockTags.register("flowers");
        PIGLIN_REPELLENTS = BlockTags.register("piglin_repellents");
        GOLD_ORES = BlockTags.register("gold_ores");
        FLOWER_POTS = BlockTags.register("flower_pots");
        ENDERMAN_HOLDABLE = BlockTags.register("enderman_holdable");
        ICE = BlockTags.register("ice");
        VALID_SPAWN = BlockTags.register("valid_spawn");
        IMPERMEABLE = BlockTags.register("impermeable");
        UNDERWATER_BONEMEALS = BlockTags.register("underwater_bonemeals");
        CORAL_BLOCKS = BlockTags.register("coral_blocks");
        WALL_CORALS = BlockTags.register("wall_corals");
        CORAL_PLANTS = BlockTags.register("coral_plants");
        CORALS = BlockTags.register("corals");
        BAMBOO_PLANTABLE_ON = BlockTags.register("bamboo_plantable_on");
        STANDING_SIGNS = BlockTags.register("standing_signs");
        WALL_SIGNS = BlockTags.register("wall_signs");
        SIGNS = BlockTags.register("signs");
        DRAGON_IMMUNE = BlockTags.register("dragon_immune");
        WITHER_IMMUNE = BlockTags.register("wither_immune");
        WITHER_SUMMON_BASE_BLOCKS = BlockTags.register("wither_summon_base_blocks");
        BEEHIVES = BlockTags.register("beehives");
        CROPS = BlockTags.register("crops");
        BEE_GROWABLES = BlockTags.register("bee_growables");
        PORTALS = BlockTags.register("portals");
        FIRE = BlockTags.register("fire");
        NYLIUM = BlockTags.register("nylium");
        WART_BLOCKS = BlockTags.register("wart_blocks");
        BEACON_BASE_BLOCKS = BlockTags.register("beacon_base_blocks");
        SOUL_SPEED_BLOCKS = BlockTags.register("soul_speed_blocks");
        WALL_POST_OVERRIDE = BlockTags.register("wall_post_override");
        CLIMBABLE = BlockTags.register("climbable");
        SHULKER_BOXES = BlockTags.register("shulker_boxes");
        HOGLIN_REPELLENTS = BlockTags.register("hoglin_repellents");
        SOUL_FIRE_BASE_BLOCKS = BlockTags.register("soul_fire_base_blocks");
    }

    static class CachingTag
    extends Tag<Block> {
        private int version = -1;
        private Tag<Block> delegate;

        public CachingTag(Identifier identifier) {
            super(identifier);
        }

        @Override
        public boolean contains(Block block) {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.contains(block);
        }

        @Override
        public Collection<Block> values() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.values();
        }

        @Override
        public Collection<Tag.Entry<Block>> entries() {
            if (this.version != latestVersion) {
                this.delegate = container.getOrCreate(this.getId());
                this.version = latestVersion;
            }
            return this.delegate.entries();
        }
    }
}

