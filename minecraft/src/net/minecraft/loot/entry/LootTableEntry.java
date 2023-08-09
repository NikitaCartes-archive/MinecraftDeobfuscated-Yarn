package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;

public class LootTableEntry extends LeafEntry {
	public static final Codec<LootTableEntry> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Identifier.CODEC.fieldOf("name").forGetter(lootTableEntry -> lootTableEntry.id))
				.<int, int, List<LootCondition>, List<LootFunction>>and(method_53290(instance))
				.apply(instance, LootTableEntry::new)
	);
	private final Identifier id;

	private LootTableEntry(Identifier id, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
		this.id = id;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.LOOT_TABLE;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		LootTable lootTable = context.getDataLookup().getLootTable(this.id);
		lootTable.generateUnprocessedLoot(context, lootConsumer);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		LootDataKey<LootTable> lootDataKey = new LootDataKey<>(LootDataType.LOOT_TABLES, this.id);
		if (reporter.isInStack(lootDataKey)) {
			reporter.report("Table " + this.id + " is recursively called");
		} else {
			super.validate(reporter);
			reporter.getDataLookup()
				.getElementOptional(lootDataKey)
				.ifPresentOrElse(
					table -> table.validate(reporter.makeChild("->{" + this.id + "}", lootDataKey)), () -> reporter.report("Unknown loot table called " + this.id)
				);
		}
	}

	public static LeafEntry.Builder<?> builder(Identifier id) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(id, weight, quality, conditions, functions));
	}
}
