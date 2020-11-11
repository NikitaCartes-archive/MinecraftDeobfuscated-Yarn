package net.minecraft.loot.provider.score;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootScoreProviderType extends JsonSerializableType<LootScoreProvider> {
	public LootScoreProviderType(JsonSerializer<? extends LootScoreProvider> jsonSerializer) {
		super(jsonSerializer);
	}
}
