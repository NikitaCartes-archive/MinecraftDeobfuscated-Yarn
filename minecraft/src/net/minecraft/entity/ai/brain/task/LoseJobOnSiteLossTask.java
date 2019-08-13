package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class LoseJobOnSiteLossTask extends Task<VillagerEntity> {
	public LoseJobOnSiteLossTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18439, MemoryModuleState.field_18457));
	}

	protected boolean method_20449(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		VillagerData villagerData = villagerEntity.getVillagerData();
		return villagerData.getProfession() != VillagerProfession.field_17051
			&& villagerData.getProfession() != VillagerProfession.field_17062
			&& villagerEntity.getExperience() == 0
			&& villagerData.getLevel() <= 1;
	}

	protected void method_20450(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(VillagerProfession.field_17051));
		villagerEntity.reinitializeBrain(serverWorld);
	}
}
