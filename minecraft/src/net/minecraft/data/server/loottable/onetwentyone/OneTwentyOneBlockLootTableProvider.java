package net.minecraft.data.server.loottable.onetwentyone;

import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class OneTwentyOneBlockLootTableProvider extends BlockLootTableGenerator {
	protected OneTwentyOneBlockLootTableProvider() {
		super(Set.of(), FeatureSet.of(FeatureFlags.UPDATE_1_21));
	}

	@Override
	protected void generate() {
		this.addDrop(Blocks.CRAFTER);
	}
}
