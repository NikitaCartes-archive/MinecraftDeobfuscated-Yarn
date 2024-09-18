package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CoralFanBlock extends DeadCoralFanBlock {
	public static final MapCodec<CoralFanBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(CoralBlockBlock.DEAD_FIELD.forGetter(block -> block.deadCoralBlock), createSettingsCodec()).apply(instance, CoralFanBlock::new)
	);
	private final Block deadCoralBlock;

	@Override
	public MapCodec<CoralFanBlock> getCodec() {
		return CODEC;
	}

	protected CoralFanBlock(Block deadCoralBlock, AbstractBlock.Settings settings) {
		super(settings);
		this.deadCoralBlock = deadCoralBlock;
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		this.checkLivingConditions(state, world, world, world.random, pos);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!isInWater(state, world, pos)) {
			world.setBlockState(pos, this.deadCoralBlock.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			this.checkLivingConditions(state, world, tickView, random, pos);
			if ((Boolean)state.get(WATERLOGGED)) {
				tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
		}
	}
}
