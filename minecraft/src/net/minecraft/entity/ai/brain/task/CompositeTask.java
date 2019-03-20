package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedList;

public class CompositeTask<E extends LivingEntity> extends Task<E> {
	private final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> field_18343;
	private final Set<MemoryModuleType<?>> field_18344;
	private final CompositeTask.Order order;
	private final CompositeTask.class_4216 runMode;
	private final WeightedList<Task<? super E>> field_18347 = new WeightedList<>();

	public CompositeTask(
		Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set,
		Set<MemoryModuleType<?>> set2,
		CompositeTask.Order order,
		CompositeTask.class_4216 arg,
		List<Pair<Task<? super E>, Integer>> list
	) {
		this.field_18343 = set;
		this.field_18344 = set2;
		this.order = order;
		this.runMode = arg;
		list.forEach(pair -> this.field_18347.add((Task<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
		return this.field_18343;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		return this.field_18347
			.stream()
			.filter(task -> task.getStatus() == Task.Status.field_18338)
			.anyMatch(task -> task.shouldKeepRunning(serverWorld, livingEntity, l));
	}

	@Override
	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
		this.order.method_18939(this.field_18347);
		this.runMode.method_19559(this.field_18347, serverWorld, livingEntity, l);
	}

	@Override
	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.tick(serverWorld, livingEntity, l));
	}

	@Override
	protected void stopInternal(ServerWorld serverWorld, E livingEntity, long l) {
		this.field_18347.stream().filter(task -> task.getStatus() == Task.Status.field_18338).forEach(task -> task.stop(serverWorld, livingEntity, l));
		this.field_18344.forEach(livingEntity.getBrain()::forget);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.field_18347 + "]";
	}

	static enum Order {
		field_18348(weightedList -> {
		}),
		field_18349(WeightedList::method_19029);

		private final Consumer<WeightedList<?>> field_18350;

		private Order(Consumer<WeightedList<?>> consumer) {
			this.field_18350 = consumer;
		}

		public void method_18939(WeightedList<?> weightedList) {
			this.field_18350.accept(weightedList);
		}
	}

	static enum class_4216 {
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

		private class_4216() {
		}

		public abstract <E extends LivingEntity> void method_19559(WeightedList<Task<? super E>> weightedList, ServerWorld serverWorld, E livingEntity, long l);
	}
}
