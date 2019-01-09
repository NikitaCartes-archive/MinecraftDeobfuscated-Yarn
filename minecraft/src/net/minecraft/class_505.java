package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_505 {
	private class_1860 field_3079;
	private final List<class_505.class_506> field_3081 = Lists.<class_505.class_506>newArrayList();
	private float field_3080;

	public void method_2571() {
		this.field_3079 = null;
		this.field_3081.clear();
		this.field_3080 = 0.0F;
	}

	public void method_2569(class_1856 arg, int i, int j) {
		this.field_3081.add(new class_505.class_506(arg, i, j));
	}

	public class_505.class_506 method_2570(int i) {
		return (class_505.class_506)this.field_3081.get(i);
	}

	public int method_2572() {
		return this.field_3081.size();
	}

	@Nullable
	public class_1860 method_2566() {
		return this.field_3079;
	}

	public void method_2565(class_1860 arg) {
		this.field_3079 = arg;
	}

	public void method_2567(class_310 arg, int i, int j, boolean bl, float f) {
		if (!class_437.method_2238()) {
			this.field_3080 += f;
		}

		class_308.method_1453();
		GlStateManager.disableLighting();

		for (int k = 0; k < this.field_3081.size(); k++) {
			class_505.class_506 lv = (class_505.class_506)this.field_3081.get(k);
			int l = lv.method_2574() + i;
			int m = lv.method_2575() + j;
			if (k == 0 && bl) {
				class_332.method_1785(l - 4, m - 4, l + 20, m + 20, 822018048);
			} else {
				class_332.method_1785(l, m, l + 16, m + 16, 822018048);
			}

			class_1799 lv2 = lv.method_2573();
			class_918 lv3 = arg.method_1480();
			lv3.method_4026(arg.field_1724, lv2, l, m);
			GlStateManager.depthFunc(516);
			class_332.method_1785(l, m, l + 16, m + 16, 822083583);
			GlStateManager.depthFunc(515);
			if (k == 0) {
				lv3.method_4025(arg.field_1772, lv2, l, m);
			}

			GlStateManager.enableLighting();
		}

		class_308.method_1450();
	}

	@Environment(EnvType.CLIENT)
	public class class_506 {
		private final class_1856 field_3082;
		private final int field_3084;
		private final int field_3083;

		public class_506(class_1856 arg2, int i, int j) {
			this.field_3082 = arg2;
			this.field_3084 = i;
			this.field_3083 = j;
		}

		public int method_2574() {
			return this.field_3084;
		}

		public int method_2575() {
			return this.field_3083;
		}

		public class_1799 method_2573() {
			class_1799[] lvs = this.field_3082.method_8105();
			return lvs[class_3532.method_15375(class_505.this.field_3080 / 30.0F) % lvs.length];
		}
	}
}
