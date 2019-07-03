package net.minecraft;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4333 {
	private static final Logger field_19522 = LogManager.getLogger();
	private volatile boolean field_19523;
	private volatile boolean field_19524;
	private volatile boolean field_19525;
	private volatile boolean field_19526;
	private volatile File field_19527;
	private volatile File field_19528;
	private volatile HttpGet field_19529;
	private Thread field_19530;
	private final RequestConfig field_19531 = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
	private static final String[] field_19532 = new String[]{
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

	public long method_20955(String string) {
		CloseableHttpClient closeableHttpClient = null;
		HttpGet httpGet = null;

		long var5;
		try {
			httpGet = new HttpGet(string);
			closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_19531).build();
			CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
			return Long.parseLong(closeableHttpResponse.getFirstHeader("Content-Length").getValue());
		} catch (Throwable var16) {
			field_19522.error("Unable to get content length for download");
			var5 = 0L;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}

			if (closeableHttpClient != null) {
				try {
					closeableHttpClient.close();
				} catch (IOException var15) {
					field_19522.error("Could not close http client", (Throwable)var15);
				}
			}
		}

		return var5;
	}

	public void method_20949(WorldDownload worldDownload, String string, class_4392.class_4393 arg, RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) {
		if (this.field_19530 == null) {
			this.field_19530 = new Thread() {
				public void run() {
					CloseableHttpClient closeableHttpClient = null;

					try {
						class_4333.this.field_19527 = File.createTempFile("backup", ".tar.gz");
						class_4333.this.field_19529 = new HttpGet(worldDownload.downloadLink);
						closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(class_4333.this.field_19531).build();
						HttpResponse httpResponse = closeableHttpClient.execute(class_4333.this.field_19529);
						arg.field_19872 = Long.parseLong(httpResponse.getFirstHeader("Content-Length").getValue());
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							OutputStream outputStream2 = new FileOutputStream(class_4333.this.field_19527);
							class_4333.class_4335 lv3 = class_4333.this.new class_4335(string.trim(), class_4333.this.field_19527, realmsAnvilLevelStorageSource, arg, worldDownload);
							class_4333.class_4334 lv4 = class_4333.this.new class_4334(outputStream2);
							lv4.method_20969(lv3);
							IOUtils.copy(httpResponse.getEntity().getContent(), lv4);
							return;
						}

						class_4333.this.field_19525 = true;
						class_4333.this.field_19529.abort();
					} catch (Exception var89) {
						class_4333.field_19522.error("Caught exception while downloading: " + var89.getMessage());
						class_4333.this.field_19525 = true;
						return;
					} finally {
						class_4333.this.field_19529.releaseConnection();
						if (class_4333.this.field_19527 != null) {
							class_4333.this.field_19527.delete();
						}

						if (!class_4333.this.field_19525) {
							if (!worldDownload.resourcePackUrl.isEmpty() && !worldDownload.resourcePackHash.isEmpty()) {
								try {
									class_4333.this.field_19527 = File.createTempFile("resources", ".tar.gz");
									class_4333.this.field_19529 = new HttpGet(worldDownload.resourcePackUrl);
									HttpResponse httpResponse3 = closeableHttpClient.execute(class_4333.this.field_19529);
									arg.field_19872 = Long.parseLong(httpResponse3.getFirstHeader("Content-Length").getValue());
									if (httpResponse3.getStatusLine().getStatusCode() != 200) {
										class_4333.this.field_19525 = true;
										class_4333.this.field_19529.abort();
										return;
									}

									OutputStream outputStream3 = new FileOutputStream(class_4333.this.field_19527);
									class_4333.class_4336 lv6 = class_4333.this.new class_4336(class_4333.this.field_19527, arg, worldDownload);
									class_4333.class_4334 lv7 = class_4333.this.new class_4334(outputStream3);
									lv7.method_20969(lv6);
									IOUtils.copy(httpResponse3.getEntity().getContent(), lv7);
								} catch (Exception var87) {
									class_4333.field_19522.error("Caught exception while downloading: " + var87.getMessage());
									class_4333.this.field_19525 = true;
								} finally {
									class_4333.this.field_19529.releaseConnection();
									if (class_4333.this.field_19527 != null) {
										class_4333.this.field_19527.delete();
									}
								}
							} else {
								class_4333.this.field_19524 = true;
							}
						}

						if (closeableHttpClient != null) {
							try {
								closeableHttpClient.close();
							} catch (IOException var86) {
								class_4333.field_19522.error("Failed to close Realms download client");
							}
						}
					}
				}
			};
			this.field_19530.setUncaughtExceptionHandler(new class_4353(field_19522));
			this.field_19530.start();
		}
	}

	public void method_20948() {
		if (this.field_19529 != null) {
			this.field_19529.abort();
		}

		if (this.field_19527 != null) {
			this.field_19527.delete();
		}

		this.field_19523 = true;
	}

	public boolean method_20957() {
		return this.field_19524;
	}

	public boolean method_20961() {
		return this.field_19525;
	}

	public boolean method_20964() {
		return this.field_19526;
	}

	public static String method_20960(String string) {
		string = string.replaceAll("[\\./\"]", "_");

		for (String string2 : field_19532) {
			if (string.equalsIgnoreCase(string2)) {
				string = "_" + string + "_";
			}
		}

		return string;
	}

	private void method_20956(String string, File file, RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) throws IOException {
		Pattern pattern = Pattern.compile(".*-([0-9]+)$");
		int i = 1;

		for (char c : RealmsSharedConstants.ILLEGAL_FILE_CHARACTERS) {
			string = string.replace(c, '_');
		}

		if (StringUtils.isEmpty(string)) {
			string = "Realm";
		}

		string = method_20960(string);

		try {
			for (RealmsLevelSummary realmsLevelSummary : realmsAnvilLevelStorageSource.getLevelList()) {
				if (realmsLevelSummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(string.toLowerCase(Locale.ROOT))) {
					Matcher matcher = pattern.matcher(realmsLevelSummary.getLevelId());
					if (matcher.matches()) {
						if (Integer.valueOf(matcher.group(1)) > i) {
							i = Integer.valueOf(matcher.group(1));
						}
					} else {
						i++;
					}
				}
			}
		} catch (Exception var22) {
			field_19522.error("Error getting level list", (Throwable)var22);
			this.field_19525 = true;
			return;
		}

		String string2;
		if (realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string) && i <= 1) {
			string2 = string;
		} else {
			string2 = string + (i == 1 ? "" : "-" + i);
			if (!realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string2)) {
				boolean bl = false;

				while (!bl) {
					i++;
					string2 = string + (i == 1 ? "" : "-" + i);
					if (realmsAnvilLevelStorageSource.isNewLevelIdAcceptable(string2)) {
						bl = true;
					}
				}
			}
		}

		TarArchiveInputStream tarArchiveInputStream = null;
		File file2 = new File(Realms.getGameDirectoryPath(), "saves");

		try {
			file2.mkdir();
			tarArchiveInputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(file))));

			for (TarArchiveEntry tarArchiveEntry = tarArchiveInputStream.getNextTarEntry();
				tarArchiveEntry != null;
				tarArchiveEntry = tarArchiveInputStream.getNextTarEntry()
			) {
				File file3 = new File(file2, tarArchiveEntry.getName().replace("world", string2));
				if (tarArchiveEntry.isDirectory()) {
					file3.mkdirs();
				} else {
					file3.createNewFile();
					byte[] bs = new byte[1024];
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file3));
					int j = 0;

					while ((j = tarArchiveInputStream.read(bs)) != -1) {
						bufferedOutputStream.write(bs, 0, j);
					}

					bufferedOutputStream.close();
					bs = null;
				}
			}
		} catch (Exception var20) {
			field_19522.error("Error extracting world", (Throwable)var20);
			this.field_19525 = true;
		} finally {
			if (tarArchiveInputStream != null) {
				tarArchiveInputStream.close();
			}

			if (file != null) {
				file.delete();
			}

			realmsAnvilLevelStorageSource.renameLevel(string2, string2.trim());
			File file4 = new File(file2, string2 + File.separator + "level.dat");
			Realms.deletePlayerTag(file4);
			this.field_19528 = new File(file2, string2 + File.separator + "resources.zip");
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4334 extends CountingOutputStream {
		private ActionListener field_19539;

		public class_4334(OutputStream outputStream) {
			super(outputStream);
		}

		public void method_20969(ActionListener actionListener) {
			this.field_19539 = actionListener;
		}

		@Override
		protected void afterWrite(int i) throws IOException {
			super.afterWrite(i);
			if (this.field_19539 != null) {
				this.field_19539.actionPerformed(new ActionEvent(this, 0, null));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4335 implements ActionListener {
		private final String field_19541;
		private final File field_19542;
		private final RealmsAnvilLevelStorageSource field_19543;
		private final class_4392.class_4393 field_19544;
		private final WorldDownload field_19545;

		private class_4335(
			String string, File file, RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource, class_4392.class_4393 arg2, WorldDownload worldDownload
		) {
			this.field_19541 = string;
			this.field_19542 = file;
			this.field_19543 = realmsAnvilLevelStorageSource;
			this.field_19544 = arg2;
			this.field_19545 = worldDownload;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			this.field_19544.field_19871 = ((class_4333.class_4334)actionEvent.getSource()).getByteCount();
			if (this.field_19544.field_19871 >= this.field_19544.field_19872 && !class_4333.this.field_19523 && !class_4333.this.field_19525) {
				try {
					class_4333.this.field_19526 = true;
					class_4333.this.method_20956(this.field_19541, this.field_19542, this.field_19543);
				} catch (IOException var3) {
					class_4333.field_19522.error("Error extracting archive", (Throwable)var3);
					class_4333.this.field_19525 = true;
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	class class_4336 implements ActionListener {
		private final File field_19547;
		private final class_4392.class_4393 field_19548;
		private final WorldDownload field_19549;

		private class_4336(File file, class_4392.class_4393 arg2, WorldDownload worldDownload) {
			this.field_19547 = file;
			this.field_19548 = arg2;
			this.field_19549 = worldDownload;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			this.field_19548.field_19871 = ((class_4333.class_4334)actionEvent.getSource()).getByteCount();
			if (this.field_19548.field_19871 >= this.field_19548.field_19872 && !class_4333.this.field_19523) {
				try {
					String string = Hashing.sha1().hashBytes(Files.toByteArray(this.field_19547)).toString();
					if (string.equals(this.field_19549.resourcePackHash)) {
						FileUtils.copyFile(this.field_19547, class_4333.this.field_19528);
						class_4333.this.field_19524 = true;
					} else {
						class_4333.field_19522.error("Resourcepack had wrong hash (expected " + this.field_19549.resourcePackHash + ", found " + string + "). Deleting it.");
						FileUtils.deleteQuietly(this.field_19547);
						class_4333.this.field_19525 = true;
					}
				} catch (IOException var3) {
					class_4333.field_19522.error("Error copying resourcepack file", var3.getMessage());
					class_4333.this.field_19525 = true;
				}
			}
		}
	}
}
