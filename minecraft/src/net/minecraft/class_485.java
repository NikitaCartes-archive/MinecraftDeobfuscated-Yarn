package net.minecraft;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_485<T extends class_1703> extends class_465<T> {
	protected boolean field_2900;

	public class_485(T arg, class_1661 arg2, class_2561 arg3) {
		super(arg, arg2, arg3);
	}

	@Override
	protected void method_2224() {
		super.method_2224();
		this.method_2476();
	}

	protected void method_2476() {
		if (this.field_2563.field_1724.method_6026().isEmpty()) {
			this.field_2776 = (this.field_2561 - this.field_2792) / 2;
			this.field_2900 = false;
		} else {
			this.field_2776 = 160 + (this.field_2561 - this.field_2792 - 200) / 2;
			this.field_2900 = true;
		}
	}

	@Override
	public void method_2214(int i, int j, float f) {
		super.method_2214(i, j, f);
		if (this.field_2900) {
			this.method_2477();
		}
	}

	private void method_2477() {
		int i = this.field_2776 - 124;
		int j = this.field_2800;
		int k = 166;
		Collection<class_1293> collection = this.field_2563.field_1724.method_6026();
		if (!collection.isEmpty()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			int l = 33;
			if (collection.size() > 5) {
				l = 132 / (collection.size() - 1);
			}

			for (class_1293 lv : Ordering.natural().sortedCopy(collection)) {
				class_1291 lv2 = lv.method_5579();
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.field_2563.method_1531().method_4618(field_2801);
				this.method_1788(i, j, 0, 166, 140, 32);
				if (lv2.method_5568()) {
					int m = lv2.method_5553();
					this.method_1788(i + 6, j + 7, m % 12 * 18, 198 + m / 12 * 18, 18, 18);
				}

				String string = class_1074.method_4662(lv2.method_5567());
				if (lv.method_5578() >= 1 && lv.method_5578() <= 9) {
					string = string + ' ' + class_1074.method_4662("enchantment.level." + (lv.method_5578() + 1));
				}

				this.field_2554.method_1720(string, (float)(i + 10 + 18), (float)(j + 6), 16777215);
				String string2 = class_1292.method_5577(lv, 1.0F);
				this.field_2554.method_1720(string2, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
				j += l;
			}
		}
	}
}
