package net.minecraft;

public class class_2585 extends class_2554 {
	private final String field_11862;

	public class_2585(String string) {
		this.field_11862 = string;
	}

	public String method_10993() {
		return this.field_11862;
	}

	@Override
	public String method_10851() {
		return this.field_11862;
	}

	public class_2585 method_10992() {
		return new class_2585(this.field_11862);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_2585)) {
			return false;
		} else {
			class_2585 lv = (class_2585)object;
			return this.field_11862.equals(lv.method_10993()) && super.equals(object);
		}
	}

	@Override
	public String toString() {
		return "TextComponent{text='" + this.field_11862 + '\'' + ", siblings=" + this.field_11729 + ", style=" + this.method_10866() + '}';
	}
}
