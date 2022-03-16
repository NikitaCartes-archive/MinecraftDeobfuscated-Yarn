/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashOverlay
extends Overlay {
    static final Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");
    private static final int MOJANG_RED = ColorHelper.Argb.getArgb(255, 239, 50, 61);
    private static final int MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);
    private static final IntSupplier BRAND_ARGB = () -> MinecraftClient.getInstance().options.getMonochromeLogo().getValue() != false ? MONOCHROME_BLACK : MOJANG_RED;
    private static final int field_32251 = 240;
    private static final float LOGO_RIGHT_HALF_V = 60.0f;
    private static final int field_32253 = 60;
    private static final int field_32254 = 120;
    private static final float LOGO_OVERLAP = 0.0625f;
    private static final float PROGRESS_LERP_DELTA = 0.95f;
    public static final long RELOAD_COMPLETE_FADE_DURATION = 1000L;
    public static final long RELOAD_START_FADE_DURATION = 500L;
    private final MinecraftClient client;
    private final ResourceReload reload;
    private final Consumer<Optional<Throwable>> exceptionHandler;
    private final boolean reloading;
    private float progress;
    private long reloadCompleteTime = -1L;
    private long reloadStartTime = -1L;

    public SplashOverlay(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading) {
        this.client = client;
        this.reload = monitor;
        this.exceptionHandler = exceptionHandler;
        this.reloading = reloading;
    }

    public static void init(MinecraftClient client) {
        client.getTextureManager().registerTexture(LOGO, new LogoTexture());
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float h;
        int k;
        float g;
        int i = this.client.getWindow().getScaledWidth();
        int j = this.client.getWindow().getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }
        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0f : -1.0f;
        float f2 = g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0f : -1.0f;
        if (f >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(matrices, 0, 0, delta);
            }
            k = MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f);
            SplashOverlay.fill(matrices, 0, 0, i, j, SplashOverlay.withAlpha(BRAND_ARGB.getAsInt(), k));
            h = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0f) {
                this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
            }
            k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
            SplashOverlay.fill(matrices, 0, 0, i, j, SplashOverlay.withAlpha(BRAND_ARGB.getAsInt(), k));
            h = MathHelper.clamp(g, 0.0f, 1.0f);
        } else {
            k = BRAND_ARGB.getAsInt();
            float m = (float)(k >> 16 & 0xFF) / 255.0f;
            float n = (float)(k >> 8 & 0xFF) / 255.0f;
            float o = (float)(k & 0xFF) / 255.0f;
            GlStateManager._clearColor(m, n, o, 1.0f);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0f;
        }
        k = (int)((double)this.client.getWindow().getScaledWidth() * 0.5);
        int p = (int)((double)this.client.getWindow().getScaledHeight() * 0.5);
        double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75, (double)this.client.getWindow().getScaledHeight()) * 0.25;
        int q = (int)(d * 0.5);
        double e = d * 4.0;
        int r = (int)(e * 0.5);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, h);
        SplashOverlay.drawTexture(matrices, k - r, p - q, r, (int)d, -0.0625f, 0.0f, 120, 60, 120, 120);
        SplashOverlay.drawTexture(matrices, k, p - q, r, (int)d, 0.0625f, 60.0f, 120, 60, 120, 120);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        int s = (int)((double)this.client.getWindow().getScaledHeight() * 0.8325);
        float t = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95f + t * 0.050000012f, 0.0f, 1.0f);
        if (f < 1.0f) {
            this.renderProgressBar(matrices, i / 2 - r, s - 5, i / 2 + r, s + 5, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
        }
        if (f >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0f)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.exceptionHandler.accept(Optional.of(throwable));
            }
            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            }
        }
    }

    private void renderProgressBar(MatrixStack matrices, int minX, int minY, int maxX, int maxY, float opacity) {
        int i = MathHelper.ceil((float)(maxX - minX - 2) * this.progress);
        int j = Math.round(opacity * 255.0f);
        int k = ColorHelper.Argb.getArgb(j, 255, 255, 255);
        SplashOverlay.fill(matrices, minX + 2, minY + 2, minX + i, maxY - 2, k);
        SplashOverlay.fill(matrices, minX + 1, minY, maxX - 1, minY + 1, k);
        SplashOverlay.fill(matrices, minX + 1, maxY, maxX - 1, maxY - 1, k);
        SplashOverlay.fill(matrices, minX, minY, minX + 1, maxY, k);
        SplashOverlay.fill(matrices, maxX, minY, maxX - 1, maxY, k);
    }

    @Override
    public boolean pausesGame() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static class LogoTexture
    extends ResourceTexture {
        public LogoTexture() {
            super(LOGO);
        }

        @Override
        protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
            ResourceTexture.TextureData textureData;
            block8: {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackProvider().getPack();
                InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, LOGO);
                try {
                    textureData = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
                    if (inputStream == null) break block8;
                } catch (Throwable throwable) {
                    try {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    } catch (IOException iOException) {
                        return new ResourceTexture.TextureData(iOException);
                    }
                }
                inputStream.close();
            }
            return textureData;
        }
    }
}

