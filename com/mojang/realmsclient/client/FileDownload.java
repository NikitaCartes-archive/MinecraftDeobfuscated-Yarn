/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FileDownload {
    private static final Logger LOGGER = LogManager.getLogger();
    private volatile boolean cancelled;
    private volatile boolean finished;
    private volatile boolean error;
    private volatile boolean extracting;
    private volatile File backupFile;
    private volatile File resourcePackPath;
    private volatile HttpGet httpRequest;
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String downloadLink) {
        Closeable closeableHttpClient = null;
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(downloadLink);
            closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse closeableHttpResponse = ((CloseableHttpClient)closeableHttpClient).execute(httpGet);
            long l = Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
            return l;
        } catch (Throwable throwable) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException iOException) {
                    LOGGER.error("Could not close http client", (Throwable)iOException);
                }
            }
        }
    }

    public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            Closeable closeableHttpClient = null;
            try {
                this.backupFile = File.createTempFile("backup", ".tar.gz");
                this.httpRequest = new HttpGet(worldDownload.downloadLink);
                closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse httpResponse = ((CloseableHttpClient)closeableHttpClient).execute(this.httpRequest);
                downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.httpRequest.abort();
                    return;
                }
                FileOutputStream outputStream2 = new FileOutputStream(this.backupFile);
                ProgressListener progressListener = new ProgressListener(message.trim(), this.backupFile, storage, status);
                DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                downloadCountingOutputStream2.setListener(progressListener);
                IOUtils.copy(httpResponse.getEntity().getContent(), (OutputStream)downloadCountingOutputStream2);
                return;
            } catch (Exception exception2) {
                LOGGER.error("Caught exception while downloading: " + exception2.getMessage());
                this.error = true;
                return;
            } finally {
                block40: {
                    block41: {
                        CloseableHttpResponse httpResponse;
                        this.httpRequest.releaseConnection();
                        if (this.backupFile != null) {
                            this.backupFile.delete();
                        }
                        if (this.error) break block40;
                        if (worldDownload.resourcePackUrl.isEmpty() || worldDownload.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.backupFile = File.createTempFile("resources", ".tar.gz");
                            this.httpRequest = new HttpGet(worldDownload.resourcePackUrl);
                            httpResponse = ((CloseableHttpClient)closeableHttpClient).execute(this.httpRequest);
                            downloadStatus.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
                            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.httpRequest.abort();
                                return;
                            }
                        } catch (Exception exception2) {
                            LOGGER.error("Caught exception while downloading: " + exception2.getMessage());
                            this.error = true;
                        }
                        FileOutputStream outputStream2 = new FileOutputStream(this.backupFile);
                        ResourcePackProgressListener resourcePackProgressListener2 = new ResourcePackProgressListener(this.backupFile, status, download);
                        DownloadCountingOutputStream downloadCountingOutputStream2 = new DownloadCountingOutputStream(outputStream2);
                        downloadCountingOutputStream2.setListener(resourcePackProgressListener2);
                        IOUtils.copy(httpResponse.getEntity().getContent(), (OutputStream)downloadCountingOutputStream2);
                        break block40;
                        finally {
                            this.httpRequest.releaseConnection();
                            if (this.backupFile != null) {
                                this.backupFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if (closeableHttpClient != null) {
                    try {
                        closeableHttpClient.close();
                    } catch (IOException iOException2) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.httpRequest != null) {
            this.httpRequest.abort();
        }
        if (this.backupFile != null) {
            this.backupFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String folder) {
        folder = folder.replaceAll("[\\./\"]", "_");
        for (String string : INVALID_FILE_NAMES) {
            if (!folder.equalsIgnoreCase(string)) continue;
            folder = "_" + folder + "_";
        }
        return folder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void untarGzipArchive(String name, File archive, LevelStorage storage) throws IOException {
        String string;
        block66: {
            boolean bl;
            int i;
            block67: {
                block65: {
                    char c;
                    Pattern pattern = Pattern.compile(".*-([0-9]+)$");
                    i = 1;
                    Object object = SharedConstants.INVALID_CHARS_LEVEL_NAME;
                    int n = ((char[])object).length;
                    for (int n2 = 0; n2 < n; name = name.replace(c, '_'), ++n2) {
                        c = object[n2];
                    }
                    if (StringUtils.isEmpty(name)) {
                        name = "Realm";
                    }
                    name = FileDownload.findAvailableFolderName(name);
                    try {
                        object = storage.getLevelList().iterator();
                        while (object.hasNext()) {
                            LevelSummary levelSummary = (LevelSummary)object.next();
                            if (!levelSummary.getName().toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) continue;
                            Matcher matcher = pattern.matcher(levelSummary.getName());
                            if (matcher.matches()) {
                                if (Integer.valueOf(matcher.group(1)) <= i) continue;
                                i = Integer.valueOf(matcher.group(1));
                                continue;
                            }
                            ++i;
                        }
                    } catch (Exception exception) {
                        LOGGER.error("Error getting level list", (Throwable)exception);
                        this.error = true;
                        return;
                    }
                    if (storage.isLevelNameValid(name) && i <= true) break block65;
                    string = name + (i == 1 ? "" : "-" + i);
                    if (storage.isLevelNameValid(string)) break block66;
                    bl = false;
                    break block67;
                }
                string = name;
                break block66;
            }
            while (!bl) {
                string = name + (++i == 1 ? "" : "-" + i);
                if (!storage.isLevelNameValid(string)) continue;
                bl = true;
            }
        }
        TarArchiveInputStream tarArchiveInputStream = null;
        File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");
        try {
            file.mkdir();
            tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(archive))));
            TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            while (tarArchiveEntry != null) {
                File file2 = new File(file, tarArchiveEntry.getName().replace("world", string));
                if (tarArchiveEntry.isDirectory()) {
                    file2.mkdirs();
                } else {
                    file2.createNewFile();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file2);){
                        IOUtils.copy((InputStream)tarArchiveInputStream, (OutputStream)fileOutputStream);
                    }
                }
                tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
            }
            return;
        } catch (Exception exception2) {
            LOGGER.error("Error extracting world", (Throwable)exception2);
            this.error = true;
            return;
        } finally {
            if (tarArchiveInputStream != null) {
                tarArchiveInputStream.close();
            }
            if (archive != null) {
                archive.delete();
            }
            try (LevelStorage.Session session = storage.createSession(string);){
                session.save(string.trim());
                Path path = session.getDirectory(WorldSavePath.LEVEL_DAT);
                FileDownload.readNbtFile(path.toFile());
            } catch (IOException iOException) {
                LOGGER.error("Failed to rename unpacked realms level {}", (Object)string, (Object)iOException);
            }
            this.resourcePackPath = new File(file, string + File.separator + "resources.zip");
        }
    }

    private static void readNbtFile(File file) {
        if (file.exists()) {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.remove("Player");
                NbtIo.writeCompressed(compoundTag, new FileOutputStream(file));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class DownloadCountingOutputStream
    extends CountingOutputStream {
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream out) {
            super(out);
        }

        public void setListener(ActionListener listener) {
            this.listener = listener;
        }

        @Override
        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent(this, 0, null));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        private ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
            this.tempFile = tempFile;
            this.downloadStatus = downloadStatus;
            this.worldDownload = worldDownload;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)e.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String string = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
                    if (string.equals(this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + string + "). Deleting it.");
                        FileUtils.deleteQuietly(this.tempFile);
                        FileDownload.this.error = true;
                    }
                } catch (IOException iOException) {
                    LOGGER.error("Error copying resourcepack file", (Object)iOException.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorage levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        private ProgressListener(String worldName, File tempFile, LevelStorage storage, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
            this.worldName = worldName;
            this.tempFile = tempFile;
            this.levelStorageSource = storage;
            this.downloadStatus = downloadStatus;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)e.getSource()).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                } catch (IOException iOException) {
                    LOGGER.error("Error extracting archive", (Throwable)iOException);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}

