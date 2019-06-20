package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2717 implements Predicate<class_2680> {
	private final class_2248 field_12423;

	public class_2717(class_2248 arg) {
		this.field_12423 = arg;
	}

	public static class_2717 method_11766(class_2248 arg) {
		return new class_2717(arg);
	}

	public boolean method_11765(@Nullable class_2680 arg) {
		return arg != null && arg.method_11614() == this.field_12423;
	}
}
