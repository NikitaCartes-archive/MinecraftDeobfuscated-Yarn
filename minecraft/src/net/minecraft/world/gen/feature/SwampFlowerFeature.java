package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class SwampFlowerFeature extends FlowerFeature {
	public SwampFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos pos) {
		return Blocks.BLUE_ORCHID.getDefaultState();
	}
}
