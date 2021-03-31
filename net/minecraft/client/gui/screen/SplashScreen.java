/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashScreen
extends Overlay {
    private static final Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");
    private static final int field_32249 = BackgroundHelper.ColorMixer.getArgb(255, 239, 50, 61);
    private static final int field_32250 = BackgroundHelper.ColorMixer.getArgb(255, 0, 0, 0);
    private static final IntSupplier BRAND_ARGB = () -> MinecraftClient.getInstance().options.monochromeLogo ? field_32250 : field_32249;
    private static final int field_32251 = 240;
    private static final float field_32252 = 60.0f;
    private static final int field_32253 = 60;
    private static final int field_32254 = 120;
    private static final float field_32255 = 0.0625f;
    private static final float field_32256 = 0.95f;
    public static final long field_32247 = 1000L;
    public static final long field_32248 = 500L;
    private final MinecraftClient client;
    private final ResourceReload reload;
    private final Consumer<Optional<Throwable>> exceptionHandler;
    private final boolean reloading;
    private float progress;
    private long reloadCompleteTime = -1L;
    private long reloadStartTime = -1L;

    public SplashScreen(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading) {
        this.client = client;
        this.reload = monitor;
        this.exceptionHandler = exceptionHandler;
        this.reloading = reloading;
    }

    public static void init(MinecraftClient client) {
        client.getTextureManager().registerTexture(LOGO, new LogoTexture());
    }

    private static int method_35732(int i, int j) {
        return i & 0xFFFFFF | j << 24;
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
            SplashScreen.fill(matrices, 0, 0, i, j, SplashScreen.method_35732(BRAND_ARGB.getAsInt(), k));
            h = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0f) {
                this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
            }
            k = MathHelper.ceil(MathHelper.clamp((double)g, 0.15, 1.0) * 255.0);
            SplashScreen.fill(matrices, 0, 0, i, j, SplashScreen.method_35732(BRAND_ARGB.getAsInt(), k));
            h = MathHelper.clamp(g, 0.0f, 1.0f);
        } else {
            SplashScreen.fill(matrices, 0, 0, i, j, BRAND_ARGB.getAsInt());
            h = 1.0f;
        }
        k = (int)((double)this.client.getWindow().getScaledWidth() * 0.5);
        int m = (int)((double)this.client.getWindow().getScaledHeight() * 0.5);
        double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75, (double)this.client.getWindow().getScaledHeight()) * 0.25;
        int n = (int)(d * 0.5);
        double e = d * 4.0;
        int o = (int)(e * 0.5);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, h);
        SplashScreen.drawTexture(matrices, k - o, m - n, o, (int)d, -0.0625f, 0.0f, 120, 60, 120, 120);
        SplashScreen.drawTexture(matrices, k, m - n, o, (int)d, 0.0625f, 60.0f, 120, 60, 120, 120);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        int p = (int)((double)this.client.getWindow().getScaledHeight() * 0.8325);
        float q = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95f + q * 0.050000012f, 0.0f, 1.0f);
        if (f < 1.0f) {
            this.renderProgressBar(matrices, i / 2 - o, p - 5, i / 2 + o, p + 5, 1.0f - MathHelper.clamp(f, 0.0f, 1.0f));
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

    private void renderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity) {
        int i = MathHelper.ceil((float)(x2 - x1 - 2) * this.progress);
        int j = Math.round(opacity * 255.0f);
        int k = BackgroundHelper.ColorMixer.getArgb(j, 255, 255, 255);
        SplashScreen.fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, k);
        SplashScreen.fill(matrices, x1 + 1, y2, x2 - 1, y2 - 1, k);
        SplashScreen.fill(matrices, x1, y1, x1 + 1, y2, k);
        SplashScreen.fill(matrices, x2, y1, x2 - 1, y2, k);
        SplashScreen.fill(matrices, x1 + 2, y1 + 2, x1 + i, y2 - 2, k);
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

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackProvider().getPack();
            try (InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, LOGO);){
                ResourceTexture.TextureData textureData = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
                return textureData;
            } catch (IOException iOException) {
                return new ResourceTexture.TextureData(iOException);
            }
        }
    }
}

