package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;

public record LootFunctionType(MapCodec<? extends LootFunction> codec) {
}
