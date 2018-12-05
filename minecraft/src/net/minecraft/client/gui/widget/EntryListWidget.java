package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget<E extends EntryListWidget.Entry<E>> extends AbstractListWidget {
	private final List<E> field_2142 = new EntryListWidget.class_352();

	public EntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
	}

	@Override
	protected boolean method_1937(int i, int j, double d, double e) {
		return this.method_1900(i).mouseClicked(d, e, j);
	}

	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Override
	protected void method_1936() {
	}

	@Override
	protected void drawEntry(int i, int j, int k, int l, int m, int n, float f) {
		this.method_1900(i).drawEntry(this.getEntryWidth(), l, m, n, this.method_1938((double)m, (double)n) && this.method_1956((double)m, (double)n) == i, f);
	}

	@Override
	protected void method_1952(int i, int j, int k, float f) {
		this.method_1900(i).method_1904(f);
	}

	@Override
	public final List<E> getListeners() {
		return this.field_2142;
	}

	protected final void method_1902() {
		this.field_2142.clear();
	}

	private E method_1900(int i) {
		return (E)this.getListeners().get(i);
	}

	protected final void method_1901(E entry) {
		this.field_2142.add(entry);
	}

	@Override
	public void method_1946(int i) {
		this.field_2176 = i;
		this.field_2177 = SystemUtil.getMeasuringTimeMili();
	}

	@Override
	protected final int getEntryCount() {
		return this.getListeners().size();
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry<E extends EntryListWidget.Entry<E>> implements GuiEventListener {
		protected EntryListWidget<E> field_2144;
		protected int field_2143;

		protected EntryListWidget<E> method_1905() {
			return this.field_2144;
		}

		protected int method_1908() {
			return this.field_2143;
		}

		protected int method_1906() {
			return this.field_2144.y1 + 4 - this.field_2144.getScrollY() + this.field_2143 * this.field_2144.entryHeight + this.field_2144.field_2174;
		}

		protected int method_1907() {
			return this.field_2144.x1 + this.field_2144.width / 2 - this.field_2144.getEntryWidth() / 2 + 2;
		}

		protected void method_1904(float f) {
		}

		public abstract void drawEntry(int i, int j, int k, int l, boolean bl, float f);
	}

	@Environment(EnvType.CLIENT)
	class class_352 extends AbstractList<E> {
		private final List<E> field_2146 = Lists.<E>newArrayList();

		private class_352() {
		}

		public E method_1912(int i) {
			return (E)this.field_2146.get(i);
		}

		public int size() {
			return this.field_2146.size();
		}

		public E method_1909(int i, E entry) {
			E entry2 = (E)this.field_2146.set(i, entry);
			entry.field_2144 = EntryListWidget.this;
			entry.field_2143 = i;
			return entry2;
		}

		public void method_1910(int i, E entry) {
			this.field_2146.add(i, entry);
			entry.field_2144 = EntryListWidget.this;
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
}
