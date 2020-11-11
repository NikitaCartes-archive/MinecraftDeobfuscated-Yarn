package net.minecraft.loot.provider.nbt;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootNbtProviderType extends JsonSerializableType<LootNbtProvider> {
	public LootNbtProviderType(JsonSerializer<? extends LootNbtProvider> jsonSerializer) {
		super(jsonSerializer);
	}
}
