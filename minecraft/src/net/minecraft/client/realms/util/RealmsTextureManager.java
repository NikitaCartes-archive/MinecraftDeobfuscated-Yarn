package net.minecraft.client.realms.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTextureManager {
	private static final Map<String, RealmsTextureManager.RealmsTexture> textures = Maps.<String, RealmsTextureManager.RealmsTexture>newHashMap();
	private static final Map<String, Boolean> skinFetchStatus = Maps.<String, Boolean>newHashMap();
	private static final Map<String, String> fetchedSkins = Maps.<String, String>newHashMap();
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier ISLES = new Identifier("textures/gui/presets/isles.png");

	public static void bindWorldTemplate(String id, @Nullable String image) {
		if (image == null) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(ISLES);
		} else {
			int i = getTextureId(id, image);
			RenderSystem.bindTexture(i);
		}
	}

	public static void withBoundFace(String uuid, Runnable r) {
		RenderSystem.pushTextureAttributes();

		try {
			bindFace(uuid);
			r.run();
		} finally {
			RenderSystem.popAttributes();
		}
	}

	private static void bindDefaultFace(UUID uuid) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultSkinHelper.getTexture(uuid));
	}

	private static void bindFace(String uuid) {
		UUID uUID = UUIDTypeAdapter.fromString(uuid);
		if (textures.containsKey(uuid)) {
			RenderSystem.bindTexture(((RealmsTextureManager.RealmsTexture)textures.get(uuid)).textureId);
		} else if (skinFetchStatus.containsKey(uuid)) {
			if (!(Boolean)skinFetchStatus.get(uuid)) {
				bindDefaultFace(uUID);
			} else if (fetchedSkins.containsKey(uuid)) {
				int i = getTextureId(uuid, (String)fetchedSkins.get(uuid));
				RenderSystem.bindTexture(i);
			} else {
				bindDefaultFace(uUID);
			}
		} else {
			skinFetchStatus.put(uuid, false);
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
									RealmsTextureManager.skinFetchStatus.remove(uuid);
									return;
								}

								BufferedImage bufferedImage;
								try {
									bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								} catch (Exception var17) {
									RealmsTextureManager.skinFetchStatus.remove(uuid);
									return;
								} finally {
									IOUtils.closeQuietly(httpURLConnection.getInputStream());
								}

								bufferedImage = new SkinProcessor().process(bufferedImage);
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
								RealmsTextureManager.fetchedSkins.put(uuid, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
								RealmsTextureManager.skinFetchStatus.put(uuid, true);
							} catch (Exception var19) {
								RealmsTextureManager.LOGGER.error("Couldn't download http texture", (Throwable)var19);
								RealmsTextureManager.skinFetchStatus.remove(uuid);
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect();
							}
						}
					} else {
						RealmsTextureManager.skinFetchStatus.put(uuid, true);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static int getTextureId(String id, String image) {
		int i;
		if (textures.containsKey(id)) {
			RealmsTextureManager.RealmsTexture realmsTexture = (RealmsTextureManager.RealmsTexture)textures.get(id);
			if (realmsTexture.image.equals(image)) {
				return realmsTexture.textureId;
			}

			RenderSystem.deleteTexture(realmsTexture.textureId);
			i = realmsTexture.textureId;
		} else {
			i = GlStateManager.genTextures();
		}

		IntBuffer intBuffer = null;
		int j = 0;
		int k = 0;

		try {
			InputStream inputStream = new ByteArrayInputStream(new Base64().decode(image));

			BufferedImage bufferedImage;
			try {
				bufferedImage = ImageIO.read(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}

			j = bufferedImage.getWidth();
			k = bufferedImage.getHeight();
			int[] is = new int[j * k];
			bufferedImage.getRGB(0, 0, j, k, is, 0, j);
			intBuffer = ByteBuffer.allocateDirect(4 * j * k).order(ByteOrder.nativeOrder()).asIntBuffer();
			intBuffer.put(is);
			intBuffer.flip();
		} catch (IOException var12) {
			var12.printStackTrace();
		}

		RenderSystem.activeTexture(33984);
		RenderSystem.bindTexture(i);
		TextureUtil.uploadImage(intBuffer, j, k);
		textures.put(id, new RealmsTextureManager.RealmsTexture(image, i));
		return i;
	}

	@Environment(EnvType.CLIENT)
	public static class RealmsTexture {
		private final String image;
		private final int textureId;

		public RealmsTexture(String image, int textureId) {
			this.image = image;
			this.textureId = textureId;
		}
	}
}
