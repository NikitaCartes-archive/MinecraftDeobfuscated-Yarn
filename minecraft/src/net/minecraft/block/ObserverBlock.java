package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ObserverBlock extends FacingBlock {
	public static final BooleanProperty field_11322 = Properties.field_12484;

	public ObserverBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, Direction.SOUTH).method_11657(field_11322, Boolean.valueOf(false)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927, field_11322);
	}

	@Override
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_10927, rotation.method_10503(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_10927)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.method_11654(field_11322)) {
			world.method_8652(blockPos, blockState.method_11657(field_11322, Boolean.valueOf(false)), 2);
		} else {
			world.method_8652(blockPos, blockState.method_11657(field_11322, Boolean.valueOf(true)), 2);
			world.method_8397().method_8676(blockPos, this, 2);
		}

		this.method_10365(world, blockPos, blockState);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (blockState.method_11654(field_10927) == direction && !(Boolean)blockState.method_11654(field_11322)) {
			this.method_10366(iWorld, blockPos);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	private void method_10366(IWorld iWorld, BlockPos blockPos) {
		if (!iWorld.isClient() && !iWorld.method_8397().method_8674(blockPos, this)) {
			iWorld.method_8397().method_8676(blockPos, this, 2);
		}
	}

	protected void method_10365(World world, BlockPos blockPos, BlockState blockState) {
		Direction direction = blockState.method_11654(field_10927);
		BlockPos blockPos2 = blockPos.method_10093(direction.getOpposite());
		world.method_8492(blockPos2, this, blockPos);
		world.method_8508(blockPos2, this, direction);
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11597(blockView, blockPos, direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11322) && blockState.method_11654(field_10927) == direction ? 15 : 0;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient() && (Boolean)blockState.method_11654(field_11322) && !world.method_8397().method_8674(blockPos, this)) {
				BlockState blockState3 = blockState.method_11657(field_11322, Boolean.valueOf(false));
				world.method_8652(blockPos, blockState3, 18);
				this.method_10365(world, blockPos, blockState3);
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if (!world.isClient && (Boolean)blockState.method_11654(field_11322) && world.method_8397().method_8674(blockPos, this)) {
				this.method_10365(world, blockPos, blockState.method_11657(field_11322, Boolean.valueOf(false)));
			}
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10927, itemPlacementContext.method_7715().getOpposite().getOpposite());
	}
}
