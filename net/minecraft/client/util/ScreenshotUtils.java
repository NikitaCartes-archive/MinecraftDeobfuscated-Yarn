/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ScreenshotUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void saveScreenshot(File gameDirectory, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        ScreenshotUtils.saveScreenshot(gameDirectory, null, framebufferWidth, framebufferHeight, framebuffer, messageReceiver);
    }

    public static void saveScreenshot(File gameDirectory, @Nullable String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ScreenshotUtils.saveScreenshotInner(gameDirectory, fileName, framebufferWidth, framebufferHeight, framebuffer, messageReceiver));
        } else {
            ScreenshotUtils.saveScreenshotInner(gameDirectory, fileName, framebufferWidth, framebufferHeight, framebuffer, messageReceiver);
        }
    }

    private static void saveScreenshotInner(File gameDirectory, @Nullable String fileName, int framebufferWidth, int framebufferHeight, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        NativeImage nativeImage = ScreenshotUtils.takeScreenshot(framebufferWidth, framebufferHeight, framebuffer);
        File file = new File(gameDirectory, "screenshots");
        file.mkdir();
        File file2 = fileName == null ? ScreenshotUtils.getScreenshotFilename(file) : new File(file, fileName);
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                nativeImage.writeFile(file2);
                MutableText text = new LiteralText(file2.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));
                messageReceiver.accept(new TranslatableText("screenshot.success", text));
            } catch (Exception exception) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
                messageReceiver.accept(new TranslatableText("screenshot.failure", exception.getMessage()));
            } finally {
                nativeImage.close();
            }
        });
    }

    public static NativeImage takeScreenshot(int width, int height, Framebuffer framebuffer) {
        width = framebuffer.textureWidth;
        height = framebuffer.textureHeight;
        NativeImage nativeImage = new NativeImage(width, height, false);
        RenderSystem.bindTexture(framebuffer.method_30277());
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();
        return nativeImage;
    }

    private static File getScreenshotFilename(File directory) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;
        File file;
        while ((file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file;
    }
}

