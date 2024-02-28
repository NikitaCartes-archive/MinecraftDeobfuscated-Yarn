package net.minecraft.data.server.loottable.onetwentyone;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

public class OneTwentyOneChestLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup registryLookup, BiConsumer<Identifier, LootTable.Builder> consumer) {
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_DISPENSER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_WATER_DISPENSER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.WATER_BUCKET).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_CHAMBER_DISPENSER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.WATER_BUCKET).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(4))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))).weight(4))
						.with(ItemEntry.builder(Items.SNOWBALL).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))).weight(6))
						.with(ItemEntry.builder(Items.EGG).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))).weight(2))
						.with(ItemEntry.builder(Items.FIRE_CHARGE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))).weight(6))
						.with(
							ItemEntry.builder(Items.SPLASH_POTION)
								.apply(SetPotionLootFunction.builder(Potions.SLOWNESS))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.SPLASH_POTION)
								.apply(SetPotionLootFunction.builder(Potions.POISON))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.SPLASH_POTION)
								.apply(SetPotionLootFunction.builder(Potions.WEAKNESS))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetPotionLootFunction.builder(Potions.SLOWNESS))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetPotionLootFunction.builder(Potions.POISON))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetPotionLootFunction.builder(Potions.WEAKNESS))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetPotionLootFunction.builder(Potions.HEALING))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
								.weight(1)
						)
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_POT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))).weight(100))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(100))
						.with(ItemEntry.builder(Items.AMETHYST_SHARD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(100))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(100))
						.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))).weight(50))
						.with(ItemEntry.builder(Items.COPPER_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))).weight(50))
						.with(ItemEntry.builder(Items.TRIAL_KEY).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))).weight(20))
						.with(ItemEntry.builder(Items.GOLD_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))).weight(20))
						.with(ItemEntry.builder(Items.DIAMOND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(5))
						.with(ItemEntry.builder(Items.EMERALD_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(1))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_SUPPLY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 5.0F))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 14.0F))).weight(2))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F)))
								.apply(SetPotionLootFunction.builder(Potions.POISON))
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SLOWNESS))
								.weight(1)
						)
						.with(ItemEntry.builder(Items.BAKED_POTATO).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))).weight(2))
						.with(ItemEntry.builder(Items.GLOW_BERRIES).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 10.0F))).weight(2))
						.with(ItemEntry.builder(Items.ACACIA_PLANKS).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))).weight(1))
						.with(ItemEntry.builder(Items.MOSS_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))).weight(1))
						.with(ItemEntry.builder(Items.BONE_MEAL).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))).weight(1))
						.with(ItemEntry.builder(Items.TUFF).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 10.0F))).weight(1))
						.with(ItemEntry.builder(Items.TORCH).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))).weight(1))
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
								.apply(SetPotionLootFunction.builder(Potions.REGENERATION))
						)
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRENGTH))
						)
						.with(
							ItemEntry.builder(Items.STONE_PICKAXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(2)
						)
						.with(ItemEntry.builder(Items.MILK_BUCKET).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_ENTRANCE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.TRIAL_KEY).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(1))
						.with(ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))).weight(5))
						.with(ItemEntry.builder(Items.WOODEN_AXE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(10))
						.with(ItemEntry.builder(Items.HONEYCOMB).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))).weight(10))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 10.0F))).weight(10))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_INTERSECTION_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.DIAMOND_BLOCK).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(1))
						.with(ItemEntry.builder(Items.EMERALD_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))).weight(5))
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.5F)))
								.weight(5)
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.5F)))
								.weight(5)
						)
						.with(ItemEntry.builder(Items.DIAMOND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(10))
						.with(ItemEntry.builder(Items.CAKE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(20))
						.with(ItemEntry.builder(Items.AMETHYST_SHARD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 20.0F))).weight(20))
						.with(ItemEntry.builder(Items.IRON_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(20))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.4F, 0.9F)))
								.apply(EnchantRandomlyLootFunction.builder())
								.weight(1)
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(1)
						)
						.with(ItemEntry.builder(Items.DIAMOND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))).weight(1))
						.with(
							ItemEntry.builder(Items.COMPASS)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(1)
						)
						.with(ItemEntry.builder(Items.BUCKET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(1))
						.with(
							ItemEntry.builder(Items.GOLDEN_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(4)
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_PICKAXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(4)
						)
						.with(ItemEntry.builder(Items.BAMBOO_PLANKS).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 15.0F))).weight(10))
						.with(ItemEntry.builder(Items.BAKED_POTATO).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 10.0F))).weight(10))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(
							ItemEntry.builder(Items.IRON_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.4F, 0.9F)))
								.apply(EnchantRandomlyLootFunction.builder())
								.weight(1)
						)
						.with(ItemEntry.builder(Items.HONEYCOMB).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))).weight(1))
						.with(
							ItemEntry.builder(Items.STONE_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(2)
						)
						.with(
							ItemEntry.builder(Items.STONE_PICKAXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
								.weight(2)
						)
						.with(ItemEntry.builder(Items.ENDER_PEARL).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(2))
						.with(ItemEntry.builder(Items.BAMBOO_HANGING_SIGN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))).weight(2))
						.with(ItemEntry.builder(Items.BAMBOO_PLANKS).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))).weight(2))
						.with(ItemEntry.builder(Items.SCAFFOLDING).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 10.0F))).weight(2))
						.with(ItemEntry.builder(Items.TORCH).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))).weight(2))
						.with(ItemEntry.builder(Items.TUFF).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 20.0F))).weight(3))
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 6.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(8).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(8))
						.with(ItemEntry.builder(Items.SHIELD).weight(6).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F))))
						.with(
							ItemEntry.builder(Items.IRON_BOOTS)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_CHESTPLATE)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_LEGGINGS)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_HELMET)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_AXE)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_PICKAXE)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_SHOVEL)
								.weight(6)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(ItemEntry.builder(Items.SADDLE).weight(6))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(6))
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.CROSSBOW)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(10.0F, 20.0F)).allowTreasureEnchantments())
						)
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(3))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(3))
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(12)
								.apply(
									new EnchantRandomlyLootFunction.Builder()
										.add(Enchantments.SHARPNESS)
										.add(Enchantments.BANE_OF_ARTHROPODS)
										.add(Enchantments.EFFICIENCY)
										.add(Enchantments.FORTUNE)
										.add(Enchantments.SILK_TOUCH)
										.add(Enchantments.FEATHER_FALLING)
								)
						)
						.with(ItemEntry.builder(Items.DIAMOND).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(5)
								.apply(
									new EnchantRandomlyLootFunction.Builder()
										.add(Enchantments.RIPTIDE)
										.add(Enchantments.LOYALTY)
										.add(Enchantments.CHANNELING)
										.add(Enchantments.IMPALING)
										.add(Enchantments.MENDING)
								)
						)
				)
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBER_KEY_SPAWNER,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.TRIAL_KEY)))
		);
		consumer.accept(
			LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BAKED_POTATO).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GLOW_BERRIES).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 10.0F))))
						.with(ItemEntry.builder(Items.ENDER_PEARL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.REGENERATION))
						)
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRENGTH))
						)
				)
		);
	}
}
