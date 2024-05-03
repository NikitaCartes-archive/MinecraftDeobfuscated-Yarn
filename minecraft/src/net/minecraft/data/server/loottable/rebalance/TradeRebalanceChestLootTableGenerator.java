package net.minecraft.data.server.loottable.rebalance;

import java.util.function.BiConsumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetInstrumentLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.InstrumentTags;

public record TradeRebalanceChestLootTableGenerator(RegistryWrapper.WrapperLookup registries) implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		lootTableBiConsumer.accept(
			LootTables.ABANDONED_MINESHAFT_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(30))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(EmptyEntry.builder().weight(5))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GLOW_BERRIES).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))))
						.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(3.0F))
						.with(ItemEntry.builder(Blocks.RAIL).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.POWERED_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.DETECTOR_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.ACTIVATOR_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.TORCH).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 16.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(EmptyEntry.builder().weight(4))
						.with(ItemEntry.builder(Items.BOOK).weight(1).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.EFFICIENCY))))
				)
		);
		lootTableBiConsumer.accept(LootTables.ANCIENT_CITY_CHEST, this.createAncientCityChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.DESERT_PYRAMID_CHEST, this.createDesertPyramidChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.JUNGLE_TEMPLE_CHEST, this.createJungleTempleChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.PILLAGER_OUTPOST_CHEST, this.createPillagerOutpostChestTableBuilder());
	}

	public LootTable.Builder createPillagerOutpostChestTableBuilder() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return LootTable.builder()
			.pool(LootPool.builder().rolls(UniformLootNumberProvider.create(0.0F, 1.0F)).with(ItemEntry.builder(Items.CROSSBOW)))
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
					.with(ItemEntry.builder(Items.WHEAT).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F))))
					.with(ItemEntry.builder(Items.POTATO).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Items.CARROT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
					.with(ItemEntry.builder(Blocks.DARK_OAK_LOG).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
					.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(7))
					.with(ItemEntry.builder(Items.STRING).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
					.with(ItemEntry.builder(Items.ARROW).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.TRIPWIRE_HOOK).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.BOOK).weight(1).apply(EnchantRandomlyLootFunction.builder(this.registries)))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(0.0F, 1.0F))
					.with(ItemEntry.builder(Items.GOAT_HORN))
					.apply(SetInstrumentLootFunction.builder(InstrumentTags.REGULAR_GOAT_HORNS))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(3))
					.with(ItemEntry.builder(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(1))
					.with(ItemEntry.builder(Items.BOOK).weight(2).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.QUICK_CHARGE))))
			);
	}

	public LootTable.Builder createDesertPyramidChestTableBuilder() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
					.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.EMERALD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.BONE).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
					.with(ItemEntry.builder(Items.SPIDER_EYE).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(25).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(20))
					.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15))
					.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5))
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder(this.registries)))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
					.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
					.with(EmptyEntry.builder().weight(15))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(4.0F))
					.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Blocks.SAND).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(4))
					.with(ItemEntry.builder(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
					.with(ItemEntry.builder(Items.BOOK).weight(2).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.UNBREAKING))))
			);
	}

	public LootTable.Builder createAncientCityChestTableBuilder() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(5.0F, 10.0F))
					.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
					.with(ItemEntry.builder(Items.MUSIC_DISC_OTHERSIDE).weight(1))
					.with(ItemEntry.builder(Items.COMPASS).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.SCULK_CATALYST).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
					.with(ItemEntry.builder(Items.NAME_TAG).weight(2))
					.with(
						ItemEntry.builder(Items.DIAMOND_HOE)
							.weight(2)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(30.0F, 50.0F)))
					)
					.with(ItemEntry.builder(Items.LEAD).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(2))
					.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(2))
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(2)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(30.0F, 50.0F)))
					)
					.with(ItemEntry.builder(Items.BOOK).weight(3).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.SWIFT_SNEAK))))
					.with(ItemEntry.builder(Items.SCULK).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 10.0F))))
					.with(ItemEntry.builder(Items.SCULK_SENSOR).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.CANDLE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.AMETHYST_SHARD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.GLOW_BERRIES).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(
						ItemEntry.builder(Items.IRON_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(ItemEntry.builder(Items.ECHO_SHARD).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.DISC_FRAGMENT_5).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(
						ItemEntry.builder(Items.POTION)
							.weight(5)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
							.apply(SetPotionLootFunction.builder(Potions.STRONG_REGENERATION))
					)
					.with(ItemEntry.builder(Items.BOOK).weight(5).apply(EnchantRandomlyLootFunction.builder(this.registries)))
					.with(ItemEntry.builder(Items.BOOK).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 10.0F))))
					.with(ItemEntry.builder(Items.BONE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.SOUL_TORCH).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.COAL).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 15.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(71))
					.with(ItemEntry.builder(Items.BOOK).weight(4).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.MENDING))))
					.with(ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(4))
					.with(ItemEntry.builder(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public LootTable.Builder createJungleTempleChestTableBuilder() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 6.0F))
					.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Blocks.BAMBOO).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.BONE).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
					.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(16).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(3))
					.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(this.registries, ConstantLootNumberProvider.create(30.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(2))
					.with(ItemEntry.builder(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(1))
					.with(ItemEntry.builder(Items.BOOK).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.UNBREAKING))))
			);
	}
}
