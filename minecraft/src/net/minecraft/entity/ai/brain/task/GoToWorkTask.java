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
		super(ImmutableMap.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456));
	}

	protected boolean method_18987(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return villagerEntity.getVillagerData().getProfession() == VillagerProfession.field_17051;
	}

	protected void method_18988(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18439).get();
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
