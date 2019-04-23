package net.minecraft.network.chat;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindComponent extends BaseComponent {
	public static Function<String, Supplier<String>> field_11766 = string -> () -> string;
	private final String keybind;
	private Supplier<String> field_11768;

	public KeybindComponent(String string) {
		this.keybind = string;
	}

	@Override
	public String getText() {
		if (this.field_11768 == null) {
			this.field_11768 = (Supplier<String>)field_11766.apply(this.keybind);
		}

		return (String)this.field_11768.get();
	}

	public KeybindComponent method_10902() {
		return new KeybindComponent(this.keybind);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof KeybindComponent)) {
			return false;
		} else {
			KeybindComponent keybindComponent = (KeybindComponent)object;
			return this.keybind.equals(keybindComponent.keybind) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
	}

	public String getKeybind() {
		return this.keybind;
	}
}
