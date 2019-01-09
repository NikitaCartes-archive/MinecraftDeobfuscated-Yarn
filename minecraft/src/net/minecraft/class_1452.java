package net.minecraft;

import javax.annotation.Nullable;

public class class_1452 extends class_1429 {
	private static final class_2940<Boolean> field_6816 = class_2945.method_12791(class_1452.class, class_2943.field_13323);
	private static final class_2940<Integer> field_6815 = class_2945.method_12791(class_1452.class, class_2943.field_13327);
	private static final class_1856 field_6817 = class_1856.method_8091(class_1802.field_8179, class_1802.field_8567, class_1802.field_8186);
	private boolean field_6814;
	private int field_6812;
	private int field_6813;

	public class_1452(class_1937 arg) {
		super(class_1299.field_6093, arg);
		this.method_5835(0.9F, 0.9F);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(0, new class_1347(this));
		this.field_6201.method_6277(1, new class_1374(this, 1.25));
		this.field_6201.method_6277(3, new class_1341(this, 1.0));
		this.field_6201.method_6277(4, new class_1391(this, 1.2, class_1856.method_8091(class_1802.field_8184), false));
		this.field_6201.method_6277(4, new class_1391(this, 1.2, false, field_6817));
		this.field_6201.method_6277(5, new class_1353(this, 1.1));
		this.field_6201.method_6277(6, new class_1394(this, 1.0));
		this.field_6201.method_6277(7, new class_1361(this, class_1657.class, 6.0F));
		this.field_6201.method_6277(8, new class_1376(this));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192(10.0);
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Nullable
	@Override
	public class_1297 method_5642() {
		return this.method_5685().isEmpty() ? null : (class_1297)this.method_5685().get(0);
	}

	@Override
	public boolean method_5956() {
		class_1297 lv = this.method_5642();
		if (!(lv instanceof class_1657)) {
			return false;
		} else {
			class_1657 lv2 = (class_1657)lv;
			return lv2.method_6047().method_7909() == class_1802.field_8184 || lv2.method_6079().method_7909() == class_1802.field_8184;
		}
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_6815.equals(arg) && this.field_6002.field_9236) {
			this.field_6814 = true;
			this.field_6812 = 0;
			this.field_6813 = this.field_6011.method_12789(field_6815);
		}

		super.method_5674(arg);
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6816, false);
		this.field_6011.method_12784(field_6815, 0);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("Saddle", this.method_6575());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6576(arg.method_10577("Saddle"));
	}

	@Override
	protected class_3414 method_5994() {
		return class_3417.field_14615;
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_14750;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14689;
	}

	@Override
	protected void method_5712(class_2338 arg, class_2680 arg2) {
		this.method_5783(class_3417.field_14894, 0.15F, 1.0F);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		if (!super.method_5992(arg, arg2)) {
			class_1799 lv = arg.method_5998(arg2);
			if (lv.method_7909() == class_1802.field_8448) {
				lv.method_7920(arg, this, arg2);
				return true;
			} else if (this.method_6575() && !this.method_5782()) {
				if (!this.field_6002.field_9236) {
					arg.method_5804(this);
				}

				return true;
			} else if (lv.method_7909() == class_1802.field_8175) {
				lv.method_7920(arg, this, arg2);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void method_16078() {
		super.method_16078();
		if (this.method_6575()) {
			this.method_5706(class_1802.field_8175);
		}
	}

	public boolean method_6575() {
		return this.field_6011.method_12789(field_6816);
	}

	public void method_6576(boolean bl) {
		if (bl) {
			this.field_6011.method_12778(field_6816, true);
		} else {
			this.field_6011.method_12778(field_6816, false);
		}
	}

	@Override
	public void method_5800(class_1538 arg) {
		if (!this.field_6002.field_9236 && !this.field_5988) {
			class_1590 lv = new class_1590(this.field_6002);
			lv.method_5673(class_1304.field_6173, new class_1799(class_1802.field_8845));
			lv.method_5808(this.field_5987, this.field_6010, this.field_6035, this.field_6031, this.field_5965);
			lv.method_5977(this.method_5987());
			if (this.method_16914()) {
				lv.method_5665(this.method_5797());
				lv.method_5880(this.method_5807());
			}

			this.field_6002.method_8649(lv);
			this.method_5650();
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		class_1297 lv = this.method_5685().isEmpty() ? null : (class_1297)this.method_5685().get(0);
		if (this.method_5782() && this.method_5956()) {
			this.field_6031 = lv.field_6031;
			this.field_5982 = this.field_6031;
			this.field_5965 = lv.field_5965 * 0.5F;
			this.method_5710(this.field_6031, this.field_5965);
			this.field_6283 = this.field_6031;
			this.field_6241 = this.field_6031;
			this.field_6013 = 1.0F;
			this.field_6281 = this.method_6029() * 0.1F;
			if (this.field_6814 && this.field_6812++ > this.field_6813) {
				this.field_6814 = false;
			}

			if (this.method_5787()) {
				float i = (float)this.method_5996(class_1612.field_7357).method_6194() * 0.225F;
				if (this.field_6814) {
					i += i * 1.15F * class_3532.method_15374((float)this.field_6812 / (float)this.field_6813 * (float) Math.PI);
				}

				this.method_6125(i);
				super.method_6091(0.0F, 0.0F, 1.0F);
			} else {
				this.field_5967 = 0.0;
				this.field_5984 = 0.0;
				this.field_6006 = 0.0;
			}

			this.field_6211 = this.field_6225;
			double d = this.field_5987 - this.field_6014;
			double e = this.field_6035 - this.field_5969;
			float j = class_3532.method_15368(d * d + e * e) * 4.0F;
			if (j > 1.0F) {
				j = 1.0F;
			}

			this.field_6225 = this.field_6225 + (j - this.field_6225) * 0.4F;
			this.field_6249 = this.field_6249 + this.field_6225;
		} else {
			this.field_6013 = 0.5F;
			this.field_6281 = 0.02F;
			super.method_6091(f, g, h);
		}
	}

	public boolean method_6577() {
		if (this.field_6814) {
			return false;
		} else {
			this.field_6814 = true;
			this.field_6812 = 0;
			this.field_6813 = this.method_6051().nextInt(841) + 140;
			this.method_5841().method_12778(field_6815, this.field_6813);
			return true;
		}
	}

	public class_1452 method_6574(class_1296 arg) {
		return new class_1452(this.field_6002);
	}

	@Override
	public boolean method_6481(class_1799 arg) {
		return field_6817.method_8093(arg);
	}
}
