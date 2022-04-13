/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask
extends Task<PathAwareEntity> {
    private static final int MAX_POSITIONS_PER_RUN = 5;
    private static final int POSITION_EXPIRE_INTERVAL = 20;
    public static final int POI_SORTING_RADIUS = 48;
    private final PointOfInterestType poiType;
    private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
    private final boolean onlyRunIfChild;
    private final Optional<Byte> entityStatus;
    private long positionExpireTimeLimit;
    private final Long2ObjectMap<RetryMarker> foundPositionsToExpiry = new Long2ObjectOpenHashMap<RetryMarker>();

    public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> moduleType, MemoryModuleType<GlobalPos> targetMemoryModuleType, boolean onlyRunIfChild, Optional<Byte> entityStatus) {
        super(FindPointOfInterestTask.create(moduleType, targetMemoryModuleType));
        this.poiType = poiType;
        this.targetMemoryModuleType = targetMemoryModuleType;
        this.onlyRunIfChild = onlyRunIfChild;
        this.entityStatus = entityStatus;
    }

    public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> moduleType, boolean onlyRunIfChild, Optional<Byte> entityStatus) {
        this(poiType, moduleType, moduleType, onlyRunIfChild, entityStatus);
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryModuleState> create(MemoryModuleType<GlobalPos> firstModule, MemoryModuleType<GlobalPos> secondModule) {
        ImmutableMap.Builder<MemoryModuleType<GlobalPos>, MemoryModuleState> builder = ImmutableMap.builder();
        builder.put(firstModule, MemoryModuleState.VALUE_ABSENT);
        if (secondModule != firstModule) {
            builder.put(secondModule, MemoryModuleState.VALUE_ABSENT);
        }
        return builder.build();
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        if (this.onlyRunIfChild && pathAwareEntity.isBaby()) {
            return false;
        }
        if (this.positionExpireTimeLimit == 0L) {
            this.positionExpireTimeLimit = pathAwareEntity.world.getTime() + (long)serverWorld.random.nextInt(20);
            return false;
        }
        return serverWorld.getTime() >= this.positionExpireTimeLimit;
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        this.positionExpireTimeLimit = l + 20L + (long)serverWorld.getRandom().nextInt(20);
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        this.foundPositionsToExpiry.long2ObjectEntrySet().removeIf(entry -> !((RetryMarker)entry.getValue()).isAttempting(l));
        Predicate<BlockPos> predicate = blockPos -> {
            RetryMarker retryMarker = (RetryMarker)this.foundPositionsToExpiry.get(blockPos.asLong());
            if (retryMarker == null) {
                return true;
            }
            if (!retryMarker.shouldRetry(l)) {
                return false;
            }
            retryMarker.setAttemptTime(l);
            return true;
        };
        Set<BlockPos> set = pointOfInterestStorage.getSortedPositions(this.poiType.getCompletionCondition(), predicate, pathAwareEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path path = pathAwareEntity.getNavigation().findPathTo(set, this.poiType.getSearchDistance());
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos2 = path.getTarget();
            pointOfInterestStorage.getType(blockPos2).ifPresent(pointOfInterestType -> {
                pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2 -> blockPos2.equals(blockPos2), blockPos2, 1);
                pathAwareEntity.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getRegistryKey(), blockPos2));
                this.entityStatus.ifPresent(byte_ -> serverWorld.sendEntityStatus(pathAwareEntity, (byte)byte_));
                this.foundPositionsToExpiry.clear();
                DebugInfoSender.sendPointOfInterest(serverWorld, blockPos2);
            });
        } else {
            for (BlockPos blockPos2 : set) {
                this.foundPositionsToExpiry.computeIfAbsent(blockPos2.asLong(), m -> new RetryMarker(pathAwareEntity.world.random, l));
            }
        }
    }

    static class RetryMarker {
        private static final int MIN_DELAY = 40;
        private static final int MAX_EXTRA_DELAY = 80;
        private static final int ATTEMPT_DURATION = 400;
        private final AbstractRandom random;
        private long previousAttemptAt;
        private long nextScheduledAttemptAt;
        private int currentDelay;

        RetryMarker(AbstractRandom random, long time) {
            this.random = random;
            this.setAttemptTime(time);
        }

        public void setAttemptTime(long time) {
            this.previousAttemptAt = time;
            int i = this.currentDelay + this.random.nextInt(40) + 40;
            this.currentDelay = Math.min(i, 400);
            this.nextScheduledAttemptAt = time + (long)this.currentDelay;
        }

        public boolean isAttempting(long time) {
            return time - this.previousAttemptAt < 400L;
        }

        public boolean shouldRetry(long time) {
            return time >= this.nextScheduledAttemptAt;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptAt + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptAt + ", currentDelay=" + this.currentDelay + "}";
        }
    }
}

