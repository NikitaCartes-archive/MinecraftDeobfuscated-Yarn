/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinTexture
extends ResourceTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private final File cacheFile;
    private final String url;
    private final boolean field_20842;
    @Nullable
    private final Runnable field_20843;
    @Nullable
    private CompletableFuture<?> field_20844;
    private boolean field_5215;

    public PlayerSkinTexture(@Nullable File file, String string, Identifier identifier, boolean bl, @Nullable Runnable runnable) {
        super(identifier);
        this.cacheFile = file;
        this.url = string;
        this.field_20842 = bl;
        this.field_20843 = runnable;
    }

    private void method_4534(NativeImage nativeImage) {
        if (this.field_20843 != null) {
            this.field_20843.run();
        }
        MinecraftClient.getInstance().execute(() -> {
            this.field_5215 = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.method_4531(nativeImage));
            } else {
                this.method_4531(nativeImage);
            }
        });
    }

    private void method_4531(NativeImage nativeImage) {
        TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, true);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        NativeImage nativeImage;
        MinecraftClient.getInstance().execute(() -> {
            if (!this.field_5215) {
                try {
                    super.load(resourceManager);
                } catch (IOException iOException) {
                    LOGGER.warn("Failed to load texture: {}", (Object)this.location, (Object)iOException);
                }
                this.field_5215 = true;
            }
        });
        if (this.field_20844 != null) {
            return;
        }
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
            FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
            nativeImage = this.method_22795(fileInputStream);
        } else {
            nativeImage = null;
        }
        if (nativeImage != null) {
            this.method_4534(nativeImage);
            return;
        }
        this.field_20844 = CompletableFuture.runAsync(() -> {
            HttpURLConnection httpURLConnection = null;
            LOGGER.debug("Downloading http texture from {} to {}", (Object)this.url, (Object)this.cacheFile);
            try {
                InputStream inputStream;
                httpURLConnection = (HttpURLConnection)new URL(this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 != 2) {
                    return;
                }
                if (this.cacheFile != null) {
                    FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), this.cacheFile);
                    inputStream = new FileInputStream(this.cacheFile);
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
                MinecraftClient.getInstance().execute(() -> {
                    NativeImage nativeImage = this.method_22795(inputStream);
                    if (nativeImage != null) {
                        this.method_4534(nativeImage);
                    }
                });
            } catch (Exception exception) {
                LOGGER.error("Couldn't download http texture", (Throwable)exception);
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }, Util.getServerWorkerExecutor());
    }

    @Nullable
    private NativeImage method_22795(InputStream inputStream) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(inputStream);
            if (this.field_20842) {
                nativeImage = PlayerSkinTexture.method_22798(nativeImage);
            }
        } catch (IOException iOException) {
            LOGGER.warn("Error while loading the skin texture", (Throwable)iOException);
        }
        return nativeImage;
    }

    private static NativeImage method_22798(NativeImage nativeImage) {
        boolean bl;
        boolean bl2 = bl = nativeImage.getHeight() == 32;
        if (bl) {
            NativeImage nativeImage2 = new NativeImage(64, 64, true);
            nativeImage2.copyFrom(nativeImage);
            nativeImage.close();
            nativeImage = nativeImage2;
            nativeImage.fillRect(0, 32, 64, 32, 0);
            nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        PlayerSkinTexture.method_22796(nativeImage, 0, 0, 32, 16);
        if (bl) {
            PlayerSkinTexture.method_22794(nativeImage, 32, 0, 64, 32);
        }
        PlayerSkinTexture.method_22796(nativeImage, 0, 16, 64, 32);
        PlayerSkinTexture.method_22796(nativeImage, 16, 48, 48, 64);
        return nativeImage;
    }

    private static void method_22794(NativeImage nativeImage, int i, int j, int k, int l) {
        int n;
        int m;
        for (m = i; m < k; ++m) {
            for (n = j; n < l; ++n) {
                int o = nativeImage.getPixelRgba(m, n);
                if ((o >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (m = i; m < k; ++m) {
            for (n = j; n < l; ++n) {
                nativeImage.setPixelRgba(m, n, nativeImage.getPixelRgba(m, n) & 0xFFFFFF);
            }
        }
    }

    private static void method_22796(NativeImage nativeImage, int i, int j, int k, int l) {
        for (int m = i; m < k; ++m) {
            for (int n = j; n < l; ++n) {
                nativeImage.setPixelRgba(m, n, nativeImage.getPixelRgba(m, n) | 0xFF000000);
            }
        }
    }
}

