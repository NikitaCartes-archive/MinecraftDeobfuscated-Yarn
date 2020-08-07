package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;

public class WantNewItemTask<E extends PiglinEntity> extends Task<E> {
	private final int range;

	public WantNewItemTask(int range) {
		super(ImmutableMap.of(MemoryModuleType.field_22334, MemoryModuleState.field_18456, MemoryModuleType.field_22332, MemoryModuleState.field_18458));
		this.range = range;
	}

	protected boolean method_24619(ServerWorld serverWorld, E piglinEntity) {
		if (!piglinEntity.getOffHandStack().isEmpty()) {
			return false;
		} else {
			Optional<ItemEntity> optional = piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22332);
			return !optional.isPresent() ? true : !((ItemEntity)optional.get()).isInRange(piglinEntity, (double)this.range);
		}
	}

	protected void method_24620(ServerWorld serverWorld, E piglinEntity, long l) {
		piglinEntity.getBrain().forget(MemoryModuleType.field_22334);
	}
}
