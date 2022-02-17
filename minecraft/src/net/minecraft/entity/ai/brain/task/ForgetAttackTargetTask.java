package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class ForgetAttackTargetTask<E extends MobEntity> extends Task<E> {
	private static final int REMEMBER_TIME = 200;
	private final Predicate<LivingEntity> alternativeCondition;
	private final Consumer<E> forgetCallback;
	private final boolean field_36709;

	public ForgetAttackTargetTask(Predicate<LivingEntity> predicate, Consumer<E> consumer, boolean bl) {
		super(
			ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED)
		);
		this.alternativeCondition = predicate;
		this.forgetCallback = consumer;
		this.field_36709 = bl;
	}

	public ForgetAttackTargetTask(Predicate<LivingEntity> condition, Consumer<E> forgetCallback) {
		this(condition, forgetCallback, true);
	}

	public ForgetAttackTargetTask(Predicate<LivingEntity> alternativeCondition) {
		this(alternativeCondition, mobEntity -> {
		});
	}

	public ForgetAttackTargetTask(Consumer<E> forgetCallback) {
		this(target -> false, forgetCallback);
	}

	public ForgetAttackTargetTask() {
		this(target -> false, mobEntity -> {
		});
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		LivingEntity livingEntity = this.getAttackTarget(mobEntity);
		if (!mobEntity.canTarget(livingEntity)) {
			this.forgetAttackTarget(mobEntity);
		} else if (this.field_36709 && cannotReachTarget(mobEntity)) {
			this.forgetAttackTarget(mobEntity);
		} else if (this.isAttackTargetDead(mobEntity)) {
			this.forgetAttackTarget(mobEntity);
		} else if (this.isAttackTargetInAnotherWorld(mobEntity)) {
			this.forgetAttackTarget(mobEntity);
		} else if (this.alternativeCondition.test(this.getAttackTarget(mobEntity))) {
			this.forgetAttackTarget(mobEntity);
		}
	}

	private boolean isAttackTargetInAnotherWorld(E entity) {
		return this.getAttackTarget(entity).world != entity.world;
	}

	private LivingEntity getAttackTarget(E entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	private static <E extends LivingEntity> boolean cannotReachTarget(E entity) {
		Optional<Long> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
		return optional.isPresent() && entity.world.getTime() - (Long)optional.get() > 200L;
	}

	private boolean isAttackTargetDead(E entity) {
		Optional<LivingEntity> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		return optional.isPresent() && !((LivingEntity)optional.get()).isAlive();
	}

	protected void forgetAttackTarget(E entity) {
		this.forgetCallback.accept(entity);
		entity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
	}
}
