package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4815<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> field_22305;
	private final int field_22306;

	public class_4815(int i, boolean bl) {
		this(livingEntity -> true, i, bl);
	}

	public class_4815(Predicate<E> predicate, int i, boolean bl) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				bl ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.field_22305 = predicate;
		this.field_22306 = i;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.field_22305.test(entity) && this.method_24580(entity).method_24516(entity, (double)this.field_22306);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		LookTargetUtil.method_24557(entity, this.method_24580(entity), 0);
	}

	private ItemEntity method_24580(E livingEntity) {
		return (ItemEntity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
	}
}
