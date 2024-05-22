package net.minecraft.item;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.InstrumentTags;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.raid.Raid;

public class ItemGroups {
	private static final Identifier INVENTORY_TAB_TEXTURE_ID = ItemGroup.getTabTextureId("inventory");
	private static final Identifier ITEM_SEARCH_TAB_TEXTURE_ID = ItemGroup.getTabTextureId("item_search");
	private static final RegistryKey<ItemGroup> BUILDING_BLOCKS = register("building_blocks");
	private static final RegistryKey<ItemGroup> COLORED_BLOCKS = register("colored_blocks");
	private static final RegistryKey<ItemGroup> NATURAL = register("natural_blocks");
	private static final RegistryKey<ItemGroup> FUNCTIONAL = register("functional_blocks");
	private static final RegistryKey<ItemGroup> REDSTONE = register("redstone_blocks");
	private static final RegistryKey<ItemGroup> HOTBAR = register("hotbar");
	private static final RegistryKey<ItemGroup> SEARCH = register("search");
	private static final RegistryKey<ItemGroup> TOOLS = register("tools_and_utilities");
	private static final RegistryKey<ItemGroup> COMBAT = register("combat");
	private static final RegistryKey<ItemGroup> FOOD_AND_DRINK = register("food_and_drinks");
	private static final RegistryKey<ItemGroup> INGREDIENTS = register("ingredients");
	private static final RegistryKey<ItemGroup> SPAWN_EGGS = register("spawn_eggs");
	private static final RegistryKey<ItemGroup> OPERATOR = register("op_blocks");
	private static final RegistryKey<ItemGroup> INVENTORY = register("inventory");
	private static final Comparator<RegistryEntry<PaintingVariant>> PAINTING_VARIANT_COMPARATOR = Comparator.comparing(
		RegistryEntry::value, Comparator.comparingInt(PaintingVariant::getArea).thenComparing(PaintingVariant::width)
	);
	@Nullable
	private static ItemGroup.DisplayContext displayContext;

