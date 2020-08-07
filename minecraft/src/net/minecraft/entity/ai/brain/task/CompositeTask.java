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
import net.minecraft.util.collection.WeightedList;

public class CompositeTask<E extends LivingEntity> extends Task<E> {
	private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
	private final CompositeTask.Order order;
	private final CompositeTask.RunMode runMode;
	private final WeightedList<Task<? super E>> tasks = new WeightedList<>();

	public CompositeTask(
		Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState,
		Set<MemoryModuleType<?>> memoriesToForgetWhenStopped,
		CompositeTask.Order order,
		CompositeTask.RunMode runMode,
		List<Pair<Task<? super E>, Integer>> tasks
	) {
		super(requiredMemoryState);
		this.memoriesToForgetWhenStopped = memoriesToForgetWhenStopped;
		this.order = order;
		this.runMode = runMode;
		tasks.forEach(pair -> this.tasks.add((Task<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).anyMatch(task -> task.shouldKeepRunning(world, entity, time));
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		this.order.apply(this.tasks);
		this.runMode.run(this.tasks, world, entity, time);
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.tick(world, entity, time));
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.stop(world, entity, time));
		this.memoriesToForgetWhenStopped.forEach(entity.getBrain()::forget);
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

		private final Consumer<WeightedList<?>> listModifier;

		private Order(Consumer<WeightedList<?>> listModifier) {
			this.listModifier = listModifier;
		}

		public void apply(WeightedList<?> list) {
			this.listModifier.accept(list);
		}
	}

	static enum RunMode {
		field_18855 {
			@Override
			public <E extends LivingEntity> void run(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
				tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18337).filter(task -> task.tryStarting(world, entity, time)).findFirst();
			}
		},
		field_18856 {
			@Override
			public <E extends LivingEntity> void run(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
				tasks.stream().filter(task -> task.getStatus() == Task.Status.field_18337).forEach(task -> task.tryStarting(world, entity, time));
			}
		};

		private RunMode() {
		}

		public abstract <E extends LivingEntity> void run(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time);
	}
}
