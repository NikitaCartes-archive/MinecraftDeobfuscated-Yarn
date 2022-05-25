package net.minecraft.server.filter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
import org.slf4j.Logger;

public class TextFilterer implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
	private static final ThreadFactory THREAD_FACTORY = runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("Chat-Filter-Worker-" + NEXT_WORKER_ID.getAndIncrement());
		return thread;
	};
	private static final String CHAT_ENDPOINT = "v1/chat";
	private final URL chatEndpoint;
	private final TextFilterer.MessageEncoder messageEncoder;
	final URL joinEndpoint;
	final TextFilterer.ProfileEncoder joinEncoder;
	final URL leaveEndpoint;
	final TextFilterer.ProfileEncoder leaveEncoder;
	private final String apiKey;
	final TextFilterer.HashIgnorer ignorer;
	final ExecutorService executor;

	private TextFilterer(
		URL chatEndpoint,
		TextFilterer.MessageEncoder messageEncoder,
		URL joinEndpoint,
		TextFilterer.ProfileEncoder joinEncoder,
		URL leaveEndpoint,
		TextFilterer.ProfileEncoder leaveEncoder,
		String apiKey,
		TextFilterer.HashIgnorer ignorer,
		int parallelism
	) {
		this.apiKey = apiKey;
		this.ignorer = ignorer;
		this.chatEndpoint = chatEndpoint;
		this.messageEncoder = messageEncoder;
		this.joinEndpoint = joinEndpoint;
		this.joinEncoder = joinEncoder;
		this.leaveEndpoint = leaveEndpoint;
		this.leaveEncoder = leaveEncoder;
		this.executor = Executors.newFixedThreadPool(parallelism, THREAD_FACTORY);
	}

	private static URL getEndpoint(URI root, @Nullable JsonObject endpoints, String key, String fallback) throws MalformedURLException {
		String string = getValue(endpoints, key, fallback);
		return root.resolve("/" + string).toURL();
	}

	private static String getValue(@Nullable JsonObject json, String key, String fallback) {
		return json != null ? JsonHelper.getString(json, key, fallback) : fallback;
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
					String string3 = JsonHelper.getString(jsonObject, "roomId", "Java:Chat");
					int j = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
					int k = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
					JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "endpoints", null);
					String string4 = getValue(jsonObject2, "chat", "v1/chat");
					boolean bl = string4.equals("v1/chat");
					URL uRL = uRI.resolve("/" + string4).toURL();
					URL uRL2 = getEndpoint(uRI, jsonObject2, "join", "v1/join");
					URL uRL3 = getEndpoint(uRI, jsonObject2, "leave", "v1/leave");
					TextFilterer.ProfileEncoder profileEncoder = profile -> {
						JsonObject jsonObjectx = new JsonObject();
						jsonObjectx.addProperty("server", string2);
						jsonObjectx.addProperty("room", string3);
						jsonObjectx.addProperty("user_id", profile.getId().toString());
						jsonObjectx.addProperty("user_display_name", profile.getName());
						return jsonObjectx;
					};
					TextFilterer.MessageEncoder messageEncoder;
					if (bl) {
						messageEncoder = (profile, message) -> {
							JsonObject jsonObjectx = new JsonObject();
							jsonObjectx.addProperty("rule", i);
							jsonObjectx.addProperty("server", string2);
							jsonObjectx.addProperty("room", string3);
							jsonObjectx.addProperty("player", profile.getId().toString());
							jsonObjectx.addProperty("player_display_name", profile.getName());
							jsonObjectx.addProperty("text", message);
							jsonObjectx.addProperty("language", "*");
							return jsonObjectx;
						};
					} else {
						String string5 = String.valueOf(i);
						messageEncoder = (profile, message) -> {
							JsonObject jsonObjectx = new JsonObject();
							jsonObjectx.addProperty("rule_id", string5);
							jsonObjectx.addProperty("category", string2);
							jsonObjectx.addProperty("subcategory", string3);
							jsonObjectx.addProperty("user_id", profile.getId().toString());
							jsonObjectx.addProperty("user_display_name", profile.getName());
							jsonObjectx.addProperty("text", message);
							jsonObjectx.addProperty("language", "*");
							return jsonObjectx;
						};
					}

					TextFilterer.HashIgnorer hashIgnorer = TextFilterer.HashIgnorer.dropHashes(j);
					String string6 = Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.US_ASCII));
					return new TextFilterer(uRL, messageEncoder, uRL2, profileEncoder, uRL3, profileEncoder, string6, hashIgnorer, k);
				}
			} catch (Exception var19) {
				LOGGER.warn("Failed to parse chat filter config {}", config, var19);
				return null;
			}
		}
	}

	void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, TextFilterer.ProfileEncoder profileEncoder, Executor executor) {
		executor.execute(() -> {
			JsonObject jsonObject = profileEncoder.encode(gameProfile);

			try {
				this.sendRequest(jsonObject, endpoint);
			} catch (Exception var6) {
				LOGGER.warn("Failed to send join/leave packet to {} for player {}", endpoint, gameProfile, var6);
			}
		});
	}

	CompletableFuture<FilteredMessage<String>> filterMessage(GameProfile gameProfile, String message, TextFilterer.HashIgnorer ignorer, Executor executor) {
		return message.isEmpty() ? CompletableFuture.completedFuture(FilteredMessage.EMPTY) : CompletableFuture.supplyAsync(() -> {
			JsonObject jsonObject = this.messageEncoder.encode(gameProfile, message);

			try {
				JsonObject jsonObject2 = this.sendJsonRequest(jsonObject, this.chatEndpoint);
				boolean bl = JsonHelper.getBoolean(jsonObject2, "response", false);
				if (bl) {
					return FilteredMessage.permitted(message);
				} else {
					String string2 = JsonHelper.getString(jsonObject2, "hashed", null);
					if (string2 == null) {
						return FilteredMessage.censored(message);
					} else {
						int i = JsonHelper.getArray(jsonObject2, "hashes").size();
						return ignorer.shouldIgnore(string2, i) ? FilteredMessage.censored(message) : new FilteredMessage<>(message, string2);
					}
				}
			} catch (Exception var9) {
				LOGGER.warn("Failed to validate message '{}'", message, var9);
				return FilteredMessage.censored(message);
			}
		}, executor);
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
					var13 = Streams.parse(new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))).getAsJsonObject();
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
			return switch (hashesToDrop) {
				case -1 -> NEVER_IGNORE;
				case 0 -> IGNORE_IF_MATCHES_ALL;
				default -> internalDropHashes(hashesToDrop);
			};
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
			TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.joinEndpoint, TextFilterer.this.joinEncoder, this.executor);
		}

		@Override
		public void onDisconnect() {
			TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.leaveEndpoint, TextFilterer.this.leaveEncoder, this.executor);
		}

		@Override
		public CompletableFuture<List<FilteredMessage<String>>> filterTexts(List<String> texts) {
			List<CompletableFuture<FilteredMessage<String>>> list = (List<CompletableFuture<FilteredMessage<String>>>)texts.stream()
				.map(text -> TextFilterer.this.filterMessage(this.gameProfile, text, TextFilterer.this.ignorer, this.executor))
				.collect(ImmutableList.toImmutableList());
			return Util.combine(list).exceptionally(throwable -> ImmutableList.of());
		}

		@Override
		public CompletableFuture<FilteredMessage<String>> filterText(String text) {
			return TextFilterer.this.filterMessage(this.gameProfile, text, TextFilterer.this.ignorer, this.executor);
		}
	}

	@FunctionalInterface
	interface MessageEncoder {
		JsonObject encode(GameProfile gameProfile, String message);
	}

	@FunctionalInterface
	interface ProfileEncoder {
		JsonObject encode(GameProfile gameProfile);
	}
}
