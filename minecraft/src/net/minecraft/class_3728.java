package net.minecraft;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3728 {
	private final class_310 field_16454;
	private final class_327 field_16457;
	private final Supplier<String> field_16456;
	private final Consumer<String> field_16458;
	private final int field_16455;
	private int field_16453;
	private int field_16452;

	public class_3728(class_310 arg, Supplier<String> supplier, Consumer<String> consumer, int i) {
		this.field_16454 = arg;
		this.field_16457 = arg.field_1772;
		this.field_16456 = supplier;
		this.field_16458 = consumer;
		this.field_16455 = i;
		this.method_16204();
	}

	public boolean method_16199(char c) {
		if (class_155.method_643(c)) {
			this.method_16197(Character.toString(c));
		}

		return true;
	}

	private void method_16197(String string) {
		if (this.field_16452 != this.field_16453) {
			this.method_16198();
		}

		String string2 = (String)this.field_16456.get();
		this.field_16453 = class_3532.method_15340(this.field_16453, 0, string2.length());
		String string3 = new StringBuilder(string2).insert(this.field_16453, string).toString();
		if (this.field_16457.method_1727(string3) <= this.field_16455) {
			this.field_16458.accept(string3);
			this.field_16452 = this.field_16453 = Math.min(string3.length(), this.field_16453 + string.length());
		}
	}

	public boolean method_16202(int i) {
		String string = (String)this.field_16456.get();
		if (class_437.isSelectAll(i)) {
			this.field_16452 = 0;
			this.field_16453 = string.length();
			return true;
		} else if (class_437.isCopy(i)) {
			this.field_16454.field_1774.method_1455(this.method_16200());
			return true;
		} else if (class_437.isPaste(i)) {
			this.method_16197(class_155.method_644(class_124.method_539(this.field_16454.field_1774.method_1460().replaceAll("\\r", ""))));
			this.field_16452 = this.field_16453;
			return true;
		} else if (class_437.isCut(i)) {
			this.field_16454.field_1774.method_1455(this.method_16200());
			this.method_16198();
			return true;
		} else if (i == 259) {
			if (!string.isEmpty()) {
				if (this.field_16452 != this.field_16453) {
					this.method_16198();
				} else if (this.field_16453 > 0) {
					string = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_16453 - 1)).toString();
					this.field_16452 = this.field_16453 = Math.max(0, this.field_16453 - 1);
					this.field_16458.accept(string);
				}
			}

			return true;
		} else if (i == 261) {
			if (!string.isEmpty()) {
				if (this.field_16452 != this.field_16453) {
					this.method_16198();
				} else if (this.field_16453 < string.length()) {
					string = new StringBuilder(string).deleteCharAt(Math.max(0, this.field_16453)).toString();
					this.field_16458.accept(string);
				}
			}

			return true;
		} else if (i == 263) {
			int j = this.field_16457.method_1726() ? 1 : -1;
			if (class_437.hasControlDown()) {
				this.field_16453 = this.field_16457.method_16196(string, j, this.field_16453, true);
			} else {
				this.field_16453 = Math.max(0, Math.min(string.length(), this.field_16453 + j));
			}

			if (!class_437.hasShiftDown()) {
				this.field_16452 = this.field_16453;
			}

			return true;
		} else if (i == 262) {
			int jx = this.field_16457.method_1726() ? -1 : 1;
			if (class_437.hasControlDown()) {
				this.field_16453 = this.field_16457.method_16196(string, jx, this.field_16453, true);
			} else {
				this.field_16453 = Math.max(0, Math.min(string.length(), this.field_16453 + jx));
			}

			if (!class_437.hasShiftDown()) {
				this.field_16452 = this.field_16453;
			}

			return true;
		} else if (i == 268) {
			this.field_16453 = 0;
			if (!class_437.hasShiftDown()) {
				this.field_16452 = this.field_16453;
			}

			return true;
		} else if (i == 269) {
			this.field_16453 = ((String)this.field_16456.get()).length();
			if (!class_437.hasShiftDown()) {
				this.field_16452 = this.field_16453;
			}

			return true;
		} else {
			return false;
		}
	}

	private String method_16200() {
		String string = (String)this.field_16456.get();
		int i = Math.min(this.field_16453, this.field_16452);
		int j = Math.max(this.field_16453, this.field_16452);
		return string.substring(i, j);
	}

	private void method_16198() {
		if (this.field_16452 != this.field_16453) {
			String string = (String)this.field_16456.get();
			int i = Math.min(this.field_16453, this.field_16452);
			int j = Math.max(this.field_16453, this.field_16452);
			String string2 = string.substring(0, i) + string.substring(j);
			this.field_16453 = i;
			this.field_16452 = this.field_16453;
			this.field_16458.accept(string2);
		}
	}

	public void method_16204() {
		this.field_16452 = this.field_16453 = ((String)this.field_16456.get()).length();
	}

	public int method_16201() {
		return this.field_16453;
	}

	public int method_16203() {
		return this.field_16452;
	}
}
