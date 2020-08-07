package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class HuntFinishTask<E extends PiglinEntity> extends Task<E> {
	public HuntFinishTask() {
		super(ImmutableMap.of(MemoryModuleType.field_22355, MemoryModuleState.field_18456, MemoryModuleType.field_22336, MemoryModuleState.field_18458));
	}

	protected void method_24596(ServerWorld serverWorld, E piglinEntity, long l) {
		if (this.hasKilledHoglin(piglinEntity)) {
			PiglinBrain.rememberHunting(piglinEntity);
		}
	}

	private boolean hasKilledHoglin(E piglin) {
		LivingEntity livingEntity = (LivingEntity)piglin.getBrain().getOptionalMemory(MemoryModuleType.field_22355).get();
		return livingEntity.getType() == EntityType.field_21973 && livingEntity.isDead();
	}
}
