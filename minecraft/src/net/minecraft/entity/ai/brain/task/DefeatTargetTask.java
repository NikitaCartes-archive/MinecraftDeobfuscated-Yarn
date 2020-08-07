package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class DefeatTargetTask extends Task<LivingEntity> {
	private final int duration;
	private final BiPredicate<LivingEntity, LivingEntity> field_25157;

	public DefeatTargetTask(int duration, BiPredicate<LivingEntity, LivingEntity> biPredicate) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22333,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_22337,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_25159,
				MemoryModuleState.field_18458
			)
		);
		this.duration = duration;
		this.field_25157 = biPredicate;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		return this.getAttackTarget(entity).isDead();
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		LivingEntity livingEntity = this.getAttackTarget(entity);
		if (this.field_25157.test(entity, livingEntity)) {
			entity.getBrain().remember(MemoryModuleType.field_25159, true, (long)this.duration);
		}

		entity.getBrain().remember(MemoryModuleType.field_22337, livingEntity.getBlockPos(), (long)this.duration);
		if (livingEntity.getType() != EntityType.field_6097 || world.getGameRules().getBoolean(GameRules.field_25401)) {
			entity.getBrain().forget(MemoryModuleType.field_22355);
			entity.getBrain().forget(MemoryModuleType.field_22333);
		}
	}

	private LivingEntity getAttackTarget(LivingEntity entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.field_22355).get();
	}
}
