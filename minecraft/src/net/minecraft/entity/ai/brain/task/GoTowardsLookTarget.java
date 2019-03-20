package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;

public class GoTowardsLookTarget extends Task<LivingEntity> {
	private final float field_18378;

	public GoTowardsLookTarget(float f) {
		this.field_18378 = f;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457), Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18456)
		);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		LookTarget lookTarget = (LookTarget)brain.getMemory(MemoryModuleType.field_18446).get();
		brain.putMemory(MemoryModuleType.field_18445, new WalkTarget(lookTarget, this.field_18378, 0));
	}
}
