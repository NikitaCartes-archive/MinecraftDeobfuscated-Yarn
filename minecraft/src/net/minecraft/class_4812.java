package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class class_4812<E extends LivingEntity, T extends Entity> extends Task<E> {
	private final int field_22300;
	private final BiPredicate<E, Entity> field_22301;

	public class_4812(int i, BiPredicate<E, Entity> biPredicate) {
		super(ImmutableMap.of(MemoryModuleType.RIDE_TARGET, MemoryModuleState.REGISTERED));
		this.field_22300 = i;
		this.field_22301 = biPredicate;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		Entity entity2 = entity.getVehicle();
		Entity entity3 = (Entity)entity.getBrain().getOptionalMemory(MemoryModuleType.RIDE_TARGET).orElse(null);
		if (entity2 == null && entity3 == null) {
			return false;
		} else {
			Entity entity4 = entity2 == null ? entity3 : entity2;
			return !this.method_24575(entity, entity4) || this.field_22301.test(entity, entity4);
		}
	}

	private boolean method_24575(E livingEntity, Entity entity) {
		return entity.isAlive() && entity.method_24516(livingEntity, (double)this.field_22300) && entity.world == livingEntity.world;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		entity.stopRiding();
		entity.getBrain().forget(MemoryModuleType.RIDE_TARGET);
	}
}
