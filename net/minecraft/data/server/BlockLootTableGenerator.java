/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
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
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionConsumingBuilder;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.BoundedIntUnaryOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;

public class BlockLootTableGenerator
implements Consumer<BiConsumer<Identifier, LootTable.Builder>> {
    private static final LootCondition.Builder field_11336 = MatchToolLootCondition.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))));
    private static final LootCondition.Builder field_11337 = field_11336.invert();
    private static final LootCondition.Builder field_11343 = MatchToolLootCondition.builder(ItemPredicate.Builder.create().item(Items.SHEARS));
    private static final LootCondition.Builder field_11342 = field_11343.withCondition(field_11336);
    private static final LootCondition.Builder field_11341 = field_11342.invert();
    private static final Set<Item> field_11340 = Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemConvertible::asItem).collect(ImmutableSet.toImmutableSet());
    private static final float[] field_11339 = new float[]{0.05f, 0.0625f, 0.083333336f, 0.1f};
    private static final float[] field_11338 = new float[]{0.025f, 0.027777778f, 0.03125f, 0.041666668f, 0.1f};
    private final Map<Identifier, LootTable.Builder> field_16493 = Maps.newHashMap();

    private static <T> T method_10393(ItemConvertible itemConvertible, LootFunctionConsumingBuilder<T> lootFunctionConsumingBuilder) {
        if (!field_11340.contains(itemConvertible.asItem())) {
            return lootFunctionConsumingBuilder.withFunction(ExplosionDecayLootFunction.builder());
        }
        return lootFunctionConsumingBuilder.getThis();
    }

    private static <T> T method_10392(ItemConvertible itemConvertible, LootConditionConsumingBuilder<T> lootConditionConsumingBuilder) {
        if (!field_11340.contains(itemConvertible.asItem())) {
            return lootConditionConsumingBuilder.withCondition(SurvivesExplosionLootCondition.builder());
        }
        return lootConditionConsumingBuilder.getThis();
    }

    private static LootTable.Builder method_10394(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
    }

    private static LootTable.Builder method_10381(Block block, LootCondition.Builder builder, LootEntry.Builder<?> builder2) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(((LeafEntry.Builder)ItemEntry.builder(block).withCondition(builder)).withChild(builder2)));
    }

    private static LootTable.Builder method_10397(Block block, LootEntry.Builder<?> builder) {
        return BlockLootTableGenerator.method_10381(block, field_11336, builder);
    }

    private static LootTable.Builder method_10380(Block block, LootEntry.Builder<?> builder) {
        return BlockLootTableGenerator.method_10381(block, field_11343, builder);
    }

    private static LootTable.Builder method_10388(Block block, LootEntry.Builder<?> builder) {
        return BlockLootTableGenerator.method_10381(block, field_11342, builder);
    }

    private static LootTable.Builder method_10382(Block block, ItemConvertible itemConvertible) {
        return BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(itemConvertible)));
    }

    private static LootTable.Builder method_10384(ItemConvertible itemConvertible, LootTableRange lootTableRange) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.method_10393(itemConvertible, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(lootTableRange)))));
    }

    private static LootTable.Builder method_10386(Block block, ItemConvertible itemConvertible, LootTableRange lootTableRange) {
        return BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(lootTableRange))));
    }

    private static LootTable.Builder method_10373(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(field_11336).withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible)));
    }

    private static LootTable.Builder method_10389(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(Blocks.FLOWER_POT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(Blocks.FLOWER_POT)))).withPool(BlockLootTableGenerator.method_10392(itemConvertible, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(ItemEntry.builder(itemConvertible))));
    }

    private static LootTable.Builder method_10383(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(block).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))))));
    }

    private static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder method_10375(Block block, Property<T> property, T comparable) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(block).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(property, comparable))))));
    }

    private static LootTable.Builder method_10396(Block block) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)))));
    }

    private static LootTable.Builder method_16876(Block block) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Lock", "BlockEntityTag.Lock").withOperation("LootTable", "BlockEntityTag.LootTable").withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed"))).withFunction(SetContentsLootFunction.builder().withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS))))));
    }

    private static LootTable.Builder method_16877(Block block) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10392(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(block).withFunction(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Patterns", "BlockEntityTag.Patterns")))));
    }

    private static LootTable.Builder method_22142(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withCondition(field_11336).withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(block).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).withFunction(CopyStateFunction.getBuilder(block).method_21898(BeeHiveBlock.HONEY_LEVEL))));
    }

    private static LootTable.Builder method_22143(Block block) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry(((LootEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(block).withCondition(field_11336)).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Bees", "BlockEntityTag.Bees"))).withFunction(CopyStateFunction.getBuilder(block).method_21898(BeeHiveBlock.HONEY_LEVEL))).withChild(ItemEntry.builder(block))));
    }

    private static LootTable.Builder method_10377(Block block, Item item) {
        return BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(item).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))));
    }

    private static LootTable.Builder method_10385(Block block, ItemConvertible itemConvertible) {
        return BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)ItemEntry.builder(itemConvertible).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(-6.0f, 2.0f)))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMin(0)))));
    }

    private static LootTable.Builder method_10371(Block block) {
        return BlockLootTableGenerator.method_10380(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)ItemEntry.builder(Items.WHEAT_SEEDS).withCondition(RandomChanceLootCondition.builder(0.125f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE, 2))));
    }

    private static LootTable.Builder method_10387(Block block, Item item) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10393(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(item).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.06666667f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, false))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.13333334f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, true))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.2f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 2))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.26666668f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 3))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.33333334f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 4))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.4f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 5))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.46666667f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 6))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(StemBlock.AGE, 7)))))));
    }

    private static LootTable.Builder method_23229(Block block, Item item) {
        return LootTable.builder().withPool(BlockLootTableGenerator.method_10393(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(item).withFunction(SetCountLootFunction.builder(BinomialLootTableRange.create(3, 0.53333336f))))));
    }

    private static LootTable.Builder method_10372(ItemConvertible itemConvertible) {
        return LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(field_11343).withEntry(ItemEntry.builder(itemConvertible)));
    }

    private static LootTable.Builder method_10390(Block block, Block block2, float ... fs) {
        return BlockLootTableGenerator.method_10388(block, ((LeafEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(block2))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, fs))).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(field_11341).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.02f, 0.022222223f, 0.025f, 0.033333335f, 0.1f))));
    }

    private static LootTable.Builder method_10378(Block block, Block block2, float ... fs) {
        return BlockLootTableGenerator.method_10390(block, block2, fs).withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withCondition(field_11341).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(Items.APPLE))).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f))));
    }

    private static LootTable.Builder method_10391(Block block, Item item, Item item2, LootCondition.Builder builder) {
        return BlockLootTableGenerator.method_10393(block, LootTable.builder().withPool(LootPool.builder().withEntry(((LeafEntry.Builder)ItemEntry.builder(item).withCondition(builder)).withChild(ItemEntry.builder(item2)))).withPool(LootPool.builder().withCondition(builder).withEntry((LootEntry.Builder<?>)ItemEntry.builder(item2).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))));
    }

    public static LootTable.Builder method_10395() {
        return LootTable.builder();
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
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
        this.method_16329(Blocks.HONEY_BLOCK);
        this.method_16329(Blocks.HONEYCOMB_BLOCK);
        this.method_16256(Blocks.FARMLAND, Blocks.DIRT);
        this.method_16256(Blocks.TRIPWIRE, Items.STRING);
        this.method_16256(Blocks.GRASS_PATH, Blocks.DIRT);
        this.method_16256(Blocks.KELP_PLANT, Blocks.KELP);
        this.method_16256(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
        this.method_16293(Blocks.STONE, block -> BlockLootTableGenerator.method_10382(block, Blocks.COBBLESTONE));
        this.method_16293(Blocks.GRASS_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DIRT));
        this.method_16293(Blocks.PODZOL, block -> BlockLootTableGenerator.method_10382(block, Blocks.DIRT));
        this.method_16293(Blocks.MYCELIUM, block -> BlockLootTableGenerator.method_10382(block, Blocks.DIRT));
        this.method_16293(Blocks.TUBE_CORAL_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DEAD_TUBE_CORAL_BLOCK));
        this.method_16293(Blocks.BRAIN_CORAL_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DEAD_BRAIN_CORAL_BLOCK));
        this.method_16293(Blocks.BUBBLE_CORAL_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
        this.method_16293(Blocks.FIRE_CORAL_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DEAD_FIRE_CORAL_BLOCK));
        this.method_16293(Blocks.HORN_CORAL_BLOCK, block -> BlockLootTableGenerator.method_10382(block, Blocks.DEAD_HORN_CORAL_BLOCK));
        this.method_16293(Blocks.BOOKSHELF, block -> BlockLootTableGenerator.method_10386(block, Items.BOOK, ConstantLootTableRange.create(3)));
        this.method_16293(Blocks.CLAY, block -> BlockLootTableGenerator.method_10386(block, Items.CLAY_BALL, ConstantLootTableRange.create(4)));
        this.method_16293(Blocks.ENDER_CHEST, block -> BlockLootTableGenerator.method_10386(block, Blocks.OBSIDIAN, ConstantLootTableRange.create(8)));
        this.method_16293(Blocks.SNOW_BLOCK, block -> BlockLootTableGenerator.method_10386(block, Items.SNOWBALL, ConstantLootTableRange.create(4)));
        this.method_16258(Blocks.CHORUS_PLANT, BlockLootTableGenerator.method_10384(Items.CHORUS_FRUIT, UniformLootTableRange.between(0.0f, 1.0f)));
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
        this.method_16293(Blocks.ACACIA_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.BIRCH_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.DARK_OAK_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.IRON_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.JUNGLE_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.OAK_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.SPRUCE_DOOR, block -> BlockLootTableGenerator.method_10375(block, DoorBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.BLACK_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.BLUE_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.BROWN_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.CYAN_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.GRAY_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.GREEN_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.LIGHT_BLUE_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.LIGHT_GRAY_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.LIME_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.MAGENTA_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.PURPLE_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.ORANGE_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.PINK_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.RED_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.WHITE_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.YELLOW_BED, block -> BlockLootTableGenerator.method_10375(block, BedBlock.PART, BedPart.HEAD));
        this.method_16293(Blocks.LILAC, block -> BlockLootTableGenerator.method_10375(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.SUNFLOWER, block -> BlockLootTableGenerator.method_10375(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.PEONY, block -> BlockLootTableGenerator.method_10375(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16293(Blocks.ROSE_BUSH, block -> BlockLootTableGenerator.method_10375(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
        this.method_16258(Blocks.TNT, LootTable.builder().withPool(BlockLootTableGenerator.method_10392(Blocks.TNT, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Blocks.TNT).withCondition(BlockStatePropertyLootCondition.builder(Blocks.TNT).method_22584(StatePredicate.Builder.create().exactMatch(TntBlock.UNSTABLE, false)))))));
        this.method_16293(Blocks.COCOA, block -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(Items.COCOA_BEANS).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2))))))));
        this.method_16293(Blocks.SEA_PICKLE, block -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder)BlockLootTableGenerator.method_10393(Blocks.SEA_PICKLE, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(block).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(2)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 2))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(3)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 3))))).withFunction((LootFunction.Builder)SetCountLootFunction.builder(ConstantLootTableRange.create(4)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, 4))))))));
        this.method_16293(Blocks.COMPOSTER, block -> LootTable.builder().withPool(LootPool.builder().withEntry((LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(Items.COMPOSTER)))).withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.BONE_MEAL)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))));
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
        this.method_16293(Blocks.PLAYER_HEAD, block -> LootTable.builder().withPool(BlockLootTableGenerator.method_10392(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)ItemEntry.builder(block).withFunction(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Owner", "SkullOwner"))))));
        this.method_16293(Blocks.BEE_NEST, BlockLootTableGenerator::method_22142);
        this.method_16293(Blocks.BEEHIVE, BlockLootTableGenerator::method_22143);
        this.method_16293(Blocks.BIRCH_LEAVES, block -> BlockLootTableGenerator.method_10390(block, Blocks.BIRCH_SAPLING, field_11339));
        this.method_16293(Blocks.ACACIA_LEAVES, block -> BlockLootTableGenerator.method_10390(block, Blocks.ACACIA_SAPLING, field_11339));
        this.method_16293(Blocks.JUNGLE_LEAVES, block -> BlockLootTableGenerator.method_10390(block, Blocks.JUNGLE_SAPLING, field_11338));
        this.method_16293(Blocks.SPRUCE_LEAVES, block -> BlockLootTableGenerator.method_10390(block, Blocks.SPRUCE_SAPLING, field_11339));
        this.method_16293(Blocks.OAK_LEAVES, block -> BlockLootTableGenerator.method_10378(block, Blocks.OAK_SAPLING, field_11339));
        this.method_16293(Blocks.DARK_OAK_LEAVES, block -> BlockLootTableGenerator.method_10378(block, Blocks.DARK_OAK_SAPLING, field_11339));
        BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS).method_22584(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
        this.method_16258(Blocks.BEETROOTS, BlockLootTableGenerator.method_10391(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, builder));
        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT).method_22584(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
        this.method_16258(Blocks.WHEAT, BlockLootTableGenerator.method_10391(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, builder2));
        BlockStatePropertyLootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS).method_22584(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
        this.method_16258(Blocks.CARROTS, BlockLootTableGenerator.method_10393(Blocks.CARROTS, LootTable.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.CARROT))).withPool(LootPool.builder().withCondition(builder3).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.CARROT).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3))))));
        BlockStatePropertyLootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES).method_22584(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
        this.method_16258(Blocks.POTATOES, BlockLootTableGenerator.method_10393(Blocks.POTATOES, LootTable.builder().withPool(LootPool.builder().withEntry(ItemEntry.builder(Items.POTATO))).withPool(LootPool.builder().withCondition(builder4).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.POTATO).withFunction(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286f, 3)))).withPool(LootPool.builder().withCondition(builder4).withEntry((LootEntry.Builder<?>)ItemEntry.builder(Items.POISONOUS_POTATO).withCondition(RandomChanceLootCondition.builder(0.02f))))));
        this.method_16293(Blocks.SWEET_BERRY_BUSH, block -> BlockLootTableGenerator.method_10393(block, LootTable.builder().withPool(LootPool.builder().withCondition(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 3))).withEntry(ItemEntry.builder(Items.SWEET_BERRIES)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withPool(LootPool.builder().withCondition(BlockStatePropertyLootCondition.builder(Blocks.SWEET_BERRY_BUSH).method_22584(StatePredicate.Builder.create().exactMatch(SweetBerryBushBlock.AGE, 2))).withEntry(ItemEntry.builder(Items.SWEET_BERRIES)).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(1.0f, 2.0f))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.method_16293(Blocks.BROWN_MUSHROOM_BLOCK, block -> BlockLootTableGenerator.method_10385(block, Blocks.BROWN_MUSHROOM));
        this.method_16293(Blocks.RED_MUSHROOM_BLOCK, block -> BlockLootTableGenerator.method_10385(block, Blocks.RED_MUSHROOM));
        this.method_16293(Blocks.COAL_ORE, block -> BlockLootTableGenerator.method_10377(block, Items.COAL));
        this.method_16293(Blocks.EMERALD_ORE, block -> BlockLootTableGenerator.method_10377(block, Items.EMERALD));
        this.method_16293(Blocks.NETHER_QUARTZ_ORE, block -> BlockLootTableGenerator.method_10377(block, Items.QUARTZ));
        this.method_16293(Blocks.DIAMOND_ORE, block -> BlockLootTableGenerator.method_10377(block, Items.DIAMOND));
        this.method_16293(Blocks.LAPIS_ORE, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)ItemEntry.builder(Items.LAPIS_LAZULI).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 9.0f)))).withFunction(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE)))));
        this.method_16293(Blocks.COBWEB, block -> BlockLootTableGenerator.method_10388(block, (LootEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(Items.STRING))));
        this.method_16293(Blocks.DEAD_BUSH, block -> BlockLootTableGenerator.method_10380(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ItemEntry.builder(Items.STICK).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(0.0f, 2.0f))))));
        this.method_16293(Blocks.SEAGRASS, BlockLootTableGenerator::method_10372);
        this.method_16293(Blocks.VINE, BlockLootTableGenerator::method_10372);
        this.method_16258(Blocks.TALL_SEAGRASS, BlockLootTableGenerator.method_10372(Blocks.SEAGRASS));
        this.method_16293(Blocks.LARGE_FERN, block -> BlockLootTableGenerator.method_10380(Blocks.FERN, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(Items.WHEAT_SEEDS))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).withCondition(RandomChanceLootCondition.builder(0.125f))));
        this.method_16258(Blocks.TALL_GRASS, BlockLootTableGenerator.method_10380(Blocks.GRASS, ((LeafEntry.Builder)((LeafEntry.Builder)BlockLootTableGenerator.method_10392(Blocks.TALL_GRASS, ItemEntry.builder(Items.WHEAT_SEEDS))).withCondition(BlockStatePropertyLootCondition.builder(Blocks.TALL_GRASS).method_22584(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER)))).withCondition(RandomChanceLootCondition.builder(0.125f))));
        this.method_16293(Blocks.MELON_STEM, block -> BlockLootTableGenerator.method_10387(block, Items.MELON_SEEDS));
        this.method_16293(Blocks.ATTACHED_MELON_STEM, block -> BlockLootTableGenerator.method_23229(block, Items.MELON_SEEDS));
        this.method_16293(Blocks.PUMPKIN_STEM, block -> BlockLootTableGenerator.method_10387(block, Items.PUMPKIN_SEEDS));
        this.method_16293(Blocks.ATTACHED_PUMPKIN_STEM, block -> BlockLootTableGenerator.method_23229(block, Items.PUMPKIN_SEEDS));
        this.method_16293(Blocks.CHORUS_FLOWER, block -> LootTable.builder().withPool(LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(block))).withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)))));
        this.method_16293(Blocks.FERN, BlockLootTableGenerator::method_10371);
        this.method_16293(Blocks.GRASS, BlockLootTableGenerator::method_10371);
        this.method_16293(Blocks.GLOWSTONE, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.GLOWSTONE_DUST).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4))))));
        this.method_16293(Blocks.MELON, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.MELON_SLICE).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3.0f, 7.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9))))));
        this.method_16293(Blocks.REDSTONE_ORE, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)ItemEntry.builder(Items.REDSTONE).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(4.0f, 5.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)))));
        this.method_16293(Blocks.SEA_LANTERN, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10393(block, ((LeafEntry.Builder)((LeafEntry.Builder)ItemEntry.builder(Items.PRISMARINE_CRYSTALS).withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 3.0f)))).withFunction(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))).withFunction(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5))))));
        this.method_16293(Blocks.NETHER_WART, block -> LootTable.builder().withPool(BlockLootTableGenerator.method_10393(block, LootPool.builder().withRolls(ConstantLootTableRange.create(1)).withEntry((LootEntry.Builder<?>)((LeafEntry.Builder)ItemEntry.builder(Items.NETHER_WART).withFunction((LootFunction.Builder)SetCountLootFunction.builder(UniformLootTableRange.between(2.0f, 4.0f)).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))).withFunction((LootFunction.Builder)ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3))))))));
        this.method_16293(Blocks.SNOW, block -> LootTable.builder().withPool(LootPool.builder().withCondition(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS)).withEntry(AlternativeEntry.builder(new LootEntry.Builder[]{AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6))), ((LeafEntry.Builder)ItemEntry.builder(Items.SNOWBALL).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7)))).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7))), ItemEntry.builder(Items.SNOWBALL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(8)))}).withCondition(field_11337), AlternativeEntry.builder(new LootEntry.Builder[]{ItemEntry.builder(Blocks.SNOW).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, true))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 2))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(3)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 3))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(4)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 4))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(5)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 5))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(6)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 6))), ((LootEntry.Builder)ItemEntry.builder(Blocks.SNOW).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(7)))).withCondition(BlockStatePropertyLootCondition.builder(block).method_22584(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, 7))), ItemEntry.builder(Blocks.SNOW_BLOCK)})}))));
        this.method_16293(Blocks.GRAVEL, block -> BlockLootTableGenerator.method_10397(block, BlockLootTableGenerator.method_10392(block, ((LeafEntry.Builder)ItemEntry.builder(Items.FLINT).withCondition(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1f, 0.14285715f, 0.25f, 1.0f))).withChild(ItemEntry.builder(block)))));
        this.method_16293(Blocks.CAMPFIRE, block -> BlockLootTableGenerator.method_10397(block, (LootEntry.Builder)BlockLootTableGenerator.method_10392(block, ItemEntry.builder(Items.CHARCOAL).withFunction(SetCountLootFunction.builder(ConstantLootTableRange.create(2))))));
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
        this.method_16258(Blocks.CAKE, BlockLootTableGenerator.method_10395());
        this.method_16258(Blocks.FROSTED_ICE, BlockLootTableGenerator.method_10395());
        this.method_16258(Blocks.SPAWNER, BlockLootTableGenerator.method_10395());
        HashSet<Identifier> set = Sets.newHashSet();
        for (Block block2 : Registry.BLOCK) {
            Identifier identifier = block2.getDropTableId();
            if (identifier == LootTables.EMPTY || !set.add(identifier)) continue;
            LootTable.Builder builder5 = this.field_16493.remove(identifier);
            if (builder5 == null) {
                throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", identifier, Registry.BLOCK.getId(block2)));
            }
            biConsumer.accept(identifier, builder5);
        }
        if (!this.field_16493.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_16493.keySet());
        }
    }

    public void method_16285(Block block2) {
        this.method_16293(block2, block -> BlockLootTableGenerator.method_10389(((FlowerPotBlock)block).getContent()));
    }

    public void method_16238(Block block, Block block2) {
        this.method_16258(block, BlockLootTableGenerator.method_10373(block2));
    }

    public void method_16256(Block block, ItemConvertible itemConvertible) {
        this.method_16258(block, BlockLootTableGenerator.method_10394(itemConvertible));
    }

    public void method_16262(Block block) {
        this.method_16238(block, block);
    }

    public void method_16329(Block block) {
        this.method_16256(block, block);
    }

    private void method_16293(Block block, Function<Block, LootTable.Builder> function) {
        this.method_16258(block, function.apply(block));
    }

    private void method_16258(Block block, LootTable.Builder builder) {
        this.field_16493.put(block.getDropTableId(), builder);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

