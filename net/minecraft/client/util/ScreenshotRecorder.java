/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * A screenshot recorder takes screenshots and saves them into tga file format. It also
 * holds a few utility methods for other types of screenshots.
 */
@Environment(value=EnvType.CLIENT)
public class ScreenshotRecorder {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SCREENSHOTS_DIRECTORY = "screenshots";
    private int unitHeight;
    private final DataOutputStream stream;
    private final byte[] buffer;
    private final int width;
    private final int height;
    private File file;

    public static void saveScreenshot(File gameDirectory, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        ScreenshotRecorder.saveScreenshot(gameDirectory, null, framebuffer, messageReceiver);
    }

    public static void saveScreenshot(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ScreenshotRecorder.saveScreenshotInner(gameDirectory, fileName, framebuffer, messageReceiver));
        } else {
            ScreenshotRecorder.saveScreenshotInner(gameDirectory, fileName, framebuffer, messageReceiver);
        }
    }

    private static void saveScreenshotInner(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(framebuffer);
        File file = new File(gameDirectory, SCREENSHOTS_DIRECTORY);
        file.mkdir();
        File file2 = fileName == null ? ScreenshotRecorder.getScreenshotFilename(file) : new File(file, fileName);
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                nativeImage.writeTo(file2);
                MutableText text = Text.literal(file2.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));
                messageReceiver.accept(Text.translatable("screenshot.success", text));
            } catch (Exception exception) {
                LOGGER.warn("Couldn't save screenshot", exception);
                messageReceiver.accept(Text.translatable("screenshot.failure", exception.getMessage()));
            } finally {
                nativeImage.close();
            }
        });
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
        String string = Util.getFormattedCurrentTime();
        int i = 1;
        File file;
        while ((file = new File(directory, string + (String)(i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file;
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
        File file = new File(gameDirectory, SCREENSHOTS_DIRECTORY);
        file.mkdir();
        String string = "huge_" + Util.getFormattedCurrentTime();
        int i = 1;
        while ((this.file = new File(file, string + (String)(i == 1 ? "" : "_" + i) + ".tga")).exists()) {
            ++i;
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
        if (i > this.width - startWidth) {
            i = this.width - startWidth;
        }
        if (j > this.height - startHeight) {
            j = this.height - startHeight;
        }
        this.unitHeight = j;
        for (int k = 0; k < j; ++k) {
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

