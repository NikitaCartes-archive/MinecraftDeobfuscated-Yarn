package net.minecraft;

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
public class class_1128<T> {
	private static final boolean field_5508 = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
	private static final boolean field_5507 = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
	private static final Logger field_5510 = LogManager.getLogger();
	protected final List<T> field_5503 = Lists.<T>newArrayList();
	private final IntList field_5505 = new IntArrayList();
	private final IntList field_5509 = new IntArrayList();
	private IntList field_5504 = new IntArrayList();
	private IntList field_5506 = new IntArrayList();
	private int field_5502;

	public void method_4806(T object, String string) {
		this.field_5502 = Math.max(this.field_5502, string.length());
		int i = this.field_5503.size();
		this.field_5503.add(object);
		this.field_5509.add(this.field_5505.size());

		for (int j = 0; j < string.length(); j++) {
			this.field_5504.add(i);
			this.field_5506.add(j);
			this.field_5505.add(string.charAt(j));
		}

		this.field_5504.add(i);
		this.field_5506.add(string.length());
		this.field_5505.add(-1);
	}

	public void method_4807() {
		int i = this.field_5505.size();
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
			is[j] = this.field_5505.getInt(j);
		}

		int j = 1;

		for (int k = Math.min(i, this.field_5502); j * 2 < k; j *= 2) {
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

		IntList intList = this.field_5504;
		IntList intList2 = this.field_5506;
		this.field_5504 = new IntArrayList(intList.size());
		this.field_5506 = new IntArrayList(intList2.size());

		for (int m = 0; m < i; m++) {
			int n = ls[m];
			this.field_5504.add(intList.getInt(n));
			this.field_5506.add(intList2.getInt(n));
		}

		if (field_5507) {
			this.method_4809();
		}
	}

	private void method_4809() {
		for (int i = 0; i < this.field_5504.size(); i++) {
			field_5510.debug("{} {}", i, this.method_4808(i));
		}

		field_5510.debug("");
	}

	private String method_4808(int i) {
		int j = this.field_5506.getInt(i);
		int k = this.field_5509.getInt(this.field_5504.getInt(i));
		StringBuilder stringBuilder = new StringBuilder();

		for (int l = 0; k + l < this.field_5505.size(); l++) {
			if (l == j) {
				stringBuilder.append('^');
			}

			int m = this.field_5505.get(k + l);
			if (m == -1) {
				break;
			}

			stringBuilder.append((char)m);
		}

		return stringBuilder.toString();
	}

	private int method_4805(String string, int i) {
		int j = this.field_5509.getInt(this.field_5504.getInt(i));
		int k = this.field_5506.getInt(i);

		for (int l = 0; l < string.length(); l++) {
			int m = this.field_5505.getInt(j + k + l);
			if (m == -1) {
				return 1;
			}

			char c = string.charAt(l);
			char d = (char)m;
			if (c < d) {
				return -1;
			}

			if (c > d) {
				return 1;
			}
		}

		return 0;
	}

	public List<T> method_4804(String string) {
		int i = this.field_5504.size();
		int j = 0;
		int k = i;

		while (j < k) {
			int l = j + (k - j) / 2;
			int m = this.method_4805(string, l);
			if (field_5508) {
				field_5510.debug("comparing lower \"{}\" with {} \"{}\": {}", string, l, this.method_4808(l), m);
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
				int n = this.method_4805(string, mx);
				if (field_5508) {
					field_5510.debug("comparing upper \"{}\" with {} \"{}\": {}", string, mx, this.method_4808(mx), n);
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
				intSet.add(this.field_5504.getInt(o));
			}

			int[] is = intSet.toIntArray();
			java.util.Arrays.sort(is);
			Set<T> set = Sets.<T>newLinkedHashSet();

			for (int p : is) {
				set.add(this.field_5503.get(p));
			}

			return Lists.<T>newArrayList(set);
		} else {
			return Collections.emptyList();
		}
	}
}
