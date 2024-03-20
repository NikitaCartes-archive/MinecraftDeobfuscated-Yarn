package net.minecraft.data.server.loottable.onetwentyone;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class OneTwentyOneShearingLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup registryLookup, BiConsumer<RegistryKey<LootTable>, LootTable.Builder> consumer) {
		consumer.accept(
			LootTables.BOGGED_SHEARING,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(2.0F))
						.with(ItemEntry.builder(Items.BROWN_MUSHROOM).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
						.with(ItemEntry.builder(Items.RED_MUSHROOM).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
	}
}
