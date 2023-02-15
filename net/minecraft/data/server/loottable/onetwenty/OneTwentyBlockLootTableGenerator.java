/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.loottable.onetwenty;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchflowerBlock;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyBlockLootTableGenerator
extends BlockLootTableGenerator {
    protected OneTwentyBlockLootTableGenerator() {
        super(Set.of(), FeatureSet.of(FeatureFlags.UPDATE_1_20));
    }

    @Override
    protected void generate() {
        this.addDrop(Blocks.BAMBOO_BLOCK);
        this.addDrop(Blocks.STRIPPED_BAMBOO_BLOCK);
        this.addDrop(Blocks.BAMBOO_PLANKS);
        this.addDrop(Blocks.BAMBOO_MOSAIC);
        this.addDrop(Blocks.BAMBOO_STAIRS);
        this.addDrop(Blocks.BAMBOO_MOSAIC_STAIRS);
        this.addDrop(Blocks.BAMBOO_SIGN);
        this.addDrop(Blocks.OAK_HANGING_SIGN);
        this.addDrop(Blocks.SPRUCE_HANGING_SIGN);
        this.addDrop(Blocks.BIRCH_HANGING_SIGN);
        this.addDrop(Blocks.ACACIA_HANGING_SIGN);
        this.addDrop(Blocks.CHERRY_HANGING_SIGN);
        this.addDrop(Blocks.JUNGLE_HANGING_SIGN);
        this.addDrop(Blocks.DARK_OAK_HANGING_SIGN);
        this.addDrop(Blocks.MANGROVE_HANGING_SIGN);
        this.addDrop(Blocks.CRIMSON_HANGING_SIGN);
        this.addDrop(Blocks.WARPED_HANGING_SIGN);
        this.addDrop(Blocks.BAMBOO_HANGING_SIGN);
        this.addDrop(Blocks.BAMBOO_PRESSURE_PLATE);
        this.addDrop(Blocks.BAMBOO_FENCE);
        this.addDrop(Blocks.BAMBOO_TRAPDOOR);
        this.addDrop(Blocks.BAMBOO_FENCE_GATE);
        this.addDrop(Blocks.BAMBOO_BUTTON);
        this.addDrop(Blocks.BAMBOO_SLAB, (Block block) -> this.slabDrops((Block)block));
        this.addDrop(Blocks.BAMBOO_MOSAIC_SLAB, (Block block) -> this.slabDrops((Block)block));
        this.addDrop(Blocks.BAMBOO_DOOR, (Block block) -> this.doorDrops((Block)block));
        this.addDropWithSilkTouch(Blocks.CHISELED_BOOKSHELF);
        this.addDrop(Blocks.DECORATED_POT, OneTwentyBlockLootTableGenerator.dropsNothing());
        this.addDrop(Blocks.PIGLIN_HEAD);
        this.addDrop(Blocks.TORCHFLOWER);
        this.addPottedPlantDrops(Blocks.POTTED_TORCHFLOWER);
        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.TORCHFLOWER_CROP).properties(StatePredicate.Builder.create().exactMatch(TorchflowerBlock.field_42776, 2));
        this.addDrop(Blocks.TORCHFLOWER_CROP, this.applyExplosionDecay(Blocks.TORCHFLOWER_CROP, LootTable.builder().pool(LootPool.builder().with(((LeafEntry.Builder)ItemEntry.builder(Items.TORCHFLOWER).conditionally(builder)).alternatively(ItemEntry.builder(Items.TORCHFLOWER_SEEDS))))));
        this.addDrop(Blocks.CHERRY_PLANKS);
        this.addDrop(Blocks.CHERRY_SAPLING);
        this.addDrop(Blocks.CHERRY_LOG);
        this.addDrop(Blocks.STRIPPED_CHERRY_LOG);
        this.addDrop(Blocks.CHERRY_WOOD);
        this.addDrop(Blocks.STRIPPED_CHERRY_WOOD);
        this.addDrop(Blocks.CHERRY_SIGN);
        this.addDrop(Blocks.CHERRY_PRESSURE_PLATE);
        this.addDrop(Blocks.CHERRY_TRAPDOOR);
        this.addDrop(Blocks.CHERRY_BUTTON);
        this.addDrop(Blocks.CHERRY_STAIRS);
        this.addDrop(Blocks.CHERRY_FENCE_GATE);
        this.addDrop(Blocks.CHERRY_FENCE);
        this.addPottedPlantDrops(Blocks.POTTED_CHERRY_SAPLING);
        this.addDrop(Blocks.CHERRY_SLAB, (Block block) -> this.slabDrops((Block)block));
        this.addDrop(Blocks.CHERRY_DOOR, (Block block) -> this.doorDrops((Block)block));
        this.addDrop(Blocks.CHERRY_LEAVES, (Block block) -> this.leavesDrops((Block)block, Blocks.CHERRY_SAPLING, SAPLING_DROP_CHANCE));
        this.addDrop(Blocks.PINK_PETALS, this.flowerbedDrops(Blocks.PINK_PETALS));
        this.addDrop(Blocks.SUSPICIOUS_SAND, OneTwentyBlockLootTableGenerator.dropsNothing());
    }
}

