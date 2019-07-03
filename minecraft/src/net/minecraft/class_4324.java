package net.minecraft;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4324 {
	private final char[] field_19453;
	private int field_19454;
	private final Runnable field_19455;

	public class_4324(char[] cs, Runnable runnable) {
		this.field_19455 = runnable;
		if (cs.length < 1) {
			throw new IllegalArgumentException("Must have at least one char");
		} else {
			this.field_19453 = cs;
			this.field_19454 = 0;
		}
	}

	public boolean method_20833(char c) {
		if (c == this.field_19453[this.field_19454]) {
			this.field_19454++;
			if (this.field_19454 == this.field_19453.length) {
				this.method_20832();
				this.field_19455.run();
				return true;
			} else {
				return false;
			}
		} else {
			this.method_20832();
			return false;
		}
	}

	public void method_20832() {
		this.field_19454 = 0;
	}

	public String toString() {
		return "KeyCombo{chars=" + Arrays.toString(this.field_19453) + ", matchIndex=" + this.field_19454 + '}';
	}
}
