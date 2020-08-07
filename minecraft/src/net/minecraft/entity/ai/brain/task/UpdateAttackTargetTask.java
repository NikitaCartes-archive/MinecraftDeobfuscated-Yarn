package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class UpdateAttackTargetTask<E extends MobEntity> extends Task<E> {
	private final Predicate<E> startCondition;
	private final Function<E, Optional<? extends LivingEntity>> targetGetter;

	public UpdateAttackTargetTask(Predicate<E> startCondition, Function<E, Optional<? extends LivingEntity>> targetGetter) {
		super(ImmutableMap.of(MemoryModuleType.field_22355, MemoryModuleState.field_18457, MemoryModuleType.field_19293, MemoryModuleState.field_18458));
		this.startCondition = startCondition;
		this.targetGetter = targetGetter;
	}

	public UpdateAttackTargetTask(Function<E, Optional<? extends LivingEntity>> targetGetter) {
		this(mobEntity -> true, targetGetter);
	}

	protected boolean method_24613(ServerWorld serverWorld, E mobEntity) {
		if (!this.startCondition.test(mobEntity)) {
			return false;
		} else {
			Optional<? extends LivingEntity> optional = (Optional<? extends LivingEntity>)this.targetGetter.apply(mobEntity);
			return optional.isPresent() && ((LivingEntity)optional.get()).isAlive();
		}
	}

	protected void method_24614(ServerWorld serverWorld, E mobEntity, long l) {
		((Optional)this.targetGetter.apply(mobEntity)).ifPresent(livingEntity -> this.updateAttackTarget(mobEntity, livingEntity));
	}

	private void updateAttackTarget(E entity, LivingEntity target) {
		entity.getBrain().remember(MemoryModuleType.field_22355, target);
		entity.getBrain().forget(MemoryModuleType.field_19293);
	}
}
