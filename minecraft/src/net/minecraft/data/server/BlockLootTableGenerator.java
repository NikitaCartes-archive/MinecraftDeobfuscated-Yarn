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
import net.minecraft.block.BeehiveBlock;
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
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableRange;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final LootCondition.Builder WITH_SILK_TOUCH = MatchToolLootCondition.builder(
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.field_9099, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder WITHOUT_SILK_TOUCH = WITH_SILK_TOUCH.invert();
	private static final LootCondition.Builder WITH_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.field_8868));
	private static final LootCondition.Builder WITH_SILK_TOUCH_OR_SHEARS = WITH_SHEARS.or(WITH_SILK_TOUCH);
	private static final LootCondition.Builder WITHOUT_SILK_TOUCH_NOR_SHEARS = WITH_SILK_TOUCH_OR_SHEARS.invert();
	private static final Set<Item> EXPLOSION_IMMUNE = (Set<Item>)Stream.of(
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
	private static final float[] SAPLING_DROP_CHANCE = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] JUNGLE_SAPLING_DROP_CHANCE = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<Identifier, LootTable.Builder> lootTables = Maps.<Identifier, LootTable.Builder>newHashMap();

	private static <T> T applyExplosionDecay(ItemConvertible drop, LootFunctionConsumingBuilder<T> builder) {
		return !EXPLOSION_IMMUNE.contains(drop.asItem()) ? builder.apply(ExplosionDecayLootFunction.builder()) : builder.getThis();
	}

	private static <T> T addSurvivesExplosionCondition(ItemConvertible drop, LootConditionConsumingBuilder<T> builder) {
		return !EXPLOSION_IMMUNE.contains(drop.asItem()) ? builder.conditionally(SurvivesExplosionLootCondition.builder()) : builder.getThis();
	}

	private static LootTable.Builder drops(ItemConvertible drop) {
		return LootTable.builder()
			.pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(drop))));
	}

	private static LootTable.Builder drops(Block drop, LootCondition.Builder conditionBuilder, LootPoolEntry.Builder<?> child) {
		return LootTable.builder()
			.pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(drop).method_421(conditionBuilder).alternatively(child)));
	}

	private static LootTable.Builder dropsWithSilkTouch(Block drop, LootPoolEntry.Builder<?> child) {
		return drops(drop, WITH_SILK_TOUCH, child);
	}

	private static LootTable.Builder dropsWithShears(Block drop, LootPoolEntry.Builder<?> child) {
		return drops(drop, WITH_SHEARS, child);
	}

	private static LootTable.Builder dropsWithSilkTouchOrShears(Block drop, LootPoolEntry.Builder<?> child) {
		return drops(drop, WITH_SILK_TOUCH_OR_SHEARS, child);
	}

	private static LootTable.Builder drops(Block dropWithSilkTouch, ItemConvertible drop) {
		return dropsWithSilkTouch(dropWithSilkTouch, (LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(dropWithSilkTouch, ItemEntry.builder(drop)));
	}

	private static LootTable.Builder drops(ItemConvertible drop, LootTableRange count) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootTableRange.create(1))
					.with((LootPoolEntry.Builder<?>)applyExplosionDecay(drop, ItemEntry.builder(drop).method_438(SetCountLootFunction.builder(count))))
			);
	}

	private static LootTable.Builder drops(Block dropWithSilkTouch, ItemConvertible drop, LootTableRange count) {
		return dropsWithSilkTouch(
			dropWithSilkTouch, (LootPoolEntry.Builder<?>)applyExplosionDecay(dropWithSilkTouch, ItemEntry.builder(drop).method_438(SetCountLootFunction.builder(count)))
		);
	}

	private static LootTable.Builder dropsWithSilkTouch(ItemConvertible drop) {
		return LootTable.builder().pool(LootPool.builder().method_356(WITH_SILK_TOUCH).rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(drop)));
	}

	private static LootTable.Builder pottedPlantDrops(ItemConvertible plant) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(Blocks.field_10495, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(Blocks.field_10495)))
			)
			.pool(addSurvivesExplosionCondition(plant, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(ItemEntry.builder(plant))));
	}

	private static LootTable.Builder slabDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootTableRange.create(1))
					.with(
						(LootPoolEntry.Builder<?>)applyExplosionDecay(
							drop,
							ItemEntry.builder(drop)
								.method_438(
									SetCountLootFunction.builder(ConstantLootTableRange.create(2))
										.method_524(
											BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.field_12682))
										)
								)
						)
					)
			);
	}

	private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T comparable) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(drop)
								.method_421(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, comparable)))
						)
				)
			);
	}

	private static LootTable.Builder nameableContainerDrops(Block drop) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(drop).method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023)))
				)
			);
	}

	private static LootTable.Builder shulkerBoxDrops(Block drop) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(drop)
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

	private static LootTable.Builder bannerDrops(Block drop) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(drop)
								.method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.field_1023))
								.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootTable.Builder beeNestDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.method_356(WITH_SILK_TOUCH)
					.rolls(ConstantLootTableRange.create(1))
					.with(
						ItemEntry.builder(drop)
							.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Bees", "BlockEntityTag.Bees"))
							.method_438(CopyStateFunction.getBuilder(drop).method_21898(BeehiveBlock.HONEY_LEVEL))
					)
			);
	}

	private static LootTable.Builder beehiveDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootTableRange.create(1))
					.with(
						ItemEntry.builder(drop)
							.method_421(WITH_SILK_TOUCH)
							.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("Bees", "BlockEntityTag.Bees"))
							.method_438(CopyStateFunction.getBuilder(drop).method_21898(BeehiveBlock.HONEY_LEVEL))
							.alternatively(ItemEntry.builder(drop))
					)
			);
	}

	private static LootTable.Builder oreDrops(Block dropWithSilkTouch, Item drop) {
		return dropsWithSilkTouch(
			dropWithSilkTouch,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(
				dropWithSilkTouch, ItemEntry.builder(drop).method_438(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130))
			)
		);
	}

	private static LootTable.Builder mushroomBlockDrops(Block dropWithSilkTouch, ItemConvertible drop) {
		return dropsWithSilkTouch(
			dropWithSilkTouch,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(
				dropWithSilkTouch,
				ItemEntry.builder(drop)
					.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0F, 2.0F)))
					.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	private static LootTable.Builder grassDrops(Block dropWithShears) {
		return dropsWithShears(
			dropWithShears,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(
				dropWithShears,
				ItemEntry.builder(Items.field_8317)
					.method_421(RandomChanceLootCondition.builder(0.125F))
					.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130, 2))
			)
		);
	}

	private static LootTable.Builder cropStemDrops(Block stem, Item drop) {
		return LootTable.builder()
			.pool(
				applyExplosionDecay(
					stem,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(
							ItemEntry.builder(drop)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))
										.method_524(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))
								)
						)
				)
			);
	}

	private static LootTable.Builder attachedCropStemDrops(Block stem, Item drop) {
		return LootTable.builder()
			.pool(
				applyExplosionDecay(
					stem,
					LootPool.builder()
						.rolls(ConstantLootTableRange.create(1))
						.with(ItemEntry.builder(drop).method_438(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))))
				)
			);
	}

	private static LootTable.Builder dropsWithShears(ItemConvertible drop) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootTableRange.create(1)).method_356(WITH_SHEARS).with(ItemEntry.builder(drop)));
	}

	private static LootTable.Builder leavesDrop(Block leaves, Block drop, float... chance) {
		return dropsWithSilkTouchOrShears(
				leaves,
				((LeafEntry.Builder)addSurvivesExplosionCondition(leaves, ItemEntry.builder(drop)))
					.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, chance))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootTableRange.create(1))
					.method_356(WITHOUT_SILK_TOUCH_NOR_SHEARS)
					.with(
						((LeafEntry.Builder)applyExplosionDecay(
								leaves, ItemEntry.builder(Items.field_8600).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
							))
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootTable.Builder oakLeavesDrop(Block leaves, Block drop, float... chance) {
		return leavesDrop(leaves, drop, chance)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootTableRange.create(1))
					.method_356(WITHOUT_SILK_TOUCH_NOR_SHEARS)
					.with(
						((LeafEntry.Builder)addSurvivesExplosionCondition(leaves, ItemEntry.builder(Items.field_8279)))
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootTable.Builder cropDrops(Block crop, Item product, Item seeds, LootCondition.Builder condition) {
		return applyExplosionDecay(
			crop,
			LootTable.builder()
				.pool(LootPool.builder().with(ItemEntry.builder(product).method_421(condition).alternatively(ItemEntry.builder(seeds))))
				.pool(
					LootPool.builder()
						.method_356(condition)
						.with(ItemEntry.builder(seeds).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
				)
		);
	}

	private static LootTable.Builder method_30159(Block block) {
		return LootTable.builder()
			.pool(LootPool.builder().method_356(WITH_SHEARS).with(ItemEntry.builder(block).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))));
	}

	private static LootTable.Builder method_30158(Block block, Block block2) {
		LootPoolEntry.Builder<?> builder = ItemEntry.builder(block2)
			.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
			.method_421(WITH_SHEARS)
			.alternatively(
				((LeafEntry.Builder)addSurvivesExplosionCondition(block, ItemEntry.builder(Items.field_8317))).method_421(RandomChanceLootCondition.builder(0.125F))
			);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(builder)
					.method_356(
						BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12607))
					)
					.method_356(
						LocationCheckLootCondition.method_30151(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create()
										.block(block)
										.state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12609).build())
										.build()
								),
							new BlockPos(0, 1, 0)
						)
					)
			)
			.pool(
				LootPool.builder()
					.with(builder)
					.method_356(
						BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12609))
					)
					.method_356(
						LocationCheckLootCondition.method_30151(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create()
										.block(block)
										.state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.field_12607).build())
										.build()
								),
							new BlockPos(0, -1, 0)
						)
					)
			);
	}

	public static LootTable.Builder dropsNothing() {
		return LootTable.builder();
	}

	public void method_10379(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.addDrop(Blocks.field_10474);
		this.addDrop(Blocks.field_10289);
		this.addDrop(Blocks.field_10508);
		this.addDrop(Blocks.field_10346);
		this.addDrop(Blocks.field_10115);
		this.addDrop(Blocks.field_10093);
		this.addDrop(Blocks.field_10566);
		this.addDrop(Blocks.field_10253);
		this.addDrop(Blocks.field_10445);
		this.addDrop(Blocks.field_10161);
		this.addDrop(Blocks.field_9975);
		this.addDrop(Blocks.field_10148);
		this.addDrop(Blocks.field_10334);
		this.addDrop(Blocks.field_10218);
		this.addDrop(Blocks.field_10075);
		this.addDrop(Blocks.field_10394);
		this.addDrop(Blocks.field_10217);
		this.addDrop(Blocks.field_10575);
		this.addDrop(Blocks.field_10276);
		this.addDrop(Blocks.field_10385);
		this.addDrop(Blocks.field_10160);
		this.addDrop(Blocks.field_10102);
		this.addDrop(Blocks.field_10534);
		this.addDrop(Blocks.field_10571);
		this.addDrop(Blocks.field_10212);
		this.addDrop(Blocks.field_10431);
		this.addDrop(Blocks.field_10037);
		this.addDrop(Blocks.field_10511);
		this.addDrop(Blocks.field_10306);
		this.addDrop(Blocks.field_10533);
		this.addDrop(Blocks.field_10010);
		this.addDrop(Blocks.field_10436);
		this.addDrop(Blocks.field_10366);
		this.addDrop(Blocks.field_10254);
		this.addDrop(Blocks.field_10622);
		this.addDrop(Blocks.field_10244);
		this.addDrop(Blocks.field_10519);
		this.addDrop(Blocks.field_22112);
		this.addDrop(Blocks.field_22119);
		this.addDrop(Blocks.field_10126);
		this.addDrop(Blocks.field_10155);
		this.addDrop(Blocks.field_10307);
		this.addDrop(Blocks.field_10303);
		this.addDrop(Blocks.field_9999);
		this.addDrop(Blocks.field_10178);
		this.addDrop(Blocks.field_10250);
		this.addDrop(Blocks.field_10558);
		this.addDrop(Blocks.field_10204);
		this.addDrop(Blocks.field_10084);
		this.addDrop(Blocks.field_10103);
		this.addDrop(Blocks.field_10374);
		this.addDrop(Blocks.field_22506);
		this.addDrop(Blocks.field_22504);
		this.addDrop(Blocks.field_10258);
		this.addDrop(Blocks.field_10562);
		this.addDrop(Blocks.field_10441);
		this.addDrop(Blocks.field_9979);
		this.addDrop(Blocks.field_10292);
		this.addDrop(Blocks.field_10361);
		this.addDrop(Blocks.field_10179);
		this.addDrop(Blocks.field_10425);
		this.addDrop(Blocks.field_10025);
		this.addDrop(Blocks.field_10615);
		this.addDrop(Blocks.field_10560);
		this.addDrop(Blocks.field_10446);
		this.addDrop(Blocks.field_10095);
		this.addDrop(Blocks.field_10215);
		this.addDrop(Blocks.field_10294);
		this.addDrop(Blocks.field_10490);
		this.addDrop(Blocks.field_10028);
		this.addDrop(Blocks.field_10459);
		this.addDrop(Blocks.field_10423);
		this.addDrop(Blocks.field_10222);
		this.addDrop(Blocks.field_10619);
		this.addDrop(Blocks.field_10259);
		this.addDrop(Blocks.field_10514);
		this.addDrop(Blocks.field_10113);
		this.addDrop(Blocks.field_10170);
		this.addDrop(Blocks.field_10314);
		this.addDrop(Blocks.field_10146);
		this.addDrop(Blocks.field_10182);
		this.addDrop(Blocks.field_10449);
		this.addDrop(Blocks.field_10086);
		this.addDrop(Blocks.field_10226);
		this.addDrop(Blocks.field_10573);
		this.addDrop(Blocks.field_10270);
		this.addDrop(Blocks.field_10048);
		this.addDrop(Blocks.field_10156);
		this.addDrop(Blocks.field_10315);
		this.addDrop(Blocks.field_10554);
		this.addDrop(Blocks.field_9995);
		this.addDrop(Blocks.field_10606);
		this.addDrop(Blocks.field_10548);
		this.addDrop(Blocks.field_10251);
		this.addDrop(Blocks.field_10559);
		this.addDrop(Blocks.field_10205);
		this.addDrop(Blocks.field_10085);
		this.addDrop(Blocks.field_10104);
		this.addDrop(Blocks.field_9989);
		this.addDrop(Blocks.field_10540);
		this.addDrop(Blocks.field_22423);
		this.addDrop(Blocks.field_10336);
		this.addDrop(Blocks.field_10563);
		this.addDrop(Blocks.field_10091);
		this.addDrop(Blocks.field_10201);
		this.addDrop(Blocks.field_9980);
		this.addDrop(Blocks.field_10121);
		this.addDrop(Blocks.field_10411);
		this.addDrop(Blocks.field_10231);
		this.addDrop(Blocks.field_10284);
		this.addDrop(Blocks.field_10544);
		this.addDrop(Blocks.field_10330);
		this.addDrop(Blocks.field_9983);
		this.addDrop(Blocks.field_10167);
		this.addDrop(Blocks.field_10596);
		this.addDrop(Blocks.field_10363);
		this.addDrop(Blocks.field_10158);
		this.addDrop(Blocks.field_10484);
		this.addDrop(Blocks.field_10332);
		this.addDrop(Blocks.field_10592);
		this.addDrop(Blocks.field_10026);
		this.addDrop(Blocks.field_10397);
		this.addDrop(Blocks.field_10470);
		this.addDrop(Blocks.field_10523);
		this.addDrop(Blocks.field_10494);
		this.addDrop(Blocks.field_10029);
		this.addDrop(Blocks.field_10424);
		this.addDrop(Blocks.field_10223);
		this.addDrop(Blocks.field_10620);
		this.addDrop(Blocks.field_10261);
		this.addDrop(Blocks.field_10515);
		this.addDrop(Blocks.field_10114);
		this.addDrop(Blocks.field_22090);
		this.addDrop(Blocks.field_22091);
		this.addDrop(Blocks.field_23151);
		this.addDrop(Blocks.field_22092);
		this.addDrop(Blocks.field_10147);
		this.addDrop(Blocks.field_10009);
		this.addDrop(Blocks.field_10450);
		this.addDrop(Blocks.field_10137);
		this.addDrop(Blocks.field_10323);
		this.addDrop(Blocks.field_10486);
		this.addDrop(Blocks.field_10017);
		this.addDrop(Blocks.field_10608);
		this.addDrop(Blocks.field_10246);
		this.addDrop(Blocks.field_10056);
		this.addDrop(Blocks.field_10065);
		this.addDrop(Blocks.field_10416);
		this.addDrop(Blocks.field_10552);
		this.addDrop(Blocks.field_10576);
		this.addDrop(Blocks.field_10188);
		this.addDrop(Blocks.field_10089);
		this.addDrop(Blocks.field_10392);
		this.addDrop(Blocks.field_10588);
		this.addDrop(Blocks.field_10266);
		this.addDrop(Blocks.field_10364);
		this.addDrop(Blocks.field_10159);
		this.addDrop(Blocks.field_10593);
		this.addDrop(Blocks.field_10471);
		this.addDrop(Blocks.field_10524);
		this.addDrop(Blocks.field_10142);
		this.addDrop(Blocks.field_10348);
		this.addDrop(Blocks.field_10234);
		this.addDrop(Blocks.field_10569);
		this.addDrop(Blocks.field_10408);
		this.addDrop(Blocks.field_10122);
		this.addDrop(Blocks.field_10625);
		this.addDrop(Blocks.field_9990);
		this.addDrop(Blocks.field_10495);
		this.addDrop(Blocks.field_10057);
		this.addDrop(Blocks.field_10066);
		this.addDrop(Blocks.field_10417);
		this.addDrop(Blocks.field_10553);
		this.addDrop(Blocks.field_10278);
		this.addDrop(Blocks.field_10493);
		this.addDrop(Blocks.field_10481);
		this.addDrop(Blocks.field_10177);
		this.addDrop(Blocks.field_10241);
		this.addDrop(Blocks.field_10042);
		this.addDrop(Blocks.field_10337);
		this.addDrop(Blocks.field_10535);
		this.addDrop(Blocks.field_10105);
		this.addDrop(Blocks.field_10414);
		this.addDrop(Blocks.field_10224);
		this.addDrop(Blocks.field_10582);
		this.addDrop(Blocks.field_10377);
		this.addDrop(Blocks.field_10429);
		this.addDrop(Blocks.field_10002);
		this.addDrop(Blocks.field_10153);
		this.addDrop(Blocks.field_10044);
		this.addDrop(Blocks.field_10437);
		this.addDrop(Blocks.field_10451);
		this.addDrop(Blocks.field_10546);
		this.addDrop(Blocks.field_10611);
		this.addDrop(Blocks.field_10184);
		this.addDrop(Blocks.field_10015);
		this.addDrop(Blocks.field_10325);
		this.addDrop(Blocks.field_10143);
		this.addDrop(Blocks.field_10014);
		this.addDrop(Blocks.field_10444);
		this.addDrop(Blocks.field_10349);
		this.addDrop(Blocks.field_10590);
		this.addDrop(Blocks.field_10235);
		this.addDrop(Blocks.field_10570);
		this.addDrop(Blocks.field_10409);
		this.addDrop(Blocks.field_10123);
		this.addDrop(Blocks.field_10526);
		this.addDrop(Blocks.field_10328);
		this.addDrop(Blocks.field_10626);
		this.addDrop(Blocks.field_10256);
		this.addDrop(Blocks.field_10616);
		this.addDrop(Blocks.field_10030);
		this.addDrop(Blocks.field_10453);
		this.addDrop(Blocks.field_10135);
		this.addDrop(Blocks.field_10006);
		this.addDrop(Blocks.field_10297);
		this.addDrop(Blocks.field_10350);
		this.addDrop(Blocks.field_10190);
		this.addDrop(Blocks.field_10130);
		this.addDrop(Blocks.field_10359);
		this.addDrop(Blocks.field_10466);
		this.addDrop(Blocks.field_9977);
		this.addDrop(Blocks.field_10482);
		this.addDrop(Blocks.field_10290);
		this.addDrop(Blocks.field_10512);
		this.addDrop(Blocks.field_10040);
		this.addDrop(Blocks.field_10393);
		this.addDrop(Blocks.field_10591);
		this.addDrop(Blocks.field_10209);
		this.addDrop(Blocks.field_10433);
		this.addDrop(Blocks.field_10510);
		this.addDrop(Blocks.field_10043);
		this.addDrop(Blocks.field_10473);
		this.addDrop(Blocks.field_10338);
		this.addDrop(Blocks.field_10536);
		this.addDrop(Blocks.field_10106);
		this.addDrop(Blocks.field_10415);
		this.addDrop(Blocks.field_10381);
		this.addDrop(Blocks.field_10344);
		this.addDrop(Blocks.field_10117);
		this.addDrop(Blocks.field_10518);
		this.addDrop(Blocks.field_10420);
		this.addDrop(Blocks.field_10360);
		this.addDrop(Blocks.field_10467);
		this.addDrop(Blocks.field_9978);
		this.addDrop(Blocks.field_10483);
		this.addDrop(Blocks.field_10291);
		this.addDrop(Blocks.field_10513);
		this.addDrop(Blocks.field_10041);
		this.addDrop(Blocks.field_10457);
		this.addDrop(Blocks.field_10196);
		this.addDrop(Blocks.field_10020);
		this.addDrop(Blocks.field_10299);
		this.addDrop(Blocks.field_10319);
		this.addDrop(Blocks.field_10144);
		this.addDrop(Blocks.field_10132);
		this.addDrop(Blocks.field_10455);
		this.addDrop(Blocks.field_10286);
		this.addDrop(Blocks.field_10505);
		this.addDrop(Blocks.field_9992);
		this.addDrop(Blocks.field_10462);
		this.addDrop(Blocks.field_10092);
		this.addDrop(Blocks.field_10541);
		this.addDrop(Blocks.field_9986);
		this.addDrop(Blocks.field_10166);
		this.addDrop(Blocks.field_10282);
		this.addDrop(Blocks.field_22422);
		this.addDrop(Blocks.field_10595);
		this.addDrop(Blocks.field_10280);
		this.addDrop(Blocks.field_10538);
		this.addDrop(Blocks.field_10345);
		this.addDrop(Blocks.field_10096);
		this.addDrop(Blocks.field_10046);
		this.addDrop(Blocks.field_10567);
		this.addDrop(Blocks.field_10220);
		this.addDrop(Blocks.field_10052);
		this.addDrop(Blocks.field_10078);
		this.addDrop(Blocks.field_10426);
		this.addDrop(Blocks.field_10550);
		this.addDrop(Blocks.field_10004);
		this.addDrop(Blocks.field_10475);
		this.addDrop(Blocks.field_10383);
		this.addDrop(Blocks.field_10501);
		this.addDrop(Blocks.field_10107);
		this.addDrop(Blocks.field_10210);
		this.addDrop(Blocks.field_10585);
		this.addDrop(Blocks.field_10242);
		this.addDrop(Blocks.field_10542);
		this.addDrop(Blocks.field_10421);
		this.addDrop(Blocks.field_10434);
		this.addDrop(Blocks.field_10038);
		this.addDrop(Blocks.field_10172);
		this.addDrop(Blocks.field_10308);
		this.addDrop(Blocks.field_10206);
		this.addDrop(Blocks.field_10011);
		this.addDrop(Blocks.field_10439);
		this.addDrop(Blocks.field_10367);
		this.addDrop(Blocks.field_10058);
		this.addDrop(Blocks.field_10458);
		this.addDrop(Blocks.field_10197);
		this.addDrop(Blocks.field_10022);
		this.addDrop(Blocks.field_10300);
		this.addDrop(Blocks.field_10321);
		this.addDrop(Blocks.field_10145);
		this.addDrop(Blocks.field_10133);
		this.addDrop(Blocks.field_10522);
		this.addDrop(Blocks.field_10353);
		this.addDrop(Blocks.field_10628);
		this.addDrop(Blocks.field_10233);
		this.addDrop(Blocks.field_10404);
		this.addDrop(Blocks.field_10456);
		this.addDrop(Blocks.field_10023);
		this.addDrop(Blocks.field_10529);
		this.addDrop(Blocks.field_10287);
		this.addDrop(Blocks.field_10506);
		this.addDrop(Blocks.field_9993);
		this.addDrop(Blocks.field_10342);
		this.addDrop(Blocks.field_10614);
		this.addDrop(Blocks.field_10264);
		this.addDrop(Blocks.field_10396);
		this.addDrop(Blocks.field_10111);
		this.addDrop(Blocks.field_10488);
		this.addDrop(Blocks.field_10502);
		this.addDrop(Blocks.field_10081);
		this.addDrop(Blocks.field_10211);
		this.addDrop(Blocks.field_10435);
		this.addDrop(Blocks.field_10039);
		this.addDrop(Blocks.field_10173);
		this.addDrop(Blocks.field_10310);
		this.addDrop(Blocks.field_10207);
		this.addDrop(Blocks.field_10012);
		this.addDrop(Blocks.field_10440);
		this.addDrop(Blocks.field_10549);
		this.addDrop(Blocks.field_10245);
		this.addDrop(Blocks.field_10607);
		this.addDrop(Blocks.field_10386);
		this.addDrop(Blocks.field_10497);
		this.addDrop(Blocks.field_9994);
		this.addDrop(Blocks.field_10216);
		this.addDrop(Blocks.field_10269);
		this.addDrop(Blocks.field_10530);
		this.addDrop(Blocks.field_10413);
		this.addDrop(Blocks.field_10059);
		this.addDrop(Blocks.field_10072);
		this.addDrop(Blocks.field_10252);
		this.addDrop(Blocks.field_10127);
		this.addDrop(Blocks.field_10489);
		this.addDrop(Blocks.field_10311);
		this.addDrop(Blocks.field_10630);
		this.addDrop(Blocks.field_10001);
		this.addDrop(Blocks.field_10517);
		this.addDrop(Blocks.field_10083);
		this.addDrop(Blocks.field_16492);
		this.addDrop(Blocks.field_21211);
		this.addDrop(Blocks.field_21212);
		this.addDrop(Blocks.field_23152);
		this.addDrop(Blocks.field_23261);
		this.addDrop(Blocks.field_22111);
		this.addDrop(Blocks.field_22503);
		this.addDrop(Blocks.field_22114);
		this.addDrop(Blocks.field_22115);
		this.addDrop(Blocks.field_22118);
		this.addDrop(Blocks.field_22505);
		this.addDrop(Blocks.field_22121);
		this.addDrop(Blocks.field_22122);
		this.addDrop(Blocks.field_22126);
		this.addDrop(Blocks.field_22127);
		this.addDrop(Blocks.field_22131);
		this.addDrop(Blocks.field_22133);
		this.addDrop(Blocks.field_22095);
		this.addDrop(Blocks.field_22097);
		this.addDrop(Blocks.field_22099);
		this.addDrop(Blocks.field_22101);
		this.addDrop(Blocks.field_22105);
		this.addDrop(Blocks.field_22130);
		this.addDrop(Blocks.field_22132);
		this.addDrop(Blocks.field_22094);
		this.addDrop(Blocks.field_22096);
		this.addDrop(Blocks.field_22098);
		this.addDrop(Blocks.field_22100);
		this.addDrop(Blocks.field_22104);
		this.addDrop(Blocks.field_22108);
		this.addDrop(Blocks.field_22109);
		this.addDrop(Blocks.field_23869);
		this.addDrop(Blocks.field_23874);
		this.addDrop(Blocks.field_23878);
		this.addDrop(Blocks.field_23870);
		this.addDrop(Blocks.field_23871);
		this.addDrop(Blocks.field_23879);
		this.addDrop(Blocks.field_23876);
		this.addDrop(Blocks.field_23875);
		this.addDrop(Blocks.field_23873);
		this.addDrop(Blocks.field_23861);
		this.addDrop(Blocks.field_23863);
		this.addDrop(Blocks.field_23864);
		this.addDrop(Blocks.field_23865);
		this.addDrop(Blocks.field_23866);
		this.addDrop(Blocks.field_23867);
		this.addDrop(Blocks.field_23868);
		this.addDrop(Blocks.field_23985);
		this.addDrop(Blocks.field_22116);
		this.addDrop(Blocks.field_22125);
		this.addDrop(Blocks.field_10362, Blocks.field_10566);
		this.addDrop(Blocks.field_10589, Items.field_8276);
		this.addDrop(Blocks.field_10194, Blocks.field_10566);
		this.addDrop(Blocks.field_10463, Blocks.field_9993);
		this.addDrop(Blocks.field_10108, Blocks.field_10211);
		this.addDrop(Blocks.field_10340, blockx -> drops(blockx, Blocks.field_10445));
		this.addDrop(Blocks.field_10219, blockx -> drops(blockx, Blocks.field_10566));
		this.addDrop(Blocks.field_10520, blockx -> drops(blockx, Blocks.field_10566));
		this.addDrop(Blocks.field_10402, blockx -> drops(blockx, Blocks.field_10566));
		this.addDrop(Blocks.field_10309, blockx -> drops(blockx, Blocks.field_10614));
		this.addDrop(Blocks.field_10629, blockx -> drops(blockx, Blocks.field_10264));
		this.addDrop(Blocks.field_10000, blockx -> drops(blockx, Blocks.field_10396));
		this.addDrop(Blocks.field_10516, blockx -> drops(blockx, Blocks.field_10111));
		this.addDrop(Blocks.field_10464, blockx -> drops(blockx, Blocks.field_10488));
		this.addDrop(Blocks.field_22120, blockx -> drops(blockx, Blocks.field_10515));
		this.addDrop(Blocks.field_22113, blockx -> drops(blockx, Blocks.field_10515));
		this.addDrop(Blocks.field_10504, blockx -> drops(blockx, Items.field_8529, ConstantLootTableRange.create(3)));
		this.addDrop(Blocks.field_10460, blockx -> drops(blockx, Items.field_8696, ConstantLootTableRange.create(4)));
		this.addDrop(Blocks.field_10443, blockx -> drops(blockx, Blocks.field_10540, ConstantLootTableRange.create(8)));
		this.addDrop(Blocks.field_10491, blockx -> drops(blockx, Items.field_8543, ConstantLootTableRange.create(4)));
		this.addDrop(Blocks.field_10021, drops(Items.field_8233, UniformLootTableRange.between(0.0F, 1.0F)));
		this.addPottedPlantDrop(Blocks.field_10468);
		this.addPottedPlantDrop(Blocks.field_10192);
		this.addPottedPlantDrop(Blocks.field_10577);
		this.addPottedPlantDrop(Blocks.field_10304);
		this.addPottedPlantDrop(Blocks.field_10564);
		this.addPottedPlantDrop(Blocks.field_10076);
		this.addPottedPlantDrop(Blocks.field_10128);
		this.addPottedPlantDrop(Blocks.field_10354);
		this.addPottedPlantDrop(Blocks.field_10151);
		this.addPottedPlantDrop(Blocks.field_9981);
		this.addPottedPlantDrop(Blocks.field_10162);
		this.addPottedPlantDrop(Blocks.field_10365);
		this.addPottedPlantDrop(Blocks.field_10598);
		this.addPottedPlantDrop(Blocks.field_10249);
		this.addPottedPlantDrop(Blocks.field_10400);
		this.addPottedPlantDrop(Blocks.field_10061);
		this.addPottedPlantDrop(Blocks.field_10074);
		this.addPottedPlantDrop(Blocks.field_10358);
		this.addPottedPlantDrop(Blocks.field_10273);
		this.addPottedPlantDrop(Blocks.field_9998);
		this.addPottedPlantDrop(Blocks.field_10138);
		this.addPottedPlantDrop(Blocks.field_10324);
		this.addPottedPlantDrop(Blocks.field_10487);
		this.addPottedPlantDrop(Blocks.field_10018);
		this.addPottedPlantDrop(Blocks.field_10586);
		this.addPottedPlantDrop(Blocks.field_22424);
		this.addPottedPlantDrop(Blocks.field_22425);
		this.addPottedPlantDrop(Blocks.field_22426);
		this.addPottedPlantDrop(Blocks.field_22427);
		this.addDrop(Blocks.field_10031, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10257, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10191, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10351, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10500, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10623, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10617, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10390, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10119, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10298, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10236, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10389, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10175, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10237, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10624, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10007, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_18891, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_18890, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10071, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10131, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10454, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10136, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10329, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10283, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10024, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10412, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10405, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10064, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10262, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10601, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10189, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10016, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10478, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10322, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10507, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_22128, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_22129, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_23872, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_23877, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_23862, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.field_10232, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10352, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10403, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_9973, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10627, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10149, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10521, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_22103, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_22102, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.field_10461, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10527, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10288, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10109, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10141, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10561, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10621, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10326, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10180, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10230, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10019, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10410, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10610, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10069, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10120, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10356, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.field_12560));
		this.addDrop(Blocks.field_10378, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.addDrop(Blocks.field_10583, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.addDrop(Blocks.field_10003, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.addDrop(Blocks.field_10430, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.field_12607));
		this.addDrop(
			Blocks.field_10375,
			LootTable.builder()
				.pool(
					addSurvivesExplosionCondition(
						Blocks.field_10375,
						LootPool.builder()
							.rolls(ConstantLootTableRange.create(1))
							.with(
								ItemEntry.builder(Blocks.field_10375)
									.method_421(
										BlockStatePropertyLootCondition.builder(Blocks.field_10375).properties(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false))
									)
							)
					)
				)
		);
		this.addDrop(
			Blocks.field_10302,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootTableRange.create(1))
							.with(
								(LootPoolEntry.Builder<?>)applyExplosionDecay(
									blockx,
									ItemEntry.builder(Items.field_8116)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.field_10476,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootTableRange.create(1))
							.with(
								(LootPoolEntry.Builder<?>)applyExplosionDecay(
									Blocks.field_10476,
									ItemEntry.builder(blockx)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(2))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2)))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3)))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(4))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4)))
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.field_17563,
			blockx -> LootTable.builder()
					.pool(LootPool.builder().with((LootPoolEntry.Builder<?>)applyExplosionDecay(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.pool(
						LootPool.builder()
							.with(ItemEntry.builder(Items.field_8324))
							.method_356(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.addDrop(Blocks.field_10327, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10333, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10034, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10200, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10228, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10485, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10181, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10312, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_10380, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16334, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16333, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16328, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16336, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16331, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16337, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16330, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16329, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16335, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.field_16332, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.field_16541, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.field_22110, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.field_10603, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10371, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10605, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10373, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10532, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10140, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10055, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10203, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10320, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10275, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10063, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10407, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10051, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10268, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10068, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10199, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10600, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.field_10062, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10281, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10602, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10165, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10185, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10198, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10452, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_9985, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10229, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10438, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10045, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10612, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10368, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10406, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10154, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.field_10547, BlockLootTableGenerator::bannerDrops);
		this.addDrop(
			Blocks.field_10432,
			blockx -> LootTable.builder()
					.pool(
						addSurvivesExplosionCondition(
							blockx,
							LootPool.builder()
								.rolls(ConstantLootTableRange.create(1))
								.with(
									ItemEntry.builder(blockx).method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.field_17027).withOperation("SkullOwner", "SkullOwner"))
								)
						)
					)
		);
		this.addDrop(Blocks.field_20421, BlockLootTableGenerator::beeNestDrops);
		this.addDrop(Blocks.field_20422, BlockLootTableGenerator::beehiveDrops);
		this.addDrop(Blocks.field_10539, blockx -> leavesDrop(blockx, Blocks.field_10575, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.field_10098, blockx -> leavesDrop(blockx, Blocks.field_10385, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.field_10335, blockx -> leavesDrop(blockx, Blocks.field_10276, JUNGLE_SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.field_9988, blockx -> leavesDrop(blockx, Blocks.field_10217, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.field_10503, blockx -> oakLeavesDrop(blockx, Blocks.field_10394, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.field_10035, blockx -> oakLeavesDrop(blockx, Blocks.field_10160, SAPLING_DROP_CHANCE));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.field_10341)
			.properties(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.addDrop(Blocks.field_10341, cropDrops(Blocks.field_10341, Items.field_8186, Items.field_8309, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.field_10293)
			.properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.addDrop(Blocks.field_10293, cropDrops(Blocks.field_10293, Items.field_8861, Items.field_8317, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.field_10609)
			.properties(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		this.addDrop(
			Blocks.field_10609,
			applyExplosionDecay(
				Blocks.field_10609,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.field_8179)))
					.pool(
						LootPool.builder()
							.method_356(builder3)
							.with(ItemEntry.builder(Items.field_8179).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
					)
			)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.field_10247)
			.properties(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.addDrop(
			Blocks.field_10247,
			applyExplosionDecay(
				Blocks.field_10247,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.field_8567)))
					.pool(
						LootPool.builder()
							.method_356(builder4)
							.with(ItemEntry.builder(Items.field_8567).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.field_9130, 0.5714286F, 3)))
					)
					.pool(LootPool.builder().method_356(builder4).with(ItemEntry.builder(Items.field_8635).method_421(RandomChanceLootCondition.builder(0.02F))))
			)
		);
		this.addDrop(
			Blocks.field_16999,
			blockx -> applyExplosionDecay(
					blockx,
					LootTable.builder()
						.pool(
							LootPool.builder()
								.method_356(
									BlockStatePropertyLootCondition.builder(Blocks.field_16999).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
								)
								.with(ItemEntry.builder(Items.field_16998))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
						.pool(
							LootPool.builder()
								.method_356(
									BlockStatePropertyLootCondition.builder(Blocks.field_16999).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
								)
								.with(ItemEntry.builder(Items.field_16998))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
						)
				)
		);
		this.addDrop(Blocks.field_10580, blockx -> mushroomBlockDrops(blockx, Blocks.field_10251));
		this.addDrop(Blocks.field_10240, blockx -> mushroomBlockDrops(blockx, Blocks.field_10559));
		this.addDrop(Blocks.field_10418, blockx -> oreDrops(blockx, Items.field_8713));
		this.addDrop(Blocks.field_10013, blockx -> oreDrops(blockx, Items.field_8687));
		this.addDrop(Blocks.field_10213, blockx -> oreDrops(blockx, Items.field_8155));
		this.addDrop(Blocks.field_10442, blockx -> oreDrops(blockx, Items.field_8477));
		this.addDrop(
			Blocks.field_23077,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8397)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F)))
							.method_438(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130))
					)
				)
		);
		this.addDrop(
			Blocks.field_10090,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8759)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F)))
							.method_438(ApplyBonusLootFunction.oreDrops(Enchantments.field_9130))
					)
				)
		);
		this.addDrop(
			Blocks.field_10343,
			blockx -> dropsWithSilkTouchOrShears(blockx, (LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(blockx, ItemEntry.builder(Items.field_8276)))
		);
		this.addDrop(
			Blocks.field_10428,
			blockx -> dropsWithShears(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx, ItemEntry.builder(Items.field_8600).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
					)
				)
		);
		this.addDrop(Blocks.field_22117, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.field_10376, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.field_10597, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.field_10238, method_30159(Blocks.field_10376));
		this.addDrop(Blocks.field_10313, blockx -> method_30158(blockx, Blocks.field_10112));
		this.addDrop(Blocks.field_10214, blockx -> method_30158(blockx, Blocks.field_10479));
		this.addDrop(Blocks.field_10168, blockx -> cropStemDrops(blockx, Items.field_8188));
		this.addDrop(Blocks.field_10150, blockx -> attachedCropStemDrops(blockx, Items.field_8188));
		this.addDrop(Blocks.field_9984, blockx -> cropStemDrops(blockx, Items.field_8706));
		this.addDrop(Blocks.field_10331, blockx -> attachedCropStemDrops(blockx, Items.field_8706));
		this.addDrop(
			Blocks.field_10528,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootTableRange.create(1))
							.with(
								((LeafEntry.Builder)addSurvivesExplosionCondition(blockx, ItemEntry.builder(blockx)))
									.method_421(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							)
					)
		);
		this.addDrop(Blocks.field_10112, BlockLootTableGenerator::grassDrops);
		this.addDrop(Blocks.field_10479, BlockLootTableGenerator::grassDrops);
		this.addDrop(
			Blocks.field_10171,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8601)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.addDrop(
			Blocks.field_10545,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8497)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.addDrop(
			Blocks.field_10080,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8725)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 5.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
					)
				)
		);
		this.addDrop(
			Blocks.field_10174,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.field_8434)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.addDrop(
			Blocks.field_9974,
			blockx -> LootTable.builder()
					.pool(
						applyExplosionDecay(
							blockx,
							LootPool.builder()
								.rolls(ConstantLootTableRange.create(1))
								.with(
									ItemEntry.builder(Items.field_8790)
										.method_438(
											SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.method_438(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.field_9130)
												.method_524(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.addDrop(
			Blocks.field_10477,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.method_356(EntityPropertiesLootCondition.create(LootContext.EntityTarget.field_935))
							.with(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6))),
											ItemEntry.builder(Items.field_8543)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7))),
											ItemEntry.builder(Items.field_8543).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))
										)
										.method_421(WITHOUT_SILK_TOUCH),
									AlternativeEntry.builder(
										ItemEntry.builder(Blocks.field_10477)
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))),
										ItemEntry.builder(Blocks.field_10477)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))),
										ItemEntry.builder(Blocks.field_10491)
									)
								)
							)
					)
		);
		this.addDrop(
			Blocks.field_10255,
			blockx -> dropsWithSilkTouch(
					blockx,
					addSurvivesExplosionCondition(
						blockx,
						ItemEntry.builder(Items.field_8145)
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(blockx))
					)
				)
		);
		this.addDrop(
			Blocks.field_17350,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(
						blockx, ItemEntry.builder(Items.field_8665).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
					)
				)
		);
		this.addDrop(
			Blocks.field_23880,
			blockx -> dropsWithSilkTouch(
					blockx,
					addSurvivesExplosionCondition(
						blockx,
						ItemEntry.builder(Items.field_8397)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F)))
							.method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(blockx))
					)
				)
		);
		this.addDrop(
			Blocks.field_23860,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(
						blockx, ItemEntry.builder(Items.SOUL_SOIL).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
					)
				)
		);
		this.addDropWithSilkTouch(Blocks.field_10033);
		this.addDropWithSilkTouch(Blocks.field_10087);
		this.addDropWithSilkTouch(Blocks.field_10227);
		this.addDropWithSilkTouch(Blocks.field_10574);
		this.addDropWithSilkTouch(Blocks.field_10271);
		this.addDropWithSilkTouch(Blocks.field_10049);
		this.addDropWithSilkTouch(Blocks.field_10157);
		this.addDropWithSilkTouch(Blocks.field_10317);
		this.addDropWithSilkTouch(Blocks.field_10555);
		this.addDropWithSilkTouch(Blocks.field_9996);
		this.addDropWithSilkTouch(Blocks.field_10248);
		this.addDropWithSilkTouch(Blocks.field_10399);
		this.addDropWithSilkTouch(Blocks.field_10060);
		this.addDropWithSilkTouch(Blocks.field_10073);
		this.addDropWithSilkTouch(Blocks.field_10357);
		this.addDropWithSilkTouch(Blocks.field_10272);
		this.addDropWithSilkTouch(Blocks.field_9997);
		this.addDropWithSilkTouch(Blocks.field_10285);
		this.addDropWithSilkTouch(Blocks.field_9991);
		this.addDropWithSilkTouch(Blocks.field_10496);
		this.addDropWithSilkTouch(Blocks.field_10469);
		this.addDropWithSilkTouch(Blocks.field_10193);
		this.addDropWithSilkTouch(Blocks.field_10578);
		this.addDropWithSilkTouch(Blocks.field_10305);
		this.addDropWithSilkTouch(Blocks.field_10565);
		this.addDropWithSilkTouch(Blocks.field_10077);
		this.addDropWithSilkTouch(Blocks.field_10129);
		this.addDropWithSilkTouch(Blocks.field_10355);
		this.addDropWithSilkTouch(Blocks.field_10152);
		this.addDropWithSilkTouch(Blocks.field_9982);
		this.addDropWithSilkTouch(Blocks.field_10163);
		this.addDropWithSilkTouch(Blocks.field_10419);
		this.addDropWithSilkTouch(Blocks.field_10118);
		this.addDropWithSilkTouch(Blocks.field_10070);
		this.addDropWithSilkTouch(Blocks.field_10295);
		this.addDropWithSilkTouch(Blocks.field_10225);
		this.addDropWithSilkTouch(Blocks.field_10384);
		this.addDropWithSilkTouch(Blocks.field_10195);
		this.addDropWithSilkTouch(Blocks.field_10556);
		this.addDropWithSilkTouch(Blocks.field_10082);
		this.addDropWithSilkTouch(Blocks.field_10572);
		this.addDropWithSilkTouch(Blocks.field_10296);
		this.addDropWithSilkTouch(Blocks.field_10579);
		this.addDropWithSilkTouch(Blocks.field_10032);
		this.addDropWithSilkTouch(Blocks.field_10125);
		this.addDropWithSilkTouch(Blocks.field_10339);
		this.addDropWithSilkTouch(Blocks.field_10134);
		this.addDropWithSilkTouch(Blocks.field_10618);
		this.addDropWithSilkTouch(Blocks.field_10169);
		this.addDropWithSilkTouch(Blocks.field_10448);
		this.addDropWithSilkTouch(Blocks.field_10097);
		this.addDropWithSilkTouch(Blocks.field_10047);
		this.addDropWithSilkTouch(Blocks.field_10568);
		this.addDropWithSilkTouch(Blocks.field_10221);
		this.addDropWithSilkTouch(Blocks.field_10053);
		this.addDropWithSilkTouch(Blocks.field_10079);
		this.addDropWithSilkTouch(Blocks.field_10427);
		this.addDropWithSilkTouch(Blocks.field_10551);
		this.addDropWithSilkTouch(Blocks.field_10005);
		this.addDropWithSilkTouch(Blocks.field_10277, Blocks.field_10340);
		this.addDropWithSilkTouch(Blocks.field_10492, Blocks.field_10445);
		this.addDropWithSilkTouch(Blocks.field_10387, Blocks.field_10056);
		this.addDropWithSilkTouch(Blocks.field_10480, Blocks.field_10065);
		this.addDropWithSilkTouch(Blocks.field_10100, Blocks.field_10416);
		this.addDropWithSilkTouch(Blocks.field_10176, Blocks.field_10552);
		this.addVinePlantDrop(Blocks.field_22123, Blocks.field_22124);
		this.addVinePlantDrop(Blocks.field_23078, Blocks.field_23079);
		this.addDrop(Blocks.field_10183, dropsNothing());
		this.addDrop(Blocks.field_10110, dropsNothing());
		this.addDrop(Blocks.field_10260, dropsNothing());
		this.addDrop(Blocks.field_10036, dropsNothing());
		this.addDrop(Blocks.field_22089, dropsNothing());
		this.addDrop(Blocks.field_10316, dropsNothing());
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (Block block : Registry.BLOCK) {
			Identifier identifier = block.getLootTableId();
			if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder5 = (LootTable.Builder)this.lootTables.remove(identifier);
				if (builder5 == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.BLOCK.getId(block)));
				}

				biConsumer.accept(identifier, builder5);
			}
		}

		if (!this.lootTables.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
		}
	}

	private void addVinePlantDrop(Block block, Block drop) {
		LootTable.Builder builder = dropsWithSilkTouchOrShears(
			block, ItemEntry.builder(block).method_421(TableBonusLootCondition.builder(Enchantments.field_9130, 0.33F, 0.55F, 0.77F, 1.0F))
		);
		this.addDrop(block, builder);
		this.addDrop(drop, builder);
	}

	public static LootTable.Builder addDoorDrop(Block block) {
		return dropsWithProperty(block, DoorBlock.HALF, DoubleBlockHalf.field_12607);
	}

	public void addPottedPlantDrop(Block block) {
		this.addDrop(block, blockx -> pottedPlantDrops(((FlowerPotBlock)blockx).getContent()));
	}

	public void addDropWithSilkTouch(Block block, Block drop) {
		this.addDrop(block, dropsWithSilkTouch(drop));
	}

	public void addDrop(Block block, ItemConvertible drop) {
		this.addDrop(block, drops(drop));
	}

	public void addDropWithSilkTouch(Block block) {
		this.addDropWithSilkTouch(block, block);
	}

	public void addDrop(Block block) {
		this.addDrop(block, block);
	}

	private void addDrop(Block block, Function<Block, LootTable.Builder> function) {
		this.addDrop(block, (LootTable.Builder)function.apply(block));
	}

	private void addDrop(Block block, LootTable.Builder lootTable) {
		this.lootTables.put(block.getLootTableId(), lootTable);
	}
}
