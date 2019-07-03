/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class FindPointOfInterestTask
extends Task<MobEntityWithAi> {
    private final PointOfInterestType poiType;
    private final MemoryModuleType<GlobalPos> targetMemoryModule;
    private final boolean onlyRunIfChild;
    private long lastRunTime;
    private final Long2LongMap field_19289 = new Long2LongOpenHashMap();
    private int field_19290;

    public FindPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl) {
        super(ImmutableMap.of(memoryModuleType, MemoryModuleState.VALUE_ABSENT));
        this.poiType = pointOfInterestType;
        this.targetMemoryModule = memoryModuleType;
        this.onlyRunIfChild = bl;
    }

    protected boolean method_20816(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        if (this.onlyRunIfChild && mobEntityWithAi.isBaby()) {
            return false;
        }
        return serverWorld.getTime() - this.lastRunTime >= 20L;
    }

    protected void method_20817(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        this.field_19290 = 0;
        this.lastRunTime = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = blockPos -> {
            boolean bl;
            BlockPos[] blockPoss;
            BlockPos blockPos2;
            long l = blockPos.asLong();
            if (this.field_19289.containsKey(l)) {
                return false;
            }
            if (++this.field_19290 >= 5) {
                return false;
            }
            if (this.poiType == PointOfInterestType.MEETING) {
                BlockPos.Mutable mutable = new BlockPos.Mutable((BlockPos)blockPos);
                this.method_20496(serverWorld, mutable);
                mutable.setOffset(Direction.UP);
                blockPos2 = mutable;
            } else {
                blockPos2 = blockPos;
            }
            BlockPos blockPos3 = new BlockPos(mobEntityWithAi);
            for (BlockPos blockPos4 : blockPoss = new BlockPos[]{blockPos3.north(), blockPos3.south(), blockPos3.west(), blockPos3.east(), blockPos3.up().north(), blockPos3.up().south(), blockPos3.up().west(), blockPos3.up().east(), blockPos3.down(), blockPos3.up(2)}) {
                if (!blockPos4.equals(blockPos2)) continue;
                return true;
            }
            Path path = mobEntityWithAi.getNavigation().findPathTo(blockPos2);
            boolean bl2 = bl = path != null && path.method_19313(blockPos2);
            if (!bl) {
                this.field_19289.put(l, this.lastRunTime + 40L);
            }
            return bl;
        };
        Optional<BlockPos> optional = pointOfInterestStorage.getNearestPosition(this.poiType.getCompletionCondition(), predicate, new BlockPos(mobEntityWithAi), 48);
        if (optional.isPresent()) {
            BlockPos blockPos2 = optional.get();
            mobEntityWithAi.getBrain().putMemory(this.targetMemoryModule, GlobalPos.create(serverWorld.getDimension().getType(), blockPos2));
            DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos2);
        } else if (this.field_19290 < 5) {
            this.field_19289.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastRunTime);
        }
    }

    private void method_20496(ServerWorld serverWorld, BlockPos.Mutable mutable) {
        do {
            mutable.setOffset(Direction.DOWN);
        } while (serverWorld.getBlockState(mutable).getCollisionShape(serverWorld, mutable).isEmpty() && mutable.getY() > 0);
    }
}

