package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;

public class RedstoneLampBlock extends Block {
	public static final MapCodec<RedstoneLampBlock> CODEC = createCodec(RedstoneLampBlock::new);
	public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

	@Override
	public MapCodec<RedstoneLampBlock> getCodec() {
		return CODEC;
	}

	public RedstoneLampBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(LIT, Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (!world.isClient) {
			boolean bl = (Boolean)state.get(LIT);
			if (bl != world.isReceivingRedstonePower(pos)) {
				if (bl) {
					world.scheduleBlockTick(pos, this, 4);
				} else {
					world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
			world.setBlockState(pos, state.cycle(LIT), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
}
