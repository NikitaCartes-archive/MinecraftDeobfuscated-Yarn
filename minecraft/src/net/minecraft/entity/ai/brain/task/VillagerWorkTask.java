package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.Timestamp;

public class VillagerWorkTask extends Task<VillagerEntity> {
	private int ticks;
	private long field_19426;
	private boolean field_18403;

	public VillagerWorkTask() {
		super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
	}

	protected boolean method_19037(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		if (serverWorld.getTime() - this.field_19426 < 40L) {
			return false;
		} else {
			this.field_19426 = serverWorld.getTime();
			return villagerEntity.method_20822();
		}
	}

	protected void method_19614(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.field_18403 = false;
		this.ticks = 0;
		villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
	}

	protected void method_19039(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Brain<VillagerEntity> brain = villagerEntity.getBrain();
		brain.putMemory(MemoryModuleType.LAST_WORKED_AT_POI, Timestamp.of(l));
		if (!this.field_18403) {
			villagerEntity.restock();
			this.field_18403 = true;
			villagerEntity.playWorkSound();
			brain.getOptionalMemory(MemoryModuleType.JOB_SITE)
				.ifPresent(globalPos -> brain.putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(globalPos.getPos())));
		}

		this.ticks++;
	}

	protected boolean method_19040(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
		if (!optional.isPresent()) {
			return false;
		} else {
			GlobalPos globalPos = (GlobalPos)optional.get();
			return this.ticks < 100
				&& Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType())
				&& globalPos.getPos().isWithinDistance(villagerEntity.getPos(), 1.73);
		}
	}
}
