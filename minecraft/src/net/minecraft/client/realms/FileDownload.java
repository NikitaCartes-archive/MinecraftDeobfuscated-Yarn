package net.minecraft.client.realms;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class FileDownload {
	static final Logger LOGGER = LogUtils.getLogger();
	volatile boolean cancelled;
	volatile boolean finished;
	volatile boolean error;
	volatile boolean extracting;
	@Nullable
	private volatile File backupFile;
	volatile File resourcePackPath;
	@Nullable
	private volatile HttpGet httpRequest;
	@Nullable
	private Thread currentThread;
	private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
	private static final String[] INVALID_FILE_NAMES = new String[]{
		"CON",
		"COM",
		"PRN",
		"AUX",
		"CLOCK$",
		"NUL",
		"COM1",
		"COM2",
		"COM3",
		"COM4",
		"COM5",
		"COM6",
		"COM7",
		"COM8",
		"COM9",
		"LPT1",
		"LPT2",
		"LPT3",
		"LPT4",
		"LPT5",
		"LPT6",
		"LPT7",
		"LPT8",
		"LPT9"
	};

	public long contentLength(String downloadLink) {
		CloseableHttpClient closeableHttpClient = null;
		HttpGet httpGet = null;

		long var5;
		try {
			httpGet = new HttpGet(downloadLink);
			closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
			CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
			return Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
		} catch (Throwable var16) {
			LOGGER.error("Unable to get content length for download");
			var5 = 0L;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}

			if (closeableHttpClient != null) {
				try {
					closeableHttpClient.close();
				} catch (IOException var15) {
					LOGGER.error("Could not close http client", (Throwable)var15);
				}
			}
		}

		return var5;
	}

	public void downloadWorld(WorldDownload download, String message, RealmsDownloadLatestWorldScreen.DownloadStatus status, LevelStorage storage) {
		if (this.currentThread == null) {
			this.currentThread = new Thread(
				() -> {
					CloseableHttpClient closeableHttpClient = null;

					try {
						this.backupFile = File.createTempFile("backup", ".tar.gz");
						this.httpRequest = new HttpGet(download.downloadLink);
						closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
						HttpResponse httpResponse = closeableHttpClient.execute(this.httpRequest);
						status.totalBytes = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							OutputStream outputStream2 = new FileOutputStream(this.backupFile);
							FileDownload.ProgressListener progressListener = new FileDownload.ProgressListener(message.trim(), this.backupFile, storage, status);
							FileDownload.DownloadCountingOutputStream downloadCountingOutputStream2 = new FileDownload.DownloadCountingOutputStream(outputStream2);
							downloadCountingOutputStream2.setListener(progressListener);
							IOUtils.copy(httpResponse.getEntity().getContent(), downloadCountingOutputStream2);
							return;
						}

						this.error = true;
						this.httpRequest.abort();
					} catch (Exception var93) {
						LOGGER.error("Caught exception while downloading: {}", var93.getMessage());
						this.error = true;
						return;
					} finally {
						this.httpRequest.releaseConnection();
						if (this.backupFile != null) {
							this.backupFile.delete();
						}

						if (!this.error) {
							if (!download.resourcePackUrl.isEmpty() && !download.resourcePackHash.isEmpty()) {
								try {
									this.backupFile = File.createTempFile("resources", ".tar.gz");
									this.httpRequest = new HttpGet(download.resourcePackUrl);
									HttpResponse httpResponse3 = closeableHttpClient.execute(this.httpRequest);
									status.totalBytes = Long.parseLong(httpResponse3.getFirstHeader("Content-Length").getValue());
									if (httpResponse3.getStatusLine().getStatusCode() != 200) {
										this.error = true;
										this.httpRequest.abort();
										return;
									}

									OutputStream outputStream3 = new FileOutputStream(this.backupFile);
									FileDownload.ResourcePackProgressListener resourcePackProgressListener3 = new FileDownload.ResourcePackProgressListener(
										this.backupFile, status, download
									);
									FileDownload.DownloadCountingOutputStream downloadCountingOutputStream3 = new FileDownload.DownloadCountingOutputStream(outputStream3);
									downloadCountingOutputStream3.setListener(resourcePackProgressListener3);
									IOUtils.copy(httpResponse3.getEntity().getContent(), downloadCountingOutputStream3);
								} catch (Exception var91) {
									LOGGER.error("Caught exception while downloading: {}", var91.getMessage());
									this.error = true;
								} finally {
									this.httpRequest.releaseConnection();
									if (this.backupFile != null) {
										this.backupFile.delete();
									}
								}
							} else {
								this.finished = true;
							}
						}

						if (closeableHttpClient != null) {
							try {
								closeableHttpClient.close();
							} catch (IOException var90) {
								LOGGER.error("Failed to close Realms download client");
							}
						}
					}
				}
			);
			this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
			this.currentThread.start();
		}
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
			if (folder.equalsIgnoreCase(string)) {
				folder = "_" + folder + "_";
			}
		}

		return folder;
	}

	void untarGzipArchive(String name, @Nullable File archive, LevelStorage storage) throws IOException {
		Pattern pattern = Pattern.compile(".*-([0-9]+)$");
		int i = 1;

		for (char c : SharedConstants.INVALID_CHARS_LEVEL_NAME) {
			name = name.replace(c, '_');
		}

		if (StringUtils.isEmpty(name)) {
			name = "Realm";
		}

		name = findAvailableFolderName(name);

		try {
			for (LevelStorage.LevelSave levelSave : storage.getLevelList()) {
				String string = levelSave.getRootPath();
				if (string.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))) {
					Matcher matcher = pattern.matcher(string);
					if (matcher.matches()) {
						int j = Integer.parseInt(matcher.group(1));
						if (j > i) {
							i = j;
						}
					} else {
						i++;
					}
				}
			}
		} catch (Exception var43) {
			LOGGER.error("Error getting level list", (Throwable)var43);
			this.error = true;
			return;
		}

		String string2;
		if (storage.isLevelNameValid(name) && i <= 1) {
			string2 = name;
		} else {
			string2 = name + (i == 1 ? "" : "-" + i);
			if (!storage.isLevelNameValid(string2)) {
				boolean bl = false;

				while (!bl) {
					i++;
					string2 = name + (i == 1 ? "" : "-" + i);
					if (storage.isLevelNameValid(string2)) {
						bl = true;
					}
				}
			}
		}

		TarArchiveInputStream tarArchiveInputStream = null;
		File file = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath(), "saves");

		try {
			file.mkdir();
			tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(archive))));

			for (TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
				tarArchiveEntry != null;
				tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()
			) {
				File file2 = new File(file, tarArchiveEntry.getName().replace("world", string2));
				if (tarArchiveEntry.isDirectory()) {
					file2.mkdirs();
				} else {
					file2.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(file2);

					try {
						IOUtils.copy(tarArchiveInputStream, fileOutputStream);
					} catch (Throwable var37) {
						try {
							fileOutputStream.close();
						} catch (Throwable var36) {
							var37.addSuppressed(var36);
						}

						throw var37;
					}

					fileOutputStream.close();
				}
			}
		} catch (Exception var41) {
			LOGGER.error("Error extracting world", (Throwable)var41);
			this.error = true;
		} finally {
			if (tarArchiveInputStream != null) {
				tarArchiveInputStream.close();
			}

			if (archive != null) {
				archive.delete();
			}

			try (LevelStorage.Session session2 = storage.createSession(string2)) {
				session2.removePlayerAndSave(string2);
			} catch (NbtException | NbtCrashException | IOException var39) {
				LOGGER.error("Failed to modify unpacked realms level {}", string2, var39);
			} catch (SymlinkValidationException var40) {
				LOGGER.warn("{}", var40.getMessage());
			}

			this.resourcePackPath = new File(file, string2 + File.separator + "resources.zip");
		}
	}

	@Environment(EnvType.CLIENT)
	static class DownloadCountingOutputStream extends CountingOutputStream {
		@Nullable
		private ActionListener listener;

		public DownloadCountingOutputStream(OutputStream stream) {
			super(stream);
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

	@Environment(EnvType.CLIENT)
	class ProgressListener implements ActionListener {
		private final String worldName;
		private final File tempFile;
		private final LevelStorage levelStorageSource;
		private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

		ProgressListener(String worldName, File tempFile, LevelStorage levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus) {
			this.worldName = worldName;
			this.tempFile = tempFile;
			this.levelStorageSource = levelStorageSource;
			this.downloadStatus = downloadStatus;
		}

		public void actionPerformed(ActionEvent e) {
			this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)e.getSource()).getByteCount();
			if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
				try {
					FileDownload.this.extracting = true;
					FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
				} catch (IOException var3) {
					FileDownload.LOGGER.error("Error extracting archive", (Throwable)var3);
					FileDownload.this.error = true;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class ResourcePackProgressListener implements ActionListener {
		private final File tempFile;
		private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
		private final WorldDownload worldDownload;

		ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
			this.tempFile = tempFile;
			this.downloadStatus = downloadStatus;
			this.worldDownload = worldDownload;
		}

		public void actionPerformed(ActionEvent e) {
			this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)e.getSource()).getByteCount();
			if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
				try {
					String string = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
					if (string.equals(this.worldDownload.resourcePackHash)) {
						FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
						FileDownload.this.finished = true;
					} else {
						FileDownload.LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", this.worldDownload.resourcePackHash, string);
						FileUtils.deleteQuietly(this.tempFile);
						FileDownload.this.error = true;
					}
				} catch (IOException var3) {
					FileDownload.LOGGER.error("Error copying resourcepack file: {}", var3.getMessage());
					FileDownload.this.error = true;
				}
			}
		}
	}
}
