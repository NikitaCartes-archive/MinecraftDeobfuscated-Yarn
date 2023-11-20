package net.minecraft.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.OptionalLong;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * A few networking utilities.
 */
public class NetworkUtils {
	private static final Logger LOGGER = LogUtils.getLogger();

	private NetworkUtils() {
	}

	public static Path download(
		Path path,
		URL url,
		Map<String, String> headers,
		HashFunction hashFunction,
		@Nullable HashCode hashCode,
		int maxBytes,
		Proxy proxy,
		NetworkUtils.DownloadListener listener
	) {
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		listener.onStart();
		Path path2;
		if (hashCode != null) {
			path2 = resolve(path, hashCode);

			try {
				if (validateHash(path2, hashFunction, hashCode)) {
					LOGGER.info("Returning cached file since actual hash matches requested");
					listener.onFinish();
					return path2;
				}
			} catch (IOException var34) {
				LOGGER.warn("Failed to check cached file {}", path2, var34);
			}

			try {
				LOGGER.warn("Existing file {} not found or had mismatched hash", path2);
				Files.deleteIfExists(path2);
			} catch (IOException var33) {
				listener.onFinish();
				throw new UncheckedIOException("Failed to remove existing file " + path2, var33);
			}
		} else {
			path2 = null;
		}

		Path hashCode3;
		try {
			httpURLConnection = (HttpURLConnection)url.openConnection(proxy);
			httpURLConnection.setInstanceFollowRedirects(true);
			headers.forEach(httpURLConnection::setRequestProperty);
			inputStream = httpURLConnection.getInputStream();
			long l = httpURLConnection.getContentLengthLong();
			OptionalLong optionalLong = l != -1L ? OptionalLong.of(l) : OptionalLong.empty();
			PathUtil.createDirectories(path);
			listener.onContentLength(optionalLong);
			if (optionalLong.isPresent() && optionalLong.getAsLong() > (long)maxBytes) {
				throw new IOException("Filesize is bigger than maximum allowed (file is " + optionalLong + ", limit is " + maxBytes + ")");
			}

			if (path2 == null) {
				Path path3 = Files.createTempFile(path, "download", ".tmp");

				try {
					HashCode hashCode3x = write(hashFunction, maxBytes, listener, inputStream, path3);
					Path path4 = resolve(path, hashCode3x);
					if (!validateHash(path4, hashFunction, hashCode3x)) {
						Files.move(path3, path4, StandardCopyOption.REPLACE_EXISTING);
					}

					return path4;
				} finally {
					Files.deleteIfExists(path3);
				}
			}

			HashCode hashCode2 = write(hashFunction, maxBytes, listener, inputStream, path2);
			if (!hashCode2.equals(hashCode)) {
				throw new IOException("Hash of downloaded file (" + hashCode2 + ") did not match requested (" + hashCode + ")");
			}

			hashCode3 = path2;
		} catch (Throwable var36) {
			if (httpURLConnection != null) {
				InputStream inputStream2 = httpURLConnection.getErrorStream();
				if (inputStream2 != null) {
					try {
						LOGGER.error("HTTP response error: {}", IOUtils.toString(inputStream2, StandardCharsets.UTF_8));
					} catch (Exception var32) {
						LOGGER.error("Failed to read response from server");
					}
				}
			}

			throw new IllegalStateException("Failed to download file " + url, var36);
		} finally {
			listener.onFinish();
			IOUtils.closeQuietly(inputStream);
		}

		return hashCode3;
	}

	private static HashCode hash(Path path, HashFunction hashFunction) throws IOException {
		Hasher hasher = hashFunction.newHasher();
		OutputStream outputStream = Funnels.asOutputStream(hasher);

		try {
			InputStream inputStream = Files.newInputStream(path);

			try {
				inputStream.transferTo(outputStream);
			} catch (Throwable var9) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}
				}

				throw var9;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Throwable var10) {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable var7) {
					var10.addSuppressed(var7);
				}
			}

			throw var10;
		}

		if (outputStream != null) {
			outputStream.close();
		}

		return hasher.hash();
	}

	private static boolean validateHash(Path path, HashFunction hashFunction, HashCode hashCode) throws IOException {
		if (Files.exists(path, new LinkOption[0])) {
			HashCode hashCode2 = hash(path, hashFunction);
			if (hashCode2.equals(hashCode)) {
				return true;
			}

			LOGGER.warn("Mismatched hash of file {}, expected {} but found {}", path, hashCode, hashCode2);
		}

		return false;
	}

	private static Path resolve(Path path, HashCode hashCode) {
		return path.resolve(hashCode.toString());
	}

	private static HashCode write(HashFunction hashFunction, int maxBytes, NetworkUtils.DownloadListener listener, InputStream stream, Path path) throws IOException {
		OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);

		HashCode var11;
		try {
			Hasher hasher = hashFunction.newHasher();
			byte[] bs = new byte[8196];
			long l = 0L;

			int i;
			while ((i = stream.read(bs)) >= 0) {
				l += (long)i;
				listener.onProgress(l);
				if (l > (long)maxBytes) {
					throw new IOException("Filesize was bigger than maximum allowed (got >= " + l + ", limit was " + maxBytes + ")");
				}

				if (Thread.interrupted()) {
					LOGGER.error("INTERRUPTED");
					throw new IOException("Download interrupted");
				}

				outputStream.write(bs, 0, i);
				hasher.putBytes(bs, 0, i);
			}

			var11 = hasher.hash();
		} catch (Throwable var13) {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable var12) {
					var13.addSuppressed(var12);
				}
			}

			throw var13;
		}

		if (outputStream != null) {
			outputStream.close();
		}

		return var11;
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

	public static boolean isPortAvailable(int port) {
		if (port >= 0 && port <= 65535) {
			try {
				ServerSocket serverSocket = new ServerSocket(port);

				boolean var2;
				try {
					var2 = serverSocket.getLocalPort() == port;
				} catch (Throwable var5) {
					try {
						serverSocket.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}

					throw var5;
				}

				serverSocket.close();
				return var2;
			} catch (IOException var6) {
				return false;
			}
		} else {
			return false;
		}
	}

	public interface DownloadListener {
		void onStart();

		void onContentLength(OptionalLong contentLength);

		void onProgress(long writtenBytes);

		void onFinish();
	}
}
