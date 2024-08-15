package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class NearestLivingEntitiesSensor<T extends LivingEntity> extends Sensor<T> {
	@Override
	protected void sense(ServerWorld world, T entity) {
		double d = entity.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
		Box box = entity.getBoundingBox().expand(d, d, d);
		List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, box, e -> e != entity && e.isAlive());
		list.sort(Comparator.comparingDouble(entity::squaredDistanceTo));
		Brain<?> brain = entity.getBrain();
		brain.remember(MemoryModuleType.MOBS, list);
		brain.remember(MemoryModuleType.VISIBLE_MOBS, new LivingTargetCache(entity, list));
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS);
	}
}
