package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class DefaultFlowerFeature extends FlowerFeature {
	public DefaultFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos pos) {
		return random.nextFloat() > 0.6666667F ? Blocks.DANDELION.getDefaultState() : Blocks.POPPY.getDefaultState();
	}
}
