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
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = Direction.random(random);
		if (direction != Direction.field_11036) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = world.method_8320(blockPos2);
			if (!blockState.isOpaque() || !Block.method_20045(blockState2, world, blockPos2, direction.getOpposite())) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				if (direction == Direction.field_11033) {
					e -= 0.05;
					d += random.nextDouble();
					f += random.nextDouble();
				} else {
					e += random.nextDouble() * 0.8;
					if (direction.getAxis() == Direction.Axis.X) {
						f += random.nextDouble();
						if (direction == Direction.field_11034) {
							d++;
						} else {
							d += 0.05;
						}
					} else {
						d += random.nextDouble();
						if (direction == Direction.field_11035) {
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
