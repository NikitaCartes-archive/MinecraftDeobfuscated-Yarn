package net.minecraft.data.server.loottable.vanilla;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.featuretoggle.FeatureFlags;

public class VanillaBlockLootTableGenerator extends BlockLootTableGenerator {
	private static final float[] JUNGLE_SAPLING_DROP_CHANCE = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
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
			Blocks.PIGLIN_HEAD,
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
		.collect(Collectors.toSet());

	public VanillaBlockLootTableGenerator() {
		super(EXPLOSION_IMMUNE, FeatureFlags.FEATURE_MANAGER.getFeatureSet());
	}

	@Override
	protected void generate() {
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
		this.addDrop(Blocks.MANGROVE_PLANKS);
		this.addDrop(Blocks.CHERRY_PLANKS);
		this.addDrop(Blocks.BAMBOO_PLANKS);
		this.addDrop(Blocks.BAMBOO_MOSAIC);
		this.addDrop(Blocks.DECORATED_POT, this::decoratedPotDrops);
		this.addDrop(Blocks.OAK_SAPLING);
		this.addDrop(Blocks.SPRUCE_SAPLING);
		this.addDrop(Blocks.BIRCH_SAPLING);
		this.addDrop(Blocks.JUNGLE_SAPLING);
		this.addDrop(Blocks.ACACIA_SAPLING);
		this.addDrop(Blocks.DARK_OAK_SAPLING);
		this.addDrop(Blocks.CHERRY_SAPLING);
		this.addDrop(Blocks.SAND);
		this.addDrop(Blocks.SUSPICIOUS_SAND, dropsNothing());
		this.addDrop(Blocks.SUSPICIOUS_GRAVEL, dropsNothing());
		this.addDrop(Blocks.RED_SAND);
		this.addDrop(Blocks.OAK_LOG);
		this.addDrop(Blocks.SPRUCE_LOG);
		this.addDrop(Blocks.BIRCH_LOG);
		this.addDrop(Blocks.JUNGLE_LOG);
		this.addDrop(Blocks.ACACIA_LOG);
		this.addDrop(Blocks.DARK_OAK_LOG);
		this.addDrop(Blocks.CHERRY_LOG);
		this.addDrop(Blocks.BAMBOO_BLOCK);
		this.addDrop(Blocks.STRIPPED_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_SPRUCE_LOG);
		this.addDrop(Blocks.STRIPPED_BIRCH_LOG);
		this.addDrop(Blocks.STRIPPED_JUNGLE_LOG);
		this.addDrop(Blocks.STRIPPED_ACACIA_LOG);
		this.addDrop(Blocks.STRIPPED_DARK_OAK_LOG);
		this.addDrop(Blocks.STRIPPED_MANGROVE_LOG);
		this.addDrop(Blocks.STRIPPED_CHERRY_LOG);
		this.addDrop(Blocks.STRIPPED_BAMBOO_BLOCK);
		this.addDrop(Blocks.STRIPPED_WARPED_STEM);
		this.addDrop(Blocks.STRIPPED_CRIMSON_STEM);
		this.addDrop(Blocks.OAK_WOOD);
		this.addDrop(Blocks.SPRUCE_WOOD);
		this.addDrop(Blocks.BIRCH_WOOD);
		this.addDrop(Blocks.JUNGLE_WOOD);
		this.addDrop(Blocks.ACACIA_WOOD);
		this.addDrop(Blocks.DARK_OAK_WOOD);
		this.addDrop(Blocks.MANGROVE_WOOD);
		this.addDrop(Blocks.CHERRY_WOOD);
		this.addDrop(Blocks.STRIPPED_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_SPRUCE_WOOD);
		this.addDrop(Blocks.STRIPPED_BIRCH_WOOD);
		this.addDrop(Blocks.STRIPPED_JUNGLE_WOOD);
		this.addDrop(Blocks.STRIPPED_ACACIA_WOOD);
		this.addDrop(Blocks.STRIPPED_DARK_OAK_WOOD);
		this.addDrop(Blocks.STRIPPED_MANGROVE_WOOD);
		this.addDrop(Blocks.STRIPPED_CHERRY_WOOD);
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
		this.addDrop(Blocks.TORCHFLOWER);
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
		this.addDrop(Blocks.MANGROVE_STAIRS);
		this.addDrop(Blocks.BAMBOO_STAIRS);
		this.addDrop(Blocks.BAMBOO_MOSAIC_STAIRS);
		this.addDrop(Blocks.REDSTONE_WIRE);
		this.addDrop(Blocks.DIAMOND_BLOCK);
		this.addDrop(Blocks.CRAFTING_TABLE);
		this.addDrop(Blocks.OAK_SIGN);
		this.addDrop(Blocks.SPRUCE_SIGN);
		this.addDrop(Blocks.BIRCH_SIGN);
		this.addDrop(Blocks.ACACIA_SIGN);
		this.addDrop(Blocks.JUNGLE_SIGN);
		this.addDrop(Blocks.DARK_OAK_SIGN);
		this.addDrop(Blocks.MANGROVE_SIGN);
		this.addDrop(Blocks.CHERRY_SIGN);
		this.addDrop(Blocks.BAMBOO_SIGN);
		this.addDrop(Blocks.OAK_HANGING_SIGN);
		this.addDrop(Blocks.SPRUCE_HANGING_SIGN);
		this.addDrop(Blocks.BIRCH_HANGING_SIGN);
		this.addDrop(Blocks.ACACIA_HANGING_SIGN);
		this.addDrop(Blocks.CHERRY_HANGING_SIGN);
		this.addDrop(Blocks.JUNGLE_HANGING_SIGN);
		this.addDrop(Blocks.DARK_OAK_HANGING_SIGN);
		this.addDrop(Blocks.MANGROVE_HANGING_SIGN);
		this.addDrop(Blocks.CRIMSON_HANGING_SIGN);
		this.addDrop(Blocks.WARPED_HANGING_SIGN);
		this.addDrop(Blocks.BAMBOO_HANGING_SIGN);
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
		this.addDrop(Blocks.MANGROVE_PRESSURE_PLATE);
		this.addDrop(Blocks.CHERRY_PRESSURE_PLATE);
		this.addDrop(Blocks.BAMBOO_PRESSURE_PLATE);
		this.addDrop(Blocks.REDSTONE_TORCH);
		this.addDrop(Blocks.STONE_BUTTON);
		this.addDrop(Blocks.CACTUS);
		this.addDrop(Blocks.SUGAR_CANE);
		this.addDrop(Blocks.JUKEBOX);
		this.addDrop(Blocks.OAK_FENCE);
		this.addDrop(Blocks.MANGROVE_FENCE);
		this.addDrop(Blocks.BAMBOO_FENCE);
		this.addDrop(Blocks.PUMPKIN);
		this.addDrop(Blocks.NETHERRACK);
		this.addDrop(Blocks.SOUL_SAND);
		this.addDrop(Blocks.SOUL_SOIL);
		this.addDrop(Blocks.BASALT);
		this.addDrop(Blocks.POLISHED_BASALT);
		this.addDrop(Blocks.SMOOTH_BASALT);
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
		this.addDrop(Blocks.MANGROVE_TRAPDOOR);
		this.addDrop(Blocks.CHERRY_TRAPDOOR);
		this.addDrop(Blocks.BAMBOO_TRAPDOOR);
		this.addDrop(Blocks.STONE_BRICKS);
		this.addDrop(Blocks.MOSSY_STONE_BRICKS);
		this.addDrop(Blocks.CRACKED_STONE_BRICKS);
		this.addDrop(Blocks.CHISELED_STONE_BRICKS);
		this.addDrop(Blocks.IRON_BARS);
		this.addDrop(Blocks.OAK_FENCE_GATE);
		this.addDrop(Blocks.MANGROVE_FENCE_GATE);
		this.addDrop(Blocks.BAMBOO_FENCE_GATE);
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
		this.addDrop(Blocks.MANGROVE_BUTTON);
		this.addDrop(Blocks.CHERRY_BUTTON);
		this.addDrop(Blocks.BAMBOO_BUTTON);
		this.addDrop(Blocks.SKELETON_SKULL);
		this.addDrop(Blocks.WITHER_SKELETON_SKULL);
		this.addDrop(Blocks.ZOMBIE_HEAD);
		this.addDrop(Blocks.CREEPER_HEAD);
		this.addDrop(Blocks.DRAGON_HEAD);
		this.addDrop(Blocks.PIGLIN_HEAD);
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
		this.addDrop(Blocks.CHERRY_STAIRS);
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
		this.addDrop(Blocks.CHERRY_FENCE_GATE);
		this.addDrop(Blocks.SPRUCE_FENCE);
		this.addDrop(Blocks.BIRCH_FENCE);
		this.addDrop(Blocks.JUNGLE_FENCE);
		this.addDrop(Blocks.ACACIA_FENCE);
		this.addDrop(Blocks.DARK_OAK_FENCE);
		this.addDrop(Blocks.CHERRY_FENCE);
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
		this.addDrop(Blocks.MUD_BRICK_WALL);
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
		this.addDrop(Blocks.MUD_BRICKS);
		this.addDrop(Blocks.MUDDY_MANGROVE_ROOTS);
		this.addDrop(Blocks.MUD_BRICK_STAIRS);
		this.addDrop(Blocks.AMETHYST_BLOCK);
		this.addDrop(Blocks.CALCITE);
		this.addDrop(Blocks.TUFF);
		this.addDrop(Blocks.TINTED_GLASS);
		this.addDropWithSilkTouch(Blocks.SCULK_SENSOR);
		this.addDropWithSilkTouch(Blocks.CALIBRATED_SCULK_SENSOR);
		this.addDropWithSilkTouch(Blocks.SCULK);
		this.addDropWithSilkTouch(Blocks.SCULK_CATALYST);
		this.addDrop(Blocks.SCULK_VEIN, block -> this.multifaceGrowthDrops(block, WITH_SILK_TOUCH));
		this.addDropWithSilkTouch(Blocks.SCULK_SHRIEKER);
		this.addDropWithSilkTouch(Blocks.CHISELED_BOOKSHELF);
		this.addDrop(Blocks.COPPER_BLOCK);
		this.addDrop(Blocks.EXPOSED_COPPER);
		this.addDrop(Blocks.WEATHERED_COPPER);
		this.addDrop(Blocks.OXIDIZED_COPPER);
		this.addDrop(Blocks.CUT_COPPER);
		this.addDrop(Blocks.EXPOSED_CUT_COPPER);
		this.addDrop(Blocks.WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.OXIDIZED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_COPPER_BLOCK);
		this.addDrop(Blocks.WAXED_WEATHERED_COPPER);
		this.addDrop(Blocks.WAXED_EXPOSED_COPPER);
		this.addDrop(Blocks.WAXED_OXIDIZED_COPPER);
		this.addDrop(Blocks.WAXED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_WEATHERED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_EXPOSED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_OXIDIZED_CUT_COPPER);
		this.addDrop(Blocks.WAXED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.CUT_COPPER_STAIRS);
		this.addDrop(Blocks.EXPOSED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.WEATHERED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.OXIDIZED_CUT_COPPER_STAIRS);
		this.addDrop(Blocks.LIGHTNING_ROD);
		this.addDrop(Blocks.POINTED_DRIPSTONE);
		this.addDrop(Blocks.DRIPSTONE_BLOCK);
		this.addDrop(Blocks.SPORE_BLOSSOM);
		this.addDrop(Blocks.FLOWERING_AZALEA);
		this.addDrop(Blocks.AZALEA);
		this.addDrop(Blocks.MOSS_CARPET);
		this.addDrop(Blocks.PINK_PETALS, this.flowerbedDrops(Blocks.PINK_PETALS));
		this.addDrop(Blocks.BIG_DRIPLEAF);
		this.addDrop(Blocks.MOSS_BLOCK);
		this.addDrop(Blocks.ROOTED_DIRT);
		this.addDrop(Blocks.COBBLED_DEEPSLATE);
		this.addDrop(Blocks.COBBLED_DEEPSLATE_STAIRS);
		this.addDrop(Blocks.COBBLED_DEEPSLATE_WALL);
		this.addDrop(Blocks.POLISHED_DEEPSLATE);
		this.addDrop(Blocks.POLISHED_DEEPSLATE_STAIRS);
		this.addDrop(Blocks.POLISHED_DEEPSLATE_WALL);
		this.addDrop(Blocks.DEEPSLATE_TILES);
		this.addDrop(Blocks.DEEPSLATE_TILE_STAIRS);
		this.addDrop(Blocks.DEEPSLATE_TILE_WALL);
		this.addDrop(Blocks.DEEPSLATE_BRICKS);
		this.addDrop(Blocks.DEEPSLATE_BRICK_STAIRS);
		this.addDrop(Blocks.DEEPSLATE_BRICK_WALL);
		this.addDrop(Blocks.CHISELED_DEEPSLATE);
		this.addDrop(Blocks.CRACKED_DEEPSLATE_BRICKS);
		this.addDrop(Blocks.CRACKED_DEEPSLATE_TILES);
		this.addDrop(Blocks.RAW_IRON_BLOCK);
		this.addDrop(Blocks.RAW_COPPER_BLOCK);
		this.addDrop(Blocks.RAW_GOLD_BLOCK);
		this.addDrop(Blocks.OCHRE_FROGLIGHT);
		this.addDrop(Blocks.VERDANT_FROGLIGHT);
		this.addDrop(Blocks.PEARLESCENT_FROGLIGHT);
		this.addDrop(Blocks.MANGROVE_ROOTS);
		this.addDrop(Blocks.MANGROVE_LOG);
		this.addDrop(Blocks.MUD);
		this.addDrop(Blocks.PACKED_MUD);
		this.addDrop(Blocks.FARMLAND, Blocks.DIRT);
		this.addDrop(Blocks.TRIPWIRE, Items.STRING);
		this.addDrop(Blocks.DIRT_PATH, Blocks.DIRT);
		this.addDrop(Blocks.KELP_PLANT, Blocks.KELP);
		this.addDrop(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
		this.addDrop(Blocks.WATER_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.LAVA_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.POWDER_SNOW_CAULDRON, Blocks.CAULDRON);
		this.addDrop(Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF);
		this.addDrop(Blocks.STONE, block -> this.drops(block, Blocks.COBBLESTONE));
		this.addDrop(Blocks.DEEPSLATE, block -> this.drops(block, Blocks.COBBLED_DEEPSLATE));
		this.addDrop(Blocks.GRASS_BLOCK, block -> this.drops(block, Blocks.DIRT));
		this.addDrop(Blocks.PODZOL, block -> this.drops(block, Blocks.DIRT));
		this.addDrop(Blocks.MYCELIUM, block -> this.drops(block, Blocks.DIRT));
		this.addDrop(Blocks.TUBE_CORAL_BLOCK, block -> this.drops(block, Blocks.DEAD_TUBE_CORAL_BLOCK));
		this.addDrop(Blocks.BRAIN_CORAL_BLOCK, block -> this.drops(block, Blocks.DEAD_BRAIN_CORAL_BLOCK));
		this.addDrop(Blocks.BUBBLE_CORAL_BLOCK, block -> this.drops(block, Blocks.DEAD_BUBBLE_CORAL_BLOCK));
		this.addDrop(Blocks.FIRE_CORAL_BLOCK, block -> this.drops(block, Blocks.DEAD_FIRE_CORAL_BLOCK));
		this.addDrop(Blocks.HORN_CORAL_BLOCK, block -> this.drops(block, Blocks.DEAD_HORN_CORAL_BLOCK));
		this.addDrop(Blocks.CRIMSON_NYLIUM, block -> this.drops(block, Blocks.NETHERRACK));
		this.addDrop(Blocks.WARPED_NYLIUM, block -> this.drops(block, Blocks.NETHERRACK));
		this.addDrop(Blocks.BOOKSHELF, block -> this.drops(block, Items.BOOK, ConstantLootNumberProvider.create(3.0F)));
		this.addDrop(Blocks.CLAY, block -> this.drops(block, Items.CLAY_BALL, ConstantLootNumberProvider.create(4.0F)));
		this.addDrop(Blocks.ENDER_CHEST, block -> this.drops(block, Blocks.OBSIDIAN, ConstantLootNumberProvider.create(8.0F)));
		this.addDrop(Blocks.SNOW_BLOCK, block -> this.drops(block, Items.SNOWBALL, ConstantLootNumberProvider.create(4.0F)));
		this.addDrop(Blocks.CHORUS_PLANT, this.drops(Items.CHORUS_FRUIT, UniformLootNumberProvider.create(0.0F, 1.0F)));
		this.addPottedPlantDrops(Blocks.POTTED_OAK_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_SPRUCE_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_BIRCH_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_JUNGLE_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_ACACIA_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_DARK_OAK_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_MANGROVE_PROPAGULE);
		this.addPottedPlantDrops(Blocks.POTTED_CHERRY_SAPLING);
		this.addPottedPlantDrops(Blocks.POTTED_FERN);
		this.addPottedPlantDrops(Blocks.POTTED_DANDELION);
		this.addPottedPlantDrops(Blocks.POTTED_POPPY);
		this.addPottedPlantDrops(Blocks.POTTED_BLUE_ORCHID);
		this.addPottedPlantDrops(Blocks.POTTED_ALLIUM);
		this.addPottedPlantDrops(Blocks.POTTED_AZURE_BLUET);
		this.addPottedPlantDrops(Blocks.POTTED_RED_TULIP);
		this.addPottedPlantDrops(Blocks.POTTED_ORANGE_TULIP);
		this.addPottedPlantDrops(Blocks.POTTED_WHITE_TULIP);
		this.addPottedPlantDrops(Blocks.POTTED_PINK_TULIP);
		this.addPottedPlantDrops(Blocks.POTTED_OXEYE_DAISY);
		this.addPottedPlantDrops(Blocks.POTTED_CORNFLOWER);
		this.addPottedPlantDrops(Blocks.POTTED_LILY_OF_THE_VALLEY);
		this.addPottedPlantDrops(Blocks.POTTED_WITHER_ROSE);
		this.addPottedPlantDrops(Blocks.POTTED_RED_MUSHROOM);
		this.addPottedPlantDrops(Blocks.POTTED_BROWN_MUSHROOM);
		this.addPottedPlantDrops(Blocks.POTTED_DEAD_BUSH);
		this.addPottedPlantDrops(Blocks.POTTED_CACTUS);
		this.addPottedPlantDrops(Blocks.POTTED_BAMBOO);
		this.addPottedPlantDrops(Blocks.POTTED_CRIMSON_FUNGUS);
		this.addPottedPlantDrops(Blocks.POTTED_WARPED_FUNGUS);
		this.addPottedPlantDrops(Blocks.POTTED_CRIMSON_ROOTS);
		this.addPottedPlantDrops(Blocks.POTTED_WARPED_ROOTS);
		this.addPottedPlantDrops(Blocks.POTTED_AZALEA_BUSH);
		this.addPottedPlantDrops(Blocks.POTTED_FLOWERING_AZALEA_BUSH);
		this.addPottedPlantDrops(Blocks.POTTED_TORCHFLOWER);
		this.addDrop(Blocks.OAK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.PETRIFIED_OAK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SPRUCE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BIRCH_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.JUNGLE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.ACACIA_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.DARK_OAK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.MANGROVE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CHERRY_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BAMBOO_MOSAIC_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.COBBLESTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.DARK_PRISMARINE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.NETHER_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.PRISMARINE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.PRISMARINE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.PURPUR_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.QUARTZ_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.RED_SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CUT_RED_SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CUT_SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.STONE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.STONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SMOOTH_STONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_GRANITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SMOOTH_RED_SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.MOSSY_STONE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_DIORITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.MOSSY_COBBLESTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.END_STONE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SMOOTH_SANDSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.SMOOTH_QUARTZ_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.GRANITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.ANDESITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.RED_NETHER_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_ANDESITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.DIORITE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CRIMSON_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WARPED_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.BLACKSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_BLACKSTONE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.OXIDIZED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WEATHERED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.EXPOSED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.WAXED_CUT_COPPER_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.COBBLED_DEEPSLATE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.POLISHED_DEEPSLATE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.DEEPSLATE_TILE_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.DEEPSLATE_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.MUD_BRICK_SLAB, block -> this.slabDrops(block));
		this.addDrop(Blocks.OAK_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.SPRUCE_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.BIRCH_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.JUNGLE_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.ACACIA_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.DARK_OAK_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.MANGROVE_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.CHERRY_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.BAMBOO_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.WARPED_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.CRIMSON_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.IRON_DOOR, block -> this.doorDrops(block));
		this.addDrop(Blocks.BLACK_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.BLUE_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.BROWN_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.CYAN_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.GRAY_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.GREEN_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIGHT_BLUE_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIGHT_GRAY_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LIME_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.MAGENTA_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.PURPLE_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.ORANGE_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.PINK_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.RED_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.WHITE_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.YELLOW_BED, block -> this.dropsWithProperty(block, BedBlock.PART, BedPart.HEAD));
		this.addDrop(Blocks.LILAC, block -> this.dropsWithProperty(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.SUNFLOWER, block -> this.dropsWithProperty(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.PEONY, block -> this.dropsWithProperty(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(Blocks.ROSE_BUSH, block -> this.dropsWithProperty(block, TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
		this.addDrop(
			Blocks.TNT,
			LootTable.builder()
				.pool(
					this.addSurvivesExplosionCondition(
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
			block -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
									block,
									ItemEntry.builder(Items.COCOA_BEANS)
										.apply(
											SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(CocoaBlock.AGE, 2)))
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.SEA_PICKLE,
			block -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
									Blocks.SEA_PICKLE,
									ItemEntry.builder(block)
										.apply(
											List.of(2, 3, 4),
											pickles -> SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)pickles.intValue()))
													.conditionally(
														BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(SeaPickleBlock.PICKLES, pickles))
													)
										)
								)
							)
					)
		);
		this.addDrop(
			Blocks.COMPOSTER,
			block -> LootTable.builder()
					.pool(LootPool.builder().with((LootPoolEntry.Builder<?>)this.applyExplosionDecay(block, ItemEntry.builder(Items.COMPOSTER))))
					.pool(
						LootPool.builder()
							.with(ItemEntry.builder(Items.BONE_MEAL))
							.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(ComposterBlock.LEVEL, 8)))
					)
		);
		this.addDrop(Blocks.CAVE_VINES, block -> BlockLootTableGenerator.glowBerryDrops(block));
		this.addDrop(Blocks.CAVE_VINES_PLANT, block -> BlockLootTableGenerator.glowBerryDrops(block));
		this.addDrop(Blocks.CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.WHITE_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.ORANGE_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.MAGENTA_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.LIGHT_BLUE_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.YELLOW_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.LIME_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.PINK_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.GRAY_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.LIGHT_GRAY_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.CYAN_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.PURPLE_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.BLUE_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.BROWN_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.GREEN_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.RED_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.BLACK_CANDLE, block -> this.candleDrops(block));
		this.addDrop(Blocks.BEACON, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.BREWING_STAND, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.CHEST, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.DISPENSER, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.DROPPER, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.ENCHANTING_TABLE, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.FURNACE, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.HOPPER, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.TRAPPED_CHEST, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.SMOKER, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.BLAST_FURNACE, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.BARREL, block -> this.nameableContainerDrops(block));
		this.addDrop(Blocks.CARTOGRAPHY_TABLE);
		this.addDrop(Blocks.FLETCHING_TABLE);
		this.addDrop(Blocks.GRINDSTONE);
		this.addDrop(Blocks.LECTERN);
		this.addDrop(Blocks.SMITHING_TABLE);
		this.addDrop(Blocks.STONECUTTER);
		this.addDrop(Blocks.BELL, this::drops);
		this.addDrop(Blocks.LANTERN, this::drops);
		this.addDrop(Blocks.SOUL_LANTERN, this::drops);
		this.addDrop(Blocks.SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.BLACK_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.BLUE_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.BROWN_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.CYAN_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.GRAY_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.GREEN_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.LIGHT_BLUE_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.LIGHT_GRAY_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.LIME_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.MAGENTA_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.ORANGE_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.PINK_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.PURPLE_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.RED_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.WHITE_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.YELLOW_SHULKER_BOX, block -> this.shulkerBoxDrops(block));
		this.addDrop(Blocks.BLACK_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.BLUE_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.BROWN_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.CYAN_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.GRAY_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.GREEN_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.LIGHT_BLUE_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.LIGHT_GRAY_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.LIME_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.MAGENTA_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.ORANGE_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.PINK_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.PURPLE_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.RED_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.WHITE_BANNER, block -> this.bannerDrops(block));
		this.addDrop(Blocks.YELLOW_BANNER, block -> this.bannerDrops(block));
		this.addDrop(
			Blocks.PLAYER_HEAD,
			block -> LootTable.builder()
					.pool(
						this.addSurvivesExplosionCondition(
							block,
							LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1.0F))
								.with(
									ItemEntry.builder(block)
										.apply(
											CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
												.withOperation("SkullOwner", "SkullOwner")
												.withOperation("note_block_sound", String.format(Locale.ROOT, "%s.%s", "BlockEntityTag", "note_block_sound"))
										)
								)
						)
					)
		);
		this.addDrop(Blocks.BEE_NEST, block -> beeNestDrops(block));
		this.addDrop(Blocks.BEEHIVE, block -> beehiveDrops(block));
		this.addDrop(Blocks.OAK_LEAVES, block -> this.oakLeavesDrops(block, Blocks.OAK_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.SPRUCE_LEAVES, block -> this.leavesDrops(block, Blocks.SPRUCE_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.BIRCH_LEAVES, block -> this.leavesDrops(block, Blocks.BIRCH_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.JUNGLE_LEAVES, block -> this.leavesDrops(block, Blocks.JUNGLE_SAPLING, JUNGLE_SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.ACACIA_LEAVES, block -> this.leavesDrops(block, Blocks.ACACIA_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.DARK_OAK_LEAVES, block -> this.oakLeavesDrops(block, Blocks.DARK_OAK_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.CHERRY_LEAVES, block -> this.leavesDrops(block, Blocks.CHERRY_SAPLING, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.AZALEA_LEAVES, block -> this.leavesDrops(block, Blocks.AZALEA, SAPLING_DROP_CHANCE));
		this.addDrop(Blocks.FLOWERING_AZALEA_LEAVES, block -> this.leavesDrops(block, Blocks.FLOWERING_AZALEA, SAPLING_DROP_CHANCE));
		LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS)
			.properties(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
		this.addDrop(Blocks.BEETROOTS, this.cropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, builder));
		LootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.WHEAT)
			.properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
		this.addDrop(Blocks.WHEAT, this.cropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, builder2));
		LootCondition.Builder builder3 = BlockStatePropertyLootCondition.builder(Blocks.CARROTS)
			.properties(StatePredicate.Builder.create().exactMatch(CarrotsBlock.AGE, 7));
		LootCondition.Builder builder4 = BlockStatePropertyLootCondition.builder(Blocks.MANGROVE_PROPAGULE)
			.properties(StatePredicate.Builder.create().exactMatch(PropaguleBlock.AGE, 4));
		this.addDrop(
			Blocks.MANGROVE_PROPAGULE,
			this.applyExplosionDecay(
				Blocks.MANGROVE_PROPAGULE, LootTable.builder().pool(LootPool.builder().conditionally(builder4).with(ItemEntry.builder(Items.MANGROVE_PROPAGULE)))
			)
		);
		this.addDrop(
			Blocks.TORCHFLOWER_CROP,
			this.applyExplosionDecay(Blocks.TORCHFLOWER_CROP, LootTable.builder().pool(LootPool.builder().with(ItemEntry.builder(Items.TORCHFLOWER_SEEDS))))
		);
		this.addDrop(Blocks.SNIFFER_EGG);
		this.addDrop(Blocks.PITCHER_CROP, block -> this.pitcherCropDrops());
		this.addDrop(Blocks.PITCHER_PLANT);
		this.addDrop(
			Blocks.PITCHER_PLANT,
			this.applyExplosionDecay(
				Blocks.PITCHER_PLANT,
				LootTable.builder()
					.pool(
						LootPool.builder()
							.with(
								ItemEntry.builder(Items.PITCHER_PLANT)
									.conditionally(
										BlockStatePropertyLootCondition.builder(Blocks.PITCHER_PLANT)
											.properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER))
									)
							)
					)
			)
		);
		this.addDrop(
			Blocks.CARROTS,
			this.applyExplosionDecay(
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
		LootCondition.Builder builder5 = BlockStatePropertyLootCondition.builder(Blocks.POTATOES)
			.properties(StatePredicate.Builder.create().exactMatch(PotatoesBlock.AGE, 7));
		this.addDrop(
			Blocks.POTATOES,
			this.applyExplosionDecay(
				Blocks.POTATOES,
				LootTable.builder()
					.pool(LootPool.builder().with(ItemEntry.builder(Items.POTATO)))
					.pool(
						LootPool.builder()
							.conditionally(builder5)
							.with(ItemEntry.builder(Items.POTATO).apply(ApplyBonusLootFunction.binomialWithBonusCount(Enchantments.FORTUNE, 0.5714286F, 3)))
					)
					.pool(LootPool.builder().conditionally(builder5).with(ItemEntry.builder(Items.POISONOUS_POTATO).conditionally(RandomChanceLootCondition.builder(0.02F))))
			)
		);
		this.addDrop(
			Blocks.SWEET_BERRY_BUSH,
			block -> this.applyExplosionDecay(
					block,
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
		this.addDrop(Blocks.BROWN_MUSHROOM_BLOCK, block -> this.mushroomBlockDrops(block, Blocks.BROWN_MUSHROOM));
		this.addDrop(Blocks.RED_MUSHROOM_BLOCK, block -> this.mushroomBlockDrops(block, Blocks.RED_MUSHROOM));
		this.addDrop(Blocks.COAL_ORE, block -> this.oreDrops(block, Items.COAL));
		this.addDrop(Blocks.DEEPSLATE_COAL_ORE, block -> this.oreDrops(block, Items.COAL));
		this.addDrop(Blocks.EMERALD_ORE, block -> this.oreDrops(block, Items.EMERALD));
		this.addDrop(Blocks.DEEPSLATE_EMERALD_ORE, block -> this.oreDrops(block, Items.EMERALD));
		this.addDrop(Blocks.NETHER_QUARTZ_ORE, block -> this.oreDrops(block, Items.QUARTZ));
		this.addDrop(Blocks.DIAMOND_ORE, block -> this.oreDrops(block, Items.DIAMOND));
		this.addDrop(Blocks.DEEPSLATE_DIAMOND_ORE, block -> this.oreDrops(block, Items.DIAMOND));
		this.addDrop(Blocks.COPPER_ORE, block -> this.copperOreDrops(block));
		this.addDrop(Blocks.DEEPSLATE_COPPER_ORE, block -> this.copperOreDrops(block));
		this.addDrop(Blocks.IRON_ORE, block -> this.oreDrops(block, Items.RAW_IRON));
		this.addDrop(Blocks.DEEPSLATE_IRON_ORE, block -> this.oreDrops(block, Items.RAW_IRON));
		this.addDrop(Blocks.GOLD_ORE, block -> this.oreDrops(block, Items.RAW_GOLD));
		this.addDrop(Blocks.DEEPSLATE_GOLD_ORE, block -> this.oreDrops(block, Items.RAW_GOLD));
		this.addDrop(
			Blocks.NETHER_GOLD_ORE,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
						block,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 6.0F)))
							.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
					)
				)
		);
		this.addDrop(Blocks.LAPIS_ORE, block -> this.lapisOreDrops(block));
		this.addDrop(Blocks.DEEPSLATE_LAPIS_ORE, block -> this.lapisOreDrops(block));
		this.addDrop(
			Blocks.COBWEB,
			block -> dropsWithSilkTouchOrShears(block, (LootPoolEntry.Builder<?>)this.addSurvivesExplosionCondition(block, ItemEntry.builder(Items.STRING)))
		);
		this.addDrop(
			Blocks.DEAD_BUSH,
			block -> dropsWithShears(
					block,
					(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
						block, ItemEntry.builder(Items.STICK).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
					)
				)
		);
		this.addDrop(Blocks.NETHER_SPROUTS, block -> BlockLootTableGenerator.dropsWithShears(block));
		this.addDrop(Blocks.SEAGRASS, block -> BlockLootTableGenerator.dropsWithShears(block));
		this.addDrop(Blocks.VINE, block -> BlockLootTableGenerator.dropsWithShears(block));
		this.addDrop(Blocks.GLOW_LICHEN, block -> this.multifaceGrowthDrops(block, WITH_SHEARS));
		this.addDrop(Blocks.HANGING_ROOTS, block -> BlockLootTableGenerator.dropsWithShears(block));
		this.addDrop(Blocks.SMALL_DRIPLEAF, block -> BlockLootTableGenerator.dropsWithShears(block));
		this.addDrop(Blocks.MANGROVE_LEAVES, block -> this.mangroveLeavesDrops(block));
		this.addDrop(Blocks.TALL_SEAGRASS, seagrassDrops(Blocks.SEAGRASS));
		this.addDrop(Blocks.LARGE_FERN, block -> this.tallGrassDrops(block, Blocks.FERN));
		this.addDrop(Blocks.TALL_GRASS, block -> this.tallGrassDrops(block, Blocks.GRASS));
		this.addDrop(Blocks.MELON_STEM, block -> this.cropStemDrops(block, Items.MELON_SEEDS));
		this.addDrop(Blocks.ATTACHED_MELON_STEM, block -> this.attachedCropStemDrops(block, Items.MELON_SEEDS));
		this.addDrop(Blocks.PUMPKIN_STEM, block -> this.cropStemDrops(block, Items.PUMPKIN_SEEDS));
		this.addDrop(Blocks.ATTACHED_PUMPKIN_STEM, block -> this.attachedCropStemDrops(block, Items.PUMPKIN_SEEDS));
		this.addDrop(
			Blocks.CHORUS_FLOWER,
			block -> LootTable.builder()
					.pool(
						LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.with(
								((LeafEntry.Builder)this.addSurvivesExplosionCondition(block, ItemEntry.builder(block)))
									.conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							)
					)
		);
		this.addDrop(Blocks.FERN, block -> this.grassDrops(block));
		this.addDrop(Blocks.GRASS, block -> this.grassDrops(block));
		this.addDrop(
			Blocks.GLOWSTONE,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
						block,
						ItemEntry.builder(Items.GLOWSTONE_DUST)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4)))
					)
				)
		);
		this.addDrop(
			Blocks.MELON,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
						block,
						ItemEntry.builder(Items.MELON_SLICE)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0F, 7.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.createMax(9)))
					)
				)
		);
		this.addDrop(Blocks.REDSTONE_ORE, block -> this.redstoneOreDrops(block));
		this.addDrop(Blocks.DEEPSLATE_REDSTONE_ORE, block -> this.redstoneOreDrops(block));
		this.addDrop(
			Blocks.SEA_LANTERN,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
						block,
						ItemEntry.builder(Items.PRISMARINE_CRYSTALS)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
							.apply(ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE))
							.apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 5)))
					)
				)
		);
		this.addDrop(
			Blocks.NETHER_WART,
			block -> LootTable.builder()
					.pool(
						this.applyExplosionDecay(
							block,
							LootPool.builder()
								.rolls(ConstantLootNumberProvider.create(1.0F))
								.with(
									ItemEntry.builder(Items.NETHER_WART)
										.apply(
											SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 4.0F))
												.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
										.apply(
											ApplyBonusLootFunction.uniformBonusCount(Enchantments.FORTUNE)
												.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(NetherWartBlock.AGE, 3)))
										)
								)
						)
					)
		);
		this.addDrop(
			Blocks.SNOW,
			block -> LootTable.builder()
					.pool(
						LootPool.builder()
							.conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
							.with(
								AlternativeEntry.builder(
									AlternativeEntry.builder(
											SnowBlock.LAYERS.getValues(),
											integer -> ItemEntry.builder(Items.SNOWBALL)
													.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, integer)))
													.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)integer.intValue())))
										)
										.conditionally(WITHOUT_SILK_TOUCH),
									AlternativeEntry.builder(
										SnowBlock.LAYERS.getValues(),
										integer -> integer == 8
												? ItemEntry.builder(Blocks.SNOW_BLOCK)
												: ItemEntry.builder(Blocks.SNOW)
													.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create((float)integer.intValue())))
													.conditionally(BlockStatePropertyLootCondition.builder(block).properties(StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, integer)))
									)
								)
							)
					)
		);
		this.addDrop(
			Blocks.GRAVEL,
			block -> dropsWithSilkTouch(
					block,
					this.addSurvivesExplosionCondition(
						block,
						ItemEntry.builder(Items.FLINT)
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(block))
					)
				)
		);
		this.addDrop(
			Blocks.CAMPFIRE,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.addSurvivesExplosionCondition(
						block, ItemEntry.builder(Items.CHARCOAL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
					)
				)
		);
		this.addDrop(
			Blocks.GILDED_BLACKSTONE,
			block -> dropsWithSilkTouch(
					block,
					this.addSurvivesExplosionCondition(
						block,
						ItemEntry.builder(Items.GOLD_NUGGET)
							.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 5.0F)))
							.conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))
							.alternatively(ItemEntry.builder(block))
					)
				)
		);
		this.addDrop(
			Blocks.SOUL_CAMPFIRE,
			block -> dropsWithSilkTouch(
					block,
					(LootPoolEntry.Builder<?>)this.addSurvivesExplosionCondition(
						block, ItemEntry.builder(Items.SOUL_SOIL).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
					)
				)
		);
		this.addDrop(
			Blocks.AMETHYST_CLUSTER,
			block -> dropsWithSilkTouch(
					block,
					ItemEntry.builder(Items.AMETHYST_SHARD)
						.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F)))
						.apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
						.conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.CLUSTER_MAX_HARVESTABLES)))
						.alternatively(
							(LootPoolEntry.Builder<?>)this.applyExplosionDecay(
								block, ItemEntry.builder(Items.AMETHYST_SHARD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F)))
							)
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
		this.addDropWithSilkTouch(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);
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
		this.addDrop(Blocks.FROGSPAWN, dropsNothing());
		this.addDrop(Blocks.REINFORCED_DEEPSLATE, dropsNothing());
		this.addDrop(Blocks.SUSPICIOUS_SAND, dropsNothing());
		this.addDrop(Blocks.SUSPICIOUS_GRAVEL, dropsNothing());
	}

	private LootTable.Builder decoratedPotDrops(Block block) {
		return LootTable.builder()
			.pool(
				LootPool.builder()
					.rolls(ConstantLootNumberProvider.create(1.0F))
					.with(
						DynamicEntry.builder(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)
							.conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(ItemTags.BREAKS_DECORATED_POTS)))
							.conditionally(WITHOUT_SILK_TOUCH)
							.alternatively(
								ItemEntry.builder(block).apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("sherds", "BlockEntityTag.sherds"))
							)
					)
			);
	}

	private LootTable.Builder pitcherCropDrops() {
		return this.applyExplosionDecay(
			Blocks.PITCHER_CROP,
			LootTable.builder()
				.pool(
					LootPool.builder()
						.with(
							AlternativeEntry.builder(
								PitcherCropBlock.AGE.getValues(),
								age -> {
									BlockStatePropertyLootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.PITCHER_CROP)
										.properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, DoubleBlockHalf.LOWER));
									BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(Blocks.PITCHER_CROP)
										.properties(StatePredicate.Builder.create().exactMatch(PitcherCropBlock.AGE, age));
									return age == 4
										? ItemEntry.builder(Items.PITCHER_PLANT)
											.conditionally(builder2)
											.conditionally(builder)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
										: ItemEntry.builder(Items.PITCHER_POD)
											.conditionally(builder2)
											.conditionally(builder)
											.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
								}
							)
						)
				)
		);
	}
}
