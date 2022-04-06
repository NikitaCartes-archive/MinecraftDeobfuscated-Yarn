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
		method_43086(wardenEntity, entity -> entity.getType() == EntityType.PLAYER)
			.or(() -> method_43086(wardenEntity, livingEntity -> livingEntity.getType() != EntityType.PLAYER))
			.ifPresentOrElse(
				entity -> wardenEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entity),
				() -> wardenEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE)
			);
	}

	private static Optional<LivingEntity> method_43086(WardenEntity wardenEntity, Predicate<LivingEntity> predicate) {
		return wardenEntity.getBrain()
			.getOptionalMemory(MemoryModuleType.MOBS)
			.stream()
			.flatMap(Collection::stream)
			.filter(wardenEntity::isValidTarget)
			.filter(predicate)
			.findFirst();
	}

	@Override
	protected int method_43081() {
		return 24;
	}
}
