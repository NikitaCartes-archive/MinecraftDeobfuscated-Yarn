package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class WalkHomeTask extends Task<LivingEntity> {
	private final float field_20290;
	private final Long2LongMap field_20291 = new Long2LongOpenHashMap();
	private int field_20292;
	private long lastRunTime;

	public WalkHomeTask(float speed) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT));
		this.field_20290 = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
		if (world.getTime() - this.lastRunTime < 20L) {
			return false;
		} else {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)entity;
			PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
			Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(
				PointOfInterestType.HOME.getCompletionCondition(), blockPos -> true, new BlockPos(entity), 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			return optional.isPresent() && !(((BlockPos)optional.get()).getSquaredDistance(new BlockPos(mobEntityWithAi)) <= 4.0);
		}
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		this.field_20292 = 0;
		this.lastRunTime = world.getTime() + (long)world.getRandom().nextInt(20);
		MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)entity;
		PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPosx -> {
			long l = blockPosx.asLong();
			if (this.field_20291.containsKey(l)) {
				return false;
			} else if (++this.field_20292 >= 5) {
				return false;
			} else {
				this.field_20291.put(l, this.lastRunTime + 40L);
				return true;
			}
		};
		Stream<BlockPos> stream = pointOfInterestStorage.getPositions(
			PointOfInterestType.HOME.getCompletionCondition(), predicate, new BlockPos(entity), 48, PointOfInterestStorage.OccupationStatus.ANY
		);
		Path path = mobEntityWithAi.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.method_21648());
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos);
			if (optional.isPresent()) {
				entity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, this.field_20290, 1));
				DebugRendererInfoManager.sendPointOfInterest(world, blockPos);
			}
		} else if (this.field_20292 < 5) {
			this.field_20291.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastRunTime);
		}
	}
}
