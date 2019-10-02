package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class WaterFluid extends BaseFluid {
	@Override
	public Fluid getFlowing() {
		return Fluids.FLOWING_WATER;
	}

	@Override
	public Fluid getStill() {
		return Fluids.WATER;
	}

	@Override
	public Item getBucketItem() {
		return Items.WATER_BUCKET;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (!fluidState.isStill() && !(Boolean)fluidState.get(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound(
					(double)blockPos.getX() + 0.5,
					(double)blockPos.getY() + 0.5,
					(double)blockPos.getZ() + 0.5,
					SoundEvents.BLOCK_WATER_AMBIENT,
					SoundCategory.BLOCKS,
					random.nextFloat() * 0.25F + 0.75F,
					random.nextFloat() + 0.5F,
					false
				);
			}
		} else if (random.nextInt(10) == 0) {
			world.addParticle(
				ParticleTypes.UNDERWATER,
				(double)((float)blockPos.getX() + random.nextFloat()),
				(double)((float)blockPos.getY() + random.nextFloat()),
				(double)((float)blockPos.getZ() + random.nextFloat()),
				0.0,
				0.0,
				0.0
			);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean isInfinite() {
		return true;
	}

	@Override
	protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.getBlockEntity(blockPos) : null;
		Block.dropStacks(blockState, iWorld.getWorld(), blockPos, blockEntity);
	}

	@Override
	public int method_15733(WorldView worldView) {
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState fluidState) {
		return Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, Integer.valueOf(method_15741(fluidState)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView worldView) {
		return 1;
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 5;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.matches(FluidTags.WATER);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends WaterFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState fluidState) {
			return (Integer)fluidState.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
	}

	public static class Still extends WaterFluid {
		@Override
		public int getLevel(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
	}
}
