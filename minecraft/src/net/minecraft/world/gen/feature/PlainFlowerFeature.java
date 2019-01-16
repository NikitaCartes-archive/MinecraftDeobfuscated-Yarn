package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class PlainFlowerFeature extends FlowerFeature {
	public PlainFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public BlockState method_13175(Random random, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.field_10048.getDefaultState();
				case 1:
					return Blocks.field_10270.getDefaultState();
				case 2:
					return Blocks.field_10315.getDefaultState();
				case 3:
				default:
					return Blocks.field_10156.getDefaultState();
			}
		} else if (random.nextInt(3) > 0) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.field_10449.getDefaultState();
				case 1:
					return Blocks.field_10573.getDefaultState();
				case 2:
					return Blocks.field_10554.getDefaultState();
				case 3:
				default:
					return Blocks.field_9995.getDefaultState();
			}
		} else {
			return Blocks.field_10182.getDefaultState();
		}
	}
}
