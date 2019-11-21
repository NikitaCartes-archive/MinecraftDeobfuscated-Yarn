/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class NearestBedSensor
extends Sensor<MobEntity> {
    private final Long2LongMap field_20295 = new Long2LongOpenHashMap();
    private int field_20296;
    private long field_20297;

    public NearestBedSensor() {
        super(20);
    }

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    @Override
    protected void sense(ServerWorld serverWorld, MobEntity mobEntity) {
        if (!mobEntity.isBaby()) {
            return;
        }
        this.field_20296 = 0;
        this.field_20297 = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = blockPos -> {
            long l = blockPos.asLong();
            if (this.field_20295.containsKey(l)) {
                return false;
            }
            if (++this.field_20296 >= 5) {
                return false;
            }
            this.field_20295.put(l, this.field_20297 + 40L);
            return true;
        };
        Stream<BlockPos> stream = pointOfInterestStorage.getPositions(PointOfInterestType.HOME.getCompletionCondition(), predicate, new BlockPos(mobEntity), 48, PointOfInterestStorage.OccupationStatus.ANY);
        Path path = mobEntity.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.method_21648());
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos2 = path.getTarget();
            Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos2);
            if (optional.isPresent()) {
                mobEntity.getBrain().putMemory(MemoryModuleType.NEAREST_BED, blockPos2);
            }
        } else if (this.field_20296 < 5) {
            this.field_20295.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.field_20297);
        }
    }
}

