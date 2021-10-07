package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_6670;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class NearestVisibleAdultSensor extends Sensor<PassiveEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
	}

	protected void sense(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		passiveEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(arg -> this.findNearestVisibleAdult(passiveEntity, arg));
	}

	private void findNearestVisibleAdult(PassiveEntity entity, class_6670 arg) {
		Optional<PassiveEntity> optional = arg.method_38975(livingEntity -> livingEntity.getType() == entity.getType() && !livingEntity.isBaby())
			.map(PassiveEntity.class::cast);
		entity.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
	}
}
