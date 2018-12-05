package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class SwampFlowerFeature extends FlowerFeature {
	public SwampFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public BlockState method_13175(Random random, BlockPos blockPos) {
		return Blocks.field_10086.getDefaultState();
	}
}
