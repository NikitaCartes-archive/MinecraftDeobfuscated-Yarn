package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractListWidget;
import net.minecraft.realms.RealmsScrolledSelectionList;

@Environment(EnvType.CLIENT)
public class ScrolledSelectionList extends AbstractListWidget {
	private final RealmsScrolledSelectionList field_2344;

	public ScrolledSelectionList(RealmsScrolledSelectionList realmsScrolledSelectionList, int i, int j, int k, int l, int m) {
		super(MinecraftClient.getInstance(), i, j, k, l, m);
		this.field_2344 = realmsScrolledSelectionList;
	}

	@Override
	protected int getEntryCount() {
		return this.field_2344.getItemCount();
	}

	@Override
	protected boolean selectEntry(int i, int j, double d, double e) {
		return this.field_2344.selectItem(i, j, d, e);
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return this.field_2344.isSelectedItem(i);
	}

	@Override
	protected void drawBackground() {
		this.field_2344.renderBackground();
	}

	@Override
	protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.field_2344.renderItem(i, j, k, l, m, n);
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	protected int getMaxScrollPosition() {
		return this.field_2344.getMaxPosition();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.field_2344.getScrollbarPosition();
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f) {
		return this.field_2344.mouseScrolled(d, e, f) ? true : super.mouseScrolled(d, e, f);
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		return this.field_2344.mouseClicked(d, e, i) ? true : super.mouseClicked(d, e, i);
	}

	@Override
	public boolean mouseReleased(double d, double e, int i) {
		return this.field_2344.mouseReleased(d, e, i);
	}

	@Override
	public boolean mouseDragged(double d, double e, int i, double f, double g) {
		return this.field_2344.mouseDragged(d, e, i, f, g);
	}
}
