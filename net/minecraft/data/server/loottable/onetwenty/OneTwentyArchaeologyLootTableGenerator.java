/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.loottable.onetwenty;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.OneTwentyLootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class OneTwentyArchaeologyLootTableGenerator
implements LootTableGenerator {
    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        exporter.accept(OneTwentyLootTables.DESERT_WELL_ARCHAEOLOGY, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with((LootPoolEntry.Builder<?>)ItemEntry.builder(Items.POTTERY_SHARD_ARMS_UP).weight(3)).with(ItemEntry.builder(Items.BRICK)).with(ItemEntry.builder(Items.EMERALD)).with(ItemEntry.builder(Items.STICK)).with((LootPoolEntry.Builder<?>)((Object)ItemEntry.builder(Items.SUSPICIOUS_STEW).apply(SetStewEffectLootFunction.builder().withEffect(StatusEffects.NIGHT_VISION, UniformLootNumberProvider.create(7.0f, 10.0f)).withEffect(StatusEffects.JUMP_BOOST, UniformLootNumberProvider.create(7.0f, 10.0f)).withEffect(StatusEffects.WEAKNESS, UniformLootNumberProvider.create(6.0f, 8.0f)).withEffect(StatusEffects.BLINDNESS, UniformLootNumberProvider.create(5.0f, 7.0f)).withEffect(StatusEffects.POISON, UniformLootNumberProvider.create(10.0f, 20.0f)).withEffect(StatusEffects.SATURATION, UniformLootNumberProvider.create(7.0f, 10.0f)))))));
        exporter.accept(OneTwentyLootTables.DESERT_PYRAMID_ARCHAEOLOGY, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(Items.POTTERY_SHARD_ARCHER)).with(ItemEntry.builder(Items.POTTERY_SHARD_PRIZE)).with(ItemEntry.builder(Items.POTTERY_SHARD_SKULL)).with(ItemEntry.builder(Items.GUNPOWDER)).with(ItemEntry.builder(Items.TNT)).with(ItemEntry.builder(Items.DIAMOND)).with(ItemEntry.builder(Items.EMERALD))));
    }
}

