package net.minecraft.realms;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(EnvType.CLIENT)
public abstract class RealmsObjectSelectionList extends RealmsGuiEventListener {
	private final RealmsObjectSelectionListProxy<RealmListEntry> proxy;

	public RealmsObjectSelectionList(int i, int j, int k, int l, int m) {
		this.proxy = new RealmsObjectSelectionListProxy<>(this, i, j, k, l, m);
	}

	public void render(int i, int j, float f) {
		this.proxy.render(i, j, f);
	}

	public void addEntry(RealmListEntry realmListEntry) {
		this.proxy.addEntry(realmListEntry);
	}

	public void remove(int i) {
		this.proxy.remove(i);
	}

	public void clear() {
		this.proxy.clear();
	}

	public boolean removeEntry(RealmListEntry realmListEntry) {
		return this.proxy.removeEntry(realmListEntry);
	}

	public int width() {
		return this.proxy.getWidth();
	}

	protected void renderItem(int i, int j, int k, int l, Tezzelator tezzelator, int m, int n) {
	}

	public void setLeftPos(int i) {
		this.proxy.setLeftPos(i);
	}

	public void renderItem(int i, int j, int k, int l, int m, int n) {
		this.renderItem(i, j, k, l, Tezzelator.instance, m, n);
	}

	public void setSelected(int i) {
		this.proxy.setSelectedItem(i);
	}

	public void itemClicked(int i, int j, double d, double e, int k) {
	}

	public int getItemCount() {
		return this.proxy.getItemCount();
	}

	public void renderBackground() {
	}

	public int getMaxPosition() {
		return 0;
	}

	public int getScrollbarPosition() {
		return this.proxy.getWidth() / 2 + 124;
	}

	public int method_20325() {
		return this.proxy.method_20327();
	}

	public int method_20326() {
		return this.proxy.method_20328();
	}

	public int headerHeight() {
		return this.proxy.headerHeight();
	}

	public int itemHeight() {
		return this.proxy.itemHeight();
	}

	public void scroll(int i) {
		this.proxy.capYPosition((double)i);
	}

	public int getScroll() {
		return (int)this.proxy.getScroll();
	}

	@Override
	public Element getProxy() {
		return this.proxy;
	}

	public int getRowWidth() {
		return (int)((double)this.width() * 0.6);
	}

	public abstract boolean isFocused();

	public void selectItem(int i) {
		this.setSelected(i);
	}

	@Nullable
	public RealmListEntry getSelected() {
		return this.proxy.getSelectedItem();
	}
}
