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
	private static final Map<Block, RenderLayer> BLOCKS = Util.make(Maps.<Block, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTripwire();
		hashMap.put(Blocks.TRIPWIRE, renderLayer);
		RenderLayer renderLayer2 = RenderLayer.getCutoutMipped();
		hashMap.put(Blocks.GRASS_BLOCK, renderLayer2);
		hashMap.put(Blocks.IRON_BARS, renderLayer2);
		hashMap.put(Blocks.GLASS_PANE, renderLayer2);
		hashMap.put(Blocks.TRIPWIRE_HOOK, renderLayer2);
		hashMap.put(Blocks.HOPPER, renderLayer2);
		hashMap.put(Blocks.CHAIN, renderLayer2);
		hashMap.put(Blocks.JUNGLE_LEAVES, renderLayer2);
		hashMap.put(Blocks.OAK_LEAVES, renderLayer2);
		hashMap.put(Blocks.SPRUCE_LEAVES, renderLayer2);
		hashMap.put(Blocks.ACACIA_LEAVES, renderLayer2);
		hashMap.put(Blocks.BIRCH_LEAVES, renderLayer2);
		hashMap.put(Blocks.DARK_OAK_LEAVES, renderLayer2);
		hashMap.put(Blocks.AZALEA_LEAVES, renderLayer2);
		hashMap.put(Blocks.AZALEA_LEAVES_FLOWERS, renderLayer2);
		RenderLayer renderLayer3 = RenderLayer.getCutout();
		hashMap.put(Blocks.OAK_SAPLING, renderLayer3);
		hashMap.put(Blocks.SPRUCE_SAPLING, renderLayer3);
		hashMap.put(Blocks.BIRCH_SAPLING, renderLayer3);
		hashMap.put(Blocks.JUNGLE_SAPLING, renderLayer3);
		hashMap.put(Blocks.ACACIA_SAPLING, renderLayer3);
		hashMap.put(Blocks.DARK_OAK_SAPLING, renderLayer3);
		hashMap.put(Blocks.GLASS, renderLayer3);
		hashMap.put(Blocks.WHITE_BED, renderLayer3);
		hashMap.put(Blocks.ORANGE_BED, renderLayer3);
		hashMap.put(Blocks.MAGENTA_BED, renderLayer3);
		hashMap.put(Blocks.LIGHT_BLUE_BED, renderLayer3);
		hashMap.put(Blocks.YELLOW_BED, renderLayer3);
		hashMap.put(Blocks.LIME_BED, renderLayer3);
		hashMap.put(Blocks.PINK_BED, renderLayer3);
		hashMap.put(Blocks.GRAY_BED, renderLayer3);
		hashMap.put(Blocks.LIGHT_GRAY_BED, renderLayer3);
		hashMap.put(Blocks.CYAN_BED, renderLayer3);
		hashMap.put(Blocks.PURPLE_BED, renderLayer3);
		hashMap.put(Blocks.BLUE_BED, renderLayer3);
		hashMap.put(Blocks.BROWN_BED, renderLayer3);
		hashMap.put(Blocks.GREEN_BED, renderLayer3);
		hashMap.put(Blocks.RED_BED, renderLayer3);
		hashMap.put(Blocks.BLACK_BED, renderLayer3);
		hashMap.put(Blocks.POWERED_RAIL, renderLayer3);
		hashMap.put(Blocks.DETECTOR_RAIL, renderLayer3);
		hashMap.put(Blocks.COBWEB, renderLayer3);
		hashMap.put(Blocks.GRASS, renderLayer3);
		hashMap.put(Blocks.FERN, renderLayer3);
		hashMap.put(Blocks.DEAD_BUSH, renderLayer3);
		hashMap.put(Blocks.SEAGRASS, renderLayer3);
		hashMap.put(Blocks.TALL_SEAGRASS, renderLayer3);
		hashMap.put(Blocks.DANDELION, renderLayer3);
		hashMap.put(Blocks.POPPY, renderLayer3);
		hashMap.put(Blocks.BLUE_ORCHID, renderLayer3);
		hashMap.put(Blocks.ALLIUM, renderLayer3);
		hashMap.put(Blocks.AZURE_BLUET, renderLayer3);
		hashMap.put(Blocks.RED_TULIP, renderLayer3);
		hashMap.put(Blocks.ORANGE_TULIP, renderLayer3);
		hashMap.put(Blocks.WHITE_TULIP, renderLayer3);
		hashMap.put(Blocks.PINK_TULIP, renderLayer3);
		hashMap.put(Blocks.OXEYE_DAISY, renderLayer3);
		hashMap.put(Blocks.CORNFLOWER, renderLayer3);
		hashMap.put(Blocks.WITHER_ROSE, renderLayer3);
		hashMap.put(Blocks.LILY_OF_THE_VALLEY, renderLayer3);
		hashMap.put(Blocks.BROWN_MUSHROOM, renderLayer3);
		hashMap.put(Blocks.RED_MUSHROOM, renderLayer3);
		hashMap.put(Blocks.TORCH, renderLayer3);
		hashMap.put(Blocks.WALL_TORCH, renderLayer3);
		hashMap.put(Blocks.SOUL_TORCH, renderLayer3);
		hashMap.put(Blocks.SOUL_WALL_TORCH, renderLayer3);
		hashMap.put(Blocks.FIRE, renderLayer3);
		hashMap.put(Blocks.SOUL_FIRE, renderLayer3);
		hashMap.put(Blocks.SPAWNER, renderLayer3);
		hashMap.put(Blocks.REDSTONE_WIRE, renderLayer3);
		hashMap.put(Blocks.WHEAT, renderLayer3);
		hashMap.put(Blocks.OAK_DOOR, renderLayer3);
		hashMap.put(Blocks.LADDER, renderLayer3);
		hashMap.put(Blocks.RAIL, renderLayer3);
		hashMap.put(Blocks.IRON_DOOR, renderLayer3);
		hashMap.put(Blocks.REDSTONE_TORCH, renderLayer3);
		hashMap.put(Blocks.REDSTONE_WALL_TORCH, renderLayer3);
		hashMap.put(Blocks.CACTUS, renderLayer3);
		hashMap.put(Blocks.SUGAR_CANE, renderLayer3);
		hashMap.put(Blocks.REPEATER, renderLayer3);
		hashMap.put(Blocks.OAK_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.SPRUCE_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.BIRCH_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.JUNGLE_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.ACACIA_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.DARK_OAK_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.CRIMSON_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.WARPED_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, renderLayer3);
		hashMap.put(Blocks.ATTACHED_MELON_STEM, renderLayer3);
		hashMap.put(Blocks.PUMPKIN_STEM, renderLayer3);
		hashMap.put(Blocks.MELON_STEM, renderLayer3);
		hashMap.put(Blocks.VINE, renderLayer3);
		hashMap.put(Blocks.GLOW_LICHEN, renderLayer3);
		hashMap.put(Blocks.LILY_PAD, renderLayer3);
		hashMap.put(Blocks.NETHER_WART, renderLayer3);
		hashMap.put(Blocks.BREWING_STAND, renderLayer3);
		hashMap.put(Blocks.COCOA, renderLayer3);
		hashMap.put(Blocks.BEACON, renderLayer3);
		hashMap.put(Blocks.FLOWER_POT, renderLayer3);
		hashMap.put(Blocks.POTTED_OAK_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_BIRCH_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_ACACIA_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, renderLayer3);
		hashMap.put(Blocks.POTTED_FERN, renderLayer3);
		hashMap.put(Blocks.POTTED_DANDELION, renderLayer3);
		hashMap.put(Blocks.POTTED_POPPY, renderLayer3);
		hashMap.put(Blocks.POTTED_BLUE_ORCHID, renderLayer3);
		hashMap.put(Blocks.POTTED_ALLIUM, renderLayer3);
		hashMap.put(Blocks.POTTED_AZURE_BLUET, renderLayer3);
		hashMap.put(Blocks.POTTED_RED_TULIP, renderLayer3);
		hashMap.put(Blocks.POTTED_ORANGE_TULIP, renderLayer3);
		hashMap.put(Blocks.POTTED_WHITE_TULIP, renderLayer3);
		hashMap.put(Blocks.POTTED_PINK_TULIP, renderLayer3);
		hashMap.put(Blocks.POTTED_OXEYE_DAISY, renderLayer3);
		hashMap.put(Blocks.POTTED_CORNFLOWER, renderLayer3);
		hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, renderLayer3);
		hashMap.put(Blocks.POTTED_WITHER_ROSE, renderLayer3);
		hashMap.put(Blocks.POTTED_RED_MUSHROOM, renderLayer3);
		hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, renderLayer3);
		hashMap.put(Blocks.POTTED_DEAD_BUSH, renderLayer3);
		hashMap.put(Blocks.POTTED_CACTUS, renderLayer3);
		hashMap.put(Blocks.CARROTS, renderLayer3);
		hashMap.put(Blocks.POTATOES, renderLayer3);
		hashMap.put(Blocks.COMPARATOR, renderLayer3);
		hashMap.put(Blocks.ACTIVATOR_RAIL, renderLayer3);
		hashMap.put(Blocks.IRON_TRAPDOOR, renderLayer3);
		hashMap.put(Blocks.SUNFLOWER, renderLayer3);
		hashMap.put(Blocks.LILAC, renderLayer3);
		hashMap.put(Blocks.ROSE_BUSH, renderLayer3);
		hashMap.put(Blocks.PEONY, renderLayer3);
		hashMap.put(Blocks.TALL_GRASS, renderLayer3);
		hashMap.put(Blocks.LARGE_FERN, renderLayer3);
		hashMap.put(Blocks.SPRUCE_DOOR, renderLayer3);
		hashMap.put(Blocks.BIRCH_DOOR, renderLayer3);
		hashMap.put(Blocks.JUNGLE_DOOR, renderLayer3);
		hashMap.put(Blocks.ACACIA_DOOR, renderLayer3);
		hashMap.put(Blocks.DARK_OAK_DOOR, renderLayer3);
		hashMap.put(Blocks.END_ROD, renderLayer3);
		hashMap.put(Blocks.CHORUS_PLANT, renderLayer3);
		hashMap.put(Blocks.CHORUS_FLOWER, renderLayer3);
		hashMap.put(Blocks.BEETROOTS, renderLayer3);
		hashMap.put(Blocks.KELP, renderLayer3);
		hashMap.put(Blocks.KELP_PLANT, renderLayer3);
		hashMap.put(Blocks.TURTLE_EGG, renderLayer3);
		hashMap.put(Blocks.DEAD_TUBE_CORAL, renderLayer3);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL, renderLayer3);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL, renderLayer3);
		hashMap.put(Blocks.DEAD_FIRE_CORAL, renderLayer3);
		hashMap.put(Blocks.DEAD_HORN_CORAL, renderLayer3);
		hashMap.put(Blocks.TUBE_CORAL, renderLayer3);
		hashMap.put(Blocks.BRAIN_CORAL, renderLayer3);
		hashMap.put(Blocks.BUBBLE_CORAL, renderLayer3);
		hashMap.put(Blocks.FIRE_CORAL, renderLayer3);
		hashMap.put(Blocks.HORN_CORAL, renderLayer3);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.TUBE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.BRAIN_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.BUBBLE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.FIRE_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.HORN_CORAL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.HORN_CORAL_WALL_FAN, renderLayer3);
		hashMap.put(Blocks.SEA_PICKLE, renderLayer3);
		hashMap.put(Blocks.CONDUIT, renderLayer3);
		hashMap.put(Blocks.BAMBOO_SAPLING, renderLayer3);
		hashMap.put(Blocks.BAMBOO, renderLayer3);
		hashMap.put(Blocks.POTTED_BAMBOO, renderLayer3);
		hashMap.put(Blocks.SCAFFOLDING, renderLayer3);
		hashMap.put(Blocks.STONECUTTER, renderLayer3);
		hashMap.put(Blocks.LANTERN, renderLayer3);
		hashMap.put(Blocks.SOUL_LANTERN, renderLayer3);
		hashMap.put(Blocks.CAMPFIRE, renderLayer3);
		hashMap.put(Blocks.SOUL_CAMPFIRE, renderLayer3);
		hashMap.put(Blocks.SWEET_BERRY_BUSH, renderLayer3);
		hashMap.put(Blocks.WEEPING_VINES, renderLayer3);
		hashMap.put(Blocks.WEEPING_VINES_PLANT, renderLayer3);
		hashMap.put(Blocks.TWISTING_VINES, renderLayer3);
		hashMap.put(Blocks.TWISTING_VINES_PLANT, renderLayer3);
		hashMap.put(Blocks.NETHER_SPROUTS, renderLayer3);
		hashMap.put(Blocks.CRIMSON_FUNGUS, renderLayer3);
		hashMap.put(Blocks.WARPED_FUNGUS, renderLayer3);
		hashMap.put(Blocks.CRIMSON_ROOTS, renderLayer3);
		hashMap.put(Blocks.WARPED_ROOTS, renderLayer3);
		hashMap.put(Blocks.POTTED_CRIMSON_FUNGUS, renderLayer3);
		hashMap.put(Blocks.POTTED_WARPED_FUNGUS, renderLayer3);
		hashMap.put(Blocks.POTTED_CRIMSON_ROOTS, renderLayer3);
		hashMap.put(Blocks.POTTED_WARPED_ROOTS, renderLayer3);
		hashMap.put(Blocks.CRIMSON_DOOR, renderLayer3);
		hashMap.put(Blocks.WARPED_DOOR, renderLayer3);
		hashMap.put(Blocks.POINTED_DRIPSTONE, renderLayer3);
		hashMap.put(Blocks.SMALL_AMETHYST_BUD, renderLayer3);
		hashMap.put(Blocks.MEDIUM_AMETHYST_BUD, renderLayer3);
		hashMap.put(Blocks.LARGE_AMETHYST_BUD, renderLayer3);
		hashMap.put(Blocks.AMETHYST_CLUSTER, renderLayer3);
		hashMap.put(Blocks.LIGHTNING_ROD, renderLayer3);
		hashMap.put(Blocks.CAVE_VINES_HEAD, renderLayer3);
		hashMap.put(Blocks.CAVE_VINES_BODY, renderLayer3);
		hashMap.put(Blocks.SPORE_BLOSSOM, renderLayer3);
		hashMap.put(Blocks.FLOWERING_AZALEA, renderLayer3);
		hashMap.put(Blocks.AZALEA, renderLayer3);
		hashMap.put(Blocks.MOSS_CARPET, renderLayer3);
		hashMap.put(Blocks.BIG_DRIPLEAF, renderLayer3);
		hashMap.put(Blocks.BIG_DRIPLEAF_STEM, renderLayer3);
		hashMap.put(Blocks.SMALL_DRIPLEAF, renderLayer3);
		hashMap.put(Blocks.HANGING_ROOTS, renderLayer3);
		hashMap.put(Blocks.SCULK_SENSOR, renderLayer3);
		RenderLayer renderLayer4 = RenderLayer.getTranslucent();
		hashMap.put(Blocks.ICE, renderLayer4);
		hashMap.put(Blocks.NETHER_PORTAL, renderLayer4);
		hashMap.put(Blocks.WHITE_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.LIME_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.PINK_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.GRAY_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.CYAN_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.BLUE_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.BROWN_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.GREEN_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.RED_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.BLACK_STAINED_GLASS, renderLayer4);
		hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.RED_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, renderLayer4);
		hashMap.put(Blocks.SLIME_BLOCK, renderLayer4);
		hashMap.put(Blocks.HONEY_BLOCK, renderLayer4);
		hashMap.put(Blocks.FROSTED_ICE, renderLayer4);
		hashMap.put(Blocks.BUBBLE_COLUMN, renderLayer4);
		hashMap.put(Blocks.TINTED_GLASS, renderLayer4);
	});
	private static final Map<Fluid, RenderLayer> FLUIDS = Util.make(Maps.<Fluid, RenderLayer>newHashMap(), hashMap -> {
		RenderLayer renderLayer = RenderLayer.getTranslucent();
		hashMap.put(Fluids.FLOWING_WATER, renderLayer);
		hashMap.put(Fluids.WATER, renderLayer);
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
