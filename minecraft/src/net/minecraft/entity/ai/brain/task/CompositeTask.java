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
		return this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).anyMatch(task -> task.shouldKeepRunning(serverWorld, livingEntity, l));
	}

	@Override
	protected boolean isTimeLimitExceeded(long l) {
		return false;
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		this.order.apply(this.tasks);
		this.runMode.run(this.tasks, serverWorld, livingEntity, l);
	}

	@Override
	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	@Override
	protected void finishRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).forEach(task -> task.stop(serverWorld, livingEntity, l));
		this.memoriesToForgetWhenStopped.forEach(livingEntity.getBrain()::forget);
	}

	@Override
	public String toString() {
		Set<? extends Task<? super E>> set = (Set<? extends Task<? super E>>)this.tasks
			.stream()
			.filter(task -> task.getStatus() == Task.Status.RUNNING)
			.collect(Collectors.toSet());
		return "(" + this.getClass().getSimpleName() + "): " + set;
	}

	static enum Order {
		ORDERED(weightedList -> {
		}),
		SHUFFLED(WeightedList::shuffle);

		private final Consumer<WeightedList<?>> consumer;

		private Order(Consumer<WeightedList<?>> consumer) {
			this.consumer = consumer;
		}

		public void apply(WeightedList<?> weightedList) {
			this.consumer.accept(weightedList);
		}
	}

	static enum RunMode {
		RUN_ONE {
			@Override
			public <E extends LivingEntity> void run(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l) {
				weightedList.stream().filter(task -> task.getStatus() == Task.Status.STOPPED).filter(task -> task.tryStarting(serverWorld, livingEntity, l)).findFirst();
			}
		},
		TRY_ALL {
			@Override
			public <E extends LivingEntity> void run(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l) {
				weightedList.stream().filter(task -> task.getStatus() == Task.Status.STOPPED).forEach(task -> task.tryStarting(serverWorld, livingEntity, l));
			}
		};

		private RunMode() {
		}

		public abstract <E extends LivingEntity> void run(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l);
	}
}
