/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashScreen
extends Overlay {
    private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
    private final MinecraftClient client;
    private final ResourceReloadMonitor reloadMonitor;
    private final Runnable field_18218;
    private final boolean field_18219;
    private float field_17770;
    private long field_17771 = -1L;
    private long field_18220 = -1L;

    public SplashScreen(MinecraftClient minecraftClient, ResourceReloadMonitor resourceReloadMonitor, Runnable runnable, boolean bl) {
        this.client = minecraftClient;
        this.reloadMonitor = resourceReloadMonitor;
        this.field_18218 = runnable;
        this.field_18219 = bl;
    }

    public static void init(MinecraftClient minecraftClient) {
        minecraftClient.getTextureManager().registerTexture(LOGO, new LogoTexture());
    }

    @Override
    public void render(int i, int j, float f) {
        float o;
        int n;
        float h;
        int k = this.client.getWindow().getScaledWidth();
        int l = this.client.getWindow().getScaledHeight();
        long m = Util.getMeasuringTimeMs();
        if (this.field_18219 && (this.reloadMonitor.isLoadStageComplete() || this.client.currentScreen != null) && this.field_18220 == -1L) {
            this.field_18220 = m;
        }
        float g = this.field_17771 > -1L ? (float)(m - this.field_17771) / 1000.0f : -1.0f;
        float f2 = h = this.field_18220 > -1L ? (float)(m - this.field_18220) / 500.0f : -1.0f;
        if (g >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(0, 0, f);
            }
            n = MathHelper.ceil((1.0f - MathHelper.clamp(g - 1.0f, 0.0f, 1.0f)) * 255.0f);
            SplashScreen.fill(0, 0, k, l, 0xFFFFFF | n << 24);
            o = 1.0f - MathHelper.clamp(g - 1.0f, 0.0f, 1.0f);
        } else if (this.field_18219) {
            if (this.client.currentScreen != null && h < 1.0f) {
                this.client.currentScreen.render(i, j, f);
            }
            n = MathHelper.ceil(MathHelper.clamp((double)h, 0.15, 1.0) * 255.0);
            SplashScreen.fill(0, 0, k, l, 0xFFFFFF | n << 24);
            o = MathHelper.clamp(h, 0.0f, 1.0f);
        } else {
            SplashScreen.fill(0, 0, k, l, -1);
            o = 1.0f;
        }
        n = (this.client.getWindow().getScaledWidth() - 256) / 2;
        int p = (this.client.getWindow().getScaledHeight() - 256) / 2;
        this.client.getTextureManager().bindTexture(LOGO);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, o);
        this.blit(n, p, 0, 0, 256, 256);
        float q = this.reloadMonitor.getProgress();
        this.field_17770 = MathHelper.clamp(this.field_17770 * 0.95f + q * 0.050000012f, 0.0f, 1.0f);
        if (g < 1.0f) {
            this.renderProgressBar(k / 2 - 150, l / 4 * 3, k / 2 + 150, l / 4 * 3 + 10, 1.0f - MathHelper.clamp(g, 0.0f, 1.0f));
        }
        if (g >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.field_17771 == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.field_18219 || h >= 2.0f)) {
            this.reloadMonitor.throwExceptions();
            this.field_17771 = Util.getMeasuringTimeMs();
            this.field_18218.run();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            }
        }
    }

    private void renderProgressBar(int i, int j, int k, int l, float f) {
        int m = MathHelper.ceil((float)(k - i - 1) * this.field_17770);
        SplashScreen.fill(i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0f - f) * 255.0f) << 16 | Math.round((1.0f - f) * 255.0f) << 8 | Math.round((1.0f - f) * 255.0f));
        SplashScreen.fill(i, j, k, l, -1);
        SplashScreen.fill(i + 1, j + 1, i + m, l - 1, 0xFF000000 | (int)MathHelper.lerp(1.0f - f, 226.0f, 255.0f) << 16 | (int)MathHelper.lerp(1.0f - f, 40.0f, 255.0f) << 8 | (int)MathHelper.lerp(1.0f - f, 55.0f, 255.0f));
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
            DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackDownloader().getPack();
            try (InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, LOGO);){
                ResourceTexture.TextureData textureData = new ResourceTexture.TextureData(null, NativeImage.read(inputStream));
                return textureData;
            } catch (IOException iOException) {
                return new ResourceTexture.TextureData(iOException);
            }
        }
    }
}

