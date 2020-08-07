package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class HuntHoglinTask<E extends PiglinEntity> extends Task<E> {
	public HuntHoglinTask() {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22339,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22333,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_22336,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_22343,
				MemoryModuleState.field_18458
			)
		);
	}

	protected boolean method_24617(ServerWorld serverWorld, PiglinEntity piglinEntity) {
		return !piglinEntity.isBaby() && !PiglinBrain.haveHuntedHoglinsRecently(piglinEntity);
	}

	protected void method_24618(ServerWorld serverWorld, E piglinEntity, long l) {
		HoglinEntity hoglinEntity = (HoglinEntity)piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22339).get();
		PiglinBrain.becomeAngryWith(piglinEntity, hoglinEntity);
		PiglinBrain.rememberHunting(piglinEntity);
		PiglinBrain.angerAtCloserTargets(piglinEntity, hoglinEntity);
		PiglinBrain.rememberGroupHunting(piglinEntity);
	}
}
