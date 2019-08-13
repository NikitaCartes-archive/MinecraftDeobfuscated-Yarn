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
	protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		if (livingEntity.getRecentDamageSource() != null) {
			brain.putMemory(MemoryModuleType.field_18451, livingEntity.getRecentDamageSource());
			Entity entity = ((DamageSource)brain.getOptionalMemory(MemoryModuleType.field_18451).get()).getAttacker();
			if (entity instanceof LivingEntity) {
				brain.putMemory(MemoryModuleType.field_18452, (LivingEntity)entity);
			}
		} else {
			brain.forget(MemoryModuleType.field_18451);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18451, MemoryModuleType.field_18452);
	}
}
