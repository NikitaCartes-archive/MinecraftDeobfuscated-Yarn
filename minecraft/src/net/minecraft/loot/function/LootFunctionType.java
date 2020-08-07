package net.minecraft.loot.function;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootFunctionType extends JsonSerializableType<LootFunction> {
	public LootFunctionType(JsonSerializer<? extends LootFunction> jsonSerializer) {
		super(jsonSerializer);
	}
}
