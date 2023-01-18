package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class TranslatableTextContent implements TextContent {
	public static final Object[] EMPTY_ARGUMENTS = new Object[0];
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

	public TranslatableTextContent(String key, @Nullable String fallback, Object[] args) {
		this.key = key;
		this.fallback = fallback;
		this.args = args;
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
			if (object instanceof Text) {
				objects[i] = Texts.parse(source, (Text)object, sender, depth);
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
