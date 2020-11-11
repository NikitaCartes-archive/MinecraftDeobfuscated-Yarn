package net.minecraft.loot.provider.number;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootNumberProviderType extends JsonSerializableType<LootNumberProvider> {
	public LootNumberProviderType(JsonSerializer<? extends LootNumberProvider> jsonSerializer) {
		super(jsonSerializer);
	}
}
