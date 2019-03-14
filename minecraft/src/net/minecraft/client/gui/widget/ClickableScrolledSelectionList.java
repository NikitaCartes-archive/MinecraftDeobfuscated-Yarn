package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

@Environment(EnvType.CLIENT)
public class ClickableScrolledSelectionList extends AbstractListWidget {
	private final RealmsClickableScrolledSelectionList field_2342;

	public ClickableScrolledSelectionList(RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.field_2342 = realmsClickableScrolledSelectionList;
	}

	@Override
	protected int getEntryCount() {
		return this.field_2342.getItemCount();
	}

	@Override
	protected boolean selectEntry(int i, int j, double d, double e) {
		return this.field_2342.selectItem(i, j, d, e);
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return this.field_2342.isSelectedItem(i);
	}

	@Override
	protected void drawBackground() {
		this.field_2342.renderBackground();
	}

	@Override
	protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2342.renderItem(i, j, k, l, m, n);
	}

	public int width() {
		return this.width;
	}

	@Override
	protected int getMaxScrollPosition() {
		return this.field_2342.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.field_2342.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2342.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2342.mouseClicked(d, e, i) ? true : method_2092(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.field_2342.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.field_2342.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	public void method_2088(int i, int j, int k, Tezzelator tezzelator) {
		this.field_2342.renderSelected(i, j, k, tezzelator);
	}

	@Override
	protected void drawEntries(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.field_2174;
			int p = this.entryHeight - 4;
			if (o > this.bottom || o + p < this.y) {
				this.method_1952(n, i, o, f);
			}

			if (this.field_2171 && this.isSelectedEntry(n)) {
				this.method_2088(this.width, o, p, Tezzelator.instance);
			}

			this.drawEntry(n, i, o, p, k, l, f);
		}
	}

	public int method_2089() {
		return this.y;
	}

	public int method_2090() {
		return this.bottom;
	}

	public int method_2091() {
		return this.field_2174;
	}

	public double getScrollY() {
		return this.scrollY;
	}

	public int method_2087() {
		return this.entryHeight;
	}
}
