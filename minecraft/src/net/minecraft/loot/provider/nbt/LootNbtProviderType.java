package net.minecraft.loot.provider.nbt;

import com.mojang.serialization.Codec;

public record LootNbtProviderType(Codec<? extends LootNbtProvider> codec) {
}
