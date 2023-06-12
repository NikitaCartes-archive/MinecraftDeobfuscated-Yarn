package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class WardenAttackablesSensor extends NearestLivingEntitiesSensor<WardenEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
	}

	protected void sense(ServerWorld serverWorld, WardenEntity wardenEntity) {
		super.sense(serverWorld, wardenEntity);
		findNearestTarget(wardenEntity, entityx -> entityx.getType() == EntityType.PLAYER)
			.or(() -> findNearestTarget(wardenEntity, entityx -> entityx.getType() != EntityType.PLAYER))
			.ifPresentOrElse(
				entityx -> wardenEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entityx),
				() -> wardenEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE)
			);
	}

	private static Optional<LivingEntity> findNearestTarget(WardenEntity warden, Predicate<LivingEntity> targetPredicate) {
		return warden.getBrain()
			.getOptionalRegisteredMemory(MemoryModuleType.MOBS)
			.stream()
			.flatMap(Collection::stream)
			.filter(warden::isValidTarget)
			.filter(targetPredicate)
			.findFirst();
	}

	@Override
	protected int getHorizontalExpansion() {
		return 24;
	}

	@Override
	protected int getHeightExpansion() {
		return 24;
	}
}
