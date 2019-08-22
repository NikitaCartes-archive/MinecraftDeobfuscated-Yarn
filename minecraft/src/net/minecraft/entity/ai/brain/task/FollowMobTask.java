package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class FollowMobTask extends Task<LivingEntity> {
	private final Predicate<LivingEntity> mobType;
	private final float maxDistanceSquared;

	public FollowMobTask(EntityCategory entityCategory, float f) {
		this(livingEntity -> entityCategory.equals(livingEntity.getType().getCategory()), f);
	}

	public FollowMobTask(EntityType<?> entityType, float f) {
		this(livingEntity -> entityType.equals(livingEntity.getType()), f);
	}

	public FollowMobTask(Predicate<LivingEntity> predicate, float f) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
		this.mobType = predicate;
		this.maxDistanceSquared = f * f;
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return ((List)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get()).stream().anyMatch(this.mobType);
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Brain<?> brain = livingEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.ifPresent(
				list -> list.stream()
						.filter(this.mobType)
						.filter(livingEntity2 -> livingEntity2.squaredDistanceTo(livingEntity) <= (double)this.maxDistanceSquared)
						.findFirst()
						.ifPresent(livingEntityxx -> brain.putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntityxx)))
			);
	}
}
