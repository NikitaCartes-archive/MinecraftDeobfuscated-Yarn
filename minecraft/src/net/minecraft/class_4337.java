package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.UploadInfo;
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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
public class class_4337 {
	private static final Logger field_19550 = LogManager.getLogger();
	private final File field_19551;
	private final long field_19552;
	private final int field_19553;
	private final UploadInfo field_19554;
	private final String field_19555;
	private final String field_19556;
	private final String field_19557;
	private final class_4351 field_19558;
	private AtomicBoolean field_19559 = new AtomicBoolean(false);
	private CompletableFuture<class_4429> field_19560;
	private final RequestConfig field_19561 = RequestConfig.custom()
		.setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L))
		.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L))
		.build();

	public class_4337(File file, long l, int i, UploadInfo uploadInfo, String string, String string2, String string3, class_4351 arg) {
		this.field_19551 = file;
		this.field_19552 = l;
		this.field_19553 = i;
		this.field_19554 = uploadInfo;
		this.field_19555 = string;
		this.field_19556 = string2;
		this.field_19557 = string3;
		this.field_19558 = arg;
	}

	public void method_20973(Consumer<class_4429> consumer) {
		if (this.field_19560 == null) {
			this.field_19560 = CompletableFuture.supplyAsync(() -> this.method_20971(0));
			this.field_19560.thenAccept(consumer);
		}
	}

	public void method_20970() {
		this.field_19559.set(true);
		if (this.field_19560 != null) {
			this.field_19560.cancel(false);
			this.field_19560 = null;
		}
	}

	private class_4429 method_20971(int i) {
		class_4429.class_4430 lv = new class_4429.class_4430();
		if (this.field_19559.get()) {
			return lv.method_21541();
		} else {
			this.field_19558.field_19602 = this.field_19551.length();
			HttpPost httpPost = new HttpPost(
				"http://" + this.field_19554.getUploadEndpoint() + ":" + this.field_19554.getPort() + "/upload" + "/" + this.field_19552 + "/" + this.field_19553
			);
			CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setDefaultRequestConfig(this.field_19561).build();

			class_4429 var8;
			try {
				this.method_20976(httpPost);
				HttpResponse httpResponse = closeableHttpClient.execute(httpPost);
				long l = this.method_20974(httpResponse);
				if (!this.method_20972(l, i)) {
					this.method_20975(httpResponse, lv);
					return lv.method_21541();
				}

				var8 = this.method_20979(l, i);
			} catch (Exception var12) {
				if (!this.field_19559.get()) {
					field_19550.error("Caught exception while uploading: ", (Throwable)var12);
				}

				return lv.method_21541();
			} finally {
				this.method_20977(httpPost, closeableHttpClient);
			}

			return var8;
		}
	}

	private void method_20977(HttpPost httpPost, CloseableHttpClient closeableHttpClient) {
		httpPost.releaseConnection();
		if (closeableHttpClient != null) {
			try {
				closeableHttpClient.close();
			} catch (IOException var4) {
				field_19550.error("Failed to close Realms upload client");
			}
		}
	}

	private void method_20976(HttpPost httpPost) throws FileNotFoundException {
		httpPost.setHeader(
			"Cookie", "sid=" + this.field_19555 + ";token=" + this.field_19554.getToken() + ";user=" + this.field_19556 + ";version=" + this.field_19557
		);
		class_4337.class_4338 lv = new class_4337.class_4338(new FileInputStream(this.field_19551), this.field_19551.length(), this.field_19558);
		lv.setContentType("application/octet-stream");
		httpPost.setEntity(lv);
	}

	private void method_20975(HttpResponse httpResponse, class_4429.class_4430 arg) throws IOException {
		int i = httpResponse.getStatusLine().getStatusCode();
		if (i == 401) {
			field_19550.debug("Realms server returned 401: " + httpResponse.getFirstHeader("WWW-Authenticate"));
		}

		arg.method_21542(i);
		if (httpResponse.getEntity() != null) {
			String string = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			if (string != null) {
				try {
					JsonParser jsonParser = new JsonParser();
					JsonElement jsonElement = jsonParser.parse(string).getAsJsonObject().get("errorMsg");
					Optional<String> optional = Optional.ofNullable(jsonElement).map(JsonElement::getAsString);
					arg.method_21543((String)optional.orElse(null));
				} catch (Exception var8) {
				}
			}
		}
	}

	private boolean method_20972(long l, int i) {
		return l > 0L && i + 1 < 5;
	}

	private class_4429 method_20979(long l, int i) throws InterruptedException {
		Thread.sleep(Duration.ofSeconds(l).toMillis());
		return this.method_20971(i + 1);
	}

	private long method_20974(HttpResponse httpResponse) {
		return (Long)Optional.ofNullable(httpResponse.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(0L);
	}

	public boolean method_20978() {
		return this.field_19560.isDone() || this.field_19560.isCancelled();
	}

	@Environment(EnvType.CLIENT)
	static class class_4338 extends InputStreamEntity {
		private final long field_19562;
		private final InputStream field_19563;
		private final class_4351 field_19564;

		public class_4338(InputStream inputStream, long l, class_4351 arg) {
			super(inputStream);
			this.field_19563 = inputStream;
			this.field_19562 = l;
			this.field_19564 = arg;
		}

		@Override
		public void writeTo(OutputStream outputStream) throws IOException {
			Args.notNull(outputStream, "Output stream");
			InputStream inputStream = this.field_19563;

			try {
				byte[] bs = new byte[4096];
				int i;
				if (this.field_19562 < 0L) {
					while ((i = inputStream.read(bs)) != -1) {
						outputStream.write(bs, 0, i);
						class_4351 var12 = this.field_19564;
						var12.field_19601 = var12.field_19601 + (long)i;
					}
				} else {
					long l = this.field_19562;

					while (l > 0L) {
						i = inputStream.read(bs, 0, (int)Math.min(4096L, l));
						if (i == -1) {
							break;
						}

						outputStream.write(bs, 0, i);
						class_4351 var7 = this.field_19564;
						var7.field_19601 = var7.field_19601 + (long)i;
						l -= (long)i;
						outputStream.flush();
					}
				}
			} finally {
				inputStream.close();
			}
		}
	}
}
