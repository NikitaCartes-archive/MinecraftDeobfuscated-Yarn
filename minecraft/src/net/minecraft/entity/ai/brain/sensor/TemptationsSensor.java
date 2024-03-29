package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TemptationsSensor extends Sensor<PathAwareEntity> {
	public static final int MAX_DISTANCE = 10;
	private static final TargetPredicate TEMPTER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0).ignoreVisibility();
	private final Predicate<ItemStack> predicate;

	public TemptationsSensor(Predicate<ItemStack> predicate) {
		this.predicate = predicate;
	}

	protected void sense(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		Brain<?> brain = pathAwareEntity.getBrain();
		List<PlayerEntity> list = (List)serverWorld.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(player -> TEMPTER_PREDICATE.test(pathAwareEntity, player))
			.filter(player -> pathAwareEntity.isInRange(player, 10.0))
			.filter(this::test)
			.filter(serverPlayerEntity -> !pathAwareEntity.hasPassenger(serverPlayerEntity))
			.sorted(Comparator.comparingDouble(pathAwareEntity::squaredDistanceTo))
			.collect(Collectors.toList());
		if (!list.isEmpty()) {
			PlayerEntity playerEntity = (PlayerEntity)list.get(0);
			brain.remember(MemoryModuleType.TEMPTING_PLAYER, playerEntity);
		} else {
			brain.forget(MemoryModuleType.TEMPTING_PLAYER);
		}
	}

	private boolean test(PlayerEntity player) {
		return this.test(player.getMainHandStack()) || this.test(player.getOffHandStack());
	}

	private boolean test(ItemStack stack) {
		return this.predicate.test(stack);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
	}
}
