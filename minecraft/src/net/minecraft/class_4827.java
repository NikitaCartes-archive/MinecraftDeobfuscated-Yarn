package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4827<E extends class_4836> extends Task<E> {
	private final int field_22328;

	public class_4827(int i) {
		super(
			ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleState.REGISTERED)
		);
		this.field_22328 = i;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E arg) {
		if (!arg.getOffHandStack().isEmpty()) {
			return false;
		} else {
			Optional<ItemEntity> optional = arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
			return !optional.isPresent() ? true : !((ItemEntity)optional.get()).method_24516(arg, (double)this.field_22328);
		}
	}

	protected void run(ServerWorld serverWorld, E arg, long l) {
		arg.getBrain().forget(MemoryModuleType.ADMIRING_ITEM);
	}
}
