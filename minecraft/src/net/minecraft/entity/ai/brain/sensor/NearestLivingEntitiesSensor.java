package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class NearestLivingEntitiesSensor extends Sensor<LivingEntity> {
	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		Box box = entity.getBoundingBox().expand(16.0, 16.0, 16.0);
		List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, box, livingEntity2 -> livingEntity2 != entity && livingEntity2.isAlive());
		list.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
		Brain<?> brain = entity.getBrain();
		brain.remember(MemoryModuleType.MOBS, list);
		brain.remember(
			MemoryModuleType.VISIBLE_MOBS,
			(List<LivingEntity>)list.stream().filter(livingEntity2 -> testTargetPredicate(entity, livingEntity2)).collect(Collectors.toList())
		);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
	}
}
