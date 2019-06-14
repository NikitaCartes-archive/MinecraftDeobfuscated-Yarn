package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_4316;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class VillagerWorkTask extends Task<VillagerEntity> {
	private int ticks;
	private boolean field_18403;

	public VillagerWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456, MemoryModuleType.field_18446, MemoryModuleState.field_18458));
	}

	protected boolean method_19037(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.method_19036(serverWorld.getTimeOfDay() % 24000L, villagerEntity.getLastRestock());
	}

	protected void method_19614(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.field_18403 = false;
		this.ticks = 0;
		villagerEntity.getBrain().forget(MemoryModuleType.field_18446);
	}

	protected void method_19039(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		brain.putMemory(MemoryModuleType.field_19386, class_4316.method_20791(l));
		if (!this.field_18403) {
			villagerEntity.restock();
			this.field_18403 = true;
			villagerEntity.playWorkSound();
			brain.getOptionalMemory(MemoryModuleType.field_18439)
				.ifPresent(globalPos -> brain.putMemory(MemoryModuleType.field_18446, new BlockPosLookTarget(globalPos.getPos())));
		}

		this.ticks++;
	}

	protected boolean method_19040(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.field_18439);
		if (!optional.isPresent()) {
			return false;
		} else {
			GlobalPos globalPos = (GlobalPos)optional.get();
			return this.ticks < 100
				&& Objects.equals(globalPos.getDimension(), serverWorld.method_8597().method_12460())
				&& globalPos.getPos().isWithinDistance(villagerEntity.method_19538(), 1.73);
		}
	}

	private boolean method_19036(long l, long m) {
		return m == 0L || l < m || l > m + 3500L;
	}
}
