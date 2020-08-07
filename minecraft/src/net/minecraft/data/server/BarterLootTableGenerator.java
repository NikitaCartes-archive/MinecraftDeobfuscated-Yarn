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
	public void method_24818(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_22402,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8529).weight(5).method_438(new EnchantRandomlyLootFunction.Builder().add(Enchantments.field_23071)))
						.with(ItemEntry.builder(Items.field_8660).weight(8).method_438(new EnchantRandomlyLootFunction.Builder().add(Enchantments.field_23071)))
						.with(
							ItemEntry.builder(Items.field_8574)
								.weight(8)
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.with(
							ItemEntry.builder(Items.field_8436)
								.weight(8)
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.with(
							ItemEntry.builder(Items.field_8574)
								.weight(10)
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.with(ItemEntry.builder(Items.field_8675).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 36.0F))))
						.with(ItemEntry.builder(Items.field_8634).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8276).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8155).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 12.0F))))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40))
						.with(ItemEntry.builder(Items.CRYING_OBSIDIAN).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8814).weight(40))
						.with(ItemEntry.builder(Items.field_8745).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SOUL_SAND).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8729).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8236).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(6.0F, 12.0F))))
						.with(ItemEntry.builder(Items.GRAVEL).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
						.with(ItemEntry.builder(Items.BLACKSTONE).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
				)
		);
	}
}
