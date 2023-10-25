package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CopperBulbBlock extends Block {
	public static final MapCodec<CopperBulbBlock> CODEC = createCodec(CopperBulbBlock::new);
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty LIT = Properties.LIT;

	@Override
	protected MapCodec<? extends CopperBulbBlock> getCodec() {
		return CODEC;
	}

	public CopperBulbBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (oldState.getBlock() != state.getBlock()) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			world.scheduleBlockTick(pos, this, 1);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl != (Boolean)state.get(POWERED)) {
			BlockState blockState = state;
			if (!(Boolean)state.get(POWERED)) {
				blockState = state.cycle(LIT);
				world.playSound(null, pos, blockState.get(LIT) ? SoundEvents.BLOCK_COPPER_BULB_TURN_ON : SoundEvents.BLOCK_COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS);
			}

			world.setBlockState(pos, blockState.with(POWERED, Boolean.valueOf(bl)), Block.NOTIFY_ALL);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, POWERED);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return world.getBlockState(pos).get(LIT) ? 15 : 0;
	}
}
