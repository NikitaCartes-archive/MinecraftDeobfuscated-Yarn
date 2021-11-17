package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class RemoveOffhandItemTask<E extends PiglinEntity> extends Task<E> {
	public RemoveOffhandItemTask() {
		super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_ABSENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
		return !piglinEntity.getOffhandStack().isEmpty() && !piglinEntity.getOffhandStack().isOf(Items.SHIELD);
	}

	protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
		PiglinBrain.consumeOffhandItem(piglinEntity, true);
	}
}
