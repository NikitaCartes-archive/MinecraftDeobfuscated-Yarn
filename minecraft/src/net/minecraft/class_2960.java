package net.minecraft;

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
import org.apache.commons.lang3.StringUtils;

public class class_2960 implements Comparable<class_2960> {
	private static final SimpleCommandExceptionType field_13354 = new SimpleCommandExceptionType(new class_2588("argument.id.invalid"));
	protected final String field_13353;
	protected final String field_13355;

	protected class_2960(String[] strings) {
		this.field_13353 = StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0];
		this.field_13355 = strings[1];
		if (!method_20209(this.field_13353)) {
			throw new class_151("Non [a-z0-9_.-] character in namespace of location: " + this.field_13353 + ':' + this.field_13355);
		} else if (!method_20208(this.field_13355)) {
			throw new class_151("Non [a-z0-9/._-] character in path of location: " + this.field_13353 + ':' + this.field_13355);
		}
	}

	public class_2960(String string) {
		this(method_12830(string, ':'));
	}

	public class_2960(String string, String string2) {
		this(new String[]{string, string2});
	}

	public static class_2960 method_12838(String string, char c) {
		return new class_2960(method_12830(string, c));
	}

	@Nullable
	public static class_2960 method_12829(String string) {
		try {
			return new class_2960(string);
		} catch (class_151 var2) {
			return null;
		}
	}

	protected static String[] method_12830(String string, char c) {
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

	public String method_12832() {
		return this.field_13355;
	}

	public String method_12836() {
		return this.field_13353;
	}

	public String toString() {
		return this.field_13353 + ':' + this.field_13355;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2960)) {
			return false;
		} else {
			class_2960 lv = (class_2960)object;
			return this.field_13353.equals(lv.field_13353) && this.field_13355.equals(lv.field_13355);
		}
	}

	public int hashCode() {
		return 31 * this.field_13353.hashCode() + this.field_13355.hashCode();
	}

	public int method_12833(class_2960 arg) {
		int i = this.field_13355.compareTo(arg.field_13355);
		if (i == 0) {
			i = this.field_13353.compareTo(arg.field_13353);
		}

		return i;
	}

	public static class_2960 method_12835(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_12831(stringReader.peek())) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());

		try {
			return new class_2960(string);
		} catch (class_151 var4) {
			stringReader.setCursor(i);
			throw field_13354.createWithContext(stringReader);
		}
	}

	public static boolean method_12831(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
	}

	private static boolean method_20208(String string) {
		return string.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 47 || i == 46);
	}

	private static boolean method_20209(String string) {
		return string.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 46);
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_20207(String string) {
		String[] strings = method_12830(string, ':');
		return method_20209(StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0]) && method_20208(strings[1]);
	}

	public static class class_2961 implements JsonDeserializer<class_2960>, JsonSerializer<class_2960> {
		public class_2960 method_12840(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_2960(class_3518.method_15287(jsonElement, "location"));
		}

		public JsonElement method_12839(class_2960 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(arg.toString());
		}
	}
}
