package net.minecraft.data.server.loottable.vanilla;

import java.util.function.BiConsumer;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class VanillaArchaeologyLootTableGenerator implements LootTableGenerator {
	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
		exporter.accept(
			LootTables.DESERT_WELL_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARMS_UP_POTTERY_SHERD).weight(2))
						.with(ItemEntry.builder(Items.BREWER_POTTERY_SHERD).weight(2))
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
			LootTables.DESERT_PYRAMID_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ARCHER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.MINER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.PRIZE_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.SKULL_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.DIAMOND))
						.with(ItemEntry.builder(Items.TNT))
						.with(ItemEntry.builder(Items.GUNPOWDER))
						.with(ItemEntry.builder(Items.EMERALD))
				)
		);
		exporter.accept(
			LootTables.TRAIL_RUINS_COMMON_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(2))
						.with(ItemEntry.builder(Items.WHEAT).weight(2))
						.with(ItemEntry.builder(Items.WOODEN_HOE).weight(2))
						.with(ItemEntry.builder(Items.CLAY).weight(2))
						.with(ItemEntry.builder(Items.BRICK).weight(2))
						.with(ItemEntry.builder(Items.YELLOW_DYE).weight(2))
						.with(ItemEntry.builder(Items.BLUE_DYE).weight(2))
						.with(ItemEntry.builder(Items.LIGHT_BLUE_DYE).weight(2))
						.with(ItemEntry.builder(Items.WHITE_DYE).weight(2))
						.with(ItemEntry.builder(Items.ORANGE_DYE).weight(2))
						.with(ItemEntry.builder(Items.RED_CANDLE).weight(2))
						.with(ItemEntry.builder(Items.GREEN_CANDLE).weight(2))
						.with(ItemEntry.builder(Items.PURPLE_CANDLE).weight(2))
						.with(ItemEntry.builder(Items.BROWN_CANDLE).weight(2))
						.with(ItemEntry.builder(Items.MAGENTA_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.PINK_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.BLUE_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.LIGHT_BLUE_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.RED_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.YELLOW_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.PURPLE_STAINED_GLASS_PANE))
						.with(ItemEntry.builder(Items.SPRUCE_HANGING_SIGN))
						.with(ItemEntry.builder(Items.OAK_HANGING_SIGN))
						.with(ItemEntry.builder(Items.GOLD_NUGGET))
						.with(ItemEntry.builder(Items.COAL))
						.with(ItemEntry.builder(Items.WHEAT_SEEDS))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS))
						.with(ItemEntry.builder(Items.DEAD_BUSH))
						.with(ItemEntry.builder(Items.FLOWER_POT))
						.with(ItemEntry.builder(Items.STRING))
						.with(ItemEntry.builder(Items.LEAD))
				)
		);
		exporter.accept(
			LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BURN_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.DANGER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.FRIEND_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.HEART_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.HEARTBREAK_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.HOWL_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.SHEAF_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
						.with(ItemEntry.builder(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE))
						.with(ItemEntry.builder(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE))
						.with(ItemEntry.builder(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
						.with(ItemEntry.builder(Items.MUSIC_DISC_RELIC))
				)
		);
		exporter.accept(
			LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.ANGLER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.SHELTER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.SNORT_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.SNIFFER_EGG))
						.with(ItemEntry.builder(Items.IRON_AXE))
						.with(ItemEntry.builder(Items.EMERALD).weight(2))
						.with(ItemEntry.builder(Items.WHEAT).weight(2))
						.with(ItemEntry.builder(Items.WOODEN_HOE).weight(2))
						.with(ItemEntry.builder(Items.COAL).weight(2))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(2))
				)
		);
		exporter.accept(
			LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(Items.BLADE_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.EXPLORER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.MOURNER_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.PLENTY_POTTERY_SHERD))
						.with(ItemEntry.builder(Items.IRON_AXE))
						.with(ItemEntry.builder(Items.EMERALD).weight(2))
						.with(ItemEntry.builder(Items.WHEAT).weight(2))
						.with(ItemEntry.builder(Items.WOODEN_HOE).weight(2))
						.with(ItemEntry.builder(Items.COAL).weight(2))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(2))
				)
		);
	}
}
