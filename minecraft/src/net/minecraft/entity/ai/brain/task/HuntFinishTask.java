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
		super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleState.REGISTERED));
	}

	protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
		if (this.hasKilledHoglin(piglinEntity)) {
			PiglinBrain.rememberHunting(piglinEntity);
		}
	}

	private boolean hasKilledHoglin(E piglin) {
		LivingEntity livingEntity = (LivingEntity)piglin.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
		return livingEntity.getType() == EntityType.HOGLIN && livingEntity.isDead();
	}
}
