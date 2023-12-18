package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;

public class BreezeAttackablesSensor extends NearestLivingEntitiesSensor<BreezeEntity> {
	public static final int RANGE = 24;

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.copyOf(Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
	}

	protected void sense(ServerWorld serverWorld, BreezeEntity breezeEntity) {
		super.sense(serverWorld, breezeEntity);
		breezeEntity.getBrain()
			.getOptionalRegisteredMemory(MemoryModuleType.MOBS)
			.stream()
			.flatMap(Collection::stream)
			.filter(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
			.filter(livingEntity -> Sensor.testAttackableTargetPredicate(breezeEntity, livingEntity))
			.findFirst()
			.ifPresentOrElse(
				livingEntity -> breezeEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, livingEntity),
				() -> breezeEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE)
			);
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
