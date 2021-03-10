package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class RenderLayer extends RenderPhase {
	private static final RenderLayer SOLID = of(
		"solid",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		2097152,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).method_34578(field_29443).method_34577(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT_MIPPED = of(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).method_34578(field_29444).method_34577(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT = of(
		"cutout",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		131072,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).method_34578(field_29445).method_34577(BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer TRANSLUCENT = of(
		"translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, true, true, method_34569(field_29446)
	);
	private static final RenderLayer TRANSLUCENT_MOVING_BLOCK = of(
		"translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, false, true, getItemPhaseData()
	);
	private static final RenderLayer TRANSLUCENT_NO_CRUMBLING = of(
		"translucent_no_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, false, true, method_34569(field_29448)
	);
	private static final RenderLayer LEASH = of(
		"leash",
		VertexFormats.POSITION_COLOR_LIGHT,
		VertexFormat.DrawMode.TRIANGLE_STRIP,
		256,
		RenderLayer.MultiPhaseParameters.builder().method_34578(field_29416).method_34577(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
	);
	private static final RenderLayer WATER_MASK = of(
		"water_mask",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder().method_34578(field_29417).method_34577(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false)
	);
	private static final RenderLayer ARMOR_GLINT = of(
		"armor_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29419)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(false)
	);
	private static final RenderLayer ARMOR_ENTITY_GLINT = of(
		"armor_entity_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29420)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(false)
	);
	private static final RenderLayer GLINT_TRANSLUCENT = of(
		"glint_translucent",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29421)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.target(ITEM_TARGET)
			.build(false)
	);
	private static final RenderLayer GLINT = of(
		"glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29422)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer DIRECT_GLINT = of(
		"glint_direct",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29423)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer ENTITY_GLINT = of(
		"entity_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29424)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer DIRECT_ENTITY_GLINT = of(
		"entity_glint_direct",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29425)
			.method_34577(new RenderPhase.Texture(ItemRenderer.ENCHANTED_ITEM_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer LIGHTNING = of(
		"lightning",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29429)
			.writeMaskState(ALL_MASK)
			.transparency(LIGHTNING_TRANSPARENCY)
			.target(WEATHER_TARGET)
			.build(false)
	);
	private static final RenderLayer TRIPWIRE = of(
		"tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 262144, true, true, getTripwirePhaseData()
	);
	private static final RenderLayer END_PORTAL = of(
		"end_portal",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29431)
			.method_34577(
				RenderPhase.class_5940.method_34560()
					.method_34563(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.method_34563(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.method_34562()
			)
			.build(false)
	);
	private static final RenderLayer END_GATEWAY = of(
		"end_gateway",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		256,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29432)
			.method_34577(
				RenderPhase.class_5940.method_34560()
					.method_34563(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.method_34563(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.method_34562()
			)
			.build(false)
	);
	public static final RenderLayer.MultiPhase LINES = of(
		"lines",
		VertexFormats.field_29337,
		VertexFormat.DrawMode.LINES,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29433)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	public static final RenderLayer.MultiPhase LINE_STRIP = of(
		"line_strip",
		VertexFormats.field_29337,
		VertexFormat.DrawMode.LINE_STRIP,
		256,
		RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29433)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	private final VertexFormat vertexFormat;
	private final VertexFormat.DrawMode drawMode;
	private final int expectedBufferSize;
	private final boolean hasCrumbling;
	private final boolean translucent;
	private final Optional<RenderLayer> optionalThis;

	public static RenderLayer getSolid() {
		return SOLID;
	}

	public static RenderLayer getCutoutMipped() {
		return CUTOUT_MIPPED;
	}

	public static RenderLayer getCutout() {
		return CUTOUT;
	}

	private static RenderLayer.MultiPhaseParameters method_34569(RenderPhase.class_5942 arg) {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.method_34578(arg)
			.method_34577(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(TRANSLUCENT_TARGET)
			.build(true);
	}

	public static RenderLayer getTranslucent() {
		return TRANSLUCENT;
	}

	private static RenderLayer.MultiPhaseParameters getItemPhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.method_34578(field_29447)
			.method_34577(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.build(true);
	}

	public static RenderLayer getTranslucentMovingBlock() {
		return TRANSLUCENT_MOVING_BLOCK;
	}

	public static RenderLayer getTranslucentNoCrumbling() {
		return TRANSLUCENT_NO_CRUMBLING;
	}

	public static RenderLayer getArmorCutoutNoCull(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29449)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(true);
		return of(
			"armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters
		);
	}

	public static RenderLayer getEntitySolid(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29450)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
	}

	public static RenderLayer getEntityCutout(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29451)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture, boolean affectsOutline) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29452)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(affectsOutline);
		return of(
			"entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters
		);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture) {
		return getEntityCutoutNoCull(texture, true);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture, boolean affectsOutline) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29404)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(affectsOutline);
		return of(
			"entity_cutout_no_cull_z_offset",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			true,
			false,
			multiPhaseParameters
		);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture) {
		return getEntityCutoutNoCullZOffset(texture, true);
	}

	public static RenderLayer getItemEntityTranslucentCull(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29405)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_TARGET)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.writeMaskState(RenderPhase.ALL_MASK)
			.build(true);
		return of(
			"item_entity_translucent_cull",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			true,
			true,
			multiPhaseParameters
		);
	}

	public static RenderLayer getEntityTranslucentCull(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29406)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return of(
			"entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters
		);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture, boolean affectsOutline) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29407)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(affectsOutline);
		return of("entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture) {
		return getEntityTranslucent(texture, true);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29408)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.build(true);
		return of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
	}

	public static RenderLayer getBeaconBeam(Identifier texture, boolean translucent) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29409)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(translucent ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
			.writeMaskState(translucent ? COLOR_MASK : ALL_MASK)
			.build(false);
		return of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityDecal(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29410)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.depthTest(EQUAL_DEPTH_TEST)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(false);
		return of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
	}

	public static RenderLayer getEntityNoOutline(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29411)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.writeMaskState(COLOR_MASK)
			.build(false);
		return of("entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, true, multiPhaseParameters);
	}

	public static RenderLayer getEntityShadow(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29412)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.cull(ENABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.writeMaskState(COLOR_MASK)
			.depthTest(LEQUAL_DEPTH_TEST)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.build(false);
		return of("entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, false, false, multiPhaseParameters);
	}

	public static RenderLayer getEntityAlpha(Identifier texture) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.method_34578(field_29413)
			.method_34577(new RenderPhase.Texture(texture, false, false))
			.cull(DISABLE_CULLING)
			.build(true);
		return of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, multiPhaseParameters);
	}

	public static RenderLayer getEyes(Identifier texture) {
		RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
		return of(
			"eyes",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29414)
				.method_34577(texture2)
				.transparency(ADDITIVE_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.build(false)
		);
	}

	public static RenderLayer getEnergySwirl(Identifier texture, float x, float y) {
		return of(
			"energy_swirl",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29415)
				.method_34577(new RenderPhase.Texture(texture, false, false))
				.texturing(new RenderPhase.OffsetTexturing(x, y))
				.transparency(ADDITIVE_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false)
		);
	}

	public static RenderLayer getLeash() {
		return LEASH;
	}

	public static RenderLayer getWaterMask() {
		return WATER_MASK;
	}

	public static RenderLayer getOutline(Identifier texture) {
		return getOutline(texture, DISABLE_CULLING);
	}

	public static RenderLayer getOutline(Identifier texture, RenderPhase.Cull cull) {
		return of(
			"outline",
			VertexFormats.POSITION_COLOR_TEXTURE,
			VertexFormat.DrawMode.QUADS,
			256,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29418)
				.method_34577(new RenderPhase.Texture(texture, false, false))
				.cull(cull)
				.depthTest(ALWAYS_DEPTH_TEST)
				.target(OUTLINE_TARGET)
				.build(RenderLayer.OutlineMode.IS_OUTLINE)
		);
	}

	public static RenderLayer getArmorGlint() {
		return ARMOR_GLINT;
	}

	public static RenderLayer getArmorEntityGlint() {
		return ARMOR_ENTITY_GLINT;
	}

	public static RenderLayer method_30676() {
		return GLINT_TRANSLUCENT;
	}

	public static RenderLayer getGlint() {
		return GLINT;
	}

	public static RenderLayer getDirectGlint() {
		return DIRECT_GLINT;
	}

	public static RenderLayer getEntityGlint() {
		return ENTITY_GLINT;
	}

	public static RenderLayer getDirectEntityGlint() {
		return DIRECT_ENTITY_GLINT;
	}

	public static RenderLayer getBlockBreaking(Identifier texture) {
		RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
		return of(
			"crumbling",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29426)
				.method_34577(texture2)
				.transparency(CRUMBLING_TRANSPARENCY)
				.writeMaskState(COLOR_MASK)
				.layering(POLYGON_OFFSET_LAYERING)
				.build(false)
		);
	}

	public static RenderLayer getText(Identifier texture) {
		return of(
			"text",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29427)
				.method_34577(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.build(false)
		);
	}

	public static RenderLayer getTextSeeThrough(Identifier texture) {
		return of(
			"text_see_through",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			VertexFormat.DrawMode.QUADS,
			256,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.method_34578(field_29428)
				.method_34577(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.depthTest(ALWAYS_DEPTH_TEST)
				.writeMaskState(COLOR_MASK)
				.build(false)
		);
	}

	public static RenderLayer getLightning() {
		return LIGHTNING;
	}

	private static RenderLayer.MultiPhaseParameters getTripwirePhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.method_34578(field_29430)
			.method_34577(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(WEATHER_TARGET)
			.build(true);
	}

	public static RenderLayer getTripwire() {
		return TRIPWIRE;
	}

	public static RenderLayer getEndPortal() {
		return END_PORTAL;
	}

	public static RenderLayer method_34571() {
		return END_GATEWAY;
	}

	public static RenderLayer getLines() {
		return LINES;
	}

	public static RenderLayer method_34572() {
		return LINE_STRIP;
	}

	public RenderLayer(
		String name,
		VertexFormat vertexFormat,
		VertexFormat.DrawMode drawMode,
		int expectedBufferSize,
		boolean hasCrumbling,
		boolean translucent,
		Runnable startAction,
		Runnable endAction
	) {
		super(name, startAction, endAction);
		this.vertexFormat = vertexFormat;
		this.drawMode = drawMode;
		this.expectedBufferSize = expectedBufferSize;
		this.hasCrumbling = hasCrumbling;
		this.translucent = translucent;
		this.optionalThis = Optional.of(this);
	}

	public static RenderLayer.MultiPhase of(
		String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, RenderLayer.MultiPhaseParameters phaseData
	) {
		return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
	}

	public static RenderLayer.MultiPhase of(
		String name,
		VertexFormat vertexFormat,
		VertexFormat.DrawMode drawMode,
		int expectedBufferSize,
		boolean hasCrumbling,
		boolean translucent,
		RenderLayer.MultiPhaseParameters phases
	) {
		return RenderLayer.MultiPhase.of(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}

	public void draw(BufferBuilder buffer, int cameraX, int cameraY, int cameraZ) {
		if (buffer.isBuilding()) {
			if (this.translucent) {
				buffer.method_31948((float)cameraX, (float)cameraY, (float)cameraZ);
			}

			buffer.end();
			this.startDrawing();
			BufferRenderer.draw(buffer);
			this.endDrawing();
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static List<RenderLayer> getBlockLayers() {
		return ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent(), getTripwire());
	}

	public int getExpectedBufferSize() {
		return this.expectedBufferSize;
	}

	public VertexFormat getVertexFormat() {
		return this.vertexFormat;
	}

	public VertexFormat.DrawMode getDrawMode() {
		return this.drawMode;
	}

	public Optional<RenderLayer> getAffectedOutline() {
		return Optional.empty();
	}

	public boolean isOutline() {
		return false;
	}

	public boolean hasCrumbling() {
		return this.hasCrumbling;
	}

	public Optional<RenderLayer> asOptional() {
		return this.optionalThis;
	}

	protected boolean method_34570(RenderLayer renderLayer) {
		return super.method_34551(renderLayer)
			&& this.vertexFormat == renderLayer.vertexFormat
			&& this.drawMode == renderLayer.drawMode
			&& this.expectedBufferSize == renderLayer.expectedBufferSize
			&& this.hasCrumbling == renderLayer.hasCrumbling
			&& this.translucent == renderLayer.translucent;
	}

	@Environment(EnvType.CLIENT)
	static final class MultiPhase extends RenderLayer {
		private static final ObjectOpenCustomHashSet<RenderLayer.MultiPhase> CACHE = new ObjectOpenCustomHashSet<>(RenderLayer.MultiPhase.class_5943.INSTANCE);
		private final RenderLayer.MultiPhaseParameters phases;
		private final int hash;
		private final Optional<RenderLayer> affectedOutline;
		private final boolean outline;

		private MultiPhase(
			String name,
			VertexFormat vertexFormat,
			VertexFormat.DrawMode drawMode,
			int expectedBufferSize,
			boolean hasCrumbling,
			boolean translucent,
			RenderLayer.MultiPhaseParameters phases
		) {
			super(
				name,
				vertexFormat,
				drawMode,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				() -> phases.phases.forEach(RenderPhase::startDrawing),
				() -> phases.phases.forEach(RenderPhase::endDrawing)
			);
			this.phases = phases;
			this.affectedOutline = phases.outlineMode == RenderLayer.OutlineMode.AFFECTS_OUTLINE
				? phases.texture.getId().map(identifier -> getOutline(identifier, phases.cull))
				: Optional.empty();
			this.outline = phases.outlineMode == RenderLayer.OutlineMode.IS_OUTLINE;
			this.hash = Objects.hash(new Object[]{super.hashCode(), phases, vertexFormat, drawMode});
		}

		private static RenderLayer.MultiPhase of(
			String name,
			VertexFormat vertexFormat,
			VertexFormat.DrawMode drawMode,
			int expectedBufferSize,
			boolean hasCrumbling,
			boolean translucent,
			RenderLayer.MultiPhaseParameters phases
		) {
			return CACHE.addOrGet(new RenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases));
		}

		@Override
		public Optional<RenderLayer> getAffectedOutline() {
			return this.affectedOutline;
		}

		@Override
		public boolean isOutline() {
			return this.outline;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			return this == object;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public String toString() {
			return "RenderType[" + this.name + ":" + this.phases + ']';
		}

		protected boolean method_34573(RenderLayer.MultiPhase multiPhase) {
			return super.method_34570(multiPhase) && Objects.equals(this.phases, multiPhase.phases);
		}

		@Environment(EnvType.CLIENT)
		static enum class_5943 implements Strategy<RenderLayer.MultiPhase> {
			INSTANCE;

			public int hashCode(@Nullable RenderLayer.MultiPhase multiPhase) {
				return multiPhase == null ? 0 : multiPhase.hash;
			}

			public boolean equals(@Nullable RenderLayer.MultiPhase multiPhase, @Nullable RenderLayer.MultiPhase multiPhase2) {
				if (multiPhase == multiPhase2) {
					return true;
				} else {
					return multiPhase != null && multiPhase2 != null ? multiPhase.method_34573(multiPhase2) : false;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class MultiPhaseParameters {
		private final RenderPhase.class_5939 texture;
		private final RenderPhase.class_5942 field_29461;
		private final RenderPhase.Transparency transparency;
		private final RenderPhase.DepthTest depthTest;
		private final RenderPhase.Cull cull;
		private final RenderPhase.Lightmap lightmap;
		private final RenderPhase.Overlay overlay;
		private final RenderPhase.Layering layering;
		private final RenderPhase.Target target;
		private final RenderPhase.Texturing texturing;
		private final RenderPhase.WriteMaskState writeMaskState;
		private final RenderPhase.LineWidth lineWidth;
		private final RenderLayer.OutlineMode outlineMode;
		private final ImmutableList<RenderPhase> phases;

		private MultiPhaseParameters(
			RenderPhase.class_5939 arg,
			RenderPhase.class_5942 arg2,
			RenderPhase.Transparency transparency,
			RenderPhase.DepthTest depthTest,
			RenderPhase.Cull cull,
			RenderPhase.Lightmap lightmap,
			RenderPhase.Overlay overlay,
			RenderPhase.Layering layering,
			RenderPhase.Target target,
			RenderPhase.Texturing texturing,
			RenderPhase.WriteMaskState writeMaskState,
			RenderPhase.LineWidth lineWidth,
			RenderLayer.OutlineMode outlineMode
		) {
			this.texture = arg;
			this.field_29461 = arg2;
			this.transparency = transparency;
			this.depthTest = depthTest;
			this.cull = cull;
			this.lightmap = lightmap;
			this.overlay = overlay;
			this.layering = layering;
			this.target = target;
			this.texturing = texturing;
			this.writeMaskState = writeMaskState;
			this.lineWidth = lineWidth;
			this.outlineMode = outlineMode;
			this.phases = ImmutableList.of(
				this.texture,
				this.field_29461,
				this.transparency,
				this.depthTest,
				this.cull,
				this.lightmap,
				this.overlay,
				this.layering,
				this.target,
				this.texturing,
				this.writeMaskState,
				this.lineWidth
			);
		}

		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderLayer.MultiPhaseParameters multiPhaseParameters = (RenderLayer.MultiPhaseParameters)object;
				return this.outlineMode == multiPhaseParameters.outlineMode && this.phases.equals(multiPhaseParameters.phases);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.phases, this.outlineMode});
		}

		public String toString() {
			return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + ']';
		}

		public static RenderLayer.MultiPhaseParameters.Builder builder() {
			return new RenderLayer.MultiPhaseParameters.Builder();
		}

		@Environment(EnvType.CLIENT)
		public static class Builder {
			private RenderPhase.class_5939 field_29462 = RenderPhase.NO_TEXTURE;
			private RenderPhase.class_5942 field_29463 = RenderPhase.field_29434;
			private RenderPhase.Transparency transparency = RenderPhase.NO_TRANSPARENCY;
			private RenderPhase.DepthTest depthTest = RenderPhase.LEQUAL_DEPTH_TEST;
			private RenderPhase.Cull cull = RenderPhase.ENABLE_CULLING;
			private RenderPhase.Lightmap lightmap = RenderPhase.DISABLE_LIGHTMAP;
			private RenderPhase.Overlay overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
			private RenderPhase.Layering layering = RenderPhase.NO_LAYERING;
			private RenderPhase.Target target = RenderPhase.MAIN_TARGET;
			private RenderPhase.Texturing texturing = RenderPhase.DEFAULT_TEXTURING;
			private RenderPhase.WriteMaskState writeMaskState = RenderPhase.ALL_MASK;
			private RenderPhase.LineWidth lineWidth = RenderPhase.FULL_LINE_WIDTH;

			private Builder() {
			}

			public RenderLayer.MultiPhaseParameters.Builder method_34577(RenderPhase.class_5939 arg) {
				this.field_29462 = arg;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder method_34578(RenderPhase.class_5942 arg) {
				this.field_29463 = arg;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder transparency(RenderPhase.Transparency transparency) {
				this.transparency = transparency;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder depthTest(RenderPhase.DepthTest depthTest) {
				this.depthTest = depthTest;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder cull(RenderPhase.Cull cull) {
				this.cull = cull;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder lightmap(RenderPhase.Lightmap lightmap) {
				this.lightmap = lightmap;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder overlay(RenderPhase.Overlay overlay) {
				this.overlay = overlay;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder layering(RenderPhase.Layering layering) {
				this.layering = layering;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder target(RenderPhase.Target target) {
				this.target = target;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder texturing(RenderPhase.Texturing texturing) {
				this.texturing = texturing;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder writeMaskState(RenderPhase.WriteMaskState writeMaskState) {
				this.writeMaskState = writeMaskState;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder lineWidth(RenderPhase.LineWidth lineWidth) {
				this.lineWidth = lineWidth;
				return this;
			}

			public RenderLayer.MultiPhaseParameters build(boolean affectsOutline) {
				return this.build(affectsOutline ? RenderLayer.OutlineMode.AFFECTS_OUTLINE : RenderLayer.OutlineMode.NONE);
			}

			public RenderLayer.MultiPhaseParameters build(RenderLayer.OutlineMode outlineMode) {
				return new RenderLayer.MultiPhaseParameters(
					this.field_29462,
					this.field_29463,
					this.transparency,
					this.depthTest,
					this.cull,
					this.lightmap,
					this.overlay,
					this.layering,
					this.target,
					this.texturing,
					this.writeMaskState,
					this.lineWidth,
					outlineMode
				);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static enum OutlineMode {
		NONE("none"),
		IS_OUTLINE("is_outline"),
		AFFECTS_OUTLINE("affects_outline");

		private final String name;

		private OutlineMode(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}
}
