package net.minecraft.loot;

import net.minecraft.util.Identifier;

public record LootDataKey<T>(LootDataType<T> type, Identifier id) {
}
