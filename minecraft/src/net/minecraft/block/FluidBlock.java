package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class FluidBlock extends Block implements FluidDrainable {
	public static final IntProperty LEVEL = Properties.LEVEL_15;
	protected final BaseFluid fluid;
	private final List<FluidState> statesByLevel;

	protected FluidBlock(BaseFluid baseFluid, Block.Settings settings) {
		super(settings);
		this.fluid = baseFluid;
		this.statesByLevel = Lists.<FluidState>newArrayList();
		this.statesByLevel.add(baseFluid.getStill(false));

		for (int i = 1; i < 8; i++) {
			this.statesByLevel.add(baseFluid.getFlowing(8 - i, false));
		}

		this.statesByLevel.add(baseFluid.getFlowing(8, true));
		this.setDefaultState(this.stateFactory.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Override
	public void onRandomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		serverWorld.getFluidState(blockPos).onRandomTick(serverWorld, blockPos, random);
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return !this.fluid.matches(FluidTags.LAVA);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		int i = (Integer)blockState.get(LEVEL);
		return (FluidState)this.statesByLevel.get(Math.min(i, 8));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isSideInvisible(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getFluidState().getFluid().matchesType(this.fluid);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.empty();
	}

	@Override
	public int getTickRate(class_4538 arg) {
		return this.fluid.getTickRate(arg);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (this.receiveNeighborFluids(world, blockPos, blockState)) {
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
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (this.receiveNeighborFluids(world, blockPos, blockState)) {
			world.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(world));
		}
	}

	public boolean receiveNeighborFluids(World world, BlockPos blockPos, BlockState blockState) {
		if (this.fluid.matches(FluidTags.LAVA)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getFluidState(blockPos.offset(direction)).matches(FluidTags.WATER)) {
					bl = true;
					break;
				}
			}

			if (bl) {
				FluidState fluidState = world.getFluidState(blockPos);
				if (fluidState.isStill()) {
					world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState());
					this.playExtinguishSound(world, blockPos);
					return false;
				}

				if (fluidState.getHeight(world, blockPos) >= 0.44444445F) {
					world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
					this.playExtinguishSound(world, blockPos);
					return false;
				}
			}
		}

		return true;
	}

	private void playExtinguishSound(IWorld iWorld, BlockPos blockPos) {
		iWorld.playLevelEvent(1501, blockPos, 0);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public Fluid tryDrainFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Integer)blockState.get(LEVEL) == 0) {
			iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 11);
			return this.fluid;
		} else {
			return Fluids.EMPTY;
		}
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (this.fluid.matches(FluidTags.LAVA)) {
			entity.setInLava();
		}
	}
}
