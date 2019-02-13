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
	public void accept(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.CHEST_ABANDONED_MINESHAFT,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8367))
						.withEntry(ItemEntry.builder(Items.field_8448).setWeight(30))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(EmptyEntry.Serializer().setWeight(5))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Blocks.field_10167).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10425).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10025).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10546).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10336).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_251,
			LootSupplier.create()
				.withPool(LootPool.create().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8207)))
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(5.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10375).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8434).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(0.0F, 1.0F))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8371))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(2))
						.withEntry(ItemEntry.builder(Items.field_8373).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8509).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_DESERT_PYRAMID,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(25).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8680).setWeight(25).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(25).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8578).setWeight(15))
						.withEntry(ItemEntry.builder(Items.field_8560).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8807).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(20).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8463).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8367).setWeight(2))
						.withEntry(EmptyEntry.Serializer().setWeight(15))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10102).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_END_CITY_TREASURE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
						.withEntry(
							ItemEntry.builder(Items.field_8802)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8285)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8058)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8348)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8805)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8377)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8250)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8371)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8660)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8523)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8396)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8743)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8403)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
						.withEntry(
							ItemEntry.builder(Items.field_8699)
								.setWeight(3)
								.withFunction(EnchantWithLevelsLootFunction.method_481(UniformLootTableRange.between(20.0F, 39.0F)).method_484())
						)
				)
		);
		biConsumer.accept(
			LootTables.CHEST_IGLOO,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8062).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(LootPool.create().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Items.field_8463)))
		);
		biConsumer.accept(
			LootTables.CHEST_JUNGLE_TEMPLE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10211).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(16).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
						.withEntry(ItemEntry.builder(Items.field_8529).withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484()))
				)
		);
		biConsumer.accept(
			LootTables.DISPENSER_JUNGLE_TEMPLE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 2.0F))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(30).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_NETHER_BRIDGE,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8845).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8678).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8884).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8790).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8560).setWeight(8))
						.withEntry(ItemEntry.builder(Items.field_8578).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8807).setWeight(3))
						.withEntry(ItemEntry.builder(Blocks.field_10540).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16593,
			LootSupplier.create()
				.withPool(LootPool.create().withRolls(UniformLootTableRange.between(0.0F, 1.0F)).withEntry(ItemEntry.builder(Items.field_8399)))
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8179).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 3.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10010).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8287).setWeight(7))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(4).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(4).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8366).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1).withFunction(EnchantRandomlyLootFunction.method_489()))
				)
		);
		biConsumer.accept(
			LootTables.field_841,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.withFunction(
									ExplorationMapLootFunction.create().destination("buried_treasure").decoration(MapIcon.Type.field_110).zoom((byte)1).skipExistingChunks(false)
								)
						)
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8251))
						.withEntry(ItemEntry.builder(Items.field_8895))
						.withEntry(ItemEntry.builder(Items.field_8557))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_880,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(8).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8635).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.withEntry(ItemEntry.builder(Items.field_8179).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 21.0F))))
						.withEntry(
							ItemEntry.builder(Items.field_8766)
								.setWeight(10)
								.withFunction(
									SetStewEffectLootFunction.create()
										.withEffect(StatusEffects.field_5904, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.field_5913, UniformLootTableRange.between(7.0F, 10.0F))
										.withEffect(StatusEffects.field_5911, UniformLootTableRange.between(6.0F, 8.0F))
										.withEffect(StatusEffects.field_5919, UniformLootTableRange.between(5.0F, 7.0F))
										.withEffect(StatusEffects.field_5899, UniformLootTableRange.between(10.0F, 20.0F))
										.withEffect(StatusEffects.field_5922, UniformLootTableRange.between(7.0F, 10.0F))
								)
						)
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 24.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10261).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10211).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10375).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8267).setWeight(3).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8577).setWeight(3).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8570).setWeight(3).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(3).withFunction(EnchantRandomlyLootFunction.method_489()))
				)
		);
		biConsumer.accept(
			LootTables.field_665,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 6.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(90).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(40).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8287).setWeight(5))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8675).setWeight(50).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_SIMPLE_DUNGEON,
			LootSupplier.create()
				.withPool(
					LootPool.create()
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
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).withFunction(EnchantRandomlyLootFunction.method_489()))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_SPAWN_BONUS,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8062))
						.withEntry(ItemEntry.builder(Items.field_8406).setWeight(3))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8387))
						.withEntry(ItemEntry.builder(Items.field_8647).setWeight(3))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(4))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10161).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10431).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10037).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10511).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10306).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10533).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10010).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_STRONGHOLD_CORRIDOR,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 3.0F))
						.withEntry(ItemEntry.builder(Items.field_8634).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
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
						.withEntry(ItemEntry.builder(Items.field_8529).withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484()))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_STRONGHOLD_CROSSING,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403))
						.withEntry(ItemEntry.builder(Items.field_8529).withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484()))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_STRONGHOLD_LIBRARY,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 10.0F))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8895))
						.withEntry(ItemEntry.builder(Items.field_8251))
						.withEntry(
							ItemEntry.builder(Items.field_8529).setWeight(10).withFunction(EnchantWithLevelsLootFunction.method_481(ConstantLootTableRange.create(30)).method_484())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_300,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8463))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(5).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8862))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(5).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.setWeight(10)
								.withFunction(
									ExplorationMapLootFunction.create().destination("buried_treasure").decoration(MapIcon.Type.field_110).zoom((byte)1).skipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_397,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(2.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8062).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8687))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(Items.field_8577))
						.withEntry(ItemEntry.builder(Items.field_8862))
						.withEntry(ItemEntry.builder(Items.field_8378).setWeight(5).withFunction(EnchantRandomlyLootFunction.method_489()))
						.withEntry(
							ItemEntry.builder(Items.field_8895)
								.setWeight(5)
								.withFunction(
									ExplorationMapLootFunction.create().destination("buried_treasure").decoration(MapIcon.Type.field_110).zoom((byte)1).skipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.CHEST_VILLAGE_BLACKSMITH,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8371).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8523).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8743).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8396).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8660).setWeight(5))
						.withEntry(ItemEntry.builder(Blocks.field_10540).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10394).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(3))
						.withEntry(ItemEntry.builder(Items.field_8578))
						.withEntry(ItemEntry.builder(Items.field_8560))
						.withEntry(ItemEntry.builder(Items.field_8807))
				)
		);
		biConsumer.accept(
			LootTables.field_17107,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8477).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8403).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8699).setWeight(5))
				)
		);
		biConsumer.accept(
			LootTables.field_16751,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8895).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8407).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8251).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10394).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_17010,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8696).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8074).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10340).setWeight(2))
						.withEntry(ItemEntry.builder(Blocks.field_10056).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(4).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8192).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10360).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.field_17009,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(4).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8743).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.field_17011,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10446).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10146).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10423).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10113).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10222).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8868).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_17012,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8389).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8046).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8748).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_17108,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8107).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8803).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8145).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8600).setWeight(6).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_18007,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8429).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8209).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8705).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_16307).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8317).setWeight(3).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16750,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 5.0F))
						.withEntry(ItemEntry.builder(Items.field_8745).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8577).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8370).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8267).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8570).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_17109,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(7).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8759).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16748,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8491).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8880).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8279).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8153).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10394).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16749,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8675).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8471).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8561).setWeight(2))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_16998).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8741).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10217).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8111).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8684).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16753,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8397).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8602).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8256).setWeight(5))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8317).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Blocks.field_10385).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8175).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10336).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(1))
				)
		);
		biConsumer.accept(
			LootTables.field_16754,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Blocks.field_10384).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10491).setWeight(4))
						.withEntry(ItemEntry.builder(Items.field_8567).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.withEntry(ItemEntry.builder(Items.field_8515).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8732).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8543).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16752,
			LootSupplier.create()
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(3.0F, 8.0F))
						.withEntry(ItemEntry.builder(Items.field_8696).setWeight(1))
						.withEntry(ItemEntry.builder(Items.field_8408).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10029).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(1))
						.withEntry(ItemEntry.builder(Blocks.field_10428).setWeight(2).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.withEntry(ItemEntry.builder(Items.field_8687).setWeight(1).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.CHEST_WOODLAND_MANSION,
			LootSupplier.create()
				.withPool(
					LootPool.create()
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
						.withEntry(ItemEntry.builder(Items.field_8529).setWeight(10).withFunction(EnchantRandomlyLootFunction.method_489()))
				)
				.withPool(
					LootPool.create()
						.withRolls(UniformLootTableRange.between(1.0F, 4.0F))
						.withEntry(ItemEntry.builder(Items.field_8620).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8695).setWeight(5).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8229).setWeight(20))
						.withEntry(ItemEntry.builder(Items.field_8861).setWeight(20).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8550).setWeight(10))
						.withEntry(ItemEntry.builder(Items.field_8725).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8713).setWeight(15).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8188).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8706).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.withEntry(ItemEntry.builder(Items.field_8309).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.withPool(
					LootPool.create()
						.withRolls(ConstantLootTableRange.create(3))
						.withEntry(ItemEntry.builder(Items.field_8606).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8054).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8511).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.withEntry(ItemEntry.builder(Items.field_8276).setWeight(10).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
	}
}
