package net.minecraft.data.server.loottable.onetwenty;

import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyBlockLootTableGenerator extends BlockLootTableGenerator {
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
		this.addDrop(Blocks.BAMBOO_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_MOSAIC_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_DOOR, block -> this.doorDrops(block));
		this.addDropWithSilkTouch(Blocks.CHISELED_BOOKSHELF);
		this.addDrop(Blocks.PIGLIN_HEAD);
	}
}
