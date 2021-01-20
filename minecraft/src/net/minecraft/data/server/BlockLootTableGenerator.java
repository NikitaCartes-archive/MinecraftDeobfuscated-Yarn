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
import net.minecraft.block.CandleBlock;
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
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
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
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
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
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder WITHOUT_SILK_TOUCH = WITH_SILK_TOUCH.invert();
	private static final LootCondition.Builder WITH_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
	private static final LootCondition.Builder WITH_SILK_TOUCH_OR_SHEARS = WITH_SHEARS.or(WITH_SILK_TOUCH);
	private static final LootCondition.Builder WITHOUT_SILK_TOUCH_NOR_SHEARS = WITH_SILK_TOUCH_OR_SHEARS.invert();
	private static final Set<Item> EXPLOSION_IMMUNE = (Set<Item>)Stream.of(
			Blocks.DRAGON_EGG,
			Blocks.BEACON,
			Blocks.CONDUIT,
			Blocks.SKELETON_SKULL,
			Blocks.WITHER_SKELETON_SKULL,
			Blocks.PLAYER_HEAD,
			Blocks.ZOMBIE_HEAD,
			Blocks.CREEPER_HEAD,
			Blocks.DRAGON_HEAD,
			Blocks.SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.LIGHT_GRAY_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX
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
			.pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop))));
	}

	private static LootTable.Builder drops(Block drop, LootCondition.Builder conditionBuilder, LootPoolEntry.Builder<?> child) {
		return LootTable.builder()
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop).conditionally(conditionBuilder).alternatively(child)));
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

	private static LootTable.Builder drops(ItemConvertible drop, LootNumberProvider count) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with((LootPoolEntry.Builder<?>)applyExplosionDecay(drop, ItemEntry.builder(drop).apply(SetCountLootFunction.builder(count))))
			);
	}

	private static LootTable.Builder drops(Block dropWithSilkTouch, ItemConvertible drop, LootNumberProvider count) {
		return dropsWithSilkTouch(
			dropWithSilkTouch, (LootPoolEntry.Builder<?>)applyExplosionDecay(dropWithSilkTouch, ItemEntry.builder(drop).apply(SetCountLootFunction.builder(count)))
		);
	}

	private static LootTable.Builder dropsWithSilkTouch(ItemConvertible drop) {
		return LootTable.builder()
			.pool(LootPool.builder().conditionally(WITH_SILK_TOUCH).rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop)));
	}

	private static LootTable.Builder pottedPlantDrops(ItemConvertible plant) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Blocks.FLOWER_POT))
				)
			)
			.pool(addSurvivesExplosionCondition(plant, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(plant))));
	}

	private static LootTable.Builder slabDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						(LootPoolEntry.Builder<?>)applyExplosionDecay(
							drop,
							ItemEntry.builder(drop)
								.apply(
									SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
										.conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))
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
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, comparable)))
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
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(drop).apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))
				)
			);
	}

	private static LootTable.Builder shulkerBoxDrops(Block drop) {
		return LootTable.builder()
			.pool(
				addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.apply(
									CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
										.withOperation("Lock", "BlockEntityTag.Lock")
										.withOperation("LootTable", "BlockEntityTag.LootTable")
										.withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.apply(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))
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
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootTable.Builder beeNestDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.conditionally(WITH_SILK_TOUCH)
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(drop)
							.apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.apply(CopyStateFunction.getBuilder(drop).addProperty(BeehiveBlock.HONEY_LEVEL))
					)
			);
	}

	private static LootTable.Builder beehiveDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(drop)
							.conditionally(WITH_SILK_TOUCH)
							.apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.apply(CopyStateFunction.getBuilder(drop).addProperty(BeehiveBlock.HONEY_LEVEL))
							.alternatively(ItemEntry.builder(drop))
					)
			);
	}

	private static LootTable.Builder oreDrops(Block dropWithSilkTouch, Item drop) {
		return dropsWithSilkTouch(
			dropWithSilkTouch,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(dropWithSilkTouch, ItemEntry.builder(drop).apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))
		);
	}

	private static LootTable.Builder mushroomBlockDrops(Block dropWithSilkTouch, ItemConvertible drop) {
		return dropsWithSilkTouch(
			dropWithSilkTouch,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(
				dropWithSilkTouch,
				ItemEntry.builder(drop)
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-6.0F, 2.0F)))
					.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	private static LootTable.Builder grassDrops(Block dropWithShears) {
		return dropsWithShears(
			dropWithShears,
			(LootPoolEntry.Builder<?>)applyExplosionDecay(
				dropWithShears,
				ItemEntry.builder(Items.WHEAT_SEEDS)
					.conditionally(RandomChanceLootCondition.builder(0.125F))
					.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
			)
		);
	}

	private static LootTable.Builder cropStemDrops(Block stem, Item drop) {
		return LootTable.builder()
			.pool(
				applyExplosionDecay(
					stem,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.06666667F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.13333334F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.2F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.26666668F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.33333334F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.4F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.46666667F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))
								)
								.apply(
									SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.53333336F))
										.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))
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
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(drop).apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.53333336F))))
				)
			);
	}

	private static LootTable.Builder dropsWithShears(ItemConvertible drop) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).conditionally(WITH_SHEARS).with(ItemEntry.builder(drop)));
	}

	private static LootTable.Builder leavesDrop(Block leaves, Block drop, float... chance) {
		return dropsWithSilkTouchOrShears(
				leaves,
				((LeafEntry.Builder)addSurvivesExplosionCondition(leaves, ItemEntry.builder(drop)))
					.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, chance))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS)
					.with(
						((LeafEntry.Builder)applyExplosionDecay(
								leaves, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
							))
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootTable.Builder oakLeavesDrop(Block leaves, Block drop, float... chance) {
		return leavesDrop(leaves, drop, chance)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS)
					.with(
						((LeafEntry.Builder)addSurvivesExplosionCondition(leaves, ItemEntry.builder(Items.APPLE)))
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootTable.Builder cropDrops(Block crop, Item product, Item seeds, LootCondition.Builder condition) {
		return applyExplosionDecay(
			crop,
			LootTable.builder()
				.pool(LootPool.builder().with(ItemEntry.builder(product).conditionally(condition).alternatively(ItemEntry.builder(seeds))))
				.pool(
					LootPool.builder()
						.conditionally(condition)
						.with(ItemEntry.builder(seeds).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
				)
		);
	}

	private static LootTable.Builder seagrassDrops(Block seagrass) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.conditionally(WITH_SHEARS)
					.with(ItemEntry.builder(seagrass).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	private static LootTable.Builder tallGrassDrops(Block tallGrass, Block grass) {
		LootPoolEntry.Builder<?> builder = ItemEntry.builder(grass)
			.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
			.conditionally(WITH_SHEARS)
			.alternatively(
				((LeafEntry.Builder)addSurvivesExplosionCondition(tallGrass, ItemEntry.builder(Items.WHEAT_SEEDS)))
					.conditionally(RandomChanceLootCondition.builder(0.125F))
			);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(builder)
					.conditionally(
						BlockStatePropertyLootCondition.builder(tallGrass).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
					)
					.conditionally(
						LocationCheckLootCondition.builder(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create()
										.block(tallGrass)
										.state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.UPPER).build())
										.build()
								),
							new BlockPos(0, 1, 0)
						)
					)
			)
			.pool(
				LootPool.builder()
					.with(builder)
					.conditionally(
						BlockStatePropertyLootCondition.builder(tallGrass).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.UPPER))
					)
					.conditionally(
						LocationCheckLootCondition.builder(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create()
										.block(tallGrass)
										.state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER).build())
										.build()
								),
							new BlockPos(0, -1, 0)
						)
					)
			);
	}

	private static LootTable.Builder candleDrops(Block candle) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						(LootPoolEntry.Builder<?>)applyExplosionDecay(
							candle,
							ItemEntry.builder(candle)
								.apply(
									SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
										.conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 2)))
								)
								.apply(
									SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
										.conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 3)))
								)
								.apply(
									SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F))
										.conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 4)))
								)
						)
					)
			);
	}

	private static LootTable.Builder candleCakeDrops(Block candle) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(candle)));
	}

	public static LootTable.Builder dropsNothing() {
		return LootTable.builder();
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.addDrop(Blocks.GRANITE);
		this.addDrop(Blocks.POLISHED_GRANITE);
		this.addDrop(Blocks.DIORITE);
		this.addDrop(Blocks.POLISHED_DIORITE);
		this.addDrop(Blocks.ANDESITE);
		this.addDrop(Blocks.POLISHED_ANDESITE);
		this.addDrop(Blocks.DIRT);
		this.addDrop(Blocks.COARSE_DIRT);
		this.addDrop(Blocks.COBBLESTONE);
		this.addDrop(Blocks.OAK_PLANKS);
		this.addDrop(Blocks.SPRUCE_PLANKS);
		this.addDrop(Blocks.BIRCH_PLANKS);
		this.addDrop(Blocks.JUNGLE_PLANKS);
		this.addDrop(Blocks.ACACIA_PLANKS);
		this.addDrop(Blocks.DARK_OAK_PLANKS);
		this.addDrop(Blocks.OAK_SAPLING);
		this.addDrop(Blocks.SPRUCE_SAPLING);
		this.addDrop(Blocks.BIRCH_SAPLING);
		this.addDrop(Blocks.JUNGLE_SAPLING);
		this.addDrop(Blocks.ACACIA_SAPLING);
		this.addDrop(Blocks.DARK_OAK_SAPLING);
		this.addDrop(Blocks.SAND);
		this.addDrop(Blocks.RED_SAND);
		this.addDrop(Blocks.GOLD_ORE);
		this.addDrop(Blocks.IRON_ORE);
		this.addDrop(Blocks.OAK_LOG);
		this.addDrop(Blocks.SPRUCE_LOG);
		this.addDrop(Blocks.BIRCH_LOG);
		this.addDrop(Blocks.JUNGLE_LOG);
		this.addDrop(Blocks.ACACIA_LOG);
		this.addDrop(Blocks.DARK_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_SPRUCE_LOG);
		this.addDrop(Blocks.STRIPPED_BIRCH_LOG);
		this.addDrop(Blocks.STRIPPED_JUNGLE_LOG);
		this.addDrop(Blocks.STRIPPED_ACACIA_LOG);
		this.addDrop(Blocks.STRIPPED_DARK_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_WARPED_STEM);
		this.addDrop(Blocks.STRIPPED_CRIMSON_STEM);
		this.addDrop(Blocks.OAK_WOOD);
		this.addDrop(Blocks.SPRUCE_WOOD);
		this.addDrop(Blocks.BIRCH_WOOD);
		this.addDrop(Blocks.JUNGLE_WOOD);
		this.addDrop(Blocks.ACACIA_WOOD);
		this.addDrop(Blocks.DARK_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_SPRUCE_WOOD);
		this.addDrop(Blocks.STRIPPED_BIRCH_WOOD);
		this.addDrop(Blocks.STRIPPED_JUNGLE_WOOD);
		this.addDrop(Blocks.STRIPPED_ACACIA_WOOD);
		this.addDrop(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_CRIMSON_HYPHAE);
		this.addDrop(Blocks.STRIPPED_WARPED_HYPHAE);
		this.addDrop(Blocks.SPONGE);
		this.addDrop(Blocks.WET_SPONGE);
		this.addDrop(Blocks.LAPIS_BLOCK);
		this.addDrop(Blocks.SANDSTONE);
		this.addDrop(Blocks.CHISELED_SANDSTONE);
		this.addDrop(Blocks.CUT_SANDSTONE);
		this.addDrop(Blocks.NOTE_BLOCK);
		this.addDrop(Blocks.POWERED_RAIL);
		this.addDrop(Blocks.DETECTOR_RAIL);
		this.addDrop(Blocks.STICKY_PISTON);
		this.addDrop(Blocks.PISTON);
		this.addDrop(Blocks.WHITE_WOOL);
		this.addDrop(Blocks.ORANGE_WOOL);
		this.addDrop(Blocks.MAGENTA_WOOL);
		this.addDrop(Blocks.LIGHT_BLUE_WOOL);
		this.addDrop(Blocks.YELLOW_WOOL);
		this.addDrop(Blocks.LIME_WOOL);
		this.addDrop(Blocks.PINK_WOOL);
		this.addDrop(Blocks.GRAY_WOOL);
		this.addDrop(Blocks.LIGHT_GRAY_WOOL);
		this.addDrop(Blocks.CYAN_WOOL);
		this.addDrop(Blocks.PURPLE_WOOL);
		this.addDrop(Blocks.BLUE_WOOL);
		this.addDrop(Blocks.BROWN_WOOL);
		this.addDrop(Blocks.GREEN_WOOL);
		this.addDrop(Blocks.RED_WOOL);
		this.addDrop(Blocks.BLACK_WOOL);
		this.addDrop(Blocks.DANDELION);
		this.addDrop(Blocks.POPPY);
		this.addDrop(Blocks.BLUE_ORCHID);
		this.addDrop(Blocks.ALLIUM);
		this.addDrop(Blocks.AZURE_BLUET);
		this.addDrop(Blocks.RED_TULIP);
		this.addDrop(Blocks.ORANGE_TULIP);
		this.addDrop(Blocks.WHITE_TULIP);
		this.addDrop(Blocks.PINK_TULIP);
		this.addDrop(Blocks.OXEYE_DAISY);
		this.addDrop(Blocks.CORNFLOWER);
		this.addDrop(Blocks.WITHER_ROSE);
		this.addDrop(Blocks.LILY_OF_THE_VALLEY);
		this.addDrop(Blocks.BROWN_MUSHROOM);
		this.addDrop(Blocks.RED_MUSHROOM);
		this.addDrop(Blocks.GOLD_BLOCK);
		this.addDrop(Blocks.IRON_BLOCK);
		this.addDrop(Blocks.BRICKS);
		this.addDrop(Blocks.MOSSY_COBBLESTONE);
		this.addDrop(Blocks.OBSIDIAN);
		this.addDrop(Blocks.CRYING_OBSIDIAN);
		this.addDrop(Blocks.TORCH);
		this.addDrop(Blocks.OAK_STAIRS);
		this.addDrop(Blocks.REDSTONE_WIRE);
		this.addDrop(Blocks.DIAMOND_BLOCK);
		this.addDrop(Blocks.CRAFTING_TABLE);
		this.addDrop(Blocks.OAK_SIGN);
		this.addDrop(Blocks.SPRUCE_SIGN);
		this.addDrop(Blocks.BIRCH_SIGN);
		this.addDrop(Blocks.ACACIA_SIGN);
		this.addDrop(Blocks.JUNGLE_SIGN);
		this.addDrop(Blocks.DARK_OAK_SIGN);
		this.addDrop(Blocks.LADDER);
		this.addDrop(Blocks.RAIL);
		this.addDrop(Blocks.COBBLESTONE_STAIRS);
		this.addDrop(Blocks.LEVER);
		this.addDrop(Blocks.STONE_PRESSURE_PLATE);
		this.addDrop(Blocks.OAK_PRESSURE_PLATE);
		this.addDrop(Blocks.SPRUCE_PRESSURE_PLATE);
		this.addDrop(Blocks.BIRCH_PRESSURE_PLATE);
		this.addDrop(Blocks.JUNGLE_PRESSURE_PLATE);
		this.addDrop(Blocks.ACACIA_PRESSURE_PLATE);
		this.addDrop(Blocks.DARK_OAK_PRESSURE_PLATE);
		this.addDrop(Blocks.REDSTONE_TORCH);
		this.addDrop(Blocks.STONE_BUTTON);
		this.addDrop(Blocks.CACTUS);
		this.addDrop(Blocks.SUGAR_CANE);
		this.addDrop(Blocks.JUKEBOX);
		this.addDrop(Blocks.OAK_FENCE);
		this.addDrop(Blocks.PUMPKIN);
		this.addDrop(Blocks.NETHERRACK);
		this.addDrop(Blocks.SOUL_SAND);
		this.addDrop(Blocks.SOUL_SOIL);
		this.addDrop(Blocks.BASALT);
		this.addDrop(Blocks.POLISHED_BASALT);
		this.addDrop(Blocks.SOUL_TORCH);
		this.addDrop(Blocks.CARVED_PUMPKIN);
		this.addDrop(Blocks.JACK_O_LANTERN);
		this.addDrop(Blocks.REPEATER);
		this.addDrop(Blocks.OAK_TRAPDOOR);
		this.addDrop(Blocks.SPRUCE_TRAPDOOR);
		this.addDrop(Blocks.BIRCH_TRAPDOOR);
		this.addDrop(Blocks.JUNGLE_TRAPDOOR);
		this.addDrop(Blocks.ACACIA_TRAPDOOR);
		this.addDrop(Blocks.DARK_OAK_TRAPDOOR);
		this.addDrop(Blocks.STONE_BRICKS);
		this.addDrop(Blocks.MOSSY_STONE_BRICKS);
		this.addDrop(Blocks.CRACKED_STONE_BRICKS);
		this.addDrop(Blocks.CHISELED_STONE_BRICKS);
		this.addDrop(Blocks.IRON_BARS);
		this.addDrop(Blocks.OAK_FENCE_GATE);
		this.addDrop(Blocks.BRICK_STAIRS);
		this.addDrop(Blocks.STONE_BRICK_STAIRS);
		this.addDrop(Blocks.LILY_PAD);
		this.addDrop(Blocks.NETHER_BRICKS);
		this.addDrop(Blocks.NETHER_BRICK_FENCE);
		this.addDrop(Blocks.NETHER_BRICK_STAIRS);
		this.addDrop(Blocks.CAULDRON);
		this.addDrop(Blocks.END_STONE);
		this.addDrop(Blocks.REDSTONE_LAMP);
		this.addDrop(Blocks.SANDSTONE_STAIRS);
		this.addDrop(Blocks.TRIPWIRE_HOOK);
		this.addDrop(Blocks.EMERALD_BLOCK);
		this.addDrop(Blocks.SPRUCE_STAIRS);
		this.addDrop(Blocks.BIRCH_STAIRS);
		this.addDrop(Blocks.JUNGLE_STAIRS);
		this.addDrop(Blocks.COBBLESTONE_WALL);
		this.addDrop(Blocks.MOSSY_COBBLESTONE_WALL);
		this.addDrop(Blocks.FLOWER_POT);
		this.addDrop(Blocks.OAK_BUTTON);
		this.addDrop(Blocks.SPRUCE_BUTTON);
		this.addDrop(Blocks.BIRCH_BUTTON);
		this.addDrop(Blocks.JUNGLE_BUTTON);
		this.addDrop(Blocks.ACACIA_BUTTON);
		this.addDrop(Blocks.DARK_OAK_BUTTON);
		this.addDrop(Blocks.SKELETON_SKULL);
		this.addDrop(Blocks.WITHER_SKELETON_SKULL);
		this.addDrop(Blocks.ZOMBIE_HEAD);
		this.addDrop(Blocks.CREEPER_HEAD);
		this.addDrop(Blocks.DRAGON_HEAD);
		this.addDrop(Blocks.ANVIL);
		this.addDrop(Blocks.CHIPPED_ANVIL);
		this.addDrop(Blocks.DAMAGED_ANVIL);
		this.addDrop(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
		this.addDrop(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		this.addDrop(Blocks.COMPARATOR);
		this.addDrop(Blocks.DAYLIGHT_DETECTOR);
		this.addDrop(Blocks.REDSTONE_BLOCK);
		this.addDrop(Blocks.QUARTZ_BLOCK);
		this.addDrop(Blocks.CHISELED_QUARTZ_BLOCK);
		this.addDrop(Blocks.QUARTZ_PILLAR);
		this.addDrop(Blocks.QUARTZ_STAIRS);
		this.addDrop(Blocks.ACTIVATOR_RAIL);
		this.addDrop(Blocks.WHITE_TERRACOTTA);
		this.addDrop(Blocks.ORANGE_TERRACOTTA);
		this.addDrop(Blocks.MAGENTA_TERRACOTTA);
		this.addDrop(Blocks.LIGHT_BLUE_TERRACOTTA);
		this.addDrop(Blocks.YELLOW_TERRACOTTA);
		this.addDrop(Blocks.LIME_TERRACOTTA);
		this.addDrop(Blocks.PINK_TERRACOTTA);
		this.addDrop(Blocks.GRAY_TERRACOTTA);
		this.addDrop(Blocks.LIGHT_GRAY_TERRACOTTA);
		this.addDrop(Blocks.CYAN_TERRACOTTA);
		this.addDrop(Blocks.PURPLE_TERRACOTTA);
		this.addDrop(Blocks.BLUE_TERRACOTTA);
		this.addDrop(Blocks.BROWN_TERRACOTTA);
		this.addDrop(Blocks.GREEN_TERRACOTTA);
		this.addDrop(Blocks.RED_TERRACOTTA);
		this.addDrop(Blocks.BLACK_TERRACOTTA);
		this.addDrop(Blocks.ACACIA_STAIRS);
		this.addDrop(Blocks.DARK_OAK_STAIRS);
		this.addDrop(Blocks.SLIME_BLOCK);
		this.addDrop(Blocks.IRON_TRAPDOOR);
		this.addDrop(Blocks.PRISMARINE);
		this.addDrop(Blocks.PRISMARINE_BRICKS);
		this.addDrop(Blocks.DARK_PRISMARINE);
		this.addDrop(Blocks.PRISMARINE_STAIRS);
		this.addDrop(Blocks.PRISMARINE_BRICK_STAIRS);
		this.addDrop(Blocks.DARK_PRISMARINE_STAIRS);
		this.addDrop(Blocks.HAY_BLOCK);
		this.addDrop(Blocks.WHITE_CARPET);
		this.addDrop(Blocks.ORANGE_CARPET);
		this.addDrop(Blocks.MAGENTA_CARPET);
		this.addDrop(Blocks.LIGHT_BLUE_CARPET);
		this.addDrop(Blocks.YELLOW_CARPET);
		this.addDrop(Blocks.LIME_CARPET);
		this.addDrop(Blocks.PINK_CARPET);
		this.addDrop(Blocks.GRAY_CARPET);
		this.addDrop(Blocks.LIGHT_GRAY_CARPET);
		this.addDrop(Blocks.CYAN_CARPET);
		this.addDrop(Blocks.PURPLE_CARPET);
		this.addDrop(Blocks.BLUE_CARPET);
		this.addDrop(Blocks.BROWN_CARPET);
		this.addDrop(Blocks.GREEN_CARPET);
		this.addDrop(Blocks.RED_CARPET);
		this.addDrop(Blocks.BLACK_CARPET);
		this.addDrop(Blocks.TERRACOTTA);
		this.addDrop(Blocks.COAL_BLOCK);
		this.addDrop(Blocks.RED_SANDSTONE);
		this.addDrop(Blocks.CHISELED_RED_SANDSTONE);
		this.addDrop(Blocks.CUT_RED_SANDSTONE);
		this.addDrop(Blocks.RED_SANDSTONE_STAIRS);
		this.addDrop(Blocks.SMOOTH_STONE);
		this.addDrop(Blocks.SMOOTH_SANDSTONE);
		this.addDrop(Blocks.SMOOTH_QUARTZ);
		this.addDrop(Blocks.SMOOTH_RED_SANDSTONE);
		this.addDrop(Blocks.SPRUCE_FENCE_GATE);
		this.addDrop(Blocks.BIRCH_FENCE_GATE);
		this.addDrop(Blocks.JUNGLE_FENCE_GATE);
		this.addDrop(Blocks.ACACIA_FENCE_GATE);
		this.addDrop(Blocks.DARK_OAK_FENCE_GATE);
		this.addDrop(Blocks.SPRUCE_FENCE);
		this.addDrop(Blocks.BIRCH_FENCE);
		this.addDrop(Blocks.JUNGLE_FENCE);
		this.addDrop(Blocks.ACACIA_FENCE);
		this.addDrop(Blocks.DARK_OAK_FENCE);
		this.addDrop(Blocks.END_ROD);
		this.addDrop(Blocks.PURPUR_BLOCK);
		this.addDrop(Blocks.PURPUR_PILLAR);
		this.addDrop(Blocks.PURPUR_STAIRS);
		this.addDrop(Blocks.END_STONE_BRICKS);
		this.addDrop(Blocks.MAGMA_BLOCK);
		this.addDrop(Blocks.NETHER_WART_BLOCK);
		this.addDrop(Blocks.RED_NETHER_BRICKS);
		this.addDrop(Blocks.BONE_BLOCK);
		this.addDrop(Blocks.OBSERVER);
		this.addDrop(Blocks.TARGET);
		this.addDrop(Blocks.WHITE_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.ORANGE_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.MAGENTA_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.YELLOW_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.LIME_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.PINK_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.GRAY_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.CYAN_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.PURPLE_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.BLUE_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.BROWN_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.GREEN_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.RED_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.BLACK_GLAZED_TERRACOTTA);
		this.addDrop(Blocks.WHITE_CONCRETE);
		this.addDrop(Blocks.ORANGE_CONCRETE);
		this.addDrop(Blocks.MAGENTA_CONCRETE);
		this.addDrop(Blocks.LIGHT_BLUE_CONCRETE);
		this.addDrop(Blocks.YELLOW_CONCRETE);
		this.addDrop(Blocks.LIME_CONCRETE);
		this.addDrop(Blocks.PINK_CONCRETE);
		this.addDrop(Blocks.GRAY_CONCRETE);
		this.addDrop(Blocks.LIGHT_GRAY_CONCRETE);
		this.addDrop(Blocks.CYAN_CONCRETE);
		this.addDrop(Blocks.PURPLE_CONCRETE);
		this.addDrop(Blocks.BLUE_CONCRETE);
		this.addDrop(Blocks.BROWN_CONCRETE);
		this.addDrop(Blocks.GREEN_CONCRETE);
		this.addDrop(Blocks.RED_CONCRETE);
		this.addDrop(Blocks.BLACK_CONCRETE);
		this.addDrop(Blocks.WHITE_CONCRETE_POWDER);
		this.addDrop(Blocks.ORANGE_CONCRETE_POWDER);
		this.addDrop(Blocks.MAGENTA_CONCRETE_POWDER);
		this.addDrop(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
		this.addDrop(Blocks.YELLOW_CONCRETE_POWDER);
		this.addDrop(Blocks.LIME_CONCRETE_POWDER);
		this.addDrop(Blocks.PINK_CONCRETE_POWDER);
		this.addDrop(Blocks.GRAY_CONCRETE_POWDER);
		this.addDrop(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
		this.addDrop(Blocks.CYAN_CONCRETE_POWDER);
		this.addDrop(Blocks.PURPLE_CONCRETE_POWDER);
		this.addDrop(Blocks.BLUE_CONCRETE_POWDER);
		this.addDrop(Blocks.BROWN_CONCRETE_POWDER);
		this.addDrop(Blocks.GREEN_CONCRETE_POWDER);
		this.addDrop(Blocks.RED_CONCRETE_POWDER);
		this.addDrop(Blocks.BLACK_CONCRETE_POWDER);
		this.addDrop(Blocks.KELP);
		this.addDrop(Blocks.DRIED_KELP_BLOCK);
		this.addDrop(Blocks.DEAD_TUBE_CORAL_BLOCK);
		this.addDrop(Blocks.DEAD_BRAIN_CORAL_BLOCK);
		this.addDrop(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
		this.addDrop(Blocks.DEAD_FIRE_CORAL_BLOCK);
		this.addDrop(Blocks.DEAD_HORN_CORAL_BLOCK);
		this.addDrop(Blocks.CONDUIT);
		this.addDrop(Blocks.DRAGON_EGG);
		this.addDrop(Blocks.BAMBOO);
		this.addDrop(Blocks.POLISHED_GRANITE_STAIRS);
		this.addDrop(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
		this.addDrop(Blocks.MOSSY_STONE_BRICK_STAIRS);
		this.addDrop(Blocks.POLISHED_DIORITE_STAIRS);
		this.addDrop(Blocks.MOSSY_COBBLESTONE_STAIRS);
		this.addDrop(Blocks.END_STONE_BRICK_STAIRS);
		this.addDrop(Blocks.STONE_STAIRS);
		this.addDrop(Blocks.SMOOTH_SANDSTONE_STAIRS);
		this.addDrop(Blocks.SMOOTH_QUARTZ_STAIRS);
		this.addDrop(Blocks.GRANITE_STAIRS);
		this.addDrop(Blocks.ANDESITE_STAIRS);
		this.addDrop(Blocks.RED_NETHER_BRICK_STAIRS);
		this.addDrop(Blocks.POLISHED_ANDESITE_STAIRS);
		this.addDrop(Blocks.DIORITE_STAIRS);
		this.addDrop(Blocks.BRICK_WALL);
		this.addDrop(Blocks.PRISMARINE_WALL);
		this.addDrop(Blocks.RED_SANDSTONE_WALL);
		this.addDrop(Blocks.MOSSY_STONE_BRICK_WALL);
		this.addDrop(Blocks.GRANITE_WALL);
		this.addDrop(Blocks.STONE_BRICK_WALL);
		this.addDrop(Blocks.NETHER_BRICK_WALL);
		this.addDrop(Blocks.ANDESITE_WALL);
		this.addDrop(Blocks.RED_NETHER_BRICK_WALL);
		this.addDrop(Blocks.SANDSTONE_WALL);
		this.addDrop(Blocks.END_STONE_BRICK_WALL);
		this.addDrop(Blocks.DIORITE_WALL);
		this.addDrop(Blocks.LOOM);
		this.addDrop(Blocks.SCAFFOLDING);
		this.addDrop(Blocks.HONEY_BLOCK);
		this.addDrop(Blocks.HONEYCOMB_BLOCK);
		this.addDrop(Blocks.RESPAWN_ANCHOR);
		this.addDrop(Blocks.LODESTONE);
		this.addDrop(Blocks.WARPED_STEM);
		this.addDrop(Blocks.WARPED_HYPHAE);
		this.addDrop(Blocks.WARPED_FUNGUS);
		this.addDrop(Blocks.WARPED_WART_BLOCK);
		this.addDrop(Blocks.CRIMSON_STEM);
		this.addDrop(Blocks.CRIMSON_HYPHAE);
		this.addDrop(Blocks.CRIMSON_FUNGUS);
		this.addDrop(Blocks.SHROOMLIGHT);
		this.addDrop(Blocks.CRIMSON_PLANKS);
		this.addDrop(Blocks.WARPED_PLANKS);
		this.addDrop(Blocks.WARPED_PRESSURE_PLATE);
		this.addDrop(Blocks.WARPED_FENCE);
		this.addDrop(Blocks.WARPED_TRAPDOOR);
		this.addDrop(Blocks.WARPED_FENCE_GATE);
		this.addDrop(Blocks.WARPED_STAIRS);
		this.addDrop(Blocks.WARPED_BUTTON);
		this.addDrop(Blocks.WARPED_SIGN);
		this.addDrop(Blocks.CRIMSON_PRESSURE_PLATE);
		this.addDrop(Blocks.CRIMSON_FENCE);
		this.addDrop(Blocks.CRIMSON_TRAPDOOR);
		this.addDrop(Blocks.CRIMSON_FENCE_GATE);
		this.addDrop(Blocks.CRIMSON_STAIRS);
		this.addDrop(Blocks.CRIMSON_BUTTON);
		this.addDrop(Blocks.CRIMSON_SIGN);
		this.addDrop(Blocks.NETHERITE_BLOCK);
		this.addDrop(Blocks.ANCIENT_DEBRIS);
		this.addDrop(Blocks.BLACKSTONE);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICKS);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
		this.addDrop(Blocks.BLACKSTONE_STAIRS);
		this.addDrop(Blocks.BLACKSTONE_WALL);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
		this.addDrop(Blocks.CHISELED_POLISHED_BLACKSTONE);
		this.addDrop(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
		this.addDrop(Blocks.POLISHED_BLACKSTONE);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_STAIRS);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BUTTON);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_WALL);
		this.addDrop(Blocks.CHISELED_NETHER_BRICKS);
		this.addDrop(Blocks.CRACKED_NETHER_BRICKS);
		this.addDrop(Blocks.QUARTZ_BRICKS);
		this.addDrop(Blocks.CHAIN);
		this.addDrop(Blocks.WARPED_ROOTS);
		this.addDrop(Blocks.CRIMSON_ROOTS);
		this.addDrop(Blocks.AMETHYST_BLOCK);
		this.addDrop(Blocks.CALCITE);
		this.addDrop(Blocks.TUFF);
		this.addDrop(Blocks.TINTED_GLASS);
		this.addDrop(Blocks.SCULK_SENSOR);
		this.addDrop(Blocks.COPPER_BLOCK);
		this.addDrop(Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK);
		this.addDrop(Blocks.SEMI_WEATHERED_COPPER_BLOCK);
		this.addDrop(Blocks.WEATHERED_COPPER_BLOCK);
		this.addDrop(Blocks.COPPER_ORE);
		this.addDrop(Blocks.CUT_COPPER);
		this.addDrop(Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.SEMI_WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_COPPER);
		this.addDrop(Blocks.WAXED_SEMI_WEATHERED_COPPER);
		this.addDrop(Blocks.WAXED_LIGHTLY_WEATHERED_COPPER);
		this.addDrop(Blocks.WAXED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.CUT_COPPER_STAIRS);
		this.addDrop(Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.LIGHTNING_ROD);
		this.addDrop(Blocks.POINTED_DRIPSTONE);
		this.addDrop(Blocks.DRIPSTONE_BLOCK);
		this.addDrop(Blocks.FARMLAND, Blocks.DIRT);
		this.addDrop(Blocks.TRIPWIRE, Items.STRING);
		this.addDrop(Blocks.DIRT_PATH, Blocks.DIRT);
		this.addDrop(Blocks.KELP_PLANT, Blocks.KELP);
		this.addDrop(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
		this.addDrop(Blocks.WATER_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.LAVA_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.POWDER_SNOW_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.STONE, blockx -> drops(blockx, Blocks.COBBLESTONE));
		this.addDrop(Blocks.GRASS_BLOCK, blockx -> drops(blockx, Blocks.DIRT));
		this.addDrop(Blocks.PODZOL, blockx -> drops(blockx, Blocks.DIRT));
		this.addDrop(Blocks.MYCELIUM, blockx -> drops(blockx, Blocks.DIRT));
		this.addDrop(Blocks.TUBE_CORAL_BLOCK, blockx -> drops(blockx, Blocks.DEAD_TUBE_CORAL_BLOCK));
		this.addDrop(Blocks.BRAIN_CORAL_BLOCK, blockx -> drops(blockx, Blocks.DEAD_BRAIN_CORAL_BLOCK));
		this.addDrop(Blocks.BUBBLE_CORAL_BLOCK, blockx -> drops(blockx, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
		this.addDrop(Blocks.FIRE_CORAL_BLOCK, blockx -> drops(blockx, Blocks.DEAD_FIRE_CORAL_BLOCK));
		this.addDrop(Blocks.HORN_CORAL_BLOCK, blockx -> drops(blockx, Blocks.DEAD_HORN_CORAL_BLOCK));
		this.addDrop(Blocks.CRIMSON_NYLIUM, blockx -> drops(blockx, Blocks.NETHERRACK));
		this.addDrop(Blocks.WARPED_NYLIUM, blockx -> drops(blockx, Blocks.NETHERRACK));
		this.addDrop(Blocks.BOOKSHELF, blockx -> drops(blockx, Items.BOOK, ConstantLootNumberProvider.create(3.0F)));
		this.addDrop(Blocks.CLAY, blockx -> drops(blockx, Items.CLAY_BALL, ConstantLootNumberProvider.create(4.0F)));
		this.addDrop(Blocks.ENDER_CHEST, blockx -> drops(blockx, Blocks.OBSIDIAN, ConstantLootNumberProvider.create(8.0F)));
		this.addDrop(Blocks.SNOW_BLOCK, blockx -> drops(blockx, Items.SNOWBALL, ConstantLootNumberProvider.create(4.0F)));
		this.addDrop(Blocks.CHORUS_PLANT, drops(Items.CHORUS_FRUIT, UniformLootNumberProvider.create(0.0F, 1.0F)));
		this.addPottedPlantDrop(Blocks.POTTED_OAK_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_SPRUCE_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_BIRCH_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_JUNGLE_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_ACACIA_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_DARK_OAK_SAPLING);
		this.addPottedPlantDrop(Blocks.POTTED_FERN);
		this.addPottedPlantDrop(Blocks.POTTED_DANDELION);
		this.addPottedPlantDrop(Blocks.POTTED_POPPY);
		this.addPottedPlantDrop(Blocks.POTTED_BLUE_ORCHID);
		this.addPottedPlantDrop(Blocks.POTTED_ALLIUM);
		this.addPottedPlantDrop(Blocks.POTTED_AZURE_BLUET);
		this.addPottedPlantDrop(Blocks.POTTED_RED_TULIP);
		this.addPottedPlantDrop(Blocks.POTTED_ORANGE_TULIP);
		this.addPottedPlantDrop(Blocks.POTTED_WHITE_TULIP);
		this.addPottedPlantDrop(Blocks.POTTED_PINK_TULIP);
		this.addPottedPlantDrop(Blocks.POTTED_OXEYE_DAISY);
		this.addPottedPlantDrop(Blocks.POTTED_CORNFLOWER);
		this.addPottedPlantDrop(Blocks.POTTED_LILY_OF_THE_VALLEY);
		this.addPottedPlantDrop(Blocks.POTTED_WITHER_ROSE);
		this.addPottedPlantDrop(Blocks.POTTED_RED_MUSHROOM);
		this.addPottedPlantDrop(Blocks.POTTED_BROWN_MUSHROOM);
		this.addPottedPlantDrop(Blocks.POTTED_DEAD_BUSH);
		this.addPottedPlantDrop(Blocks.POTTED_CACTUS);
		this.addPottedPlantDrop(Blocks.POTTED_BAMBOO);
		this.addPottedPlantDrop(Blocks.POTTED_CRIMSON_FUNGUS);
		this.addPottedPlantDrop(Blocks.POTTED_WARPED_FUNGUS);
		this.addPottedPlantDrop(Blocks.POTTED_CRIMSON_ROOTS);
		this.addPottedPlantDrop(Blocks.POTTED_WARPED_ROOTS);
		this.addDrop(Blocks.ACACIA_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.BIRCH_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.COBBLESTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.DARK_OAK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.DARK_PRISMARINE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.JUNGLE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.NETHER_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.OAK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.PETRIFIED_OAK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.PRISMARINE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.PURPUR_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.QUARTZ_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.CUT_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SPRUCE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.STONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SMOOTH_STONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.POLISHED_GRANITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.POLISHED_DIORITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.END_STONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.GRANITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.ANDESITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.DIORITE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.CRIMSON_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.WARPED_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.BLACKSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.WEATHERED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.WAXED_CUT_COPPER_SLAB, BlockLootTableGenerator::slabDrops);
		this.addDrop(Blocks.ACACIA_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.BIRCH_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.DARK_OAK_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.IRON_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.JUNGLE_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.OAK_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.SPRUCE_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.WARPED_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.CRIMSON_DOOR, BlockLootTableGenerator::addDoorDrop);
		this.addDrop(Blocks.BLACK_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.BLUE_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.BROWN_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.CYAN_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.GRAY_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.GREEN_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIGHT_BLUE_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIGHT_GRAY_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIME_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.MAGENTA_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.PURPLE_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.ORANGE_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.PINK_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.RED_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.WHITE_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.YELLOW_BED, blockx -> dropsWithProperty(blockx, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LILAC, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.SUNFLOWER, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.PEONY, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.ROSE_BUSH, blockx -> dropsWithProperty(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(
			Blocks.TNT,
			LootTable.builder()
				.pool(
					addSurvivesExplosionCondition(
						Blocks.TNT,
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								ItemEntry.builder(Blocks.TNT)
									.conditionally(BlockStatePropertyLootCondition.builder(Blocks.TNT).properties(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))
							)
					)
				)
		);
		this.addDrop(
			Blocks.COCOA,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								(LootPoolEntry.Builder<?>)applyExplosionDecay(
									blockx,
									ItemEntry.builder(Items.COCOA_BEANS)
										.apply(
											SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.SEA_PICKLE,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								(LootPoolEntry.Builder<?>)applyExplosionDecay(
									Blocks.SEA_PICKLE,
									ItemEntry.builder(blockx)
										.apply(
											SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2)))
										)
										.apply(
											SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3)))
										)
										.apply(
											SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4)))
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.COMPOSTER,
			blockx -> LootTable.builder()
					.pool(LootPool.builder().with((LootPoolEntry.Builder<?>)applyExplosionDecay(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.pool(
						LootPool.builder()
							.with(ItemEntry.builder(Items.BONE_MEAL))
							.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.addDrop(Blocks.CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.WHITE_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.ORANGE_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.MAGENTA_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.LIGHT_BLUE_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.YELLOW_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.LIME_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.PINK_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.GRAY_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.LIGHT_GRAY_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.CYAN_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.PURPLE_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.BLUE_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.BROWN_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.GREEN_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.RED_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.BLACK_CANDLE, BlockLootTableGenerator::candleDrops);
		this.addDrop(Blocks.BEACON, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.BREWING_STAND, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.CHEST, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.DISPENSER, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.DROPPER, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.ENCHANTING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.FURNACE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.HOPPER, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.TRAPPED_CHEST, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.SMOKER, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.BLAST_FURNACE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.BARREL, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.CARTOGRAPHY_TABLE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.FLETCHING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.GRINDSTONE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.LECTERN, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.SMITHING_TABLE, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.STONECUTTER, BlockLootTableGenerator::nameableContainerDrops);
		this.addDrop(Blocks.BELL, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.LANTERN, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.SOUL_LANTERN, BlockLootTableGenerator::drops);
		this.addDrop(Blocks.SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.BLACK_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.BLUE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.BROWN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.CYAN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.GRAY_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.GREEN_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.LIME_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.MAGENTA_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.ORANGE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.PINK_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.PURPLE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.RED_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.WHITE_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.YELLOW_SHULKER_BOX, BlockLootTableGenerator::shulkerBoxDrops);
		this.addDrop(Blocks.BLACK_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.BLUE_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.BROWN_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.CYAN_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.GRAY_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.GREEN_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.LIGHT_BLUE_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.LIGHT_GRAY_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.LIME_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.MAGENTA_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.ORANGE_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.PINK_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.PURPLE_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.RED_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.WHITE_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(Blocks.YELLOW_BANNER, BlockLootTableGenerator::bannerDrops);
		this.addDrop(
			Blocks.PLAYER_HEAD,
			blockx -> LootTable.builder()
					.pool(
						addSurvivesExplosionCondition(
							blockx,
							LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1.0F))
								.with(ItemEntry.builder(blockx).apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("SkullOwner", "SkullOwner")))
						)
					)
		);
		this.addDrop(Blocks.BEE_NEST, BlockLootTableGenerator::beeNestDrops);
		this.addDrop(Blocks.BEEHIVE, BlockLootTableGenerator::beehiveDrops);
		this.addDrop(Blocks.BIRCH_LEAVES, blockx -> leavesDrop(blockx, Blocks.BIRCH_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.ACACIA_LEAVES, blockx -> leavesDrop(blockx, Blocks.ACACIA_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.JUNGLE_LEAVES, blockx -> leavesDrop(blockx, Blocks.JUNGLE_SAPLING, JUNGLE_SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.SPRUCE_LEAVES, blockx -> leavesDrop(blockx, Blocks.SPRUCE_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.OAK_LEAVES, blockx -> oakLeavesDrop(blockx, Blocks.OAK_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.DARK_OAK_LEAVES, blockx -> oakLeavesDrop(blockx, Blocks.DARK_OAK_SAPLING, SAPLING_DROP_CHANCE));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS)
			.properties(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.addDrop(Blocks.BEETROOTS, cropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT)
			.properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.addDrop(Blocks.WHEAT, cropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS)
			.properties(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		this.addDrop(
			Blocks.CARROTS,
			applyExplosionDecay(
				Blocks.CARROTS,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.CARROT)))
					.pool(
						LootPool.builder()
							.conditionally(builder3)
							.with(ItemEntry.builder(Items.CARROT).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
			)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES)
			.properties(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.addDrop(
			Blocks.POTATOES,
			applyExplosionDecay(
				Blocks.POTATOES,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.POTATO)))
					.pool(
						LootPool.builder()
							.conditionally(builder4)
							.with(ItemEntry.builder(Items.POTATO).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
					.pool(LootPool.builder().conditionally(builder4).with(ItemEntry.builder(Items.POISONOUS_POTATO).conditionally(RandomChanceLootCondition.builder(0.02F))))
			)
		);
		this.addDrop(
			Blocks.SWEET_BERRY_BUSH,
			blockx -> applyExplosionDecay(
					blockx,
					LootTable.builder()
						.pool(
							LootPool.builder()
								.conditionally(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
								)
								.with(ItemEntry.builder(Items.SWEET_BERRIES))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
								.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
						.pool(
							LootPool.builder()
								.conditionally(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
								)
								.with(ItemEntry.builder(Items.SWEET_BERRIES))
								.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
								.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
				)
		);
		this.addDrop(Blocks.BROWN_MUSHROOM_BLOCK, blockx -> mushroomBlockDrops(blockx, Blocks.BROWN_MUSHROOM));
		this.addDrop(Blocks.RED_MUSHROOM_BLOCK, blockx -> mushroomBlockDrops(blockx, Blocks.RED_MUSHROOM));
		this.addDrop(Blocks.COAL_ORE, blockx -> oreDrops(blockx, Items.COAL));
		this.addDrop(Blocks.EMERALD_ORE, blockx -> oreDrops(blockx, Items.EMERALD));
		this.addDrop(Blocks.NETHER_QUARTZ_ORE, blockx -> oreDrops(blockx, Items.QUARTZ));
		this.addDrop(Blocks.DIAMOND_ORE, blockx -> oreDrops(blockx, Items.DIAMOND));
		this.addDrop(
			Blocks.NETHER_GOLD_ORE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))
							.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.addDrop(
			Blocks.LAPIS_ORE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.LAPIS_LAZULI)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F)))
							.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.addDrop(
			Blocks.COBWEB,
			blockx -> dropsWithSilkTouchOrShears(blockx, (LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(blockx, ItemEntry.builder(Items.STRING)))
		);
		this.addDrop(
			Blocks.DEAD_BUSH,
			blockx -> dropsWithShears(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
					)
				)
		);
		this.addDrop(Blocks.NETHER_SPROUTS, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.SEAGRASS, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.VINE, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.GLOW_LICHEN, BlockLootTableGenerator::dropsWithShears);
		this.addDrop(Blocks.TALL_SEAGRASS, seagrassDrops(Blocks.SEAGRASS));
		this.addDrop(Blocks.LARGE_FERN, blockx -> tallGrassDrops(blockx, Blocks.FERN));
		this.addDrop(Blocks.TALL_GRASS, blockx -> tallGrassDrops(blockx, Blocks.GRASS));
		this.addDrop(Blocks.MELON_STEM, blockx -> cropStemDrops(blockx, Items.MELON_SEEDS));
		this.addDrop(Blocks.ATTACHED_MELON_STEM, blockx -> attachedCropStemDrops(blockx, Items.MELON_SEEDS));
		this.addDrop(Blocks.PUMPKIN_STEM, blockx -> cropStemDrops(blockx, Items.PUMPKIN_SEEDS));
		this.addDrop(Blocks.ATTACHED_PUMPKIN_STEM, blockx -> attachedCropStemDrops(blockx, Items.PUMPKIN_SEEDS));
		this.addDrop(
			Blocks.CHORUS_FLOWER,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								((LeafEntry.Builder)addSurvivesExplosionCondition(blockx, ItemEntry.builder(blockx)))
									.conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							)
					)
		);
		this.addDrop(Blocks.FERN, BlockLootTableGenerator::grassDrops);
		this.addDrop(Blocks.GRASS, BlockLootTableGenerator::grassDrops);
		this.addDrop(
			Blocks.GLOWSTONE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.GLOWSTONE_DUST)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.addDrop(
			Blocks.MELON,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.MELON_SLICE)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.addDrop(
			Blocks.REDSTONE_ORE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.REDSTONE)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 5.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
					)
				)
		);
		this.addDrop(
			Blocks.SEA_LANTERN,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.PRISMARINE_CRYSTALS)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.addDrop(
			Blocks.NETHER_WART,
			blockx -> LootTable.builder()
					.pool(
						applyExplosionDecay(
							blockx,
							LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1.0F))
								.with(
									ItemEntry.builder(Items.NETHER_WART)
										.apply(
											SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.apply(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.addDrop(
			Blocks.SNOW,
			blockx -> LootTable.builder()
					.pool(
						LootPool.builder()
							.conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							.with(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(5.0F))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(6.0F))),
											ItemEntry.builder(Items.SNOWBALL)
												.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))
												.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(7.0F))),
											ItemEntry.builder(Items.SNOWBALL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(8.0F)))
										)
										.conditionally(WITHOUT_SILK_TOUCH),
									AlternativeEntry.builder(
										ItemEntry.builder(Blocks.SNOW)
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(5.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(6.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))),
										ItemEntry.builder(Blocks.SNOW)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(7.0F)))
											.conditionally(BlockStatePropertyLootCondition.builder(blockx).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))),
										ItemEntry.builder(Blocks.SNOW_BLOCK)
									)
								)
							)
					)
		);
		this.addDrop(
			Blocks.GRAVEL,
			blockx -> dropsWithSilkTouch(
					blockx,
					addSurvivesExplosionCondition(
						blockx,
						ItemEntry.builder(Items.FLINT)
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(blockx))
					)
				)
		);
		this.addDrop(
			Blocks.CAMPFIRE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(
						blockx, ItemEntry.builder(Items.CHARCOAL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
					)
				)
		);
		this.addDrop(
			Blocks.GILDED_BLACKSTONE,
			blockx -> dropsWithSilkTouch(
					blockx,
					addSurvivesExplosionCondition(
						blockx,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(blockx))
					)
				)
		);
		this.addDrop(
			Blocks.SOUL_CAMPFIRE,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)addSurvivesExplosionCondition(
						blockx, ItemEntry.builder(Items.SOUL_SOIL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
					)
				)
		);
		this.addDrop(
			Blocks.AMETHYST_CLUSTER,
			blockx -> dropsWithSilkTouch(
					blockx,
					(LootPoolEntry.Builder<?>)applyExplosionDecay(
						blockx,
						ItemEntry.builder(Items.AMETHYST_SHARD)
							.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F)))
							.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.addDropWithSilkTouch(Blocks.SMALL_AMETHYST_BUD);
		this.addDropWithSilkTouch(Blocks.MEDIUM_AMETHYST_BUD);
		this.addDropWithSilkTouch(Blocks.LARGE_AMETHYST_BUD);
		this.addDropWithSilkTouch(Blocks.GLASS);
		this.addDropWithSilkTouch(Blocks.WHITE_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.ORANGE_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.YELLOW_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.LIME_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.PINK_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.GRAY_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.CYAN_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.PURPLE_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.BLUE_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.BROWN_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.GREEN_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.RED_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.BLACK_STAINED_GLASS);
		this.addDropWithSilkTouch(Blocks.GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
		this.addDropWithSilkTouch(Blocks.ICE);
		this.addDropWithSilkTouch(Blocks.PACKED_ICE);
		this.addDropWithSilkTouch(Blocks.BLUE_ICE);
		this.addDropWithSilkTouch(Blocks.TURTLE_EGG);
		this.addDropWithSilkTouch(Blocks.MUSHROOM_STEM);
		this.addDropWithSilkTouch(Blocks.DEAD_TUBE_CORAL);
		this.addDropWithSilkTouch(Blocks.DEAD_BRAIN_CORAL);
		this.addDropWithSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
		this.addDropWithSilkTouch(Blocks.DEAD_FIRE_CORAL);
		this.addDropWithSilkTouch(Blocks.DEAD_HORN_CORAL);
		this.addDropWithSilkTouch(Blocks.TUBE_CORAL);
		this.addDropWithSilkTouch(Blocks.BRAIN_CORAL);
		this.addDropWithSilkTouch(Blocks.BUBBLE_CORAL);
		this.addDropWithSilkTouch(Blocks.FIRE_CORAL);
		this.addDropWithSilkTouch(Blocks.HORN_CORAL);
		this.addDropWithSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.TUBE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.BRAIN_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.BUBBLE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.FIRE_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.HORN_CORAL_FAN);
		this.addDropWithSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
		this.addDropWithSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
		this.addDropWithSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
		this.addDropWithSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
		this.addDropWithSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
		this.addDropWithSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
		this.addVinePlantDrop(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
		this.addVinePlantDrop(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
		this.addDrop(Blocks.CAKE, dropsNothing());
		this.addDrop(Blocks.CANDLE_CAKE, candleCakeDrops(Blocks.CANDLE));
		this.addDrop(Blocks.WHITE_CANDLE_CAKE, candleCakeDrops(Blocks.WHITE_CANDLE));
		this.addDrop(Blocks.ORANGE_CANDLE_CAKE, candleCakeDrops(Blocks.ORANGE_CANDLE));
		this.addDrop(Blocks.MAGENTA_CANDLE_CAKE, candleCakeDrops(Blocks.MAGENTA_CANDLE));
		this.addDrop(Blocks.LIGHT_BLUE_CANDLE_CAKE, candleCakeDrops(Blocks.LIGHT_BLUE_CANDLE));
		this.addDrop(Blocks.YELLOW_CANDLE_CAKE, candleCakeDrops(Blocks.YELLOW_CANDLE));
		this.addDrop(Blocks.LIME_CANDLE_CAKE, candleCakeDrops(Blocks.LIME_CANDLE));
		this.addDrop(Blocks.PINK_CANDLE_CAKE, candleCakeDrops(Blocks.PINK_CANDLE));
		this.addDrop(Blocks.GRAY_CANDLE_CAKE, candleCakeDrops(Blocks.GRAY_CANDLE));
		this.addDrop(Blocks.LIGHT_GRAY_CANDLE_CAKE, candleCakeDrops(Blocks.LIGHT_GRAY_CANDLE));
		this.addDrop(Blocks.CYAN_CANDLE_CAKE, candleCakeDrops(Blocks.CYAN_CANDLE));
		this.addDrop(Blocks.PURPLE_CANDLE_CAKE, candleCakeDrops(Blocks.PURPLE_CANDLE));
		this.addDrop(Blocks.BLUE_CANDLE_CAKE, candleCakeDrops(Blocks.BLUE_CANDLE));
		this.addDrop(Blocks.BROWN_CANDLE_CAKE, candleCakeDrops(Blocks.BROWN_CANDLE));
		this.addDrop(Blocks.GREEN_CANDLE_CAKE, candleCakeDrops(Blocks.GREEN_CANDLE));
		this.addDrop(Blocks.RED_CANDLE_CAKE, candleCakeDrops(Blocks.RED_CANDLE));
		this.addDrop(Blocks.BLACK_CANDLE_CAKE, candleCakeDrops(Blocks.BLACK_CANDLE));
		this.addDrop(Blocks.FROSTED_ICE, dropsNothing());
		this.addDrop(Blocks.SPAWNER, dropsNothing());
		this.addDrop(Blocks.FIRE, dropsNothing());
		this.addDrop(Blocks.SOUL_FIRE, dropsNothing());
		this.addDrop(Blocks.NETHER_PORTAL, dropsNothing());
		this.addDrop(Blocks.BUDDING_AMETHYST, dropsNothing());
		this.addDrop(Blocks.POWDER_SNOW, dropsNothing());
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
			block, ItemEntry.builder(block).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F))
		);
		this.addDrop(block, builder);
		this.addDrop(drop, builder);
	}

	public static LootTable.Builder addDoorDrop(Block block) {
		return dropsWithProperty(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
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
