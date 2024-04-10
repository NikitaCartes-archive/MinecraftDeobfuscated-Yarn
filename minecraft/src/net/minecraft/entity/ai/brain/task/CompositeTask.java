package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.WeightedList;

public class CompositeTask<E extends LivingEntity> implements Task<E> {
	private final Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState;
	private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
	private final CompositeTask.Order order;
	private final CompositeTask.RunMode runMode;
	private final WeightedList<Task<? super E>> tasks = new WeightedList<>();
	private MultiTickTask.Status status = MultiTickTask.Status.STOPPED;

	public CompositeTask(
		Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState,
		Set<MemoryModuleType<?>> memoriesToForgetWhenStopped,
		CompositeTask.Order order,
		CompositeTask.RunMode runMode,
		List<Pair<? extends Task<? super E>, Integer>> tasks
	) {
		this.requiredMemoryState = requiredMemoryState;
		this.memoriesToForgetWhenStopped = memoriesToForgetWhenStopped;
		this.order = order;
		this.runMode = runMode;
		tasks.forEach(task -> this.tasks.add((Task<? super E>)task.getFirst(), (Integer)task.getSecond()));
	}

	@Override
	public MultiTickTask.Status getStatus() {
		return this.status;
	}

	private boolean shouldStart(E entity) {
		for (Entry<MemoryModuleType<?>, MemoryModuleState> entry : this.requiredMemoryState.entrySet()) {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)entry.getKey();
			MemoryModuleState memoryModuleState = (MemoryModuleState)entry.getValue();
			if (!entity.getBrain().isMemoryInState(memoryModuleType, memoryModuleState)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public final boolean tryStarting(ServerWorld world, E entity, long time) {
		if (this.shouldStart(entity)) {
			this.status = MultiTickTask.Status.RUNNING;
			this.order.apply(this.tasks);
			this.runMode.run(this.tasks.stream(), world, entity, time);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public final void tick(ServerWorld world, E entity, long time) {
		this.tasks.stream().filter(task -> task.getStatus() == MultiTickTask.Status.RUNNING).forEach(task -> task.tick(world, entity, time));
		if (this.tasks.stream().noneMatch(task -> task.getStatus() == MultiTickTask.Status.RUNNING)) {
			this.stop(world, entity, time);
		}
	}

	@Override
	public final void stop(ServerWorld world, E entity, long time) {
		this.status = MultiTickTask.Status.STOPPED;
		this.tasks.stream().filter(task -> task.getStatus() == MultiTickTask.Status.RUNNING).forEach(task -> task.stop(world, entity, time));
		this.memoriesToForgetWhenStopped.forEach(entity.getBrain()::forget);
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public String toString() {
		Set<? extends Task<? super E>> set = (Set<? extends Task<? super E>>)this.tasks
			.stream()
			.filter(task -> task.getStatus() == MultiTickTask.Status.RUNNING)
			.collect(Collectors.toSet());
		return "(" + this.getClass().getSimpleName() + "): " + set;
	}

	public static enum Order {
		ORDERED(list -> {
		}),
		SHUFFLED(WeightedList::shuffle);

		private final Consumer<WeightedList<?>> listModifier;

		private Order(final Consumer<WeightedList<?>> listModifier) {
			this.listModifier = listModifier;
		}

		public void apply(WeightedList<?> list) {
			this.listModifier.accept(list);
		}
	}

	public static enum RunMode {
		RUN_ONE {
			@Override
			public <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
				tasks.filter(task -> task.getStatus() == MultiTickTask.Status.STOPPED).filter(task -> task.tryStarting(world, entity, time)).findFirst();
			}
		},
		TRY_ALL {
			@Override
			public <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
				tasks.filter(task -> task.getStatus() == MultiTickTask.Status.STOPPED).forEach(task -> task.tryStarting(world, entity, time));
			}
		};

		public abstract <E extends LivingEntity> void run(Stream<Task<? super E>> tasks, ServerWorld world, E entity, long time);
	}
}
