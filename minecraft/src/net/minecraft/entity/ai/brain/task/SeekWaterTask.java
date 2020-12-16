package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class SeekWaterTask extends Task<PathAwareEntity> {
	private final int range;
	private final float speed;

	public SeekWaterTask(int range, float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED
			)
		);
		this.range = range;
		this.speed = speed;
	}

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		return !pathAwareEntity.world.getFluidState(pathAwareEntity.getBlockPos()).isIn(FluidTags.WATER);
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.iterateOutwards(pathAwareEntity.getBlockPos(), this.range, this.range, this.range)) {
			if (pathAwareEntity.world.getFluidState(blockPos2).isIn(FluidTags.WATER)) {
				blockPos = blockPos2.toImmutable();
				break;
			}
		}

		if (blockPos != null) {
			LookTargetUtil.walkTowards(pathAwareEntity, blockPos, this.speed, 0);
		}
	}
}
