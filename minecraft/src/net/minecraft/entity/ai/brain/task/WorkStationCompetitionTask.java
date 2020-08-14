package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class WorkStationCompetitionTask extends Task<VillagerEntity> {
	final VillagerProfession profession;

	public WorkStationCompetitionTask(VillagerProfession profession) {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
		this.profession = profession;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
		serverWorld.getPointOfInterestStorage()
			.getType(globalPos.getPos())
			.ifPresent(
				pointOfInterestType -> LookTargetUtil.streamSeenVillagers(
							villagerEntity, villagerEntityxx -> this.isUsingWorkStationAt(globalPos, pointOfInterestType, villagerEntityxx)
						)
						.reduce(villagerEntity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager)
			);
	}

	private static VillagerEntity keepJobSiteForMoreExperiencedVillager(VillagerEntity first, VillagerEntity second) {
		VillagerEntity villagerEntity;
		VillagerEntity villagerEntity2;
		if (first.getExperience() > second.getExperience()) {
			villagerEntity = first;
			villagerEntity2 = second;
		} else {
			villagerEntity = second;
			villagerEntity2 = first;
		}

		villagerEntity2.getBrain().forget(MemoryModuleType.JOB_SITE);
		return villagerEntity;
	}

	private boolean isUsingWorkStationAt(GlobalPos pos, PointOfInterestType poiType, VillagerEntity villager) {
		return this.hasJobSite(villager)
			&& pos.equals(villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get())
			&& this.isCompletedWorkStation(poiType, villager.getVillagerData().getProfession());
	}

	private boolean isCompletedWorkStation(PointOfInterestType poiType, VillagerProfession profession) {
		return profession.getWorkStation().getCompletionCondition().test(poiType);
	}

	private boolean hasJobSite(VillagerEntity villager) {
		return villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent();
	}
}
