package net.minecraft.loot.entry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.JsonSerializing;

public class LootPoolEntryTypes {
	public static final LootPoolEntryType EMPTY = register("empty", new EmptyEntry.Serializer());
	public static final LootPoolEntryType ITEM = register("item", new ItemEntry.Serializer());
	public static final LootPoolEntryType LOOT_TABLE = register("loot_table", new LootTableEntry.Serializer());
	public static final LootPoolEntryType DYNAMIC = register("dynamic", new DynamicEntry.Serializer());
	public static final LootPoolEntryType TAG = register("tag", new TagEntry.Serializer());
	public static final LootPoolEntryType ALTERNATIVES = register("alternatives", CombinedEntry.createSerializer(AlternativeEntry::new));
	public static final LootPoolEntryType SEQUENCE = register("sequence", CombinedEntry.createSerializer(SequenceEntry::new));
	public static final LootPoolEntryType GROUP = register("group", CombinedEntry.createSerializer(GroupEntry::new));

	private static LootPoolEntryType register(String id, JsonSerializer<? extends LootPoolEntry> jsonSerializer) {
		return Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, new Identifier(id), new LootPoolEntryType(jsonSerializer));
	}

	public static Object createGsonSerializer() {
		return JsonSerializing.<LootPoolEntry, LootPoolEntryType>createSerializerBuilder(Registries.LOOT_POOL_ENTRY_TYPE, "entry", "type", LootPoolEntry::getType)
			.build();
	}
}
