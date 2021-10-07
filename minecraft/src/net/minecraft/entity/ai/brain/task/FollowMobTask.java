package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_6670;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;

public class FollowMobTask extends Task<LivingEntity> {
	private final Predicate<LivingEntity> predicate;
	private final float maxDistanceSquared;
	private Optional<LivingEntity> field_35102 = Optional.empty();

	public FollowMobTask(Tag<EntityType<?>> entityType, float maxDistance) {
		this(livingEntity -> livingEntity.getType().isIn(entityType), maxDistance);
	}

	public FollowMobTask(SpawnGroup group, float maxDistance) {
		this(livingEntity -> group.equals(livingEntity.getType().getSpawnGroup()), maxDistance);
	}

	public FollowMobTask(EntityType<?> entityType, float maxDistance) {
		this(livingEntity -> entityType.equals(livingEntity.getType()), maxDistance);
	}

	public FollowMobTask(float maxDistance) {
		this(livingEntity -> true, maxDistance);
	}

	public FollowMobTask(Predicate<LivingEntity> predicate, float maxDistance) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT));
		this.predicate = predicate;
		this.maxDistanceSquared = maxDistance * maxDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		class_6670 lv = (class_6670)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get();
		this.field_35102 = lv.method_38975(this.predicate.and(livingEntity2 -> livingEntity2.squaredDistanceTo(entity) <= (double)this.maxDistanceSquared));
		return this.field_35102.isPresent();
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget((Entity)this.field_35102.get(), true));
		this.field_35102 = Optional.empty();
	}
}
