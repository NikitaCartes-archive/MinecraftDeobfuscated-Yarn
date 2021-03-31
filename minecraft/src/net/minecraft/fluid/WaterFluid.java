package net.minecraft.fluid;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class WaterFluid extends FlowableFluid {
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

	@Override
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
		if (!state.isStill() && !(Boolean)state.get(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound(
					(double)pos.getX() + 0.5,
					(double)pos.getY() + 0.5,
					(double)pos.getZ() + 0.5,
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
				(double)pos.getX() + random.nextDouble(),
				(double)pos.getY() + random.nextDouble(),
				(double)pos.getZ() + random.nextDouble(),
				0.0,
				0.0,
				0.0
			);
		}
	}

	@Nullable
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean isInfinite() {
		return true;
	}

	@Override
	protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropStacks(state, world, pos, blockEntity);
	}

	@Override
	public int getFlowSpeed(WorldView world) {
		return 4;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, Integer.valueOf(getBlockStateLevel(state)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView world) {
		return 1;
	}

	@Override
	public int getTickRate(WorldView world) {
		return 5;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
		return direction == Direction.DOWN && !fluid.isIn(FluidTags.WATER);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	@Override
	public Optional<SoundEvent> getBucketFillSound() {
		return Optional.of(SoundEvents.ITEM_BUCKET_FILL);
	}

	public static class Flowing extends WaterFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state) {
			return (Integer)state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}

	public static class Still extends WaterFluid {
		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}
}
