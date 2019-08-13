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
		super(ImmutableMap.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456, MemoryModuleType.field_18446, MemoryModuleState.field_18458));
	}

	protected boolean method_21641(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (serverWorld.getTime() - this.lastCheckedTime < 300L) {
			return false;
		} else if (serverWorld.random.nextInt(2) != 0) {
			return false;
		} else {
			this.lastCheckedTime = serverWorld.getTime();
			GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18439).get();
			return Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType()) && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
		}
	}

	protected void method_21642(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		brain.putMemory(MemoryModuleType.field_19386, Timestamp.of(l));
		brain.getOptionalMemory(MemoryModuleType.field_18439)
			.ifPresent(globalPos -> brain.putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(globalPos.getPos())));
		villagerEntity.playWorkSound();
		if (villagerEntity.shouldRestock()) {
			villagerEntity.restock();
		}
	}
}
