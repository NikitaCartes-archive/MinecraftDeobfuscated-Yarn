package net.minecraft.loot.provider.number;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class LootNumberProviderTypes {
	public static final LootNumberProviderType CONSTANT = register("constant", new ConstantLootNumberProvider.Serializer());
	public static final LootNumberProviderType UNIFORM = register("uniform", new UniformLootNumberProvider.Serializer());
	public static final LootNumberProviderType BINOMIAL = register("binomial", new BinomialLootNumberProvider.Serializer());
	public static final LootNumberProviderType SCORE = register("score", new ScoreLootNumberProvider.Serializer());

	private static LootNumberProviderType register(String id, JsonSerializer<? extends LootNumberProvider> jsonSerializer) {
		return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, new Identifier(id), new LootNumberProviderType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootNumberProvider, LootNumberProviderType>createSerializerBuilder(
				Registries.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", LootNumberProvider::getType
			)
			.elementSerializer(CONSTANT, new ConstantLootNumberProvider.CustomSerializer())
			.defaultType(UNIFORM)
			.build();
	}
}
