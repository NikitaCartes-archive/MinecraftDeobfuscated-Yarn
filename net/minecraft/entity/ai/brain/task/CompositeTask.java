/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WeightedList;

public class CompositeTask<E extends LivingEntity>
extends Task<E> {
    private final Set<MemoryModuleType<?>> memoriesToForgetWhenStopped;
    private final Order order;
    private final RunMode runMode;
    private final WeightedList<Task<? super E>> tasks = new WeightedList();

    public CompositeTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, Set<MemoryModuleType<?>> memoriesToForgetWhenStopped, Order order, RunMode runMode, List<Pair<Task<? super E>, Integer>> tasks) {
        super(requiredMemoryState);
        this.memoriesToForgetWhenStopped = memoriesToForgetWhenStopped;
        this.order = order;
        this.runMode = runMode;
        tasks.forEach(pair -> this.tasks.add((Task<E>)pair.getFirst(), (Integer)pair.getSecond()));
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
        return this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).anyMatch(task -> task.shouldKeepRunning(world, entity, time));
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
        this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).forEach(task -> task.tick(world, entity, time));
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E livingEntity, long time) {
        this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).forEach(task -> task.stop(serverWorld, livingEntity, time));
        this.memoriesToForgetWhenStopped.forEach(((LivingEntity)livingEntity).getBrain()::forget);
    }

    @Override
    public String toString() {
        Set set = this.tasks.stream().filter(task -> task.getStatus() == Task.Status.RUNNING).collect(Collectors.toSet());
        return "(" + this.getClass().getSimpleName() + "): " + set;
    }

    static enum RunMode {
        RUN_ONE{

            @Override
            public <E extends LivingEntity> void run(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
                tasks.stream().filter(task -> task.getStatus() == Task.Status.STOPPED).filter(task -> task.tryStarting(world, entity, time)).findFirst();
            }
        }
        ,
        TRY_ALL{

            @Override
            public <E extends LivingEntity> void run(WeightedList<Task<? super E>> tasks, ServerWorld world, E entity, long time) {
                tasks.stream().filter(task -> task.getStatus() == Task.Status.STOPPED).forEach(task -> task.tryStarting(world, entity, time));
            }
        };


        public abstract <E extends LivingEntity> void run(WeightedList<Task<? super E>> var1, ServerWorld var2, E var3, long var4);
    }

    static enum Order {
        ORDERED(weightedList -> {}),
        SHUFFLED(WeightedList::shuffle);

        private final Consumer<WeightedList<?>> consumer;

        private Order(Consumer<WeightedList<?>> consumer) {
            this.consumer = consumer;
        }

        public void apply(WeightedList<?> list) {
            this.consumer.accept(list);
        }
    }
}

