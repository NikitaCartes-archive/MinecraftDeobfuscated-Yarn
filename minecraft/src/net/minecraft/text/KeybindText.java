package net.minecraft.text;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.class_7417;
import net.minecraft.class_7420;

public class KeybindText implements class_7417 {
	private final String key;
	@Nullable
	private Supplier<Text> translated;

	public KeybindText(String key) {
		this.key = key;
	}

	private Text getTranslated() {
		if (this.translated == null) {
			this.translated = (Supplier<Text>)class_7420.field_39013.apply(this.key);
		}

		return (Text)this.translated.get();
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
		return this.getTranslated().visit(visitor);
	}

	@Override
	public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		return this.getTranslated().visit(styledVisitor, style);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof KeybindText keybindText && this.key.equals(keybindText.key)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.key.hashCode();
	}

	public String toString() {
		return "keybind{" + this.key + "}";
	}

	public String getKey() {
		return this.key;
	}
}
