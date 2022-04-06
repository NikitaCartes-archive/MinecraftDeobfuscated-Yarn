/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
    final LootPoolEntry[] entries;
    final LootCondition[] conditions;
    private final Predicate<LootContext> predicate;
    final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> javaFunctions;
    final LootNumberProvider rolls;
    final LootNumberProvider bonusRolls;

    LootPool(LootPoolEntry[] entries, LootCondition[] conditions, LootFunction[] functions, LootNumberProvider rolls, LootNumberProvider bonusRolls) {
        this.entries = entries;
        this.conditions = conditions;
        this.predicate = LootConditionTypes.joinAnd(conditions);
        this.functions = functions;
        this.javaFunctions = LootFunctionTypes.join(functions);
        this.rolls = rolls;
        this.bonusRolls = bonusRolls;
    }

    private void supplyOnce(Consumer<ItemStack> lootConsumer, LootContext context) {
        AbstractRandom abstractRandom = context.getRandom();
        ArrayList<LootChoice> list = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        for (LootPoolEntry lootPoolEntry : this.entries) {
            lootPoolEntry.expand(context, choice -> {
                int i = choice.getWeight(context.getLuck());
                if (i > 0) {
                    list.add(choice);
                    mutableInt.add(i);
                }
            });
        }
        int i = list.size();
        if (mutableInt.intValue() == 0 || i == 0) {
            return;
        }
        if (i == 1) {
            ((LootChoice)list.get(0)).generateLoot(lootConsumer, context);
            return;
        }
        int j = abstractRandom.nextInt(mutableInt.intValue());
        for (LootChoice lootChoice : list) {
            if ((j -= lootChoice.getWeight(context.getLuck())) >= 0) continue;
            lootChoice.generateLoot(lootConsumer, context);
            return;
        }
    }

    public void addGeneratedLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        if (!this.predicate.test(context)) {
            return;
        }
        Consumer<ItemStack> consumer = LootFunction.apply(this.javaFunctions, lootConsumer, context);
        int i = this.rolls.nextInt(context) + MathHelper.floor(this.bonusRolls.nextFloat(context) * context.getLuck());
        for (int j = 0; j < i; ++j) {
            this.supplyOnce(consumer, context);
        }
    }

    public void validate(LootTableReporter reporter) {
        int i;
        for (i = 0; i < this.conditions.length; ++i) {
            this.conditions[i].validate(reporter.makeChild(".condition[" + i + "]"));
        }
        for (i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(reporter.makeChild(".functions[" + i + "]"));
        }
        for (i = 0; i < this.entries.length; ++i) {
            this.entries[i].validate(reporter.makeChild(".entries[" + i + "]"));
        }
        this.rolls.validate(reporter.makeChild(".rolls"));
        this.bonusRolls.validate(reporter.makeChild(".bonusRolls"));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    implements LootFunctionConsumingBuilder<Builder>,
    LootConditionConsumingBuilder<Builder> {
        private final List<LootPoolEntry> entries = Lists.newArrayList();
        private final List<LootCondition> conditions = Lists.newArrayList();
        private final List<LootFunction> functions = Lists.newArrayList();
        private LootNumberProvider rolls = ConstantLootNumberProvider.create(1.0f);
        private LootNumberProvider bonusRollsRange = ConstantLootNumberProvider.create(0.0f);

        public Builder rolls(LootNumberProvider rolls) {
            this.rolls = rolls;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        public Builder bonusRolls(LootNumberProvider bonusRolls) {
            this.bonusRollsRange = bonusRolls;
            return this;
        }

        public Builder with(LootPoolEntry.Builder<?> entry) {
            this.entries.add(entry.build());
            return this;
        }

        @Override
        public Builder conditionally(LootCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this;
        }

        @Override
        public Builder apply(LootFunction.Builder builder) {
            this.functions.add(builder.build());
            return this;
        }

        public LootPool build() {
            if (this.rolls == null) {
                throw new IllegalArgumentException("Rolls not set");
            }
            return new LootPool(this.entries.toArray(new LootPoolEntry[0]), this.conditions.toArray(new LootCondition[0]), this.functions.toArray(new LootFunction[0]), this.rolls, this.bonusRollsRange);
        }

        @Override
        public /* synthetic */ Object getThis() {
            return this.getThis();
        }

        @Override
        public /* synthetic */ Object apply(LootFunction.Builder function) {
            return this.apply(function);
        }

        @Override
        public /* synthetic */ Object conditionally(LootCondition.Builder condition) {
            return this.conditionally(condition);
        }
    }

    public static class Serializer
    implements JsonDeserializer<LootPool>,
    JsonSerializer<LootPool> {
        @Override
        public LootPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "loot pool");
            LootPoolEntry[] lootPoolEntrys = JsonHelper.deserialize(jsonObject, "entries", jsonDeserializationContext, LootPoolEntry[].class);
            LootCondition[] lootConditions = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            LootNumberProvider lootNumberProvider = JsonHelper.deserialize(jsonObject, "rolls", jsonDeserializationContext, LootNumberProvider.class);
            LootNumberProvider lootNumberProvider2 = JsonHelper.deserialize(jsonObject, "bonus_rolls", ConstantLootNumberProvider.create(0.0f), jsonDeserializationContext, LootNumberProvider.class);
            return new LootPool(lootPoolEntrys, lootConditions, lootFunctions, lootNumberProvider, lootNumberProvider2);
        }

        @Override
        public JsonElement serialize(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("rolls", jsonSerializationContext.serialize(lootPool.rolls));
            jsonObject.add("bonus_rolls", jsonSerializationContext.serialize(lootPool.bonusRolls));
            jsonObject.add("entries", jsonSerializationContext.serialize(lootPool.entries));
            if (!ArrayUtils.isEmpty(lootPool.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize(lootPool.conditions));
            }
            if (!ArrayUtils.isEmpty(lootPool.functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize(lootPool.functions));
            }
            return jsonObject;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
            return this.serialize((LootPool)entry, unused, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement json, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, unused, context);
        }
    }
}

