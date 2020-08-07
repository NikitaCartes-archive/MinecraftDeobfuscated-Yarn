package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class AdmireItemTask<E extends PiglinEntity> extends Task<E> {
	private final int duration;

	public AdmireItemTask(int duration) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_22332,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22334,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_22473,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_25814,
				MemoryModuleState.field_18457
			)
		);
		this.duration = duration;
	}

	protected boolean method_24609(ServerWorld serverWorld, E piglinEntity) {
		ItemEntity itemEntity = (ItemEntity)piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22332).get();
		return PiglinBrain.isGoldenItem(itemEntity.getStack().getItem());
	}

	protected void method_24610(ServerWorld serverWorld, E piglinEntity, long l) {
		piglinEntity.getBrain().remember(MemoryModuleType.field_22334, true, (long)this.duration);
	}
}
