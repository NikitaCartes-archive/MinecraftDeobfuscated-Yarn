package net.minecraft;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1548 extends class_1588 {
	private static final class_2940<Integer> field_7230 = class_2945.method_12791(class_1548.class, class_2943.field_13327);
	private static final class_2940<Boolean> field_7224 = class_2945.method_12791(class_1548.class, class_2943.field_13323);
	private static final class_2940<Boolean> field_7231 = class_2945.method_12791(class_1548.class, class_2943.field_13323);
	private int field_7229;
	private int field_7227;
	private int field_7228 = 30;
	private int field_7225 = 3;
	private int field_7226;

	public class_1548(class_1937 arg) {
		super(class_1299.field_6046, arg);
		this.method_5835(0.6F, 1.7F);
	}

	@Override
	protected void method_5959() {
		this.field_6201.method_6277(1, new class_1347(this));
		this.field_6201.method_6277(2, new class_1389(this));
		this.field_6201.method_6277(3, new class_1338(this, class_3701.class, 6.0F, 1.0, 1.2));
		this.field_6201.method_6277(3, new class_1338(this, class_1451.class, 6.0F, 1.0, 1.2));
		this.field_6201.method_6277(4, new class_1366(this, 1.0, false));
		this.field_6201.method_6277(5, new class_1394(this, 0.8));
		this.field_6201.method_6277(6, new class_1361(this, class_1657.class, 8.0F));
		this.field_6201.method_6277(6, new class_1376(this));
		this.field_6185.method_6277(1, new class_1400(this, class_1657.class, true));
		this.field_6185.method_6277(2, new class_1399(this));
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7357).method_6192(0.25);
	}

	@Override
	public int method_5850() {
		return this.method_5968() == null ? 3 : 3 + (int)(this.method_6032() - 1.0F);
	}

	@Override
	public void method_5747(float f, float g) {
		super.method_5747(f, g);
		this.field_7227 = (int)((float)this.field_7227 + f * 1.5F);
		if (this.field_7227 > this.field_7228 - 5) {
			this.field_7227 = this.field_7228 - 5;
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_7230, -1);
		this.field_6011.method_12784(field_7224, false);
		this.field_6011.method_12784(field_7231, false);
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		if (this.field_6011.method_12789(field_7224)) {
			arg.method_10556("powered", true);
		}

		arg.method_10575("Fuse", (short)this.field_7228);
		arg.method_10567("ExplosionRadius", (byte)this.field_7225);
		arg.method_10556("ignited", this.method_7000());
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.field_6011.method_12778(field_7224, arg.method_10577("powered"));
		if (arg.method_10573("Fuse", 99)) {
			this.field_7228 = arg.method_10568("Fuse");
		}

		if (arg.method_10573("ExplosionRadius", 99)) {
			this.field_7225 = arg.method_10571("ExplosionRadius");
		}

		if (arg.method_10577("ignited")) {
			this.method_7004();
		}
	}

	@Override
	public void method_5773() {
		if (this.method_5805()) {
			this.field_7229 = this.field_7227;
			if (this.method_7000()) {
				this.method_7005(1);
			}

			int i = this.method_7007();
			if (i > 0 && this.field_7227 == 0) {
				this.method_5783(class_3417.field_15057, 1.0F, 0.5F);
			}

			this.field_7227 += i;
			if (this.field_7227 < 0) {
				this.field_7227 = 0;
			}

			if (this.field_7227 >= this.field_7228) {
				this.field_7227 = this.field_7228;
				this.method_7006();
			}
		}

		super.method_5773();
	}

	@Override
	protected class_3414 method_6011(class_1282 arg) {
		return class_3417.field_15192;
	}

	@Override
	protected class_3414 method_6002() {
		return class_3417.field_14907;
	}

	@Override
	protected void method_6099(class_1282 arg, int i, boolean bl) {
		super.method_6099(arg, i, bl);
		class_1297 lv = arg.method_5529();
		if (lv != this && lv instanceof class_1548) {
			class_1548 lv2 = (class_1548)lv;
			if (lv2.method_7008()) {
				lv2.method_7002();
				this.method_5706(class_1802.field_8681);
			}
		}
	}

	@Override
	public boolean method_6121(class_1297 arg) {
		return true;
	}

	public boolean method_7009() {
		return this.field_6011.method_12789(field_7224);
	}

	@Environment(EnvType.CLIENT)
	public float method_7003(float f) {
		return class_3532.method_16439(f, (float)this.field_7229, (float)this.field_7227) / (float)(this.field_7228 - 2);
	}

	public int method_7007() {
		return this.field_6011.method_12789(field_7230);
	}

	public void method_7005(int i) {
		this.field_6011.method_12778(field_7230, i);
	}

	@Override
	public void method_5800(class_1538 arg) {
		super.method_5800(arg);
		this.field_6011.method_12778(field_7224, true);
	}

	@Override
	protected boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() == class_1802.field_8884) {
			this.field_6002
				.method_8465(
					arg, this.field_5987, this.field_6010, this.field_6035, class_3417.field_15145, this.method_5634(), 1.0F, this.field_5974.nextFloat() * 0.4F + 0.8F
				);
			arg.method_6104(arg2);
			if (!this.field_6002.field_9236) {
				this.method_7004();
				lv.method_7956(1, arg);
				return true;
			}
		}

		return super.method_5992(arg, arg2);
	}

	private void method_7006() {
		if (!this.field_6002.field_9236) {
			boolean bl = this.field_6002.method_8450().method_8355("mobGriefing");
			float f = this.method_7009() ? 2.0F : 1.0F;
			this.field_6272 = true;
			this.field_6002.method_8437(this, this.field_5987, this.field_6010, this.field_6035, (float)this.field_7225 * f, bl);
			this.method_5650();
			this.method_7001();
		}
	}

	private void method_7001() {
		Collection<class_1293> collection = this.method_6026();
		if (!collection.isEmpty()) {
			class_1295 lv = new class_1295(this.field_6002, this.field_5987, this.field_6010, this.field_6035);
			lv.method_5603(2.5F);
			lv.method_5609(-0.5F);
			lv.method_5595(10);
			lv.method_5604(lv.method_5605() / 2);
			lv.method_5596(-lv.method_5599() / (float)lv.method_5605());

			for (class_1293 lv2 : collection) {
				lv.method_5610(new class_1293(lv2));
			}

			this.field_6002.method_8649(lv);
		}
	}

	public boolean method_7000() {
		return this.field_6011.method_12789(field_7231);
	}

	public void method_7004() {
		this.field_6011.method_12778(field_7231, true);
	}

	public boolean method_7008() {
		return this.method_7009() && this.field_7226 < 1;
	}

	public void method_7002() {
		this.field_7226++;
	}
}
