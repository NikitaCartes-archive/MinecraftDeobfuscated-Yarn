package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;

public record LootFunctionType<T extends LootFunction>(MapCodec<T> codec) {
}
