package net.minecraft;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2708 implements class_2596<class_2602> {
	private double field_12395;
	private double field_12392;
	private double field_12390;
	private float field_12393;
	private float field_12391;
	private Set<class_2708.class_2709> field_12396;
	private int field_12394;

	public class_2708() {
	}

	public class_2708(double d, double e, double f, float g, float h, Set<class_2708.class_2709> set, int i) {
		this.field_12395 = d;
		this.field_12392 = e;
		this.field_12390 = f;
		this.field_12393 = g;
		this.field_12391 = h;
		this.field_12396 = set;
		this.field_12394 = i;
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12395 = arg.readDouble();
		this.field_12392 = arg.readDouble();
		this.field_12390 = arg.readDouble();
		this.field_12393 = arg.readFloat();
		this.field_12391 = arg.readFloat();
		this.field_12396 = class_2708.class_2709.method_11744(arg.readUnsignedByte());
		this.field_12394 = arg.method_10816();
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeDouble(this.field_12395);
		arg.writeDouble(this.field_12392);
		arg.writeDouble(this.field_12390);
		arg.writeFloat(this.field_12393);
		arg.writeFloat(this.field_12391);
		arg.writeByte(class_2708.class_2709.method_11741(this.field_12396));
		arg.method_10804(this.field_12394);
	}

	public void method_11740(class_2602 arg) {
		arg.method_11157(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_11734() {
		return this.field_12395;
	}

	@Environment(EnvType.CLIENT)
	public double method_11735() {
		return this.field_12392;
	}

	@Environment(EnvType.CLIENT)
	public double method_11738() {
		return this.field_12390;
	}

	@Environment(EnvType.CLIENT)
	public float method_11736() {
		return this.field_12393;
	}

	@Environment(EnvType.CLIENT)
	public float method_11739() {
		return this.field_12391;
	}

	@Environment(EnvType.CLIENT)
	public int method_11737() {
		return this.field_12394;
	}

	@Environment(EnvType.CLIENT)
	public Set<class_2708.class_2709> method_11733() {
		return this.field_12396;
	}

	public static enum class_2709 {
		field_12400(0),
		field_12398(1),
		field_12403(2),
		field_12401(3),
		field_12397(4);

		private final int field_12399;

		private class_2709(int j) {
			this.field_12399 = j;
		}

		private int method_11742() {
			return 1 << this.field_12399;
		}

		private boolean method_11743(int i) {
			return (i & this.method_11742()) == this.method_11742();
		}

		public static Set<class_2708.class_2709> method_11744(int i) {
			Set<class_2708.class_2709> set = EnumSet.noneOf(class_2708.class_2709.class);

			for (class_2708.class_2709 lv : values()) {
				if (lv.method_11743(i)) {
					set.add(lv);
				}
			}

			return set;
		}

		public static int method_11741(Set<class_2708.class_2709> set) {
			int i = 0;

			for (class_2708.class_2709 lv : set) {
				i |= lv.method_11742();
			}

			return i;
		}
	}
}
