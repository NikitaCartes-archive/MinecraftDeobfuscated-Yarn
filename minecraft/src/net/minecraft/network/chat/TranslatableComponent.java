package net.minecraft.network.chat;

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
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Language;

public class TranslatableComponent extends BaseComponent implements ComponentWithSelectors {
	private static final Language EMPTY_LANGUAGE = new Language();
	private static final Language LANGUAGE = Language.getInstance();
	private final String key;
	private final Object[] params;
	private final Object lock = new Object();
	private long languageTimeLoaded = -1L;
	protected final List<Component> translatedText = Lists.<Component>newArrayList();
	public static final Pattern PARAM_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

	public TranslatableComponent(String string, Object... objects) {
		this.key = string;
		this.params = objects;

		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object instanceof Component) {
				Component component = ((Component)object).copy();
				this.params[i] = component;
				component.getStyle().setParent(this.getStyle());
			} else if (object == null) {
				this.params[i] = "null";
			}
		}
	}

	@VisibleForTesting
	synchronized void updateTranslation() {
		synchronized (this.lock) {
			long l = LANGUAGE.getTimeLoaded();
			if (l == this.languageTimeLoaded) {
				return;
			}

			this.languageTimeLoaded = l;
			this.translatedText.clear();
		}

		try {
			this.setTranslatedText(LANGUAGE.translate(this.key));
		} catch (TranslationException var6) {
			this.translatedText.clear();

			try {
				this.setTranslatedText(EMPTY_LANGUAGE.translate(this.key));
			} catch (TranslationException var5) {
				throw var6;
			}
		}
	}

	protected void setTranslatedText(String string) {
		Matcher matcher = PARAM_PATTERN.matcher(string);

		try {
			int i = 0;
			int j = 0;

			while (matcher.find(j)) {
				int k = matcher.start();
				int l = matcher.end();
				if (k > j) {
					Component component = new TextComponent(String.format(string.substring(j, k)));
					component.getStyle().setParent(this.getStyle());
					this.translatedText.add(component);
				}

				String string2 = matcher.group(2);
				String string3 = string.substring(k, l);
				if ("%".equals(string2) && "%%".equals(string3)) {
					Component component2 = new TextComponent("%");
					component2.getStyle().setParent(this.getStyle());
					this.translatedText.add(component2);
				} else {
					if (!"s".equals(string2)) {
						throw new TranslationException(this, "Unsupported format: '" + string3 + "'");
					}

					String string4 = matcher.group(1);
					int m = string4 != null ? Integer.parseInt(string4) - 1 : i++;
					if (m < this.params.length) {
						this.translatedText.add(this.getArgument(m));
					}
				}

				j = l;
			}

			if (j < string.length()) {
				Component component3 = new TextComponent(String.format(string.substring(j)));
				component3.getStyle().setParent(this.getStyle());
				this.translatedText.add(component3);
			}
		} catch (IllegalFormatException var11) {
			throw new TranslationException(this, var11);
		}
	}

	private Component getArgument(int i) {
		if (i >= this.params.length) {
			throw new TranslationException(this, i);
		} else {
			Object object = this.params[i];
			Component component;
			if (object instanceof Component) {
				component = (Component)object;
			} else {
				component = new TextComponent(object == null ? "null" : object.toString());
				component.getStyle().setParent(this.getStyle());
			}

			return component;
		}
	}

	@Override
	public Component setStyle(Style style) {
		super.setStyle(style);

		for (Object object : this.params) {
			if (object instanceof Component) {
				((Component)object).getStyle().setParent(this.getStyle());
			}
		}

		if (this.languageTimeLoaded > -1L) {
			for (Component component : this.translatedText) {
				component.getStyle().setParent(style);
			}
		}

		return this;
	}

	@Override
	public Stream<Component> stream() {
		this.updateTranslation();
		return Streams.concat(this.translatedText.stream(), this.siblings.stream()).flatMap(Component::stream);
	}

	@Override
	public String getText() {
		this.updateTranslation();
		StringBuilder stringBuilder = new StringBuilder();

		for (Component component : this.translatedText) {
			stringBuilder.append(component.getText());
		}

		return stringBuilder.toString();
	}

	public TranslatableComponent method_11020() {
		Object[] objects = new Object[this.params.length];

		for (int i = 0; i < this.params.length; i++) {
			if (this.params[i] instanceof Component) {
				objects[i] = ((Component)this.params[i]).copy();
			} else {
				objects[i] = this.params[i];
			}
		}

		return new TranslatableComponent(this.key, objects);
	}

	@Override
	public Component resolve(@Nullable ServerCommandSource serverCommandSource, @Nullable Entity entity) throws CommandSyntaxException {
		Object[] objects = new Object[this.params.length];

		for (int i = 0; i < objects.length; i++) {
			Object object = this.params[i];
			if (object instanceof Component) {
				objects[i] = Components.resolveAndStyle(serverCommandSource, (Component)object, entity);
			} else {
				objects[i] = object;
			}
		}

		return new TranslatableComponent(this.key, objects);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof TranslatableComponent)) {
			return false;
		} else {
			TranslatableComponent translatableComponent = (TranslatableComponent)object;
			return Arrays.equals(this.params, translatableComponent.params) && this.key.equals(translatableComponent.key) && super.equals(object);
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
			+ this.siblings
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
