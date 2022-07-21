package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.text.Text;

public class class_7642 {
	@Nullable
	private class_7642.class_7643 field_39909;

	public void method_45036(String string, Text text) {
		this.field_39909 = new class_7642.class_7643(string, text);
	}

	@Nullable
	public Text method_45035(String string) {
		class_7642.class_7643 lv = this.field_39909;
		if (lv != null && lv.method_45037(string)) {
			this.field_39909 = null;
			return lv.preview();
		} else {
			return null;
		}
	}

	static record class_7643(String query, Text preview) {
		public boolean method_45037(String string) {
			return this.query.equals(string);
		}
	}
}
