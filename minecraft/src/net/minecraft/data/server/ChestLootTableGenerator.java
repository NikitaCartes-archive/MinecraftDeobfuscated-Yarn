package net.minecraft.data.server;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.Identifier;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.entry.EmptyEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.world.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.world.loot.function.ExplorationMapLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;
import net.minecraft.world.loot.function.SetStewEffectLootFunction;

public class ChestLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	public void method_10399(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_472,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8367))
						.withEntry(ItemEntry.builder(Items.field_8448).setWeight(30))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(EmptyEntry.Serializer().setWeight(5))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Blocks.field_10167).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10425).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10025).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10546).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10336).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_251,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8207)))
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(5.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10375).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(0.0F, 1.0F))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8371))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(2))
						.withEntry(ItemEntry.builder(Items.field_8373).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8509).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_885,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8680).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8578).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8560).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8807).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(20).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8367).setWeight(2))
						.withEntry(EmptyEntry.Serializer().setWeight(15))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10102).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_274,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
						.withEntry(
							ItemEntry.builder(Items.field_8802)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8285)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8058)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8348)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8805)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8377)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8250)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8371)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8660)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8523)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8396)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8743)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8403)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8699)
								.setWeight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_662,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8062).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8463)))
		);
		biConsumer.accept(
			LootTables.field_803,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10211).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(16).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
						.withEntry(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_751,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 2.0F))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(30).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_615,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8845).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8678).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8884).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8790).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8560).setWeight(8))
						.withEntry(ItemEntry.builder(Items.field_8578).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8807).setWeight(3))
						.withEntry(ItemEntry.builder(Blocks.field_10540).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16593,
			LootSupplier.builder()
				.withPool(LootPool.builder().withRolls(UniformLootTableRange.between(0.0F, 1.0F)).withEntry(ItemEntry.builder(Items.field_8399)))
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8179).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10010).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8287).setWeight(7))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.TRIPWIRE_HOOK).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.field_841,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.field_110)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8251))
						.withEntry(ItemEntry.builder(Items.field_8895))
						.withEntry(ItemEntry.builder(Items.field_8557))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_880,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(8).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8635).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8179).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 21.0F))))
						.withEntry(
							ItemEntry.builder(Items.field_8766)
								.setWeight(10)
								.method_438(
									SetStewEffectLootFunction.builder()
										.withEffect(StatusEffects.field_5925, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.field_5913, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.field_5911, UniformLootTableRange.between(6.0F, 8.0F))
										.withEffect(StatusEffects.field_5919, UniformLootTableRange.between(5.0F, 7.0F))
										.withEffect(StatusEffects.field_5899, UniformLootTableRange.between(10.0F, 20.0F))
										.withEffect(StatusEffects.field_5922, UniformLootTableRange.between(7.0F, 10.0F))
								)
						)
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 24.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10261).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10211).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10375).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8267).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8577).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8570).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(3).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.field_665,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(90).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8287).setWeight(5))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8675).setWeight(50).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_356,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8367).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8144).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8075).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8448).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8560).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8578).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8807).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SPAWN_BONUS_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8062))
						.withEntry(ItemEntry.builder(Items.field_8406).setWeight(3))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8387))
						.withEntry(ItemEntry.builder(Items.field_8647).setWeight(3))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10161).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10431).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10037).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10511).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10306).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10533).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10010).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_842,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8634).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8371).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8523).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8743).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8396).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8660).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8463))
						.withEntry(ItemEntry.builder(Items.field_8175))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
						.withEntry(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_800,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403))
						.withEntry(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_683,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8895))
						.withEntry(ItemEntry.builder(Items.field_8251))
						.withEntry(
							ItemEntry.builder(Items.field_8529)
								.setWeight(10)
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_300,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8463))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8862))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.setWeight(10)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.field_110)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_397,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8062).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8862))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.setWeight(5)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination("buried_treasure")
										.withDecoration(MapIcon.Type.field_110)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_WEAPONSMITH_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8371).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8523).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8743).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8396).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8660).setWeight(5))
						.withEntry(ItemEntry.builder(Blocks.field_10540).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10394).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TOOLSMITH_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8699).setWeight(5))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_CARTOGRAPHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8895).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8251).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_MASON_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8696).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.FLOWER_POT).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10340).setWeight(2))
						.withEntry(ItemEntry.builder(Blocks.field_10056).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8192).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10360).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_ARMORER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8743).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SHEPARD_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10446).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10146).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10423).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10113).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10222).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8868).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_BUTCHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8389).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8046).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8748).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FLETCHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8803).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8145).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FISHER_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8429).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8705).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.BARREL).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8317).setWeight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TANNERY_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8745).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8577).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8267).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8570).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TEMPLE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_PLAINS_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.DANDELION).setWeight(2))
						.withEntry(ItemEntry.builder(Items.POPPY).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10394).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8675).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.FERN).setWeight(2))
						.withEntry(ItemEntry.builder(Items.LARGE_FERN).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_16998).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8741).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10217).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8111).setWeight(1))
						.withEntry(ItemEntry.builder(Items.SPRUCE_LOG).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.GRASS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.TALL_GRASS).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8317).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10385).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10336).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10384).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10491).setWeight(4))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8515).setWeight(1))
						.withEntry(ItemEntry.builder(Items.FURNACE).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8543).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_DESERT_HOUSE_CHEST,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8696).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8408).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10029).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10428).setWeight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_484,
			LootSupplier.builder()
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8719).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8367).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8144).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8075).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8448).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8873).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8527).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8058).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.withPool(
					LootPool.builder()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
	}
}
