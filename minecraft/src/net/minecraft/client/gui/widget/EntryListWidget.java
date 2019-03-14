package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.InputListener;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractListWidget {
	private final List<E> entries = new EntryListWidget.Entries();

	public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	protected boolean selectEntry(int i, int j, double d, double e) {
		return this.getEntry(i).mouseClicked(d, e, j);
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return false;
	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.getEntry(i).draw(this.getEntryWidth(), l, m, n, this.isSelected((double)m, (double)n) && this.getSelectedEntry((double)m, (double)n) == i, f);
	}

	@Override
	protected void method_1952(int i, int j, int k, float f) {
		this.getEntry(i).method_1904(f);
	}

	@Override
	public final List<E> getInputListeners() {
		return this.entries;
	}

	protected final void clearEntries() {
		this.entries.clear();
	}

	public void method_19349(E entry) {
		int i = entry.getY() - this.y - 4 - this.getEntryHeight();
		if (i < 0) {
			this.scroll(i);
		}

		int j = this.bottom - entry.getY() - this.getEntryHeight() - this.getEntryHeight();
		if (j < 0) {
			this.scroll(-j);
		}
	}

	private E getEntry(int i) {
		return (E)this.getInputListeners().get(i);
	}

	protected final int addEntry(E entry) {
		this.entries.add(entry);
		return this.entries.size() - 1;
	}

	@Override
	protected final int getEntryCount() {
		return this.getInputListeners().size();
	}

	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<E> {
		private final List<E> field_2146 = Lists.<E>newArrayList();

		private Entries() {
		}

		public E method_1912(int i) {
			return (E)this.field_2146.get(i);
		}

		public int size() {
			return this.field_2146.size();
		}

		public E method_1909(int i, E entry) {
			E entry2 = (E)this.field_2146.set(i, entry);
			entry.parent = EntryListWidget.this;
			entry.field_2143 = i;
			return entry2;
		}

		public void method_1910(int i, E entry) {
			this.field_2146.add(i, entry);
			entry.parent = EntryListWidget.this;
			entry.field_2143 = i;
			int j = i + 1;

			while (j < this.size()) {
				this.method_1912(j).field_2143 = j++;
			}
		}

		public E method_1911(int i) {
			E entry = (E)this.field_2146.remove(i);
			int j = i;

			while (j < this.size()) {
				this.method_1912(j).field_2143 = j++;
			}

			return entry;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements InputListener {
		protected EntryListWidget<E> parent;
		protected int field_2143;

		protected EntryListWidget<E> getParent() {
			return this.parent;
		}

		protected int method_1908() {
			return this.field_2143;
		}

		public int getY() {
			return this.parent.y + 4 - this.parent.getScrollY() + this.field_2143 * this.parent.entryHeight + this.parent.field_2174;
		}

		protected int getX() {
			return this.parent.x + this.parent.width / 2 - this.parent.getEntryWidth() / 2 + 2;
		}

		protected void method_1904(float f) {
		}

		public abstract void draw(int i, int j, int k, int l, boolean bl, float f);

		@Override
		public boolean method_19356(double d, double e) {
			return this.parent.getSelectedEntry(d, e) == this.field_2143;
		}
	}
}
