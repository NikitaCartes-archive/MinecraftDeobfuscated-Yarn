package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2839 implements class_2791 {
	private static final Logger field_12920 = LogManager.getLogger();
	private final class_1923 field_12928;
	private boolean field_12924;
	private class_1959[] field_12913;
	@Nullable
	private class_3568 field_17105;
	private final Map<class_2902.class_2903, class_2902> field_12912 = Maps.newEnumMap(class_2902.class_2903.class);
	private volatile class_2806 field_12918 = class_2806.field_12798;
	private final Map<class_2338, class_2586> field_12917 = Maps.<class_2338, class_2586>newHashMap();
	private final Map<class_2338, class_2487> field_12927 = Maps.<class_2338, class_2487>newHashMap();
	private final class_2826[] field_12909 = new class_2826[16];
	private final List<class_2487> field_12929 = Lists.<class_2487>newArrayList();
	private final List<class_2338> field_12919 = Lists.<class_2338>newArrayList();
	private final ShortList[] field_12921 = new ShortList[16];
	private final Map<String, class_3449> field_12915 = Maps.<String, class_3449>newHashMap();
	private final Map<String, LongSet> field_12930 = Maps.<String, LongSet>newHashMap();
	private final class_2843 field_12916;
	private final class_2850<class_2248> field_12911;
	private final class_2850<class_3611> field_12923;
	private long field_12925;
	private final Map<class_2893.class_2894, BitSet> field_12926 = Maps.<class_2893.class_2894, BitSet>newHashMap();
	private volatile boolean field_12914;

	public class_2839(class_1923 arg, class_2843 arg2) {
		this(
			arg,
			arg2,
			null,
			new class_2850<>(argx -> argx == null || argx.method_9564().method_11588(), class_2378.field_11146::method_10221, class_2378.field_11146::method_10223, arg),
			new class_2850<>(argx -> argx == null || argx == class_3612.field_15906, class_2378.field_11154::method_10221, class_2378.field_11154::method_10223, arg)
		);
	}

	public class_2839(class_1923 arg, class_2843 arg2, @Nullable class_2826[] args, class_2850<class_2248> arg3, class_2850<class_3611> arg4) {
		this.field_12928 = arg;
		this.field_12916 = arg2;
		this.field_12911 = arg3;
		this.field_12923 = arg4;
		if (args != null) {
			if (this.field_12909.length == args.length) {
				System.arraycopy(args, 0, this.field_12909, 0, this.field_12909.length);
			} else {
				field_12920.warn("Could not set level chunk sections, array length is {} instead of {}", args.length, this.field_12909.length);
			}
		}
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		int i = arg.method_10264();
		if (class_1937.method_8476(i)) {
			return class_2246.field_10243.method_9564();
		} else {
			class_2826 lv = this.method_12006()[i >> 4];
			return class_2826.method_18090(lv) ? class_2246.field_10124.method_9564() : lv.method_12254(arg.method_10263() & 15, i & 15, arg.method_10260() & 15);
		}
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		int i = arg.method_10264();
		if (class_1937.method_8476(i)) {
			return class_3612.field_15906.method_15785();
		} else {
			class_2826 lv = this.method_12006()[i >> 4];
			return class_2826.method_18090(lv) ? class_3612.field_15906.method_15785() : lv.method_12255(arg.method_10263() & 15, i & 15, arg.method_10260() & 15);
		}
	}

	@Override
	public int method_8317(class_2338 arg) {
		return this.field_17105 == null ? 0 : this.method_8320(arg).method_11630();
	}

	@Override
	public Stream<class_2338> method_12018() {
		return this.field_12919.stream();
	}

	public ShortList[] method_12296() {
		ShortList[] shortLists = new ShortList[16];

		for (class_2338 lv : this.field_12919) {
			class_2791.method_12026(shortLists, lv.method_10264() >> 4).add(method_12300(lv));
		}

		return shortLists;
	}

	public void method_12304(short s, int i) {
		this.method_12315(method_12314(s, i, this.field_12928));
	}

	public void method_12315(class_2338 arg) {
		this.field_12919.add(arg.method_10062());
	}

	@Nullable
	@Override
	public class_2680 method_12010(class_2338 arg, class_2680 arg2, boolean bl) {
		int i = arg.method_10263();
		int j = arg.method_10264();
		int k = arg.method_10260();
		if (j >= 0 && j < 256) {
			if (this.field_12909[j >> 4] == class_2818.field_12852 && arg2.method_11614() == class_2246.field_10124) {
				return arg2;
			} else {
				if (arg2.method_11630() > 0) {
					this.field_12919.add(new class_2338((i & 15) + this.method_12004().method_8326(), j, (k & 15) + this.method_12004().method_8328()));
				}

				class_2826 lv = this.method_16679(j >> 4);
				class_2680 lv2 = lv.method_16675(i & 15, j & 15, k & 15, arg2);
				EnumSet<class_2902.class_2903> enumSet = this.method_12009().method_12160();
				EnumSet<class_2902.class_2903> enumSet2 = null;

				for (class_2902.class_2903 lv3 : enumSet) {
					class_2902 lv4 = (class_2902)this.field_12912.get(lv3);
					if (lv4 == null) {
						if (enumSet2 == null) {
							enumSet2 = EnumSet.noneOf(class_2902.class_2903.class);
						}

						enumSet2.add(lv3);
					}
				}

				if (enumSet2 != null) {
					class_2902.method_16684(this, enumSet2);
				}

				for (class_2902.class_2903 lv3x : enumSet) {
					((class_2902)this.field_12912.get(lv3x)).method_12597(i & 15, j, k & 15, arg2);
				}

				return lv2;
			}
		} else {
			return class_2246.field_10243.method_9564();
		}
	}

	public class_2826 method_16679(int i) {
		if (this.field_12909[i] == class_2818.field_12852) {
			this.field_12909[i] = new class_2826(i << 4);
		}

		return this.field_12909[i];
	}

	@Override
	public void method_12007(class_2338 arg, class_2586 arg2) {
		arg2.method_10998(arg);
		this.field_12917.put(arg, arg2);
	}

	@Override
	public Set<class_2338> method_12021() {
		Set<class_2338> set = Sets.<class_2338>newHashSet(this.field_12927.keySet());
		set.addAll(this.field_12917.keySet());
		return set;
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		return (class_2586)this.field_12917.get(arg);
	}

	public Map<class_2338, class_2586> method_12309() {
		return this.field_12917;
	}

	public void method_12302(class_2487 arg) {
		this.field_12929.add(arg);
	}

	@Override
	public void method_12002(class_1297 arg) {
		class_2487 lv = new class_2487();
		arg.method_5662(lv);
		this.method_12302(lv);
	}

	public List<class_2487> method_12295() {
		return this.field_12929;
	}

	@Override
	public void method_12022(class_1959[] args) {
		this.field_12913 = args;
	}

	@Override
	public class_1959[] method_12036() {
		return this.field_12913;
	}

	@Override
	public void method_12008(boolean bl) {
		this.field_12924 = bl;
	}

	@Override
	public boolean method_12044() {
		return this.field_12924;
	}

	@Override
	public class_2806 method_12009() {
		return this.field_12918;
	}

	public void method_12308(class_2806 arg) {
		this.field_12918 = arg;
		this.method_12008(true);
	}

	@Override
	public class_2826[] method_12006() {
		return this.field_12909;
	}

	@Nullable
	@Override
	public class_3568 method_12023() {
		return this.field_17105;
	}

	@Override
	public Collection<Entry<class_2902.class_2903, class_2902>> method_12011() {
		return Collections.unmodifiableSet(this.field_12912.entrySet());
	}

	@Override
	public void method_12037(class_2902.class_2903 arg, long[] ls) {
		this.method_12032(arg).method_12600(ls);
	}

	@Override
	public class_2902 method_12032(class_2902.class_2903 arg) {
		return (class_2902)this.field_12912.computeIfAbsent(arg, argx -> new class_2902(this, argx));
	}

	@Override
	public int method_12005(class_2902.class_2903 arg, int i, int j) {
		class_2902 lv = (class_2902)this.field_12912.get(arg);
		if (lv == null) {
			class_2902.method_16684(this, EnumSet.of(arg));
			lv = (class_2902)this.field_12912.get(arg);
		}

		return lv.method_12603(i & 15, j & 15) - 1;
	}

	@Override
	public class_1923 method_12004() {
		return this.field_12928;
	}

	@Override
	public void method_12043(long l) {
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return (class_3449)this.field_12915.get(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
		this.field_12915.put(string, arg);
		this.field_12924 = true;
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return Collections.unmodifiableMap(this.field_12915);
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
		this.field_12915.clear();
		this.field_12915.putAll(map);
		this.field_12924 = true;
	}

	@Override
	public LongSet method_12180(String string) {
		return (LongSet)this.field_12930.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_12182(String string, long l) {
		((LongSet)this.field_12930.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
		this.field_12924 = true;
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return Collections.unmodifiableMap(this.field_12930);
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
		this.field_12930.clear();
		this.field_12930.putAll(map);
		this.field_12924 = true;
	}

	public static short method_12300(class_2338 arg) {
		int i = arg.method_10263();
		int j = arg.method_10264();
		int k = arg.method_10260();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static class_2338 method_12314(short s, int i, class_1923 arg) {
		int j = (s & 15) + (arg.field_9181 << 4);
		int k = (s >>> 4 & 15) + (i << 4);
		int l = (s >>> 8 & 15) + (arg.field_9180 << 4);
		return new class_2338(j, k, l);
	}

	@Override
	public void method_12039(class_2338 arg) {
		if (!class_1937.method_8518(arg)) {
			class_2791.method_12026(this.field_12921, arg.method_10264() >> 4).add(method_12300(arg));
		}
	}

	@Override
	public ShortList[] method_12012() {
		return this.field_12921;
	}

	@Override
	public void method_12029(short s, int i) {
		class_2791.method_12026(this.field_12921, i).add(s);
	}

	public class_2850<class_2248> method_12303() {
		return this.field_12911;
	}

	public class_2850<class_3611> method_12313() {
		return this.field_12923;
	}

	@Override
	public class_2843 method_12003() {
		return this.field_12916;
	}

	@Override
	public void method_12028(long l) {
		this.field_12925 = l;
	}

	@Override
	public long method_12033() {
		return this.field_12925;
	}

	@Override
	public void method_12042(class_2487 arg) {
		this.field_12927.put(new class_2338(arg.method_10550("x"), arg.method_10550("y"), arg.method_10550("z")), arg);
	}

	public Map<class_2338, class_2487> method_12316() {
		return Collections.unmodifiableMap(this.field_12927);
	}

	@Override
	public class_2487 method_12024(class_2338 arg) {
		return (class_2487)this.field_12927.get(arg);
	}

	@Override
	public void method_12041(class_2338 arg) {
		this.field_12917.remove(arg);
		this.field_12927.remove(arg);
	}

	@Override
	public BitSet method_12025(class_2893.class_2894 arg) {
		return (BitSet)this.field_12926.computeIfAbsent(arg, argx -> new BitSet(65536));
	}

	public void method_12307(class_2893.class_2894 arg, BitSet bitSet) {
		this.field_12926.put(arg, bitSet);
	}

	@Override
	public void method_17032(class_3568 arg) {
		this.field_17105 = arg;
	}

	@Override
	public boolean method_12038() {
		return this.field_12914;
	}

	@Override
	public void method_12020(boolean bl) {
		this.field_12914 = bl;
		this.method_12008(true);
	}
}
