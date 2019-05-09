package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;

public class Identifier implements Comparable<Identifier> {
	private static final SimpleCommandExceptionType COMMAND_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid"));
	protected final String namespace;
	protected final String path;

	protected Identifier(String[] strings) {
		this.namespace = StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0];
		this.path = strings[1];
		if (!isNamespaceValid(this.namespace)) {
			throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
		} else if (!isPathValid(this.path)) {
			throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
		}
	}

	public Identifier(String string) {
		this(split(string, ':'));
	}

	public Identifier(String string, String string2) {
		this(new String[]{string, string2});
	}

	public static Identifier splitOn(String string, char c) {
		return new Identifier(split(string, c));
	}

	@Nullable
	public static Identifier ofNullable(String string) {
		try {
			return new Identifier(string);
		} catch (InvalidIdentifierException var2) {
			return null;
		}
	}

	protected static String[] split(String string, char c) {
		String[] strings = new String[]{"minecraft", string};
		int i = string.indexOf(c);
		if (i >= 0) {
			strings[1] = string.substring(i + 1, string.length());
			if (i >= 1) {
				strings[0] = string.substring(0, i);
			}
		}

		return strings;
	}

	public String getPath() {
		return this.path;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String toString() {
		return this.namespace + ':' + this.path;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Identifier)) {
			return false;
		} else {
			Identifier identifier = (Identifier)object;
			return this.namespace.equals(identifier.namespace) && this.path.equals(identifier.path);
		}
	}

	public int hashCode() {
		return 31 * this.namespace.hashCode() + this.path.hashCode();
	}

	public int method_12833(Identifier identifier) {
		int i = this.path.compareTo(identifier.path);
		if (i == 0) {
			i = this.namespace.compareTo(identifier.namespace);
		}

		return i;
	}

	public static Identifier fromCommandInput(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && isCharValid(stringReader.peek())) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());

		try {
			return new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			stringReader.setCursor(i);
			throw COMMAND_EXCEPTION.createWithContext(stringReader);
		}
	}

	public static boolean isCharValid(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
	}

	private static boolean isPathValid(String string) {
		return string.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 47 || i == 46);
	}

	private static boolean isNamespaceValid(String string) {
		return string.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 46);
	}

	@Environment(EnvType.CLIENT)
	public static boolean isValid(String string) {
		String[] strings = split(string, ':');
		return isNamespaceValid(StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0]) && isPathValid(strings[1]);
	}

	public static class Serializer implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
		public Identifier method_12840(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new Identifier(JsonHelper.asString(jsonElement, "location"));
		}

		public JsonElement method_12839(Identifier identifier, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(identifier.toString());
		}
	}
}
