package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask extends Task<MobEntityWithAi> {
	private final PointOfInterestType poiType;
	private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
	private final boolean onlyRunIfChild;
	private long positionExpireTimeLimit;
	private final Long2LongMap foundPositionsToExpiry = new Long2LongOpenHashMap();
	private int tries;

	public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> targetMemoryModule, boolean onlyRunIfChild) {
		super(ImmutableMap.of(targetMemoryModule, MemoryModuleState.VALUE_ABSENT));
		this.poiType = poiType;
		this.targetMemoryModuleType = targetMemoryModule;
		this.onlyRunIfChild = onlyRunIfChild;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		return this.onlyRunIfChild && mobEntityWithAi.isBaby() ? false : serverWorld.getTime() - this.positionExpireTimeLimit >= 20L;
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		this.tries = 0;
		this.positionExpireTimeLimit = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPosx -> {
			long lx = blockPosx.asLong();
			if (this.foundPositionsToExpiry.containsKey(lx)) {
				return false;
			} else if (++this.tries >= 5) {
				return false;
			} else {
				this.foundPositionsToExpiry.put(lx, this.positionExpireTimeLimit + 40L);
				return true;
			}
		};
		Stream<BlockPos> stream = pointOfInterestStorage.getPositions(
			this.poiType.getCompletionCondition(), predicate, mobEntityWithAi.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
		);
		Path path = mobEntityWithAi.getNavigation().findPathToAny(stream, this.poiType.getSearchDistance());
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			pointOfInterestStorage.getType(blockPos).ifPresent(pointOfInterestType -> {
				pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2 -> blockPos2.equals(blockPos), blockPos, 1);
				mobEntityWithAi.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getDimension().getType(), blockPos));
				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			});
		} else if (this.tries < 5) {
			this.foundPositionsToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.positionExpireTimeLimit);
		}
	}
}
