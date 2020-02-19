package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.util.Identifier;

public class BarterLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.PIGLIN_BARTERING_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.WARPED_NYLIUM).setWeight(1))
						.withEntry(ItemEntry.builder(Items.QUARTZ).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.OBSIDIAN).setWeight(1))
						.withEntry(ItemEntry.builder(Items.GLOWSTONE_DUST).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.MAGMA_CREAM).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.ENDER_PEARL).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.SHROOMLIGHT).setWeight(5))
						.withEntry(ItemEntry.builder(Items.FIRE_CHARGE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.GRAVEL).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.PORKCHOP).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.LEATHER).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.WARPED_FUNGI).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.SOUL_SAND).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.RED_MUSHROOM).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BROWN_MUSHROOM).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.FLINT).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.CRIMSON_FUNGI).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.NETHER_BRICK).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
	}
}
