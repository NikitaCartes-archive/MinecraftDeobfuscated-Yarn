package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ListWidget;

@Environment(EnvType.CLIENT)
public class RealmsClickableScrolledSelectionListProxy extends ListWidget {
	private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;

	public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsClickableScrolledSelectionList = realmsClickableScrolledSelectionList;
	}

	@Override
	public int getItemCount() {
		return this.realmsClickableScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectItem(int index, int button, double mouseX, double mouseY) {
		return this.realmsClickableScrolledSelectionList.selectItem(index, button, mouseX, mouseY);
	}

	@Override
	public boolean isSelectedItem(int index) {
		return this.realmsClickableScrolledSelectionList.isSelectedItem(index);
	}

	@Override
	public void renderBackground() {
		this.realmsClickableScrolledSelectionList.renderBackground();
	}

	@Override
	public void renderItem(int index, int y, int i, int j, int k, int l, float f) {
		this.realmsClickableScrolledSelectionList.renderItem(index, y, i, j, k, l);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxPosition() {
		return this.realmsClickableScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
	}

	public void itemClicked(int i, int j, int k, int l, int m) {
		this.realmsClickableScrolledSelectionList.itemClicked(i, j, (double)k, (double)l, m);
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		return this.realmsClickableScrolledSelectionList.mouseScrolled(d, e, amount) ? true : super.mouseScrolled(d, e, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.realmsClickableScrolledSelectionList.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.realmsClickableScrolledSelectionList.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.realmsClickableScrolledSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
			? true
			: super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	public void renderSelected(int i, int j, int k, Tezzelator tezzelator) {
		this.realmsClickableScrolledSelectionList.renderSelected(i, j, k, tezzelator);
	}

	@Override
	public void renderList(int x, int y, int mouseX, int mouseY, float f) {
		int i = this.getItemCount();

		for (int j = 0; j < i; j++) {
			int k = y + j * this.itemHeight + this.headerHeight;
			int l = this.itemHeight - 4;
			if (k > this.bottom || k + l < this.top) {
				this.updateItemPosition(j, x, k, f);
			}

			if (this.renderSelection && this.isSelectedItem(j)) {
				this.renderSelected(this.width, k, l, Tezzelator.instance);
			}

			this.renderItem(j, x, k, l, mouseX, mouseY, f);
		}
	}

	public int y0() {
		return this.top;
	}

	public int y1() {
		return this.bottom;
	}

	public int headerHeight() {
		return this.headerHeight;
	}

	public double yo() {
		return this.scroll;
	}

	public int itemHeight() {
		return this.itemHeight;
	}
}
