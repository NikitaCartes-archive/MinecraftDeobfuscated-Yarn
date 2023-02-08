/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public abstract class RenderPhase {
    private static final float VIEW_OFFSET_Z_LAYERING_SCALE = 0.99975586f;
    public static final double field_42230 = 8.0;
    protected final String name;
    private final Runnable beginAction;
    private final Runnable endAction;
    protected static final Transparency NO_TRANSPARENCY = new Transparency("no_transparency", () -> RenderSystem.disableBlend(), () -> {});
    protected static final Transparency ADDITIVE_TRANSPARENCY = new Transparency("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency LIGHTNING_TRANSPARENCY = new Transparency("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency GLINT_TRANSPARENCY = new Transparency("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_COLOR, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency CRUMBLING_TRANSPARENCY = new Transparency("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.DST_COLOR, GlStateManager.DstFactor.SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final Transparency TRANSLUCENT_TRANSPARENCY = new Transparency("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final ShaderProgram NO_PROGRAM = new ShaderProgram();
    protected static final ShaderProgram BLOCK_PROGRAM = new ShaderProgram(GameRenderer::getBlockProgram);
    protected static final ShaderProgram NEW_ENTITY_PROGRAM = new ShaderProgram(GameRenderer::getNewEntityProgram);
    protected static final ShaderProgram POSITION_COLOR_LIGHTMAP_PROGRAM = new ShaderProgram(GameRenderer::getPositionColorLightmapProgram);
    protected static final ShaderProgram POSITION_PROGRAM = new ShaderProgram(GameRenderer::getPositionProgram);
    protected static final ShaderProgram POSITION_COLOR_TEXTURE_PROGRAM = new ShaderProgram(GameRenderer::getPositionColorTexProgram);
    protected static final ShaderProgram POSITION_TEXTURE_PROGRAM = new ShaderProgram(GameRenderer::getPositionTexProgram);
    protected static final ShaderProgram POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM = new ShaderProgram(GameRenderer::getPositionColorTexLightmapProgram);
    protected static final ShaderProgram COLOR_PROGRAM = new ShaderProgram(GameRenderer::getPositionColorProgram);
    protected static final ShaderProgram SOLID_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeSolidProgram);
    protected static final ShaderProgram CUTOUT_MIPPED_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeCutoutMippedProgram);
    protected static final ShaderProgram CUTOUT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeCutoutProgram);
    protected static final ShaderProgram TRANSLUCENT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTranslucentProgram);
    protected static final ShaderProgram TRANSLUCENT_MOVING_BLOCK_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTranslucentMovingBlockProgram);
    protected static final ShaderProgram TRANSLUCENT_NO_CRUMBLING_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTranslucentNoCrumblingProgram);
    protected static final ShaderProgram ARMOR_CUTOUT_NO_CULL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeArmorCutoutNoCullProgram);
    protected static final ShaderProgram ENTITY_SOLID_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntitySolidProgram);
    protected static final ShaderProgram ENTITY_CUTOUT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityCutoutProgram);
    protected static final ShaderProgram ENTITY_CUTOUT_NONULL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityCutoutNoNullProgram);
    protected static final ShaderProgram ENTITY_CUTOUT_NONULL_OFFSET_Z_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityCutoutNoNullZOffsetProgram);
    protected static final ShaderProgram ITEM_ENTITY_TRANSLUCENT_CULL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeItemEntityTranslucentCullProgram);
    protected static final ShaderProgram ENTITY_TRANSLUCENT_CULL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityTranslucentCullProgram);
    protected static final ShaderProgram ENTITY_TRANSLUCENT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityTranslucentProgram);
    protected static final ShaderProgram ENTITY_TRANSLUCENT_EMISSIVE_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityTranslucentEmissiveProgram);
    protected static final ShaderProgram ENTITY_SMOOTH_CUTOUT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntitySmoothCutoutProgram);
    protected static final ShaderProgram BEACON_BEAM_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeBeaconBeamProgram);
    protected static final ShaderProgram ENTITY_DECAL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityDecalProgram);
    protected static final ShaderProgram ENTITY_NO_OUTLINE_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityNoOutlineProgram);
    protected static final ShaderProgram ENTITY_SHADOW_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityShadowProgram);
    protected static final ShaderProgram ENTITY_ALPHA_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityAlphaProgram);
    protected static final ShaderProgram EYES_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEyesProgram);
    protected static final ShaderProgram ENERGY_SWIRL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEnergySwirlProgram);
    protected static final ShaderProgram LEASH_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeLeashProgram);
    protected static final ShaderProgram WATER_MASK_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeWaterMaskProgram);
    protected static final ShaderProgram OUTLINE_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeOutlineProgram);
    protected static final ShaderProgram ARMOR_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeArmorGlintProgram);
    protected static final ShaderProgram ARMOR_ENTITY_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeArmorEntityGlintProgram);
    protected static final ShaderProgram TRANSLUCENT_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeGlintTranslucentProgram);
    protected static final ShaderProgram GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeGlintProgram);
    protected static final ShaderProgram DIRECT_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeGlintDirectProgram);
    protected static final ShaderProgram ENTITY_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityGlintProgram);
    protected static final ShaderProgram DIRECT_ENTITY_GLINT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEntityGlintDirectProgram);
    protected static final ShaderProgram CRUMBLING_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeCrumblingProgram);
    protected static final ShaderProgram TEXT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextProgram);
    protected static final ShaderProgram TEXT_BACKGROUND_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextBackgroundProgram);
    protected static final ShaderProgram TEXT_INTENSITY_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextIntensityProgram);
    protected static final ShaderProgram TRANSPARENT_TEXT_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextSeeThroughProgram);
    protected static final ShaderProgram TRANSPARENT_TEXT_BACKGROUND_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextBackgroundSeeThroughProgram);
    protected static final ShaderProgram TRANSPARENT_TEXT_INTENSITY_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTextIntensitySeeThroughProgram);
    protected static final ShaderProgram LIGHTNING_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeLightningProgram);
    protected static final ShaderProgram TRIPWIRE_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeTripwireProgram);
    protected static final ShaderProgram END_PORTAL_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEndPortalProgram);
    protected static final ShaderProgram END_GATEWAY_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeEndGatewayProgram);
    protected static final ShaderProgram LINES_PROGRAM = new ShaderProgram(GameRenderer::getRenderTypeLinesProgram);
    protected static final Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true);
    protected static final Texture BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false);
    protected static final TextureBase NO_TEXTURE = new TextureBase();
    protected static final Texturing DEFAULT_TEXTURING = new Texturing("default_texturing", () -> {}, () -> {});
    protected static final Texturing GLINT_TEXTURING = new Texturing("glint_texturing", () -> RenderPhase.setupGlintTexturing(8.0f), () -> RenderSystem.resetTextureMatrix());
    protected static final Texturing ENTITY_GLINT_TEXTURING = new Texturing("entity_glint_texturing", () -> RenderPhase.setupGlintTexturing(0.16f), () -> RenderSystem.resetTextureMatrix());
    protected static final Lightmap ENABLE_LIGHTMAP = new Lightmap(true);
    protected static final Lightmap DISABLE_LIGHTMAP = new Lightmap(false);
    protected static final Overlay ENABLE_OVERLAY_COLOR = new Overlay(true);
    protected static final Overlay DISABLE_OVERLAY_COLOR = new Overlay(false);
    protected static final Cull ENABLE_CULLING = new Cull(true);
    protected static final Cull DISABLE_CULLING = new Cull(false);
    protected static final DepthTest ALWAYS_DEPTH_TEST = new DepthTest("always", 519);
    protected static final DepthTest EQUAL_DEPTH_TEST = new DepthTest("==", 514);
    protected static final DepthTest LEQUAL_DEPTH_TEST = new DepthTest("<=", 515);
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
    protected static final Layering VIEW_OFFSET_Z_LAYERING = new Layering("view_offset_z_layering", () -> {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.scale(0.99975586f, 0.99975586f, 0.99975586f);
        RenderSystem.applyModelViewMatrix();
    }, () -> {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    });
    protected static final Target MAIN_TARGET = new Target("main_target", () -> {}, () -> {});
    protected static final Target OUTLINE_TARGET = new Target("outline_target", () -> MinecraftClient.getInstance().worldRenderer.getEntityOutlinesFramebuffer().beginWrite(false), () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final Target TRANSLUCENT_TARGET = new Target("translucent_target", () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().worldRenderer.getTranslucentFramebuffer().beginWrite(false);
        }
    }, () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    });
    protected static final Target PARTICLES_TARGET = new Target("particles_target", () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().worldRenderer.getParticlesFramebuffer().beginWrite(false);
        }
    }, () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    });
    protected static final Target WEATHER_TARGET = new Target("weather_target", () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer().beginWrite(false);
        }
    }, () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    });
    protected static final Target CLOUDS_TARGET = new Target("clouds_target", () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer().beginWrite(false);
        }
    }, () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    });
    protected static final Target ITEM_TARGET = new Target("item_entity_target", () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().worldRenderer.getEntityFramebuffer().beginWrite(false);
        }
    }, () -> {
        if (MinecraftClient.isFabulousGraphicsOrBetter()) {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        }
    });
    protected static final LineWidth FULL_LINE_WIDTH = new LineWidth(OptionalDouble.of(1.0));

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
        float f = (float)(l % 110000L) / 110000.0f;
        float g = (float)(l % 30000L) / 30000.0f;
        Matrix4f matrix4f = new Matrix4f().translation(-f, g, 0.0f);
        matrix4f.rotateZ(0.17453292f).scale(scale);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Transparency
    extends RenderPhase {
        public Transparency(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class ShaderProgram
    extends RenderPhase {
        private final Optional<Supplier<net.minecraft.client.gl.ShaderProgram>> supplier;

        public ShaderProgram(Supplier<net.minecraft.client.gl.ShaderProgram> supplier) {
            super("shader", () -> RenderSystem.setShader(supplier), () -> {});
            this.supplier = Optional.of(supplier);
        }

        public ShaderProgram() {
            super("shader", () -> RenderSystem.setShader(() -> null), () -> {});
            this.supplier = Optional.empty();
        }

        @Override
        public String toString() {
            return this.name + "[" + this.supplier + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Texture
    extends TextureBase {
        private final Optional<Identifier> id;
        private final boolean blur;
        private final boolean mipmap;

        public Texture(Identifier id, boolean blur, boolean mipmap) {
            super(() -> {
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.getTexture(id).setFilter(blur, mipmap);
                RenderSystem.setShaderTexture(0, id);
            }, () -> {});
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

    @Environment(value=EnvType.CLIENT)
    protected static class TextureBase
    extends RenderPhase {
        public TextureBase(Runnable apply, Runnable unapply) {
            super("texture", apply, unapply);
        }

        TextureBase() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<Identifier> getId() {
            return Optional.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Texturing
    extends RenderPhase {
        public Texturing(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Lightmap
    extends Toggleable {
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

    @Environment(value=EnvType.CLIENT)
    protected static class Overlay
    extends Toggleable {
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

    @Environment(value=EnvType.CLIENT)
    protected static class Cull
    extends Toggleable {
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

    @Environment(value=EnvType.CLIENT)
    protected static class DepthTest
    extends RenderPhase {
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

    @Environment(value=EnvType.CLIENT)
    protected static class WriteMaskState
    extends RenderPhase {
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

    @Environment(value=EnvType.CLIENT)
    protected static class Layering
    extends RenderPhase {
        public Layering(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Target
    extends RenderPhase {
        public Target(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class LineWidth
    extends RenderPhase {
        private final OptionalDouble width;

        public LineWidth(OptionalDouble width) {
            super("line_width", () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    if (width.isPresent()) {
                        RenderSystem.lineWidth((float)width.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5f, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0f * 2.5f));
                    }
                }
            }, () -> {
                if (!Objects.equals(width, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = width;
        }

        @Override
        public String toString() {
            return this.name + "[" + (Serializable)(this.width.isPresent() ? Double.valueOf(this.width.getAsDouble()) : "window_scale") + "]";
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Toggleable
    extends RenderPhase {
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

    @Environment(value=EnvType.CLIENT)
    protected static final class OffsetTexturing
    extends Texturing {
        public OffsetTexturing(float x, float y) {
            super("offset_texturing", () -> RenderSystem.setTextureMatrix(new Matrix4f().translation(x, y, 0.0f)), () -> RenderSystem.resetTextureMatrix());
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Textures
    extends TextureBase {
        private final Optional<Identifier> id;

        Textures(ImmutableList<Triple<Identifier, Boolean, Boolean>> textures) {
            super(() -> {
                int i = 0;
                for (Triple triple : textures) {
                    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                    textureManager.getTexture((Identifier)triple.getLeft()).setFilter((Boolean)triple.getMiddle(), (Boolean)triple.getRight());
                    RenderSystem.setShaderTexture(i++, (Identifier)triple.getLeft());
                }
            }, () -> {});
            this.id = textures.stream().findFirst().map(Triple::getLeft);
        }

        @Override
        protected Optional<Identifier> getId() {
            return this.id;
        }

        public static Builder create() {
            return new Builder();
        }

        @Environment(value=EnvType.CLIENT)
        public static final class Builder {
            private final ImmutableList.Builder<Triple<Identifier, Boolean, Boolean>> textures = new ImmutableList.Builder();

            public Builder add(Identifier id, boolean blur, boolean mipmap) {
                this.textures.add((Object)Triple.of(id, blur, mipmap));
                return this;
            }

            public Textures build() {
                return new Textures((ImmutableList<Triple<Identifier, Boolean, Boolean>>)this.textures.build());
            }
        }
    }
}

