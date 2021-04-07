package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

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
			Predicate<BlockPos> predicate = blockPosx -> {
				long l = blockPosx.asLong();
				if (this.positionToExpiryTime.containsKey(l)) {
					return false;
				} else if (++this.tries >= 5) {
					return false;
				} else {
					this.positionToExpiryTime.put(l, this.expiryTime + 40L);
					return true;
				}
			};
			Stream<BlockPos> stream = pointOfInterestStorage.getPositions(
				PointOfInterestType.HOME.getCompletionCondition(), predicate, mobEntity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.ANY
			);
			Path path = mobEntity.getNavigation().findPathToAny(stream, PointOfInterestType.HOME.getSearchDistance());
			if (path != null && path.reachesTarget()) {
				BlockPos blockPos = path.getTarget();
				Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos);
				if (optional.isPresent()) {
					mobEntity.getBrain().remember(MemoryModuleType.NEAREST_BED, blockPos);
				}
			} else if (this.tries < 5) {
				this.positionToExpiryTime.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.expiryTime);
			}
		}
	}
}
