/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RenderPhase {
    protected final String name;
    private final Runnable beginAction;
    private final Runnable endAction;
    protected static final Transparency NO_TRANSPARENCY = new Transparency("no_transparency", () -> RenderSystem.disableBlend(), () -> {});
    protected static final Transparency FORCED_TRANSPARENCY = new Transparency("forced_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendColor(1.0f, 1.0f, 1.0f, 0.15f);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
    });
    protected static final Transparency ADDITIVE_TRANSPARENCY = new Transparency("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency LIGHTNING_TRANSPARENCY = new Transparency("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency GLINT_TRANSPARENCY = new Transparency("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency CRUMBLING_TRANSPARENCY = new Transparency("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency TRANSLUCENT_TRANSPARENCY = new Transparency("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }, () -> RenderSystem.disableBlend());
    protected static final Alpha ZERO_ALPHA = new Alpha(0.0f);
    protected static final Alpha ONE_TENTH_ALPHA = new Alpha(0.003921569f);
    protected static final Alpha HALF_ALPHA = new Alpha(0.5f);
    protected static final ShadeModel SHADE_MODEL = new ShadeModel(false);
    protected static final ShadeModel SMOOTH_SHADE_MODEL = new ShadeModel(true);
    protected static final Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, true);
    protected static final Texture BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, false);
    protected static final Texture NO_TEXTURE = new Texture();
    protected static final Texturing DEFAULT_TEXTURING = new Texturing("default_texturing", () -> {}, () -> {});
    protected static final Texturing OUTLINE_TEXTURING = new Texturing("outline_texturing", () -> RenderSystem.setupOutline(), () -> RenderSystem.teardownOutline());
    protected static final Texturing GLINT_TEXTURING = new Texturing("glint_texturing", () -> RenderPhase.setupGlintTexturing(8.0f), () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final Texturing ENTITY_GLINT_TEXTURING = new Texturing("entity_glint_texturing", () -> RenderPhase.setupGlintTexturing(0.16f), () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final Lightmap ENABLE_LIGHTMAP = new Lightmap(true);
    protected static final Lightmap DISABLE_LIGHTMAP = new Lightmap(false);
    protected static final Overlay ENABLE_OVERLAY_COLOR = new Overlay(true);
    protected static final Overlay DISABLE_OVERLAY_COLOR = new Overlay(false);
    protected static final DiffuseLighting ENABLE_DIFFUSE_LIGHTING = new DiffuseLighting(true);
    protected static final DiffuseLighting DISABLE_DIFFUSE_LIGHTING = new DiffuseLighting(false);
    protected static final Cull ENABLE_CULLING = new Cull(true);
    protected static final Cull DISABLE_CULLING = new Cull(false);
    protected static final DepthTest ALWAYS_DEPTH_TEST = new DepthTest(519);
    protected static final DepthTest EQUAL_DEPTH_TEST = new DepthTest(514);
    protected static final DepthTest LEQUAL_DEPTH_TEST = new DepthTest(515);
    protected static final WriteMaskState ALL_MASK = new WriteMaskState(true, true);
    protected static final WriteMaskState COLOR_MASK = new WriteMaskState(true, false);
    protected static final WriteMaskState DEPTH_MASK = new WriteMaskState(false, true);
    protected static final Layering NO_LAYERING = new Layering("no_layering", () -> {}, () -> {});
    protected static final Layering POLYGON_OFFSET_LAYERING = new Layering("polygon_offset_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
    });
    protected static final Layering PROJECTION_LAYERING = new Layering("projection_layering", () -> {
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(1.0f, 1.0f, 0.999f);
        RenderSystem.matrixMode(5888);
    }, () -> {
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final Fog NO_FOG = new Fog("no_fog", () -> {}, () -> {});
    protected static final Fog FOG = new Fog("fog", () -> {
        BackgroundRenderer.setFogBlack();
        RenderSystem.enableFog();
    }, () -> RenderSystem.disableFog());
    protected static final Fog BLACK_FOG = new Fog("black_fog", () -> {
        RenderSystem.fog(2918, 0.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.enableFog();
    }, () -> {
        BackgroundRenderer.setFogBlack();
        RenderSystem.disableFog();
    });
    protected static final Target MAIN_TARGET = new Target("main_target", () -> {}, () -> {});
    protected static final Target OUTLINE_TARGET = new Target("outline_target", () -> MinecraftClient.getInstance().worldRenderer.method_22990().beginWrite(false), () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final LineWidth FULL_LINEWIDTH = new LineWidth(1.0f);

    public RenderPhase(String string, Runnable runnable, Runnable runnable2) {
        this.name = string;
        this.beginAction = runnable;
        this.endAction = runnable2;
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
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        RenderPhase renderPhase = (RenderPhase)object;
        return this.name.equals(renderPhase.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private static void setupGlintTexturing(float f) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long l = Util.getMeasuringTimeMs() * 8L;
        float g = (float)(l % 110000L) / 110000.0f;
        float h = (float)(l % 30000L) / 30000.0f;
        RenderSystem.translatef(-g, h, 0.0f);
        RenderSystem.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(f, f, f);
        RenderSystem.matrixMode(5888);
    }

    @Environment(value=EnvType.CLIENT)
    public static class LineWidth
    extends RenderPhase {
        private final float width;

        public LineWidth(float f) {
            super("alpha", () -> {
                if (f != 1.0f) {
                    RenderSystem.lineWidth(f);
                }
            }, () -> {
                if (f != 1.0f) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = f;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            if (!super.equals(object)) {
                return false;
            }
            return this.width == ((LineWidth)object).width;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), Float.valueOf(this.width));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Target
    extends RenderPhase {
        public Target(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Fog
    extends RenderPhase {
        public Fog(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Layering
    extends RenderPhase {
        public Layering(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WriteMaskState
    extends RenderPhase {
        private final boolean color;
        private final boolean depth;

        public WriteMaskState(boolean bl, boolean bl2) {
            super("write_mask_state", () -> {
                if (!bl2) {
                    RenderSystem.depthMask(bl2);
                }
                if (!bl) {
                    RenderSystem.colorMask(bl, bl, bl, bl);
                }
            }, () -> {
                if (!bl2) {
                    RenderSystem.depthMask(true);
                }
                if (!bl) {
                    RenderSystem.colorMask(true, true, true, true);
                }
            });
            this.color = bl;
            this.depth = bl2;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            WriteMaskState writeMaskState = (WriteMaskState)object;
            return this.color == writeMaskState.color && this.depth == writeMaskState.depth;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.color, this.depth);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DepthTest
    extends RenderPhase {
        private final int func;

        public DepthTest(int i) {
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
            this.func = i;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            DepthTest depthTest = (DepthTest)object;
            return this.func == depthTest.func;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.func);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cull
    extends Toggleable {
        public Cull(boolean bl) {
            super("cull", () -> {
                if (bl) {
                    RenderSystem.enableCull();
                }
            }, () -> {
                if (bl) {
                    RenderSystem.disableCull();
                }
            }, bl);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DiffuseLighting
    extends Toggleable {
        public DiffuseLighting(boolean bl) {
            super("diffuse_lighting", () -> {
                if (bl) {
                    GuiLighting.enable();
                }
            }, () -> {
                if (bl) {
                    GuiLighting.disable();
                }
            }, bl);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Overlay
    extends Toggleable {
        public Overlay(boolean bl) {
            super("overlay", () -> {
                if (bl) {
                    MinecraftClient.getInstance().gameRenderer.method_22975().setupOverlayColor();
                }
            }, () -> {
                if (bl) {
                    MinecraftClient.getInstance().gameRenderer.method_22975().teardownOverlayColor();
                }
            }, bl);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Lightmap
    extends Toggleable {
        public Lightmap(boolean bl) {
            super("lightmap", () -> {
                if (bl) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().enable();
                }
            }, () -> {
                if (bl) {
                    MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().disable();
                }
            }, bl);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Toggleable
    extends RenderPhase {
        private final boolean enabled;

        public Toggleable(String string, Runnable runnable, Runnable runnable2, boolean bl) {
            super(string, runnable, runnable2);
            this.enabled = bl;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Toggleable toggleable = (Toggleable)object;
            return this.enabled == toggleable.enabled;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(this.enabled);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class PortalTexturing
    extends Texturing {
        private final int layer;

        public PortalTexturing(int i) {
            super("portal_texturing", () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.translatef(0.5f, 0.5f, 0.0f);
                RenderSystem.scalef(0.5f, 0.5f, 1.0f);
                RenderSystem.translatef(17.0f / (float)i, (2.0f + (float)i / 1.5f) * ((float)(Util.getMeasuringTimeMs() % 800000L) / 800000.0f), 0.0f);
                RenderSystem.rotatef(((float)(i * i) * 4321.0f + (float)i * 9.0f) * 2.0f, 0.0f, 0.0f, 1.0f);
                RenderSystem.scalef(4.5f - (float)i / 4.0f, 4.5f - (float)i / 4.0f, 1.0f);
                RenderSystem.mulTextureByProjModelView();
                RenderSystem.matrixMode(5888);
                RenderSystem.setupEndPortalTexGen();
            }, () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(5888);
                RenderSystem.clearTexGen();
            });
            this.layer = i;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            PortalTexturing portalTexturing = (PortalTexturing)object;
            return this.layer == portalTexturing.layer;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.layer);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class OffsetTexturing
    extends Texturing {
        private final float x;
        private final float y;

        public OffsetTexturing(float f, float g) {
            super("offset_texturing", () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.translatef(f, g, 0.0f);
                RenderSystem.matrixMode(5888);
            }, () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(5888);
            });
            this.x = f;
            this.y = g;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            OffsetTexturing offsetTexturing = (OffsetTexturing)object;
            return Float.compare(offsetTexturing.x, this.x) == 0 && Float.compare(offsetTexturing.y, this.y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Texturing
    extends RenderPhase {
        public Texturing(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Texture
    extends RenderPhase {
        private final Optional<Identifier> id;
        private final boolean bilinear;
        private final boolean mipmap;

        public Texture(Identifier identifier, boolean bl, boolean bl2) {
            super("texture", () -> {
                RenderSystem.enableTexture();
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.bindTexture(identifier);
                textureManager.getTexture(identifier).setFilter(bl, bl2);
            }, () -> {});
            this.id = Optional.of(identifier);
            this.bilinear = bl;
            this.mipmap = bl2;
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
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Texture texture = (Texture)object;
            return this.id.equals(texture.id) && this.bilinear == texture.bilinear && this.mipmap == texture.mipmap;
        }

        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        protected Optional<Identifier> getId() {
            return this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ShadeModel
    extends RenderPhase {
        private final boolean smooth;

        public ShadeModel(boolean bl) {
            super("shade_model", () -> RenderSystem.shadeModel(bl ? 7425 : 7424), () -> RenderSystem.shadeModel(7424));
            this.smooth = bl;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            ShadeModel shadeModel = (ShadeModel)object;
            return this.smooth == shadeModel.smooth;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(this.smooth);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Alpha
    extends RenderPhase {
        private final float alpha;

        public Alpha(float f) {
            super("alpha", () -> {
                if (f > 0.0f) {
                    RenderSystem.enableAlphaTest();
                    RenderSystem.alphaFunc(516, f);
                } else {
                    RenderSystem.disableAlphaTest();
                }
            }, () -> {
                RenderSystem.disableAlphaTest();
                RenderSystem.defaultAlphaFunc();
            });
            this.alpha = f;
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            if (!super.equals(object)) {
                return false;
            }
            return this.alpha == ((Alpha)object).alpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), Float.valueOf(this.alpha));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Transparency
    extends RenderPhase {
        public Transparency(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }
}

