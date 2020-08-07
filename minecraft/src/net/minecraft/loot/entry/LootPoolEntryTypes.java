package net.minecraft.loot.entry;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootPoolEntryTypes {
	public static final LootPoolEntryType field_25206 = register("empty", new EmptyEntry.Serializer());
	public static final LootPoolEntryType field_25207 = register("item", new ItemEntry.Serializer());
	public static final LootPoolEntryType field_25208 = register("loot_table", new LootTableEntry.Serializer());
	public static final LootPoolEntryType field_25209 = register("dynamic", new DynamicEntry.Serializer());
	public static final LootPoolEntryType field_25210 = register("tag", new TagEntry.Serializer());
	public static final LootPoolEntryType field_25211 = register("alternatives", CombinedEntry.createSerializer(AlternativeEntry::new));
	public static final LootPoolEntryType field_25212 = register("sequence", CombinedEntry.createSerializer(GroupEntry::new));
	public static final LootPoolEntryType field_25213 = register("group", CombinedEntry.createSerializer(SequenceEntry::new));

	private static LootPoolEntryType register(String id, JsonSerializer<? extends LootPoolEntry> jsonSerializer) {
		return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new Identifier(id), new LootPoolEntryType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootPoolEntry, LootPoolEntryType>createTypeHandler(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootPoolEntry::getType)
			.createGsonSerializer();
	}
}
