package net.minecraft.loot.condition;

import net.minecraft.util.JsonSerializable;
import net.minecraft.util.JsonSerializableType;

public class LootConditionType extends JsonSerializableType<LootCondition> {
	public LootConditionType(JsonSerializable<? extends LootCondition> jsonSerializable) {
		super(jsonSerializable);
	}
}
