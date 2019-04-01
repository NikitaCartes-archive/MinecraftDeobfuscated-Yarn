package net.minecraft;

import java.util.function.Function;
import java.util.function.Supplier;

public class class_2572 extends class_2554 {
	public static Function<String, Supplier<String>> field_11766 = string -> () -> string;
	private final String field_11767;
	private Supplier<String> field_11768;

	public class_2572(String string) {
		this.field_11767 = string;
	}

	@Override
	public String method_10851() {
		if (this.field_11768 == null) {
			this.field_11768 = (Supplier<String>)field_11766.apply(this.field_11767);
		}

		return (String)this.field_11768.get();
	}

	public class_2572 method_10902() {
		return new class_2572(this.field_11767);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2572)) {
			return false;
		} else {
			class_2572 lv = (class_2572)object;
			return this.field_11767.equals(lv.field_11767) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "KeybindComponent{keybind='" + this.field_11767 + '\'' + ", siblings=" + this.field_11729 + ", style=" + this.method_10866() + '}';
	}

	public String method_10901() {
		return this.field_11767;
	}
}
