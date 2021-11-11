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
		return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		List<PlayerEntity> list = (List<PlayerEntity>)world.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(player -> entity.isInRange(player, 16.0))
			.sorted(Comparator.comparingDouble(entity::squaredDistanceTo))
			.collect(Collectors.toList());
		Brain<?> brain = entity.getBrain();
		brain.remember(MemoryModuleType.NEAREST_PLAYERS, list);
		List<PlayerEntity> list2 = (List<PlayerEntity>)list.stream().filter(player -> testTargetPredicate(entity, player)).collect(Collectors.toList());
		brain.remember(MemoryModuleType.NEAREST_VISIBLE_PLAYER, list2.isEmpty() ? null : (PlayerEntity)list2.get(0));
		Optional<PlayerEntity> optional = list2.stream().filter(player -> testAttackableTargetPredicate(entity, player)).findFirst();
		brain.remember(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, optional);
	}
}
