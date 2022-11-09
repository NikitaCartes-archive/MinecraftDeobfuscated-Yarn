package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.kinds.Const.Mu;
import com.mojang.datafixers.util.Unit;
import javax.annotation.Nullable;

/**
 * A query of an entity's brain. There are three types, with each querying a different
 * value. If a query fails, the task does not run.
 * 
 * @see MemoryQueryResult
 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.Context
 */
public interface MemoryQuery<F extends K1, Value> {
	MemoryModuleType<Value> memory();

	MemoryModuleState getState();

	@Nullable
	MemoryQueryResult<F, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value);

	/**
	 * A query that succeeds if a value is <strong>not</strong> present in the memory. The
	 * query result is always {@code Unit.INSTANCE}.
	 * 
	 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.Context#queryMemoryAbsent
	 */
	public static record Absent<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<Mu<Unit>, Value> {
		@Override
		public MemoryModuleState getState() {
			return MemoryModuleState.VALUE_ABSENT;
		}

		@Override
		public MemoryQueryResult<Mu<Unit>, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
			return value.isPresent() ? null : new MemoryQueryResult<>(brain, this.memory, Const.create(Unit.INSTANCE));
		}
	}

	/**
	 * A query that always succeeds. The value is an optional that contains the value if it
	 * is present in the memory.
	 * 
	 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.Context#queryMemoryOptional
	 */
	public static record Optional<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> {
		@Override
		public MemoryModuleState getState() {
			return MemoryModuleState.REGISTERED;
		}

		@Override
		public MemoryQueryResult<com.mojang.datafixers.kinds.OptionalBox.Mu, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
			return new MemoryQueryResult<>(brain, this.memory, OptionalBox.create(value));
		}
	}

	/**
	 * A query that succeeds if a value is present in the memory. The result is the queried value.
	 * 
	 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.Context#queryMemoryValue
	 */
	public static record Value<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<com.mojang.datafixers.kinds.IdF.Mu, Value> {
		@Override
		public MemoryModuleState getState() {
			return MemoryModuleState.VALUE_PRESENT;
		}

		@Override
		public MemoryQueryResult<com.mojang.datafixers.kinds.IdF.Mu, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
			return value.isEmpty() ? null : new MemoryQueryResult<>(brain, this.memory, IdF.create((Value)value.get()));
		}
	}
}
