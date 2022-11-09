package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.slf4j.Logger;

public class PointOfInterestSet {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Short2ObjectMap<PointOfInterest> pointsOfInterestByPos = new Short2ObjectOpenHashMap<>();
	private final Map<RegistryEntry<PointOfInterestType>, Set<PointOfInterest>> pointsOfInterestByType = Maps.<RegistryEntry<PointOfInterestType>, Set<PointOfInterest>>newHashMap();
	private final Runnable updateListener;
	private boolean valid;

	public static Codec<PointOfInterestSet> createCodec(Runnable updateListener) {
		return RecordCodecBuilder.<PointOfInterestSet>create(
				instance -> instance.group(
							RecordCodecBuilder.point(updateListener),
							Codec.BOOL.optionalFieldOf("Valid", Boolean.valueOf(false)).forGetter(poiSet -> poiSet.valid),
							PointOfInterest.createCodec(updateListener).listOf().fieldOf("Records").forGetter(poiSet -> ImmutableList.copyOf(poiSet.pointsOfInterestByPos.values()))
						)
						.apply(instance, PointOfInterestSet::new)
			)
			.orElseGet(Util.addPrefix("Failed to read POI section: ", LOGGER::error), () -> new PointOfInterestSet(updateListener, false, ImmutableList.of()));
	}

	public PointOfInterestSet(Runnable updateListener) {
		this(updateListener, true, ImmutableList.of());
	}

	private PointOfInterestSet(Runnable updateListener, boolean valid, List<PointOfInterest> pois) {
		this.updateListener = updateListener;
		this.valid = valid;
		pois.forEach(this::add);
	}

	public Stream<PointOfInterest> get(Predicate<RegistryEntry<PointOfInterestType>> predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
		return this.pointsOfInterestByType
			.entrySet()
			.stream()
			.filter(entry -> predicate.test((RegistryEntry)entry.getKey()))
			.flatMap(entry -> ((Set)entry.getValue()).stream())
			.filter(occupationStatus.getPredicate());
	}

	public void add(BlockPos pos, RegistryEntry<PointOfInterestType> type) {
		if (this.add(new PointOfInterest(pos, type, this.updateListener))) {
			LOGGER.debug("Added POI of type {} @ {}", type.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]"), pos);
			this.updateListener.run();
		}
	}

	private boolean add(PointOfInterest poi) {
		BlockPos blockPos = poi.getPos();
		RegistryEntry<PointOfInterestType> registryEntry = poi.getType();
		short s = ChunkSectionPos.packLocal(blockPos);
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(s);
		if (pointOfInterest != null) {
			if (registryEntry.equals(pointOfInterest.getType())) {
				return false;
			}

			Util.error("POI data mismatch: already registered at " + blockPos);
		}

		this.pointsOfInterestByPos.put(s, poi);
		((Set)this.pointsOfInterestByType.computeIfAbsent(registryEntry, type -> Sets.newHashSet())).add(poi);
		return true;
	}

	public void remove(BlockPos pos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.remove(ChunkSectionPos.packLocal(pos));
		if (pointOfInterest == null) {
			LOGGER.error("POI data mismatch: never registered at {}", pos);
		} else {
			((Set)this.pointsOfInterestByType.get(pointOfInterest.getType())).remove(pointOfInterest);
			LOGGER.debug("Removed POI of type {} @ {}", LogUtils.defer(pointOfInterest::getType), LogUtils.defer(pointOfInterest::getPos));
			this.updateListener.run();
		}
	}

	@Deprecated
	@Debug
	public int getFreeTickets(BlockPos pos) {
		return (Integer)this.get(pos).map(PointOfInterest::getFreeTickets).orElse(0);
	}

	public boolean releaseTicket(BlockPos pos) {
		PointOfInterest pointOfInterest = this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos));
		if (pointOfInterest == null) {
			throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("POI never registered at " + pos));
		} else {
			boolean bl = pointOfInterest.releaseTicket();
			this.updateListener.run();
			return bl;
		}
	}

	public boolean test(BlockPos pos, Predicate<RegistryEntry<PointOfInterestType>> predicate) {
		return this.getType(pos).filter(predicate).isPresent();
	}

	public Optional<RegistryEntry<PointOfInterestType>> getType(BlockPos pos) {
		return this.get(pos).map(PointOfInterest::getType);
	}

	private Optional<PointOfInterest> get(BlockPos pos) {
		return Optional.ofNullable(this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos)));
	}

	public void updatePointsOfInterest(Consumer<BiConsumer<BlockPos, RegistryEntry<PointOfInterestType>>> updater) {
		if (!this.valid) {
			Short2ObjectMap<PointOfInterest> short2ObjectMap = new Short2ObjectOpenHashMap<>(this.pointsOfInterestByPos);
			this.clear();
			updater.accept(
				(BiConsumer)(pos, registryEntry) -> {
					short s = ChunkSectionPos.packLocal(pos);
					PointOfInterest pointOfInterest = short2ObjectMap.computeIfAbsent(
						s, (Short2ObjectFunction<? extends PointOfInterest>)(sx -> new PointOfInterest(pos, registryEntry, this.updateListener))
					);
					this.add(pointOfInterest);
				}
			);
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
