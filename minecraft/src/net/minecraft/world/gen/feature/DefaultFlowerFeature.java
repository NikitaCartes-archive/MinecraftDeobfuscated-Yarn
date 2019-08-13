package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class DefaultFlowerFeature extends FlowerFeature {
	public DefaultFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos blockPos) {
		return random.nextFloat() > 0.6666667F ? Blocks.field_10182.getDefaultState() : Blocks.field_10449.getDefaultState();
	}
}
