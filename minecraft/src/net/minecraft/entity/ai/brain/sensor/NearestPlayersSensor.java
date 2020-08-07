package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_18443, MemoryModuleType.field_18444, MemoryModuleType.field_22354);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		List<PlayerEntity> list = (List<PlayerEntity>)world.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(serverPlayerEntity -> entity.isInRange(serverPlayerEntity, 16.0))
			.sorted(Comparator.comparingDouble(entity::squaredDistanceTo))
			.collect(Collectors.toList());
		Brain<?> brain = entity.getBrain();
		brain.remember(MemoryModuleType.field_18443, list);
		List<PlayerEntity> list2 = (List<PlayerEntity>)list.stream().filter(playerEntity -> method_30954(entity, playerEntity)).collect(Collectors.toList());
		brain.remember(MemoryModuleType.field_18444, list2.isEmpty() ? null : (PlayerEntity)list2.get(0));
		Optional<PlayerEntity> optional = list2.stream().filter(EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL).findFirst();
		brain.remember(MemoryModuleType.field_22354, optional);
	}
}
