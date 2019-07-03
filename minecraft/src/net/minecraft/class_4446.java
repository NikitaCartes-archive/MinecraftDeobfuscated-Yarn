package net.minecraft;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4446 {
	private static final Map<String, class_4446.class_4447> field_20253 = new HashMap();
	private static final Map<String, Boolean> field_20254 = new HashMap();
	private static final Map<String, String> field_20255 = new HashMap();
	private static final Logger field_20256 = LogManager.getLogger();

	public static void method_21560(String string, String string2) {
		if (string2 == null) {
			RealmsScreen.bind("textures/gui/presets/isles.png");
		} else {
			int i = method_21564(string, string2);
			GlStateManager.bindTexture(i);
		}
	}

	public static void method_21559(String string, Runnable runnable) {
		GLX.withTextureRestore(() -> {
			method_21558(string);
			runnable.run();
		});
	}

	private static void method_21561(UUID uUID) {
		RealmsScreen.bind((uUID.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
	}

	private static void method_21558(String string) {
		UUID uUID = UUIDTypeAdapter.fromString(string);
		if (field_20253.containsKey(string)) {
			GlStateManager.bindTexture(((class_4446.class_4447)field_20253.get(string)).field_20259);
		} else if (field_20254.containsKey(string)) {
			if (!(Boolean)field_20254.get(string)) {
				method_21561(uUID);
			} else if (field_20255.containsKey(string)) {
				int i = method_21564(string, (String)field_20255.get(string));
				GlStateManager.bindTexture(i);
			} else {
				method_21561(uUID);
			}
		} else {
			field_20254.put(string, false);
			method_21561(uUID);
			Thread thread = new Thread("Realms Texture Downloader") {
				public void run() {
					Map<Type, MinecraftProfileTexture> map = class_4448.method_21569(string);
					if (map.containsKey(Type.SKIN)) {
						MinecraftProfileTexture minecraftProfileTexture = (MinecraftProfileTexture)map.get(Type.SKIN);
						String string = minecraftProfileTexture.getUrl();
						HttpURLConnection httpURLConnection = null;
						class_4446.field_20256.debug("Downloading http texture from {}", string);

						try {
							try {
								httpURLConnection = (HttpURLConnection)new URL(string).openConnection(Realms.getProxy());
								httpURLConnection.setDoInput(true);
								httpURLConnection.setDoOutput(false);
								httpURLConnection.connect();
								if (httpURLConnection.getResponseCode() / 100 != 2) {
									class_4446.field_20254.remove(string);
									return;
								}

								BufferedImage bufferedImage;
								try {
									bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
								} catch (Exception var17) {
									class_4446.field_20254.remove(string);
									return;
								} finally {
									IOUtils.closeQuietly(httpURLConnection.getInputStream());
								}

								bufferedImage = new class_4449().method_21573(bufferedImage);
								ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
								ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
								class_4446.field_20255.put(string, DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray()));
								class_4446.field_20254.put(string, true);
							} catch (Exception var19) {
								class_4446.field_20256.error("Couldn't download http texture", (Throwable)var19);
								class_4446.field_20254.remove(string);
							}
						} finally {
							if (httpURLConnection != null) {
								httpURLConnection.disconnect();
							}
						}
					} else {
						class_4446.field_20254.put(string, true);
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	private static int method_21564(String string, String string2) {
		int i;
		if (field_20253.containsKey(string)) {
			class_4446.class_4447 lv = (class_4446.class_4447)field_20253.get(string);
			if (lv.field_20258.equals(string2)) {
				return lv.field_20259;
			}

			GlStateManager.deleteTexture(lv.field_20259);
			i = lv.field_20259;
		} else {
			i = GlStateManager.genTexture();
		}

		IntBuffer intBuffer = null;
		int j = 0;
		int k = 0;

		try {
			InputStream inputStream = new ByteArrayInputStream(new Base64().decode(string2));

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

		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.bindTexture(i);
		TextureUtil.initTexture(intBuffer, j, k);
		field_20253.put(string, new class_4446.class_4447(string2, i));
		return i;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4447 {
		String field_20258;
		int field_20259;

		public class_4447(String string, int i) {
			this.field_20258 = string;
			this.field_20259 = i;
		}
	}
}
