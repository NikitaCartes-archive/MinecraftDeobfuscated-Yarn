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
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18451, MemoryModuleType.field_18452);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		DamageSource damageSource = entity.getRecentDamageSource();
		if (damageSource != null) {
			brain.remember(MemoryModuleType.field_18451, entity.getRecentDamageSource());
			Entity entity2 = damageSource.getAttacker();
			if (entity2 instanceof LivingEntity) {
				brain.remember(MemoryModuleType.field_18452, (LivingEntity)entity2);
			}
		} else {
			brain.forget(MemoryModuleType.field_18451);
		}

		brain.getOptionalMemory(MemoryModuleType.field_18452).ifPresent(livingEntity -> {
			if (!livingEntity.isAlive() || livingEntity.world != world) {
				brain.forget(MemoryModuleType.field_18452);
			}
		});
	}
}
