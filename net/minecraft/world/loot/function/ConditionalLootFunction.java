/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.condition.LootConditions;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.function.LootFunction;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConditionalLootFunction
implements LootFunction {
    protected final class_4570[] conditions;
    private final Predicate<LootContext> predicate;

    protected ConditionalLootFunction(class_4570[] args) {
        this.conditions = args;
        this.predicate = LootConditions.joinAnd(args);
    }

    public final ItemStack method_521(ItemStack itemStack, LootContext lootContext) {
        return this.predicate.test(lootContext) ? this.process(itemStack, lootContext) : itemStack;
    }

    protected abstract ItemStack process(ItemStack var1, LootContext var2);

    @Override
    public void check(LootTableReporter lootTableReporter) {
        LootFunction.super.check(lootTableReporter);
        for (int i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].check(lootTableReporter.makeChild(".conditions[" + i + "]"));
        }
    }

    protected static Builder<?> builder(Function<class_4570[], LootFunction> function) {
        return new Joiner(function);
    }

    @Override
    public /* synthetic */ Object apply(Object object, Object object2) {
        return this.method_521((ItemStack)object, (LootContext)object2);
    }

    public static abstract class Factory<T extends ConditionalLootFunction>
    extends LootFunction.Factory<T> {
        public Factory(Identifier identifier, Class<T> class_) {
            super(identifier, class_);
        }

        public void method_529(JsonObject jsonObject, T conditionalLootFunction, JsonSerializationContext jsonSerializationContext) {
            if (!ArrayUtils.isEmpty(((ConditionalLootFunction)conditionalLootFunction).conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize(((ConditionalLootFunction)conditionalLootFunction).conditions));
            }
        }

        public final T method_528(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            class_4570[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new class_4570[0], jsonDeserializationContext, class_4570[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, class_4570[] var3);

        @Override
        public /* synthetic */ LootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.method_528(jsonObject, jsonDeserializationContext);
        }
    }

    static final class Joiner
    extends Builder<Joiner> {
        private final Function<class_4570[], LootFunction> joiner;

        public Joiner(Function<class_4570[], LootFunction> function) {
            this.joiner = function;
        }

        protected Joiner method_527() {
            return this;
        }

        @Override
        public LootFunction build() {
            return this.joiner.apply(this.getConditions());
        }

        @Override
        protected /* synthetic */ Builder getThisBuilder() {
            return this.method_527();
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    implements LootFunction.Builder,
    ConditionConsumerBuilder<T> {
        private final List<class_4570> conditionList = Lists.newArrayList();

        public T method_524(class_4570.Builder builder) {
            this.conditionList.add(builder.build());
            return this.getThisBuilder();
        }

        public final T method_525() {
            return this.getThisBuilder();
        }

        protected abstract T getThisBuilder();

        protected class_4570[] getConditions() {
            return this.conditionList.toArray(new class_4570[0]);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.method_525();
        }

        @Override
        public /* synthetic */ Object withCondition(class_4570.Builder builder) {
            return this.method_524(builder);
        }
    }
}

