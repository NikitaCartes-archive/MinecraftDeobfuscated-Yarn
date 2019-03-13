package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.TaskPriority;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractRedstoneGateBlock extends HorizontalFacingBlock {
	protected static final VoxelShape field_10912 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final BooleanProperty field_10911 = Properties.field_12484;

	protected AbstractRedstoneGateBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10912;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return viewableWorld.method_8320(blockPos2).method_11631(viewableWorld, blockPos2);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!this.method_9996(world, blockPos, blockState)) {
			boolean bl = (Boolean)blockState.method_11654(field_10911);
			boolean bl2 = this.method_9990(world, blockPos, blockState);
			if (bl && !bl2) {
				world.method_8652(blockPos, blockState.method_11657(field_10911, Boolean.valueOf(false)), 2);
			} else if (!bl) {
				world.method_8652(blockPos, blockState.method_11657(field_10911, Boolean.valueOf(true)), 2);
				if (!bl2) {
					world.method_8397().method_8675(blockPos, this, this.method_9992(blockState), TaskPriority.field_9310);
				}
			}
		}
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11597(blockView, blockPos, direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.method_11654(field_10911)) {
			return 0;
		} else {
			return blockState.method_11654(field_11177) == direction ? this.method_9993(blockView, blockPos, blockState) : 0;
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (blockState.method_11591(world, blockPos)) {
			this.method_9998(world, blockPos, blockState);
		} else {
			BlockEntity blockEntity = this.hasBlockEntity() ? world.method_8321(blockPos) : null;
			method_9610(blockState, world, blockPos, blockEntity);
			world.method_8650(blockPos);

			for (Direction direction : Direction.values()) {
				world.method_8452(blockPos.method_10093(direction), this);
			}
		}
	}

	protected void method_9998(World world, BlockPos blockPos, BlockState blockState) {
		if (!this.method_9996(world, blockPos, blockState)) {
			boolean bl = (Boolean)blockState.method_11654(field_10911);
			boolean bl2 = this.method_9990(world, blockPos, blockState);
			if (bl != bl2 && !world.method_8397().method_8677(blockPos, this)) {
				TaskPriority taskPriority = TaskPriority.field_9310;
				if (this.method_9988(world, blockPos, blockState)) {
					taskPriority = TaskPriority.field_9315;
				} else if (bl) {
					taskPriority = TaskPriority.field_9313;
				}

				world.method_8397().method_8675(blockPos, this, this.method_9992(blockState), taskPriority);
			}
		}
	}

	public boolean method_9996(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		return false;
	}

	protected boolean method_9990(World world, BlockPos blockPos, BlockState blockState) {
		return this.method_9991(world, blockPos, blockState) > 0;
	}

	protected int method_9991(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.method_11654(field_11177);
		BlockPos blockPos2 = blockPos.method_10093(direction);
		int i = world.method_8499(blockPos2, direction);
		if (i >= 15) {
			return i;
		} else {
			BlockState blockState2 = world.method_8320(blockPos2);
			return Math.max(i, blockState2.getBlock() == Blocks.field_10091 ? (Integer)blockState2.method_11654(RedstoneWireBlock.field_11432) : 0);
		}
	}

	protected int method_10000(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.method_11654(field_11177);
		Direction direction2 = direction.rotateYClockwise();
		Direction direction3 = direction.rotateYCounterclockwise();
		return Math.max(
			this.method_9995(viewableWorld, blockPos.method_10093(direction2), direction2),
			this.method_9995(viewableWorld, blockPos.method_10093(direction3), direction3)
		);
	}

	protected int method_9995(ViewableWorld viewableWorld, BlockPos blockPos, Direction direction) {
		BlockState blockState = viewableWorld.method_8320(blockPos);
		Block block = blockState.getBlock();
		if (this.method_9989(blockState)) {
			if (block == Blocks.field_10002) {
				return 15;
			} else {
				return block == Blocks.field_10091 ? (Integer)blockState.method_11654(RedstoneWireBlock.field_11432) : viewableWorld.method_8596(blockPos, direction);
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_11177, itemPlacementContext.method_8042().getOpposite());
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (this.method_9990(world, blockPos, blockState)) {
			world.method_8397().method_8676(blockPos, this, 1);
		}
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		this.method_9997(world, blockPos, blockState);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			super.method_9536(blockState, world, blockPos, blockState2, bl);
			this.method_9997(world, blockPos, blockState);
		}
	}

	protected void method_9997(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.method_11654(field_11177);
		BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
		world.method_8492(blockPos2, this, blockPos);
		world.method_8508(blockPos2, this, direction);
	}

	protected boolean method_9989(BlockState blockState) {
		return blockState.emitsRedstonePower();
	}

	protected int method_9993(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return 15;
	}

	public static boolean method_9999(BlockState blockState) {
		return blockState.getBlock() instanceof AbstractRedstoneGateBlock;
	}

	public boolean method_9988(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		Direction direction = ((Direction)blockState.method_11654(field_11177)).getOpposite();
		BlockState blockState2 = blockView.method_8320(blockPos.method_10093(direction));
		return method_9999(blockState2) && blockState2.method_11654(field_11177) != direction;
	}

	protected abstract int method_9992(BlockState blockState);

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return true;
	}
}
