package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty field_11322 = Properties.field_12484;

	public ObserverBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, Direction.field_11035).method_11657(field_11322, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927, field_11322);
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10927, blockRotation.rotate(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_10927)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11322)) {
			world.method_8652(blockPos, blockState.method_11657(field_11322, Boolean.valueOf(false)), 2);
		} else {
			world.method_8652(blockPos, blockState.method_11657(field_11322, Boolean.valueOf(true)), 2);
			world.method_8397().schedule(blockPos, this, 2);
		}

		this.method_10365(world, blockPos, blockState);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (blockState.method_11654(field_10927) == direction && !(Boolean)blockState.method_11654(field_11322)) {
			this.scheduleTick(iWorld, blockPos);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private void scheduleTick(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.isClient() && !iWorld.method_8397().isScheduled(blockPos, this)) {
			iWorld.method_8397().schedule(blockPos, this, 2);
		}
	}

	protected void method_10365(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.method_11654(field_10927);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		world.method_8492(blockPos2, this, blockPos);
		world.method_8508(blockPos2, this, direction);
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.getWeakRedstonePower(blockView, blockPos, direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11322) && blockState.method_11654(field_10927) == direction ? 15 : 0;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient() && (Boolean)blockState.method_11654(field_11322) && !world.method_8397().isScheduled(blockPos, this)) {
				BlockState blockState3 = blockState.method_11657(field_11322, Boolean.valueOf(false));
				world.method_8652(blockPos, blockState3, 18);
				this.method_10365(world, blockPos, blockState3);
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient && (Boolean)blockState.method_11654(field_11322) && world.method_8397().isScheduled(blockPos, this)) {
				this.method_10365(world, blockPos, blockState.method_11657(field_11322, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10927, itemPlacementContext.getPlayerLookDirection().getOpposite().getOpposite());
	}
}
