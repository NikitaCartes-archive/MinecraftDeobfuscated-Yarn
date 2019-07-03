package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4429 {
	public final int field_20205;
	public final String field_20206;

	public class_4429(int i, String string) {
		this.field_20205 = i;
		this.field_20206 = string;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4430 {
		private int field_20207 = -1;
		private String field_20208 = null;

		public class_4429.class_4430 method_21542(int i) {
			this.field_20207 = i;
			return this;
		}

		public class_4429.class_4430 method_21543(String string) {
			this.field_20208 = string;
			return this;
		}

		public class_4429 method_21541() {
			return new class_4429(this.field_20207, this.field_20208);
		}
	}
}
