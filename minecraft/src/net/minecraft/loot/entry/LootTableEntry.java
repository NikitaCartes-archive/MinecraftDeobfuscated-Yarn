package net.minecraft.loot.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
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
		instance -> instance.group(Codec.either(Identifier.CODEC, LootTable.CODEC).fieldOf("value").forGetter(entry -> entry.value))
				.<int, int, List<LootCondition>, List<LootFunction>>and(addLeafFields(instance))
				.apply(instance, LootTableEntry::new)
	);
	private final Either<Identifier, LootTable> value;

	private LootTableEntry(Either<Identifier, LootTable> value, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
		this.value = value;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.LOOT_TABLE;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		this.value.<LootTable>map(id -> context.getDataLookup().getLootTable(id), table -> table).generateUnprocessedLoot(context, lootConsumer);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		Optional<Identifier> optional = this.value.left();
		if (optional.isPresent()) {
			LootDataKey<LootTable> lootDataKey = new LootDataKey<>(LootDataType.LOOT_TABLES, (Identifier)optional.get());
			if (reporter.isInStack(lootDataKey)) {
				reporter.report("Table " + optional.get() + " is recursively called");
				return;
			}
		}

		super.validate(reporter);
		this.value
			.ifLeft(
				id -> {
					LootDataKey<LootTable> lootDataKeyx = new LootDataKey<>(LootDataType.LOOT_TABLES, id);
					reporter.getDataLookup()
						.getElementOptional(lootDataKeyx)
						.ifPresentOrElse(table -> table.validate(reporter.makeChild("->{" + id + "}", lootDataKeyx)), () -> reporter.report("Unknown loot table called " + id));
				}
			)
			.ifRight(table -> table.validate(reporter.makeChild("->{inline}")));
	}

	public static LeafEntry.Builder<?> builder(Identifier id) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(Either.left(id), weight, quality, conditions, functions));
	}

	public static LeafEntry.Builder<?> builder(LootTable table) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(Either.right(table), weight, quality, conditions, functions));
	}
}
