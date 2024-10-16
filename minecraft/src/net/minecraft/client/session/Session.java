package net.minecraft.client.session;

import com.mojang.util.UndashedUuid;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Session {
	private final String username;
	private final UUID uuid;
	private final String accessToken;
	private final Optional<String> xuid;
	private final Optional<String> clientId;
	private final Session.AccountType accountType;

	public Session(String username, UUID uuid, String accessToken, Optional<String> xuid, Optional<String> clientId, Session.AccountType accountType) {
		this.username = username;
		this.uuid = uuid;
		this.accessToken = accessToken;
		this.xuid = xuid;
		this.clientId = clientId;
		this.accountType = accountType;
	}

	public String getSessionId() {
		return "token:" + this.accessToken + ":" + UndashedUuid.toString(this.uuid);
	}

	/**
	 * {@return the UUID, or {@code null} if it is invalid}
	 */
	public UUID getUuidOrNull() {
		return this.uuid;
	}

	public String getUsername() {
		return this.username;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public Optional<String> getClientId() {
		return this.clientId;
	}

	public Optional<String> getXuid() {
		return this.xuid;
	}

	public Session.AccountType getAccountType() {
		return this.accountType;
	}

	@Environment(EnvType.CLIENT)
	public static enum AccountType {
		LEGACY("legacy"),
		MOJANG("mojang"),
		MSA("msa");

		private static final Map<String, Session.AccountType> BY_NAME = (Map<String, Session.AccountType>)Arrays.stream(values())
			.collect(Collectors.toMap(type -> type.name, Function.identity()));
		private final String name;

		private AccountType(final String name) {
			this.name = name;
		}

		@Nullable
		public static Session.AccountType byName(String name) {
			return (Session.AccountType)BY_NAME.get(name.toLowerCase(Locale.ROOT));
		}

		public String getName() {
			return this.name;
		}
	}
}
