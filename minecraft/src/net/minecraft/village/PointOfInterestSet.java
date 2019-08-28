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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestSet implements DynamicSerializable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<>();
	private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.<PointOfInterestType, Set<PointOfInterest>>newHashMap();
	private final Runnable updateListener;
	private boolean valid;

	public PointOfInterestSet(Runnable runnable) {
		this.updateListener = runnable;
		this.valid = true;
	}

	public <T> PointOfInterestSet(Runnable runnable, Dynamic<T> dynamic) {
		this.updateListener = runnable;

		try {
			this.valid = dynamic.get("Valid").asBoolean(false);
			dynamic.get("Records").asStream().forEach(dynamicx -> this.add(new PointOfInterest(dynamicx, runnable)));
		} catch (Exception var4) {
			LOGGER.error("Failed to load POI chunk", (Throwable)var4);
			this.clear();
			this.valid = false;
		}
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
		if (this.add(new PointOfInterest(blockPos, pointOfInterestType, this.updateListener))) {
			LOGGER.debug("Added POI of type {} @ {}", () -> pointOfInterestType, () -> blockPos);
			this.updateListener.run();
		}
	}

	private boolean add(PointOfInterest pointOfInterest) {
		BlockPos blockPos = pointOfInterest.getPos();
		PointOfInterestType pointOfInterestType = pointOfInterest.getType();
		short s = ChunkSectionPos.packToShort(blockPos);
		PointOfInterest pointOfInterest2 = this.pointsOfInterestByPos.get(s);
		if (pointOfInterest2 != null) {
			if (pointOfInterestType.equals(pointOfInterest2.getType())) {
				return false;
			} else {
				throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("POI data mismatch: already registered at " + blockPos));
			}
		} else {
			this.pointsOfInterestByPos.put(s, pointOfInterest);
			((Set)this.pointsOfInterestByType.computeIfAbsent(pointOfInterestType, pointOfInterestTypex -> Sets.newHashSet())).add(pointOfInterest);
			return true;
		}
	}

	public void remove(BlockPos blockPos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.remove(ChunkSectionPos.packToShort(blockPos));
		if (pointOfInterest == null) {
			LOGGER.error("POI data mismatch: never registered at " + blockPos);
		} else {
			((Set)this.pointsOfInterestByType.get(pointOfInterest.getType())).remove(pointOfInterest);
			LOGGER.debug("Removed POI of type {} @ {}", pointOfInterest::getType, pointOfInterest::getPos);
			this.updateListener.run();
		}
	}

	public boolean releaseTicket(BlockPos blockPos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(ChunkSectionPos.packToShort(blockPos));
		if (pointOfInterest == null) {
			throw (IllegalStateException)SystemUtil.throwOrPause(new IllegalStateException("POI never registered at " + blockPos));
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
		T object = dynamicOps.createList(this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.serialize(dynamicOps)));
		return dynamicOps.createMap(
			ImmutableMap.of(dynamicOps.createString("Records"), object, dynamicOps.createString("Valid"), dynamicOps.createBoolean(this.valid))
		);
	}

	public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, PointOfInterestType>> consumer) {
		if (!this.valid) {
			Short2ObjectMap<PointOfInterest> short2ObjectMap = new Short2ObjectOpenHashMap<>(this.pointsOfInterestByPos);
			this.clear();
			consumer.accept((BiConsumer)(blockPos, pointOfInterestType) -> {
				short s = ChunkSectionPos.packToShort(blockPos);
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
}
