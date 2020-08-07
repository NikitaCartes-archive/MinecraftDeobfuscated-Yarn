package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;

public class ForgetAngryAtTargetTask<E extends MobEntity> extends Task<E> {
	public ForgetAngryAtTargetTask() {
		super(ImmutableMap.of(MemoryModuleType.field_22333, MemoryModuleState.field_18456));
	}

	protected void method_24629(ServerWorld serverWorld, E mobEntity, long l) {
		LookTargetUtil.getEntity(mobEntity, MemoryModuleType.field_22333).ifPresent(livingEntity -> {
			if (livingEntity.isDead() && (livingEntity.getType() != EntityType.field_6097 || serverWorld.getGameRules().getBoolean(GameRules.field_25401))) {
				mobEntity.getBrain().forget(MemoryModuleType.field_22333);
			}
		});
	}
}
