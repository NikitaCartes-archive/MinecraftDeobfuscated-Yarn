package net.minecraft.village;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestSet implements DynamicSerializable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<>();
	private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.<PointOfInterestType, Set<PointOfInterest>>newHashMap();
	private final Runnable updateListener;

	public PointOfInterestSet(Runnable runnable) {
		this.updateListener = runnable;
	}

	public <T> PointOfInterestSet(Runnable runnable, Dynamic<T> dynamic) {
		this(runnable);
		dynamic.asStream().forEach(dynamicx -> {
			PointOfInterest pointOfInterest = new PointOfInterest(dynamicx, runnable);
			this.pointsOfInterestByPos.put(ChunkSectionPos.packToShort(pointOfInterest.getPos()), pointOfInterest);
			((Set)this.pointsOfInterestByType.computeIfAbsent(pointOfInterest.getType(), pointOfInterestType -> Sets.newHashSet())).add(pointOfInterest);
		});
	}

	public Stream<PointOfInterest> get(Predicate<PointOfInterestType> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return this.pointsOfInterestByType
			.entrySet()
			.stream()
			.filter(entry -> predicate.test(entry.getKey()))
			.flatMap(entry -> ((Set)entry.getValue()).stream())
			.filter(occupationStatus.getPredicate());
	}

	public void add(BlockPos blockPos, PointOfInterestType pointOfInterestType) {
		short s = ChunkSectionPos.packToShort(blockPos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		if (pointOfInterest != null) {
			if (!pointOfInterestType.equals(pointOfInterest.getType())) {
				throw new IllegalStateException("POI data mismatch: already registered at " + blockPos);
			}
		} else {
			PointOfInterest pointOfInterest2 = new PointOfInterest(blockPos, pointOfInterestType, this.updateListener);
			this.pointsOfInterestByPos.put(s, pointOfInterest2);
			((Set)this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType, pointOfInterestTypex -> Sets.newHashSet())).add(pointOfInterest2);
			LOGGER.debug(String.format("Added POI of type %s @ %s", pointOfInterestType, blockPos));
			this.updateListener.run();
		}
	}

	public void remove(BlockPos blockPos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.remove(ChunkSectionPos.packToShort(blockPos));
		if (pointOfInterest == null) {
			LOGGER.error("POI data mismatch: never registered at " + blockPos);
		} else {
			((Set)this.pointsOfInterestByType.get(pointOfInterest.getType())).remove(pointOfInterest);
			LOGGER.debug(String.format("Removed POI of type %s @ %s", pointOfInterest.getType(), pointOfInterest.getPos()));
			this.updateListener.run();
		}
	}

	public boolean releaseTicket(BlockPos blockPos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(ChunkSectionPos.packToShort(blockPos));
		if (pointOfInterest == null) {
			throw new IllegalStateException("POI never registered at " + blockPos);
		} else {
			boolean bl = pointOfInterest.releaseTicket();
			this.updateListener.run();
			return bl;
		}
	}

	public boolean test(BlockPos blockPos, Predicate<PointOfInterestType> predicate) {
		short s = ChunkSectionPos.packToShort(blockPos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		return pointOfInterest != null && predicate.test(pointOfInterest.getType());
	}

	public Optional<PointOfInterestType> getType(BlockPos blockPos) {
		short s = ChunkSectionPos.packToShort(blockPos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		return pointOfInterest != null ? Optional.of(pointOfInterest.getType()) : Optional.empty();
	}

	@Override
	public <T> T serialize(DynamicOps<T> dynamicOps) {
		return dynamicOps.createList(this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.serialize(dynamicOps)));
	}
}
