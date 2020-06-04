package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class class_5355<E extends PassiveEntity> extends Task<E> {
	private final IntRange field_25357;
	private final float field_25358;

	public class_5355(IntRange intRange, float f) {
		super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.field_25357 = intRange;
		this.field_25358 = f;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E passiveEntity) {
		if (!passiveEntity.isBaby()) {
			return false;
		} else {
			PassiveEntity passiveEntity2 = this.method_29520(passiveEntity);
			return passiveEntity.isInRange(passiveEntity2, (double)(this.field_25357.method_29493() + 1))
				&& !passiveEntity.isInRange(passiveEntity2, (double)this.field_25357.method_29492());
		}
	}

	protected void run(ServerWorld serverWorld, E passiveEntity, long l) {
		LookTargetUtil.walkTowards(passiveEntity, this.method_29520(passiveEntity), this.field_25358, this.field_25357.method_29492() - 1);
	}

	private PassiveEntity method_29520(E passiveEntity) {
		return (PassiveEntity)passiveEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
	}
}
