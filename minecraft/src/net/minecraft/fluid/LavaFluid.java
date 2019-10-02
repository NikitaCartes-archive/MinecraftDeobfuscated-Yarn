package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
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
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class LavaFluid extends BaseFluid {
	@Override
	public Fluid getFlowing() {
		return Fluids.FLOWING_LAVA;
	}

	@Override
	public Fluid getStill() {
		return Fluids.LAVA;
	}

	@Override
	public Item getBucketItem() {
		return Items.LAVA_BUCKET;
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
				world.addParticle(ParticleTypes.LAVA, d, e, f, 0.0, 0.0, 0.0);
				world.playSound(d, e, f, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				world.playSound(
					(double)blockPos.getX(),
					(double)blockPos.getY(),
					(double)blockPos.getZ(),
					SoundEvents.BLOCK_LAVA_AMBIENT,
					SoundCategory.BLOCKS,
					0.2F + random.nextFloat() * 0.2F,
					0.9F + random.nextFloat() * 0.15F,
					false
				);
			}
		}
	}

	@Override
	public void onRandomTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockPos2 = blockPos;

				for (int j = 0; j < i; j++) {
					blockPos2 = blockPos2.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!world.canSetBlock(blockPos2)) {
						return;
					}

					BlockState blockState = world.getBlockState(blockPos2);
					if (blockState.isAir()) {
						if (this.method_15819(world, blockPos2)) {
							world.setBlockState(blockPos2, Blocks.FIRE.getDefaultState());
							return;
						}
					} else if (blockState.getMaterial().blocksMovement()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!world.canSetBlock(blockPos3)) {
						return;
					}

					if (world.isAir(blockPos3.up()) && this.method_15817(world, blockPos3)) {
						world.setBlockState(blockPos3.up(), Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
	}

	private boolean method_15819(WorldView worldView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.method_15817(worldView, blockPos.offset(direction))) {
				return true;
			}
		}

		return false;
	}

	private boolean method_15817(WorldView worldView, BlockPos blockPos) {
		return blockPos.getY() >= 0 && blockPos.getY() < 256 && !worldView.isChunkLoaded(blockPos)
			? false
			: worldView.getBlockState(blockPos).getMaterial().isBurnable();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public ParticleEffect getParticle() {
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		this.method_15818(iWorld, blockPos);
	}

	@Override
	public int method_15733(WorldView worldView) {
		return worldView.getDimension().doesWaterVaporize() ? 4 : 2;
	}

	@Override
	public BlockState toBlockState(FluidState fluidState) {
		return Blocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, Integer.valueOf(method_15741(fluidState)));
	}

	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}

	@Override
	public int getLevelDecreasePerBlock(WorldView worldView) {
		return worldView.getDimension().doesWaterVaporize() ? 1 : 2;
	}

	@Override
	public boolean method_15777(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return fluidState.getHeight(blockView, blockPos) >= 0.44444445F && fluid.matches(FluidTags.WATER);
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return worldView.getDimension().isNether() ? 10 : 30;
	}

	@Override
	public int getNextTickDelay(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
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

	private void method_15818(IWorld iWorld, BlockPos blockPos) {
		iWorld.playLevelEvent(1501, blockPos, 0);
	}

	@Override
	protected boolean isInfinite() {
		return false;
	}

	@Override
	protected void flow(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (direction == Direction.DOWN) {
			FluidState fluidState2 = iWorld.getFluidState(blockPos);
			if (this.matches(FluidTags.LAVA) && fluidState2.matches(FluidTags.WATER)) {
				if (blockState.getBlock() instanceof FluidBlock) {
					iWorld.setBlockState(blockPos, Blocks.STONE.getDefaultState(), 3);
				}

				this.method_15818(iWorld, blockPos);
				return;
			}
		}

		super.flow(iWorld, blockPos, blockState, direction, fluidState);
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
