/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class WalkHomeTask
extends Task<LivingEntity> {
    private final float speed;
    private final Long2LongMap positionToExpiry = new Long2LongOpenHashMap();
    private int tries;
    private long expiryTimeLimit;

    public WalkHomeTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleState.VALUE_ABSENT));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (world.getTime() - this.expiryTimeLimit < 20L) {
            return false;
        }
        PathAwareEntity pathAwareEntity = (PathAwareEntity)entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(PointOfInterestType.HOME.getCompletionCondition(), entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        return optional.isPresent() && !(optional.get().getSquaredDistance(pathAwareEntity.getBlockPos()) <= 4.0);
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        this.tries = 0;
        this.expiryTimeLimit = world.getTime() + (long)world.getRandom().nextInt(20);
        PathAwareEntity pathAwareEntity = (PathAwareEntity)entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = blockPos -> {
            long l = blockPos.asLong();
            if (this.positionToExpiry.containsKey(l)) {
                return false;
            }
            if (++this.tries >= 5) {
                return false;
            }
            this.positionToExpiry.put(l, this.expiryTimeLimit + 40L);
            return true;
        };
        Stream<BlockPos> stream = pointOfInterestStorage.getPositions(PointOfInterestType.HOME.getCompletionCondition(), predicate, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY);
        Path path = pathAwareEntity.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.getSearchDistance());
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos2 = path.getTarget();
            Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos2);
            if (optional.isPresent()) {
                entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos2, this.speed, 1));
                DebugInfoSender.sendPointOfInterest(world, blockPos2);
            }
        } else if (this.tries < 5) {
            this.positionToExpiry.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTimeLimit);
        }
    }
}

