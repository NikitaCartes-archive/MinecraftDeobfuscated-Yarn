package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;

public class VanillaGiftLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			LootTables.CAT_MORNING_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.RABBIT_HIDE).weight(10))
						.with(ItemEntry.builder(Items.RABBIT_FOOT).weight(10))
						.with(ItemEntry.builder(Items.CHICKEN).weight(10))
						.with(ItemEntry.builder(Items.FEATHER).weight(10))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10))
						.with(ItemEntry.builder(Items.STRING).weight(10))
						.with(ItemEntry.builder(Items.PHANTOM_MEMBRANE).weight(2))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.CHAINMAIL_HELMET))
						.with(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE))
						.with(ItemEntry.builder(Items.CHAINMAIL_LEGGINGS))
						.with(ItemEntry.builder(Items.CHAINMAIL_BOOTS))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.COOKED_RABBIT))
						.with(ItemEntry.builder(Items.COOKED_CHICKEN))
						.with(ItemEntry.builder(Items.COOKED_PORKCHOP))
						.with(ItemEntry.builder(Items.COOKED_BEEF))
						.with(ItemEntry.builder(Items.COOKED_MUTTON))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.MAP)).with(ItemEntry.builder(Items.PAPER)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.REDSTONE)).with(ItemEntry.builder(Items.LAPIS_LAZULI)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BREAD))
						.with(ItemEntry.builder(Items.PUMPKIN_PIE))
						.with(ItemEntry.builder(Items.COOKIE))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.COD)).with(ItemEntry.builder(Items.SALMON)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARROW).weight(26))
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SWIFTNESS))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.SLOWNESS))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.STRENGTH))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.HEALING))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.HARMING))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.LEAPING))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.REGENERATION))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.FIRE_RESISTANCE))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.WATER_BREATHING))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.INVISIBILITY))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.NIGHT_VISION))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.WEAKNESS))
						)
						.with(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
								.apply(SetPotionLootFunction.builder(Potions.POISON))
						)
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.LEATHER)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.BOOK)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY,
			LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Items.CLAY)))
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.WHITE_WOOL))
						.with(ItemEntry.builder(Items.ORANGE_WOOL))
						.with(ItemEntry.builder(Items.MAGENTA_WOOL))
						.with(ItemEntry.builder(Items.LIGHT_BLUE_WOOL))
						.with(ItemEntry.builder(Items.YELLOW_WOOL))
						.with(ItemEntry.builder(Items.LIME_WOOL))
						.with(ItemEntry.builder(Items.PINK_WOOL))
						.with(ItemEntry.builder(Items.GRAY_WOOL))
						.with(ItemEntry.builder(Items.LIGHT_GRAY_WOOL))
						.with(ItemEntry.builder(Items.CYAN_WOOL))
						.with(ItemEntry.builder(Items.PURPLE_WOOL))
						.with(ItemEntry.builder(Items.BLUE_WOOL))
						.with(ItemEntry.builder(Items.BROWN_WOOL))
						.with(ItemEntry.builder(Items.GREEN_WOOL))
						.with(ItemEntry.builder(Items.RED_WOOL))
						.with(ItemEntry.builder(Items.BLACK_WOOL))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.STONE_PICKAXE))
						.with(ItemEntry.builder(Items.STONE_AXE))
						.with(ItemEntry.builder(Items.STONE_HOE))
						.with(ItemEntry.builder(Items.STONE_SHOVEL))
				)
		);
		exporter.accept(
			LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.STONE_AXE))
						.with(ItemEntry.builder(Items.GOLDEN_AXE))
						.with(ItemEntry.builder(Items.IRON_AXE))
				)
		);
		exporter.accept(
			LootTables.SNIFFER_DIGGING_GAMEPLAY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.TORCHFLOWER_SEEDS))
						.with(ItemEntry.builder(Items.PITCHER_POD))
				)
		);
	}
}
