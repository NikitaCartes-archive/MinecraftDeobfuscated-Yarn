package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_1628 extends class_1588 {
	private static final class_2940<Byte> field_7403 = class_2945.method_12791(class_1628.class, class_2943.field_13319);

	protected class_1628(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.method_5835(1.4F, 0.9F);
	}

	public class_1628(class_1937 arg) {
		this(class_1299.field_6079, arg);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(3, new class_1359(this, 0.4F));
		this.field_6201.method_6277(4, new class_1628.class_1629(this));
		this.field_6201.method_6277(5, new class_1394(this, 0.8));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(6, new class_1376(this));
		this.field_6185.method_6277(1, new class_1399(this));
		this.field_6185.method_6277(2, new class_1628.class_1631(this, class_1657.class));
		this.field_6185.method_6277(3, new class_1628.class_1631(this, class_1439.class));
	}

	@Override
	public double method_5621() {
		return (double)(this.field_6019 * 0.5F);
	}

	@Override
	protected class_1408 method_5965(class_1937 arg) {
		return new class_1410(this, arg);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7403, (byte)0);
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (!this.field_6002.field_9236) {
			this.method_7166(this.field_5976);
		}
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(16.0);
		this.method_5996(class_1612.field_7357).method_6192(0.3F);
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_15170;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14657;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14579;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14760, 0.15F, 1.0F);
	}

	@Override
	public boolean method_6101() {
		return this.method_7167();
	}

	@Override
	public void method_5844(class_2680 arg, class_243 arg2) {
		if (arg.method_11614() != class_2246.field_10343) {
			super.method_5844(arg, arg2);
		}
	}

	@Override
	public class_1310 method_6046() {
		return class_1310.field_6293;
	}

	@Override
	public boolean method_6049(class_1293 arg) {
		return arg.method_5579() == class_1294.field_5899 ? false : super.method_6049(arg);
	}

	public boolean method_7167() {
		return (this.field_6011.method_12789(field_7403) & 1) != 0;
	}

	public void method_7166(boolean bl) {
		byte b = this.field_6011.method_12789(field_7403);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.field_6011.method_12778(field_7403, b);
	}

	@Nullable
	@Override
	public class_1315 method_5943(class_1936 arg, class_1266 arg2, class_3730 arg3, @Nullable class_1315 arg4, @Nullable class_2487 arg5) {
		arg4 = super.method_5943(arg, arg2, arg3, arg4, arg5);
		if (arg.method_8409().nextInt(100) == 0) {
			class_1613 lv = new class_1613(this.field_6002);
			lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, 0.0F);
			lv.method_5943(arg, arg2, arg3, null, null);
			arg.method_8649(lv);
			lv.method_5804(this);
		}

		if (arg4 == null) {
			arg4 = new class_1628.class_1630();
			if (arg.method_8407() == class_1267.field_5807 && arg.method_8409().nextFloat() < 0.1F * arg2.method_5458()) {
				((class_1628.class_1630)arg4).method_7168(arg.method_8409());
			}
		}

		if (arg4 instanceof class_1628.class_1630) {
			class_1291 lv2 = ((class_1628.class_1630)arg4).field_7404;
			if (lv2 != null) {
				this.method_6092(new class_1293(lv2, Integer.MAX_VALUE));
			}
		}

		return arg4;
	}

	@Override
	public float method_5751() {
		return 0.65F;
	}

	static class class_1629 extends class_1366 {
		public class_1629(class_1628 arg) {
			super(arg, 1.0, true);
		}

		@Override
		public boolean method_6264() {
			return super.method_6264() && !this.field_6503.method_5782();
		}

		@Override
		public boolean method_6266() {
			float f = this.field_6503.method_5718();
			if (f >= 0.5F && this.field_6503.method_6051().nextInt(100) == 0) {
				this.field_6503.method_5980(null);
				return false;
			} else {
				return super.method_6266();
			}
		}

		@Override
		protected double method_6289(class_1309 arg) {
			return (double)(4.0F + arg.field_5998);
		}
	}

	public static class class_1630 implements class_1315 {
		public class_1291 field_7404;

		public void method_7168(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.field_7404 = class_1294.field_5904;
			} else if (i <= 2) {
				this.field_7404 = class_1294.field_5910;
			} else if (i <= 3) {
				this.field_7404 = class_1294.field_5924;
			} else if (i <= 4) {
				this.field_7404 = class_1294.field_5905;
			}
		}
	}

	static class class_1631<T extends class_1309> extends class_1400<T> {
		public class_1631(class_1628 arg, Class<T> class_) {
			super(arg, class_, true);
		}

		@Override
		public boolean method_6264() {
			float f = this.field_6660.method_5718();
			return f >= 0.5F ? false : super.method_6264();
		}
	}
}
