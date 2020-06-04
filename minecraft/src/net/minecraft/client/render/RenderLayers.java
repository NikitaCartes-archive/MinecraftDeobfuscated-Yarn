package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RenderLayers {
	private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.<Block, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getCutoutMipped();
		hashMap.put(Blocks.GRASS_BLOCK, renderLayer);
		hashMap.put(Blocks.IRON_BARS, renderLayer);
		hashMap.put(Blocks.GLASS_PANE, renderLayer);
		hashMap.put(Blocks.TRIPWIRE_HOOK, renderLayer);
		hashMap.put(Blocks.HOPPER, renderLayer);
		hashMap.put(Blocks.CHAIN, renderLayer);
		hashMap.put(Blocks.JUNGLE_LEAVES, renderLayer);
		hashMap.put(Blocks.OAK_LEAVES, renderLayer);
		hashMap.put(Blocks.SPRUCE_LEAVES, renderLayer);
		hashMap.put(Blocks.ACACIA_LEAVES, renderLayer);
		hashMap.put(Blocks.BIRCH_LEAVES, renderLayer);
		hashMap.put(Blocks.DARK_OAK_LEAVES, renderLayer);
		RenderLayer renderLayer2 = RenderLayer.getCutout();
		hashMap.put(Blocks.OAK_SAPLING, renderLayer2);
		hashMap.put(Blocks.SPRUCE_SAPLING, renderLayer2);
		hashMap.put(Blocks.BIRCH_SAPLING, renderLayer2);
		hashMap.put(Blocks.JUNGLE_SAPLING, renderLayer2);
		hashMap.put(Blocks.ACACIA_SAPLING, renderLayer2);
		hashMap.put(Blocks.DARK_OAK_SAPLING, renderLayer2);
		hashMap.put(Blocks.GLASS, renderLayer2);
		hashMap.put(Blocks.WHITE_BED, renderLayer2);
		hashMap.put(Blocks.ORANGE_BED, renderLayer2);
		hashMap.put(Blocks.MAGENTA_BED, renderLayer2);
		hashMap.put(Blocks.LIGHT_BLUE_BED, renderLayer2);
		hashMap.put(Blocks.YELLOW_BED, renderLayer2);
		hashMap.put(Blocks.LIME_BED, renderLayer2);
		hashMap.put(Blocks.PINK_BED, renderLayer2);
		hashMap.put(Blocks.GRAY_BED, renderLayer2);
		hashMap.put(Blocks.LIGHT_GRAY_BED, renderLayer2);
		hashMap.put(Blocks.CYAN_BED, renderLayer2);
		hashMap.put(Blocks.PURPLE_BED, renderLayer2);
		hashMap.put(Blocks.BLUE_BED, renderLayer2);
		hashMap.put(Blocks.BROWN_BED, renderLayer2);
		hashMap.put(Blocks.GREEN_BED, renderLayer2);
		hashMap.put(Blocks.RED_BED, renderLayer2);
		hashMap.put(Blocks.BLACK_BED, renderLayer2);
		hashMap.put(Blocks.POWERED_RAIL, renderLayer2);
		hashMap.put(Blocks.DETECTOR_RAIL, renderLayer2);
		hashMap.put(Blocks.COBWEB, renderLayer2);
		hashMap.put(Blocks.GRASS, renderLayer2);
		hashMap.put(Blocks.FERN, renderLayer2);
		hashMap.put(Blocks.DEAD_BUSH, renderLayer2);
		hashMap.put(Blocks.SEAGRASS, renderLayer2);
		hashMap.put(Blocks.TALL_SEAGRASS, renderLayer2);
		hashMap.put(Blocks.DANDELION, renderLayer2);
		hashMap.put(Blocks.POPPY, renderLayer2);
		hashMap.put(Blocks.BLUE_ORCHID, renderLayer2);
		hashMap.put(Blocks.ALLIUM, renderLayer2);
		hashMap.put(Blocks.AZURE_BLUET, renderLayer2);
		hashMap.put(Blocks.RED_TULIP, renderLayer2);
		hashMap.put(Blocks.ORANGE_TULIP, renderLayer2);
		hashMap.put(Blocks.WHITE_TULIP, renderLayer2);
		hashMap.put(Blocks.PINK_TULIP, renderLayer2);
		hashMap.put(Blocks.OXEYE_DAISY, renderLayer2);
		hashMap.put(Blocks.CORNFLOWER, renderLayer2);
		hashMap.put(Blocks.WITHER_ROSE, renderLayer2);
		hashMap.put(Blocks.LILY_OF_THE_VALLEY, renderLayer2);
		hashMap.put(Blocks.BROWN_MUSHROOM, renderLayer2);
		hashMap.put(Blocks.RED_MUSHROOM, renderLayer2);
		hashMap.put(Blocks.TORCH, renderLayer2);
		hashMap.put(Blocks.WALL_TORCH, renderLayer2);
		hashMap.put(Blocks.SOUL_TORCH, renderLayer2);
		hashMap.put(Blocks.SOUL_WALL_TORCH, renderLayer2);
		hashMap.put(Blocks.FIRE, renderLayer2);
		hashMap.put(Blocks.SOUL_FIRE, renderLayer2);
		hashMap.put(Blocks.SPAWNER, renderLayer2);
		hashMap.put(Blocks.REDSTONE_WIRE, renderLayer2);
		hashMap.put(Blocks.WHEAT, renderLayer2);
		hashMap.put(Blocks.OAK_DOOR, renderLayer2);
		hashMap.put(Blocks.LADDER, renderLayer2);
		hashMap.put(Blocks.RAIL, renderLayer2);
		hashMap.put(Blocks.IRON_DOOR, renderLayer2);
		hashMap.put(Blocks.REDSTONE_TORCH, renderLayer2);
		hashMap.put(Blocks.REDSTONE_WALL_TORCH, renderLayer2);
		hashMap.put(Blocks.CACTUS, renderLayer2);
		hashMap.put(Blocks.SUGAR_CANE, renderLayer2);
		hashMap.put(Blocks.REPEATER, renderLayer2);
		hashMap.put(Blocks.OAK_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.SPRUCE_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.BIRCH_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.JUNGLE_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.ACACIA_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.DARK_OAK_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.CRIMSON_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.WARPED_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, renderLayer2);
		hashMap.put(Blocks.ATTACHED_MELON_STEM, renderLayer2);
		hashMap.put(Blocks.PUMPKIN_STEM, renderLayer2);
		hashMap.put(Blocks.MELON_STEM, renderLayer2);
		hashMap.put(Blocks.VINE, renderLayer2);
		hashMap.put(Blocks.LILY_PAD, renderLayer2);
		hashMap.put(Blocks.NETHER_WART, renderLayer2);
		hashMap.put(Blocks.BREWING_STAND, renderLayer2);
		hashMap.put(Blocks.COCOA, renderLayer2);
		hashMap.put(Blocks.BEACON, renderLayer2);
		hashMap.put(Blocks.FLOWER_POT, renderLayer2);
		hashMap.put(Blocks.POTTED_OAK_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_BIRCH_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_ACACIA_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, renderLayer2);
		hashMap.put(Blocks.POTTED_FERN, renderLayer2);
		hashMap.put(Blocks.POTTED_DANDELION, renderLayer2);
		hashMap.put(Blocks.POTTED_POPPY, renderLayer2);
		hashMap.put(Blocks.POTTED_BLUE_ORCHID, renderLayer2);
		hashMap.put(Blocks.POTTED_ALLIUM, renderLayer2);
		hashMap.put(Blocks.POTTED_AZURE_BLUET, renderLayer2);
		hashMap.put(Blocks.POTTED_RED_TULIP, renderLayer2);
		hashMap.put(Blocks.POTTED_ORANGE_TULIP, renderLayer2);
		hashMap.put(Blocks.POTTED_WHITE_TULIP, renderLayer2);
		hashMap.put(Blocks.POTTED_PINK_TULIP, renderLayer2);
		hashMap.put(Blocks.POTTED_OXEYE_DAISY, renderLayer2);
		hashMap.put(Blocks.POTTED_CORNFLOWER, renderLayer2);
		hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, renderLayer2);
		hashMap.put(Blocks.POTTED_WITHER_ROSE, renderLayer2);
		hashMap.put(Blocks.POTTED_RED_MUSHROOM, renderLayer2);
		hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, renderLayer2);
		hashMap.put(Blocks.POTTED_DEAD_BUSH, renderLayer2);
		hashMap.put(Blocks.POTTED_CACTUS, renderLayer2);
		hashMap.put(Blocks.CARROTS, renderLayer2);
		hashMap.put(Blocks.POTATOES, renderLayer2);
		hashMap.put(Blocks.COMPARATOR, renderLayer2);
		hashMap.put(Blocks.ACTIVATOR_RAIL, renderLayer2);
		hashMap.put(Blocks.IRON_TRAPDOOR, renderLayer2);
		hashMap.put(Blocks.SUNFLOWER, renderLayer2);
		hashMap.put(Blocks.LILAC, renderLayer2);
		hashMap.put(Blocks.ROSE_BUSH, renderLayer2);
		hashMap.put(Blocks.PEONY, renderLayer2);
		hashMap.put(Blocks.TALL_GRASS, renderLayer2);
		hashMap.put(Blocks.LARGE_FERN, renderLayer2);
		hashMap.put(Blocks.SPRUCE_DOOR, renderLayer2);
		hashMap.put(Blocks.BIRCH_DOOR, renderLayer2);
		hashMap.put(Blocks.JUNGLE_DOOR, renderLayer2);
		hashMap.put(Blocks.ACACIA_DOOR, renderLayer2);
		hashMap.put(Blocks.DARK_OAK_DOOR, renderLayer2);
		hashMap.put(Blocks.END_ROD, renderLayer2);
		hashMap.put(Blocks.CHORUS_PLANT, renderLayer2);
		hashMap.put(Blocks.CHORUS_FLOWER, renderLayer2);
		hashMap.put(Blocks.BEETROOTS, renderLayer2);
		hashMap.put(Blocks.KELP, renderLayer2);
		hashMap.put(Blocks.KELP_PLANT, renderLayer2);
		hashMap.put(Blocks.TURTLE_EGG, renderLayer2);
		hashMap.put(Blocks.DEAD_TUBE_CORAL, renderLayer2);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL, renderLayer2);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL, renderLayer2);
		hashMap.put(Blocks.DEAD_FIRE_CORAL, renderLayer2);
		hashMap.put(Blocks.DEAD_HORN_CORAL, renderLayer2);
		hashMap.put(Blocks.TUBE_CORAL, renderLayer2);
		hashMap.put(Blocks.BRAIN_CORAL, renderLayer2);
		hashMap.put(Blocks.BUBBLE_CORAL, renderLayer2);
		hashMap.put(Blocks.FIRE_CORAL, renderLayer2);
		hashMap.put(Blocks.HORN_CORAL, renderLayer2);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.TUBE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.BRAIN_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.BUBBLE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.FIRE_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.HORN_CORAL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.HORN_CORAL_WALL_FAN, renderLayer2);
		hashMap.put(Blocks.SEA_PICKLE, renderLayer2);
		hashMap.put(Blocks.CONDUIT, renderLayer2);
		hashMap.put(Blocks.BAMBOO_SAPLING, renderLayer2);
		hashMap.put(Blocks.BAMBOO, renderLayer2);
		hashMap.put(Blocks.POTTED_BAMBOO, renderLayer2);
		hashMap.put(Blocks.SCAFFOLDING, renderLayer2);
		hashMap.put(Blocks.STONECUTTER, renderLayer2);
		hashMap.put(Blocks.LANTERN, renderLayer2);
		hashMap.put(Blocks.SOUL_LANTERN, renderLayer2);
		hashMap.put(Blocks.CAMPFIRE, renderLayer2);
		hashMap.put(Blocks.SOUL_CAMPFIRE, renderLayer2);
		hashMap.put(Blocks.SWEET_BERRY_BUSH, renderLayer2);
		hashMap.put(Blocks.WEEPING_VINES, renderLayer2);
		hashMap.put(Blocks.WEEPING_VINES_PLANT, renderLayer2);
		hashMap.put(Blocks.TWISTING_VINES, renderLayer2);
		hashMap.put(Blocks.TWISTING_VINES_PLANT, renderLayer2);
		hashMap.put(Blocks.NETHER_SPROUTS, renderLayer2);
		hashMap.put(Blocks.CRIMSON_FUNGUS, renderLayer2);
		hashMap.put(Blocks.WARPED_FUNGUS, renderLayer2);
		hashMap.put(Blocks.CRIMSON_ROOTS, renderLayer2);
		hashMap.put(Blocks.WARPED_ROOTS, renderLayer2);
		hashMap.put(Blocks.POTTED_CRIMSON_FUNGUS, renderLayer2);
		hashMap.put(Blocks.POTTED_WARPED_FUNGUS, renderLayer2);
		hashMap.put(Blocks.POTTED_CRIMSON_ROOTS, renderLayer2);
		hashMap.put(Blocks.POTTED_WARPED_ROOTS, renderLayer2);
		hashMap.put(Blocks.CRIMSON_DOOR, renderLayer2);
		hashMap.put(Blocks.WARPED_DOOR, renderLayer2);
		RenderLayer renderLayer3 = RenderLayer.getTranslucent();
		hashMap.put(Blocks.ICE, renderLayer3);
		hashMap.put(Blocks.NETHER_PORTAL, renderLayer3);
		hashMap.put(Blocks.WHITE_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.LIME_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.PINK_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.GRAY_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.CYAN_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.BLUE_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.BROWN_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.GREEN_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.RED_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.BLACK_STAINED_GLASS, renderLayer3);
		hashMap.put(Blocks.TRIPWIRE, renderLayer3);
		hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.RED_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, renderLayer3);
		hashMap.put(Blocks.SLIME_BLOCK, renderLayer3);
		hashMap.put(Blocks.HONEY_BLOCK, renderLayer3);
		hashMap.put(Blocks.FROSTED_ICE, renderLayer3);
		hashMap.put(Blocks.BUBBLE_COLUMN, renderLayer3);
	});
	private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.<Fluid, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTranslucent();
		hashMap.put(Fluids.FLOWING_WATER, renderLayer);
		hashMap.put(Fluids.WATER, renderLayer);
	});
	private static boolean fancyGraphics;

	public static RenderLayer getBlockLayer(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphics ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			return renderLayer != null ? renderLayer : RenderLayer.getSolid();
		}
	}

	public static RenderLayer method_29359(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return fancyGraphics ? RenderLayer.getCutoutMipped() : RenderLayer.getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)BLOCKS.get(block);
			if (renderLayer != null) {
				return renderLayer == RenderLayer.getTranslucent() ? RenderLayer.method_29380() : renderLayer;
			} else {
				return RenderLayer.getSolid();
			}
		}
	}

	public static RenderLayer getEntityBlockLayer(BlockState state, boolean bl) {
		RenderLayer renderLayer = getBlockLayer(state);
		if (renderLayer == RenderLayer.getTranslucent()) {
			return bl ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.method_29382();
		} else {
			return TexturedRenderLayers.getEntityCutout();
		}
	}

	public static RenderLayer getItemLayer(ItemStack stack, boolean bl) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			return getEntityBlockLayer(block.getDefaultState(), bl);
		} else {
			return bl ? TexturedRenderLayers.getEntityTranslucentCull() : TexturedRenderLayers.method_29382();
		}
	}

	public static RenderLayer getFluidLayer(FluidState state) {
		RenderLayer renderLayer = (RenderLayer)FLUIDS.get(state.getFluid());
		return renderLayer != null ? renderLayer : RenderLayer.getSolid();
	}

	public static void setFancyGraphics(boolean fancyGraphics) {
		RenderLayers.fancyGraphics = fancyGraphics;
	}
}
