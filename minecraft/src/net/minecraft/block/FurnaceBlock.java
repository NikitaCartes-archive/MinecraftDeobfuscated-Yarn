package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FurnaceBlock extends AbstractFurnaceBlock {
	protected FurnaceBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new FurnaceBlockEntity();
	}

	@Override
	protected void method_17025(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof FurnaceBlockEntity) {
			playerEntity.openContainer((NameableContainerProvider)blockEntity);
			playerEntity.increaseStat(Stats.field_15379);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(field_11105)) {
			double d = (double)blockPos.getX() + 0.5;
			double e = (double)blockPos.getY();
			double f = (double)blockPos.getZ() + 0.5;
			if (random.nextDouble() < 0.1) {
				world.playSound(d, e, f, SoundEvents.field_15006, SoundCategory.field_15245, 1.0F, 1.0F, false);
			}

			Direction direction = blockState.get(FACING);
			Direction.Axis axis = direction.getAxis();
			double g = 0.52;
			double h = random.nextDouble() * 0.6 - 0.3;
			double i = axis == Direction.Axis.X ? (double)direction.getOffsetX() * 0.52 : h;
			double j = random.nextDouble() * 6.0 / 16.0;
			double k = axis == Direction.Axis.Z ? (double)direction.getOffsetZ() * 0.52 : h;
			world.addParticle(ParticleTypes.field_11251, d + i, e + j, f + k, 0.0, 0.0, 0.0);
			world.addParticle(ParticleTypes.field_11240, d + i, e + j, f + k, 0.0, 0.0, 0.0);
		}
	}
}
