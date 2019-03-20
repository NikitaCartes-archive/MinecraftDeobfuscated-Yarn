package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

@Environment(EnvType.CLIENT)
public class ClickableScrolledSelectionList extends AbstractListWidget {
	private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;

	public ClickableScrolledSelectionList(RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsClickableScrolledSelectionList = realmsClickableScrolledSelectionList;
	}

	@Override
	public int getEntryCount() {
		return this.realmsClickableScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectEntry(int i, int j, double d, double e) {
		return this.realmsClickableScrolledSelectionList.selectItem(i, j, d, e);
	}

	@Override
	public boolean isSelectedEntry(int i) {
		return this.realmsClickableScrolledSelectionList.isSelectedItem(i);
	}

	@Override
	public void drawBackground() {
		this.realmsClickableScrolledSelectionList.renderBackground();
	}

	@Override
	public void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.realmsClickableScrolledSelectionList.renderItem(i, j, k, l, m, n);
	}

	public int width() {
		return this.width;
	}

	@Override
	public int getMaxScrollPosition() {
		return this.realmsClickableScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
	}

	public void itemClicked(int i, int j, int k, int l, int m) {
		this.realmsClickableScrolledSelectionList.itemClicked(i, j, (double)k, (double)l, m);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.realmsClickableScrolledSelectionList.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsClickableScrolledSelectionList.mouseClicked(d, e, i) ? true : access$001(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsClickableScrolledSelectionList.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsClickableScrolledSelectionList.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	public void renderSelected(int i, int j, int k, Tezzelator tezzelator) {
		this.realmsClickableScrolledSelectionList.renderSelected(i, j, k, tezzelator);
	}

	@Override
	public void drawEntries(int i, int j, int k, int l, float f) {
		int m = this.getEntryCount();

		for (int n = 0; n < m; n++) {
			int o = j + n * this.entryHeight + this.headerHeight;
			int p = this.entryHeight - 4;
			if (o > this.bottom || o + p < this.y) {
				this.updateItemPosition(n, i, o, f);
			}

			if (this.renderSelection && this.isSelectedEntry(n)) {
				this.renderSelected(this.width, o, p, Tezzelator.instance);
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

	public int headerHeight() {
		return this.headerHeight;
	}

	public double getScrollY() {
		return this.scrollY;
	}

	public int itemHeight() {
		return this.entryHeight;
	}
}
