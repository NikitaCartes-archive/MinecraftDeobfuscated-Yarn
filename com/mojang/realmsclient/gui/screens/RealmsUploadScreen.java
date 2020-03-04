/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.SizeUnit;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ReentrantLock uploadLock = new ReentrantLock();
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private final RealmsResetWorldScreen lastScreen;
    private final LevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    private volatile String field_20503;
    private volatile String status;
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private ButtonWidget backButton;
    private ButtonWidget cancelButton;
    private int animTick;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private final Runnable field_22728;

    public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, LevelSummary levelSummary, Runnable runnable) {
        this.worldId = worldId;
        this.slotId = slotId;
        this.lastScreen = lastScreen;
        this.selectedLevel = levelSummary;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create(0.1f);
        this.field_22728 = runnable;
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.backButton = new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, I18n.translate("gui.back", new Object[0]), buttonWidget -> this.onBack());
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.onCancel()));
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.upload();
            } else {
                this.lastScreen.switchSlot(() -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.client.openScreen(this);
                        this.upload();
                    }
                });
            }
        }
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void onBack() {
        this.field_22728.run();
    }

    private void onCancel() {
        this.cancelled = true;
        this.client.openScreen(this.lastScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten.longValue() == this.uploadStatus.totalBytes.longValue()) {
            this.status = I18n.translate("mco.upload.verifying", new Object[0]);
            this.cancelButton.active = false;
        }
        this.drawCenteredString(this.textRenderer, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots();
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar();
            this.drawUploadSpeed();
        }
        if (this.field_20503 != null) {
            String[] strings = this.field_20503.split("\\\\n");
            for (int i = 0; i < strings.length; ++i) {
                this.drawCenteredString(this.textRenderer, strings[i], this.width / 2, 110 + 12 * i, 0xFF0000);
            }
        }
        super.render(mouseX, mouseY, delta);
    }

    private void drawDots() {
        int i = this.textRenderer.getStringWidth(this.status);
        this.textRenderer.draw(DOTS[this.animTick / 10 % DOTS.length], this.width / 2 + i / 2 + 5, 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar() {
        double d = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0;
        if (d > 100.0) {
            d = 100.0;
        }
        this.progress = String.format(Locale.ROOT, "%.1f", d);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        double e = this.width / 2 - 100;
        double f = 0.5;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        bufferBuilder.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).next();
        tessellator.draw();
        RenderSystem.enableTexture();
        this.drawCenteredString(this.textRenderer, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed() {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawUploadSpeed0(this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawUploadSpeed0(this.bytesPersSecond);
        }
    }

    private void drawUploadSpeed0(long bytesPersSecond) {
        if (bytesPersSecond > 0L) {
            int i = this.textRenderer.getStringWidth(this.progress);
            String string = "(" + SizeUnit.getUserFriendlyString(bytesPersSecond) + "/s)";
            this.textRenderer.draw(string, this.width / 2 + i / 2 + 15, 84.0f, 0xFFFFFF);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            ArrayList<String> list = Lists.newArrayList();
            list.add(this.status);
            if (this.progress != null) {
                list.add(this.progress + "%");
            }
            if (this.field_20503 != null) {
                list.add(this.field_20503);
            }
            Realms.narrateNow(String.join((CharSequence)System.lineSeparator(), list));
        }
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(() -> {
            File file = null;
            RealmsClient realmsClient = RealmsClient.createRealmsClient();
            long l = this.worldId;
            try {
                if (!uploadLock.tryLock(1L, TimeUnit.SECONDS)) {
                    return;
                }
                this.status = I18n.translate("mco.upload.preparing", new Object[0]);
                UploadInfo uploadInfo = null;
                for (int i = 0; i < 20; ++i) {
                    block35: {
                        try {
                            if (!this.cancelled) break block35;
                            this.uploadCancelled();
                            return;
                        } catch (RetryCallException retryCallException) {
                            Thread.sleep(retryCallException.delaySeconds * 1000);
                            continue;
                        }
                    }
                    uploadInfo = realmsClient.upload(l, UploadTokenCache.get(l));
                    break;
                }
                if (uploadInfo == null) {
                    this.status = I18n.translate("mco.upload.close.failure", new Object[0]);
                    return;
                }
                UploadTokenCache.put(l, uploadInfo.getToken());
                if (!uploadInfo.isWorldClosed()) {
                    this.status = I18n.translate("mco.upload.close.failure", new Object[0]);
                    return;
                }
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                File file2 = new File(this.client.runDirectory.getAbsolutePath(), "saves");
                file = this.tarGzipArchive(new File(file2, this.selectedLevel.getName()));
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                if (!this.verify(file)) {
                    long m = file.length();
                    SizeUnit sizeUnit = SizeUnit.getLargestUnit(m);
                    SizeUnit sizeUnit2 = SizeUnit.getLargestUnit(0x140000000L);
                    if (SizeUnit.humanReadableSize(m, sizeUnit).equals(SizeUnit.humanReadableSize(0x140000000L, sizeUnit2)) && sizeUnit != SizeUnit.B) {
                        SizeUnit sizeUnit3 = SizeUnit.values()[sizeUnit.ordinal() - 1];
                        this.field_20503 = I18n.translate("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()) + "\\n" + I18n.translate("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit3), SizeUnit.humanReadableSize(0x140000000L, sizeUnit3));
                        return;
                    }
                    this.field_20503 = I18n.translate("mco.upload.size.failure.line1", this.selectedLevel.getDisplayName()) + "\\n" + I18n.translate("mco.upload.size.failure.line2", SizeUnit.humanReadableSize(m, sizeUnit), SizeUnit.humanReadableSize(0x140000000L, sizeUnit2));
                    return;
                }
                this.status = I18n.translate("mco.upload.uploading", this.selectedLevel.getDisplayName());
                FileUpload fileUpload = new FileUpload(file, this.worldId, this.slotId, uploadInfo, this.client.getSession(), SharedConstants.getGameVersion().getName(), this.uploadStatus);
                fileUpload.upload(uploadResult -> {
                    if (uploadResult.statusCode >= 200 && uploadResult.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = I18n.translate("mco.upload.done", new Object[0]);
                        this.backButton.setMessage(I18n.translate("gui.done", new Object[0]));
                        UploadTokenCache.invalidate(l);
                    } else {
                        this.field_20503 = uploadResult.statusCode == 400 && uploadResult.errorMessage != null ? I18n.translate("mco.upload.failed", uploadResult.errorMessage) : I18n.translate("mco.upload.failed", uploadResult.statusCode);
                    }
                });
                while (!fileUpload.isFinished()) {
                    if (this.cancelled) {
                        fileUpload.cancel();
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException interruptedException) {
                        LOGGER.error("Failed to check Realms file upload status");
                    }
                }
            } catch (IOException iOException) {
                this.field_20503 = I18n.translate("mco.upload.failed", iOException.getMessage());
            } catch (RealmsServiceException realmsServiceException) {
                this.field_20503 = I18n.translate("mco.upload.failed", realmsServiceException.toString());
            } catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            } finally {
                this.uploadFinished = true;
                if (!uploadLock.isHeldByCurrentThread()) {
                    return;
                }
                uploadLock.unlock();
                this.showDots = false;
                this.children.clear();
                this.addButton(this.backButton);
                if (file != null) {
                    LOGGER.debug("Deleting file " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }).start();
    }

    private void uploadCancelled() {
        this.status = I18n.translate("mco.upload.cancelled", new Object[0]);
        LOGGER.debug("Upload was cancelled");
    }

    private boolean verify(File archive) {
        return archive.length() < 0x140000000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive(File pathToDirectoryFile) throws IOException {
        try (TarArchiveOutputStream tarArchiveOutputStream = null;){
            File file = File.createTempFile("realms-upload-file", ".tar.gz");
            tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
            tarArchiveOutputStream.setLongFileMode(3);
            this.addFileToTarGz(tarArchiveOutputStream, pathToDirectoryFile.getAbsolutePath(), "world", true);
            tarArchiveOutputStream.finish();
            File file2 = file;
            return file2;
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean root) throws IOException {
        if (this.cancelled) {
            return;
        }
        File file = new File(path);
        String string = root ? base : base + file.getName();
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string);
        tOut.putArchiveEntry(tarArchiveEntry);
        if (file.isFile()) {
            IOUtils.copy(new FileInputStream(file), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    this.addFileToTarGz(tOut, file2.getAbsolutePath(), string + "/", false);
                }
            }
        }
    }
}

