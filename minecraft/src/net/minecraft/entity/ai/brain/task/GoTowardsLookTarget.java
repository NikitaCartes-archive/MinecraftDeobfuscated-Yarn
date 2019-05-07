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

	public GoTowardsLookTarget(float f, int i) {
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457, MemoryModuleType.field_18446, MemoryModuleState.field_18456));
		this.speed = f;
		this.completionRange = i;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		LookTarget lookTarget = (LookTarget)brain.getOptionalMemory(MemoryModuleType.field_18446).get();
		brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(lookTarget, this.speed, this.completionRange));
	}
}
