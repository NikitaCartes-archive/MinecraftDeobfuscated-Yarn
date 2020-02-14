package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4829<E extends MobEntity> extends Task<E> {
	public class_4829() {
		super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_PRESENT));
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		if (this.method_24628(mobEntity)) {
			mobEntity.getBrain().forget(MemoryModuleType.ANGRY_AT);
		}
	}

	private boolean method_24628(E mobEntity) {
		Optional<LivingEntity> optional = LookTargetUtil.method_24560(mobEntity, MemoryModuleType.ANGRY_AT);
		return !optional.isPresent() || !((LivingEntity)optional.get()).isAlive();
	}
}
