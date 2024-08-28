package net.minecraft.data.server.loottable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CaveVines;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class BlockLootTableGenerator implements LootTableGenerator {
	protected final RegistryWrapper.WrapperLookup registries;
	protected final Set<Item> explosionImmuneItems;
	protected final FeatureSet requiredFeatures;
	protected final Map<RegistryKey<LootTable>, LootTable.Builder> lootTables;
	protected static final float[] SAPLING_DROP_CHANCE = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
	private static final float[] LEAVES_STICK_DROP_CHANCE = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

	protected LootCondition.Builder createSilkTouchCondition() {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return MatchToolLootCondition.builder(
			ItemPredicate.Builder.create()
				.subPredicate(
					ItemSubPredicateTypes.ENCHANTMENTS,
					EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(Enchantments.SILK_TOUCH), NumberRange.IntRange.atLeast(1))))
				)
		);
	}

	protected LootCondition.Builder createWithoutSilkTouchCondition() {
		return this.createSilkTouchCondition().invert();
	}

	protected LootCondition.Builder createWithShearsCondition() {
		return MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(this.registries.getOrThrow(RegistryKeys.ITEM), Items.SHEARS));
	}

	private LootCondition.Builder createWithShearsOrSilkTouchCondition() {
		return this.createWithShearsCondition().or(this.createSilkTouchCondition());
	}

	private LootCondition.Builder createWithoutShearsOrSilkTouchCondition() {
		return this.createWithShearsOrSilkTouchCondition().invert();
	}

	protected BlockLootTableGenerator(Set<Item> explosionImmuneItems, FeatureSet requiredFeatures, RegistryWrapper.WrapperLookup registries) {
		this(explosionImmuneItems, requiredFeatures, new HashMap(), registries);
	}

	protected BlockLootTableGenerator(
		Set<Item> explosionImmuneItems,
		FeatureSet requiredFeatures,
		Map<RegistryKey<LootTable>, LootTable.Builder> lootTables,
		RegistryWrapper.WrapperLookup registries
	) {
		this.explosionImmuneItems = explosionImmuneItems;
		this.requiredFeatures = requiredFeatures;
		this.lootTables = lootTables;
		this.registries = registries;
	}

	protected <T extends LootFunctionConsumingBuilder<T>> T applyExplosionDecay(ItemConvertible drop, LootFunctionConsumingBuilder<T> builder) {
		return !this.explosionImmuneItems.contains(drop.asItem()) ? builder.apply(ExplosionDecayLootFunction.builder()) : builder.getThisFunctionConsumingBuilder();
	}

	protected <T extends LootConditionConsumingBuilder<T>> T addSurvivesExplosionCondition(ItemConvertible drop, LootConditionConsumingBuilder<T> builder) {
		return !this.explosionImmuneItems.contains(drop.asItem())
			? builder.conditionally(SurvivesExplosionLootCondition.builder())
			: builder.getThisConditionConsumingBuilder();
	}

	public LootTable.Builder drops(ItemConvertible drop) {
		return LootTable.builder()
			.pool(this.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop))));
	}

	private static LootTable.Builder drops(Block drop, LootCondition.Builder conditionBuilder, LootPoolEntry.Builder<?> child) {
		return LootTable.builder()
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop).conditionally(conditionBuilder).alternatively(child)));
	}

	protected LootTable.Builder dropsWithSilkTouch(Block block, LootPoolEntry.Builder<?> loot) {
		return drops(block, this.createSilkTouchCondition(), loot);
	}

	protected LootTable.Builder dropsWithShears(Block block, LootPoolEntry.Builder<?> loot) {
		return drops(block, this.createWithShearsCondition(), loot);
	}

	protected LootTable.Builder dropsWithSilkTouchOrShears(Block block, LootPoolEntry.Builder<?> loot) {
		return drops(block, this.createWithShearsOrSilkTouchCondition(), loot);
	}

	protected LootTable.Builder drops(Block withSilkTouch, ItemConvertible withoutSilkTouch) {
		return this.dropsWithSilkTouch(
			withSilkTouch, (LootPoolEntry.Builder<?>)this.addSurvivesExplosionCondition(withSilkTouch, ItemEntry.builder(withoutSilkTouch))
		);
	}

	protected LootTable.Builder drops(ItemConvertible drop, LootNumberProvider count) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with((LootPoolEntry.Builder<?>)this.applyExplosionDecay(drop, ItemEntry.builder(drop).apply(SetCountLootFunction.builder(count))))
			);
	}

	protected LootTable.Builder drops(Block block, ItemConvertible drop, LootNumberProvider count) {
		return this.dropsWithSilkTouch(
			block, (LootPoolEntry.Builder<?>)this.applyExplosionDecay(block, ItemEntry.builder(drop).apply(SetCountLootFunction.builder(count)))
		);
	}

	private LootTable.Builder dropsWithSilkTouch(ItemConvertible drop) {
		return LootTable.builder()
			.pool(LootPool.builder().conditionally(this.createSilkTouchCondition()).rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop)));
	}

	private LootTable.Builder pottedPlantDrops(ItemConvertible drop) {
		return LootTable.builder()
			.pool(
				this.addSurvivesExplosionCondition(
					Blocks.FLOWER_POT, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(Blocks.FLOWER_POT))
				)
			)
			.pool(this.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop))));
	}

	protected LootTable.Builder slabDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
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

	protected <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T value) {
		return LootTable.builder()
			.pool(
				this.addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, value)))
						)
				)
			);
	}

	protected LootTable.Builder nameableContainerDrops(Block drop) {
		return LootTable.builder()
			.pool(
				this.addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY).include(DataComponentTypes.CUSTOM_NAME))
						)
				)
			);
	}

	protected LootTable.Builder shulkerBoxDrops(Block drop) {
		return LootTable.builder()
			.pool(
				this.addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(
									CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
										.include(DataComponentTypes.CUSTOM_NAME)
										.include(DataComponentTypes.CONTAINER)
										.include(DataComponentTypes.LOCK)
										.include(DataComponentTypes.CONTAINER_LOOT)
								)
						)
				)
			);
	}

	protected LootTable.Builder copperOreDrops(Block drop) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouch(
			drop,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				drop,
				ItemEntry.builder(Items.RAW_COPPER)
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
					.apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
			)
		);
	}

	protected LootTable.Builder lapisOreDrops(Block drop) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouch(
			drop,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				drop,
				ItemEntry.builder(Items.LAPIS_LAZULI)
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 9.0F)))
					.apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
			)
		);
	}

	protected LootTable.Builder redstoneOreDrops(Block drop) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouch(
			drop,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				drop,
				ItemEntry.builder(Items.REDSTONE)
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4.0F, 5.0F)))
					.apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
			)
		);
	}

	protected LootTable.Builder bannerDrops(Block drop) {
		return LootTable.builder()
			.pool(
				this.addSurvivesExplosionCondition(
					drop,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(
									CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
										.include(DataComponentTypes.CUSTOM_NAME)
										.include(DataComponentTypes.ITEM_NAME)
										.include(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
										.include(DataComponentTypes.BANNER_PATTERNS)
										.include(DataComponentTypes.RARITY)
								)
						)
				)
			);
	}

	protected LootTable.Builder beeNestDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.conditionally(this.createSilkTouchCondition())
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(drop)
							.apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY).include(DataComponentTypes.BEES))
							.apply(CopyStateLootFunction.builder(drop).addProperty(BeehiveBlock.HONEY_LEVEL))
					)
			);
	}

	protected LootTable.Builder beehiveDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						ItemEntry.builder(drop)
							.conditionally(this.createSilkTouchCondition())
							.apply(CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY).include(DataComponentTypes.BEES))
							.apply(CopyStateLootFunction.builder(drop).addProperty(BeehiveBlock.HONEY_LEVEL))
							.alternatively(ItemEntry.builder(drop))
					)
			);
	}

	protected LootTable.Builder glowBerryDrops(Block drop) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(ItemEntry.builder(Items.GLOW_BERRIES))
					.conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(CaveVines.BERRIES, true)))
			);
	}

	protected LootTable.Builder oreDrops(Block withSilkTouch, Item withoutSilkTouch) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouch(
			withSilkTouch,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				withSilkTouch, ItemEntry.builder(withoutSilkTouch).apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))
			)
		);
	}

	protected LootTable.Builder mushroomBlockDrops(Block withSilkTouch, ItemConvertible withoutSilkTouch) {
		return this.dropsWithSilkTouch(
			withSilkTouch,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				withSilkTouch,
				ItemEntry.builder(withoutSilkTouch)
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-6.0F, 2.0F)))
					.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))
			)
		);
	}

	protected LootTable.Builder shortPlantDrops(Block withShears) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithShears(
			withShears,
			(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
				withShears,
				ItemEntry.builder(Items.WHEAT_SEEDS)
					.conditionally(RandomChanceLootCondition.builder(0.125F))
					.apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 2))
			)
		);
	}

	public LootTable.Builder cropStemDrops(Block stem, Item drop) {
		return LootTable.builder()
			.pool(
				this.applyExplosionDecay(
					stem,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(
							ItemEntry.builder(drop)
								.apply(
									StemBlock.AGE.getValues(),
									age -> SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, (float)(age + 1) / 15.0F))
											.conditionally(BlockStatePropertyLootCondition.builder(stem).properties(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, age)))
								)
						)
				)
			);
	}

	public LootTable.Builder attachedCropStemDrops(Block stem, Item drop) {
		return LootTable.builder()
			.pool(
				this.applyExplosionDecay(
					stem,
					LootPool.builder()
						.rolls(ConstantLootNumberProvider.create(1.0F))
						.with(ItemEntry.builder(drop).apply(SetCountLootFunction.builder(BinomialLootNumberProvider.create(3, 0.53333336F))))
				)
			);
	}

	protected LootTable.Builder dropsWithShears(ItemConvertible item) {
		return LootTable.builder()
			.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).conditionally(this.createWithShearsCondition()).with(ItemEntry.builder(item)));
	}

	protected LootTable.Builder multifaceGrowthDrops(Block drop, LootCondition.Builder condition) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(
						(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
							drop,
							ItemEntry.builder(drop)
								.conditionally(condition)
								.apply(
									Direction.values(),
									direction -> SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F), true)
											.conditionally(
												BlockStatePropertyLootCondition.builder(drop)
													.properties(StatePredicate.Builder.create().exactMatch(MultifaceGrowthBlock.getProperty(direction), true))
											)
								)
								.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(-1.0F), true))
						)
					)
			);
	}

	protected LootTable.Builder leavesDrops(Block leaves, Block sapling, float... saplingChance) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouchOrShears(
				leaves,
				((LeafEntry.Builder)this.addSurvivesExplosionCondition(leaves, ItemEntry.builder(sapling)))
					.conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), saplingChance))
			)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(this.createWithoutShearsOrSilkTouchCondition())
					.with(
						((LeafEntry.Builder)this.applyExplosionDecay(
								leaves, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
							))
							.conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), LEAVES_STICK_DROP_CHANCE))
					)
			);
	}

	protected LootTable.Builder oakLeavesDrops(Block leaves, Block sapling, float... saplingChance) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.leavesDrops(leaves, sapling, saplingChance)
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.conditionally(this.createWithoutShearsOrSilkTouchCondition())
					.with(
						((LeafEntry.Builder)this.addSurvivesExplosionCondition(leaves, ItemEntry.builder(Items.APPLE)))
							.conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
					)
			);
	}

	protected LootTable.Builder mangroveLeavesDrops(Block leaves) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.dropsWithSilkTouchOrShears(
			leaves,
			((LeafEntry.Builder)this.applyExplosionDecay(
					Blocks.MANGROVE_LEAVES, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
				))
				.conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), LEAVES_STICK_DROP_CHANCE))
		);
	}

	protected LootTable.Builder cropDrops(Block crop, Item product, Item seeds, LootCondition.Builder condition) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		return this.applyExplosionDecay(
			crop,
			LootTable.builder()
				.pool(LootPool.builder().with(ItemEntry.builder(product).conditionally(condition).alternatively(ItemEntry.builder(seeds))))
				.pool(
					LootPool.builder()
						.conditionally(condition)
						.with(ItemEntry.builder(seeds).apply(ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
				)
		);
	}

	protected LootTable.Builder seagrassDrops(Block seagrass) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.conditionally(this.createWithShearsCondition())
					.with(ItemEntry.builder(seagrass).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))))
			);
	}

	protected LootTable.Builder tallPlantDrops(Block tallPlant, Block shortPlant) {
		RegistryWrapper.Impl<Block> impl = this.registries.getOrThrow(RegistryKeys.BLOCK);
		LootPoolEntry.Builder<?> builder = ItemEntry.builder(shortPlant)
			.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
			.conditionally(this.createWithShearsCondition())
			.alternatively(
				((LeafEntry.Builder)this.addSurvivesExplosionCondition(tallPlant, ItemEntry.builder(Items.WHEAT_SEEDS)))
					.conditionally(RandomChanceLootCondition.builder(0.125F))
			);
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.with(builder)
					.conditionally(
						BlockStatePropertyLootCondition.builder(tallPlant).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
					)
					.conditionally(
						LocationCheckLootCondition.builder(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create().blocks(impl, tallPlant).state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.UPPER))
								),
							new BlockPos(0, 1, 0)
						)
					)
			)
			.pool(
				LootPool.builder()
					.with(builder)
					.conditionally(
						BlockStatePropertyLootCondition.builder(tallPlant).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.UPPER))
					)
					.conditionally(
						LocationCheckLootCondition.builder(
							LocationPredicate.Builder.create()
								.block(
									BlockPredicate.Builder.create().blocks(impl, tallPlant).state(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
								),
							new BlockPos(0, -1, 0)
						)
					)
			);
	}

	protected LootTable.Builder candleDrops(Block candle) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
							candle,
							ItemEntry.builder(candle)
								.apply(
									List.of(2, 3, 4),
									candles -> SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)candles.intValue()))
											.conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, candles)))
								)
						)
					)
			);
	}

	protected LootTable.Builder flowerbedDrops(Block flowerbed) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
							flowerbed,
							ItemEntry.builder(flowerbed)
								.apply(
									IntStream.rangeClosed(1, 4).boxed().toList(),
									flowerAmount -> SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)flowerAmount.intValue()))
											.conditionally(
												BlockStatePropertyLootCondition.builder(flowerbed)
													.properties(StatePredicate.Builder.create().exactMatch(FlowerbedBlock.FLOWER_AMOUNT, flowerAmount))
											)
								)
						)
					)
			);
	}

	protected static LootTable.Builder candleCakeDrops(Block candleCake) {
		return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(candleCake)));
	}

	public static LootTable.Builder dropsNothing() {
		return LootTable.builder();
	}

	protected abstract void generate();

	@Override
	public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
		this.generate();
		Set<RegistryKey<LootTable>> set = new HashSet();

		for (Block block : Registries.BLOCK) {
			if (block.isEnabled(this.requiredFeatures)) {
				block.getLootTableKey().ifPresent(registryKey -> {
					if (set.add(registryKey)) {
						LootTable.Builder builder = (LootTable.Builder)this.lootTables.remove(registryKey);
						if (builder == null) {
							throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", registryKey.getValue(), Registries.BLOCK.getId(block)));
						}

						lootTableBiConsumer.accept(registryKey, builder);
					}
				});
			}
		}

		if (!this.lootTables.isEmpty()) {
			throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
		}
	}

	protected void addVinePlantDrop(Block vine, Block vinePlant) {
		RegistryWrapper.Impl<Enchantment> impl = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
		LootTable.Builder builder = this.dropsWithSilkTouchOrShears(
			vine, ItemEntry.builder(vine).conditionally(TableBonusLootCondition.builder(impl.getOrThrow(Enchantments.FORTUNE), 0.33F, 0.55F, 0.77F, 1.0F))
		);
		this.addDrop(vine, builder);
		this.addDrop(vinePlant, builder);
	}

	protected LootTable.Builder doorDrops(Block block) {
		return this.dropsWithProperty(block, DoorBlock.HALF, DoubleBlockHalf.LOWER);
	}

	protected void addPottedPlantDrops(Block block) {
		this.addDrop(block, flowerPot -> this.pottedPlantDrops(((FlowerPotBlock)flowerPot).getContent()));
	}

	protected void addDropWithSilkTouch(Block block, Block drop) {
		this.addDrop(block, this.dropsWithSilkTouch(drop));
	}

	protected void addDrop(Block block, ItemConvertible drop) {
		this.addDrop(block, this.drops(drop));
	}

	protected void addDropWithSilkTouch(Block block) {
		this.addDropWithSilkTouch(block, block);
	}

	protected void addDrop(Block block) {
		this.addDrop(block, block);
	}

	protected void addDrop(Block block, Function<Block, LootTable.Builder> lootTableFunction) {
		this.addDrop(block, (LootTable.Builder)lootTableFunction.apply(block));
	}

	protected void addDrop(Block block, LootTable.Builder lootTable) {
		this.lootTables
			.put((RegistryKey)block.getLootTableKey().orElseThrow(() -> new IllegalStateException("Block " + block + " does not have loot table")), lootTable);
	}
}
