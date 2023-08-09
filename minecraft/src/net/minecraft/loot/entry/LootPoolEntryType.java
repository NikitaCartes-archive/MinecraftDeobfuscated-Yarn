package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;

public record LootPoolEntryType(Codec<? extends LootPoolEntry> codec) {
}
