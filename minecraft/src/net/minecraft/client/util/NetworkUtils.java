package net.minecraft.client.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A few client-side networking utilities.
 * 
 * @implNote This is not marked as client-only because it's used by the
 * {@code /publish} command, which is only available to integrated servers
 * yet was retained by proguard.
 */
public class NetworkUtils {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ListeningExecutorService EXECUTOR = MoreExecutors.listeningDecorator(
		Executors.newCachedThreadPool(
			new ThreadFactoryBuilder().setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).setNameFormat("Downloader %d").build()
		)
	);

	private NetworkUtils() {
	}

	public static String method_34938(Map<String, Object> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<String, Object> entry : map.entrySet()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append('&');
			}

			try {
				stringBuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException var6) {
				var6.printStackTrace();
			}

			if (entry.getValue() != null) {
				stringBuilder.append('=');

				try {
					stringBuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException var5) {
					var5.printStackTrace();
				}
			}
		}

		return stringBuilder.toString();
	}

	public static String method_34937(URL uRL, Map<String, Object> map, boolean bl, @Nullable Proxy proxy) {
		return method_34936(uRL, method_34938(map), bl, proxy);
	}

	private static String method_34936(URL uRL, String string, boolean bl, @Nullable Proxy proxy) {
		try {
			if (proxy == null) {
				proxy = Proxy.NO_PROXY;
			}

			HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection(proxy);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length", string.getBytes().length + "");
			httpURLConnection.setRequestProperty("Content-Language", "en-US");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
			dataOutputStream.writeBytes(string);
			dataOutputStream.flush();
			dataOutputStream.close();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();

			String string2;
			while ((string2 = bufferedReader.readLine()) != null) {
				stringBuilder.append(string2);
				stringBuilder.append('\r');
			}

			bufferedReader.close();
			return stringBuilder.toString();
		} catch (Exception var9) {
			if (!bl) {
				LOGGER.error("Could not post to {}", uRL, var9);
			}

			return "";
		}
	}

	public static CompletableFuture<?> downloadResourcePack(
		File file, String string, Map<String, String> map, int i, @Nullable ProgressListener progressListener, Proxy proxy
	) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection httpURLConnection = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			if (progressListener != null) {
				progressListener.setTitleAndTask(new TranslatableText("resourcepack.downloading"));
				progressListener.setTask(new TranslatableText("resourcepack.requesting"));
			}

			try {
				byte[] bs = new byte[4096];
				URL uRL = new URL(string);
				httpURLConnection = (HttpURLConnection)uRL.openConnection(proxy);
				httpURLConnection.setInstanceFollowRedirects(true);
				float f = 0.0F;
				float g = (float)map.entrySet().size();

				for (Entry<String, String> entry : map.entrySet()) {
					httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
					if (progressListener != null) {
						progressListener.progressStagePercentage((int)(++f / g * 100.0F));
					}
				}

				inputStream = httpURLConnection.getInputStream();
				g = (float)httpURLConnection.getContentLength();
				int j = httpURLConnection.getContentLength();
				if (progressListener != null) {
					progressListener.setTask(new TranslatableText("resourcepack.progress", String.format(Locale.ROOT, "%.2f", g / 1000.0F / 1000.0F)));
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
				if (i > 0 && g > (float)i) {
					if (progressListener != null) {
						progressListener.setDone();
					}

					throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + i + ")");
				} else {
					int k;
					while ((k = inputStream.read(bs)) >= 0) {
						f += (float)k;
						if (progressListener != null) {
							progressListener.progressStagePercentage((int)(f / g * 100.0F));
						}

						if (i > 0 && f > (float)i) {
							if (progressListener != null) {
								progressListener.setDone();
							}

							throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + i + ")");
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
			} catch (Throwable var22) {
				var22.printStackTrace();
				if (httpURLConnection != null) {
					InputStream inputStream2 = httpURLConnection.getErrorStream();

					try {
						LOGGER.error(IOUtils.toString(inputStream2));
					} catch (IOException var21) {
						var21.printStackTrace();
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
