package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleParameters;
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

public abstract class LavaFluid extends BaseFluid {
	@Override
	public Fluid getFlowing() {
		return Fluids.FLOWING_LAVA;
	}

	@Override
	public Fluid getStill() {
		return Fluids.LAVA;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public Item getBucketItem() {
		return Items.field_8187;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		BlockPos blockPos2 = blockPos.up();
		if (world.getBlockState(blockPos2).isAir() && !world.getBlockState(blockPos2).isFullOpaque(world, blockPos2)) {
			if (random.nextInt(100) == 0) {
				double d = (double)((float)blockPos.getX() + random.nextFloat());
				double e = (double)(blockPos.getY() + 1);
				double f = (double)((float)blockPos.getZ() + random.nextFloat());
				world.addParticle(ParticleTypes.field_11239, d, e, f, 0.0, 0.0, 0.0);
				world.playSound(d, e, f, SoundEvents.field_14576, SoundCategory.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.playSound(
					(double)blockPos.getX(),
					(double)blockPos.getY(),
					(double)blockPos.getZ(),
					SoundEvents.field_15021,
					SoundCategory.field_15245,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		}
	}

	@Override
	public void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockPos2 = blockPos;

				for (int j = 0; j < i; j++) {
					blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.isHeightValidAndBlockLoaded(blockPos2)) {
						return;
					}

					BlockState blockState = world.getBlockState(blockPos2);
					if (blockState.isAir()) {
						if (this.method_15819(world, blockPos2)) {
							world.setBlockState(blockPos2, Blocks.field_10036.getDefaultState());
							return;
						}
					} else if (blockState.getMaterial().suffocates()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.isHeightValidAndBlockLoaded(blockPos3)) {
						return;
					}

					if (world.isAir(blockPos3.up()) && this.method_15817(world, blockPos3)) {
						world.setBlockState(blockPos3.up(), Blocks.field_10036.getDefaultState());
					}
				}
			}
		}
	}

	private boolean method_15819(ViewableWorld viewableWorld, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_15817(viewableWorld, blockPos.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private boolean method_15817(ViewableWorld viewableWorld, BlockPos blockPos) {
		return blockPos.getY() >= 0 && blockPos.getY() < 256 && !viewableWorld.isBlockLoaded(blockPos)
			? false
			: viewableWorld.getBlockState(blockPos).getMaterial().isBurnable();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleParameters getParticle() {
		return ParticleTypes.field_11223;
	}

	@Override
	protected void method_15730(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_15818(iWorld, blockPos);
	}

	@Override
	public int method_15733(ViewableWorld viewableWorld) {
		return viewableWorld.getDimension().doesWaterVaporize() ? 4 : 2;
	}

	@Override
	public BlockState toBlockState(FluidState fluidState) {
		return Blocks.field_10164.getDefaultState().with(FluidBlock.LEVEL, Integer.valueOf(method_15741(fluidState)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}

	@Override
	public int method_15739(ViewableWorld viewableWorld) {
		return viewableWorld.getDimension().doesWaterVaporize() ? 1 : 2;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.getHeight(blockView, blockPos) >= 0.44444445F && fluid.matches(FluidTags.field_15517);
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return viewableWorld.getDimension().isNether() ? 10 : 30;
	}

	@Override
	public int method_15753(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		int i = this.getTickRate(world);
		if (!fluidState.isEmpty()
			&& !fluidState2.isEmpty()
			&& !(Boolean)fluidState.get(FALLING)
			&& !(Boolean)fluidState2.get(FALLING)
			&& fluidState2.getHeight(world, blockPos) > fluidState.getHeight(world, blockPos)
			&& world.getRandom().nextInt(4) != 0) {
			i *= 4;
		}

		return i;
	}

	protected void method_15818(IWorld iWorld, BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		iWorld.playSound(
			null, blockPos, SoundEvents.field_15112, SoundCategory.field_15245, 0.5F, 2.6F + (iWorld.getRandom().nextFloat() - iWorld.getRandom().nextFloat()) * 0.8F
		);

		for (int i = 0; i < 8; i++) {
			iWorld.addParticle(ParticleTypes.field_11237, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_15737() {
		return false;
	}

	@Override
	protected void method_15745(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			FluidState fluidState2 = iWorld.getFluidState(blockPos);
			if (this.matches(FluidTags.field_15518) && fluidState2.matches(FluidTags.field_15517)) {
				if (blockState.getBlock() instanceof FluidBlock) {
					iWorld.setBlockState(blockPos, Blocks.field_10340.getDefaultState(), 3);
				}

				this.method_15818(iWorld, blockPos);
				return;
			}
		}

		super.method_15745(iWorld, blockPos, blockState, direction, fluidState);
	}

	@Override
	protected boolean hasRandomTicks() {
		return true;
	}

	@Override
	protected float getBlastResistance() {
		return 100.0F;
	}

	public static class Flowing extends LavaFluid {
		@Override
		protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.with(LEVEL);
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

	public static class Still extends LavaFluid {
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
