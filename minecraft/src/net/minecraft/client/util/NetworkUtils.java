package net.minecraft.client.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * A few client-side networking utilities.
 * 
 * @implNote This is not marked as client-only because it's used by the
 * {@code /publish} command, which is only available to integrated servers
 * yet was retained by proguard.
 */
public class NetworkUtils {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(
		Executors.newCachedThreadPool(
			new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).setNameFormat("Downloader %d").build()
		)
	);

	private NetworkUtils() {
	}

	public static CompletableFuture<?> downloadResourcePack(
		File file, URL url, Map<String, String> headers, int maxFileSize, @Nullable ProgressListener progressListener, Proxy proxy
	) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection httpURLConnection = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if (progressListener != null) {
				progressListener.setTitleAndTask(Text.method_43471("resourcepack.downloading"));
				progressListener.setTask(Text.method_43471("resourcepack.requesting"));
			}

			try {
				byte[] bs = new byte[4096];
				httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
				httpURLConnection.setInstanceFollowRedirects(true);
				float f = 0.0F;
				float g = (float)headers.entrySet().size();

				for (Entry<String, String> entry : headers.entrySet()) {
					httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
					if (progressListener != null) {
						progressListener.progressStagePercentage((int)(++f / g * 100.0F));
					}
				}

				inputStream = httpURLConnection.getInputStream();
				g = (float)httpURLConnection.getContentLength();
				int j = httpURLConnection.getContentLength();
				if (progressListener != null) {
					progressListener.setTask(Text.method_43469("resourcepack.progress", String.format(Locale.ROOT, "%.2f", g / 1000.0F / 1000.0F)));
				}

				if (file.exists()) {
					long l = file.length();
					if (l == (long)j) {
						if (progressListener != null) {
							progressListener.setDone();
						}

						return null;
					}

					LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", file, j, l);
					FileUtils.deleteQuietly(file);
				} else if (file.getParentFile() != null) {
					file.getParentFile().mkdirs();
				}

				outputStream = new DataOutputStream(new FileOutputStream(file));
				if (maxFileSize > 0 && g > (float)maxFileSize) {
					if (progressListener != null) {
						progressListener.setDone();
					}

					throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + maxFileSize + ")");
				} else {
					int k;
					while ((k = inputStream.read(bs)) >= 0) {
						f += (float)k;
						if (progressListener != null) {
							progressListener.progressStagePercentage((int)(f / g * 100.0F));
						}

						if (maxFileSize > 0 && f > (float)maxFileSize) {
							if (progressListener != null) {
								progressListener.setDone();
							}

							throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + maxFileSize + ")");
						}

						if (Thread.interrupted()) {
							LOGGER.error("INTERRUPTED");
							if (progressListener != null) {
								progressListener.setDone();
							}

							return null;
						}

						outputStream.write(bs, 0, k);
					}

					if (progressListener != null) {
						progressListener.setDone();
					}

					return null;
				}
			} catch (Throwable var21) {
				LOGGER.error("Failed to download file", var21);
				if (httpURLConnection != null) {
					InputStream inputStream2 = httpURLConnection.getErrorStream();

					try {
						LOGGER.error("HTTP response error: {}", IOUtils.toString(inputStream2, StandardCharsets.UTF_8));
					} catch (IOException var20) {
						LOGGER.error("Failed to read response from server");
					}
				}

				if (progressListener != null) {
					progressListener.setDone();
				}

				return null;
			} finally {
				IOUtils.closeQuietly(inputStream);
				IOUtils.closeQuietly(outputStream);
			}
		}, EXECUTOR);
	}

	public static int findLocalPort() {
		try {
			ServerSocket serverSocket = new ServerSocket(0);

			int var1;
			try {
				var1 = serverSocket.getLocalPort();
			} catch (Throwable var4) {
				try {
					serverSocket.close();
				} catch (Throwable var3) {
					var4.addSuppressed(var3);
				}

				throw var4;
			}

			serverSocket.close();
			return var1;
		} catch (IOException var5) {
			return 25564;
		}
	}
}
