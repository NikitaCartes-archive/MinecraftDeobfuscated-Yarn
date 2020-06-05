/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootPoolEntryType
extends JsonSerializableType<LootPoolEntry> {
    public LootPoolEntryType(JsonSerializer<? extends LootPoolEntry> jsonSerializer) {
        super(jsonSerializer);
    }
}

