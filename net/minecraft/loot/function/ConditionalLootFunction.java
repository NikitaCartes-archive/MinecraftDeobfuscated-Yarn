/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction
implements LootFunction {
    protected final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;

    protected ConditionalLootFunction(LootCondition[] conditions) {
        this.conditions = conditions;
        this.predicate = LootConditions.joinAnd(conditions);
    }

    @Override
    public final ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
    }

    protected abstract ItemStack process(ItemStack var1, LootContext var2);

    @Override
    public void validate(LootTableReporter reporter) {
        LootFunction.super.validate(reporter);
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(reporter.makeChild(".conditions[" + i + "]"));
        }
    }

    protected static Builder<?> builder(Function<LootCondition[], LootFunction> joiner) {
        return new Joiner(joiner);
    }

    @Override
    public /* synthetic */ Object apply(Object itemStack, Object context) {
        return this.apply((ItemStack)itemStack, (LootContext)context);
    }

    public static abstract class Factory<T extends ConditionalLootFunction>
    extends LootFunction.Factory<T> {
        public Factory(Identifier identifier, Class<T> class_) {
            super(identifier, class_);
        }

        @Override
        public void toJson(JsonObject jsonObject, T conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty(((ConditionalLootFunction)conditionalLootFunction).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize(((ConditionalLootFunction)conditionalLootFunction).conditions));
            }
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lootConditions);
        }

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3);

        @Override
        public /* synthetic */ LootFunction fromJson(JsonObject json, JsonDeserializationContext context) {
            return this.fromJson(json, context);
        }
    }

    static final class Joiner
    extends Builder<Joiner> {
        private final Function<LootCondition[], LootFunction> joiner;

        public Joiner(Function<LootCondition[], LootFunction> joiner) {
            this.joiner = joiner;
        }

        @Override
        protected Joiner getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return this.joiner.apply(this.getConditions());
        }

        @Override
        protected /* synthetic */ Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootFunction.Builder,
    LootConditionConsumingBuilder<T> {
        private final List<LootCondition> conditionList = Lists.newArrayList();

        @Override
        public T conditionally(LootCondition.Builder builder) {
            this.conditionList.add(builder.build());
            return this.getThisBuilder();
        }

        @Override
        public final T getThis() {
            return this.getThisBuilder();
        }

        protected abstract T getThisBuilder();

        protected LootCondition[] getConditions() {
            return this.conditionList.toArray(new LootCondition[0]);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object conditionally(LootCondition.Builder condition) {
            return this.conditionally(condition);
        }
    }
}

