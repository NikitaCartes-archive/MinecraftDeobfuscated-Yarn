package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.entry.ItemEntry;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public void method_16120(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.ENTITY_CAT_MORNING_GIFT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8245).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8073).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8726).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8614).setWeight(2))
				)
		);
	}
}
