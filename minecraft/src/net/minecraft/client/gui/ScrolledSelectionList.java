package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.realms.RealmsScrolledSelectionList;

@Environment(EnvType.CLIENT)
public class ScrolledSelectionList extends AbstractListWidget {
	private final RealmsScrolledSelectionList realmsScrolledSelectionList;

	public ScrolledSelectionList(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsScrolledSelectionList = realmsScrolledSelectionList;
	}

	@Override
	public int getEntryCount() {
		return this.realmsScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectEntry(int i, int j, double d, double e) {
		return this.realmsScrolledSelectionList.selectItem(i, j, d, e);
	}

	@Override
	public boolean isSelectedEntry(int i) {
		return this.realmsScrolledSelectionList.isSelectedItem(i);
	}

	@Override
	public void drawBackground() {
		this.realmsScrolledSelectionList.renderBackground();
	}

	@Override
	public void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.realmsScrolledSelectionList.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxScrollPosition() {
		return this.realmsScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsScrolledSelectionList.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.realmsScrolledSelectionList.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsScrolledSelectionList.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsScrolledSelectionList.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsScrolledSelectionList.mouseDragged(d, e, i, f, g);
	}
}
