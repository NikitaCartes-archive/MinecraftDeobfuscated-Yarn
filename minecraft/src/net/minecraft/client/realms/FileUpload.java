package net.minecraft.client.realms;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.gui.screen.UploadResult;
import net.minecraft.client.util.Session;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FileUpload {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int MAX_ATTEMPTS = 5;
	private static final String UPLOAD_ENDPOINT = "/upload";
	private final File file;
	private final long worldId;
	private final int slotId;
	private final UploadInfo uploadInfo;
	private final String sessionId;
	private final String username;
	private final String clientVersion;
	private final UploadStatus uploadStatus;
	private final AtomicBoolean cancelled = new AtomicBoolean(false);
	@Nullable
	private CompletableFuture<UploadResult> uploadTask;
	private final RequestConfig requestConfig = RequestConfig.custom()
		.setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L))
		.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L))
		.build();

	public FileUpload(File file, long worldId, int slotId, UploadInfo uploadInfo, Session session, String clientVersion, UploadStatus uploadStatus) {
		this.file = file;
		this.worldId = worldId;
		this.slotId = slotId;
		this.uploadInfo = uploadInfo;
		this.sessionId = session.getSessionId();
		this.username = session.getUsername();
		this.clientVersion = clientVersion;
		this.uploadStatus = uploadStatus;
	}

	public void upload(Consumer<UploadResult> callback) {
		if (this.uploadTask == null) {
			this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0));
			this.uploadTask.thenAccept(callback);
		}
	}

	public void cancel() {
		this.cancelled.set(true);
		if (this.uploadTask != null) {
			this.uploadTask.cancel(false);
			this.uploadTask = null;
		}
	}

	private UploadResult requestUpload(int currentAttempt) {
		UploadResult.Builder builder = new UploadResult.Builder();
		if (this.cancelled.get()) {
			return builder.build();
		} else {
			this.uploadStatus.totalBytes = this.file.length();
			HttpPost httpPost = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId));
			CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();

			UploadResult var8;
			try {
				this.setupRequest(httpPost);
				HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
				long l = this.getRetryDelaySeconds(httpResponse);
				if (!this.shouldRetry(l, currentAttempt)) {
					this.handleResponse(httpResponse, builder);
					return builder.build();
				}

				var8 = this.retryUploadAfter(l, currentAttempt);
			} catch (Exception var12) {
				if (!this.cancelled.get()) {
					LOGGER.error("Caught exception while uploading: ", (Throwable)var12);
				}

				return builder.build();
			} finally {
				this.cleanup(httpPost, closeableHttpClient);
			}

			return var8;
		}
	}

	private void cleanup(HttpPost request, @Nullable CloseableHttpClient client) {
		request.releaseConnection();
		if (client != null) {
			try {
				client.close();
			} catch (IOException var4) {
				LOGGER.error("Failed to close Realms upload client");
			}
		}
	}

	private void setupRequest(HttpPost request) throws FileNotFoundException {
		request.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
		FileUpload.CustomInputStreamEntity customInputStreamEntity = new FileUpload.CustomInputStreamEntity(
			new FileInputStream(this.file), this.file.length(), this.uploadStatus
		);
		customInputStreamEntity.setContentType("application/octet-stream");
		request.setEntity(customInputStreamEntity);
	}

	private void handleResponse(HttpResponse response, UploadResult.Builder uploadResultBuilder) throws IOException {
		int i = response.getStatusLine().getStatusCode();
		if (i == 401) {
			LOGGER.debug("Realms server returned 401: {}", response.getFirstHeader("WWW-Authenticate"));
		}

		uploadResultBuilder.withStatusCode(i);
		if (response.getEntity() != null) {
			String string = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (string != null) {
				try {
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(string).getAsJsonObject().get("errorMsg");
					Optional<String> optional = Optional.ofNullable(jsonElement).map(JsonElement::getAsString);
					uploadResultBuilder.withErrorMessage((String)optional.orElse(null));
				} catch (Exception var8) {
				}
			}
		}
	}

	private boolean shouldRetry(long retryDelaySeconds, int currentAttempt) {
		return retryDelaySeconds > 0L && currentAttempt + 1 < 5;
	}

	private UploadResult retryUploadAfter(long retryDelaySeconds, int currentAttempt) throws InterruptedException {
		Thread.sleep(Duration.ofSeconds(retryDelaySeconds).toMillis());
		return this.requestUpload(currentAttempt + 1);
	}

	private long getRetryDelaySeconds(HttpResponse response) {
		return (Long)Optional.ofNullable(response.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
	}

	public boolean isFinished() {
		return this.uploadTask.isDone() || this.uploadTask.isCancelled();
	}

	@Environment(EnvType.CLIENT)
	static class CustomInputStreamEntity extends InputStreamEntity {
		private final long length;
		private final InputStream content;
		private final UploadStatus uploadStatus;

		public CustomInputStreamEntity(InputStream content, long length, UploadStatus uploadStatus) {
			super(content);
			this.content = content;
			this.length = length;
			this.uploadStatus = uploadStatus;
		}

		@Override
		public void writeTo(OutputStream outstream) throws IOException {
			Args.notNull(outstream, "Output stream");
			InputStream inputStream = this.content;

			try {
				byte[] bs = new byte[4096];
				int i;
				if (this.length < 0L) {
					while ((i = inputStream.read(bs)) != -1) {
						outstream.write(bs, 0, i);
						this.uploadStatus.bytesWritten += (long)i;
					}
				} else {
					long l = this.length;

					while (l > 0L) {
						i = inputStream.read(bs, 0, (int)Math.min(4096L, l));
						if (i == -1) {
							break;
						}

						outstream.write(bs, 0, i);
						this.uploadStatus.bytesWritten += (long)i;
						l -= (long)i;
						outstream.flush();
					}
				}
			} finally {
				inputStream.close();
			}
		}
	}
}
