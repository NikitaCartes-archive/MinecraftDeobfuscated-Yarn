package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedList;

public class CompositeTask<E extends LivingEntity> extends Task<E> {
	private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
	private final CompositeTask.Order order;
	private final CompositeTask.RunMode runMode;
	private final WeightedList<Task<? super E>> tasks = new WeightedList<>();

	public CompositeTask(
		Map<MemoryModuleType<?>, MemoryModuleState> map,
		Set<MemoryModuleType<?>> set,
		CompositeTask.Order order,
		CompositeTask.RunMode runMode,
		List<Pair<Task<? super E>, Integer>> list
	) {
		super(map);
		this.memoriesToForgetWhenStopped = set;
		this.order = order;
		this.runMode = runMode;
		list.forEach(pair -> this.tasks.add((Task<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		return this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).anyMatch(task -> task.shouldKeepRunning(serverWorld, livingEntity, l));
	}

	@Override
	protected boolean isTimeLimitExceeded(long l) {
		return false;
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		this.order.apply(this.tasks);
		this.runMode.method_19559(this.tasks, serverWorld, livingEntity, l);
	}

	@Override
	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	@Override
	protected void finishRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.stop(serverWorld, livingEntity, l));
		this.memoriesToForgetWhenStopped.forEach(livingEntity.getBrain()::forget);
	}

	@Override
	public String toString() {
		Set<? extends Task<? super E>> set = (Set<? extends Task<? super E>>)this.tasks
			.stream()
			.filter(task -> task.getStatus() == Task.Status.field_18338)
			.collect(Collectors.toSet());
		return "(" + this.getClass().getSimpleName() + "): " + set;
	}

	static enum Order {
		field_18348(weightedList -> {
		}),
		field_18349(WeightedList::shuffle);

		private final Consumer<WeightedList<?>> consumer;

		private Order(Consumer<WeightedList<?>> consumer) {
			this.consumer = consumer;
		}

		public void apply(WeightedList<?> weightedList) {
			this.consumer.accept(weightedList);
		}
	}

	static enum RunMode {
		field_18855 {
			@Override
			public <E extends LivingEntity> void method_19559(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l) {
				weightedList.stream()
					.filter(task -> task.getStatus() == Task.Status.field_18337)
					.filter(task -> task.tryStarting(serverWorld, livingEntity, l))
					.findFirst();
			}
		},
		field_18856 {
			@Override
			public <E extends LivingEntity> void method_19559(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l) {
				weightedList.stream().filter(task -> task.getStatus() == Task.Status.field_18337).forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
			}
		};

		private RunMode() {
		}

		public abstract <E extends LivingEntity> void method_19559(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l);
	}
}
