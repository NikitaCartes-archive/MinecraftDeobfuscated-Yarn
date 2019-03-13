package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SmokerBlock extends AbstractFurnaceBlock {
	protected SmokerBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new SmokerBlockEntity();
	}

	@Override
	protected void method_17025(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof SmokerBlockEntity) {
			playerEntity.openContainer((NameableContainerProvider)blockEntity);
			playerEntity.method_7281(Stats.field_17273);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11105)) {
			double d = (double)blockPos.getX() + 0.5;
			double e = (double)blockPos.getY();
			double f = (double)blockPos.getZ() + 0.5;
			if (random.nextDouble() < 0.1) {
				world.method_8486(d, e, f, SoundEvents.field_17618, SoundCategory.field_15245, 1.0F, 1.0F, false);
			}

			world.method_8406(ParticleTypes.field_11251, d, e + 1.1, f, 0.0, 0.0, 0.0);
		}
	}
}
