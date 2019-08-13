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
		.put(EntityType.field_6051, 8.0F)
		.put(EntityType.field_6054, 8.0F)
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
		return MathHelper.floor(livingEntity2.squaredDistanceTo(livingEntity) - livingEntity3.squaredDistanceTo(livingEntity));
	}

	private boolean isCloseEnoughForDanger(LivingEntity livingEntity, LivingEntity livingEntity2) {
		float f = SQUARED_DISTANCES_FOR_DANGER.get(livingEntity2.getType());
		return livingEntity2.squaredDistanceTo(livingEntity) <= (double)(f * f);
	}

	private boolean isHostile(LivingEntity livingEntity) {
		return SQUARED_DISTANCES_FOR_DANGER.containsKey(livingEntity.getType());
	}
}
