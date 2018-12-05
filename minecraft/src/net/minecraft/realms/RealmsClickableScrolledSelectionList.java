package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_400;
import net.minecraft.client.gui.GuiEventListener;

@Environment(EnvType.CLIENT)
public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
	private final class_400 proxy;

	public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new class_400(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.draw(i, j, f);
	}

	public int width() {
		return this.proxy.width();
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
		return this.proxy.width() / 2 + 124;
	}

	@Override
	public GuiEventListener getProxy() {
		return this.proxy;
	}

	public void scroll(int i) {
		this.proxy.scrollByY(i);
	}

	public int getScroll() {
		return this.proxy.getScrollY();
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

	public int method_1915() {
		return this.proxy.method_2089();
	}

	public int method_1916() {
		return this.proxy.method_2090();
	}

	public int headerHeight() {
		return this.proxy.method_2091();
	}

	public double method_1917() {
		return this.proxy.method_2086();
	}

	public int itemHeight() {
		return this.proxy.method_2087();
	}

	public boolean isVisible() {
		return this.proxy.method_1939();
	}
}
