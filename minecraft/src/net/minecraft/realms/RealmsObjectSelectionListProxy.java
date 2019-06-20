package net.minecraft.realms;

import java.util.Collection;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4280;

@Environment(EnvType.CLIENT)
public class RealmsObjectSelectionListProxy<E extends class_4280.class_4281<E>> extends class_4280<E> {
	private final RealmsObjectSelectionList realmsObjectSelectionList;

	public RealmsObjectSelectionListProxy(RealmsObjectSelectionList realmsObjectSelectionList, int i, int j, int k, int l, int m) {
		super(class_310.method_1551(), i, j, k, l, m);
		this.realmsObjectSelectionList = realmsObjectSelectionList;
	}

	@Override
	public int getItemCount() {
		return super.getItemCount();
	}

	public void clear() {
		super.clearEntries();
	}

	@Override
	public boolean isFocused() {
		return this.realmsObjectSelectionList.isFocused();
	}

	protected void setSelectedItem(int i) {
		if (i == -1) {
			super.setSelected(null);
		} else if (super.getItemCount() != 0) {
			E lv = super.getEntry(i);
			super.setSelected(lv);
		}
	}

	public void setSelected(@Nullable E arg) {
		super.setSelected(arg);
		this.realmsObjectSelectionList.selectItem(super.children().indexOf(arg));
	}

	@Override
	public void renderBackground() {
		this.realmsObjectSelectionList.renderBackground();
	}

	public int getWidth() {
		return this.width;
	}

	@Override
	public int getMaxPosition() {
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
	public int getRowWidth() {
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

	protected final int addEntry(E arg) {
		return super.addEntry(arg);
	}

	public E remove(int i) {
		return super.remove(i);
	}

	public boolean removeEntry(E arg) {
		return super.removeEntry(arg);
	}

	@Override
	public void setScrollAmount(double d) {
		super.setScrollAmount(d);
	}

	public int method_26764() {
		return this.field_19085;
	}

	public int method_26765() {
		return this.field_19086;
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

	@Override
	public void replaceEntries(Collection<E> collection) {
		super.replaceEntries(collection);
	}

	@Override
	public int getRowTop(int i) {
		return super.getRowTop(i);
	}

	@Override
	public int getRowLeft() {
		return super.getRowLeft();
	}
}
