package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

@Environment(EnvType.CLIENT)
public class class_400 extends class_358 {
	private final RealmsClickableScrolledSelectionList field_2342;

	public class_400(RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(class_310.method_1551(), i, j, k, l, m);
		this.field_2342 = realmsClickableScrolledSelectionList;
	}

	@Override
	protected int method_1947() {
		return this.field_2342.getItemCount();
	}

	@Override
	protected boolean method_1937(int i, int j, double d, double e) {
		return this.field_2342.selectItem(i, j, d, e);
	}

	@Override
	protected boolean method_1955(int i) {
		return this.field_2342.isSelectedItem(i);
	}

	@Override
	protected void method_1936() {
		this.field_2342.renderBackground();
	}

	@Override
	protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2342.renderItem(i, j, k, l, m, n);
	}

	public int method_2085() {
		return this.field_2168;
	}

	@Override
	protected int method_1928() {
		return this.field_2342.getMaxPosition();
	}

	@Override
	protected int method_1948() {
		return this.field_2342.getScrollbarPosition();
	}

	@Override
	public boolean method_16802(double d) {
		return this.field_2342.mouseScrolled(d) ? true : super.method_16802(d);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		return this.field_2342.mouseClicked(d, e, i) ? true : method_2092(this, d, e, i);
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		return this.field_2342.mouseReleased(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		return this.field_2342.mouseDragged(d, e, i, f, g) ? true : super.method_16801(d, e, i, f, g);
	}

	public void method_2088(int i, int j, int k, Tezzelator tezzelator) {
		this.field_2342.renderSelected(i, j, k, tezzelator);
	}

	@Override
	protected void method_1950(int i, int j, int k, int l, float f) {
		int m = this.method_1947();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.field_2179 + this.field_2174;
			int p = this.field_2179 - 4;
			if (o > this.field_2165 || o + p < this.field_2166) {
				this.method_1952(n, i, o, f);
			}

			if (this.field_2171 && this.method_1955(n)) {
				this.method_2088(this.field_2168, o, p, Tezzelator.instance);
			}

			this.method_1935(n, i, o, p, k, l, f);
		}
	}

	public int method_2089() {
		return this.field_2166;
	}

	public int method_2090() {
		return this.field_2165;
	}

	public int method_2091() {
		return this.field_2174;
	}

	public double method_2086() {
		return this.field_2175;
	}

	public int method_2087() {
		return this.field_2179;
	}
}
