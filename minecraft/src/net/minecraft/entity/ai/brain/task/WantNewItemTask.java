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
		super(
			ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.REGISTERED)
		);
		this.range = range;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
		if (!piglinEntity.getOffHandStack().isEmpty()) {
			return false;
		} else {
			Optional<ItemEntity> optional = piglinEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
			return !optional.isPresent() ? true : !((ItemEntity)optional.get()).isInRange(piglinEntity, (double)this.range);
		}
	}

	protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
		piglinEntity.getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
	}
}
