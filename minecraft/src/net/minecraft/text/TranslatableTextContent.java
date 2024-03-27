package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;
import net.minecraft.util.dynamic.Codecs;

public class TranslatableTextContent implements TextContent {
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];
	private static final Codec<Object> OBJECT_ARGUMENT_CODEC = Codecs.BASIC_OBJECT.validate(TranslatableTextContent::validate);
	private static final Codec<Object> ARGUMENT_CODEC = Codec.either(OBJECT_ARGUMENT_CODEC, TextCodecs.CODEC)
		.xmap(
			either -> either.map(object -> object, text -> Objects.requireNonNullElse(text.getLiteralString(), text)),
			argument -> argument instanceof Text text ? Either.right(text) : Either.left(argument)
		);
	public static final MapCodec<TranslatableTextContent> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.STRING.fieldOf("translate").forGetter(content -> content.key),
					Codec.STRING.lenientOptionalFieldOf("fallback").forGetter(content -> Optional.ofNullable(content.fallback)),
					ARGUMENT_CODEC.listOf().optionalFieldOf("with").forGetter(content -> toOptionalList(content.args))
				)
				.apply(instance, TranslatableTextContent::of)
	);
	public static final TextContent.Type<TranslatableTextContent> TYPE = new TextContent.Type<>(CODEC, "translatable");
	private static final StringVisitable LITERAL_PERCENT_SIGN = StringVisitable.plain("%");
	private static final StringVisitable NULL_ARGUMENT = StringVisitable.plain("null");
	private final String key;
	@Nullable
	private final String fallback;
	private final Object[] args;
	@Nullable
	private Language languageCache;
	private List<StringVisitable> translations = ImmutableList.of();
	private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	private static DataResult<Object> validate(@Nullable Object object) {
		return !isPrimitive(object) ? DataResult.error(() -> "This value needs to be parsed as component") : DataResult.success(object);
	}

	/**
	 * {@return whether {@code argument} is primitive}
	 * 
	 * <p>Primitives include numbers, booleans, and strings. These (along with {@code Text})
	 * can be used as translatable text arguments; others need to be converted first.
	 */
	public static boolean isPrimitive(@Nullable Object argument) {
		return argument instanceof Number || argument instanceof Boolean || argument instanceof String;
	}

	private static Optional<List<Object>> toOptionalList(Object[] args) {
		return args.length == 0 ? Optional.empty() : Optional.of(Arrays.asList(args));
	}

	private static Object[] toArray(Optional<List<Object>> args) {
		return (Object[])args.map(list -> list.isEmpty() ? EMPTY_ARGUMENTS : list.toArray()).orElse(EMPTY_ARGUMENTS);
	}

	private static TranslatableTextContent of(String key, Optional<String> fallback, Optional<List<Object>> args) {
		return new TranslatableTextContent(key, (String)fallback.orElse(null), toArray(args));
	}

	public TranslatableTextContent(String key, @Nullable String fallback, Object[] args) {
		this.key = key;
		this.fallback = fallback;
		this.args = args;
	}

	@Override
	public TextContent.Type<?> getType() {
		return TYPE;
	}

	private void updateTranslations() {
		Language language = Language.getInstance();
		if (language != this.languageCache) {
			this.languageCache = language;
			String string = this.fallback != null ? language.get(this.key, this.fallback) : language.get(this.key);

			try {
				Builder<StringVisitable> builder = ImmutableList.builder();
				this.forEachPart(string, builder::add);
				this.translations = builder.build();
			} catch (TranslationException var4) {
				this.translations = ImmutableList.of(StringVisitable.plain(string));
			}
		}
	}

	private void forEachPart(String translation, Consumer<StringVisitable> partsConsumer) {
		Matcher matcher = ARG_FORMAT.matcher(translation);

		try {
			int i = 0;
			int j = 0;

			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					String string = translation.substring(j, k);
					if (string.indexOf(37) != -1) {
						throw new IllegalArgumentException();
					}

					partsConsumer.accept(StringVisitable.plain(string));
				}

				String string = matcher.group(2);
				String string2 = translation.substring(k, l);
				if ("%".equals(string) && "%%".equals(string2)) {
					partsConsumer.accept(LITERAL_PERCENT_SIGN);
				} else {
					if (!"s".equals(string)) {
						throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
					}

					String string3 = matcher.group(1);
					int m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
					partsConsumer.accept(this.getArg(m));
				}

				j = l;
			}

			if (j < translation.length()) {
				String string4 = translation.substring(j);
				if (string4.indexOf(37) != -1) {
					throw new IllegalArgumentException();
				}

				partsConsumer.accept(StringVisitable.plain(string4));
			}
		} catch (IllegalArgumentException var12) {
			throw new TranslationException(this, var12);
		}
	}

	private StringVisitable getArg(int index) {
		if (index >= 0 && index < this.args.length) {
			Object object = this.args[index];
			if (object instanceof Text) {
				return (Text)object;
			} else {
				return object == null ? NULL_ARGUMENT : StringVisitable.plain(object.toString());
			}
		} else {
			throw new TranslationException(this, index);
		}
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		this.updateTranslations();

		for (StringVisitable stringVisitable : this.translations) {
			Optional<T> optional = stringVisitable.visit(visitor, style);
			if (optional.isPresent()) {
				return optional;
			}
		}

		return Optional.empty();
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		this.updateTranslations();

		for (StringVisitable stringVisitable : this.translations) {
			Optional<T> optional = stringVisitable.visit(visitor);
			if (optional.isPresent()) {
				return optional;
			}
		}

		return Optional.empty();
	}

	@Override
	public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
		Object[] objects = new Object[this.args.length];

		for (int i = 0; i < objects.length; i++) {
			Object object = this.args[i];
			if (object instanceof Text text) {
				objects[i] = Texts.parse(source, text, sender, depth);
			} else {
				objects[i] = object;
			}
		}

		return MutableText.of(new TranslatableTextContent(this.key, this.fallback, objects));
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof TranslatableTextContent translatableTextContent
				&& Objects.equals(this.key, translatableTextContent.key)
				&& Objects.equals(this.fallback, translatableTextContent.fallback)
				&& Arrays.equals(this.args, translatableTextContent.args)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		int i = Objects.hashCode(this.key);
		i = 31 * i + Objects.hashCode(this.fallback);
		return 31 * i + Arrays.hashCode(this.args);
	}

	public String toString() {
		return "translation{key='"
			+ this.key
			+ "'"
			+ (this.fallback != null ? ", fallback='" + this.fallback + "'" : "")
			+ ", args="
			+ Arrays.toString(this.args)
			+ "}";
	}

	public String getKey() {
		return this.key;
	}

	@Nullable
	public String getFallback() {
		return this.fallback;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
