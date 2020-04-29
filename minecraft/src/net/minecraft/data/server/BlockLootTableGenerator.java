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
import net.minecraft.loot.entry.LootEntry;
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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final LootCondition.Builder NEEDS_SILK_TOUCH = MatchToolLootCondition.builder(
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH = NEEDS_SILK_TOUCH.invert();
	private static final LootCondition.Builder NEEDS_SHEARS = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
	private static final LootCondition.Builder NEEDS_SILK_TOUCH_SHEARS = NEEDS_SHEARS.withCondition(NEEDS_SILK_TOUCH);
	private static final LootCondition.Builder DOESNT_NEED_SILK_TOUCH_SHEARS = NEEDS_SILK_TOUCH_SHEARS.invert();
	private static final Set<Item> ALWAYS_DROPPED_FROM_EXPLOSION = (Set<Item>)Stream.of(
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
	private static final float[] SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<Identifier, LootTable.Builder> lootTables = Maps.<Identifier, LootTable.Builder>newHashMap();

	private static <T> T addExplosionDecayLootFunction(ItemConvertible itemConvertible, LootFunctionConsumingBuilder<T> lootFunctionConsumingBuilder) {
		return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem())
			? lootFunctionConsumingBuilder.withFunction(ExplosionDecayLootFunction.builder())
			: lootFunctionConsumingBuilder.getThis();
	}

	private static <T> T addSurvivesExplosionLootCondition(ItemConvertible itemConvertible, LootConditionConsumingBuilder<T> lootConditionConsumingBuilder) {
		return !ALWAYS_DROPPED_FROM_EXPLOSION.contains(itemConvertible.asItem())
			? lootConditionConsumingBuilder.withCondition(SurvivesExplosionLootCondition.builder())
			: lootConditionConsumingBuilder.getThis();
	}

	private static LootTable.Builder create(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))
				)
			);
	}

	private static LootTable.Builder create(Block block, LootCondition.Builder conditionBuilder, LootEntry.Builder<?> child) {
		return LootTable.builder()
			.withPool(
				LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).withCondition(conditionBuilder).withChild(child))
			);
	}

	private static LootTable.Builder createForNeedingSilkTouch(Block block, LootEntry.Builder<?> child) {
		return create(block, NEEDS_SILK_TOUCH, child);
	}

	private static LootTable.Builder createForNeedingShears(Block block, LootEntry.Builder<?> child) {
		return create(block, NEEDS_SHEARS, child);
	}

	private static LootTable.Builder createForNeedingSilkTouchShears(Block block, LootEntry.Builder<?> child) {
		return create(block, NEEDS_SILK_TOUCH_SHEARS, child);
	}

	private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible lootWithoutSilkTouch) {
		return createForNeedingSilkTouch(block, (LootEntry.Builder<?>)addSurvivesExplosionLootCondition(block, ItemEntry.builder(lootWithoutSilkTouch)));
	}

	private static LootTable.Builder create(ItemConvertible itemConvertible, LootTableRange count) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)addExplosionDecayLootFunction(itemConvertible, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(count)))
					)
			);
	}

	private static LootTable.Builder createForBlockWithItemDrops(Block block, ItemConvertible lootWithoutSilkTouch, LootTableRange count) {
		return createForNeedingSilkTouch(
			block, (LootEntry.Builder<?>)addExplosionDecayLootFunction(block, ItemEntry.builder(lootWithoutSilkTouch).withFunction(SetCountLootFunction.builder(count)))
		);
	}

	private static LootTable.Builder createForNeedingSilkTouch(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withCondition(NEEDS_SILK_TOUCH).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder createForPottedPlant(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					Blocks.FLOWER_POT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.FLOWER_POT))
				)
			)
			.withPool(
				addSurvivesExplosionLootCondition(
					itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))
				)
			);
	}

	private static LootTable.Builder createForSlabs(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)addExplosionDecayLootFunction(
							block,
							ItemEntry.builder(block)
								.withFunction(
									SetCountLootFunction.builder(ConstantLootTableRange.create(2))
										.withCondition(
											BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE))
										)
								)
						)
					)
			);
	}

	private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder createForMultiblock(Block block, Property<T> property, T comparable) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(property, comparable)))
						)
				)
			);
	}

	private static LootTable.Builder createForNameableContainer(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))
				)
			);
	}

	private static LootTable.Builder createForShulkerBox(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.withFunction(
									CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY)
										.withOperation("Lock", "BlockEntityTag.Lock")
										.withOperation("LootTable", "BlockEntityTag.LootTable")
										.withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.withFunction(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))
						)
				)
			);
	}

	private static LootTable.Builder createForBanner(Block block) {
		return LootTable.builder()
			.withPool(
				addSurvivesExplosionLootCondition(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootTable.Builder createForBeeNest(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withCondition(NEEDS_SILK_TOUCH)
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL))
					)
			);
	}

	private static LootTable.Builder createForBeehive(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.withCondition(NEEDS_SILK_TOUCH)
							.withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.withFunction(CopyStateFunction.getBuilder(block).method_21898(BeehiveBlock.HONEY_LEVEL))
							.withChild(ItemEntry.builder(block))
					)
			);
	}

	private static LootTable.Builder createForOreWithSingleItemDrop(Block block, Item item) {
		return createForNeedingSilkTouch(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(block, ItemEntry.builder(item).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))
		);
	}

	private static LootTable.Builder createForLargeMushroomBlock(Block block, ItemConvertible loot) {
		return createForNeedingSilkTouch(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(
				block,
				ItemEntry.builder(loot)
					.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0F, 2.0F)))
					.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	private static LootTable.Builder createForTallGrass(Block block) {
		return createForNeedingShears(
			block,
			(LootEntry.Builder<?>)addExplosionDecayLootFunction(
				block,
				ItemEntry.builder(Items.WHEAT_SEEDS)
					.withCondition(RandomChanceLootCondition.builder(0.125F))
					.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
			)
		);
	}

	private static LootTable.Builder createForCropStem(Block block, Item seeds) {
		return LootTable.builder()
			.withPool(
				addExplosionDecayLootFunction(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(seeds)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))
								)
								.withFunction(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))
										.withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))
								)
						)
				)
			);
	}

	private static LootTable.Builder createForAttachedCropStem(Block block, Item seeds) {
		return LootTable.builder()
			.withPool(
				addExplosionDecayLootFunction(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(seeds).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))))
				)
			);
	}

	private static LootTable.Builder createForBlockNeedingShears(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(NEEDS_SHEARS).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder createForLeaves(Block leafBlock, Block sapling, float... saplingDropChances) {
		return createForNeedingSilkTouchShears(
				leafBlock,
				((LeafEntry.Builder)addSurvivesExplosionLootCondition(leafBlock, ItemEntry.builder(sapling)))
					.withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, saplingDropChances))
			)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS)
					.withEntry(
						((LeafEntry.Builder)addExplosionDecayLootFunction(
								leafBlock, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
							))
							.withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootTable.Builder createForOakLeaves(Block block, Block block2, float... fs) {
		return createForLeaves(block, block2, fs)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withCondition(DOESNT_NEED_SILK_TOUCH_SHEARS)
					.withEntry(
						((LeafEntry.Builder)addSurvivesExplosionLootCondition(block, ItemEntry.builder(Items.APPLE)))
							.withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootTable.Builder createForCrops(Block block, Item food, Item seeds, LootCondition.Builder condition) {
		return addExplosionDecayLootFunction(
			block,
			LootTable.builder()
				.withPool(LootPool.builder().withEntry(ItemEntry.builder(food).withCondition(condition).withChild(ItemEntry.builder(seeds))))
				.withPool(
					LootPool.builder()
						.withCondition(condition)
						.withEntry(ItemEntry.builder(seeds).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
				)
		);
	}

	public static LootTable.Builder createEmpty() {
		return LootTable.builder();
	}

	public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.registerForSelfDrop(Blocks.GRANITE);
		this.registerForSelfDrop(Blocks.POLISHED_GRANITE);
		this.registerForSelfDrop(Blocks.DIORITE);
		this.registerForSelfDrop(Blocks.POLISHED_DIORITE);
		this.registerForSelfDrop(Blocks.ANDESITE);
		this.registerForSelfDrop(Blocks.POLISHED_ANDESITE);
		this.registerForSelfDrop(Blocks.DIRT);
		this.registerForSelfDrop(Blocks.COARSE_DIRT);
		this.registerForSelfDrop(Blocks.COBBLESTONE);
		this.registerForSelfDrop(Blocks.OAK_PLANKS);
		this.registerForSelfDrop(Blocks.SPRUCE_PLANKS);
		this.registerForSelfDrop(Blocks.BIRCH_PLANKS);
		this.registerForSelfDrop(Blocks.JUNGLE_PLANKS);
		this.registerForSelfDrop(Blocks.ACACIA_PLANKS);
		this.registerForSelfDrop(Blocks.DARK_OAK_PLANKS);
		this.registerForSelfDrop(Blocks.OAK_SAPLING);
		this.registerForSelfDrop(Blocks.SPRUCE_SAPLING);
		this.registerForSelfDrop(Blocks.BIRCH_SAPLING);
		this.registerForSelfDrop(Blocks.JUNGLE_SAPLING);
		this.registerForSelfDrop(Blocks.ACACIA_SAPLING);
		this.registerForSelfDrop(Blocks.DARK_OAK_SAPLING);
		this.registerForSelfDrop(Blocks.SAND);
		this.registerForSelfDrop(Blocks.RED_SAND);
		this.registerForSelfDrop(Blocks.GOLD_ORE);
		this.registerForSelfDrop(Blocks.IRON_ORE);
		this.registerForSelfDrop(Blocks.OAK_LOG);
		this.registerForSelfDrop(Blocks.SPRUCE_LOG);
		this.registerForSelfDrop(Blocks.BIRCH_LOG);
		this.registerForSelfDrop(Blocks.JUNGLE_LOG);
		this.registerForSelfDrop(Blocks.ACACIA_LOG);
		this.registerForSelfDrop(Blocks.DARK_OAK_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_SPRUCE_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_BIRCH_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_JUNGLE_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_ACACIA_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_DARK_OAK_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_OAK_LOG);
		this.registerForSelfDrop(Blocks.STRIPPED_WARPED_STEM);
		this.registerForSelfDrop(Blocks.STRIPPED_CRIMSON_STEM);
		this.registerForSelfDrop(Blocks.OAK_WOOD);
		this.registerForSelfDrop(Blocks.SPRUCE_WOOD);
		this.registerForSelfDrop(Blocks.BIRCH_WOOD);
		this.registerForSelfDrop(Blocks.JUNGLE_WOOD);
		this.registerForSelfDrop(Blocks.ACACIA_WOOD);
		this.registerForSelfDrop(Blocks.DARK_OAK_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_OAK_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_SPRUCE_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_BIRCH_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_JUNGLE_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_ACACIA_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.registerForSelfDrop(Blocks.STRIPPED_CRIMSON_HYPHAE);
		this.registerForSelfDrop(Blocks.STRIPPED_WARPED_HYPHAE);
		this.registerForSelfDrop(Blocks.SPONGE);
		this.registerForSelfDrop(Blocks.WET_SPONGE);
		this.registerForSelfDrop(Blocks.LAPIS_BLOCK);
		this.registerForSelfDrop(Blocks.SANDSTONE);
		this.registerForSelfDrop(Blocks.CHISELED_SANDSTONE);
		this.registerForSelfDrop(Blocks.CUT_SANDSTONE);
		this.registerForSelfDrop(Blocks.NOTE_BLOCK);
		this.registerForSelfDrop(Blocks.POWERED_RAIL);
		this.registerForSelfDrop(Blocks.DETECTOR_RAIL);
		this.registerForSelfDrop(Blocks.STICKY_PISTON);
		this.registerForSelfDrop(Blocks.PISTON);
		this.registerForSelfDrop(Blocks.WHITE_WOOL);
		this.registerForSelfDrop(Blocks.ORANGE_WOOL);
		this.registerForSelfDrop(Blocks.MAGENTA_WOOL);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_WOOL);
		this.registerForSelfDrop(Blocks.YELLOW_WOOL);
		this.registerForSelfDrop(Blocks.LIME_WOOL);
		this.registerForSelfDrop(Blocks.PINK_WOOL);
		this.registerForSelfDrop(Blocks.GRAY_WOOL);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_WOOL);
		this.registerForSelfDrop(Blocks.CYAN_WOOL);
		this.registerForSelfDrop(Blocks.PURPLE_WOOL);
		this.registerForSelfDrop(Blocks.BLUE_WOOL);
		this.registerForSelfDrop(Blocks.BROWN_WOOL);
		this.registerForSelfDrop(Blocks.GREEN_WOOL);
		this.registerForSelfDrop(Blocks.RED_WOOL);
		this.registerForSelfDrop(Blocks.BLACK_WOOL);
		this.registerForSelfDrop(Blocks.DANDELION);
		this.registerForSelfDrop(Blocks.POPPY);
		this.registerForSelfDrop(Blocks.BLUE_ORCHID);
		this.registerForSelfDrop(Blocks.ALLIUM);
		this.registerForSelfDrop(Blocks.AZURE_BLUET);
		this.registerForSelfDrop(Blocks.RED_TULIP);
		this.registerForSelfDrop(Blocks.ORANGE_TULIP);
		this.registerForSelfDrop(Blocks.WHITE_TULIP);
		this.registerForSelfDrop(Blocks.PINK_TULIP);
		this.registerForSelfDrop(Blocks.OXEYE_DAISY);
		this.registerForSelfDrop(Blocks.CORNFLOWER);
		this.registerForSelfDrop(Blocks.WITHER_ROSE);
		this.registerForSelfDrop(Blocks.LILY_OF_THE_VALLEY);
		this.registerForSelfDrop(Blocks.BROWN_MUSHROOM);
		this.registerForSelfDrop(Blocks.RED_MUSHROOM);
		this.registerForSelfDrop(Blocks.GOLD_BLOCK);
		this.registerForSelfDrop(Blocks.IRON_BLOCK);
		this.registerForSelfDrop(Blocks.BRICKS);
		this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE);
		this.registerForSelfDrop(Blocks.OBSIDIAN);
		this.registerForSelfDrop(Blocks.CRYING_OBSIDIAN);
		this.registerForSelfDrop(Blocks.TORCH);
		this.registerForSelfDrop(Blocks.OAK_STAIRS);
		this.registerForSelfDrop(Blocks.REDSTONE_WIRE);
		this.registerForSelfDrop(Blocks.DIAMOND_BLOCK);
		this.registerForSelfDrop(Blocks.CRAFTING_TABLE);
		this.registerForSelfDrop(Blocks.OAK_SIGN);
		this.registerForSelfDrop(Blocks.SPRUCE_SIGN);
		this.registerForSelfDrop(Blocks.BIRCH_SIGN);
		this.registerForSelfDrop(Blocks.ACACIA_SIGN);
		this.registerForSelfDrop(Blocks.JUNGLE_SIGN);
		this.registerForSelfDrop(Blocks.DARK_OAK_SIGN);
		this.registerForSelfDrop(Blocks.LADDER);
		this.registerForSelfDrop(Blocks.RAIL);
		this.registerForSelfDrop(Blocks.COBBLESTONE_STAIRS);
		this.registerForSelfDrop(Blocks.LEVER);
		this.registerForSelfDrop(Blocks.STONE_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.OAK_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.SPRUCE_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.BIRCH_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.JUNGLE_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.ACACIA_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.DARK_OAK_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.REDSTONE_TORCH);
		this.registerForSelfDrop(Blocks.STONE_BUTTON);
		this.registerForSelfDrop(Blocks.CACTUS);
		this.registerForSelfDrop(Blocks.SUGAR_CANE);
		this.registerForSelfDrop(Blocks.JUKEBOX);
		this.registerForSelfDrop(Blocks.OAK_FENCE);
		this.registerForSelfDrop(Blocks.PUMPKIN);
		this.registerForSelfDrop(Blocks.NETHERRACK);
		this.registerForSelfDrop(Blocks.SOUL_SAND);
		this.registerForSelfDrop(Blocks.SOUL_SOIL);
		this.registerForSelfDrop(Blocks.BASALT);
		this.registerForSelfDrop(Blocks.POLISHED_BASALT);
		this.registerForSelfDrop(Blocks.SOUL_TORCH);
		this.registerForSelfDrop(Blocks.CARVED_PUMPKIN);
		this.registerForSelfDrop(Blocks.JACK_O_LANTERN);
		this.registerForSelfDrop(Blocks.REPEATER);
		this.registerForSelfDrop(Blocks.OAK_TRAPDOOR);
		this.registerForSelfDrop(Blocks.SPRUCE_TRAPDOOR);
		this.registerForSelfDrop(Blocks.BIRCH_TRAPDOOR);
		this.registerForSelfDrop(Blocks.JUNGLE_TRAPDOOR);
		this.registerForSelfDrop(Blocks.ACACIA_TRAPDOOR);
		this.registerForSelfDrop(Blocks.DARK_OAK_TRAPDOOR);
		this.registerForSelfDrop(Blocks.STONE_BRICKS);
		this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICKS);
		this.registerForSelfDrop(Blocks.CRACKED_STONE_BRICKS);
		this.registerForSelfDrop(Blocks.CHISELED_STONE_BRICKS);
		this.registerForSelfDrop(Blocks.IRON_BARS);
		this.registerForSelfDrop(Blocks.OAK_FENCE_GATE);
		this.registerForSelfDrop(Blocks.BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.STONE_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.LILY_PAD);
		this.registerForSelfDrop(Blocks.NETHER_BRICKS);
		this.registerForSelfDrop(Blocks.NETHER_BRICK_FENCE);
		this.registerForSelfDrop(Blocks.NETHER_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.CAULDRON);
		this.registerForSelfDrop(Blocks.END_STONE);
		this.registerForSelfDrop(Blocks.REDSTONE_LAMP);
		this.registerForSelfDrop(Blocks.SANDSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.TRIPWIRE_HOOK);
		this.registerForSelfDrop(Blocks.EMERALD_BLOCK);
		this.registerForSelfDrop(Blocks.SPRUCE_STAIRS);
		this.registerForSelfDrop(Blocks.BIRCH_STAIRS);
		this.registerForSelfDrop(Blocks.JUNGLE_STAIRS);
		this.registerForSelfDrop(Blocks.COBBLESTONE_WALL);
		this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE_WALL);
		this.registerForSelfDrop(Blocks.FLOWER_POT);
		this.registerForSelfDrop(Blocks.OAK_BUTTON);
		this.registerForSelfDrop(Blocks.SPRUCE_BUTTON);
		this.registerForSelfDrop(Blocks.BIRCH_BUTTON);
		this.registerForSelfDrop(Blocks.JUNGLE_BUTTON);
		this.registerForSelfDrop(Blocks.ACACIA_BUTTON);
		this.registerForSelfDrop(Blocks.DARK_OAK_BUTTON);
		this.registerForSelfDrop(Blocks.SKELETON_SKULL);
		this.registerForSelfDrop(Blocks.WITHER_SKELETON_SKULL);
		this.registerForSelfDrop(Blocks.ZOMBIE_HEAD);
		this.registerForSelfDrop(Blocks.CREEPER_HEAD);
		this.registerForSelfDrop(Blocks.DRAGON_HEAD);
		this.registerForSelfDrop(Blocks.ANVIL);
		this.registerForSelfDrop(Blocks.CHIPPED_ANVIL);
		this.registerForSelfDrop(Blocks.DAMAGED_ANVIL);
		this.registerForSelfDrop(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.COMPARATOR);
		this.registerForSelfDrop(Blocks.DAYLIGHT_DETECTOR);
		this.registerForSelfDrop(Blocks.REDSTONE_BLOCK);
		this.registerForSelfDrop(Blocks.QUARTZ_BLOCK);
		this.registerForSelfDrop(Blocks.CHISELED_QUARTZ_BLOCK);
		this.registerForSelfDrop(Blocks.QUARTZ_PILLAR);
		this.registerForSelfDrop(Blocks.QUARTZ_STAIRS);
		this.registerForSelfDrop(Blocks.ACTIVATOR_RAIL);
		this.registerForSelfDrop(Blocks.WHITE_TERRACOTTA);
		this.registerForSelfDrop(Blocks.ORANGE_TERRACOTTA);
		this.registerForSelfDrop(Blocks.MAGENTA_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_TERRACOTTA);
		this.registerForSelfDrop(Blocks.YELLOW_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIME_TERRACOTTA);
		this.registerForSelfDrop(Blocks.PINK_TERRACOTTA);
		this.registerForSelfDrop(Blocks.GRAY_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_TERRACOTTA);
		this.registerForSelfDrop(Blocks.CYAN_TERRACOTTA);
		this.registerForSelfDrop(Blocks.PURPLE_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BLUE_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BROWN_TERRACOTTA);
		this.registerForSelfDrop(Blocks.GREEN_TERRACOTTA);
		this.registerForSelfDrop(Blocks.RED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BLACK_TERRACOTTA);
		this.registerForSelfDrop(Blocks.ACACIA_STAIRS);
		this.registerForSelfDrop(Blocks.DARK_OAK_STAIRS);
		this.registerForSelfDrop(Blocks.SLIME_BLOCK);
		this.registerForSelfDrop(Blocks.IRON_TRAPDOOR);
		this.registerForSelfDrop(Blocks.PRISMARINE);
		this.registerForSelfDrop(Blocks.PRISMARINE_BRICKS);
		this.registerForSelfDrop(Blocks.DARK_PRISMARINE);
		this.registerForSelfDrop(Blocks.PRISMARINE_STAIRS);
		this.registerForSelfDrop(Blocks.PRISMARINE_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.DARK_PRISMARINE_STAIRS);
		this.registerForSelfDrop(Blocks.HAY_BLOCK);
		this.registerForSelfDrop(Blocks.WHITE_CARPET);
		this.registerForSelfDrop(Blocks.ORANGE_CARPET);
		this.registerForSelfDrop(Blocks.MAGENTA_CARPET);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_CARPET);
		this.registerForSelfDrop(Blocks.YELLOW_CARPET);
		this.registerForSelfDrop(Blocks.LIME_CARPET);
		this.registerForSelfDrop(Blocks.PINK_CARPET);
		this.registerForSelfDrop(Blocks.GRAY_CARPET);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_CARPET);
		this.registerForSelfDrop(Blocks.CYAN_CARPET);
		this.registerForSelfDrop(Blocks.PURPLE_CARPET);
		this.registerForSelfDrop(Blocks.BLUE_CARPET);
		this.registerForSelfDrop(Blocks.BROWN_CARPET);
		this.registerForSelfDrop(Blocks.GREEN_CARPET);
		this.registerForSelfDrop(Blocks.RED_CARPET);
		this.registerForSelfDrop(Blocks.BLACK_CARPET);
		this.registerForSelfDrop(Blocks.TERRACOTTA);
		this.registerForSelfDrop(Blocks.COAL_BLOCK);
		this.registerForSelfDrop(Blocks.RED_SANDSTONE);
		this.registerForSelfDrop(Blocks.CHISELED_RED_SANDSTONE);
		this.registerForSelfDrop(Blocks.CUT_RED_SANDSTONE);
		this.registerForSelfDrop(Blocks.RED_SANDSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.SMOOTH_STONE);
		this.registerForSelfDrop(Blocks.SMOOTH_SANDSTONE);
		this.registerForSelfDrop(Blocks.SMOOTH_QUARTZ);
		this.registerForSelfDrop(Blocks.SMOOTH_RED_SANDSTONE);
		this.registerForSelfDrop(Blocks.SPRUCE_FENCE_GATE);
		this.registerForSelfDrop(Blocks.BIRCH_FENCE_GATE);
		this.registerForSelfDrop(Blocks.JUNGLE_FENCE_GATE);
		this.registerForSelfDrop(Blocks.ACACIA_FENCE_GATE);
		this.registerForSelfDrop(Blocks.DARK_OAK_FENCE_GATE);
		this.registerForSelfDrop(Blocks.SPRUCE_FENCE);
		this.registerForSelfDrop(Blocks.BIRCH_FENCE);
		this.registerForSelfDrop(Blocks.JUNGLE_FENCE);
		this.registerForSelfDrop(Blocks.ACACIA_FENCE);
		this.registerForSelfDrop(Blocks.DARK_OAK_FENCE);
		this.registerForSelfDrop(Blocks.END_ROD);
		this.registerForSelfDrop(Blocks.PURPUR_BLOCK);
		this.registerForSelfDrop(Blocks.PURPUR_PILLAR);
		this.registerForSelfDrop(Blocks.PURPUR_STAIRS);
		this.registerForSelfDrop(Blocks.END_STONE_BRICKS);
		this.registerForSelfDrop(Blocks.MAGMA_BLOCK);
		this.registerForSelfDrop(Blocks.NETHER_WART_BLOCK);
		this.registerForSelfDrop(Blocks.RED_NETHER_BRICKS);
		this.registerForSelfDrop(Blocks.BONE_BLOCK);
		this.registerForSelfDrop(Blocks.OBSERVER);
		this.registerForSelfDrop(Blocks.TARGET);
		this.registerForSelfDrop(Blocks.WHITE_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.ORANGE_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.MAGENTA_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.YELLOW_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIME_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.PINK_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.GRAY_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.CYAN_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.PURPLE_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BLUE_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BROWN_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.GREEN_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.RED_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.BLACK_GLAZED_TERRACOTTA);
		this.registerForSelfDrop(Blocks.WHITE_CONCRETE);
		this.registerForSelfDrop(Blocks.ORANGE_CONCRETE);
		this.registerForSelfDrop(Blocks.MAGENTA_CONCRETE);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_CONCRETE);
		this.registerForSelfDrop(Blocks.YELLOW_CONCRETE);
		this.registerForSelfDrop(Blocks.LIME_CONCRETE);
		this.registerForSelfDrop(Blocks.PINK_CONCRETE);
		this.registerForSelfDrop(Blocks.GRAY_CONCRETE);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_CONCRETE);
		this.registerForSelfDrop(Blocks.CYAN_CONCRETE);
		this.registerForSelfDrop(Blocks.PURPLE_CONCRETE);
		this.registerForSelfDrop(Blocks.BLUE_CONCRETE);
		this.registerForSelfDrop(Blocks.BROWN_CONCRETE);
		this.registerForSelfDrop(Blocks.GREEN_CONCRETE);
		this.registerForSelfDrop(Blocks.RED_CONCRETE);
		this.registerForSelfDrop(Blocks.BLACK_CONCRETE);
		this.registerForSelfDrop(Blocks.WHITE_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.ORANGE_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.MAGENTA_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.YELLOW_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.LIME_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.PINK_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.GRAY_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.CYAN_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.PURPLE_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.BLUE_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.BROWN_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.GREEN_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.RED_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.BLACK_CONCRETE_POWDER);
		this.registerForSelfDrop(Blocks.KELP);
		this.registerForSelfDrop(Blocks.DRIED_KELP_BLOCK);
		this.registerForSelfDrop(Blocks.DEAD_TUBE_CORAL_BLOCK);
		this.registerForSelfDrop(Blocks.DEAD_BRAIN_CORAL_BLOCK);
		this.registerForSelfDrop(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
		this.registerForSelfDrop(Blocks.DEAD_FIRE_CORAL_BLOCK);
		this.registerForSelfDrop(Blocks.DEAD_HORN_CORAL_BLOCK);
		this.registerForSelfDrop(Blocks.CONDUIT);
		this.registerForSelfDrop(Blocks.DRAGON_EGG);
		this.registerForSelfDrop(Blocks.BAMBOO);
		this.registerForSelfDrop(Blocks.POLISHED_GRANITE_STAIRS);
		this.registerForSelfDrop(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.POLISHED_DIORITE_STAIRS);
		this.registerForSelfDrop(Blocks.MOSSY_COBBLESTONE_STAIRS);
		this.registerForSelfDrop(Blocks.END_STONE_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.STONE_STAIRS);
		this.registerForSelfDrop(Blocks.SMOOTH_SANDSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.SMOOTH_QUARTZ_STAIRS);
		this.registerForSelfDrop(Blocks.GRANITE_STAIRS);
		this.registerForSelfDrop(Blocks.ANDESITE_STAIRS);
		this.registerForSelfDrop(Blocks.RED_NETHER_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.POLISHED_ANDESITE_STAIRS);
		this.registerForSelfDrop(Blocks.DIORITE_STAIRS);
		this.registerForSelfDrop(Blocks.BRICK_WALL);
		this.registerForSelfDrop(Blocks.PRISMARINE_WALL);
		this.registerForSelfDrop(Blocks.RED_SANDSTONE_WALL);
		this.registerForSelfDrop(Blocks.MOSSY_STONE_BRICK_WALL);
		this.registerForSelfDrop(Blocks.GRANITE_WALL);
		this.registerForSelfDrop(Blocks.STONE_BRICK_WALL);
		this.registerForSelfDrop(Blocks.NETHER_BRICK_WALL);
		this.registerForSelfDrop(Blocks.ANDESITE_WALL);
		this.registerForSelfDrop(Blocks.RED_NETHER_BRICK_WALL);
		this.registerForSelfDrop(Blocks.SANDSTONE_WALL);
		this.registerForSelfDrop(Blocks.END_STONE_BRICK_WALL);
		this.registerForSelfDrop(Blocks.DIORITE_WALL);
		this.registerForSelfDrop(Blocks.LOOM);
		this.registerForSelfDrop(Blocks.SCAFFOLDING);
		this.registerForSelfDrop(Blocks.HONEY_BLOCK);
		this.registerForSelfDrop(Blocks.HONEYCOMB_BLOCK);
		this.registerForSelfDrop(Blocks.RESPAWN_ANCHOR);
		this.registerForSelfDrop(Blocks.LODESTONE);
		this.registerForSelfDrop(Blocks.WARPED_STEM);
		this.registerForSelfDrop(Blocks.WARPED_HYPHAE);
		this.registerForSelfDrop(Blocks.WARPED_NYLIUM);
		this.registerForSelfDrop(Blocks.WARPED_FUNGUS);
		this.registerForSelfDrop(Blocks.WARPED_WART_BLOCK);
		this.registerForSelfDrop(Blocks.WARPED_ROOTS);
		this.registerForSelfDrop(Blocks.CRIMSON_STEM);
		this.registerForSelfDrop(Blocks.CRIMSON_HYPHAE);
		this.registerForSelfDrop(Blocks.CRIMSON_NYLIUM);
		this.registerForSelfDrop(Blocks.CRIMSON_FUNGUS);
		this.registerForSelfDrop(Blocks.SHROOMLIGHT);
		this.registerForSelfDrop(Blocks.CRIMSON_ROOTS);
		this.registerForSelfDrop(Blocks.CRIMSON_PLANKS);
		this.registerForSelfDrop(Blocks.WARPED_PLANKS);
		this.registerForSelfDrop(Blocks.WARPED_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.WARPED_FENCE);
		this.registerForSelfDrop(Blocks.WARPED_TRAPDOOR);
		this.registerForSelfDrop(Blocks.WARPED_FENCE_GATE);
		this.registerForSelfDrop(Blocks.WARPED_STAIRS);
		this.registerForSelfDrop(Blocks.WARPED_BUTTON);
		this.registerForSelfDrop(Blocks.WARPED_SIGN);
		this.registerForSelfDrop(Blocks.CRIMSON_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.CRIMSON_FENCE);
		this.registerForSelfDrop(Blocks.CRIMSON_TRAPDOOR);
		this.registerForSelfDrop(Blocks.CRIMSON_FENCE_GATE);
		this.registerForSelfDrop(Blocks.CRIMSON_STAIRS);
		this.registerForSelfDrop(Blocks.CRIMSON_BUTTON);
		this.registerForSelfDrop(Blocks.CRIMSON_SIGN);
		this.registerForSelfDrop(Blocks.NETHERITE_BLOCK);
		this.registerForSelfDrop(Blocks.ANCIENT_DEBRIS);
		this.registerForSelfDrop(Blocks.BLACKSTONE);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICKS);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
		this.registerForSelfDrop(Blocks.BLACKSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.BLACKSTONE_WALL);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
		this.registerForSelfDrop(Blocks.CHISELED_POLISHED_BLACKSTONE);
		this.registerForSelfDrop(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_STAIRS);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_BUTTON);
		this.registerForSelfDrop(Blocks.POLISHED_BLACKSTONE_WALL);
		this.registerForSelfDrop(Blocks.CHISELED_NETHER_BRICKS);
		this.registerForSelfDrop(Blocks.CRACKED_NETHER_BRICKS);
		this.registerForSelfDrop(Blocks.QUARTZ_BRICKS);
		this.registerForSelfDrop(Blocks.CHAIN);
		this.register(Blocks.FARMLAND, Blocks.DIRT);
		this.register(Blocks.TRIPWIRE, Items.STRING);
		this.register(Blocks.GRASS_PATH, Blocks.DIRT);
		this.register(Blocks.KELP_PLANT, Blocks.KELP);
		this.register(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
		this.registerWithFunction(Blocks.STONE, blockx -> createForBlockWithItemDrops(blockx, Blocks.COBBLESTONE));
		this.registerWithFunction(Blocks.GRASS_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DIRT));
		this.registerWithFunction(Blocks.PODZOL, blockx -> createForBlockWithItemDrops(blockx, Blocks.DIRT));
		this.registerWithFunction(Blocks.MYCELIUM, blockx -> createForBlockWithItemDrops(blockx, Blocks.DIRT));
		this.registerWithFunction(Blocks.TUBE_CORAL_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DEAD_TUBE_CORAL_BLOCK));
		this.registerWithFunction(Blocks.BRAIN_CORAL_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DEAD_BRAIN_CORAL_BLOCK));
		this.registerWithFunction(Blocks.BUBBLE_CORAL_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
		this.registerWithFunction(Blocks.FIRE_CORAL_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DEAD_FIRE_CORAL_BLOCK));
		this.registerWithFunction(Blocks.HORN_CORAL_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Blocks.DEAD_HORN_CORAL_BLOCK));
		this.registerWithFunction(Blocks.BOOKSHELF, blockx -> createForBlockWithItemDrops(blockx, Items.BOOK, ConstantLootTableRange.create(3)));
		this.registerWithFunction(Blocks.CLAY, blockx -> createForBlockWithItemDrops(blockx, Items.CLAY_BALL, ConstantLootTableRange.create(4)));
		this.registerWithFunction(Blocks.ENDER_CHEST, blockx -> createForBlockWithItemDrops(blockx, Blocks.OBSIDIAN, ConstantLootTableRange.create(8)));
		this.registerWithFunction(Blocks.SNOW_BLOCK, blockx -> createForBlockWithItemDrops(blockx, Items.SNOWBALL, ConstantLootTableRange.create(4)));
		this.register(Blocks.CHORUS_PLANT, create(Items.CHORUS_FRUIT, UniformLootTableRange.between(0.0F, 1.0F)));
		this.registerForPottedPlant(Blocks.POTTED_OAK_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_SPRUCE_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_BIRCH_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_JUNGLE_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_ACACIA_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_DARK_OAK_SAPLING);
		this.registerForPottedPlant(Blocks.POTTED_FERN);
		this.registerForPottedPlant(Blocks.POTTED_DANDELION);
		this.registerForPottedPlant(Blocks.POTTED_POPPY);
		this.registerForPottedPlant(Blocks.POTTED_BLUE_ORCHID);
		this.registerForPottedPlant(Blocks.POTTED_ALLIUM);
		this.registerForPottedPlant(Blocks.POTTED_AZURE_BLUET);
		this.registerForPottedPlant(Blocks.POTTED_RED_TULIP);
		this.registerForPottedPlant(Blocks.POTTED_ORANGE_TULIP);
		this.registerForPottedPlant(Blocks.POTTED_WHITE_TULIP);
		this.registerForPottedPlant(Blocks.POTTED_PINK_TULIP);
		this.registerForPottedPlant(Blocks.POTTED_OXEYE_DAISY);
		this.registerForPottedPlant(Blocks.POTTED_CORNFLOWER);
		this.registerForPottedPlant(Blocks.POTTED_LILY_OF_THE_VALLEY);
		this.registerForPottedPlant(Blocks.POTTED_WITHER_ROSE);
		this.registerForPottedPlant(Blocks.POTTED_RED_MUSHROOM);
		this.registerForPottedPlant(Blocks.POTTED_BROWN_MUSHROOM);
		this.registerForPottedPlant(Blocks.POTTED_DEAD_BUSH);
		this.registerForPottedPlant(Blocks.POTTED_CACTUS);
		this.registerForPottedPlant(Blocks.POTTED_BAMBOO);
		this.registerForPottedPlant(Blocks.POTTED_CRIMSON_FUNGUS);
		this.registerForPottedPlant(Blocks.POTTED_WARPED_FUNGUS);
		this.registerForPottedPlant(Blocks.POTTED_CRIMSON_ROOTS);
		this.registerForPottedPlant(Blocks.POTTED_WARPED_ROOTS);
		this.registerWithFunction(Blocks.ACACIA_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.BIRCH_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.COBBLESTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.DARK_OAK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.DARK_PRISMARINE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.JUNGLE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.NETHER_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.OAK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.PETRIFIED_OAK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.PRISMARINE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.PURPUR_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.QUARTZ_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.CUT_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SPRUCE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.STONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SMOOTH_STONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.POLISHED_GRANITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.POLISHED_DIORITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.END_STONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.GRANITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.ANDESITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.DIORITE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.CRIMSON_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.WARPED_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.BLACKSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLootTableGenerator::createForSlabs);
		this.registerWithFunction(Blocks.ACACIA_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.BIRCH_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.DARK_OAK_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.IRON_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.JUNGLE_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.OAK_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.SPRUCE_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.WARPED_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.CRIMSON_DOOR, BlockLootTableGenerator::method_24817);
		this.registerWithFunction(Blocks.BLACK_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.BLUE_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.BROWN_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.CYAN_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.GRAY_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.GREEN_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.LIGHT_BLUE_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.LIGHT_GRAY_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.LIME_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.MAGENTA_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.PURPLE_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.ORANGE_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.PINK_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.RED_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.WHITE_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.YELLOW_BED, blockx -> createForMultiblock(blockx, BedBlock.PART, BedPart.HEAD));
		this.registerWithFunction(Blocks.LILAC, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.registerWithFunction(Blocks.SUNFLOWER, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.registerWithFunction(Blocks.PEONY, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.registerWithFunction(Blocks.ROSE_BUSH, blockx -> createForMultiblock(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.register(
			Blocks.TNT,
			LootTable.builder()
				.withPool(
					addSurvivesExplosionLootCondition(
						Blocks.TNT,
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								ItemEntry.builder(Blocks.TNT)
									.withCondition(BlockStatePropertyLootCondition.builder(Blocks.TNT).method_22584(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))
							)
					)
				)
		);
		this.registerWithFunction(
			Blocks.COCOA,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)addExplosionDecayLootFunction(
									blockx,
									ItemEntry.builder(Items.COCOA_BEANS)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.SEA_PICKLE,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)addExplosionDecayLootFunction(
									Blocks.SEA_PICKLE,
									ItemEntry.builder(blockx)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(2))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2)))
										)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3)))
										)
										.withFunction(
											SetCountLootFunction.builder(ConstantLootTableRange.create(4))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4)))
										)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.COMPOSTER,
			blockx -> LootTable.builder()
					.withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)addExplosionDecayLootFunction(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.withPool(
						LootPool.builder()
							.withEntry(ItemEntry.builder(Items.BONE_MEAL))
							.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.registerWithFunction(Blocks.BEACON, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.BREWING_STAND, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.CHEST, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.DISPENSER, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.DROPPER, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.ENCHANTING_TABLE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.FURNACE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.HOPPER, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.TRAPPED_CHEST, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.SMOKER, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.BLAST_FURNACE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.BARREL, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.CARTOGRAPHY_TABLE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.FLETCHING_TABLE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.GRINDSTONE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.LECTERN, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.SMITHING_TABLE, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.STONECUTTER, BlockLootTableGenerator::createForNameableContainer);
		this.registerWithFunction(Blocks.BELL, BlockLootTableGenerator::create);
		this.registerWithFunction(Blocks.LANTERN, BlockLootTableGenerator::create);
		this.registerWithFunction(Blocks.SOUL_LANTERN, BlockLootTableGenerator::create);
		this.registerWithFunction(Blocks.SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.BLACK_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.BLUE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.BROWN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.CYAN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.GRAY_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.GREEN_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.LIME_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.MAGENTA_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.ORANGE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.PINK_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.PURPLE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.RED_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.WHITE_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.YELLOW_SHULKER_BOX, BlockLootTableGenerator::createForShulkerBox);
		this.registerWithFunction(Blocks.BLACK_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.BLUE_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.BROWN_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.CYAN_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.GRAY_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.GREEN_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.LIGHT_BLUE_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.LIGHT_GRAY_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.LIME_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.MAGENTA_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.ORANGE_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.PINK_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.PURPLE_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.RED_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.WHITE_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(Blocks.YELLOW_BANNER, BlockLootTableGenerator::createForBanner);
		this.registerWithFunction(
			Blocks.PLAYER_HEAD,
			blockx -> LootTable.builder()
					.withPool(
						addSurvivesExplosionLootCondition(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(blockx).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("SkullOwner", "SkullOwner"))
								)
						)
					)
		);
		this.registerWithFunction(Blocks.BEE_NEST, BlockLootTableGenerator::createForBeeNest);
		this.registerWithFunction(Blocks.BEEHIVE, BlockLootTableGenerator::createForBeehive);
		this.registerWithFunction(Blocks.BIRCH_LEAVES, blockx -> createForLeaves(blockx, Blocks.BIRCH_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.ACACIA_LEAVES, blockx -> createForLeaves(blockx, Blocks.ACACIA_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.JUNGLE_LEAVES, blockx -> createForLeaves(blockx, Blocks.JUNGLE_SAPLING, JUNGLE_SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.SPRUCE_LEAVES, blockx -> createForLeaves(blockx, Blocks.SPRUCE_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.OAK_LEAVES, blockx -> createForOakLeaves(blockx, Blocks.OAK_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
		this.registerWithFunction(Blocks.DARK_OAK_LEAVES, blockx -> createForOakLeaves(blockx, Blocks.DARK_OAK_SAPLING, SAPLING_DROP_CHANCES_FROM_LEAVES));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS)
			.method_22584(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.register(Blocks.BEETROOTS, createForCrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT)
			.method_22584(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.register(Blocks.WHEAT, createForCrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS)
			.method_22584(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		this.register(
			Blocks.CARROTS,
			addExplosionDecayLootFunction(
				Blocks.CARROTS,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.CARROT)))
					.withPool(
						LootPool.builder()
							.withCondition(builder3)
							.withEntry(ItemEntry.builder(Items.CARROT).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
			)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES)
			.method_22584(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.register(
			Blocks.POTATOES,
			addExplosionDecayLootFunction(
				Blocks.POTATOES,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.POTATO)))
					.withPool(
						LootPool.builder()
							.withCondition(builder4)
							.withEntry(ItemEntry.builder(Items.POTATO).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
					.withPool(
						LootPool.builder().withCondition(builder4).withEntry(ItemEntry.builder(Items.POISONOUS_POTATO).withCondition(RandomChanceLootCondition.builder(0.02F)))
					)
			)
		);
		this.registerWithFunction(
			Blocks.SWEET_BERRY_BUSH,
			blockx -> addExplosionDecayLootFunction(
					blockx,
					LootTable.builder()
						.withPool(
							LootPool.builder()
								.withCondition(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
								)
								.withEntry(ItemEntry.builder(Items.SWEET_BERRIES))
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
								.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
						.withPool(
							LootPool.builder()
								.withCondition(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
								)
								.withEntry(ItemEntry.builder(Items.SWEET_BERRIES))
								.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
				)
		);
		this.registerWithFunction(Blocks.BROWN_MUSHROOM_BLOCK, blockx -> createForLargeMushroomBlock(blockx, Blocks.BROWN_MUSHROOM));
		this.registerWithFunction(Blocks.RED_MUSHROOM_BLOCK, blockx -> createForLargeMushroomBlock(blockx, Blocks.RED_MUSHROOM));
		this.registerWithFunction(Blocks.COAL_ORE, blockx -> createForOreWithSingleItemDrop(blockx, Items.COAL));
		this.registerWithFunction(Blocks.EMERALD_ORE, blockx -> createForOreWithSingleItemDrop(blockx, Items.EMERALD));
		this.registerWithFunction(Blocks.NETHER_QUARTZ_ORE, blockx -> createForOreWithSingleItemDrop(blockx, Items.QUARTZ));
		this.registerWithFunction(Blocks.DIAMOND_ORE, blockx -> createForOreWithSingleItemDrop(blockx, Items.DIAMOND));
		this.registerWithFunction(
			Blocks.NETHER_GOLD_ORE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 6.0F)))
							.withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.registerWithFunction(
			Blocks.LAPIS_ORE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.LAPIS_LAZULI)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F)))
							.withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.registerWithFunction(
			Blocks.COBWEB,
			blockx -> createForNeedingSilkTouchShears(blockx, (LootEntry.Builder<?>)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(Items.STRING)))
		);
		this.registerWithFunction(
			Blocks.DEAD_BUSH,
			blockx -> createForNeedingShears(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.NETHER_SPROUTS,
			blockx -> createForNeedingSilkTouchShears(blockx, (LootEntry.Builder<?>)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(Blocks.NETHER_SPROUTS)))
		);
		this.registerWithFunction(Blocks.SEAGRASS, BlockLootTableGenerator::createForBlockNeedingShears);
		this.registerWithFunction(Blocks.VINE, BlockLootTableGenerator::createForBlockNeedingShears);
		this.register(Blocks.TALL_SEAGRASS, createForBlockNeedingShears(Blocks.SEAGRASS));
		this.registerWithFunction(
			Blocks.LARGE_FERN,
			blockx -> createForNeedingShears(
					Blocks.FERN,
					((LeafEntry.Builder)((LeafEntry.Builder)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(Items.WHEAT_SEEDS)))
							.withCondition(
								BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
							))
						.withCondition(RandomChanceLootCondition.builder(0.125F))
				)
		);
		this.register(
			Blocks.TALL_GRASS,
			createForNeedingShears(
				Blocks.GRASS,
				((LeafEntry.Builder)((LeafEntry.Builder)addSurvivesExplosionLootCondition(Blocks.TALL_GRASS, ItemEntry.builder(Items.WHEAT_SEEDS)))
						.withCondition(
							BlockStatePropertyLootCondition.builder(Blocks.TALL_GRASS)
								.method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
						))
					.withCondition(RandomChanceLootCondition.builder(0.125F))
			)
		);
		this.registerWithFunction(Blocks.MELON_STEM, blockx -> createForCropStem(blockx, Items.MELON_SEEDS));
		this.registerWithFunction(Blocks.ATTACHED_MELON_STEM, blockx -> createForAttachedCropStem(blockx, Items.MELON_SEEDS));
		this.registerWithFunction(Blocks.PUMPKIN_STEM, blockx -> createForCropStem(blockx, Items.PUMPKIN_SEEDS));
		this.registerWithFunction(Blocks.ATTACHED_PUMPKIN_STEM, blockx -> createForAttachedCropStem(blockx, Items.PUMPKIN_SEEDS));
		this.registerWithFunction(
			Blocks.CHORUS_FLOWER,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								((LeafEntry.Builder)addSurvivesExplosionLootCondition(blockx, ItemEntry.builder(blockx)))
									.withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							)
					)
		);
		this.registerWithFunction(Blocks.FERN, BlockLootTableGenerator::createForTallGrass);
		this.registerWithFunction(Blocks.GRASS, BlockLootTableGenerator::createForTallGrass);
		this.registerWithFunction(
			Blocks.GLOWSTONE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.GLOWSTONE_DUST)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.MELON,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.MELON_SLICE)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.REDSTONE_ORE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.REDSTONE)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 5.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
					)
				)
		);
		this.registerWithFunction(
			Blocks.SEA_LANTERN,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addExplosionDecayLootFunction(
						blockx,
						ItemEntry.builder(Items.PRISMARINE_CRYSTALS)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
							.withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.NETHER_WART,
			blockx -> LootTable.builder()
					.withPool(
						addExplosionDecayLootFunction(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(Items.NETHER_WART)
										.withFunction(
											SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.withFunction(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.registerWithFunction(
			Blocks.SNOW,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							.withEntry(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6))),
											ItemEntry.builder(Items.SNOWBALL)
												.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))
												.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7))),
											ItemEntry.builder(Items.SNOWBALL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))
										)
										.withCondition(DOESNT_NEED_SILK_TOUCH),
									AlternativeEntry.builder(
										ItemEntry.builder(Blocks.SNOW)
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))),
										ItemEntry.builder(Blocks.SNOW)
											.withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))
											.withCondition(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))),
										ItemEntry.builder(Blocks.SNOW_BLOCK)
									)
								)
							)
					)
		);
		this.registerWithFunction(
			Blocks.GRAVEL,
			blockx -> createForNeedingSilkTouch(
					blockx,
					addSurvivesExplosionLootCondition(
						blockx,
						ItemEntry.builder(Items.FLINT)
							.withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.withChild(ItemEntry.builder(blockx))
					)
				)
		);
		this.registerWithFunction(
			Blocks.CAMPFIRE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addSurvivesExplosionLootCondition(
						blockx, ItemEntry.builder(Items.CHARCOAL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
					)
				)
		);
		this.registerWithFunction(
			Blocks.GILDED_BLACKSTONE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					addSurvivesExplosionLootCondition(
						blockx,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 5.0F)))
							.withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.withChild(ItemEntry.builder(blockx))
					)
				)
		);
		this.registerWithFunction(
			Blocks.SOUL_CAMPFIRE,
			blockx -> createForNeedingSilkTouch(
					blockx,
					(LootEntry.Builder<?>)addSurvivesExplosionLootCondition(
						blockx, ItemEntry.builder(Items.SOUL_SOIL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(1)))
					)
				)
		);
		this.registerForNeedingSilkTouch(Blocks.GLASS);
		this.registerForNeedingSilkTouch(Blocks.WHITE_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.ORANGE_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.YELLOW_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.LIME_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.PINK_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.GRAY_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.CYAN_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.PURPLE_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.BLUE_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.BROWN_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.GREEN_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.RED_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.BLACK_STAINED_GLASS);
		this.registerForNeedingSilkTouch(Blocks.GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
		this.registerForNeedingSilkTouch(Blocks.ICE);
		this.registerForNeedingSilkTouch(Blocks.PACKED_ICE);
		this.registerForNeedingSilkTouch(Blocks.BLUE_ICE);
		this.registerForNeedingSilkTouch(Blocks.TURTLE_EGG);
		this.registerForNeedingSilkTouch(Blocks.MUSHROOM_STEM);
		this.registerForNeedingSilkTouch(Blocks.DEAD_TUBE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.DEAD_BRAIN_CORAL);
		this.registerForNeedingSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.DEAD_FIRE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.DEAD_HORN_CORAL);
		this.registerForNeedingSilkTouch(Blocks.TUBE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.BRAIN_CORAL);
		this.registerForNeedingSilkTouch(Blocks.BUBBLE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.FIRE_CORAL);
		this.registerForNeedingSilkTouch(Blocks.HORN_CORAL);
		this.registerForNeedingSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.TUBE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.BRAIN_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.BUBBLE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.FIRE_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.HORN_CORAL_FAN);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
		this.registerForNeedingSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
		this.method_26000(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
		this.method_26000(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
		this.register(Blocks.CAKE, createEmpty());
		this.register(Blocks.FROSTED_ICE, createEmpty());
		this.register(Blocks.SPAWNER, createEmpty());
		this.register(Blocks.FIRE, createEmpty());
		this.register(Blocks.SOUL_FIRE, createEmpty());
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

	private void method_26000(Block block, Block block2) {
		LootTable.Builder builder = createForNeedingSilkTouchShears(
			block, ItemEntry.builder(block).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F))
		);
		this.register(block, builder);
		this.register(block2, builder);
	}

	public static LootTable.Builder method_24817(Block block) {
		return createForMultiblock(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
	}

	public void registerForPottedPlant(Block block) {
		this.registerWithFunction(block, blockx -> createForPottedPlant(((FlowerPotBlock)blockx).getContent()));
	}

	public void registerForNeedingSilkTouch(Block block, Block droppedBlock) {
		this.register(block, createForNeedingSilkTouch(droppedBlock));
	}

	public void register(Block block, ItemConvertible loot) {
		this.register(block, create(loot));
	}

	public void registerForNeedingSilkTouch(Block block) {
		this.registerForNeedingSilkTouch(block, block);
	}

	public void registerForSelfDrop(Block block) {
		this.register(block, block);
	}

	private void registerWithFunction(Block block, Function<Block, LootTable.Builder> function) {
		this.register(block, (LootTable.Builder)function.apply(block));
	}

	private void register(Block block, LootTable.Builder builder) {
		this.lootTables.put(block.getLootTableId(), builder);
	}
}
