package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2263;
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

public class FluidBlock extends Block implements class_2263 {
	public static final IntegerProperty field_11278 = Properties.FLUID_BLOCK_LEVEL;
	protected final BaseFluid field_11279;
	private final List<FluidState> field_11276;
	private final Map<BlockState, VoxelShape> field_11277 = Maps.<BlockState, VoxelShape>newIdentityHashMap();

	protected FluidBlock(BaseFluid baseFluid, Block.Settings settings) {
		super(settings);
		this.field_11279 = baseFluid;
		this.field_11276 = Lists.<FluidState>newArrayList();
		this.field_11276.add(baseFluid.getState(false));

		for (int i = 1; i < 8; i++) {
			this.field_11276.add(baseFluid.method_15728(8 - i, false));
		}

		this.field_11276.add(baseFluid.method_15728(8, true));
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11278, Integer.valueOf(0)));
	}

	@Override
	public void randomTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		world.getFluidState(blockPos).onRandomTick(world, blockPos, random);
	}

	@Override
	public boolean method_9579(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return !this.field_11279.matches(FluidTags.field_15518);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		int i = (Integer)blockState.get(field_11278);
		return (FluidState)this.field_11276.get(Math.min(i, 8));
	}

	@Override
	public boolean canCollideWith(BlockState blockState) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(BlockState blockState, BlockState blockState2, Direction direction) {
		return blockState2.getFluidState().getFluid().matchesType(this.field_11279) ? true : super.isFullBoundsCubeForCulling(blockState);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		FluidState fluidState = blockView.getFluidState(blockPos.up());
		return fluidState.getFluid().matchesType(this.field_11279)
			? VoxelShapes.fullCube()
			: (VoxelShape)this.field_11277.computeIfAbsent(blockState, blockStatex -> {
				FluidState fluidStatex = blockStatex.getFluidState();
				return VoxelShapes.cube(0.0, 0.0, 0.0, 1.0, (double)fluidStatex.method_15763(), 1.0);
			});
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.NONE;
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		return Collections.emptyList();
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return this.field_11279.method_15789(viewableWorld);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(world));
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (blockState.getFluidState().isStill() || blockState2.getFluidState().isStill()) {
			iWorld.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (this.method_10316(world, blockPos, blockState)) {
			world.getFluidTickScheduler().schedule(blockPos, blockState.getFluidState().getFluid(), this.getTickRate(world));
		}
	}

	public boolean method_10316(World world, BlockPos blockPos, BlockState blockState) {
		if (this.field_11279.matches(FluidTags.field_15518)) {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && world.getFluidState(blockPos.method_10093(direction)).matches(FluidTags.field_15517)) {
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

				if (fluidState.method_15763() >= 0.44444445F) {
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
			iWorld.method_8406(ParticleTypes.field_11237, d + Math.random(), e + 1.2, f + Math.random(), 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11278);
	}

	@Override
	public Fluid method_9700(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		if ((Integer)blockState.get(field_11278) == 0) {
			iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 11);
			return this.field_11279;
		} else {
			return Fluids.field_15906;
		}
	}
}
