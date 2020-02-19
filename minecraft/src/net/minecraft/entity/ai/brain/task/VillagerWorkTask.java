package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Timestamp;

public class VillagerWorkTask extends Task<VillagerEntity> {
	private long lastCheckedTime;

	public VillagerWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
	}

	protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
			return false;
		} else if (serverWorld.random.nextInt(2) != 0) {
			return false;
		} else {
			this.lastCheckedTime = serverWorld.getTime();
			GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).get();
			return Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType()) && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
		}
	}

	protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		brain.remember(MemoryModuleType.LAST_WORKED_AT_POI, Timestamp.of(l));
		brain.getOptionalMemory(MemoryModuleType.JOB_SITE)
			.ifPresent(globalPos -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
		villagerEntity.playWorkSound();
		if (villagerEntity.shouldRestock()) {
			villagerEntity.restock();
		}
	}
}
