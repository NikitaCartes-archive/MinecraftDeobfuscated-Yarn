package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class NearestBedSensor extends Sensor<MobEntity> {
	public NearestBedSensor() {
		super(100);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.field_19007);
	}

	protected void method_19998(ServerWorld serverWorld, MobEntity mobEntity) {
		mobEntity.getBrain().setMemory(MemoryModuleType.field_19007, this.findNearestBed(serverWorld, mobEntity));
	}

	private Optional<BlockPos> findNearestBed(ServerWorld serverWorld, MobEntity mobEntity) {
		PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
		Predicate<BlockPos> predicate = blockPos -> {
			if (blockPos.equals(new BlockPos(mobEntity))) {
				return true;
			} else {
				Path path = mobEntity.getNavigation().findPathTo(blockPos);
				return path != null && path.method_19315();
			}
		};
		return pointOfInterestStorage.getNearestPosition(
			PointOfInterestType.field_18517.getCompletionCondition(), predicate, new BlockPos(mobEntity), 16, PointOfInterestStorage.OccupationStatus.field_18489
		);
	}
}
