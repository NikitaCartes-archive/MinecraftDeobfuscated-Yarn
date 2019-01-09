package net.minecraft;

import java.util.UUID;

public abstract class class_1259 {
	private final UUID field_5772;
	protected class_2561 field_5777;
	protected float field_5774;
	protected class_1259.class_1260 field_5778;
	protected class_1259.class_1261 field_5779;
	protected boolean field_5776;
	protected boolean field_5775;
	protected boolean field_5773;

	public class_1259(UUID uUID, class_2561 arg, class_1259.class_1260 arg2, class_1259.class_1261 arg3) {
		this.field_5772 = uUID;
		this.field_5777 = arg;
		this.field_5778 = arg2;
		this.field_5779 = arg3;
		this.field_5774 = 1.0F;
	}

	public UUID method_5407() {
		return this.field_5772;
	}

	public class_2561 method_5414() {
		return this.field_5777;
	}

	public void method_5413(class_2561 arg) {
		this.field_5777 = arg;
	}

	public float method_5412() {
		return this.field_5774;
	}

	public void method_5408(float f) {
		this.field_5774 = f;
	}

	public class_1259.class_1260 method_5420() {
		return this.field_5778;
	}

	public void method_5416(class_1259.class_1260 arg) {
		this.field_5778 = arg;
	}

	public class_1259.class_1261 method_5415() {
		return this.field_5779;
	}

	public void method_5409(class_1259.class_1261 arg) {
		this.field_5779 = arg;
	}

	public boolean method_5417() {
		return this.field_5776;
	}

	public class_1259 method_5406(boolean bl) {
		this.field_5776 = bl;
		return this;
	}

	public boolean method_5418() {
		return this.field_5775;
	}

	public class_1259 method_5410(boolean bl) {
		this.field_5775 = bl;
		return this;
	}

	public class_1259 method_5411(boolean bl) {
		this.field_5773 = bl;
		return this;
	}

	public boolean method_5419() {
		return this.field_5773;
	}

	public static enum class_1260 {
		field_5788("pink", class_124.field_1061),
		field_5780("blue", class_124.field_1078),
		field_5784("red", class_124.field_1079),
		field_5785("green", class_124.field_1060),
		field_5782("yellow", class_124.field_1054),
		field_5783("purple", class_124.field_1058),
		field_5786("white", class_124.field_1068);

		private final String field_5781;
		private final class_124 field_5787;

		private class_1260(String string2, class_124 arg) {
			this.field_5781 = string2;
			this.field_5787 = arg;
		}

		public class_124 method_5423() {
			return this.field_5787;
		}

		public String method_5421() {
			return this.field_5781;
		}

		public static class_1259.class_1260 method_5422(String string) {
			for (class_1259.class_1260 lv : values()) {
				if (lv.field_5781.equals(string)) {
					return lv;
				}
			}

			return field_5786;
		}
	}

	public static enum class_1261 {
		field_5795("progress"),
		field_5796("notched_6"),
		field_5791("notched_10"),
		field_5793("notched_12"),
		field_5790("notched_20");

		private final String field_5794;

		private class_1261(String string2) {
			this.field_5794 = string2;
		}

		public String method_5425() {
			return this.field_5794;
		}

		public static class_1259.class_1261 method_5424(String string) {
			for (class_1259.class_1261 lv : values()) {
				if (lv.field_5794.equals(string)) {
					return lv;
				}
			}

			return field_5795;
		}
	}
}
