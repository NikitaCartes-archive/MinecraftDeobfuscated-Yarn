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

	private final int field_2674;
	private final int field_2672;
	private final int field_2671;
	private final int field_2670;
	private final int field_2669;

	private AdvancementTabType(int j, int k, int l, int m, int n) {
		this.field_2674 = j;
		this.field_2672 = k;
		this.field_2671 = l;
		this.field_2670 = m;
		this.field_2669 = n;
	}

	public int method_2304() {
		return this.field_2669;
	}

	public void drawBackground(DrawableHelper drawable, int x, int y, boolean selected, int index) {
		int i = this.field_2674;
		if (index > 0) {
			i += this.field_2671;
		}

		if (index == this.field_2669 - 1) {
			i += this.field_2671;
		}

		int j = selected ? this.field_2672 + this.field_2670 : this.field_2672;
		drawable.blit(x + this.method_2302(index), y + this.method_2305(index), i, j, this.field_2671, this.field_2670);
	}

	public void drawIcon(int x, int y, int index, ItemRenderer itemRenderer, ItemStack icon) {
		int i = x + this.method_2302(index);
		int j = y + this.method_2305(index);
		switch (this) {
			case ABOVE:
				i += 6;
				j += 9;
				break;
			case BELOW:
				i += 6;
				j += 6;
				break;
			case LEFT:
				i += 10;
				j += 5;
				break;
			case RIGHT:
				i += 6;
				j += 5;
		}

		itemRenderer.renderGuiItem(null, icon, i, j);
	}

	public int method_2302(int i) {
		switch (this) {
			case ABOVE:
				return (this.field_2671 + 4) * i;
			case BELOW:
				return (this.field_2671 + 4) * i;
			case LEFT:
				return -this.field_2671 + 4;
			case RIGHT:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int method_2305(int i) {
		switch (this) {
			case ABOVE:
				return -this.field_2670 + 4;
			case BELOW:
				return 136;
			case LEFT:
				return this.field_2670 * i;
			case RIGHT:
				return this.field_2670 * i;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean method_2303(int i, int j, int k, double d, double e) {
		int l = i + this.method_2302(k);
		int m = j + this.method_2305(k);
		return d > (double)l && d < (double)(l + this.field_2671) && e > (double)m && e < (double)(m + this.field_2670);
	}
}
