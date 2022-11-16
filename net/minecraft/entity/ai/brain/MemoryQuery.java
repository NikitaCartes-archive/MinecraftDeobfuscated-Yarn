/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import org.jetbrains.annotations.Nullable;

/**
 * A query of an entity's brain. There are three types, with each querying a different
 * value. If a query fails, the task does not run.
 * 
 * @see MemoryQueryResult
 * @see net.minecraft.entity.ai.brain.task.TaskTriggerer.TaskContext
 */
public interface MemoryQuery<F extends K1, Value> {
    public MemoryModuleType<Value> memory();

    public MemoryModuleState getState();

    @Nullable
    public MemoryQueryResult<F, Value> toQueryResult(Brain<?> var1, java.util.Optional<Value> var2);

    public record Absent<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<Const.Mu<Unit>, Value>
    {
        @Override
        public MemoryModuleState getState() {
            return MemoryModuleState.VALUE_ABSENT;
        }

        @Override
        public MemoryQueryResult<Const.Mu<Unit>, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
            if (value.isPresent()) {
                return null;
            }
            return new MemoryQueryResult<Unit, Value>(brain, this.memory, Const.create(Unit.INSTANCE));
        }
    }

    public record Value<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<IdF.Mu, Value>
    {
        @Override
        public MemoryModuleState getState() {
            return MemoryModuleState.VALUE_PRESENT;
        }

        @Override
        public MemoryQueryResult<IdF.Mu, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
            if (value.isEmpty()) {
                return null;
            }
            return new MemoryQueryResult(brain, this.memory, IdF.create(value.get()));
        }
    }

    public record Optional<Value>(MemoryModuleType<Value> memory) implements MemoryQuery<OptionalBox.Mu, Value>
    {
        @Override
        public MemoryModuleState getState() {
            return MemoryModuleState.REGISTERED;
        }

        @Override
        public MemoryQueryResult<OptionalBox.Mu, Value> toQueryResult(Brain<?> brain, java.util.Optional<Value> value) {
            return new MemoryQueryResult(brain, this.memory, OptionalBox.create(value));
        }
    }
}

