package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
	protected abstract boolean matches(ServerWorld world, LivingEntity entity, LivingEntity target);

	protected abstract MemoryModuleType<LivingEntity> getOutputMemoryModule();

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(this.getOutputMemoryModule());
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(this.getOutputMemoryModule(), this.getNearestVisibleLivingEntity(world, entity));
	}

	private Optional<LivingEntity> getNearestVisibleLivingEntity(ServerWorld world, LivingEntity entity) {
		return this.getVisibleLivingEntities(entity).flatMap(entities -> entities.findFirst(target -> this.matches(world, entity, target)));
	}

	protected Optional<LivingTargetCache> getVisibleLivingEntities(LivingEntity entity) {
		return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
	}
}
