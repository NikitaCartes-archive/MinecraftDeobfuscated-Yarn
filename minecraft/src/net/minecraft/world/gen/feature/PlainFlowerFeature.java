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
	public BlockState getFlowerToPlace(Random random, BlockPos blockPos) {
		double d = Biome.field_9324.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0);
		if (d < -0.8) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.field_10048.method_9564();
				case 1:
					return Blocks.field_10270.method_9564();
				case 2:
					return Blocks.field_10315.method_9564();
				case 3:
				default:
					return Blocks.field_10156.method_9564();
			}
		} else if (random.nextInt(3) > 0) {
			int i = random.nextInt(4);
			switch (i) {
				case 0:
					return Blocks.field_10449.method_9564();
				case 1:
					return Blocks.field_10573.method_9564();
				case 2:
					return Blocks.field_10554.method_9564();
				case 3:
				default:
					return Blocks.field_9995.method_9564();
			}
		} else {
			return Blocks.field_10182.method_9564();
		}
	}
}
