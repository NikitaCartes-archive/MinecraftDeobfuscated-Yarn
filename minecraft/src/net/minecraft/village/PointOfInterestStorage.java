package net.minecraft.village;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.io.File;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.util.SectionRelativeLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage extends SerializingRegionBasedStorage<PointOfInterestSet> {
	private final PointOfInterestStorage.PathfindingFavorProvider levelProcessor = new PointOfInterestStorage.PathfindingFavorProvider();

	public PointOfInterestStorage(File file) {
		super(file, PointOfInterestSet::new, PointOfInterestSet::new);
	}

	public void add(BlockPos blockPos, PointOfInterestType pointOfInterestType) {
		this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).add(blockPos, pointOfInterestType);
	}

	public void remove(BlockPos blockPos) {
		this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).remove(blockPos);
	}

	private Stream<PointOfInterest> get(
		Predicate<PointOfInterestType> predicate, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		int j = i * i;
		return ChunkPos.streamPositions(new ChunkPos(blockPos), Math.floorDiv(i, 16))
			.flatMap(
				chunkPos -> this.get(predicate, chunkPos, occupationStatus).filter(pointOfInterest -> pointOfInterest.getPos().squaredDistanceTo(blockPos) <= (double)j)
			);
	}

	public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, ChunkPos chunkPos, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return IntStream.range(0, 16).boxed().flatMap(integer -> this.get(predicate, ChunkSectionPos.from(chunkPos, integer).asLong(), occupationStatus));
	}

	private Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, long l, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return (Stream<PointOfInterest>)this.get(l).map(pointOfInterestSet -> pointOfInterestSet.get(predicate, occupationStatus)).orElseGet(Stream::empty);
	}

	public Optional<BlockPos> getPosition(
		Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i, PointOfInterestStorage.OccupationStatus occupationStatus
	) {
		return this.get(predicate, blockPos, i, occupationStatus).map(PointOfInterest::getPos).filter(predicate2).findFirst();
	}

	public Optional<BlockPos> getPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i) {
		return this.get(predicate, blockPos, i, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
			.filter(pointOfInterest -> predicate2.test(pointOfInterest.getPos()))
			.findFirst()
			.map(pointOfInterest -> {
				pointOfInterest.reserveTicket();
				return pointOfInterest.getPos();
			});
	}

	public Optional<BlockPos> getNearestPosition(Predicate<PointOfInterestType> predicate, Predicate<BlockPos> predicate2, BlockPos blockPos, int i) {
		return this.get(predicate, blockPos, i, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
			.sorted(Comparator.comparingDouble(pointOfInterest -> pointOfInterest.getPos().squaredDistanceTo(blockPos)))
			.filter(pointOfInterest -> predicate2.test(pointOfInterest.getPos()))
			.findFirst()
			.map(pointOfInterest -> {
				pointOfInterest.reserveTicket();
				return pointOfInterest.getPos();
			});
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

	public int getLevel(ChunkSectionPos chunkSectionPos) {
		this.levelProcessor.method_19134();
		return this.levelProcessor.getLevel(chunkSectionPos.asLong());
	}

	private boolean method_19133(long l) {
		return this.get(PointOfInterestType.ALWAYS_TRUE, l, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).count() > 0L;
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		super.tick(booleanSupplier);
		this.levelProcessor.method_19134();
	}

	@Override
	protected void onUpdate(long l) {
		super.onUpdate(l);
		this.levelProcessor.method_18750(l, this.levelProcessor.method_18749(l), false);
	}

	@Override
	protected void onLoad(long l) {
		this.levelProcessor.method_18750(l, this.levelProcessor.method_18749(l), false);
	}

	public void method_19510(ChunkPos chunkPos, ChunkSection chunkSection) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, chunkSection.getYOffset() >> 4);
		Optional<PointOfInterestSet> optional = this.get(chunkSectionPos.asLong());
		if (!optional.isPresent()) {
			if (!PointOfInterestType.method_19518().noneMatch(chunkSection::method_19523)) {
				chunkSectionPos.method_19533()
					.forEach(
						blockPos -> {
							BlockState blockState = chunkSection.getBlockState(
								ChunkSectionPos.toLocalCoord(blockPos.getX()), ChunkSectionPos.toLocalCoord(blockPos.getY()), ChunkSectionPos.toLocalCoord(blockPos.getZ())
							);
							PointOfInterestType.method_19516(blockState).ifPresent(pointOfInterestType -> this.add(blockPos, pointOfInterestType));
						}
					);
			}
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

	final class PathfindingFavorProvider extends SectionRelativeLevelPropagator {
		private final Long2ByteMap pathfindingFavors = new Long2ByteOpenHashMap();

		protected PathfindingFavorProvider() {
			super(5, 16, 256);
			this.pathfindingFavors.defaultReturnValue((byte)5);
		}

		@Override
		protected int method_18749(long l) {
			return PointOfInterestStorage.this.method_19133(l) ? 0 : 5;
		}

		@Override
		protected int getLevel(long l) {
			return this.pathfindingFavors.get(l);
		}

		@Override
		protected void setLevel(long l, int i) {
			if (i > 4) {
				this.pathfindingFavors.remove(l);
			} else {
				this.pathfindingFavors.put(l, (byte)i);
			}
		}

		public void method_19134() {
			super.updateAllRecursively(Integer.MAX_VALUE);
		}
	}
}
