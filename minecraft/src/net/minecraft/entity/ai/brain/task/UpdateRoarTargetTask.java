package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class UpdateRoarTargetTask extends UpdateAttackTargetTask<WardenEntity> {
	public UpdateRoarTargetTask(Predicate<WardenEntity> predicate, Function<WardenEntity, Optional<? extends LivingEntity>> function, int i) {
		super(predicate, function, i);
	}

	protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		LookTargetUtil.lookAt(wardenEntity, (LivingEntity)wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET).get());
	}

	protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		this.runAndForget(serverWorld, wardenEntity, l);
	}

	private void runAndForget(ServerWorld world, WardenEntity entity, long time) {
		super.run(world, entity, time);
		entity.getBrain().forget(MemoryModuleType.ROAR_TARGET);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
		Optional<LivingEntity> optional = wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ROAR_TARGET);
		return optional.filter(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR).isPresent();
	}
}
