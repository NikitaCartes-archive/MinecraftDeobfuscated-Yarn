package net.minecraft.loot.entry;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;
import net.minecraft.util.registry.Registry;

public class LootPoolEntryTypes {
	public static final LootPoolEntryType EMPTY = register("empty", new EmptyEntry.Serializer());
	public static final LootPoolEntryType ITEM = register("item", new ItemEntry.Serializer());
	public static final LootPoolEntryType LOOT_TABLE = register("loot_table", new LootTableEntry.Serializer());
	public static final LootPoolEntryType DYNAMIC = register("dynamic", new DynamicEntry.Serializer());
	public static final LootPoolEntryType TAG = register("tag", new TagEntry.Serializer());
	public static final LootPoolEntryType ALTERNATIVES = register("alternatives", CombinedEntry.createSerializer(AlternativeEntry::new));
	public static final LootPoolEntryType SEQUENCE = register("sequence", CombinedEntry.createSerializer(GroupEntry::new));
	public static final LootPoolEntryType GROUP = register("group", CombinedEntry.createSerializer(SequenceEntry::new));

	private static LootPoolEntryType register(String id, JsonSerializer<? extends LootPoolEntry> jsonSerializer) {
		return Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new Identifier(id), new LootPoolEntryType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootPoolEntry, LootPoolEntryType>createTypeHandler(Registry.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootPoolEntry::getType)
			.createGsonSerializer();
	}
}
