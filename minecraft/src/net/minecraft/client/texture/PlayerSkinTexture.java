package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PlayerSkinTexture extends ResourceTexture {
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
		MinecraftClient.getInstance().execute(() -> {
			if (!this.field_5215) {
				try {
					super.load(resourceManager);
				} catch (IOException var3x) {
					LOGGER.warn("Failed to load texture: {}", this.location, var3x);
				}

				this.field_5215 = true;
			}
		});
		if (this.field_20844 == null) {
			NativeImage nativeImage;
			if (this.cacheFile != null && this.cacheFile.isFile()) {
				LOGGER.debug("Loading http texture from local cache ({})", this.cacheFile);
				FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
				nativeImage = this.method_22795(fileInputStream);
			} else {
				nativeImage = null;
			}

			if (nativeImage != null) {
				this.method_4534(nativeImage);
			} else {
				this.field_20844 = CompletableFuture.runAsync(() -> {
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
								NativeImage nativeImagex = this.method_22795(inputStream);
								if (nativeImagex != null) {
									this.method_4534(nativeImagex);
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
				}, SystemUtil.getServerWorkerExecutor());
			}
		}
	}

	@Nullable
	private NativeImage method_22795(InputStream inputStream) {
		NativeImage nativeImage = null;

		try {
			nativeImage = NativeImage.read(inputStream);
			if (this.field_20842) {
				nativeImage = method_22798(nativeImage);
			}
		} catch (IOException var4) {
			LOGGER.warn("Error while loading the skin texture", (Throwable)var4);
		}

		return nativeImage;
	}

	private static NativeImage method_22798(NativeImage nativeImage) {
		boolean bl = nativeImage.getHeight() == 32;
		if (bl) {
			NativeImage nativeImage2 = new NativeImage(64, 64, true);
			nativeImage2.copyFrom(nativeImage);
			nativeImage.close();
			nativeImage = nativeImage2;
			nativeImage2.fillRect(0, 32, 64, 32, 0);
			nativeImage2.copyRect(4, 16, 16, 32, 4, 4, true, false);
			nativeImage2.copyRect(8, 16, 16, 32, 4, 4, true, false);
			nativeImage2.copyRect(0, 20, 24, 32, 4, 12, true, false);
			nativeImage2.copyRect(4, 20, 16, 32, 4, 12, true, false);
			nativeImage2.copyRect(8, 20, 8, 32, 4, 12, true, false);
			nativeImage2.copyRect(12, 20, 16, 32, 4, 12, true, false);
			nativeImage2.copyRect(44, 16, -8, 32, 4, 4, true, false);
			nativeImage2.copyRect(48, 16, -8, 32, 4, 4, true, false);
			nativeImage2.copyRect(40, 20, 0, 32, 4, 12, true, false);
			nativeImage2.copyRect(44, 20, -8, 32, 4, 12, true, false);
			nativeImage2.copyRect(48, 20, -16, 32, 4, 12, true, false);
			nativeImage2.copyRect(52, 20, -8, 32, 4, 12, true, false);
		}

		method_22796(nativeImage, 0, 0, 32, 16);
		if (bl) {
			method_22794(nativeImage, 32, 0, 64, 32);
		}

		method_22796(nativeImage, 0, 16, 64, 32);
		method_22796(nativeImage, 16, 48, 48, 64);
		return nativeImage;
	}

	private static void method_22794(NativeImage nativeImage, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				int o = nativeImage.getPixelRGBA(m, n);
				if ((o >> 24 & 0xFF) < 128) {
					return;
				}
			}
		}

		for (int m = i; m < k; m++) {
			for (int nx = j; nx < l; nx++) {
				nativeImage.setPixelRGBA(m, nx, nativeImage.getPixelRGBA(m, nx) & 16777215);
			}
		}
	}

	private static void method_22796(NativeImage nativeImage, int i, int j, int k, int l) {
		for (int m = i; m < k; m++) {
			for (int n = j; n < l; n++) {
				nativeImage.setPixelRGBA(m, n, nativeImage.getPixelRGBA(m, n) | 0xFF000000);
			}
		}
	}
}
