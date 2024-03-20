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
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

/**
 * An identifier used to identify things. This is also known as "resource location",
 * "namespaced ID", "location", or just "ID". This is a non-typed immutable object,
 * and identifies things using a combination of namespace and path. Identifiers should
 * always be compared using {@link #equals} method, not {@code ==}.
 * 
 * <h2 id="format">Format</h2>
 * <p>Identifiers are formatted as {@code <namespace>:<path>}. If the namespace and colon
 * are omitted, the namespace defaults to {@value #DEFAULT_NAMESPACE}.
 * 
 * <p><strong>The namespace and path must contain only ASCII lowercase letters ({@code
 * [a-z]}), ASCII digits ({@code [0-9]}), or the characters {@code _}, {@code .}, and
 * {@code -}. </strong> The path can also contain the standard path separator {@code
 * /}. Uppercase letters cannot be used. {@link #isValid} can be used to check whether a
 * string is a valid identifier. When handling externally provided identifiers, it should
 * either validate or use {@link #tryParse} instead of the constructor. Another common
 * mistake is using a formatted string with {@code %d} or {@code %f} to construct an
 * identifier without specifying the locate explicitly, as they are not guaranteed to be
 * ASCII digits in certain locales. Use {@link String#format(Locale, String, Object[])}
 * with {@link java.util.Locale#ROOT} instead of {@link String#formatted}.
 * 
 * <h3 id="namespace">Namespace</h3>
 * <p>The <strong>namespace</strong> of an identifier identifies the origin of the thing.
 * For example, two mods to the game could both add an item with the ID "orange";
 * the namespace is used to differentiate the two. (The convention is to use the ID
 * assigned to the mod as the namespace.)
 * 
 * <p>A namespace only determines the source of an identifier, and does not determine its purpose; so long as
 * two identifiers are used for different purposes, they can share the namespace and path.
 * For example, the identifier {@code minecraft:dirt} is shared by blocks and items.
 * There is no need to change the identifier to, say, {@code minecraft_block:dirt} or
 * {@code minecraft_item:dirt}.
 * 
 * <p>Several namespaces are reserved for vanilla use. While those identifiers can be used for
 * referencing and overwriting vanilla things, it is highly discouraged to use them to
 * identify your own, new things. For example, a modded block or a new biome added by
 * data packs should not use the reserved namespaces, but it's fine to use them when
 * modifying an existing biome under that namespace. The reserved namespaces are
 * {@value #DEFAULT_NAMESPACE}, {@code brigadier}, and {@value #REALMS_NAMESPACE}.
 * {@value #DEFAULT_NAMESPACE} is also the default namespace used when no namespace is
 * provided.
 * 
 * <h3 id="path">Path</h3>
 * <p>The path of the identifier identifies the thing within the namespace, such as
 * between different items from the same mod. Additionally, this is sometimes used to
 * refer to a file path, such as in textures.
 * 
 * <h2 id="Creation">Creation</h2>
 * <p>There are many ways to create a new identifier:
 * 
 * <ul>
 * <li>{@link Identifier(String)} creates an identifier from a string in
 * {@code <namespace>:<path>} format. If the colon is missing, the created identifier
 * has the namespace {@value #DEFAULT_NAMESPACE} and the argument is used as the path.
 * When passed an invalid value, this throws {@link InvalidIdentifierException}.</li>
 * <li>{@link Identifier(String, String)} creates an identifier from namespace and path.
 * When passed an invalid value, this throws {@link InvalidIdentifierException}.</li>
 * <li>{@link #tryParse} creates an identifier from a string in
 * {@code <namespace>:<path>} format. If the colon is missing, the created identifier
 * has the namespace {@value #DEFAULT_NAMESPACE} and the argument is used as the path.
 * When passed an invalid value, this returns {@code null}.</li>
 * <li>{@link #of} creates an identifier from namespace and path.
 * When passed an invalid value, this returns {@code null}.</li>
 * <li>{@link #fromCommandInput} reads an identifier from command input reader.
 * When an invalid value is read, this throws {@link #COMMAND_EXCEPTION}.</li>
 * <li>{@link Identifier.Serializer} is a serializer for Gson.</li>
 * <li>{@link #CODEC} can be used to serialize and deserialize an identifier using
 * DataFixerUpper.</li>
 * </ul>
 * 
 * <h2 id="using">Using Identifier</h2>
 * <p>Identifiers identify several objects in the game. {@link
 * net.minecraft.registry.Registry} holds objects, such as blocks and items, that are
 * identified by an identifier. Textures are also identified using an identifier; such
 * an identifier is represented as a file path with an extension, such as {@code
 * minecraft:textures/entity/pig/pig.png}.
 * 
 * <p>The string representation of the identifier ({@code <namespace>:<path>}) can be
 * obtained by calling {@link #toString}. This always includes the namespace. An identifier
 * can be converted to a translation key using {@link #toTranslationKey(String)} method.
 * 
 * <h3 id="registrykey">RegistryKey</h3>
 * <p>Identifier is not type-aware; {@code minecraft:tnt} could refer to a TNT block, a TNT
 * item, or a TNT entity. To identify a registered object uniquely, {@link
 * net.minecraft.registry.RegistryKey} can be used. A registry key is a combination
 * of the registry's identifier and the object's identifier.
 */
