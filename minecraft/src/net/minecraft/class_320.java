package net.minecraft;

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
public class class_320 {
	private final String field_1982;
	private final String field_1985;
	private final String field_1983;
	private final class_320.class_321 field_1984;

	public class_320(String string, String string2, String string3, String string4) {
		this.field_1982 = string;
		this.field_1985 = string2;
		this.field_1983 = string3;
		this.field_1984 = class_320.class_321.method_1679(string4);
	}

	public String method_1675() {
		return "token:" + this.field_1983 + ":" + this.field_1985;
	}

	public String method_1673() {
		return this.field_1985;
	}

	public String method_1676() {
		return this.field_1982;
	}

	public String method_1674() {
		return this.field_1983;
	}

	public GameProfile method_1677() {
		try {
			UUID uUID = UUIDTypeAdapter.fromString(this.method_1673());
			return new GameProfile(uUID, this.method_1676());
		} catch (IllegalArgumentException var2) {
			return new GameProfile(null, this.method_1676());
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_321 {
		field_1990("legacy"),
		field_1988("mojang");

		private static final Map<String, class_320.class_321> field_1989 = (Map<String, class_320.class_321>)Arrays.stream(values())
			.collect(Collectors.toMap(arg -> arg.field_1986, Function.identity()));
		private final String field_1986;

		private class_321(String string2) {
			this.field_1986 = string2;
		}

		@Nullable
		public static class_320.class_321 method_1679(String string) {
			return (class_320.class_321)field_1989.get(string.toLowerCase(Locale.ROOT));
		}
	}
}
