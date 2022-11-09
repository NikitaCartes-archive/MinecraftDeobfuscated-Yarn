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
	private static final int RUN_TIME = 200;
	private static final int GOLEM_DETECTED_WARMUP = 600;

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

	public static void senseIronGolem(LivingEntity entity) {
		Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
		if (optional.isPresent()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(livingEntity -> livingEntity.getType().equals(EntityType.IRON_GOLEM));
			if (bl) {
				rememberIronGolem(entity);
			}
		}
	}

	public static void rememberIronGolem(LivingEntity entity) {
		entity.getBrain().remember(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
	}
}
