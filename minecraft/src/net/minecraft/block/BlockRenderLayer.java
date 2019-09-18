package net.minecraft.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class BlockRenderLayer {
	private static final Set<BlockRenderLayer> field_20801 = Sets.<BlockRenderLayer>newHashSet();
	public static final BlockRenderLayer field_9178 = method_22717(new BlockRenderLayer("solid", 2097152, () -> {
	}, () -> {
	}));
	public static final BlockRenderLayer CUTOUT_MIPPED = method_22717(new BlockRenderLayer("cutout_mipped", 131072, () -> {
		RenderSystem.enableAlphaTest();
		RenderSystem.alphaFunc(516, 0.5F);
	}, () -> {
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultAlphaFunc();
	}));
	public static final BlockRenderLayer field_9174 = method_22717(new BlockRenderLayer("cutout", 131072, () -> {
		Texture texture = MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		texture.bindTexture();
		texture.pushFilter(false, false);
		CUTOUT_MIPPED.method_22723();
	}, () -> {
		Texture texture = MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		texture.bindTexture();
		texture.popFilter();
		CUTOUT_MIPPED.method_22724();
	}));
	public static final BlockRenderLayer field_9179 = method_22717(new BlockRenderLayer("translucent", 262144, () -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
	}));
	public static final BlockRenderLayer field_20799 = method_22717(new BlockRenderLayer("entity", 262144, () -> {
		Texture texture = MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		texture.bindTexture();
		GuiLighting.disable();
		RenderSystem.blendFunc(770, 771);
		RenderSystem.enableBlend();
		if (MinecraftClient.isAmbientOcclusionEnabled()) {
			RenderSystem.shadeModel(7425);
		} else {
			RenderSystem.shadeModel(7424);
		}
	}, () -> GuiLighting.enable()));
	public static final BlockRenderLayer field_20800 = method_22717(
		new BlockRenderLayer(
			"crumbling",
			262144,
			() -> {
				RenderSystem.polygonOffset(-1.0F, -10.0F);
				RenderSystem.enablePolygonOffset();
				RenderSystem.defaultAlphaFunc();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(
					GlStateManager.class_4535.DST_COLOR, GlStateManager.class_4534.SRC_COLOR, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
				);
			},
			() -> {
				RenderSystem.disableAlphaTest();
				RenderSystem.polygonOffset(0.0F, 0.0F);
				RenderSystem.disablePolygonOffset();
				RenderSystem.enableAlphaTest();
				RenderSystem.disableBlend();
			}
		)
	);
	private static boolean field_20802;
	private static final Map<Block, BlockRenderLayer> field_20803 = SystemUtil.consume(Maps.<Block, BlockRenderLayer>newHashMap(), hashMap -> {
		hashMap.put(Blocks.GRASS_BLOCK, CUTOUT_MIPPED);
		hashMap.put(Blocks.IRON_BARS, CUTOUT_MIPPED);
		hashMap.put(Blocks.GLASS_PANE, CUTOUT_MIPPED);
		hashMap.put(Blocks.TRIPWIRE_HOOK, CUTOUT_MIPPED);
		hashMap.put(Blocks.HOPPER, CUTOUT_MIPPED);
		hashMap.put(Blocks.JUNGLE_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.OAK_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.SPRUCE_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.ACACIA_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.BIRCH_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.DARK_OAK_LEAVES, CUTOUT_MIPPED);
		hashMap.put(Blocks.OAK_SAPLING, field_9174);
		hashMap.put(Blocks.SPRUCE_SAPLING, field_9174);
		hashMap.put(Blocks.BIRCH_SAPLING, field_9174);
		hashMap.put(Blocks.JUNGLE_SAPLING, field_9174);
		hashMap.put(Blocks.ACACIA_SAPLING, field_9174);
		hashMap.put(Blocks.DARK_OAK_SAPLING, field_9174);
		hashMap.put(Blocks.GLASS, field_9174);
		hashMap.put(Blocks.WHITE_BED, field_9174);
		hashMap.put(Blocks.ORANGE_BED, field_9174);
		hashMap.put(Blocks.MAGENTA_BED, field_9174);
		hashMap.put(Blocks.LIGHT_BLUE_BED, field_9174);
		hashMap.put(Blocks.YELLOW_BED, field_9174);
		hashMap.put(Blocks.LIME_BED, field_9174);
		hashMap.put(Blocks.PINK_BED, field_9174);
		hashMap.put(Blocks.GRAY_BED, field_9174);
		hashMap.put(Blocks.LIGHT_GRAY_BED, field_9174);
		hashMap.put(Blocks.CYAN_BED, field_9174);
		hashMap.put(Blocks.PURPLE_BED, field_9174);
		hashMap.put(Blocks.BLUE_BED, field_9174);
		hashMap.put(Blocks.BROWN_BED, field_9174);
		hashMap.put(Blocks.GREEN_BED, field_9174);
		hashMap.put(Blocks.RED_BED, field_9174);
		hashMap.put(Blocks.BLACK_BED, field_9174);
		hashMap.put(Blocks.POWERED_RAIL, field_9174);
		hashMap.put(Blocks.DETECTOR_RAIL, field_9174);
		hashMap.put(Blocks.COBWEB, field_9174);
		hashMap.put(Blocks.GRASS, field_9174);
		hashMap.put(Blocks.FERN, field_9174);
		hashMap.put(Blocks.DEAD_BUSH, field_9174);
		hashMap.put(Blocks.SEAGRASS, field_9174);
		hashMap.put(Blocks.TALL_SEAGRASS, field_9174);
		hashMap.put(Blocks.DANDELION, field_9174);
		hashMap.put(Blocks.POPPY, field_9174);
		hashMap.put(Blocks.BLUE_ORCHID, field_9174);
		hashMap.put(Blocks.ALLIUM, field_9174);
		hashMap.put(Blocks.AZURE_BLUET, field_9174);
		hashMap.put(Blocks.RED_TULIP, field_9174);
		hashMap.put(Blocks.ORANGE_TULIP, field_9174);
		hashMap.put(Blocks.WHITE_TULIP, field_9174);
		hashMap.put(Blocks.PINK_TULIP, field_9174);
		hashMap.put(Blocks.OXEYE_DAISY, field_9174);
		hashMap.put(Blocks.CORNFLOWER, field_9174);
		hashMap.put(Blocks.WITHER_ROSE, field_9174);
		hashMap.put(Blocks.LILY_OF_THE_VALLEY, field_9174);
		hashMap.put(Blocks.BROWN_MUSHROOM, field_9174);
		hashMap.put(Blocks.RED_MUSHROOM, field_9174);
		hashMap.put(Blocks.TORCH, field_9174);
		hashMap.put(Blocks.WALL_TORCH, field_9174);
		hashMap.put(Blocks.FIRE, field_9174);
		hashMap.put(Blocks.SPAWNER, field_9174);
		hashMap.put(Blocks.REDSTONE_WIRE, field_9174);
		hashMap.put(Blocks.WHEAT, field_9174);
		hashMap.put(Blocks.OAK_DOOR, field_9174);
		hashMap.put(Blocks.LADDER, field_9174);
		hashMap.put(Blocks.RAIL, field_9174);
		hashMap.put(Blocks.IRON_DOOR, field_9174);
		hashMap.put(Blocks.REDSTONE_TORCH, field_9174);
		hashMap.put(Blocks.REDSTONE_WALL_TORCH, field_9174);
		hashMap.put(Blocks.CACTUS, field_9174);
		hashMap.put(Blocks.SUGAR_CANE, field_9174);
		hashMap.put(Blocks.REPEATER, field_9174);
		hashMap.put(Blocks.OAK_TRAPDOOR, field_9174);
		hashMap.put(Blocks.SPRUCE_TRAPDOOR, field_9174);
		hashMap.put(Blocks.BIRCH_TRAPDOOR, field_9174);
		hashMap.put(Blocks.JUNGLE_TRAPDOOR, field_9174);
		hashMap.put(Blocks.ACACIA_TRAPDOOR, field_9174);
		hashMap.put(Blocks.DARK_OAK_TRAPDOOR, field_9174);
		hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, field_9174);
		hashMap.put(Blocks.ATTACHED_MELON_STEM, field_9174);
		hashMap.put(Blocks.PUMPKIN_STEM, field_9174);
		hashMap.put(Blocks.MELON_STEM, field_9174);
		hashMap.put(Blocks.VINE, field_9174);
		hashMap.put(Blocks.LILY_PAD, field_9174);
		hashMap.put(Blocks.NETHER_WART, field_9174);
		hashMap.put(Blocks.BREWING_STAND, field_9174);
		hashMap.put(Blocks.COCOA, field_9174);
		hashMap.put(Blocks.BEACON, field_9174);
		hashMap.put(Blocks.FLOWER_POT, field_9174);
		hashMap.put(Blocks.POTTED_OAK_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_BIRCH_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_ACACIA_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, field_9174);
		hashMap.put(Blocks.POTTED_FERN, field_9174);
		hashMap.put(Blocks.POTTED_DANDELION, field_9174);
		hashMap.put(Blocks.POTTED_POPPY, field_9174);
		hashMap.put(Blocks.POTTED_BLUE_ORCHID, field_9174);
		hashMap.put(Blocks.POTTED_ALLIUM, field_9174);
		hashMap.put(Blocks.POTTED_AZURE_BLUET, field_9174);
		hashMap.put(Blocks.POTTED_RED_TULIP, field_9174);
		hashMap.put(Blocks.POTTED_ORANGE_TULIP, field_9174);
		hashMap.put(Blocks.POTTED_WHITE_TULIP, field_9174);
		hashMap.put(Blocks.POTTED_PINK_TULIP, field_9174);
		hashMap.put(Blocks.POTTED_OXEYE_DAISY, field_9174);
		hashMap.put(Blocks.POTTED_CORNFLOWER, field_9174);
		hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, field_9174);
		hashMap.put(Blocks.POTTED_WITHER_ROSE, field_9174);
		hashMap.put(Blocks.POTTED_RED_MUSHROOM, field_9174);
		hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, field_9174);
		hashMap.put(Blocks.POTTED_DEAD_BUSH, field_9174);
		hashMap.put(Blocks.POTTED_CACTUS, field_9174);
		hashMap.put(Blocks.CARROTS, field_9174);
		hashMap.put(Blocks.POTATOES, field_9174);
		hashMap.put(Blocks.COMPARATOR, field_9174);
		hashMap.put(Blocks.ACTIVATOR_RAIL, field_9174);
		hashMap.put(Blocks.IRON_TRAPDOOR, field_9174);
		hashMap.put(Blocks.SUNFLOWER, field_9174);
		hashMap.put(Blocks.LILAC, field_9174);
		hashMap.put(Blocks.ROSE_BUSH, field_9174);
		hashMap.put(Blocks.PEONY, field_9174);
		hashMap.put(Blocks.TALL_GRASS, field_9174);
		hashMap.put(Blocks.LARGE_FERN, field_9174);
		hashMap.put(Blocks.SPRUCE_DOOR, field_9174);
		hashMap.put(Blocks.BIRCH_DOOR, field_9174);
		hashMap.put(Blocks.JUNGLE_DOOR, field_9174);
		hashMap.put(Blocks.ACACIA_DOOR, field_9174);
		hashMap.put(Blocks.DARK_OAK_DOOR, field_9174);
		hashMap.put(Blocks.END_ROD, field_9174);
		hashMap.put(Blocks.CHORUS_PLANT, field_9174);
		hashMap.put(Blocks.CHORUS_FLOWER, field_9174);
		hashMap.put(Blocks.BEETROOTS, field_9174);
		hashMap.put(Blocks.KELP, field_9174);
		hashMap.put(Blocks.KELP_PLANT, field_9174);
		hashMap.put(Blocks.TURTLE_EGG, field_9174);
		hashMap.put(Blocks.DEAD_TUBE_CORAL, field_9174);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL, field_9174);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL, field_9174);
		hashMap.put(Blocks.DEAD_FIRE_CORAL, field_9174);
		hashMap.put(Blocks.DEAD_HORN_CORAL, field_9174);
		hashMap.put(Blocks.TUBE_CORAL, field_9174);
		hashMap.put(Blocks.BRAIN_CORAL, field_9174);
		hashMap.put(Blocks.BUBBLE_CORAL, field_9174);
		hashMap.put(Blocks.FIRE_CORAL, field_9174);
		hashMap.put(Blocks.HORN_CORAL, field_9174);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, field_9174);
		hashMap.put(Blocks.TUBE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.BRAIN_CORAL_FAN, field_9174);
		hashMap.put(Blocks.BUBBLE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.FIRE_CORAL_FAN, field_9174);
		hashMap.put(Blocks.HORN_CORAL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.HORN_CORAL_WALL_FAN, field_9174);
		hashMap.put(Blocks.SEA_PICKLE, field_9174);
		hashMap.put(Blocks.CONDUIT, field_9174);
		hashMap.put(Blocks.BAMBOO_SAPLING, field_9174);
		hashMap.put(Blocks.BAMBOO, field_9174);
		hashMap.put(Blocks.POTTED_BAMBOO, field_9174);
		hashMap.put(Blocks.SCAFFOLDING, field_9174);
		hashMap.put(Blocks.STONECUTTER, field_9174);
		hashMap.put(Blocks.LANTERN, field_9174);
		hashMap.put(Blocks.CAMPFIRE, field_9174);
		hashMap.put(Blocks.SWEET_BERRY_BUSH, field_9174);
		hashMap.put(Blocks.ICE, field_9179);
		hashMap.put(Blocks.NETHER_PORTAL, field_9179);
		hashMap.put(Blocks.WHITE_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.LIME_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.PINK_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.GRAY_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.CYAN_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.BLUE_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.BROWN_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.GREEN_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.RED_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.BLACK_STAINED_GLASS, field_9179);
		hashMap.put(Blocks.TRIPWIRE, field_9179);
		hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.RED_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, field_9179);
		hashMap.put(Blocks.SLIME_BLOCK, field_9179);
		hashMap.put(Blocks.FROSTED_ICE, field_9179);
		hashMap.put(Blocks.BUBBLE_COLUMN, field_9179);
	});
	private static final Map<Fluid, BlockRenderLayer> field_20804 = SystemUtil.consume(Maps.<Fluid, BlockRenderLayer>newHashMap(), hashMap -> {
		hashMap.put(Fluids.FLOWING_WATER, field_9179);
		hashMap.put(Fluids.WATER, field_9179);
	});
	private final String field_20805;
	private final int field_20806;
	private final Runnable field_20807;
	private final Runnable field_20808;

	private static BlockRenderLayer method_22717(BlockRenderLayer blockRenderLayer) {
		field_20801.add(blockRenderLayer);
		return blockRenderLayer;
	}

	BlockRenderLayer(String string, int i, Runnable runnable, Runnable runnable2) {
		this.field_20805 = string;
		this.field_20806 = i;
		this.field_20807 = runnable;
		this.field_20808 = runnable2;
	}

	public static void method_22719(boolean bl) {
		field_20802 = bl;
	}

	public String toString() {
		return this.field_20805;
	}

	public static BlockRenderLayer method_22715(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return field_20802 ? CUTOUT_MIPPED : field_9178;
		} else {
			BlockRenderLayer blockRenderLayer = (BlockRenderLayer)field_20803.get(block);
			return blockRenderLayer != null ? blockRenderLayer : field_9178;
		}
	}

	public static BlockRenderLayer method_22716(FluidState fluidState) {
		BlockRenderLayer blockRenderLayer = (BlockRenderLayer)field_20804.get(fluidState.getFluid());
		return blockRenderLayer != null ? blockRenderLayer : field_9178;
	}

	public static Set<BlockRenderLayer> method_22720() {
		return ImmutableSet.of(field_9178, CUTOUT_MIPPED, field_9174, field_9179);
	}

	public int method_22722() {
		return this.field_20806;
	}

	public void method_22723() {
		this.field_20807.run();
	}

	public void method_22724() {
		this.field_20808.run();
	}
}
