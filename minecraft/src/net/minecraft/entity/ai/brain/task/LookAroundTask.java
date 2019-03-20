package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class LookAroundTask extends Task<MobEntity> {
	public LookAroundTask(int i, int j) {
		super(i, j);
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18456));
	}

	protected boolean method_18967(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return mobEntity.getBrain().getMemory(MemoryModuleType.field_18446).filter(lookTarget -> lookTarget.method_18990(mobEntity)).isPresent();
	}

	protected void method_18968(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().forget(MemoryModuleType.field_18446);
	}

	protected void method_18969(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		mobEntity.getBrain().getMemory(MemoryModuleType.field_18446).ifPresent(lookTarget -> mobEntity.getLookControl().method_19615(lookTarget.getPos()));
	}
}
