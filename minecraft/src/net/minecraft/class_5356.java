package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class class_5356 extends Sensor<PassiveEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.VISIBLE_MOBS);
	}

	protected void sense(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		passiveEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).ifPresent(list -> this.method_29529(passiveEntity, list));
	}

	private void method_29529(PassiveEntity passiveEntity, List<LivingEntity> list) {
		Optional<PassiveEntity> optional = list.stream()
			.filter(livingEntity -> livingEntity.getType() == passiveEntity.getType())
			.map(livingEntity -> (PassiveEntity)livingEntity)
			.filter(passiveEntityx -> !passiveEntityx.isBaby())
			.findFirst();
		passiveEntity.getBrain().remember(MemoryModuleType.NEAREST_VISIBLE_ADULT, optional);
	}
}
