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
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class NearestBedSensor extends Sensor<MobEntity> {
	private final Long2LongMap field_20295 = new Long2LongOpenHashMap();
	private int field_20296;
	private long field_20297;

	public NearestBedSensor() {
		super(20);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_19007);
	}

	protected void method_21646(ServerWorld serverWorld, MobEntity mobEntity) {
		if (mobEntity.isBaby()) {
			this.field_20296 = 0;
			this.field_20297 = serverWorld.getTime() + (long)serverWorld.getRandom().nextInt(20);
			PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
			Predicate<BlockPos> predicate = blockPosx -> {
				long l = blockPosx.asLong();
				if (this.field_20295.containsKey(l)) {
					return false;
				} else if (++this.field_20296 >= 5) {
					return false;
				} else {
					this.field_20295.put(l, this.field_20297 + 40L);
					return true;
				}
			};
			Stream<BlockPos> stream = pointOfInterestStorage.method_21647(
				PointOfInterestType.field_18517.getCompletionCondition(), predicate, new BlockPos(mobEntity), 48, PointOfInterestStorage.OccupationStatus.field_18489
			);
			Path path = mobEntity.getNavigation().method_21643(stream, PointOfInterestType.field_18517.method_21648());
			if (path != null && path.method_21655()) {
				BlockPos blockPos = path.method_48();
				Optional<PointOfInterestType> optional = pointOfInterestStorage.getType(blockPos);
				if (optional.isPresent()) {
					mobEntity.getBrain().putMemory(MemoryModuleType.field_19007, blockPos);
				}
			} else if (this.field_20296 < 5) {
				this.field_20295.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.field_20297);
			}
		}
	}
}
