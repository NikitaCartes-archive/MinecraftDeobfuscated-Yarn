package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3935 extends class_3872 implements class_3936<class_3916> {
	private final class_3916 field_17422;
	private final class_1712 field_17423 = new class_1712() {
		@Override
		public void method_7634(class_1703 arg, class_2371<class_1799> arg2) {
			class_3935.this.method_17574();
		}

		@Override
		public void method_7635(class_1703 arg, int i, class_1799 arg2) {
			class_3935.this.method_17574();
		}

		@Override
		public void method_7633(class_1703 arg, int i, int j) {
			if (i == 0) {
				class_3935.this.method_17575();
			}
		}
	};

	public class_3935(class_3916 arg, class_1661 arg2, class_2561 arg3) {
		this.field_17422 = arg;
	}

	public class_3916 method_17573() {
		return this.field_17422;
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.field_17422.method_7596(this.field_17423);
	}

	@Override
	public void method_2210() {
		this.field_2563.field_1724.method_7346();
		super.method_2210();
	}

	@Override
	public void method_2234() {
		super.method_2234();
		this.field_17422.method_7603(this.field_17423);
	}

	@Override
	protected void method_17557() {
		if (this.field_2563.field_1724.method_7294()) {
			this.method_2219(new class_339(0, this.field_2561 / 2 - 100, 196, 98, 20, class_1074.method_4662("gui.done")) {
				@Override
				public void method_1826(double d, double e) {
					class_3935.this.field_2563.method_1507(null);
				}
			});
			this.method_2219(new class_339(3, this.field_2561 / 2 + 2, 196, 98, 20, class_1074.method_4662("lectern.take_book")) {
				@Override
				public void method_1826(double d, double e) {
					class_3935.this.method_17572(3);
				}
			});
		} else {
			super.method_17557();
		}
	}

	@Override
	protected void method_17057() {
		this.method_17572(1);
	}

	@Override
	protected void method_17058() {
		this.method_17572(2);
	}

	@Override
	protected void method_17559() {
	}

	private void method_17572(int i) {
		this.field_2563.field_1761.method_2900(this.field_17422.field_7763, i);
	}

	@Override
	public boolean method_2222() {
		return false;
	}

	private void method_17574() {
		class_1799 lv = this.field_17422.method_17418();
		this.method_17554(class_3872.class_3931.method_17562(lv));
	}

	private void method_17575() {
		this.method_17556(this.field_17422.method_17419());
	}
}
