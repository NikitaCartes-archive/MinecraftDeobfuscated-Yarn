/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Optional;
import java.util.Random;
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
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class FindPointOfInterestTask
extends Task<PathAwareEntity> {
    private final PointOfInterestType poiType;
    private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
    private final boolean onlyRunIfChild;
    private final Optional<Byte> field_25812;
    private long positionExpireTimeLimit;
    private final Long2ObjectMap<RetryMarker> foundPositionsToExpiry = new Long2ObjectOpenHashMap<RetryMarker>();

    public FindPointOfInterestTask(PointOfInterestType poiType, MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2, boolean bl, Optional<Byte> optional) {
        super(FindPointOfInterestTask.method_29245(memoryModuleType, memoryModuleType2));
        this.poiType = poiType;
        this.targetMemoryModuleType = memoryModuleType2;
        this.onlyRunIfChild = bl;
        this.field_25812 = optional;
    }

    public FindPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl, Optional<Byte> optional) {
        this(pointOfInterestType, memoryModuleType, memoryModuleType, bl, optional);
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryModuleState> method_29245(MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2) {
        ImmutableMap.Builder<MemoryModuleType<GlobalPos>, MemoryModuleState> builder = ImmutableMap.builder();
        builder.put(memoryModuleType, MemoryModuleState.VALUE_ABSENT);
        if (memoryModuleType2 != memoryModuleType) {
            builder.put(memoryModuleType2, MemoryModuleState.VALUE_ABSENT);
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
        this.foundPositionsToExpiry.long2ObjectEntrySet().removeIf(entry -> !((RetryMarker)entry.getValue()).method_29927(l));
        Predicate<BlockPos> predicate = blockPos -> {
            RetryMarker retryMarker = (RetryMarker)this.foundPositionsToExpiry.get(blockPos.asLong());
            if (retryMarker == null) {
                return true;
            }
            if (!retryMarker.method_29928(l)) {
                return false;
            }
            retryMarker.method_29926(l);
            return true;
        };
        Set<BlockPos> set = pointOfInterestStorage.getPositions(this.poiType.getCompletionCondition(), predicate, pathAwareEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path path = pathAwareEntity.getNavigation().method_29934(set, this.poiType.getSearchDistance());
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos2 = path.getTarget();
            pointOfInterestStorage.getType(blockPos2).ifPresent(pointOfInterestType -> {
                pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2 -> blockPos2.equals(blockPos2), blockPos2, 1);
                pathAwareEntity.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getRegistryKey(), blockPos2));
                this.field_25812.ifPresent(byte_ -> serverWorld.sendEntityStatus(pathAwareEntity, (byte)byte_));
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
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptAt + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptAt + ", currentDelay=" + this.currentDelay + '}';
        }
    }
}

