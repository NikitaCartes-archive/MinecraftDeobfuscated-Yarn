package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class FollowMobTask extends Task<LivingEntity> {
	private final EntityType<?> mobType;
	private final float maxDistanceSquared;

	public FollowMobTask(EntityType<?> entityType, float f) {
		this.mobType = entityType;
		this.maxDistanceSquared = f * f;
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return ImmutableSet.of(
			Pair.of(MemoryModuleType.field_18446, MemoryModuleState.field_18457), Pair.of(MemoryModuleType.field_18442, MemoryModuleState.field_18456)
		);
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return ((List)livingEntity.getBrain().getMemory(MemoryModuleType.field_18442).get())
			.stream()
			.anyMatch(livingEntityx -> this.mobType.equals(livingEntityx.getType()));
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getMemory(MemoryModuleType.field_18442)
			.ifPresent(
				list -> list.stream()
						.filter(livingEntityxx -> this.mobType.equals(livingEntityxx.getType()))
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.maxDistanceSquared)
						.findFirst()
						.ifPresent(livingEntityxx -> brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntityxx)))
			);
	}
}
