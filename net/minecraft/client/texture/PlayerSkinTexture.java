/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ImageFilter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.UncaughtExceptionLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinTexture
extends ResourceTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger DOWNLOAD_THREAD_COUNTER = new AtomicInteger(0);
    @Nullable
    private final File cacheFile;
    private final String url;
    @Nullable
    private final ImageFilter filter;
    @Nullable
    private Thread downloadThread;
    private volatile boolean field_5215;

    public PlayerSkinTexture(@Nullable File file, String string, Identifier identifier, @Nullable ImageFilter imageFilter) {
        super(identifier);
        this.cacheFile = file;
        this.url = string;
        this.filter = imageFilter;
    }

    private void method_4531(NativeImage nativeImage) {
        TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void method_4534(NativeImage nativeImage) {
        if (this.filter != null) {
            this.filter.method_3238();
        }
        PlayerSkinTexture playerSkinTexture = this;
        synchronized (playerSkinTexture) {
            this.method_4531(nativeImage);
            this.field_5215 = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        if (!this.field_5215) {
            PlayerSkinTexture playerSkinTexture = this;
            synchronized (playerSkinTexture) {
                super.load(resourceManager);
                this.field_5215 = true;
            }
        }
        if (this.downloadThread == null) {
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
                try (NativeImage nativeImage = null;){
                    nativeImage = NativeImage.fromInputStream(new FileInputStream(this.cacheFile));
                    if (this.filter != null) {
                        nativeImage = this.filter.filterImage(nativeImage);
                    }
                    this.method_4534(nativeImage);
                }
            } else {
                this.startTextureDownload();
            }
        }
    }

    protected void startTextureDownload() {
        this.downloadThread = new Thread("Texture Downloader #" + DOWNLOAD_THREAD_COUNTER.incrementAndGet()){

            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                LOGGER.debug("Downloading http texture from {} to {}", (Object)PlayerSkinTexture.this.url, (Object)PlayerSkinTexture.this.cacheFile);
                try {
                    InputStream inputStream;
                    httpURLConnection = (HttpURLConnection)new URL(PlayerSkinTexture.this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() / 100 != 2) {
                        return;
                    }
                    if (PlayerSkinTexture.this.cacheFile != null) {
                        FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), PlayerSkinTexture.this.cacheFile);
                        inputStream = new FileInputStream(PlayerSkinTexture.this.cacheFile);
                    } else {
                        inputStream = httpURLConnection.getInputStream();
                    }
                    MinecraftClient.getInstance().execute(() -> {
                        NativeImage nativeImage = null;
                        try {
                            nativeImage = NativeImage.fromInputStream(inputStream);
                            if (PlayerSkinTexture.this.filter != null) {
                                nativeImage = PlayerSkinTexture.this.filter.filterImage(nativeImage);
                            }
                            PlayerSkinTexture.this.method_4534(nativeImage);
                        } catch (IOException iOException) {
                            LOGGER.warn("Error while loading the skin texture", (Throwable)iOException);
                        } finally {
                            if (nativeImage != null) {
                                nativeImage.close();
                            }
                            IOUtils.closeQuietly(inputStream);
                        }
                    });
                } catch (Exception exception) {
                    LOGGER.error("Couldn't download http texture", (Throwable)exception);
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        };
        this.downloadThread.setDaemon(true);
        this.downloadThread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        this.downloadThread.start();
    }
}

