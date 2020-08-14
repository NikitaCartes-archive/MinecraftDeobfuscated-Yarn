package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class BarterLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.PIGLIN_BARTERING_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
						.with(ItemEntry.builder(Items.IRON_BOOTS).weight(8).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
						.with(
							ItemEntry.builder(Items.POTION)
								.weight(8)
								.apply(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.with(
							ItemEntry.builder(Items.SPLASH_POTION)
								.weight(8)
								.apply(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.with(
							ItemEntry.builder(Items.POTION)
								.weight(10)
								.apply(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 36.0F))))
						.with(ItemEntry.builder(Items.ENDER_PEARL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Items.QUARTZ).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 12.0F))))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40))
						.with(ItemEntry.builder(Items.CRYING_OBSIDIAN).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FIRE_CHARGE).weight(40))
						.with(ItemEntry.builder(Items.LEATHER).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SOUL_SAND).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.NETHER_BRICK).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(6.0F, 12.0F))))
						.with(ItemEntry.builder(Items.GRAVEL).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
						.with(ItemEntry.builder(Items.BLACKSTONE).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
				)
		);
	}
}
