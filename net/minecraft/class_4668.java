/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class class_4668 {
    protected final String field_21363;
    private final Runnable field_21361;
    private final Runnable field_21362;
    protected static final class_4685 field_21364 = new class_4685("no_transparency", () -> RenderSystem.disableBlend(), () -> {});
    protected static final class_4685 field_21365 = new class_4685("forced_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendColor(1.0f, 1.0f, 1.0f, 0.15f);
        RenderSystem.blendFunc(GlStateManager.class_4535.CONSTANT_ALPHA, GlStateManager.class_4534.ONE_MINUS_CONSTANT_ALPHA);
        RenderSystem.depthMask(false);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
    });
    protected static final class_4685 field_21366 = new class_4685("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final class_4685 field_21367 = new class_4685("lightning_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final class_4685 field_21368 = new class_4685("glint_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_COLOR, GlStateManager.class_4534.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final class_4685 field_21369 = new class_4685("crumbling_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.DST_COLOR, GlStateManager.class_4534.SRC_COLOR, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final class_4685 field_21370 = new class_4685("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }, () -> RenderSystem.disableBlend());
    protected static final class_4669 field_21371 = new class_4669(0.0f);
    protected static final class_4669 field_21372 = new class_4669(0.1f);
    protected static final class_4669 field_21373 = new class_4669(0.5f);
    protected static final class_4681 field_21374 = new class_4681(false);
    protected static final class_4681 field_21375 = new class_4681(true);
    protected static final class_4683 field_21376 = new class_4683(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, true);
    protected static final class_4683 field_21377 = new class_4683(SpriteAtlasTexture.BLOCK_ATLAS_TEX, false, false);
    protected static final class_4683 field_21378 = new class_4683();
    protected static final class_4684 field_21379 = new class_4684("default_texturing", () -> {}, () -> {});
    protected static final class_4684 field_21380 = new class_4684("outline_texturing", () -> RenderSystem.setupOutline(), () -> RenderSystem.teardownOutline());
    protected static final class_4684 field_21381 = new class_4684("glint_texturing", () -> class_4668.method_23517(8.0f), () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final class_4684 field_21382 = new class_4684("entity_glint_texturing", () -> class_4668.method_23517(0.16f), () -> {
        RenderSystem.matrixMode(5890);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final class_4676 field_21383 = new class_4676(true);
    protected static final class_4676 field_21384 = new class_4676(false);
    protected static final class_4679 field_21385 = new class_4679(true);
    protected static final class_4679 field_21386 = new class_4679(false);
    protected static final class_4673 field_21387 = new class_4673(true);
    protected static final class_4673 field_21388 = new class_4673(false);
    protected static final class_4671 field_21344 = new class_4671(true);
    protected static final class_4671 field_21345 = new class_4671(false);
    protected static final class_4672 field_21346 = new class_4672(519);
    protected static final class_4672 field_21347 = new class_4672(514);
    protected static final class_4672 field_21348 = new class_4672(515);
    protected static final class_4686 field_21349 = new class_4686(true, true);
    protected static final class_4686 field_21350 = new class_4686(true, false);
    protected static final class_4686 field_21351 = new class_4686(false, true);
    protected static final class_4675 field_21352 = new class_4675("no_layering", () -> {}, () -> {});
    protected static final class_4675 field_21353 = new class_4675("polygon_offset_layering", () -> {
        RenderSystem.polygonOffset(-1.0f, -10.0f);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
    });
    protected static final class_4675 field_21354 = new class_4675("projection_layering", () -> {
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(1.0f, 1.0f, 0.999f);
        RenderSystem.matrixMode(5888);
    }, () -> {
        RenderSystem.matrixMode(5889);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
    });
    protected static final class_4674 field_21355 = new class_4674("no_fog", () -> RenderSystem.disableFog(), () -> RenderSystem.enableFog());
    protected static final class_4674 field_21356 = new class_4674("fog", () -> {}, () -> {});
    protected static final class_4674 field_21357 = new class_4674("black_fog", () -> BackgroundRenderer.setFogBlack(true), () -> BackgroundRenderer.setFogBlack(false));
    protected static final class_4678 field_21358 = new class_4678("main_target", () -> {}, () -> {});
    protected static final class_4678 field_21359 = new class_4678("outline_target", () -> MinecraftClient.getInstance().worldRenderer.method_22990().beginWrite(false), () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false));
    protected static final class_4677 field_21360 = new class_4677(1.0f);

    public class_4668(String string, Runnable runnable, Runnable runnable2) {
        this.field_21363 = string;
        this.field_21361 = runnable;
        this.field_21362 = runnable2;
    }

    public void method_23516() {
        this.field_21361.run();
    }

    public void method_23518() {
        this.field_21362.run();
    }

    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_4668 lv = (class_4668)object;
        return this.field_21363.equals(lv.field_21363);
    }

    public int hashCode() {
        return this.field_21363.hashCode();
    }

    private static void method_23517(float f) {
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        long l = SystemUtil.getMeasuringTimeMs() * 8L;
        float g = (float)(l % 110000L) / 110000.0f;
        float h = (float)(l % 30000L) / 30000.0f;
        RenderSystem.translatef(-g, h, 0.0f);
        RenderSystem.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(f, f, f);
        RenderSystem.matrixMode(5888);
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4677
    extends class_4668 {
        private final float field_21392;

        public class_4677(float f) {
            super("alpha", () -> {
                if (f != 1.0f) {
                    RenderSystem.lineWidth(f);
                }
            }, () -> {
                if (f != 1.0f) {
                    RenderSystem.lineWidth(1.0f);
                }
            });
            this.field_21392 = f;
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
            return this.field_21392 == ((class_4677)object).field_21392;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), Float.valueOf(this.field_21392));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4678
    extends class_4668 {
        public class_4678(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4674
    extends class_4668 {
        public class_4674(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4675
    extends class_4668 {
        public class_4675(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4686
    extends class_4668 {
        private final boolean field_21400;
        private final boolean field_21401;

        public class_4686(boolean bl, boolean bl2) {
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
            this.field_21400 = bl;
            this.field_21401 = bl2;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4686 lv = (class_4686)object;
            return this.field_21400 == lv.field_21400 && this.field_21401 == lv.field_21401;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.field_21400, this.field_21401);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4672
    extends class_4668 {
        private final int field_21391;

        public class_4672(int i) {
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
            this.field_21391 = i;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4672 lv = (class_4672)object;
            return this.field_21391 == lv.field_21391;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.field_21391);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4671
    extends class_4670 {
        public class_4671(boolean bl) {
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
    public static class class_4673
    extends class_4670 {
        public class_4673(boolean bl) {
            super("diffuse_lighting", () -> {
                if (bl) {
                    GuiLighting.method_22890();
                }
            }, () -> {
                if (bl) {
                    GuiLighting.disable();
                }
            }, bl);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4679
    extends class_4670 {
        public class_4679(boolean bl) {
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
    public static class class_4676
    extends class_4670 {
        public class_4676(boolean bl) {
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
    static class class_4670
    extends class_4668 {
        private final boolean field_21390;

        public class_4670(String string, Runnable runnable, Runnable runnable2, boolean bl) {
            super(string, runnable, runnable2);
            this.field_21390 = bl;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4670 lv = (class_4670)object;
            return this.field_21390 == lv.field_21390;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(this.field_21390);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_4680
    extends class_4684 {
        private final int field_21393;

        public class_4680(int i) {
            super("portal_texturing", () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.translatef(0.5f, 0.5f, 0.0f);
                RenderSystem.scalef(0.5f, 0.5f, 1.0f);
                RenderSystem.translatef(17.0f / (float)i, (2.0f + (float)i / 1.5f) * ((float)(SystemUtil.getMeasuringTimeMs() % 800000L) / 800000.0f), 0.0f);
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
            this.field_21393 = i;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4680 lv = (class_4680)object;
            return this.field_21393 == lv.field_21393;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.field_21393);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class class_4682
    extends class_4684 {
        private final float field_21395;
        private final float field_21396;

        public class_4682(float f, float g) {
            super("swirl_texturing", () -> {
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
            this.field_21395 = f;
            this.field_21396 = g;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4682 lv = (class_4682)object;
            return Float.compare(lv.field_21395, this.field_21395) == 0 && Float.compare(lv.field_21396, this.field_21396) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Float.valueOf(this.field_21395), Float.valueOf(this.field_21396));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4684
    extends class_4668 {
        public class_4684(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4683
    extends class_4668 {
        private final Optional<Identifier> field_21397;
        private final boolean field_21398;
        private final boolean field_21399;

        public class_4683(Identifier identifier, boolean bl, boolean bl2) {
            super("texture", () -> {
                RenderSystem.enableTexture();
                TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
                textureManager.bindTexture(identifier);
                textureManager.getTexture(identifier).setFilter(bl, bl2);
            }, () -> {});
            this.field_21397 = Optional.of(identifier);
            this.field_21398 = bl;
            this.field_21399 = bl2;
        }

        public class_4683() {
            super("texture", () -> RenderSystem.disableTexture(), () -> RenderSystem.enableTexture());
            this.field_21397 = Optional.empty();
            this.field_21398 = false;
            this.field_21399 = false;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4683 lv = (class_4683)object;
            return this.field_21397.equals(lv.field_21397) && this.field_21398 == lv.field_21398 && this.field_21399 == lv.field_21399;
        }

        @Override
        public int hashCode() {
            return this.field_21397.hashCode();
        }

        protected Optional<Identifier> method_23564() {
            return this.field_21397;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4681
    extends class_4668 {
        private final boolean field_21394;

        public class_4681(boolean bl) {
            super("shade_model", () -> RenderSystem.shadeModel(bl ? 7425 : 7424), () -> RenderSystem.shadeModel(7424));
            this.field_21394 = bl;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            class_4681 lv = (class_4681)object;
            return this.field_21394 == lv.field_21394;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(this.field_21394);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4669
    extends class_4668 {
        private final float field_21389;

        public class_4669(float f) {
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
            this.field_21389 = f;
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
            return this.field_21389 == ((class_4669)object).field_21389;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), Float.valueOf(this.field_21389));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4685
    extends class_4668 {
        public class_4685(String string, Runnable runnable, Runnable runnable2) {
            super(string, runnable, runnable2);
        }
    }
}

