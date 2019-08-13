package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class NearestPlayersSensor extends Sensor<LivingEntity> {
	@Override
	protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		List<PlayerEntity> list = (List<PlayerEntity>)serverWorld.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(serverPlayerEntity -> livingEntity.squaredDistanceTo(serverPlayerEntity) < 256.0)
			.sorted(Comparator.comparingDouble(livingEntity::squaredDistanceTo))
			.collect(Collectors.toList());
		Brain<?> brain = livingEntity.getBrain();
		brain.putMemory(MemoryModuleType.field_18443, list);
		brain.setMemory(MemoryModuleType.field_18444, list.stream().filter(livingEntity::canSee).findFirst());
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18443, MemoryModuleType.field_18444);
	}
}
