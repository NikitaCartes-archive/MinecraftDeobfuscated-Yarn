package net.minecraft;

import javax.annotation.Nullable;

public interface class_3726 {
	class_3726 field_16449 = new class_3727(false, -Double.MAX_VALUE);

	static class_3726 method_16194() {
		return field_16449;
	}

	static class_3726 method_16195(@Nullable class_1297 arg) {
		return (class_3726)(arg == null ? method_16194() : new class_3727(arg));
	}

	boolean method_16193();

	boolean method_16192(class_265 arg, class_2338 arg2);
}
