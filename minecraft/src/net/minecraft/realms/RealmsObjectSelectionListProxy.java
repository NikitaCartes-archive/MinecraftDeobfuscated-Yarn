package net.minecraft.realms;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.AlwaysSelectedItemListWidget;

@Environment(EnvType.CLIENT)
public class RealmsObjectSelectionListProxy<E extends AlwaysSelectedItemListWidget.Item<E>> extends AlwaysSelectedItemListWidget<E> {
	private final RealmsObjectSelectionList realmsObjectSelectionList;

	public RealmsObjectSelectionListProxy(RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.realmsObjectSelectionList = realmsObjectSelectionList;
	}

	@Override
	public int getItemCount() {
		return super.getItemCount();
	}

	public void clear() {
		super.clearItems();
	}

	@Override
	public boolean isFocused() {
		return this.realmsObjectSelectionList.isFocused();
	}

	protected void setSelectedItem(int i) {
		if (super.getItemCount() != 0) {
			super.selectItem(super.getItem(i));
		}
	}

	public void setSelected(@Nullable E item) {
		super.selectItem(item);
		this.realmsObjectSelectionList.selectItem(super.children().indexOf(item));
	}

	@Override
	public void drawBackground() {
		this.realmsObjectSelectionList.renderBackground();
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxScrollPosition() {
		return this.realmsObjectSelectionList.getMaxPosition();
	}

	@Override
	public int getScrollbarPosition() {
		return this.realmsObjectSelectionList.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.realmsObjectSelectionList.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public int getItemWidth() {
		return this.realmsObjectSelectionList.getRowWidth();
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.realmsObjectSelectionList.mouseClicked(d, e, i) ? true : access$001(this, d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.realmsObjectSelectionList.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.realmsObjectSelectionList.mouseDragged(d, e, i, f, g) ? true : super.mouseDragged(d, e, i, f, g);
	}

	protected final int addEntry(E item) {
		return super.addItem(item);
	}

	public E remove(int i) {
		return super.remove(i);
	}

	public boolean removeEntry(E item) {
		return super.removeEntry(item);
	}

	@Override
	public void capYPosition(double d) {
		super.capYPosition(d);
	}

	public int method_20327() {
		return this.top;
	}

	public int method_20328() {
		return this.bottom;
	}

	public int headerHeight() {
		return this.headerHeight;
	}

	public int itemHeight() {
		return this.itemHeight;
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		return super.keyPressed(i, j, k) ? true : this.realmsObjectSelectionList.keyPressed(i, j, k);
	}
}
