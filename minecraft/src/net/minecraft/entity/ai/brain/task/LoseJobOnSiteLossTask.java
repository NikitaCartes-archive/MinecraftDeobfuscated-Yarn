package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;

public class LoseJobOnSiteLossTask {
	public static Task<VillagerEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.JOB_SITE))
					.apply(
						context,
						jobSite -> (world, entity, time) -> {
								VillagerData villagerData = entity.getVillagerData();
								if (villagerData.getProfession() != VillagerProfession.NONE
									&& villagerData.getProfession() != VillagerProfession.NITWIT
									&& entity.getExperience() == 0
									&& villagerData.getLevel() <= 1) {
									entity.setVillagerData(entity.getVillagerData().withProfession(VillagerProfession.NONE));
									entity.reinitializeBrain(world);
									return true;
								} else {
									return false;
								}
							}
					)
		);
	}
}
