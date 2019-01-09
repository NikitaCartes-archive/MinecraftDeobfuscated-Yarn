package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;

public class class_1590 extends class_1642 {
	private static final UUID field_7311 = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final class_1322 field_7307 = new class_1322(field_7311, "Attacking speed boost", 0.05, class_1322.class_1323.field_6328).method_6187(false);
	private int field_7309;
	private int field_7308;
	private UUID field_7310;

	public class_1590(class_1937 arg) {
		super(class_1299.field_6050, arg);
		this.field_5977 = true;
	}

	@Override
	public void method_6015(@Nullable class_1309 arg) {
		super.method_6015(arg);
		if (arg != null) {
			this.field_7310 = arg.method_5667();
		}
	}

	@Override
	protected void method_7208() {
		this.field_6201.method_6277(2, new class_1396(this, 1.0, false));
		this.field_6201.method_6277(7, new class_1394(this, 1.0));
		this.field_6185.method_6277(1, new class_1590.class_1592(this));
		this.field_6185.method_6277(2, new class_1590.class_1591(this));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(field_7428).method_6192(0.0);
		this.method_5996(class_1612.field_7357).method_6192(0.23F);
		this.method_5996(class_1612.field_7363).method_6192(5.0);
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	protected void method_5958() {
		class_1324 lv = this.method_5996(class_1612.field_7357);
		if (this.method_7079()) {
			if (!this.method_6109() && !lv.method_6196(field_7307)) {
				lv.method_6197(field_7307);
			}

			this.field_7309--;
		} else if (lv.method_6196(field_7307)) {
			lv.method_6202(field_7307);
		}

		if (this.field_7308 > 0 && --this.field_7308 == 0) {
			this.method_5783(class_3417.field_14852, this.method_6107() * 2.0F, ((this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		if (this.field_7309 > 0 && this.field_7310 != null && this.method_6065() == null) {
			class_1657 lv2 = this.field_6002.method_8420(this.field_7310);
			this.method_6015(lv2);
			this.field_6258 = lv2;
			this.field_6238 = this.method_6117();
		}

		super.method_5958();
	}

	@Override
	public boolean method_5979(class_1936 arg, class_3730 arg2) {
		return arg.method_8407() != class_1267.field_5801;
	}

	@Override
	public boolean method_5957(class_1941 arg) {
		return arg.method_8606(this, this.method_5829()) && arg.method_8587(this, this.method_5829()) && !arg.method_8599(this.method_5829());
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10575("Anger", (short)this.field_7309);
		if (this.field_7310 != null) {
			arg.method_10582("HurtBy", this.field_7310.toString());
		} else {
			arg.method_10582("HurtBy", "");
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_7309 = arg.method_10568("Anger");
		String string = arg.method_10558("HurtBy");
		if (!string.isEmpty()) {
			this.field_7310 = UUID.fromString(string);
			class_1657 lv = this.field_6002.method_8420(this.field_7310);
			this.method_6015(lv);
			if (lv != null) {
				this.field_6258 = lv;
				this.field_6238 = this.method_6117();
			}
		}
	}

	@Override
	public boolean method_5643(class_1282 arg, float f) {
		if (this.method_5679(arg)) {
			return false;
		} else {
			class_1297 lv = arg.method_5529();
			if (lv instanceof class_1657 && !((class_1657)lv).method_7337()) {
				this.method_7077(lv);
			}

			return super.method_5643(arg, f);
		}
	}

	private void method_7077(class_1297 arg) {
		this.field_7309 = 400 + this.field_5974.nextInt(400);
		this.field_7308 = this.field_5974.nextInt(40);
		if (arg instanceof class_1309) {
			this.method_6015((class_1309)arg);
		}
	}

	public boolean method_7079() {
		return this.field_7309 > 0;
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14926;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14710;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14743;
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		return false;
	}

	@Override
	protected void method_5964(class_1266 arg) {
		this.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8845));
	}

	@Override
	protected class_1799 method_7215() {
		return class_1799.field_8037;
	}

	@Override
	public boolean method_7076(class_1657 arg) {
		return this.method_7079();
	}

	static class class_1591 extends class_1400<class_1657> {
		public class_1591(class_1590 arg) {
			super(arg, class_1657.class, true);
		}

		@Override
		public boolean method_6264() {
			return ((class_1590)this.field_6660).method_7079() && super.method_6264();
		}
	}

	static class class_1592 extends class_1399 {
		public class_1592(class_1590 arg) {
			super(arg);
			this.method_6318(new Class[]{class_1642.class});
		}

		@Override
		protected void method_6319(class_1314 arg, class_1309 arg2) {
			super.method_6319(arg, arg2);
			if (arg instanceof class_1590) {
				((class_1590)arg).method_7077(arg2);
			}
		}
	}
}
