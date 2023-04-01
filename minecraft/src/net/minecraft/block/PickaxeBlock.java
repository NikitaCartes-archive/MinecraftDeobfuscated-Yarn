package net.minecraft.block;

import net.minecraft.class_8293;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.tick.TickPriority;

public class PickaxeBlock extends WorldModifyingBlock {
	protected PickaxeBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected TickPriority getTickPriority() {
		return TickPriority.EXTREMELY_HIGH;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (class_8293.field_43550.method_50116()) {
			BlockPos blockPos = pos.offset(state.get(FACING));
			if (!world.getBlockState(blockPos).isAir()) {
				world.breakBlock(blockPos, true);
			}
		}
	}
}
