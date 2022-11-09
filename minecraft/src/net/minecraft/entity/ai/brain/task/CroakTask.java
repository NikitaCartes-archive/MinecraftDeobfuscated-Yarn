package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;

public class CroakTask extends MultiTickTask<FrogEntity> {
	private static final int MAX_RUN_TICK = 60;
	private static final int RUN_TIME = 100;
	private int runningTicks;

	public CroakTask() {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), 100);
	}

	protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
		return frogEntity.getPose() == EntityPose.STANDING;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		return this.runningTicks < 60;
	}

	protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		if (!frogEntity.isInsideWaterOrBubbleColumn() && !frogEntity.isInLava()) {
			frogEntity.setPose(EntityPose.CROAKING);
			this.runningTicks = 0;
		}
	}

	protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		frogEntity.setPose(EntityPose.STANDING);
	}

	protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
		this.runningTicks++;
	}
}
