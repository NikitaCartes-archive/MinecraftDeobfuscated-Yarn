package net.minecraft.client.realms.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;

@Environment(EnvType.CLIENT)
public class RealmsTextureManager {
	private static final Map<String, RealmsTextureManager.RealmsTexture> TEXTURES = Maps.<String, RealmsTextureManager.RealmsTexture>newHashMap();
	static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.<String, Boolean>newHashMap();
	static final Map<String, String> FETCHED_SKINS = Maps.<String, String>newHashMap();
	static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ISLES = new Identifier("textures/gui/presets/isles.png");

	public static void bindWorldTemplate(String id, @Nullable String image) {
		if (image == null) {
			RenderSystem.setShaderTexture(0, ISLES);
		} else {
			int i = getTextureId(id, image);
			RenderSystem.setShaderTexture(0, i);
		}
	}

	public static void withBoundFace(String uuid, Runnable r) {
		bindFace(uuid);
		r.run();
	}

	private static void bindDefaultFace(UUID uuid) {
		RenderSystem.setShaderTexture(0, DefaultSkinHelper.getTexture(uuid));
	}

	private static void bindFace(String uuid) {
		UUID uUID = UUIDTypeAdapter.fromString(uuid);
		if (TEXTURES.containsKey(uuid)) {
			int i = ((RealmsTextureManager.RealmsTexture)TEXTURES.get(uuid)).textureId;
			RenderSystem.setShaderTexture(0, i);
		} else if (SKIN_FETCH_STATUS.containsKey(uuid)) {
			if (!(Boolean)SKIN_FETCH_STATUS.get(uuid)) {
				bindDefaultFace(uUID);
			} else if (FETCHED_SKINS.containsKey(uuid)) {
				int i = getTextureId(uuid, (String)FETCHED_SKINS.get(uuid));
				RenderSystem.setShaderTexture(0, i);
			} else {
				bindDefaultFace(uUID);
			}
		} else {
			SKIN_FETCH_STATUS.put(uuid, false);
			bindDefaultFace(uUID);
			Thread thread = new Thread("Realms Texture Downloader") {
				public void run() {
					Map<Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(uuid);
					if (map.containsKey(Type.SKIN)) {
						MinecraftProfileTexture minecraftProfileTexture = (MinecraftProfileTexture)map.get(Type.SKIN);
						String string = minecraftProfileTexture.getUrl();
						HttpURLConnection httpURLConnection = null;
						RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", string);

						try {
							try {
								httpURLConnection = (HttpURLConnection)new URL(string).openConnection(MinecraftClient.getInstance().getNetworkProxy());
								httpURLConnection.setDoInput(true);
								httpURLConnection.setDoOutput(false);
								httpURLConnection.connect();
								if (httpURLConnection.getResponseCode() / 100 != 2) {
									RealmsTextureManager.SKIN_FETCH_STATUS.remove(uuid);
									return;
								}

								BufferedImage bufferedImage;
								try {
									bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								} catch (Exception var17) {
									RealmsTextureManager.SKIN_FETCH_STATUS.remove(uuid);
									return;
								} finally {
									IOUtils.closeQuietly(httpURLConnection.getInputStream());
								}

								bufferedImage = new SkinProcessor().process(bufferedImage);
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
								RealmsTextureManager.FETCHED_SKINS.put(uuid, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
								RealmsTextureManager.SKIN_FETCH_STATUS.put(uuid, true);
							} catch (Exception var19) {
								RealmsTextureManager.LOGGER.error("Couldn't download http texture", (Throwable)var19);
								RealmsTextureManager.SKIN_FETCH_STATUS.remove(uuid);
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect();
							}
						}
					} else {
						RealmsTextureManager.SKIN_FETCH_STATUS.put(uuid, true);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static int getTextureId(String id, String image) {
		RealmsTextureManager.RealmsTexture realmsTexture = (RealmsTextureManager.RealmsTexture)TEXTURES.get(id);
		if (realmsTexture != null && realmsTexture.image.equals(image)) {
			return realmsTexture.textureId;
		} else {
			int i;
			if (realmsTexture != null) {
				i = realmsTexture.textureId;
			} else {
				i = GlStateManager._genTexture();
			}

			RealmsTextureManager.RealmsTextureImage realmsTextureImage = RealmsTextureManager.RealmsTextureImage.fromBase64(image);
			RenderSystem.activeTexture(33984);
			RenderSystem.bindTextureForSetup(i);
			TextureUtil.initTexture(realmsTextureImage.buffer, realmsTextureImage.width, realmsTextureImage.height);
			TEXTURES.put(id, new RealmsTextureManager.RealmsTexture(image, i));
			return i;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsTexture {
		final String image;
		final int textureId;

		public RealmsTexture(String image, int textureId) {
			this.image = image;
			this.textureId = textureId;
		}
	}

	@Environment(EnvType.CLIENT)
	static class RealmsTextureImage {
		final int width;
		final int height;
		final IntBuffer buffer;
		private static final Supplier<RealmsTextureManager.RealmsTextureImage> FALLBACK = Suppliers.memoize(() -> {
			int i = 16;
			int j = 16;
			IntBuffer intBuffer = BufferUtils.createIntBuffer(256);
			int k = -16777216;
			int l = -524040;

			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					if (m < 8 ^ n < 8) {
						intBuffer.put(n + m * 16, -524040);
					} else {
						intBuffer.put(n + m * 16, -16777216);
					}
				}
			}

			return new RealmsTextureManager.RealmsTextureImage(16, 16, intBuffer);
		});

		private RealmsTextureImage(int width, int height, IntBuffer buffer) {
			this.width = width;
			this.height = height;
			this.buffer = buffer;
		}

		public static RealmsTextureManager.RealmsTextureImage fromBase64(String string) {
			try {
				InputStream inputStream = new ByteArrayInputStream(new Base64().decode(string));
				BufferedImage bufferedImage = ImageIO.read(inputStream);
				if (bufferedImage != null) {
					int i = bufferedImage.getWidth();
					int j = bufferedImage.getHeight();
					int[] is = new int[i * j];
					bufferedImage.getRGB(0, 0, i, j, is, 0, i);
					IntBuffer intBuffer = BufferUtils.createIntBuffer(i * j);
					intBuffer.put(is);
					intBuffer.flip();
					return new RealmsTextureManager.RealmsTextureImage(i, j, intBuffer);
				}

				RealmsTextureManager.LOGGER.warn("Unknown image format: {}", string);
			} catch (IOException var7) {
				RealmsTextureManager.LOGGER.warn("Failed to load world image: {}", string, var7);
			}

			return (RealmsTextureManager.RealmsTextureImage)FALLBACK.get();
		}
	}
}
