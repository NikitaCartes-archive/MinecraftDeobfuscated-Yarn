package net.minecraft;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;

public interface class_2791 extends class_2810 {
	@Nullable
	class_2680 method_12010(class_2338 arg, class_2680 arg2, boolean bl);

	void method_12007(class_2338 arg, class_2586 arg2);

	void method_12002(class_1297 arg);

	@Nullable
	default class_2826 method_12040() {
		class_2826[] lvs = this.method_12006();

		for (int i = lvs.length - 1; i >= 0; i--) {
			class_2826 lv = lvs[i];
			if (!class_2826.method_18090(lv)) {
				return lv;
			}
		}

		return null;
	}

	default int method_12031() {
		class_2826 lv = this.method_12040();
		return lv == null ? 0 : lv.method_12259();
	}

	Set<class_2338> method_12021();

	class_2826[] method_12006();

	@Nullable
	class_3568 method_12023();

	default int method_12035(class_2338 arg, int i, boolean bl) {
		class_3568 lv = this.method_12023();
		if (lv != null && this.method_12009().method_12165(class_2806.field_12805)) {
			int j = bl ? lv.method_15562(class_1944.field_9284).method_15543(arg) - i : 0;
			int k = lv.method_15562(class_1944.field_9282).method_15543(arg);
			return Math.max(k, j);
		} else {
			return 0;
		}
	}

	Collection<Entry<class_2902.class_2903, class_2902>> method_12011();

	void method_12037(class_2902.class_2903 arg, long[] ls);

	class_2902 method_12032(class_2902.class_2903 arg);

	int method_12005(class_2902.class_2903 arg, int i, int j);

	class_1923 method_12004();

	void method_12043(long l);

	Map<String, class_3449> method_12016();

	void method_12034(Map<String, class_3449> map);

	default class_1959 method_16552(class_2338 arg) {
		int i = arg.method_10263() & 15;
		int j = arg.method_10260() & 15;
		return this.method_12036()[j << 4 | i];
	}

	default boolean method_12228(int i, int j) {
		if (i < 0) {
			i = 0;
		}

		if (j >= 256) {
			j = 255;
		}

		for (int k = i; k <= j; k += 16) {
			if (!class_2826.method_18090(this.method_12006()[k >> 4])) {
				return false;
			}
		}

		return true;
	}

	class_1959[] method_12036();

	void method_12008(boolean bl);

	boolean method_12044();

	class_2806 method_12009();

	void method_12041(class_2338 arg);

	void method_17032(class_3568 arg);

	default void method_12039(class_2338 arg) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", arg);
	}

	ShortList[] method_12012();

	default void method_12029(short s, int i) {
		method_12026(this.method_12012(), i).add(s);
	}

	default void method_12042(class_2487 arg) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	class_2487 method_12024(class_2338 arg);

	@Nullable
	class_2487 method_20598(class_2338 arg);

	default void method_12022(class_1959[] args) {
		throw new UnsupportedOperationException();
	}

	Stream<class_2338> method_12018();

	class_1951<class_2248> method_12013();

	class_1951<class_3611> method_12014();

	default BitSet method_12025(class_2893.class_2894 arg) {
		throw new RuntimeException("Meaningless in this context");
	}

	class_2843 method_12003();

	void method_12028(long l);

	long method_12033();

	static ShortList method_12026(ShortList[] shortLists, int i) {
		if (shortLists[i] == null) {
			shortLists[i] = new ShortArrayList();
		}

		return shortLists[i];
	}

	boolean method_12038();

	void method_12020(boolean bl);
}
