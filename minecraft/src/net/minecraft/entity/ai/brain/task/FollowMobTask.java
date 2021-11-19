package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;

public class FollowMobTask extends Task<LivingEntity> {
	private final Predicate<LivingEntity> predicate;
	private final float maxDistanceSquared;
	private Optional<LivingEntity> target = Optional.empty();

	public FollowMobTask(Tag<EntityType<?>> entityType, float maxDistance) {
		this(entity -> entity.getType().isIn(entityType), maxDistance);
	}

	public FollowMobTask(SpawnGroup group, float maxDistance) {
		this(entity -> group.equals(entity.getType().getSpawnGroup()), maxDistance);
	}

	public FollowMobTask(EntityType<?> entityType, float maxDistance) {
		this(entity -> entityType.equals(entity.getType()), maxDistance);
	}

	public FollowMobTask(float maxDistance) {
		this(entity -> true, maxDistance);
	}

	public FollowMobTask(Predicate<LivingEntity> predicate, float maxDistance) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
		this.predicate = predicate;
		this.maxDistanceSquared = maxDistance * maxDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		LivingTargetCache livingTargetCache = (LivingTargetCache)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
		this.target = livingTargetCache.findFirst(this.predicate.and(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= (double)this.maxDistanceSquared));
		return this.target.isPresent();
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)this.target.get(), true));
		this.target = Optional.empty();
	}
}
