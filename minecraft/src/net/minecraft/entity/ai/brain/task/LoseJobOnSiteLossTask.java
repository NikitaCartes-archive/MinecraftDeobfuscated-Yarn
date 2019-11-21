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
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		VillagerData villagerData = villagerEntity.getVillagerData();
		return villagerData.getProfession() != VillagerProfession.NONE
			&& villagerData.getProfession() != VillagerProfession.NITWIT
			&& villagerEntity.getExperience() == 0
			&& villagerData.getLevel() <= 1;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(VillagerProfession.NONE));
		villagerEntity.reinitializeBrain(serverWorld);
	}
}
