package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PlayerSkinTexture extends ResourceTexture {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int WIDTH = 64;
	private static final int HEIGHT = 64;
	private static final int OLD_HEIGHT = 32;
	@Nullable
	private final File cacheFile;
	private final String url;
	private final boolean convertLegacy;
	@Nullable
	private final Runnable loadedCallback;
	@Nullable
	private CompletableFuture<?> loader;
	private boolean loaded;

	public PlayerSkinTexture(@Nullable File cacheFile, String url, Identifier fallbackSkin, boolean convertLegacy, @Nullable Runnable callback) {
		super(fallbackSkin);
		this.cacheFile = cacheFile;
		this.url = url;
		this.convertLegacy = convertLegacy;
		this.loadedCallback = callback;
	}

	private void onTextureLoaded(NativeImage image) {
		if (this.loadedCallback != null) {
			this.loadedCallback.run();
		}

		MinecraftClient.getInstance().execute(() -> {
			this.loaded = true;
			if (!RenderSystem.isOnRenderThread()) {
				RenderSystem.recordRenderCall(() -> this.uploadTexture(image));
			} else {
				this.uploadTexture(image);
			}
		});
	}

	private void uploadTexture(NativeImage image) {
		TextureUtil.prepareImage(this.getGlId(), image.getWidth(), image.getHeight());
		image.upload(0, 0, 0, true);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		MinecraftClient.getInstance().execute(() -> {
			if (!this.loaded) {
				try {
					super.load(manager);
				} catch (IOException var3x) {
					LOGGER.warn("Failed to load texture: {}", this.location, var3x);
				}

				this.loaded = true;
			}
		});
		if (this.loader == null) {
			NativeImage nativeImage;
			if (this.cacheFile != null && this.cacheFile.isFile()) {
				LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
				FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
				nativeImage = this.loadTexture(fileInputStream);
			} else {
				nativeImage = null;
			}

			if (nativeImage != null) {
				this.onTextureLoaded(nativeImage);
			} else {
				this.loader = CompletableFuture.runAsync(() -> {
					HttpURLConnection httpURLConnection = null;
					LOGGER.debug("Downloading http texture from {} to {}", this.url, this.cacheFile);

					try {
						httpURLConnection = (HttpURLConnection)new URL(this.url).openConnection(MinecraftClient.getInstance().getNetworkProxy());
						httpURLConnection.setDoInput(true);
						httpURLConnection.setDoOutput(false);
						httpURLConnection.connect();
						if (httpURLConnection.getResponseCode() / 100 == 2) {
							InputStream inputStream;
							if (this.cacheFile != null) {
								FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), this.cacheFile);
								inputStream = new FileInputStream(this.cacheFile);
							} else {
								inputStream = httpURLConnection.getInputStream();
							}

							MinecraftClient.getInstance().execute(() -> {
								NativeImage nativeImagex = this.loadTexture(inputStream);
								if (nativeImagex != null) {
									this.onTextureLoaded(nativeImagex);
								}
							});
							return;
						}
					} catch (Exception var6) {
						LOGGER.error("Couldn't download http texture", (Throwable)var6);
						return;
					} finally {
						if (httpURLConnection != null) {
							httpURLConnection.disconnect();
						}
					}
				}, Util.getMainWorkerExecutor().named("downloadTexture"));
			}
		}
	}

	@Nullable
	private NativeImage loadTexture(InputStream stream) {
		NativeImage nativeImage = null;

		try {
			nativeImage = NativeImage.read(stream);
			if (this.convertLegacy) {
				nativeImage = this.remapTexture(nativeImage);
			}
		} catch (Exception var4) {
			LOGGER.warn("Error while loading the skin texture", (Throwable)var4);
		}

		return nativeImage;
	}

	@Nullable
	private NativeImage remapTexture(NativeImage image) {
		int i = image.getHeight();
		int j = image.getWidth();
		if (j == 64 && (i == 32 || i == 64)) {
			boolean bl = i == 32;
			if (bl) {
				NativeImage nativeImage = new NativeImage(64, 64, true);
				nativeImage.copyFrom(image);
				image.close();
				image = nativeImage;
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

			stripAlpha(image, 0, 0, 32, 16);
			if (bl) {
				stripColor(image, 32, 0, 64, 32);
			}

			stripAlpha(image, 0, 16, 64, 32);
			stripAlpha(image, 16, 48, 48, 64);
			return image;
		} else {
			image.close();
			LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", j, i, this.url);
			return null;
		}
	}

	private static void stripColor(NativeImage image, int x1, int y1, int x2, int y2) {
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				int k = image.getColorArgb(i, j);
				if (ColorHelper.getAlpha(k) < 128) {
					return;
				}
			}
		}

		for (int i = x1; i < x2; i++) {
			for (int jx = y1; jx < y2; jx++) {
				image.setColorArgb(i, jx, image.getColorArgb(i, jx) & 16777215);
			}
		}
	}

	private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				image.setColorArgb(i, j, ColorHelper.fullAlpha(image.getColorArgb(i, j)));
			}
		}
	}
}
