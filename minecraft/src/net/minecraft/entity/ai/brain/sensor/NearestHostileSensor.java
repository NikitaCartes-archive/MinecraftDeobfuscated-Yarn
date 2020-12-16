package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class NearestHostileSensor extends Sensor<LivingEntity> {
	protected abstract Optional<LivingEntity> getNearestHostile(LivingEntity entity);

	protected abstract boolean isCloseEnoughForDanger(LivingEntity entity, LivingEntity target);

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(entity));
	}

	protected Optional<List<LivingEntity>> getVisibleMobs(LivingEntity entity) {
		return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
	}
}
