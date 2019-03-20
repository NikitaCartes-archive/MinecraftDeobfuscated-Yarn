package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.ScrolledSelectionList;

@Environment(EnvType.CLIENT)
public abstract class RealmsScrolledSelectionList extends RealmsGuiEventListener {
	private final ScrolledSelectionList proxy;

	public RealmsScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new ScrolledSelectionList(this, i, j, k, l, m);
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

	public void scroll(int i) {
		this.proxy.scroll(i);
	}

	public int getScroll() {
		return this.proxy.getScrollY();
	}

	protected void renderList(int i, int j, int k, int l) {
	}

	@Override
	public InputListener getProxy() {
		return this.proxy;
	}
}
