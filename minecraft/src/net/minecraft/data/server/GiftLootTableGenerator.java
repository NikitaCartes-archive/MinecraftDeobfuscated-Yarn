package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootTable;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void method_20187(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.CAT_MORNING_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.RABBIT_HIDE).setWeight(10))
						.withEntry(ItemEntry.builder(Items.RABBIT_FOOT).setWeight(10))
						.withEntry(ItemEntry.builder(Items.CHICKEN).setWeight(10))
						.withEntry(ItemEntry.builder(Items.FEATHER).setWeight(10))
						.withEntry(ItemEntry.builder(Items.ROTTEN_FLESH).setWeight(10))
						.withEntry(ItemEntry.builder(Items.STRING).setWeight(10))
						.withEntry(ItemEntry.builder(Items.PHANTOM_MEMBRANE).setWeight(2))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.CHAINMAIL_HELMET))
						.withEntry(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE))
						.withEntry(ItemEntry.builder(Items.CHAINMAIL_LEGGINGS))
						.withEntry(ItemEntry.builder(Items.CHAINMAIL_BOOTS))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.COOKED_RABBIT))
						.withEntry(ItemEntry.builder(Items.COOKED_CHICKEN))
						.withEntry(ItemEntry.builder(Items.COOKED_PORKCHOP))
						.withEntry(ItemEntry.builder(Items.COOKED_BEEF))
						.withEntry(ItemEntry.builder(Items.COOKED_MUTTON))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.MAP)).withEntry(ItemEntry.builder(Items.PAPER)))
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.REDSTONE))
						.withEntry(ItemEntry.builder(Items.LAPIS_LAZULI))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.BREAD))
						.withEntry(ItemEntry.builder(Items.PUMPKIN_PIE))
						.withEntry(ItemEntry.builder(Items.COOKIE))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.COD)).withEntry(ItemEntry.builder(Items.SALMON)))
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.ARROW).setWeight(26))
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:swiftness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:strength"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:healing"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:harming"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:leaping"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:regeneration"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(
									SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance")))
								)
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(
									SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water_breathing")))
								)
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:invisibility"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:night_vision"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:weakness"))))
						)
						.withEntry(
							ItemEntry.builder(Items.TIPPED_ARROW)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:poison"))))
						)
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.LEATHER)))
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.BOOK)))
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY,
			LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.CLAY)))
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.WHITE_WOOL))
						.withEntry(ItemEntry.builder(Items.ORANGE_WOOL))
						.withEntry(ItemEntry.builder(Items.MAGENTA_WOOL))
						.withEntry(ItemEntry.builder(Items.LIGHT_BLUE_WOOL))
						.withEntry(ItemEntry.builder(Items.YELLOW_WOOL))
						.withEntry(ItemEntry.builder(Items.LIME_WOOL))
						.withEntry(ItemEntry.builder(Items.PINK_WOOL))
						.withEntry(ItemEntry.builder(Items.GRAY_WOOL))
						.withEntry(ItemEntry.builder(Items.LIGHT_GRAY_WOOL))
						.withEntry(ItemEntry.builder(Items.CYAN_WOOL))
						.withEntry(ItemEntry.builder(Items.PURPLE_WOOL))
						.withEntry(ItemEntry.builder(Items.BLUE_WOOL))
						.withEntry(ItemEntry.builder(Items.BROWN_WOOL))
						.withEntry(ItemEntry.builder(Items.GREEN_WOOL))
						.withEntry(ItemEntry.builder(Items.RED_WOOL))
						.withEntry(ItemEntry.builder(Items.BLACK_WOOL))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STONE_PICKAXE))
						.withEntry(ItemEntry.builder(Items.STONE_AXE))
						.withEntry(ItemEntry.builder(Items.STONE_HOE))
						.withEntry(ItemEntry.builder(Items.STONE_SHOVEL))
				)
		);
		biConsumer.accept(
			LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY,
			LootTable.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.STONE_AXE))
						.withEntry(ItemEntry.builder(Items.GOLDEN_AXE))
						.withEntry(ItemEntry.builder(Items.IRON_AXE))
				)
		);
	}
}
