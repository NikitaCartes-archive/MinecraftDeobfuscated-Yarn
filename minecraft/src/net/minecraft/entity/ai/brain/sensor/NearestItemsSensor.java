package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class NearestItemsSensor extends Sensor<MobEntity> {
	private static final long HORIZONTAL_RANGE = 32L;
	private static final long VERTICAL_RANGE = 16L;
	public static final int MAX_RANGE = 32;

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
	}

	protected void sense(ServerWorld serverWorld, MobEntity mobEntity) {
		Brain<?> brain = mobEntity.getBrain();
		List<ItemEntity> list = serverWorld.getEntitiesByClass(ItemEntity.class, mobEntity.getBoundingBox().expand(32.0, 16.0, 32.0), itemEntity -> true);
		list.sort(Comparator.comparingDouble(mobEntity::squaredDistanceTo));
		Optional<ItemEntity> optional = list.stream()
			.filter(itemEntity -> mobEntity.canGather(itemEntity.getStack()))
			.filter(itemEntity -> itemEntity.isInRange(mobEntity, 32.0))
			.filter(mobEntity::canSee)
			.findFirst();
		brain.remember(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
	}
}
