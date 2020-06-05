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
		super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_PRESENT));
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		LookTargetUtil.getEntity(mobEntity, MemoryModuleType.ANGRY_AT).ifPresent(livingEntity -> {
			if (livingEntity.isDead() && (livingEntity.getType() != EntityType.PLAYER || serverWorld.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS))) {
				mobEntity.getBrain().forget(MemoryModuleType.ANGRY_AT);
			}
		});
	}
}
