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
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsResetWorldScreen lastScreen;
    private final RealmsLevelSummary selectedLevel;
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
    private RealmsButton backButton;
    private RealmsButton cancelButton;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private static final ReentrantLock uploadLock = new ReentrantLock();

    public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, RealmsLevelSummary selectedLevel) {
        this.worldId = worldId;
        this.slotId = slotId;
        this.lastScreen = lastScreen;
        this.selectedLevel = selectedLevel;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create(0.1f);
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.backButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsUploadScreen.getLocalizedString("gui.back")){

            @Override
            public void onPress() {
                RealmsUploadScreen.this.onBack();
            }
        };
        this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsUploadScreen.getLocalizedString("gui.cancel")){

            @Override
            public void onPress() {
                RealmsUploadScreen.this.onCancel();
            }
        };
        this.buttonsAdd(this.cancelButton);
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.upload();
            } else {
                this.lastScreen.switchSlot(this);
            }
        }
    }

    @Override
    public void confirmResult(boolean result, int buttonId) {
        if (result && !this.uploadStarted) {
            this.uploadStarted = true;
            Realms.setScreen(this);
            this.upload();
        }
    }

    @Override
    public void removed() {
        this.setKeyboardHandlerSendRepeatsToGui(false);
    }

    private void onBack() {
        this.lastScreen.confirmResult(true, 0);
    }

    private void onCancel() {
        this.cancelled = true;
        Realms.setScreen(this.lastScreen);
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        if (eventKey == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.renderBackground();
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten.longValue() == this.uploadStatus.totalBytes.longValue()) {
            this.status = RealmsUploadScreen.getLocalizedString("mco.upload.verifying");
            this.cancelButton.active(false);
        }
        this.drawCenteredString(this.status, this.width() / 2, 50, 0xFFFFFF);
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
                this.drawCenteredString(strings[i], this.width() / 2, 110 + 12 * i, 0xFF0000);
            }
        }
        super.render(xm, ym, a);
    }

    private void drawDots() {
        int i = this.fontWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + i / 2 + 5, 50, 0xFFFFFF);
    }

    private void drawProgressBar() {
        double d = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0;
        if (d > 100.0) {
            d = 100.0;
        }
        this.progress = String.format(Locale.ROOT, "%.1f", d);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        double e = this.width() / 2 - 100;
        double f = 0.5;
        Tezzelator tezzelator = Tezzelator.instance;
        tezzelator.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        tezzelator.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        tezzelator.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        tezzelator.end();
        RenderSystem.enableTexture();
        this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed() {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = System.currentTimeMillis() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawUploadSpeed0(this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = System.currentTimeMillis();
        } else {
            this.drawUploadSpeed0(this.bytesPersSecond);
        }
    }

    private void drawUploadSpeed0(long bytesPersSecond) {
        if (bytesPersSecond > 0L) {
            int i = this.fontWidth(this.progress);
            String string = "(" + RealmsUploadScreen.humanReadableByteCount(bytesPersSecond) + ")";
            this.drawString(string, this.width() / 2 + i / 2 + 15, 84, 0xFFFFFF);
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int i = 1024;
        if (bytes < 1024L) {
            return bytes + " B";
        }
        int j = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "KMGTPE".charAt(j - 1) + "";
        return String.format(Locale.ROOT, "%.1f %sB/s", (double)bytes / Math.pow(1024.0, j), string);
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

    public static Unit getLargestUnit(long bytes) {
        if (bytes < 1024L) {
            return Unit.B;
        }
        int i = (int)(Math.log(bytes) / Math.log(1024.0));
        String string = "KMGTPE".charAt(i - 1) + "";
        try {
            return Unit.valueOf(string + "B");
        } catch (Exception exception) {
            return Unit.GB;
        }
    }

    public static double convertToUnit(long bytes, Unit unit) {
        if (unit.equals((Object)Unit.B)) {
            return bytes;
        }
        return (double)bytes / Math.pow(1024.0, unit.ordinal());
    }

    public static String humanReadableSize(long bytes, Unit unit) {
        return String.format("%." + (unit.equals((Object)Unit.GB) ? "1" : "0") + "f %s", RealmsUploadScreen.convertToUnit(bytes, unit), unit.name());
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
                this.status = RealmsUploadScreen.getLocalizedString("mco.upload.preparing");
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
                    this.status = RealmsUploadScreen.getLocalizedString("mco.upload.close.failure");
                    return;
                }
                UploadTokenCache.put(l, uploadInfo.getToken());
                if (!uploadInfo.isWorldClosed()) {
                    this.status = RealmsUploadScreen.getLocalizedString("mco.upload.close.failure");
                    return;
                }
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                File file2 = new File(Realms.getGameDirectoryPath(), "saves");
                file = this.tarGzipArchive(new File(file2, this.selectedLevel.getLevelId()));
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                if (!this.verify(file)) {
                    long m = file.length();
                    Unit unit = RealmsUploadScreen.getLargestUnit(m);
                    Unit unit2 = RealmsUploadScreen.getLargestUnit(0x140000000L);
                    if (RealmsUploadScreen.humanReadableSize(m, unit).equals(RealmsUploadScreen.humanReadableSize(0x140000000L, unit2)) && unit != Unit.B) {
                        Unit unit3 = Unit.values()[unit.ordinal() - 1];
                        this.field_20503 = RealmsUploadScreen.getLocalizedString("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()) + "\\n" + RealmsUploadScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(m, unit3), RealmsUploadScreen.humanReadableSize(0x140000000L, unit3));
                        return;
                    }
                    this.field_20503 = RealmsUploadScreen.getLocalizedString("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()) + "\\n" + RealmsUploadScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(m, unit), RealmsUploadScreen.humanReadableSize(0x140000000L, unit2));
                    return;
                }
                this.status = RealmsUploadScreen.getLocalizedString("mco.upload.uploading", this.selectedLevel.getLevelName());
                FileUpload fileUpload = new FileUpload(file, this.worldId, this.slotId, uploadInfo, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), this.uploadStatus);
                fileUpload.upload(uploadResult -> {
                    if (uploadResult.statusCode >= 200 && uploadResult.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = RealmsUploadScreen.getLocalizedString("mco.upload.done");
                        this.backButton.setMessage(RealmsUploadScreen.getLocalizedString("gui.done"));
                        UploadTokenCache.invalidate(l);
                    } else {
                        this.field_20503 = uploadResult.statusCode == 400 && uploadResult.errorMessage != null ? RealmsUploadScreen.getLocalizedString("mco.upload.failed", uploadResult.errorMessage) : RealmsUploadScreen.getLocalizedString("mco.upload.failed", uploadResult.statusCode);
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
                this.field_20503 = RealmsUploadScreen.getLocalizedString("mco.upload.failed", iOException.getMessage());
            } catch (RealmsServiceException realmsServiceException) {
                this.field_20503 = RealmsUploadScreen.getLocalizedString("mco.upload.failed", realmsServiceException.toString());
            } catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            } finally {
                this.uploadFinished = true;
                if (!uploadLock.isHeldByCurrentThread()) {
                    return;
                }
                uploadLock.unlock();
                this.showDots = false;
                this.childrenClear();
                this.buttonsAdd(this.backButton);
                if (file != null) {
                    LOGGER.debug("Deleting file " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }).start();
    }

    private void uploadCancelled() {
        this.status = RealmsUploadScreen.getLocalizedString("mco.upload.cancelled");
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

    @Environment(value=EnvType.CLIENT)
    static enum Unit {
        B,
        KB,
        MB,
        GB;

    }
}

