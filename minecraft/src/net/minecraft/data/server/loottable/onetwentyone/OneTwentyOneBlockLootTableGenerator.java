package net.minecraft.data.server.loottable.onetwentyone;

import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneBlockLootTableGenerator extends BlockLootTableGenerator {
	protected OneTwentyOneBlockLootTableGenerator() {
		super(Set.of(), FeatureSet.of(FeatureFlags.UPDATE_1_21));
	}

	@Override
	protected void generate() {
		this.addDrop(Blocks.CRAFTER);
		this.addDrop(Blocks.CHISELED_TUFF);
		this.addDrop(Blocks.TUFF_STAIRS);
		this.addDrop(Blocks.TUFF_WALL);
		this.addDrop(Blocks.POLISHED_TUFF);
		this.addDrop(Blocks.POLISHED_TUFF_STAIRS);
		this.addDrop(Blocks.POLISHED_TUFF_WALL);
		this.addDrop(Blocks.TUFF_BRICKS);
		this.addDrop(Blocks.TUFF_BRICK_STAIRS);
		this.addDrop(Blocks.TUFF_BRICK_WALL);
		this.addDrop(Blocks.CHISELED_TUFF_BRICKS);
		this.addDrop(Blocks.TUFF_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.TUFF_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_TUFF_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CHISELED_COPPER);
		this.addDrop(Blocks.EXPOSED_CHISELED_COPPER);
		this.addDrop(Blocks.WEATHERED_CHISELED_COPPER);
		this.addDrop(Blocks.OXIDIZED_CHISELED_COPPER);
		this.addDrop(Blocks.WAXED_CHISELED_COPPER);
		this.addDrop(Blocks.WAXED_EXPOSED_CHISELED_COPPER);
		this.addDrop(Blocks.WAXED_WEATHERED_CHISELED_COPPER);
		this.addDrop(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
		this.addDrop(Blocks.COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.EXPOSED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WEATHERED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.OXIDIZED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WAXED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WAXED_EXPOSED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WAXED_WEATHERED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WAXED_OXIDIZED_COPPER_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.COPPER_TRAPDOOR);
		this.addDrop(Blocks.EXPOSED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.WEATHERED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.OXIDIZED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.WAXED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
		this.addDrop(Blocks.COPPER_GRATE);
		this.addDrop(Blocks.EXPOSED_COPPER_GRATE);
		this.addDrop(Blocks.WEATHERED_COPPER_GRATE);
		this.addDrop(Blocks.OXIDIZED_COPPER_GRATE);
		this.addDrop(Blocks.WAXED_COPPER_GRATE);
		this.addDrop(Blocks.WAXED_EXPOSED_COPPER_GRATE);
		this.addDrop(Blocks.WAXED_WEATHERED_COPPER_GRATE);
		this.addDrop(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
		this.addDrop(Blocks.COPPER_BULB);
		this.addDrop(Blocks.EXPOSED_COPPER_BULB);
		this.addDrop(Blocks.WEATHERED_COPPER_BULB);
		this.addDrop(Blocks.OXIDIZED_COPPER_BULB);
		this.addDrop(Blocks.WAXED_COPPER_BULB);
		this.addDrop(Blocks.WAXED_EXPOSED_COPPER_BULB);
		this.addDrop(Blocks.WAXED_WEATHERED_COPPER_BULB);
		this.addDrop(Blocks.WAXED_OXIDIZED_COPPER_BULB);
		this.addDrop(Blocks.TRIAL_SPAWNER, dropsNothing());
		this.addDrop(Blocks.VAULT, dropsNothing());
	}
}
