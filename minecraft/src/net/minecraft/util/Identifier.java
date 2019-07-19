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
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

/**
 * The namespace and path must contain only lowercase letters ([a-z]), digits ([0-9]), or the characters '_', '.', and '-'. The path can also contain the standard path separator '/'.
 */
public class Identifier implements Comparable<Identifier> {
	private static final SimpleCommandExceptionType COMMAND_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.id.invalid"));
	protected final String namespace;
	protected final String path;

	protected Identifier(String[] id) {
		this.namespace = StringUtils.isEmpty(id[0]) ? "minecraft" : id[0];
		this.path = id[1];
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

	public static Identifier splitOn(String id, char delimiter) {
		return new Identifier(split(id, delimiter));
	}

	@Nullable
	public static Identifier tryParse(String id) {
		try {
			return new Identifier(id);
		} catch (InvalidIdentifierException var2) {
			return null;
		}
	}

	protected static String[] split(String id, char delimiter) {
		String[] strings = new String[]{"minecraft", id};
		int i = id.indexOf(delimiter);
		if (i >= 0) {
			strings[1] = id.substring(i + 1, id.length());
			if (i >= 1) {
				strings[0] = id.substring(0, i);
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

	public int compareTo(Identifier identifier) {
		int i = this.path.compareTo(identifier.path);
		if (i == 0) {
			i = this.namespace.compareTo(identifier.namespace);
		}

		return i;
	}

	public static Identifier fromCommandInput(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && isCharValid(reader.peek())) {
			reader.skip();
		}

		String string = reader.getString().substring(i, reader.getCursor());

		try {
			return new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			reader.setCursor(i);
			throw COMMAND_EXCEPTION.createWithContext(reader);
		}
	}

	public static boolean isCharValid(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
	}

	private static boolean isPathValid(String path) {
		return path.chars().allMatch(c -> c == 95 || c == 45 || c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 47 || c == 46);
	}

	private static boolean isNamespaceValid(String namespace) {
		return namespace.chars().allMatch(c -> c == 95 || c == 45 || c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 46);
	}

	@Environment(EnvType.CLIENT)
	public static boolean isValid(String id) {
		String[] strings = split(id, ':');
		return isNamespaceValid(StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0]) && isPathValid(strings[1]);
	}

	public static class Serializer implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
		public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new Identifier(JsonHelper.asString(jsonElement, "location"));
		}

		public JsonElement serialize(Identifier identifier, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(identifier.toString());
		}
	}
}
