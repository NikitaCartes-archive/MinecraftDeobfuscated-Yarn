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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		super.randomDisplayTick(state, world, pos, random);
		if (random.nextInt(10) == 0) {
			world.addParticle(
				ParticleTypes.MYCELIUM,
				(double)pos.getX() + (double)random.nextFloat(),
				(double)pos.getY() + 1.1,
				(double)pos.getZ() + (double)random.nextFloat(),
				0.0,
				0.0,
				0.0
			);
		}
	}
}
