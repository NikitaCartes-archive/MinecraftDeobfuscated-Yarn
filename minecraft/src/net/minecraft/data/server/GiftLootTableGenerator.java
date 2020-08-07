package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void method_20187(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_16216,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8245).weight(10))
						.with(ItemEntry.builder(Items.field_8073).weight(10))
						.with(ItemEntry.builder(Items.field_8726).weight(10))
						.with(ItemEntry.builder(Items.field_8153).weight(10))
						.with(ItemEntry.builder(Items.field_8511).weight(10))
						.with(ItemEntry.builder(Items.field_8276).weight(10))
						.with(ItemEntry.builder(Items.field_8614).weight(2))
				)
		);
		biConsumer.accept(
			LootTables.field_19062,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8283))
						.with(ItemEntry.builder(Items.field_8873))
						.with(ItemEntry.builder(Items.field_8218))
						.with(ItemEntry.builder(Items.field_8313))
				)
		);
		biConsumer.accept(
			LootTables.field_19063,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8752))
						.with(ItemEntry.builder(Items.field_8544))
						.with(ItemEntry.builder(Items.field_8261))
						.with(ItemEntry.builder(Items.field_8176))
						.with(ItemEntry.builder(Items.field_8347))
				)
		);
		biConsumer.accept(
			LootTables.field_19064,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8895)).with(ItemEntry.builder(Items.field_8407)))
		);
		biConsumer.accept(
			LootTables.field_19065,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8725)).with(ItemEntry.builder(Items.field_8759)))
		);
		biConsumer.accept(
			LootTables.field_19066,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8229))
						.with(ItemEntry.builder(Items.field_8741))
						.with(ItemEntry.builder(Items.field_8423))
				)
		);
		biConsumer.accept(
			LootTables.field_19067,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8429)).with(ItemEntry.builder(Items.field_8209)))
		);
		biConsumer.accept(
			LootTables.field_19068,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8107).weight(26))
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:swiftness"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:strength"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:healing"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:harming"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:leaping"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:regeneration"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water_breathing"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:invisibility"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:night_vision"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:weakness"))))
						)
						.with(
							ItemEntry.builder(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:poison"))))
						)
				)
		);
		biConsumer.accept(
			LootTables.field_19069, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8745)))
		);
		biConsumer.accept(
			LootTables.field_19070, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8529)))
		);
		biConsumer.accept(
			LootTables.field_19071, LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.CLAY)))
		);
		biConsumer.accept(
			LootTables.field_19072,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
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
		biConsumer.accept(
			LootTables.field_19073,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8387))
						.with(ItemEntry.builder(Items.field_8062))
						.with(ItemEntry.builder(Items.field_8431))
						.with(ItemEntry.builder(Items.field_8776))
				)
		);
		biConsumer.accept(
			LootTables.field_19074,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8062))
						.with(ItemEntry.builder(Items.field_8825))
						.with(ItemEntry.builder(Items.field_8475))
				)
		);
	}
}
