package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ScaffoldingBlock extends Block implements Waterloggable {
	private static final VoxelShape field_16494;
	private static final VoxelShape field_16497;
	private static final VoxelShape field_17577 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	private static final VoxelShape field_17578 = VoxelShapes.method_1077().offset(0.0, -1.0, 0.0);
	public static final IntegerProperty field_16495 = Properties.field_16503;
	public static final BooleanProperty field_16496 = Properties.field_12508;
	public static final BooleanProperty field_16547 = Properties.field_16562;

	protected ScaffoldingBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_16495, Integer.valueOf(7))
				.method_11657(field_16496, Boolean.valueOf(false))
				.method_11657(field_16547, Boolean.valueOf(false))
		);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16495, field_16496, field_16547);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		if (!verticalEntityPosition.method_17785(blockState.getBlock().getItem())) {
			return blockState.method_11654(field_16547) ? field_16497 : field_16494;
		} else {
			return VoxelShapes.method_1077();
		}
	}

	@Override
	public VoxelShape method_9584(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.method_1077();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem();
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.method_8037();
		World world = itemPlacementContext.method_8045();
		int i = this.method_16372(world, blockPos);
		return this.method_9564()
			.method_11657(field_16496, Boolean.valueOf(world.method_8316(blockPos).getFluid() == Fluids.WATER))
			.method_11657(field_16495, Integer.valueOf(i))
			.method_11657(field_16547, Boolean.valueOf(this.method_16373(world, blockPos, i)));
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (!world.isClient) {
			world.method_8397().method_8676(blockPos, this, 1);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_16496)) {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (!iWorld.isClient()) {
			iWorld.method_8397().method_8676(blockPos, this, 1);
		}

		return blockState;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = this.method_16372(world, blockPos);
		BlockState blockState2 = blockState.method_11657(field_16495, Integer.valueOf(i))
			.method_11657(field_16547, Boolean.valueOf(this.method_16373(world, blockPos, i)));
		if ((Integer)blockState2.method_11654(field_16495) == 7) {
			if ((Integer)blockState.method_11654(field_16495) == 7) {
				world.spawnEntity(
					new FallingBlockEntity(
						world,
						(double)blockPos.getX() + 0.5,
						(double)blockPos.getY(),
						(double)blockPos.getZ() + 0.5,
						blockState2.method_11657(field_16496, Boolean.valueOf(false))
					)
				);
			} else {
				world.method_8651(blockPos, true);
			}
		} else if (blockState != blockState2) {
			world.method_8652(blockPos, blockState2, 3);
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		if (verticalEntityPosition.method_16192(VoxelShapes.method_1077(), blockPos, true) && !verticalEntityPosition.isSneaking()) {
			return field_16494;
		} else {
			return blockState.method_11654(field_16495) != 0 && blockState.method_11654(field_16547) && verticalEntityPosition.method_16192(field_17578, blockPos, true)
				? field_17577
				: VoxelShapes.method_1073();
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_16496) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	private boolean method_16373(BlockView blockView, BlockPos blockPos, int i) {
		return i > 0 && blockView.method_8320(blockPos.down()).getBlock() != this;
	}

	private int method_16372(BlockView blockView, BlockPos blockPos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).method_10098(Direction.DOWN);
		BlockState blockState = blockView.method_8320(mutable);
		int i = 7;
		if (blockState.getBlock() == this) {
			i = (Integer)blockState.method_11654(field_16495);
		} else if (Block.method_9501(blockState.method_11628(blockView, mutable), Direction.UP)) {
			return 0;
		}

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockState blockState2 = blockView.method_8320(mutable.method_10101(blockPos).method_10098(direction));
			if (blockState2.getBlock() == this) {
				i = Math.min(i, (Integer)blockState2.method_11654(field_16495) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return i;
	}

	static {
		VoxelShape voxelShape = Block.method_9541(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
		VoxelShape voxelShape2 = Block.method_9541(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
		VoxelShape voxelShape3 = Block.method_9541(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
		VoxelShape voxelShape4 = Block.method_9541(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
		VoxelShape voxelShape5 = Block.method_9541(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
		field_16494 = VoxelShapes.method_17786(voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5);
		VoxelShape voxelShape6 = Block.method_9541(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
		VoxelShape voxelShape7 = Block.method_9541(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape8 = Block.method_9541(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape9 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
		field_16497 = VoxelShapes.method_17786(ScaffoldingBlock.field_17577, field_16494, voxelShape7, voxelShape6, voxelShape9, voxelShape8);
	}
}
