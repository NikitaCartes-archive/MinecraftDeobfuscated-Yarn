package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class FindWalkTarget extends Task<MobEntityWithAi> {
	private final float field_18375;

	public FindWalkTarget(float f) {
		this.field_18375 = f;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457));
	}

	protected void method_18996(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(AiUtil.method_6378(mobEntityWithAi, 10, 7));
		mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.field_18375, 0)));
	}
}
