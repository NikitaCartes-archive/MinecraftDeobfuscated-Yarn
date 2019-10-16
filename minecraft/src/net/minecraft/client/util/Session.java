package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Session {
	private final String username;
	private final String uuid;
	private final String accessToken;
	private final Session.AccountType accountType;

	public Session(String string, String string2, String string3, String string4) {
		this.username = string;
		this.uuid = string2;
		this.accessToken = string3;
		this.accountType = Session.AccountType.byName(string4);
	}

	public String getSessionId() {
		return "token:" + this.accessToken + ":" + this.uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public String getUsername() {
		return this.username;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public GameProfile getProfile() {
		try {
			UUID uUID = UUIDTypeAdapter.fromString(this.getUuid());
			return new GameProfile(uUID, this.getUsername());
		} catch (IllegalArgumentException var2) {
			return new GameProfile(null, this.getUsername());
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum AccountType {
		LEGACY("legacy"),
		MOJANG("mojang");

		private static final Map<String, Session.AccountType> BY_NAME = (Map<String, Session.AccountType>)Arrays.stream(values())
			.collect(Collectors.toMap(accountType -> accountType.name, Function.identity()));
		private final String name;

		private AccountType(String string2) {
			this.name = string2;
		}

		@Nullable
		public static Session.AccountType byName(String string) {
			return (Session.AccountType)BY_NAME.get(string.toLowerCase(Locale.ROOT));
		}
	}
}
