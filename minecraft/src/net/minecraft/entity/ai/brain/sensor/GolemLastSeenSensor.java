package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
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
	protected void sense(ServerWorld serverWorld, LivingEntity livingEntity) {
		senseIronGolem(serverWorld.getTime(), livingEntity);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.MOBS);
	}

	public static void senseIronGolem(long l, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<List<LivingEntity>> optional = brain.getOptionalMemory(MemoryModuleType.MOBS);
		if (optional.isPresent()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(livingEntityx -> livingEntityx.getType().equals(EntityType.IRON_GOLEM));
			if (bl) {
				brain.putMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, l);
			}
		}
	}
}