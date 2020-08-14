package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class NearestVisibleAdultSensor extends Sensor<PassiveEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
	}

	protected void sense(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		passiveEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> this.findNearestVisibleAdult(passiveEntity, list));
	}

	private void findNearestVisibleAdult(PassiveEntity entity, List<LivingEntity> visibleMobs) {
		Optional<PassiveEntity> optional = visibleMobs.stream()
			.filter(livingEntity -> livingEntity.getType() == entity.getType())
			.map(livingEntity -> (PassiveEntity)livingEntity)
			.filter(passiveEntity -> !passiveEntity.isBaby())
			.findFirst();
		entity.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
	}
}
