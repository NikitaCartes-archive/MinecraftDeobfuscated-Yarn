package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootPoolEntryTypes {
	public static final Codec<LootPoolEntry> CODEC = Registries.LOOT_POOL_ENTRY_TYPE.getCodec().dispatch(LootPoolEntry::getType, LootPoolEntryType::codec);
	public static final LootPoolEntryType EMPTY = register("empty", EmptyEntry.CODEC);
	public static final LootPoolEntryType ITEM = register("item", ItemEntry.CODEC);
	public static final LootPoolEntryType LOOT_TABLE = register("loot_table", LootTableEntry.CODEC);
	public static final LootPoolEntryType DYNAMIC = register("dynamic", DynamicEntry.CODEC);
	public static final LootPoolEntryType TAG = register("tag", TagEntry.CODEC);
	public static final LootPoolEntryType ALTERNATIVES = register("alternatives", AlternativeEntry.CODEC);
	public static final LootPoolEntryType SEQUENCE = register("sequence", SequenceEntry.CODEC);
	public static final LootPoolEntryType GROUP = register("group", GroupEntry.CODEC);

	private static LootPoolEntryType register(String id, MapCodec<? extends LootPoolEntry> codec) {
		return Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, Identifier.ofVanilla(id), new LootPoolEntryType(codec));
	}
}
