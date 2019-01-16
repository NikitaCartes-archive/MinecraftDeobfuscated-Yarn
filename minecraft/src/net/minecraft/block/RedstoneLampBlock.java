package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneLampBlock extends Block {
	public static final BooleanProperty field_11413 = RedstoneTorchBlock.field_11446;

	public RedstoneLampBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(field_11413, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(field_11413) ? super.getLuminance(blockState) : 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		super.onBlockAdded(blockState, world, blockPos, blockState2);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_11413, Boolean.valueOf(itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getPos())));
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			boolean bl = (Boolean)blockState.get(field_11413);
			if (bl != world.isReceivingRedstonePower(blockPos)) {
				if (bl) {
					world.getBlockTickScheduler().schedule(blockPos, this, 4);
				} else {
					world.setBlockState(blockPos, blockState.method_11572(field_11413), 2);
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if ((Boolean)blockState.get(field_11413) && !world.isReceivingRedstonePower(blockPos)) {
				world.setBlockState(blockPos, blockState.method_11572(field_11413), 2);
			}
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11413);
	}
}
