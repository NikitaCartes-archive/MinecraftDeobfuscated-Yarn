package net.minecraft.loot.entry;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryType(MapCodec<? extends LootPoolEntry> codec) {
}
