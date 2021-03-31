package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ScreenshotUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private int field_32157;
	private final DataOutputStream field_32158;
	private final byte[] field_32159;
	private final int field_32160;
	private final int field_32161;
	private File field_32162;

	public static void saveScreenshot(File gameDirectory, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
		saveScreenshot(gameDirectory, null, framebufferWidth, framebufferHeight, framebuffer, messageReceiver);
	}

	public static void saveScreenshot(
		File gameDirectory, @Nullable String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver
	) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> saveScreenshotInner(gameDirectory, fileName, framebufferWidth, framebufferHeight, framebuffer, messageReceiver));
		} else {
			saveScreenshotInner(gameDirectory, fileName, framebufferWidth, framebufferHeight, framebuffer, messageReceiver);
		}
	}

	private static void saveScreenshotInner(
		File gameDirectory, @Nullable String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver
	) {
		NativeImage nativeImage = takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
		File file = new File(gameDirectory, "screenshots");
		file.mkdir();
		File file2;
		if (fileName == null) {
			file2 = getScreenshotFilename(file);
		} else {
			file2 = new File(file, fileName);
		}

		Util.getIoWorkerExecutor()
			.execute(
				() -> {
					try {
						nativeImage.writeFile(file2);
						Text text = new LiteralText(file2.getName())
							.formatted(Formatting.UNDERLINE)
							.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));
						messageReceiver.accept(new TranslatableText("screenshot.success", text));
					} catch (Exception var7x) {
						LOGGER.warn("Couldn't save screenshot", (Throwable)var7x);
						messageReceiver.accept(new TranslatableText("screenshot.failure", var7x.getMessage()));
					} finally {
						nativeImage.close();
					}
				}
			);
	}

	public static NativeImage takeScreenshot(int width, int height, Framebuffer framebuffer) {
		width = framebuffer.textureWidth;
		height = framebuffer.textureHeight;
		NativeImage nativeImage = new NativeImage(width, height, false);
		RenderSystem.bindTexture(framebuffer.getColorAttachment());
		nativeImage.loadFromTextureImage(0, true);
		nativeImage.mirrorVertically();
		return nativeImage;
	}

	private static File getScreenshotFilename(File directory) {
		String string = DATE_FORMAT.format(new Date());
		int i = 1;

		while (true) {
			File file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png");
			if (!file.exists()) {
				return file;
			}

			i++;
		}
	}

	public ScreenshotUtils(File file, int i, int j, int k) throws IOException {
		this.field_32160 = i;
		this.field_32161 = j;
		this.field_32157 = k;
		File file2 = new File(file, "screenshots");
		file2.mkdir();
		String string = "huge_" + DATE_FORMAT.format(new Date());
		int l = 1;

		while ((this.field_32162 = new File(file2, string + (l == 1 ? "" : "_" + l) + ".tga")).exists()) {
			l++;
		}

		byte[] bs = new byte[18];
		bs[2] = 2;
		bs[12] = (byte)(i % 256);
		bs[13] = (byte)(i / 256);
		bs[14] = (byte)(j % 256);
		bs[15] = (byte)(j / 256);
		bs[16] = 24;
		this.field_32159 = new byte[i * k * 3];
		this.field_32158 = new DataOutputStream(new FileOutputStream(this.field_32162));
		this.field_32158.write(bs);
	}

	public void method_35711(ByteBuffer byteBuffer, int i, int j, int k, int l) {
		int m = k;
		int n = l;
		if (k > this.field_32160 - i) {
			m = this.field_32160 - i;
		}

		if (l > this.field_32161 - j) {
			n = this.field_32161 - j;
		}

		this.field_32157 = n;

		for (int o = 0; o < n; o++) {
			byteBuffer.position((l - n) * k * 3 + o * k * 3);
			int p = (i + o * this.field_32160) * 3;
			byteBuffer.get(this.field_32159, p, m * 3);
		}
	}

	public void method_35710() throws IOException {
		this.field_32158.write(this.field_32159, 0, this.field_32160 * 3 * this.field_32157);
	}

	public File method_35712() throws IOException {
		this.field_32158.close();
		return this.field_32162;
	}
}
