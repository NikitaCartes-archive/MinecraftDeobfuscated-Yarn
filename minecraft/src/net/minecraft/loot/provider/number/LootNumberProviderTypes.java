package net.minecraft.loot.provider.number;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootNumberProviderTypes {
	public static final LootNumberProviderType CONSTANT = register("constant", new ConstantLootNumberProvider.Serializer());
	public static final LootNumberProviderType UNIFORM = register("uniform", new UniformLootNumberProvider.Serializer());
	public static final LootNumberProviderType BINOMIAL = register("binomial", new BinomialLootNumberProvider.Serializer());
	public static final LootNumberProviderType SCORE = register("score", new ScoreLootNumberProvider.Serializer());

	private static LootNumberProviderType register(String id, JsonSerializer<? extends LootNumberProvider> jsonSerializer) {
		return Registry.register(Registry.LOOT_NUMBER_PROVIDER_TYPE, new Identifier(id), new LootNumberProviderType(jsonSerializer));
	}

	public static Object method_32455() {
		return JsonSerializing.<LootNumberProvider, LootNumberProviderType>createTypeHandler(
				Registry.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", LootNumberProvider::getType
			)
			.method_32385(CONSTANT, new ConstantLootNumberProvider.CustomSerializer())
			.method_33409(UNIFORM)
			.createGsonSerializer();
	}
}
