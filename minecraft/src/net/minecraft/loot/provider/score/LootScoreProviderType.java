package net.minecraft.loot.provider.score;

import com.mojang.serialization.MapCodec;

public record LootScoreProviderType(MapCodec<? extends LootScoreProvider> codec) {
}
