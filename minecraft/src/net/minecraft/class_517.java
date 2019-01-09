package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_517 extends class_507 {
	private Iterator<class_1792> field_3153;
	private Set<class_1792> field_3149;
	private class_1735 field_3150;
	private class_1792 field_3152;
	private float field_3151;

	@Override
	protected boolean method_2589() {
		boolean bl = !this.method_17061();
		this.method_17060(bl);
		return bl;
	}

	protected abstract boolean method_17061();

	protected abstract void method_17060(boolean bl);

	@Override
	public boolean method_2605() {
		return this.method_17062();
	}

	protected abstract boolean method_17062();

	@Override
	protected void method_2593(boolean bl) {
		this.method_17063(bl);
		if (!bl) {
			this.field_3086.method_2638();
		}

		this.method_2588();
	}

	protected abstract void method_17063(boolean bl);

	@Override
	protected void method_2585() {
		this.field_3088.method_1962(152, 182, 28, 18, field_3097);
	}

	@Override
	protected String method_2599() {
		return class_1074.method_4662(this.field_3088.method_1965() ? this.method_17064() : "gui.recipebook.toggleRecipes.all");
	}

	protected abstract String method_17064();

	@Override
	public void method_2600(@Nullable class_1735 arg) {
		super.method_2600(arg);
		if (arg != null && arg.field_7874 < this.field_3095.method_7658()) {
			this.field_3150 = null;
		}
	}

	@Override
	public void method_2596(class_1860 arg, List<class_1735> list) {
		class_1799 lv = arg.method_8110();
		this.field_3092.method_2565(arg);
		this.field_3092.method_2569(class_1856.method_8101(lv), ((class_1735)list.get(2)).field_7873, ((class_1735)list.get(2)).field_7872);
		class_2371<class_1856> lv2 = arg.method_8117();
		this.field_3150 = (class_1735)list.get(1);
		if (this.field_3149 == null) {
			this.field_3149 = this.method_17065();
		}

		this.field_3153 = this.field_3149.iterator();
		this.field_3152 = null;
		Iterator<class_1856> iterator = lv2.iterator();

		for (int i = 0; i < 2; i++) {
			if (!iterator.hasNext()) {
				return;
			}

			class_1856 lv3 = (class_1856)iterator.next();
			if (!lv3.method_8103()) {
				class_1735 lv4 = (class_1735)list.get(i);
				this.field_3092.method_2569(lv3, lv4.field_7873, lv4.field_7872);
			}
		}
	}

	protected abstract Set<class_1792> method_17065();

	@Override
	public void method_2581(int i, int j, boolean bl, float f) {
		super.method_2581(i, j, bl, f);
		if (this.field_3150 != null) {
			if (!class_437.method_2238()) {
				this.field_3151 += f;
			}

			class_308.method_1453();
			GlStateManager.disableLighting();
			int k = this.field_3150.field_7873 + i;
			int l = this.field_3150.field_7872 + j;
			class_332.method_1785(k, l, k + 16, l + 16, 822018048);
			this.field_3091.method_1480().method_4026(this.field_3091.field_1724, this.method_2658().method_7854(), k, l);
			GlStateManager.depthFunc(516);
			class_332.method_1785(k, l, k + 16, l + 16, 822083583);
			GlStateManager.depthFunc(515);
			GlStateManager.enableLighting();
			class_308.method_1450();
		}
	}

	private class_1792 method_2658() {
		if (this.field_3152 == null || this.field_3151 > 30.0F) {
			this.field_3151 = 0.0F;
			if (this.field_3153 == null || !this.field_3153.hasNext()) {
				if (this.field_3149 == null) {
					this.field_3149 = this.method_17065();
				}

				this.field_3153 = this.field_3149.iterator();
			}

			this.field_3152 = (class_1792)this.field_3153.next();
		}

		return this.field_3152;
	}
}
