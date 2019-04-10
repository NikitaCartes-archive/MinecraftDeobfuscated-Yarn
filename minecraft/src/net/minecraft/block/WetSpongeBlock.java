package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WetSpongeBlock extends Block {
	protected WetSpongeBlock(Block.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = Direction.random(random);
		if (direction != Direction.UP) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (!blockState.isFullBoundsCubeForCulling() || !Block.isSolidFullSquare(blockState2, world, blockPos2, direction.getOpposite())) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				if (direction == Direction.DOWN) {
					e -= 0.05;
					d += random.nextDouble();
					f += random.nextDouble();
				} else {
					e += random.nextDouble() * 0.8;
					if (direction.getAxis() == Direction.Axis.X) {
						f += random.nextDouble();
						if (direction == Direction.EAST) {
							d++;
						} else {
							d += 0.05;
						}
					} else {
						d += random.nextDouble();
						if (direction == Direction.SOUTH) {
							f++;
						} else {
							f += 0.05;
						}
					}
				}

				world.addParticle(ParticleTypes.field_11232, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}
}
