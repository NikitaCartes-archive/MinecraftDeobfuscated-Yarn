package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlyphContainer<T> {
	private static final int ROW_SHIFT = 8;
	private static final int ENTRIES_PER_ROW = 256;
	private static final int LAST_ENTRY_NUM_IN_ROW = 255;
	private static final int LAST_ROW_NUM = 4351;
	private static final int NUM_ROWS = 4352;
	private final T[] defaultRow;
	private final T[][] rows;
	private final IntFunction<T[]> makeRow;

	public GlyphContainer(IntFunction<T[]> makeRow, IntFunction<T[][]> makeScroll) {
		this.defaultRow = (T[])((Object[])makeRow.apply(256));
		this.rows = (T[][])((Object[][])makeScroll.apply(4352));
		Arrays.fill(this.rows, this.defaultRow);
		this.makeRow = makeRow;
	}

	public void clear() {
		Arrays.fill(this.rows, this.defaultRow);
	}

	@Nullable
	public T get(int codePoint) {
		int i = codePoint >> 8;
		int j = codePoint & 0xFF;
		return this.rows[i][j];
	}

	@Nullable
	public T put(int codePoint, T glyph) {
		int i = codePoint >> 8;
		int j = codePoint & 0xFF;
		T[] objects = this.rows[i];
		if (objects == this.defaultRow) {
			objects = (T[])((Object[])this.makeRow.apply(256));
			this.rows[i] = objects;
			objects[j] = glyph;
			return null;
		} else {
			T object = objects[j];
			objects[j] = glyph;
			return object;
		}
	}

	public T computeIfAbsent(int codePoint, IntFunction<T> ifAbsent) {
		int i = codePoint >> 8;
		int j = codePoint & 0xFF;
		T[] objects = this.rows[i];
		T object = objects[j];
		if (object != null) {
			return object;
		} else {
			if (objects == this.defaultRow) {
				objects = (T[])((Object[])this.makeRow.apply(256));
				this.rows[i] = objects;
			}

			T object2 = (T)ifAbsent.apply(codePoint);
			objects[j] = object2;
			return object2;
		}
	}

	@Nullable
	public T remove(int codePoint) {
		int i = codePoint >> 8;
		int j = codePoint & 0xFF;
		T[] objects = this.rows[i];
		if (objects == this.defaultRow) {
			return null;
		} else {
			T object = objects[j];
			objects[j] = null;
			return object;
		}
	}

	public void forEachGlyph(GlyphContainer.GlyphConsumer<T> glyphConsumer) {
		for (int i = 0; i < this.rows.length; i++) {
			T[] objects = this.rows[i];
			if (objects != this.defaultRow) {
				for (int j = 0; j < objects.length; j++) {
					T object = objects[j];
					if (object != null) {
						int k = i << 8 | j;
						glyphConsumer.accept(k, object);
					}
				}
			}
		}
	}

	public IntSet getProvidedGlyphs() {
		IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
		this.forEachGlyph((codePoint, glyph) -> intOpenHashSet.add(codePoint));
		return intOpenHashSet;
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface GlyphConsumer<T> {
		void accept(int codePoint, T glyph);
	}
}
