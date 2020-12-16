package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;

public class AxolotlTemptationsSensor extends Sensor<PathAwareEntity> {
	private static final TargetPredicate field_28330 = new TargetPredicate()
		.setBaseMaxDistance(10.0)
		.includeInvulnerable()
		.includeTeammates()
		.ignoreEntityTargetRules()
		.includeHidden();
	private final Ingredient ingredient;

	public AxolotlTemptationsSensor(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	protected void sense(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		Brain<?> brain = pathAwareEntity.getBrain();
		List<PlayerEntity> list = (List<PlayerEntity>)serverWorld.getPlayers()
			.stream()
			.filter(EntityPredicates.EXCEPT_SPECTATOR)
			.filter(player -> field_28330.test(pathAwareEntity, player))
			.filter(serverPlayerEntity -> pathAwareEntity.isInRange(serverPlayerEntity, 10.0))
			.filter(this::test)
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
		return this.ingredient.test(stack);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
	}
}
