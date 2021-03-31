package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
	protected abstract boolean matches(LivingEntity entity, LivingEntity target);

	protected abstract MemoryModuleType<LivingEntity> getOutputMemoryModule();

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(this.getOutputMemoryModule());
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(this.getOutputMemoryModule(), this.getNearestVisibleLivingEntity(entity));
	}

	private Optional<LivingEntity> getNearestVisibleLivingEntity(LivingEntity entity) {
		return this.getVisibleLivingEntities(entity)
			.flatMap(list -> list.stream().filter(livingEntity2 -> this.matches(entity, livingEntity2)).min(Comparator.comparingDouble(entity::squaredDistanceTo)));
	}

	protected Optional<List<LivingEntity>> getVisibleLivingEntities(LivingEntity entity) {
		return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
	}
}
