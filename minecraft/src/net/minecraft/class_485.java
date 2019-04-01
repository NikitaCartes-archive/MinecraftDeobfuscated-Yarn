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
	protected void init() {
		super.init();
		this.method_2476();
	}

	protected void method_2476() {
		if (this.minecraft.field_1724.method_6026().isEmpty()) {
			this.field_2776 = (this.width - this.field_2792) / 2;
			this.field_2900 = false;
		} else {
			this.field_2776 = 160 + (this.width - this.field_2792 - 200) / 2;
			this.field_2900 = true;
		}
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		if (this.field_2900) {
			this.method_2477();
		}
	}

	private void method_2477() {
		int i = this.field_2776 - 124;
		Collection<class_1293> collection = this.minecraft.field_1724.method_6026();
		if (!collection.isEmpty()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			int j = 33;
			if (collection.size() > 5) {
				j = 132 / (collection.size() - 1);
			}

			Iterable<class_1293> iterable = Ordering.natural().sortedCopy(collection);
			this.method_18642(i, j, iterable);
			this.method_18643(i, j, iterable);
			this.method_18644(i, j, iterable);
		}
	}

	private void method_18642(int i, int j, Iterable<class_1293> iterable) {
		this.minecraft.method_1531().method_4618(field_2801);
		int k = this.field_2800;

		for (class_1293 lv : iterable) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.blit(i, k, 0, 166, 140, 32);
			k += j;
		}
	}

	private void method_18643(int i, int j, Iterable<class_1293> iterable) {
		this.minecraft.method_1531().method_4618(class_1059.field_18229);
		class_4074 lv = this.minecraft.method_18505();
		int k = this.field_2800;

		for (class_1293 lv2 : iterable) {
			class_1291 lv3 = lv2.method_5579();
			this.blit(i + 6, k + 7, lv.method_18663(lv3), 18, 18);
			k += j;
		}
	}

	private void method_18644(int i, int j, Iterable<class_1293> iterable) {
		int k = this.field_2800;

		for (class_1293 lv : iterable) {
			String string = class_1074.method_4662(lv.method_5579().method_5567());
			if (lv.method_5578() >= 1 && lv.method_5578() <= 9) {
				string = string + ' ' + class_1074.method_4662("enchantment.level." + (lv.method_5578() + 1));
			}

			this.font.method_1720(string, (float)(i + 10 + 18), (float)(k + 6), 16777215);
			String string2 = class_1292.method_5577(lv, 1.0F);
			this.font.method_1720(string2, (float)(i + 10 + 18), (float)(k + 6 + 10), 8355711);
			k += j;
		}
	}
}
