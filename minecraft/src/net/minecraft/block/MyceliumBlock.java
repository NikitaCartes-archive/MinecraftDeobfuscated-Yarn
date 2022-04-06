package net.minecraft.block;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.World;

public class MyceliumBlock extends SpreadableBlock {
	public MyceliumBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, AbstractRandom random) {
		super.randomDisplayTick(state, world, pos, random);
		if (random.nextInt(10) == 0) {
			world.addParticle(
				ParticleTypes.MYCELIUM, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0
			);
		}
	}
}
