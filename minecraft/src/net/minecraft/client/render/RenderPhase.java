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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public abstract class RenderPhase {
	private static final float field_32771 = 0.99975586F;
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
	protected static final RenderPhase.Shader NO_SHADER = new RenderPhase.Shader();
	protected static final RenderPhase.Shader BLOCK_SHADER = new RenderPhase.Shader(GameRenderer::getBlockShader);
	protected static final RenderPhase.Shader NEW_ENTITY_SHADER = new RenderPhase.Shader(GameRenderer::getNewEntityShader);
	protected static final RenderPhase.Shader POSITION_COLOR_LIGHTMAP_SHADER = new RenderPhase.Shader(GameRenderer::getPositionColorLightmapShader);
	protected static final RenderPhase.Shader POSITION_SHADER = new RenderPhase.Shader(GameRenderer::getPositionShader);
	protected static final RenderPhase.Shader POSITION_COLOR_TEXTURE_SHADER = new RenderPhase.Shader(GameRenderer::getPositionColorTexShader);
	protected static final RenderPhase.Shader POSITION_TEXTURE_SHADER = new RenderPhase.Shader(GameRenderer::getPositionTexShader);
	protected static final RenderPhase.Shader POSITION_COLOR_TEXTURE_LIGHTMAP_SHADER = new RenderPhase.Shader(GameRenderer::getPositionColorTexLightmapShader);
	protected static final RenderPhase.Shader COLOR_SHADER = new RenderPhase.Shader(GameRenderer::getPositionColorShader);
	protected static final RenderPhase.Shader SOLID_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeSolidShader);
	protected static final RenderPhase.Shader CUTOUT_MIPPED_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeCutoutMippedShader);
	protected static final RenderPhase.Shader CUTOUT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeCutoutShader);
	protected static final RenderPhase.Shader TRANSLUCENT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTranslucentShader);
	protected static final RenderPhase.Shader TRANSLUCENT_MOVING_BLOCK_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTranslucentMovingBlockShader);
	protected static final RenderPhase.Shader TRANSLUCENT_NO_CRUMBLING_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTranslucentNoCrumblingShader);
	protected static final RenderPhase.Shader ARMOR_CUTOUT_NO_CULL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeArmorCutoutNoCullShader);
	protected static final RenderPhase.Shader ENTITY_SOLID_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntitySolidShader);
	protected static final RenderPhase.Shader ENTITY_CUTOUT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityCutoutShader);
	protected static final RenderPhase.Shader ENTITY_CUTOUT_NONULL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityCutoutNoNullShader);
	protected static final RenderPhase.Shader ENTITY_CUTOUT_NONULL_OFFSET_Z_SHADER = new RenderPhase.Shader(
		GameRenderer::getRenderTypeEntityCutoutNoNullZOffsetShader
	);
	protected static final RenderPhase.Shader ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new RenderPhase.Shader(
		GameRenderer::getRenderTypeItemEntityTranslucentCullShader
	);
	protected static final RenderPhase.Shader ENTITY_TRANSLUCENT_CULL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityTranslucentCullShader);
	protected static final RenderPhase.Shader ENTITY_TRANSLUCENT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityTranslucentShader);
	protected static final RenderPhase.Shader ENTITY_SMOOTH_CUTOUT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntitySmoothCutoutShader);
	protected static final RenderPhase.Shader BEACON_BEAM_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeBeaconBeamShader);
	protected static final RenderPhase.Shader ENTITY_DECAL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityDecalShader);
	protected static final RenderPhase.Shader ENTITY_NO_OUTLINE_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityNoOutlineShader);
	protected static final RenderPhase.Shader ENTITY_SHADOW_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityShadowShader);
	protected static final RenderPhase.Shader ENTITY_ALPHA_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityAlphaShader);
	protected static final RenderPhase.Shader EYES_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEyesShader);
	protected static final RenderPhase.Shader ENERGY_SWIRL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEnergySwirlShader);
	protected static final RenderPhase.Shader LEASH_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeLeashShader);
	protected static final RenderPhase.Shader WATER_MASK_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeWaterMaskShader);
	protected static final RenderPhase.Shader OUTLINE_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeOutlineShader);
	protected static final RenderPhase.Shader ARMOR_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeArmorGlintShader);
	protected static final RenderPhase.Shader ARMOR_ENTITY_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeArmorEntityGlintShader);
	protected static final RenderPhase.Shader TRANSLUCENT_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeGlintTranslucentShader);
	protected static final RenderPhase.Shader GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeGlintShader);
	protected static final RenderPhase.Shader DIRECT_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeGlintDirectShader);
	protected static final RenderPhase.Shader ENTITY_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityGlintShader);
	protected static final RenderPhase.Shader DIRECT_ENTITY_GLINT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEntityGlintDirectShader);
	protected static final RenderPhase.Shader CRUMBLING_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeCrumblingShader);
	protected static final RenderPhase.Shader TEXT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTextShader);
	protected static final RenderPhase.Shader TRANSPARENT_TEXT_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTextSeeThroughShader);
	protected static final RenderPhase.Shader LIGHTNING_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeLightningShader);
	protected static final RenderPhase.Shader TRIPWIRE_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeTripwireShader);
	protected static final RenderPhase.Shader END_PORTAL_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEndPortalShader);
	protected static final RenderPhase.Shader END_GATEWAY_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeEndGatewayShader);
	protected static final RenderPhase.Shader LINES_SHADER = new RenderPhase.Shader(GameRenderer::getRenderTypeLinesShader);
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
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.scale(0.99975586F, 0.99975586F, 0.99975586F);
		RenderSystem.applyModelViewMatrix();
	}, () -> {
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.pop();
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
	protected static final RenderPhase.Target ITEM_TARGET = new RenderPhase.Target("item_entity_target", () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer().beginWrite(false);
		}
	}, () -> {
		if (MinecraftClient.isFabulousGraphicsOrBetter()) {
			MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
		}
	});
	protected static final RenderPhase.LineWidth FULL_LINE_WIDTH = new RenderPhase.LineWidth(OptionalDouble.of(1.0));

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
		long l = Util.getMeasuringTimeMs() * 8L;
		float f = (float)(l % 110000L) / 110000.0F;
		float g = (float)(l % 30000L) / 30000.0F;
		Matrix4f matrix4f = Matrix4f.translate(-f, g, 0.0F);
		matrix4f.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(10.0F));
		matrix4f.multiply(Matrix4f.scale(scale, scale, scale));
		RenderSystem.setTextureMatrix(matrix4f);
	}

	@Environment(EnvType.CLIENT)
	public static class Cull extends RenderPhase.Toggleable {
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
	public static class DepthTest extends RenderPhase {
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
			return this.name + '[' + this.depthFunctionName + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Layering extends RenderPhase {
		public Layering(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Lightmap extends RenderPhase.Toggleable {
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
	public static class LineWidth extends RenderPhase {
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
			return this.name + '[' + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class OffsetTexturing extends RenderPhase.Texturing {
		public OffsetTexturing(float x, float y) {
			super("offset_texturing", () -> RenderSystem.setTextureMatrix(Matrix4f.translate(x, y, 0.0F)), () -> RenderSystem.resetTextureMatrix());
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Overlay extends RenderPhase.Toggleable {
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
	public static class Shader extends RenderPhase {
		private final Optional<Supplier<net.minecraft.client.render.Shader>> supplier;

		public Shader(Supplier<net.minecraft.client.render.Shader> supplier) {
			super("shader", () -> RenderSystem.setShader(supplier), () -> {
			});
			this.supplier = Optional.of(supplier);
		}

		public Shader() {
			super("shader", () -> RenderSystem.setShader(() -> null), () -> {
			});
			this.supplier = Optional.empty();
		}

		@Override
		public String toString() {
			return this.name + '[' + this.supplier + "]";
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Target extends RenderPhase {
		public Target(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Texture extends RenderPhase.TextureBase {
		private final Optional<Identifier> id;
		private final boolean blur;
		private final boolean mipmap;

		public Texture(Identifier id, boolean blur, boolean mipmap) {
			super(() -> {
				RenderSystem.enableTexture();
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
			return this.name + '[' + this.id + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
		}

		@Override
		protected Optional<Identifier> getId() {
			return this.id;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class TextureBase extends RenderPhase {
		public TextureBase(Runnable apply, Runnable unapply) {
			super("texture", apply, unapply);
		}

		private TextureBase() {
			super("texture", () -> {
			}, () -> {
			});
		}

		protected Optional<Identifier> getId() {
			return Optional.empty();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Textures extends RenderPhase.TextureBase {
		private final Optional<Identifier> id;

		private Textures(ImmutableList<Triple<Identifier, Boolean, Boolean>> textures) {
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
	public static class Texturing extends RenderPhase {
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
			return this.name + '[' + this.enabled + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Transparency extends RenderPhase {
		public Transparency(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class WriteMaskState extends RenderPhase {
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
			return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + ']';
		}
	}
}
