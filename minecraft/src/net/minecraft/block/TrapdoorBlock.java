package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TrapdoorBlock extends HorizontalFacingBlock implements Waterloggable {
	public static final BooleanProperty field_11631 = Properties.field_12537;
	public static final EnumProperty<BlockHalf> field_11625 = Properties.field_12518;
	public static final BooleanProperty field_11629 = Properties.field_12484;
	public static final BooleanProperty field_11626 = Properties.field_12508;
	protected static final VoxelShape field_11627 = Block.method_9541(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	protected static final VoxelShape field_11630 = Block.method_9541(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11624 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_11633 = Block.method_9541(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_11632 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	protected static final VoxelShape field_11628 = Block.method_9541(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

	protected TrapdoorBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11177, Direction.NORTH)
				.method_11657(field_11631, Boolean.valueOf(false))
				.method_11657(field_11625, BlockHalf.BOTTOM)
				.method_11657(field_11629, Boolean.valueOf(false))
				.method_11657(field_11626, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		if (!(Boolean)blockState.method_11654(field_11631)) {
			return blockState.method_11654(field_11625) == BlockHalf.TOP ? field_11628 : field_11632;
		} else {
			switch ((Direction)blockState.method_11654(field_11177)) {
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
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Boolean)blockState.method_11654(field_11631);
			case field_48:
				return (Boolean)blockState.method_11654(field_11626);
			case field_51:
				return (Boolean)blockState.method_11654(field_11631);
			default:
				return false;
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (this.field_10635 == Material.METAL) {
			return false;
		} else {
			blockState = blockState.method_11572(field_11631);
			world.method_8652(blockPos, blockState, 2);
			if ((Boolean)blockState.method_11654(field_11626)) {
				world.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			this.method_10740(playerEntity, world, blockPos, (Boolean)blockState.method_11654(field_11631));
			return true;
		}
	}

	protected void method_10740(@Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, boolean bl) {
		if (bl) {
			int i = this.field_10635 == Material.METAL ? 1037 : 1007;
			world.method_8444(playerEntity, i, blockPos, 0);
		} else {
			int i = this.field_10635 == Material.METAL ? 1036 : 1013;
			world.method_8444(playerEntity, i, blockPos, 0);
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			boolean bl = world.method_8479(blockPos);
			if (bl != (Boolean)blockState.method_11654(field_11629)) {
				if ((Boolean)blockState.method_11654(field_11631) != bl) {
					blockState = blockState.method_11657(field_11631, Boolean.valueOf(bl));
					this.method_10740(null, world, blockPos, bl);
				}

				world.method_8652(blockPos, blockState.method_11657(field_11629, Boolean.valueOf(bl)), 2);
				if ((Boolean)blockState.method_11654(field_11626)) {
					world.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
				}
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564();
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		Direction direction = itemPlacementContext.method_8038();
		if (!itemPlacementContext.method_7717() && direction.getAxis().isHorizontal()) {
			blockState = blockState.method_11657(field_11177, direction)
				.method_11657(
					field_11625, itemPlacementContext.method_17698().y - (double)itemPlacementContext.method_8037().getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM
				);
		} else {
			blockState = blockState.method_11657(field_11177, itemPlacementContext.method_8042().getOpposite())
				.method_11657(field_11625, direction == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
		}

		if (itemPlacementContext.method_8045().method_8479(itemPlacementContext.method_8037())) {
			blockState = blockState.method_11657(field_11631, Boolean.valueOf(true)).method_11657(field_11629, Boolean.valueOf(true));
		}

		return blockState.method_11657(field_11626, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_11631, field_11625, field_11629, field_11626);
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11626) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_11626)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}
}
