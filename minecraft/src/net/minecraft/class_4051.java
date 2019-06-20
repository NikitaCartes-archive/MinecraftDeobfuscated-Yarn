package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class class_4051 {
	public static final class_4051 field_18092 = new class_4051();
	private double field_18093 = -1.0;
	private boolean field_18094;
	private boolean field_18095;
	private boolean field_18096;
	private boolean field_18097;
	private boolean field_18098 = true;
	private Predicate<class_1309> field_18099;

	public class_4051 method_18418(double d) {
		this.field_18093 = d;
		return this;
	}

	public class_4051 method_18417() {
		this.field_18094 = true;
		return this;
	}

	public class_4051 method_18421() {
		this.field_18095 = true;
		return this;
	}

	public class_4051 method_18422() {
		this.field_18096 = true;
		return this;
	}

	public class_4051 method_18423() {
		this.field_18097 = true;
		return this;
	}

	public class_4051 method_18424() {
		this.field_18098 = false;
		return this;
	}

	public class_4051 method_18420(@Nullable Predicate<class_1309> predicate) {
		this.field_18099 = predicate;
		return this;
	}

	public boolean method_18419(@Nullable class_1309 arg, class_1309 arg2) {
		if (arg == arg2) {
			return false;
		} else if (arg2.method_7325()) {
			return false;
		} else if (!arg2.method_5805()) {
			return false;
		} else if (!this.field_18094 && arg2.method_5655()) {
			return false;
		} else if (this.field_18099 != null && !this.field_18099.test(arg2)) {
			return false;
		} else {
			if (arg != null) {
				if (!this.field_18097) {
					if (!arg.method_18395(arg2)) {
						return false;
					}

					if (!arg.method_5973(arg2.method_5864())) {
						return false;
					}
				}

				if (!this.field_18095 && arg.method_5722(arg2)) {
					return false;
				}

				if (this.field_18093 > 0.0) {
					double d = this.field_18098 ? arg2.method_18390(arg) : 1.0;
					double e = this.field_18093 * d;
					double f = arg.method_5649(arg2.field_5987, arg2.field_6010, arg2.field_6035);
					if (f > e * e) {
						return false;
					}
				}

				if (!this.field_18096 && arg instanceof class_1308 && !((class_1308)arg).method_5985().method_6369(arg2)) {
					return false;
				}
			}

			return true;
		}
	}
}
