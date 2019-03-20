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
		.put(EntityType.VINDICATOR, 8.0F)
		.put(EntityType.VEX, 8.0F)
		.put(EntityType.PILLAGER, 15.0F)
		.put(EntityType.ILLUSIONER, 12.0F)
		.build();

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18453);
	}

	@Override
	public void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		livingEntity.getBrain().setMemory(MemoryModuleType.field_18453, this.method_19618(livingEntity));
	}

	private Optional<LivingEntity> method_19618(LivingEntity livingEntity) {
		return this.method_19620(livingEntity)
			.flatMap(
				list -> list.stream()
						.filter(this::method_19104)
						.filter(livingEntity2 -> this.method_19105(livingEntity, livingEntity2))
						.min((livingEntity2, livingEntity3) -> this.method_19619(livingEntity, livingEntity2, livingEntity3))
			);
	}

	private Optional<List<LivingEntity>> method_19620(LivingEntity livingEntity) {
		return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18442);
	}

	private int method_19619(LivingEntity livingEntity, LivingEntity livingEntity2, LivingEntity livingEntity3) {
		return (int)livingEntity2.squaredDistanceTo(livingEntity) - (int)livingEntity3.squaredDistanceTo(livingEntity);
	}

	private boolean method_19105(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return livingEntity2.squaredDistanceTo(livingEntity) <= (double)SQUARED_DISTANCES_FOR_DANGER.get(livingEntity2.getType()).floatValue();
	}

	private boolean method_19104(LivingEntity livingEntity) {
		return SQUARED_DISTANCES_FOR_DANGER.containsKey(livingEntity.getType());
	}
}
