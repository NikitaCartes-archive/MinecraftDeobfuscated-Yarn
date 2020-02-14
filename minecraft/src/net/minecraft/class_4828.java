package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class class_4828<E extends MobEntity> extends Task<E> {
	private final Predicate<LivingEntity> field_22329;

	public class_4828(Predicate<LivingEntity> predicate) {
		super(
			ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED)
		);
		this.field_22329 = predicate;
	}

	public class_4828() {
		this(livingEntity -> false);
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		if (method_24621(mobEntity)) {
			this.method_24627(mobEntity);
		} else if (this.method_24626(mobEntity)) {
			this.method_24627(mobEntity);
		} else if (this.method_24622(mobEntity)) {
			this.method_24627(mobEntity);
		} else if (!EntityPredicates.field_22280.test(this.method_24625(mobEntity))) {
			this.method_24627(mobEntity);
		} else if (this.field_22329.test(this.method_24625(mobEntity))) {
			this.method_24627(mobEntity);
		}
	}

	private boolean method_24622(E mobEntity) {
		return this.method_24625(mobEntity).world != mobEntity.world;
	}

	private LivingEntity method_24625(E mobEntity) {
		return (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	private static <E extends LivingEntity> boolean method_24621(E livingEntity) {
		Optional<Long> optional = livingEntity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		return optional.isPresent() && livingEntity.world.getTime() - (Long)optional.get() > 200L;
	}

	private boolean method_24626(E mobEntity) {
		Optional<LivingEntity> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		return optional.isPresent() && !((LivingEntity)optional.get()).isAlive();
	}

	private void method_24627(E mobEntity) {
		mobEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
	}
}
