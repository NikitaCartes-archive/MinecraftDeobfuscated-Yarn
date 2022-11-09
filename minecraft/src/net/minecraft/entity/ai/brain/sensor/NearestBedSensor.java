package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class NearestBedSensor extends Sensor<MobEntity> {
	private static final int REMEMBER_TIME = 40;
	private static final int MAX_TRIES = 5;
	private static final int MAX_EXPIRY_TIME = 20;
	private final Long2LongMap positionToExpiryTime = new Long2LongOpenHashMap();
	private int tries;
	private long expiryTime;

	public NearestBedSensor() {
		super(20);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
	}

	protected void sense(ServerWorld serverWorld, MobEntity mobEntity) {
		if (mobEntity.isBaby()) {
			this.tries = 0;
			this.expiryTime = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Predicate<BlockPos> predicate = pos -> {
				long l = pos.asLong();
				if (this.positionToExpiryTime.containsKey(l)) {
					return false;
				} else if (++this.tries >= 5) {
					return false;
				} else {
					this.positionToExpiryTime.put(l, this.expiryTime + 40L);
					return true;
				}
			};
			Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = (Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>>)pointOfInterestStorage.getTypesAndPositions(
					registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME), predicate, mobEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY
				)
				.collect(Collectors.toSet());
			Path path = FindPointOfInterestTask.findPathToPoi(mobEntity, set);
			if (path != null && path.reachesTarget()) {
				BlockPos blockPos = path.getTarget();
				Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(blockPos);
				if (optional.isPresent()) {
					mobEntity.getBrain().remember(MemoryModuleType.NEAREST_BED, blockPos);
				}
			} else if (this.tries < 5) {
				this.positionToExpiryTime.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTime);
			}
		}
	}
}
