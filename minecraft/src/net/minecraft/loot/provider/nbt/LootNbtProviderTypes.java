package net.minecraft.loot.provider.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class LootNbtProviderTypes {
	private static final Codec<LootNbtProvider> BASE_CODEC = Registries.LOOT_NBT_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootNbtProvider::getType, LootNbtProviderType::codec);
	public static final Codec<LootNbtProvider> CODEC = Codecs.createLazy(
		() -> Codec.either(ContextLootNbtProvider.INLINE_CODEC, BASE_CODEC)
				.xmap(
					either -> either.map(Function.identity(), Function.identity()),
					provider -> provider instanceof ContextLootNbtProvider contextLootNbtProvider ? Either.left(contextLootNbtProvider) : Either.right(provider)
				)
	);
	public static final LootNbtProviderType STORAGE = register("storage", StorageLootNbtProvider.CODEC);
	public static final LootNbtProviderType CONTEXT = register("context", ContextLootNbtProvider.CODEC);

	private static LootNbtProviderType register(String id, Codec<? extends LootNbtProvider> codec) {
		return Registry.register(Registries.LOOT_NBT_PROVIDER_TYPE, new Identifier(id), new LootNbtProviderType(codec));
	}
}
