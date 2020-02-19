package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAngryAtTargetTask<E extends MobEntity> extends Task<E> {
	public ForgetAngryAtTargetTask() {
		super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_PRESENT));
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		if (this.hasAngryAtTarget(mobEntity)) {
			mobEntity.getBrain().forget(MemoryModuleType.ANGRY_AT);
		}
	}

	private boolean hasAngryAtTarget(E entity) {
		Optional<LivingEntity> optional = LookTargetUtil.getEntity(entity, MemoryModuleType.ANGRY_AT);
		return !optional.isPresent() || !((LivingEntity)optional.get()).isAlive();
	}
}
