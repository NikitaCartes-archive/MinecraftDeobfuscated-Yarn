package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_364;
import net.minecraft.class_400;

@Environment(EnvType.CLIENT)
public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
	private final class_400 proxy;

	public RealmsClickableScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new class_400(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.method_1930(i, j, f);
	}

	public int width() {
		return this.proxy.method_2085();
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
		return this.proxy.method_2085() / 2 + 124;
	}

	@Override
	public class_364 getProxy() {
		return this.proxy;
	}

	public void scroll(int i) {
		this.proxy.method_1951(i);
	}

	public int getScroll() {
		return this.proxy.method_1944();
	}

	protected void renderList(int i, int j, int k, int l) {
	}

	public void itemClicked(int i, int j, double d, double e, int k) {
	}

	public void renderSelected(int i, int j, int k, Tezzelator tezzelator) {
	}

	public void setLeftPos(int i) {
		this.proxy.method_1945(i);
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
