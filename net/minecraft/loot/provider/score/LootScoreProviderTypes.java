/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.provider.score;

import net.minecraft.loot.provider.score.ContextLootScoreProvider;
import net.minecraft.loot.provider.score.FixedLootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootScoreProviderTypes {
    public static final LootScoreProviderType FIXED = LootScoreProviderTypes.register("fixed", new FixedLootScoreProvider.Serializer());
    public static final LootScoreProviderType CONTEXT = LootScoreProviderTypes.register("context", new ContextLootScoreProvider.class_5666());

    private static LootScoreProviderType register(String id, JsonSerializer<? extends LootScoreProvider> jsonSerializer) {
        return Registry.register(Registry.LOOT_SCORE_PROVIDER_TYPE, new Identifier(id), new LootScoreProviderType(jsonSerializer));
    }

    public static Object method_32478() {
        return JsonSerializing.createTypeHandler(Registry.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", LootScoreProvider::getType).method_32385(CONTEXT, new ContextLootScoreProvider.class_5665()).createGsonSerializer();
    }
}

