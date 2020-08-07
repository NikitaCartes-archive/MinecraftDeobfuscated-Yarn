package net.minecraft.loot.entry;

import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootPoolEntryType extends JsonSerializableType<LootPoolEntry> {
	public LootPoolEntryType(JsonSerializer<? extends LootPoolEntry> jsonSerializer) {
		super(jsonSerializer);
	}
}
