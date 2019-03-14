package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;

public class class_4133 extends Task<VillagerEntity> {
	private boolean field_18403;
	private int field_18404;

	protected boolean method_19037(ServerWorld serverWorld, VillagerEntity villagerEntity) {
		return this.method_19036(serverWorld.getTimeOfDay() % 24000L, villagerEntity.getLastRestock());
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18439, MemoryModuleState.field_18456));
	}

	protected void method_19038(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		this.field_18403 = false;
		this.field_18404 = 0;
	}

	protected void method_19039(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		if (!this.field_18403) {
			villagerEntity.method_19182();
			this.field_18403 = true;
			villagerEntity.playWorkSound();
		}

		this.field_18404++;
	}

	protected boolean method_19040(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
		GlobalPos globalPos = (GlobalPos)villagerEntity.getBrain().getMemory(MemoryModuleType.field_18439).get();
		return this.field_18404 < 100
			&& Objects.equals(globalPos.getDimension(), serverWorld.getDimension().getType())
			&& villagerEntity.squaredDistanceTo(globalPos.getPos()) <= 4.0;
	}

	private boolean method_19036(long l, long m) {
		return m == 0L || l < m || l > m + 3500L;
	}
}
