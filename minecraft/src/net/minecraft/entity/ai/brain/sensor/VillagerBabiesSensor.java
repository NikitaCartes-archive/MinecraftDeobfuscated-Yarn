package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class VillagerBabiesSensor extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(entity));
	}

	private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
		return ImmutableList.copyOf(this.getVisibleMobs(entities).iterate(this::isVillagerBaby));
	}

	private boolean isVillagerBaby(LivingEntity entity) {
		return entity.getType() == EntityType.VILLAGER && entity.isBaby();
	}

	private LivingTargetCache getVisibleMobs(LivingEntity entity) {
		return (LivingTargetCache)entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
	}
}
