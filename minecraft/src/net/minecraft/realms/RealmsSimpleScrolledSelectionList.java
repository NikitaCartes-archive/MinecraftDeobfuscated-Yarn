package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_401;
import net.minecraft.client.gui.GuiEventListener;

@Environment(EnvType.CLIENT)
public abstract class RealmsSimpleScrolledSelectionList extends RealmsGuiEventListener {
	private final class_401 proxy;

	public RealmsSimpleScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new class_401(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.draw(i, j, f);
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
}
