package net.minecraft.loot.provider.nbt;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class LootNbtProviderTypes {
	public static final LootNbtProviderType STORAGE = register("storage", new StorageLootNbtProvider.Serializer());
	public static final LootNbtProviderType CONTEXT = register("context", new ContextLootNbtProvider.Serializer());

	private static LootNbtProviderType register(String id, JsonSerializer<? extends LootNbtProvider> jsonSerializer) {
		return Registry.register(Registries.LOOT_NBT_PROVIDER_TYPE, new Identifier(id), new LootNbtProviderType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootNbtProvider, LootNbtProviderType>createSerializerBuilder(
				Registries.LOOT_NBT_PROVIDER_TYPE, "provider", "type", LootNbtProvider::getType
			)
			.elementSerializer(CONTEXT, new ContextLootNbtProvider.CustomSerializer())
			.build();
	}
}
