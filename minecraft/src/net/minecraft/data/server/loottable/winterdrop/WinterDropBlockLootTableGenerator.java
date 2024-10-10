package net.minecraft.data.server.loottable.winterdrop;

import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class WinterDropBlockLootTableGenerator extends BlockLootTableGenerator {
	public WinterDropBlockLootTableGenerator(RegistryWrapper.WrapperLookup registries) {
		super(Set.of(), FeatureSet.of(FeatureFlags.WINTER_DROP), registries);
	}

	@Override
	protected void generate() {
		this.addDrop(Blocks.PALE_OAK_PLANKS);
		this.addDrop(Blocks.PALE_OAK_SAPLING);
		this.addDrop(Blocks.PALE_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_PALE_OAK_LOG);
		this.addDrop(Blocks.PALE_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_PALE_OAK_WOOD);
		this.addDrop(Blocks.PALE_OAK_SIGN);
		this.addDrop(Blocks.PALE_OAK_HANGING_SIGN);
		this.addDrop(Blocks.PALE_OAK_PRESSURE_PLATE);
		this.addDrop(Blocks.PALE_OAK_TRAPDOOR);
		this.addDrop(Blocks.PALE_OAK_BUTTON);
		this.addDrop(Blocks.PALE_OAK_STAIRS);
		this.addDrop(Blocks.PALE_OAK_FENCE_GATE);
		this.addDrop(Blocks.PALE_OAK_FENCE);
		this.addDrop(Blocks.PALE_MOSS_CARPET, block -> this.paleMossCarpetDrops(block));
		this.addDrop(Blocks.PALE_HANGING_MOSS, block -> this.dropsWithSilkTouchOrShears(block));
		this.addDrop(Blocks.PALE_MOSS_BLOCK);
		this.addPottedPlantDrops(Blocks.POTTED_PALE_OAK_SAPLING);
		this.addDrop(Blocks.PALE_OAK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.PALE_OAK_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.PALE_OAK_LEAVES, block -> this.leavesDrops(block, Blocks.PALE_OAK_SAPLING, SAPLING_DROP_CHANCE));
		this.addDropWithSilkTouch(Blocks.CREAKING_HEART);
	}
}
