package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4824<E extends MobEntity> extends Task<E> {
	private final Predicate<E> field_22325;
	private final Function<E, Optional<? extends LivingEntity>> field_22326;

	public class_4824(Predicate<E> predicate, Function<E, Optional<? extends LivingEntity>> function) {
		super(
			ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED)
		);
		this.field_22325 = predicate;
		this.field_22326 = function;
	}

	public class_4824(Function<E, Optional<? extends LivingEntity>> function) {
		this(mobEntity -> true, function);
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		if (!this.field_22325.test(mobEntity)) {
			return false;
		} else {
			Optional<? extends LivingEntity> optional = (Optional<? extends LivingEntity>)this.field_22326.apply(mobEntity);
			return optional.isPresent() && ((LivingEntity)optional.get()).isAlive();
		}
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		((Optional)this.field_22326.apply(mobEntity)).ifPresent(livingEntity -> this.method_24612(mobEntity, livingEntity));
	}

	private void method_24612(E mobEntity, LivingEntity livingEntity) {
		mobEntity.getBrain().putMemory(MemoryModuleType.ATTACK_TARGET, livingEntity);
		mobEntity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
	}
}
