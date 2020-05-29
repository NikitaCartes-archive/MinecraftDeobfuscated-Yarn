/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.JsonSerializable;
import net.minecraft.util.JsonSerializableType;

public class LootPoolEntryType
extends JsonSerializableType<LootPoolEntry> {
    public LootPoolEntryType(JsonSerializable<? extends LootPoolEntry> jsonSerializable) {
        super(jsonSerializable);
    }
}

