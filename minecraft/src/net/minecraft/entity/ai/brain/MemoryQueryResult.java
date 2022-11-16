package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.K1;
import java.util.Optional;

/**
 * The result of a {@link MemoryQuery}. This is passed as a lambda argument to
 * {@link net.minecraft.entity.ai.brain.task.TaskTriggerer#task}. Use
 * {@link net.minecraft.entity.ai.brain.task.TaskTriggerer.TaskContext#getValue} to
 * get the value.
 * 
 * <p>It is also possible to set or forget the stored memory value using methods in
 * this class.
 * 
 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.TaskContext#getValue
 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.TaskContext#getOptionalValue
 */
public final class MemoryQueryResult<F extends K1, Value> {
	private final Brain<?> brain;
	private final MemoryModuleType<Value> memory;
	private final App<F, Value> value;

	public MemoryQueryResult(Brain<?> brain, MemoryModuleType<Value> memory, App<F, Value> value) {
		this.brain = brain;
		this.memory = memory;
		this.value = value;
	}

	public App<F, Value> getValue() {
		return this.value;
	}

	public void remember(Value value) {
		this.brain.remember(this.memory, Optional.of(value));
	}

	public void remember(Optional<Value> value) {
		this.brain.remember(this.memory, value);
	}

	public void remember(Value value, long expiry) {
		this.brain.remember(this.memory, value, expiry);
	}

	public void forget() {
		this.brain.forget(this.memory);
	}
}