	private static RegistryKey<ItemGroup> register(String id) {
		return RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.ofVanilla(id));
	}

	public static ItemGroup registerAndGetDefault(Registry<ItemGroup> registry) {
		Registry.register(
			registry,
			BUILDING_BLOCKS,
			ItemGroup.create(ItemGroup.Row.TOP, 0)
				.displayName(Text.translatable("itemGroup.buildingBlocks"))
				.icon(() -> new ItemStack(Blocks.BRICKS))
				.entries((displayContext, entries) -> {
					entries.add(Items.OAK_LOG);
					entries.add(Items.OAK_WOOD);
					entries.add(Items.STRIPPED_OAK_LOG);
					entries.add(Items.STRIPPED_OAK_WOOD);
					entries.add(Items.OAK_PLANKS);
					entries.add(Items.OAK_STAIRS);
					entries.add(Items.OAK_SLAB);
					entries.add(Items.OAK_FENCE);
					entries.add(Items.OAK_FENCE_GATE);
					entries.add(Items.OAK_DOOR);
					entries.add(Items.OAK_TRAPDOOR);
					entries.add(Items.OAK_PRESSURE_PLATE);
					entries.add(Items.OAK_BUTTON);
					entries.add(Items.SPRUCE_LOG);
					entries.add(Items.SPRUCE_WOOD);
					entries.add(Items.STRIPPED_SPRUCE_LOG);
					entries.add(Items.STRIPPED_SPRUCE_WOOD);
					entries.add(Items.SPRUCE_PLANKS);
					entries.add(Items.SPRUCE_STAIRS);
					entries.add(Items.SPRUCE_SLAB);
					entries.add(Items.SPRUCE_FENCE);
					entries.add(Items.SPRUCE_FENCE_GATE);
					entries.add(Items.SPRUCE_DOOR);
					entries.add(Items.SPRUCE_TRAPDOOR);
					entries.add(Items.SPRUCE_PRESSURE_PLATE);
					entries.add(Items.SPRUCE_BUTTON);
					entries.add(Items.BIRCH_LOG);
					entries.add(Items.BIRCH_WOOD);
					entries.add(Items.STRIPPED_BIRCH_LOG);
					entries.add(Items.STRIPPED_BIRCH_WOOD);
					entries.add(Items.BIRCH_PLANKS);
					entries.add(Items.BIRCH_STAIRS);
					entries.add(Items.BIRCH_SLAB);
					entries.add(Items.BIRCH_FENCE);
					entries.add(Items.BIRCH_FENCE_GATE);
					entries.add(Items.BIRCH_DOOR);
					entries.add(Items.BIRCH_TRAPDOOR);
					entries.add(Items.BIRCH_PRESSURE_PLATE);
					entries.add(Items.BIRCH_BUTTON);
					entries.add(Items.JUNGLE_LOG);
					entries.add(Items.JUNGLE_WOOD);
					entries.add(Items.STRIPPED_JUNGLE_LOG);
					entries.add(Items.STRIPPED_JUNGLE_WOOD);
					entries.add(Items.JUNGLE_PLANKS);
					entries.add(Items.JUNGLE_STAIRS);
					entries.add(Items.JUNGLE_SLAB);
					entries.add(Items.JUNGLE_FENCE);
					entries.add(Items.JUNGLE_FENCE_GATE);
					entries.add(Items.JUNGLE_DOOR);
					entries.add(Items.JUNGLE_TRAPDOOR);
					entries.add(Items.JUNGLE_PRESSURE_PLATE);
					entries.add(Items.JUNGLE_BUTTON);
					entries.add(Items.ACACIA_LOG);
					entries.add(Items.ACACIA_WOOD);
					entries.add(Items.STRIPPED_ACACIA_LOG);
					entries.add(Items.STRIPPED_ACACIA_WOOD);
					entries.add(Items.ACACIA_PLANKS);
					entries.add(Items.ACACIA_STAIRS);
					entries.add(Items.ACACIA_SLAB);
					entries.add(Items.ACACIA_FENCE);
					entries.add(Items.ACACIA_FENCE_GATE);
					entries.add(Items.ACACIA_DOOR);
					entries.add(Items.ACACIA_TRAPDOOR);
					entries.add(Items.ACACIA_PRESSURE_PLATE);
					entries.add(Items.ACACIA_BUTTON);
					entries.add(Items.DARK_OAK_LOG);
					entries.add(Items.DARK_OAK_WOOD);
					entries.add(Items.STRIPPED_DARK_OAK_LOG);
					entries.add(Items.STRIPPED_DARK_OAK_WOOD);
					entries.add(Items.DARK_OAK_PLANKS);
					entries.add(Items.DARK_OAK_STAIRS);
					entries.add(Items.DARK_OAK_SLAB);
					entries.add(Items.DARK_OAK_FENCE);
					entries.add(Items.DARK_OAK_FENCE_GATE);
					entries.add(Items.DARK_OAK_DOOR);
					entries.add(Items.DARK_OAK_TRAPDOOR);
					entries.add(Items.DARK_OAK_PRESSURE_PLATE);
					entries.add(Items.DARK_OAK_BUTTON);
					entries.add(Items.MANGROVE_LOG);
					entries.add(Items.MANGROVE_WOOD);
					entries.add(Items.STRIPPED_MANGROVE_LOG);
					entries.add(Items.STRIPPED_MANGROVE_WOOD);
					entries.add(Items.MANGROVE_PLANKS);
					entries.add(Items.MANGROVE_STAIRS);
					entries.add(Items.MANGROVE_SLAB);
					entries.add(Items.MANGROVE_FENCE);
					entries.add(Items.MANGROVE_FENCE_GATE);
					entries.add(Items.MANGROVE_DOOR);
					entries.add(Items.MANGROVE_TRAPDOOR);
					entries.add(Items.MANGROVE_PRESSURE_PLATE);
					entries.add(Items.MANGROVE_BUTTON);
					entries.add(Items.CHERRY_LOG);
					entries.add(Items.CHERRY_WOOD);
					entries.add(Items.STRIPPED_CHERRY_LOG);
					entries.add(Items.STRIPPED_CHERRY_WOOD);
					entries.add(Items.CHERRY_PLANKS);
					entries.add(Items.CHERRY_STAIRS);
					entries.add(Items.CHERRY_SLAB);
					entries.add(Items.CHERRY_FENCE);
					entries.add(Items.CHERRY_FENCE_GATE);
					entries.add(Items.CHERRY_DOOR);
					entries.add(Items.CHERRY_TRAPDOOR);
					entries.add(Items.CHERRY_PRESSURE_PLATE);
					entries.add(Items.CHERRY_BUTTON);
					entries.add(Items.BAMBOO_BLOCK);
					entries.add(Items.STRIPPED_BAMBOO_BLOCK);
					entries.add(Items.BAMBOO_PLANKS);
					entries.add(Items.BAMBOO_MOSAIC);
					entries.add(Items.BAMBOO_STAIRS);
					entries.add(Items.BAMBOO_MOSAIC_STAIRS);
					entries.add(Items.BAMBOO_SLAB);
					entries.add(Items.BAMBOO_MOSAIC_SLAB);
					entries.add(Items.BAMBOO_FENCE);
					entries.add(Items.BAMBOO_FENCE_GATE);
					entries.add(Items.BAMBOO_DOOR);
					entries.add(Items.BAMBOO_TRAPDOOR);
					entries.add(Items.BAMBOO_PRESSURE_PLATE);
					entries.add(Items.BAMBOO_BUTTON);
					entries.add(Items.CRIMSON_STEM);
					entries.add(Items.CRIMSON_HYPHAE);
					entries.add(Items.STRIPPED_CRIMSON_STEM);
					entries.add(Items.STRIPPED_CRIMSON_HYPHAE);
					entries.add(Items.CRIMSON_PLANKS);
					entries.add(Items.CRIMSON_STAIRS);
					entries.add(Items.CRIMSON_SLAB);
					entries.add(Items.CRIMSON_FENCE);
					entries.add(Items.CRIMSON_FENCE_GATE);
					entries.add(Items.CRIMSON_DOOR);
					entries.add(Items.CRIMSON_TRAPDOOR);
					entries.add(Items.CRIMSON_PRESSURE_PLATE);
					entries.add(Items.CRIMSON_BUTTON);
					entries.add(Items.WARPED_STEM);
					entries.add(Items.WARPED_HYPHAE);
					entries.add(Items.STRIPPED_WARPED_STEM);
					entries.add(Items.STRIPPED_WARPED_HYPHAE);
					entries.add(Items.WARPED_PLANKS);
					entries.add(Items.WARPED_STAIRS);
					entries.add(Items.WARPED_SLAB);
					entries.add(Items.WARPED_FENCE);
					entries.add(Items.WARPED_FENCE_GATE);
					entries.add(Items.WARPED_DOOR);
					entries.add(Items.WARPED_TRAPDOOR);
					entries.add(Items.WARPED_PRESSURE_PLATE);
					entries.add(Items.WARPED_BUTTON);
					entries.add(Items.STONE);
					entries.add(Items.STONE_STAIRS);
					entries.add(Items.STONE_SLAB);
					entries.add(Items.STONE_PRESSURE_PLATE);
					entries.add(Items.STONE_BUTTON);
					entries.add(Items.COBBLESTONE);
					entries.add(Items.COBBLESTONE_STAIRS);
					entries.add(Items.COBBLESTONE_SLAB);
					entries.add(Items.COBBLESTONE_WALL);
					entries.add(Items.MOSSY_COBBLESTONE);
					entries.add(Items.MOSSY_COBBLESTONE_STAIRS);
					entries.add(Items.MOSSY_COBBLESTONE_SLAB);
					entries.add(Items.MOSSY_COBBLESTONE_WALL);
					entries.add(Items.SMOOTH_STONE);
					entries.add(Items.SMOOTH_STONE_SLAB);
					entries.add(Items.STONE_BRICKS);
					entries.add(Items.CRACKED_STONE_BRICKS);
					entries.add(Items.STONE_BRICK_STAIRS);
					entries.add(Items.STONE_BRICK_SLAB);
					entries.add(Items.STONE_BRICK_WALL);
					entries.add(Items.CHISELED_STONE_BRICKS);
					entries.add(Items.MOSSY_STONE_BRICKS);
					entries.add(Items.MOSSY_STONE_BRICK_STAIRS);
					entries.add(Items.MOSSY_STONE_BRICK_SLAB);
					entries.add(Items.MOSSY_STONE_BRICK_WALL);
					entries.add(Items.GRANITE);
					entries.add(Items.GRANITE_STAIRS);
					entries.add(Items.GRANITE_SLAB);
					entries.add(Items.GRANITE_WALL);
					entries.add(Items.POLISHED_GRANITE);
					entries.add(Items.POLISHED_GRANITE_STAIRS);
					entries.add(Items.POLISHED_GRANITE_SLAB);
					entries.add(Items.DIORITE);
					entries.add(Items.DIORITE_STAIRS);
					entries.add(Items.DIORITE_SLAB);
					entries.add(Items.DIORITE_WALL);
					entries.add(Items.POLISHED_DIORITE);
					entries.add(Items.POLISHED_DIORITE_STAIRS);
					entries.add(Items.POLISHED_DIORITE_SLAB);
					entries.add(Items.ANDESITE);
					entries.add(Items.ANDESITE_STAIRS);
					entries.add(Items.ANDESITE_SLAB);
					entries.add(Items.ANDESITE_WALL);
					entries.add(Items.POLISHED_ANDESITE);
					entries.add(Items.POLISHED_ANDESITE_STAIRS);
					entries.add(Items.POLISHED_ANDESITE_SLAB);
					entries.add(Items.DEEPSLATE);
					entries.add(Items.COBBLED_DEEPSLATE);
					entries.add(Items.COBBLED_DEEPSLATE_STAIRS);
					entries.add(Items.COBBLED_DEEPSLATE_SLAB);
					entries.add(Items.COBBLED_DEEPSLATE_WALL);
					entries.add(Items.CHISELED_DEEPSLATE);
					entries.add(Items.POLISHED_DEEPSLATE);
					entries.add(Items.POLISHED_DEEPSLATE_STAIRS);
					entries.add(Items.POLISHED_DEEPSLATE_SLAB);
					entries.add(Items.POLISHED_DEEPSLATE_WALL);
					entries.add(Items.DEEPSLATE_BRICKS);
					entries.add(Items.CRACKED_DEEPSLATE_BRICKS);
					entries.add(Items.DEEPSLATE_BRICK_STAIRS);
					entries.add(Items.DEEPSLATE_BRICK_SLAB);
					entries.add(Items.DEEPSLATE_BRICK_WALL);
					entries.add(Items.DEEPSLATE_TILES);
					entries.add(Items.CRACKED_DEEPSLATE_TILES);
					entries.add(Items.DEEPSLATE_TILE_STAIRS);
					entries.add(Items.DEEPSLATE_TILE_SLAB);
					entries.add(Items.DEEPSLATE_TILE_WALL);
					entries.add(Items.REINFORCED_DEEPSLATE);
					entries.add(Items.TUFF);
					entries.add(Items.TUFF_STAIRS);
					entries.add(Items.TUFF_SLAB);
					entries.add(Items.TUFF_WALL);
					entries.add(Items.CHISELED_TUFF);
					entries.add(Items.POLISHED_TUFF);
					entries.add(Items.POLISHED_TUFF_STAIRS);
					entries.add(Items.POLISHED_TUFF_SLAB);
					entries.add(Items.POLISHED_TUFF_WALL);
					entries.add(Items.TUFF_BRICKS);
					entries.add(Items.TUFF_BRICK_STAIRS);
					entries.add(Items.TUFF_BRICK_SLAB);
					entries.add(Items.TUFF_BRICK_WALL);
					entries.add(Items.CHISELED_TUFF_BRICKS);
					entries.add(Items.BRICKS);
					entries.add(Items.BRICK_STAIRS);
					entries.add(Items.BRICK_SLAB);
					entries.add(Items.BRICK_WALL);
					entries.add(Items.PACKED_MUD);
					entries.add(Items.MUD_BRICKS);
					entries.add(Items.MUD_BRICK_STAIRS);
					entries.add(Items.MUD_BRICK_SLAB);
					entries.add(Items.MUD_BRICK_WALL);
					entries.add(Items.SANDSTONE);
					entries.add(Items.SANDSTONE_STAIRS);
					entries.add(Items.SANDSTONE_SLAB);
					entries.add(Items.SANDSTONE_WALL);
					entries.add(Items.CHISELED_SANDSTONE);
					entries.add(Items.SMOOTH_SANDSTONE);
					entries.add(Items.SMOOTH_SANDSTONE_STAIRS);
					entries.add(Items.SMOOTH_SANDSTONE_SLAB);
					entries.add(Items.CUT_SANDSTONE);
					entries.add(Items.CUT_SANDSTONE_SLAB);
					entries.add(Items.RED_SANDSTONE);
					entries.add(Items.RED_SANDSTONE_STAIRS);
					entries.add(Items.RED_SANDSTONE_SLAB);
					entries.add(Items.RED_SANDSTONE_WALL);
					entries.add(Items.CHISELED_RED_SANDSTONE);
					entries.add(Items.SMOOTH_RED_SANDSTONE);
					entries.add(Items.SMOOTH_RED_SANDSTONE_STAIRS);
					entries.add(Items.SMOOTH_RED_SANDSTONE_SLAB);
					entries.add(Items.CUT_RED_SANDSTONE);
					entries.add(Items.CUT_RED_SANDSTONE_SLAB);
					entries.add(Items.SEA_LANTERN);
					entries.add(Items.PRISMARINE);
					entries.add(Items.PRISMARINE_STAIRS);
					entries.add(Items.PRISMARINE_SLAB);
					entries.add(Items.PRISMARINE_WALL);
					entries.add(Items.PRISMARINE_BRICKS);
					entries.add(Items.PRISMARINE_BRICK_STAIRS);
					entries.add(Items.PRISMARINE_BRICK_SLAB);
					entries.add(Items.DARK_PRISMARINE);
					entries.add(Items.DARK_PRISMARINE_STAIRS);
					entries.add(Items.DARK_PRISMARINE_SLAB);
					entries.add(Items.NETHERRACK);
					entries.add(Items.NETHER_BRICKS);
					entries.add(Items.CRACKED_NETHER_BRICKS);
					entries.add(Items.NETHER_BRICK_STAIRS);
					entries.add(Items.NETHER_BRICK_SLAB);
					entries.add(Items.NETHER_BRICK_WALL);
					entries.add(Items.NETHER_BRICK_FENCE);
					entries.add(Items.CHISELED_NETHER_BRICKS);
					entries.add(Items.RED_NETHER_BRICKS);
					entries.add(Items.RED_NETHER_BRICK_STAIRS);
					entries.add(Items.RED_NETHER_BRICK_SLAB);
					entries.add(Items.RED_NETHER_BRICK_WALL);
					entries.add(Items.BASALT);
					entries.add(Items.SMOOTH_BASALT);
					entries.add(Items.POLISHED_BASALT);
					entries.add(Items.BLACKSTONE);
					entries.add(Items.GILDED_BLACKSTONE);
					entries.add(Items.BLACKSTONE_STAIRS);
					entries.add(Items.BLACKSTONE_SLAB);
					entries.add(Items.BLACKSTONE_WALL);
					entries.add(Items.CHISELED_POLISHED_BLACKSTONE);
					entries.add(Items.POLISHED_BLACKSTONE);
					entries.add(Items.POLISHED_BLACKSTONE_STAIRS);
					entries.add(Items.POLISHED_BLACKSTONE_SLAB);
					entries.add(Items.POLISHED_BLACKSTONE_WALL);
					entries.add(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE);
					entries.add(Items.POLISHED_BLACKSTONE_BUTTON);
					entries.add(Items.POLISHED_BLACKSTONE_BRICKS);
					entries.add(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS);
					entries.add(Items.POLISHED_BLACKSTONE_BRICK_STAIRS);
					entries.add(Items.POLISHED_BLACKSTONE_BRICK_SLAB);
					entries.add(Items.POLISHED_BLACKSTONE_BRICK_WALL);
					entries.add(Items.END_STONE);
					entries.add(Items.END_STONE_BRICKS);
					entries.add(Items.END_STONE_BRICK_STAIRS);
					entries.add(Items.END_STONE_BRICK_SLAB);
					entries.add(Items.END_STONE_BRICK_WALL);
					entries.add(Items.PURPUR_BLOCK);
					entries.add(Items.PURPUR_PILLAR);
					entries.add(Items.PURPUR_STAIRS);
					entries.add(Items.PURPUR_SLAB);
					entries.add(Items.COAL_BLOCK);
					entries.add(Items.IRON_BLOCK);
					entries.add(Items.IRON_BARS);
					entries.add(Items.IRON_DOOR);
					entries.add(Items.IRON_TRAPDOOR);
					entries.add(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
					entries.add(Items.CHAIN);
					entries.add(Items.GOLD_BLOCK);
					entries.add(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
					entries.add(Items.REDSTONE_BLOCK);
					entries.add(Items.EMERALD_BLOCK);
					entries.add(Items.LAPIS_BLOCK);
					entries.add(Items.DIAMOND_BLOCK);
					entries.add(Items.NETHERITE_BLOCK);
					entries.add(Items.QUARTZ_BLOCK);
					entries.add(Items.QUARTZ_STAIRS);
					entries.add(Items.QUARTZ_SLAB);
					entries.add(Items.CHISELED_QUARTZ_BLOCK);
					entries.add(Items.QUARTZ_BRICKS);
					entries.add(Items.QUARTZ_PILLAR);
					entries.add(Items.SMOOTH_QUARTZ);
					entries.add(Items.SMOOTH_QUARTZ_STAIRS);
					entries.add(Items.SMOOTH_QUARTZ_SLAB);
					entries.add(Items.AMETHYST_BLOCK);
					entries.add(Items.COPPER_BLOCK);
					entries.add(Items.CHISELED_COPPER);
					entries.add(Items.COPPER_GRATE);
					entries.add(Items.CUT_COPPER);
					entries.add(Items.CUT_COPPER_STAIRS);
					entries.add(Items.CUT_COPPER_SLAB);
					entries.add(Items.COPPER_DOOR);
					entries.add(Items.COPPER_TRAPDOOR);
					entries.add(Items.COPPER_BULB);
					entries.add(Items.EXPOSED_COPPER);
					entries.add(Items.EXPOSED_CHISELED_COPPER);
					entries.add(Items.EXPOSED_COPPER_GRATE);
					entries.add(Items.EXPOSED_CUT_COPPER);
					entries.add(Items.EXPOSED_CUT_COPPER_STAIRS);
					entries.add(Items.EXPOSED_CUT_COPPER_SLAB);
					entries.add(Items.EXPOSED_COPPER_DOOR);
					entries.add(Items.EXPOSED_COPPER_TRAPDOOR);
					entries.add(Items.EXPOSED_COPPER_BULB);
					entries.add(Items.WEATHERED_COPPER);
					entries.add(Items.WEATHERED_CHISELED_COPPER);
					entries.add(Items.WEATHERED_COPPER_GRATE);
					entries.add(Items.WEATHERED_CUT_COPPER);
					entries.add(Items.WEATHERED_CUT_COPPER_STAIRS);
					entries.add(Items.WEATHERED_CUT_COPPER_SLAB);
					entries.add(Items.WEATHERED_COPPER_DOOR);
					entries.add(Items.WEATHERED_COPPER_TRAPDOOR);
					entries.add(Items.WEATHERED_COPPER_BULB);
					entries.add(Items.OXIDIZED_COPPER);
					entries.add(Items.OXIDIZED_CHISELED_COPPER);
					entries.add(Items.OXIDIZED_COPPER_GRATE);
					entries.add(Items.OXIDIZED_CUT_COPPER);
					entries.add(Items.OXIDIZED_CUT_COPPER_STAIRS);
					entries.add(Items.OXIDIZED_CUT_COPPER_SLAB);
					entries.add(Items.OXIDIZED_COPPER_DOOR);
					entries.add(Items.OXIDIZED_COPPER_TRAPDOOR);
					entries.add(Items.OXIDIZED_COPPER_BULB);
					entries.add(Items.WAXED_COPPER_BLOCK);
					entries.add(Items.WAXED_CHISELED_COPPER);
					entries.add(Items.WAXED_COPPER_GRATE);
					entries.add(Items.WAXED_CUT_COPPER);
					entries.add(Items.WAXED_CUT_COPPER_STAIRS);
					entries.add(Items.WAXED_CUT_COPPER_SLAB);
					entries.add(Items.WAXED_COPPER_DOOR);
					entries.add(Items.WAXED_COPPER_TRAPDOOR);
					entries.add(Items.WAXED_COPPER_BULB);
					entries.add(Items.WAXED_EXPOSED_COPPER);
					entries.add(Items.WAXED_EXPOSED_CHISELED_COPPER);
					entries.add(Items.WAXED_EXPOSED_COPPER_GRATE);
					entries.add(Items.WAXED_EXPOSED_CUT_COPPER);
					entries.add(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS);
					entries.add(Items.WAXED_EXPOSED_CUT_COPPER_SLAB);
					entries.add(Items.WAXED_EXPOSED_COPPER_DOOR);
					entries.add(Items.WAXED_EXPOSED_COPPER_TRAPDOOR);
					entries.add(Items.WAXED_EXPOSED_COPPER_BULB);
					entries.add(Items.WAXED_WEATHERED_COPPER);
					entries.add(Items.WAXED_WEATHERED_CHISELED_COPPER);
					entries.add(Items.WAXED_WEATHERED_COPPER_GRATE);
					entries.add(Items.WAXED_WEATHERED_CUT_COPPER);
					entries.add(Items.WAXED_WEATHERED_CUT_COPPER_STAIRS);
					entries.add(Items.WAXED_WEATHERED_CUT_COPPER_SLAB);
					entries.add(Items.WAXED_WEATHERED_COPPER_DOOR);
					entries.add(Items.WAXED_WEATHERED_COPPER_TRAPDOOR);
					entries.add(Items.WAXED_WEATHERED_COPPER_BULB);
					entries.add(Items.WAXED_OXIDIZED_COPPER);
					entries.add(Items.WAXED_OXIDIZED_CHISELED_COPPER);
					entries.add(Items.WAXED_OXIDIZED_COPPER_GRATE);
					entries.add(Items.WAXED_OXIDIZED_CUT_COPPER);
					entries.add(Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
					entries.add(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB);
					entries.add(Items.WAXED_OXIDIZED_COPPER_DOOR);
					entries.add(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR);
					entries.add(Items.WAXED_OXIDIZED_COPPER_BULB);
				})
				.build()
		);
		Registry.register(
			registry,
			COLORED_BLOCKS,
			ItemGroup.create(ItemGroup.Row.TOP, 1)
				.displayName(Text.translatable("itemGroup.coloredBlocks"))
				.icon(() -> new ItemStack(Blocks.CYAN_WOOL))
				.entries((displayContext, entries) -> {
					entries.add(Items.WHITE_WOOL);
					entries.add(Items.LIGHT_GRAY_WOOL);
					entries.add(Items.GRAY_WOOL);
					entries.add(Items.BLACK_WOOL);
					entries.add(Items.BROWN_WOOL);
					entries.add(Items.RED_WOOL);
					entries.add(Items.ORANGE_WOOL);
					entries.add(Items.YELLOW_WOOL);
					entries.add(Items.LIME_WOOL);
					entries.add(Items.GREEN_WOOL);
					entries.add(Items.CYAN_WOOL);
					entries.add(Items.LIGHT_BLUE_WOOL);
					entries.add(Items.BLUE_WOOL);
					entries.add(Items.PURPLE_WOOL);
					entries.add(Items.MAGENTA_WOOL);
					entries.add(Items.PINK_WOOL);
					entries.add(Items.WHITE_CARPET);
					entries.add(Items.LIGHT_GRAY_CARPET);
					entries.add(Items.GRAY_CARPET);
					entries.add(Items.BLACK_CARPET);
					entries.add(Items.BROWN_CARPET);
					entries.add(Items.RED_CARPET);
					entries.add(Items.ORANGE_CARPET);
					entries.add(Items.YELLOW_CARPET);
					entries.add(Items.LIME_CARPET);
					entries.add(Items.GREEN_CARPET);
					entries.add(Items.CYAN_CARPET);
					entries.add(Items.LIGHT_BLUE_CARPET);
					entries.add(Items.BLUE_CARPET);
					entries.add(Items.PURPLE_CARPET);
					entries.add(Items.MAGENTA_CARPET);
					entries.add(Items.PINK_CARPET);
					entries.add(Items.TERRACOTTA);
					entries.add(Items.WHITE_TERRACOTTA);
					entries.add(Items.LIGHT_GRAY_TERRACOTTA);
					entries.add(Items.GRAY_TERRACOTTA);
					entries.add(Items.BLACK_TERRACOTTA);
					entries.add(Items.BROWN_TERRACOTTA);
					entries.add(Items.RED_TERRACOTTA);
					entries.add(Items.ORANGE_TERRACOTTA);
					entries.add(Items.YELLOW_TERRACOTTA);
					entries.add(Items.LIME_TERRACOTTA);
					entries.add(Items.GREEN_TERRACOTTA);
					entries.add(Items.CYAN_TERRACOTTA);
					entries.add(Items.LIGHT_BLUE_TERRACOTTA);
					entries.add(Items.BLUE_TERRACOTTA);
					entries.add(Items.PURPLE_TERRACOTTA);
					entries.add(Items.MAGENTA_TERRACOTTA);
					entries.add(Items.PINK_TERRACOTTA);
					entries.add(Items.WHITE_CONCRETE);
					entries.add(Items.LIGHT_GRAY_CONCRETE);
					entries.add(Items.GRAY_CONCRETE);
					entries.add(Items.BLACK_CONCRETE);
					entries.add(Items.BROWN_CONCRETE);
					entries.add(Items.RED_CONCRETE);
					entries.add(Items.ORANGE_CONCRETE);
					entries.add(Items.YELLOW_CONCRETE);
					entries.add(Items.LIME_CONCRETE);
					entries.add(Items.GREEN_CONCRETE);
					entries.add(Items.CYAN_CONCRETE);
					entries.add(Items.LIGHT_BLUE_CONCRETE);
					entries.add(Items.BLUE_CONCRETE);
					entries.add(Items.PURPLE_CONCRETE);
					entries.add(Items.MAGENTA_CONCRETE);
					entries.add(Items.PINK_CONCRETE);
					entries.add(Items.WHITE_CONCRETE_POWDER);
					entries.add(Items.LIGHT_GRAY_CONCRETE_POWDER);
					entries.add(Items.GRAY_CONCRETE_POWDER);
					entries.add(Items.BLACK_CONCRETE_POWDER);
					entries.add(Items.BROWN_CONCRETE_POWDER);
					entries.add(Items.RED_CONCRETE_POWDER);
					entries.add(Items.ORANGE_CONCRETE_POWDER);
					entries.add(Items.YELLOW_CONCRETE_POWDER);
					entries.add(Items.LIME_CONCRETE_POWDER);
					entries.add(Items.GREEN_CONCRETE_POWDER);
					entries.add(Items.CYAN_CONCRETE_POWDER);
					entries.add(Items.LIGHT_BLUE_CONCRETE_POWDER);
					entries.add(Items.BLUE_CONCRETE_POWDER);
					entries.add(Items.PURPLE_CONCRETE_POWDER);
					entries.add(Items.MAGENTA_CONCRETE_POWDER);
					entries.add(Items.PINK_CONCRETE_POWDER);
					entries.add(Items.WHITE_GLAZED_TERRACOTTA);
					entries.add(Items.LIGHT_GRAY_GLAZED_TERRACOTTA);
					entries.add(Items.GRAY_GLAZED_TERRACOTTA);
					entries.add(Items.BLACK_GLAZED_TERRACOTTA);
					entries.add(Items.BROWN_GLAZED_TERRACOTTA);
					entries.add(Items.RED_GLAZED_TERRACOTTA);
					entries.add(Items.ORANGE_GLAZED_TERRACOTTA);
					entries.add(Items.YELLOW_GLAZED_TERRACOTTA);
					entries.add(Items.LIME_GLAZED_TERRACOTTA);
					entries.add(Items.GREEN_GLAZED_TERRACOTTA);
					entries.add(Items.CYAN_GLAZED_TERRACOTTA);
					entries.add(Items.LIGHT_BLUE_GLAZED_TERRACOTTA);
					entries.add(Items.BLUE_GLAZED_TERRACOTTA);
					entries.add(Items.PURPLE_GLAZED_TERRACOTTA);
					entries.add(Items.MAGENTA_GLAZED_TERRACOTTA);
					entries.add(Items.PINK_GLAZED_TERRACOTTA);
					entries.add(Items.GLASS);
					entries.add(Items.TINTED_GLASS);
					entries.add(Items.WHITE_STAINED_GLASS);
					entries.add(Items.LIGHT_GRAY_STAINED_GLASS);
					entries.add(Items.GRAY_STAINED_GLASS);
					entries.add(Items.BLACK_STAINED_GLASS);
					entries.add(Items.BROWN_STAINED_GLASS);
					entries.add(Items.RED_STAINED_GLASS);
					entries.add(Items.ORANGE_STAINED_GLASS);
					entries.add(Items.YELLOW_STAINED_GLASS);
					entries.add(Items.LIME_STAINED_GLASS);
					entries.add(Items.GREEN_STAINED_GLASS);
					entries.add(Items.CYAN_STAINED_GLASS);
					entries.add(Items.LIGHT_BLUE_STAINED_GLASS);
					entries.add(Items.BLUE_STAINED_GLASS);
					entries.add(Items.PURPLE_STAINED_GLASS);
					entries.add(Items.MAGENTA_STAINED_GLASS);
					entries.add(Items.PINK_STAINED_GLASS);
					entries.add(Items.GLASS_PANE);
					entries.add(Items.WHITE_STAINED_GLASS_PANE);
					entries.add(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
					entries.add(Items.GRAY_STAINED_GLASS_PANE);
					entries.add(Items.BLACK_STAINED_GLASS_PANE);
					entries.add(Items.BROWN_STAINED_GLASS_PANE);
					entries.add(Items.RED_STAINED_GLASS_PANE);
					entries.add(Items.ORANGE_STAINED_GLASS_PANE);
					entries.add(Items.YELLOW_STAINED_GLASS_PANE);
					entries.add(Items.LIME_STAINED_GLASS_PANE);
					entries.add(Items.GREEN_STAINED_GLASS_PANE);
					entries.add(Items.CYAN_STAINED_GLASS_PANE);
					entries.add(Items.LIGHT_BLUE_STAINED_GLASS_PANE);
					entries.add(Items.BLUE_STAINED_GLASS_PANE);
					entries.add(Items.PURPLE_STAINED_GLASS_PANE);
					entries.add(Items.MAGENTA_STAINED_GLASS_PANE);
					entries.add(Items.PINK_STAINED_GLASS_PANE);
					entries.add(Items.SHULKER_BOX);
					entries.add(Items.WHITE_SHULKER_BOX);
					entries.add(Items.LIGHT_GRAY_SHULKER_BOX);
					entries.add(Items.GRAY_SHULKER_BOX);
					entries.add(Items.BLACK_SHULKER_BOX);
					entries.add(Items.BROWN_SHULKER_BOX);
					entries.add(Items.RED_SHULKER_BOX);
					entries.add(Items.ORANGE_SHULKER_BOX);
					entries.add(Items.YELLOW_SHULKER_BOX);
					entries.add(Items.LIME_SHULKER_BOX);
					entries.add(Items.GREEN_SHULKER_BOX);
					entries.add(Items.CYAN_SHULKER_BOX);
					entries.add(Items.LIGHT_BLUE_SHULKER_BOX);
					entries.add(Items.BLUE_SHULKER_BOX);
					entries.add(Items.PURPLE_SHULKER_BOX);
					entries.add(Items.MAGENTA_SHULKER_BOX);
					entries.add(Items.PINK_SHULKER_BOX);
					entries.add(Items.WHITE_BED);
					entries.add(Items.LIGHT_GRAY_BED);
					entries.add(Items.GRAY_BED);
					entries.add(Items.BLACK_BED);
					entries.add(Items.BROWN_BED);
					entries.add(Items.RED_BED);
					entries.add(Items.ORANGE_BED);
					entries.add(Items.YELLOW_BED);
					entries.add(Items.LIME_BED);
					entries.add(Items.GREEN_BED);
					entries.add(Items.CYAN_BED);
					entries.add(Items.LIGHT_BLUE_BED);
					entries.add(Items.BLUE_BED);
					entries.add(Items.PURPLE_BED);
					entries.add(Items.MAGENTA_BED);
					entries.add(Items.PINK_BED);
					entries.add(Items.CANDLE);
					entries.add(Items.WHITE_CANDLE);
					entries.add(Items.LIGHT_GRAY_CANDLE);
					entries.add(Items.GRAY_CANDLE);
					entries.add(Items.BLACK_CANDLE);
					entries.add(Items.BROWN_CANDLE);
					entries.add(Items.RED_CANDLE);
					entries.add(Items.ORANGE_CANDLE);
					entries.add(Items.YELLOW_CANDLE);
					entries.add(Items.LIME_CANDLE);
					entries.add(Items.GREEN_CANDLE);
					entries.add(Items.CYAN_CANDLE);
					entries.add(Items.LIGHT_BLUE_CANDLE);
					entries.add(Items.BLUE_CANDLE);
					entries.add(Items.PURPLE_CANDLE);
					entries.add(Items.MAGENTA_CANDLE);
					entries.add(Items.PINK_CANDLE);
					entries.add(Items.WHITE_BANNER);
					entries.add(Items.LIGHT_GRAY_BANNER);
					entries.add(Items.GRAY_BANNER);
					entries.add(Items.BLACK_BANNER);
					entries.add(Items.BROWN_BANNER);
					entries.add(Items.RED_BANNER);
					entries.add(Items.ORANGE_BANNER);
					entries.add(Items.YELLOW_BANNER);
					entries.add(Items.LIME_BANNER);
					entries.add(Items.GREEN_BANNER);
					entries.add(Items.CYAN_BANNER);
					entries.add(Items.LIGHT_BLUE_BANNER);
					entries.add(Items.BLUE_BANNER);
					entries.add(Items.PURPLE_BANNER);
					entries.add(Items.MAGENTA_BANNER);
					entries.add(Items.PINK_BANNER);
				})
				.build()
		);
		Registry.register(
			registry,
			NATURAL,
			ItemGroup.create(ItemGroup.Row.TOP, 2)
				.displayName(Text.translatable("itemGroup.natural"))
				.icon(() -> new ItemStack(Blocks.GRASS_BLOCK))
				.entries((displayContext, entries) -> {
					entries.add(Items.GRASS_BLOCK);
					entries.add(Items.PODZOL);
					entries.add(Items.MYCELIUM);
					entries.add(Items.DIRT_PATH);
					entries.add(Items.DIRT);
					entries.add(Items.COARSE_DIRT);
					entries.add(Items.ROOTED_DIRT);
					entries.add(Items.FARMLAND);
					entries.add(Items.MUD);
					entries.add(Items.CLAY);
					entries.add(Items.GRAVEL);
					entries.add(Items.SAND);
					entries.add(Items.SANDSTONE);
					entries.add(Items.RED_SAND);
					entries.add(Items.RED_SANDSTONE);
					entries.add(Items.ICE);
					entries.add(Items.PACKED_ICE);
					entries.add(Items.BLUE_ICE);
					entries.add(Items.SNOW_BLOCK);
					entries.add(Items.SNOW);
					entries.add(Items.MOSS_BLOCK);
					entries.add(Items.MOSS_CARPET);
					entries.add(Items.STONE);
					entries.add(Items.DEEPSLATE);
					entries.add(Items.GRANITE);
					entries.add(Items.DIORITE);
					entries.add(Items.ANDESITE);
					entries.add(Items.CALCITE);
					entries.add(Items.TUFF);
					entries.add(Items.DRIPSTONE_BLOCK);
					entries.add(Items.POINTED_DRIPSTONE);
					entries.add(Items.PRISMARINE);
					entries.add(Items.MAGMA_BLOCK);
					entries.add(Items.OBSIDIAN);
					entries.add(Items.CRYING_OBSIDIAN);
					entries.add(Items.NETHERRACK);
					entries.add(Items.CRIMSON_NYLIUM);
					entries.add(Items.WARPED_NYLIUM);
					entries.add(Items.SOUL_SAND);
					entries.add(Items.SOUL_SOIL);
					entries.add(Items.BONE_BLOCK);
					entries.add(Items.BLACKSTONE);
					entries.add(Items.BASALT);
					entries.add(Items.SMOOTH_BASALT);
					entries.add(Items.END_STONE);
					entries.add(Items.COAL_ORE);
					entries.add(Items.DEEPSLATE_COAL_ORE);
					entries.add(Items.IRON_ORE);
					entries.add(Items.DEEPSLATE_IRON_ORE);
					entries.add(Items.COPPER_ORE);
					entries.add(Items.DEEPSLATE_COPPER_ORE);
					entries.add(Items.GOLD_ORE);
					entries.add(Items.DEEPSLATE_GOLD_ORE);
					entries.add(Items.REDSTONE_ORE);
					entries.add(Items.DEEPSLATE_REDSTONE_ORE);
					entries.add(Items.EMERALD_ORE);
					entries.add(Items.DEEPSLATE_EMERALD_ORE);
					entries.add(Items.LAPIS_ORE);
					entries.add(Items.DEEPSLATE_LAPIS_ORE);
					entries.add(Items.DIAMOND_ORE);
					entries.add(Items.DEEPSLATE_DIAMOND_ORE);
					entries.add(Items.NETHER_GOLD_ORE);
					entries.add(Items.NETHER_QUARTZ_ORE);
					entries.add(Items.ANCIENT_DEBRIS);
					entries.add(Items.RAW_IRON_BLOCK);
					entries.add(Items.RAW_COPPER_BLOCK);
					entries.add(Items.RAW_GOLD_BLOCK);
					entries.add(Items.GLOWSTONE);
					entries.add(Items.AMETHYST_BLOCK);
					entries.add(Items.BUDDING_AMETHYST);
					entries.add(Items.SMALL_AMETHYST_BUD);
					entries.add(Items.MEDIUM_AMETHYST_BUD);
					entries.add(Items.LARGE_AMETHYST_BUD);
					entries.add(Items.AMETHYST_CLUSTER);
					entries.add(Items.OAK_LOG);
					entries.add(Items.SPRUCE_LOG);
					entries.add(Items.BIRCH_LOG);
					entries.add(Items.JUNGLE_LOG);
					entries.add(Items.ACACIA_LOG);
					entries.add(Items.DARK_OAK_LOG);
					entries.add(Items.MANGROVE_LOG);
					entries.add(Items.MANGROVE_ROOTS);
					entries.add(Items.MUDDY_MANGROVE_ROOTS);
					entries.add(Items.CHERRY_LOG);
					entries.add(Items.MUSHROOM_STEM);
					entries.add(Items.CRIMSON_STEM);
					entries.add(Items.WARPED_STEM);
					entries.add(Items.OAK_LEAVES);
					entries.add(Items.SPRUCE_LEAVES);
					entries.add(Items.BIRCH_LEAVES);
					entries.add(Items.JUNGLE_LEAVES);
					entries.add(Items.ACACIA_LEAVES);
					entries.add(Items.DARK_OAK_LEAVES);
					entries.add(Items.MANGROVE_LEAVES);
					entries.add(Items.CHERRY_LEAVES);
					entries.add(Items.AZALEA_LEAVES);
					entries.add(Items.FLOWERING_AZALEA_LEAVES);
					entries.add(Items.BROWN_MUSHROOM_BLOCK);
					entries.add(Items.RED_MUSHROOM_BLOCK);
					entries.add(Items.NETHER_WART_BLOCK);
					entries.add(Items.WARPED_WART_BLOCK);
					entries.add(Items.SHROOMLIGHT);
					entries.add(Items.OAK_SAPLING);
					entries.add(Items.SPRUCE_SAPLING);
					entries.add(Items.BIRCH_SAPLING);
					entries.add(Items.JUNGLE_SAPLING);
					entries.add(Items.ACACIA_SAPLING);
					entries.add(Items.DARK_OAK_SAPLING);
					entries.add(Items.MANGROVE_PROPAGULE);
					entries.add(Items.CHERRY_SAPLING);
					entries.add(Items.AZALEA);
					entries.add(Items.FLOWERING_AZALEA);
					entries.add(Items.BROWN_MUSHROOM);
					entries.add(Items.RED_MUSHROOM);
					entries.add(Items.CRIMSON_FUNGUS);
					entries.add(Items.WARPED_FUNGUS);
					entries.add(Items.SHORT_GRASS);
					entries.add(Items.FERN);
					entries.add(Items.DEAD_BUSH);
					entries.add(Items.DANDELION);
					entries.add(Items.POPPY);
					entries.add(Items.BLUE_ORCHID);
					entries.add(Items.ALLIUM);
					entries.add(Items.AZURE_BLUET);
					entries.add(Items.RED_TULIP);
					entries.add(Items.ORANGE_TULIP);
					entries.add(Items.WHITE_TULIP);
					entries.add(Items.PINK_TULIP);
					entries.add(Items.OXEYE_DAISY);
					entries.add(Items.CORNFLOWER);
					entries.add(Items.LILY_OF_THE_VALLEY);
					entries.add(Items.TORCHFLOWER);
					entries.add(Items.WITHER_ROSE);
					entries.add(Items.PINK_PETALS);
					entries.add(Items.SPORE_BLOSSOM);
					entries.add(Items.BAMBOO);
					entries.add(Items.SUGAR_CANE);
					entries.add(Items.CACTUS);
					entries.add(Items.CRIMSON_ROOTS);
					entries.add(Items.WARPED_ROOTS);
					entries.add(Items.NETHER_SPROUTS);
					entries.add(Items.WEEPING_VINES);
					entries.add(Items.TWISTING_VINES);
					entries.add(Items.VINE);
					entries.add(Items.TALL_GRASS);
					entries.add(Items.LARGE_FERN);
					entries.add(Items.SUNFLOWER);
					entries.add(Items.LILAC);
					entries.add(Items.ROSE_BUSH);
					entries.add(Items.PEONY);
					entries.add(Items.PITCHER_PLANT);
					entries.add(Items.BIG_DRIPLEAF);
					entries.add(Items.SMALL_DRIPLEAF);
					entries.add(Items.CHORUS_PLANT);
					entries.add(Items.CHORUS_FLOWER);
					entries.add(Items.GLOW_LICHEN);
					entries.add(Items.HANGING_ROOTS);
					entries.add(Items.FROGSPAWN);
					entries.add(Items.TURTLE_EGG);
					entries.add(Items.SNIFFER_EGG);
					entries.add(Items.WHEAT_SEEDS);
					entries.add(Items.COCOA_BEANS);
					entries.add(Items.PUMPKIN_SEEDS);
					entries.add(Items.MELON_SEEDS);
					entries.add(Items.BEETROOT_SEEDS);
					entries.add(Items.TORCHFLOWER_SEEDS);
					entries.add(Items.PITCHER_POD);
					entries.add(Items.GLOW_BERRIES);
					entries.add(Items.SWEET_BERRIES);
					entries.add(Items.NETHER_WART);
					entries.add(Items.LILY_PAD);
					entries.add(Items.SEAGRASS);
					entries.add(Items.SEA_PICKLE);
					entries.add(Items.KELP);
					entries.add(Items.DRIED_KELP_BLOCK);
					entries.add(Items.TUBE_CORAL_BLOCK);
					entries.add(Items.BRAIN_CORAL_BLOCK);
					entries.add(Items.BUBBLE_CORAL_BLOCK);
					entries.add(Items.FIRE_CORAL_BLOCK);
					entries.add(Items.HORN_CORAL_BLOCK);
					entries.add(Items.DEAD_TUBE_CORAL_BLOCK);
					entries.add(Items.DEAD_BRAIN_CORAL_BLOCK);
					entries.add(Items.DEAD_BUBBLE_CORAL_BLOCK);
					entries.add(Items.DEAD_FIRE_CORAL_BLOCK);
					entries.add(Items.DEAD_HORN_CORAL_BLOCK);
					entries.add(Items.TUBE_CORAL);
					entries.add(Items.BRAIN_CORAL);
					entries.add(Items.BUBBLE_CORAL);
					entries.add(Items.FIRE_CORAL);
					entries.add(Items.HORN_CORAL);
					entries.add(Items.DEAD_TUBE_CORAL);
					entries.add(Items.DEAD_BRAIN_CORAL);
					entries.add(Items.DEAD_BUBBLE_CORAL);
					entries.add(Items.DEAD_FIRE_CORAL);
					entries.add(Items.DEAD_HORN_CORAL);
					entries.add(Items.TUBE_CORAL_FAN);
					entries.add(Items.BRAIN_CORAL_FAN);
					entries.add(Items.BUBBLE_CORAL_FAN);
					entries.add(Items.FIRE_CORAL_FAN);
					entries.add(Items.HORN_CORAL_FAN);
					entries.add(Items.DEAD_TUBE_CORAL_FAN);
					entries.add(Items.DEAD_BRAIN_CORAL_FAN);
					entries.add(Items.DEAD_BUBBLE_CORAL_FAN);
					entries.add(Items.DEAD_FIRE_CORAL_FAN);
					entries.add(Items.DEAD_HORN_CORAL_FAN);
					entries.add(Items.SPONGE);
					entries.add(Items.WET_SPONGE);
					entries.add(Items.MELON);
					entries.add(Items.PUMPKIN);
					entries.add(Items.CARVED_PUMPKIN);
					entries.add(Items.JACK_O_LANTERN);
					entries.add(Items.HAY_BLOCK);
					entries.add(Items.BEE_NEST);
					entries.add(Items.HONEYCOMB_BLOCK);
					entries.add(Items.SLIME_BLOCK);
					entries.add(Items.HONEY_BLOCK);
					entries.add(Items.OCHRE_FROGLIGHT);
					entries.add(Items.VERDANT_FROGLIGHT);
					entries.add(Items.PEARLESCENT_FROGLIGHT);
					entries.add(Items.SCULK);
					entries.add(Items.SCULK_VEIN);
					entries.add(Items.SCULK_CATALYST);
					entries.add(Items.SCULK_SHRIEKER);
					entries.add(Items.SCULK_SENSOR);
					entries.add(Items.COBWEB);
					entries.add(Items.BEDROCK);
				})
				.build()
		);
		Registry.register(
			registry,
			FUNCTIONAL,
			ItemGroup.create(ItemGroup.Row.TOP, 3)
				.displayName(Text.translatable("itemGroup.functional"))
				.icon(() -> new ItemStack(Items.OAK_SIGN))
				.entries(
					(displayContext, entries) -> {
						entries.add(Items.TORCH);
						entries.add(Items.SOUL_TORCH);
						entries.add(Items.REDSTONE_TORCH);
						entries.add(Items.LANTERN);
						entries.add(Items.SOUL_LANTERN);
						entries.add(Items.CHAIN);
						entries.add(Items.END_ROD);
						entries.add(Items.SEA_LANTERN);
						entries.add(Items.REDSTONE_LAMP);
						entries.add(Items.COPPER_BULB);
						entries.add(Items.EXPOSED_COPPER_BULB);
						entries.add(Items.WEATHERED_COPPER_BULB);
						entries.add(Items.OXIDIZED_COPPER_BULB);
						entries.add(Items.WAXED_COPPER_BULB);
						entries.add(Items.WAXED_EXPOSED_COPPER_BULB);
						entries.add(Items.WAXED_WEATHERED_COPPER_BULB);
						entries.add(Items.WAXED_OXIDIZED_COPPER_BULB);
						entries.add(Items.GLOWSTONE);
						entries.add(Items.SHROOMLIGHT);
						entries.add(Items.OCHRE_FROGLIGHT);
						entries.add(Items.VERDANT_FROGLIGHT);
						entries.add(Items.PEARLESCENT_FROGLIGHT);
						entries.add(Items.CRYING_OBSIDIAN);
						entries.add(Items.GLOW_LICHEN);
						entries.add(Items.MAGMA_BLOCK);
						entries.add(Items.CRAFTING_TABLE);
						entries.add(Items.STONECUTTER);
						entries.add(Items.CARTOGRAPHY_TABLE);
						entries.add(Items.FLETCHING_TABLE);
						entries.add(Items.SMITHING_TABLE);
						entries.add(Items.GRINDSTONE);
						entries.add(Items.LOOM);
						entries.add(Items.FURNACE);
						entries.add(Items.SMOKER);
						entries.add(Items.BLAST_FURNACE);
						entries.add(Items.CAMPFIRE);
						entries.add(Items.SOUL_CAMPFIRE);
						entries.add(Items.ANVIL);
						entries.add(Items.CHIPPED_ANVIL);
						entries.add(Items.DAMAGED_ANVIL);
						entries.add(Items.COMPOSTER);
						entries.add(Items.NOTE_BLOCK);
						entries.add(Items.JUKEBOX);
						entries.add(Items.ENCHANTING_TABLE);
						entries.add(Items.END_CRYSTAL);
						entries.add(Items.BREWING_STAND);
						entries.add(Items.CAULDRON);
						entries.add(Items.BELL);
						entries.add(Items.BEACON);
						entries.add(Items.CONDUIT);
						entries.add(Items.LODESTONE);
						entries.add(Items.LADDER);
						entries.add(Items.SCAFFOLDING);
						entries.add(Items.BEE_NEST);
						entries.add(Items.BEEHIVE);
						entries.add(Items.SUSPICIOUS_SAND);
						entries.add(Items.SUSPICIOUS_GRAVEL);
						entries.add(Items.LIGHTNING_ROD);
						entries.add(Items.FLOWER_POT);
						entries.add(Items.DECORATED_POT);
						entries.add(Items.ARMOR_STAND);
						entries.add(Items.ITEM_FRAME);
						entries.add(Items.GLOW_ITEM_FRAME);
						entries.add(Items.PAINTING);
						displayContext.lookup()
							.getOptionalWrapper(RegistryKeys.PAINTING_VARIANT)
							.ifPresent(
								registryWrapper -> addPaintings(
										entries,
										displayContext.lookup(),
										registryWrapper,
										registryEntry -> registryEntry.isIn(PaintingVariantTags.PLACEABLE),
										ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
									)
							);
						entries.add(Items.BOOKSHELF);
						entries.add(Items.CHISELED_BOOKSHELF);
						entries.add(Items.LECTERN);
						entries.add(Items.TINTED_GLASS);
						entries.add(Items.OAK_SIGN);
						entries.add(Items.OAK_HANGING_SIGN);
						entries.add(Items.SPRUCE_SIGN);
						entries.add(Items.SPRUCE_HANGING_SIGN);
						entries.add(Items.BIRCH_SIGN);
						entries.add(Items.BIRCH_HANGING_SIGN);
						entries.add(Items.JUNGLE_SIGN);
						entries.add(Items.JUNGLE_HANGING_SIGN);
						entries.add(Items.ACACIA_SIGN);
						entries.add(Items.ACACIA_HANGING_SIGN);
						entries.add(Items.DARK_OAK_SIGN);
						entries.add(Items.DARK_OAK_HANGING_SIGN);
						entries.add(Items.MANGROVE_SIGN);
						entries.add(Items.MANGROVE_HANGING_SIGN);
						entries.add(Items.CHERRY_SIGN);
						entries.add(Items.CHERRY_HANGING_SIGN);
						entries.add(Items.BAMBOO_SIGN);
						entries.add(Items.BAMBOO_HANGING_SIGN);
						entries.add(Items.CRIMSON_SIGN);
						entries.add(Items.CRIMSON_HANGING_SIGN);
						entries.add(Items.WARPED_SIGN);
						entries.add(Items.WARPED_HANGING_SIGN);
						entries.add(Items.CHEST);
						entries.add(Items.BARREL);
						entries.add(Items.ENDER_CHEST);
						entries.add(Items.SHULKER_BOX);
						entries.add(Items.WHITE_SHULKER_BOX);
						entries.add(Items.LIGHT_GRAY_SHULKER_BOX);
						entries.add(Items.GRAY_SHULKER_BOX);
						entries.add(Items.BLACK_SHULKER_BOX);
						entries.add(Items.BROWN_SHULKER_BOX);
						entries.add(Items.RED_SHULKER_BOX);
						entries.add(Items.ORANGE_SHULKER_BOX);
						entries.add(Items.YELLOW_SHULKER_BOX);
						entries.add(Items.LIME_SHULKER_BOX);
						entries.add(Items.GREEN_SHULKER_BOX);
						entries.add(Items.CYAN_SHULKER_BOX);
						entries.add(Items.LIGHT_BLUE_SHULKER_BOX);
						entries.add(Items.BLUE_SHULKER_BOX);
						entries.add(Items.PURPLE_SHULKER_BOX);
						entries.add(Items.MAGENTA_SHULKER_BOX);
						entries.add(Items.PINK_SHULKER_BOX);
						entries.add(Items.RESPAWN_ANCHOR);
						entries.add(Items.WHITE_BED);
						entries.add(Items.LIGHT_GRAY_BED);
						entries.add(Items.GRAY_BED);
						entries.add(Items.BLACK_BED);
						entries.add(Items.BROWN_BED);
						entries.add(Items.RED_BED);
						entries.add(Items.ORANGE_BED);
						entries.add(Items.YELLOW_BED);
						entries.add(Items.LIME_BED);
						entries.add(Items.GREEN_BED);
						entries.add(Items.CYAN_BED);
						entries.add(Items.LIGHT_BLUE_BED);
						entries.add(Items.BLUE_BED);
						entries.add(Items.PURPLE_BED);
						entries.add(Items.MAGENTA_BED);
						entries.add(Items.PINK_BED);
						entries.add(Items.CANDLE);
						entries.add(Items.WHITE_CANDLE);
						entries.add(Items.LIGHT_GRAY_CANDLE);
						entries.add(Items.GRAY_CANDLE);
						entries.add(Items.BLACK_CANDLE);
						entries.add(Items.BROWN_CANDLE);
						entries.add(Items.RED_CANDLE);
						entries.add(Items.ORANGE_CANDLE);
						entries.add(Items.YELLOW_CANDLE);
						entries.add(Items.LIME_CANDLE);
						entries.add(Items.GREEN_CANDLE);
						entries.add(Items.CYAN_CANDLE);
						entries.add(Items.LIGHT_BLUE_CANDLE);
						entries.add(Items.BLUE_CANDLE);
						entries.add(Items.PURPLE_CANDLE);
						entries.add(Items.MAGENTA_CANDLE);
						entries.add(Items.PINK_CANDLE);
						entries.add(Items.WHITE_BANNER);
						entries.add(Items.LIGHT_GRAY_BANNER);
						entries.add(Items.GRAY_BANNER);
						entries.add(Items.BLACK_BANNER);
						entries.add(Items.BROWN_BANNER);
						entries.add(Items.RED_BANNER);
						entries.add(Items.ORANGE_BANNER);
						entries.add(Items.YELLOW_BANNER);
						entries.add(Items.LIME_BANNER);
						entries.add(Items.GREEN_BANNER);
						entries.add(Items.CYAN_BANNER);
						entries.add(Items.LIGHT_BLUE_BANNER);
						entries.add(Items.BLUE_BANNER);
						entries.add(Items.PURPLE_BANNER);
						entries.add(Items.MAGENTA_BANNER);
						entries.add(Items.PINK_BANNER);
						entries.add(Raid.getOminousBanner(displayContext.lookup().getWrapperOrThrow(RegistryKeys.BANNER_PATTERN)));
						entries.add(Items.SKELETON_SKULL);
						entries.add(Items.WITHER_SKELETON_SKULL);
						entries.add(Items.PLAYER_HEAD);
						entries.add(Items.ZOMBIE_HEAD);
						entries.add(Items.CREEPER_HEAD);
						entries.add(Items.PIGLIN_HEAD);
						entries.add(Items.DRAGON_HEAD);
						entries.add(Items.DRAGON_EGG);
						entries.add(Items.END_PORTAL_FRAME);
						entries.add(Items.ENDER_EYE);
						entries.add(Items.VAULT);
						entries.add(Items.INFESTED_STONE);
						entries.add(Items.INFESTED_COBBLESTONE);
						entries.add(Items.INFESTED_STONE_BRICKS);
						entries.add(Items.INFESTED_MOSSY_STONE_BRICKS);
						entries.add(Items.INFESTED_CRACKED_STONE_BRICKS);
						entries.add(Items.INFESTED_CHISELED_STONE_BRICKS);
						entries.add(Items.INFESTED_DEEPSLATE);
					}
				)
				.build()
		);
		Registry.register(
			registry,
			REDSTONE,
			ItemGroup.create(ItemGroup.Row.TOP, 4)
				.displayName(Text.translatable("itemGroup.redstone"))
				.icon(() -> new ItemStack(Items.REDSTONE))
				.entries((displayContext, entries) -> {
					entries.add(Items.REDSTONE);
					entries.add(Items.REDSTONE_TORCH);
					entries.add(Items.REDSTONE_BLOCK);
					entries.add(Items.REPEATER);
					entries.add(Items.COMPARATOR);
					entries.add(Items.TARGET);
					entries.add(Items.WAXED_COPPER_BULB);
					entries.add(Items.WAXED_EXPOSED_COPPER_BULB);
					entries.add(Items.WAXED_WEATHERED_COPPER_BULB);
					entries.add(Items.WAXED_OXIDIZED_COPPER_BULB);
					entries.add(Items.LEVER);
					entries.add(Items.OAK_BUTTON);
					entries.add(Items.STONE_BUTTON);
					entries.add(Items.OAK_PRESSURE_PLATE);
					entries.add(Items.STONE_PRESSURE_PLATE);
					entries.add(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
					entries.add(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
					entries.add(Items.SCULK_SENSOR);
					entries.add(Items.CALIBRATED_SCULK_SENSOR);
					entries.add(Items.SCULK_SHRIEKER);
					entries.add(Items.AMETHYST_BLOCK);
					entries.add(Items.WHITE_WOOL);
					entries.add(Items.TRIPWIRE_HOOK);
					entries.add(Items.STRING);
					entries.add(Items.LECTERN);
					entries.add(Items.DAYLIGHT_DETECTOR);
					entries.add(Items.LIGHTNING_ROD);
					entries.add(Items.PISTON);
					entries.add(Items.STICKY_PISTON);
					entries.add(Items.SLIME_BLOCK);
					entries.add(Items.HONEY_BLOCK);
					entries.add(Items.DISPENSER);
					entries.add(Items.DROPPER);
					entries.add(Items.CRAFTER);
					entries.add(Items.HOPPER);
					entries.add(Items.CHEST);
					entries.add(Items.BARREL);
					entries.add(Items.CHISELED_BOOKSHELF);
					entries.add(Items.FURNACE);
					entries.add(Items.TRAPPED_CHEST);
					entries.add(Items.JUKEBOX);
					entries.add(Items.DECORATED_POT);
					entries.add(Items.OBSERVER);
					entries.add(Items.NOTE_BLOCK);
					entries.add(Items.COMPOSTER);
					entries.add(Items.CAULDRON);
					entries.add(Items.RAIL);
					entries.add(Items.POWERED_RAIL);
					entries.add(Items.DETECTOR_RAIL);
					entries.add(Items.ACTIVATOR_RAIL);
					entries.add(Items.MINECART);
					entries.add(Items.HOPPER_MINECART);
					entries.add(Items.CHEST_MINECART);
					entries.add(Items.FURNACE_MINECART);
					entries.add(Items.TNT_MINECART);
					entries.add(Items.OAK_CHEST_BOAT);
					entries.add(Items.BAMBOO_CHEST_RAFT);
					entries.add(Items.OAK_DOOR);
					entries.add(Items.IRON_DOOR);
					entries.add(Items.OAK_FENCE_GATE);
					entries.add(Items.OAK_TRAPDOOR);
					entries.add(Items.IRON_TRAPDOOR);
					entries.add(Items.TNT);
					entries.add(Items.REDSTONE_LAMP);
					entries.add(Items.BELL);
					entries.add(Items.BIG_DRIPLEAF);
					entries.add(Items.ARMOR_STAND);
					entries.add(Items.REDSTONE_ORE);
				})
				.build()
		);
		Registry.register(
			registry,
			HOTBAR,
			ItemGroup.create(ItemGroup.Row.TOP, 5)
				.displayName(Text.translatable("itemGroup.hotbar"))
				.icon(() -> new ItemStack(Blocks.BOOKSHELF))
				.special()
				.type(ItemGroup.Type.HOTBAR)
				.build()
		);
		Registry.register(
			registry,
			SEARCH,
			ItemGroup.create(ItemGroup.Row.TOP, 6)
				.displayName(Text.translatable("itemGroup.search"))
				.icon(() -> new ItemStack(Items.COMPASS))
				.entries((displayContext, entries) -> {
					Set<ItemStack> set = ItemStackSet.create();

					for (ItemGroup itemGroup : registry) {
						if (itemGroup.getType() != ItemGroup.Type.SEARCH) {
							set.addAll(itemGroup.getSearchTabStacks());
						}
					}

					entries.addAll(set);
				})
				.texture(ITEM_SEARCH_TAB_TEXTURE_ID)
				.special()
				.type(ItemGroup.Type.SEARCH)
				.build()
		);
		Registry.register(
			registry,
			TOOLS,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 0)
				.displayName(Text.translatable("itemGroup.tools"))
				.icon(() -> new ItemStack(Items.DIAMOND_PICKAXE))
				.entries(
					(displayContext, entries) -> {
						entries.add(Items.WOODEN_SHOVEL);
						entries.add(Items.WOODEN_PICKAXE);
						entries.add(Items.WOODEN_AXE);
						entries.add(Items.WOODEN_HOE);
						entries.add(Items.STONE_SHOVEL);
						entries.add(Items.STONE_PICKAXE);
						entries.add(Items.STONE_AXE);
						entries.add(Items.STONE_HOE);
						entries.add(Items.IRON_SHOVEL);
						entries.add(Items.IRON_PICKAXE);
						entries.add(Items.IRON_AXE);
						entries.add(Items.IRON_HOE);
						entries.add(Items.GOLDEN_SHOVEL);
						entries.add(Items.GOLDEN_PICKAXE);
						entries.add(Items.GOLDEN_AXE);
						entries.add(Items.GOLDEN_HOE);
						entries.add(Items.DIAMOND_SHOVEL);
						entries.add(Items.DIAMOND_PICKAXE);
						entries.add(Items.DIAMOND_AXE);
						entries.add(Items.DIAMOND_HOE);
						entries.add(Items.NETHERITE_SHOVEL);
						entries.add(Items.NETHERITE_PICKAXE);
						entries.add(Items.NETHERITE_AXE);
						entries.add(Items.NETHERITE_HOE);
						entries.add(Items.BUCKET);
						entries.add(Items.WATER_BUCKET);
						entries.add(Items.COD_BUCKET);
						entries.add(Items.SALMON_BUCKET);
						entries.add(Items.TROPICAL_FISH_BUCKET);
						entries.add(Items.PUFFERFISH_BUCKET);
						entries.add(Items.AXOLOTL_BUCKET);
						entries.add(Items.TADPOLE_BUCKET);
						entries.add(Items.LAVA_BUCKET);
						entries.add(Items.POWDER_SNOW_BUCKET);
						entries.add(Items.MILK_BUCKET);
						entries.add(Items.FISHING_ROD);
						entries.add(Items.FLINT_AND_STEEL);
						entries.add(Items.FIRE_CHARGE);
						entries.add(Items.BONE_MEAL);
						entries.add(Items.SHEARS);
						entries.add(Items.BRUSH);
						entries.add(Items.NAME_TAG);
						entries.add(Items.LEAD);
						if (displayContext.enabledFeatures().contains(FeatureFlags.BUNDLE)) {
							entries.add(Items.BUNDLE);
						}

						entries.add(Items.COMPASS);
						entries.add(Items.RECOVERY_COMPASS);
						entries.add(Items.CLOCK);
						entries.add(Items.SPYGLASS);
						entries.add(Items.MAP);
						entries.add(Items.WRITABLE_BOOK);
						entries.add(Items.WIND_CHARGE);
						entries.add(Items.ENDER_PEARL);
						entries.add(Items.ENDER_EYE);
						entries.add(Items.ELYTRA);
						addFireworkRockets(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
						entries.add(Items.SADDLE);
						entries.add(Items.CARROT_ON_A_STICK);
						entries.add(Items.WARPED_FUNGUS_ON_A_STICK);
						entries.add(Items.OAK_BOAT);
						entries.add(Items.OAK_CHEST_BOAT);
						entries.add(Items.SPRUCE_BOAT);
						entries.add(Items.SPRUCE_CHEST_BOAT);
						entries.add(Items.BIRCH_BOAT);
						entries.add(Items.BIRCH_CHEST_BOAT);
						entries.add(Items.JUNGLE_BOAT);
						entries.add(Items.JUNGLE_CHEST_BOAT);
						entries.add(Items.ACACIA_BOAT);
						entries.add(Items.ACACIA_CHEST_BOAT);
						entries.add(Items.DARK_OAK_BOAT);
						entries.add(Items.DARK_OAK_CHEST_BOAT);
						entries.add(Items.MANGROVE_BOAT);
						entries.add(Items.MANGROVE_CHEST_BOAT);
						entries.add(Items.CHERRY_BOAT);
						entries.add(Items.CHERRY_CHEST_BOAT);
						entries.add(Items.BAMBOO_RAFT);
						entries.add(Items.BAMBOO_CHEST_RAFT);
						entries.add(Items.RAIL);
						entries.add(Items.POWERED_RAIL);
						entries.add(Items.DETECTOR_RAIL);
						entries.add(Items.ACTIVATOR_RAIL);
						entries.add(Items.MINECART);
						entries.add(Items.HOPPER_MINECART);
						entries.add(Items.CHEST_MINECART);
						entries.add(Items.FURNACE_MINECART);
						entries.add(Items.TNT_MINECART);
						displayContext.lookup()
							.getOptionalWrapper(RegistryKeys.INSTRUMENT)
							.ifPresent(wrapper -> addInstruments(entries, wrapper, Items.GOAT_HORN, InstrumentTags.GOAT_HORNS, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS));
						entries.add(Items.MUSIC_DISC_13);
						entries.add(Items.MUSIC_DISC_CAT);
						entries.add(Items.MUSIC_DISC_BLOCKS);
						entries.add(Items.MUSIC_DISC_CHIRP);
						entries.add(Items.MUSIC_DISC_FAR);
						entries.add(Items.MUSIC_DISC_MALL);
						entries.add(Items.MUSIC_DISC_MELLOHI);
						entries.add(Items.MUSIC_DISC_STAL);
						entries.add(Items.MUSIC_DISC_STRAD);
						entries.add(Items.MUSIC_DISC_WARD);
						entries.add(Items.MUSIC_DISC_11);
						entries.add(Items.MUSIC_DISC_CREATOR_MUSIC_BOX);
						entries.add(Items.MUSIC_DISC_WAIT);
						entries.add(Items.MUSIC_DISC_CREATOR);
						entries.add(Items.MUSIC_DISC_PRECIPICE);
						entries.add(Items.MUSIC_DISC_OTHERSIDE);
						entries.add(Items.MUSIC_DISC_RELIC);
						entries.add(Items.MUSIC_DISC_5);
						entries.add(Items.MUSIC_DISC_PIGSTEP);
					}
				)
				.build()
		);
		Registry.register(
			registry,
			COMBAT,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 1)
				.displayName(Text.translatable("itemGroup.combat"))
				.icon(() -> new ItemStack(Items.NETHERITE_SWORD))
				.entries(
					(displayContext, entries) -> {
						entries.add(Items.WOODEN_SWORD);
						entries.add(Items.STONE_SWORD);
						entries.add(Items.IRON_SWORD);
						entries.add(Items.GOLDEN_SWORD);
						entries.add(Items.DIAMOND_SWORD);
						entries.add(Items.NETHERITE_SWORD);
						entries.add(Items.WOODEN_AXE);
						entries.add(Items.STONE_AXE);
						entries.add(Items.IRON_AXE);
						entries.add(Items.GOLDEN_AXE);
						entries.add(Items.DIAMOND_AXE);
						entries.add(Items.NETHERITE_AXE);
						entries.add(Items.TRIDENT);
						entries.add(Items.MACE);
						entries.add(Items.SHIELD);
						entries.add(Items.LEATHER_HELMET);
						entries.add(Items.LEATHER_CHESTPLATE);
						entries.add(Items.LEATHER_LEGGINGS);
						entries.add(Items.LEATHER_BOOTS);
						entries.add(Items.CHAINMAIL_HELMET);
						entries.add(Items.CHAINMAIL_CHESTPLATE);
						entries.add(Items.CHAINMAIL_LEGGINGS);
						entries.add(Items.CHAINMAIL_BOOTS);
						entries.add(Items.IRON_HELMET);
						entries.add(Items.IRON_CHESTPLATE);
						entries.add(Items.IRON_LEGGINGS);
						entries.add(Items.IRON_BOOTS);
						entries.add(Items.GOLDEN_HELMET);
						entries.add(Items.GOLDEN_CHESTPLATE);
						entries.add(Items.GOLDEN_LEGGINGS);
						entries.add(Items.GOLDEN_BOOTS);
						entries.add(Items.DIAMOND_HELMET);
						entries.add(Items.DIAMOND_CHESTPLATE);
						entries.add(Items.DIAMOND_LEGGINGS);
						entries.add(Items.DIAMOND_BOOTS);
						entries.add(Items.NETHERITE_HELMET);
						entries.add(Items.NETHERITE_CHESTPLATE);
						entries.add(Items.NETHERITE_LEGGINGS);
						entries.add(Items.NETHERITE_BOOTS);
						entries.add(Items.TURTLE_HELMET);
						entries.add(Items.LEATHER_HORSE_ARMOR);
						entries.add(Items.IRON_HORSE_ARMOR);
						entries.add(Items.GOLDEN_HORSE_ARMOR);
						entries.add(Items.DIAMOND_HORSE_ARMOR);
						entries.add(Items.WOLF_ARMOR);
						entries.add(Items.TOTEM_OF_UNDYING);
						entries.add(Items.TNT);
						entries.add(Items.END_CRYSTAL);
						entries.add(Items.SNOWBALL);
						entries.add(Items.EGG);
						entries.add(Items.WIND_CHARGE);
						entries.add(Items.BOW);
						entries.add(Items.CROSSBOW);
						addFireworkRockets(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
						entries.add(Items.ARROW);
						entries.add(Items.SPECTRAL_ARROW);
						displayContext.lookup()
							.getOptionalWrapper(RegistryKeys.POTION)
							.ifPresent(
								registryWrapper -> addPotions(
										entries, registryWrapper, Items.TIPPED_ARROW, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, displayContext.enabledFeatures()
									)
							);
					}
				)
				.build()
		);
		Registry.register(
			registry,
			FOOD_AND_DRINK,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 2)
				.displayName(Text.translatable("itemGroup.foodAndDrink"))
				.icon(() -> new ItemStack(Items.GOLDEN_APPLE))
				.entries((displayContext, entries) -> {
					entries.add(Items.APPLE);
					entries.add(Items.GOLDEN_APPLE);
					entries.add(Items.ENCHANTED_GOLDEN_APPLE);
					entries.add(Items.MELON_SLICE);
					entries.add(Items.SWEET_BERRIES);
					entries.add(Items.GLOW_BERRIES);
					entries.add(Items.CHORUS_FRUIT);
					entries.add(Items.CARROT);
					entries.add(Items.GOLDEN_CARROT);
					entries.add(Items.POTATO);
					entries.add(Items.BAKED_POTATO);
					entries.add(Items.POISONOUS_POTATO);
					entries.add(Items.BEETROOT);
					entries.add(Items.DRIED_KELP);
					entries.add(Items.BEEF);
					entries.add(Items.COOKED_BEEF);
					entries.add(Items.PORKCHOP);
					entries.add(Items.COOKED_PORKCHOP);
					entries.add(Items.MUTTON);
					entries.add(Items.COOKED_MUTTON);
					entries.add(Items.CHICKEN);
					entries.add(Items.COOKED_CHICKEN);
					entries.add(Items.RABBIT);
					entries.add(Items.COOKED_RABBIT);
					entries.add(Items.COD);
					entries.add(Items.COOKED_COD);
					entries.add(Items.SALMON);
					entries.add(Items.COOKED_SALMON);
					entries.add(Items.TROPICAL_FISH);
					entries.add(Items.PUFFERFISH);
					entries.add(Items.BREAD);
					entries.add(Items.COOKIE);
					entries.add(Items.CAKE);
					entries.add(Items.PUMPKIN_PIE);
					entries.add(Items.ROTTEN_FLESH);
					entries.add(Items.SPIDER_EYE);
					entries.add(Items.MUSHROOM_STEW);
					entries.add(Items.BEETROOT_SOUP);
					entries.add(Items.RABBIT_STEW);
					addSuspiciousStews(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
					entries.add(Items.MILK_BUCKET);
					entries.add(Items.HONEY_BOTTLE);
					addOminousBottles(entries, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
					displayContext.lookup().getOptionalWrapper(RegistryKeys.POTION).ifPresent(registryWrapper -> {
						addPotions(entries, registryWrapper, Items.POTION, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, displayContext.enabledFeatures());
						addPotions(entries, registryWrapper, Items.SPLASH_POTION, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, displayContext.enabledFeatures());
						addPotions(entries, registryWrapper, Items.LINGERING_POTION, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS, displayContext.enabledFeatures());
					});
				})
				.build()
		);
		Registry.register(
			registry,
			INGREDIENTS,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 3)
				.displayName(Text.translatable("itemGroup.ingredients"))
				.icon(() -> new ItemStack(Items.IRON_INGOT))
				.entries((displayContext, entries) -> {
					entries.add(Items.COAL);
					entries.add(Items.CHARCOAL);
					entries.add(Items.RAW_IRON);
					entries.add(Items.RAW_COPPER);
					entries.add(Items.RAW_GOLD);
					entries.add(Items.EMERALD);
					entries.add(Items.LAPIS_LAZULI);
					entries.add(Items.DIAMOND);
					entries.add(Items.ANCIENT_DEBRIS);
					entries.add(Items.QUARTZ);
					entries.add(Items.AMETHYST_SHARD);
					entries.add(Items.IRON_NUGGET);
					entries.add(Items.GOLD_NUGGET);
					entries.add(Items.IRON_INGOT);
					entries.add(Items.COPPER_INGOT);
					entries.add(Items.GOLD_INGOT);
					entries.add(Items.NETHERITE_SCRAP);
					entries.add(Items.NETHERITE_INGOT);
					entries.add(Items.STICK);
					entries.add(Items.FLINT);
					entries.add(Items.WHEAT);
					entries.add(Items.BONE);
					entries.add(Items.BONE_MEAL);
					entries.add(Items.STRING);
					entries.add(Items.FEATHER);
					entries.add(Items.SNOWBALL);
					entries.add(Items.EGG);
					entries.add(Items.LEATHER);
					entries.add(Items.RABBIT_HIDE);
					entries.add(Items.HONEYCOMB);
					entries.add(Items.INK_SAC);
					entries.add(Items.GLOW_INK_SAC);
					entries.add(Items.TURTLE_SCUTE);
					entries.add(Items.ARMADILLO_SCUTE);
					entries.add(Items.SLIME_BALL);
					entries.add(Items.CLAY_BALL);
					entries.add(Items.PRISMARINE_SHARD);
					entries.add(Items.PRISMARINE_CRYSTALS);
					entries.add(Items.NAUTILUS_SHELL);
					entries.add(Items.HEART_OF_THE_SEA);
					entries.add(Items.FIRE_CHARGE);
					entries.add(Items.BLAZE_ROD);
					entries.add(Items.BREEZE_ROD);
					entries.add(Items.HEAVY_CORE);
					entries.add(Items.NETHER_STAR);
					entries.add(Items.ENDER_PEARL);
					entries.add(Items.ENDER_EYE);
					entries.add(Items.SHULKER_SHELL);
					entries.add(Items.POPPED_CHORUS_FRUIT);
					entries.add(Items.ECHO_SHARD);
					entries.add(Items.DISC_FRAGMENT_5);
					entries.add(Items.WHITE_DYE);
					entries.add(Items.LIGHT_GRAY_DYE);
					entries.add(Items.GRAY_DYE);
					entries.add(Items.BLACK_DYE);
					entries.add(Items.BROWN_DYE);
					entries.add(Items.RED_DYE);
					entries.add(Items.ORANGE_DYE);
					entries.add(Items.YELLOW_DYE);
					entries.add(Items.LIME_DYE);
					entries.add(Items.GREEN_DYE);
					entries.add(Items.CYAN_DYE);
					entries.add(Items.LIGHT_BLUE_DYE);
					entries.add(Items.BLUE_DYE);
					entries.add(Items.PURPLE_DYE);
					entries.add(Items.MAGENTA_DYE);
					entries.add(Items.PINK_DYE);
					entries.add(Items.BOWL);
					entries.add(Items.BRICK);
					entries.add(Items.NETHER_BRICK);
					entries.add(Items.PAPER);
					entries.add(Items.BOOK);
					entries.add(Items.FIREWORK_STAR);
					entries.add(Items.GLASS_BOTTLE);
					entries.add(Items.NETHER_WART);
					entries.add(Items.REDSTONE);
					entries.add(Items.GLOWSTONE_DUST);
					entries.add(Items.GUNPOWDER);
					entries.add(Items.DRAGON_BREATH);
					entries.add(Items.FERMENTED_SPIDER_EYE);
					entries.add(Items.BLAZE_POWDER);
					entries.add(Items.SUGAR);
					entries.add(Items.RABBIT_FOOT);
					entries.add(Items.GLISTERING_MELON_SLICE);
					entries.add(Items.SPIDER_EYE);
					entries.add(Items.PUFFERFISH);
					entries.add(Items.MAGMA_CREAM);
					entries.add(Items.GOLDEN_CARROT);
					entries.add(Items.GHAST_TEAR);
					entries.add(Items.TURTLE_HELMET);
					entries.add(Items.PHANTOM_MEMBRANE);
					entries.add(Items.FLOWER_BANNER_PATTERN);
					entries.add(Items.CREEPER_BANNER_PATTERN);
					entries.add(Items.SKULL_BANNER_PATTERN);
					entries.add(Items.MOJANG_BANNER_PATTERN);
					entries.add(Items.GLOBE_BANNER_PATTERN);
					entries.add(Items.PIGLIN_BANNER_PATTERN);
					entries.add(Items.FLOW_BANNER_PATTERN);
					entries.add(Items.GUSTER_BANNER_PATTERN);
					entries.add(Items.ANGLER_POTTERY_SHERD);
					entries.add(Items.ARCHER_POTTERY_SHERD);
					entries.add(Items.ARMS_UP_POTTERY_SHERD);
					entries.add(Items.BLADE_POTTERY_SHERD);
					entries.add(Items.BREWER_POTTERY_SHERD);
					entries.add(Items.BURN_POTTERY_SHERD);
					entries.add(Items.DANGER_POTTERY_SHERD);
					entries.add(Items.FLOW_POTTERY_SHERD);
					entries.add(Items.EXPLORER_POTTERY_SHERD);
					entries.add(Items.FRIEND_POTTERY_SHERD);
					entries.add(Items.GUSTER_POTTERY_SHERD);
					entries.add(Items.HEART_POTTERY_SHERD);
					entries.add(Items.HEARTBREAK_POTTERY_SHERD);
					entries.add(Items.HOWL_POTTERY_SHERD);
					entries.add(Items.MINER_POTTERY_SHERD);
					entries.add(Items.MOURNER_POTTERY_SHERD);
					entries.add(Items.PLENTY_POTTERY_SHERD);
					entries.add(Items.PRIZE_POTTERY_SHERD);
					entries.add(Items.SCRAPE_POTTERY_SHERD);
					entries.add(Items.SHEAF_POTTERY_SHERD);
					entries.add(Items.SHELTER_POTTERY_SHERD);
					entries.add(Items.SKULL_POTTERY_SHERD);
					entries.add(Items.SNORT_POTTERY_SHERD);
					entries.add(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
					entries.add(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
					entries.add(Items.EXPERIENCE_BOTTLE);
					entries.add(Items.TRIAL_KEY);
					entries.add(Items.OMINOUS_TRIAL_KEY);
					displayContext.lookup().getOptionalWrapper(RegistryKeys.ENCHANTMENT).ifPresent(registryWrapper -> {
						addMaxLevelEnchantedBooks(entries, registryWrapper, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
						addAllLevelEnchantedBooks(entries, registryWrapper, ItemGroup.StackVisibility.SEARCH_TAB_ONLY);
					});
				})
				.build()
		);
		Registry.register(
			registry,
			SPAWN_EGGS,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 4)
				.displayName(Text.translatable("itemGroup.spawnEggs"))
				.icon(() -> new ItemStack(Items.PIG_SPAWN_EGG))
				.entries((displayContext, entries) -> {
					entries.add(Items.SPAWNER);
					entries.add(Items.TRIAL_SPAWNER);
					entries.add(Items.ALLAY_SPAWN_EGG);
					entries.add(Items.ARMADILLO_SPAWN_EGG);
					entries.add(Items.AXOLOTL_SPAWN_EGG);
					entries.add(Items.BAT_SPAWN_EGG);
					entries.add(Items.BEE_SPAWN_EGG);
					entries.add(Items.BLAZE_SPAWN_EGG);
					entries.add(Items.BOGGED_SPAWN_EGG);
					entries.add(Items.BREEZE_SPAWN_EGG);
					entries.add(Items.CAMEL_SPAWN_EGG);
					entries.add(Items.CAT_SPAWN_EGG);
					entries.add(Items.CAVE_SPIDER_SPAWN_EGG);
					entries.add(Items.CHICKEN_SPAWN_EGG);
					entries.add(Items.COD_SPAWN_EGG);
					entries.add(Items.COW_SPAWN_EGG);
					entries.add(Items.CREEPER_SPAWN_EGG);
					entries.add(Items.DOLPHIN_SPAWN_EGG);
					entries.add(Items.DONKEY_SPAWN_EGG);
					entries.add(Items.DROWNED_SPAWN_EGG);
					entries.add(Items.ELDER_GUARDIAN_SPAWN_EGG);
					entries.add(Items.ENDERMAN_SPAWN_EGG);
					entries.add(Items.ENDERMITE_SPAWN_EGG);
					entries.add(Items.EVOKER_SPAWN_EGG);
					entries.add(Items.FOX_SPAWN_EGG);
					entries.add(Items.FROG_SPAWN_EGG);
					entries.add(Items.GHAST_SPAWN_EGG);
					entries.add(Items.GLOW_SQUID_SPAWN_EGG);
					entries.add(Items.GOAT_SPAWN_EGG);
					entries.add(Items.GUARDIAN_SPAWN_EGG);
					entries.add(Items.HOGLIN_SPAWN_EGG);
					entries.add(Items.HORSE_SPAWN_EGG);
					entries.add(Items.HUSK_SPAWN_EGG);
					entries.add(Items.IRON_GOLEM_SPAWN_EGG);
					entries.add(Items.LLAMA_SPAWN_EGG);
					entries.add(Items.MAGMA_CUBE_SPAWN_EGG);
					entries.add(Items.MOOSHROOM_SPAWN_EGG);
					entries.add(Items.MULE_SPAWN_EGG);
					entries.add(Items.OCELOT_SPAWN_EGG);
					entries.add(Items.PANDA_SPAWN_EGG);
					entries.add(Items.PARROT_SPAWN_EGG);
					entries.add(Items.PHANTOM_SPAWN_EGG);
					entries.add(Items.PIG_SPAWN_EGG);
					entries.add(Items.PIGLIN_SPAWN_EGG);
					entries.add(Items.PIGLIN_BRUTE_SPAWN_EGG);
					entries.add(Items.PILLAGER_SPAWN_EGG);
					entries.add(Items.POLAR_BEAR_SPAWN_EGG);
					entries.add(Items.PUFFERFISH_SPAWN_EGG);
					entries.add(Items.RABBIT_SPAWN_EGG);
					entries.add(Items.RAVAGER_SPAWN_EGG);
					entries.add(Items.SALMON_SPAWN_EGG);
					entries.add(Items.SHEEP_SPAWN_EGG);
					entries.add(Items.SHULKER_SPAWN_EGG);
					entries.add(Items.SILVERFISH_SPAWN_EGG);
					entries.add(Items.SKELETON_SPAWN_EGG);
					entries.add(Items.SKELETON_HORSE_SPAWN_EGG);
					entries.add(Items.SLIME_SPAWN_EGG);
					entries.add(Items.SNIFFER_SPAWN_EGG);
					entries.add(Items.SNOW_GOLEM_SPAWN_EGG);
					entries.add(Items.SPIDER_SPAWN_EGG);
					entries.add(Items.SQUID_SPAWN_EGG);
					entries.add(Items.STRAY_SPAWN_EGG);
					entries.add(Items.STRIDER_SPAWN_EGG);
					entries.add(Items.TADPOLE_SPAWN_EGG);
					entries.add(Items.TRADER_LLAMA_SPAWN_EGG);
					entries.add(Items.TROPICAL_FISH_SPAWN_EGG);
					entries.add(Items.TURTLE_SPAWN_EGG);
					entries.add(Items.VEX_SPAWN_EGG);
					entries.add(Items.VILLAGER_SPAWN_EGG);
					entries.add(Items.VINDICATOR_SPAWN_EGG);
					entries.add(Items.WANDERING_TRADER_SPAWN_EGG);
					entries.add(Items.WARDEN_SPAWN_EGG);
					entries.add(Items.WITCH_SPAWN_EGG);
					entries.add(Items.WITHER_SKELETON_SPAWN_EGG);
					entries.add(Items.WOLF_SPAWN_EGG);
					entries.add(Items.ZOGLIN_SPAWN_EGG);
					entries.add(Items.ZOMBIE_SPAWN_EGG);
					entries.add(Items.ZOMBIE_HORSE_SPAWN_EGG);
					entries.add(Items.ZOMBIE_VILLAGER_SPAWN_EGG);
					entries.add(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG);
				})
				.build()
		);
		Registry.register(
			registry,
			OPERATOR,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 5)
				.displayName(Text.translatable("itemGroup.op"))
				.icon(() -> new ItemStack(Items.COMMAND_BLOCK))
				.special()
				.entries(
					(displayContext, entries) -> {
						if (displayContext.hasPermissions()) {
							entries.add(Items.COMMAND_BLOCK);
							entries.add(Items.CHAIN_COMMAND_BLOCK);
							entries.add(Items.REPEATING_COMMAND_BLOCK);
							entries.add(Items.COMMAND_BLOCK_MINECART);
							entries.add(Items.JIGSAW);
							entries.add(Items.STRUCTURE_BLOCK);
							entries.add(Items.STRUCTURE_VOID);
							entries.add(Items.BARRIER);
							entries.add(Items.DEBUG_STICK);

							for (int i = 15; i >= 0; i--) {
								entries.add(LightBlock.addNbtForLevel(new ItemStack(Items.LIGHT), i));
							}

							displayContext.lookup()
								.getOptionalWrapper(RegistryKeys.PAINTING_VARIANT)
								.ifPresent(
									registryWrapper -> addPaintings(
											entries,
											displayContext.lookup(),
											registryWrapper,
											registryEntry -> !registryEntry.isIn(PaintingVariantTags.PLACEABLE),
											ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS
										)
								);
						}
					}
				)
				.build()
		);
		return Registry.register(
			registry,
			INVENTORY,
			ItemGroup.create(ItemGroup.Row.BOTTOM, 6)
				.displayName(Text.translatable("itemGroup.inventory"))
				.icon(() -> new ItemStack(Blocks.CHEST))
				.texture(INVENTORY_TAB_TEXTURE_ID)
				.noRenderedName()
				.special()
				.type(ItemGroup.Type.INVENTORY)
				.noScrollbar()
				.build()
		);
	}

	public static void collect() {
		Map<Pair<ItemGroup.Row, Integer>, String> map = new HashMap();

		for (RegistryKey<ItemGroup> registryKey : Registries.ITEM_GROUP.getKeys()) {
			ItemGroup itemGroup = Registries.ITEM_GROUP.getOrThrow(registryKey);
			String string = itemGroup.getDisplayName().getString();
			String string2 = (String)map.put(Pair.of(itemGroup.getRow(), itemGroup.getColumn()), string);
			if (string2 != null) {
				throw new IllegalArgumentException("Duplicate position: " + string + " vs. " + string2);
			}
		}
	}

	public static ItemGroup getDefaultTab() {
		return Registries.ITEM_GROUP.getOrThrow(BUILDING_BLOCKS);
	}

	private static void addPotions(
		ItemGroup.Entries entries, RegistryWrapper<Potion> registryWrapper, Item item, ItemGroup.StackVisibility visibility, FeatureSet enabledFeatures
	) {
		registryWrapper.streamEntries()
			.filter(potionEntry -> ((Potion)potionEntry.value()).isEnabled(enabledFeatures))
			.map(entry -> PotionContentsComponent.createStack(item, entry))
			.forEach(stack -> entries.add(stack, visibility));
	}

	private static void addMaxLevelEnchantedBooks(
		ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility
	) {
		registryWrapper.streamEntries()
			.map(
				enchantmentEntry -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantmentEntry, ((Enchantment)enchantmentEntry.value()).getMaxLevel()))
			)
			.forEach(stack -> entries.add(stack, stackVisibility));
	}

	private static void addAllLevelEnchantedBooks(
		ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility
	) {
		registryWrapper.streamEntries()
			.flatMap(
				enchantmentEntry -> IntStream.rangeClosed(((Enchantment)enchantmentEntry.value()).getMinLevel(), ((Enchantment)enchantmentEntry.value()).getMaxLevel())
						.mapToObj(level -> EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantmentEntry, level)))
			)
			.forEach(stack -> entries.add(stack, stackVisibility));
	}

	private static void addInstruments(
		ItemGroup.Entries entries, RegistryWrapper<Instrument> registryWrapper, Item item, TagKey<Instrument> instrumentTag, ItemGroup.StackVisibility visibility
	) {
		registryWrapper.getOptional(instrumentTag)
			.ifPresent(
				entryList -> entryList.stream().map(instrument -> GoatHornItem.getStackForInstrument(item, instrument)).forEach(stack -> entries.add(stack, visibility))
			);
	}

	private static void addSuspiciousStews(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
		List<SuspiciousStewIngredient> list = SuspiciousStewIngredient.getAll();
		Set<ItemStack> set = ItemStackSet.create();

		for (SuspiciousStewIngredient suspiciousStewIngredient : list) {
			ItemStack itemStack = new ItemStack(Items.SUSPICIOUS_STEW);
			itemStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, suspiciousStewIngredient.getStewEffects());
			set.add(itemStack);
		}

		entries.addAll(set, visibility);
	}

	private static void addOminousBottles(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
		for (int i = 0; i <= 4; i++) {
			ItemStack itemStack = new ItemStack(Items.OMINOUS_BOTTLE);
			itemStack.set(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, i);
			entries.add(itemStack, visibility);
		}
	}

	private static void addFireworkRockets(ItemGroup.Entries entries, ItemGroup.StackVisibility visibility) {
		for (byte b : FireworkRocketItem.FLIGHT_VALUES) {
			ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET);
			itemStack.set(DataComponentTypes.FIREWORKS, new FireworksComponent(b, List.of()));
			entries.add(itemStack, visibility);
		}
	}

	private static void addPaintings(
		ItemGroup.Entries entries,
		RegistryWrapper.WrapperLookup registryLookup,
		RegistryWrapper.Impl<PaintingVariant> registryWrapper,
		Predicate<RegistryEntry<PaintingVariant>> filter,
		ItemGroup.StackVisibility stackVisibility
	) {
		RegistryOps<NbtElement> registryOps = registryLookup.getOps(NbtOps.INSTANCE);
		registryWrapper.streamEntries()
			.filter(filter)
			.sorted(PAINTING_VARIANT_COMPARATOR)
			.forEach(
				paintingVariantEntry -> {
					NbtComponent nbtComponent = NbtComponent.DEFAULT
						.with(registryOps, PaintingEntity.VARIANT_MAP_CODEC, paintingVariantEntry)
						.getOrThrow()
						.apply(nbt -> nbt.putString("id", "minecraft:painting"));
					ItemStack itemStack = new ItemStack(Items.PAINTING);
					itemStack.set(DataComponentTypes.ENTITY_DATA, nbtComponent);
					entries.add(itemStack, stackVisibility);
				}
			);
	}

	public static List<ItemGroup> getGroupsToDisplay() {
		return stream().filter(ItemGroup::shouldDisplay).toList();
	}

	public static List<ItemGroup> getGroups() {
		return stream().toList();
	}

	private static Stream<ItemGroup> stream() {
		return Registries.ITEM_GROUP.stream();
	}

	public static ItemGroup getSearchGroup() {
		return Registries.ITEM_GROUP.getOrThrow(SEARCH);
	}

	private static void updateEntries(ItemGroup.DisplayContext displayContext) {
		stream().filter(group -> group.getType() == ItemGroup.Type.CATEGORY).forEach(group -> group.updateEntries(displayContext));
		stream().filter(group -> group.getType() != ItemGroup.Type.CATEGORY).forEach(group -> group.updateEntries(displayContext));
	}

	public static boolean updateDisplayContext(FeatureSet enabledFeatures, boolean operatorEnabled, RegistryWrapper.WrapperLookup lookup) {
		if (displayContext != null && !displayContext.doesNotMatch(enabledFeatures, operatorEnabled, lookup)) {
			return false;
		} else {
			displayContext = new ItemGroup.DisplayContext(enabledFeatures, operatorEnabled, lookup);
			updateEntries(displayContext);
			return true;
		}
	}
}
