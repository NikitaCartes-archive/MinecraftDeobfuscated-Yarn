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
		return ImmutableSet.of(MemoryModuleType.field_18441);
	}

	public static void senseIronGolem(long l, LivingEntity livingEntity) {
		Brain<?> brain = livingEntity.getBrain();
		Optional<List<LivingEntity>> optional = brain.getOptionalMemory(MemoryModuleType.field_18441);
		if (optional.isPresent()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(livingEntityx -> livingEntityx.getType().equals(EntityType.field_6147));
			if (bl) {
				brain.putMemory(MemoryModuleType.field_19355, l);
			}
		}
	}
}
