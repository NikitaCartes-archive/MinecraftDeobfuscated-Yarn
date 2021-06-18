package net.minecraft.server.filter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextFilterer implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
	private static final ThreadFactory THREAD_FACTORY = runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("Chat-Filter-Worker-" + NEXT_WORKER_ID.getAndIncrement());
		return thread;
	};
	private final URL chatEndpoint;
	final URL joinEndpoint;
	final URL leaveEndpoint;
	private final String apiKey;
	private final int ruleId;
	private final String serverId;
	final TextFilterer.HashIgnorer ignorer;
	final ExecutorService executor;

	private TextFilterer(URI apiUrl, String apiKey, int ruleId, String serverId, TextFilterer.HashIgnorer ignorer, int threadsNumber) throws MalformedURLException {
		this.apiKey = apiKey;
		this.ruleId = ruleId;
		this.serverId = serverId;
		this.ignorer = ignorer;
		this.chatEndpoint = apiUrl.resolve("/v1/chat").toURL();
		this.joinEndpoint = apiUrl.resolve("/v1/join").toURL();
		this.leaveEndpoint = apiUrl.resolve("/v1/leave").toURL();
		this.executor = Executors.newFixedThreadPool(threadsNumber, THREAD_FACTORY);
	}

	@Nullable
	public static TextFilterer load(String config) {
		if (Strings.isNullOrEmpty(config)) {
			return null;
		} else {
			try {
				JsonObject jsonObject = JsonHelper.deserialize(config);
				URI uRI = new URI(JsonHelper.getString(jsonObject, "apiServer"));
				String string = JsonHelper.getString(jsonObject, "apiKey");
				if (string.isEmpty()) {
					throw new IllegalArgumentException("Missing API key");
				} else {
					int i = JsonHelper.getInt(jsonObject, "ruleId", 1);
					String string2 = JsonHelper.getString(jsonObject, "serverId", "");
					int j = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
					int k = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
					TextFilterer.HashIgnorer hashIgnorer = TextFilterer.HashIgnorer.dropHashes(j);
					return new TextFilterer(uRI, new Base64().encodeToString(string.getBytes(StandardCharsets.US_ASCII)), i, string2, hashIgnorer, k);
				}
			} catch (Exception var9) {
				LOGGER.warn("Failed to parse chat filter config {}", config, var9);
				return null;
			}
		}
	}

	void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, Executor executor) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("server", this.serverId);
		jsonObject.addProperty("room", "Chat");
		jsonObject.addProperty("user_id", gameProfile.getId().toString());
		jsonObject.addProperty("user_display_name", gameProfile.getName());
		executor.execute(() -> {
			try {
				this.sendRequest(jsonObject, endpoint);
			} catch (Exception var5) {
				LOGGER.warn("Failed to send join/leave packet to {} for player {}", endpoint, gameProfile, var5);
			}
		});
	}

	CompletableFuture<TextStream.Message> filterMessage(GameProfile gameProfile, String message, TextFilterer.HashIgnorer ignorer, Executor executor) {
		if (message.isEmpty()) {
			return CompletableFuture.completedFuture(TextStream.Message.EMPTY);
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("rule", this.ruleId);
			jsonObject.addProperty("server", this.serverId);
			jsonObject.addProperty("room", "Chat");
			jsonObject.addProperty("player", gameProfile.getId().toString());
			jsonObject.addProperty("player_display_name", gameProfile.getName());
			jsonObject.addProperty("text", message);
			return CompletableFuture.supplyAsync(() -> {
				try {
					JsonObject jsonObject2 = this.sendJsonRequest(jsonObject, this.chatEndpoint);
					boolean bl = JsonHelper.getBoolean(jsonObject2, "response", false);
					if (bl) {
						return TextStream.Message.permitted(message);
					} else {
						String string2 = JsonHelper.getString(jsonObject2, "hashed", null);
						if (string2 == null) {
							return TextStream.Message.censored(message);
						} else {
							int i = JsonHelper.getArray(jsonObject2, "hashes").size();
							return ignorer.shouldIgnore(string2, i) ? TextStream.Message.censored(message) : new TextStream.Message(message, string2);
						}
					}
				} catch (Exception var8) {
					LOGGER.warn("Failed to validate message '{}'", message, var8);
					return TextStream.Message.censored(message);
				}
			}, executor);
		}
	}

	public void close() {
		this.executor.shutdownNow();
	}

	private void consumeFully(InputStream inputStream) throws IOException {
		byte[] bs = new byte[1024];

		while (inputStream.read(bs) != -1) {
		}
	}

	private JsonObject sendJsonRequest(JsonObject payload, URL endpoint) throws IOException {
		HttpURLConnection httpURLConnection = this.createConnection(payload, endpoint);
		InputStream inputStream = httpURLConnection.getInputStream();

		JsonObject var13;
		label74: {
			try {
				if (httpURLConnection.getResponseCode() == 204) {
					var13 = new JsonObject();
					break label74;
				}

				try {
					var13 = Streams.parse(new JsonReader(new InputStreamReader(inputStream))).getAsJsonObject();
				} finally {
					this.consumeFully(inputStream);
				}
			} catch (Throwable var12) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var10) {
						var12.addSuppressed(var10);
					}
				}

				throw var12;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var13;
		}

		if (inputStream != null) {
			inputStream.close();
		}

		return var13;
	}

	private void sendRequest(JsonObject payload, URL endpoint) throws IOException {
		HttpURLConnection httpURLConnection = this.createConnection(payload, endpoint);
		InputStream inputStream = httpURLConnection.getInputStream();

		try {
			this.consumeFully(inputStream);
		} catch (Throwable var8) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable var7) {
					var8.addSuppressed(var7);
				}
			}

			throw var8;
		}

		if (inputStream != null) {
			inputStream.close();
		}
	}

	private HttpURLConnection createConnection(JsonObject payload, URL endpoint) throws IOException {
		HttpURLConnection httpURLConnection = (HttpURLConnection)endpoint.openConnection();
		httpURLConnection.setConnectTimeout(15000);
		httpURLConnection.setReadTimeout(2000);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setDoInput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		httpURLConnection.setRequestProperty("Accept", "application/json");
		httpURLConnection.setRequestProperty("Authorization", "Basic " + this.apiKey);
		httpURLConnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getGameVersion().getName());
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);

		try {
			JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);

			try {
				Streams.write(payload, jsonWriter);
			} catch (Throwable var10) {
				try {
					jsonWriter.close();
				} catch (Throwable var9) {
					var10.addSuppressed(var9);
				}

				throw var10;
			}

			jsonWriter.close();
		} catch (Throwable var11) {
			try {
				outputStreamWriter.close();
			} catch (Throwable var8) {
				var11.addSuppressed(var8);
			}

			throw var11;
		}

		outputStreamWriter.close();
		int i = httpURLConnection.getResponseCode();
		if (i >= 200 && i < 300) {
			return httpURLConnection;
		} else {
			throw new TextFilterer.FailedHttpRequestException(i + " " + httpURLConnection.getResponseMessage());
		}
	}

	public TextStream createFilterer(GameProfile gameProfile) {
		return new TextFilterer.Impl(gameProfile);
	}

	public static class FailedHttpRequestException extends RuntimeException {
		FailedHttpRequestException(String message) {
			super(message);
		}
	}

	@FunctionalInterface
	public interface HashIgnorer {
		TextFilterer.HashIgnorer NEVER_IGNORE = (hashes, hashesSize) -> false;
		TextFilterer.HashIgnorer IGNORE_IF_MATCHES_ALL = (hashes, hashesSize) -> hashes.length() == hashesSize;

		static TextFilterer.HashIgnorer internalDropHashes(int hashesToDrop) {
			return (hashes, hashesSize) -> hashesSize >= hashesToDrop;
		}

		static TextFilterer.HashIgnorer dropHashes(int hashesToDrop) {
			switch (hashesToDrop) {
				case -1:
					return NEVER_IGNORE;
				case 0:
					return IGNORE_IF_MATCHES_ALL;
				default:
					return internalDropHashes(hashesToDrop);
			}
		}

		boolean shouldIgnore(String hashes, int hashesSize);
	}

	class Impl implements TextStream {
		private final GameProfile gameProfile;
		private final Executor executor;

		Impl(GameProfile gameProfile) {
			this.gameProfile = gameProfile;
			TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(TextFilterer.this.executor, "chat stream for " + gameProfile.getName());
			this.executor = taskExecutor::send;
		}

		@Override
		public void onConnect() {
			TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.joinEndpoint, this.executor);
		}

		@Override
		public void onDisconnect() {
			TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.leaveEndpoint, this.executor);
		}

		@Override
		public CompletableFuture<List<TextStream.Message>> filterTexts(List<String> texts) {
			List<CompletableFuture<TextStream.Message>> list = (List<CompletableFuture<TextStream.Message>>)texts.stream()
				.map(string -> TextFilterer.this.filterMessage(this.gameProfile, string, TextFilterer.this.ignorer, this.executor))
				.collect(ImmutableList.toImmutableList());
			return Util.combine(list).exceptionally(throwable -> ImmutableList.of());
		}

		@Override
		public CompletableFuture<TextStream.Message> filterText(String text) {
			return TextFilterer.this.filterMessage(this.gameProfile, text, TextFilterer.this.ignorer, this.executor);
		}
	}
}
