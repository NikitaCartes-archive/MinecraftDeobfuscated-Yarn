package net.minecraft.loot.function;

import net.minecraft.util.JsonSerializable;
import net.minecraft.util.JsonSerializableType;

public class LootFunctionType extends JsonSerializableType<LootFunction> {
	public LootFunctionType(JsonSerializable<? extends LootFunction> jsonSerializable) {
		super(jsonSerializable);
	}
}
