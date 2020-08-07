package net.minecraft.text;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

public class TranslatableText extends BaseText implements ParsableText {
	private static final Object[] EMPTY_ARGUMENTS = new Object[0];
	private static final StringVisitable LITERAL_PERCENT_SIGN = StringVisitable.plain("%");
	private static final StringVisitable NULL_ARGUMENT = StringVisitable.plain("null");
	private final String key;
	private final Object[] args;
	@Nullable
	private Language languageCache;
	private final List<StringVisitable> translations = Lists.<StringVisitable>newArrayList();
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
			this.translations.clear();
			String string = language.get(this.key);

			try {
				this.setTranslation(string);
			} catch (TranslationException var4) {
				this.translations.clear();
				this.translations.add(StringVisitable.plain(string));
			}
		}
	}

	private void setTranslation(String translation) {
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

					this.translations.add(StringVisitable.plain(string));
				}

				String string = matcher.group(2);
				String string2 = translation.substring(k, l);
				if ("%".equals(string) && "%%".equals(string2)) {
					this.translations.add(LITERAL_PERCENT_SIGN);
				} else {
					if (!"s".equals(string)) {
						throw new TranslationException(this, "Unsupported format: '" + string2 + "'");
					}

					String string3 = matcher.group(1);
					int m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
					if (m < this.args.length) {
						this.translations.add(this.getArg(m));
					}
				}

				j = l;
			}

			if (j < translation.length()) {
				String string4 = translation.substring(j);
				if (string4.indexOf(37) != -1) {
					throw new IllegalArgumentException();
				}

				this.translations.add(StringVisitable.plain(string4));
			}
		} catch (IllegalArgumentException var11) {
			throw new TranslationException(this, var11);
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

	public TranslatableText method_11020() {
		return new TranslatableText(this.key, this.args);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> visitor, Style style) {
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

		return new TranslatableText(this.key, objects);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof TranslatableText)) {
			return false;
		} else {
			TranslatableText translatableText = (TranslatableText)object;
			return Arrays.equals(this.args, translatableText.args) && this.key.equals(translatableText.key) && super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		int i = super.hashCode();
		i = 31 * i + this.key.hashCode();
		return 31 * i + Arrays.hashCode(this.args);
	}

	@Override
	public String toString() {
		return "TranslatableComponent{key='"
			+ this.key
			+ '\''
			+ ", args="
			+ Arrays.toString(this.args)
			+ ", siblings="
			+ this.siblings
			+ ", style="
			+ this.getStyle()
			+ '}';
	}

	public String getKey() {
		return this.key;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
