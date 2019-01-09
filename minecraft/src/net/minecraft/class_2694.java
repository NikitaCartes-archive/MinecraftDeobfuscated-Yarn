package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_2694 {
	private final class_1941 field_12330;
	private final class_2338 field_12331;
	private final boolean field_12329;
	private class_2680 field_12326;
	private class_2586 field_12327;
	private boolean field_12328;

	public class_2694(class_1941 arg, class_2338 arg2, boolean bl) {
		this.field_12330 = arg;
		this.field_12331 = arg2;
		this.field_12329 = bl;
	}

	public class_2680 method_11681() {
		if (this.field_12326 == null && (this.field_12329 || this.field_12330.method_8591(this.field_12331))) {
			this.field_12326 = this.field_12330.method_8320(this.field_12331);
		}

		return this.field_12326;
	}

	@Nullable
	public class_2586 method_11680() {
		if (this.field_12327 == null && !this.field_12328) {
			this.field_12327 = this.field_12330.method_8321(this.field_12331);
			this.field_12328 = true;
		}

		return this.field_12327;
	}

	public class_1941 method_11679() {
		return this.field_12330;
	}

	public class_2338 method_11683() {
		return this.field_12331;
	}

	public static Predicate<class_2694> method_11678(Predicate<class_2680> predicate) {
		return arg -> arg != null && predicate.test(arg.method_11681());
	}
}
