package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class LeapingChargeTask extends Task<MobEntity> {
	public static final int RUN_TIME = 100;
	private final UniformIntProvider cooldownRange;

	public LeapingChargeTask(UniformIntProvider cooldownRange) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_PRESENT), 100);
		this.cooldownRange = cooldownRange;
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return !mobEntity.isOnGround();
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.setNoDrag(true);
		mobEntity.setPose(EntityPose.LONG_JUMPING);
	}

	protected void finishRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		if (mobEntity.isOnGround()) {
			mobEntity.setVelocity(mobEntity.getVelocity().multiply(0.1F));
		}

		mobEntity.setNoDrag(false);
		mobEntity.setPose(EntityPose.STANDING);
		mobEntity.getBrain().forget(MemoryModuleType.LONG_JUMP_MID_JUMP);
		mobEntity.getBrain().remember(MemoryModuleType.LONG_JUMP_COOLING_DOWN, this.cooldownRange.get(serverWorld.random));
	}
}
