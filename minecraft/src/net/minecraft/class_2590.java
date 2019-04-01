package net.minecraft;

public class class_2590 extends IllegalArgumentException {
	public class_2590(class_2588 arg, String string) {
		super(String.format("Error parsing: %s: %s", arg, string));
	}

	public class_2590(class_2588 arg, int i) {
		super(String.format("Invalid index %d requested for %s", i, arg));
	}

	public class_2590(class_2588 arg, Throwable throwable) {
		super(String.format("Error while parsing: %s", arg), throwable);
	}
}
