/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.class_5330;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.SequenceEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializable;
import net.minecraft.util.registry.Registry;

public class LootPoolEntryTypes {
    public static final LootPoolEntryType EMPTY = LootPoolEntryTypes.register("empty", new EmptyEntry.Serializer());
    public static final LootPoolEntryType ITEM = LootPoolEntryTypes.register("item", new ItemEntry.Serializer());
    public static final LootPoolEntryType LOOT_TABLE = LootPoolEntryTypes.register("loot_table", new LootTableEntry.Serializer());
    public static final LootPoolEntryType DYNAMIC = LootPoolEntryTypes.register("dynamic", new DynamicEntry.Serializer());
    public static final LootPoolEntryType TAG = LootPoolEntryTypes.register("tag", new TagEntry.Serializer());
    public static final LootPoolEntryType ALTERNATIVES = LootPoolEntryTypes.register("alternatives", CombinedEntry.createSerializer(AlternativeEntry::new));
    public static final LootPoolEntryType SEQUENCE = LootPoolEntryTypes.register("sequence", CombinedEntry.createSerializer(GroupEntry::new));
    public static final LootPoolEntryType GROUP = LootPoolEntryTypes.register("group", CombinedEntry.createSerializer(SequenceEntry::new));

    private static LootPoolEntryType register(String id, JsonSerializable<? extends LootPoolEntry> jsonSerializable) {
        return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new Identifier(id), new LootPoolEntryType(jsonSerializable));
    }

    public static Object method_29316() {
        return class_5330.method_29306(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootPoolEntry::method_29318).method_29307();
    }
}

