package net.minecraft;

import java.util.Collection;

public class class_3486 {
	private static class_3503<class_3611> field_15520 = new class_3503<>(arg -> false, arg -> null, "", false, "");
	private static int field_15519;
	public static final class_3494<class_3611> field_15517 = method_15095("water");
	public static final class_3494<class_3611> field_15518 = method_15095("lava");

	public static void method_15096(class_3503<class_3611> arg) {
		field_15520 = arg;
		field_15519++;
	}

	private static class_3494<class_3611> method_15095(String string) {
		return new class_3486.class_3487(new class_2960(string));
	}

	public static class class_3487 extends class_3494<class_3611> {
		private int field_15522 = -1;
		private class_3494<class_3611> field_15521;

		public class_3487(class_2960 arg) {
			super(arg);
		}

		public boolean method_15101(class_3611 arg) {
			if (this.field_15522 != class_3486.field_15519) {
				this.field_15521 = class_3486.field_15520.method_15188(this.method_15143());
				this.field_15522 = class_3486.field_15519;
			}

			return this.field_15521.method_15141(arg);
		}

		@Override
		public Collection<class_3611> method_15138() {
			if (this.field_15522 != class_3486.field_15519) {
				this.field_15521 = class_3486.field_15520.method_15188(this.method_15143());
				this.field_15522 = class_3486.field_15519;
			}

			return this.field_15521.method_15138();
		}

		@Override
		public Collection<class_3494.class_3496<class_3611>> method_15139() {
			if (this.field_15522 != class_3486.field_15519) {
				this.field_15521 = class_3486.field_15520.method_15188(this.method_15143());
				this.field_15522 = class_3486.field_15519;
			}

			return this.field_15521.method_15139();
		}
	}
}
