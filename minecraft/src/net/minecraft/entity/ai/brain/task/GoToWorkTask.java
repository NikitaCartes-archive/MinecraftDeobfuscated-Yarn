package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask extends Task<VillagerEntity> {
	public GoToWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return villagerEntity.getVillagerData().getProfession() == VillagerProfession.NONE;
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
		MinecraftServer minecraftServer = serverWorld.getServer();
		minecraftServer.getWorld(globalPos.getDimension())
			.getPointOfInterestStorage()
			.getType(globalPos.getPos())
			.ifPresent(
				pointOfInterestType -> Registry.VILLAGER_PROFESSION
						.stream()
						.filter(villagerProfession -> villagerProfession.getWorkStation() == pointOfInterestType)
						.findFirst()
						.ifPresent(villagerProfession -> {
							villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(villagerProfession));
							villagerEntity.reinitializeBrain(serverWorld);
						})
			);
	}
}
