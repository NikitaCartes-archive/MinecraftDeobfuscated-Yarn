package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.BinomialLootTableRange;
import net.minecraft.world.loot.ConditionConsumerBuilder;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.FunctionConsumerBuilder;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.world.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.MatchToolLootCondition;
import net.minecraft.world.loot.condition.RandomChanceLootCondition;
import net.minecraft.world.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.world.loot.condition.TableBonusLootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.entry.AlternativeEntry;
import net.minecraft.world.loot.entry.DynamicEntry;
import net.minecraft.world.loot.entry.ItemEntry;
import net.minecraft.world.loot.entry.LeafEntry;
import net.minecraft.world.loot.entry.LootEntry;
import net.minecraft.world.loot.function.ApplyBonusLootFunction;
import net.minecraft.world.loot.function.CopyNameLootFunction;
import net.minecraft.world.loot.function.CopyNbtLootFunction;
import net.minecraft.world.loot.function.ExplosionDecayLootFunction;
import net.minecraft.world.loot.function.LimitCountLootFunction;
import net.minecraft.world.loot.function.SetContentsLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootSupplier.Builder>> {
	private static final LootCondition.Builder field_11336 = MatchToolLootCondition.builder(
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.field_9099, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder field_11337 = field_11336.invert();
	private static final LootCondition.Builder field_11343 = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.field_8868));
	private static final LootCondition.Builder field_11342 = field_11343.withCondition(field_11336);
	private static final LootCondition.Builder field_11341 = field_11342.invert();
	private static final Set<Item> field_11340 = (Set<Item>)Stream.of(
			Blocks.field_10081,
			Blocks.field_10327,
			Blocks.field_10502,
			Blocks.field_10481,
			Blocks.field_10177,
			Blocks.field_10432,
			Blocks.field_10241,
			Blocks.field_10042,
			Blocks.field_10337,
			Blocks.field_10603,
			Blocks.field_10371,
			Blocks.field_10605,
			Blocks.field_10373,
			Blocks.field_10532,
			Blocks.field_10140,
			Blocks.field_10055,
			Blocks.field_10203,
			Blocks.field_10320,
			Blocks.field_10275,
			Blocks.field_10063,
			Blocks.field_10407,
			Blocks.field_10051,
			Blocks.field_10268,
			Blocks.field_10068,
			Blocks.field_10199,
			Blocks.field_10600
		)
		.map(ItemConvertible::asItem)
		.collect(ImmutableSet.toImmutableSet());
	private static final float[] field_11339 = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] field_11338 = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<Identifier, LootSupplier.Builder> field_16493 = Maps.<Identifier, LootSupplier.Builder>newHashMap();

	private static <T> T method_10393(ItemConvertible itemConvertible, FunctionConsumerBuilder<T> functionConsumerBuilder) {
		return !field_11340.contains(itemConvertible.asItem())
			? functionConsumerBuilder.withFunction(ExplosionDecayLootFunction.builder())
			: functionConsumerBuilder.getThis();
	}

	private static <T> T method_10392(ItemConvertible itemConvertible, ConditionConsumerBuilder<T> conditionConsumerBuilder) {
		return !field_11340.contains(itemConvertible.asItem())
			? conditionConsumerBuilder.withCondition(SurvivesExplosionLootCondition.builder())
			: conditionConsumerBuilder.getThis();
	}

	private static LootSupplier.Builder method_10394(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
	}

	private static LootSupplier.Builder method_10381(Block block, LootCondition.Builder builder, LootEntry.Builder<?> builder2) {
		return LootSupplier.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).method_421(builder).withChild(builder2)));
	}

	private static LootSupplier.Builder method_10397(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11336, builder);
	}

	private static LootSupplier.Builder method_10380(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11343, builder);
	}

	private static LootSupplier.Builder method_10388(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11342, builder);
	}

	private static LootSupplier.Builder method_10382(Block block, ItemConvertible itemConvertible) {
		return method_10397(block, (LootEntry.Builder<?>)method_10392(block, ItemEntry.builder(itemConvertible)));
	}

	private static LootSupplier.Builder method_10384(ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return LootSupplier.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)method_10393(itemConvertible, ItemEntry.builder(itemConvertible).method_438(SetCountLootFunction.builder(lootTableRange)))
					)
			);
	}

	private static LootSupplier.Builder method_10386(Block block, ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return method_10397(
			block, (LootEntry.Builder<?>)method_10393(block, ItemEntry.builder(itemConvertible).method_438(SetCountLootFunction.builder(lootTableRange)))
		);
	}

	private static LootSupplier.Builder method_10373(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(LootPool.builder().method_356(field_11336).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootSupplier.Builder method_10389(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(method_10392(Blocks.field_10495, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.field_10495))))
			.withPool(method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
	}

	private static LootSupplier.Builder method_10383(Block block) {
		return LootSupplier.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)method_10393(
							block,
							ItemEntry.builder(block)
								.method_438(
									SetCountLootFunction.builder(ConstantLootTableRange.create(2))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(SlabBlock.TYPE, SlabType.field_12682))
								)
						)
					)
			);
	}

	private static <T extends Comparable<T>> LootSupplier.Builder method_10375(Block block, Property<T> property, T comparable) {
		return LootSupplier.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(block).method_421(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(property, comparable)))
				)
			);
	}

	private static LootSupplier.Builder method_10396(Block block) {
		return LootSupplier.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(block).method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023)))
				)
			);
	}

	private static LootSupplier.Builder method_16876(Block block) {
		return LootSupplier.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023))
								.method_438(
									CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027)
										.withOperation("Lock", "BlockEntityTag.Lock")
										.withOperation("LootTable", "BlockEntityTag.LootTable")
										.withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.method_438(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))
						)
				)
			);
	}

	private static LootSupplier.Builder method_16877(Block block) {
		return LootSupplier.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023))
								.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootSupplier.Builder method_10377(Block block, Item item) {
		return method_10397(
			block, (LootEntry.Builder<?>)method_10393(block, ItemEntry.builder(item).method_438(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130)))
		);
	}

	private static LootSupplier.Builder method_10385(Block block, ItemConvertible itemConvertible) {
		return method_10397(
			block,
			(LootEntry.Builder<?>)method_10393(
				block,
				ItemEntry.builder(itemConvertible)
					.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0F, 2.0F)))
					.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	private static LootSupplier.Builder method_10371(Block block) {
		return method_10380(
			block,
			(LootEntry.Builder<?>)method_10393(
				block,
				ItemEntry.builder(Items.field_8317)
					.method_421(RandomChanceLootCondition.builder(0.125F))
					.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130, 2))
			)
		);
	}

	private static LootSupplier.Builder method_10387(Block block, Item item) {
		return LootSupplier.builder()
			.withPool(
				method_10393(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(item)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 0))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 1))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 2))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 3))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 4))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 5))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 6))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))
										.method_524(BlockStatePropertyLootCondition.builder(block).withBlockStateProperty(StemBlock.field_11584, 7))
								)
						)
				)
			);
	}

	private static LootSupplier.Builder method_10372(ItemConvertible itemConvertible) {
		return LootSupplier.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).method_356(field_11343).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootSupplier.Builder method_10390(Block block, Block block2, float... fs) {
		return method_10388(
				block, ((LeafEntry.Builder)method_10392(block, ItemEntry.builder(block2))).method_421(TableBonusLootCondition.builder(Enchantments.field_9130, fs))
			)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.method_356(field_11341)
					.withEntry(
						((LeafEntry.Builder)method_10393(
								block, ItemEntry.builder(Items.field_8600).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
							))
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootSupplier.Builder method_10378(Block block, Block block2, float... fs) {
		return method_10390(block, block2, fs)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.method_356(field_11341)
					.withEntry(
						((LeafEntry.Builder)method_10392(block, ItemEntry.builder(Items.field_8279)))
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootSupplier.Builder method_10391(Block block, Item item, Item item2, LootCondition.Builder builder) {
		return method_10393(
			block,
			LootSupplier.builder()
				.withPool(LootPool.builder().withEntry(ItemEntry.builder(item).method_421(builder).withChild(ItemEntry.builder(item2))))
				.withPool(
					LootPool.builder()
						.method_356(builder)
						.withEntry(ItemEntry.builder(item2).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
				)
		);
	}

	public static LootSupplier.Builder method_10395() {
		return LootSupplier.builder();
	}

	public void method_10379(BiConsumer<Identifier, LootSupplier.Builder> biConsumer) {
		this.method_16329(Blocks.field_10474);
		this.method_16329(Blocks.field_10289);
		this.method_16329(Blocks.field_10508);
		this.method_16329(Blocks.field_10346);
		this.method_16329(Blocks.field_10115);
		this.method_16329(Blocks.field_10093);
		this.method_16329(Blocks.field_10566);
		this.method_16329(Blocks.field_10253);
		this.method_16329(Blocks.field_10445);
		this.method_16329(Blocks.field_10161);
		this.method_16329(Blocks.field_9975);
		this.method_16329(Blocks.field_10148);
		this.method_16329(Blocks.field_10334);
		this.method_16329(Blocks.field_10218);
		this.method_16329(Blocks.field_10075);
		this.method_16329(Blocks.field_10394);
		this.method_16329(Blocks.field_10217);
		this.method_16329(Blocks.field_10575);
		this.method_16329(Blocks.field_10276);
		this.method_16329(Blocks.field_10385);
		this.method_16329(Blocks.field_10160);
		this.method_16329(Blocks.field_10102);
		this.method_16329(Blocks.field_10534);
		this.method_16329(Blocks.field_10571);
		this.method_16329(Blocks.field_10212);
		this.method_16329(Blocks.field_10431);
		this.method_16329(Blocks.field_10037);
		this.method_16329(Blocks.field_10511);
		this.method_16329(Blocks.field_10306);
		this.method_16329(Blocks.field_10533);
		this.method_16329(Blocks.field_10010);
		this.method_16329(Blocks.field_10436);
		this.method_16329(Blocks.field_10366);
		this.method_16329(Blocks.field_10254);
		this.method_16329(Blocks.field_10622);
		this.method_16329(Blocks.field_10244);
		this.method_16329(Blocks.field_10519);
		this.method_16329(Blocks.field_10126);
		this.method_16329(Blocks.field_10155);
		this.method_16329(Blocks.field_10307);
		this.method_16329(Blocks.field_10303);
		this.method_16329(Blocks.field_9999);
		this.method_16329(Blocks.field_10178);
		this.method_16329(Blocks.field_10250);
		this.method_16329(Blocks.field_10558);
		this.method_16329(Blocks.field_10204);
		this.method_16329(Blocks.field_10084);
		this.method_16329(Blocks.field_10103);
		this.method_16329(Blocks.field_10374);
		this.method_16329(Blocks.field_10258);
		this.method_16329(Blocks.field_10562);
		this.method_16329(Blocks.field_10441);
		this.method_16329(Blocks.field_9979);
		this.method_16329(Blocks.field_10292);
		this.method_16329(Blocks.field_10361);
		this.method_16329(Blocks.field_10179);
		this.method_16329(Blocks.field_10425);
		this.method_16329(Blocks.field_10025);
		this.method_16329(Blocks.field_10615);
		this.method_16329(Blocks.field_10560);
		this.method_16329(Blocks.field_10446);
		this.method_16329(Blocks.field_10095);
		this.method_16329(Blocks.field_10215);
		this.method_16329(Blocks.field_10294);
		this.method_16329(Blocks.field_10490);
		this.method_16329(Blocks.field_10028);
		this.method_16329(Blocks.field_10459);
		this.method_16329(Blocks.field_10423);
		this.method_16329(Blocks.field_10222);
		this.method_16329(Blocks.field_10619);
		this.method_16329(Blocks.field_10259);
		this.method_16329(Blocks.field_10514);
		this.method_16329(Blocks.field_10113);
		this.method_16329(Blocks.field_10170);
		this.method_16329(Blocks.field_10314);
		this.method_16329(Blocks.field_10146);
		this.method_16329(Blocks.field_10182);
		this.method_16329(Blocks.field_10449);
		this.method_16329(Blocks.field_10086);
		this.method_16329(Blocks.field_10226);
		this.method_16329(Blocks.field_10573);
		this.method_16329(Blocks.field_10270);
		this.method_16329(Blocks.field_10048);
		this.method_16329(Blocks.field_10156);
		this.method_16329(Blocks.field_10315);
		this.method_16329(Blocks.field_10554);
		this.method_16329(Blocks.field_9995);
		this.method_16329(Blocks.field_10606);
		this.method_16329(Blocks.field_10548);
		this.method_16329(Blocks.field_10251);
		this.method_16329(Blocks.field_10559);
		this.method_16329(Blocks.field_10205);
		this.method_16329(Blocks.field_10085);
		this.method_16329(Blocks.field_10104);
		this.method_16329(Blocks.field_9989);
		this.method_16329(Blocks.field_10540);
		this.method_16329(Blocks.field_10336);
		this.method_16329(Blocks.field_10563);
		this.method_16329(Blocks.field_10091);
		this.method_16329(Blocks.field_10201);
		this.method_16329(Blocks.field_9980);
		this.method_16329(Blocks.field_10121);
		this.method_16329(Blocks.field_10411);
		this.method_16329(Blocks.field_10231);
		this.method_16329(Blocks.field_10284);
		this.method_16329(Blocks.field_10544);
		this.method_16329(Blocks.field_10330);
		this.method_16329(Blocks.field_9983);
		this.method_16329(Blocks.field_10167);
		this.method_16329(Blocks.field_10596);
		this.method_16329(Blocks.field_10363);
		this.method_16329(Blocks.field_10158);
		this.method_16329(Blocks.field_10484);
		this.method_16329(Blocks.field_10332);
		this.method_16329(Blocks.field_10592);
		this.method_16329(Blocks.field_10026);
		this.method_16329(Blocks.field_10397);
		this.method_16329(Blocks.field_10470);
		this.method_16329(Blocks.field_10523);
		this.method_16329(Blocks.field_10494);
		this.method_16329(Blocks.field_10029);
		this.method_16329(Blocks.field_10424);
		this.method_16329(Blocks.field_10223);
		this.method_16329(Blocks.field_10620);
		this.method_16329(Blocks.field_10261);
		this.method_16329(Blocks.field_10515);
		this.method_16329(Blocks.field_10114);
		this.method_16329(Blocks.field_10147);
		this.method_16329(Blocks.field_10009);
		this.method_16329(Blocks.field_10450);
		this.method_16329(Blocks.field_10137);
		this.method_16329(Blocks.field_10323);
		this.method_16329(Blocks.field_10486);
		this.method_16329(Blocks.field_10017);
		this.method_16329(Blocks.field_10608);
		this.method_16329(Blocks.field_10246);
		this.method_16329(Blocks.field_10056);
		this.method_16329(Blocks.field_10065);
		this.method_16329(Blocks.field_10416);
		this.method_16329(Blocks.field_10552);
		this.method_16329(Blocks.field_10576);
		this.method_16329(Blocks.field_10188);
		this.method_16329(Blocks.field_10089);
		this.method_16329(Blocks.field_10392);
		this.method_16329(Blocks.field_10588);
		this.method_16329(Blocks.field_10266);
		this.method_16329(Blocks.field_10364);
		this.method_16329(Blocks.field_10159);
		this.method_16329(Blocks.field_10593);
		this.method_16329(Blocks.field_10471);
		this.method_16329(Blocks.field_10524);
		this.method_16329(Blocks.field_10142);
		this.method_16329(Blocks.field_10348);
		this.method_16329(Blocks.field_10234);
		this.method_16329(Blocks.field_10569);
		this.method_16329(Blocks.field_10408);
		this.method_16329(Blocks.field_10122);
		this.method_16329(Blocks.field_10625);
		this.method_16329(Blocks.field_9990);
		this.method_16329(Blocks.field_10495);
		this.method_16329(Blocks.field_10057);
		this.method_16329(Blocks.field_10066);
		this.method_16329(Blocks.field_10417);
		this.method_16329(Blocks.field_10553);
		this.method_16329(Blocks.field_10278);
		this.method_16329(Blocks.field_10493);
		this.method_16329(Blocks.field_10481);
		this.method_16329(Blocks.field_10177);
		this.method_16329(Blocks.field_10241);
		this.method_16329(Blocks.field_10042);
		this.method_16329(Blocks.field_10337);
		this.method_16329(Blocks.field_10535);
		this.method_16329(Blocks.field_10105);
		this.method_16329(Blocks.field_10414);
		this.method_16329(Blocks.field_10224);
		this.method_16329(Blocks.field_10582);
		this.method_16329(Blocks.field_10377);
		this.method_16329(Blocks.field_10429);
		this.method_16329(Blocks.field_10002);
		this.method_16329(Blocks.field_10153);
		this.method_16329(Blocks.field_10044);
		this.method_16329(Blocks.field_10437);
		this.method_16329(Blocks.field_10451);
		this.method_16329(Blocks.field_10546);
		this.method_16329(Blocks.field_10611);
		this.method_16329(Blocks.field_10184);
		this.method_16329(Blocks.field_10015);
		this.method_16329(Blocks.field_10325);
		this.method_16329(Blocks.field_10143);
		this.method_16329(Blocks.field_10014);
		this.method_16329(Blocks.field_10444);
		this.method_16329(Blocks.field_10349);
		this.method_16329(Blocks.field_10590);
		this.method_16329(Blocks.field_10235);
		this.method_16329(Blocks.field_10570);
		this.method_16329(Blocks.field_10409);
		this.method_16329(Blocks.field_10123);
		this.method_16329(Blocks.field_10526);
		this.method_16329(Blocks.field_10328);
		this.method_16329(Blocks.field_10626);
		this.method_16329(Blocks.field_10256);
		this.method_16329(Blocks.field_10616);
		this.method_16329(Blocks.field_10030);
		this.method_16329(Blocks.field_10453);
		this.method_16329(Blocks.field_10135);
		this.method_16329(Blocks.field_10006);
		this.method_16329(Blocks.field_10297);
		this.method_16329(Blocks.field_10350);
		this.method_16329(Blocks.field_10190);
		this.method_16329(Blocks.field_10130);
		this.method_16329(Blocks.field_10359);
		this.method_16329(Blocks.field_10466);
		this.method_16329(Blocks.field_9977);
		this.method_16329(Blocks.field_10482);
		this.method_16329(Blocks.field_10290);
		this.method_16329(Blocks.field_10512);
		this.method_16329(Blocks.field_10040);
		this.method_16329(Blocks.field_10393);
		this.method_16329(Blocks.field_10591);
		this.method_16329(Blocks.field_10209);
		this.method_16329(Blocks.field_10433);
		this.method_16329(Blocks.field_10510);
		this.method_16329(Blocks.field_10043);
		this.method_16329(Blocks.field_10473);
		this.method_16329(Blocks.field_10338);
		this.method_16329(Blocks.field_10536);
		this.method_16329(Blocks.field_10106);
		this.method_16329(Blocks.field_10415);
		this.method_16329(Blocks.field_10381);
		this.method_16329(Blocks.field_10344);
		this.method_16329(Blocks.field_10117);
		this.method_16329(Blocks.field_10518);
		this.method_16329(Blocks.field_10420);
		this.method_16329(Blocks.field_10360);
		this.method_16329(Blocks.field_10467);
		this.method_16329(Blocks.field_9978);
		this.method_16329(Blocks.field_10483);
		this.method_16329(Blocks.field_10291);
		this.method_16329(Blocks.field_10513);
		this.method_16329(Blocks.field_10041);
		this.method_16329(Blocks.field_10457);
		this.method_16329(Blocks.field_10196);
		this.method_16329(Blocks.field_10020);
		this.method_16329(Blocks.field_10299);
		this.method_16329(Blocks.field_10319);
		this.method_16329(Blocks.field_10144);
		this.method_16329(Blocks.field_10132);
		this.method_16329(Blocks.field_10455);
		this.method_16329(Blocks.field_10286);
		this.method_16329(Blocks.field_10505);
		this.method_16329(Blocks.field_9992);
		this.method_16329(Blocks.field_10462);
		this.method_16329(Blocks.field_10092);
		this.method_16329(Blocks.field_10541);
		this.method_16329(Blocks.field_9986);
		this.method_16329(Blocks.field_10166);
		this.method_16329(Blocks.field_10282);
		this.method_16329(Blocks.field_10595);
		this.method_16329(Blocks.field_10280);
		this.method_16329(Blocks.field_10538);
		this.method_16329(Blocks.field_10345);
		this.method_16329(Blocks.field_10096);
		this.method_16329(Blocks.field_10046);
		this.method_16329(Blocks.field_10567);
		this.method_16329(Blocks.field_10220);
		this.method_16329(Blocks.field_10052);
		this.method_16329(Blocks.field_10078);
		this.method_16329(Blocks.field_10426);
		this.method_16329(Blocks.field_10550);
		this.method_16329(Blocks.field_10004);
		this.method_16329(Blocks.field_10475);
		this.method_16329(Blocks.field_10383);
		this.method_16329(Blocks.field_10501);
		this.method_16329(Blocks.field_10107);
		this.method_16329(Blocks.field_10210);
		this.method_16329(Blocks.field_10585);
		this.method_16329(Blocks.field_10242);
		this.method_16329(Blocks.field_10542);
		this.method_16329(Blocks.field_10421);
		this.method_16329(Blocks.field_10434);
		this.method_16329(Blocks.field_10038);
		this.method_16329(Blocks.field_10172);
		this.method_16329(Blocks.field_10308);
		this.method_16329(Blocks.field_10206);
		this.method_16329(Blocks.field_10011);
		this.method_16329(Blocks.field_10439);
		this.method_16329(Blocks.field_10367);
		this.method_16329(Blocks.field_10058);
		this.method_16329(Blocks.field_10458);
		this.method_16329(Blocks.field_10197);
		this.method_16329(Blocks.field_10022);
		this.method_16329(Blocks.field_10300);
		this.method_16329(Blocks.field_10321);
		this.method_16329(Blocks.field_10145);
		this.method_16329(Blocks.field_10133);
		this.method_16329(Blocks.field_10522);
		this.method_16329(Blocks.field_10353);
		this.method_16329(Blocks.field_10628);
		this.method_16329(Blocks.field_10233);
		this.method_16329(Blocks.field_10404);
		this.method_16329(Blocks.field_10456);
		this.method_16329(Blocks.field_10023);
		this.method_16329(Blocks.field_10529);
		this.method_16329(Blocks.field_10287);
		this.method_16329(Blocks.field_10506);
		this.method_16329(Blocks.field_9993);
		this.method_16329(Blocks.field_10342);
		this.method_16329(Blocks.field_10614);
		this.method_16329(Blocks.field_10264);
		this.method_16329(Blocks.field_10396);
		this.method_16329(Blocks.field_10111);
		this.method_16329(Blocks.field_10488);
		this.method_16329(Blocks.field_10502);
		this.method_16329(Blocks.field_10081);
		this.method_16329(Blocks.field_10211);
		this.method_16329(Blocks.field_10435);
		this.method_16329(Blocks.field_10039);
		this.method_16329(Blocks.field_10173);
		this.method_16329(Blocks.field_10310);
		this.method_16329(Blocks.field_10207);
		this.method_16329(Blocks.field_10012);
		this.method_16329(Blocks.field_10440);
		this.method_16329(Blocks.field_10549);
		this.method_16329(Blocks.field_10245);
		this.method_16329(Blocks.field_10607);
		this.method_16329(Blocks.field_10386);
		this.method_16329(Blocks.field_10497);
		this.method_16329(Blocks.field_9994);
		this.method_16329(Blocks.field_10216);
		this.method_16329(Blocks.field_10269);
		this.method_16329(Blocks.field_10530);
		this.method_16329(Blocks.field_10413);
		this.method_16329(Blocks.field_10059);
		this.method_16329(Blocks.field_10072);
		this.method_16329(Blocks.field_10252);
		this.method_16329(Blocks.field_10127);
		this.method_16329(Blocks.field_10489);
		this.method_16329(Blocks.field_10311);
		this.method_16329(Blocks.field_10630);
		this.method_16329(Blocks.field_10001);
		this.method_16329(Blocks.field_10517);
		this.method_16329(Blocks.field_10083);
		this.method_16329(Blocks.field_16492);
		this.method_16256(Blocks.field_10362, Blocks.field_10566);
		this.method_16256(Blocks.field_10589, Items.field_8276);
		this.method_16256(Blocks.field_10194, Blocks.field_10566);
		this.method_16256(Blocks.field_10463, Blocks.field_9993);
		this.method_16256(Blocks.field_10108, Blocks.field_10211);
		this.method_16293(Blocks.field_10340, blockx -> method_10382(blockx, Blocks.field_10445));
		this.method_16293(Blocks.field_10219, blockx -> method_10382(blockx, Blocks.field_10566));
		this.method_16293(Blocks.field_10520, blockx -> method_10382(blockx, Blocks.field_10566));
		this.method_16293(Blocks.field_10402, blockx -> method_10382(blockx, Blocks.field_10566));
		this.method_16293(Blocks.field_10309, blockx -> method_10382(blockx, Blocks.field_10614));
		this.method_16293(Blocks.field_10629, blockx -> method_10382(blockx, Blocks.field_10264));
		this.method_16293(Blocks.field_10000, blockx -> method_10382(blockx, Blocks.field_10396));
		this.method_16293(Blocks.field_10516, blockx -> method_10382(blockx, Blocks.field_10111));
		this.method_16293(Blocks.field_10464, blockx -> method_10382(blockx, Blocks.field_10488));
		this.method_16293(Blocks.field_10504, blockx -> method_10386(blockx, Items.field_8529, ConstantLootTableRange.create(3)));
		this.method_16293(Blocks.field_10460, blockx -> method_10386(blockx, Items.field_8696, ConstantLootTableRange.create(4)));
		this.method_16293(Blocks.field_10443, blockx -> method_10386(blockx, Blocks.field_10540, ConstantLootTableRange.create(8)));
		this.method_16293(Blocks.field_10491, blockx -> method_10386(blockx, Items.field_8543, ConstantLootTableRange.create(4)));
		this.method_16258(Blocks.field_10021, method_10384(Items.field_8233, UniformLootTableRange.between(0.0F, 1.0F)));
		this.method_16285(Blocks.field_10468);
		this.method_16285(Blocks.field_10192);
		this.method_16285(Blocks.field_10577);
		this.method_16285(Blocks.field_10304);
		this.method_16285(Blocks.field_10564);
		this.method_16285(Blocks.field_10076);
		this.method_16285(Blocks.field_10128);
		this.method_16285(Blocks.field_10354);
		this.method_16285(Blocks.field_10151);
		this.method_16285(Blocks.field_9981);
		this.method_16285(Blocks.field_10162);
		this.method_16285(Blocks.field_10365);
		this.method_16285(Blocks.field_10598);
		this.method_16285(Blocks.field_10249);
		this.method_16285(Blocks.field_10400);
		this.method_16285(Blocks.field_10061);
		this.method_16285(Blocks.field_10074);
		this.method_16285(Blocks.field_10358);
		this.method_16285(Blocks.field_10273);
		this.method_16285(Blocks.field_9998);
		this.method_16285(Blocks.field_10138);
		this.method_16285(Blocks.field_10324);
		this.method_16285(Blocks.field_10487);
		this.method_16285(Blocks.field_10018);
		this.method_16285(Blocks.field_10586);
		this.method_16293(Blocks.field_10031, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10257, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10191, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10351, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10500, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10623, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10617, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10390, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10119, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10298, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10236, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10389, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10175, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10237, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10624, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10007, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_18891, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_18890, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10071, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10131, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10454, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10136, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10329, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10283, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10024, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10412, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10405, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10064, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10262, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10601, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10189, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10016, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10478, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10322, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10507, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.field_10232, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10352, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10403, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_9973, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10627, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10149, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10521, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10461, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10527, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10288, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10109, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10141, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10561, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10621, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10326, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10180, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10230, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10019, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10410, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10610, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10069, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10120, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10356, blockx -> method_10375(blockx, BedBlock.PART, BedPart.field_12560));
		this.method_16293(Blocks.field_10378, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10583, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10003, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10430, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.method_16293(Blocks.field_10375, blockx -> method_10375(blockx, TntBlock.UNSTABLE, false));
		this.method_16293(
			Blocks.field_10302,
			blockx -> LootSupplier.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)method_10393(
									blockx,
									ItemEntry.builder(Items.field_8116)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(CocoaBlock.field_10779, 2))
										)
								)
							)
					)
		);
		this.method_16293(
			Blocks.field_10476,
			blockx -> LootSupplier.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)method_10393(
									blockx,
									ItemEntry.builder(blockx)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(2))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SeaPickleBlock.field_11472, 2))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SeaPickleBlock.field_11472, 3))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(4))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SeaPickleBlock.field_11472, 4))
										)
								)
							)
					)
		);
		this.method_16293(
			Blocks.field_17563,
			blockx -> LootSupplier.builder()
					.withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)method_10393(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.withPool(
						LootPool.builder()
							.withEntry(ItemEntry.builder(Items.field_8324))
							.method_356(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(ComposterBlock.field_17565, 8))
					)
		);
		this.method_16293(Blocks.field_10327, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10333, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10034, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10200, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10228, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10485, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10181, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10312, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_10380, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16334, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16333, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16328, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16336, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16331, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16337, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16330, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16329, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16335, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.field_16332, BlockLootTableGenerator::method_10394);
		this.method_16293(Blocks.field_16541, BlockLootTableGenerator::method_10394);
		this.method_16293(Blocks.field_10603, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10371, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10605, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10373, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10532, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10140, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10055, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10203, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10320, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10275, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10063, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10407, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10051, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10268, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10068, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10199, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10600, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.field_10062, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10281, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10602, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10165, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10185, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10198, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10452, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_9985, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10229, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10438, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10045, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10612, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10368, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10406, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10154, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.field_10547, BlockLootTableGenerator::method_16877);
		this.method_16293(
			Blocks.field_10432,
			blockx -> LootSupplier.builder()
					.withPool(
						method_10392(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(blockx).method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Owner", "SkullOwner"))
								)
						)
					)
		);
		this.method_16293(Blocks.field_10539, blockx -> method_10390(blockx, Blocks.field_10575, field_11339));
		this.method_16293(Blocks.field_10098, blockx -> method_10390(blockx, Blocks.field_10385, field_11339));
		this.method_16293(Blocks.field_10335, blockx -> method_10390(blockx, Blocks.field_10276, field_11338));
		this.method_16293(Blocks.field_9988, blockx -> method_10390(blockx, Blocks.field_10217, field_11339));
		this.method_16293(Blocks.field_10503, blockx -> method_10378(blockx, Blocks.field_10394, field_11339));
		this.method_16293(Blocks.field_10035, blockx -> method_10378(blockx, Blocks.field_10160, field_11339));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.field_10341).withBlockStateProperty(BeetrootsBlock.field_9962, 3);
		this.method_16293(Blocks.field_10341, blockx -> method_10391(blockx, Items.field_8186, Items.field_8309, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.field_10293).withBlockStateProperty(CropBlock.field_10835, 7);
		this.method_16293(Blocks.field_10293, blockx -> method_10391(blockx, Items.field_8861, Items.field_8317, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.field_10609).withBlockStateProperty(CarrotsBlock.field_10835, 7);
		this.method_16293(
			Blocks.field_10609,
			blockx -> method_10393(
					blockx,
					LootSupplier.builder()
						.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.field_8179)))
						.withPool(
							LootPool.builder()
								.method_356(builder3)
								.withEntry(ItemEntry.builder(Items.field_8179).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
						)
				)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.field_10247).withBlockStateProperty(PotatoesBlock.field_10835, 7);
		this.method_16293(
			Blocks.field_10247,
			blockx -> method_10393(
					blockx,
					LootSupplier.builder()
						.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.field_8567)))
						.withPool(
							LootPool.builder()
								.method_356(builder4)
								.withEntry(ItemEntry.builder(Items.field_8567).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
						)
						.withPool(LootPool.builder().method_356(builder4).withEntry(ItemEntry.builder(Items.field_8635).method_421(RandomChanceLootCondition.builder(0.02F))))
				)
		);
		this.method_16293(
			Blocks.field_16999,
			blockx -> method_10393(
					blockx,
					LootSupplier.builder()
						.withPool(
							LootPool.builder()
								.method_356(BlockStatePropertyLootCondition.builder(Blocks.field_16999).withBlockStateProperty(SweetBerryBushBlock.field_17000, 3))
								.withEntry(ItemEntry.builder(Items.field_16998))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
						.withPool(
							LootPool.builder()
								.method_356(BlockStatePropertyLootCondition.builder(Blocks.field_16999).withBlockStateProperty(SweetBerryBushBlock.field_17000, 2))
								.withEntry(ItemEntry.builder(Items.field_16998))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
				)
		);
		this.method_16293(Blocks.field_10580, blockx -> method_10385(blockx, Blocks.field_10251));
		this.method_16293(Blocks.field_10240, blockx -> method_10385(blockx, Blocks.field_10559));
		this.method_16293(Blocks.field_10418, blockx -> method_10377(blockx, Items.field_8713));
		this.method_16293(Blocks.field_10013, blockx -> method_10377(blockx, Items.field_8687));
		this.method_16293(Blocks.field_10213, blockx -> method_10377(blockx, Items.field_8155));
		this.method_16293(Blocks.field_10442, blockx -> method_10377(blockx, Items.field_8477));
		this.method_16293(
			Blocks.field_10090,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.field_8759)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F)))
							.method_438(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130))
					)
				)
		);
		this.method_16293(Blocks.field_10343, blockx -> method_10388(blockx, (LootEntry.Builder<?>)method_10392(blockx, ItemEntry.builder(Items.field_8276))));
		this.method_16293(
			Blocks.field_10428,
			blockx -> method_10380(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx, ItemEntry.builder(Items.field_8600).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
					)
				)
		);
		this.method_16293(Blocks.field_10376, BlockLootTableGenerator::method_10372);
		this.method_16293(Blocks.field_10597, BlockLootTableGenerator::method_10372);
		this.method_16258(Blocks.field_10238, method_10372(Blocks.field_10376));
		this.method_16258(Blocks.field_10313, method_10372(Blocks.field_10112));
		this.method_16293(
			Blocks.field_10214,
			blockx -> method_10380(
					Blocks.field_10479,
					((LeafEntry.Builder)((LeafEntry.Builder)method_10392(blockx, ItemEntry.builder(Items.field_8317)))
							.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(TallPlantBlock.HALF, DoubleBlockHalf.field_12607)))
						.method_421(RandomChanceLootCondition.builder(0.125F))
				)
		);
		this.method_16293(Blocks.field_10168, blockx -> method_10387(blockx, Items.field_8188));
		this.method_16293(Blocks.field_9984, blockx -> method_10387(blockx, Items.field_8706));
		this.method_16293(
			Blocks.field_10528,
			blockx -> LootSupplier.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								((LeafEntry.Builder)method_10392(blockx, ItemEntry.builder(blockx)))
									.method_421(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							)
					)
		);
		this.method_16293(Blocks.field_10112, BlockLootTableGenerator::method_10371);
		this.method_16293(Blocks.field_10479, BlockLootTableGenerator::method_10371);
		this.method_16293(
			Blocks.field_10171,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.field_8601)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.method_16293(
			Blocks.field_10545,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.field_8497)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.method_16293(
			Blocks.field_10080,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.field_8725)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 5.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
					)
				)
		);
		this.method_16293(
			Blocks.field_10174,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.field_8434)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.method_16293(
			Blocks.field_9974,
			blockx -> LootSupplier.builder()
					.withPool(
						method_10393(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(Items.field_8790)
										.method_438(
											SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(NetherWartBlock.field_11306, 3))
										)
										.method_438(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130)
												.method_524(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(NetherWartBlock.field_11306, 3))
										)
								)
						)
					)
		);
		this.method_16293(
			Blocks.field_10477,
			blockx -> LootSupplier.builder()
					.withPool(
						LootPool.builder()
							.method_356(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							.withEntry(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.field_8543).method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 1)),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 2))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 3))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 4))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 5))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 6))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 7))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7))),
											ItemEntry.builder(Items.field_8543).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))
										)
										.method_421(field_11337),
									AlternativeEntry.builder(
										ItemEntry.builder(blockx).method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 1)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 2)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 3)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 4)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 5)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 6)),
										ItemEntry.builder(blockx)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).withBlockStateProperty(SnowBlock.field_11518, 7)),
										ItemEntry.builder(Blocks.field_10491)
									)
								)
							)
					)
		);
		this.method_16293(
			Blocks.field_10255,
			blockx -> method_10397(
					blockx,
					method_10392(
						blockx,
						ItemEntry.builder(Items.field_8145)
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.withChild(ItemEntry.builder(blockx))
					)
				)
		);
		this.method_16293(
			Blocks.field_17350,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10392(blockx, ItemEntry.builder(Items.field_8665).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
				)
		);
		this.method_16262(Blocks.field_10033);
		this.method_16262(Blocks.field_10087);
		this.method_16262(Blocks.field_10227);
		this.method_16262(Blocks.field_10574);
		this.method_16262(Blocks.field_10271);
		this.method_16262(Blocks.field_10049);
		this.method_16262(Blocks.field_10157);
		this.method_16262(Blocks.field_10317);
		this.method_16262(Blocks.field_10555);
		this.method_16262(Blocks.field_9996);
		this.method_16262(Blocks.field_10248);
		this.method_16262(Blocks.field_10399);
		this.method_16262(Blocks.field_10060);
		this.method_16262(Blocks.field_10073);
		this.method_16262(Blocks.field_10357);
		this.method_16262(Blocks.field_10272);
		this.method_16262(Blocks.field_9997);
		this.method_16262(Blocks.field_10285);
		this.method_16262(Blocks.field_9991);
		this.method_16262(Blocks.field_10496);
		this.method_16262(Blocks.field_10469);
		this.method_16262(Blocks.field_10193);
		this.method_16262(Blocks.field_10578);
		this.method_16262(Blocks.field_10305);
		this.method_16262(Blocks.field_10565);
		this.method_16262(Blocks.field_10077);
		this.method_16262(Blocks.field_10129);
		this.method_16262(Blocks.field_10355);
		this.method_16262(Blocks.field_10152);
		this.method_16262(Blocks.field_9982);
		this.method_16262(Blocks.field_10163);
		this.method_16262(Blocks.field_10419);
		this.method_16262(Blocks.field_10118);
		this.method_16262(Blocks.field_10070);
		this.method_16262(Blocks.field_10295);
		this.method_16262(Blocks.field_10225);
		this.method_16262(Blocks.field_10384);
		this.method_16262(Blocks.field_10195);
		this.method_16262(Blocks.field_10556);
		this.method_16262(Blocks.field_10082);
		this.method_16262(Blocks.field_10572);
		this.method_16262(Blocks.field_10296);
		this.method_16262(Blocks.field_10579);
		this.method_16262(Blocks.field_10032);
		this.method_16262(Blocks.field_10125);
		this.method_16262(Blocks.field_10339);
		this.method_16262(Blocks.field_10134);
		this.method_16262(Blocks.field_10618);
		this.method_16262(Blocks.field_10169);
		this.method_16262(Blocks.field_10448);
		this.method_16262(Blocks.field_10097);
		this.method_16262(Blocks.field_10047);
		this.method_16262(Blocks.field_10568);
		this.method_16262(Blocks.field_10221);
		this.method_16262(Blocks.field_10053);
		this.method_16262(Blocks.field_10079);
		this.method_16262(Blocks.field_10427);
		this.method_16262(Blocks.field_10551);
		this.method_16262(Blocks.field_10005);
		this.method_16238(Blocks.field_10277, Blocks.field_10340);
		this.method_16238(Blocks.field_10492, Blocks.field_10445);
		this.method_16238(Blocks.field_10387, Blocks.field_10056);
		this.method_16238(Blocks.field_10480, Blocks.field_10065);
		this.method_16238(Blocks.field_10100, Blocks.field_10416);
		this.method_16238(Blocks.field_10176, Blocks.field_10552);
		this.method_16258(Blocks.field_10183, method_10395());
		this.method_16258(Blocks.field_10331, method_10395());
		this.method_16258(Blocks.field_10150, method_10395());
		this.method_16258(Blocks.field_10110, method_10395());
		this.method_16258(Blocks.field_10260, method_10395());
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (Block block : Registry.BLOCK) {
			Identifier identifier = block.getDropTableId();
			if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootSupplier.Builder builder5 = (LootSupplier.Builder)this.field_16493.remove(identifier);
				if (builder5 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.BLOCK.getId(block)));
				}

				biConsumer.accept(identifier, builder5);
			}
		}

		if (!this.field_16493.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_16493.keySet());
		}
	}

	public void method_16285(Block block) {
		this.method_16293(block, blockx -> method_10389(((FlowerPotBlock)blockx).getContent()));
	}

	public void method_16238(Block block, Block block2) {
		this.method_16258(block, method_10373(block2));
	}

	public void method_16256(Block block, ItemConvertible itemConvertible) {
		this.method_16258(block, method_10394(itemConvertible));
	}

	public void method_16262(Block block) {
		this.method_16238(block, block);
	}

	public void method_16329(Block block) {
		this.method_16256(block, block);
	}

	private void method_16293(Block block, Function<Block, LootSupplier.Builder> function) {
		this.method_16258(block, (LootSupplier.Builder)function.apply(block));
	}

	private void method_16258(Block block, LootSupplier.Builder builder) {
		this.field_16493.put(block.getDropTableId(), builder);
	}
}
