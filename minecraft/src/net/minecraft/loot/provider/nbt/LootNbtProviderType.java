package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.MapCodec;

public record LootNbtProviderType(MapCodec<? extends LootNbtProvider> codec) {
}
