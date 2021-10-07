package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_6670;
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
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getVisibleVillagerBabies(entity));
	}

	private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
		return ImmutableList.copyOf(this.getVisibleMobs(entities).method_38978(this::isVillagerBaby));
	}

	private boolean isVillagerBaby(LivingEntity entity) {
		return entity.getType() == EntityType.VILLAGER && entity.isBaby();
	}

	private class_6670 getVisibleMobs(LivingEntity entity) {
		return (class_6670)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(class_6670.method_38971());
	}
}
