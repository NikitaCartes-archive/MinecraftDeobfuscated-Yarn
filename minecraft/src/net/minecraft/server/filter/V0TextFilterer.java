package net.minecraft.server.filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import net.minecraft.network.message.FilterMask;
import net.minecraft.util.JsonHelper;

public class V0TextFilterer extends AbstractTextFilterer {
	private static final String CHAT_ENDPOINT = "v1/chat";
	final URL joinEndpoint;
	final V0TextFilterer.ProfileEncoder joinEncoder;
	final URL leaveEndpoint;
	final V0TextFilterer.ProfileEncoder leaveEncoder;
	private final String apiKey;

	private V0TextFilterer(
		URL chatEndpoint,
		AbstractTextFilterer.MessageEncoder messageEncoder,
		URL joinEndpoint,
		V0TextFilterer.ProfileEncoder joinEncoder,
		URL leaveEndpoint,
		V0TextFilterer.ProfileEncoder leaveEncoder,
		String apiKey,
		AbstractTextFilterer.HashIgnorer ignorer,
		ExecutorService threadPool
	) {
		super(chatEndpoint, messageEncoder, ignorer, threadPool);
		this.joinEndpoint = joinEndpoint;
		this.joinEncoder = joinEncoder;
		this.leaveEndpoint = leaveEndpoint;
		this.leaveEncoder = leaveEncoder;
		this.apiKey = apiKey;
	}

	@Nullable
	public static AbstractTextFilterer load(String config) {
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
				String string4 = getEndpointPath(jsonObject2, "chat", "v1/chat");
				boolean bl = string4.equals("v1/chat");
				URL uRL = uRI.resolve("/" + string4).toURL();
				URL uRL2 = resolveEndpoint(uRI, jsonObject2, "join", "v1/join");
				URL uRL3 = resolveEndpoint(uRI, jsonObject2, "leave", "v1/leave");
				V0TextFilterer.ProfileEncoder profileEncoder = profile -> {
					JsonObject jsonObjectx = new JsonObject();
					jsonObjectx.addProperty("server", string2);
					jsonObjectx.addProperty("room", string3);
					jsonObjectx.addProperty("user_id", profile.getId().toString());
					jsonObjectx.addProperty("user_display_name", profile.getName());
					return jsonObjectx;
				};
				AbstractTextFilterer.MessageEncoder messageEncoder;
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

				AbstractTextFilterer.HashIgnorer hashIgnorer = AbstractTextFilterer.HashIgnorer.dropHashes(j);
				ExecutorService executorService = newThreadPool(k);
				String string6 = Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.US_ASCII));
				return new V0TextFilterer(uRL, messageEncoder, uRL2, profileEncoder, uRL3, profileEncoder, string6, hashIgnorer, executorService);
			}
		} catch (Exception var20) {
			LOGGER.warn("Failed to parse chat filter config {}", config, var20);
			return null;
		}
	}

	@Override
	public TextStream createFilterer(GameProfile profile) {
		return new AbstractTextFilterer.StreamImpl(profile) {
			@Override
			public void onConnect() {
				V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.joinEndpoint, V0TextFilterer.this.joinEncoder, this.executor);
			}

			@Override
			public void onDisconnect() {
				V0TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, V0TextFilterer.this.leaveEndpoint, V0TextFilterer.this.leaveEncoder, this.executor);
			}
		};
	}

	void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, V0TextFilterer.ProfileEncoder profileEncoder, Executor executor) {
		executor.execute(() -> {
			JsonObject jsonObject = profileEncoder.encode(gameProfile);

			try {
				this.sendRequest(jsonObject, endpoint);
			} catch (Exception var6) {
				LOGGER.warn("Failed to send join/leave packet to {} for player {}", endpoint, gameProfile, var6);
			}
		});
	}

	private void sendRequest(JsonObject payload, URL endpoint) throws IOException {
		HttpURLConnection httpURLConnection = this.openConnection(payload, endpoint);
		InputStream inputStream = httpURLConnection.getInputStream();

		try {
			this.discardRestOfInput(inputStream);
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

	@Override
	protected void addAuthentication(HttpURLConnection connection) {
		connection.setRequestProperty("Authorization", "Basic " + this.apiKey);
	}

	@Override
	protected FilteredMessage filter(String raw, AbstractTextFilterer.HashIgnorer hashIgnorer, JsonObject response) {
		boolean bl = JsonHelper.getBoolean(response, "response", false);
		if (bl) {
			return FilteredMessage.permitted(raw);
		} else {
			String string = JsonHelper.getString(response, "hashed", null);
			if (string == null) {
				return FilteredMessage.censored(raw);
			} else {
				JsonArray jsonArray = JsonHelper.getArray(response, "hashes");
				FilterMask filterMask = this.createFilterMask(raw, jsonArray, hashIgnorer);
				return new FilteredMessage(raw, filterMask);
			}
		}
	}

	@FunctionalInterface
	interface ProfileEncoder {
		JsonObject encode(GameProfile gameProfile);
	}
}
