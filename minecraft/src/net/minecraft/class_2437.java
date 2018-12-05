package net.minecraft;

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
import net.minecraft.world.loot.function.SetTagLootFunction;

public class class_2437 implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public static final LootCondition.Builder field_11346 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9417));
	public static final LootCondition.Builder field_11347 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9432));
	public static final LootCondition.Builder field_11350 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9474));
	public static final LootCondition.Builder field_11349 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9440));
	public static final LootCondition.Builder field_11348 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9426));
	public static final LootCondition.Builder field_11351 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9405));
	public static final LootCondition.Builder field_11352 = LocationCheckLootCondition.method_884(new LocationPredicate.Builder().biome(Biomes.field_9468));

	public void method_10405(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.GAMEPLAY_FISHING,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.method_351(LootTableEntry.method_428(LootTables.field_266).setWeight(10).setQuality(-2))
						.method_351(LootTableEntry.method_428(LootTables.field_854).setWeight(5).setQuality(2))
						.method_351(LootTableEntry.method_428(LootTables.field_795).setWeight(85).setQuality(-1))
				)
		);
		biConsumer.accept(
			LootTables.field_795,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_351(ItemEntry.method_411(Items.field_8429).setWeight(60))
						.method_351(ItemEntry.method_411(Items.field_8209).setWeight(25))
						.method_351(ItemEntry.method_411(Items.field_8846).setWeight(2))
						.method_351(ItemEntry.method_411(Items.field_8323).setWeight(13))
				)
		);
		biConsumer.accept(
			LootTables.field_266,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_351(
							ItemEntry.method_411(Items.field_8370).setWeight(10).withFunction(SetDamageLootFunction.method_633(UniformLootTableRange.method_377(0.0F, 0.9F)))
						)
						.method_351(ItemEntry.method_411(Items.field_8745).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8606).setWeight(10))
						.method_351(
							ItemEntry.method_411(Items.field_8574)
								.setWeight(10)
								.withFunction(SetTagLootFunction.method_677(SystemUtil.consume(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.method_351(ItemEntry.method_411(Items.field_8276).setWeight(5))
						.method_351(
							ItemEntry.method_411(Items.field_8378).setWeight(2).withFunction(SetDamageLootFunction.method_633(UniformLootTableRange.method_377(0.0F, 0.9F)))
						)
						.method_351(ItemEntry.method_411(Items.field_8428).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8600).setWeight(5))
						.method_351(ItemEntry.method_411(Items.field_8794).setWeight(1).withFunction(SetCountLootFunction.method_621(ConstantLootTableRange.create(10))))
						.method_351(ItemEntry.method_411(Blocks.field_10348).setWeight(10))
						.method_351(ItemEntry.method_411(Items.field_8511).setWeight(10))
						.method_351(
							ItemEntry.method_411(Blocks.field_10211)
								.withCondition(field_11346.or(field_11347).or(field_11350).or(field_11349).or(field_11348).or(field_11351).or(field_11352))
								.setWeight(10)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_854,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.method_351(ItemEntry.method_411(Blocks.field_10588))
						.method_351(ItemEntry.method_411(Items.field_8448))
						.method_351(ItemEntry.method_411(Items.field_8175))
						.method_351(
							ItemEntry.method_411(Items.field_8102)
								.withFunction(SetDamageLootFunction.method_633(UniformLootTableRange.method_377(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484())
						)
						.method_351(
							ItemEntry.method_411(Items.field_8378)
								.withFunction(SetDamageLootFunction.method_633(UniformLootTableRange.method_377(0.0F, 0.25F)))
								.withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484())
						)
						.method_351(ItemEntry.method_411(Items.field_8529).withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484()))
						.method_351(ItemEntry.method_411(Items.field_8864))
				)
		);
	}
}
