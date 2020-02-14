package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class class_4809<E extends MobEntity, T> extends Task<E> {
	private final Predicate<E> field_22288;
	private final MemoryModuleType<? extends T> field_22289;
	private final MemoryModuleType<T> field_22290;
	private final class_4801 field_22291;

	public class_4809(Predicate<E> predicate, MemoryModuleType<? extends T> memoryModuleType, MemoryModuleType<T> memoryModuleType2, class_4801 arg) {
		super(ImmutableMap.of(memoryModuleType2, MemoryModuleState.VALUE_PRESENT, memoryModuleType, MemoryModuleState.VALUE_ABSENT));
		this.field_22288 = predicate;
		this.field_22289 = memoryModuleType;
		this.field_22290 = memoryModuleType2;
		this.field_22291 = arg;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		return this.field_22288.test(mobEntity);
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		Brain<?> brain = mobEntity.getBrain();
		brain.method_24525(this.field_22290, (T)brain.getOptionalMemory(this.field_22289).get(), l, (long)this.field_22291.method_24503(serverWorld.random));
	}
}
