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
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class BarterLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.PIGLIN_BARTERING_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.NETHERITE_HOE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.WARPED_NYLIUM).setWeight(5))
						.withEntry(
							ItemEntry.builder(Items.POTION)
								.setWeight(5)
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.withEntry(
							ItemEntry.builder(Items.SPLASH_POTION)
								.setWeight(5)
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.withEntry(ItemEntry.builder(Items.QUARTZ).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
						.withEntry(ItemEntry.builder(Items.GLOWSTONE_DUST).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.MAGMA_CREAM).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.ENDER_PEARL).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 24.0F))))
						.withEntry(ItemEntry.builder(Items.SHROOMLIGHT).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(6.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.FIRE_CHARGE).setWeight(20))
						.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F)))
						.withEntry(ItemEntry.builder(Items.GRAVEL).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 16.0F))))
						.withEntry(ItemEntry.builder(Items.LEATHER).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.WARPED_FUNGUS).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.NETHER_BRICK).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 16.0F))))
						.withEntry(ItemEntry.builder(Items.OBSIDIAN).setWeight(20))
						.withEntry(ItemEntry.builder(Items.CRYING_OBSIDIAN).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
	}
}
