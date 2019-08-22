package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.entry.EmptyEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.world.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.world.loot.function.ExplorationMapLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetStewEffectLootFunction;

public class ChestLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public void method_10399(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.ABANDONED_MINESHAFT_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE).setWeight(20))
						.withEntry(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE))
						.withEntry(ItemEntry.builder(Items.NAME_TAG).setWeight(30))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.IRON_PICKAXE).setWeight(5))
						.withEntry(EmptyEntry.Serializer().setWeight(5))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.LAPIS_LAZULI).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.MELON_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Blocks.RAIL).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.POWERED_RAIL).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.DETECTOR_RAIL).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.ACTIVATOR_RAIL).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.TORCH).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F))))
				)
		);
		biConsumer.accept(
			LootTables.BURIED_TREASURE_CHEST,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.HEART_OF_THE_SEA)))
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(5.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.TNT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(0.0F, 1.0F))
						.withEntry(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.withEntry(ItemEntry.builder(Items.IRON_SWORD))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(2))
						.withEntry(ItemEntry.builder(Items.COOKED_COD).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.COOKED_SALMON).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.DESERT_PYRAMID_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.SPIDER_EYE).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(20))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR).setWeight(15))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).setWeight(10))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).setWeight(5))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(20).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE).setWeight(20))
						.withEntry(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
						.withEntry(EmptyEntry.Serializer().setWeight(15))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.GUNPOWDER).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.SAND).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.END_CITY_TREASURE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(3))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_SWORD)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_BOOTS)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_LEGGINGS)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_HELMET)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.DIAMOND_SHOVEL)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_SWORD)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_BOOTS)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_CHESTPLATE)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_LEGGINGS)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_HELMET)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_PICKAXE)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.IRON_SHOVEL)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.IGLOO_CHEST_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_NUGGET).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.STONE_AXE).setWeight(2))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10))
						.withEntry(ItemEntry.builder(Items.EMERALD))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.GOLDEN_APPLE)))
		);
		biConsumer.accept(
			LootTables.JUNGLE_TEMPLE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.BAMBOO).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(16).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(3))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.BOOK).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 2.0F))
						.withEntry(ItemEntry.builder(Items.ARROW).setWeight(30).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.NETHER_BRIDGE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.GOLDEN_SWORD).setWeight(5))
						.withEntry(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.FLINT_AND_STEEL).setWeight(5))
						.withEntry(ItemEntry.builder(Items.NETHER_WART).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(10))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).setWeight(8))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR).setWeight(5))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).setWeight(3))
						.withEntry(ItemEntry.builder(Blocks.OBSIDIAN).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.PILLAGER_OUTPOST_CHEST,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(UniformLootTableRange.between(0.0F, 1.0F)).withEntry(ItemEntry.builder(Items.CROSSBOW)))
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.POTATO).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.CARROT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Blocks.DARK_OAK_LOG).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).setWeight(7))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.ARROW).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.TRIPWIRE_HOOK).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(1).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_MAP_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.MAP)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.COMPASS))
						.withEntry(ItemEntry.builder(Items.MAP))
						.withEntry(ItemEntry.builder(Items.CLOCK))
						.withEntry(ItemEntry.builder(Items.PAPER).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.FEATHER).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_SUPPLY_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.PAPER).setWeight(8).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.POTATO).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.POISONOUS_POTATO).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.CARROT).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 21.0F))))
						.withEntry(
							ItemEntry.builder(Items.SUSPICIOUS_STEW)
								.setWeight(10)
								.method_438(
									SetStewEffectLootFunction.builder()
										.withEffect(StatusEffects.NIGHT_VISION, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.JUMP_BOOST, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.WEAKNESS, UniformLootTableRange.between(6.0F, 8.0F))
										.withEffect(StatusEffects.BLINDNESS, UniformLootTableRange.between(5.0F, 7.0F))
										.withEffect(StatusEffects.POISON, UniformLootTableRange.between(10.0F, 20.0F))
										.withEffect(StatusEffects.SATURATION, UniformLootTableRange.between(7.0F, 10.0F))
								)
						)
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 24.0F))))
						.withEntry(ItemEntry.builder(Blocks.PUMPKIN).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.BAMBOO).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.GUNPOWDER).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Blocks.TNT).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.LEATHER_HELMET).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.LEATHER_CHESTPLATE).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.LEATHER_LEGGINGS).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.LEATHER_BOOTS).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_TREASURE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(90).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(5))
						.withEntry(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).setWeight(5))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.IRON_NUGGET).setWeight(50).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_NUGGET).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.LAPIS_LAZULI).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SIMPLE_DUNGEON_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(20))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE).setWeight(15))
						.withEntry(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
						.withEntry(ItemEntry.builder(Items.MUSIC_DISC_13).setWeight(15))
						.withEntry(ItemEntry.builder(Items.MUSIC_DISC_CAT).setWeight(15))
						.withEntry(ItemEntry.builder(Items.NAME_TAG).setWeight(20))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).setWeight(10))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR).setWeight(15))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).setWeight(5))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(20))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BUCKET).setWeight(10))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.MELON_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.GUNPOWDER).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SPAWN_BONUS_CHEST_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STONE_AXE))
						.withEntry(ItemEntry.builder(Items.WOODEN_AXE).setWeight(3))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STONE_PICKAXE))
						.withEntry(ItemEntry.builder(Items.WOODEN_PICKAXE).setWeight(3))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.SALMON).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.STICK).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.OAK_PLANKS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.OAK_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.SPRUCE_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.BIRCH_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.JUNGLE_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.ACACIA_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.DARK_OAK_LOG).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_CORRIDOR_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.ENDER_PEARL).setWeight(10))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_PICKAXE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_SWORD).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_CHESTPLATE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_HELMET).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_LEGGINGS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_BOOTS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE))
						.withEntry(ItemEntry.builder(Items.SADDLE))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.BOOK).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_CROSSING_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_PICKAXE))
						.withEntry(ItemEntry.builder(Items.BOOK).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_LIBRARY_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.PAPER).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.MAP))
						.withEntry(ItemEntry.builder(Items.COMPASS))
						.withEntry(
							ItemEntry.builder(Items.BOOK)
								.setWeight(10)
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.UNDERWATER_RUIN_BIG_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_NUGGET).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HELMET))
						.withEntry(ItemEntry.builder(Items.FISHING_ROD).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(
							ItemEntry.builder(Items.MAP)
								.setWeight(10)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.UNDERWATER_RUIN_SMALL_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.STONE_AXE).setWeight(2))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(5))
						.withEntry(ItemEntry.builder(Items.EMERALD))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HELMET))
						.withEntry(ItemEntry.builder(Items.FISHING_ROD).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(
							ItemEntry.builder(Items.MAP)
								.setWeight(5)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_WEAPONSMITH_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_PICKAXE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_SWORD).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_CHESTPLATE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_HELMET).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_LEGGINGS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.IRON_BOOTS).setWeight(5))
						.withEntry(ItemEntry.builder(Blocks.OBSIDIAN).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.OAK_SAPLING).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(3))
						.withEntry(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_TOOLSMITH_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.DIAMOND).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_PICKAXE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.STICK).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_SHOVEL).setWeight(5))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_CARTOGRAPHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.MAP).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.PAPER).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.COMPASS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.STICK).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_MASON_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.CLAY_BALL).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.FLOWER_POT).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.STONE).setWeight(2))
						.withEntry(ItemEntry.builder(Blocks.STONE_BRICKS).setWeight(2))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.YELLOW_DYE).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.SMOOTH_STONE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_ARMORER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.IRON_HELMET).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_SHEPHERD_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Blocks.WHITE_WOOL).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.BLACK_WOOL).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.GRAY_WOOL).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.BROWN_WOOL).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.LIGHT_GRAY_WOOL).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
						.withEntry(ItemEntry.builder(Items.SHEARS).setWeight(1))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_BUTCHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
						.withEntry(ItemEntry.builder(Items.PORKCHOP).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BEEF).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.MUTTON).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_FLETCHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
						.withEntry(ItemEntry.builder(Items.ARROW).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.FEATHER).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.EGG).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.FLINT).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.STICK).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_FISHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1))
						.withEntry(ItemEntry.builder(Items.COD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.SALMON).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.WATER_BUCKET).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BARREL).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.WHEAT_SEEDS).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_TANNERY_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.LEATHER).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.LEATHER_CHESTPLATE).setWeight(2))
						.withEntry(ItemEntry.builder(Items.LEATHER_BOOTS).setWeight(2))
						.withEntry(ItemEntry.builder(Items.LEATHER_HELMET).setWeight(2))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.LEATHER_LEGGINGS).setWeight(2))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_TEMPLE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.LAPIS_LAZULI).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_PLAINS_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.GOLD_NUGGET).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.DANDELION).setWeight(2))
						.withEntry(ItemEntry.builder(Items.POPPY).setWeight(1))
						.withEntry(ItemEntry.builder(Items.POTATO).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.APPLE).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(1))
						.withEntry(ItemEntry.builder(Items.FEATHER).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.OAK_SAPLING).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_TAIGA_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.IRON_NUGGET).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.FERN).setWeight(2))
						.withEntry(ItemEntry.builder(Items.LARGE_FERN).setWeight(2))
						.withEntry(ItemEntry.builder(Items.POTATO).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.SWEET_BERRIES).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_SEEDS).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_PIE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.SPRUCE_SAPLING).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.SPRUCE_SIGN).setWeight(1))
						.withEntry(ItemEntry.builder(Items.SPRUCE_LOG).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_SAVANNA_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.GOLD_NUGGET).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.GRASS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.TALL_GRASS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.WHEAT_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.ACACIA_SAPLING).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.SADDLE).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.TORCH).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.BUCKET).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_SNOWY_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Blocks.BLUE_ICE).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.SNOW_BLOCK).setWeight(4))
						.withEntry(ItemEntry.builder(Items.POTATO).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SOUP).setWeight(1))
						.withEntry(ItemEntry.builder(Items.FURNACE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.SNOWBALL).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_VILLAGE_DESERT_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.CLAY_BALL).setWeight(1))
						.withEntry(ItemEntry.builder(Items.GREEN_DYE).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.CACTUS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.DEAD_BUSH).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.EMERALD).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.WOODLAND_MANSION_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.LEAD).setWeight(20))
						.withEntry(ItemEntry.builder(Items.GOLDEN_APPLE).setWeight(15))
						.withEntry(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).setWeight(2))
						.withEntry(ItemEntry.builder(Items.MUSIC_DISC_13).setWeight(15))
						.withEntry(ItemEntry.builder(Items.MUSIC_DISC_CAT).setWeight(15))
						.withEntry(ItemEntry.builder(Items.NAME_TAG).setWeight(20))
						.withEntry(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE).setWeight(10))
						.withEntry(ItemEntry.builder(Items.DIAMOND_HOE).setWeight(15))
						.withEntry(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).setWeight(5))
						.withEntry(ItemEntry.builder(Items.BOOK).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.IRON_INGOT).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.GOLD_INGOT).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BREAD).setWeight(20))
						.withEntry(ItemEntry.builder(Items.WHEAT).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BUCKET).setWeight(10))
						.withEntry(ItemEntry.builder(Items.REDSTONE).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.COAL).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.MELON_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.BEETROOT_SEEDS).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.BONE).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.GUNPOWDER).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
	}
}
