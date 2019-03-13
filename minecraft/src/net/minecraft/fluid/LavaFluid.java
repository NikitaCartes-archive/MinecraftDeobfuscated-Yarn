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
	public Fluid method_15750() {
		return Fluids.FLOWING_LAVA;
	}

	@Override
	public Fluid method_15751() {
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
	public void method_15776(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		BlockPos blockPos2 = blockPos.up();
		if (world.method_8320(blockPos2).isAir() && !world.method_8320(blockPos2).method_11598(world, blockPos2)) {
			if (random.nextInt(100) == 0) {
				double d = (double)((float)blockPos.getX() + random.nextFloat());
				double e = (double)(blockPos.getY() + 1);
				double f = (double)((float)blockPos.getZ() + random.nextFloat());
				world.method_8406(ParticleTypes.field_11239, d, e, f, 0.0, 0.0, 0.0);
				world.method_8486(d, e, f, SoundEvents.field_14576, SoundCategory.field_15245, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.method_8486(
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
	public void method_15792(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (world.getGameRules().getBoolean("doFireTick")) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockPos2 = blockPos;

				for (int j = 0; j < i; j++) {
					blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.method_8477(blockPos2)) {
						return;
					}

					BlockState blockState = world.method_8320(blockPos2);
					if (blockState.isAir()) {
						if (this.method_15819(world, blockPos2)) {
							world.method_8501(blockPos2, Blocks.field_10036.method_9564());
							return;
						}
					} else if (blockState.method_11620().suffocates()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.method_8477(blockPos3)) {
						return;
					}

					if (world.method_8623(blockPos3.up()) && this.method_15817(world, blockPos3)) {
						world.method_8501(blockPos3.up(), Blocks.field_10036.method_9564());
					}
				}
			}
		}
	}

	private boolean method_15819(ViewableWorld viewableWorld, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_15817(viewableWorld, blockPos.method_10093(direction))) {
				return true;
			}
		}

		return false;
	}

	private boolean method_15817(ViewableWorld viewableWorld, BlockPos blockPos) {
		return blockPos.getY() >= 0 && blockPos.getY() < 256 && !viewableWorld.method_8591(blockPos)
			? false
			: viewableWorld.method_8320(blockPos).method_11620().isBurnable();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleParameters method_15787() {
		return ParticleTypes.field_11223;
	}

	@Override
	protected void method_15730(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_15818(iWorld, blockPos);
	}

	@Override
	public int method_15733(ViewableWorld viewableWorld) {
		return viewableWorld.method_8597().doesWaterVaporize() ? 4 : 2;
	}

	@Override
	public BlockState method_15790(FluidState fluidState) {
		return Blocks.field_10164.method_9564().method_11657(FluidBlock.field_11278, Integer.valueOf(method_15741(fluidState)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}

	@Override
	public int method_15739(ViewableWorld viewableWorld) {
		return viewableWorld.method_8597().doesWaterVaporize() ? 1 : 2;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.method_15763(blockView, blockPos) >= 0.44444445F && fluid.method_15791(FluidTags.field_15517);
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return viewableWorld.method_8597().isNether() ? 10 : 30;
	}

	@Override
	public int method_15753(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		int i = this.getTickRate(world);
		if (!fluidState.isEmpty()
			&& !fluidState2.isEmpty()
			&& !(Boolean)fluidState.method_11654(FALLING)
			&& !(Boolean)fluidState2.method_11654(FALLING)
			&& fluidState2.method_15763(world, blockPos) > fluidState.method_15763(world, blockPos)
			&& world.getRandom().nextInt(4) != 0) {
			i *= 4;
		}

		return i;
	}

	protected void method_15818(IWorld iWorld, BlockPos blockPos) {
		double d = (double)blockPos.getX();
		double e = (double)blockPos.getY();
		double f = (double)blockPos.getZ();
		iWorld.method_8396(
			null, blockPos, SoundEvents.field_15112, SoundCategory.field_15245, 0.5F, 2.6F + (iWorld.getRandom().nextFloat() - iWorld.getRandom().nextFloat()) * 0.8F
		);

		for (int i = 0; i < 8; i++) {
			iWorld.method_8406(ParticleTypes.field_11237, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected boolean method_15737() {
		return false;
	}

	@Override
	protected void method_15745(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			FluidState fluidState2 = iWorld.method_8316(blockPos);
			if (this.method_15791(FluidTags.field_15518) && fluidState2.method_15767(FluidTags.field_15517)) {
				if (blockState.getBlock() instanceof FluidBlock) {
					iWorld.method_8652(blockPos, Blocks.field_10340.method_9564(), 3);
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

	public static class Still extends LavaFluid {
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
