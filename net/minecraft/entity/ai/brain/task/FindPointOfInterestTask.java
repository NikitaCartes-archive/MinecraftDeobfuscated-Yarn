/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

public class FindPointOfInterestTask
extends Task<PathAwareEntity> {
    private static final int MAX_POSITIONS_PER_RUN = 5;
    private static final int POSITION_EXPIRE_INTERVAL = 20;
    public static final int POI_SORTING_RADIUS = 48;
    private final Predicate<RegistryEntry<PointOfInterestType>> poiTypePredicate;
    private final MemoryModuleType<GlobalPos> targetMemoryModuleType;
    private final boolean onlyRunIfChild;
    private final Optional<Byte> entityStatus;
    private long positionExpireTimeLimit;
    private final Long2ObjectMap<RetryMarker> foundPositionsToExpiry = new Long2ObjectOpenHashMap<RetryMarker>();

    public FindPointOfInterestTask(Predicate<RegistryEntry<PointOfInterestType>> poiTypePredicate, MemoryModuleType<GlobalPos> moduleType, MemoryModuleType<GlobalPos> targetMemoryModuleType, boolean onlyRunIfChild, Optional<Byte> entityStatus) {
        super(FindPointOfInterestTask.create(moduleType, targetMemoryModuleType));
        this.poiTypePredicate = poiTypePredicate;
        this.targetMemoryModuleType = targetMemoryModuleType;
        this.onlyRunIfChild = onlyRunIfChild;
        this.entityStatus = entityStatus;
    }

    public FindPointOfInterestTask(Predicate<RegistryEntry<PointOfInterestType>> poiTypePredicate, MemoryModuleType<GlobalPos> moduleType, boolean onlyRunIfChild, Optional<Byte> entityStatus) {
        this(poiTypePredicate, moduleType, moduleType, onlyRunIfChild, entityStatus);
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
        Predicate<BlockPos> predicate = pos -> {
            RetryMarker retryMarker = (RetryMarker)this.foundPositionsToExpiry.get(pos.asLong());
            if (retryMarker == null) {
                return true;
            }
            if (!retryMarker.shouldRetry(l)) {
                return false;
            }
            retryMarker.setAttemptTime(l);
            return true;
        };
        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = pointOfInterestStorage.getSortedTypesAndPositions(this.poiTypePredicate, predicate, pathAwareEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path path = FindPointOfInterestTask.findPathToPoi(pathAwareEntity, set);
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos = path.getTarget();
            pointOfInterestStorage.getType(blockPos).ifPresent(registryEntry2 -> {
                pointOfInterestStorage.getPosition(this.poiTypePredicate, (registryEntry, blockPos2) -> blockPos2.equals(blockPos), blockPos, 1);
                pathAwareEntity.getBrain().remember(this.targetMemoryModuleType, GlobalPos.create(serverWorld.getRegistryKey(), blockPos));
                this.entityStatus.ifPresent(byte_ -> serverWorld.sendEntityStatus(pathAwareEntity, (byte)byte_));
                this.foundPositionsToExpiry.clear();
                DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
            });
        } else {
            for (Pair<RegistryEntry<PointOfInterestType>, BlockPos> pair : set) {
                this.foundPositionsToExpiry.computeIfAbsent(pair.getSecond().asLong(), m -> new RetryMarker(pathAwareEntity.world.random, l));
            }
        }
    }

    @Nullable
    public static Path findPathToPoi(MobEntity entity, Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> pois) {
        if (pois.isEmpty()) {
            return null;
        }
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        int i = 1;
        for (Pair<RegistryEntry<PointOfInterestType>, BlockPos> pair : pois) {
            i = Math.max(i, pair.getFirst().value().searchDistance());
            set.add(pair.getSecond());
        }
        return entity.getNavigation().findPathTo(set, i);
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

