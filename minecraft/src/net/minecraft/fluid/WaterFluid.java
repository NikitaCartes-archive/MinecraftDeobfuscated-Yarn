package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
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
import net.minecraft.state.StateFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class WaterFluid extends BaseFluid {
	@Override
	public Fluid method_15750() {
		return Fluids.FLOWING_WATER;
	}

	@Override
	public Fluid method_15751() {
		return Fluids.WATER;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}

	@Override
	public Item getBucketItem() {
		return Items.field_8705;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_15776(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (!fluidState.isStill() && !(Boolean)fluidState.method_11654(FALLING)) {
			if (random.nextInt(64) == 0) {
				world.playSound(
					(double)blockPos.getX() + 0.5,
					(double)blockPos.getY() + 0.5,
					(double)blockPos.getZ() + 0.5,
					SoundEvents.field_15237,
					SoundCategory.field_15245,
					random.nextFloat() * 0.25F + 0.75F,
					random.nextFloat() + 0.5F,
					false
				);
			}
		} else if (random.nextInt(10) == 0) {
			world.addParticle(
				ParticleTypes.field_11210,
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
		return ParticleTypes.field_11232;
	}

	@Override
	protected boolean isInfinite() {
		return true;
	}

	@Override
	protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.method_8321(blockPos) : null;
		Block.method_9610(blockState, iWorld.getWorld(), blockPos, blockEntity);
	}

	@Override
	public int method_15733(ViewableWorld viewableWorld) {
		return 4;
	}

	@Override
	public BlockState method_15790(FluidState fluidState) {
		return Blocks.field_10382.method_9564().method_11657(FluidBlock.field_11278, Integer.valueOf(method_15741(fluidState)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER;
	}

	@Override
	public int getLevelDecreasePerBlock(ViewableWorld viewableWorld) {
		return 1;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 5;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return direction == Direction.field_11033 && !fluid.matches(FluidTags.field_15517);
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends WaterFluid {
		@Override
		protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.method_11667(LEVEL);
		}

		@Override
		public int method_15779(FluidState fluidState) {
			return (Integer)fluidState.method_11654(LEVEL);
		}

		@Override
		public boolean method_15793(FluidState fluidState) {
			return false;
		}
	}

	public static class Still extends WaterFluid {
		@Override
		public int method_15779(FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean method_15793(FluidState fluidState) {
			return true;
		}
	}
}