public class Identifier implements Comparable<Identifier> {
	public static final Codec<Identifier> CODEC = Codec.STRING.<Identifier>comapFlatMap(Identifier::validate, Identifier::toString).stable();
	public static final PacketCodec<ByteBuf, Identifier> PACKET_CODEC = PacketCodecs.STRING.xmap(Identifier::new, Identifier::toString);
	public static final SimpleCommandExceptionType COMMAND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.id.invalid"));
	public static final char NAMESPACE_SEPARATOR = ':';
	public static final String DEFAULT_NAMESPACE = "minecraft";
	public static final String REALMS_NAMESPACE = "realms";
	private final String namespace;
	private final String path;

	protected Identifier(String namespace, String path, @Nullable Identifier.ExtraData extraData) {
		this.namespace = namespace;
		this.path = path;
	}

	public Identifier(String namespace, String path) {
		this(validateNamespace(namespace, path), validatePath(namespace, path), null);
	}

	private Identifier(String[] id) {
		this(id[0], id[1]);
	}

	/**
	 * <p>Takes a string of the form {@code <namespace>:<path>} or {@code <path>} which will use the default namespace, for example {@code minecraft:iron_ingot} or {@code iron_ingot}.
	 * <p>The string will be split (on the {@code :}) into an identifier with the specified path and namespace.
	 * Prefer using the {@link net.minecraft.util.Identifier#Identifier(java.lang.String, java.lang.String) Identifier(java.lang.String, java.lang.String)} constructor that takes the namespace and path as individual parameters to avoid mistakes.
	 * <p>If there is no colon in the given string argument, the namespace will be set to {@value #DEFAULT_NAMESPACE}, and the path will be the given argument.
	 * If the colon is the first character of the given string argument (i.e. the namespace is empty), the namespace will also be set to {@value #DEFAULT_NAMESPACE}, and the path will be the given argument without the initial colon.
	 * @throws InvalidIdentifierException if the string cannot be parsed as an identifier.
	 */
	public Identifier(String id) {
		this(split(id, ':'));
	}

	public static Identifier splitOn(String id, char delimiter) {
		return new Identifier(split(id, delimiter));
	}

	/**
	 * {@return {@code id} parsed as an identifier, or {@code null} if it cannot be parsed}
	 * 
	 * @see #of
	 */
	@Nullable
	public static Identifier tryParse(String id) {
		try {
			return new Identifier(id);
		} catch (InvalidIdentifierException var2) {
			return null;
		}
	}

	/**
	 * {@return an identifier from the provided {@code namespace} and {@code path}, or
	 * {@code null} if either argument is invalid}
	 * 
	 * @see #tryParse
	 */
	@Nullable
	public static Identifier of(String namespace, String path) {
		try {
			return new Identifier(namespace, path);
		} catch (InvalidIdentifierException var3) {
			return null;
		}
	}

	/**
	 * Splits the {@code id} into an array of two strings at the first occurrence of {@code delimiter}, excluding the delimiter character, or uses {@value #DEFAULT_NAMESPACE} for the first string in the resulting array when the deliminator does not exist or is the first character.
	 */
	protected static String[] split(String id, char delimiter) {
		String[] strings = new String[]{"minecraft", id};
		int i = id.indexOf(delimiter);
		if (i >= 0) {
			strings[1] = id.substring(i + 1);
			if (i >= 1) {
				strings[0] = id.substring(0, i);
			}
		}

		return strings;
	}

	public static DataResult<Identifier> validate(String id) {
		try {
			return DataResult.success(new Identifier(id));
		} catch (InvalidIdentifierException var2) {
			return DataResult.error(() -> "Not a valid resource location: " + id + " " + var2.getMessage());
		}
	}

	/**
	 * {@return the path of the identifier}
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * {@return the namespace of the identifier}
	 * 
	 * <p>This returns {@value #DEFAULT_NAMESPACE} for identifiers created without a namespace.
	 */
	public String getNamespace() {
		return this.namespace;
	}

