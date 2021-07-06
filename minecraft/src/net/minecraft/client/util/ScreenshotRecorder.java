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

/**
 * A screenshot recorder takes screenshots and saves them into tga file format. It also
 * holds a few utility methods for other types of screenshots.
 */
@Environment(EnvType.CLIENT)
public class ScreenshotRecorder {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private int unitHeight;
	private final DataOutputStream stream;
	private final byte[] buffer;
	private final int width;
	private final int height;
	private File file;

	public static void saveScreenshot(File gameDirectory, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
		saveScreenshot(gameDirectory, null, framebuffer, messageReceiver);
	}

	public static void saveScreenshot(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> saveScreenshotInner(gameDirectory, fileName, framebuffer, messageReceiver));
		} else {
			saveScreenshotInner(gameDirectory, fileName, framebuffer, messageReceiver);
		}
	}

	private static void saveScreenshotInner(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
		NativeImage nativeImage = takeScreenshot(framebuffer);
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
						nativeImage.writeTo(file2);
						Text text = new LiteralText(file2.getName())
							.formatted(Formatting.UNDERLINE)
							.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));
						messageReceiver.accept(new TranslatableText("screenshot.success", text));
					} catch (Exception var7) {
						LOGGER.warn("Couldn't save screenshot", (Throwable)var7);
						messageReceiver.accept(new TranslatableText("screenshot.failure", var7.getMessage()));
					} finally {
						nativeImage.close();
					}
				}
			);
	}

	public static NativeImage takeScreenshot(Framebuffer framebuffer) {
		int i = framebuffer.textureWidth;
		int j = framebuffer.textureHeight;
		NativeImage nativeImage = new NativeImage(i, j, false);
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

	/**
	 * Creates a screenshot recorder for huge screenshots.
	 * 
	 * @see net.minecraft.client.MinecraftClient#takeHugeScreenshot
	 */
	public ScreenshotRecorder(File gameDirectory, int width, int height, int unitHeight) throws IOException {
		this.width = width;
		this.height = height;
		this.unitHeight = unitHeight;
		File file = new File(gameDirectory, "screenshots");
		file.mkdir();
		String string = "huge_" + DATE_FORMAT.format(new Date());
		int i = 1;

		while ((this.file = new File(file, string + (i == 1 ? "" : "_" + i) + ".tga")).exists()) {
			i++;
		}

		byte[] bs = new byte[18];
		bs[2] = 2;
		bs[12] = (byte)(width % 256);
		bs[13] = (byte)(width / 256);
		bs[14] = (byte)(height % 256);
		bs[15] = (byte)(height / 256);
		bs[16] = 24;
		this.buffer = new byte[width * unitHeight * 3];
		this.stream = new DataOutputStream(new FileOutputStream(this.file));
		this.stream.write(bs);
	}

	/**
	 * Transports image data from {@code data} into {@link #buffer}.
	 */
	public void getIntoBuffer(ByteBuffer data, int startWidth, int startHeight, int unitWidth, int unitHeight) {
		int i = unitWidth;
		int j = unitHeight;
		if (unitWidth > this.width - startWidth) {
			i = this.width - startWidth;
		}

		if (unitHeight > this.height - startHeight) {
			j = this.height - startHeight;
		}

		this.unitHeight = j;

		for (int k = 0; k < j; k++) {
			data.position((unitHeight - j) * unitWidth * 3 + k * unitWidth * 3);
			int l = (startWidth + k * this.width) * 3;
			data.get(this.buffer, l, i * 3);
		}
	}

	/**
	 * Writes the contents in the {@link #buffer} into the {@link #stream}.
	 */
	public void writeToStream() throws IOException {
		this.stream.write(this.buffer, 0, this.width * 3 * this.unitHeight);
	}

	/**
	 * Finish taking the screenshot and return the complete tga file.
	 * 
	 * @return the tga file
	 */
	public File finish() throws IOException {
		this.stream.close();
		return this.file;
	}
}
