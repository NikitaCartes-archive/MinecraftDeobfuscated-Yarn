package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class VillagerBabiesSensor extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
	}

	@Override
	protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		livingEntity.getBrain().putMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(livingEntity));
	}

	private List<LivingEntity> getVisibleVillagerBabies(LivingEntity livingEntity) {
		return (List<LivingEntity>)this.getVisibleMobs(livingEntity).stream().filter(this::isVillagerBaby).collect(Collectors.toList());
	}

	private boolean isVillagerBaby(LivingEntity livingEntity) {
		return livingEntity.getType() == EntityType.VILLAGER && livingEntity.isBaby();
	}

	private List<LivingEntity> getVisibleMobs(LivingEntity livingEntity) {
		return (List<LivingEntity>)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
	}
}
