package net.minecraft;

import javax.annotation.Nullable;

public class class_3722 extends class_2586 implements class_3829, class_3908 {
	private final class_1263 field_17386 = new class_1263() {
		@Override
		public int method_5439() {
			return 1;
		}

		@Override
		public boolean method_5442() {
			return class_3722.this.field_17388.method_7960();
		}

		@Override
		public class_1799 method_5438(int i) {
			return i == 0 ? class_3722.this.field_17388 : class_1799.field_8037;
		}

		@Override
		public class_1799 method_5434(int i, int j) {
			if (i == 0) {
				class_1799 lv = class_3722.this.field_17388.method_7971(j);
				if (class_3722.this.field_17388.method_7960()) {
					class_3722.this.method_17525();
				}

				return lv;
			} else {
				return class_1799.field_8037;
			}
		}

		@Override
		public class_1799 method_5441(int i) {
			if (i == 0) {
				class_1799 lv = class_3722.this.field_17388;
				class_3722.this.field_17388 = class_1799.field_8037;
				class_3722.this.method_17525();
				return lv;
			} else {
				return class_1799.field_8037;
			}
		}

		@Override
		public void method_5447(int i, class_1799 arg) {
		}

		@Override
		public int method_5444() {
			return 1;
		}

		@Override
		public void method_5431() {
			class_3722.this.method_5431();
		}

		@Override
		public boolean method_5443(class_1657 arg) {
			if (class_3722.this.field_11863.method_8321(class_3722.this.field_11867) != class_3722.this) {
				return false;
			} else {
				return arg.method_5649(
							(double)class_3722.this.field_11867.method_10263() + 0.5,
							(double)class_3722.this.field_11867.method_10264() + 0.5,
							(double)class_3722.this.field_11867.method_10260() + 0.5
						)
						> 64.0
					? false
					: class_3722.this.method_17522();
			}
		}

		@Override
		public boolean method_5437(int i, class_1799 arg) {
			return false;
		}

		@Override
		public void method_5448() {
		}
	};
	private final class_3913 field_17387 = new class_3913() {
		@Override
		public int method_17390(int i) {
			return i == 0 ? class_3722.this.field_17389 : 0;
		}

		@Override
		public void method_17391(int i, int j) {
			if (i == 0) {
				class_3722.this.method_17511(j);
			}
		}

		@Override
		public int method_17389() {
			return 1;
		}
	};
	private class_1799 field_17388 = class_1799.field_8037;
	private int field_17389;
	private int field_17390;

	public class_3722() {
		super(class_2591.field_16412);
	}

	public class_1799 method_17520() {
		return this.field_17388;
	}

	public boolean method_17522() {
		class_1792 lv = this.field_17388.method_7909();
		return lv == class_1802.field_8674 || lv == class_1802.field_8360;
	}

	public void method_17513(class_1799 arg) {
		this.method_17514(arg, null);
	}

	private void method_17525() {
		this.field_17389 = 0;
		this.field_17390 = 0;
		class_3715.method_17473(this.method_10997(), this.method_11016(), this.method_11010(), false);
	}

	public void method_17514(class_1799 arg, @Nullable class_1657 arg2) {
		this.field_17388 = this.method_17518(arg, arg2);
		this.field_17389 = 0;
		this.field_17390 = class_1843.method_17443(this.field_17388);
		this.method_5431();
	}

	private void method_17511(int i) {
		int j = class_3532.method_15340(i, 0, this.field_17390 - 1);
		if (j != this.field_17389) {
			this.field_17389 = j;
			this.method_5431();
			class_3715.method_17471(this.method_10997(), this.method_11016(), this.method_11010());
		}
	}

	public int method_17523() {
		return this.field_17389;
	}

	public int method_17524() {
		float f = this.field_17390 > 1 ? (float)this.method_17523() / ((float)this.field_17390 - 1.0F) : 1.0F;
		return class_3532.method_15375(f * 14.0F) + (this.method_17522() ? 1 : 0);
	}

	private class_1799 method_17518(class_1799 arg, @Nullable class_1657 arg2) {
		if (this.field_11863 instanceof class_3218 && arg.method_7909() == class_1802.field_8360) {
			class_1843.method_8054(arg, this.method_17512(arg2), arg2);
		}

		return arg;
	}

	private class_2168 method_17512(@Nullable class_1657 arg) {
		String string;
		class_2561 lv;
		if (arg == null) {
			string = "Lectern";
			lv = new class_2585("Lectern");
		} else {
			string = arg.method_5477().getString();
			lv = arg.method_5476();
		}

		class_243 lv2 = new class_243(
			(double)this.field_11867.method_10263() + 0.5, (double)this.field_11867.method_10264() + 0.5, (double)this.field_11867.method_10260() + 0.5
		);
		return new class_2168(class_2165.field_17395, lv2, class_241.field_1340, (class_3218)this.field_11863, 2, string, lv, this.field_11863.method_8503(), arg);
	}

	@Override
	public void method_11014(class_2487 arg) {
		super.method_11014(arg);
		if (arg.method_10573("Book", 10)) {
			this.field_17388 = this.method_17518(class_1799.method_7915(arg.method_10562("Book")), null);
		} else {
			this.field_17388 = class_1799.field_8037;
		}

		this.field_17390 = class_1843.method_17443(this.field_17388);
		this.field_17389 = class_3532.method_15340(arg.method_10550("Page"), 0, this.field_17390 - 1);
	}

	@Override
	public class_2487 method_11007(class_2487 arg) {
		super.method_11007(arg);
		if (!this.method_17520().method_7960()) {
			arg.method_10566("Book", this.method_17520().method_7953(new class_2487()));
			arg.method_10569("Page", this.field_17389);
		}

		return arg;
	}

	@Override
	public void method_5448() {
		this.method_17513(class_1799.field_8037);
	}

	@Override
	public class_1703 createMenu(int i, class_1661 arg, class_1657 arg2) {
		return new class_3916(i, this.field_17386, this.field_17387);
	}

	@Override
	public class_2561 method_5476() {
		return new class_2588("container.lectern");
	}
}
