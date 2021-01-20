package net.minecraft.loot.provider.score;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootScoreProviderTypes {
	public static final LootScoreProviderType FIXED = register("fixed", new FixedLootScoreProvider.Serializer());
	public static final LootScoreProviderType CONTEXT = register("context", new ContextLootScoreProvider.Serializer());

	private static LootScoreProviderType register(String id, JsonSerializer<? extends LootScoreProvider> jsonSerializer) {
		return Registry.register(Registry.LOOT_SCORE_PROVIDER_TYPE, new Identifier(id), new LootScoreProviderType(jsonSerializer));
	}

	public static Object method_32478() {
		return JsonSerializing.<LootScoreProvider, LootScoreProviderType>createTypeHandler(
				Registry.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", LootScoreProvider::getType
			)
			.method_32385(CONTEXT, new ContextLootScoreProvider.CustomSerializer())
			.createGsonSerializer();
	}
}
