package net.minecraft.text;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindText extends BaseText {
	private static Function<String, Supplier<Text>> translator = key -> () -> new LiteralText(key);
	private final String key;
	private Supplier<Text> translated;

	public KeybindText(String key) {
		this.key = key;
	}

	public static void setTranslator(Function<String, Supplier<Text>> translator) {
		KeybindText.translator = translator;
	}

	private Text getTranslated() {
		if (this.translated == null) {
			this.translated = (Supplier<Text>)translator.apply(this.key);
		}

		return (Text)this.translated.get();
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return this.getTranslated().visit(visitor);
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return this.getTranslated().visit(visitor, style);
	}

	public KeybindText copy() {
		return new KeybindText(this.key);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			return !(object instanceof KeybindText keybindText) ? false : this.key.equals(keybindText.key) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.key + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
	}

	public String getKey() {
		return this.key;
	}
}
