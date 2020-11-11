/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.score;

import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootScoreProviderType
extends JsonSerializableType<LootScoreProvider> {
    public LootScoreProviderType(JsonSerializer<? extends LootScoreProvider> jsonSerializer) {
        super(jsonSerializer);
    }
}

