package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class WalkToNearestVisibleWantedItemTask<E extends LivingEntity> extends Task<E> {
	private final Predicate<E> startCondition;
	private final int radius;
	private final float field_23131;

	public WalkToNearestVisibleWantedItemTask(float f, boolean bl, int i) {
		this(livingEntity -> true, f, bl, i);
	}

	public WalkToNearestVisibleWantedItemTask(Predicate<E> startCondition, float f, boolean requiresWalkTarget, int i) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.WALK_TARGET,
				requiresWalkTarget ? MemoryModuleState.REGISTERED : MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.startCondition = startCondition;
		this.radius = i;
		this.field_23131 = f;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return this.startCondition.test(entity) && this.getNearestVisibleWantedItem(entity).isInRange(entity, (double)this.radius);
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		LookTargetUtil.walkTowards(entity, this.getNearestVisibleWantedItem(entity), this.field_23131, 0);
	}

	private ItemEntity getNearestVisibleWantedItem(E entity) {
		return (ItemEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
	}
}
