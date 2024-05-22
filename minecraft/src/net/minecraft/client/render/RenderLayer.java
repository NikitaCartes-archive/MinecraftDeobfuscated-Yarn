package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9801;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/**
 * Defines settings that should be used when rendering something.
 * 
 * <p>This includes {@linkplain VertexFormat vertex format}, {@linkplain
 * VertexFormat.DrawMode draw mode}, {@linkplain
 * net.minecraft.client.gl.ShaderProgram shader program}, texture,
 * some uniform variables values (such as {@code LineWidth} when using the
 * {@link GameRenderer#getRenderTypeLinesProgram rendertype_lines} shader
 * program), and some GL state values (such as whether to enable depth
 * testing).
 * 
 * <p>Before drawing something, a render layer setups these states. After
 * drawing something, a render layer resets those states to default.
 */
@Environment(EnvType.CLIENT)
public abstract class RenderLayer extends RenderPhase {
	private static final int field_32777 = 1048576;
	public static final int SOLID_BUFFER_SIZE = 4194304;
	public static final int CUTOUT_BUFFER_SIZE = 786432;
	public static final int DEFAULT_BUFFER_SIZE = 1536;
	private static final RenderLayer SOLID = of(
		"solid",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		4194304,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).program(SOLID_PROGRAM).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT_MIPPED = of(
		"cutout_mipped",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		4194304,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).program(CUTOUT_MIPPED_PROGRAM).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer CUTOUT = of(
		"cutout",
		VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
		VertexFormat.DrawMode.QUADS,
		786432,
		true,
		false,
		RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).program(CUTOUT_PROGRAM).texture(BLOCK_ATLAS_TEXTURE).build(true)
	);
	private static final RenderLayer TRANSLUCENT = of(
		"translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 786432, true, true, of(TRANSLUCENT_PROGRAM)
	);
	private static final RenderLayer TRANSLUCENT_MOVING_BLOCK = of(
		"translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 786432, false, true, getItemPhaseData()
	);
	private static final Function<Identifier, RenderLayer> ARMOR_CUTOUT_NO_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> createArmorCutoutNoCull("armor_cutout_no_cull", texture, false))
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SOLID = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_SOLID_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of("entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_CUTOUT_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of("entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_CUTOUT_NO_CULL = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_CUTOUT_NONULL_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(NO_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(affectsOutline);
			return of(
				"entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters
			);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
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
				1536,
				true,
				false,
				multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ITEM_ENTITY_TRANSLUCENT_CULL_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.target(ITEM_ENTITY_TARGET)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(RenderPhase.ALL_MASK)
				.build(true);
			return of(
				"item_entity_translucent_cull",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				1536,
				true,
				true,
				multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_TRANSLUCENT_CULL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_TRANSLUCENT_CULL_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true);
			return of(
				"entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, true, multiPhaseParameters
			);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_TRANSLUCENT_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(affectsOutline);
			return of(
				"entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, true, multiPhaseParameters
			);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.writeMaskState(COLOR_MASK)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(affectsOutline);
			return of(
				"entity_translucent_emissive",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				1536,
				true,
				true,
				multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SMOOTH_CUTOUT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_SMOOTH_CUTOUT_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.build(true);
			return of("entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, multiPhaseParameters);
		})
	);
	private static final BiFunction<Identifier, Boolean, RenderLayer> BEACON_BEAM = Util.memoize(
		(BiFunction<Identifier, Boolean, RenderLayer>)((texture, affectsOutline) -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(BEACON_BEAM_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(affectsOutline ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
				.writeMaskState(affectsOutline ? COLOR_MASK : ALL_MASK)
				.build(false);
			return of("beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, false, true, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_DECAL = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_DECAL_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.depthTest(EQUAL_DEPTH_TEST)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(false);
			return of("entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_NO_OUTLINE = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_NO_OUTLINE_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(COLOR_MASK)
				.build(false);
			return of(
				"entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, false, true, multiPhaseParameters
			);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_SHADOW = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_SHADOW_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(ENABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.writeMaskState(COLOR_MASK)
				.depthTest(LEQUAL_DEPTH_TEST)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.build(false);
			return of("entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, false, false, multiPhaseParameters);
		})
	);
	private static final Function<Identifier, RenderLayer> ENTITY_ALPHA = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
				.program(ENTITY_ALPHA_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.cull(DISABLE_CULLING)
				.build(true);
			return of("entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, multiPhaseParameters);
		})
	);
	private static final BiFunction<Identifier, RenderPhase.Transparency, RenderLayer> EYES = Util.memoize(
		(BiFunction<Identifier, RenderPhase.Transparency, RenderLayer>)((texture, transparency) -> {
			RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
			return of(
				"eyes",
				VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder().program(EYES_PROGRAM).texture(texture2).transparency(transparency).writeMaskState(COLOR_MASK).build(false)
			);
		})
	);
	private static final RenderLayer LEASH = of(
		"leash",
		VertexFormats.POSITION_COLOR_LIGHT,
		VertexFormat.DrawMode.TRIANGLE_STRIP,
		1536,
		RenderLayer.MultiPhaseParameters.builder().program(LEASH_PROGRAM).texture(NO_TEXTURE).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).build(false)
	);
	private static final RenderLayer WATER_MASK = of(
		"water_mask",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder().program(WATER_MASK_PROGRAM).texture(NO_TEXTURE).writeMaskState(DEPTH_MASK).build(false)
	);
	private static final RenderLayer ARMOR_ENTITY_GLINT = of(
		"armor_entity_glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(ARMOR_ENTITY_GLINT_PROGRAM)
			.texture(new RenderPhase.Texture(ItemRenderer.ENTITY_ENCHANTMENT_GLINT, true, false))
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
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(TRANSLUCENT_GLINT_PROGRAM)
			.texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(GLINT_TEXTURING)
			.target(ITEM_ENTITY_TARGET)
			.build(false)
	);
	private static final RenderLayer GLINT = of(
		"glint",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(GLINT_PROGRAM)
			.texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, true, false))
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
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(ENTITY_GLINT_PROGRAM)
			.texture(new RenderPhase.Texture(ItemRenderer.ENTITY_ENCHANTMENT_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.target(ITEM_ENTITY_TARGET)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final RenderLayer DIRECT_ENTITY_GLINT = of(
		"entity_glint_direct",
		VertexFormats.POSITION_TEXTURE,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(DIRECT_ENTITY_GLINT_PROGRAM)
			.texture(new RenderPhase.Texture(ItemRenderer.ENTITY_ENCHANTMENT_GLINT, true, false))
			.writeMaskState(COLOR_MASK)
			.cull(DISABLE_CULLING)
			.depthTest(EQUAL_DEPTH_TEST)
			.transparency(GLINT_TRANSPARENCY)
			.texturing(ENTITY_GLINT_TEXTURING)
			.build(false)
	);
	private static final Function<Identifier, RenderLayer> CRUMBLING = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> {
			RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false, false);
			return of(
				"crumbling",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(CRUMBLING_PROGRAM)
					.texture(texture2)
					.transparency(CRUMBLING_TRANSPARENCY)
					.writeMaskState(COLOR_MASK)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			);
		})
	);
	private static final Function<Identifier, RenderLayer> TEXT = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				786432,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TEXT_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)
			))
	);
	private static final RenderLayer TEXT_BACKGROUND = of(
		"text_background",
		VertexFormats.POSITION_COLOR_LIGHT,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.program(TEXT_BACKGROUND_PROGRAM)
			.texture(NO_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.build(false)
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				786432,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TEXT_INTENSITY_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_POLYGON_OFFSET = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_polygon_offset",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TEXT_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity_polygon_offset",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TEXT_INTENSITY_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.layering(POLYGON_OFFSET_LAYERING)
					.build(false)
			))
	);
	private static final Function<Identifier, RenderLayer> TEXT_SEE_THROUGH = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_see_through",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TRANSPARENT_TEXT_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.depthTest(ALWAYS_DEPTH_TEST)
					.writeMaskState(COLOR_MASK)
					.build(false)
			))
	);
	private static final RenderLayer TEXT_BACKGROUND_SEE_THROUGH = of(
		"text_background_see_through",
		VertexFormats.POSITION_COLOR_LIGHT,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.program(TRANSPARENT_TEXT_BACKGROUND_PROGRAM)
			.texture(NO_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.lightmap(ENABLE_LIGHTMAP)
			.depthTest(ALWAYS_DEPTH_TEST)
			.writeMaskState(COLOR_MASK)
			.build(false)
	);
	private static final Function<Identifier, RenderLayer> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
		(Function<Identifier, RenderLayer>)(texture -> of(
				"text_intensity_see_through",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
				VertexFormat.DrawMode.QUADS,
				1536,
				false,
				true,
				RenderLayer.MultiPhaseParameters.builder()
					.program(TRANSPARENT_TEXT_INTENSITY_PROGRAM)
					.texture(new RenderPhase.Texture(texture, false, false))
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.depthTest(ALWAYS_DEPTH_TEST)
					.writeMaskState(COLOR_MASK)
					.build(false)
			))
	);
	private static final RenderLayer LIGHTNING = of(
		"lightning",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.program(LIGHTNING_PROGRAM)
			.writeMaskState(ALL_MASK)
			.transparency(LIGHTNING_TRANSPARENCY)
			.target(WEATHER_TARGET)
			.build(false)
	);
	private static final RenderLayer TRIPWIRE = of(
		"tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, true, getTripwirePhaseData()
	);
	private static final RenderLayer END_PORTAL = of(
		"end_portal",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.program(END_PORTAL_PROGRAM)
			.texture(
				RenderPhase.Textures.create()
					.add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.build()
			)
			.build(false)
	);
	private static final RenderLayer END_GATEWAY = of(
		"end_gateway",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		false,
		RenderLayer.MultiPhaseParameters.builder()
			.program(END_GATEWAY_PROGRAM)
			.texture(
				RenderPhase.Textures.create()
					.add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false)
					.add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false)
					.build()
			)
			.build(false)
	);
	private static final RenderLayer FAST_CLOUDS = getClouds(false);
	private static final RenderLayer FANCY_CLOUDS = getClouds(true);
	public static final RenderLayer.MultiPhase LINES = of(
		"lines",
		VertexFormats.LINES,
		VertexFormat.DrawMode.LINES,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(LINES_PROGRAM)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_ENTITY_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	public static final RenderLayer.MultiPhase LINE_STRIP = of(
		"line_strip",
		VertexFormats.LINES,
		VertexFormat.DrawMode.LINE_STRIP,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(LINES_PROGRAM)
			.lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty()))
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_ENTITY_TARGET)
			.writeMaskState(ALL_MASK)
			.cull(DISABLE_CULLING)
			.build(false)
	);
	private static final Function<Double, RenderLayer.MultiPhase> DEBUG_LINE_STRIP = Util.memoize(
		(Function<Double, RenderLayer.MultiPhase>)(lineWidth -> of(
				"debug_line_strip",
				VertexFormats.POSITION_COLOR,
				VertexFormat.DrawMode.DEBUG_LINE_STRIP,
				1536,
				RenderLayer.MultiPhaseParameters.builder()
					.program(COLOR_PROGRAM)
					.lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
					.transparency(NO_TRANSPARENCY)
					.cull(DISABLE_CULLING)
					.build(false)
			))
	);
	private static final RenderLayer.MultiPhase DEBUG_FILLED_BOX = of(
		"debug_filled_box",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.TRIANGLE_STRIP,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder().program(COLOR_PROGRAM).layering(VIEW_OFFSET_Z_LAYERING).transparency(TRANSLUCENT_TRANSPARENCY).build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_QUADS = of(
		"debug_quads",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder().program(COLOR_PROGRAM).transparency(TRANSLUCENT_TRANSPARENCY).cull(DISABLE_CULLING).build(false)
	);
	private static final RenderLayer.MultiPhase DEBUG_SECTION_QUADS = of(
		"debug_section_quads",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		false,
		true,
		RenderLayer.MultiPhaseParameters.builder()
			.program(COLOR_PROGRAM)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.cull(ENABLE_CULLING)
			.build(false)
	);
	private static final RenderLayer.MultiPhase GUI = of(
		"gui",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		786432,
		RenderLayer.MultiPhaseParameters.builder().program(GUI_PROGRAM).transparency(TRANSLUCENT_TRANSPARENCY).depthTest(LEQUAL_DEPTH_TEST).build(false)
	);
	private static final RenderLayer.MultiPhase GUI_OVERLAY = of(
		"gui_overlay",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(GUI_OVERLAY_PROGRAM)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.depthTest(ALWAYS_DEPTH_TEST)
			.writeMaskState(COLOR_MASK)
			.build(false)
	);
	private static final RenderLayer.MultiPhase GUI_TEXT_HIGHLIGHT = of(
		"gui_text_highlight",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(GUI_TEXT_HIGHLIGHT_PROGRAM)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.depthTest(ALWAYS_DEPTH_TEST)
			.colorLogic(OR_REVERSE)
			.build(false)
	);
	private static final RenderLayer.MultiPhase GUI_GHOST_RECIPE_OVERLAY = of(
		"gui_ghost_recipe_overlay",
		VertexFormats.POSITION_COLOR,
		VertexFormat.DrawMode.QUADS,
		1536,
		RenderLayer.MultiPhaseParameters.builder()
			.program(GUI_GHOST_RECIPE_OVERLAY_PROGRAM)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.depthTest(BIGGER_DEPTH_TEST)
			.writeMaskState(COLOR_MASK)
			.build(false)
	);
	private static final ImmutableList<RenderLayer> BLOCK_LAYERS = ImmutableList.of(getSolid(), getCutoutMipped(), getCutout(), getTranslucent(), getTripwire());
	private final VertexFormat vertexFormat;
	private final VertexFormat.DrawMode drawMode;
	private final int expectedBufferSize;
	private final boolean hasCrumbling;
	private final boolean translucent;

	public static RenderLayer getSolid() {
		return SOLID;
	}

	public static RenderLayer getCutoutMipped() {
		return CUTOUT_MIPPED;
	}

	public static RenderLayer getCutout() {
		return CUTOUT;
	}

	private static RenderLayer.MultiPhaseParameters of(RenderPhase.ShaderProgram program) {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.program(program)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
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
			.program(TRANSLUCENT_MOVING_BLOCK_PROGRAM)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.target(ITEM_ENTITY_TARGET)
			.build(true);
	}

	public static RenderLayer getTranslucentMovingBlock() {
		return TRANSLUCENT_MOVING_BLOCK;
	}

	private static RenderLayer.MultiPhase createArmorCutoutNoCull(String name, Identifier texture, boolean decal) {
		RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
			.program(ARMOR_CUTOUT_NO_CULL_PROGRAM)
			.texture(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.layering(VIEW_OFFSET_Z_LAYERING)
			.depthTest(decal ? EQUAL_DEPTH_TEST : LEQUAL_DEPTH_TEST)
			.build(true);
		return of(name, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1536, true, false, multiPhaseParameters);
	}

	public static RenderLayer getArmorCutoutNoCull(Identifier texture) {
		return (RenderLayer)ARMOR_CUTOUT_NO_CULL.apply(texture);
	}

	public static RenderLayer createArmorDecalCutoutNoCull(Identifier texture) {
		return createArmorCutoutNoCull("armor_decal_cutout_no_cull", texture, true);
	}

	public static RenderLayer getEntitySolid(Identifier texture) {
		return (RenderLayer)ENTITY_SOLID.apply(texture);
	}

	public static RenderLayer getEntityCutout(Identifier texture) {
		return (RenderLayer)ENTITY_CUTOUT.apply(texture);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_CUTOUT_NO_CULL.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityCutoutNoCull(Identifier texture) {
		return getEntityCutoutNoCull(texture, true);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture) {
		return getEntityCutoutNoCullZOffset(texture, true);
	}

	public static RenderLayer getItemEntityTranslucentCull(Identifier texture) {
		return (RenderLayer)ITEM_ENTITY_TRANSLUCENT_CULL.apply(texture);
	}

	public static RenderLayer getEntityTranslucentCull(Identifier texture) {
		return (RenderLayer)ENTITY_TRANSLUCENT_CULL.apply(texture);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_TRANSLUCENT.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityTranslucent(Identifier texture) {
		return getEntityTranslucent(texture, true);
	}

	public static RenderLayer getEntityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
		return (RenderLayer)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
	}

	public static RenderLayer getEntityTranslucentEmissive(Identifier texture) {
		return getEntityTranslucentEmissive(texture, true);
	}

	public static RenderLayer getEntitySmoothCutout(Identifier texture) {
		return (RenderLayer)ENTITY_SMOOTH_CUTOUT.apply(texture);
	}

	public static RenderLayer getBeaconBeam(Identifier texture, boolean translucent) {
		return (RenderLayer)BEACON_BEAM.apply(texture, translucent);
	}

	public static RenderLayer getEntityDecal(Identifier texture) {
		return (RenderLayer)ENTITY_DECAL.apply(texture);
	}

	public static RenderLayer getEntityNoOutline(Identifier texture) {
		return (RenderLayer)ENTITY_NO_OUTLINE.apply(texture);
	}

	public static RenderLayer getEntityShadow(Identifier texture) {
		return (RenderLayer)ENTITY_SHADOW.apply(texture);
	}

	public static RenderLayer getEntityAlpha(Identifier texture) {
		return (RenderLayer)ENTITY_ALPHA.apply(texture);
	}

	public static RenderLayer getEyes(Identifier texture) {
		return (RenderLayer)EYES.apply(texture, ADDITIVE_TRANSPARENCY);
	}

	public static RenderLayer getEntityTranslucentEmissiveNoOutline(Identifier texture) {
		return (RenderLayer)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, false);
	}

	public static RenderLayer getBreezeWind(Identifier texture, float x, float y) {
		return of(
			"breeze_wind",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			1536,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(BREEZE_WIND_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
				.texturing(new RenderPhase.OffsetTexturing(x, y))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(DISABLE_OVERLAY_COLOR)
				.build(false)
		);
	}

	public static RenderLayer getEnergySwirl(Identifier texture, float x, float y) {
		return of(
			"energy_swirl",
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			1536,
			false,
			true,
			RenderLayer.MultiPhaseParameters.builder()
				.program(ENERGY_SWIRL_PROGRAM)
				.texture(new RenderPhase.Texture(texture, false, false))
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
		return (RenderLayer)RenderLayer.MultiPhase.CULLING_LAYERS.apply(texture, DISABLE_CULLING);
	}

	public static RenderLayer getArmorEntityGlint() {
		return ARMOR_ENTITY_GLINT;
	}

	public static RenderLayer getGlintTranslucent() {
		return GLINT_TRANSLUCENT;
	}

	public static RenderLayer getGlint() {
		return GLINT;
	}

	public static RenderLayer getEntityGlint() {
		return ENTITY_GLINT;
	}

	public static RenderLayer getDirectEntityGlint() {
		return DIRECT_ENTITY_GLINT;
	}

	public static RenderLayer getBlockBreaking(Identifier texture) {
		return (RenderLayer)CRUMBLING.apply(texture);
	}

	public static RenderLayer getText(Identifier texture) {
		return (RenderLayer)TEXT.apply(texture);
	}

	public static RenderLayer getTextBackground() {
		return TEXT_BACKGROUND;
	}

	public static RenderLayer getTextIntensity(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY.apply(texture);
	}

	public static RenderLayer getTextPolygonOffset(Identifier texture) {
		return (RenderLayer)TEXT_POLYGON_OFFSET.apply(texture);
	}

	public static RenderLayer getTextIntensityPolygonOffset(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY_POLYGON_OFFSET.apply(texture);
	}

	public static RenderLayer getTextSeeThrough(Identifier texture) {
		return (RenderLayer)TEXT_SEE_THROUGH.apply(texture);
	}

	public static RenderLayer getTextBackgroundSeeThrough() {
		return TEXT_BACKGROUND_SEE_THROUGH;
	}

	public static RenderLayer getTextIntensitySeeThrough(Identifier texture) {
		return (RenderLayer)TEXT_INTENSITY_SEE_THROUGH.apply(texture);
	}

	public static RenderLayer getLightning() {
		return LIGHTNING;
	}

	private static RenderLayer.MultiPhaseParameters getTripwirePhaseData() {
		return RenderLayer.MultiPhaseParameters.builder()
			.lightmap(ENABLE_LIGHTMAP)
			.program(TRIPWIRE_PROGRAM)
			.texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
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

	public static RenderLayer getEndGateway() {
		return END_GATEWAY;
	}

	private static RenderLayer.MultiPhase getClouds(boolean fancy) {
		return of(
			"clouds",
			VertexFormats.POSITION_TEXTURE_COLOR_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			false,
			false,
			RenderLayer.MultiPhaseParameters.builder()
				.program(CLOUDS_PROGRAM)
				.texture(new RenderPhase.Texture(WorldRenderer.CLOUDS, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.writeMaskState(fancy ? DEPTH_MASK : ALL_MASK)
				.target(CLOUDS_TARGET)
				.build(true)
		);
	}

	public static RenderLayer getFastClouds() {
		return FAST_CLOUDS;
	}

	public static RenderLayer getFancyClouds() {
		return FANCY_CLOUDS;
	}

	public static RenderLayer getLines() {
		return LINES;
	}

	public static RenderLayer getLineStrip() {
		return LINE_STRIP;
	}

	public static RenderLayer getDebugLineStrip(double lineWidth) {
		return (RenderLayer)DEBUG_LINE_STRIP.apply(lineWidth);
	}

	public static RenderLayer getDebugFilledBox() {
		return DEBUG_FILLED_BOX;
	}

	public static RenderLayer getDebugQuads() {
		return DEBUG_QUADS;
	}

	public static RenderLayer getDebugSectionQuads() {
		return DEBUG_SECTION_QUADS;
	}

	public static RenderLayer getGui() {
		return GUI;
	}

	public static RenderLayer getGuiOverlay() {
		return GUI_OVERLAY;
	}

	public static RenderLayer getGuiTextHighlight() {
		return GUI_TEXT_HIGHLIGHT;
	}

	public static RenderLayer getGuiGhostRecipeOverlay() {
		return GUI_GHOST_RECIPE_OVERLAY;
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
	}

	static RenderLayer.MultiPhase of(
		String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, RenderLayer.MultiPhaseParameters phaseData
	) {
		return of(name, vertexFormat, drawMode, expectedBufferSize, false, false, phaseData);
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
		return new RenderLayer.MultiPhase(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
	}

	public void method_60895(class_9801 arg) {
		this.startDrawing();
		BufferRenderer.drawWithGlobalProgram(arg);
		this.endDrawing();
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static List<RenderLayer> getBlockLayers() {
		return BLOCK_LAYERS;
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

	public boolean areVerticesNotShared() {
		return !this.drawMode.shareVertices;
	}

	public boolean method_60894() {
		return this.translucent;
	}

	@Environment(EnvType.CLIENT)
	static final class MultiPhase extends RenderLayer {
		static final BiFunction<Identifier, RenderPhase.Cull, RenderLayer> CULLING_LAYERS = Util.memoize(
			(BiFunction<Identifier, RenderPhase.Cull, RenderLayer>)((texture, culling) -> RenderLayer.of(
					"outline",
					VertexFormats.POSITION_TEXTURE_COLOR,
					VertexFormat.DrawMode.QUADS,
					1536,
					RenderLayer.MultiPhaseParameters.builder()
						.program(OUTLINE_PROGRAM)
						.texture(new RenderPhase.Texture(texture, false, false))
						.cull(culling)
						.depthTest(ALWAYS_DEPTH_TEST)
						.target(OUTLINE_TARGET)
						.build(RenderLayer.OutlineMode.IS_OUTLINE)
				))
		);
		private final RenderLayer.MultiPhaseParameters phases;
		private final Optional<RenderLayer> affectedOutline;
		private final boolean outline;

		MultiPhase(
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
				? phases.texture.getId().map(texture -> (RenderLayer)CULLING_LAYERS.apply(texture, phases.cull))
				: Optional.empty();
			this.outline = phases.outlineMode == RenderLayer.OutlineMode.IS_OUTLINE;
		}

		@Override
		public Optional<RenderLayer> getAffectedOutline() {
			return this.affectedOutline;
		}

		@Override
		public boolean isOutline() {
			return this.outline;
		}

		protected final RenderLayer.MultiPhaseParameters getPhases() {
			return this.phases;
		}

		@Override
		public String toString() {
			return "RenderType[" + this.name + ":" + this.phases + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	protected static final class MultiPhaseParameters {
		final RenderPhase.TextureBase texture;
		private final RenderPhase.ShaderProgram program;
		private final RenderPhase.Transparency transparency;
		private final RenderPhase.DepthTest depthTest;
		final RenderPhase.Cull cull;
		private final RenderPhase.Lightmap lightmap;
		private final RenderPhase.Overlay overlay;
		private final RenderPhase.Layering layering;
		private final RenderPhase.Target target;
		private final RenderPhase.Texturing texturing;
		private final RenderPhase.WriteMaskState writeMaskState;
		private final RenderPhase.LineWidth lineWidth;
		private final RenderPhase.ColorLogic colorLogic;
		final RenderLayer.OutlineMode outlineMode;
		final ImmutableList<RenderPhase> phases;

		MultiPhaseParameters(
			RenderPhase.TextureBase texture,
			RenderPhase.ShaderProgram program,
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
			RenderPhase.ColorLogic colorLogic,
			RenderLayer.OutlineMode outlineMode
		) {
			this.texture = texture;
			this.program = program;
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
			this.colorLogic = colorLogic;
			this.outlineMode = outlineMode;
			this.phases = ImmutableList.of(
				this.texture,
				this.program,
				this.transparency,
				this.depthTest,
				this.cull,
				this.lightmap,
				this.overlay,
				this.layering,
				this.target,
				this.texturing,
				this.writeMaskState,
				this.colorLogic,
				this.lineWidth
			);
		}

		public String toString() {
			return "CompositeState[" + this.phases + ", outlineProperty=" + this.outlineMode + "]";
		}

		public static RenderLayer.MultiPhaseParameters.Builder builder() {
			return new RenderLayer.MultiPhaseParameters.Builder();
		}

		@Environment(EnvType.CLIENT)
		public static class Builder {
			private RenderPhase.TextureBase texture = RenderPhase.NO_TEXTURE;
			private RenderPhase.ShaderProgram program = RenderPhase.NO_PROGRAM;
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
			private RenderPhase.ColorLogic colorLogic = RenderPhase.NO_COLOR_LOGIC;

			Builder() {
			}

			public RenderLayer.MultiPhaseParameters.Builder texture(RenderPhase.TextureBase texture) {
				this.texture = texture;
				return this;
			}

			public RenderLayer.MultiPhaseParameters.Builder program(RenderPhase.ShaderProgram program) {
				this.program = program;
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

			public RenderLayer.MultiPhaseParameters.Builder colorLogic(RenderPhase.ColorLogic colorLogic) {
				this.colorLogic = colorLogic;
				return this;
			}

			public RenderLayer.MultiPhaseParameters build(boolean affectsOutline) {
				return this.build(affectsOutline ? RenderLayer.OutlineMode.AFFECTS_OUTLINE : RenderLayer.OutlineMode.NONE);
			}

			public RenderLayer.MultiPhaseParameters build(RenderLayer.OutlineMode outlineMode) {
				return new RenderLayer.MultiPhaseParameters(
					this.texture,
					this.program,
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
					this.colorLogic,
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

		private OutlineMode(final String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}
}
