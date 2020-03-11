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
import net.minecraft.loot.entry.LootEntry;
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
        biConsumer.accept(LootTables.PIGLIN_BARTERING_GAMEPLAY, LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.NETHERITE_HOE).setWeight(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.ENCHANTED_BOOK).setWeight(1)).withFunction(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.IRON_BOOTS).setWeight(5)).withFunction(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.POTION).setWeight(10)).withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.SPLASH_POTION).setWeight(10)).withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.IRON_NUGGET).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(9.0f, 36.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.QUARTZ).setWeight(20)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 16.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).setWeight(20)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(5.0f, 12.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.MAGMA_CREAM).setWeight(20)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 6.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.ENDER_PEARL).setWeight(20)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 8.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.STRING).setWeight(20)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 24.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.FIRE_CHARGE).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 5.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GRAVEL).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0f, 16.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.LEATHER).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 10.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_BRICK).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 16.0f)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.OBSIDIAN).setWeight(40)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.CRYING_OBSIDIAN).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 3.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.SOUL_SAND).setWeight(40)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 16.0f))))));
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

