package net.minecraft.loot.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class LootTableEntry extends LeafEntry {
	public static final MapCodec<LootTableEntry> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.either(RegistryKey.createCodec(RegistryKeys.LOOT_TABLE), LootTable.CODEC).fieldOf("value").forGetter(entry -> entry.value))
				.<int, int, List<LootCondition>, List<LootFunction>>and(addLeafFields(instance))
				.apply(instance, LootTableEntry::new)
	);
	private final Either<RegistryKey<LootTable>, LootTable> value;

	private LootTableEntry(Either<RegistryKey<LootTable>, LootTable> value, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
		this.value = value;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.LOOT_TABLE;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		this.value
			.<LootTable>map(
				key -> (LootTable)context.getLookup().getOptionalEntry(RegistryKeys.LOOT_TABLE, key).map(RegistryEntry::value).orElse(LootTable.EMPTY), table -> table
			)
			.generateUnprocessedLoot(context, lootConsumer);
	}

	@Override
	public void validate(LootTableReporter reporter) {
		Optional<RegistryKey<LootTable>> optional = this.value.left();
		if (optional.isPresent()) {
			RegistryKey<LootTable> registryKey = (RegistryKey<LootTable>)optional.get();
			if (!reporter.canUseReferences()) {
				reporter.report("Uses reference to " + registryKey.getValue() + ", but references are not allowed");
				return;
			}

			if (reporter.isInStack(registryKey)) {
				reporter.report("Table " + registryKey.getValue() + " is recursively called");
				return;
			}
		}

		super.validate(reporter);
		this.value
			.ifLeft(
				key -> reporter.getDataLookup()
						.getOptionalEntry(RegistryKeys.LOOT_TABLE, key)
						.ifPresentOrElse(
							entry -> ((LootTable)entry.value()).validate(reporter.makeChild("->{" + key.getValue() + "}", key)),
							() -> reporter.report("Unknown loot table called " + key.getValue())
						)
			)
			.ifRight(table -> table.validate(reporter.makeChild("->{inline}")));
	}

	public static LeafEntry.Builder<?> builder(RegistryKey<LootTable> key) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(Either.left(key), weight, quality, conditions, functions));
	}

	public static LeafEntry.Builder<?> builder(LootTable table) {
		return builder((weight, quality, conditions, functions) -> new LootTableEntry(Either.right(table), weight, quality, conditions, functions));
	}
}
