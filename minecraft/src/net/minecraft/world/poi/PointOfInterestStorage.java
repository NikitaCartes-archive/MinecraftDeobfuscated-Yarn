package net.minecraft.world.poi;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
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
import net.minecraft.util.SectionDistanceLevelPropagator;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage extends SerializingRegionBasedStorage<PointOfInterestSet> {
	private final PointOfInterestStorage.PointOfInterestDistanceTracker pointOfInterestDistanceTracker = new PointOfInterestStorage.PointOfInterestDistanceTracker(
		
	);

	public PointOfInterestStorage(File file, DataFixer dataFixer) {
		super(file, PointOfInterestSet::new, PointOfInterestSet::new, dataFixer, DataFixTypes.POI_CHUNK);
	}

	public void add(BlockPos pos, PointOfInterestType type) {
		this.getOrCreate(ChunkSectionPos.from(pos).asLong()).add(pos, type);
	}

	public void remove(BlockPos pos) {
		this.getOrCreate(ChunkSectionPos.from(pos).asLong()).remove(pos);
	}

	public long count(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return this.get(typePredicate, pos, radius, occupationStatus).count();
	}

	public Stream<PointOfInterest> get(
		Predicate<PointOfInterestType> typePredicate, BlockPos pos, int radius, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		int i = radius * radius;
		return ChunkPos.stream(new ChunkPos(pos), Math.floorDiv(radius, 16))
			.flatMap(
				chunkPos -> this.get(typePredicate, chunkPos, occupationStatus).filter(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(pos) <= (double)i)
			);
	}

	public Stream<PointOfInterest> get(Predicate<PointOfInterestType> typePredicate, ChunkPos pos, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return IntStream.range(0, 16).boxed().flatMap(integer -> this.get(typePredicate, ChunkSectionPos.from(pos, integer).asLong(), occupationStatus));
	}

	private Stream<PointOfInterest> get(Predicate<PointOfInterestType> typePredicate, long pos, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return (Stream<PointOfInterest>)this.get(pos).map(pointOfInterestSet -> pointOfInterestSet.get(typePredicate, occupationStatus)).orElseGet(Stream::empty);
	}

	public Stream<BlockPos> method_21647(
		Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.get(predicate, blockPos, i, occupationStatus).map(PointOfInterest::getPos).filter(predicate2);
	}

	public Optional<BlockPos> getPosition(
		Predicate<PointOfInterestType> typePredicate,
		Predicate<BlockPos> posPredicate,
		BlockPos pos,
		int radius,
		PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.method_21647(typePredicate, posPredicate, pos, radius, occupationStatus).findFirst();
	}

	public Optional<BlockPos> getNearestPosition(
		Predicate<PointOfInterestType> typePredicate,
		Predicate<BlockPos> blockPosPredicate,
		BlockPos pos,
		int radius,
		PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.get(typePredicate, pos, radius, occupationStatus)
			.map(PointOfInterest::getPos)
			.sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(pos)))
			.filter(blockPosPredicate)
			.findFirst();
	}

	public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> positionPredicate, BlockPos pos, int radius) {
		return this.get(typePredicate, pos, radius, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
			.filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos()))
			.findFirst()
			.map(pointOfInterest -> {
				pointOfInterest.reserveTicket();
				return pointOfInterest.getPos();
			});
	}

	public Optional<BlockPos> getPosition(
		Predicate<PointOfInterestType> typePredicate,
		Predicate<BlockPos> positionPredicate,
		PointOfInterestStorage.OccupationStatus occupationStatus,
		BlockPos pos,
		int radius,
		Random random
	) {
		List<PointOfInterest> list = (List<PointOfInterest>)this.get(typePredicate, pos, radius, occupationStatus).collect(Collectors.toList());
		Collections.shuffle(list, random);
		return list.stream().filter(pointOfInterest -> positionPredicate.test(pointOfInterest.getPos())).findFirst().map(PointOfInterest::getPos);
	}

	public boolean releaseTicket(BlockPos pos) {
		return this.getOrCreate(ChunkSectionPos.from(pos).asLong()).releaseTicket(pos);
	}

	public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
		return (Boolean)this.get(ChunkSectionPos.from(pos).asLong()).map(pointOfInterestSet -> pointOfInterestSet.test(pos, predicate)).orElse(false);
	}

	public Optional<PointOfInterestType> getType(BlockPos pos) {
		PointOfInterestSet pointOfInterestSet = this.getOrCreate(ChunkSectionPos.from(pos).asLong());
		return pointOfInterestSet.getType(pos);
	}

	public int getDistanceFromNearestOccupied(ChunkSectionPos pos) {
		this.pointOfInterestDistanceTracker.update();
		return this.pointOfInterestDistanceTracker.getLevel(pos.asLong());
	}

	private boolean isOccupied(long l) {
		Optional<PointOfInterestSet> optional = this.getIfLoaded(l);
		return optional == null
			? false
			: (Boolean)optional.map(
					pointOfInterestSet -> pointOfInterestSet.get(PointOfInterestType.ALWAYS_TRUE, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).count() > 0L
				)
				.orElse(false);
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
				if (shouldScan(chunkSection)) {
					this.scanAndPopulate(chunkSection, chunkSectionPos, biConsumer);
				}
			}), () -> {
			if (shouldScan(chunkSection)) {
				PointOfInterestSet pointOfInterestSet = this.getOrCreate(chunkSectionPos.asLong());
				this.scanAndPopulate(chunkSection, chunkSectionPos, pointOfInterestSet::add);
			}
		});
	}

	private static boolean shouldScan(ChunkSection chunkSection) {
		return PointOfInterestType.getAllAssociatedStates().anyMatch(chunkSection::method_19523);
	}

	private void scanAndPopulate(ChunkSection chunkSection, ChunkSectionPos chunkSectionPos, BiConsumer<BlockPos, PointOfInterestType> biConsumer) {
		chunkSectionPos.streamBlocks()
			.forEach(
				blockPos -> {
					BlockState blockState = chunkSection.getBlockState(
						ChunkSectionPos.getLocalCoord(blockPos.getX()), ChunkSectionPos.getLocalCoord(blockPos.getY()), ChunkSectionPos.getLocalCoord(blockPos.getZ())
					);
					PointOfInterestType.from(blockState).ifPresent(pointOfInterestType -> biConsumer.accept(blockPos, pointOfInterestType));
				}
			);
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

	final class PointOfInterestDistanceTracker extends SectionDistanceLevelPropagator {
		private final Long2ByteMap distances = new Long2ByteOpenHashMap();

		protected PointOfInterestDistanceTracker() {
			super(7, 16, 256);
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
}
