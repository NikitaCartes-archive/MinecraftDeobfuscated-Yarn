package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;

public class VanillaBarterLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			LootTables.PIGLIN_BARTERING_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
						.with(ItemEntry.builder(Items.IRON_BOOTS).weight(8).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
						.with(ItemEntry.builder(Items.POTION).weight(8).apply(SetPotionLootFunction.builder(Potions.FIRE_RESISTANCE)))
						.with(ItemEntry.builder(Items.SPLASH_POTION).weight(8).apply(SetPotionLootFunction.builder(Potions.FIRE_RESISTANCE)))
						.with(ItemEntry.builder(Items.POTION).weight(10).apply(SetPotionLootFunction.builder(Potions.WATER)))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(10.0F, 36.0F))))
						.with(ItemEntry.builder(Items.ENDER_PEARL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 9.0F))))
						.with(ItemEntry.builder(Items.QUARTZ).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 12.0F))))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40))
						.with(ItemEntry.builder(Items.CRYING_OBSIDIAN).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FIRE_CHARGE).weight(40))
						.with(ItemEntry.builder(Items.LEATHER).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SOUL_SAND).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.NETHER_BRICK).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 12.0F))))
						.with(ItemEntry.builder(Items.GRAVEL).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 16.0F))))
						.with(ItemEntry.builder(Items.BLACKSTONE).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 16.0F))))
				)
		);
	}
}
