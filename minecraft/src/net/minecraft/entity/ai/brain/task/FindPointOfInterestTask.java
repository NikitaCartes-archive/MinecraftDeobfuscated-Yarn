package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask extends Task<MobEntityWithAi> {
	private final PointOfInterestType poiType;
	private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
	private final boolean onlyRunIfChild;
	private long positionExpireTimeLimit;
	private final Long2ObjectMap<FindPointOfInterestTask.RetryMarker> foundPositionsToExpiry = new Long2ObjectOpenHashMap<>();

	public FindPointOfInterestTask(
		PointOfInterestType poiType, MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2, boolean bl
	) {
		super(method_29245(memoryModuleType, memoryModuleType2));
		this.poiType = poiType;
		this.targetMemoryModuleType = memoryModuleType2;
		this.onlyRunIfChild = bl;
	}

	public FindPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl) {
		this(pointOfInterestType, memoryModuleType, memoryModuleType, bl);
	}

	private static ImmutableMap<MemoryModuleType<?>, MemoryModuleState> method_29245(
		MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2
	) {
		Builder<MemoryModuleType<?>, MemoryModuleState> builder = ImmutableMap.builder();
		builder.put(memoryModuleType, MemoryModuleState.VALUE_ABSENT);
		if (memoryModuleType2 != memoryModuleType) {
			builder.put(memoryModuleType2, MemoryModuleState.VALUE_ABSENT);
		}

		return builder.build();
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
		if (this.onlyRunIfChild && mobEntityWithAi.isBaby()) {
			return false;
		} else if (this.positionExpireTimeLimit == 0L) {
			this.positionExpireTimeLimit = mobEntityWithAi.world.getTime() + (long)serverWorld.random.nextInt(20);
			return false;
		} else {
			return serverWorld.getTime() >= this.positionExpireTimeLimit;
		}
	}

	protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
		this.positionExpireTimeLimit = l + 20L + (long)serverWorld.getRandom().nextInt(20);
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		this.foundPositionsToExpiry.long2ObjectEntrySet().removeIf(entry -> !((FindPointOfInterestTask.RetryMarker)entry.getValue()).method_29927(l));
		Predicate<BlockPos> predicate = blockPos -> {
			FindPointOfInterestTask.RetryMarker retryMarker = this.foundPositionsToExpiry.get(blockPos.asLong());
			if (retryMarker == null) {
				return true;
			} else if (!retryMarker.method_29928(l)) {
				return false;
			} else {
				retryMarker.method_29926(l);
				return true;
			}
		};
		Set<BlockPos> set = (Set<BlockPos>)pointOfInterestStorage.getPositions(
				this.poiType.getCompletionCondition(), predicate, mobEntityWithAi.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
			)
			.limit(5L)
			.collect(Collectors.toSet());
		Path path = mobEntityWithAi.getNavigation().method_29934(set, this.poiType.getSearchDistance());
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			pointOfInterestStorage.getType(blockPos).ifPresent(pointOfInterestType -> {
				pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2x -> blockPos2x.equals(blockPos), blockPos, 1);
				mobEntityWithAi.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getRegistryKey(), blockPos));
				this.foundPositionsToExpiry.clear();
				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			});
		} else {
			for (BlockPos blockPos2 : set) {
				this.foundPositionsToExpiry.computeIfAbsent(blockPos2.asLong(), m -> new FindPointOfInterestTask.RetryMarker(mobEntityWithAi.world.random, l));
			}
		}
	}

	static class RetryMarker {
		private final Random random;
		private long previousAttemptAt;
		private long nextScheduledAttemptAt;
		private int currentDelay;

		RetryMarker(Random random, long time) {
			this.random = random;
			this.method_29926(time);
		}

		public void method_29926(long time) {
			this.previousAttemptAt = time;
			int i = this.currentDelay + this.random.nextInt(40) + 40;
			this.currentDelay = Math.min(i, 400);
			this.nextScheduledAttemptAt = time + (long)this.currentDelay;
		}

		public boolean method_29927(long time) {
			return time - this.previousAttemptAt < 400L;
		}

		public boolean method_29928(long time) {
			return time >= this.nextScheduledAttemptAt;
		}

		public String toString() {
			return "RetryMarker{, previousAttemptAt="
				+ this.previousAttemptAt
				+ ", nextScheduledAttemptAt="
				+ this.nextScheduledAttemptAt
				+ ", currentDelay="
				+ this.currentDelay
				+ '}';
		}
	}
}
