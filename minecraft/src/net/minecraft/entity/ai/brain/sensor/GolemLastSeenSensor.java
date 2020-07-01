package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class GolemLastSeenSensor extends Sensor<LivingEntity> {
	public GolemLastSeenSensor() {
		this(200);
	}

	public GolemLastSeenSensor(int i) {
		super(i);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		senseIronGolem(entity);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.MOBS);
	}

	public static void senseIronGolem(LivingEntity livingEntity) {
		Optional<List<LivingEntity>> optional = livingEntity.getBrain().getOptionalMemory(MemoryModuleType.MOBS);
		if (optional.isPresent()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(livingEntityx -> livingEntityx.getType().equals(EntityType.IRON_GOLEM));
			if (bl) {
				method_30233(livingEntity);
			}
		}
	}

	public static void method_30233(LivingEntity livingEntity) {
		livingEntity.getBrain().remember(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
	}
}
