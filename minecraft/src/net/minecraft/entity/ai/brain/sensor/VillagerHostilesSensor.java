package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
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
	public void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> SQUARED_DISTANCES_FOR_DANGER.containsKey(livingEntityxx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)SQUARED_DISTANCES_FOR_DANGER.get(livingEntity2.getType()).floatValue())
						.findFirst()
						.ifPresent(livingEntityxx -> brain.putMemory(MemoryModuleType.field_18453, livingEntityxx))
			);
	}

	@Override
	protected Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18453);
	}
}
