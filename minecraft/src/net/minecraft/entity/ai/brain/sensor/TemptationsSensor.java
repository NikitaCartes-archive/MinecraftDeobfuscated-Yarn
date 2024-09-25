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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class TemptationsSensor extends Sensor<PathAwareEntity> {
	private static final TargetPredicate TEMPTER_PREDICATE = TargetPredicate.createNonAttackable().ignoreVisibility();
	private final Predicate<ItemStack> predicate;

	public TemptationsSensor(Predicate<ItemStack> predicate) {
		this.predicate = predicate;
	}

	protected void sense(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		Brain<?> brain = pathAwareEntity.getBrain();
		TargetPredicate targetPredicate = TEMPTER_PREDICATE.copy()
			.setBaseMaxDistance((double)((float)pathAwareEntity.getAttributeValue(EntityAttributes.TEMPT_RANGE)));
		List<PlayerEntity> list = (List<PlayerEntity>)serverWorld.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(player -> targetPredicate.test(serverWorld, pathAwareEntity, player))
			.filter(this::test)
			.filter(playerx -> !pathAwareEntity.hasPassenger(playerx))
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
