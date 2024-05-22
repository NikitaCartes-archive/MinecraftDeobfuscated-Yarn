package net.minecraft.loot.provider.nbt;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootNbtProviderTypes {
	private static final Codec<LootNbtProvider> BASE_CODEC = Registries.LOOT_NBT_PROVIDER_TYPE
		.getCodec()
		.dispatch(LootNbtProvider::getType, LootNbtProviderType::codec);
	public static final Codec<LootNbtProvider> CODEC = Codec.lazyInitialized(
		() -> Codec.either(ContextLootNbtProvider.INLINE_CODEC, BASE_CODEC)
				.xmap(
					Either::unwrap,
					provider -> provider instanceof ContextLootNbtProvider contextLootNbtProvider ? Either.left(contextLootNbtProvider) : Either.right(provider)
				)
	);
	public static final LootNbtProviderType STORAGE = register("storage", StorageLootNbtProvider.CODEC);
	public static final LootNbtProviderType CONTEXT = register("context", ContextLootNbtProvider.CODEC);

	private static LootNbtProviderType register(String id, MapCodec<? extends LootNbtProvider> codec) {
		return Registry.register(Registries.LOOT_NBT_PROVIDER_TYPE, Identifier.ofVanilla(id), new LootNbtProviderType(codec));
	}
}
