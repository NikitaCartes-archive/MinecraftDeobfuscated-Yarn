/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PointOfInterestSet
implements DynamicSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<PointOfInterest>();
    private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.newHashMap();
    private final Runnable updateListener;
    private boolean valid;

    public PointOfInterestSet(Runnable runnable) {
        this.updateListener = runnable;
        this.valid = true;
    }

    public <T> PointOfInterestSet(Runnable runnable, Dynamic<T> dynamic2) {
        this.updateListener = runnable;
        try {
            this.valid = dynamic2.get("Valid").asBoolean(false);
            dynamic2.get("Records").asStream().forEach(dynamic -> this.add(new PointOfInterest(dynamic, runnable)));
        } catch (Exception exception) {
            LOGGER.error("Failed to load POI chunk", (Throwable)exception);
            this.clear();
            this.valid = false;
        }
    }

    public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
        return this.pointsOfInterestByType.entrySet().stream().filter(entry -> predicate.test((PointOfInterestType)entry.getKey())).flatMap(entry -> ((Set)entry.getValue()).stream()).filter(occupationStatus.getPredicate());
    }

    public void add(BlockPos blockPos, PointOfInterestType pointOfInterestType) {
        if (this.add(new PointOfInterest(blockPos, pointOfInterestType, this.updateListener))) {
            LOGGER.debug("Added POI of type {} @ {}", () -> pointOfInterestType, () -> blockPos);
            this.updateListener.run();
        }
    }

    private boolean add(PointOfInterest pointOfInterest) {
        BlockPos blockPos = pointOfInterest.getPos();
        PointOfInterestType pointOfInterestType2 = pointOfInterest.getType();
        short s = ChunkSectionPos.getPackedLocalPos(blockPos);
        PointOfInterest pointOfInterest2 = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        if (pointOfInterest2 != null) {
            if (pointOfInterestType2.equals(pointOfInterest2.getType())) {
                return false;
            }
            throw Util.throwOrPause(new IllegalStateException("POI data mismatch: already registered at " + blockPos));
        }
        this.pointsOfInterestByPos.put(s, pointOfInterest);
        this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType2, pointOfInterestType -> Sets.newHashSet()).add(pointOfInterest);
        return true;
    }

    public void remove(BlockPos blockPos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.getPackedLocalPos(blockPos));
        if (pointOfInterest == null) {
            LOGGER.error("POI data mismatch: never registered at " + blockPos);
            return;
        }
        this.pointsOfInterestByType.get(pointOfInterest.getType()).remove(pointOfInterest);
        Supplier[] supplierArray = new Supplier[2];
        supplierArray[0] = pointOfInterest::getType;
        supplierArray[1] = pointOfInterest::getPos;
        LOGGER.debug("Removed POI of type {} @ {}", supplierArray);
        this.updateListener.run();
    }

    public boolean releaseTicket(BlockPos blockPos) {
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.getPackedLocalPos(blockPos));
        if (pointOfInterest == null) {
            throw Util.throwOrPause(new IllegalStateException("POI never registered at " + blockPos));
        }
        boolean bl = pointOfInterest.releaseTicket();
        this.updateListener.run();
        return bl;
    }

    public boolean test(BlockPos blockPos, Predicate<PointOfInterestType> predicate) {
        short s = ChunkSectionPos.getPackedLocalPos(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null && predicate.test(pointOfInterest.getType());
    }

    public Optional<PointOfInterestType> getType(BlockPos blockPos) {
        short s = ChunkSectionPos.getPackedLocalPos(blockPos);
        PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
        return pointOfInterest != null ? Optional.of(pointOfInterest.getType()) : Optional.empty();
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createList(this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.serialize(dynamicOps)));
        return (T)dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Records"), object, dynamicOps.createString("Valid"), dynamicOps.createBoolean(this.valid)));
    }

    public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
        if (!this.valid) {
            Short2ObjectOpenHashMap<PointOfInterest> short2ObjectMap = new Short2ObjectOpenHashMap<PointOfInterest>(this.pointsOfInterestByPos);
            this.clear();
            consumer.accept((blockPos, pointOfInterestType) -> {
                short s = ChunkSectionPos.getPackedLocalPos(blockPos);
                PointOfInterest pointOfInterest = short2ObjectMap.computeIfAbsent(s, i -> new PointOfInterest((BlockPos)blockPos, (PointOfInterestType)pointOfInterestType, this.updateListener));
                this.add(pointOfInterest);
            });
            this.valid = true;
            this.updateListener.run();
        }
    }

    private void clear() {
        this.pointsOfInterestByPos.clear();
        this.pointsOfInterestByType.clear();
    }

    boolean method_22444() {
        return this.valid;
    }
}

