package net.minecraft.village;

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
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.util.SectionRelativeLevelPropagator;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage extends SerializingRegionBasedStorage<PointOfInterestSet> {
	private final PointOfInterestStorage.PointOfInterestDistanceTracker pointOfInterestDistanceTracker = new PointOfInterestStorage.PointOfInterestDistanceTracker(
		
	);

	public PointOfInterestStorage(File file, DataFixer dataFixer) {
		super(file, PointOfInterestSet::new, PointOfInterestSet::new, dataFixer, DataFixTypes.field_19221);
	}

	public void add(BlockPos blockPos, PointOfInterestType pointOfInterestType) {
		this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).add(blockPos, pointOfInterestType);
	}

	public void remove(BlockPos blockPos) {
		this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).remove(blockPos);
	}

	public long count(Predicate<PointOfInterestType> predicate, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return this.get(predicate, blockPos, i, occupationStatus).count();
	}

	public Stream<PointOfInterest> get(
		Predicate<PointOfInterestType> predicate, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		int j = i * i;
		return ChunkPos.stream(new ChunkPos(blockPos), Math.floorDiv(i, 16))
			.flatMap(
				chunkPos -> this.get(predicate, chunkPos, occupationStatus).filter(pointOfInterest -> pointOfInterest.getPos().getSquaredDistance(blockPos) <= (double)j)
			);
	}

	public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, ChunkPos chunkPos, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return IntStream.range(0, 16).boxed().flatMap(integer -> this.get(predicate, ChunkSectionPos.from(chunkPos, integer).asLong(), occupationStatus));
	}

	private Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, long l, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return (Stream<PointOfInterest>)this.get(l).map(pointOfInterestSet -> pointOfInterestSet.get(predicate, occupationStatus)).orElseGet(Stream::empty);
	}

	public Stream<BlockPos> method_21647(
		Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.get(predicate, blockPos, i, occupationStatus).map(PointOfInterest::getPos).filter(predicate2);
	}

	public Optional<BlockPos> getPosition(
		Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.method_21647(predicate, predicate2, blockPos, i, occupationStatus).findFirst();
	}

	public Optional<BlockPos> getNearestPosition(
		Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.get(predicate, blockPos, i, occupationStatus)
			.map(PointOfInterest::getPos)
			.sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(blockPos)))
			.filter(predicate2)
			.findFirst();
	}

	public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i) {
		return this.get(predicate, blockPos, i, PointOfInterestStorage.OccupationStatus.field_18487)
			.filter(pointOfInterest -> predicate2.test(pointOfInterest.getPos()))
			.findFirst()
			.map(pointOfInterest -> {
				pointOfInterest.reserveTicket();
				return pointOfInterest.getPos();
			});
	}

	public Optional<BlockPos> getPosition(
		Predicate<PointOfInterestType> predicate,
		Predicate<BlockPos> predicate2,
		PointOfInterestStorage.OccupationStatus occupationStatus,
		BlockPos blockPos,
		int i,
		Random random
	) {
		List<PointOfInterest> list = (List<PointOfInterest>)this.get(predicate, blockPos, i, occupationStatus).collect(Collectors.toList());
		Collections.shuffle(list, random);
		return list.stream().filter(pointOfInterest -> predicate2.test(pointOfInterest.getPos())).findFirst().map(PointOfInterest::getPos);
	}

	public boolean releaseTicket(BlockPos blockPos) {
		return this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).releaseTicket(blockPos);
	}

	public boolean test(BlockPos blockPos, Predicate<PointOfInterestType> predicate) {
		return (Boolean)this.get(ChunkSectionPos.from(blockPos).asLong()).map(pointOfInterestSet -> pointOfInterestSet.test(blockPos, predicate)).orElse(false);
	}

	public Optional<PointOfInterestType> getType(BlockPos blockPos) {
		PointOfInterestSet pointOfInterestSet = this.getOrCreate(ChunkSectionPos.from(blockPos).asLong());
		return pointOfInterestSet.getType(blockPos);
	}

	public int getDistanceFromNearestOccupied(ChunkSectionPos chunkSectionPos) {
		this.pointOfInterestDistanceTracker.update();
		return this.pointOfInterestDistanceTracker.getLevel(chunkSectionPos.asLong());
	}

	private boolean isOccupied(long l) {
		Optional<PointOfInterestSet> optional = this.getIfLoaded(l);
		return optional == null
			? false
			: (Boolean)optional.map(
					pointOfInterestSet -> pointOfInterestSet.get(PointOfInterestType.ALWAYS_TRUE, PointOfInterestStorage.OccupationStatus.field_18488).count() > 0L
				)
				.orElse(false);
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		super.tick(booleanSupplier);
		this.pointOfInterestDistanceTracker.update();
	}

	@Override
	protected void onUpdate(long l) {
		super.onUpdate(l);
		this.pointOfInterestDistanceTracker.update(l, this.pointOfInterestDistanceTracker.getInitialLevel(l), false);
	}

	@Override
	protected void onLoad(long l) {
		this.pointOfInterestDistanceTracker.update(l, this.pointOfInterestDistanceTracker.getInitialLevel(l), false);
	}

	public void initForPalette(ChunkPos chunkPos, ChunkSection chunkSection) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, chunkSection.getYOffset() >> 4);
		SystemUtil.ifPresentOrElse(this.get(chunkSectionPos.asLong()), pointOfInterestSet -> pointOfInterestSet.updatePointsOfInterest(biConsumer -> {
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
						ChunkSectionPos.toLocalCoord(blockPos.getX()), ChunkSectionPos.toLocalCoord(blockPos.getY()), ChunkSectionPos.toLocalCoord(blockPos.getZ())
					);
					PointOfInterestType.from(blockState).ifPresent(pointOfInterestType -> biConsumer.accept(blockPos, pointOfInterestType));
				}
			);
	}

	public static enum OccupationStatus {
		field_18487(PointOfInterest::hasSpace),
		field_18488(PointOfInterest::isOccupied),
		field_18489(pointOfInterest -> true);

		private final Predicate<? super PointOfInterest> predicate;

		private OccupationStatus(Predicate<? super PointOfInterest> predicate) {
			this.predicate = predicate;
		}

		public Predicate<? super PointOfInterest> getPredicate() {
			return this.predicate;
		}
	}

	final class PointOfInterestDistanceTracker extends SectionRelativeLevelPropagator {
		private final Long2ByteMap distances = new Long2ByteOpenHashMap();

		protected PointOfInterestDistanceTracker() {
			super(7, 16, 256);
			this.distances.defaultReturnValue((byte)7);
		}

		@Override
		protected int getInitialLevel(long l) {
			return PointOfInterestStorage.this.isOccupied(l) ? 0 : 7;
		}

		@Override
		protected int getLevel(long l) {
			return this.distances.get(l);
		}

		@Override
		protected void setLevel(long l, int i) {
			if (i > 6) {
				this.distances.remove(l);
			} else {
				this.distances.put(l, (byte)i);
			}
		}

		public void update() {
			super.updateAllRecursively(Integer.MAX_VALUE);
		}
	}
}
