package net.minecraft.command.argument.packrat;

public record Symbol<T>(String name) {
	public String toString() {
		return "<" + this.name + ">";
	}

	public static <T> Symbol<T> of(String name) {
		return new Symbol<>(name);
	}
}
