package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.LocationCheckLootCondition;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LootTableEntry;
import net.minecraft.world.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetDamageLootFunction;
import net.minecraft.world.loot.function.SetNbtLootFunction;

public class FishingLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public static final LootCondition.Builder field_11346 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9417));
	public static final LootCondition.Builder field_11347 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9432));
	public static final LootCondition.Builder field_11350 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9474));
	public static final LootCondition.Builder field_11349 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9440));
	public static final LootCondition.Builder field_11348 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9426));
	public static final LootCondition.Builder field_11351 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9405));
	public static final LootCondition.Builder field_11352 = LocationCheckLootCondition.builder(new LocationPredicate.Builder().biome(Biomes.field_9468));

	public void method_10405(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_353,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(LootTableEntry.builder(LootTables.field_266).setWeight(10).setQuality(-2))
						.withEntry(LootTableEntry.builder(LootTables.field_854).setWeight(5).setQuality(2))
						.withEntry(LootTableEntry.builder(LootTables.field_795).setWeight(85).setQuality(-1))
				)
		);
		biConsumer.accept(
			LootTables.field_795,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.field_8429).setWeight(60))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(25))
						.withEntry(ItemEntry.builder(Items.field_8846).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8323).setWeight(13))
				)
		);
		biConsumer.accept(
			LootTables.field_266,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(10).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.field_8745).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10))
						.withEntry(
							ItemEntry.builder(Items.field_8574)
								.setWeight(10)
								.method_438(SetNbtLootFunction.builder(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(2).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.withEntry(ItemEntry.builder(Items.field_8428).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8794).setWeight(1).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(10))))
						.withEntry(ItemEntry.builder(Blocks.field_10348).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(
							ItemEntry.builder(Blocks.field_10211)
								.method_421(
									field_11346.withCondition(field_11347)
										.withCondition(field_11350)
										.withCondition(field_11349)
										.withCondition(field_11348)
										.withCondition(field_11351)
										.withCondition(field_11352)
								)
								.setWeight(10)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_854,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withEntry(ItemEntry.builder(Blocks.field_10588))
						.withEntry(ItemEntry.builder(Items.field_8448))
						.withEntry(ItemEntry.builder(Items.field_8175))
						.withEntry(
							ItemEntry.builder(Items.field_8102)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8378)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.withEntry(ItemEntry.builder(Items.field_8864))
				)
		);
	}
}
