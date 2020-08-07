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
import net.minecraft.util.math.MathHelper;

public class VillagerHostilesSensor extends Sensor<LivingEntity> {
	private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder()
		.put(EntityType.field_6123, 8.0F)
		.put(EntityType.field_6090, 12.0F)
		.put(EntityType.field_6071, 8.0F)
		.put(EntityType.field_6065, 12.0F)
		.put(EntityType.field_6105, 15.0F)
		.put(EntityType.field_6134, 12.0F)
		.put(EntityType.field_6059, 8.0F)
		.put(EntityType.field_6117, 10.0F)
		.put(EntityType.field_23696, 10.0F)
		.put(EntityType.field_6051, 8.0F)
		.put(EntityType.field_6054, 8.0F)
		.build();

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18453);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.field_18453, this.getNearestHostile(entity));
	}

	private Optional<LivingEntity> getNearestHostile(LivingEntity entity) {
		return this.getVisibleMobs(entity)
			.flatMap(
				list -> list.stream()
						.filter(this::isHostile)
						.filter(livingEntity2 -> this.isCloseEnoughForDanger(entity, livingEntity2))
						.min((livingEntity2, livingEntity3) -> this.compareDistances(entity, livingEntity2, livingEntity3))
			);
	}

	private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity entity) {
		return entity.getBrain().getOptionalMemory(MemoryModuleType.field_18442);
	}

	private int compareDistances(LivingEntity entity, LivingEntity hostile1, LivingEntity hostile2) {
		return MathHelper.floor(hostile1.squaredDistanceTo(entity) - hostile2.squaredDistanceTo(entity));
	}

	private boolean isCloseEnoughForDanger(LivingEntity entity, LivingEntity hostile) {
		float f = SQUARED_DISTANCES_FOR_DANGER.get(hostile.getType());
		return hostile.squaredDistanceTo(entity) <= (double)(f * f);
	}

	private boolean isHostile(LivingEntity entity) {
		return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
	}
}
