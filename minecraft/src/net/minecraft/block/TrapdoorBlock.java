package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.class_2760;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
	public static final BooleanProperty field_11631 = Properties.OPEN;
	public static final EnumProperty<class_2760> field_11625 = Properties.field_12518;
	public static final BooleanProperty field_11629 = Properties.POWERED;
	public static final BooleanProperty field_11626 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11627 = Block.createCubeShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_11630 = Block.createCubeShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11624 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_11633 = Block.createCubeShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11632 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape field_11628 = Block.createCubeShape(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected TrapdoorBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11177, Direction.NORTH)
				.with(field_11631, Boolean.valueOf(false))
				.with(field_11625, class_2760.BOTTOM)
				.with(field_11629, Boolean.valueOf(false))
				.with(field_11626, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		if (!(Boolean)blockState.get(field_11631)) {
			return blockState.get(field_11625) == class_2760.TOP ? field_11628 : field_11632;
		} else {
			switch ((Direction)blockState.get(field_11177)) {
				case NORTH:
				default:
					return field_11633;
				case SOUTH:
					return field_11624;
				case WEST:
					return field_11630;
				case EAST:
					return field_11627;
			}
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		switch (placementEnvironment) {
			case field_50:
				return (Boolean)blockState.get(field_11631);
			case field_48:
				return (Boolean)blockState.get(field_11626);
			case field_51:
				return (Boolean)blockState.get(field_11631);
			default:
				return false;
		}
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (this.material == Material.METAL) {
			return false;
		} else {
			blockState = blockState.method_11572(field_11631);
			world.setBlockState(blockPos, blockState, 2);
			if ((Boolean)blockState.get(field_11626)) {
				world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(world));
			}

			this.method_10740(playerEntity, world, blockPos, (Boolean)blockState.get(field_11631));
			return true;
		}
	}

	protected void method_10740(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, boolean bl) {
		if (bl) {
			int i = this.material == Material.METAL ? 1037 : 1007;
			world.fireWorldEvent(playerEntity, i, blockPos, 0);
		} else {
			int i = this.material == Material.METAL ? 1036 : 1013;
			world.fireWorldEvent(playerEntity, i, blockPos, 0);
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isRemote) {
			boolean bl = world.isReceivingRedstonePower(blockPos);
			if (bl != (Boolean)blockState.get(field_11629)) {
				if ((Boolean)blockState.get(field_11631) != bl) {
					blockState = blockState.with(field_11631, Boolean.valueOf(bl));
					this.method_10740(null, world, blockPos, bl);
				}

				world.setBlockState(blockPos, blockState.with(field_11629, Boolean.valueOf(bl)), 2);
				if ((Boolean)blockState.get(field_11626)) {
					world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(world));
				}
			}
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		Direction direction = itemPlacementContext.method_8038();
		if (!itemPlacementContext.method_7717() && direction.getAxis().isHorizontal()) {
			blockState = blockState.with(field_11177, direction).with(field_11625, itemPlacementContext.getHitY() > 0.5F ? class_2760.TOP : class_2760.BOTTOM);
		} else {
			blockState = blockState.with(field_11177, itemPlacementContext.method_8042().getOpposite())
				.with(field_11625, direction == Direction.UP ? class_2760.BOTTOM : class_2760.TOP);
		}

		if (itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getPos())) {
			blockState = blockState.with(field_11631, Boolean.valueOf(true)).with(field_11629, Boolean.valueOf(true));
		}

		return blockState.with(field_11626, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11177, field_11631, field_11625, field_11629, field_11626);
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_11626) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.get(field_11626)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
