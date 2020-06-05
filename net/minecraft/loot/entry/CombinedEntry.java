/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.JsonHelper;

public abstract class CombinedEntry
extends LootPoolEntry {
    protected final LootPoolEntry[] children;
    private final EntryCombiner predicate;

    protected CombinedEntry(LootPoolEntry[] children, LootCondition[] conditions) {
        super(conditions);
        this.children = children;
        this.predicate = this.combine(children);
    }

    @Override
    public void validate(LootTableReporter reporter) {
        super.validate(reporter);
        if (this.children.length == 0) {
            reporter.report("Empty children list");
        }
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].validate(reporter.makeChild(".entry[" + i + "]"));
        }
    }

    protected abstract EntryCombiner combine(EntryCombiner[] var1);

    @Override
    public final boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
        if (!this.test(lootContext)) {
            return false;
        }
        return this.predicate.expand(lootContext, consumer);
    }

    public static <T extends CombinedEntry> LootPoolEntry.Serializer<T> createSerializer(final Factory<T> factory) {
        return new LootPoolEntry.Serializer<T>(){

            @Override
            public void addEntryFields(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
                jsonObject.add("children", jsonSerializationContext.serialize(((CombinedEntry)combinedEntry).children));
            }

            @Override
            public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
                LootPoolEntry[] lootPoolEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootPoolEntry[].class);
                return factory.create(lootPoolEntrys, lootConditions);
            }

            @Override
            public /* synthetic */ LootPoolEntry fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
                return this.fromJson(json, context, conditions);
            }
        };
    }

    @FunctionalInterface
    public static interface Factory<T extends CombinedEntry> {
        public T create(LootPoolEntry[] var1, LootCondition[] var2);
    }
}

