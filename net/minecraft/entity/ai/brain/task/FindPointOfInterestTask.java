/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.network.DebugRendererInfoManager;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class FindPointOfInterestTask
extends Task<MobEntityWithAi> {
    private final PointOfInterestType poiType;
    private final MemoryModuleType<GlobalPos> field_20287;
    private final boolean onlyRunIfChild;
    private long lastRunTime;
    private final Long2LongMap field_19289 = new Long2LongOpenHashMap();
    private int field_19290;

    public FindPointOfInterestTask(PointOfInterestType pointOfInterestType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl) {
        super(ImmutableMap.of(memoryModuleType, MemoryModuleState.VALUE_ABSENT));
        this.poiType = pointOfInterestType;
        this.field_20287 = memoryModuleType;
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
            long l = blockPos.asLong();
            if (this.field_19289.containsKey(l)) {
                return false;
            }
            if (++this.field_19290 >= 5) {
                return false;
            }
            this.field_19289.put(l, this.lastRunTime + 40L);
            return true;
        };
        Stream<BlockPos> stream = pointOfInterestStorage.method_21647(this.poiType.getCompletionCondition(), predicate, new BlockPos(mobEntityWithAi), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        Path path = mobEntityWithAi.getNavigation().method_21643(stream, this.poiType.method_21648());
        if (path != null && path.method_21655()) {
            BlockPos blockPos2 = path.method_48();
            pointOfInterestStorage.getType(blockPos2).ifPresent(pointOfInterestType -> {
                pointOfInterestStorage.getPosition(this.poiType.getCompletionCondition(), blockPos2 -> blockPos2.equals(blockPos2), blockPos2, 1);
                mobEntityWithAi.getBrain().putMemory(this.field_20287, GlobalPos.create(serverWorld.getDimension().getType(), blockPos2));
                DebugRendererInfoManager.sendPointOfInterest(serverWorld, blockPos2);
            });
        } else if (this.field_19290 < 5) {
            this.field_19289.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastRunTime);
        }
    }
}

