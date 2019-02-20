package net.minecraft.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_2566;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

public class TranslatableTextComponent extends AbstractTextComponent implements class_2566 {
	private static final Language EMPTY_LANGUAGE = new Language();
	private static final Language LANGUAGE = Language.getInstance();
	private final String key;
	private final Object[] params;
	private final Object field_11873 = new Object();
	private long field_11871 = -1L;
	protected final List<TextComponent> field_11877 = Lists.<TextComponent>newArrayList();
	public static final Pattern PARAM_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public TranslatableTextComponent(String string, Object... objects) {
		this.key = string;
		this.params = objects;

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof TextComponent) {
				TextComponent textComponent = ((TextComponent)object).copy();
				this.params[i] = textComponent;
				textComponent.getStyle().setParent(this.getStyle());
			} else if (object == null) {
				this.params[i] = "null";
			}
		}
	}

	@VisibleForTesting
	synchronized void method_11025() {
		synchronized (this.field_11873) {
			long l = LANGUAGE.getTimeLoaded();
			if (l == this.field_11871) {
				return;
			}

			this.field_11871 = l;
			this.field_11877.clear();
		}

		try {
			this.method_11024(LANGUAGE.translate(this.key));
		} catch (ComponentTranslationException var6) {
			this.field_11877.clear();

			try {
				this.method_11024(EMPTY_LANGUAGE.translate(this.key));
			} catch (ComponentTranslationException var5) {
				throw var6;
			}
		}
	}

	protected void method_11024(String string) {
		Matcher matcher = PARAM_PATTERN.matcher(string);

		try {
			int i = 0;
			int j = 0;

			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					TextComponent textComponent = new StringTextComponent(String.format(string.substring(j, k)));
					textComponent.getStyle().setParent(this.getStyle());
					this.field_11877.add(textComponent);
				}

				String string2 = matcher.group(2);
				String string3 = string.substring(k, l);
				if ("%".equals(string2) && "%%".equals(string3)) {
					TextComponent textComponent2 = new StringTextComponent("%");
					textComponent2.getStyle().setParent(this.getStyle());
					this.field_11877.add(textComponent2);
				} else {
					if (!"s".equals(string2)) {
						throw new ComponentTranslationException(this, "Unsupported format: '" + string3 + "'");
					}

					String string4 = matcher.group(1);
					int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;
					if (m < this.params.length) {
						this.field_11877.add(this.method_11021(m));
					}
				}

				j = l;
			}

			if (j < string.length()) {
				TextComponent textComponent3 = new StringTextComponent(String.format(string.substring(j)));
				textComponent3.getStyle().setParent(this.getStyle());
				this.field_11877.add(textComponent3);
			}
		} catch (IllegalFormatException var11) {
			throw new ComponentTranslationException(this, var11);
		}
	}

	private TextComponent method_11021(int i) {
		if (i >= this.params.length) {
			throw new ComponentTranslationException(this, i);
		} else {
			Object object = this.params[i];
			TextComponent textComponent;
			if (object instanceof TextComponent) {
				textComponent = (TextComponent)object;
			} else {
				textComponent = new StringTextComponent(object == null ? "null" : object.toString());
				textComponent.getStyle().setParent(this.getStyle());
			}

			return textComponent;
		}
	}

	@Override
	public TextComponent setStyle(Style style) {
		super.setStyle(style);

		for (Object object : this.params) {
			if (object instanceof TextComponent) {
				((TextComponent)object).getStyle().setParent(this.getStyle());
			}
		}

		if (this.field_11871 > -1L) {
			for (TextComponent textComponent : this.field_11877) {
				textComponent.getStyle().setParent(style);
			}
		}

		return this;
	}

	@Override
	public Stream<TextComponent> stream() {
		this.method_11025();
		return Streams.concat(this.field_11877.stream(), this.children.stream()).flatMap(TextComponent::stream);
	}

	@Override
	public String getText() {
		this.method_11025();
		StringBuilder stringBuilder = new StringBuilder();

		for (TextComponent textComponent : this.field_11877) {
			stringBuilder.append(textComponent.getText());
		}

		return stringBuilder.toString();
	}

	public TranslatableTextComponent method_11020() {
		Object[] objects = new Object[this.params.length];

		for (int i = 0; i < this.params.length; i++) {
			if (this.params[i] instanceof TextComponent) {
				objects[i] = ((TextComponent)this.params[i]).copy();
			} else {
				objects[i] = this.params[i];
			}
		}

		return new TranslatableTextComponent(this.key, objects);
	}

	@Override
	public TextComponent method_10890(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		Object[] objects = new Object[this.params.length];

		for (int i = 0; i < objects.length; i++) {
			Object object = this.params[i];
			if (object instanceof TextComponent) {
				objects[i] = TextFormatter.method_10881(serverCommandSource, (TextComponent)object, entity);
			} else {
				objects[i] = object;
			}
		}

		return new TranslatableTextComponent(this.key, objects);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof TranslatableTextComponent)) {
			return false;
		} else {
			TranslatableTextComponent translatableTextComponent = (TranslatableTextComponent)object;
			return Arrays.equals(this.params, translatableTextComponent.params) && this.key.equals(translatableTextComponent.key) && super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		int i = super.hashCode();
		i = 31 * i + this.key.hashCode();
		return 31 * i + Arrays.hashCode(this.params);
	}

	@Override
	public String toString() {
		return "TranslatableComponent{key='"
			+ this.key
			+ '\''
			+ ", args="
			+ Arrays.toString(this.params)
			+ ", siblings="
			+ this.children
			+ ", style="
			+ this.getStyle()
			+ '}';
	}

	public String getKey() {
		return this.key;
	}

	public Object[] getParams() {
		return this.params;
	}
}
