package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.StructureFeature;

public class ChestLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.ABANDONED_MINESHAFT_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(30))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(EmptyEntry.Serializer().weight(5))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Blocks.RAIL).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.POWERED_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.DETECTOR_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.ACTIVATOR_RAIL).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.TORCH).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F))))
				)
		);
		biConsumer.accept(
			LootTables.BASTION_BRIDGE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Blocks.LODESTONE).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 2.0F))
						.with(
							ItemEntry.builder(Items.CROSSBOW)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.5F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.SPECTRAL_ARROW).apply(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 28.0F))))
						.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.GOLD_BLOCK).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.GOLDEN_CHESTPLATE)
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_HELMET)
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_LEGGINGS)
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_BOOTS).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))).apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_AXE).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))).apply(EnchantRandomlyLootFunction.builder())
						)
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.BASTION_HOGLIN_STABLE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.DIAMOND_SHOVEL)
								.weight(15)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.15F, 0.8F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.weight(12)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.15F, 0.95F)))
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(8).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(5).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
						.with(ItemEntry.builder(Items.SADDLE).weight(12).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.GOLD_BLOCK).weight(16).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 17.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(10).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(
							ItemEntry.builder(Items.GOLDEN_AXE).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))).apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.GLOWSTONE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 6.0F))))
						.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.SOUL_SAND).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.CRIMSON_NYLIUM).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.ARROW).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.PORKCHOP).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.COOKED_PORKCHOP).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.CRIMSON_FUNGUS).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.CRIMSON_ROOTS).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.BASTION_OTHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.weight(6)
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.DIAMOND_SHOVEL).weight(6).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.CROSSBOW)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.9F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(4).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.SPECTRAL_ARROW).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 22.0F))))
						.with(ItemEntry.builder(Items.PIGLIN_BANNER_PATTERN).weight(9).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.MUSIC_DISC_PIGSTEP).weight(5).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(12).apply(SetCountLootFunction.builder(UniformLootTableRange.between(6.0F, 17.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(9).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED)))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(2))
						.with(
							ItemEntry.builder(Items.IRON_SWORD)
								.weight(2)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.9F)))
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.IRON_BLOCK).weight(2).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.GOLDEN_BOOTS)
								.apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.apply(new EnchantRandomlyLootFunction.Builder().add(Enchantments.SOUL_SPEED))
						)
						.with(
							ItemEntry.builder(Items.GOLDEN_AXE).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))).apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.GOLD_BLOCK).weight(2).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.CROSSBOW).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.GOLDEN_BOOTS).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.CHAIN).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 10.0F))))
						.with(ItemEntry.builder(Items.MAGMA_CREAM).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Blocks.BONE_BLOCK).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 6.0F))))
						.with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.ARROW).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.COOKED_PORKCHOP).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		biConsumer.accept(
			LootTables.BASTION_TREASURE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.NETHERITE_INGOT).weight(15).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.ANCIENT_DEBRIS).weight(10).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.NETHERITE_SCRAP).weight(8).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.ANCIENT_DEBRIS).weight(4).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
						.with(
							ItemEntry.builder(Items.DIAMOND_SWORD)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_HELMET)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_LEGGINGS)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_BOOTS)
								.weight(6)
								.apply(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.apply(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.DIAMOND_SWORD).weight(6))
						.with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_HELMET).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_BOOTS).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_LEGGINGS).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2).apply(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(ItemEntry.builder(Items.SPECTRAL_ARROW).apply(SetCountLootFunction.builder(UniformLootTableRange.between(12.0F, 25.0F))))
						.with(ItemEntry.builder(Blocks.GOLD_BLOCK).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.IRON_BLOCK).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Blocks.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.with(ItemEntry.builder(Items.QUARTZ).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 23.0F))))
						.with(ItemEntry.builder(Blocks.GILDED_BLACKSTONE).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 15.0F))))
						.with(ItemEntry.builder(Items.MAGMA_CREAM).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.BURIED_TREASURE_CHEST,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.HEART_OF_THE_SEA)))
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(5.0F, 8.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.TNT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.PRISMARINE_CRYSTALS).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(0.0F, 1.0F))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.IRON_SWORD))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(2))
						.with(ItemEntry.builder(Items.COOKED_COD).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.COOKED_SALMON).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.DESERT_PYRAMID_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BONE).weight(25).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.SPIDER_EYE).weight(25).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(25).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(20))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.BOOK).weight(20).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(20))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
						.with(EmptyEntry.Serializer().weight(15))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(4))
						.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.SAND).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.END_CITY_TREASURE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 6.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(3))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
						.with(
							ItemEntry.builder(Items.DIAMOND_SWORD)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_BOOTS)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_CHESTPLATE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_LEGGINGS)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_HELMET)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_PICKAXE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.DIAMOND_SHOVEL)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_SWORD)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_BOOTS)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_CHESTPLATE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_LEGGINGS)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_HELMET)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_PICKAXE)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.IRON_SHOVEL)
								.weight(3)
								.apply(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.IGLOO_CHEST_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STONE_AXE).weight(2))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.GOLDEN_APPLE)))
		);
		biConsumer.accept(
			LootTables.JUNGLE_TEMPLE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 6.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.BAMBOO).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BONE).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(16).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(3))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 2.0F))
						.with(ItemEntry.builder(Items.ARROW).weight(30).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.NETHER_BRIDGE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(5))
						.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(5))
						.with(ItemEntry.builder(Items.FLINT_AND_STEEL).weight(5))
						.with(ItemEntry.builder(Items.NETHER_WART).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(10))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(8))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(3))
						.with(ItemEntry.builder(Blocks.OBSIDIAN).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.PILLAGER_OUTPOST_CHEST,
			LootTable.builder()
				.pool(LootPool.builder().rolls(UniformLootTableRange.between(0.0F, 1.0F)).with(ItemEntry.builder(Items.CROSSBOW)))
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.WHEAT).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.with(ItemEntry.builder(Items.POTATO).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.CARROT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Blocks.DARK_OAK_LOG).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(7))
						.with(ItemEntry.builder(Items.STRING).weight(4).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.ARROW).weight(4).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.TRIPWIRE_HOOK).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(1).apply(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_MAP_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.MAP)
								.apply(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.BURIED_TREASURE)
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.COMPASS))
						.with(ItemEntry.builder(Items.MAP))
						.with(ItemEntry.builder(Items.CLOCK))
						.with(ItemEntry.builder(Items.PAPER).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.FEATHER).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_SUPPLY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 10.0F))
						.with(ItemEntry.builder(Items.PAPER).weight(8).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Items.POTATO).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.POISONOUS_POTATO).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.CARROT).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.WHEAT).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 21.0F))))
						.with(
							ItemEntry.builder(Items.SUSPICIOUS_STEW)
								.weight(10)
								.apply(
									SetStewEffectLootFunction.builder()
										.withEffect(StatusEffects.NIGHT_VISION, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.JUMP_BOOST, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.WEAKNESS, UniformLootTableRange.between(6.0F, 8.0F))
										.withEffect(StatusEffects.BLINDNESS, UniformLootTableRange.between(5.0F, 7.0F))
										.withEffect(StatusEffects.POISON, UniformLootTableRange.between(10.0F, 20.0F))
										.withEffect(StatusEffects.SATURATION, UniformLootTableRange.between(7.0F, 10.0F))
								)
						)
						.with(ItemEntry.builder(Items.COAL).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 24.0F))))
						.with(ItemEntry.builder(Blocks.PUMPKIN).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.BAMBOO).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GUNPOWDER).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.TNT).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.LEATHER_HELMET).weight(3).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(3).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(3).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(3).apply(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.SHIPWRECK_TREASURE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 6.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(90).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.DIAMOND).weight(5))
						.with(ItemEntry.builder(Items.EXPERIENCE_BOTTLE).weight(5))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 5.0F))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(50).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SIMPLE_DUNGEON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.SADDLE).weight(20))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(15))
						.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(15))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(20))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(10))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR).weight(15))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(20))
						.with(ItemEntry.builder(Items.WHEAT).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BUCKET).weight(10))
						.with(ItemEntry.builder(Items.REDSTONE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SPAWN_BONUS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.STONE_AXE)).with(ItemEntry.builder(Items.WOODEN_AXE).weight(3))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.STONE_PICKAXE))
						.with(ItemEntry.builder(Items.WOODEN_PICKAXE).weight(3))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.APPLE).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.SALMON).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(4))
						.with(ItemEntry.builder(Items.STICK).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.OAK_PLANKS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.OAK_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.SPRUCE_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.BIRCH_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.JUNGLE_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.ACACIA_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.DARK_OAK_LOG).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_CORRIDOR_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.ENDER_PEARL).weight(10))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
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
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_CROSSING_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.REDSTONE).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE))
						.with(ItemEntry.builder(Items.BOOK).apply(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments()))
				)
		);
		biConsumer.accept(
			LootTables.STRONGHOLD_LIBRARY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 10.0F))
						.with(ItemEntry.builder(Items.BOOK).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.PAPER).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.MAP))
						.with(ItemEntry.builder(Items.COMPASS))
						.with(
							ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.UNDERWATER_RUIN_BIG_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE))
						.with(ItemEntry.builder(Items.BOOK).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(10)
								.apply(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.BURIED_TREASURE)
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.UNDERWATER_RUIN_SMALL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.COAL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STONE_AXE).weight(2))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(5))
						.with(ItemEntry.builder(Items.EMERALD))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET))
						.with(ItemEntry.builder(Items.FISHING_ROD).weight(5).apply(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.MAP)
								.weight(5)
								.apply(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.BURIED_TREASURE)
										.withDecoration(MapIcon.Type.RED_X)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_WEAPONSMITH_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(ItemEntry.builder(Items.IRON_SWORD).weight(5))
						.with(ItemEntry.builder(Items.IRON_CHESTPLATE).weight(5))
						.with(ItemEntry.builder(Items.IRON_HELMET).weight(5))
						.with(ItemEntry.builder(Items.IRON_LEGGINGS).weight(5))
						.with(ItemEntry.builder(Items.IRON_BOOTS).weight(5))
						.with(ItemEntry.builder(Blocks.OBSIDIAN).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.OAK_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(3))
						.with(ItemEntry.builder(Items.IRON_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR))
						.with(ItemEntry.builder(Items.DIAMOND_HORSE_ARMOR))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TOOLSMITH_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.DIAMOND).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_PICKAXE).weight(5))
						.with(ItemEntry.builder(Items.COAL).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.IRON_SHOVEL).weight(5))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_CARTOGRAPHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.MAP).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.PAPER).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.COMPASS).weight(5))
						.with(ItemEntry.builder(Items.BREAD).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_MASON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.CLAY_BALL).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FLOWER_POT).weight(1))
						.with(ItemEntry.builder(Blocks.STONE).weight(2))
						.with(ItemEntry.builder(Blocks.STONE_BRICKS).weight(2))
						.with(ItemEntry.builder(Items.BREAD).weight(4).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.YELLOW_DYE).weight(1))
						.with(ItemEntry.builder(Blocks.SMOOTH_STONE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_ARMORER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(4).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.IRON_HELMET).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SHEPARD_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Blocks.WHITE_WOOL).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.BLACK_WOOL).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.GRAY_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.BROWN_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.LIGHT_GRAY_WOOL).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.SHEARS).weight(1))
						.with(ItemEntry.builder(Items.WHEAT).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_BUTCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.PORKCHOP).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WHEAT).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BEEF).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.MUTTON).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FLETCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.ARROW).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FEATHER).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EGG).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FLINT).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.STICK).weight(6).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FISHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.EMERALD).weight(1))
						.with(ItemEntry.builder(Items.COD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.SALMON).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WATER_BUCKET).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BARREL).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.WHEAT_SEEDS).weight(3).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TANNERY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.LEATHER).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.LEATHER_CHESTPLATE).weight(2))
						.with(ItemEntry.builder(Items.LEATHER_BOOTS).weight(2))
						.with(ItemEntry.builder(Items.LEATHER_HELMET).weight(2))
						.with(ItemEntry.builder(Items.BREAD).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.LEATHER_LEGGINGS).weight(2))
						.with(ItemEntry.builder(Items.SADDLE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TEMPLE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.REDSTONE).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(7).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.LAPIS_LAZULI).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_PLAINS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.DANDELION).weight(2))
						.with(ItemEntry.builder(Items.POPPY).weight(1))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.APPLE).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(1))
						.with(ItemEntry.builder(Items.FEATHER).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.OAK_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.FERN).weight(2))
						.with(ItemEntry.builder(Items.LARGE_FERN).weight(2))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.SWEET_BERRIES).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_PIE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.SPRUCE_SAPLING).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.SPRUCE_SIGN).weight(1))
						.with(ItemEntry.builder(Items.SPRUCE_LOG).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GRASS).weight(5))
						.with(ItemEntry.builder(Items.TALL_GRASS).weight(5))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.WHEAT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.ACACIA_SAPLING).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.SADDLE).weight(1))
						.with(ItemEntry.builder(Blocks.TORCH).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.BUCKET).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Blocks.BLUE_ICE).weight(1))
						.with(ItemEntry.builder(Blocks.SNOW_BLOCK).weight(4))
						.with(ItemEntry.builder(Items.POTATO).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SOUP).weight(1))
						.with(ItemEntry.builder(Items.FURNACE).weight(1))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.SNOWBALL).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_DESERT_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.CLAY_BALL).weight(1))
						.with(ItemEntry.builder(Items.GREEN_DYE).weight(1))
						.with(ItemEntry.builder(Blocks.CACTUS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.WHEAT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BOOK).weight(1))
						.with(ItemEntry.builder(Blocks.DEAD_BUSH).weight(2).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.EMERALD).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.WOODLAND_MANSION_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.LEAD).weight(20))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(2))
						.with(ItemEntry.builder(Items.MUSIC_DISC_13).weight(15))
						.with(ItemEntry.builder(Items.MUSIC_DISC_CAT).weight(15))
						.with(ItemEntry.builder(Items.NAME_TAG).weight(20))
						.with(ItemEntry.builder(Items.CHAINMAIL_CHESTPLATE).weight(10))
						.with(ItemEntry.builder(Items.DIAMOND_HOE).weight(15))
						.with(ItemEntry.builder(Items.DIAMOND_CHESTPLATE).weight(5))
						.with(ItemEntry.builder(Items.BOOK).weight(10).apply(EnchantRandomlyLootFunction.builder()))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.IRON_INGOT).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BREAD).weight(20))
						.with(ItemEntry.builder(Items.WHEAT).weight(20).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BUCKET).weight(10))
						.with(ItemEntry.builder(Items.REDSTONE).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.COAL).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.MELON_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.PUMPKIN_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.BEETROOT_SEEDS).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.BONE).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.GUNPOWDER).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.ROTTEN_FLESH).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.STRING).weight(10).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.RUINED_PORTAL_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(4.0F, 8.0F))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.FLINT).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.IRON_NUGGET).weight(40).apply(SetCountLootFunction.builder(UniformLootTableRange.between(9.0F, 18.0F))))
						.with(ItemEntry.builder(Items.FLINT_AND_STEEL).weight(40))
						.with(ItemEntry.builder(Items.FIRE_CHARGE).weight(40))
						.with(ItemEntry.builder(Items.GOLDEN_APPLE).weight(15))
						.with(ItemEntry.builder(Items.GOLD_NUGGET).weight(15).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 24.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_SWORD).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_AXE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_HOE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_SHOVEL).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_PICKAXE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_BOOTS).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_CHESTPLATE).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_HELMET).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GOLDEN_LEGGINGS).weight(15).apply(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.GLISTERING_MELON_SLICE).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.GOLDEN_HORSE_ARMOR).weight(5))
						.with(ItemEntry.builder(Items.LIGHT_WEIGHTED_PRESSURE_PLATE).weight(5))
						.with(ItemEntry.builder(Items.GOLDEN_CARROT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.CLOCK).weight(5))
						.with(ItemEntry.builder(Items.GOLD_INGOT).weight(5).apply(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BELL).weight(1))
						.with(ItemEntry.builder(Items.ENCHANTED_GOLDEN_APPLE).weight(1))
						.with(ItemEntry.builder(Items.GOLD_BLOCK).weight(1).apply(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
	}
}
