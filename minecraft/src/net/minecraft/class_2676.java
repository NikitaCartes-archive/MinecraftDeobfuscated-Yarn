package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2676 implements class_2596<class_2602> {
	private int field_12265;
	private int field_12264;
	private int field_12263;
	private int field_12262;
	private int field_16418;
	private int field_16417;
	private List<byte[]> field_12266;
	private List<byte[]> field_12261;

	public class_2676() {
	}

	public class_2676(class_1923 arg, class_3568 arg2) {
		this.field_12265 = arg.field_9181;
		this.field_12264 = arg.field_9180;
		this.field_12266 = Lists.<byte[]>newArrayList();
		this.field_12261 = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			class_2804 lv = arg2.method_15562(class_1944.field_9284).method_15544(class_4076.method_18681(arg, -1 + i));
			class_2804 lv2 = arg2.method_15562(class_1944.field_9282).method_15544(class_4076.method_18681(arg, -1 + i));
			if (lv != null) {
				if (lv.method_12146()) {
					this.field_16418 |= 1 << i;
				} else {
					this.field_12263 |= 1 << i;
					this.field_12266.add(lv.method_12137().clone());
				}
			}

			if (lv2 != null) {
				if (lv2.method_12146()) {
					this.field_16417 |= 1 << i;
				} else {
					this.field_12262 |= 1 << i;
					this.field_12261.add(lv2.method_12137().clone());
				}
			}
		}
	}

	public class_2676(class_1923 arg, class_3568 arg2, int i, int j) {
		this.field_12265 = arg.field_9181;
		this.field_12264 = arg.field_9180;
		this.field_12263 = i;
		this.field_12262 = j;
		this.field_12266 = Lists.<byte[]>newArrayList();
		this.field_12261 = Lists.<byte[]>newArrayList();

		for (int k = 0; k < 18; k++) {
			if ((this.field_12263 & 1 << k) != 0) {
				class_2804 lv = arg2.method_15562(class_1944.field_9284).method_15544(class_4076.method_18681(arg, -1 + k));
				if (lv != null && !lv.method_12146()) {
					this.field_12266.add(lv.method_12137().clone());
				} else {
					this.field_12263 &= ~(1 << k);
					if (lv != null) {
						this.field_16418 |= 1 << k;
					}
				}
			}

			if ((this.field_12262 & 1 << k) != 0) {
				class_2804 lv = arg2.method_15562(class_1944.field_9282).method_15544(class_4076.method_18681(arg, -1 + k));
				if (lv != null && !lv.method_12146()) {
					this.field_12261.add(lv.method_12137().clone());
				} else {
					this.field_12262 &= ~(1 << k);
					if (lv != null) {
						this.field_16417 |= 1 << k;
					}
				}
			}
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12265 = arg.method_10816();
		this.field_12264 = arg.method_10816();
		this.field_12263 = arg.method_10816();
		this.field_12262 = arg.method_10816();
		this.field_16418 = arg.method_10816();
		this.field_16417 = arg.method_10816();
		this.field_12266 = Lists.<byte[]>newArrayList();

		for (int i = 0; i < 18; i++) {
			if ((this.field_12263 & 1 << i) != 0) {
				this.field_12266.add(arg.method_10803(2048));
			}
		}

		this.field_12261 = Lists.<byte[]>newArrayList();

		for (int ix = 0; ix < 18; ix++) {
			if ((this.field_12262 & 1 << ix) != 0) {
				this.field_12261.add(arg.method_10803(2048));
			}
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.method_10804(this.field_12265);
		arg.method_10804(this.field_12264);
		arg.method_10804(this.field_12263);
		arg.method_10804(this.field_12262);
		arg.method_10804(this.field_16418);
		arg.method_10804(this.field_16417);

		for (byte[] bs : this.field_12266) {
			arg.method_10813(bs);
		}

		for (byte[] bs : this.field_12261) {
			arg.method_10813(bs);
		}
	}

	public void method_11560(class_2602 arg) {
		arg.method_11143(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11558() {
		return this.field_12265;
	}

	@Environment(EnvType.CLIENT)
	public int method_11554() {
		return this.field_12264;
	}

	@Environment(EnvType.CLIENT)
	public int method_11556() {
		return this.field_12263;
	}

	@Environment(EnvType.CLIENT)
	public int method_16124() {
		return this.field_16418;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11555() {
		return this.field_12266;
	}

	@Environment(EnvType.CLIENT)
	public int method_11559() {
		return this.field_12262;
	}

	@Environment(EnvType.CLIENT)
	public int method_16125() {
		return this.field_16417;
	}

	@Environment(EnvType.CLIENT)
	public List<byte[]> method_11557() {
		return this.field_12261;
	}
}
