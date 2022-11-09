package net.minecraft.entity.ai.brain.task;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class WorkStationCompetitionTask {
	public static Task<VillagerEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(MemoryModuleType.JOB_SITE), context.queryMemoryValue(MemoryModuleType.MOBS))
					.apply(
						context,
						(jobSite, mobs) -> (world, entity, time) -> {
								GlobalPos globalPos = context.getValue(jobSite);
								world.getPointOfInterestStorage()
									.getType(globalPos.getPos())
									.ifPresent(
										poiType -> context.<List>getValue(mobs)
												.stream()
												.filter(mob -> mob instanceof VillagerEntity && mob != entity)
												.map(villager -> (VillagerEntity)villager)
												.filter(LivingEntity::isAlive)
												.filter(villager -> isUsingWorkStationAt(globalPos, poiType, villager))
												.reduce(entity, WorkStationCompetitionTask::keepJobSiteForMoreExperiencedVillager)
									);
								return true;
							}
					)
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

	private static boolean isUsingWorkStationAt(GlobalPos pos, RegistryEntry<PointOfInterestType> poiType, VillagerEntity villager) {
		Optional<GlobalPos> optional = villager.getBrain().getOptionalRegisteredMemory(MemoryModuleType.JOB_SITE);
		return optional.isPresent() && pos.equals(optional.get()) && isCompletedWorkStation(poiType, villager.getVillagerData().getProfession());
	}

	private static boolean isCompletedWorkStation(RegistryEntry<PointOfInterestType> poiType, VillagerProfession profession) {
		return profession.heldWorkstation().test(poiType);
	}
}
