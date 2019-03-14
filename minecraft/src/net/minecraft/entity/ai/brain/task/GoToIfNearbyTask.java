package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ai.AiUtil;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.Vec3d;

public class GoToIfNearbyTask extends Task<MobEntityWithAi> {
	private final MemoryModuleType<GlobalPos> target;
	private long nextUpdateTime;
	private final int maxDistanceSquared;

	public GoToIfNearbyTask(MemoryModuleType<GlobalPos> memoryModuleType, int i) {
		this.target = memoryModuleType;
		this.maxDistanceSquared = i;
	}

	protected boolean method_18993(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		Optional<GlobalPos> optional = mobEntityWithAi.getBrain().getMemory(this.target);
		return optional.isPresent()
			&& Objects.equals(serverWorld.getDimension().getType(), ((GlobalPos)optional.get()).getDimension())
			&& mobEntityWithAi.squaredDistanceTo(((GlobalPos)optional.get()).getPos()) <= (double)this.maxDistanceSquared;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(Pair.of(MemoryModuleType.field_18445, MemoryModuleState.field_18458), Pair.of(this.target, MemoryModuleState.field_18456));
	}

	protected void method_18994(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		if (l > this.nextUpdateTime) {
			Optional<Vec3d> optional = Optional.ofNullable(AiUtil.method_6378(mobEntityWithAi, 8, 6));
			mobEntityWithAi.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, 0.3F, 1)));
			this.nextUpdateTime = l + 80L;
		}
	}
}
