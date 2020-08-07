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
		return ImmutableSet.of(MemoryModuleType.field_19006);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.field_19006, this.getVisibleVillagerBabies(entity));
	}

	private List<LivingEntity> getVisibleVillagerBabies(LivingEntity entities) {
		return (List<LivingEntity>)this.getVisibleMobs(entities).stream().filter(this::isVillagerBaby).collect(Collectors.toList());
	}

	private boolean isVillagerBaby(LivingEntity entity) {
		return entity.getType() == EntityType.field_6077 && entity.isBaby();
	}

	private List<LivingEntity> getVisibleMobs(LivingEntity entity) {
		return (List<LivingEntity>)entity.getBrain().getOptionalMemory(MemoryModuleType.field_18442).orElse(Lists.newArrayList());
	}
}
