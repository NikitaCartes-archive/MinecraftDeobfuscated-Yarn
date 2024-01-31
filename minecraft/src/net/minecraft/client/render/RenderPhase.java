package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public abstract class RenderPhase {
	private static final float VIEW_OFFSET_Z_LAYERING_SCALE = 0.99975586F;
	public static final double field_42230 = 8.0;
	protected final String name;
	private final Runnable beginAction;
	private final Runnable endAction;
	protected static final RenderPhase.Transparency NO_TRANSPARENCY = new RenderPhase.Transparency("no_transparency", () -> RenderSystem.disableBlend(), () -> {
	});
	protected static final RenderPhase.Transparency ADDITIVE_TRANSPARENCY = new RenderPhase.Transparency("additive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	protected static final RenderPhase.Transparency LIGHTNING_TRANSPARENCY = new RenderPhase.Transparency("lightning_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	protected static final RenderPhase.Transparency GLINT_TRANSPARENCY = new RenderPhase.Transparency("glint_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});
	protected static final RenderPhase.Transparency CRUMBLING_TRANSPARENCY = new RenderPhase.Transparency(
		"crumbling_transparency",
		() -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
			);
		},
		() -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	);
	protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency(
		"translucent_transparency",
		() -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.SRC_ALPHA,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SrcFactor.ONE,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
			);
		},
		() -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}
	);
	protected static final RenderPhase.ShaderProgram NO_PROGRAM = new RenderPhase.ShaderProgram();
	protected static final RenderPhase.ShaderProgram POSITION_COLOR_LIGHTMAP_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionColorLightmapProgram);
	protected static final RenderPhase.ShaderProgram POSITION_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionProgram);
	protected static final RenderPhase.ShaderProgram POSITION_COLOR_TEXTURE_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionColorTexProgram);
	protected static final RenderPhase.ShaderProgram POSITION_TEXTURE_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionTexProgram);
	protected static final RenderPhase.ShaderProgram POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getPositionColorTexLightmapProgram
	);
	protected static final RenderPhase.ShaderProgram COLOR_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getPositionColorProgram);
	protected static final RenderPhase.ShaderProgram SOLID_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeSolidProgram);
	protected static final RenderPhase.ShaderProgram CUTOUT_MIPPED_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeCutoutMippedProgram);
	protected static final RenderPhase.ShaderProgram CUTOUT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeCutoutProgram);
	protected static final RenderPhase.ShaderProgram TRANSLUCENT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTranslucentProgram);
	protected static final RenderPhase.ShaderProgram TRANSLUCENT_MOVING_BLOCK_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeTranslucentMovingBlockProgram
	);
	protected static final RenderPhase.ShaderProgram ARMOR_CUTOUT_NO_CULL_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeArmorCutoutNoCullProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_SOLID_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntitySolidProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_CUTOUT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityCutoutProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_CUTOUT_NONULL_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityCutoutNoNullProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityCutoutNoNullZOffsetProgram
	);
	protected static final RenderPhase.ShaderProgram ITEM_ENTITY_TRANSLUCENT_CULL_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeItemEntityTranslucentCullProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_TRANSLUCENT_CULL_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityTranslucentCullProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_TRANSLUCENT_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityTranslucentProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityTranslucentEmissiveProgram
	);
	protected static final RenderPhase.ShaderProgram ENTITY_SMOOTH_CUTOUT_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntitySmoothCutoutProgram
	);
	protected static final RenderPhase.ShaderProgram BEACON_BEAM_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeBeaconBeamProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_DECAL_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityDecalProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_NO_OUTLINE_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityNoOutlineProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_SHADOW_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityShadowProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_ALPHA_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityAlphaProgram);
	protected static final RenderPhase.ShaderProgram EYES_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEyesProgram);
	protected static final RenderPhase.ShaderProgram ENERGY_SWIRL_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEnergySwirlProgram);
	protected static final RenderPhase.ShaderProgram LEASH_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeLeashProgram);
	protected static final RenderPhase.ShaderProgram WATER_MASK_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeWaterMaskProgram);
	protected static final RenderPhase.ShaderProgram OUTLINE_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeOutlineProgram);
	protected static final RenderPhase.ShaderProgram ARMOR_GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeArmorGlintProgram);
	protected static final RenderPhase.ShaderProgram ARMOR_ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeArmorEntityGlintProgram);
	protected static final RenderPhase.ShaderProgram TRANSLUCENT_GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGlintTranslucentProgram);
	protected static final RenderPhase.ShaderProgram GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGlintProgram);
	protected static final RenderPhase.ShaderProgram DIRECT_GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGlintDirectProgram);
	protected static final RenderPhase.ShaderProgram ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityGlintProgram);
	protected static final RenderPhase.ShaderProgram DIRECT_ENTITY_GLINT_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeEntityGlintDirectProgram
	);
	protected static final RenderPhase.ShaderProgram CRUMBLING_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeCrumblingProgram);
	protected static final RenderPhase.ShaderProgram TEXT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTextProgram);
	protected static final RenderPhase.ShaderProgram TEXT_BACKGROUND_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTextBackgroundProgram);
	protected static final RenderPhase.ShaderProgram TEXT_INTENSITY_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTextIntensityProgram);
	protected static final RenderPhase.ShaderProgram TRANSPARENT_TEXT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTextSeeThroughProgram);
	protected static final RenderPhase.ShaderProgram TRANSPARENT_TEXT_BACKGROUND_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeTextBackgroundSeeThroughProgram
	);
	protected static final RenderPhase.ShaderProgram TRANSPARENT_TEXT_INTENSITY_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeTextIntensitySeeThroughProgram
	);
	protected static final RenderPhase.ShaderProgram LIGHTNING_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeLightningProgram);
	protected static final RenderPhase.ShaderProgram TRIPWIRE_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeTripwireProgram);
	protected static final RenderPhase.ShaderProgram END_PORTAL_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEndPortalProgram);
	protected static final RenderPhase.ShaderProgram END_GATEWAY_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEndGatewayProgram);
	protected static final RenderPhase.ShaderProgram CLOUDS_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeCloudsProgram);
	protected static final RenderPhase.ShaderProgram LINES_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeLinesProgram);
	protected static final RenderPhase.ShaderProgram GUI_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGuiProgram);
	protected static final RenderPhase.ShaderProgram GUI_OVERLAY_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGuiOverlayProgram);
	protected static final RenderPhase.ShaderProgram GUI_TEXT_HIGHLIGHT_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeGuiTextHighlightProgram);
	protected static final RenderPhase.ShaderProgram GUI_GHOST_RECIPE_OVERLAY_PROGRAM = new RenderPhase.ShaderProgram(
		GameRenderer::getRenderTypeGuiGhostRecipeOverlayProgram
	);
	protected static final RenderPhase.ShaderProgram BREEZE_WIND_PROGRAM = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeBreezeWindProgram);
	protected static final RenderPhase.Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true);
	protected static final RenderPhase.Texture BLOCK_ATLAS_TEXTURE = new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false);
	protected static final RenderPhase.TextureBase NO_TEXTURE = new RenderPhase.TextureBase();
	protected static final RenderPhase.Texturing DEFAULT_TEXTURING = new RenderPhase.Texturing("default_texturing", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Texturing GLINT_TEXTURING = new RenderPhase.Texturing(
		"glint_texturing", () -> setupGlintTexturing(8.0F), () -> RenderSystem.resetTextureMatrix()
	);
	protected static final RenderPhase.Texturing ENTITY_GLINT_TEXTURING = new RenderPhase.Texturing(
		"entity_glint_texturing", () -> setupGlintTexturing(0.16F), () -> RenderSystem.resetTextureMatrix()
	);
	protected static final RenderPhase.Lightmap ENABLE_LIGHTMAP = new RenderPhase.Lightmap(true);
	protected static final RenderPhase.Lightmap DISABLE_LIGHTMAP = new RenderPhase.Lightmap(false);
	protected static final RenderPhase.Overlay ENABLE_OVERLAY_COLOR = new RenderPhase.Overlay(true);
	protected static final RenderPhase.Overlay DISABLE_OVERLAY_COLOR = new RenderPhase.Overlay(false);
	protected static final RenderPhase.Cull ENABLE_CULLING = new RenderPhase.Cull(true);
	protected static final RenderPhase.Cull DISABLE_CULLING = new RenderPhase.Cull(false);
	protected static final RenderPhase.DepthTest ALWAYS_DEPTH_TEST = new RenderPhase.DepthTest("always", 519);
	protected static final RenderPhase.DepthTest EQUAL_DEPTH_TEST = new RenderPhase.DepthTest("==", 514);
	protected static final RenderPhase.DepthTest LEQUAL_DEPTH_TEST = new RenderPhase.DepthTest("<=", 515);
	protected static final RenderPhase.DepthTest BIGGER_DEPTH_TEST = new RenderPhase.DepthTest(">", 516);
	protected static final RenderPhase.WriteMaskState ALL_MASK = new RenderPhase.WriteMaskState(true, true);
	protected static final RenderPhase.WriteMaskState COLOR_MASK = new RenderPhase.WriteMaskState(true, false);
	protected static final RenderPhase.WriteMaskState DEPTH_MASK = new RenderPhase.WriteMaskState(false, true);
	protected static final RenderPhase.Layering NO_LAYERING = new RenderPhase.Layering("no_layering", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Layering POLYGON_OFFSET_LAYERING = new RenderPhase.Layering("polygon_offset_layering", () -> {
		RenderSystem.polygonOffset(-1.0F, -10.0F);
		RenderSystem.enablePolygonOffset();
	}, () -> {
		RenderSystem.polygonOffset(0.0F, 0.0F);
		RenderSystem.disablePolygonOffset();
	});
	protected static final RenderPhase.Layering VIEW_OFFSET_Z_LAYERING = new RenderPhase.Layering("view_offset_z_layering", () -> {
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.pushMatrix();
		matrix4fStack.scale(0.99975586F, 0.99975586F, 0.99975586F);
		RenderSystem.applyModelViewMatrix();
	}, () -> {
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.popMatrix();
		RenderSystem.applyModelViewMatrix();
	});
	protected static final RenderPhase.Target MAIN_TARGET = new RenderPhase.Target("main_target", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Target OUTLINE_TARGET = new RenderPhase.Target(
		"outline_target",
		() -> MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer().beginWrite(false),
		() -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)
	);
	protected static final RenderPhase.Target TRANSLUCENT_TARGET = new RenderPhase.Target("translucent_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.Target PARTICLES_TARGET = new RenderPhase.Target("particles_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getParticlesFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.Target WEATHER_TARGET = new RenderPhase.Target("weather_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.Target CLOUDS_TARGET = new RenderPhase.Target("clouds_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.Target ITEM_ENTITY_TARGET = new RenderPhase.Target("item_entity_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.LineWidth FULL_LINE_WIDTH = new RenderPhase.LineWidth(OptionalDouble.of(1.0));
	protected static final RenderPhase.ColorLogic NO_COLOR_LOGIC = new RenderPhase.ColorLogic("no_color_logic", () -> RenderSystem.disableColorLogicOp(), () -> {
	});
	protected static final RenderPhase.ColorLogic OR_REVERSE = new RenderPhase.ColorLogic("or_reverse", () -> {
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
	}, () -> RenderSystem.disableColorLogicOp());

	public RenderPhase(String name, Runnable beginAction, Runnable endAction) {
		this.name = name;
		this.beginAction = beginAction;
		this.endAction = endAction;
	}

	public void startDrawing() {
		this.beginAction.run();
	}

	public void endDrawing() {
		this.endAction.run();
	}

	public String toString() {
		return this.name;
	}

	private static void setupGlintTexturing(float scale) {
		long l = (long)((double)Util.getMeasuringTimeMs() * MinecraftClient.getInstance().options.getGlintSpeed().getValue() * 8.0);
		float f = (float)(l % 110000L) / 110000.0F;
		float g = (float)(l % 30000L) / 30000.0F;
		Matrix4f matrix4f = new Matrix4f().translation(-f, g, 0.0F);
		matrix4f.rotateZ((float) (Math.PI / 18)).scale(scale);
		RenderSystem.setTextureMatrix(matrix4f);
	}

	@Environment(EnvType.CLIENT)
	protected static class ColorLogic extends RenderPhase {
		public ColorLogic(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Cull extends RenderPhase.Toggleable {
		public Cull(boolean culling) {
			super("cull", () -> {
				if (!culling) {
					RenderSystem.disableCull();
				}
			}, () -> {
				if (!culling) {
					RenderSystem.enableCull();
				}
			}, culling);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class DepthTest extends RenderPhase {
		/**
		 * A string representation of the comparison function used by this {@code DepthTest} phase.
		 * @see org.lwjgl.opengl.GL11#glDepthFunc(int)
		 */
		private final String depthFunctionName;

		public DepthTest(String depthFunctionName, int depthFunction) {
			super("depth_test", () -> {
				if (depthFunction != 519) {
					RenderSystem.enableDepthTest();
					RenderSystem.depthFunc(depthFunction);
				}
			}, () -> {
				if (depthFunction != 519) {
					RenderSystem.disableDepthTest();
					RenderSystem.depthFunc(515);
				}
			});
			this.depthFunctionName = depthFunctionName;
		}

		@Override
		public String toString() {
			return this.name + "[" + this.depthFunctionName + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Layering extends RenderPhase {
		public Layering(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Lightmap extends RenderPhase.Toggleable {
		public Lightmap(boolean lightmap) {
			super("lightmap", () -> {
				if (lightmap) {
					MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
				}
			}, () -> {
				if (lightmap) {
					MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
				}
			}, lightmap);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class LineWidth extends RenderPhase {
		private final OptionalDouble width;

		public LineWidth(OptionalDouble width) {
			super("line_width", () -> {
				if (!Objects.equals(width, OptionalDouble.of(1.0))) {
					if (width.isPresent()) {
						RenderSystem.lineWidth((float)width.getAsDouble());
					} else {
						RenderSystem.lineWidth(Math.max(2.5F, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
					}
				}
			}, () -> {
				if (!Objects.equals(width, OptionalDouble.of(1.0))) {
					RenderSystem.lineWidth(1.0F);
				}
			});
			this.width = width;
		}

		@Override
		public String toString() {
			return this.name + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	protected static final class OffsetTexturing extends RenderPhase.Texturing {
		public OffsetTexturing(float x, float y) {
			super("offset_texturing", () -> RenderSystem.setTextureMatrix(new Matrix4f().translation(x, y, 0.0F)), () -> RenderSystem.resetTextureMatrix());
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Overlay extends RenderPhase.Toggleable {
		public Overlay(boolean overlayColor) {
			super("overlay", () -> {
				if (overlayColor) {
					MinecraftClient.getInstance().gameRenderer.getOverlayTexture().setupOverlayColor();
				}
			}, () -> {
				if (overlayColor) {
					MinecraftClient.getInstance().gameRenderer.getOverlayTexture().teardownOverlayColor();
				}
			}, overlayColor);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class ShaderProgram extends RenderPhase {
		private final Optional<Supplier<net.minecraft.client.gl.ShaderProgram>> supplier;

		public ShaderProgram(Supplier<net.minecraft.client.gl.ShaderProgram> supplier) {
			super("shader", () -> RenderSystem.setShader(supplier), () -> {
			});
			this.supplier = Optional.of(supplier);
		}

		public ShaderProgram() {
			super("shader", () -> RenderSystem.setShader(() -> null), () -> {
			});
			this.supplier = Optional.empty();
		}

		@Override
		public String toString() {
			return this.name + "[" + this.supplier + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Target extends RenderPhase {
		public Target(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Texture extends RenderPhase.TextureBase {
		private final Optional<Identifier> id;
		private final boolean blur;
		private final boolean mipmap;

		public Texture(Identifier id, boolean blur, boolean mipmap) {
			super(() -> {
				TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
				textureManager.getTexture(id).setFilter(blur, mipmap);
				RenderSystem.setShaderTexture(0, id);
			}, () -> {
			});
			this.id = Optional.of(id);
			this.blur = blur;
			this.mipmap = mipmap;
		}

		@Override
		public String toString() {
			return this.name + "[" + this.id + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
		}

		@Override
		protected Optional<Identifier> getId() {
			return this.id;
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class TextureBase extends RenderPhase {
		public TextureBase(Runnable apply, Runnable unapply) {
			super("texture", apply, unapply);
		}

		TextureBase() {
			super("texture", () -> {
			}, () -> {
			});
		}

		protected Optional<Identifier> getId() {
			return Optional.empty();
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Textures extends RenderPhase.TextureBase {
		private final Optional<Identifier> id;

		Textures(ImmutableList<Triple<Identifier, Boolean, Boolean>> textures) {
			super(() -> {
				int i = 0;

				for (Triple<Identifier, Boolean, Boolean> triple : textures) {
					TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
					textureManager.getTexture(triple.getLeft()).setFilter(triple.getMiddle(), triple.getRight());
					RenderSystem.setShaderTexture(i++, triple.getLeft());
				}
			}, () -> {
			});
			this.id = textures.stream().findFirst().map(Triple::getLeft);
		}

		@Override
		protected Optional<Identifier> getId() {
			return this.id;
		}

		public static RenderPhase.Textures.Builder create() {
			return new RenderPhase.Textures.Builder();
		}

		@Environment(EnvType.CLIENT)
		public static final class Builder {
			private final ImmutableList.Builder<Triple<Identifier, Boolean, Boolean>> textures = new ImmutableList.Builder<>();

			public RenderPhase.Textures.Builder add(Identifier id, boolean blur, boolean mipmap) {
				this.textures.add(Triple.of(id, blur, mipmap));
				return this;
			}

			public RenderPhase.Textures build() {
				return new RenderPhase.Textures(this.textures.build());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Texturing extends RenderPhase {
		public Texturing(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Toggleable extends RenderPhase {
		private final boolean enabled;

		public Toggleable(String name, Runnable apply, Runnable unapply, boolean enabled) {
			super(name, apply, unapply);
			this.enabled = enabled;
		}

		@Override
		public String toString() {
			return this.name + "[" + this.enabled + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class Transparency extends RenderPhase {
		public Transparency(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	protected static class WriteMaskState extends RenderPhase {
		private final boolean color;
		private final boolean depth;

		public WriteMaskState(boolean color, boolean depth) {
			super("write_mask_state", () -> {
				if (!depth) {
					RenderSystem.depthMask(depth);
				}

				if (!color) {
					RenderSystem.colorMask(color, color, color, color);
				}
			}, () -> {
				if (!depth) {
					RenderSystem.depthMask(true);
				}

				if (!color) {
					RenderSystem.colorMask(true, true, true, true);
				}
			});
			this.color = color;
			this.depth = depth;
		}

		@Override
		public String toString() {
			return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + "]";
		}
	}
}
