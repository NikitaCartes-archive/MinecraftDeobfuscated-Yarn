package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneLampBlock extends Block {
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	public RedstoneLampBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return blockState.get(LIT) ? super.getLuminance(blockState) : 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		super.onBlockAdded(blockState, world, blockPos, blockState2, bl);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(LIT, Boolean.valueOf(itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getBlockPos())));
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			boolean bl2 = (Boolean)blockState.get(LIT);
			if (bl2 != world.isReceivingRedstonePower(blockPos)) {
				if (bl2) {
					world.getBlockTickScheduler().schedule(blockPos, this, 4);
				} else {
					world.setBlockState(blockPos, blockState.cycle(LIT), 2);
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if ((Boolean)blockState.get(LIT) && !serverWorld.isReceivingRedstonePower(blockPos)) {
			serverWorld.setBlockState(blockPos, blockState.cycle(LIT), 2);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return true;
	}
}
