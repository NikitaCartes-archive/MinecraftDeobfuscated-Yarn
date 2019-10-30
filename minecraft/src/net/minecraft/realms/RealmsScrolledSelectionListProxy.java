package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ListWidget;

@Environment(EnvType.CLIENT)
public class RealmsScrolledSelectionListProxy extends ListWidget {
	private final RealmsScrolledSelectionList realmsScrolledSelectionList;

	public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsScrolledSelectionList = realmsScrolledSelectionList;
	}

	@Override
	public int getItemCount() {
		return this.realmsScrolledSelectionList.getItemCount();
	}

	@Override
	public boolean selectItem(int index, int button, double mouseX, double mouseY) {
		return this.realmsScrolledSelectionList.selectItem(index, button, mouseX, mouseY);
	}

	@Override
	public boolean isSelectedItem(int index) {
		return this.realmsScrolledSelectionList.isSelectedItem(index);
	}

	@Override
	public void renderBackground() {
		this.realmsScrolledSelectionList.renderBackground();
	}

	@Override
	public void renderItem(int index, int y, int i, int j, int k, int l, float f) {
		this.realmsScrolledSelectionList.renderItem(index, y, i, j, k, l);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxPosition() {
		return this.realmsScrolledSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsScrolledSelectionList.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double amount) {
		return this.realmsScrolledSelectionList.mouseScrolled(d, e, amount) ? true : super.mouseScrolled(d, e, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.realmsScrolledSelectionList.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.realmsScrolledSelectionList.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.realmsScrolledSelectionList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
}
