package net.minecraft.loot.provider.score;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class LootScoreProviderTypes {
	private static final Codec<LootScoreProvider> BASE_CODEC = Registries.LOOT_SCORE_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootScoreProvider::getType, LootScoreProviderType::codec);
	public static final Codec<LootScoreProvider> CODEC = Codecs.createLazy(
		() -> Codec.either(ContextLootScoreProvider.field_45893, BASE_CODEC)
				.xmap(
					either -> either.map(Function.identity(), Function.identity()),
					lootScoreProvider -> lootScoreProvider instanceof ContextLootScoreProvider contextLootScoreProvider
							? Either.left(contextLootScoreProvider)
							: Either.right(lootScoreProvider)
				)
	);
	public static final LootScoreProviderType FIXED = register("fixed", FixedLootScoreProvider.CODEC);
	public static final LootScoreProviderType CONTEXT = register("context", ContextLootScoreProvider.CODEC);

	private static LootScoreProviderType register(String id, Codec<? extends LootScoreProvider> codec) {
		return Registry.register(Registries.LOOT_SCORE_PROVIDER_TYPE, new Identifier(id), new LootScoreProviderType(codec));
	}
}
