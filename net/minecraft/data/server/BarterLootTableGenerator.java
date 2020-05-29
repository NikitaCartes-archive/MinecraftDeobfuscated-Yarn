/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class BarterLootTableGenerator
implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
        biConsumer.accept(LootTables.PIGLIN_BARTERING_GAMEPLAY, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.BOOK).weight(5)).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.IRON_BOOTS).weight(8)).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.POTION).weight(10)).apply(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.SPLASH_POTION).weight(10)).apply(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.IRON_NUGGET).weight(10)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(9.0f, 36.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.QUARTZ).weight(20)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 16.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).weight(20)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0f, 12.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.MAGMA_CREAM).weight(20)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 6.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.ENDER_PEARL).weight(20)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 8.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.STRING).weight(20)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 24.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.FIRE_CHARGE).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 5.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GRAVEL).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 16.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.LEATHER).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 10.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_BRICK).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 16.0f)))).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.OBSIDIAN).weight(40)).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.CRYING_OBSIDIAN).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 3.0f)))).with((LootPoolEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.SOUL_SAND).weight(40)).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 16.0f))))));
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

