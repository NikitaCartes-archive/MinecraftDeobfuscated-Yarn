package net.minecraft;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

public class class_9068<T extends LivingEntity> extends Sensor<T> {
	private final BiPredicate<T, LivingEntity> field_47764;
	private final Predicate<T> field_47765;
	private final MemoryModuleType<Boolean> field_47766;
	private final int field_47767;

	public class_9068(int i, BiPredicate<T, LivingEntity> biPredicate, Predicate<T> predicate, MemoryModuleType<Boolean> memoryModuleType, int j) {
		super(i);
		this.field_47764 = biPredicate;
		this.field_47765 = predicate;
		this.field_47766 = memoryModuleType;
		this.field_47767 = j;
	}

	@Override
	protected void sense(ServerWorld world, T entity) {
		if (!this.field_47765.test(entity)) {
			this.method_55705(entity);
		} else {
			this.method_55702(entity);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return Set.of(MemoryModuleType.MOBS);
	}

	public void method_55702(T livingEntity) {
		Optional<List<LivingEntity>> optional = livingEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.MOBS);
		if (!optional.isEmpty()) {
			boolean bl = ((List)optional.get()).stream().anyMatch(livingEntity2 -> this.field_47764.test(livingEntity, livingEntity2));
			if (bl) {
				this.method_55704(livingEntity);
			}
		}
	}

	public void method_55704(T livingEntity) {
		livingEntity.getBrain().remember(this.field_47766, true, (long)this.field_47767);
	}

	public void method_55705(T livingEntity) {
		livingEntity.getBrain().forget(this.field_47766);
	}
}
