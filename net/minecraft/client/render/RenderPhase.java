/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.class_5944;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class RenderPhase {
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
    protected static final class_5942 field_29434 = new class_5942();
    protected static final class_5942 field_29435 = new class_5942(GameRenderer::method_34544);
    protected static final class_5942 field_29436 = new class_5942(GameRenderer::method_34545);
    protected static final class_5942 field_29437 = new class_5942(GameRenderer::method_34547);
    protected static final class_5942 field_29438 = new class_5942(GameRenderer::method_34539);
    protected static final class_5942 field_29439 = new class_5942(GameRenderer::method_34541);
    protected static final class_5942 field_29440 = new class_5942(GameRenderer::method_34542);
    protected static final class_5942 field_29441 = new class_5942(GameRenderer::method_34548);
    protected static final class_5942 field_29442 = new class_5942(GameRenderer::method_34540);
    protected static final class_5942 field_29443 = new class_5942(GameRenderer::method_34495);
    protected static final class_5942 field_29444 = new class_5942(GameRenderer::method_34496);
    protected static final class_5942 field_29445 = new class_5942(GameRenderer::method_34497);
    protected static final class_5942 field_29446 = new class_5942(GameRenderer::method_34498);
    protected static final class_5942 field_29447 = new class_5942(GameRenderer::method_34499);
    protected static final class_5942 field_29448 = new class_5942(GameRenderer::method_34500);
    protected static final class_5942 field_29449 = new class_5942(GameRenderer::method_34501);
    protected static final class_5942 field_29450 = new class_5942(GameRenderer::method_34502);
    protected static final class_5942 field_29451 = new class_5942(GameRenderer::method_34503);
    protected static final class_5942 field_29452 = new class_5942(GameRenderer::method_34504);
    protected static final class_5942 field_29404 = new class_5942(GameRenderer::method_34505);
    protected static final class_5942 field_29405 = new class_5942(GameRenderer::method_34506);
    protected static final class_5942 field_29406 = new class_5942(GameRenderer::method_34507);
    protected static final class_5942 field_29407 = new class_5942(GameRenderer::method_34508);
    protected static final class_5942 field_29408 = new class_5942(GameRenderer::method_34509);
    protected static final class_5942 field_29409 = new class_5942(GameRenderer::method_34510);
    protected static final class_5942 field_29410 = new class_5942(GameRenderer::method_34511);
    protected static final class_5942 field_29411 = new class_5942(GameRenderer::method_34512);
    protected static final class_5942 field_29412 = new class_5942(GameRenderer::method_34513);
    protected static final class_5942 field_29413 = new class_5942(GameRenderer::method_34514);
    protected static final class_5942 field_29414 = new class_5942(GameRenderer::method_34515);
    protected static final class_5942 field_29415 = new class_5942(GameRenderer::method_34516);
    protected static final class_5942 field_29416 = new class_5942(GameRenderer::method_34517);
    protected static final class_5942 field_29417 = new class_5942(GameRenderer::method_34518);
    protected static final class_5942 field_29418 = new class_5942(GameRenderer::method_34519);
    protected static final class_5942 field_29419 = new class_5942(GameRenderer::method_34520);
    protected static final class_5942 field_29420 = new class_5942(GameRenderer::method_34523);
    protected static final class_5942 field_29421 = new class_5942(GameRenderer::method_34524);
    protected static final class_5942 field_29422 = new class_5942(GameRenderer::method_34525);
    protected static final class_5942 field_29423 = new class_5942(GameRenderer::method_34526);
    protected static final class_5942 field_29424 = new class_5942(GameRenderer::method_34527);
    protected static final class_5942 field_29425 = new class_5942(GameRenderer::method_34528);
    protected static final class_5942 field_29426 = new class_5942(GameRenderer::method_34536);
    protected static final class_5942 field_29427 = new class_5942(GameRenderer::method_34529);
    protected static final class_5942 field_29428 = new class_5942(GameRenderer::method_34530);
    protected static final class_5942 field_29429 = new class_5942(GameRenderer::method_34531);
    protected static final class_5942 field_29430 = new class_5942(GameRenderer::method_34532);
    protected static final class_5942 field_29431 = new class_5942(GameRenderer::method_34533);
    protected static final class_5942 field_29432 = new class_5942(GameRenderer::method_34534);
    protected static final class_5942 field_29433 = new class_5942(GameRenderer::method_34535);
    protected static final Texture MIPMAP_BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true);
    protected static final Texture BLOCK_ATLAS_TEXTURE = new Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, false);
    protected static final class_5939 NO_TEXTURE = new class_5939();
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

    protected boolean method_34551(RenderPhase renderPhase) {
        return this.name.equals(renderPhase.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name;
    }

    private static void setupGlintTexturing(float scale) {
        long l = Util.getMeasuringTimeMs() * 8L;
        float f = (float)(l % 110000L) / 110000.0f;
        float g = (float)(l % 30000L) / 30000.0f;
        Matrix4f matrix4f = Matrix4f.translate(-f, g, 0.0f);
        matrix4f.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(10.0f));
        matrix4f.multiply(Matrix4f.scale(scale, scale, scale));
        RenderSystem.setTextureMatrix(matrix4f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class LineWidth
    extends RenderPhase {
        private final OptionalDouble width;

        public LineWidth(OptionalDouble optionalDouble) {
            super("line_width", () -> {
                if (!Objects.equals(optionalDouble, OptionalDouble.of(1.0))) {
                    if (optionalDouble.isPresent()) {
                        RenderSystem.lineWidth((float)optionalDouble.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5f, (float)MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0f * 2.5f));
                    }
                }
            }, () -> {
                if (!Objects.equals(optionalDouble, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.width = optionalDouble;
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
            return Objects.equals(this.width, ((LineWidth)object).width);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), this.width);
        }

        @Override
        public String toString() {
            return this.name + '[' + (this.width.isPresent() ? Double.valueOf(this.width.getAsDouble()) : "window_scale") + ']';
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

        @Override
        public String toString() {
            return this.name + "[writeColor=" + this.color + ", writeDepth=" + this.depth + ']';
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DepthTest
    extends RenderPhase {
        private final String depthFunction;
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
            this.depthFunction = string;
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

        @Override
        public String toString() {
            return this.name + '[' + this.depthFunction + ']';
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cull
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
    public static class Overlay
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
    public static class Lightmap
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

        @Override
        public String toString() {
            return this.name + '[' + this.enabled + ']';
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class OffsetTexturing
    extends Texturing {
        private final float x;
        private final float y;

        public OffsetTexturing(float x, float y) {
            super("offset_texturing", () -> RenderSystem.setTextureMatrix(Matrix4f.translate(x, y, 0.0f)), () -> RenderSystem.resetTextureMatrix());
            this.x = x;
            this.y = y;
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
    extends class_5939 {
        private final Optional<Identifier> id;
        private final boolean bilinear;
        private final boolean mipmap;

        public Texture(Identifier identifier, boolean bl, boolean bl2) {
            super(() -> {
                RenderSystem.enableTexture();
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.getTexture(identifier).setFilter(bl, bl2);
                RenderSystem.setShaderTexture(0, identifier);
            }, () -> {});
            this.id = Optional.of(identifier);
            this.bilinear = bl;
            this.mipmap = bl2;
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

        @Override
        public String toString() {
            return this.name + '[' + this.id + "(blur=" + this.bilinear + ", mipmap=" + this.mipmap + ")]";
        }

        @Override
        protected Optional<Identifier> getId() {
            return this.id;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5940
    extends class_5939 {
        private final Optional<Identifier> field_29453;

        private class_5940(ImmutableList<Triple<Identifier, Boolean, Boolean>> immutableList) {
            super(() -> {
                int i = 0;
                for (Triple triple : immutableList) {
                    TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                    textureManager.getTexture((Identifier)triple.getLeft()).setFilter((Boolean)triple.getMiddle(), (Boolean)triple.getRight());
                    RenderSystem.setShaderTexture(i++, (Identifier)triple.getLeft());
                }
            }, () -> {});
            this.field_29453 = immutableList.stream().findFirst().map(Triple::getLeft);
        }

        @Override
        protected Optional<Identifier> getId() {
            return this.field_29453;
        }

        public static class_5941 method_34560() {
            return new class_5941();
        }

        @Environment(value=EnvType.CLIENT)
        public static final class class_5941 {
            private final ImmutableList.Builder<Triple<Identifier, Boolean, Boolean>> field_29454 = new ImmutableList.Builder();

            public class_5941 method_34563(Identifier identifier, boolean bl, boolean bl2) {
                this.field_29454.add((Object)Triple.of(identifier, bl, bl2));
                return this;
            }

            public class_5940 method_34562() {
                return new class_5940((ImmutableList)this.field_29454.build());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5939
    extends RenderPhase {
        public class_5939(Runnable runnable, Runnable runnable2) {
            super("texture", runnable, runnable2);
        }

        private class_5939() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<Identifier> getId() {
            return Optional.empty();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5942
    extends RenderPhase {
        private final Optional<Supplier<class_5944>> field_29455;

        public class_5942(Supplier<class_5944> supplier) {
            super("shader", () -> RenderSystem.setShader(supplier), () -> {});
            this.field_29455 = Optional.of(supplier);
        }

        public class_5942() {
            super("shader", () -> RenderSystem.setShader(() -> null), () -> {});
            this.field_29455 = Optional.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_5942 lv = (class_5942)object;
            return this.field_29455.equals(lv.field_29455);
        }

        @Override
        public int hashCode() {
            return this.field_29455.hashCode();
        }

        @Override
        public String toString() {
            return this.name + '[' + this.field_29455 + "]";
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

