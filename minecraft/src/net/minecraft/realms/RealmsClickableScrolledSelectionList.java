package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(EnvType.CLIENT)
public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
	private final RealmsClickableScrolledSelectionListProxy proxy;

	public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new RealmsClickableScrolledSelectionListProxy(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.render(i, j, f);
	}

	public int width() {
		return this.proxy.getWidth();
	}

	protected void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
	}

	public void renderItem(int i, int j, int k, int l, int m, int n) {
		this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
	}

	public int getItemCount() {
		return 0;
	}

	public boolean selectItem(int i, int j, double d, double e) {
		return true;
	}

	public boolean isSelectedItem(int i) {
		return false;
	}

	public void renderBackground() {
	}

	public int getMaxPosition() {
		return 0;
	}

	public int getScrollbarPosition() {
		return this.proxy.getWidth() / 2 + 124;
	}

	@Override
	public Element getProxy() {
		return this.proxy;
	}

	public void scroll(int i) {
		this.proxy.scroll(i);
	}

	public int getScroll() {
		return this.proxy.getScroll();
	}

	protected void renderList(int i, int j, int k, int l) {
	}

	public void itemClicked(int i, int j, double d, double e, int k) {
	}

	public void renderSelected(int i, int j, int k, Tezzelator tezzelator) {
	}

	public void setLeftPos(int i) {
		this.proxy.setLeftPos(i);
	}

	public int y0() {
		return this.proxy.y0();
	}

	public int y1() {
		return this.proxy.y1();
	}

	public int headerHeight() {
		return this.proxy.headerHeight();
	}

	public double yo() {
		return this.proxy.yo();
	}

	public int itemHeight() {
		return this.proxy.itemHeight();
	}

	public boolean isVisible() {
		return this.proxy.isVisible();
	}
}
