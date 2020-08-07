package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class RidingTask<E extends LivingEntity, T extends Entity> extends Task<E> {
	private final int range;
	private final BiPredicate<E, Entity> alternativeRideCondition;

	public RidingTask(int range, BiPredicate<E, Entity> alternativeRideCondition) {
		super(ImmutableMap.of(MemoryModuleType.field_22356, MemoryModuleState.field_18458));
		this.range = range;
		this.alternativeRideCondition = alternativeRideCondition;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		Entity entity2 = entity.getVehicle();
		Entity entity3 = (Entity)entity.getBrain().getOptionalMemory(MemoryModuleType.field_22356).orElse(null);
		if (entity2 == null && entity3 == null) {
			return false;
		} else {
			Entity entity4 = entity2 == null ? entity3 : entity2;
			return !this.canRideTarget(entity, entity4) || this.alternativeRideCondition.test(entity, entity4);
		}
	}

	private boolean canRideTarget(E entity, Entity target) {
		return target.isAlive() && target.isInRange(entity, (double)this.range) && target.world == entity.world;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		entity.stopRiding();
		entity.getBrain().forget(MemoryModuleType.field_22356);
	}
}
