package net.minecraft.loot.provider.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class LootNumberProviderTypes {
	private static final Codec<LootNumberProvider> BASE_CODEC = Registries.LOOT_NUMBER_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootNumberProvider::getType, LootNumberProviderType::codec);
	public static final Codec<LootNumberProvider> CODEC = Codecs.createLazy(
		() -> {
			Codec<LootNumberProvider> codec = Codecs.alternatively(BASE_CODEC, UniformLootNumberProvider.CODEC);
			return Codec.either(ConstantLootNumberProvider.field_45887, codec)
				.xmap(
					either -> either.map(Function.identity(), Function.identity()),
					lootNumberProvider -> lootNumberProvider instanceof ConstantLootNumberProvider constantLootNumberProvider
							? Either.left(constantLootNumberProvider)
							: Either.right(lootNumberProvider)
				);
		}
	);
	public static final LootNumberProviderType CONSTANT = register("constant", ConstantLootNumberProvider.CODEC);
	public static final LootNumberProviderType UNIFORM = register("uniform", UniformLootNumberProvider.CODEC);
	public static final LootNumberProviderType BINOMIAL = register("binomial", BinomialLootNumberProvider.CODEC);
	public static final LootNumberProviderType SCORE = register("score", ScoreLootNumberProvider.CODEC);

	private static LootNumberProviderType register(String id, Codec<? extends LootNumberProvider> codec) {
		return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, new Identifier(id), new LootNumberProviderType(codec));
	}
}
