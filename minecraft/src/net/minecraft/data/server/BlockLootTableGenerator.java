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
import net.minecraft.block.BeeHiveBlock;
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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.BinomialLootTableRange;
import net.minecraft.world.loot.ConstantLootTableRange;
import net.minecraft.world.loot.LootPool;
import net.minecraft.world.loot.LootTable;
import net.minecraft.world.loot.LootTableRange;
import net.minecraft.world.loot.LootTables;
import net.minecraft.world.loot.UniformLootTableRange;
import net.minecraft.world.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.world.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.condition.LootConditionConsumingBuilder;
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
import net.minecraft.world.loot.function.CopyStateFunction;
import net.minecraft.world.loot.function.ExplosionDecayLootFunction;
import net.minecraft.world.loot.function.LimitCountLootFunction;
import net.minecraft.world.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.world.loot.function.SetContentsLootFunction;
import net.minecraft.world.loot.function.SetCountLootFunction;

public class BlockLootTableGenerator implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
	private static final LootCondition.Builder field_11336 = MatchToolLootCondition.builder(
		ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))
	);
	private static final LootCondition.Builder field_11337 = field_11336.invert();
	private static final LootCondition.Builder field_11343 = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
	private static final LootCondition.Builder field_11342 = field_11343.withCondition(field_11336);
	private static final LootCondition.Builder field_11341 = field_11342.invert();
	private static final Set<Item> field_11340 = (Set<Item>)Stream.of(
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
	private static final float[] field_11339 = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] field_11338 = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
	private final Map<Identifier, LootTable.Builder> field_16493 = Maps.<Identifier, LootTable.Builder>newHashMap();

	private static <T> T method_10393(ItemConvertible itemConvertible, LootFunctionConsumingBuilder<T> lootFunctionConsumingBuilder) {
		return !field_11340.contains(itemConvertible.asItem())
			? lootFunctionConsumingBuilder.withFunction(ExplosionDecayLootFunction.builder())
			: lootFunctionConsumingBuilder.getThis();
	}

	private static <T> T method_10392(ItemConvertible itemConvertible, LootConditionConsumingBuilder<T> lootConditionConsumingBuilder) {
		return !field_11340.contains(itemConvertible.asItem())
			? lootConditionConsumingBuilder.withCondition(SurvivesExplosionLootCondition.builder())
			: lootConditionConsumingBuilder.getThis();
	}

	private static LootTable.Builder method_10394(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
	}

	private static LootTable.Builder method_10381(Block block, LootCondition.Builder builder, LootEntry.Builder<?> builder2) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(block).method_421(builder).withChild(builder2)));
	}

	private static LootTable.Builder method_10397(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11336, builder);
	}

	private static LootTable.Builder method_10380(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11343, builder);
	}

	private static LootTable.Builder method_10388(Block block, LootEntry.Builder<?> builder) {
		return method_10381(block, field_11342, builder);
	}

	private static LootTable.Builder method_10382(Block block, ItemConvertible itemConvertible) {
		return method_10397(block, (LootEntry.Builder<?>)method_10392(block, ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder method_10384(ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)method_10393(itemConvertible, ItemEntry.builder(itemConvertible).method_438(SetCountLootFunction.builder(lootTableRange)))
					)
			);
	}

	private static LootTable.Builder method_10386(Block block, ItemConvertible itemConvertible, LootTableRange lootTableRange) {
		return method_10397(
			block, (LootEntry.Builder<?>)method_10393(block, ItemEntry.builder(itemConvertible).method_438(SetCountLootFunction.builder(lootTableRange)))
		);
	}

	private static LootTable.Builder method_10373(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().method_356(field_11336).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder method_10389(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(method_10392(Blocks.FLOWER_POT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.FLOWER_POT))))
			.withPool(method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
	}

	private static LootTable.Builder method_10383(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						(LootEntry.Builder<?>)method_10393(
							block,
							ItemEntry.builder(block)
								.method_438(
									SetCountLootFunction.builder(ConstantLootTableRange.create(2))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))
								)
						)
					)
			);
	}

	private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder method_10375(Block block, Property<T> property, T comparable) {
		return LootTable.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.method_421(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(property, comparable)))
						)
				)
			);
	}

	private static LootTable.Builder method_10396(Block block) {
		return LootTable.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(ItemEntry.builder(block).method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))
				)
			);
	}

	private static LootTable.Builder method_16876(Block block) {
		return LootTable.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.method_438(
									CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY)
										.withOperation("Lock", "BlockEntityTag.Lock")
										.withOperation("LootTable", "BlockEntityTag.LootTable")
										.withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")
								)
								.method_438(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))
						)
				)
			);
	}

	private static LootTable.Builder method_16877(Block block) {
		return LootTable.builder()
			.withPool(
				method_10392(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(block)
								.method_438(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
								.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns"))
						)
				)
			);
	}

	private static LootTable.Builder method_22142(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.method_356(field_11336)
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.method_438(CopyStateFunction.getBuilder(block).method_21898(BeeHiveBlock.HONEY_LEVEL))
					)
			);
	}

	private static LootTable.Builder method_22143(Block block) {
		return LootTable.builder()
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.withEntry(
						ItemEntry.builder(block)
							.method_421(field_11336)
							.method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))
							.method_438(CopyStateFunction.getBuilder(block).method_21898(BeeHiveBlock.HONEY_LEVEL))
							.withChild(ItemEntry.builder(block))
					)
			);
	}

	private static LootTable.Builder method_10377(Block block, Item item) {
		return method_10397(
			block, (LootEntry.Builder<?>)method_10393(block, ItemEntry.builder(item).method_438(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))
		);
	}

	private static LootTable.Builder method_10385(Block block, ItemConvertible itemConvertible) {
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

	private static LootTable.Builder method_10371(Block block) {
		return method_10380(
			block,
			(LootEntry.Builder<?>)method_10393(
				block,
				ItemEntry.builder(Items.WHEAT_SEEDS)
					.method_421(RandomChanceLootCondition.builder(0.125F))
					.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))
			)
		);
	}

	private static LootTable.Builder method_10387(Block block, Item item) {
		return LootTable.builder()
			.withPool(
				method_10393(
					block,
					LootPool.builder()
						.withRolls(ConstantLootTableRange.create(1))
						.withEntry(
							ItemEntry.builder(item)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 0)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 1)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6)))
								)
								.method_438(
									SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336F))
										.method_524(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))
								)
						)
				)
			);
	}

	private static LootTable.Builder method_10372(ItemConvertible itemConvertible) {
		return LootTable.builder()
			.withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).method_356(field_11343).withEntry(ItemEntry.builder(itemConvertible)));
	}

	private static LootTable.Builder method_10390(Block block, Block block2, float... fs) {
		return method_10388(
				block, ((LeafEntry.Builder)method_10392(block, ItemEntry.builder(block2))).method_421(TableBonusLootCondition.builder(Enchantments.FORTUNE, fs))
			)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.method_356(field_11341)
					.withEntry(
						((LeafEntry.Builder)method_10393(
								block, ItemEntry.builder(Items.STICK).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
							))
							.method_421(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))
					)
			);
	}

	private static LootTable.Builder method_10378(Block block, Block block2, float... fs) {
		return method_10390(block, block2, fs)
			.withPool(
				LootPool.builder()
					.withRolls(ConstantLootTableRange.create(1))
					.method_356(field_11341)
					.withEntry(
						((LeafEntry.Builder)method_10392(block, ItemEntry.builder(Items.APPLE)))
							.method_421(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	private static LootTable.Builder method_10391(Block block, Item item, Item item2, LootCondition.Builder builder) {
		return method_10393(
			block,
			LootTable.builder()
				.withPool(LootPool.builder().withEntry(ItemEntry.builder(item).method_421(builder).withChild(ItemEntry.builder(item2))))
				.withPool(
					LootPool.builder()
						.method_356(builder)
						.withEntry(ItemEntry.builder(item2).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
				)
		);
	}

	public static LootTable.Builder method_10395() {
		return LootTable.builder();
	}

	public void method_10379(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
		this.method_16329(Blocks.GRANITE);
		this.method_16329(Blocks.POLISHED_GRANITE);
		this.method_16329(Blocks.DIORITE);
		this.method_16329(Blocks.POLISHED_DIORITE);
		this.method_16329(Blocks.ANDESITE);
		this.method_16329(Blocks.POLISHED_ANDESITE);
		this.method_16329(Blocks.DIRT);
		this.method_16329(Blocks.COARSE_DIRT);
		this.method_16329(Blocks.COBBLESTONE);
		this.method_16329(Blocks.OAK_PLANKS);
		this.method_16329(Blocks.SPRUCE_PLANKS);
		this.method_16329(Blocks.BIRCH_PLANKS);
		this.method_16329(Blocks.JUNGLE_PLANKS);
		this.method_16329(Blocks.ACACIA_PLANKS);
		this.method_16329(Blocks.DARK_OAK_PLANKS);
		this.method_16329(Blocks.OAK_SAPLING);
		this.method_16329(Blocks.SPRUCE_SAPLING);
		this.method_16329(Blocks.BIRCH_SAPLING);
		this.method_16329(Blocks.JUNGLE_SAPLING);
		this.method_16329(Blocks.ACACIA_SAPLING);
		this.method_16329(Blocks.DARK_OAK_SAPLING);
		this.method_16329(Blocks.SAND);
		this.method_16329(Blocks.RED_SAND);
		this.method_16329(Blocks.GOLD_ORE);
		this.method_16329(Blocks.IRON_ORE);
		this.method_16329(Blocks.OAK_LOG);
		this.method_16329(Blocks.SPRUCE_LOG);
		this.method_16329(Blocks.BIRCH_LOG);
		this.method_16329(Blocks.JUNGLE_LOG);
		this.method_16329(Blocks.ACACIA_LOG);
		this.method_16329(Blocks.DARK_OAK_LOG);
		this.method_16329(Blocks.STRIPPED_SPRUCE_LOG);
		this.method_16329(Blocks.STRIPPED_BIRCH_LOG);
		this.method_16329(Blocks.STRIPPED_JUNGLE_LOG);
		this.method_16329(Blocks.STRIPPED_ACACIA_LOG);
		this.method_16329(Blocks.STRIPPED_DARK_OAK_LOG);
		this.method_16329(Blocks.STRIPPED_OAK_LOG);
		this.method_16329(Blocks.OAK_WOOD);
		this.method_16329(Blocks.SPRUCE_WOOD);
		this.method_16329(Blocks.BIRCH_WOOD);
		this.method_16329(Blocks.JUNGLE_WOOD);
		this.method_16329(Blocks.ACACIA_WOOD);
		this.method_16329(Blocks.DARK_OAK_WOOD);
		this.method_16329(Blocks.STRIPPED_OAK_WOOD);
		this.method_16329(Blocks.STRIPPED_SPRUCE_WOOD);
		this.method_16329(Blocks.STRIPPED_BIRCH_WOOD);
		this.method_16329(Blocks.STRIPPED_JUNGLE_WOOD);
		this.method_16329(Blocks.STRIPPED_ACACIA_WOOD);
		this.method_16329(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.method_16329(Blocks.SPONGE);
		this.method_16329(Blocks.WET_SPONGE);
		this.method_16329(Blocks.LAPIS_BLOCK);
		this.method_16329(Blocks.SANDSTONE);
		this.method_16329(Blocks.CHISELED_SANDSTONE);
		this.method_16329(Blocks.CUT_SANDSTONE);
		this.method_16329(Blocks.NOTE_BLOCK);
		this.method_16329(Blocks.POWERED_RAIL);
		this.method_16329(Blocks.DETECTOR_RAIL);
		this.method_16329(Blocks.STICKY_PISTON);
		this.method_16329(Blocks.PISTON);
		this.method_16329(Blocks.WHITE_WOOL);
		this.method_16329(Blocks.ORANGE_WOOL);
		this.method_16329(Blocks.MAGENTA_WOOL);
		this.method_16329(Blocks.LIGHT_BLUE_WOOL);
		this.method_16329(Blocks.YELLOW_WOOL);
		this.method_16329(Blocks.LIME_WOOL);
		this.method_16329(Blocks.PINK_WOOL);
		this.method_16329(Blocks.GRAY_WOOL);
		this.method_16329(Blocks.LIGHT_GRAY_WOOL);
		this.method_16329(Blocks.CYAN_WOOL);
		this.method_16329(Blocks.PURPLE_WOOL);
		this.method_16329(Blocks.BLUE_WOOL);
		this.method_16329(Blocks.BROWN_WOOL);
		this.method_16329(Blocks.GREEN_WOOL);
		this.method_16329(Blocks.RED_WOOL);
		this.method_16329(Blocks.BLACK_WOOL);
		this.method_16329(Blocks.DANDELION);
		this.method_16329(Blocks.POPPY);
		this.method_16329(Blocks.BLUE_ORCHID);
		this.method_16329(Blocks.ALLIUM);
		this.method_16329(Blocks.AZURE_BLUET);
		this.method_16329(Blocks.RED_TULIP);
		this.method_16329(Blocks.ORANGE_TULIP);
		this.method_16329(Blocks.WHITE_TULIP);
		this.method_16329(Blocks.PINK_TULIP);
		this.method_16329(Blocks.OXEYE_DAISY);
		this.method_16329(Blocks.CORNFLOWER);
		this.method_16329(Blocks.WITHER_ROSE);
		this.method_16329(Blocks.LILY_OF_THE_VALLEY);
		this.method_16329(Blocks.BROWN_MUSHROOM);
		this.method_16329(Blocks.RED_MUSHROOM);
		this.method_16329(Blocks.GOLD_BLOCK);
		this.method_16329(Blocks.IRON_BLOCK);
		this.method_16329(Blocks.BRICKS);
		this.method_16329(Blocks.MOSSY_COBBLESTONE);
		this.method_16329(Blocks.OBSIDIAN);
		this.method_16329(Blocks.TORCH);
		this.method_16329(Blocks.OAK_STAIRS);
		this.method_16329(Blocks.REDSTONE_WIRE);
		this.method_16329(Blocks.DIAMOND_BLOCK);
		this.method_16329(Blocks.CRAFTING_TABLE);
		this.method_16329(Blocks.OAK_SIGN);
		this.method_16329(Blocks.SPRUCE_SIGN);
		this.method_16329(Blocks.BIRCH_SIGN);
		this.method_16329(Blocks.ACACIA_SIGN);
		this.method_16329(Blocks.JUNGLE_SIGN);
		this.method_16329(Blocks.DARK_OAK_SIGN);
		this.method_16329(Blocks.LADDER);
		this.method_16329(Blocks.RAIL);
		this.method_16329(Blocks.COBBLESTONE_STAIRS);
		this.method_16329(Blocks.LEVER);
		this.method_16329(Blocks.STONE_PRESSURE_PLATE);
		this.method_16329(Blocks.OAK_PRESSURE_PLATE);
		this.method_16329(Blocks.SPRUCE_PRESSURE_PLATE);
		this.method_16329(Blocks.BIRCH_PRESSURE_PLATE);
		this.method_16329(Blocks.JUNGLE_PRESSURE_PLATE);
		this.method_16329(Blocks.ACACIA_PRESSURE_PLATE);
		this.method_16329(Blocks.DARK_OAK_PRESSURE_PLATE);
		this.method_16329(Blocks.REDSTONE_TORCH);
		this.method_16329(Blocks.STONE_BUTTON);
		this.method_16329(Blocks.CACTUS);
		this.method_16329(Blocks.SUGAR_CANE);
		this.method_16329(Blocks.JUKEBOX);
		this.method_16329(Blocks.OAK_FENCE);
		this.method_16329(Blocks.PUMPKIN);
		this.method_16329(Blocks.NETHERRACK);
		this.method_16329(Blocks.SOUL_SAND);
		this.method_16329(Blocks.CARVED_PUMPKIN);
		this.method_16329(Blocks.JACK_O_LANTERN);
		this.method_16329(Blocks.REPEATER);
		this.method_16329(Blocks.OAK_TRAPDOOR);
		this.method_16329(Blocks.SPRUCE_TRAPDOOR);
		this.method_16329(Blocks.BIRCH_TRAPDOOR);
		this.method_16329(Blocks.JUNGLE_TRAPDOOR);
		this.method_16329(Blocks.ACACIA_TRAPDOOR);
		this.method_16329(Blocks.DARK_OAK_TRAPDOOR);
		this.method_16329(Blocks.STONE_BRICKS);
		this.method_16329(Blocks.MOSSY_STONE_BRICKS);
		this.method_16329(Blocks.CRACKED_STONE_BRICKS);
		this.method_16329(Blocks.CHISELED_STONE_BRICKS);
		this.method_16329(Blocks.IRON_BARS);
		this.method_16329(Blocks.OAK_FENCE_GATE);
		this.method_16329(Blocks.BRICK_STAIRS);
		this.method_16329(Blocks.STONE_BRICK_STAIRS);
		this.method_16329(Blocks.LILY_PAD);
		this.method_16329(Blocks.NETHER_BRICKS);
		this.method_16329(Blocks.NETHER_BRICK_FENCE);
		this.method_16329(Blocks.NETHER_BRICK_STAIRS);
		this.method_16329(Blocks.CAULDRON);
		this.method_16329(Blocks.END_STONE);
		this.method_16329(Blocks.REDSTONE_LAMP);
		this.method_16329(Blocks.SANDSTONE_STAIRS);
		this.method_16329(Blocks.TRIPWIRE_HOOK);
		this.method_16329(Blocks.EMERALD_BLOCK);
		this.method_16329(Blocks.SPRUCE_STAIRS);
		this.method_16329(Blocks.BIRCH_STAIRS);
		this.method_16329(Blocks.JUNGLE_STAIRS);
		this.method_16329(Blocks.COBBLESTONE_WALL);
		this.method_16329(Blocks.MOSSY_COBBLESTONE_WALL);
		this.method_16329(Blocks.FLOWER_POT);
		this.method_16329(Blocks.OAK_BUTTON);
		this.method_16329(Blocks.SPRUCE_BUTTON);
		this.method_16329(Blocks.BIRCH_BUTTON);
		this.method_16329(Blocks.JUNGLE_BUTTON);
		this.method_16329(Blocks.ACACIA_BUTTON);
		this.method_16329(Blocks.DARK_OAK_BUTTON);
		this.method_16329(Blocks.SKELETON_SKULL);
		this.method_16329(Blocks.WITHER_SKELETON_SKULL);
		this.method_16329(Blocks.ZOMBIE_HEAD);
		this.method_16329(Blocks.CREEPER_HEAD);
		this.method_16329(Blocks.DRAGON_HEAD);
		this.method_16329(Blocks.ANVIL);
		this.method_16329(Blocks.CHIPPED_ANVIL);
		this.method_16329(Blocks.DAMAGED_ANVIL);
		this.method_16329(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
		this.method_16329(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
		this.method_16329(Blocks.COMPARATOR);
		this.method_16329(Blocks.DAYLIGHT_DETECTOR);
		this.method_16329(Blocks.REDSTONE_BLOCK);
		this.method_16329(Blocks.QUARTZ_BLOCK);
		this.method_16329(Blocks.CHISELED_QUARTZ_BLOCK);
		this.method_16329(Blocks.QUARTZ_PILLAR);
		this.method_16329(Blocks.QUARTZ_STAIRS);
		this.method_16329(Blocks.ACTIVATOR_RAIL);
		this.method_16329(Blocks.WHITE_TERRACOTTA);
		this.method_16329(Blocks.ORANGE_TERRACOTTA);
		this.method_16329(Blocks.MAGENTA_TERRACOTTA);
		this.method_16329(Blocks.LIGHT_BLUE_TERRACOTTA);
		this.method_16329(Blocks.YELLOW_TERRACOTTA);
		this.method_16329(Blocks.LIME_TERRACOTTA);
		this.method_16329(Blocks.PINK_TERRACOTTA);
		this.method_16329(Blocks.GRAY_TERRACOTTA);
		this.method_16329(Blocks.LIGHT_GRAY_TERRACOTTA);
		this.method_16329(Blocks.CYAN_TERRACOTTA);
		this.method_16329(Blocks.PURPLE_TERRACOTTA);
		this.method_16329(Blocks.BLUE_TERRACOTTA);
		this.method_16329(Blocks.BROWN_TERRACOTTA);
		this.method_16329(Blocks.GREEN_TERRACOTTA);
		this.method_16329(Blocks.RED_TERRACOTTA);
		this.method_16329(Blocks.BLACK_TERRACOTTA);
		this.method_16329(Blocks.ACACIA_STAIRS);
		this.method_16329(Blocks.DARK_OAK_STAIRS);
		this.method_16329(Blocks.SLIME_BLOCK);
		this.method_16329(Blocks.IRON_TRAPDOOR);
		this.method_16329(Blocks.PRISMARINE);
		this.method_16329(Blocks.PRISMARINE_BRICKS);
		this.method_16329(Blocks.DARK_PRISMARINE);
		this.method_16329(Blocks.PRISMARINE_STAIRS);
		this.method_16329(Blocks.PRISMARINE_BRICK_STAIRS);
		this.method_16329(Blocks.DARK_PRISMARINE_STAIRS);
		this.method_16329(Blocks.HAY_BLOCK);
		this.method_16329(Blocks.WHITE_CARPET);
		this.method_16329(Blocks.ORANGE_CARPET);
		this.method_16329(Blocks.MAGENTA_CARPET);
		this.method_16329(Blocks.LIGHT_BLUE_CARPET);
		this.method_16329(Blocks.YELLOW_CARPET);
		this.method_16329(Blocks.LIME_CARPET);
		this.method_16329(Blocks.PINK_CARPET);
		this.method_16329(Blocks.GRAY_CARPET);
		this.method_16329(Blocks.LIGHT_GRAY_CARPET);
		this.method_16329(Blocks.CYAN_CARPET);
		this.method_16329(Blocks.PURPLE_CARPET);
		this.method_16329(Blocks.BLUE_CARPET);
		this.method_16329(Blocks.BROWN_CARPET);
		this.method_16329(Blocks.GREEN_CARPET);
		this.method_16329(Blocks.RED_CARPET);
		this.method_16329(Blocks.BLACK_CARPET);
		this.method_16329(Blocks.TERRACOTTA);
		this.method_16329(Blocks.COAL_BLOCK);
		this.method_16329(Blocks.RED_SANDSTONE);
		this.method_16329(Blocks.CHISELED_RED_SANDSTONE);
		this.method_16329(Blocks.CUT_RED_SANDSTONE);
		this.method_16329(Blocks.RED_SANDSTONE_STAIRS);
		this.method_16329(Blocks.SMOOTH_STONE);
		this.method_16329(Blocks.SMOOTH_SANDSTONE);
		this.method_16329(Blocks.SMOOTH_QUARTZ);
		this.method_16329(Blocks.SMOOTH_RED_SANDSTONE);
		this.method_16329(Blocks.SPRUCE_FENCE_GATE);
		this.method_16329(Blocks.BIRCH_FENCE_GATE);
		this.method_16329(Blocks.JUNGLE_FENCE_GATE);
		this.method_16329(Blocks.ACACIA_FENCE_GATE);
		this.method_16329(Blocks.DARK_OAK_FENCE_GATE);
		this.method_16329(Blocks.SPRUCE_FENCE);
		this.method_16329(Blocks.BIRCH_FENCE);
		this.method_16329(Blocks.JUNGLE_FENCE);
		this.method_16329(Blocks.ACACIA_FENCE);
		this.method_16329(Blocks.DARK_OAK_FENCE);
		this.method_16329(Blocks.END_ROD);
		this.method_16329(Blocks.PURPUR_BLOCK);
		this.method_16329(Blocks.PURPUR_PILLAR);
		this.method_16329(Blocks.PURPUR_STAIRS);
		this.method_16329(Blocks.END_STONE_BRICKS);
		this.method_16329(Blocks.MAGMA_BLOCK);
		this.method_16329(Blocks.NETHER_WART_BLOCK);
		this.method_16329(Blocks.RED_NETHER_BRICKS);
		this.method_16329(Blocks.BONE_BLOCK);
		this.method_16329(Blocks.OBSERVER);
		this.method_16329(Blocks.WHITE_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.ORANGE_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.MAGENTA_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.YELLOW_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.LIME_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.PINK_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.GRAY_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.CYAN_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.PURPLE_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.BLUE_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.BROWN_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.GREEN_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.RED_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.BLACK_GLAZED_TERRACOTTA);
		this.method_16329(Blocks.WHITE_CONCRETE);
		this.method_16329(Blocks.ORANGE_CONCRETE);
		this.method_16329(Blocks.MAGENTA_CONCRETE);
		this.method_16329(Blocks.LIGHT_BLUE_CONCRETE);
		this.method_16329(Blocks.YELLOW_CONCRETE);
		this.method_16329(Blocks.LIME_CONCRETE);
		this.method_16329(Blocks.PINK_CONCRETE);
		this.method_16329(Blocks.GRAY_CONCRETE);
		this.method_16329(Blocks.LIGHT_GRAY_CONCRETE);
		this.method_16329(Blocks.CYAN_CONCRETE);
		this.method_16329(Blocks.PURPLE_CONCRETE);
		this.method_16329(Blocks.BLUE_CONCRETE);
		this.method_16329(Blocks.BROWN_CONCRETE);
		this.method_16329(Blocks.GREEN_CONCRETE);
		this.method_16329(Blocks.RED_CONCRETE);
		this.method_16329(Blocks.BLACK_CONCRETE);
		this.method_16329(Blocks.WHITE_CONCRETE_POWDER);
		this.method_16329(Blocks.ORANGE_CONCRETE_POWDER);
		this.method_16329(Blocks.MAGENTA_CONCRETE_POWDER);
		this.method_16329(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
		this.method_16329(Blocks.YELLOW_CONCRETE_POWDER);
		this.method_16329(Blocks.LIME_CONCRETE_POWDER);
		this.method_16329(Blocks.PINK_CONCRETE_POWDER);
		this.method_16329(Blocks.GRAY_CONCRETE_POWDER);
		this.method_16329(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
		this.method_16329(Blocks.CYAN_CONCRETE_POWDER);
		this.method_16329(Blocks.PURPLE_CONCRETE_POWDER);
		this.method_16329(Blocks.BLUE_CONCRETE_POWDER);
		this.method_16329(Blocks.BROWN_CONCRETE_POWDER);
		this.method_16329(Blocks.GREEN_CONCRETE_POWDER);
		this.method_16329(Blocks.RED_CONCRETE_POWDER);
		this.method_16329(Blocks.BLACK_CONCRETE_POWDER);
		this.method_16329(Blocks.KELP);
		this.method_16329(Blocks.DRIED_KELP_BLOCK);
		this.method_16329(Blocks.DEAD_TUBE_CORAL_BLOCK);
		this.method_16329(Blocks.DEAD_BRAIN_CORAL_BLOCK);
		this.method_16329(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
		this.method_16329(Blocks.DEAD_FIRE_CORAL_BLOCK);
		this.method_16329(Blocks.DEAD_HORN_CORAL_BLOCK);
		this.method_16329(Blocks.CONDUIT);
		this.method_16329(Blocks.DRAGON_EGG);
		this.method_16329(Blocks.BAMBOO);
		this.method_16329(Blocks.POLISHED_GRANITE_STAIRS);
		this.method_16329(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
		this.method_16329(Blocks.MOSSY_STONE_BRICK_STAIRS);
		this.method_16329(Blocks.POLISHED_DIORITE_STAIRS);
		this.method_16329(Blocks.MOSSY_COBBLESTONE_STAIRS);
		this.method_16329(Blocks.END_STONE_BRICK_STAIRS);
		this.method_16329(Blocks.STONE_STAIRS);
		this.method_16329(Blocks.SMOOTH_SANDSTONE_STAIRS);
		this.method_16329(Blocks.SMOOTH_QUARTZ_STAIRS);
		this.method_16329(Blocks.GRANITE_STAIRS);
		this.method_16329(Blocks.ANDESITE_STAIRS);
		this.method_16329(Blocks.RED_NETHER_BRICK_STAIRS);
		this.method_16329(Blocks.POLISHED_ANDESITE_STAIRS);
		this.method_16329(Blocks.DIORITE_STAIRS);
		this.method_16329(Blocks.BRICK_WALL);
		this.method_16329(Blocks.PRISMARINE_WALL);
		this.method_16329(Blocks.RED_SANDSTONE_WALL);
		this.method_16329(Blocks.MOSSY_STONE_BRICK_WALL);
		this.method_16329(Blocks.GRANITE_WALL);
		this.method_16329(Blocks.STONE_BRICK_WALL);
		this.method_16329(Blocks.NETHER_BRICK_WALL);
		this.method_16329(Blocks.ANDESITE_WALL);
		this.method_16329(Blocks.RED_NETHER_BRICK_WALL);
		this.method_16329(Blocks.SANDSTONE_WALL);
		this.method_16329(Blocks.END_STONE_BRICK_WALL);
		this.method_16329(Blocks.DIORITE_WALL);
		this.method_16329(Blocks.LOOM);
		this.method_16329(Blocks.SCAFFOLDING);
		this.method_16256(Blocks.FARMLAND, Blocks.DIRT);
		this.method_16256(Blocks.TRIPWIRE, Items.STRING);
		this.method_16256(Blocks.GRASS_PATH, Blocks.DIRT);
		this.method_16256(Blocks.KELP_PLANT, Blocks.KELP);
		this.method_16256(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
		this.method_16293(Blocks.STONE, blockx -> method_10382(blockx, Blocks.COBBLESTONE));
		this.method_16293(Blocks.GRASS_BLOCK, blockx -> method_10382(blockx, Blocks.DIRT));
		this.method_16293(Blocks.PODZOL, blockx -> method_10382(blockx, Blocks.DIRT));
		this.method_16293(Blocks.MYCELIUM, blockx -> method_10382(blockx, Blocks.DIRT));
		this.method_16293(Blocks.TUBE_CORAL_BLOCK, blockx -> method_10382(blockx, Blocks.DEAD_TUBE_CORAL_BLOCK));
		this.method_16293(Blocks.BRAIN_CORAL_BLOCK, blockx -> method_10382(blockx, Blocks.DEAD_BRAIN_CORAL_BLOCK));
		this.method_16293(Blocks.BUBBLE_CORAL_BLOCK, blockx -> method_10382(blockx, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
		this.method_16293(Blocks.FIRE_CORAL_BLOCK, blockx -> method_10382(blockx, Blocks.DEAD_FIRE_CORAL_BLOCK));
		this.method_16293(Blocks.HORN_CORAL_BLOCK, blockx -> method_10382(blockx, Blocks.DEAD_HORN_CORAL_BLOCK));
		this.method_16293(Blocks.BOOKSHELF, blockx -> method_10386(blockx, Items.BOOK, ConstantLootTableRange.create(3)));
		this.method_16293(Blocks.CLAY, blockx -> method_10386(blockx, Items.CLAY_BALL, ConstantLootTableRange.create(4)));
		this.method_16293(Blocks.ENDER_CHEST, blockx -> method_10386(blockx, Blocks.OBSIDIAN, ConstantLootTableRange.create(8)));
		this.method_16293(Blocks.SNOW_BLOCK, blockx -> method_10386(blockx, Items.SNOWBALL, ConstantLootTableRange.create(4)));
		this.method_16258(Blocks.CHORUS_PLANT, method_10384(Items.CHORUS_FRUIT, UniformLootTableRange.between(0.0F, 1.0F)));
		this.method_16285(Blocks.POTTED_OAK_SAPLING);
		this.method_16285(Blocks.POTTED_SPRUCE_SAPLING);
		this.method_16285(Blocks.POTTED_BIRCH_SAPLING);
		this.method_16285(Blocks.POTTED_JUNGLE_SAPLING);
		this.method_16285(Blocks.POTTED_ACACIA_SAPLING);
		this.method_16285(Blocks.POTTED_DARK_OAK_SAPLING);
		this.method_16285(Blocks.POTTED_FERN);
		this.method_16285(Blocks.POTTED_DANDELION);
		this.method_16285(Blocks.POTTED_POPPY);
		this.method_16285(Blocks.POTTED_BLUE_ORCHID);
		this.method_16285(Blocks.POTTED_ALLIUM);
		this.method_16285(Blocks.POTTED_AZURE_BLUET);
		this.method_16285(Blocks.POTTED_RED_TULIP);
		this.method_16285(Blocks.POTTED_ORANGE_TULIP);
		this.method_16285(Blocks.POTTED_WHITE_TULIP);
		this.method_16285(Blocks.POTTED_PINK_TULIP);
		this.method_16285(Blocks.POTTED_OXEYE_DAISY);
		this.method_16285(Blocks.POTTED_CORNFLOWER);
		this.method_16285(Blocks.POTTED_LILY_OF_THE_VALLEY);
		this.method_16285(Blocks.POTTED_WITHER_ROSE);
		this.method_16285(Blocks.POTTED_RED_MUSHROOM);
		this.method_16285(Blocks.POTTED_BROWN_MUSHROOM);
		this.method_16285(Blocks.POTTED_DEAD_BUSH);
		this.method_16285(Blocks.POTTED_CACTUS);
		this.method_16285(Blocks.POTTED_BAMBOO);
		this.method_16293(Blocks.ACACIA_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.BIRCH_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.COBBLESTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.DARK_OAK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.DARK_PRISMARINE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.JUNGLE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.NETHER_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.OAK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.PETRIFIED_OAK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.PRISMARINE_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.PRISMARINE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.PURPUR_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.QUARTZ_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.RED_SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.CUT_SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SPRUCE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.STONE_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.STONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SMOOTH_STONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.POLISHED_GRANITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.POLISHED_DIORITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.END_STONE_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.SMOOTH_QUARTZ_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.GRANITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.ANDESITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.RED_NETHER_BRICK_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.POLISHED_ANDESITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.DIORITE_SLAB, BlockLootTableGenerator::method_10383);
		this.method_16293(Blocks.ACACIA_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.BIRCH_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.DARK_OAK_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.IRON_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.JUNGLE_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.OAK_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.SPRUCE_DOOR, blockx -> method_10375(blockx, DoorBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.BLACK_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.BLUE_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.BROWN_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.CYAN_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.GRAY_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.GREEN_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.LIGHT_BLUE_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.LIGHT_GRAY_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.LIME_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.MAGENTA_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.PURPLE_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.ORANGE_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.PINK_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.RED_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.WHITE_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.YELLOW_BED, blockx -> method_10375(blockx, BedBlock.PART, BedPart.HEAD));
		this.method_16293(Blocks.LILAC, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.SUNFLOWER, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.PEONY, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16293(Blocks.ROSE_BUSH, blockx -> method_10375(blockx, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.method_16258(
			Blocks.TNT,
			LootTable.builder()
				.withPool(
					method_10392(
						Blocks.TNT,
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								ItemEntry.builder(Blocks.TNT)
									.method_421(BlockStatePropertyLootCondition.builder(Blocks.TNT).method_22584(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))
							)
					)
				)
		);
		this.method_16293(
			Blocks.COCOA,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)method_10393(
									blockx,
									ItemEntry.builder(Items.COCOA_BEANS)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.method_16293(
			Blocks.SEA_PICKLE,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								(LootEntry.Builder<?>)method_10393(
									Blocks.SEA_PICKLE,
									ItemEntry.builder(blockx)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(2))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2)))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(3))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3)))
										)
										.method_438(
											SetCountLootFunction.builder(ConstantLootTableRange.create(4))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4)))
										)
								)
							)
					)
		);
		this.method_16293(
			Blocks.COMPOSTER,
			blockx -> LootTable.builder()
					.withPool(LootPool.builder().withEntry((LootEntry.Builder<?>)method_10393(blockx, ItemEntry.builder(Items.COMPOSTER))))
					.withPool(
						LootPool.builder()
							.withEntry(ItemEntry.builder(Items.BONE_MEAL))
							.method_356(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.method_16293(Blocks.BEACON, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.BREWING_STAND, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.CHEST, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.DISPENSER, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.DROPPER, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.ENCHANTING_TABLE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.FURNACE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.HOPPER, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.TRAPPED_CHEST, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.SMOKER, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.BLAST_FURNACE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.BARREL, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.CARTOGRAPHY_TABLE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.FLETCHING_TABLE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.GRINDSTONE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.LECTERN, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.SMITHING_TABLE, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.STONECUTTER, BlockLootTableGenerator::method_10396);
		this.method_16293(Blocks.BELL, BlockLootTableGenerator::method_10394);
		this.method_16293(Blocks.LANTERN, BlockLootTableGenerator::method_10394);
		this.method_16293(Blocks.SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.BLACK_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.BLUE_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.BROWN_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.CYAN_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.GRAY_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.GREEN_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.LIME_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.MAGENTA_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.ORANGE_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.PINK_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.PURPLE_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.RED_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.WHITE_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.YELLOW_SHULKER_BOX, BlockLootTableGenerator::method_16876);
		this.method_16293(Blocks.BLACK_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.BLUE_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.BROWN_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.CYAN_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.GRAY_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.GREEN_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.LIGHT_BLUE_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.LIGHT_GRAY_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.LIME_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.MAGENTA_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.ORANGE_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.PINK_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.PURPLE_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.RED_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.WHITE_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(Blocks.YELLOW_BANNER, BlockLootTableGenerator::method_16877);
		this.method_16293(
			Blocks.PLAYER_HEAD,
			blockx -> LootTable.builder()
					.withPool(
						method_10392(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(blockx).method_438(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Owner", "SkullOwner"))
								)
						)
					)
		);
		this.method_16293(Blocks.BEE_NEST, BlockLootTableGenerator::method_22142);
		this.method_16293(Blocks.BEE_HIVE, BlockLootTableGenerator::method_22143);
		this.method_16293(Blocks.BIRCH_LEAVES, blockx -> method_10390(blockx, Blocks.BIRCH_SAPLING, field_11339));
		this.method_16293(Blocks.ACACIA_LEAVES, blockx -> method_10390(blockx, Blocks.ACACIA_SAPLING, field_11339));
		this.method_16293(Blocks.JUNGLE_LEAVES, blockx -> method_10390(blockx, Blocks.JUNGLE_SAPLING, field_11338));
		this.method_16293(Blocks.SPRUCE_LEAVES, blockx -> method_10390(blockx, Blocks.SPRUCE_SAPLING, field_11339));
		this.method_16293(Blocks.OAK_LEAVES, blockx -> method_10378(blockx, Blocks.OAK_SAPLING, field_11339));
		this.method_16293(Blocks.DARK_OAK_LEAVES, blockx -> method_10378(blockx, Blocks.DARK_OAK_SAPLING, field_11339));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS)
			.method_22584(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.method_16258(Blocks.BEETROOTS, method_10391(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT)
			.method_22584(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.method_16258(Blocks.WHEAT, method_10391(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS)
			.method_22584(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		this.method_16258(
			Blocks.CARROTS,
			method_10393(
				Blocks.CARROTS,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.CARROT)))
					.withPool(
						LootPool.builder()
							.method_356(builder3)
							.withEntry(ItemEntry.builder(Items.CARROT).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
			)
		);
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES)
			.method_22584(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.method_16258(
			Blocks.POTATOES,
			method_10393(
				Blocks.POTATOES,
				LootTable.builder()
					.withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.POTATO)))
					.withPool(
						LootPool.builder()
							.method_356(builder4)
							.withEntry(ItemEntry.builder(Items.POTATO).method_438(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
					.withPool(
						LootPool.builder().method_356(builder4).withEntry(ItemEntry.builder(Items.POISONOUS_POTATO).method_421(RandomChanceLootCondition.builder(0.02F)))
					)
			)
		);
		this.method_16293(
			Blocks.SWEET_BERRY_BUSH,
			blockx -> method_10393(
					blockx,
					LootTable.builder()
						.withPool(
							LootPool.builder()
								.method_356(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))
								)
								.withEntry(ItemEntry.builder(Items.SWEET_BERRIES))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
						.withPool(
							LootPool.builder()
								.method_356(
									BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))
								)
								.withEntry(ItemEntry.builder(Items.SWEET_BERRIES))
								.method_353(SetCountLootFunction.builder(UniformLootTableRange.between(1.0F, 2.0F)))
								.method_353(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
						)
				)
		);
		this.method_16293(Blocks.BROWN_MUSHROOM_BLOCK, blockx -> method_10385(blockx, Blocks.BROWN_MUSHROOM));
		this.method_16293(Blocks.RED_MUSHROOM_BLOCK, blockx -> method_10385(blockx, Blocks.RED_MUSHROOM));
		this.method_16293(Blocks.COAL_ORE, blockx -> method_10377(blockx, Items.COAL));
		this.method_16293(Blocks.EMERALD_ORE, blockx -> method_10377(blockx, Items.EMERALD));
		this.method_16293(Blocks.NETHER_QUARTZ_ORE, blockx -> method_10377(blockx, Items.QUARTZ));
		this.method_16293(Blocks.DIAMOND_ORE, blockx -> method_10377(blockx, Items.DIAMOND));
		this.method_16293(
			Blocks.LAPIS_ORE,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.LAPIS_LAZULI)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 9.0F)))
							.method_438(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.method_16293(Blocks.COBWEB, blockx -> method_10388(blockx, (LootEntry.Builder<?>)method_10392(blockx, ItemEntry.builder(Items.STRING))));
		this.method_16293(
			Blocks.DEAD_BUSH,
			blockx -> method_10380(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx, ItemEntry.builder(Items.STICK).method_438(SetCountLootFunction.builder(UniformLootTableRange.between(0.0F, 2.0F)))
					)
				)
		);
		this.method_16293(Blocks.SEAGRASS, BlockLootTableGenerator::method_10372);
		this.method_16293(Blocks.VINE, BlockLootTableGenerator::method_10372);
		this.method_16258(Blocks.TALL_SEAGRASS, method_10372(Blocks.SEAGRASS));
		this.method_16293(
			Blocks.LARGE_FERN,
			blockx -> method_10380(
					Blocks.FERN,
					((LeafEntry.Builder)((LeafEntry.Builder)method_10392(blockx, ItemEntry.builder(Items.WHEAT_SEEDS)))
							.method_421(
								BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
							))
						.method_421(RandomChanceLootCondition.builder(0.125F))
				)
		);
		this.method_16258(
			Blocks.TALL_GRASS,
			method_10380(
				Blocks.GRASS,
				((LeafEntry.Builder)((LeafEntry.Builder)method_10392(Blocks.TALL_GRASS, ItemEntry.builder(Items.WHEAT_SEEDS)))
						.method_421(
							BlockStatePropertyLootCondition.builder(Blocks.TALL_GRASS)
								.method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
						))
					.method_421(RandomChanceLootCondition.builder(0.125F))
			)
		);
		this.method_16293(Blocks.MELON_STEM, blockx -> method_10387(blockx, Items.MELON_SEEDS));
		this.method_16293(Blocks.PUMPKIN_STEM, blockx -> method_10387(blockx, Items.PUMPKIN_SEEDS));
		this.method_16293(
			Blocks.CHORUS_FLOWER,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.withRolls(ConstantLootTableRange.create(1))
							.withEntry(
								((LeafEntry.Builder)method_10392(blockx, ItemEntry.builder(blockx))).method_421(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							)
					)
		);
		this.method_16293(Blocks.FERN, BlockLootTableGenerator::method_10371);
		this.method_16293(Blocks.GRASS, BlockLootTableGenerator::method_10371);
		this.method_16293(
			Blocks.GLOWSTONE,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.GLOWSTONE_DUST)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.method_16293(
			Blocks.MELON,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.MELON_SLICE)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(3.0F, 7.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.method_16293(
			Blocks.REDSTONE_ORE,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.REDSTONE)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(4.0F, 5.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
					)
				)
		);
		this.method_16293(
			Blocks.SEA_LANTERN,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10393(
						blockx,
						ItemEntry.builder(Items.PRISMARINE_CRYSTALS)
							.method_438(SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 3.0F)))
							.method_438(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.method_438(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.method_16293(
			Blocks.NETHER_WART,
			blockx -> LootTable.builder()
					.withPool(
						method_10393(
							blockx,
							LootPool.builder()
								.withRolls(ConstantLootTableRange.create(1))
								.withEntry(
									ItemEntry.builder(Items.NETHER_WART)
										.method_438(
											SetCountLootFunction.builder(UniformLootTableRange.between(2.0F, 4.0F))
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.method_438(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)
												.method_524(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.method_16293(
			Blocks.SNOW,
			blockx -> LootTable.builder()
					.withPool(
						LootPool.builder()
							.method_356(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							.withEntry(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6))),
											ItemEntry.builder(Items.SNOWBALL)
												.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))
												.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7))),
											ItemEntry.builder(Items.SNOWBALL).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))
										)
										.method_421(field_11337),
									AlternativeEntry.builder(
										ItemEntry.builder(Blocks.SNOW)
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 1))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))),
										ItemEntry.builder(Blocks.SNOW)
											.method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))
											.method_421(BlockStatePropertyLootCondition.builder(blockx).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))),
										ItemEntry.builder(Blocks.SNOW_BLOCK)
									)
								)
							)
					)
		);
		this.method_16293(
			Blocks.GRAVEL,
			blockx -> method_10397(
					blockx,
					method_10392(
						blockx,
						ItemEntry.builder(Items.FLINT)
							.method_421(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.withChild(ItemEntry.builder(blockx))
					)
				)
		);
		this.method_16293(
			Blocks.CAMPFIRE,
			blockx -> method_10397(
					blockx,
					(LootEntry.Builder<?>)method_10392(blockx, ItemEntry.builder(Items.CHARCOAL).method_438(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))
				)
		);
		this.method_16262(Blocks.GLASS);
		this.method_16262(Blocks.WHITE_STAINED_GLASS);
		this.method_16262(Blocks.ORANGE_STAINED_GLASS);
		this.method_16262(Blocks.MAGENTA_STAINED_GLASS);
		this.method_16262(Blocks.LIGHT_BLUE_STAINED_GLASS);
		this.method_16262(Blocks.YELLOW_STAINED_GLASS);
		this.method_16262(Blocks.LIME_STAINED_GLASS);
		this.method_16262(Blocks.PINK_STAINED_GLASS);
		this.method_16262(Blocks.GRAY_STAINED_GLASS);
		this.method_16262(Blocks.LIGHT_GRAY_STAINED_GLASS);
		this.method_16262(Blocks.CYAN_STAINED_GLASS);
		this.method_16262(Blocks.PURPLE_STAINED_GLASS);
		this.method_16262(Blocks.BLUE_STAINED_GLASS);
		this.method_16262(Blocks.BROWN_STAINED_GLASS);
		this.method_16262(Blocks.GREEN_STAINED_GLASS);
		this.method_16262(Blocks.RED_STAINED_GLASS);
		this.method_16262(Blocks.BLACK_STAINED_GLASS);
		this.method_16262(Blocks.GLASS_PANE);
		this.method_16262(Blocks.WHITE_STAINED_GLASS_PANE);
		this.method_16262(Blocks.ORANGE_STAINED_GLASS_PANE);
		this.method_16262(Blocks.MAGENTA_STAINED_GLASS_PANE);
		this.method_16262(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
		this.method_16262(Blocks.YELLOW_STAINED_GLASS_PANE);
		this.method_16262(Blocks.LIME_STAINED_GLASS_PANE);
		this.method_16262(Blocks.PINK_STAINED_GLASS_PANE);
		this.method_16262(Blocks.GRAY_STAINED_GLASS_PANE);
		this.method_16262(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
		this.method_16262(Blocks.CYAN_STAINED_GLASS_PANE);
		this.method_16262(Blocks.PURPLE_STAINED_GLASS_PANE);
		this.method_16262(Blocks.BLUE_STAINED_GLASS_PANE);
		this.method_16262(Blocks.BROWN_STAINED_GLASS_PANE);
		this.method_16262(Blocks.GREEN_STAINED_GLASS_PANE);
		this.method_16262(Blocks.RED_STAINED_GLASS_PANE);
		this.method_16262(Blocks.BLACK_STAINED_GLASS_PANE);
		this.method_16262(Blocks.ICE);
		this.method_16262(Blocks.PACKED_ICE);
		this.method_16262(Blocks.BLUE_ICE);
		this.method_16262(Blocks.TURTLE_EGG);
		this.method_16262(Blocks.MUSHROOM_STEM);
		this.method_16262(Blocks.DEAD_TUBE_CORAL);
		this.method_16262(Blocks.DEAD_BRAIN_CORAL);
		this.method_16262(Blocks.DEAD_BUBBLE_CORAL);
		this.method_16262(Blocks.DEAD_FIRE_CORAL);
		this.method_16262(Blocks.DEAD_HORN_CORAL);
		this.method_16262(Blocks.TUBE_CORAL);
		this.method_16262(Blocks.BRAIN_CORAL);
		this.method_16262(Blocks.BUBBLE_CORAL);
		this.method_16262(Blocks.FIRE_CORAL);
		this.method_16262(Blocks.HORN_CORAL);
		this.method_16262(Blocks.DEAD_TUBE_CORAL_FAN);
		this.method_16262(Blocks.DEAD_BRAIN_CORAL_FAN);
		this.method_16262(Blocks.DEAD_BUBBLE_CORAL_FAN);
		this.method_16262(Blocks.DEAD_FIRE_CORAL_FAN);
		this.method_16262(Blocks.DEAD_HORN_CORAL_FAN);
		this.method_16262(Blocks.TUBE_CORAL_FAN);
		this.method_16262(Blocks.BRAIN_CORAL_FAN);
		this.method_16262(Blocks.BUBBLE_CORAL_FAN);
		this.method_16262(Blocks.FIRE_CORAL_FAN);
		this.method_16262(Blocks.HORN_CORAL_FAN);
		this.method_16238(Blocks.INFESTED_STONE, Blocks.STONE);
		this.method_16238(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
		this.method_16238(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
		this.method_16238(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
		this.method_16238(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
		this.method_16238(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
		this.method_16258(Blocks.CAKE, method_10395());
		this.method_16258(Blocks.ATTACHED_PUMPKIN_STEM, method_10395());
		this.method_16258(Blocks.ATTACHED_MELON_STEM, method_10395());
		this.method_16258(Blocks.FROSTED_ICE, method_10395());
		this.method_16258(Blocks.SPAWNER, method_10395());
		Set<Identifier> set = Sets.<Identifier>newHashSet();

		for (Block block : Registry.BLOCK) {
			Identifier identifier = block.getDropTableId();
			if (identifier != LootTables.EMPTY && set.add(identifier)) {
				LootTable.Builder builder5 = (LootTable.Builder)this.field_16493.remove(identifier);
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

	private void method_16293(Block block, Function<Block, LootTable.Builder> function) {
		this.method_16258(block, (LootTable.Builder)function.apply(block));
	}

	private void method_16258(Block block, LootTable.Builder builder) {
		this.field_16493.put(block.getDropTableId(), builder);
	}
}
