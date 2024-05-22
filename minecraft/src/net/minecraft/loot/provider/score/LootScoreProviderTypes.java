package net.minecraft.loot.provider.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootScoreProviderTypes {
	private static final Codec<LootScoreProvider> BASE_CODEC = Registries.LOOT_SCORE_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootScoreProvider::getType, LootScoreProviderType::codec);
	public static final Codec<LootScoreProvider> CODEC = Codec.lazyInitialized(
		() -> Codec.either(ContextLootScoreProvider.INLINE_CODEC, BASE_CODEC)
				.xmap(
					Either::unwrap,
					provider -> provider instanceof ContextLootScoreProvider contextLootScoreProvider ? Either.left(contextLootScoreProvider) : Either.right(provider)
				)
	);
	public static final LootScoreProviderType FIXED = register("fixed", FixedLootScoreProvider.CODEC);
	public static final LootScoreProviderType CONTEXT = register("context", ContextLootScoreProvider.CODEC);

	private static LootScoreProviderType register(String id, MapCodec<? extends LootScoreProvider> codec) {
		return Registry.register(Registries.LOOT_SCORE_PROVIDER_TYPE, Identifier.ofVanilla(id), new LootScoreProviderType(codec));
	}
}
