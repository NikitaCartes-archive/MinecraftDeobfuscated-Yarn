package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public class VillagerHostilesSensor extends NearestHostileSensor {
	private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.<EntityType<?>, Float>builder()
		.put(EntityType.DROWNED, 8.0F)
		.put(EntityType.EVOKER, 12.0F)
		.put(EntityType.HUSK, 8.0F)
		.put(EntityType.ILLUSIONER, 12.0F)
		.put(EntityType.PILLAGER, 15.0F)
		.put(EntityType.RAVAGER, 12.0F)
		.put(EntityType.VEX, 8.0F)
		.put(EntityType.VINDICATOR, 10.0F)
		.put(EntityType.ZOGLIN, 10.0F)
		.put(EntityType.ZOMBIE, 8.0F)
		.put(EntityType.ZOMBIE_VILLAGER, 8.0F)
		.build();

	@Override
	protected Optional<LivingEntity> getNearestHostile(LivingEntity entity) {
		return this.getVisibleMobs(entity)
			.flatMap(
				list -> list.stream()
						.filter(this::isHostile)
						.filter(livingEntity2 -> this.isCloseEnoughForDanger(entity, livingEntity2))
						.min(Comparator.comparingDouble(entity::squaredDistanceTo))
			);
	}

	@Override
	protected boolean isCloseEnoughForDanger(LivingEntity entity, LivingEntity target) {
		float f = SQUARED_DISTANCES_FOR_DANGER.get(target.getType());
		return target.squaredDistanceTo(entity) <= (double)(f * f);
	}

	private boolean isHostile(LivingEntity entity) {
		return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
	}
}
