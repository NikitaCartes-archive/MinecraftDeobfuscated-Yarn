package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class GoToWorkTask extends Task<VillagerEntity> {
	public GoToWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.field_25160, MemoryModuleState.field_18456));
	}

	protected boolean method_18987(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		BlockPos blockPos = ((GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25160).get()).getPos();
		return blockPos.isWithinDistance(villagerEntity.getPos(), 2.0) || villagerEntity.isNatural();
	}

	protected void method_18988(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_25160).get();
		villagerEntity.getBrain().forget(MemoryModuleType.field_25160);
		villagerEntity.getBrain().remember(MemoryModuleType.field_18439, globalPos);
		serverWorld.sendEntityStatus(villagerEntity, (byte)14);
		if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.field_17051) {
			MinecraftServer minecraftServer = serverWorld.getServer();
			Optional.ofNullable(minecraftServer.getWorld(globalPos.getDimension()))
				.flatMap(serverWorldx -> serverWorldx.getPointOfInterestStorage().getType(globalPos.getPos()))
				.flatMap(
					pointOfInterestType -> Registry.VILLAGER_PROFESSION
							.stream()
							.filter(villagerProfession -> villagerProfession.getWorkStation() == pointOfInterestType)
							.findFirst()
				)
				.ifPresent(villagerProfession -> {
					villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(villagerProfession));
					villagerEntity.reinitializeBrain(serverWorld);
				});
		}
	}
}
