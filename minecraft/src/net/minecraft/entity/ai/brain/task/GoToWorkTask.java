package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask {
	public static Task<VillagerEntity> create() {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE), context.queryMemoryOptional(MemoryModuleType.JOB_SITE))
					.apply(
						context,
						(potentialJobSite, jobSite) -> (world, entity, time) -> {
								GlobalPos globalPos = context.getValue(potentialJobSite);
								if (!globalPos.getPos().isWithinDistance(entity.getPos(), 2.0) && !entity.isNatural()) {
									return false;
								} else {
									potentialJobSite.forget();
									jobSite.remember(globalPos);
									world.sendEntityStatus(entity, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
									if (entity.getVillagerData().getProfession() != VillagerProfession.NONE) {
										return true;
									} else {
										MinecraftServer minecraftServer = world.getServer();
										Optional.ofNullable(minecraftServer.getWorld(globalPos.getDimension()))
											.flatMap(jobSiteWorld -> jobSiteWorld.getPointOfInterestStorage().getType(globalPos.getPos()))
											.flatMap(poiType -> Registries.VILLAGER_PROFESSION.stream().filter(profession -> profession.heldWorkstation().test(poiType)).findFirst())
											.ifPresent(profession -> {
												entity.setVillagerData(entity.getVillagerData().withProfession(profession));
												entity.reinitializeBrain(world);
											});
										return true;
									}
								}
							}
					)
		);
	}
}
