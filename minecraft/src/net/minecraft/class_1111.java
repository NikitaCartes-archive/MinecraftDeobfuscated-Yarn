package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1111 implements class_1148<class_1111> {
	private final class_2960 field_5469;
	private final float field_5466;
	private final float field_5464;
	private final int field_5468;
	private final class_1111.class_1112 field_5470;
	private final boolean field_5467;
	private final boolean field_5465;
	private final int field_5463;

	public class_1111(String string, float f, float g, int i, class_1111.class_1112 arg, boolean bl, boolean bl2, int j) {
		this.field_5469 = new class_2960(string);
		this.field_5466 = f;
		this.field_5464 = g;
		this.field_5468 = i;
		this.field_5470 = arg;
		this.field_5467 = bl;
		this.field_5465 = bl2;
		this.field_5463 = j;
	}

	public class_2960 method_4767() {
		return this.field_5469;
	}

	public class_2960 method_4766() {
		return new class_2960(this.field_5469.method_12836(), "sounds/" + this.field_5469.method_12832() + ".ogg");
	}

	public float method_4771() {
		return this.field_5466;
	}

	public float method_4772() {
		return this.field_5464;
	}

	@Override
	public int method_4894() {
		return this.field_5468;
	}

	public class_1111 method_4765() {
		return this;
	}

	public class_1111.class_1112 method_4768() {
		return this.field_5470;
	}

	public boolean method_4769() {
		return this.field_5467;
	}

	public boolean method_4764() {
		return this.field_5465;
	}

	public int method_4770() {
		return this.field_5463;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_1112 {
		field_5474("file"),
		field_5473("event");

		private final String field_5472;

		private class_1112(String string2) {
			this.field_5472 = string2;
		}

		public static class_1111.class_1112 method_4773(String string) {
			for (class_1111.class_1112 lv : values()) {
				if (lv.field_5472.equals(string)) {
					return lv;
				}
			}

			return null;
		}
	}
}
