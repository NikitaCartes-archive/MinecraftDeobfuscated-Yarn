package net.minecraft.server.filter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCertificate;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;

public class V1TextFilterer extends AbstractTextFilterer {
	private final ConfidentialClientApplication application;
	private final ClientCredentialParameters credentialParameters;
	private final Set<String> fullyFilteredEvents;
	private final int readTimeout;

	private V1TextFilterer(
		URL url,
		AbstractTextFilterer.MessageEncoder messageEncoder,
		AbstractTextFilterer.HashIgnorer hashIgnorer,
		ExecutorService threadPool,
		ConfidentialClientApplication application,
		ClientCredentialParameters credentialParameters,
		Set<String> fullyFilteredEvents,
		int readTimeout
	) {
		super(url, messageEncoder, hashIgnorer, threadPool);
		this.application = application;
		this.credentialParameters = credentialParameters;
		this.fullyFilteredEvents = fullyFilteredEvents;
		this.readTimeout = readTimeout;
	}

	@Nullable
	public static AbstractTextFilterer load(String response) {
		JsonObject jsonObject = JsonHelper.deserialize(response);
		URI uRI = URI.create(JsonHelper.getString(jsonObject, "apiServer"));
		String string = JsonHelper.getString(jsonObject, "apiPath");
		String string2 = JsonHelper.getString(jsonObject, "scope");
		String string3 = JsonHelper.getString(jsonObject, "serverId", "");
		String string4 = JsonHelper.getString(jsonObject, "applicationId");
		String string5 = JsonHelper.getString(jsonObject, "tenantId");
		String string6 = JsonHelper.getString(jsonObject, "roomId", "Java:Chat");
		String string7 = JsonHelper.getString(jsonObject, "certificatePath");
		String string8 = JsonHelper.getString(jsonObject, "certificatePassword", "");
		int i = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
		int j = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
		JsonArray jsonArray = JsonHelper.getArray(jsonObject, "fullyFilteredEvents");
		Set<String> set = new HashSet();
		jsonArray.forEach(json -> set.add(JsonHelper.asString(json, "filteredEvent")));
		int k = JsonHelper.getInt(jsonObject, "connectionReadTimeoutMs", 2000);

		URL uRL;
		try {
			uRL = uRI.resolve(string).toURL();
		} catch (MalformedURLException var26) {
			throw new RuntimeException(var26);
		}

		AbstractTextFilterer.MessageEncoder messageEncoder = (profile, message) -> {
			JsonObject jsonObjectx = new JsonObject();
			jsonObjectx.addProperty("userId", profile.getId().toString());
			jsonObjectx.addProperty("userDisplayName", profile.getName());
			jsonObjectx.addProperty("server", string3);
			jsonObjectx.addProperty("room", string6);
			jsonObjectx.addProperty("area", "JavaChatRealms");
			jsonObjectx.addProperty("data", message);
			jsonObjectx.addProperty("language", "*");
			return jsonObjectx;
		};
		AbstractTextFilterer.HashIgnorer hashIgnorer = AbstractTextFilterer.HashIgnorer.dropHashes(i);
		ExecutorService executorService = newThreadPool(j);

		IClientCertificate iClientCertificate;
		try {
			InputStream inputStream = Files.newInputStream(Path.of(string7));

			try {
				iClientCertificate = ClientCredentialFactory.createFromCertificate(inputStream, string8);
			} catch (Throwable var27) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var24) {
						var27.addSuppressed(var24);
					}
				}

				throw var27;
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception var28) {
			LOGGER.warn("Failed to open certificate file");
			return null;
		}

		ConfidentialClientApplication confidentialClientApplication;
		try {
			confidentialClientApplication = ConfidentialClientApplication.builder(string4, iClientCertificate)
				.sendX5c(true)
				.executorService(executorService)
				.authority(String.format(Locale.ROOT, "https://login.microsoftonline.com/%s/", string5))
				.build();
		} catch (Exception var25) {
			LOGGER.warn("Failed to create confidential client application");
			return null;
		}

		ClientCredentialParameters clientCredentialParameters = ClientCredentialParameters.builder(Set.of(string2)).build();
		return new V1TextFilterer(uRL, messageEncoder, hashIgnorer, executorService, confidentialClientApplication, clientCredentialParameters, set, k);
	}

	private IAuthenticationResult getAuthToken() {
		return (IAuthenticationResult)this.application.acquireToken(this.credentialParameters).join();
	}

	@Override
	protected void addAuthentication(HttpURLConnection connection) {
		IAuthenticationResult iAuthenticationResult = this.getAuthToken();
		connection.setRequestProperty("Authorization", "Bearer " + iAuthenticationResult.accessToken());
	}

	@Override
	protected FilteredMessage filter(String raw, AbstractTextFilterer.HashIgnorer hashIgnorer, JsonObject response) {
		JsonObject jsonObject = JsonHelper.getObject(response, "result", null);
		if (jsonObject == null) {
			return FilteredMessage.censored(raw);
		} else {
			boolean bl = JsonHelper.getBoolean(jsonObject, "filtered", true);
			if (!bl) {
				return FilteredMessage.permitted(raw);
			} else {
				for (JsonElement jsonElement : JsonHelper.getArray(jsonObject, "events", new JsonArray())) {
					JsonObject jsonObject2 = jsonElement.getAsJsonObject();
					String string = JsonHelper.getString(jsonObject2, "id", "");
					if (this.fullyFilteredEvents.contains(string)) {
						return FilteredMessage.censored(raw);
					}
				}

				JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "redactedTextIndex", new JsonArray());
				return new FilteredMessage(raw, this.createFilterMask(raw, jsonArray2, hashIgnorer));
			}
		}
	}

	@Override
	protected int getReadTimeout() {
		return this.readTimeout;
	}
}
