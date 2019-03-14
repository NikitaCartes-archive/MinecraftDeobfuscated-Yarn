package net.minecraft.village;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.io.File;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_4079;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.storage.SerializingRegionBasedStorage;

public class PointOfInterestStorage extends SerializingRegionBasedStorage<PointOfInterestSet> {
	private final PointOfInterestStorage.LevelProcessor levelProcessor = new PointOfInterestStorage.LevelProcessor();

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
			.filter(pointOfInterest -> predicate2.test(pointOfInterest.getPos()))
			.reduce(
				(pointOfInterest, pointOfInterest2) -> pointOfInterest.getPos().squaredDistanceTo(blockPos) < pointOfInterest2.getPos().squaredDistanceTo(blockPos)
						? pointOfInterest
						: pointOfInterest2
			)
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
		return this.getOrCreate(ChunkSectionPos.from(blockPos).asLong()).getType(blockPos);
	}

	public int getLevel(ChunkSectionPos chunkSectionPos) {
		this.levelProcessor.updateLevels();
		return this.levelProcessor.getLevel(chunkSectionPos.asLong());
	}

	private boolean method_19133(long l) {
		return this.get(PointOfInterestType.ALWAYS_TRUE, l, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).count() > 0L;
	}

	@Override
	public void tick(BooleanSupplier booleanSupplier) {
		super.tick(booleanSupplier);
		this.levelProcessor.updateLevels();
	}

	@Override
	protected void onUpdate(long l) {
		super.onUpdate(l);
		this.levelProcessor.scheduleNewLevelUpdate(l, this.levelProcessor.method_18749(l), false);
	}

	@Override
	protected void onLoad(long l) {
		this.levelProcessor.scheduleNewLevelUpdate(l, this.levelProcessor.method_18749(l), false);
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

	final class LevelProcessor extends class_4079 {
		private final Long2ByteMap levels = new Long2ByteOpenHashMap();

		protected LevelProcessor() {
			super(5, 16, 256);
			this.levels.defaultReturnValue((byte)5);
		}

		@Override
		protected int method_18749(long l) {
			return PointOfInterestStorage.this.method_19133(l) ? 0 : 5;
		}

		@Override
		protected int getLevel(long l) {
			return this.levels.get(l);
		}

		@Override
		protected void setLevel(long l, int i) {
			if (i > 4) {
				this.levels.remove(l);
			} else {
				this.levels.put(l, (byte)i);
			}
		}

		public void updateLevels() {
			super.updateLevels(Integer.MAX_VALUE);
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
