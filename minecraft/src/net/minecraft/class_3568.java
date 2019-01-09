package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3568 implements class_3565 {
	@Nullable
	private final class_3558<?, ?> field_15814;
	@Nullable
	private final class_3558<?, ?> field_15813;

	public class_3568(class_2823 arg, boolean bl, boolean bl2) {
		this.field_15814 = bl ? new class_3552(arg) : null;
		this.field_15813 = bl2 ? new class_3572(arg) : null;
	}

	public void method_15559(class_2338 arg) {
		if (this.field_15814 != null) {
			this.field_15814.method_15513(arg);
		}

		if (this.field_15813 != null) {
			this.field_15813.method_15513(arg);
		}
	}

	public void method_15560(class_2338 arg, int i) {
		if (this.field_15814 != null) {
			this.field_15814.method_15514(arg, i);
		}
	}

	public boolean method_15561() {
		return this.field_15813 != null && this.field_15813.method_15518() ? true : this.field_15814 != null && this.field_15814.method_15518();
	}

	public int method_15563(int i, boolean bl, boolean bl2) {
		if (this.field_15814 != null && this.field_15813 != null) {
			int j = i / 2;
			int k = this.field_15814.method_15516(j, bl, bl2);
			int l = i - j + k;
			int m = this.field_15813.method_15516(l, bl, bl2);
			return k == 0 && m > 0 ? this.field_15814.method_15516(m, bl, bl2) : m;
		} else if (this.field_15814 != null) {
			return this.field_15814.method_15516(i, bl, bl2);
		} else {
			return this.field_15813 != null ? this.field_15813.method_15516(i, bl, bl2) : i;
		}
	}

	@Override
	public void method_15551(int i, int j, int k, boolean bl) {
		if (this.field_15814 != null) {
			this.field_15814.method_15551(i, j, k, bl);
		}

		if (this.field_15813 != null) {
			this.field_15813.method_15551(i, j, k, bl);
		}
	}

	public void method_15557(int i, int j, boolean bl) {
		if (this.field_15814 != null) {
			this.field_15814.method_15512(i, j, bl);
		}

		if (this.field_15813 != null) {
			this.field_15813.method_15512(i, j, bl);
		}
	}

	public class_3562 method_15562(class_1944 arg) {
		if (arg == class_1944.field_9282) {
			return (class_3562)(this.field_15814 == null ? class_3562.class_3563.field_15812 : this.field_15814);
		} else {
			return (class_3562)(this.field_15813 == null ? class_3562.class_3563.field_15812 : this.field_15813);
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_15564(class_1944 arg, class_2338 arg2) {
		if (arg == class_1944.field_9282) {
			if (this.field_15814 != null) {
				return this.field_15814.method_15520(class_2338.method_10090(arg2.method_10063()));
			}
		} else if (this.field_15813 != null) {
			return this.field_15813.method_15520(class_2338.method_10090(arg2.method_10063()));
		}

		return "n/a";
	}

	public void method_15558(class_1944 arg, int i, int j, int k, class_2804 arg2) {
		if (arg == class_1944.field_9282) {
			if (this.field_15814 != null) {
				this.field_15814.method_15515(i, j, k, arg2);
			}
		} else if (this.field_15813 != null) {
			this.field_15813.method_15515(i, j, k, arg2);
		}
	}
}
