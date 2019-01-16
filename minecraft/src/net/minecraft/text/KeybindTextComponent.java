package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindTextComponent extends AbstractTextComponent {
	public static Function<String, Supplier<String>> field_11766 = string -> () -> string;
	private final String keybind;
	private Supplier<String> field_11768;

	public KeybindTextComponent(String string) {
		this.keybind = string;
	}

	@Override
	public String getText() {
		if (this.field_11768 == null) {
			this.field_11768 = (Supplier<String>)field_11766.apply(this.keybind);
		}

		return (String)this.field_11768.get();
	}

	public KeybindTextComponent copy() {
		return new KeybindTextComponent(this.keybind);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof KeybindTextComponent)) {
			return false;
		} else {
			KeybindTextComponent keybindTextComponent = (KeybindTextComponent)object;
			return this.keybind.equals(keybindTextComponent.keybind) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.children + ", style=" + this.getStyle() + '}';
	}

	public String getKeybind() {
		return this.keybind;
	}
}
