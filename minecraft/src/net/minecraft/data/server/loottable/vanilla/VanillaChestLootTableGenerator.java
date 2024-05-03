package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.function.SetInstrumentLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetOminousBottleAmplifierLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.text.Text;

public record VanillaChestLootTableGenerator(RegistryWrapper.WrapperLookup registries) implements LootTableGenerator {
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
		);
		lootTableBiConsumer.accept(LootTables.BASTION_BRIDGE_CHEST, this.createBastionBridgeChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.BASTION_HOGLIN_STABLE_CHEST, this.createBastionHoglinStableChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.BASTION_OTHER_CHEST, this.createBastionOtherChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.BASTION_TREASURE_CHEST, this.createBastionTreasureChestTableBuilder());
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(LootTables.ANCIENT_CITY_CHEST, this.createAncientCityChestTableBuilder());
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(LootTables.DESERT_PYRAMID_CHEST, this.createDesertPyramidChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.END_CITY_TREASURE_CHEST, this.createEndCityTreasureChestTableBuilder());
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(LootTables.JUNGLE_TEMPLE_CHEST, this.createJungleTempleChestTableBuilder());
		lootTableBiConsumer.accept(
			LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 2.0F))
						.with(ItemEntry.builder(Items.ARROW).weight(30).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
				)
		);
		lootTableBiConsumer.accept(LootTables.NETHER_BRIDGE_CHEST, this.createNetherBridgeChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.PILLAGER_OUTPOST_CHEST, this.createPillagerOutpostChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.SHIPWRECK_MAP_CHEST, this.createShipwreckMapChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.SHIPWRECK_SUPPLY_CHEST, this.createShipwreckSupplyChestTableBuilder());
		lootTableBiConsumer.accept(LootTables.SHIPWRECK_TREASURE_CHEST, this.createShipwreckTreasureChestTableBuilder());
		lootTableBiConsumer.accept(
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
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder(this.registries)))
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(LootTables.STRONGHOLD_CORRIDOR_CHEST, this.createStrongholdCorridorChestTableBuilder());
		lootTableBiConsumer.accept(
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
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(this.registries, ConstantLootNumberProvider.create(30.0F))))
				)
		);
		lootTableBiConsumer.accept(LootTables.STRONGHOLD_LIBRARY_CHEST, this.createStrongholdLibraryChestTableBuilder());
		lootTableBiConsumer.accept(
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
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(10)
								.apply(
									ExplorationMapLootFunction.builder()
										.withDestination(StructureTags.ON_TREASURE_MAPS)
										.withDecoration(MapDecorationTypes.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
								.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure"), SetNameLootFunction.Target.ITEM_NAME))
						)
				)
		);
		lootTableBiConsumer.accept(
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
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(5)
								.apply(
									ExplorationMapLootFunction.builder()
										.withDestination(StructureTags.ON_TREASURE_MAPS)
										.withDecoration(MapDecorationTypes.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
								.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure"), SetNameLootFunction.Target.ITEM_NAME))
						)
				)
		);
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.SHORT_GRASS).weight(5))
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(LootTables.WOODLAND_MANSION_CHEST, this.createWoodlandMansionChestTableBuilder());
		lootTableBiConsumer.accept(
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
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_AXE).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_HOE).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_SHOVEL).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_PICKAXE).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_BOOTS).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
						.with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).weight(15).apply(EnchantRandomlyLootFunction.builder(this.registries)))
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_DISPENSER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 8.0F))))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_WATER_DISPENSER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.WATER_BUCKET).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
				)
		);
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_POT,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))).weight(125))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))).weight(100))
						.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(100))
						.with(ItemEntry.builder(Items.TRIAL_KEY).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(10))
						.with(ItemEntry.builder(Items.MUSIC_DISC_CREATOR_MUSIC_BOX).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))).weight(5))
						.with(ItemEntry.builder(Items.EMERALD_BLOCK).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_BLOCK).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))).weight(1))
				)
		);
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.4F, 0.9F)))
								.apply(EnchantRandomlyLootFunction.builder(this.registries))
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_CORRIDOR_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootNumberProvider.create(1.0F, 3.0F))
						.with(
							ItemEntry.builder(Items.IRON_AXE)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.4F, 0.9F)))
								.apply(EnchantRandomlyLootFunction.builder(this.registries))
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SHIELD).weight(3).apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.5F, 1.0F))))
						.with(ItemEntry.builder(Items.BOW).weight(3).apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(5.0F, 15.0F))))
						.with(
							ItemEntry.builder(Items.CROSSBOW).weight(2).apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(5.0F, 20.0F)))
						)
						.with(
							ItemEntry.builder(Items.IRON_AXE).weight(2).apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(0.0F, 10.0F)))
						)
						.with(
							ItemEntry.builder(Items.IRON_CHESTPLATE)
								.weight(2)
								.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(0.0F, 10.0F)))
						)
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(2)
								.apply(
									new EnchantRandomlyLootFunction.Builder()
										.options(
											RegistryEntryList.of(
												impl.getOrThrow(Enchantments.SHARPNESS),
												impl.getOrThrow(Enchantments.BANE_OF_ARTHROPODS),
												impl.getOrThrow(Enchantments.EFFICIENCY),
												impl.getOrThrow(Enchantments.FORTUNE),
												impl.getOrThrow(Enchantments.SILK_TOUCH),
												impl.getOrThrow(Enchantments.FEATHER_FALLING)
											)
										)
								)
						)
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(2)
								.apply(
									new EnchantRandomlyLootFunction.Builder()
										.options(
											RegistryEntryList.of(
												impl.getOrThrow(Enchantments.RIPTIDE),
												impl.getOrThrow(Enchantments.LOYALTY),
												impl.getOrThrow(Enchantments.CHANNELING),
												impl.getOrThrow(Enchantments.IMPALING),
												impl.getOrThrow(Enchantments.MENDING)
											)
										)
								)
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.weight(1)
								.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(5.0F, 15.0F)))
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.weight(1)
								.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(5.0F, 15.0F)))
						)
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_COMMON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARROW).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F))))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.weight(4)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 8.0F)))
								.apply(SetPotionLootFunction.builder(Potions.POISON))
						)
						.with(ItemEntry.builder(Items.EMERALD).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.WIND_CHARGE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.HONEY_BOTTLE).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(
							ItemEntry.builder(Items.OMINOUS_BOTTLE)
								.weight(2)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetOminousBottleAmplifierLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
						)
						.with(ItemEntry.builder(Items.WIND_CHARGE).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(1).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(4))
						.with(ItemEntry.builder(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE).weight(3))
						.with(ItemEntry.builder(Items.GUSTER_BANNER_PATTERN).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_PRECIPICE).weight(2))
						.with(ItemEntry.builder(Items.TRIDENT).weight(1))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_RARE_CHEST).weight(8))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_COMMON_CHEST).weight(2))
				)
				.pool(LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 3.0F)).with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_COMMON_CHEST)))
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.conditionally(RandomChanceLootCondition.builder(0.25F))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_UNIQUE_CHEST))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD_BLOCK).weight(5))
						.with(ItemEntry.builder(Items.IRON_BLOCK).weight(4))
						.with(
							ItemEntry.builder(Items.CROSSBOW).weight(4).apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(5.0F, 20.0F)))
						)
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(3))
						.with(
							ItemEntry.builder(Items.DIAMOND_AXE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(10.0F, 20.0F)))
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(10.0F, 20.0F)))
						)
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(2)
								.apply(
									new EnchantRandomlyLootFunction.Builder()
										.options(
											RegistryEntryList.of(
												impl.getOrThrow(Enchantments.KNOCKBACK),
												impl.getOrThrow(Enchantments.PUNCH),
												impl.getOrThrow(Enchantments.SMITE),
												impl.getOrThrow(Enchantments.LOOTING),
												impl.getOrThrow(Enchantments.MULTISHOT)
											)
										)
								)
						)
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(2)
								.apply(
									new EnchantRandomlyLootFunction.Builder().options(RegistryEntryList.of(impl.getOrThrow(Enchantments.BREACH), impl.getOrThrow(Enchantments.DENSITY)))
								)
						)
						.with(
							ItemEntry.builder(Items.BOOK)
								.weight(2)
								.apply(new SetEnchantmentsLootFunction.Builder().enchantment(impl.getOrThrow(Enchantments.WIND_BURST), ConstantLootNumberProvider.create(1.0F)))
						)
						.with(ItemEntry.builder(Items.DIAMOND_BLOCK).weight(1))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(5).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 10.0F))))
						.with(ItemEntry.builder(Items.WIND_CHARGE).weight(4).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(8.0F, 12.0F))))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.weight(3)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 12.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRONG_SLOWNESS))
						)
						.with(ItemEntry.builder(Items.DIAMOND).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F))))
						.with(
							ItemEntry.builder(Items.OMINOUS_BOTTLE)
								.weight(1)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetOminousBottleAmplifierLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
						)
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(3))
						.with(ItemEntry.builder(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE).weight(3))
						.with(ItemEntry.builder(Items.FLOW_BANNER_PATTERN).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_CREATOR).weight(1))
						.with(ItemEntry.builder(Items.HEAVY_CORE).weight(1))
				)
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE_CHEST).weight(8))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST).weight(2))
				)
				.pool(
					LootPool.builder().rolls(UniformLootNumberProvider.create(1.0F, 3.0F)).with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON_CHEST))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.conditionally(RandomChanceLootCondition.builder(0.75F))
						.with(LootTableEntry.builder(LootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE_CHEST))
				)
		);
		this.acceptTrialSpawnerTables(lootTableBiConsumer);
	}

	public void acceptTrialSpawnerTables(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_KEY_SPAWNER,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.TRIAL_KEY)))
		);
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_CONSUMABLES_SPAWNER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.COOKED_CHICKEN).weight(3).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BAKED_POTATO).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.REGENERATION))
						)
						.with(
							ItemEntry.builder(Items.POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SWIFTNESS))
						)
				)
		);
		lootTableBiConsumer.accept(
			LootTables.OMINOUS_TRIAL_CHAMBER_KEY_SPAWNER,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.OMINOUS_TRIAL_KEY)))
		);
		lootTableBiConsumer.accept(
			LootTables.OMINOUS_TRIAL_CHAMBER_CONSUMABLES_SPAWNER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.COOKED_BEEF).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.BAKED_POTATO).weight(3).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(2).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F))))
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
		lootTableBiConsumer.accept(
			LootTables.TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS_SPAWNER,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.WIND_CHARGED))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.OOZING))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.WEAVING))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.INFESTED))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRENGTH))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SWIFTNESS))
						)
						.with(
							ItemEntry.builder(Items.LINGERING_POTION)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SLOW_FALLING))
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
						.with(
							ItemEntry.builder(Items.ARROW)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.POISON))
						)
						.with(
							ItemEntry.builder(Items.ARROW)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRONG_SLOWNESS))
						)
						.with(ItemEntry.builder(Items.FIRE_CHARGE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WIND_CHARGE).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
				)
		);
	}

	public LootTable.Builder createShipwreckSupplyChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.LEATHER_HELMET).weight(3).apply(EnchantRandomlyLootFunction.builder(this.registries)))
					.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(3).apply(EnchantRandomlyLootFunction.builder(this.registries)))
					.with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(3).apply(EnchantRandomlyLootFunction.builder(this.registries)))
					.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(3).apply(EnchantRandomlyLootFunction.builder(this.registries)))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(5))
					.with(ItemEntry.builder(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	public LootTable.Builder createShipwreckMapChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.MAP)
							.apply(
								ExplorationMapLootFunction.builder()
									.withDestination(StructureTags.ON_TREASURE_MAPS)
									.withDecoration(MapDecorationTypes.RED_X)
									.withZoom((byte)1)
									.withSkipExistingChunks(false)
							)
							.apply(SetNameLootFunction.builder(Text.translatable("filled_map.buried_treasure"), SetNameLootFunction.Target.ITEM_NAME))
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

	public LootTable.Builder createBastionHoglinStableChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.DIAMOND_SHOVEL)
							.weight(15)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.8F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(12)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.15F, 0.95F)))
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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

	public LootTable.Builder createBastionBridgeChestTableBuilder() {
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
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_HELMET)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_LEGGINGS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_BOOTS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_AXE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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

	public LootTable.Builder createEndCityTreasureChestTableBuilder() {
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
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_BOOTS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_HELMET)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_SHOVEL)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_SWORD)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_BOOTS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_CHESTPLATE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_LEGGINGS)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_HELMET)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_PICKAXE)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
					.with(
						ItemEntry.builder(Items.IRON_SHOVEL)
							.weight(3)
							.apply(EnchantWithLevelsLootFunction.builder(this.registries, UniformLootNumberProvider.create(20.0F, 39.0F)))
					)
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(14))
					.with(ItemEntry.builder(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public LootTable.Builder createNetherBridgeChestTableBuilder() {
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

	public LootTable.Builder createBastionTreasureChestTableBuilder() {
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
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_HELMET)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_LEGGINGS)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(
						ItemEntry.builder(Items.DIAMOND_BOOTS)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.8F, 1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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

	public LootTable.Builder createBastionOtherChestTableBuilder() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(Items.DIAMOND_PICKAXE)
							.weight(6)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(ItemEntry.builder(Items.DIAMOND_SHOVEL).weight(6).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(
						ItemEntry.builder(Items.CROSSBOW)
							.weight(6)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.9F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(4).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(10).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(10.0F, 22.0F))))
					.with(ItemEntry.builder(Items.PIGLIN_BANNER_PATTERN).weight(9).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.MUSIC_DISC_PIGSTEP).weight(5).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(12).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(6.0F, 17.0F))))
					.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(9).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.SOUL_SPEED))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(2.0F))
					.with(
						ItemEntry.builder(Items.IRON_SWORD)
							.weight(2)
							.apply(SetDamageLootFunction.builder(UniformLootNumberProvider.create(0.1F, 0.9F)))
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
					)
					.with(ItemEntry.builder(Blocks.IRON_BLOCK).weight(2).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))))
					.with(
						ItemEntry.builder(Items.GOLDEN_BOOTS)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(new EnchantRandomlyLootFunction.Builder().option(impl.getOrThrow(Enchantments.SOUL_SPEED)))
					)
					.with(
						ItemEntry.builder(Items.GOLDEN_AXE)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
							.apply(EnchantRandomlyLootFunction.builder(this.registries))
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

	public LootTable.Builder createWoodlandMansionChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder(this.registries)))
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

	public LootTable.Builder createStrongholdLibraryChestTableBuilder() {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(UniformLootNumberProvider.create(2.0F, 10.0F))
					.with(ItemEntry.builder(Items.BOOK).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F))))
					.with(ItemEntry.builder(Items.PAPER).weight(20).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 7.0F))))
					.with(ItemEntry.builder(Items.MAP))
					.with(ItemEntry.builder(Items.COMPASS))
					.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantWithLevelsLootFunction.builder(this.registries, ConstantLootNumberProvider.create(30.0F))))
			)
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1)));
	}

	public LootTable.Builder createStrongholdCorridorChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(this.registries, ConstantLootNumberProvider.create(30.0F))))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(EmptyEntry.builder().weight(9))
					.with(ItemEntry.builder(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
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
					.with(EmptyEntry.builder().weight(75))
					.with(ItemEntry.builder(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).weight(4))
					.with(ItemEntry.builder(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE).weight(1))
			);
	}

	public LootTable.Builder createJungleTempleChestTableBuilder() {
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
			);
	}

	public LootTable.Builder createShipwreckTreasureChestTableBuilder() {
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

	public LootTable.Builder createPillagerOutpostChestTableBuilder() {
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
			);
	}

	public LootTable.Builder createDesertPyramidChestTableBuilder() {
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
					.with(ItemEntry.builder(Items.BOOK).weight(20).apply(EnchantRandomlyLootFunction.builder(this.registries)))
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
