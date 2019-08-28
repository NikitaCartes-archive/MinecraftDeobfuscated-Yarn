package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
enum AdvancementTabType {
	ABOVE(0, 0, 28, 32, 8),
	BELOW(84, 0, 28, 32, 8),
	LEFT(0, 64, 32, 28, 5),
	RIGHT(96, 64, 32, 28, 5);

	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int tabCount;

	private AdvancementTabType(int j, int k, int l, int m, int n) {
		this.u = j;
		this.v = k;
		this.width = l;
		this.height = m;
		this.tabCount = n;
	}

	public int getTabCount() {
		return this.tabCount;
	}

	public void drawBackground(DrawableHelper drawableHelper, int i, int j, boolean bl, int k) {
		int l = this.u;
		if (k > 0) {
			l += this.width;
		}

		if (k == this.tabCount - 1) {
			l += this.width;
		}

		int m = bl ? this.v + this.height : this.v;
		drawableHelper.blit(i + this.getTabX(k), j + this.getTabY(k), l, m, this.width, this.height);
	}

	public void drawIcon(int i, int j, int k, ItemRenderer itemRenderer, ItemStack itemStack) {
		int l = i + this.getTabX(k);
		int m = j + this.getTabY(k);
		switch (this) {
			case ABOVE:
				l += 6;
				m += 9;
				break;
			case BELOW:
				l += 6;
				m += 6;
				break;
			case LEFT:
				l += 10;
				m += 5;
				break;
			case RIGHT:
				l += 6;
				m += 5;
		}

		itemRenderer.renderGuiItem(null, itemStack, l, m);
	}

	public int getTabX(int i) {
		switch (this) {
			case ABOVE:
				return (this.width + 4) * i;
			case BELOW:
				return (this.width + 4) * i;
			case LEFT:
				return -this.width + 4;
			case RIGHT:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int getTabY(int i) {
		switch (this) {
			case ABOVE:
				return -this.height + 4;
			case BELOW:
				return 136;
			case LEFT:
				return this.height * i;
			case RIGHT:
				return this.height * i;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean isClickOnTab(int i, int j, int k, double d, double e) {
		int l = i + this.getTabX(k);
		int m = j + this.getTabY(k);
		return d > (double)l && d < (double)(l + this.width) && e > (double)m && e < (double)(m + this.height);
	}
}
