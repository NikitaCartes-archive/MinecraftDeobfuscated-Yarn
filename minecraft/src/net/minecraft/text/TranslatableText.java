package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.class_7417;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

public class TranslatableText implements class_7417 {
	private static final Object[] EMPTY_ARGUMENTS = new Object[0];
	private static final StringVisitable LITERAL_PERCENT_SIGN = StringVisitable.plain("%");
	private static final StringVisitable NULL_ARGUMENT = StringVisitable.plain("null");
	private final String key;
	private final Object[] args;
	@Nullable
	private Language languageCache;
	private List<StringVisitable> translations = ImmutableList.of();
	private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public TranslatableText(String key) {
		this.key = key;
		this.args = EMPTY_ARGUMENTS;
	}

	public TranslatableText(String key, Object... args) {
		this.key = key;
		this.args = args;
	}

	private void updateTranslations() {
		Language language = Language.getInstance();
		if (language != this.languageCache) {
			this.languageCache = language;
			String string = language.get(this.key);

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
					if (m < this.args.length) {
						partsConsumer.accept(this.getArg(m));
					}
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
		if (index >= this.args.length) {
			throw new TranslationException(this, index);
		} else {
			Object object = this.args[index];
			if (object instanceof Text) {
				return (Text)object;
			} else {
				return object == null ? NULL_ARGUMENT : StringVisitable.plain(object.toString());
			}
		}
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		this.updateTranslations();

		for (StringVisitable stringVisitable : this.translations) {
			Optional<T> optional = stringVisitable.visit(styledVisitor, style);
			if (optional.isPresent()) {
				return optional;
			}
		}

		return Optional.empty();
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
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
	public MutableText parse(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity, int i) throws CommandSyntaxException {
		Object[] objects = new Object[this.args.length];

		for (int j = 0; j < objects.length; j++) {
			Object object = this.args[j];
			if (object instanceof Text) {
				objects[j] = Texts.parse(serverCommandSource, (Text)object, entity, i);
			} else {
				objects[j] = object;
			}
		}

		return MutableText.method_43477(new TranslatableText(this.key, objects));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof TranslatableText translatableText && this.key.equals(translatableText.key) && Arrays.equals(this.args, translatableText.args)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		int i = super.hashCode();
		i = 31 * i + this.key.hashCode();
		return 31 * i + Arrays.hashCode(this.args);
	}

	public String toString() {
		return "translation{key='" + this.key + "', args=" + Arrays.toString(this.args) + "}";
	}

	public String getKey() {
		return this.key;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
