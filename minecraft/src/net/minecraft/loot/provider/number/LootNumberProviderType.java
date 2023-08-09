package net.minecraft.loot.provider.number;

import com.mojang.serialization.Codec;

public record LootNumberProviderType(Codec<? extends LootNumberProvider> codec) {
}
