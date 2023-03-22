package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetInstrumentLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VanillaChestLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			LootTables.ABANDONED_MINESHAFT_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(30))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
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
		);
		exporter.accept(LootTables.BASTION_BRIDGE_CHEST, createBastionBridgeChestTableBuilder());
		exporter.accept(LootTables.BASTION_HOGLIN_STABLE_CHEST, createBastionHoglinStableChestTableBuilder());
		exporter.accept(LootTables.BASTION_OTHER_CHEST, createBastionOtherChestTableBuilder());
		exporter.accept(LootTables.BASTION_TREASURE_CHEST, createBastionTreasureChestTableBuilder());
		exporter.accept(
			LootTables.BURIED_TREASURE_CHEST,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.HEART_OF_THE_SEA)))
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(5.0F, 8.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.TNT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(0.0F, 1.0F))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.IRON_SWORD))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(2.0F))
						.with(ItemEntry.builder(Items.COOKED_COD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.COOKED_SALMON).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(0.0F, 2.0F))
						.with(ItemEntry.builder(Items.POTION))
						.apply(SetPotionLootFunction.builder(Potions.WATER_BREATHING))
				)
		);
		exporter.accept(LootTables.ANCIENT_CITY_CHEST, createAncientCityChestTableBuilder());
		exporter.accept(
			LootTables.ANCIENT_CITY_ICE_BOX_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(4.0F, 10.0F))
						.with(
							ItemEntry.builder(Items.SUSPICIOUS_STEW)
								.weight(1)
								.apply(
									SetStewEffectLootFunction.builder()
										.withEffect(StatusEffects.NIGHT_VISION, UniformLootNumberProvider.create(7.0F, 10.0F))
										.withEffect(StatusEffects.BLINDNESS, UniformLootNumberProvider.create(5.0F, 7.0F))
								)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))
						)
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.BAKED_POTATO).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.PACKED_ICE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.SNOWBALL).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
				)
		);
		exporter.accept(LootTables.DESERT_PYRAMID_CHEST, createDesertPyramidChestTableBuilder());
		exporter.accept(LootTables.END_CITY_TREASURE_CHEST, createEndCityTreasureChestTableBuilder());
		exporter.accept(
			LootTables.IGLOO_CHEST_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STONE_AXE).weight(2))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
				)
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.GOLDEN_APPLE)))
		);
		exporter.accept(LootTables.JUNGLE_TEMPLE_CHEST, createJungleTempleChestTableBuilder());
		exporter.accept(
			LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
						.with(ItemEntry.builder(Items.ARROW).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
				)
		);
		exporter.accept(LootTables.NETHER_BRIDGE_CHEST, createNetherBridgeChestTableBuilder());
		exporter.accept(LootTables.PILLAGER_OUTPOST_CHEST, createPillagerOutpostChestTableBuilder());
		exporter.accept(LootTables.SHIPWRECK_MAP_CHEST, createShipwreckMapChestTableBuilder());
		exporter.accept(LootTables.SHIPWRECK_SUPPLY_CHEST, createShipwreckSupplyChestTableBuilder());
		exporter.accept(LootTables.SHIPWRECK_TREASURE_CHEST, createShipwreckTreasureChestTableBuilder());
		exporter.accept(
			LootTables.SIMPLE_DUNGEON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.SADDLE).weight(20))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_OTHERSIDE).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(15))
						.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(15))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(20))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(20))
						.with(ItemEntry.builder(Items.WHEAT).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BUCKET).weight(10))
						.with(ItemEntry.builder(Items.REDSTONE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(3.0F))
						.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
				)
		);
		exporter.accept(
			LootTables.SPAWN_BONUS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.STONE_AXE))
						.with(ItemEntry.builder(Items.WOODEN_AXE).weight(3))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.STONE_PICKAXE))
						.with(ItemEntry.builder(Items.WOODEN_PICKAXE).weight(3))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(3.0F))
						.with(ItemEntry.builder(Items.APPLE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.SALMON).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(4.0F))
						.with(ItemEntry.builder(Items.STICK).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.OAK_PLANKS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.OAK_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.SPRUCE_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.BIRCH_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.JUNGLE_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.ACACIA_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.DARK_OAK_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.MANGROVE_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
		exporter.accept(LootTables.STRONGHOLD_CORRIDOR_CHEST, createStrongholdCorridorChestTableBuilder());
		exporter.accept(
			LootTables.STRONGHOLD_CROSSING_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE))
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments()))
				)
		);
		exporter.accept(LootTables.STRONGHOLD_LIBRARY_CHEST, createStrongholdLibraryChestTableBuilder());
		exporter.accept(
			LootTables.UNDERWATER_RUIN_BIG_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE))
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(10)
								.apply(
									ExplorationMapLootFunction.builder()
										.withDestination(StructureTags.ON_TREASURE_MAPS)
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
								.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure")))
						)
				)
		);
		exporter.accept(
			LootTables.UNDERWATER_RUIN_SMALL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STONE_AXE).weight(2))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(5))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(5)
								.apply(
									ExplorationMapLootFunction.builder()
										.withDestination(StructureTags.ON_TREASURE_MAPS)
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
								.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure")))
						)
				)
		);
		exporter.accept(
			LootTables.VILLAGE_WEAPONSMITH_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(ItemEntry.builder(Items.IRON_SWORD).weight(5))
						.with(ItemEntry.builder(Items.IRON_CHESTPLATE).weight(5))
						.with(ItemEntry.builder(Items.IRON_HELMET).weight(5))
						.with(ItemEntry.builder(Items.IRON_LEGGINGS).weight(5))
						.with(ItemEntry.builder(Items.IRON_BOOTS).weight(5))
						.with(ItemEntry.builder(Blocks.OBSIDIAN).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.OAK_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(3))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_TOOLSMITH_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(ItemEntry.builder(Items.COAL).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_SHOVEL).weight(5))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_CARTOGRAPHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.MAP).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.PAPER).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.COMPASS).weight(5))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_MASON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.CLAY_BALL).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FLOWER_POT).weight(1))
						.with(ItemEntry.builder(Blocks.STONE).weight(2))
						.with(ItemEntry.builder(Blocks.STONE_BRICKS).weight(2))
						.with(ItemEntry.builder(Items.BREAD).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.YELLOW_DYE).weight(1))
						.with(ItemEntry.builder(Blocks.SMOOTH_STONE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_ARMORER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.IRON_HELMET).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_SHEPARD_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Blocks.WHITE_WOOL).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.BLACK_WOOL).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.GRAY_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.BROWN_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.LIGHT_GRAY_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.SHEARS).weight(1))
						.with(ItemEntry.builder(Items.WHEAT).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_BUTCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.PORKCHOP).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WHEAT).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BEEF).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.MUTTON).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_FLETCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.ARROW).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FEATHER).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EGG).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FLINT).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_FISHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.COD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.SALMON).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WATER_BUCKET).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BARREL).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WHEAT_SEEDS).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_TANNERY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.LEATHER).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(2))
						.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(2))
						.with(ItemEntry.builder(Items.LEATHER_HELMET).weight(2))
						.with(ItemEntry.builder(Items.BREAD).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(2))
						.with(ItemEntry.builder(Items.SADDLE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_TEMPLE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.REDSTONE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_PLAINS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.DANDELION).weight(2))
						.with(ItemEntry.builder(Items.POPPY).weight(1))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(1))
						.with(ItemEntry.builder(Items.FEATHER).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.OAK_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.FERN).weight(2))
						.with(ItemEntry.builder(Items.LARGE_FERN).weight(2))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SWEET_BERRIES).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_PIE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.SPRUCE_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.SPRUCE_SIGN).weight(1))
						.with(ItemEntry.builder(Items.SPRUCE_LOG).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GRASS).weight(5))
						.with(ItemEntry.builder(Items.TALL_GRASS).weight(5))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.WHEAT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.ACACIA_SAPLING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(1))
						.with(ItemEntry.builder(Blocks.TORCH).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.BUCKET).weight(1))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Blocks.BLUE_ICE).weight(1))
						.with(ItemEntry.builder(Blocks.SNOW_BLOCK).weight(4))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SOUP).weight(1))
						.with(ItemEntry.builder(Items.FURNACE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SNOWBALL).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
				)
		);
		exporter.accept(
			LootTables.VILLAGE_DESERT_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.CLAY_BALL).weight(1))
						.with(ItemEntry.builder(Items.GREEN_DYE).weight(1))
						.with(ItemEntry.builder(Blocks.CACTUS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(1))
						.with(ItemEntry.builder(Blocks.DEAD_BUSH).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
		exporter.accept(LootTables.WOODLAND_MANSION_CHEST, createWoodlandMansionChestTableBuilder());
		exporter.accept(
			LootTables.RUINED_PORTAL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(4.0F, 8.0F))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.FLINT).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(9.0F, 18.0F))))
						.with(ItemEntry.builder(Items.FLINT_AND_STEEL).weight(40))
						.with(ItemEntry.builder(Items.FIRE_CHARGE).weight(40))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 24.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_AXE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_HOE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_SHOVEL).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_PICKAXE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_BOOTS).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GLISTERING_MELON_SLICE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.LIGHT_WEIGHTED_PRESSURE_PLATE).weight(5))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.CLOCK).weight(5))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BELL).weight(1))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(1))
						.with(ItemEntry.builder(Items.GOLD_BLOCK).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
		);
	}

	public static LootTable.Builder createShipwreckSupplyChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(3.0F, 10.0F))
					.with(ItemEntry.builder(Items.PAPER).weight(8).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 12.0F))))
					.with(ItemEntry.builder(Items.POTATO).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Items.MOSS_BLOCK).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.POISONOUS_POTATO).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Items.CARROT).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
					.with(ItemEntry.builder(Items.WHEAT).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 21.0F))))
					.with(
						ItemEntry.builder(Items.SUSPICIOUS_STEW)
							.weight(10)
							.apply(
								SetStewEffectLootFunction.builder()
									.withEffect(StatusEffects.NIGHT_VISION, UniformLootNumberProvider.create(7.0F, 10.0F))
									.withEffect(StatusEffects.JUMP_BOOST, UniformLootNumberProvider.create(7.0F, 10.0F))
									.withEffect(StatusEffects.WEAKNESS, UniformLootNumberProvider.create(6.0F, 8.0F))
									.withEffect(StatusEffects.BLINDNESS, UniformLootNumberProvider.create(5.0F, 7.0F))
									.withEffect(StatusEffects.POISON, UniformLootNumberProvider.create(10.0F, 20.0F))
									.withEffect(StatusEffects.SATURATION, UniformLootNumberProvider.create(7.0F, 10.0F))
							)
					)
					.with(ItemEntry.builder(Items.COAL).weight(6).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
					.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 24.0F))))
					.with(ItemEntry.builder(Blocks.PUMPKIN).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Blocks.BAMBOO).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.GUNPOWDER).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.TNT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
					.with(ItemEntry.builder(Items.LEATHER_HELMET).weight(3).apply(EnchantRandomlyLootFunction.builder()))
					.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(3).apply(EnchantRandomlyLootFunction.builder()))
					.with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(3).apply(EnchantRandomlyLootFunction.builder()))
					.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(3).apply(EnchantRandomlyLootFunction.builder()))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(5))
					.with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	public static LootTable.Builder createShipwreckMapChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.MAP)
							.apply(
								ExplorationMapLootFunction.builder()
									.withDestination(StructureTags.ON_TREASURE_MAPS)
									.withDecoration(MapIcon.Type.RED_X)
									.withZoom((byte)1)
									.withSkipExistingChunks(false)
							)
							.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure")))
					)
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(3.0F))
					.with(ItemEntry.builder(Items.COMPASS))
					.with(ItemEntry.builder(Items.MAP))
					.with(ItemEntry.builder(Items.CLOCK))
					.with(ItemEntry.builder(Items.PAPER).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
					.with(ItemEntry.builder(Items.FEATHER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.BOOK).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(5))
					.with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	public static LootTable.Builder createBastionHoglinStableChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.DIAMOND_SHOVEL)
							.weight(15)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(12)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.95F)))
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(8).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(5).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(12).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Blocks.GOLD_BLOCK).weight(16).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 17.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(10).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(3.0F, 4.0F))
					.with(
						ItemEntry.builder(Items.GOLDEN_AXE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.GLOWSTONE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))))
					.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.SOUL_SAND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Blocks.CRIMSON_NYLIUM).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
					.with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 17.0F))))
					.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
					.with(ItemEntry.builder(Items.PORKCHOP).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Items.COOKED_PORKCHOP).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.CRIMSON_FUNGUS).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Blocks.CRIMSON_ROOTS).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(11))
					.with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(9))
					.with(ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createBastionBridgeChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(ItemEntry.builder(Blocks.LODESTONE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
					.with(
						ItemEntry.builder(Items.CROSSBOW)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.5F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Items.SPECTRAL_ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(10.0F, 28.0F))))
					.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 12.0F))))
					.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
					.with(ItemEntry.builder(Blocks.GOLD_BLOCK).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_SWORD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(
						ItemEntry.builder(Items.GOLDEN_CHESTPLATE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_HELMET)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_LEGGINGS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_BOOTS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_AXE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
					.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
					.with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 17.0F))))
					.with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(11))
					.with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(9))
					.with(ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createEndCityTreasureChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 6.0F))
					.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(3))
					.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
					.with(
						ItemEntry.builder(Items.DIAMOND_SWORD)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_BOOTS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_HELMET)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_SHOVEL)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_SWORD)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_BOOTS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_CHESTPLATE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_HELMET)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_PICKAXE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(
						ItemEntry.builder(Items.IRON_SHOVEL)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(14))
					.with(ItemEntry.builder(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createNetherBridgeChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 4.0F))
					.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(5))
					.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(5))
					.with(ItemEntry.builder(Items.FLINT_AND_STEEL).weight(5))
					.with(ItemEntry.builder(Items.NETHER_WART).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(10))
					.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(8))
					.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(5))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(3))
					.with(ItemEntry.builder(Blocks.OBSIDIAN).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(14))
					.with(ItemEntry.builder(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createBastionTreasureChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(3.0F))
					.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(15).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Blocks.ANCIENT_DEBRIS).weight(10).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(8).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Blocks.ANCIENT_DEBRIS).weight(4).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
					.with(
						ItemEntry.builder(Items.DIAMOND_SWORD)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_HELMET)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_BOOTS)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Items.DIAMOND_SWORD).weight(6))
					.with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(5))
					.with(ItemEntry.builder(Items.DIAMOND_HELMET).weight(5))
					.with(ItemEntry.builder(Items.DIAMOND_BOOTS).weight(5))
					.with(ItemEntry.builder(Items.DIAMOND_LEGGINGS).weight(5))
					.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(3.0F, 4.0F))
					.with(ItemEntry.builder(Items.SPECTRAL_ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(12.0F, 25.0F))))
					.with(ItemEntry.builder(Blocks.GOLD_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.IRON_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 9.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 9.0F))))
					.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 5.0F))))
					.with(ItemEntry.builder(Items.QUARTZ).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 23.0F))))
					.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 15.0F))))
					.with(ItemEntry.builder(Items.MAGMA_CREAM).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 8.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(11))
					.with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			)
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1)));
	}

	public static LootTable.Builder createBastionOtherChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(6)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Items.DIAMOND_SHOVEL).weight(6).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(
						ItemEntry.builder(Items.CROSSBOW)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.9F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(4).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(10.0F, 22.0F))))
					.with(ItemEntry.builder(Items.PIGLIN_BANNER_PATTERN).weight(9).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.MUSIC_DISC_PIGSTEP).weight(5).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 17.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(9).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(2.0F))
					.with(
						ItemEntry.builder(Items.IRON_SWORD)
							.weight(2)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.9F)))
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Blocks.IRON_BLOCK).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(
						ItemEntry.builder(Items.GOLDEN_BOOTS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_AXE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder())
					)
					.with(ItemEntry.builder(Blocks.GOLD_BLOCK).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.CROSSBOW).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 6.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_SWORD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_HELMET).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_BOOTS).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(3.0F, 4.0F))
					.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Blocks.CHAIN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 10.0F))))
					.with(ItemEntry.builder(Items.MAGMA_CREAM).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F))))
					.with(ItemEntry.builder(Blocks.BONE_BLOCK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 6.0F))))
					.with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
					.with(ItemEntry.builder(Blocks.OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
					.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
					.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 6.0F))))
					.with(ItemEntry.builder(Items.ARROW).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(5.0F, 17.0F))))
					.with(ItemEntry.builder(Items.COOKED_PORKCHOP).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(11))
					.with(ItemEntry.builder(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(9))
					.with(ItemEntry.builder(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createWoodlandMansionChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
					.with(ItemEntry.builder(Items.LEAD).weight(20))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
					.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
					.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(15))
					.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(15))
					.with(ItemEntry.builder(Items.NAME_TAG).weight(20))
					.with(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE).weight(10))
					.with(ItemEntry.builder(Items.DIAMOND_HOE).weight(15))
					.with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(5))
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(1.0F, 4.0F))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.BREAD).weight(20))
					.with(ItemEntry.builder(Items.WHEAT).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.BUCKET).weight(10))
					.with(ItemEntry.builder(Items.REDSTONE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
					.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
					.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(3.0F))
					.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
					.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 8.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(1))
					.with(ItemEntry.builder(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createStrongholdLibraryChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 10.0F))
					.with(ItemEntry.builder(Items.BOOK).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.PAPER).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.MAP))
					.with(ItemEntry.builder(Items.COMPASS))
					.with(
						ItemEntry.builder(Items.BOOK)
							.weight(10)
							.apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments())
					)
			)
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)));
	}

	public static LootTable.Builder createStrongholdCorridorChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 3.0F))
					.with(ItemEntry.builder(Items.ENDER_PEARL).weight(10))
					.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F))))
					.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
					.with(ItemEntry.builder(Items.IRON_SWORD).weight(5))
					.with(ItemEntry.builder(Items.IRON_CHESTPLATE).weight(5))
					.with(ItemEntry.builder(Items.IRON_HELMET).weight(5))
					.with(ItemEntry.builder(Items.IRON_LEGGINGS).weight(5))
					.with(ItemEntry.builder(Items.IRON_BOOTS).weight(5))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE))
					.with(ItemEntry.builder(Items.SADDLE))
					.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
					.with(ItemEntry.builder(Items.MUSIC_DISC_OTHERSIDE))
					.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments()))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(9))
					.with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createAncientCityChestTableBuilder() {
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
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(30.0F, 50.0F)).allowTreasureEnchantments())
					)
					.with(ItemEntry.builder(Items.LEAD).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.SADDLE).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(2))
					.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(2))
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(2)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(30.0F, 50.0F)).allowTreasureEnchantments())
					)
					.with(ItemEntry.builder(Items.BOOK).weight(3).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SWIFT_SNEAK)))
					.with(ItemEntry.builder(Items.SCULK).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 10.0F))))
					.with(ItemEntry.builder(Items.SCULK_SENSOR).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.CANDLE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
					.with(ItemEntry.builder(Items.AMETHYST_SHARD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.GLOW_BERRIES).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(
						ItemEntry.builder(Items.IRON_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(UniformLootNumberProvider.create(20.0F, 39.0F)).allowTreasureEnchantments())
					)
					.with(ItemEntry.builder(Items.ECHO_SHARD).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.DISC_FRAGMENT_5).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(
						ItemEntry.builder(Items.POTION)
							.weight(5)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
							.apply(SetPotionLootFunction.builder(Potions.STRONG_REGENERATION))
					)
					.with(ItemEntry.builder(Items.BOOK).weight(5).apply(EnchantRandomlyLootFunction.builder()))
					.with(ItemEntry.builder(Items.BOOK).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 10.0F))))
					.with(ItemEntry.builder(Items.BONE).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.SOUL_TORCH).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 15.0F))))
					.with(ItemEntry.builder(Items.COAL).weight(7).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 15.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(75))
					.with(ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(4))
					.with(ItemEntry.builder(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public static LootTable.Builder createJungleTempleChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootNumberProvider.create(30.0F)).allowTreasureEnchantments()))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(2))
					.with(ItemEntry.builder(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	public static LootTable.Builder createShipwreckTreasureChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(3.0F, 6.0F))
					.with(ItemEntry.builder(Items.IRON_INGOT).weight(90).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.GOLD_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.EMERALD).weight(40).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 5.0F))))
					.with(ItemEntry.builder(Items.DIAMOND).weight(5))
					.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(5))
			)
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 5.0F))
					.with(ItemEntry.builder(Items.IRON_NUGGET).weight(50).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
					.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
					.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 10.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(5))
					.with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	public static LootTable.Builder createPillagerOutpostChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).weight(1).apply(EnchantRandomlyLootFunction.builder()))
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
			);
	}

	public static LootTable.Builder createDesertPyramidChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).weight(20).apply(EnchantRandomlyLootFunction.builder()))
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
					.with(EmptyEntry.builder().weight(6))
					.with(ItemEntry.builder(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}
}
