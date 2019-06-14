package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;

public class GiftLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public void method_20187(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_16216,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8245).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8073).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8726).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8153).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8511).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8276).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8614).setWeight(2))
				)
		);
		biConsumer.accept(
			LootTables.field_19062,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8283))
						.method_351(ItemEntry.method_411(Items.field_8873))
						.method_351(ItemEntry.method_411(Items.field_8218))
						.method_351(ItemEntry.method_411(Items.field_8313))
				)
		);
		biConsumer.accept(
			LootTables.field_19063,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8752))
						.method_351(ItemEntry.method_411(Items.field_8544))
						.method_351(ItemEntry.method_411(Items.field_8261))
						.method_351(ItemEntry.method_411(Items.field_8176))
						.method_351(ItemEntry.method_411(Items.field_8347))
				)
		);
		biConsumer.accept(
			LootTables.field_19064,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8895))
						.method_351(ItemEntry.method_411(Items.field_8407))
				)
		);
		biConsumer.accept(
			LootTables.field_19065,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8725))
						.method_351(ItemEntry.method_411(Items.field_8759))
				)
		);
		biConsumer.accept(
			LootTables.field_19066,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8229))
						.method_351(ItemEntry.method_411(Items.field_8741))
						.method_351(ItemEntry.method_411(Items.field_8423))
				)
		);
		biConsumer.accept(
			LootTables.field_19067,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8429))
						.method_351(ItemEntry.method_411(Items.field_8209))
				)
		);
		biConsumer.accept(
			LootTables.field_19068,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8107).setWeight(26))
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:swiftness"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:strength"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:healing"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:harming"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:leaping"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:regeneration"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(
									SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:fire_resistance")))
								)
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(
									SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water_breathing")))
								)
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:invisibility"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:night_vision"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:weakness"))))
						)
						.method_351(
							ItemEntry.method_411(Items.field_8087)
								.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 1.0F)))
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:poison"))))
						)
				)
		);
		biConsumer.accept(
			LootTables.field_19069,
			LootSupplier.builder().withPool(LootPool.builder().method_352(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(Items.field_8745)))
		);
		biConsumer.accept(
			LootTables.field_19070,
			LootSupplier.builder().withPool(LootPool.builder().method_352(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(Items.field_8529)))
		);
		biConsumer.accept(
			LootTables.field_19071,
			LootSupplier.builder().withPool(LootPool.builder().method_352(ConstantLootTableRange.create(1)).method_351(ItemEntry.method_411(Items.CLAY)))
		);
		biConsumer.accept(
			LootTables.field_19072,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.WHITE_WOOL))
						.method_351(ItemEntry.method_411(Items.ORANGE_WOOL))
						.method_351(ItemEntry.method_411(Items.MAGENTA_WOOL))
						.method_351(ItemEntry.method_411(Items.LIGHT_BLUE_WOOL))
						.method_351(ItemEntry.method_411(Items.YELLOW_WOOL))
						.method_351(ItemEntry.method_411(Items.LIME_WOOL))
						.method_351(ItemEntry.method_411(Items.PINK_WOOL))
						.method_351(ItemEntry.method_411(Items.GRAY_WOOL))
						.method_351(ItemEntry.method_411(Items.LIGHT_GRAY_WOOL))
						.method_351(ItemEntry.method_411(Items.CYAN_WOOL))
						.method_351(ItemEntry.method_411(Items.PURPLE_WOOL))
						.method_351(ItemEntry.method_411(Items.BLUE_WOOL))
						.method_351(ItemEntry.method_411(Items.BROWN_WOOL))
						.method_351(ItemEntry.method_411(Items.GREEN_WOOL))
						.method_351(ItemEntry.method_411(Items.RED_WOOL))
						.method_351(ItemEntry.method_411(Items.BLACK_WOOL))
				)
		);
		biConsumer.accept(
			LootTables.field_19073,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8387))
						.method_351(ItemEntry.method_411(Items.field_8062))
						.method_351(ItemEntry.method_411(Items.field_8431))
						.method_351(ItemEntry.method_411(Items.field_8776))
				)
		);
		biConsumer.accept(
			LootTables.field_19074,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.method_352(ConstantLootTableRange.create(1))
						.method_351(ItemEntry.method_411(Items.field_8062))
						.method_351(ItemEntry.method_411(Items.field_8825))
						.method_351(ItemEntry.method_411(Items.field_8475))
				)
		);
	}
}
