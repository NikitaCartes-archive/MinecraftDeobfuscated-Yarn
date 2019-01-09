package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_364;
import net.minecraft.class_402;

@Environment(EnvType.CLIENT)
public abstract class RealmsScrolledSelectionList extends RealmsGuiEventListener {
	private final class_402 proxy;

	public RealmsScrolledSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new class_402(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.method_1930(i, j, f);
	}

	public int width() {
		return this.proxy.method_2094();
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
		return this.proxy.method_2094() / 2 + 124;
	}

	public void scroll(int i) {
		this.proxy.method_1951(i);
	}

	public int getScroll() {
		return this.proxy.method_1944();
	}

	protected void renderList(int i, int j, int k, int l) {
	}

	@Override
	public class_364 getProxy() {
		return this.proxy;
	}
}
