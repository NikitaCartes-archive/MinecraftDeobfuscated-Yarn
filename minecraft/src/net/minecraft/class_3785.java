package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class class_3785 {
	public static final class_3785 field_16679 = new class_3785(
		new class_2960("empty"), new class_2960("empty"), ImmutableList.of(), class_3785.class_3786.field_16687
	);
	public static final class_3785 field_16746 = new class_3785(
		new class_2960("invalid"), new class_2960("invalid"), ImmutableList.of(), class_3785.class_3786.field_16687
	);
	private final class_2960 field_16678;
	private final ImmutableList<Pair<class_3784, Integer>> field_16864;
	private final List<class_3784> field_16680;
	private final class_2960 field_16681;
	private final class_3785.class_3786 field_16863;

	public class_3785(class_2960 arg, class_2960 arg2, List<Pair<class_3784, Integer>> list, class_3785.class_3786 arg3) {
		this.field_16678 = arg;
		this.field_16864 = ImmutableList.copyOf(list);
		this.field_16680 = Lists.<class_3784>newArrayList();

		for (Pair<class_3784, Integer> pair : list) {
			for (Integer integer = 0; integer < pair.getSecond(); integer = integer + 1) {
				this.field_16680.add(pair.getFirst().method_16622(arg3));
			}
		}

		this.field_16681 = arg2;
		this.field_16863 = arg3;
	}

	public class_3784 method_16630(int i) {
		return (class_3784)this.field_16680.get(i);
	}

	public class_2960 method_16634() {
		return this.field_16681;
	}

	public class_3784 method_16631(Random random) {
		return (class_3784)this.field_16680.get(random.nextInt(this.field_16680.size()));
	}

	public int[] method_16633(Random random) {
		int[] is = new int[this.field_16680.size()];
		int i = 0;

		while (i < is.length) {
			is[i] = i++;
		}

		IntArrays.shuffle(is, random);
		return is;
	}

	public class_2960 method_16629() {
		return this.field_16678;
	}

	public int method_16632() {
		return this.field_16680.size();
	}

	public static enum class_3786 {
		field_16686("terrain_matching", ImmutableList.of(new class_3795(class_2902.class_2903.field_13194, -1))),
		field_16687("rigid", ImmutableList.of());

		private static final Map<String, class_3785.class_3786> field_16684 = (Map<String, class_3785.class_3786>)Arrays.stream(values())
			.collect(Collectors.toMap(class_3785.class_3786::method_16635, arg -> arg));
		private final String field_16682;
		private final ImmutableList<class_3491> field_16685;

		private class_3786(String string2, ImmutableList<class_3491> immutableList) {
			this.field_16682 = string2;
			this.field_16685 = immutableList;
		}

		public String method_16635() {
			return this.field_16682;
		}

		public static class_3785.class_3786 method_16638(String string) {
			return (class_3785.class_3786)field_16684.get(string);
		}

		public ImmutableList<class_3491> method_16636() {
			return this.field_16685;
		}
	}
}
