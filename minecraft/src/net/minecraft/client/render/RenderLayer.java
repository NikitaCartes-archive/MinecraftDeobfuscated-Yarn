package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class RenderLayer {
	public static final RenderLayer SOLID = new RenderLayer("solid", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 2097152, true, () -> {
		RenderSystem.enableTexture();
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, true);
		RenderSystem.enableCull();
		RenderSystem.shadeModel(7425);
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableBlend();
		RenderSystem.depthFunc(515);
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
	}, () -> {
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
		RenderSystem.shadeModel(7424);
	});
	public static final RenderLayer CUTOUT_MIPPED = new RenderLayer("cutout_mipped", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 131072, true, () -> {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, true);
		RenderSystem.enableAlphaTest();
		RenderSystem.alphaFunc(516, 0.5F);
		RenderSystem.disableBlend();
		RenderSystem.shadeModel(7425);
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
	}, () -> {
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
		RenderSystem.shadeModel(7424);
	});
	public static final RenderLayer CUTOUT = new RenderLayer("cutout", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 131072, true, () -> {
		CUTOUT_MIPPED.begin();
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
	}, () -> CUTOUT_MIPPED.end());
	public static final RenderLayer TRANSLUCENT = new RenderLayer("translucent", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 262144, true, () -> {
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
	}, () -> {
		RenderSystem.disableBlend();
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
		RenderSystem.shadeModel(7424);
	});
	public static final RenderLayer TRANSLUCENT_NO_CRUMBLING = new RenderLayer(
		"translucent_no_crumbling", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 256, false, TRANSLUCENT::begin, TRANSLUCENT::end
	);
	public static final RenderLayer LEASH = new RenderLayer("leash", VertexFormats.POSITION_COLOR, 7, 256, false, () -> {
		RenderSystem.disableTexture();
		RenderSystem.disableCull();
	}, () -> {
		RenderSystem.enableTexture();
		RenderSystem.enableCull();
	});
	public static final RenderLayer WATER_MASK = new RenderLayer("water_mask", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 256, false, () -> {
		RenderSystem.disableTexture();
		RenderSystem.colorMask(false, false, false, false);
	}, () -> {
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableTexture();
	});
	public static final RenderLayer GLINT = new RenderLayer("glint", VertexFormats.POSITION_UV, 7, 256, false, () -> method_23010(8.0F), () -> method_23039());
	public static final RenderLayer ENTITY_GLINT = new RenderLayer(
		"entity_glint", VertexFormats.POSITION_UV, 7, 256, false, () -> method_23010(0.16F), () -> method_23039()
	);
	public static final RenderLayer BEACON_BEAM = new RenderLayer("beacon_beam", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 256, false, () -> {
		RenderSystem.defaultAlphaFunc();
		MinecraftClient.getInstance().getTextureManager().bindTexture(BeaconBlockEntityRenderer.BEAM_TEX);
		RenderSystem.texParameter(3553, 10242, 10497);
		RenderSystem.texParameter(3553, 10243, 10497);
		RenderSystem.disableFog();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
	}, () -> {
		RenderSystem.enableFog();
		RenderSystem.depthMask(true);
	});
	public static final RenderLayer LIGHTNING = new RenderLayer("lightning", VertexFormats.POSITION_COLOR, 7, 256, false, () -> {
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE);
		RenderSystem.shadeModel(7425);
		RenderSystem.disableAlphaTest();
	}, () -> {
		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
	});
	public static final RenderLayer LINES = new RenderLayer("lines", VertexFormats.POSITION_COLOR, 1, 256, false, () -> {
		RenderSystem.disableAlphaTest();
		RenderSystem.lineWidth(Math.max(2.5F, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
		RenderSystem.disableTexture();
		RenderSystem.matrixMode(5889);
		RenderSystem.pushMatrix();
		RenderSystem.scalef(1.0F, 1.0F, 0.999F);
		RenderSystem.matrixMode(5888);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}, () -> {
		RenderSystem.matrixMode(5889);
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.enableTexture();
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	});
	private static boolean field_20802;
	private static final Map<Block, RenderLayer> field_20803 = SystemUtil.consume(Maps.<Block, RenderLayer>newHashMap(), hashMap -> {
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
		hashMap.put(Blocks.OAK_SAPLING, CUTOUT);
		hashMap.put(Blocks.SPRUCE_SAPLING, CUTOUT);
		hashMap.put(Blocks.BIRCH_SAPLING, CUTOUT);
		hashMap.put(Blocks.JUNGLE_SAPLING, CUTOUT);
		hashMap.put(Blocks.ACACIA_SAPLING, CUTOUT);
		hashMap.put(Blocks.DARK_OAK_SAPLING, CUTOUT);
		hashMap.put(Blocks.GLASS, CUTOUT);
		hashMap.put(Blocks.WHITE_BED, CUTOUT);
		hashMap.put(Blocks.ORANGE_BED, CUTOUT);
		hashMap.put(Blocks.MAGENTA_BED, CUTOUT);
		hashMap.put(Blocks.LIGHT_BLUE_BED, CUTOUT);
		hashMap.put(Blocks.YELLOW_BED, CUTOUT);
		hashMap.put(Blocks.LIME_BED, CUTOUT);
		hashMap.put(Blocks.PINK_BED, CUTOUT);
		hashMap.put(Blocks.GRAY_BED, CUTOUT);
		hashMap.put(Blocks.LIGHT_GRAY_BED, CUTOUT);
		hashMap.put(Blocks.CYAN_BED, CUTOUT);
		hashMap.put(Blocks.PURPLE_BED, CUTOUT);
		hashMap.put(Blocks.BLUE_BED, CUTOUT);
		hashMap.put(Blocks.BROWN_BED, CUTOUT);
		hashMap.put(Blocks.GREEN_BED, CUTOUT);
		hashMap.put(Blocks.RED_BED, CUTOUT);
		hashMap.put(Blocks.BLACK_BED, CUTOUT);
		hashMap.put(Blocks.POWERED_RAIL, CUTOUT);
		hashMap.put(Blocks.DETECTOR_RAIL, CUTOUT);
		hashMap.put(Blocks.COBWEB, CUTOUT);
		hashMap.put(Blocks.GRASS, CUTOUT);
		hashMap.put(Blocks.FERN, CUTOUT);
		hashMap.put(Blocks.DEAD_BUSH, CUTOUT);
		hashMap.put(Blocks.SEAGRASS, CUTOUT);
		hashMap.put(Blocks.TALL_SEAGRASS, CUTOUT);
		hashMap.put(Blocks.DANDELION, CUTOUT);
		hashMap.put(Blocks.POPPY, CUTOUT);
		hashMap.put(Blocks.BLUE_ORCHID, CUTOUT);
		hashMap.put(Blocks.ALLIUM, CUTOUT);
		hashMap.put(Blocks.AZURE_BLUET, CUTOUT);
		hashMap.put(Blocks.RED_TULIP, CUTOUT);
		hashMap.put(Blocks.ORANGE_TULIP, CUTOUT);
		hashMap.put(Blocks.WHITE_TULIP, CUTOUT);
		hashMap.put(Blocks.PINK_TULIP, CUTOUT);
		hashMap.put(Blocks.OXEYE_DAISY, CUTOUT);
		hashMap.put(Blocks.CORNFLOWER, CUTOUT);
		hashMap.put(Blocks.WITHER_ROSE, CUTOUT);
		hashMap.put(Blocks.LILY_OF_THE_VALLEY, CUTOUT);
		hashMap.put(Blocks.BROWN_MUSHROOM, CUTOUT);
		hashMap.put(Blocks.RED_MUSHROOM, CUTOUT);
		hashMap.put(Blocks.TORCH, CUTOUT);
		hashMap.put(Blocks.WALL_TORCH, CUTOUT);
		hashMap.put(Blocks.FIRE, CUTOUT);
		hashMap.put(Blocks.SPAWNER, CUTOUT);
		hashMap.put(Blocks.REDSTONE_WIRE, CUTOUT);
		hashMap.put(Blocks.WHEAT, CUTOUT);
		hashMap.put(Blocks.OAK_DOOR, CUTOUT);
		hashMap.put(Blocks.LADDER, CUTOUT);
		hashMap.put(Blocks.RAIL, CUTOUT);
		hashMap.put(Blocks.IRON_DOOR, CUTOUT);
		hashMap.put(Blocks.REDSTONE_TORCH, CUTOUT);
		hashMap.put(Blocks.REDSTONE_WALL_TORCH, CUTOUT);
		hashMap.put(Blocks.CACTUS, CUTOUT);
		hashMap.put(Blocks.SUGAR_CANE, CUTOUT);
		hashMap.put(Blocks.REPEATER, CUTOUT);
		hashMap.put(Blocks.OAK_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.SPRUCE_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.BIRCH_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.JUNGLE_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.ACACIA_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.DARK_OAK_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.ATTACHED_PUMPKIN_STEM, CUTOUT);
		hashMap.put(Blocks.ATTACHED_MELON_STEM, CUTOUT);
		hashMap.put(Blocks.PUMPKIN_STEM, CUTOUT);
		hashMap.put(Blocks.MELON_STEM, CUTOUT);
		hashMap.put(Blocks.VINE, CUTOUT);
		hashMap.put(Blocks.LILY_PAD, CUTOUT);
		hashMap.put(Blocks.NETHER_WART, CUTOUT);
		hashMap.put(Blocks.BREWING_STAND, CUTOUT);
		hashMap.put(Blocks.COCOA, CUTOUT);
		hashMap.put(Blocks.BEACON, CUTOUT);
		hashMap.put(Blocks.FLOWER_POT, CUTOUT);
		hashMap.put(Blocks.POTTED_OAK_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_SPRUCE_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_BIRCH_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_JUNGLE_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_ACACIA_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_DARK_OAK_SAPLING, CUTOUT);
		hashMap.put(Blocks.POTTED_FERN, CUTOUT);
		hashMap.put(Blocks.POTTED_DANDELION, CUTOUT);
		hashMap.put(Blocks.POTTED_POPPY, CUTOUT);
		hashMap.put(Blocks.POTTED_BLUE_ORCHID, CUTOUT);
		hashMap.put(Blocks.POTTED_ALLIUM, CUTOUT);
		hashMap.put(Blocks.POTTED_AZURE_BLUET, CUTOUT);
		hashMap.put(Blocks.POTTED_RED_TULIP, CUTOUT);
		hashMap.put(Blocks.POTTED_ORANGE_TULIP, CUTOUT);
		hashMap.put(Blocks.POTTED_WHITE_TULIP, CUTOUT);
		hashMap.put(Blocks.POTTED_PINK_TULIP, CUTOUT);
		hashMap.put(Blocks.POTTED_OXEYE_DAISY, CUTOUT);
		hashMap.put(Blocks.POTTED_CORNFLOWER, CUTOUT);
		hashMap.put(Blocks.POTTED_LILY_OF_THE_VALLEY, CUTOUT);
		hashMap.put(Blocks.POTTED_WITHER_ROSE, CUTOUT);
		hashMap.put(Blocks.POTTED_RED_MUSHROOM, CUTOUT);
		hashMap.put(Blocks.POTTED_BROWN_MUSHROOM, CUTOUT);
		hashMap.put(Blocks.POTTED_DEAD_BUSH, CUTOUT);
		hashMap.put(Blocks.POTTED_CACTUS, CUTOUT);
		hashMap.put(Blocks.CARROTS, CUTOUT);
		hashMap.put(Blocks.POTATOES, CUTOUT);
		hashMap.put(Blocks.COMPARATOR, CUTOUT);
		hashMap.put(Blocks.ACTIVATOR_RAIL, CUTOUT);
		hashMap.put(Blocks.IRON_TRAPDOOR, CUTOUT);
		hashMap.put(Blocks.SUNFLOWER, CUTOUT);
		hashMap.put(Blocks.LILAC, CUTOUT);
		hashMap.put(Blocks.ROSE_BUSH, CUTOUT);
		hashMap.put(Blocks.PEONY, CUTOUT);
		hashMap.put(Blocks.TALL_GRASS, CUTOUT);
		hashMap.put(Blocks.LARGE_FERN, CUTOUT);
		hashMap.put(Blocks.SPRUCE_DOOR, CUTOUT);
		hashMap.put(Blocks.BIRCH_DOOR, CUTOUT);
		hashMap.put(Blocks.JUNGLE_DOOR, CUTOUT);
		hashMap.put(Blocks.ACACIA_DOOR, CUTOUT);
		hashMap.put(Blocks.DARK_OAK_DOOR, CUTOUT);
		hashMap.put(Blocks.END_ROD, CUTOUT);
		hashMap.put(Blocks.CHORUS_PLANT, CUTOUT);
		hashMap.put(Blocks.CHORUS_FLOWER, CUTOUT);
		hashMap.put(Blocks.BEETROOTS, CUTOUT);
		hashMap.put(Blocks.KELP, CUTOUT);
		hashMap.put(Blocks.KELP_PLANT, CUTOUT);
		hashMap.put(Blocks.TURTLE_EGG, CUTOUT);
		hashMap.put(Blocks.DEAD_TUBE_CORAL, CUTOUT);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL, CUTOUT);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL, CUTOUT);
		hashMap.put(Blocks.DEAD_FIRE_CORAL, CUTOUT);
		hashMap.put(Blocks.DEAD_HORN_CORAL, CUTOUT);
		hashMap.put(Blocks.TUBE_CORAL, CUTOUT);
		hashMap.put(Blocks.BRAIN_CORAL, CUTOUT);
		hashMap.put(Blocks.BUBBLE_CORAL, CUTOUT);
		hashMap.put(Blocks.FIRE_CORAL, CUTOUT);
		hashMap.put(Blocks.HORN_CORAL, CUTOUT);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_HORN_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.TUBE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.BRAIN_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.BUBBLE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.FIRE_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.HORN_CORAL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.DEAD_HORN_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.TUBE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.BRAIN_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.BUBBLE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.FIRE_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.HORN_CORAL_WALL_FAN, CUTOUT);
		hashMap.put(Blocks.SEA_PICKLE, CUTOUT);
		hashMap.put(Blocks.CONDUIT, CUTOUT);
		hashMap.put(Blocks.BAMBOO_SAPLING, CUTOUT);
		hashMap.put(Blocks.BAMBOO, CUTOUT);
		hashMap.put(Blocks.POTTED_BAMBOO, CUTOUT);
		hashMap.put(Blocks.SCAFFOLDING, CUTOUT);
		hashMap.put(Blocks.STONECUTTER, CUTOUT);
		hashMap.put(Blocks.LANTERN, CUTOUT);
		hashMap.put(Blocks.CAMPFIRE, CUTOUT);
		hashMap.put(Blocks.SWEET_BERRY_BUSH, CUTOUT);
		hashMap.put(Blocks.ICE, TRANSLUCENT);
		hashMap.put(Blocks.NETHER_PORTAL, TRANSLUCENT);
		hashMap.put(Blocks.WHITE_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.LIME_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.PINK_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.GRAY_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.CYAN_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.BLUE_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.BROWN_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.GREEN_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.RED_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.BLACK_STAINED_GLASS, TRANSLUCENT);
		hashMap.put(Blocks.TRIPWIRE, TRANSLUCENT);
		hashMap.put(Blocks.WHITE_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.ORANGE_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.MAGENTA_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.YELLOW_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.LIME_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.PINK_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.GRAY_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.CYAN_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.PURPLE_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.BLUE_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.BROWN_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.GREEN_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.RED_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.BLACK_STAINED_GLASS_PANE, TRANSLUCENT);
		hashMap.put(Blocks.SLIME_BLOCK, TRANSLUCENT);
		hashMap.put(Blocks.FROSTED_ICE, TRANSLUCENT);
		hashMap.put(Blocks.BUBBLE_COLUMN, TRANSLUCENT);
	});
	private static final Map<Fluid, RenderLayer> field_20804 = SystemUtil.consume(Maps.<Fluid, RenderLayer>newHashMap(), hashMap -> {
		hashMap.put(Fluids.FLOWING_WATER, TRANSLUCENT);
		hashMap.put(Fluids.WATER, TRANSLUCENT);
	});
	private final String name;
	private final VertexFormat vertexFormat;
	private final int drawMode;
	private final int expectedBufferSize;
	private final Runnable beginAction;
	private final Runnable endAction;
	private final boolean field_20975;

	public static RenderLayer method_23017(Identifier identifier) {
		return method_23019(identifier, false, true, false);
	}

	public static RenderLayer method_23019(Identifier identifier, boolean bl, boolean bl2, boolean bl3) {
		return method_23020(identifier, bl, bl2, bl3, 0.1F, false, true);
	}

	public static RenderLayer method_23020(Identifier identifier, boolean bl, boolean bl2, boolean bl3, float f, boolean bl4, boolean bl5) {
		return new RenderLayer.class_4601<RenderLayer.class_4600>(
			"new_entity", VertexFormats.POSITION_UV_NORMAL_2, 256, new RenderLayer.class_4600(identifier, bl, bl2, bl3, f, bl4, bl5), false, arg -> {
				RenderSystem.disableCull();
				RenderSystem.enableRescaleNormal();
				RenderSystem.shadeModel(arg.field_20979 ? 7425 : 7424);
				MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
				MinecraftClient.getInstance().gameRenderer.method_22975().setupOverlayColor();
				MinecraftClient.getInstance().getTextureManager().bindTexture(arg.field_20976);
				RenderSystem.texParameter(3553, 10241, 9728);
				RenderSystem.texParameter(3553, 10240, 9728);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				if (arg.field_20977) {
					RenderSystem.depthMask(false);
					RenderSystem.enableBlend();
					RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 0.15F);
					RenderSystem.blendFunc(GlStateManager.class_4535.CONSTANT_ALPHA, GlStateManager.class_4534.ONE_MINUS_CONSTANT_ALPHA);
				}

				if (arg.field_20980 <= 0.0F) {
					RenderSystem.disableAlphaTest();
				} else {
					RenderSystem.enableAlphaTest();
					RenderSystem.alphaFunc(516, arg.field_20980);
				}

				if (arg.field_20978) {
					GuiLighting.method_22890();
				}

				if (arg.field_20981) {
					RenderSystem.depthFunc(514);
				}
			}, arg -> {
				RenderSystem.shadeModel(7424);
				MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
				MinecraftClient.getInstance().gameRenderer.method_22975().teardownOverlayColor();
				RenderSystem.enableCull();
				RenderSystem.cullFace(GlStateManager.FaceSides.BACK);
				if (arg.field_20977) {
					RenderSystem.defaultBlendFunc();
					RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.depthMask(true);
				}

				if (arg.field_20978) {
					GuiLighting.disable();
				}

				if (arg.field_20981) {
					RenderSystem.depthFunc(515);
				}

				RenderSystem.disableAlphaTest();
				RenderSystem.defaultAlphaFunc();
			}
		) {
			@Override
			public Optional<Identifier> method_23289() {
				return this.method_23294().field_21069 ? Optional.of(this.method_23294().field_20976) : Optional.empty();
			}
		};
	}

	public static RenderLayer method_23026(Identifier identifier) {
		return new RenderLayer.class_4601<>("eyes", VertexFormats.POSITION_UV_NORMAL_2, 256, identifier, false, identifierx -> {
			MinecraftClient.getInstance().getTextureManager().bindTexture(identifierx);
			RenderSystem.enableBlend();
			RenderSystem.disableAlphaTest();
			RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
			RenderSystem.depthMask(false);
			BackgroundRenderer.setFogBlack(true);
			RenderSystem.enableDepthTest();
		}, identifierx -> {
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.enableAlphaTest();
			BackgroundRenderer.setFogBlack(false);
			RenderSystem.defaultBlendFunc();
		});
	}

	public static RenderLayer method_23018(Identifier identifier, float f, float g) {
		RenderLayer renderLayer = method_23017(identifier);
		return new RenderLayer.class_4601<>("power_swirl", VertexFormats.POSITION_UV_NORMAL_2, 256, new RenderLayer.class_4602(identifier, f, g), false, arg -> {
			renderLayer.begin();
			RenderSystem.matrixMode(5890);
			RenderSystem.pushMatrix();
			RenderSystem.loadIdentity();
			RenderSystem.translatef(arg.field_20984, arg.field_20985, 0.0F);
			RenderSystem.matrixMode(5888);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
			BackgroundRenderer.setFogBlack(true);
		}, arg -> {
			renderLayer.end();
			BackgroundRenderer.setFogBlack(false);
			RenderSystem.matrixMode(5890);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(5888);
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
		});
	}

	public static RenderLayer method_23287(Identifier identifier) {
		return new RenderLayer.class_4601<>("outline", VertexFormats.field_20887, 256, identifier, false, identifierx -> {
			MinecraftClient.getInstance().getTextureManager().bindTexture(identifierx);
			RenderSystem.disableCull();
			RenderSystem.depthFunc(519);
			RenderSystem.disableFog();
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.disableBlend();
			RenderSystem.setupOutline();
			MinecraftClient.getInstance().worldRenderer.method_22990().beginWrite(false);
		}, identifierx -> {
			RenderSystem.enableCull();
			RenderSystem.depthFunc(515);
			RenderSystem.disableAlphaTest();
			RenderSystem.enableFog();
			RenderSystem.teardownOutline();
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		});
	}

	public static RenderLayer method_23011(int i) {
		return new RenderLayer.class_4601<>(
			"crumbling",
			VertexFormats.POSITION_COLOR_UV_NORMAL,
			256,
			i,
			false,
			integer -> {
				MinecraftClient.getInstance().getTextureManager().bindTexture((Identifier)ModelLoader.field_21020.get(integer));
				RenderSystem.polygonOffset(-1.0F, -10.0F);
				RenderSystem.enablePolygonOffset();
				RenderSystem.defaultAlphaFunc();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableBlend();
				RenderSystem.depthMask(false);
				RenderSystem.blendFuncSeparate(
					GlStateManager.class_4535.DST_COLOR, GlStateManager.class_4534.SRC_COLOR, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO
				);
			},
			integer -> {
				RenderSystem.disableAlphaTest();
				RenderSystem.polygonOffset(0.0F, 0.0F);
				RenderSystem.disablePolygonOffset();
				RenderSystem.disableBlend();
				RenderSystem.depthMask(true);
			}
		);
	}

	public static RenderLayer method_23028(Identifier identifier) {
		return new RenderLayer.class_4601<>("text", VertexFormats.field_20888, 256, identifier, false, identifierx -> {
			MinecraftClient.getInstance().getTextureManager().bindTexture(identifierx);
			RenderSystem.enableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
		}, identifierx -> MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable());
	}

	public static RenderLayer method_23030(Identifier identifier) {
		RenderLayer renderLayer = method_23028(identifier);
		return new RenderLayer.class_4601<>("text_see_through", VertexFormats.field_20888, 256, identifier, false, identifierx -> {
			renderLayer.begin();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
		}, identifierx -> {
			renderLayer.end();
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
		});
	}

	public static RenderLayer method_23021(int i) {
		return new RenderLayer.class_4601<>(
			"portal",
			VertexFormats.POSITION_COLOR,
			256,
			i,
			false,
			integer -> {
				RenderSystem.enableBlend();
				if (integer >= 2) {
					RenderSystem.blendFunc(GlStateManager.class_4535.ONE.value, GlStateManager.class_4534.ONE.value);
					MinecraftClient.getInstance().getTextureManager().bindTexture(EndPortalBlockEntityRenderer.PORTAL_TEX);
					BackgroundRenderer.setFogBlack(true);
				} else {
					RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA.value, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA.value);
					MinecraftClient.getInstance().getTextureManager().bindTexture(EndPortalBlockEntityRenderer.SKY_TEX);
				}

				RenderSystem.matrixMode(5890);
				RenderSystem.pushMatrix();
				RenderSystem.loadIdentity();
				RenderSystem.translatef(0.5F, 0.5F, 0.0F);
				RenderSystem.scalef(0.5F, 0.5F, 1.0F);
				RenderSystem.translatef(
					17.0F / (float)integer.intValue(), (2.0F + (float)integer.intValue() / 1.5F) * ((float)(SystemUtil.getMeasuringTimeMs() % 800000L) / 800000.0F), 0.0F
				);
				RenderSystem.rotatef(((float)(integer * integer) * 4321.0F + (float)integer.intValue() * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.scalef(4.5F - (float)integer.intValue() / 4.0F, 4.5F - (float)integer.intValue() / 4.0F, 1.0F);
				RenderSystem.mulTextureByProjModelView();
				RenderSystem.matrixMode(5888);
				RenderSystem.setupEndPortalTexGen();
			},
			integer -> {
				RenderSystem.defaultBlendFunc();
				RenderSystem.matrixMode(5890);
				RenderSystem.popMatrix();
				RenderSystem.matrixMode(5888);
				RenderSystem.clearTexGen();
				BackgroundRenderer.setFogBlack(false);
			}
		);
	}

	public RenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, Runnable runnable, Runnable runnable2) {
		this.name = string;
		this.vertexFormat = vertexFormat;
		this.drawMode = i;
		this.expectedBufferSize = j;
		this.beginAction = runnable;
		this.endAction = runnable2;
		this.field_20975 = bl;
	}

	public static void method_22719(boolean bl) {
		field_20802 = bl;
	}

	public void method_23012(BufferBuilder bufferBuilder) {
		if (bufferBuilder.method_22893()) {
			bufferBuilder.end();
			this.begin();
			BufferRenderer.draw(bufferBuilder);
			this.end();
		}
	}

	public String toString() {
		return this.name;
	}

	public static RenderLayer method_22715(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return field_20802 ? CUTOUT_MIPPED : SOLID;
		} else {
			RenderLayer renderLayer = (RenderLayer)field_20803.get(block);
			return renderLayer != null ? renderLayer : SOLID;
		}
	}

	public static RenderLayer method_22716(FluidState fluidState) {
		RenderLayer renderLayer = (RenderLayer)field_20804.get(fluidState.getFluid());
		return renderLayer != null ? renderLayer : SOLID;
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT);
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public void begin() {
		this.beginAction.run();
	}

	public void end() {
		this.endAction.run();
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public int getDrawMode() {
		return this.drawMode;
	}

	public Optional<Identifier> method_23289() {
		return Optional.empty();
	}

	public boolean method_23037() {
		return this.field_20975;
	}

	public boolean equals(@Nullable Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			RenderLayer renderLayer = (RenderLayer)object;
			return this.name.equals(renderLayer.name);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	private static void method_23010(float f) {
		RenderSystem.enableTexture();
		TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		textureManager.bindTexture(ItemRenderer.field_21010);
		RenderSystem.texParameter(3553, 10241, 9728);
		RenderSystem.texParameter(3553, 10240, 9728);
		RenderSystem.texParameter(3553, 10242, 10497);
		RenderSystem.texParameter(3553, 10243, 10497);
		RenderSystem.depthMask(false);
		RenderSystem.depthFunc(514);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_COLOR, GlStateManager.class_4534.ONE);
		RenderSystem.matrixMode(5890);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		long l = SystemUtil.getMeasuringTimeMs() * 8L;
		float g = (float)(l % 110000L) / 110000.0F;
		float h = (float)(l % 30000L) / 30000.0F;
		RenderSystem.translatef(-g, h, 0.0F);
		RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
		RenderSystem.scalef(f, f, f);
	}

	private static void method_23039() {
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
		RenderSystem.depthFunc(515);
		RenderSystem.depthMask(true);
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4600 {
		private final Identifier field_20976;
		private final boolean field_20977;
		private final boolean field_20978;
		private final boolean field_20979;
		private final float field_20980;
		private final boolean field_20981;
		private final boolean field_21069;

		public class_4600(Identifier identifier, boolean bl, boolean bl2, boolean bl3, float f, boolean bl4, boolean bl5) {
			this.field_20976 = identifier;
			this.field_20977 = bl;
			this.field_20978 = bl2;
			this.field_20979 = bl3;
			this.field_20980 = f;
			this.field_20981 = bl4;
			this.field_21069 = bl5;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.class_4600 lv = (RenderLayer.class_4600)object;
				return this.field_20977 == lv.field_20977 && this.field_20978 == lv.field_20978 && this.field_20976.equals(lv.field_20976);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_20976, this.field_20977, this.field_20978});
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4601<S> extends RenderLayer {
		private final S field_20982;

		protected final S method_23294() {
			return this.field_20982;
		}

		public class_4601(String string, VertexFormat vertexFormat, int i, S object, boolean bl, Consumer<S> consumer, Consumer<S> consumer2) {
			super(string, vertexFormat, 7, i, bl, () -> consumer.accept(object), () -> consumer2.accept(object));
			this.field_20982 = object;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			if (!super.equals(object)) {
				return false;
			} else if (this.getClass() != object.getClass()) {
				return false;
			} else {
				RenderLayer.class_4601<?> lv = (RenderLayer.class_4601<?>)object;
				return this.field_20982.equals(lv.field_20982);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{super.hashCode(), this.field_20982});
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4602 {
		private final Identifier field_20983;
		private final float field_20984;
		private final float field_20985;

		public class_4602(Identifier identifier, float f, float g) {
			this.field_20983 = identifier;
			this.field_20984 = f;
			this.field_20985 = g;
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.class_4602 lv = (RenderLayer.class_4602)object;
				return Float.compare(lv.field_20984, this.field_20984) == 0
					&& Float.compare(lv.field_20985, this.field_20985) == 0
					&& this.field_20983.equals(lv.field_20983);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_20983, this.field_20984, this.field_20985});
		}
	}
}
