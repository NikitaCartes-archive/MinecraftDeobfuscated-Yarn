package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;

public class FishingLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public static final LootCondition.Builder NEEDS_JUNGLE_BIOME = LocationCheckLootCondition.builder(LocationPredicate.Builder.create().biome(Biomes.field_9417));
	public static final LootCondition.Builder NEEDS_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9432)
	);
	public static final LootCondition.Builder NEEDS_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9474)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9440)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9426)
	);
	public static final LootCondition.Builder NEEDS_MODIFIED_JUNGLE_EDGE_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9405)
	);
	public static final LootCondition.Builder NEEDS_BAMBOO_JUNGLE_HILLS_BIOME = LocationCheckLootCondition.builder(
		LocationPredicate.Builder.create().biome(Biomes.field_9468)
	);

	public void method_10405(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_353,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(LootTableEntry.builder(LootTables.field_266).weight(10).quality(-2))
						.with(
							LootTableEntry.builder(LootTables.field_854)
								.weight(5)
								.quality(2)
								.method_421(
									EntityPropertiesLootCondition.builder(LootContext.EntityTarget.field_935, EntityPredicate.Builder.create().fishHook(FishingHookPredicate.of(true)))
								)
						)
						.with(LootTableEntry.builder(LootTables.field_795).weight(85).quality(-1))
				)
		);
		biConsumer.accept(
			LootTables.field_795,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(ItemEntry.builder(Items.field_8429).weight(60))
						.with(ItemEntry.builder(Items.field_8209).weight(25))
						.with(ItemEntry.builder(Items.field_8846).weight(2))
						.with(ItemEntry.builder(Items.field_8323).weight(13))
				)
		);
		biConsumer.accept(
			LootTables.field_266,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(ItemEntry.builder(Blocks.field_10588).weight(17))
						.with(ItemEntry.builder(Items.field_8370).weight(10).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.with(ItemEntry.builder(Items.field_8745).weight(10))
						.with(ItemEntry.builder(Items.field_8606).weight(10))
						.with(
							ItemEntry.builder(Items.field_8574)
								.weight(10)
								.method_438(SetNbtLootFunction.builder(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:water"))))
						)
						.with(ItemEntry.builder(Items.field_8276).weight(5))
						.with(ItemEntry.builder(Items.field_8378).weight(2).method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.9F))))
						.with(ItemEntry.builder(Items.field_8428).weight(10))
						.with(ItemEntry.builder(Items.field_8600).weight(5))
						.with(ItemEntry.builder(Items.field_8794).weight(1).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(10))))
						.with(ItemEntry.builder(Blocks.field_10348).weight(10))
						.with(ItemEntry.builder(Items.field_8511).weight(10))
						.with(
							ItemEntry.builder(Blocks.field_10211)
								.method_421(
									NEEDS_JUNGLE_BIOME.or(NEEDS_JUNGLE_HILLS_BIOME)
										.or(NEEDS_JUNGLE_EDGE_BIOME)
										.or(NEEDS_BAMBOO_JUNGLE_BIOME)
										.or(NEEDS_MODIFIED_JUNGLE_BIOME)
										.or(NEEDS_MODIFIED_JUNGLE_EDGE_BIOME)
										.or(NEEDS_BAMBOO_JUNGLE_HILLS_BIOME)
								)
								.weight(10)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_854,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(ItemEntry.builder(Items.field_8448))
						.with(ItemEntry.builder(Items.field_8175))
						.with(
							ItemEntry.builder(Items.field_8102)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8378)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.0F, 0.25F)))
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
						.with(ItemEntry.builder(Items.field_8864))
				)
		);
	}
}
