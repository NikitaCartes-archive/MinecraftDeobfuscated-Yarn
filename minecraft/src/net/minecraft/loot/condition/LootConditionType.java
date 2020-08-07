package net.minecraft.loot.condition;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootConditionType extends JsonSerializableType<LootCondition> {
	public LootConditionType(JsonSerializer<? extends LootCondition> jsonSerializer) {
		super(jsonSerializer);
	}
}
