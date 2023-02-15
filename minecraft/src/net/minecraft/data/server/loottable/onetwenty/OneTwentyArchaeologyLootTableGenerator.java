package net.minecraft.data.server.loottable.onetwenty;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.OneTwentyLootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class OneTwentyArchaeologyLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			OneTwentyLootTables.DESERT_WELL_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.POTTERY_SHARD_ARMS_UP).weight(3))
						.with(ItemEntry.builder(Items.BRICK))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.STICK))
						.with(
							ItemEntry.builder(Items.SUSPICIOUS_STEW)
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
				)
		);
		exporter.accept(
			OneTwentyLootTables.DESERT_PYRAMID_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.POTTERY_SHARD_ARCHER))
						.with(ItemEntry.builder(Items.POTTERY_SHARD_PRIZE))
						.with(ItemEntry.builder(Items.POTTERY_SHARD_SKULL))
						.with(ItemEntry.builder(Items.GUNPOWDER))
						.with(ItemEntry.builder(Items.TNT))
						.with(ItemEntry.builder(Items.DIAMOND))
						.with(ItemEntry.builder(Items.EMERALD))
				)
		);
	}
}
