/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.class_4570;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LocationCheckLootCondition;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LeafEntry;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.entry.LootTableEntry;
import net.minecraft.world.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetDamageLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;

public class FishingLootTableGenerator
implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
    public static final class_4570.Builder field_11346 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.JUNGLE));
    public static final class_4570.Builder field_11347 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.JUNGLE_HILLS));
    public static final class_4570.Builder field_11350 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.JUNGLE_EDGE));
    public static final class_4570.Builder field_11349 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.BAMBOO_JUNGLE));
    public static final class_4570.Builder field_11348 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.MODIFIED_JUNGLE));
    public static final class_4570.Builder field_11351 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.MODIFIED_JUNGLE_EDGE));
    public static final class_4570.Builder field_11352 = LocationCheckLootCondition.builder(LocationPredicate.Builder.method_22484().biome(Biomes.BAMBOO_JUNGLE_HILLS));

    public void method_10405(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
        biConsumer.accept(LootTables.FISHING_GAMEPLAY, LootSupplier.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)LootTableEntry.builder(LootTables.FISHING_JUNK_GAMEPLAY).setWeight(10)).setQuality(-2)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)LootTableEntry.builder(LootTables.FISHING_TREASURE_GAMEPLAY).setWeight(5)).setQuality(2)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY).setWeight(85)).setQuality(-1))));
        biConsumer.accept(LootTables.FISHING_FISH_GAMEPLAY, LootSupplier.builder().withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.COD).setWeight(60)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.SALMON).setWeight(25)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.TROPICAL_FISH).setWeight(2)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.PUFFERFISH).setWeight(13))));
        biConsumer.accept(LootTables.FISHING_JUNK_GAMEPLAY, LootSupplier.builder().withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.LEATHER_BOOTS).setWeight(10)).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0f, 0.9f)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.LEATHER).setWeight(10)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.BONE).setWeight(10)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.POTION).setWeight(10)).method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.STRING).setWeight(5)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.FISHING_ROD).setWeight(2)).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0f, 0.9f)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.BOWL).setWeight(10)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.STICK).setWeight(5)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.INK_SAC).setWeight(1)).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(10)))).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Blocks.TRIPWIRE_HOOK).setWeight(10)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Blocks.BAMBOO).method_421(field_11346.withCondition(field_11347).withCondition(field_11350).withCondition(field_11349).withCondition(field_11348).withCondition(field_11351).withCondition(field_11352))).setWeight(10))));
        biConsumer.accept(LootTables.FISHING_TREASURE_GAMEPLAY, LootSupplier.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(Blocks.LILY_PAD)).withEntry(ItemEntry.builder(Items.NAME_TAG)).withEntry(ItemEntry.builder(Items.SADDLE)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.BOW).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0f, 0.25f)))).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.FISHING_ROD).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0f, 0.25f)))).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.BOOK).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())).withEntry(ItemEntry.builder(Items.NAUTILUS_SHELL))));
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.method_10405((BiConsumer)object);
    }
}

