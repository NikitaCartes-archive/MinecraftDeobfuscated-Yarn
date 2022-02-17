package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class WardenAttackablesSensor extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_ATTACKABLE);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain()
			.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.flatMap(
				warden -> warden.findAny(
						target -> WardenEntity.isValidTarget(target) && target.getType() == EntityType.PLAYER,
						target -> WardenEntity.isValidTarget(target) && target.getType() != EntityType.PLAYER
					)
			)
			.ifPresent(warden -> entity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, warden));
	}
}
