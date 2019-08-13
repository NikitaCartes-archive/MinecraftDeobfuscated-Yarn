package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindText extends BaseText {
	public static Function<String, Supplier<String>> i18n = string -> () -> string;
	private final String key;
	private Supplier<String> name;

	public KeybindText(String string) {
		this.key = string;
	}

	@Override
	public String asString() {
		if (this.name == null) {
			this.name = (Supplier<String>)i18n.apply(this.key);
		}

		return (String)this.name.get();
	}

	public KeybindText method_10902() {
		return new KeybindText(this.key);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof KeybindText)) {
			return false;
		} else {
			KeybindText keybindText = (KeybindText)object;
			return this.key.equals(keybindText.key) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.key + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}

	public String getKey() {
		return this.key;
	}
}
