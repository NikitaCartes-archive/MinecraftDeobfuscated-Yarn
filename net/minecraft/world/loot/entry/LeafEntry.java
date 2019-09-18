/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.class_4570;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.FunctionConsumerBuilder;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.LootFunction;
import net.minecraft.world.loot.function.LootFunctions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LeafEntry
extends LootEntry {
    protected final int weight;
    protected final int quality;
    protected final LootFunction[] functions;
    private final BiFunction<ItemStack, LootContext, ItemStack> compiledFunctions;
    private final LootChoice choice = new Choice(){

        @Override
        public void drop(Consumer<ItemStack> consumer, LootContext lootContext) {
            LeafEntry.this.drop(LootFunction.apply(LeafEntry.this.compiledFunctions, consumer, lootContext), lootContext);
        }
    };

    protected LeafEntry(int i, int j, class_4570[] args, LootFunction[] lootFunctions) {
        super(args);
        this.weight = i;
        this.quality = j;
        this.functions = lootFunctions;
        this.compiledFunctions = LootFunctions.join(lootFunctions);
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        super.check(lootTableReporter);
        for (int i = 0; i < this.functions.length; ++i) {
            this.functions[i].check(lootTableReporter.makeChild(".functions[" + i + "]"));
        }
    }

    protected abstract void drop(Consumer<ItemStack> var1, LootContext var2);

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

    public static abstract class Serializer<T extends LeafEntry>
    extends LootEntry.Serializer<T> {
        public Serializer(Identifier identifier, Class<T> class_) {
            super(identifier, class_);
        }

        public void method_442(JsonObject jsonObject, T leafEntry, JsonSerializationContext jsonSerializationContext) {
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

        public final T method_441(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            int i = JsonHelper.getInt(jsonObject, "weight", 1);
            int j = JsonHelper.getInt(jsonObject, "quality", 0);
            LootFunction[] lootFunctions = JsonHelper.deserialize(jsonObject, "functions", new LootFunction[0], jsonDeserializationContext, LootFunction[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, lootFunctions);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, class_4570[] var5, LootFunction[] var6);

        @Override
        public /* synthetic */ LootEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            return this.method_441(jsonObject, jsonDeserializationContext, args);
        }
    }

    static class BasicBuilder
    extends Builder<BasicBuilder> {
        private final Factory factory;

        public BasicBuilder(Factory factory) {
            this.factory = factory;
        }

        protected BasicBuilder method_440() {
            return this;
        }

        @Override
        public LootEntry build() {
            return this.factory.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
        }

        @Override
        protected /* synthetic */ LootEntry.Builder getThisBuilder() {
            return this.method_440();
        }
    }

    @FunctionalInterface
    public static interface Factory {
        public LeafEntry build(int var1, int var2, class_4570[] var3, LootFunction[] var4);
    }

    public static abstract class Builder<T extends Builder<T>>
    extends LootEntry.Builder<T>
    implements FunctionConsumerBuilder<T> {
        protected int weight = 1;
        protected int quality = 0;
        private final List<LootFunction> functions = Lists.newArrayList();

        public T method_438(LootFunction.Builder builder) {
            this.functions.add(builder.build());
            return (T)((Builder)this.getThisBuilder());
        }

        protected LootFunction[] getFunctions() {
            return this.functions.toArray(new LootFunction[0]);
        }

        public T setWeight(int i) {
            this.weight = i;
            return (T)((Builder)this.getThisBuilder());
        }

        public T setQuality(int i) {
            this.quality = i;
            return (T)((Builder)this.getThisBuilder());
        }

        @Override
        public /* synthetic */ Object withFunction(LootFunction.Builder builder) {
            return this.method_438(builder);
        }
    }

    public abstract class Choice
    implements LootChoice {
        protected Choice() {
        }

        @Override
        public int getWeight(float f) {
            return Math.max(MathHelper.floor((float)LeafEntry.this.weight + (float)LeafEntry.this.quality * f), 0);
        }
    }
}

