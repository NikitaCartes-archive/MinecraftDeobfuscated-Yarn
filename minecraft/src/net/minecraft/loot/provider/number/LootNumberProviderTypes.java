package net.minecraft.loot.provider.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootNumberProviderTypes {
	private static final Codec<LootNumberProvider> BASE_CODEC = Registries.LOOT_NUMBER_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootNumberProvider::getType, LootNumberProviderType::codec);
	public static final Codec<LootNumberProvider> CODEC = Codec.lazyInitialized(
		() -> {
			Codec<LootNumberProvider> codec = Codec.withAlternative(BASE_CODEC, UniformLootNumberProvider.CODEC.codec());
			return Codec.either(ConstantLootNumberProvider.INLINE_CODEC, codec)
				.xmap(
					Either::unwrap,
					provider -> provider instanceof ConstantLootNumberProvider constantLootNumberProvider ? Either.left(constantLootNumberProvider) : Either.right(provider)
				);
		}
	);
	public static final LootNumberProviderType CONSTANT = register("constant", ConstantLootNumberProvider.CODEC);
	public static final LootNumberProviderType UNIFORM = register("uniform", UniformLootNumberProvider.CODEC);
	public static final LootNumberProviderType BINOMIAL = register("binomial", BinomialLootNumberProvider.CODEC);
	public static final LootNumberProviderType SCORE = register("score", ScoreLootNumberProvider.CODEC);
	public static final LootNumberProviderType STORAGE = register("storage", StorageLootNumberProvider.CODEC);
	public static final LootNumberProviderType ENCHANTMENT_LEVEL = register("enchantment_level", EnchantmentLevelLootNumberProvider.CODEC);

	private static LootNumberProviderType register(String id, MapCodec<? extends LootNumberProvider> codec) {
		return Registry.register(Registries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.ofVanilla(id), new LootNumberProviderType(codec));
	}
}
