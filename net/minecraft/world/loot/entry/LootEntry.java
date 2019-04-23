/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.LootChoiceProvider;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextType;
import net.minecraft.world.loot.entry.AlternativeEntry;

public abstract class LootEntry
implements LootChoiceProvider {
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> conditionPredicate;

    protected LootEntry(LootCondition[] lootConditions) {
        this.conditions = lootConditions;
        this.conditionPredicate = LootConditions.joinAnd(lootConditions);
    }

    public void check(LootTableReporter lootTableReporter, Function<Identifier, LootSupplier> function, Set<Identifier> set, LootContextType lootContextType) {
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(lootTableReporter.makeChild(".condition[" + i + "]"), function, set, lootContextType);
        }
    }

    protected final boolean test(LootContext lootContext) {
        return this.conditionPredicate.test(lootContext);
    }

    public static abstract class Serializer<T extends LootEntry> {
        private final Identifier id;
        private final Class<T> type;

        protected Serializer(Identifier identifier, Class<T> class_) {
            this.id = identifier;
            this.type = class_;
        }

        public Identifier getIdentifier() {
            return this.id;
        }

        public Class<T> getType() {
            return this.type;
        }

        public abstract void toJson(JsonObject var1, T var2, JsonSerializationContext var3);

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);
    }

    public static abstract class Builder<T extends Builder<T>>
    implements ConditionConsumerBuilder<T> {
        private final List<LootCondition> children = Lists.newArrayList();

        protected abstract T getThisBuilder();

        public T method_421(LootCondition.Builder builder) {
            this.children.add(builder.build());
            return this.getThisBuilder();
        }

        public final T method_416() {
            return this.getThisBuilder();
        }

        protected LootCondition[] getConditions() {
            return this.children.toArray(new LootCondition[0]);
        }

        public AlternativeEntry.Builder withChild(Builder<?> builder) {
            return new AlternativeEntry.Builder(this, builder);
        }

        public abstract LootEntry build();

        @Override
        public /* synthetic */ Object getThis() {
            return this.method_416();
        }

        @Override
        public /* synthetic */ Object withCondition(LootCondition.Builder builder) {
            return this.method_421(builder);
        }
    }
}

