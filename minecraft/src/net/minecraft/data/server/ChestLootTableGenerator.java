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
	public void method_10399(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		biConsumer.accept(
			LootTables.field_472,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8463).weight(20))
						.with(ItemEntry.builder(Items.field_8367))
						.with(ItemEntry.builder(Items.field_8448).weight(30))
						.with(ItemEntry.builder(Items.field_8529).weight(10).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8403).weight(5))
						.with(EmptyEntry.Serializer().weight(5))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8725).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8759).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8477).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8188).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8706).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8309).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Blocks.field_10167).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.field_10425).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10025).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10546).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10336).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 16.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_24048,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Blocks.field_23261).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 2.0F))
						.with(
							ItemEntry.builder(Items.field_8399)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.5F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.field_8236).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 28.0F))))
						.with(ItemEntry.builder(Blocks.field_23880).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.field_22423).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.field_10205).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8695).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8620).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8845).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.field_8678)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8862)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8416)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8753)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8825)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8276).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8745).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8107).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.field_8675).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8397).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_24049,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.field_8250)
								.weight(15)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.15F, 0.8F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8377)
								.weight(12)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.15F, 0.95F)))
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.field_22021).weight(8).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(5).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
						.with(ItemEntry.builder(Items.field_8175).weight(12).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.field_10205).weight(16).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8071).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 17.0F))))
						.with(ItemEntry.builder(Items.field_8463).weight(10).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(
							ItemEntry.builder(Items.field_8825)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.field_22423).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_10171).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 6.0F))))
						.with(ItemEntry.builder(Blocks.field_23880).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_10114).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.field_22120).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8397).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8745).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8107).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.field_8276).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8389).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8261).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_22121).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.field_22125).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_24047,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.field_8377)
								.weight(6)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.field_8250).weight(6).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.field_8399)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.9F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.ANCIENT_DEBRIS).weight(12).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_22021).weight(4).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8236).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(10.0F, 22.0F))))
						.with(ItemEntry.builder(Items.field_23831).weight(9).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_23984).weight(5).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8071).weight(12).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(6.0F, 17.0F))))
						.with(ItemEntry.builder(Items.field_8463).weight(9).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8529).weight(10).method_438(new EnchantRandomlyLootFunction.Builder().add(Enchantments.field_23071)))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(2))
						.with(
							ItemEntry.builder(Items.field_8371)
								.weight(2)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.1F, 0.9F)))
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.field_10085).weight(2).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(
							ItemEntry.builder(Items.field_8753)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(new EnchantRandomlyLootFunction.Builder().add(Enchantments.field_23071))
						)
						.with(
							ItemEntry.builder(Items.field_8825)
								.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Blocks.field_10205).weight(2).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8399).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8695).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8845).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8678).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8862).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8416).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_8753).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.field_22423).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(ItemEntry.builder(Blocks.field_23880).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_23985).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 10.0F))))
						.with(ItemEntry.builder(Items.field_8135).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Blocks.field_10166).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8675).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.field_10540).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8397).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8276).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8107).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 17.0F))))
						.with(ItemEntry.builder(Items.field_8261).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
		);
		biConsumer.accept(
			LootTables.field_24046,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.field_22020).weight(15).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.field_22109).weight(10).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Items.field_22021).weight(8).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
						.with(ItemEntry.builder(Blocks.field_22109).weight(4).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
						.with(
							ItemEntry.builder(Items.field_8802)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8058)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8805)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8348)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(
							ItemEntry.builder(Items.field_8285)
								.weight(6)
								.method_438(SetDamageLootFunction.builder(UniformLootTableRange.between(0.8F, 1.0F)))
								.method_438(EnchantRandomlyLootFunction.builder())
						)
						.with(ItemEntry.builder(Items.field_8802).weight(6))
						.with(ItemEntry.builder(Items.field_8058).weight(5))
						.with(ItemEntry.builder(Items.field_8805).weight(5))
						.with(ItemEntry.builder(Items.field_8285).weight(5))
						.with(ItemEntry.builder(Items.field_8348).weight(5))
						.with(ItemEntry.builder(Items.field_8477).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8367).weight(2).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8236).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(12.0F, 25.0F))))
						.with(ItemEntry.builder(Blocks.field_10205).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_10085).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8620).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 9.0F))))
						.with(ItemEntry.builder(Blocks.field_22423).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8155).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 23.0F))))
						.with(ItemEntry.builder(Blocks.field_23880).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 15.0F))))
						.with(ItemEntry.builder(Items.field_8135).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_251,
			LootTable.builder()
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8207)))
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(5.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10375).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8687).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8477).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8434).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
				.pool(
					LootPool.builder().rolls(UniformLootTableRange.between(0.0F, 1.0F)).with(ItemEntry.builder(Items.field_8577)).with(ItemEntry.builder(Items.field_8371))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(2))
						.with(ItemEntry.builder(Items.field_8373).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8509).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_885,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8477).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8606).weight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8680).weight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(25).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(20))
						.with(ItemEntry.builder(Items.field_8578).weight(15))
						.with(ItemEntry.builder(Items.field_8560).weight(10))
						.with(ItemEntry.builder(Items.field_8807).weight(5))
						.with(ItemEntry.builder(Items.field_8529).weight(20).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8463).weight(20))
						.with(ItemEntry.builder(Items.field_8367).weight(2))
						.with(EmptyEntry.Serializer().weight(15))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(4))
						.with(ItemEntry.builder(Items.field_8606).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8054).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8276).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.field_10102).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_274,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 6.0F))
						.with(ItemEntry.builder(Items.field_8477).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8309).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(3))
						.with(ItemEntry.builder(Items.field_8578))
						.with(ItemEntry.builder(Items.field_8560))
						.with(ItemEntry.builder(Items.field_8807))
						.with(
							ItemEntry.builder(Items.field_8802)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8285)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8058)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8348)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8805)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8377)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8250)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8371)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8660)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8523)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8396)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8743)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8403)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
						.with(
							ItemEntry.builder(Items.field_8699)
								.weight(3)
								.method_438(EnchantWithLevelsLootFunction.builder(UniformLootTableRange.between(20.0F, 39.0F)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_662,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8279).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8397).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8062).weight(2))
						.with(ItemEntry.builder(Items.field_8511).weight(10))
						.with(ItemEntry.builder(Items.field_8687))
						.with(ItemEntry.builder(Items.field_8861).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8463)))
		);
		biConsumer.accept(
			LootTables.field_803,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 6.0F))
						.with(ItemEntry.builder(Items.field_8477).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.field_10211).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8606).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(16).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(3))
						.with(ItemEntry.builder(Items.field_8578))
						.with(ItemEntry.builder(Items.field_8560))
						.with(ItemEntry.builder(Items.field_8807))
						.with(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_751,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 2.0F))
						.with(ItemEntry.builder(Items.field_8107).weight(30).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_615,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8477).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8845).weight(5))
						.with(ItemEntry.builder(Items.field_8678).weight(5))
						.with(ItemEntry.builder(Items.field_8884).weight(5))
						.with(ItemEntry.builder(Items.field_8790).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(10))
						.with(ItemEntry.builder(Items.field_8560).weight(8))
						.with(ItemEntry.builder(Items.field_8578).weight(5))
						.with(ItemEntry.builder(Items.field_8807).weight(3))
						.with(ItemEntry.builder(Blocks.field_10540).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_16593,
			LootTable.builder()
				.pool(LootPool.builder().rolls(UniformLootTableRange.between(0.0F, 1.0F)).with(ItemEntry.builder(Items.field_8399)))
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8861).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8567).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8179).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 5.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Blocks.field_10010).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8287).weight(7))
						.with(ItemEntry.builder(Items.field_8276).weight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8107).weight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.TRIPWIRE_HOOK).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8529).weight(1).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.field_841,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(Items.field_8895)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.field_24857)
										.withDecoration(MapIcon.Type.field_110)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.field_8251))
						.with(ItemEntry.builder(Items.field_8895))
						.with(ItemEntry.builder(Items.field_8557))
						.with(ItemEntry.builder(Items.field_8407).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.field_8153).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8529).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_880,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 10.0F))
						.with(ItemEntry.builder(Items.field_8407).weight(8).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Items.field_8567).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8635).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F))))
						.with(ItemEntry.builder(Items.field_8179).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8861).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(8.0F, 21.0F))))
						.with(
							ItemEntry.builder(Items.field_8766)
								.weight(10)
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
						.with(ItemEntry.builder(Items.field_8713).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(5.0F, 24.0F))))
						.with(ItemEntry.builder(Blocks.field_10261).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10211).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8054).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Blocks.field_10375).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8267).weight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8577).weight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8570).weight(3).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8370).weight(3).method_438(EnchantRandomlyLootFunction.builder()))
				)
		);
		biConsumer.accept(
			LootTables.field_665,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 6.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(90).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8477).weight(5))
						.with(ItemEntry.builder(Items.field_8287).weight(5))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8675).weight(50).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.field_8397).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
						.with(ItemEntry.builder(Items.field_8759).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 10.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_356,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8175).weight(20))
						.with(ItemEntry.builder(Items.field_8463).weight(15))
						.with(ItemEntry.builder(Items.field_8367).weight(2))
						.with(ItemEntry.builder(Items.field_8144).weight(15))
						.with(ItemEntry.builder(Items.field_8075).weight(15))
						.with(ItemEntry.builder(Items.field_8448).weight(20))
						.with(ItemEntry.builder(Items.field_8560).weight(10))
						.with(ItemEntry.builder(Items.field_8578).weight(15))
						.with(ItemEntry.builder(Items.field_8807).weight(5))
						.with(ItemEntry.builder(Items.field_8529).weight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(20))
						.with(ItemEntry.builder(Items.field_8861).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8550).weight(10))
						.with(ItemEntry.builder(Items.field_8725).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8188).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8706).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8309).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.field_8606).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8054).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8276).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.SPAWN_BONUS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8062)).with(ItemEntry.builder(Items.field_8406).weight(3))
				)
				.pool(
					LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Items.field_8387)).with(ItemEntry.builder(Items.field_8647).weight(3))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.field_8279).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8209).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(4))
						.with(ItemEntry.builder(Items.field_8600).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.field_10161).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 12.0F))))
						.with(ItemEntry.builder(Blocks.field_10431).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10037).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10511).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10306).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10533).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10010).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_842,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8634).weight(10))
						.with(ItemEntry.builder(Items.field_8477).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8725).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8279).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8403).weight(5))
						.with(ItemEntry.builder(Items.field_8371).weight(5))
						.with(ItemEntry.builder(Items.field_8523).weight(5))
						.with(ItemEntry.builder(Items.field_8743).weight(5))
						.with(ItemEntry.builder(Items.field_8396).weight(5))
						.with(ItemEntry.builder(Items.field_8660).weight(5))
						.with(ItemEntry.builder(Items.field_8463))
						.with(ItemEntry.builder(Items.field_8175))
						.with(ItemEntry.builder(Items.field_8578))
						.with(ItemEntry.builder(Items.field_8560))
						.with(ItemEntry.builder(Items.field_8807))
						.with(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_800,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8725).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8279).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8403))
						.with(
							ItemEntry.builder(Items.field_8529).method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_683,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 10.0F))
						.with(ItemEntry.builder(Items.field_8529).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8407).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8895))
						.with(ItemEntry.builder(Items.field_8251))
						.with(
							ItemEntry.builder(Items.field_8529)
								.weight(10)
								.method_438(EnchantWithLevelsLootFunction.builder(ConstantLootTableRange.create(30)).allowTreasureEnchantments())
						)
				)
		);
		biConsumer.accept(
			LootTables.field_300,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8713).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8397).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8687))
						.with(ItemEntry.builder(Items.field_8861).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8463))
						.with(ItemEntry.builder(Items.field_8529).weight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8577))
						.with(ItemEntry.builder(Items.field_8862))
						.with(ItemEntry.builder(Items.field_8378).weight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.field_8895)
								.weight(10)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.field_24857)
										.withDecoration(MapIcon.Type.field_110)
										.withZoom((byte)1)
										.withSkipExistingChunks(false)
								)
						)
				)
		);
		biConsumer.accept(
			LootTables.field_397,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(2.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8713).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8062).weight(2))
						.with(ItemEntry.builder(Items.field_8511).weight(5))
						.with(ItemEntry.builder(Items.field_8687))
						.with(ItemEntry.builder(Items.field_8861).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(Items.field_8577))
						.with(ItemEntry.builder(Items.field_8862))
						.with(ItemEntry.builder(Items.field_8378).weight(5).method_438(EnchantRandomlyLootFunction.builder()))
						.with(
							ItemEntry.builder(Items.field_8895)
								.weight(5)
								.method_438(
									ExplorationMapLootFunction.create()
										.withDestination(StructureFeature.field_24857)
										.withDecoration(MapIcon.Type.field_110)
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
						.with(ItemEntry.builder(Items.field_8477).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8279).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8403).weight(5))
						.with(ItemEntry.builder(Items.field_8371).weight(5))
						.with(ItemEntry.builder(Items.field_8523).weight(5))
						.with(ItemEntry.builder(Items.field_8743).weight(5))
						.with(ItemEntry.builder(Items.field_8396).weight(5))
						.with(ItemEntry.builder(Items.field_8660).weight(5))
						.with(ItemEntry.builder(Blocks.field_10540).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Blocks.field_10394).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(3))
						.with(ItemEntry.builder(Items.field_8578))
						.with(ItemEntry.builder(Items.field_8560))
						.with(ItemEntry.builder(Items.field_8807))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TOOLSMITH_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8477).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8620).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8403).weight(5))
						.with(ItemEntry.builder(Items.field_8713).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8600).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8699).weight(5))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_CARTOGRAPHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8895).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8407).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8251).weight(5))
						.with(ItemEntry.builder(Items.field_8229).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8600).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_MASON_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8696).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.FLOWER_POT).weight(1))
						.with(ItemEntry.builder(Blocks.field_10340).weight(2))
						.with(ItemEntry.builder(Blocks.field_10056).weight(2))
						.with(ItemEntry.builder(Items.field_8229).weight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8192).weight(1))
						.with(ItemEntry.builder(Blocks.field_10360).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_ARMORER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(4).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8743).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SHEPARD_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Blocks.field_10446).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Blocks.field_10146).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10423).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10113).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Blocks.field_10222).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
						.with(ItemEntry.builder(Items.field_8868).weight(1))
						.with(ItemEntry.builder(Items.field_8861).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 6.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_BUTCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
						.with(ItemEntry.builder(Items.field_8389).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8861).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8046).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8748).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FLETCHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
						.with(ItemEntry.builder(Items.field_8107).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8153).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8803).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8145).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8600).weight(6).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_FISHER_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8687).weight(1))
						.with(ItemEntry.builder(Items.field_8429).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8209).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8705).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.BARREL).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8317).weight(3).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TANNERY_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 5.0F))
						.with(ItemEntry.builder(Items.field_8745).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8577).weight(2))
						.with(ItemEntry.builder(Items.field_8370).weight(2))
						.with(ItemEntry.builder(Items.field_8267).weight(2))
						.with(ItemEntry.builder(Items.field_8229).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8570).weight(2))
						.with(ItemEntry.builder(Items.field_8175).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TEMPLE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8725).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(7).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8759).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_PLAINS_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8397).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.DANDELION).weight(2))
						.with(ItemEntry.builder(Items.POPPY).weight(1))
						.with(ItemEntry.builder(Items.field_8567).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8279).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8529).weight(1))
						.with(ItemEntry.builder(Items.field_8153).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10394).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8675).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.FERN).weight(2))
						.with(ItemEntry.builder(Items.LARGE_FERN).weight(2))
						.with(ItemEntry.builder(Items.field_8567).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_16998).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8706).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8741).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10217).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8111).weight(1))
						.with(ItemEntry.builder(Items.SPRUCE_LOG).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8397).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.GRASS).weight(5))
						.with(ItemEntry.builder(Items.TALL_GRASS).weight(5))
						.with(ItemEntry.builder(Items.field_8229).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8317).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Blocks.field_10385).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8175).weight(1))
						.with(ItemEntry.builder(Blocks.field_10336).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8550).weight(1))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Blocks.field_10384).weight(1))
						.with(ItemEntry.builder(Blocks.field_10491).weight(4))
						.with(ItemEntry.builder(Items.field_8567).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8309).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 5.0F))))
						.with(ItemEntry.builder(Items.field_8515).weight(1))
						.with(ItemEntry.builder(Items.FURNACE).weight(1))
						.with(ItemEntry.builder(Items.field_8687).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8543).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
				)
		);
		biConsumer.accept(
			LootTables.VILLAGE_DESERT_HOUSE_CHEST,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(3.0F, 8.0F))
						.with(ItemEntry.builder(Items.field_8696).weight(1))
						.with(ItemEntry.builder(Items.field_8408).weight(1))
						.with(ItemEntry.builder(Blocks.field_10029).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8861).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 7.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8529).weight(1))
						.with(ItemEntry.builder(Blocks.field_10428).weight(2).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
						.with(ItemEntry.builder(Items.field_8687).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 3.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_484,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 3.0F))
						.with(ItemEntry.builder(Items.field_8719).weight(20))
						.with(ItemEntry.builder(Items.field_8463).weight(15))
						.with(ItemEntry.builder(Items.field_8367).weight(2))
						.with(ItemEntry.builder(Items.field_8144).weight(15))
						.with(ItemEntry.builder(Items.field_8075).weight(15))
						.with(ItemEntry.builder(Items.field_8448).weight(20))
						.with(ItemEntry.builder(Items.field_8873).weight(10))
						.with(ItemEntry.builder(Items.field_8527).weight(15))
						.with(ItemEntry.builder(Items.field_8058).weight(5))
						.with(ItemEntry.builder(Items.field_8529).weight(10).method_438(EnchantRandomlyLootFunction.builder()))
				)
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(1.0F, 4.0F))
						.with(ItemEntry.builder(Items.field_8620).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8229).weight(20))
						.with(ItemEntry.builder(Items.field_8861).weight(20).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8550).weight(10))
						.with(ItemEntry.builder(Items.field_8725).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8713).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8188).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8706).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8309).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))))
				)
				.pool(
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(3))
						.with(ItemEntry.builder(Items.field_8606).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8054).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8511).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
						.with(ItemEntry.builder(Items.field_8276).weight(10).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 8.0F))))
				)
		);
		biConsumer.accept(
			LootTables.field_24050,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.rolls(UniformLootTableRange.between(4.0F, 8.0F))
						.with(ItemEntry.builder(Items.OBSIDIAN).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
						.with(ItemEntry.builder(Items.field_8145).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 4.0F))))
						.with(ItemEntry.builder(Items.field_8675).weight(40).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(9.0F, 18.0F))))
						.with(ItemEntry.builder(Items.field_8884).weight(40))
						.with(ItemEntry.builder(Items.field_8814).weight(40))
						.with(ItemEntry.builder(Items.field_8463).weight(15))
						.with(ItemEntry.builder(Items.field_8397).weight(15).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 24.0F))))
						.with(ItemEntry.builder(Items.field_8845).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8825).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8303).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8322).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8335).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8753).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8678).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8862).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8416).weight(15).method_438(EnchantRandomlyLootFunction.builder()))
						.with(ItemEntry.builder(Items.field_8597).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.field_8560).weight(5))
						.with(ItemEntry.builder(Items.LIGHT_WEIGHTED_PRESSURE_PLATE).weight(5))
						.with(ItemEntry.builder(Items.field_8071).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 12.0F))))
						.with(ItemEntry.builder(Items.field_8557).weight(5))
						.with(ItemEntry.builder(Items.field_8695).weight(5).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 8.0F))))
						.with(ItemEntry.builder(Items.BELL).weight(1))
						.with(ItemEntry.builder(Items.field_8367).weight(1))
						.with(ItemEntry.builder(Items.GOLD_BLOCK).weight(1).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F))))
				)
		);
	}
}
