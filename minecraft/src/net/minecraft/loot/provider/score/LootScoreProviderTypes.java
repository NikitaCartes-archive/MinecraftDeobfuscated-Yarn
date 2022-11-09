package net.minecraft.loot.provider.score;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class LootScoreProviderTypes {
	public static final LootScoreProviderType FIXED = register("fixed", new FixedLootScoreProvider.Serializer());
	public static final LootScoreProviderType CONTEXT = register("context", new ContextLootScoreProvider.Serializer());

	private static LootScoreProviderType register(String id, JsonSerializer<? extends LootScoreProvider> jsonSerializer) {
		return Registry.register(Registries.LOOT_SCORE_PROVIDER_TYPE, new Identifier(id), new LootScoreProviderType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootScoreProvider, LootScoreProviderType>createSerializerBuilder(
				Registries.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", LootScoreProvider::getType
			)
			.elementSerializer(CONTEXT, new ContextLootScoreProvider.CustomSerializer())
			.build();
	}
}
