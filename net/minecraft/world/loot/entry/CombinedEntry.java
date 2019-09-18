/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.class_4570;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.LootChoice;
import net.minecraft.world.loot.LootTableReporter;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.EntryCombiner;
import net.minecraft.world.loot.entry.LootEntry;

public abstract class CombinedEntry
extends LootEntry {
    protected final LootEntry[] children;
    private final EntryCombiner predicate;

    protected CombinedEntry(LootEntry[] lootEntrys, class_4570[] args) {
        super(args);
        this.children = lootEntrys;
        this.predicate = this.combine(lootEntrys);
    }

    @Override
    public void check(LootTableReporter lootTableReporter) {
        super.check(lootTableReporter);
        if (this.children.length == 0) {
            lootTableReporter.report("Empty children list");
        }
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].check(lootTableReporter.makeChild(".entry[" + i + "]"));
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

    public static <T extends CombinedEntry> Serializer<T> createSerializer(Identifier identifier, Class<T> class_, final Factory<T> factory) {
        return new Serializer<T>(identifier, class_){

            @Override
            protected T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] lootEntrys, class_4570[] args) {
                return factory.create(lootEntrys, args);
            }
        };
    }

    public static abstract class Serializer<T extends CombinedEntry>
    extends LootEntry.Serializer<T> {
        public Serializer(Identifier identifier, Class<T> class_) {
            super(identifier, class_);
        }

        public void method_397(JsonObject jsonObject, T combinedEntry, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("children", jsonSerializationContext.serialize(((CombinedEntry)combinedEntry).children));
        }

        public final T method_396(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            LootEntry[] lootEntrys = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lootEntrys, args);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootEntry[] var3, class_4570[] var4);

        @Override
        public /* synthetic */ LootEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            return this.method_396(jsonObject, jsonDeserializationContext, args);
        }
    }

    @FunctionalInterface
    public static interface Factory<T extends CombinedEntry> {
        public T create(LootEntry[] var1, class_4570[] var2);
    }
}

