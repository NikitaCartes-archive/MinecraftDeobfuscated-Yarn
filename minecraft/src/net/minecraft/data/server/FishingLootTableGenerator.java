package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;

public class FishingLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public static final LootCondition.Builder NEEDS_JUNGLE_BIOME = LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(Biomes.JUNGLE));
	public static final LootCondition.Builder NEEDS_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.JUNGLE_HILLS)
	);
	public static final LootCondition.Builder NEEDS_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.JUNGLE_EDGE)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.BAMBOO_JUNGLE)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.MODIFIED_JUNGLE)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.MODIFIED_JUNGLE_EDGE)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.BAMBOO_JUNGLE_HILLS)
	);

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.FISHING_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_JUNK_GAMEPLAY).setWeight(10).setQuality(-2))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_TREASURE_GAMEPLAY).setWeight(5).setQuality(2))
						.withEntry(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY).setWeight(85).setQuality(-1))
				)
		);
		biConsumer.accept(
			LootTables.FISHING_FISH_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.COD).setWeight(60))
						.withEntry(ItemEntry.builder(Items.SALMON).setWeight(25))
						.withEntry(ItemEntry.builder(Items.TROPICAL_FISH).setWeight(2))
						.withEntry(ItemEntry.builder(Items.PUFFERFISH).setWeight(13))
				)
		);
		biConsumer.accept(
			LootTables.FISHING_JUNK_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.LEATHER_BOOTS).setWeight(10).withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.LEATHER).setWeight(10))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(10))
						.withEntry(
							ItemEntry.builder(Items.POTION)
								.setWeight(10)
								.withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(5))
						.withEntry(ItemEntry.builder(Items.FISHING_ROD).setWeight(2).withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.BOWL).setWeight(10))
						.withEntry(ItemEntry.builder(Items.STICK).setWeight(5))
						.withEntry(ItemEntry.builder(Items.INK_SAC).setWeight(1).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(10))))
						.withEntry(ItemEntry.builder(Blocks.TRIPWIRE_HOOK).setWeight(10))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10))
						.withEntry(
							ItemEntry.builder(Blocks.BAMBOO)
								.withCondition(
									NEEDS_JUNGLE_BIOME.withCondition(NEEDS_JUNGLE_HILLS_BIOME)
										.withCondition(NEEDS_JUNGLE_EDGE_BIOME)
										.withCondition(NEEDS_BAMBOO_JUNGLE_BIOME)
										.withCondition(NEEDS_MODIFIED_JUNGLE_BIOME)
										.withCondition(NEEDS_MODIFIED_JUNGLE_EDGE_BIOME)
										.withCondition(NEEDS_BAMBOO_JUNGLE_HILLS_BIOME)
								)
								.setWeight(10)
						)
				)
		);
		biConsumer.accept(
			LootTables.FISHING_TREASURE_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Blocks.LILY_PAD))
						.withEntry(ItemEntry.builder(Items.NAME_TAG))
						.withEntry(ItemEntry.builder(Items.SADDLE))
						.withEntry(
							ItemEntry.builder(Items.BOW)
								.withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.FISHING_ROD)
								.withFunction(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.BOOK).withFunction(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(ItemEntry.builder(Items.NAUTILUS_SHELL))
				)
		);
	}
}
