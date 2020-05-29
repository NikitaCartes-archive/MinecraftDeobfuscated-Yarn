package net.minecraft.loot.entry;

import net.minecraft.util.JsonSerializable;
import net.minecraft.util.JsonSerializableType;

public class LootPoolEntryType extends JsonSerializableType<LootPoolEntry> {
	public LootPoolEntryType(JsonSerializable<? extends LootPoolEntry> jsonSerializable) {
		super(jsonSerializable);
	}
}
