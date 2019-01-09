package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2710 implements Predicate<class_2680> {
	private static final class_2710 field_12404 = new class_2710(class_3614.field_15959) {
		@Override
		public boolean method_11745(@Nullable class_2680 arg) {
			return arg != null && arg.method_11588();
		}
	};
	private final class_3614 field_12405;

	private class_2710(class_3614 arg) {
		this.field_12405 = arg;
	}

	public static class_2710 method_11746(class_3614 arg) {
		return arg == class_3614.field_15959 ? field_12404 : new class_2710(arg);
	}

	public boolean method_11745(@Nullable class_2680 arg) {
		return arg != null && arg.method_11620() == this.field_12405;
	}
}
