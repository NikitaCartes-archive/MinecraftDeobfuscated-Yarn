package net.minecraft;

import java.util.List;
import java.util.function.Predicate;

public class class_1454 extends class_1422 {
	private static final class_2940<Integer> field_6835 = class_2945.method_12791(class_1454.class, class_2943.field_13327);
	private int field_6833;
	private int field_6832;
	private static final Predicate<class_1309> field_6834 = arg -> {
		if (arg == null) {
			return false;
		} else {
			return !(arg instanceof class_1657) || !((class_1657)arg).method_7325() && !((class_1657)arg).method_7337()
				? arg.method_6046() != class_1310.field_6292
				: false;
		}
	};
	private float field_6831 = -1.0F;
	private float field_6830;

	public class_1454(class_1937 arg) {
		super(class_1299.field_6062, arg);
		this.method_5835(0.7F, 0.7F);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6835, 0);
	}

	public int method_6594() {
		return this.field_6011.method_12789(field_6835);
	}

	public void method_6596(int i) {
		this.field_6011.method_12778(field_6835, i);
		this.method_6592(i);
	}

	private void method_6592(int i) {
		float f = 1.0F;
		if (i == 1) {
			f = 0.7F;
		} else if (i == 0) {
			f = 0.5F;
		}

		this.method_6595(f);
	}

	@Override
	protected final void method_5835(float f, float g) {
		boolean bl = this.field_6831 > 0.0F;
		this.field_6831 = f;
		this.field_6830 = g;
		if (!bl) {
			this.method_6595(1.0F);
		}
	}

	private void method_6595(float f) {
		super.method_5835(this.field_6831 * f, this.field_6830 * f);
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		this.method_6592(this.method_6594());
		super.method_5674(arg);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("PuffState", this.method_6594());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6596(arg.method_10550("PuffState"));
	}

	@Override
	protected class_1799 method_6452() {
		return new class_1799(class_1802.field_8108);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.field_6201.method_6277(1, new class_1454.class_1455(this));
	}

	@Override
	public void method_5773() {
		if (this.method_5805() && !this.field_6002.field_9236) {
			if (this.field_6833 > 0) {
				if (this.method_6594() == 0) {
					this.method_5783(class_3417.field_15235, this.method_6107(), this.method_6017());
					this.method_6596(1);
				} else if (this.field_6833 > 40 && this.method_6594() == 1) {
					this.method_5783(class_3417.field_15235, this.method_6107(), this.method_6017());
					this.method_6596(2);
				}

				this.field_6833++;
			} else if (this.method_6594() != 0) {
				if (this.field_6832 > 60 && this.method_6594() == 2) {
					this.method_5783(class_3417.field_15133, this.method_6107(), this.method_6017());
					this.method_6596(1);
				} else if (this.field_6832 > 100 && this.method_6594() == 1) {
					this.method_5783(class_3417.field_15133, this.method_6107(), this.method_6017());
					this.method_6596(0);
				}

				this.field_6832++;
			}
		}

		super.method_5773();
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.method_6594() > 0) {
			for (class_1308 lv : this.field_6002.method_8390(class_1308.class, this.method_5829().method_1014(0.3), field_6834)) {
				if (lv.method_5805()) {
					this.method_6593(lv);
				}
			}
		}
	}

	private void method_6593(class_1308 arg) {
		int i = this.method_6594();
		if (arg.method_5643(class_1282.method_5511(this), (float)(1 + i))) {
			arg.method_6092(new class_1293(class_1294.field_5899, 60 * i, 0));
			this.method_5783(class_3417.field_14848, 1.0F, 1.0F);
		}
	}

	@Override
	public void method_5694(class_1657 arg) {
		int i = this.method_6594();
		if (arg instanceof class_3222 && i > 0 && arg.method_5643(class_1282.method_5511(this), (float)(1 + i))) {
			((class_3222)arg).field_13987.method_14364(new class_2668(9, 0.0F));
			arg.method_6092(new class_1293(class_1294.field_5899, 60 * i, 0));
		}
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14553;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14888;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14748;
	}

	@Override
	protected class_3414 method_6457() {
		return class_3417.field_15004;
	}

	static class class_1455 extends class_1352 {
		private final class_1454 field_6836;

		public class_1455(class_1454 arg) {
			this.field_6836 = arg;
		}

		@Override
		public boolean method_6264() {
			List<class_1309> list = this.field_6836.field_6002.method_8390(class_1309.class, this.field_6836.method_5829().method_1014(2.0), class_1454.field_6834);
			return !list.isEmpty();
		}

		@Override
		public void method_6269() {
			this.field_6836.field_6833 = 1;
			this.field_6836.field_6832 = 0;
		}

		@Override
		public void method_6270() {
			this.field_6836.field_6833 = 0;
		}

		@Override
		public boolean method_6266() {
			List<class_1309> list = this.field_6836.field_6002.method_8390(class_1309.class, this.field_6836.method_5829().method_1014(2.0), class_1454.field_6834);
			return !list.isEmpty();
		}
	}
}
