package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RenderLayers {
	private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.<Block, RenderLayer>newHashMap(), map -> {
		RenderLayer renderLayer = RenderLayer.getTripwire();
		map.put(Blocks.TRIPWIRE, renderLayer);
		RenderLayer renderLayer2 = RenderLayer.getCutoutMipped();
		map.put(Blocks.GRASS_BLOCK, renderLayer2);
		map.put(Blocks.IRON_BARS, renderLayer2);
		map.put(Blocks.GLASS_PANE, renderLayer2);
		map.put(Blocks.TRIPWIRE_HOOK, renderLayer2);
		map.put(Blocks.HOPPER, renderLayer2);
		map.put(Blocks.CHAIN, renderLayer2);
		map.put(Blocks.JUNGLE_LEAVES, renderLayer2);
		map.put(Blocks.OAK_LEAVES, renderLayer2);
		map.put(Blocks.SPRUCE_LEAVES, renderLayer2);
		map.put(Blocks.ACACIA_LEAVES, renderLayer2);
		map.put(Blocks.CHERRY_LEAVES, renderLayer2);
		map.put(Blocks.BIRCH_LEAVES, renderLayer2);
		map.put(Blocks.DARK_OAK_LEAVES, renderLayer2);
		map.put(Blocks.AZALEA_LEAVES, renderLayer2);
		map.put(Blocks.FLOWERING_AZALEA_LEAVES, renderLayer2);
		map.put(Blocks.MANGROVE_ROOTS, renderLayer2);
		map.put(Blocks.MANGROVE_LEAVES, renderLayer2);
		RenderLayer renderLayer3 = RenderLayer.getCutout();
		map.put(Blocks.OAK_SAPLING, renderLayer3);
		map.put(Blocks.SPRUCE_SAPLING, renderLayer3);
		map.put(Blocks.BIRCH_SAPLING, renderLayer3);
		map.put(Blocks.JUNGLE_SAPLING, renderLayer3);
		map.put(Blocks.ACACIA_SAPLING, renderLayer3);
		map.put(Blocks.CHERRY_SAPLING, renderLayer3);
		map.put(Blocks.DARK_OAK_SAPLING, renderLayer3);
		map.put(Blocks.GLASS, renderLayer3);
		map.put(Blocks.WHITE_BED, renderLayer3);
		map.put(Blocks.ORANGE_BED, renderLayer3);
		map.put(Blocks.MAGENTA_BED, renderLayer3);
		map.put(Blocks.LIGHT_BLUE_BED, renderLayer3);
		map.put(Blocks.YELLOW_BED, renderLayer3);
		map.put(Blocks.LIME_BED, renderLayer3);
		map.put(Blocks.PINK_BED, renderLayer3);
		map.put(Blocks.GRAY_BED, renderLayer3);
		map.put(Blocks.LIGHT_GRAY_BED, renderLayer3);
		map.put(Blocks.CYAN_BED, renderLayer3);
		map.put(Blocks.PURPLE_BED, renderLayer3);
		map.put(Blocks.BLUE_BED, renderLayer3);
		map.put(Blocks.BROWN_BED, renderLayer3);
		map.put(Blocks.GREEN_BED, renderLayer3);
		map.put(Blocks.RED_BED, renderLayer3);
		map.put(Blocks.BLACK_BED, renderLayer3);
		map.put(Blocks.POWERED_RAIL, renderLayer3);
		map.put(Blocks.DETECTOR_RAIL, renderLayer3);
		map.put(Blocks.COBWEB, renderLayer3);
		map.put(Blocks.SHORT_GRASS, renderLayer3);
		map.put(Blocks.FERN, renderLayer3);
		map.put(Blocks.DEAD_BUSH, renderLayer3);
		map.put(Blocks.SEAGRASS, renderLayer3);
		map.put(Blocks.TALL_SEAGRASS, renderLayer3);
		map.put(Blocks.DANDELION, renderLayer3);
		map.put(Blocks.POPPY, renderLayer3);
		map.put(Blocks.BLUE_ORCHID, renderLayer3);
		map.put(Blocks.ALLIUM, renderLayer3);
		map.put(Blocks.AZURE_BLUET, renderLayer3);
		map.put(Blocks.RED_TULIP, renderLayer3);
		map.put(Blocks.ORANGE_TULIP, renderLayer3);
		map.put(Blocks.WHITE_TULIP, renderLayer3);
		map.put(Blocks.PINK_TULIP, renderLayer3);
		map.put(Blocks.OXEYE_DAISY, renderLayer3);
		map.put(Blocks.CORNFLOWER, renderLayer3);
		map.put(Blocks.WITHER_ROSE, renderLayer3);
		map.put(Blocks.LILY_OF_THE_VALLEY, renderLayer3);
		map.put(Blocks.BROWN_MUSHROOM, renderLayer3);
		map.put(Blocks.RED_MUSHROOM, renderLayer3);
		map.put(Blocks.TORCH, renderLayer3);
		map.put(Blocks.WALL_TORCH, renderLayer3);
		map.put(Blocks.SOUL_TORCH, renderLayer3);
		map.put(Blocks.SOUL_WALL_TORCH, renderLayer3);
		map.put(Blocks.FIRE, renderLayer3);
		map.put(Blocks.SOUL_FIRE, renderLayer3);
		map.put(Blocks.SPAWNER, renderLayer3);
		map.put(Blocks.TRIAL_SPAWNER, renderLayer3);
		map.put(Blocks.VAULT, renderLayer3);
		map.put(Blocks.REDSTONE_WIRE, renderLayer3);
		map.put(Blocks.WHEAT, renderLayer3);
		map.put(Blocks.OAK_DOOR, renderLayer3);
		map.put(Blocks.LADDER, renderLayer3);
		map.put(Blocks.RAIL, renderLayer3);
		map.put(Blocks.IRON_DOOR, renderLayer3);
		map.put(Blocks.REDSTONE_TORCH, renderLayer3);
		map.put(Blocks.REDSTONE_WALL_TORCH, renderLayer3);
		map.put(Blocks.CACTUS, renderLayer3);
		map.put(Blocks.SUGAR_CANE, renderLayer3);
		map.put(Blocks.REPEATER, renderLayer3);
		map.put(Blocks.OAK_TRAPDOOR, renderLayer3);
		map.put(Blocks.SPRUCE_TRAPDOOR, renderLayer3);
		map.put(Blocks.BIRCH_TRAPDOOR, renderLayer3);
		map.put(Blocks.JUNGLE_TRAPDOOR, renderLayer3);
		map.put(Blocks.ACACIA_TRAPDOOR, renderLayer3);
		map.put(Blocks.CHERRY_TRAPDOOR, renderLayer3);
		map.put(Blocks.DARK_OAK_TRAPDOOR, renderLayer3);
		map.put(Blocks.CRIMSON_TRAPDOOR, renderLayer3);
		map.put(Blocks.WARPED_TRAPDOOR, renderLayer3);
		map.put(Blocks.MANGROVE_TRAPDOOR, renderLayer3);
		map.put(Blocks.BAMBOO_TRAPDOOR, renderLayer3);
		map.put(Blocks.COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.EXPOSED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.WEATHERED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.WAXED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, renderLayer3);
		map.put(Blocks.ATTACHED_PUMPKIN_STEM, renderLayer3);
		map.put(Blocks.ATTACHED_MELON_STEM, renderLayer3);
		map.put(Blocks.PUMPKIN_STEM, renderLayer3);
		map.put(Blocks.MELON_STEM, renderLayer3);
		map.put(Blocks.VINE, renderLayer3);
		map.put(Blocks.GLOW_LICHEN, renderLayer3);
		map.put(Blocks.LILY_PAD, renderLayer3);
		map.put(Blocks.NETHER_WART, renderLayer3);
		map.put(Blocks.BREWING_STAND, renderLayer3);
		map.put(Blocks.COCOA, renderLayer3);
		map.put(Blocks.BEACON, renderLayer3);
		map.put(Blocks.FLOWER_POT, renderLayer3);
		map.put(Blocks.POTTED_OAK_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_SPRUCE_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_BIRCH_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_JUNGLE_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_ACACIA_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_CHERRY_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_DARK_OAK_SAPLING, renderLayer3);
		map.put(Blocks.POTTED_MANGROVE_PROPAGULE, renderLayer3);
		map.put(Blocks.POTTED_FERN, renderLayer3);
		map.put(Blocks.POTTED_DANDELION, renderLayer3);
		map.put(Blocks.POTTED_POPPY, renderLayer3);
		map.put(Blocks.POTTED_BLUE_ORCHID, renderLayer3);
		map.put(Blocks.POTTED_ALLIUM, renderLayer3);
		map.put(Blocks.POTTED_AZURE_BLUET, renderLayer3);
		map.put(Blocks.POTTED_RED_TULIP, renderLayer3);
		map.put(Blocks.POTTED_ORANGE_TULIP, renderLayer3);
		map.put(Blocks.POTTED_WHITE_TULIP, renderLayer3);
		map.put(Blocks.POTTED_PINK_TULIP, renderLayer3);
		map.put(Blocks.POTTED_OXEYE_DAISY, renderLayer3);
		map.put(Blocks.POTTED_CORNFLOWER, renderLayer3);
		map.put(Blocks.POTTED_LILY_OF_THE_VALLEY, renderLayer3);
		map.put(Blocks.POTTED_WITHER_ROSE, renderLayer3);
		map.put(Blocks.POTTED_RED_MUSHROOM, renderLayer3);
		map.put(Blocks.POTTED_BROWN_MUSHROOM, renderLayer3);
		map.put(Blocks.POTTED_DEAD_BUSH, renderLayer3);
		map.put(Blocks.POTTED_CACTUS, renderLayer3);
		map.put(Blocks.POTTED_AZALEA_BUSH, renderLayer3);
		map.put(Blocks.POTTED_FLOWERING_AZALEA_BUSH, renderLayer3);
		map.put(Blocks.POTTED_TORCHFLOWER, renderLayer3);
		map.put(Blocks.CARROTS, renderLayer3);
		map.put(Blocks.POTATOES, renderLayer3);
		map.put(Blocks.COMPARATOR, renderLayer3);
		map.put(Blocks.ACTIVATOR_RAIL, renderLayer3);
		map.put(Blocks.IRON_TRAPDOOR, renderLayer3);
		map.put(Blocks.SUNFLOWER, renderLayer3);
		map.put(Blocks.LILAC, renderLayer3);
		map.put(Blocks.ROSE_BUSH, renderLayer3);
		map.put(Blocks.PEONY, renderLayer3);
		map.put(Blocks.TALL_GRASS, renderLayer3);
		map.put(Blocks.LARGE_FERN, renderLayer3);
		map.put(Blocks.SPRUCE_DOOR, renderLayer3);
		map.put(Blocks.BIRCH_DOOR, renderLayer3);
		map.put(Blocks.JUNGLE_DOOR, renderLayer3);
		map.put(Blocks.ACACIA_DOOR, renderLayer3);
		map.put(Blocks.CHERRY_DOOR, renderLayer3);
		map.put(Blocks.DARK_OAK_DOOR, renderLayer3);
		map.put(Blocks.MANGROVE_DOOR, renderLayer3);
		map.put(Blocks.BAMBOO_DOOR, renderLayer3);
		map.put(Blocks.COPPER_DOOR, renderLayer3);
		map.put(Blocks.EXPOSED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.WEATHERED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.OXIDIZED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.WAXED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, renderLayer3);
		map.put(Blocks.END_ROD, renderLayer3);
		map.put(Blocks.CHORUS_PLANT, renderLayer3);
		map.put(Blocks.CHORUS_FLOWER, renderLayer3);
		map.put(Blocks.TORCHFLOWER, renderLayer3);
		map.put(Blocks.TORCHFLOWER_CROP, renderLayer3);
		map.put(Blocks.PITCHER_PLANT, renderLayer3);
		map.put(Blocks.PITCHER_CROP, renderLayer3);
		map.put(Blocks.BEETROOTS, renderLayer3);
		map.put(Blocks.KELP, renderLayer3);
		map.put(Blocks.KELP_PLANT, renderLayer3);
		map.put(Blocks.TURTLE_EGG, renderLayer3);
		map.put(Blocks.DEAD_TUBE_CORAL, renderLayer3);
		map.put(Blocks.DEAD_BRAIN_CORAL, renderLayer3);
		map.put(Blocks.DEAD_BUBBLE_CORAL, renderLayer3);
		map.put(Blocks.DEAD_FIRE_CORAL, renderLayer3);
		map.put(Blocks.DEAD_HORN_CORAL, renderLayer3);
		map.put(Blocks.TUBE_CORAL, renderLayer3);
		map.put(Blocks.BRAIN_CORAL, renderLayer3);
		map.put(Blocks.BUBBLE_CORAL, renderLayer3);
		map.put(Blocks.FIRE_CORAL, renderLayer3);
		map.put(Blocks.HORN_CORAL, renderLayer3);
		map.put(Blocks.DEAD_TUBE_CORAL_FAN, renderLayer3);
		map.put(Blocks.DEAD_BRAIN_CORAL_FAN, renderLayer3);
		map.put(Blocks.DEAD_BUBBLE_CORAL_FAN, renderLayer3);
		map.put(Blocks.DEAD_FIRE_CORAL_FAN, renderLayer3);
		map.put(Blocks.DEAD_HORN_CORAL_FAN, renderLayer3);
		map.put(Blocks.TUBE_CORAL_FAN, renderLayer3);
		map.put(Blocks.BRAIN_CORAL_FAN, renderLayer3);
		map.put(Blocks.BUBBLE_CORAL_FAN, renderLayer3);
		map.put(Blocks.FIRE_CORAL_FAN, renderLayer3);
		map.put(Blocks.HORN_CORAL_FAN, renderLayer3);
		map.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.TUBE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.BRAIN_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.BUBBLE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.FIRE_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.HORN_CORAL_WALL_FAN, renderLayer3);
		map.put(Blocks.SEA_PICKLE, renderLayer3);
		map.put(Blocks.CONDUIT, renderLayer3);
		map.put(Blocks.BAMBOO_SAPLING, renderLayer3);
		map.put(Blocks.BAMBOO, renderLayer3);
		map.put(Blocks.POTTED_BAMBOO, renderLayer3);
		map.put(Blocks.SCAFFOLDING, renderLayer3);
		map.put(Blocks.STONECUTTER, renderLayer3);
		map.put(Blocks.LANTERN, renderLayer3);
		map.put(Blocks.SOUL_LANTERN, renderLayer3);
		map.put(Blocks.CAMPFIRE, renderLayer3);
		map.put(Blocks.SOUL_CAMPFIRE, renderLayer3);
		map.put(Blocks.SWEET_BERRY_BUSH, renderLayer3);
		map.put(Blocks.WEEPING_VINES, renderLayer3);
		map.put(Blocks.WEEPING_VINES_PLANT, renderLayer3);
		map.put(Blocks.TWISTING_VINES, renderLayer3);
		map.put(Blocks.TWISTING_VINES_PLANT, renderLayer3);
		map.put(Blocks.NETHER_SPROUTS, renderLayer3);
		map.put(Blocks.CRIMSON_FUNGUS, renderLayer3);
		map.put(Blocks.WARPED_FUNGUS, renderLayer3);
		map.put(Blocks.CRIMSON_ROOTS, renderLayer3);
		map.put(Blocks.WARPED_ROOTS, renderLayer3);
		map.put(Blocks.POTTED_CRIMSON_FUNGUS, renderLayer3);
		map.put(Blocks.POTTED_WARPED_FUNGUS, renderLayer3);
		map.put(Blocks.POTTED_CRIMSON_ROOTS, renderLayer3);
		map.put(Blocks.POTTED_WARPED_ROOTS, renderLayer3);
		map.put(Blocks.CRIMSON_DOOR, renderLayer3);
		map.put(Blocks.WARPED_DOOR, renderLayer3);
		map.put(Blocks.POINTED_DRIPSTONE, renderLayer3);
		map.put(Blocks.SMALL_AMETHYST_BUD, renderLayer3);
		map.put(Blocks.MEDIUM_AMETHYST_BUD, renderLayer3);
		map.put(Blocks.LARGE_AMETHYST_BUD, renderLayer3);
		map.put(Blocks.AMETHYST_CLUSTER, renderLayer3);
		map.put(Blocks.LIGHTNING_ROD, renderLayer3);
		map.put(Blocks.CAVE_VINES, renderLayer3);
		map.put(Blocks.CAVE_VINES_PLANT, renderLayer3);
		map.put(Blocks.SPORE_BLOSSOM, renderLayer3);
		map.put(Blocks.FLOWERING_AZALEA, renderLayer3);
		map.put(Blocks.AZALEA, renderLayer3);
		map.put(Blocks.PINK_PETALS, renderLayer3);
		map.put(Blocks.BIG_DRIPLEAF, renderLayer3);
		map.put(Blocks.BIG_DRIPLEAF_STEM, renderLayer3);
		map.put(Blocks.SMALL_DRIPLEAF, renderLayer3);
		map.put(Blocks.HANGING_ROOTS, renderLayer3);
		map.put(Blocks.SCULK_SENSOR, renderLayer3);
		map.put(Blocks.CALIBRATED_SCULK_SENSOR, renderLayer3);
		map.put(Blocks.SCULK_VEIN, renderLayer3);
		map.put(Blocks.SCULK_SHRIEKER, renderLayer3);
		map.put(Blocks.MANGROVE_PROPAGULE, renderLayer3);
		map.put(Blocks.FROGSPAWN, renderLayer3);
		map.put(Blocks.COPPER_GRATE, renderLayer3);
		map.put(Blocks.EXPOSED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.WEATHERED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.OXIDIZED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.WAXED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, renderLayer3);
		map.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, renderLayer3);
		RenderLayer renderLayer4 = RenderLayer.getTranslucent();
		map.put(Blocks.ICE, renderLayer4);
		map.put(Blocks.NETHER_PORTAL, renderLayer4);
		map.put(Blocks.WHITE_STAINED_GLASS, renderLayer4);
		map.put(Blocks.ORANGE_STAINED_GLASS, renderLayer4);
		map.put(Blocks.MAGENTA_STAINED_GLASS, renderLayer4);
		map.put(Blocks.LIGHT_BLUE_STAINED_GLASS, renderLayer4);
		map.put(Blocks.YELLOW_STAINED_GLASS, renderLayer4);
		map.put(Blocks.LIME_STAINED_GLASS, renderLayer4);
		map.put(Blocks.PINK_STAINED_GLASS, renderLayer4);
		map.put(Blocks.GRAY_STAINED_GLASS, renderLayer4);
		map.put(Blocks.LIGHT_GRAY_STAINED_GLASS, renderLayer4);
		map.put(Blocks.CYAN_STAINED_GLASS, renderLayer4);
		map.put(Blocks.PURPLE_STAINED_GLASS, renderLayer4);
		map.put(Blocks.BLUE_STAINED_GLASS, renderLayer4);
		map.put(Blocks.BROWN_STAINED_GLASS, renderLayer4);
		map.put(Blocks.GREEN_STAINED_GLASS, renderLayer4);
		map.put(Blocks.RED_STAINED_GLASS, renderLayer4);
		map.put(Blocks.BLACK_STAINED_GLASS, renderLayer4);
		map.put(Blocks.WHITE_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.ORANGE_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.MAGENTA_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.YELLOW_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.LIME_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.PINK_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.GRAY_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.CYAN_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.PURPLE_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.BLUE_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.BROWN_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.GREEN_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.RED_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.BLACK_STAINED_GLASS_PANE, renderLayer4);
		map.put(Blocks.SLIME_BLOCK, renderLayer4);
		map.put(Blocks.HONEY_BLOCK, renderLayer4);
		map.put(Blocks.FROSTED_ICE, renderLayer4);
		map.put(Blocks.BUBBLE_COLUMN, renderLayer4);
		map.put(Blocks.TINTED_GLASS, renderLayer4);
	});
	private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.<Fluid, RenderLayer>newHashMap(), map -> {
		RenderLayer renderLayer = RenderLayer.getTranslucent();
		map.put(Fluids.FLOWING_WATER, renderLayer);
		map.put(Fluids.WATER, renderLayer);
	});
	private static boolean fancyGraphicsOrBetter;

	public static RenderLayer getBlockLayer(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			return renderLayer != null ? renderLayer : RenderLayer.getSolid();
		}
	}

	public static RenderLayer getMovingBlockLayer(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphicsOrBetter ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			if (renderLayer != null) {
				return renderLayer == RenderLayer.getTranslucent() ? RenderLayer.getTranslucentMovingBlock() : renderLayer;
			} else {
				return RenderLayer.getSolid();
			}
		}
	}

	public static RenderLayer getEntityBlockLayer(BlockState state, boolean direct) {
		RenderLayer renderLayer = getBlockLayer(state);
		if (renderLayer == RenderLayer.getTranslucent()) {
			if (!MinecraftClient.isFabulousGraphicsOrBetter()) {
				return TexturedRenderLayers.getEntityTranslucentCull();
			} else {
				return direct ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
			}
		} else {
			return TexturedRenderLayers.getEntityCutout();
		}
	}

	public static RenderLayer getItemLayer(ItemStack stack, boolean direct) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			return getEntityBlockLayer(block.getDefaultState(), direct);
		} else {
			return direct ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.getItemEntityTranslucentCull();
		}
	}

	public static RenderLayer getFluidLayer(FluidState state) {
		RenderLayer renderLayer = (RenderLayer)FLUIDS.get(state.getFluid());
		return renderLayer != null ? renderLayer : RenderLayer.getSolid();
	}

	public static void setFancyGraphicsOrBetter(boolean fancyGraphicsOrBetter) {
		RenderLayers.fancyGraphicsOrBetter = fancyGraphicsOrBetter;
	}
}
