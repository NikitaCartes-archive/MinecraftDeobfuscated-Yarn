package net.minecraft.loot.provider.score;

import com.mojang.serialization.Codec;

public record LootScoreProviderType(Codec<? extends LootScoreProvider> codec) {
}
