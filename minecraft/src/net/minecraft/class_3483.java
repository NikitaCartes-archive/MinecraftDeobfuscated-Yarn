package net.minecraft;

import java.util.Collection;
import java.util.Optional;

public class class_3483 {
	private static class_3503<class_1299<?>> field_15508 = new class_3503<>(arg -> Optional.empty(), "", false, "");
	private static int field_15509;
	public static final class_3494<class_1299<?>> field_15507 = method_15077("skeletons");
	public static final class_3494<class_1299<?>> field_19168 = method_15077("raiders");

	public static void method_15078(class_3503<class_1299<?>> arg) {
		field_15508 = arg;
		field_15509++;
	}

	public static class_3503<class_1299<?>> method_15082() {
		return field_15508;
	}

	private static class_3494<class_1299<?>> method_15077(String string) {
		return new class_3483.class_3484(new class_2960(string));
	}

	public static class class_3484 extends class_3494<class_1299<?>> {
		private int field_15511 = -1;
		private class_3494<class_1299<?>> field_15510;

		public class_3484(class_2960 arg) {
			super(arg);
		}

		public boolean method_15084(class_1299<?> arg) {
			if (this.field_15511 != class_3483.field_15509) {
				this.field_15510 = class_3483.field_15508.method_15188(this.method_15143());
				this.field_15511 = class_3483.field_15509;
			}

			return this.field_15510.method_15141(arg);
		}

		@Override
		public Collection<class_1299<?>> method_15138() {
			if (this.field_15511 != class_3483.field_15509) {
				this.field_15510 = class_3483.field_15508.method_15188(this.method_15143());
				this.field_15511 = class_3483.field_15509;
			}

			return this.field_15510.method_15138();
		}

		@Override
		public Collection<class_3494.class_3496<class_1299<?>>> method_15139() {
			if (this.field_15511 != class_3483.field_15509) {
				this.field_15510 = class_3483.field_15508.method_15188(this.method_15143());
				this.field_15511 = class_3483.field_15509;
			}

			return this.field_15510.method_15139();
		}
	}
}
