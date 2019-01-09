package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_514 extends class_339 {
	private static final class_2960 field_3143 = new class_2960("textures/gui/recipe_book.png");
	private class_1729<?> field_3137;
	private class_3439 field_3138;
	private class_516 field_3142;
	private float field_3140;
	private float field_3139;
	private int field_3141;

	public class_514() {
		super(0, 0, 0, 25, 25, "");
	}

	public void method_2640(class_516 arg, class_513 arg2) {
		this.field_3142 = arg;
		this.field_3137 = (class_1729<?>)arg2.method_2637().field_1724.field_7512;
		this.field_3138 = arg2.method_2633();
		List<class_1860> list = arg.method_2651(this.field_3138.method_14880(this.field_3137));

		for (class_1860 lv : list) {
			if (this.field_3138.method_14883(lv)) {
				arg2.method_2629(list);
				this.field_3139 = 15.0F;
				break;
			}
		}
	}

	public class_516 method_2645() {
		return this.field_3142;
	}

	public void method_2641(int i, int j) {
		this.field_2069 = i;
		this.field_2068 = j;
	}

	@Override
	public void method_1824(int i, int j, float f) {
		if (this.field_2076) {
			if (!class_437.method_2238()) {
				this.field_3140 += f;
			}

			this.field_2075 = i >= this.field_2069 && j >= this.field_2068 && i < this.field_2069 + this.field_2071 && j < this.field_2068 + this.field_2070;
			class_308.method_1453();
			class_310 lv = class_310.method_1551();
			lv.method_1531().method_4618(field_3143);
			GlStateManager.disableLighting();
			int k = 29;
			if (!this.field_3142.method_2655()) {
				k += 25;
			}

			int l = 206;
			if (this.field_3142.method_2651(this.field_3138.method_14880(this.field_3137)).size() > 1) {
				l += 25;
			}

			boolean bl = this.field_3139 > 0.0F;
			if (bl) {
				float g = 1.0F + 0.1F * (float)Math.sin((double)(this.field_3139 / 15.0F * (float) Math.PI));
				GlStateManager.pushMatrix();
				GlStateManager.translatef((float)(this.field_2069 + 8), (float)(this.field_2068 + 12), 0.0F);
				GlStateManager.scalef(g, g, 1.0F);
				GlStateManager.translatef((float)(-(this.field_2069 + 8)), (float)(-(this.field_2068 + 12)), 0.0F);
				this.field_3139 -= f;
			}

			this.method_1788(this.field_2069, this.field_2068, k, l, this.field_2071, this.field_2070);
			List<class_1860> list = this.method_2639();
			this.field_3141 = class_3532.method_15375(this.field_3140 / 30.0F) % list.size();
			class_1799 lv2 = ((class_1860)list.get(this.field_3141)).method_8110();
			int m = 4;
			if (this.field_3142.method_2656() && this.method_2639().size() > 1) {
				lv.method_1480().method_4023(lv2, this.field_2069 + m + 1, this.field_2068 + m + 1);
				m--;
			}

			lv.method_1480().method_4023(lv2, this.field_2069 + m, this.field_2068 + m);
			if (bl) {
				GlStateManager.popMatrix();
			}

			GlStateManager.enableLighting();
			class_308.method_1450();
		}
	}

	private List<class_1860> method_2639() {
		List<class_1860> list = this.field_3142.method_2648(true);
		if (!this.field_3138.method_14880(this.field_3137)) {
			list.addAll(this.field_3142.method_2648(false));
		}

		return list;
	}

	public boolean method_2642() {
		return this.method_2639().size() == 1;
	}

	public class_1860 method_2643() {
		List<class_1860> list = this.method_2639();
		return (class_1860)list.get(this.field_3141);
	}

	public List<String> method_2644(class_437 arg) {
		class_1799 lv = ((class_1860)this.method_2639().get(this.field_3141)).method_8110();
		List<String> list = arg.method_2239(lv);
		if (this.field_3142.method_2651(this.field_3138.method_14880(this.field_3137)).size() > 1) {
			list.add(class_1074.method_4662("gui.recipebook.moreRecipes"));
		}

		return list;
	}

	@Override
	public int method_1825() {
		return 25;
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		if (i == 0 || i == 1) {
			boolean bl = this.method_1829(d, e);
			if (bl) {
				this.method_1832(class_310.method_1551().method_1483());
				if (i == 0) {
					this.method_1826(d, e);
				}

				return true;
			}
		}

		return false;
	}
}
