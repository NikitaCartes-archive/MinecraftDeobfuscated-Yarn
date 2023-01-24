package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

public class VanillaFishingLootTableGenerator implements LootTableGenerator {
	public static final LootCondition.Builder NEEDS_JUNGLE_BIOME = LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(BiomeKeys.JUNGLE));
	public static final LootCondition.Builder NEEDS_SPARSE_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(BiomeKeys.SPARSE_JUNGLE)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(BiomeKeys.BAMBOO_JUNGLE)
	);

	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			LootTables.FISHING_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(LootTableEntry.builder(LootTables.FISHING_JUNK_GAMEPLAY).weight(10).quality(-2))
						.with(
							LootTableEntry.builder(LootTables.FISHING_TREASURE_GAMEPLAY)
								.weight(5)
								.quality(2)
								.conditionally(
									EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().typeSpecific(FishingHookPredicate.of(true)))
								)
						)
						.with(LootTableEntry.builder(LootTables.FISHING_FISH_GAMEPLAY).weight(85).quality(-1))
				)
		);
		exporter.accept(LootTables.FISHING_FISH_GAMEPLAY, createFishTableBuilder());
		exporter.accept(
			LootTables.FISHING_JUNK_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(ItemEntry.builder(Blocks.LILY_PAD).weight(17))
						.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(10).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0F, 0.9F))))
						.with(ItemEntry.builder(Items.LEATHER).weight(10))
						.with(ItemEntry.builder(Items.BONE).weight(10))
						.with(ItemEntry.builder(Items.POTION).weight(10).apply(SetPotionLootFunction.builder(Potions.WATER)))
						.with(ItemEntry.builder(Items.STRING).weight(5))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(2).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0F, 0.9F))))
						.with(ItemEntry.builder(Items.BOWL).weight(10))
						.with(ItemEntry.builder(Items.STICK).weight(5))
						.with(ItemEntry.builder(Items.INK_SAC).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(10.0F))))
						.with(ItemEntry.builder(Blocks.TRIPWIRE_HOOK).weight(10))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10))
						.with(ItemEntry.builder(Blocks.BAMBOO).conditionally(NEEDS_JUNGLE_BIOME.or(NEEDS_SPARSE_JUNGLE_BIOME).or(NEEDS_BAMBOO_JUNGLE_BIOME)).weight(10))
				)
		);
		exporter.accept(
			LootTables.FISHING_TREASURE_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(ItemEntry.builder(Items.NAME_TAG))
						.with(ItemEntry.builder(Items.SADDLE))
						.with(
							ItemEntry.builder(Items.BOW)
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0F, 0.25F)))
								.apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.FISHING_ROD)
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.0F, 0.25F)))
								.apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments())
						)
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments()))
						.with(ItemEntry.builder(Items.NAUTILUS_SHELL))
				)
		);
	}

	public static LootTable.Builder createFishTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(ItemEntry.builder(Items.COD).weight(60))
					.with(ItemEntry.builder(Items.SALMON).weight(25))
					.with(ItemEntry.builder(Items.TROPICAL_FISH).weight(2))
					.with(ItemEntry.builder(Items.PUFFERFISH).weight(13))
			);
	}
}
