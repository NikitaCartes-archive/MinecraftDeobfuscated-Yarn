package net.minecraft;

import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1669 extends class_1297 {
	private int field_7609;
	private boolean field_7610;
	private int field_7607 = 22;
	private boolean field_7608;
	private class_1309 field_7605;
	private UUID field_7606;

	public class_1669(class_1937 arg) {
		super(class_1299.field_6060, arg);
		this.method_5835(0.5F, 0.8F);
	}

	public class_1669(class_1937 arg, double d, double e, double f, float g, int i, class_1309 arg2) {
		this(arg);
		this.field_7609 = i;
		this.method_7473(arg2);
		this.field_6031 = g * (180.0F / (float)Math.PI);
		this.method_5814(d, e, f);
	}

	@Override
	protected void method_5693() {
	}

	public void method_7473(@Nullable class_1309 arg) {
		this.field_7605 = arg;
		this.field_7606 = arg == null ? null : arg.method_5667();
	}

	@Nullable
	public class_1309 method_7470() {
		if (this.field_7605 == null && this.field_7606 != null && this.field_6002 instanceof class_3218) {
			class_1297 lv = this.field_6002.method_14190(this.field_7606);
			if (lv instanceof class_1309) {
				this.field_7605 = (class_1309)lv;
			}
		}

		return this.field_7605;
	}

	@Override
	protected void method_5749(class_2487 arg) {
		this.field_7609 = arg.method_10550("Warmup");
		if (arg.method_10576("OwnerUUID")) {
			this.field_7606 = arg.method_10584("OwnerUUID");
		}
	}

	@Override
	protected void method_5652(class_2487 arg) {
		arg.method_10569("Warmup", this.field_7609);
		if (this.field_7606 != null) {
			arg.method_10560("OwnerUUID", this.field_7606);
		}
	}

	@Override
	public void method_5773() {
		super.method_5773();
		if (this.field_6002.field_9236) {
			if (this.field_7608) {
				this.field_7607--;
				if (this.field_7607 == 14) {
					for (int i = 0; i < 12; i++) {
						double d = this.field_5987 + (this.field_5974.nextDouble() * 2.0 - 1.0) * (double)this.field_5998 * 0.5;
						double e = this.field_6010 + 0.05 + this.field_5974.nextDouble();
						double f = this.field_6035 + (this.field_5974.nextDouble() * 2.0 - 1.0) * (double)this.field_5998 * 0.5;
						double g = (this.field_5974.nextDouble() * 2.0 - 1.0) * 0.3;
						double h = 0.3 + this.field_5974.nextDouble() * 0.3;
						double j = (this.field_5974.nextDouble() * 2.0 - 1.0) * 0.3;
						this.field_6002.method_8406(class_2398.field_11205, d, e + 1.0, f, g, h, j);
					}
				}
			}
		} else if (--this.field_7609 < 0) {
			if (this.field_7609 == -8) {
				for (class_1309 lv : this.field_6002.method_8403(class_1309.class, this.method_5829().method_1009(0.2, 0.0, 0.2))) {
					this.method_7471(lv);
				}
			}

			if (!this.field_7610) {
				this.field_6002.method_8421(this, (byte)4);
				this.field_7610 = true;
			}

			if (--this.field_7607 < 0) {
				this.method_5650();
			}
		}
	}

	private void method_7471(class_1309 arg) {
		class_1309 lv = this.method_7470();
		if (arg.method_5805() && !arg.method_5655() && arg != lv) {
			if (lv == null) {
				arg.method_5643(class_1282.field_5846, 6.0F);
			} else {
				if (lv.method_5722(arg)) {
					return;
				}

				arg.method_5643(class_1282.method_5536(this, lv), 6.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		super.method_5711(b);
		if (b == 4) {
			this.field_7608 = true;
			if (!this.method_5701()) {
				this.field_6002
					.method_8486(
						this.field_5987, this.field_6010, this.field_6035, class_3417.field_14692, this.method_5634(), 1.0F, this.field_5974.nextFloat() * 0.2F + 0.85F, false
					);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_7472(float f) {
		if (!this.field_7608) {
			return 0.0F;
		} else {
			int i = this.field_7607 - 2;
			return i <= 0 ? 1.0F : 1.0F - ((float)i - f) / 20.0F;
		}
	}
}
