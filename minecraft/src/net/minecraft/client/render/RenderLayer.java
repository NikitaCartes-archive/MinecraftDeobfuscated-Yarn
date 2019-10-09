package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4668;
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
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class RenderLayer extends class_4668 {
	private static final RenderLayer SOLID = new RenderLayer.class_4687(
		"solid",
		VertexFormats.POSITION_COLOR_UV_NORMAL,
		7,
		2097152,
		true,
		false,
		RenderLayer.class_4688.method_23598().method_23612(field_21375).method_23608(field_21383).method_23613(field_21376).method_23617(false)
	);
	private static final RenderLayer CUTOUT_MIPPED = new RenderLayer.class_4687(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_UV_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.class_4688.method_23598()
			.method_23612(field_21375)
			.method_23608(field_21383)
			.method_23613(field_21376)
			.method_23602(field_21373)
			.method_23617(false)
	);
	private static final RenderLayer CUTOUT = new RenderLayer.class_4687(
		"cutout",
		VertexFormats.POSITION_COLOR_UV_NORMAL,
		7,
		131072,
		true,
		false,
		RenderLayer.class_4688.method_23598()
			.method_23612(field_21375)
			.method_23608(field_21383)
			.method_23613(field_21377)
			.method_23602(field_21373)
			.method_23617(false)
	);
	private static final RenderLayer TRANSLUCENT = new RenderLayer.class_4687(
		"translucent",
		VertexFormats.POSITION_COLOR_UV_NORMAL,
		7,
		262144,
		true,
		true,
		RenderLayer.class_4688.method_23598()
			.method_23612(field_21375)
			.method_23608(field_21383)
			.method_23613(field_21377)
			.method_23615(field_21370)
			.method_23617(false)
	);
	private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = new RenderLayer(
		"translucent_no_crumbling", VertexFormats.POSITION_COLOR_UV_NORMAL, 7, 256, false, true, TRANSLUCENT::method_23516, TRANSLUCENT::method_23518
	);
	private static final RenderLayer LEASH = new RenderLayer.class_4687(
		"leash", VertexFormats.POSITION_COLOR, 7, 256, RenderLayer.class_4688.method_23598().method_23613(field_21378).method_23603(field_21345).method_23617(false)
	);
	private static final RenderLayer WATER_MASK = new RenderLayer.class_4687(
		"water_mask", VertexFormats.POSITION, 7, 256, RenderLayer.class_4688.method_23598().method_23613(field_21378).method_23616(field_21351).method_23617(false)
	);
	private static final RenderLayer GLINT = new RenderLayer.class_4687(
		"glint",
		VertexFormats.POSITION_UV,
		7,
		256,
		RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(ItemRenderer.field_21010, false, false))
			.method_23616(field_21350)
			.method_23604(field_21347)
			.method_23615(field_21368)
			.method_23614(field_21381)
			.method_23617(false)
	);
	private static final RenderLayer ENTITY_GLINT = new RenderLayer.class_4687(
		"entity_glint",
		VertexFormats.POSITION_UV,
		7,
		256,
		RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(ItemRenderer.field_21010, false, false))
			.method_23616(field_21350)
			.method_23604(field_21347)
			.method_23615(field_21368)
			.method_23614(field_21382)
			.method_23617(false)
	);
	private static final RenderLayer BEACON_BEAM = new RenderLayer.class_4687(
		"beacon_beam",
		VertexFormats.POSITION_COLOR_UV_NORMAL,
		7,
		256,
		false,
		true,
		RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(BeaconBlockEntityRenderer.BEAM_TEX, false, false))
			.method_23615(field_21370)
			.method_23616(field_21350)
			.method_23606(field_21355)
			.method_23617(false)
	);
	private static final RenderLayer LIGHTNING = new RenderLayer.class_4687(
		"lightning",
		VertexFormats.POSITION_COLOR,
		7,
		256,
		false,
		true,
		RenderLayer.class_4688.method_23598().method_23616(field_21350).method_23615(field_21367).method_23612(field_21375).method_23617(false)
	);
	private static boolean field_20802;
	private static final Map<Block, RenderLayer> blockHandler = SystemUtil.consume(Maps.<Block, RenderLayer>newHashMap(), hashMap -> {
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
		hashMap.put(Blocks.HONEY_BLOCK, TRANSLUCENT);
		hashMap.put(Blocks.FROSTED_ICE, TRANSLUCENT);
		hashMap.put(Blocks.BUBBLE_COLUMN, TRANSLUCENT);
	});
	private static final Map<Fluid, RenderLayer> fluidHandler = SystemUtil.consume(Maps.<Fluid, RenderLayer>newHashMap(), hashMap -> {
		hashMap.put(Fluids.FLOWING_WATER, TRANSLUCENT);
		hashMap.put(Fluids.WATER, TRANSLUCENT);
	});
	private final VertexFormat vertexFormat;
	private final int drawMode;
	private final int expectedBufferSize;
	private final boolean field_20975;
	private final boolean field_21402;

	public static RenderLayer getSolid() {
		return SOLID;
	}

	public static RenderLayer getCutoutMipped() {
		return CUTOUT_MIPPED;
	}

	public static RenderLayer getCutout() {
		return CUTOUT;
	}

	public static RenderLayer getTranslucent() {
		return TRANSLUCENT;
	}

	public static RenderLayer getTranslucentNoCrumbling() {
		return TRANSLUCENT_NO_CRUMBLING;
	}

	public static RenderLayer getEntitySolid(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21364)
			.method_23605(field_21387)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_solid", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityCutout(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21364)
			.method_23605(field_21387)
			.method_23602(field_21372)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_cutout", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21364)
			.method_23605(field_21387)
			.method_23602(field_21372)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_cutout_no_cull", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityTranslucent(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21370)
			.method_23605(field_21387)
			.method_23602(field_21372)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_translucent", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityForceTranslucent(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21365)
			.method_23605(field_21387)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_force_translucent", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, false, true, lv);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23602(field_21373)
			.method_23605(field_21387)
			.method_23612(field_21375)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_smooth_cutout", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityDecal(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23605(field_21387)
			.method_23602(field_21372)
			.method_23604(field_21347)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(false);
		return new RenderLayer.class_4687("entity_decal", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEntityNoOutline(Identifier identifier) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23615(field_21370)
			.method_23605(field_21387)
			.method_23602(field_21372)
			.method_23603(field_21345)
			.method_23608(field_21383)
			.method_23611(field_21385)
			.method_23617(false);
		return new RenderLayer.class_4687("entity_no_outline", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, false, true, lv);
	}

	public static RenderLayer getEntityAlpha(Identifier identifier, float f) {
		RenderLayer.class_4688 lv = RenderLayer.class_4688.method_23598()
			.method_23613(new class_4668.class_4683(identifier, false, false))
			.method_23602(new class_4668.class_4669(f))
			.method_23603(field_21345)
			.method_23617(true);
		return new RenderLayer.class_4687("entity_alpha", VertexFormats.POSITION_UV_NORMAL_2, 7, 256, lv);
	}

	public static RenderLayer getEyes(Identifier identifier) {
		class_4668.class_4683 lv = new class_4668.class_4683(identifier, false, false);
		return new RenderLayer.class_4687(
			"eyes",
			VertexFormats.POSITION_UV_NORMAL_2,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598().method_23613(lv).method_23615(field_21366).method_23616(field_21350).method_23606(field_21357).method_23617(false)
		);
	}

	public static RenderLayer getPowerSwirl(Identifier identifier, float f, float g) {
		return new RenderLayer.class_4687(
			"power_swirl",
			VertexFormats.POSITION_UV_NORMAL_2,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598()
				.method_23613(new class_4668.class_4683(identifier, false, false))
				.method_23614(new class_4668.class_4682(f, g))
				.method_23606(field_21357)
				.method_23615(field_21366)
				.method_23605(field_21387)
				.method_23602(field_21372)
				.method_23603(field_21345)
				.method_23608(field_21383)
				.method_23611(field_21385)
				.method_23617(false)
		);
	}

	public static RenderLayer getLeash() {
		return LEASH;
	}

	public static RenderLayer getWaterMask() {
		return WATER_MASK;
	}

	public static RenderLayer getOutline(Identifier identifier) {
		return new RenderLayer.class_4687(
			"outline",
			VertexFormats.field_20887,
			7,
			256,
			RenderLayer.class_4688.method_23598()
				.method_23613(new class_4668.class_4683(identifier, false, false))
				.method_23603(field_21345)
				.method_23604(field_21346)
				.method_23602(field_21372)
				.method_23614(field_21380)
				.method_23606(field_21355)
				.method_23610(field_21359)
				.method_23617(false)
		);
	}

	public static RenderLayer getGlint() {
		return GLINT;
	}

	public static RenderLayer getEntityGlint() {
		return ENTITY_GLINT;
	}

	public static RenderLayer getCrumbling(int i) {
		class_4668.class_4683 lv = new class_4668.class_4683((Identifier)ModelLoader.field_21020.get(i), false, false);
		return new RenderLayer.class_4687(
			"crumbling",
			VertexFormats.POSITION_COLOR_UV_NORMAL,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598()
				.method_23613(lv)
				.method_23602(field_21372)
				.method_23615(field_21369)
				.method_23616(field_21350)
				.method_23607(field_21353)
				.method_23617(false)
		);
	}

	public static RenderLayer getText(Identifier identifier) {
		return new RenderLayer.class_4687(
			"text",
			VertexFormats.field_20888,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598()
				.method_23613(new class_4668.class_4683(identifier, false, false))
				.method_23602(field_21372)
				.method_23615(field_21370)
				.method_23608(field_21383)
				.method_23617(false)
		);
	}

	public static RenderLayer getTextSeeThrough(Identifier identifier) {
		return new RenderLayer.class_4687(
			"text_see_through",
			VertexFormats.field_20888,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598()
				.method_23613(new class_4668.class_4683(identifier, false, false))
				.method_23602(field_21372)
				.method_23615(field_21370)
				.method_23608(field_21383)
				.method_23604(field_21346)
				.method_23616(field_21350)
				.method_23617(false)
		);
	}

	public static RenderLayer getBeaconBeam() {
		return BEACON_BEAM;
	}

	public static RenderLayer getLightning() {
		return LIGHTNING;
	}

	public static RenderLayer getEndPortal(int i) {
		class_4668.class_4685 lv;
		class_4668.class_4683 lv2;
		if (i <= 1) {
			lv = field_21370;
			lv2 = new class_4668.class_4683(EndPortalBlockEntityRenderer.SKY_TEX, false, false);
		} else {
			lv = field_21366;
			lv2 = new class_4668.class_4683(EndPortalBlockEntityRenderer.PORTAL_TEX, false, false);
		}

		return new RenderLayer.class_4687(
			"end_portal",
			VertexFormats.POSITION_COLOR,
			7,
			256,
			false,
			true,
			RenderLayer.class_4688.method_23598()
				.method_23615(lv)
				.method_23613(lv2)
				.method_23614(new class_4668.class_4680(i))
				.method_23606(field_21357)
				.method_23617(false)
		);
	}

	public static RenderLayer getLines() {
		return new RenderLayer.class_4687(
			"lines",
			VertexFormats.POSITION_COLOR,
			1,
			256,
			RenderLayer.class_4688.method_23598()
				.method_23609(new class_4668.class_4677(Math.max(2.5F, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F)))
				.method_23607(field_21354)
				.method_23615(field_21370)
				.method_23617(false)
		);
	}

	public RenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, runnable, runnable2);
		this.vertexFormat = vertexFormat;
		this.drawMode = i;
		this.expectedBufferSize = j;
		this.field_20975 = bl;
		this.field_21402 = bl2;
	}

	public static void method_22719(boolean bl) {
		field_20802 = bl;
	}

	public void method_23012(BufferBuilder bufferBuilder) {
		if (bufferBuilder.method_22893()) {
			bufferBuilder.end();
			this.method_23516();
			BufferRenderer.draw(bufferBuilder);
			this.method_23518();
		}
	}

	public String toString() {
		return this.field_21363;
	}

	public static RenderLayer method_22715(BlockState blockState) {
		Block block = blockState.getBlock();
		if (block instanceof LeavesBlock) {
			return field_20802 ? getCutoutMipped() : getSolid();
		} else {
			RenderLayer renderLayer = (RenderLayer)blockHandler.get(block);
			return renderLayer != null ? renderLayer : getSolid();
		}
	}

	public static RenderLayer method_23575(BlockState blockState) {
		RenderLayer renderLayer = method_22715(blockState);
		if (renderLayer == getTranslucent()) {
			return getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		} else {
			return renderLayer != getCutout() && renderLayer != getCutoutMipped()
				? getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX)
				: getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		}
	}

	public static RenderLayer method_23571(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			return method_23575(block.getDefaultState());
		} else {
			return getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		}
	}

	public static RenderLayer method_22716(FluidState fluidState) {
		RenderLayer renderLayer = (RenderLayer)fluidHandler.get(fluidState.getFluid());
		return renderLayer != null ? renderLayer : getSolid();
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent());
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
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

	@Environment(EnvType.CLIENT)
	static class class_4687 extends RenderLayer {
		private final RenderLayer.class_4688 field_21403;
		private int field_21404;
		private boolean field_21405 = false;

		public class_4687(String string, VertexFormat vertexFormat, int i, int j, RenderLayer.class_4688 arg) {
			this(string, vertexFormat, i, j, false, false, arg);
		}

		public class_4687(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, RenderLayer.class_4688 arg) {
			super(string, vertexFormat, i, j, bl, bl2, () -> arg.field_21422.forEach(class_4668::method_23516), () -> arg.field_21422.forEach(class_4668::method_23518));
			this.field_21403 = arg;
		}

		@Override
		public Optional<Identifier> method_23289() {
			return this.method_23597().field_21421 ? this.method_23597().field_21406.method_23564() : Optional.empty();
		}

		protected final RenderLayer.class_4688 method_23597() {
			return this.field_21403;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			if (!super.equals(object)) {
				return false;
			} else if (this.getClass() != object.getClass()) {
				return false;
			} else {
				RenderLayer.class_4687 lv = (RenderLayer.class_4687)object;
				return this.field_21403.equals(lv.field_21403);
			}
		}

		@Override
		public int hashCode() {
			if (!this.field_21405) {
				this.field_21405 = true;
				this.field_21404 = Objects.hash(new Object[]{super.hashCode(), this.field_21403});
			}

			return this.field_21404;
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class class_4688 {
		private final class_4668.class_4683 field_21406;
		private final class_4668.class_4685 field_21407;
		private final class_4668.class_4673 field_21408;
		private final class_4668.class_4681 field_21409;
		private final class_4668.class_4669 field_21410;
		private final class_4668.class_4672 field_21411;
		private final class_4668.class_4671 field_21412;
		private final class_4668.class_4676 field_21413;
		private final class_4668.class_4679 field_21414;
		private final class_4668.class_4674 field_21415;
		private final class_4668.class_4675 field_21416;
		private final class_4668.class_4678 field_21417;
		private final class_4668.class_4684 field_21418;
		private final class_4668.class_4686 field_21419;
		private final class_4668.class_4677 field_21420;
		private final boolean field_21421;
		private final ImmutableList<class_4668> field_21422;

		private class_4688(
			class_4668.class_4683 arg,
			class_4668.class_4685 arg2,
			class_4668.class_4673 arg3,
			class_4668.class_4681 arg4,
			class_4668.class_4669 arg5,
			class_4668.class_4672 arg6,
			class_4668.class_4671 arg7,
			class_4668.class_4676 arg8,
			class_4668.class_4679 arg9,
			class_4668.class_4674 arg10,
			class_4668.class_4675 arg11,
			class_4668.class_4678 arg12,
			class_4668.class_4684 arg13,
			class_4668.class_4686 arg14,
			class_4668.class_4677 arg15,
			boolean bl
		) {
			this.field_21406 = arg;
			this.field_21407 = arg2;
			this.field_21408 = arg3;
			this.field_21409 = arg4;
			this.field_21410 = arg5;
			this.field_21411 = arg6;
			this.field_21412 = arg7;
			this.field_21413 = arg8;
			this.field_21414 = arg9;
			this.field_21415 = arg10;
			this.field_21416 = arg11;
			this.field_21417 = arg12;
			this.field_21418 = arg13;
			this.field_21419 = arg14;
			this.field_21420 = arg15;
			this.field_21421 = bl;
			this.field_21422 = ImmutableList.of(
				this.field_21406,
				this.field_21407,
				this.field_21408,
				this.field_21409,
				this.field_21410,
				this.field_21411,
				this.field_21412,
				this.field_21413,
				this.field_21414,
				this.field_21415,
				this.field_21416,
				this.field_21417,
				this.field_21418,
				this.field_21419,
				this.field_21420
			);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.class_4688 lv = (RenderLayer.class_4688)object;
				return this.field_21421 == lv.field_21421 && this.field_21422.equals(lv.field_21422);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.field_21422, this.field_21421});
		}

		public static RenderLayer.class_4688.class_4689 method_23598() {
			return new RenderLayer.class_4688.class_4689();
		}

		@Environment(EnvType.CLIENT)
		public static class class_4689 {
			private class_4668.class_4683 field_21423 = class_4668.field_21378;
			private class_4668.class_4685 field_21424 = class_4668.field_21364;
			private class_4668.class_4673 field_21425 = class_4668.field_21388;
			private class_4668.class_4681 field_21426 = class_4668.field_21374;
			private class_4668.class_4669 field_21427 = class_4668.field_21371;
			private class_4668.class_4672 field_21428 = class_4668.field_21348;
			private class_4668.class_4671 field_21429 = class_4668.field_21344;
			private class_4668.class_4676 field_21430 = class_4668.field_21384;
			private class_4668.class_4679 field_21431 = class_4668.field_21386;
			private class_4668.class_4674 field_21432 = class_4668.field_21356;
			private class_4668.class_4675 field_21433 = class_4668.field_21352;
			private class_4668.class_4678 field_21434 = class_4668.field_21358;
			private class_4668.class_4684 field_21435 = class_4668.field_21379;
			private class_4668.class_4686 field_21436 = class_4668.field_21349;
			private class_4668.class_4677 field_21437 = class_4668.field_21360;

			private class_4689() {
			}

			public RenderLayer.class_4688.class_4689 method_23613(class_4668.class_4683 arg) {
				this.field_21423 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23615(class_4668.class_4685 arg) {
				this.field_21424 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23605(class_4668.class_4673 arg) {
				this.field_21425 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23612(class_4668.class_4681 arg) {
				this.field_21426 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23602(class_4668.class_4669 arg) {
				this.field_21427 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23604(class_4668.class_4672 arg) {
				this.field_21428 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23603(class_4668.class_4671 arg) {
				this.field_21429 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23608(class_4668.class_4676 arg) {
				this.field_21430 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23611(class_4668.class_4679 arg) {
				this.field_21431 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23606(class_4668.class_4674 arg) {
				this.field_21432 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23607(class_4668.class_4675 arg) {
				this.field_21433 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23610(class_4668.class_4678 arg) {
				this.field_21434 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23614(class_4668.class_4684 arg) {
				this.field_21435 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23616(class_4668.class_4686 arg) {
				this.field_21436 = arg;
				return this;
			}

			public RenderLayer.class_4688.class_4689 method_23609(class_4668.class_4677 arg) {
				this.field_21437 = arg;
				return this;
			}

			public RenderLayer.class_4688 method_23617(boolean bl) {
				return new RenderLayer.class_4688(
					this.field_21423,
					this.field_21424,
					this.field_21425,
					this.field_21426,
					this.field_21427,
					this.field_21428,
					this.field_21429,
					this.field_21430,
					this.field_21431,
					this.field_21432,
					this.field_21433,
					this.field_21434,
					this.field_21435,
					this.field_21436,
					this.field_21437,
					bl
				);
			}
		}
	}
}
