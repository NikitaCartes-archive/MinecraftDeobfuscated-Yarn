package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SuffixArray<T> {
	private static final boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
	private static final boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<T> objects = Lists.<T>newArrayList();
	private final IntList characters = new IntArrayList();
	private final IntList suffixStarts = new IntArrayList();
	private IntList suffixIndexToObjectIndex = new IntArrayList();
	private IntList suffixSplits = new IntArrayList();
	private int maxTextLength;

	public void add(T object, String text) {
		this.maxTextLength = Math.max(this.maxTextLength, text.length());
		int i = this.objects.size();
		this.objects.add(object);
		this.suffixStarts.add(this.characters.size());

		for (int j = 0; j < text.length(); j++) {
			this.suffixIndexToObjectIndex.add(i);
			this.suffixSplits.add(j);
			this.characters.add(text.charAt(j));
		}

		this.suffixIndexToObjectIndex.add(i);
		this.suffixSplits.add(text.length());
		this.characters.add(-1);
	}

	public void sort() {
		int i = this.characters.size();
		int[] is = new int[i];
		final int[] js = new int[i];
		final int[] ks = new int[i];
		int[] ls = new int[i];
		IntComparator intComparator = new IntComparator() {
			@Override
			public int compare(int i, int j) {
				return js[i] == js[j] ? Integer.compare(ks[i], ks[j]) : Integer.compare(js[i], js[j]);
			}

			@Override
			public int compare(Integer integer, Integer integer2) {
				return this.compare(integer.intValue(), integer2.intValue());
			}
		};
		Swapper swapper = (ix, jx) -> {
			if (ix != jx) {
				int kx = js[ix];
				js[ix] = js[jx];
				js[jx] = kx;
				kx = ks[ix];
				ks[ix] = ks[jx];
				ks[jx] = kx;
				kx = ls[ix];
				ls[ix] = ls[jx];
				ls[jx] = kx;
			}
		};

		for (int j = 0; j < i; j++) {
			is[j] = this.characters.getInt(j);
		}

		int j = 1;

		for (int k = Math.min(i, this.maxTextLength); j * 2 < k; j *= 2) {
			for (int l = 0; l < i; ls[l] = l++) {
				js[l] = is[l];
				ks[l] = l + j < i ? is[l + j] : -2;
			}

			Arrays.quickSort(0, i, intComparator, swapper);

			for (int l = 0; l < i; l++) {
				if (l > 0 && js[l] == js[l - 1] && ks[l] == ks[l - 1]) {
					is[ls[l]] = is[ls[l - 1]];
				} else {
					is[ls[l]] = l;
				}
			}
		}

		IntList intList = this.suffixIndexToObjectIndex;
		IntList intList2 = this.suffixSplits;
		this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
		this.suffixSplits = new IntArrayList(intList2.size());

		for (int m = 0; m < i; m++) {
			int n = ls[m];
			this.suffixIndexToObjectIndex.add(intList.getInt(n));
			this.suffixSplits.add(intList2.getInt(n));
		}

		if (PRINT_ARRAY) {
			this.printArray();
		}
	}

	private void printArray() {
		for (int i = 0; i < this.suffixIndexToObjectIndex.size(); i++) {
			LOGGER.debug("{} {}", i, this.getDebugString(i));
		}

		LOGGER.debug("");
	}

	private String getDebugString(int suffixIndex) {
		int i = this.suffixSplits.getInt(suffixIndex);
		int j = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
		StringBuilder stringBuilder = new StringBuilder();

		for (int k = 0; j + k < this.characters.size(); k++) {
			if (k == i) {
				stringBuilder.append('^');
			}

			int l = this.characters.get(j + k);
			if (l == -1) {
				break;
			}

			stringBuilder.append((char)l);
		}

		return stringBuilder.toString();
	}

	private int compare(String string, int suffixIndex) {
		int i = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(suffixIndex));
		int j = this.suffixSplits.getInt(suffixIndex);

		for (int k = 0; k < string.length(); k++) {
			int l = this.characters.getInt(i + j + k);
			if (l == -1) {
				return 1;
			}

			char c = string.charAt(k);
			char d = (char)l;
			if (c < d) {
				return -1;
			}

			if (c > d) {
				return 1;
			}
		}

		return 0;
	}

	public List<T> findAll(String text) {
		int i = this.suffixIndexToObjectIndex.size();
		int j = 0;
		int k = i;

		while (j < k) {
			int l = j + (k - j) / 2;
			int m = this.compare(text, l);
			if (PRINT_COMPARISONS) {
				LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", text, l, this.getDebugString(l), m);
			}

			if (m > 0) {
				j = l + 1;
			} else {
				k = l;
			}
		}

		if (j >= 0 && j < i) {
			int lx = j;
			k = i;

			while (j < k) {
				int mx = j + (k - j) / 2;
				int n = this.compare(text, mx);
				if (PRINT_COMPARISONS) {
					LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", text, mx, this.getDebugString(mx), n);
				}

				if (n >= 0) {
					j = mx + 1;
				} else {
					k = mx;
				}
			}

			int mxx = j;
			IntSet intSet = new IntOpenHashSet();

			for (int o = lx; o < mxx; o++) {
				intSet.add(this.suffixIndexToObjectIndex.getInt(o));
			}

			int[] is = intSet.toIntArray();
			java.util.Arrays.sort(is);
			Set<T> set = Sets.<T>newLinkedHashSet();

			for (int p : is) {
				set.add(this.objects.get(p));
			}

			return Lists.<T>newArrayList(set);
		} else {
			return Collections.emptyList();
		}
	}
}
