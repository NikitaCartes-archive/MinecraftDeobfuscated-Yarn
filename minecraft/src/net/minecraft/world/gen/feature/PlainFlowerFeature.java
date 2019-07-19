package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class PlainFlowerFeature extends FlowerFeature {
	public PlainFlowerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public BlockState getFlowerToPlace(Random random, BlockPos pos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)pos.getX() / 200.0, (double)pos.getZ() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.ORANGE_TULIP.getDefaultState();
				case 1:
					return Blocks.RED_TULIP.getDefaultState();
				case 2:
					return Blocks.PINK_TULIP.getDefaultState();
				case 3:
				default:
					return Blocks.WHITE_TULIP.getDefaultState();
			}
		} else if (random.nextInt(3) > 0) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.POPPY.getDefaultState();
				case 1:
					return Blocks.AZURE_BLUET.getDefaultState();
				case 2:
					return Blocks.OXEYE_DAISY.getDefaultState();
				case 3:
				default:
					return Blocks.CORNFLOWER.getDefaultState();
			}
		} else {
			return Blocks.DANDELION.getDefaultState();
		}
	}
}
