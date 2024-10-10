package net.minecraft.client.gl;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ShaderProgramKeys {
	private static final List<ShaderProgramKey> ALL = new ArrayList();
	public static final ShaderProgramKey BLIT_SCREEN = register("blit_screen", VertexFormats.BLIT_SCREEN);
	public static final ShaderProgramKey LIGHTMAP = register("lightmap", VertexFormats.BLIT_SCREEN);
	public static final ShaderProgramKey PARTICLE = register("particle", VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
	public static final ShaderProgramKey POSITION = register("position", VertexFormats.POSITION);
	public static final ShaderProgramKey POSITION_COLOR = register("position_color", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey POSITION_COLOR_LIGHTMAP = register("position_color_lightmap", VertexFormats.POSITION_COLOR_LIGHT);
	public static final ShaderProgramKey POSITION_COLOR_TEX_LIGHTMAP = register("position_color_tex_lightmap", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
	public static final ShaderProgramKey POSITION_TEX = register("position_tex", VertexFormats.POSITION_TEXTURE);
	public static final ShaderProgramKey POSITION_TEX_COLOR = register("position_tex_color", VertexFormats.POSITION_TEXTURE_COLOR);
	public static final ShaderProgramKey RENDERTYPE_SOLID = register("rendertype_solid", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_CUTOUT_MIPPED = register("rendertype_cutout_mipped", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_CUTOUT = register("rendertype_cutout", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_TRANSLUCENT = register("rendertype_translucent", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_TRANSLUCENT_MOVING_BLOCK = register(
		"rendertype_translucent_moving_block", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ARMOR_CUTOUT_NO_CULL = register(
		"rendertype_armor_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ARMOR_TRANSLUCENT = register(
		"rendertype_armor_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_SOLID = register("rendertype_entity_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT = register("rendertype_entity_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT_NO_CULL = register(
		"rendertype_entity_cutout_no_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET = register(
		"rendertype_entity_cutout_no_cull_z_offset", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL = register(
		"rendertype_item_entity_translucent_cull", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_TRANSLUCENT = register(
		"rendertype_entity_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE = register(
		"rendertype_entity_translucent_emissive", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_SMOOTH_CUTOUT = register(
		"rendertype_entity_smooth_cutout", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_BEACON_BEAM = register("rendertype_beacon_beam", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_DECAL = register("rendertype_entity_decal", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_NO_OUTLINE = register(
		"rendertype_entity_no_outline", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
	);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_SHADOW = register("rendertype_entity_shadow", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_ALPHA = register("rendertype_entity_alpha", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_EYES = register("rendertype_eyes", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_ENERGY_SWIRL = register("rendertype_energy_swirl", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_LEASH = register("rendertype_leash", VertexFormats.POSITION_COLOR_LIGHT);
	public static final ShaderProgramKey RENDERTYPE_WATER_MASK = register("rendertype_water_mask", VertexFormats.POSITION);
	public static final ShaderProgramKey RENDERTYPE_OUTLINE = register("rendertype_outline", VertexFormats.POSITION_TEXTURE_COLOR);
	public static final ShaderProgramKey RENDERTYPE_ARMOR_ENTITY_GLINT = register("rendertype_armor_entity_glint", VertexFormats.POSITION_TEXTURE);
	public static final ShaderProgramKey RENDERTYPE_GLINT_TRANSLUCENT = register("rendertype_glint_translucent", VertexFormats.POSITION_TEXTURE);
	public static final ShaderProgramKey RENDERTYPE_GLINT = register("rendertype_glint", VertexFormats.POSITION_TEXTURE);
	public static final ShaderProgramKey RENDERTYPE_ENTITY_GLINT = register("rendertype_entity_glint", VertexFormats.POSITION_TEXTURE);
	public static final ShaderProgramKey RENDERTYPE_TEXT = register("rendertype_text", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
	public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND = register("rendertype_text_background", VertexFormats.POSITION_COLOR_LIGHT);
	public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY = register("rendertype_text_intensity", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
	public static final ShaderProgramKey RENDERTYPE_TEXT_SEE_THROUGH = register("rendertype_text_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
	public static final ShaderProgramKey RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH = register(
		"rendertype_text_background_see_through", VertexFormats.POSITION_COLOR_LIGHT
	);
	public static final ShaderProgramKey RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH = register(
		"rendertype_text_intensity_see_through", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT
	);
	public static final ShaderProgramKey RENDERTYPE_LIGHTNING = register("rendertype_lightning", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_TRIPWIRE = register("rendertype_tripwire", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_END_PORTAL = register("rendertype_end_portal", VertexFormats.POSITION);
	public static final ShaderProgramKey RENDERTYPE_END_GATEWAY = register("rendertype_end_gateway", VertexFormats.POSITION);
	public static final ShaderProgramKey RENDERTYPE_CLOUDS = register("rendertype_clouds", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_LINES = register("rendertype_lines", VertexFormats.LINES);
	public static final ShaderProgramKey RENDERTYPE_CRUMBLING = register("rendertype_crumbling", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
	public static final ShaderProgramKey RENDERTYPE_GUI = register("rendertype_gui", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_GUI_OVERLAY = register("rendertype_gui_overlay", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_GUI_TEXT_HIGHLIGHT = register("rendertype_gui_text_highlight", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY = register("rendertype_gui_ghost_recipe_overlay", VertexFormats.POSITION_COLOR);
	public static final ShaderProgramKey RENDERTYPE_BREEZE_WIND = register("rendertype_breeze_wind", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);

	private static ShaderProgramKey register(String id, VertexFormat format) {
		return register(id, format, Defines.EMPTY);
	}

	private static ShaderProgramKey register(String is, VertexFormat format, Defines defines) {
		ShaderProgramKey shaderProgramKey = new ShaderProgramKey(Identifier.ofVanilla("core/" + is), format, defines);
		ALL.add(shaderProgramKey);
		return shaderProgramKey;
	}

	public static List<ShaderProgramKey> getAll() {
		return ALL;
	}
}
