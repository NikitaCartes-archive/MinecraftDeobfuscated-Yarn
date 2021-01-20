package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask extends Task<PathAwareEntity> {
	private final PointOfInterestType poiType;
	private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
	private final boolean onlyRunIfChild;
	private final Optional<Byte> field_25812;
	private long positionExpireTimeLimit;
	private final Long2ObjectMap<FindPointOfInterestTask.RetryMarker> foundPositionsToExpiry = new Long2ObjectOpenHashMap<>();

	public FindPointOfInterestTask(
		PointOfInterestType poiType,
		MemoryModuleType<GlobalPos> memoryModuleType,
		MemoryModuleType<GlobalPos> targetMemoryModuleType,
		boolean onlyRunIfChild,
		Optional<Byte> optional
	) {
		super(method_29245(memoryModuleType, targetMemoryModuleType));
		this.poiType = poiType;
		this.targetMemoryModuleType = targetMemoryModuleType;
		this.onlyRunIfChild = onlyRunIfChild;
		this.field_25812 = optional;
	}

	public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> memoryModuleType, boolean onlyRunIfChild, Optional<Byte> optional) {
		this(poiType, memoryModuleType, memoryModuleType, onlyRunIfChild, optional);
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

	protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
		if (this.onlyRunIfChild && pathAwareEntity.isBaby()) {
			return false;
		} else if (this.positionExpireTimeLimit == 0L) {
			this.positionExpireTimeLimit = pathAwareEntity.world.getTime() + (long)serverWorld.random.nextInt(20);
			return false;
		} else {
			return serverWorld.getTime() >= this.positionExpireTimeLimit;
		}
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
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
		Set<BlockPos> set = (Set<BlockPos>)pointOfInterestStorage.method_30957(
				this.poiType.getCompletionCondition(), predicate, pathAwareEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
			)
			.limit(5L)
			.collect(Collectors.toSet());
		Path path = pathAwareEntity.getNavigation().method_29934(set, this.poiType.getSearchDistance());
		if (path != null && path.reachesTarget()) {
			BlockPos blockPos = path.getTarget();
			pointOfInterestStorage.getType(blockPos).ifPresent(pointOfInterestType -> {
				pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2x -> blockPos2x.equals(blockPos), blockPos, 1);
				pathAwareEntity.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getRegistryKey(), blockPos));
				this.field_25812.ifPresent(byte_ -> serverWorld.sendEntityStatus(pathAwareEntity, byte_));
				this.foundPositionsToExpiry.clear();
				DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
			});
		} else {
			for (BlockPos blockPos2 : set) {
				this.foundPositionsToExpiry.computeIfAbsent(blockPos2.asLong(), m -> new FindPointOfInterestTask.RetryMarker(pathAwareEntity.world.random, l));
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