	public Identifier withPath(String path) {
		return new Identifier(this.namespace, validatePath(this.namespace, path), null);
	}

	public Identifier withPath(UnaryOperator<String> pathFunction) {
		return this.withPath((String)pathFunction.apply(this.path));
	}

	public Identifier withPrefixedPath(String prefix) {
		return this.withPath(prefix + this.path);
	}

	public Identifier withSuffixedPath(String suffix) {
		return this.withPath(this.path + suffix);
	}

	public String toString() {
		return this.namespace + ":" + this.path;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof Identifier identifier) ? false : this.namespace.equals(identifier.namespace) && this.path.equals(identifier.path);
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

	/**
	 * {@return the string representation of the identifier with slashes and colons replaced
	 * with underscores}
	 */
	public String toUnderscoreSeparatedString() {
		return this.toString().replace('/', '_').replace(':', '_');
	}

	/**
	 * {@return the long translation key, without omitting the default namespace}
	 */
	public String toTranslationKey() {
		return this.namespace + "." + this.path;
	}

	/**
	 * {@return the short translation key, with the default namespace omitted if present}
	 */
	public String toShortTranslationKey() {
		return this.namespace.equals("minecraft") ? this.path : this.toTranslationKey();
	}

	/**
	 * {@return the {@linkplain #toTranslationKey() long translation key} prefixed with
	 * {@code prefix} and a dot}
	 */
	public String toTranslationKey(String prefix) {
		return prefix + "." + this.toTranslationKey();
	}

	/**
	 * {@return the {@linkplain #toTranslationKey() long translation key} prefixed with
	 * {@code prefix} and a dot, and suffixed with a dot and {@code suffix}}
	 */
	public String toTranslationKey(String prefix, String suffix) {
		return prefix + "." + this.toTranslationKey() + "." + suffix;
	}

	private static String readString(StringReader reader) {
		int i = reader.getCursor();

		while (reader.canRead() && isCharValid(reader.peek())) {
			reader.skip();
		}

		return reader.getString().substring(i, reader.getCursor());
	}

	public static Identifier fromCommandInput(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		String string = readString(reader);

		try {
			return new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			reader.setCursor(i);
			throw COMMAND_EXCEPTION.createWithContext(reader);
		}
	}

	public static Identifier fromCommandInputNonEmpty(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		String string = readString(reader);
		if (string.isEmpty()) {
			throw COMMAND_EXCEPTION.createWithContext(reader);
		} else {
			try {
				return new Identifier(string);
			} catch (InvalidIdentifierException var4) {
				reader.setCursor(i);
				throw COMMAND_EXCEPTION.createWithContext(reader);
			}
		}
	}

	public static boolean isCharValid(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
	}

	/**
	 * {@return whether {@code path} can be used as an identifier's path}
	 */
	public static boolean isPathValid(String path) {
		for (int i = 0; i < path.length(); i++) {
			if (!isPathCharacterValid(path.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@return whether {@code namespace} can be used as an identifier's namespace}
	 */
	public static boolean isNamespaceValid(String namespace) {
		for (int i = 0; i < namespace.length(); i++) {
			if (!isNamespaceCharacterValid(namespace.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private static String validateNamespace(String namespace, String path) {
		if (!isNamespaceValid(namespace)) {
			throw new InvalidIdentifierException("Non [a-z0-9_.-] character in namespace of location: " + namespace + ":" + path);
		} else {
			return namespace;
		}
	}

	/**
	 * {@return whether {@code character} is valid for use in identifier paths}
	 */
	public static boolean isPathCharacterValid(char character) {
		return character == '_'
			|| character == '-'
			|| character >= 'a' && character <= 'z'
			|| character >= '0' && character <= '9'
			|| character == '/'
			|| character == '.';
	}

	/**
	 * {@return whether {@code character} is valid for use in identifier namespaces}
	 */
	private static boolean isNamespaceCharacterValid(char character) {
		return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
	}

	/**
	 * {@return whether {@code id} can be parsed as an identifier}
	 */
	public static boolean isValid(String id) {
		String[] strings = split(id, ':');
		return isNamespaceValid(StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0]) && isPathValid(strings[1]);
	}

	private static String validatePath(String namespace, String path) {
		if (!isPathValid(path)) {
			throw new InvalidIdentifierException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
		} else {
			return path;
		}
	}

	/**
	 * A piece of extra data that a subclass may attach to an identifier. This is
	 * not used by {@link Identifier} itself.
	 */
	protected interface ExtraData {
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
