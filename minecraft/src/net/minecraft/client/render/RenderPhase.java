package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public abstract class RenderPhase {
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
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE);
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
	protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}, () -> RenderSystem.disableBlend());
	protected static final RenderPhase.Alpha ZERO_ALPHA = new RenderPhase.Alpha(0.0F);
	protected static final RenderPhase.Alpha ONE_TENTH_ALPHA = new RenderPhase.Alpha(0.003921569F);
	protected static final RenderPhase.Alpha HALF_ALPHA = new RenderPhase.Alpha(0.5F);
	protected static final RenderPhase.ShadeModel SHADE_MODEL = new RenderPhase.ShadeModel(false);
	protected static final RenderPhase.ShadeModel SMOOTH_SHADE_MODEL = new RenderPhase.ShadeModel(true);
	protected static final RenderPhase.Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, true);
	protected static final RenderPhase.Texture BLOCK_ATLAS_TEXTURE = new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, false);
	protected static final RenderPhase.Texture NO_TEXTURE = new RenderPhase.Texture();
	protected static final RenderPhase.Texturing DEFAULT_TEXTURING = new RenderPhase.Texturing("default_texturing", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Texturing OUTLINE_TEXTURING = new RenderPhase.Texturing(
		"outline_texturing", () -> RenderSystem.setupOutline(), () -> RenderSystem.teardownOutline()
	);
	protected static final RenderPhase.Texturing GLINT_TEXTURING = new RenderPhase.Texturing("glint_texturing", () -> setupGlintTexturing(8.0F), () -> {
		RenderSystem.matrixMode(5890);
		RenderSystem.popMatrix();
		RenderSystem.matrixMode(5888);
	});
	protected static final RenderPhase.Texturing ENTITY_GLINT_TEXTURING = new RenderPhase.Texturing(
		"entity_glint_texturing", () -> setupGlintTexturing(0.16F), () -> {
			RenderSystem.matrixMode(5890);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(5888);
		}
	);
	protected static final RenderPhase.Lightmap ENABLE_LIGHTMAP = new RenderPhase.Lightmap(true);
	protected static final RenderPhase.Lightmap DISABLE_LIGHTMAP = new RenderPhase.Lightmap(false);
	protected static final RenderPhase.Overlay ENABLE_OVERLAY_COLOR = new RenderPhase.Overlay(true);
	protected static final RenderPhase.Overlay DISABLE_OVERLAY_COLOR = new RenderPhase.Overlay(false);
	protected static final RenderPhase.DiffuseLighting ENABLE_DIFFUSE_LIGHTING = new RenderPhase.DiffuseLighting(true);
	protected static final RenderPhase.DiffuseLighting DISABLE_DIFFUSE_LIGHTING = new RenderPhase.DiffuseLighting(false);
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
	protected static final RenderPhase.Layering field_22241 = new RenderPhase.Layering("view_offset_z_layering", () -> {
		RenderSystem.pushMatrix();
		RenderSystem.scalef(0.99975586F, 0.99975586F, 0.99975586F);
	}, RenderSystem::popMatrix);
	protected static final RenderPhase.Fog NO_FOG = new RenderPhase.Fog("no_fog", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Fog FOG = new RenderPhase.Fog("fog", () -> {
		BackgroundRenderer.setFogBlack();
		RenderSystem.enableFog();
	}, () -> RenderSystem.disableFog());
	protected static final RenderPhase.Fog BLACK_FOG = new RenderPhase.Fog("black_fog", () -> {
		RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
		RenderSystem.enableFog();
	}, () -> {
		BackgroundRenderer.setFogBlack();
		RenderSystem.disableFog();
	});
	protected static final RenderPhase.Target MAIN_TARGET = new RenderPhase.Target("main_target", () -> {
	}, () -> {
	});
	protected static final RenderPhase.Target OUTLINE_TARGET = new RenderPhase.Target(
		"outline_target",
		() -> MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer().beginWrite(false),
		() -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)
	);
	protected static final RenderPhase.LineWidth FULL_LINEWIDTH = new RenderPhase.LineWidth(OptionalDouble.of(1.0));

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

	public boolean equals(@Nullable Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			RenderPhase renderPhase = (RenderPhase)object;
			return this.name.equals(renderPhase.name);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public String toString() {
		return this.name;
	}

	private static void setupGlintTexturing(float scale) {
		RenderSystem.matrixMode(5890);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		long l = Util.getMeasuringTimeMs() * 8L;
		float f = (float)(l % 110000L) / 110000.0F;
		float g = (float)(l % 30000L) / 30000.0F;
		RenderSystem.translatef(-f, g, 0.0F);
		RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
		RenderSystem.scalef(scale, scale, scale);
		RenderSystem.matrixMode(5888);
	}

	@Environment(EnvType.CLIENT)
	public static class Alpha extends RenderPhase {
		private final float alpha;

		public Alpha(float alpha) {
			super("alpha", () -> {
				if (alpha > 0.0F) {
					RenderSystem.enableAlphaTest();
					RenderSystem.alphaFunc(516, alpha);
				} else {
					RenderSystem.disableAlphaTest();
				}
			}, () -> {
				RenderSystem.disableAlphaTest();
				RenderSystem.defaultAlphaFunc();
			});
			this.alpha = alpha;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else if (object == null || this.getClass() != object.getClass()) {
				return false;
			} else {
				return !super.equals(object) ? false : this.alpha == ((RenderPhase.Alpha)object).alpha;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{super.hashCode(), this.alpha});
		}

		@Override
		public String toString() {
			return this.name + '[' + this.alpha + ']';
		}
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
		private final String field_22242;
		private final int func;

		public DepthTest(String string, int i) {
			super("depth_test", () -> {
				if (i != 519) {
					RenderSystem.enableDepthTest();
					RenderSystem.depthFunc(i);
				}
			}, () -> {
				if (i != 519) {
					RenderSystem.disableDepthTest();
					RenderSystem.depthFunc(515);
				}
			});
			this.field_22242 = string;
			this.func = i;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.DepthTest depthTest = (RenderPhase.DepthTest)object;
				return this.func == depthTest.func;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(this.func);
		}

		@Override
		public String toString() {
			return this.name + '[' + this.field_22242 + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class DiffuseLighting extends RenderPhase.Toggleable {
		public DiffuseLighting(boolean guiLighting) {
			super("diffuse_lighting", () -> {
				if (guiLighting) {
					net.minecraft.client.render.DiffuseLighting.enable();
				}
			}, () -> {
				if (guiLighting) {
					net.minecraft.client.render.DiffuseLighting.disable();
				}
			}, guiLighting);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Fog extends RenderPhase {
		public Fog(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
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

		public LineWidth(OptionalDouble optionalDouble) {
			super("line_width", () -> {
				if (!Objects.equals(optionalDouble, OptionalDouble.of(1.0))) {
					if (optionalDouble.isPresent()) {
						RenderSystem.lineWidth((float)optionalDouble.getAsDouble());
					} else {
						RenderSystem.lineWidth(Math.max(2.5F, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
					}
				}
			}, () -> {
				if (!Objects.equals(optionalDouble, OptionalDouble.of(1.0))) {
					RenderSystem.lineWidth(1.0F);
				}
			});
			this.width = optionalDouble;
		}

		@Override
		public boolean equals(@Nullable Object object) {
			if (this == object) {
				return true;
			} else if (object == null || this.getClass() != object.getClass()) {
				return false;
			} else {
				return !super.equals(object) ? false : Objects.equals(this.width, ((RenderPhase.LineWidth)object).width);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{super.hashCode(), this.width});
		}

		@Override
		public String toString() {
			return this.name + '[' + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class OffsetTexturing extends RenderPhase.Texturing {
		private final float x;
		private final float y;

		public OffsetTexturing(float x, float y) {
			super("offset_texturing", () -> {
				RenderSystem.matrixMode(5890);
				RenderSystem.pushMatrix();
				RenderSystem.loadIdentity();
				RenderSystem.translatef(x, y, 0.0F);
				RenderSystem.matrixMode(5888);
			}, () -> {
				RenderSystem.matrixMode(5890);
				RenderSystem.popMatrix();
				RenderSystem.matrixMode(5888);
			});
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.OffsetTexturing offsetTexturing = (RenderPhase.OffsetTexturing)object;
				return Float.compare(offsetTexturing.x, this.x) == 0 && Float.compare(offsetTexturing.y, this.y) == 0;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{this.x, this.y});
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
	public static final class PortalTexturing extends RenderPhase.Texturing {
		private final int layer;

		public PortalTexturing(int layer) {
			super("portal_texturing", () -> {
				RenderSystem.matrixMode(5890);
				RenderSystem.pushMatrix();
				RenderSystem.loadIdentity();
				RenderSystem.translatef(0.5F, 0.5F, 0.0F);
				RenderSystem.scalef(0.5F, 0.5F, 1.0F);
				RenderSystem.translatef(17.0F / (float)layer, (2.0F + (float)layer / 1.5F) * ((float)(Util.getMeasuringTimeMs() % 800000L) / 800000.0F), 0.0F);
				RenderSystem.rotatef(((float)(layer * layer) * 4321.0F + (float)layer * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
				RenderSystem.scalef(4.5F - (float)layer / 4.0F, 4.5F - (float)layer / 4.0F, 1.0F);
				RenderSystem.mulTextureByProjModelView();
				RenderSystem.matrixMode(5888);
				RenderSystem.setupEndPortalTexGen();
			}, () -> {
				RenderSystem.matrixMode(5890);
				RenderSystem.popMatrix();
				RenderSystem.matrixMode(5888);
				RenderSystem.clearTexGen();
			});
			this.layer = layer;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.PortalTexturing portalTexturing = (RenderPhase.PortalTexturing)object;
				return this.layer == portalTexturing.layer;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(this.layer);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class ShadeModel extends RenderPhase {
		private final boolean smooth;

		public ShadeModel(boolean smooth) {
			super("shade_model", () -> RenderSystem.shadeModel(smooth ? 7425 : 7424), () -> RenderSystem.shadeModel(7424));
			this.smooth = smooth;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.ShadeModel shadeModel = (RenderPhase.ShadeModel)object;
				return this.smooth == shadeModel.smooth;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Boolean.hashCode(this.smooth);
		}

		@Override
		public String toString() {
			return this.name + '[' + (this.smooth ? "smooth" : "flat") + ']';
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Target extends RenderPhase {
		public Target(String string, Runnable runnable, Runnable runnable2) {
			super(string, runnable, runnable2);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Texture extends RenderPhase {
		private final Optional<Identifier> id;
		private final boolean bilinear;
		private final boolean mipmap;

		public Texture(Identifier id, boolean bilinear, boolean mipmap) {
			super("texture", () -> {
				RenderSystem.enableTexture();
				TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
				textureManager.bindTexture(id);
				textureManager.getTexture(id).setFilter(bilinear, mipmap);
			}, () -> {
			});
			this.id = Optional.of(id);
			this.bilinear = bilinear;
			this.mipmap = mipmap;
		}

		public Texture() {
			super("texture", () -> RenderSystem.disableTexture(), () -> RenderSystem.enableTexture());
			this.id = Optional.empty();
			this.bilinear = false;
			this.mipmap = false;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.Texture texture = (RenderPhase.Texture)object;
				return this.id.equals(texture.id) && this.bilinear == texture.bilinear && this.mipmap == texture.mipmap;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return this.id.hashCode();
		}

		@Override
		public String toString() {
			return this.name + '[' + this.id + "(blur=" + this.bilinear + ", mipmap=" + this.mipmap + ")]";
		}

		protected Optional<Identifier> getId() {
			return this.id;
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

		public Toggleable(String string, Runnable runnable, Runnable runnable2, boolean bl) {
			super(string, runnable, runnable2);
			this.enabled = bl;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.Toggleable toggleable = (RenderPhase.Toggleable)object;
				return this.enabled == toggleable.enabled;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Boolean.hashCode(this.enabled);
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
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			} else if (object != null && this.getClass() == object.getClass()) {
				RenderPhase.WriteMaskState writeMaskState = (RenderPhase.WriteMaskState)object;
				return this.color == writeMaskState.color && this.depth == writeMaskState.depth;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(new Object[]{this.color, this.depth});
		}

		@Override
		public String toString() {
			return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + ']';
		}
	}
}
