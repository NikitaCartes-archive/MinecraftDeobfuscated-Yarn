package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask extends Task<VillagerEntity> {
	public GoToWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		BlockPos blockPos = ((GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).getPos();
		return blockPos.isWithinDistance(villagerEntity.getPos(), 2.0) || villagerEntity.isNatural();
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
		villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
		villagerEntity.getBrain().remember(MemoryModuleType.JOB_SITE, globalPos);
		serverWorld.sendEntityStatus(villagerEntity, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES);
		if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.NONE) {
			MinecraftServer minecraftServer = serverWorld.getServer();
			Optional.ofNullable(minecraftServer.getWorld(globalPos.getDimension()))
				.flatMap(world -> world.getPointOfInterestStorage().getType(globalPos.getPos()))
				.flatMap(poiType -> Registry.VILLAGER_PROFESSION.stream().filter(profession -> profession.getWorkStation() == poiType).findFirst())
				.ifPresent(profession -> {
					villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(profession));
					villagerEntity.reinitializeBrain(serverWorld);
				});
		}
	}
}
