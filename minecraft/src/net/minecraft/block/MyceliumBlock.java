package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MyceliumBlock extends SpreadableBlock {
	public MyceliumBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		super.randomDisplayTick(blockState, world, blockPos, random);
		if (random.nextInt(10) == 0) {
			world.method_8406(
				ParticleTypes.field_11219,
				(double)((float)blockPos.getX() + random.nextFloat()),
				(double)((float)blockPos.getY() + 1.1F),
				(double)((float)blockPos.getZ() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}
}
