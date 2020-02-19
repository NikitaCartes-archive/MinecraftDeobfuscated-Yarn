/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.util.Identifier;

public class BarterLootTableGenerator
implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
        biConsumer.accept(LootTables.PIGLIN_BARTERING_GAMEPLAY, LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.WARPED_NYLIUM).setWeight(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.QUARTZ).setWeight(1)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.OBSIDIAN).setWeight(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).setWeight(2)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.MAGMA_CREAM).setWeight(2)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 3.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.ENDER_PEARL).setWeight(2)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.SHROOMLIGHT).setWeight(5)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.FIRE_CHARGE).setWeight(5)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.GRAVEL).setWeight(5)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 12.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.PORKCHOP).setWeight(5)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 5.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.LEATHER).setWeight(5)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 7.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.WARPED_FUNGI).setWeight(5)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.SOUL_SAND).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.RED_MUSHROOM).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.BROWN_MUSHROOM).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.FLINT).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0f, 8.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 12.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.CRIMSON_FUNGI).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f)))).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_BRICK).setWeight(10)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 4.0f))))));
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

