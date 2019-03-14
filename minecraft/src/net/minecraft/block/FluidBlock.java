package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class FluidBlock extends Block implements FluidDrainable {
	public static final IntegerProperty LEVEL = Properties.FLUID_BLOCK_LEVEL;
	protected final BaseFluid fluid;
	private final List<FluidState> statesByLevel;

	protected FluidBlock(BaseFluid baseFluid, Block.Settings settings) {
		super(settings);
		this.fluid = baseFluid;
		this.statesByLevel = Lists.<FluidState>newArrayList();
		this.statesByLevel.add(baseFluid.getState(false));

		for (int i = 1; i < 8; i++) {
			this.statesByLevel.add(baseFluid.getFlowing(8 - i, false));
		}

		this.statesByLevel.add(baseFluid.getFlowing(8, true));
		this.setDefaultState(this.stateFactory.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public void onRandomTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.getFluidState(blockPos).onRandomTick(world, blockPos, random);
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return !this.fluid.matches(FluidTags.field_15518);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		int i = (Integer)blockState.get(LEVEL);
		return (FluidState)this.statesByLevel.get(Math.min(i, 8));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean skipRenderingSide(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getFluidState().getFluid().matchesType(this.fluid) ? true : super.isFullBoundsCubeForCulling(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11455;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return VoxelShapes.empty();
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.fluid.getTickRate(viewableWorld);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(world));
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (blockState.getFluidState().isStill() || blockState2.getFluidState().isStill()) {
			iWorld.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(iWorld));
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(world));
		}
	}

	public boolean method_10316(World world, BlockPos blockPos, BlockState blockState) {
		if (this.fluid.matches(FluidTags.field_15518)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getFluidState(blockPos.offset(direction)).matches(FluidTags.field_15517)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				FluidState fluidState = world.getFluidState(blockPos);
				if (fluidState.isStill()) {
					world.setBlockState(blockPos, Blocks.field_10540.getDefaultState());
					this.method_10318(world, blockPos);
					return false;
				}

				if (fluidState.getHeight(world, blockPos) >= 0.44444445F) {
					world.setBlockState(blockPos, Blocks.field_10445.getDefaultState());
					this.method_10318(world, blockPos);
					return false;
				}
			}
		}

		return true;
	}

	protected void method_10318(IWorld iWorld, BlockPos blockPos) {
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
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(LEVEL);
	}

	@Override
	public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Integer)blockState.get(LEVEL) == 0) {
			iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 11);
			return this.fluid;
		} else {
			return Fluids.EMPTY;
		}
	}
}
