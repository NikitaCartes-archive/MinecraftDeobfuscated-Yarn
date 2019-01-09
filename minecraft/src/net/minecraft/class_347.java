package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_347 extends class_339 {
	private boolean field_2131;

	public class_347(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	public boolean method_1896() {
		return this.field_2131;
	}

	public void method_1895(boolean bl) {
		this.field_2131 = bl;
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			class_310.method_1551().method_1531().method_4618(class_339.field_2072);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean bl = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			class_347.class_348 lv;
			if (this.field_2131) {
				if (!this.field_2078) {
					lv = class_347.class_348.field_2139;
				} else if (bl) {
					lv = class_347.class_348.field_2138;
				} else {
					lv = class_347.class_348.field_2137;
				}
			} else if (!this.field_2078) {
				lv = class_347.class_348.field_2140;
			} else if (bl) {
				lv = class_347.class_348.field_2133;
			} else {
				lv = class_347.class_348.field_2132;
			}

			this.method_1788(this.field_2069, this.field_2068, lv.method_1897(), lv.method_1898(), this.field_2071, this.field_2070);
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_348 {
		field_2137(0, 146),
		field_2138(0, 166),
		field_2139(0, 186),
		field_2132(20, 146),
		field_2133(20, 166),
		field_2140(20, 186);

		private final int field_2135;
		private final int field_2134;

		private class_348(int j, int k) {
			this.field_2135 = j;
			this.field_2134 = k;
		}

		public int method_1897() {
			return this.field_2135;
		}

		public int method_1898() {
			return this.field_2134;
		}
	}
}
