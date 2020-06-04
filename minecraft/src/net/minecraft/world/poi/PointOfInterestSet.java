package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestSet {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<>();
	private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.<PointOfInterestType, Set<PointOfInterest>>newHashMap();
	private final Runnable updateListener;
	private boolean valid;

	public static Codec<PointOfInterestSet> method_28364(Runnable runnable) {
		return RecordCodecBuilder.<PointOfInterestSet>create(
				instance -> instance.group(
							RecordCodecBuilder.point(runnable),
							Codec.BOOL.optionalFieldOf("Valid", Boolean.valueOf(false)).forGetter(pointOfInterestSet -> pointOfInterestSet.valid),
							PointOfInterest.method_28359(runnable)
								.listOf()
								.fieldOf("Records")
								.forGetter(pointOfInterestSet -> ImmutableList.copyOf(pointOfInterestSet.pointsOfInterestByPos.values()))
						)
						.apply(instance, PointOfInterestSet::new)
			)
			.withDefault(
				Util.method_29188("Failed to read POI section: ", LOGGER::error),
				(Supplier<? extends PointOfInterestSet>)(() -> new PointOfInterestSet(runnable, false, ImmutableList.of()))
			);
	}

	public PointOfInterestSet(Runnable updateListener) {
		this(updateListener, true, ImmutableList.of());
	}

	private PointOfInterestSet(Runnable runnable, boolean bl, List<PointOfInterest> list) {
		this.updateListener = runnable;
		this.valid = bl;
		list.forEach(this::add);
	}

	public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return this.pointsOfInterestByType
			.entrySet()
			.stream()
			.filter(entry -> predicate.test(entry.getKey()))
			.flatMap(entry -> ((Set)entry.getValue()).stream())
			.filter(occupationStatus.getPredicate());
	}

	public void add(BlockPos pos, PointOfInterestType type) {
		if (this.add(new PointOfInterest(pos, type, this.updateListener))) {
			LOGGER.debug("Added POI of type {} @ {}", () -> type, () -> pos);
			this.updateListener.run();
		}
	}

	private boolean add(PointOfInterest poi) {
		BlockPos blockPos = poi.getPos();
		PointOfInterestType pointOfInterestType = poi.getType();
		short s = ChunkSectionPos.getPackedLocalPos(blockPos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		if (pointOfInterest != null) {
			if (pointOfInterestType.equals(pointOfInterest.getType())) {
				return false;
			} else {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("POI data mismatch: already registered at " + blockPos));
			}
		} else {
			this.pointsOfInterestByPos.put(s, poi);
			((Set)this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType, pointOfInterestTypex -> Sets.newHashSet())).add(poi);
			return true;
		}
	}

	public void remove(BlockPos pos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.remove(ChunkSectionPos.getPackedLocalPos(pos));
		if (pointOfInterest == null) {
			LOGGER.error("POI data mismatch: never registered at " + pos);
		} else {
			((Set)this.pointsOfInterestByType.get(pointOfInterest.getType())).remove(pointOfInterest);
			LOGGER.debug("Removed POI of type {} @ {}", pointOfInterest::getType, pointOfInterest::getPos);
			this.updateListener.run();
		}
	}

	public boolean releaseTicket(BlockPos pos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(ChunkSectionPos.getPackedLocalPos(pos));
		if (pointOfInterest == null) {
			throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("POI never registered at " + pos));
		} else {
			boolean bl = pointOfInterest.releaseTicket();
			this.updateListener.run();
			return bl;
		}
	}

	public boolean test(BlockPos pos, Predicate<PointOfInterestType> predicate) {
		short s = ChunkSectionPos.getPackedLocalPos(pos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		return pointOfInterest != null && predicate.test(pointOfInterest.getType());
	}

	public Optional<PointOfInterestType> getType(BlockPos pos) {
		short s = ChunkSectionPos.getPackedLocalPos(pos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		return pointOfInterest != null ? Optional.of(pointOfInterest.getType()) : Optional.empty();
	}

	public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
		if (!this.valid) {
			Short2ObjectMap<PointOfInterest> short2ObjectMap = new Short2ObjectOpenHashMap<>(this.pointsOfInterestByPos);
			this.clear();
			consumer.accept((BiConsumer)(blockPos, pointOfInterestType) -> {
				short s = ChunkSectionPos.getPackedLocalPos(blockPos);
				PointOfInterest pointOfInterest = short2ObjectMap.computeIfAbsent(s, i -> new PointOfInterest(blockPos, pointOfInterestType, this.updateListener));
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

	boolean isValid() {
		return this.valid;
	}
}
