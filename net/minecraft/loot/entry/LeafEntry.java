/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LeafEntry
extends LootPoolEntry {
    public static final int field_31847 = 1;
    public static final int field_31848 = 0;
    protected final int weight;
    protected final int quality;
    protected final LootFunction[] functions;
    final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
    private final LootChoice choice = new Choice(){

        @Override
        public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
            LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, lootConsumer, context), context);
        }
    };

    protected LeafEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
        super(conditions);
        this.weight = weight;
        this.quality = quality;
        this.functions = functions;
        this.compiledFunctions = LootFunctionTypes.join(functions);
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].validate(reporter.makeChild(".functions[" + i + "]"));
        }
    }

    protected abstract void generateLoot(Consumer<ItemStack> var1, LootContext var2);

    @Override
    public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
        if (this.test(lootContext)) {
            consumer.accept(this.choice);
            return true;
        }
        return false;
    }

    public static Builder<?> builder(Factory factory) {
        return new BasicBuilder(factory);
    }

    static class BasicBuilder
    extends Builder<BasicBuilder> {
        private final Factory factory;

        public BasicBuilder(Factory factory) {
            this.factory = factory;
        }

        @Override
        protected BasicBuilder getThisBuilder() {
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    @FunctionalInterface
    protected static interface Factory {
        public LeafEntry build(int var1, int var2, LootCondition[] var3, LootFunction[] var4);
    }

    public static abstract class Serializer<T extends LeafEntry>
    extends LootPoolEntry.Serializer<T> {
        @Override
        public void addEntryFields(JsonObject jsonObject, T leafEntry, JsonSerializationContext jsonSerializationContext) {
            if (((LeafEntry)leafEntry).weight != 1) {
                jsonObject.addProperty("weight", ((LeafEntry)leafEntry).weight);
            }
            if (((LeafEntry)leafEntry).quality != 0) {
                jsonObject.addProperty("quality", ((LeafEntry)leafEntry).quality);
            }
            if (!ArrayUtils.isEmpty(((LeafEntry)leafEntry).functions)) {
                jsonObject.add("functions", jsonSerializationContext.serialize(((LeafEntry)leafEntry).functions));
            }
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            int i = JsonHelper.getInt(jsonObject, "weight", 1);
            int j = JsonHelper.getInt(jsonObject, "quality", 0);
            LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, lootConditions, lootFunctions);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootCondition[] var5, LootFunction[] var6);

        @Override
        public /* synthetic */ LootPoolEntry fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootPoolEntry.Builder<T>
    implements LootFunctionConsumingBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootFunction> functions = Lists.newArrayList();

        @Override
        public T apply(LootFunction.Builder builder) {
            this.functions.add(builder.build());
            return (T)((Builder)this.getThisBuilder());
        }

        protected LootFunction[] getFunctions() {
            return this.functions.toArray(new LootFunction[0]);
        }

        public T weight(int weight) {
            this.weight = weight;
            return (T)((Builder)this.getThisBuilder());
        }

        public T quality(int quality) {
            this.quality = quality;
            return (T)((Builder)this.getThisBuilder());
        }

        @Override
        public /* synthetic */ LootFunctionConsumingBuilder getThisFunctionConsumingBuilder() {
            return (LootFunctionConsumingBuilder)((Object)super.getThisConditionConsumingBuilder());
        }

        @Override
        public /* synthetic */ LootFunctionConsumingBuilder apply(LootFunction.Builder function) {
            return this.apply(function);
        }
    }

    protected abstract class Choice
    implements LootChoice {
        protected Choice() {
        }

        @Override
        public int getWeight(float luck) {
            return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * luck), 0);
        }
    }
}

