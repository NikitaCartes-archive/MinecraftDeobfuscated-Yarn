package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class VillagerHostilesSensor extends Sensor<LivingEntity> {
	private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder()
		.put(EntityType.ZOMBIE, 8.0F)
		.put(EntityType.EVOKER, 12.0F)
		.put(EntityType.VINDICATOR, 10.0F)
		.put(EntityType.VEX, 8.0F)
		.put(EntityType.PILLAGER, 15.0F)
		.put(EntityType.ILLUSIONER, 12.0F)
		.put(EntityType.RAVAGER, 12.0F)
		.put(EntityType.HUSK, 8.0F)
		.build();

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18453);
	}

	@Override
	protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		livingEntity.getBrain().setMemory(MemoryModuleType.field_18453, this.getNearestHostile(livingEntity));
	}

	private Optional<LivingEntity> getNearestHostile(LivingEntity livingEntity) {
		return this.getVisibleMobs(livingEntity)
			.flatMap(
				list -> list.stream()
						.filter(this::isHostile)
						.filter(livingEntity2 -> this.isCloseEnoughForDanger(livingEntity, livingEntity2))
						.min((livingEntity2, livingEntity3) -> this.compareDistances(livingEntity, livingEntity2, livingEntity3))
			);
	}

	private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity livingEntity) {
		return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18442);
	}

	private int compareDistances(LivingEntity livingEntity, LivingEntity livingEntity2, LivingEntity livingEntity3) {
		return (int)livingEntity2.squaredDistanceTo(livingEntity) - (int)livingEntity3.squaredDistanceTo(livingEntity);
	}

	private boolean isCloseEnoughForDanger(LivingEntity livingEntity, LivingEntity livingEntity2) {
		float f = SQUARED_DISTANCES_FOR_DANGER.get(livingEntity2.getType());
		return livingEntity2.squaredDistanceTo(livingEntity) <= (double)(f * f);
	}

	private boolean isHostile(LivingEntity livingEntity) {
		return SQUARED_DISTANCES_FOR_DANGER.containsKey(livingEntity.getType());
	}
}
