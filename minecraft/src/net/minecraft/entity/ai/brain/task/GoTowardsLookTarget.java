package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;

public class GoTowardsLookTarget extends Task<LivingEntity> {
	private final float speed;
	private final int completionRange;

	public GoTowardsLookTarget(float speed, int completionRange) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_PRESENT));
		this.speed = speed;
		this.completionRange = completionRange;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		LookTarget lookTarget = (LookTarget)brain.getOptionalMemory(MemoryModuleType.LOOK_TARGET).get();
		brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lookTarget, this.speed, this.completionRange));
	}
}
