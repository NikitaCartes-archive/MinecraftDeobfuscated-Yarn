package net.minecraft.loot.function;

import com.mojang.serialization.Codec;

public record LootFunctionType(Codec<? extends LootFunction> codec) {
}
