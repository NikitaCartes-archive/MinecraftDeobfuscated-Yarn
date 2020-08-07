package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
enum AdvancementTabType {
	field_2678(0, 0, 28, 32, 8),
	field_2673(84, 0, 28, 32, 8),
	field_2675(0, 64, 32, 28, 5),
	field_2677(96, 64, 32, 28, 5);

	private final int u;
	private final int v;
	private final int width;
	private final int height;
	private final int tabCount;

	private AdvancementTabType(int u, int v, int width, int height, int tabCount) {
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
		this.tabCount = tabCount;
	}

	public int getTabCount() {
		return this.tabCount;
	}

	public void drawBackground(MatrixStack matrixStack, DrawableHelper drawableHelper, int i, int j, boolean bl, int k) {
		int l = this.u;
		if (k > 0) {
			l += this.width;
		}

		if (k == this.tabCount - 1) {
			l += this.width;
		}

		int m = bl ? this.v + this.height : this.v;
		drawableHelper.drawTexture(matrixStack, i + this.getTabX(k), j + this.getTabY(k), l, m, this.width, this.height);
	}

	public void drawIcon(int x, int y, int index, ItemRenderer itemRenderer, ItemStack icon) {
		int i = x + this.getTabX(index);
		int j = y + this.getTabY(index);
		switch (this) {
			case field_2678:
				i += 6;
				j += 9;
				break;
			case field_2673:
				i += 6;
				j += 6;
				break;
			case field_2675:
				i += 10;
				j += 5;
				break;
			case field_2677:
				i += 6;
				j += 5;
		}

		itemRenderer.renderInGui(icon, i, j);
	}

	public int getTabX(int index) {
		switch (this) {
			case field_2678:
				return (this.width + 4) * index;
			case field_2673:
				return (this.width + 4) * index;
			case field_2675:
				return -this.width + 4;
			case field_2677:
				return 248;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public int getTabY(int index) {
		switch (this) {
			case field_2678:
				return -this.height + 4;
			case field_2673:
				return 136;
			case field_2675:
				return this.height * index;
			case field_2677:
				return this.height * index;
			default:
				throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
		}
	}

	public boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
		int i = screenX + this.getTabX(index);
		int j = screenY + this.getTabY(index);
		return mouseX > (double)i && mouseX < (double)(i + this.width) && mouseY > (double)j && mouseY < (double)(j + this.height);
	}
}
