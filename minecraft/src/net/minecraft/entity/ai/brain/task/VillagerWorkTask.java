package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;

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
			return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
		}
	}

	protected void method_21642(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		brain.remember(MemoryModuleType.field_19386, l);
		brain.getOptionalMemory(MemoryModuleType.field_18439)
			.ifPresent(globalPos -> brain.remember(MemoryModuleType.field_18446, new BlockPosLookTarget(globalPos.getPos())));
		villagerEntity.playWorkSound();
		this.performAdditionalWork(serverWorld, villagerEntity);
		if (villagerEntity.shouldRestock()) {
			villagerEntity.restock();
		}
	}

	protected void performAdditionalWork(ServerWorld world, VillagerEntity entity) {
	}

	protected boolean method_26336(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18439);
		if (!optional.isPresent()) {
			return false;
		} else {
			GlobalPos globalPos = (GlobalPos)optional.get();
			return globalPos.getDimension() == serverWorld.getRegistryKey() && globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
		}
	}
}
