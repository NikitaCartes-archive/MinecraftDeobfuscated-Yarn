/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.SectionDistanceLevelPropagator;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage
extends SerializingRegionBasedStorage<PointOfInterestSet> {
    private final PointOfInterestDistanceTracker pointOfInterestDistanceTracker;
    private final LongSet preloadedChunks = new LongOpenHashSet();

    public PointOfInterestStorage(File file, DataFixer dataFixer, boolean bl) {
        super(file, PointOfInterestSet::createCodec, PointOfInterestSet::new, dataFixer, DataFixTypes.POI_CHUNK, bl);
        this.pointOfInterestDistanceTracker = new PointOfInterestDistanceTracker();
    }

    public void add(BlockPos pos, PointOfInterestType type) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).add(pos, type);
    }

    public void remove(BlockPos pos) {
        ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).remove(pos);
    }

    public long count(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getInCircle(typePredicate, pos, radius, occupationStatus).count();
    }

    public boolean hasTypeAt(PointOfInterestType type, BlockPos pos) {
        Optional<PointOfInterestType> optional = ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).getType(pos);
        return optional.isPresent() && optional.get().equals(type);
    }

    public Stream<PointOfInterest> getInSquare(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        int i = Math.floorDiv(radius, 16) + 1;
        return ChunkPos.stream(new ChunkPos(pos), i).flatMap(chunkPos -> this.getInChunk(typePredicate, (ChunkPos)chunkPos, occupationStatus)).filter(pointOfInterest -> {
            BlockPos blockPos2 = pointOfInterest.getPos();
            return Math.abs(blockPos2.getX() - pos.getX()) <= radius && Math.abs(blockPos2.getZ() - pos.getZ()) <= radius;
        });
    }

    public Stream<PointOfInterest> getInCircle(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        int i = radius * radius;
        return this.getInSquare(typePredicate, pos, radius, occupationStatus).filter(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos) <= (double)i);
    }

    public Stream<PointOfInterest> getInChunk(Predicate<PointOfInterestType> predicate, ChunkPos chunkPos, OccupationStatus occupationStatus) {
        return IntStream.range(0, 16).boxed().map(integer -> this.get(ChunkSectionPos.from(chunkPos, integer).asLong())).filter(Optional::isPresent).flatMap(optional -> ((PointOfInterestSet)optional.get()).get(predicate, occupationStatus));
    }

    public Stream<BlockPos> getPositions(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getInCircle(typePredicate, pos, radius, occupationStatus).map(PointOfInterest::getPos).filter(posPredicate);
    }

    public Stream<BlockPos> method_30957(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, OccupationStatus occupationStatus) {
        return this.getPositions(predicate, predicate2, blockPos, i, occupationStatus).sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(blockPos)));
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
        return this.getPositions(typePredicate, posPredicate, pos, radius, occupationStatus).findFirst();
    }

    public Optional<BlockPos> getNearestPosition(Predicate<PointOfInterestType> typePredicate, BlockPos blockPos, int i, OccupationStatus occupationStatus) {
        return this.getInCircle(typePredicate, blockPos, i, occupationStatus).map(PointOfInterest::getPos).min(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(blockPos)));
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> positionPredicate, BlockPos pos, int radius) {
        return this.getInCircle(typePredicate, pos, radius, OccupationStatus.HAS_SPACE).filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().map(pointOfInterest -> {
            pointOfInterest.reserveTicket();
            return pointOfInterest.getPos();
        });
    }

    public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> positionPredicate, OccupationStatus occupationStatus, BlockPos pos, int radius, Random random) {
        List list = this.getInCircle(typePredicate, pos, radius, occupationStatus).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list.stream().filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().map(PointOfInterest::getPos);
    }

    public boolean releaseTicket(BlockPos pos) {
        return ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong())).releaseTicket(pos);
    }

    public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
        return this.get(ChunkSectionPos.from(pos).asLong()).map(pointOfInterestSet -> pointOfInterestSet.test(pos, predicate)).orElse(false);
    }

    public Optional<PointOfInterestType> getType(BlockPos pos) {
        PointOfInterestSet pointOfInterestSet = (PointOfInterestSet)this.getOrCreate(ChunkSectionPos.from(pos).asLong());
        return pointOfInterestSet.getType(pos);
    }

    public int getDistanceFromNearestOccupied(ChunkSectionPos pos) {
        this.pointOfInterestDistanceTracker.update();
        return this.pointOfInterestDistanceTracker.getLevel(pos.asLong());
    }

    private boolean isOccupied(long pos) {
        Optional optional = this.getIfLoaded(pos);
        if (optional == null) {
            return false;
        }
        return optional.map(pointOfInterestSet -> pointOfInterestSet.get(PointOfInterestType.ALWAYS_TRUE, OccupationStatus.IS_OCCUPIED).count() > 0L).orElse(false);
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        super.tick(shouldKeepTicking);
        this.pointOfInterestDistanceTracker.update();
    }

    @Override
    protected void onUpdate(long pos) {
        super.onUpdate(pos);
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
    }

    @Override
    protected void onLoad(long pos) {
        this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
    }

    public void initForPalette(ChunkPos chunkPos, ChunkSection chunkSection) {
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, chunkSection.getYOffset() >> 4);
        Util.ifPresentOrElse(this.get(chunkSectionPos.asLong()), pointOfInterestSet -> pointOfInterestSet.updatePointsOfInterest(biConsumer -> {
            if (PointOfInterestStorage.shouldScan(chunkSection)) {
                this.scanAndPopulate(chunkSection, chunkSectionPos, (BiConsumer<BlockPos, PointOfInterestType>)biConsumer);
            }
        }), () -> {
            if (PointOfInterestStorage.shouldScan(chunkSection)) {
                PointOfInterestSet pointOfInterestSet = (PointOfInterestSet)this.getOrCreate(chunkSectionPos.asLong());
                this.scanAndPopulate(chunkSection, chunkSectionPos, pointOfInterestSet::add);
            }
        });
    }

    private static boolean shouldScan(ChunkSection chunkSection) {
        return chunkSection.hasAny(PointOfInterestType.REGISTERED_STATES::contains);
    }

    private void scanAndPopulate(ChunkSection chunkSection, ChunkSectionPos chunkSectionPos, BiConsumer<BlockPos, PointOfInterestType> biConsumer) {
        chunkSectionPos.streamBlocks().forEach(blockPos -> {
            BlockState blockState = chunkSection.getBlockState(ChunkSectionPos.getLocalCoord(blockPos.getX()), ChunkSectionPos.getLocalCoord(blockPos.getY()), ChunkSectionPos.getLocalCoord(blockPos.getZ()));
            PointOfInterestType.from(blockState).ifPresent(pointOfInterestType -> biConsumer.accept((BlockPos)blockPos, (PointOfInterestType)pointOfInterestType));
        });
    }

    /**
     * Preloads chunks in a square area with the given radius. Loads the chunks with {@code ChunkStatus.EMPTY}.
     * 
     * @param radius The radius in blocks
     */
    public void preloadChunks(WorldView world, BlockPos pos, int radius) {
        ChunkSectionPos.stream(new ChunkPos(pos), Math.floorDiv(radius, 16)).map(chunkSectionPos -> Pair.of(chunkSectionPos, this.get(chunkSectionPos.asLong()))).filter(pair -> ((Optional)pair.getSecond()).map(PointOfInterestSet::isValid).orElse(false) == false).map(pair -> ((ChunkSectionPos)pair.getFirst()).toChunkPos()).filter(chunkPos -> this.preloadedChunks.add(chunkPos.toLong())).forEach(chunkPos -> world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY));
    }

    final class PointOfInterestDistanceTracker
    extends SectionDistanceLevelPropagator {
        private final Long2ByteMap distances;

        protected PointOfInterestDistanceTracker() {
            super(7, 16, 256);
            this.distances = new Long2ByteOpenHashMap();
            this.distances.defaultReturnValue((byte)7);
        }

        @Override
        protected int getInitialLevel(long id) {
            return PointOfInterestStorage.this.isOccupied(id) ? 0 : 7;
        }

        @Override
        protected int getLevel(long id) {
            return this.distances.get(id);
        }

        @Override
        protected void setLevel(long id, int level) {
            if (level > 6) {
                this.distances.remove(id);
            } else {
                this.distances.put(id, (byte)level);
            }
        }

        public void update() {
            super.applyPendingUpdates(Integer.MAX_VALUE);
        }
    }

    public static enum OccupationStatus {
        HAS_SPACE(PointOfInterest::hasSpace),
        IS_OCCUPIED(PointOfInterest::isOccupied),
        ANY(pointOfInterest -> true);

        private final Predicate<? super PointOfInterest> predicate;

        private OccupationStatus(Predicate<? super PointOfInterest> predicate) {
            this.predicate = predicate;
        }

        public Predicate<? super PointOfInterest> getPredicate() {
            return this.predicate;
        }
    }
}

