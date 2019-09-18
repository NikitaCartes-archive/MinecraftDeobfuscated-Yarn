package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlFramebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ScreenshotUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	public static void saveScreenshot(File file, int i, int j, GlFramebuffer glFramebuffer, Consumer<Text> consumer) {
		method_22690(file, null, i, j, glFramebuffer, consumer);
	}

	public static void method_22690(File file, @Nullable String string, int i, int j, GlFramebuffer glFramebuffer, Consumer<Text> consumer) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> saveScreenshot(file, string, i, j, glFramebuffer, consumer));
		} else {
			saveScreenshot(file, string, i, j, glFramebuffer, consumer);
		}
	}

	private static void saveScreenshot(File file, @Nullable String string, int i, int j, GlFramebuffer glFramebuffer, Consumer<Text> consumer) {
		NativeImage nativeImage = takeScreenshot(i, j, glFramebuffer);
		File file2 = new File(file, "screenshots");
		file2.mkdir();
		File file3;
		if (string == null) {
			file3 = getScreenshotFilename(file2);
		} else {
			file3 = new File(file2, string);
		}

		ResourceImpl.RESOURCE_IO_EXECUTOR
			.execute(
				() -> {
					try {
						nativeImage.writeFile(file3);
						Text text = new LiteralText(file3.getName())
							.formatted(Formatting.UNDERLINE)
							.styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath())));
						consumer.accept(new TranslatableText("screenshot.success", text));
					} catch (Exception var7x) {
						LOGGER.warn("Couldn't save screenshot", (Throwable)var7x);
						consumer.accept(new TranslatableText("screenshot.failure", var7x.getMessage()));
					} finally {
						nativeImage.close();
					}
				}
			);
	}

	public static NativeImage takeScreenshot(int i, int j, GlFramebuffer glFramebuffer) {
		i = glFramebuffer.texWidth;
		j = glFramebuffer.texHeight;
		NativeImage nativeImage = new NativeImage(i, j, false);
		RenderSystem.bindTexture(glFramebuffer.colorAttachment);
		nativeImage.loadFromTextureImage(0, true);
		nativeImage.mirrorVertically();
		return nativeImage;
	}

	private static File getScreenshotFilename(File file) {
		String string = DATE_FORMAT.format(new Date());
		int i = 1;

		while (true) {
			File file2 = new File(file, string + (i == 1 ? "" : "_" + i) + ".png");
			if (!file2.exists()) {
				return file2;
			}

			i++;
		}
	}
}
