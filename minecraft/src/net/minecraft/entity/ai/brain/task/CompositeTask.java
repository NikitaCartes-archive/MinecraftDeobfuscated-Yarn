package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.class_4131;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public class CompositeTask<E extends LivingEntity> extends Task<E> {
	private final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> field_18343;
	private final Set<MemoryModuleType<?>> field_18344;
	private final CompositeTask.Order order;
	private final CompositeTask.RunMode runMode;
	private final class_4131<Task<? super E>> field_18347 = new class_4131<>();

	public CompositeTask(
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set,
		Set<MemoryModuleType<?>> set2,
		CompositeTask.Order order,
		CompositeTask.RunMode runMode,
		List<Pair<Task<? super E>, Integer>> list
	) {
		this.field_18343 = set;
		this.field_18344 = set2;
		this.order = order;
		this.runMode = runMode;
		list.forEach(pair -> this.field_18347.method_19031((Task<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return this.field_18343;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		return this.field_18347
			.method_19032()
			.filter(task -> task.getStatus() == Task.Status.field_18338)
			.anyMatch(task -> task.shouldKeepRunning(serverWorld, livingEntity, l));
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		this.order.method_18939(this.field_18347);
		if (this.runMode == CompositeTask.RunMode.field_18352) {
			this.field_18347
				.method_19032()
				.filter(task -> task.getStatus() == Task.Status.field_18337)
				.filter(task -> task.tryStarting(serverWorld, livingEntity, l))
				.findFirst();
		} else if (this.runMode == CompositeTask.RunMode.field_18353) {
			this.field_18347.method_19032().filter(task -> task.getStatus() == Task.Status.field_18337).forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
		}
	}

	@Override
	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347.method_19032().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	@Override
	protected void method_18926(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347.method_19032().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.stop(serverWorld, livingEntity, l));
		Brain<?> brain = livingEntity.getBrain();
		this.field_18344.forEach(brain::forget);
	}

	static enum Order {
		field_18348(arg -> {
		}),
		field_18349(class_4131::method_19029);

		private final Consumer<class_4131<?>> field_18350;

		private Order(Consumer<class_4131<?>> consumer) {
			this.field_18350 = consumer;
		}

		public void method_18939(class_4131<?> arg) {
			this.field_18350.accept(arg);
		}
	}

	static enum RunMode {
		field_18352,
		field_18353;
	}
}
