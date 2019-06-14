package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class TrappedChestBlock extends ChestBlock {
	public TrappedChestBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new TrappedChestBlockEntity();
	}

	@Override
	protected Stat<Identifier> getOpenStat() {
		return Stats.field_15419.getOrCreateStat(Stats.field_15402);
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return MathHelper.clamp(ChestBlockEntity.getPlayersLookingInChestCount(blockView, blockPos), 0, 15);
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.field_11036 ? blockState.getWeakRedstonePower(blockView, blockPos, direction) : 0;
	}
}
