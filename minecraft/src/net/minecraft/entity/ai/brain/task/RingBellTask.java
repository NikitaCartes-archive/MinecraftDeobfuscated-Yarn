package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;

public class RingBellTask extends Task<LivingEntity> {
	public RingBellTask() {
		super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT));
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return serverWorld.random.nextFloat() > 0.95F;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		BlockPos blockPos = ((GlobalPos)brain.getOptionalMemory(MemoryModuleType.MEETING_POINT).get()).getPos();
		if (blockPos.isWithinDistance(new BlockPos(livingEntity), 3.0)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.BELL) {
				BellBlock bellBlock = (BellBlock)blockState.getBlock();
				bellBlock.ring(serverWorld, blockPos, null);
			}
		}
	}
}
