package net.minecraft.loot.provider.number;

import com.mojang.serialization.MapCodec;

public record LootNumberProviderType(MapCodec<? extends LootNumberProvider> codec) {
}
