package net.minecraft.world.poi;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PointOfInterestSet implements DynamicSerializable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<>();
	private final Map<PointOfInterestType, Set<PointOfInterest>> pointsOfInterestByType = Maps.<PointOfInterestType, Set<PointOfInterest>>newHashMap();
	private final Runnable updateListener;
	private boolean valid;

	public PointOfInterestSet(Runnable updateListener) {
		this.updateListener = updateListener;
		this.valid = true;
	}

	public <T> PointOfInterestSet(Runnable updateListener, Dynamic<T> dynamic) {
		this.updateListener = updateListener;

		try {
			this.valid = dynamic.get("Valid").asBoolean(false);
			dynamic.get("Records").asStream().forEach(dynamicx -> this.add(new PointOfInterest(dynamicx, updateListener)));
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

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		T object = ops.createList(this.pointsOfInterestByPos.values().stream().map(pointOfInterest -> pointOfInterest.serialize(ops)));
		return ops.createMap(ImmutableMap.of(ops.createString("Records"), object, ops.createString("Valid"), ops.createBoolean(this.valid)));
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
