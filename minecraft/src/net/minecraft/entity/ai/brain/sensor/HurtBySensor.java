package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

public class HurtBySensor extends Sensor<LivingEntity> {
	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		if (entity.getRecentDamageSource() != null) {
			brain.putMemory(MemoryModuleType.HURT_BY, entity.getRecentDamageSource());
			Entity entity2 = ((DamageSource)brain.getOptionalMemory(MemoryModuleType.HURT_BY).get()).getAttacker();
			if (entity2 instanceof LivingEntity) {
				brain.putMemory(MemoryModuleType.HURT_BY_ENTITY, (LivingEntity)entity2);
			}
		} else {
			brain.forget(MemoryModuleType.HURT_BY);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY);
	}
}
