package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsScrolledSelectionList;

@Environment(EnvType.CLIENT)
public class class_402 extends class_358 {
	private final RealmsScrolledSelectionList field_2344;

	public class_402(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(class_310.method_1551(), i, j, k, l, m);
		this.field_2344 = realmsScrolledSelectionList;
	}

	@Override
	protected int method_1947() {
		return this.field_2344.getItemCount();
	}

	@Override
	protected boolean method_1937(int i, int j, double d, double e) {
		return this.field_2344.selectItem(i, j, d, e);
	}

	@Override
	protected boolean method_1955(int i) {
		return this.field_2344.isSelectedItem(i);
	}

	@Override
	protected void method_1936() {
		this.field_2344.renderBackground();
	}

	@Override
	protected void method_1935(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2344.renderItem(i, j, k, l, m, n);
	}

	public int method_2094() {
		return this.field_2168;
	}

	@Override
	protected int method_1928() {
		return this.field_2344.getMaxPosition();
	}

	@Override
	protected int method_1948() {
		return this.field_2344.getScrollbarPosition();
	}

	@Override
	public boolean method_16802(double d) {
		return this.field_2344.mouseScrolled(d) ? true : super.method_16802(d);
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		return this.field_2344.mouseClicked(d, e, i) ? true : super.method_16807(d, e, i);
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		return this.field_2344.mouseReleased(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		return this.field_2344.mouseDragged(d, e, i, f, g);
	}
}
