package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2818 implements class_2791 {
	private static final Logger field_12839 = LogManager.getLogger();
	public static final class_2826 field_12852 = null;
	private final class_2826[] field_12840 = new class_2826[16];
	private final class_1959[] field_12842;
	private final Map<class_2338, class_2487> field_12846 = Maps.<class_2338, class_2487>newHashMap();
	private boolean field_12855;
	private final class_1937 field_12858;
	private final Map<class_2902.class_2903, class_2902> field_12853 = Maps.newEnumMap(class_2902.class_2903.class);
	private final class_2843 field_12849;
	private final Map<class_2338, class_2586> field_12854 = Maps.<class_2338, class_2586>newHashMap();
	private final class_3509<class_1297>[] field_12833;
	private final Map<String, class_3449> field_12838 = Maps.<String, class_3449>newHashMap();
	private final Map<String, LongSet> field_12845 = Maps.<String, LongSet>newHashMap();
	private final ShortList[] field_12836 = new ShortList[16];
	private final class_1951<class_2248> field_12841;
	private final class_1951<class_3611> field_12857;
	private boolean field_12837;
	private long field_12844;
	private boolean field_12834;
	private long field_12843;
	@Nullable
	private Supplier<class_3193.class_3194> field_12856;
	@Nullable
	private Consumer<class_2818> field_12850;
	private final class_1923 field_12848;
	private volatile boolean field_12847;

	@Environment(EnvType.CLIENT)
	public class_2818(class_1937 arg, class_1923 arg2, class_1959[] args) {
		this(arg, arg2, args, class_2843.field_12950, class_1925.method_8339(), class_1925.method_8339(), 0L, null, null);
	}

	public class_2818(
		class_1937 arg,
		class_1923 arg2,
		class_1959[] args,
		class_2843 arg3,
		class_1951<class_2248> arg4,
		class_1951<class_3611> arg5,
		long l,
		@Nullable class_2826[] args2,
		@Nullable Consumer<class_2818> consumer
	) {
		this.field_12833 = new class_3509[16];
		this.field_12858 = arg;
		this.field_12848 = arg2;
		this.field_12849 = arg3;

		for (class_2902.class_2903 lv : class_2902.class_2903.values()) {
			if (class_2806.field_12803.method_12160().contains(lv)) {
				this.field_12853.put(lv, new class_2902(this, lv));
			}
		}

		for (int i = 0; i < this.field_12833.length; i++) {
			this.field_12833[i] = new class_3509<>(class_1297.class);
		}

		this.field_12842 = args;
		this.field_12841 = arg4;
		this.field_12857 = arg5;
		this.field_12843 = l;
		this.field_12850 = consumer;
		if (args2 != null) {
			if (this.field_12840.length == args2.length) {
				System.arraycopy(args2, 0, this.field_12840, 0, this.field_12840.length);
			} else {
				field_12839.warn("Could not set level chunk sections, array length is {} instead of {}", args2.length, this.field_12840.length);
			}
		}
	}

	public class_2818(class_1937 arg, class_2839 arg2) {
		this(
			arg, arg2.method_12004(), arg2.method_12036(), arg2.method_12003(), arg2.method_12303(), arg2.method_12313(), arg2.method_12033(), arg2.method_12006(), null
		);

		for (class_2487 lv : arg2.method_12295()) {
			class_1299.method_17842(lv, arg, argx -> {
				this.method_12002(argx);
				return argx;
			});
		}

		for (class_2586 lv2 : arg2.method_12309().values()) {
			this.method_12216(lv2);
		}

		this.field_12846.putAll(arg2.method_12316());

		for (int i = 0; i < arg2.method_12012().length; i++) {
			this.field_12836[i] = arg2.method_12012()[i];
		}

		this.method_12034(arg2.method_12016());
		this.method_12183(arg2.method_12179());

		for (Entry<class_2902.class_2903, class_2902> entry : arg2.method_12011()) {
			if (class_2806.field_12803.method_12160().contains(entry.getKey())) {
				this.method_12032((class_2902.class_2903)entry.getKey()).method_12600(((class_2902)entry.getValue()).method_12598());
			}
		}

		this.method_12020(arg2.method_12038());
		this.field_12834 = true;
	}

	@Override
	public class_2902 method_12032(class_2902.class_2903 arg) {
		return (class_2902)this.field_12853.computeIfAbsent(arg, argx -> new class_2902(this, argx));
	}

	@Override
	public Set<class_2338> method_12021() {
		Set<class_2338> set = Sets.<class_2338>newHashSet(this.field_12846.keySet());
		set.addAll(this.field_12854.keySet());
		return set;
	}

	@Override
	public class_2826[] method_12006() {
		return this.field_12840;
	}

	@Override
	public class_2680 method_8320(class_2338 arg) {
		int i = arg.method_10263();
		int j = arg.method_10264();
		int k = arg.method_10260();
		if (this.field_12858.method_8527() == class_1942.field_9266) {
			class_2680 lv = null;
			if (j == 60) {
				lv = class_2246.field_10499.method_9564();
			}

			if (j == 70) {
				lv = class_2891.method_12578(i, k);
			}

			return lv == null ? class_2246.field_10124.method_9564() : lv;
		} else {
			try {
				if (j >= 0 && j >> 4 < this.field_12840.length) {
					class_2826 lv2 = this.field_12840[j >> 4];
					if (!class_2826.method_18090(lv2)) {
						return lv2.method_12254(i & 15, j & 15, k & 15);
					}
				}

				return class_2246.field_10124.method_9564();
			} catch (Throwable var8) {
				class_128 lv3 = class_128.method_560(var8, "Getting block state");
				class_129 lv4 = lv3.method_562("Block being got");
				lv4.method_577("Location", () -> class_129.method_581(i, j, k));
				throw new class_148(lv3);
			}
		}
	}

	@Override
	public class_3610 method_8316(class_2338 arg) {
		return this.method_12234(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public class_3610 method_12234(int i, int j, int k) {
		try {
			if (j >= 0 && j >> 4 < this.field_12840.length) {
				class_2826 lv = this.field_12840[j >> 4];
				if (!class_2826.method_18090(lv)) {
					return lv.method_12255(i & 15, j & 15, k & 15);
				}
			}

			return class_3612.field_15906.method_15785();
		} catch (Throwable var7) {
			class_128 lv2 = class_128.method_560(var7, "Getting fluid state");
			class_129 lv3 = lv2.method_562("Block being got");
			lv3.method_577("Location", () -> class_129.method_581(i, j, k));
			throw new class_148(lv2);
		}
	}

	@Nullable
	@Override
	public class_2680 method_12010(class_2338 arg, class_2680 arg2, boolean bl) {
		int i = arg.method_10263() & 15;
		int j = arg.method_10264();
		int k = arg.method_10260() & 15;
		class_2826 lv = this.field_12840[j >> 4];
		if (lv == field_12852) {
			if (arg2.method_11588()) {
				return null;
			}

			lv = new class_2826(j >> 4 << 4);
			this.field_12840[j >> 4] = lv;
		}

		boolean bl2 = lv.method_12261();
		class_2680 lv2 = lv.method_16675(i, j & 15, k, arg2);
		if (lv2 == arg2) {
			return null;
		} else {
			class_2248 lv3 = arg2.method_11614();
			class_2248 lv4 = lv2.method_11614();
			((class_2902)this.field_12853.get(class_2902.class_2903.field_13197)).method_12597(i, j, k, arg2);
			((class_2902)this.field_12853.get(class_2902.class_2903.field_13203)).method_12597(i, j, k, arg2);
			((class_2902)this.field_12853.get(class_2902.class_2903.field_13200)).method_12597(i, j, k, arg2);
			((class_2902)this.field_12853.get(class_2902.class_2903.field_13202)).method_12597(i, j, k, arg2);
			boolean bl3 = lv.method_12261();
			if (bl2 != bl3) {
				this.field_12858.method_8398().method_12130().method_15552(arg, bl3);
			}

			if (!this.field_12858.field_9236) {
				lv2.method_11600(this.field_12858, arg, arg2, bl);
			} else if (lv4 != lv3 && lv4 instanceof class_2343) {
				this.field_12858.method_8544(arg);
			}

			if (lv.method_12254(i, j & 15, k).method_11614() != lv3) {
				return null;
			} else {
				if (lv4 instanceof class_2343) {
					class_2586 lv5 = this.method_12201(arg, class_2818.class_2819.field_12859);
					if (lv5 != null) {
						lv5.method_11000();
					}
				}

				if (!this.field_12858.field_9236) {
					arg2.method_11580(this.field_12858, arg, lv2, bl);
				}

				if (lv3 instanceof class_2343) {
					class_2586 lv5 = this.method_12201(arg, class_2818.class_2819.field_12859);
					if (lv5 == null) {
						lv5 = ((class_2343)lv3).method_10123(this.field_12858);
						this.field_12858.method_8526(arg, lv5);
					} else {
						lv5.method_11000();
					}
				}

				this.field_12834 = true;
				return lv2;
			}
		}
	}

	@Nullable
	@Override
	public class_3568 method_12023() {
		return this.field_12858.method_8398().method_12130();
	}

	@Override
	public int method_8317(class_2338 arg) {
		return this.method_8320(arg).method_11630();
	}

	public int method_12233(class_2338 arg, int i) {
		return this.method_12035(arg, i, this.field_12858.method_8597().method_12451());
	}

	@Override
	public void method_12002(class_1297 arg) {
		this.field_12837 = true;
		int i = class_3532.method_15357(arg.field_5987 / 16.0);
		int j = class_3532.method_15357(arg.field_6035 / 16.0);
		if (i != this.field_12848.field_9181 || j != this.field_12848.field_9180) {
			field_12839.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.field_12848.field_9181, this.field_12848.field_9180, arg);
			arg.method_5650();
		}

		int k = class_3532.method_15357(arg.field_6010 / 16.0);
		if (k < 0) {
			k = 0;
		}

		if (k >= this.field_12833.length) {
			k = this.field_12833.length - 1;
		}

		arg.field_6016 = true;
		arg.field_6024 = this.field_12848.field_9181;
		arg.field_5959 = k;
		arg.field_5980 = this.field_12848.field_9180;
		this.field_12833[k].add(arg);
	}

	@Override
	public void method_12037(class_2902.class_2903 arg, long[] ls) {
		((class_2902)this.field_12853.get(arg)).method_12600(ls);
	}

	public void method_12203(class_1297 arg) {
		this.method_12219(arg, arg.field_5959);
	}

	public void method_12219(class_1297 arg, int i) {
		if (i < 0) {
			i = 0;
		}

		if (i >= this.field_12833.length) {
			i = this.field_12833.length - 1;
		}

		this.field_12833[i].remove(arg);
	}

	@Override
	public int method_12005(class_2902.class_2903 arg, int i, int j) {
		return ((class_2902)this.field_12853.get(arg)).method_12603(i & 15, j & 15) - 1;
	}

	@Nullable
	private class_2586 method_12208(class_2338 arg) {
		class_2680 lv = this.method_8320(arg);
		class_2248 lv2 = lv.method_11614();
		return !lv2.method_9570() ? null : ((class_2343)lv2).method_10123(this.field_12858);
	}

	@Nullable
	@Override
	public class_2586 method_8321(class_2338 arg) {
		return this.method_12201(arg, class_2818.class_2819.field_12859);
	}

	@Nullable
	public class_2586 method_12201(class_2338 arg, class_2818.class_2819 arg2) {
		class_2586 lv = (class_2586)this.field_12854.get(arg);
		if (lv == null) {
			class_2487 lv2 = (class_2487)this.field_12846.remove(arg);
			if (lv2 != null) {
				class_2586 lv3 = this.method_12204(arg, lv2);
				if (lv3 != null) {
					return lv3;
				}
			}
		}

		if (lv == null) {
			if (arg2 == class_2818.class_2819.field_12860) {
				lv = this.method_12208(arg);
				this.field_12858.method_8526(arg, lv);
			}
		} else if (lv.method_11015()) {
			this.field_12854.remove(arg);
			return null;
		}

		return lv;
	}

	public void method_12216(class_2586 arg) {
		this.method_12007(arg.method_11016(), arg);
		if (this.field_12855 || this.field_12858.method_8608()) {
			this.field_12858.method_8438(arg);
		}
	}

	@Override
	public void method_12007(class_2338 arg, class_2586 arg2) {
		arg2.method_11009(this.field_12858);
		arg2.method_10998(arg);
		if (this.method_8320(arg).method_11614() instanceof class_2343) {
			if (this.field_12854.containsKey(arg)) {
				((class_2586)this.field_12854.get(arg)).method_11012();
			}

			arg2.method_10996();
			this.field_12854.put(arg.method_10062(), arg2);
		}
	}

	@Override
	public void method_12042(class_2487 arg) {
		this.field_12846.put(new class_2338(arg.method_10550("x"), arg.method_10550("y"), arg.method_10550("z")), arg);
	}

	@Override
	public void method_12041(class_2338 arg) {
		if (this.field_12855 || this.field_12858.method_8608()) {
			class_2586 lv = (class_2586)this.field_12854.remove(arg);
			if (lv != null) {
				lv.method_11012();
			}
		}
	}

	public void method_12206() {
		if (this.field_12850 != null) {
			this.field_12850.accept(this);
			this.field_12850 = null;
		}
	}

	public void method_12220() {
		this.field_12834 = true;
	}

	public void method_12205(@Nullable class_1297 arg, class_238 arg2, List<class_1297> list, @Nullable Predicate<? super class_1297> predicate) {
		int i = class_3532.method_15357((arg2.field_1322 - 2.0) / 16.0);
		int j = class_3532.method_15357((arg2.field_1325 + 2.0) / 16.0);
		i = class_3532.method_15340(i, 0, this.field_12833.length - 1);
		j = class_3532.method_15340(j, 0, this.field_12833.length - 1);

		for (int k = i; k <= j; k++) {
			if (!this.field_12833[k].isEmpty()) {
				for (class_1297 lv : this.field_12833[k]) {
					if (lv.method_5829().method_994(arg2) && lv != arg) {
						if (predicate == null || predicate.test(lv)) {
							list.add(lv);
						}

						if (lv instanceof class_1510) {
							for (class_1508 lv2 : ((class_1510)lv).method_5690()) {
								if (lv2 != arg && lv2.method_5829().method_994(arg2) && (predicate == null || predicate.test(lv2))) {
									list.add(lv2);
								}
							}
						}
					}
				}
			}
		}
	}

	public void method_18029(@Nullable class_1299<?> arg, class_238 arg2, List<class_1297> list, Predicate<? super class_1297> predicate) {
		int i = class_3532.method_15357((arg2.field_1322 - 2.0) / 16.0);
		int j = class_3532.method_15357((arg2.field_1325 + 2.0) / 16.0);
		i = class_3532.method_15340(i, 0, this.field_12833.length - 1);
		j = class_3532.method_15340(j, 0, this.field_12833.length - 1);

		for (int k = i; k <= j; k++) {
			for (class_1297 lv : this.field_12833[k].method_15216(class_1297.class)) {
				if ((arg == null || lv.method_5864() == arg) && lv.method_5829().method_994(arg2) && predicate.test(lv)) {
					list.add(lv);
				}
			}
		}
	}

	public <T extends class_1297> void method_12210(Class<? extends T> class_, class_238 arg, List<T> list, @Nullable Predicate<? super T> predicate) {
		int i = class_3532.method_15357((arg.field_1322 - 2.0) / 16.0);
		int j = class_3532.method_15357((arg.field_1325 + 2.0) / 16.0);
		i = class_3532.method_15340(i, 0, this.field_12833.length - 1);
		j = class_3532.method_15340(j, 0, this.field_12833.length - 1);

		for (int k = i; k <= j; k++) {
			for (T lv : this.field_12833[k].method_15216(class_)) {
				if (lv.method_5829().method_994(arg) && (predicate == null || predicate.test(lv))) {
					list.add(lv);
				}
			}
		}
	}

	public boolean method_12223() {
		return false;
	}

	@Override
	public class_1923 method_12004() {
		return this.field_12848;
	}

	@Environment(EnvType.CLIENT)
	public void method_12224(class_2540 arg, class_2487 arg2, int i, boolean bl) {
		Predicate<class_2338> predicate = bl ? argx -> true : argx -> (i & 1 << (argx.method_10264() >> 4)) != 0;
		Sets.newHashSet(this.field_12854.keySet()).stream().filter(predicate).forEach(this.field_12858::method_8544);

		for (int j = 0; j < this.field_12840.length; j++) {
			class_2826 lv = this.field_12840[j];
			if ((i & 1 << j) == 0) {
				if (bl && lv != field_12852) {
					this.field_12840[j] = field_12852;
				}
			} else {
				if (lv == field_12852) {
					lv = new class_2826(j << 4);
					this.field_12840[j] = lv;
				}

				lv.method_12258(arg);
			}
		}

		if (bl) {
			for (int jx = 0; jx < this.field_12842.length; jx++) {
				this.field_12842[jx] = class_2378.field_11153.method_10200(arg.readInt());
			}
		}

		for (class_2902.class_2903 lv2 : class_2902.class_2903.values()) {
			String string = lv2.method_12605();
			if (arg2.method_10573(string, 12)) {
				this.method_12037(lv2, arg2.method_10565(string));
			}
		}

		for (class_2586 lv3 : this.field_12854.values()) {
			lv3.method_11000();
		}
	}

	@Override
	public class_1959[] method_12036() {
		return this.field_12842;
	}

	public void method_12226(boolean bl) {
		this.field_12855 = bl;
	}

	public class_1937 method_12200() {
		return this.field_12858;
	}

	@Override
	public Collection<Entry<class_2902.class_2903, class_2902>> method_12011() {
		return Collections.unmodifiableSet(this.field_12853.entrySet());
	}

	public Map<class_2338, class_2586> method_12214() {
		return this.field_12854;
	}

	public class_3509<class_1297>[] method_12215() {
		return this.field_12833;
	}

	@Override
	public class_2487 method_12024(class_2338 arg) {
		return (class_2487)this.field_12846.get(arg);
	}

	@Override
	public Stream<class_2338> method_12018() {
		return StreamSupport.stream(
				class_2338.method_10094(
						this.field_12848.method_8326(), 0, this.field_12848.method_8328(), this.field_12848.method_8327(), 255, this.field_12848.method_8329()
					)
					.spliterator(),
				false
			)
			.filter(arg -> this.method_8320(arg).method_11630() != 0);
	}

	@Override
	public class_1951<class_2248> method_12013() {
		return this.field_12841;
	}

	@Override
	public class_1951<class_3611> method_12014() {
		return this.field_12857;
	}

	@Override
	public void method_12008(boolean bl) {
		this.field_12834 = bl;
	}

	@Override
	public boolean method_12044() {
		return this.field_12834 || this.field_12837 && this.field_12858.method_8510() != this.field_12844;
	}

	public void method_12232(boolean bl) {
		this.field_12837 = bl;
	}

	@Override
	public void method_12043(long l) {
		this.field_12844 = l;
	}

	@Nullable
	@Override
	public class_3449 method_12181(String string) {
		return (class_3449)this.field_12838.get(string);
	}

	@Override
	public void method_12184(String string, class_3449 arg) {
		this.field_12838.put(string, arg);
	}

	@Override
	public Map<String, class_3449> method_12016() {
		return this.field_12838;
	}

	@Override
	public void method_12034(Map<String, class_3449> map) {
		this.field_12838.clear();
		this.field_12838.putAll(map);
	}

	@Override
	public LongSet method_12180(String string) {
		return (LongSet)this.field_12845.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_12182(String string, long l) {
		((LongSet)this.field_12845.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
	}

	@Override
	public Map<String, LongSet> method_12179() {
		return this.field_12845;
	}

	@Override
	public void method_12183(Map<String, LongSet> map) {
		this.field_12845.clear();
		this.field_12845.putAll(map);
	}

	@Override
	public long method_12033() {
		return this.field_12843;
	}

	@Override
	public void method_12028(long l) {
		this.field_12843 = l;
	}

	public void method_12221() {
		class_1923 lv = this.method_12004();

		for (int i = 0; i < this.field_12836.length; i++) {
			if (this.field_12836[i] != null) {
				for (Short short_ : this.field_12836[i]) {
					class_2338 lv2 = class_2839.method_12314(short_, i, lv);
					class_2680 lv3 = this.method_8320(lv2);
					class_2680 lv4 = class_2248.method_9510(lv3, this.field_12858, lv2);
					this.field_12858.method_8652(lv2, lv4, 20);
				}

				this.field_12836[i].clear();
			}
		}

		if (this.field_12841 instanceof class_2850) {
			((class_2850)this.field_12841).method_12368(this.field_12858.method_8397(), arg -> this.method_8320(arg).method_11614());
		}

		if (this.field_12857 instanceof class_2850) {
			((class_2850)this.field_12857).method_12368(this.field_12858.method_8405(), arg -> this.method_8316(arg).method_15772());
		}

		for (class_2338 lv5 : Sets.newHashSet(this.field_12846.keySet())) {
			this.method_8321(lv5);
		}

		this.field_12846.clear();
		this.field_12849.method_12356(this);
	}

	@Nullable
	private class_2586 method_12204(class_2338 arg, class_2487 arg2) {
		class_2586 lv2;
		if ("DUMMY".equals(arg2.method_10558("id"))) {
			class_2248 lv = this.method_8320(arg).method_11614();
			if (lv instanceof class_2343) {
				lv2 = ((class_2343)lv).method_10123(this.field_12858);
			} else {
				lv2 = null;
				field_12839.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", arg, this.method_8320(arg));
			}
		} else {
			lv2 = class_2586.method_11005(arg2);
		}

		if (lv2 != null) {
			lv2.method_10998(arg);
			this.method_12216(lv2);
		} else {
			field_12839.warn("Tried to load a block entity for block {} but failed at location {}", this.method_8320(arg), arg);
		}

		return lv2;
	}

	@Override
	public class_2843 method_12003() {
		return this.field_12849;
	}

	@Override
	public ShortList[] method_12012() {
		return this.field_12836;
	}

	@Override
	public class_2806 method_12009() {
		return class_2806.field_12803;
	}

	public class_3193.class_3194 method_12225() {
		return this.field_12856 == null ? class_3193.class_3194.field_13876 : (class_3193.class_3194)this.field_12856.get();
	}

	public void method_12207(Supplier<class_3193.class_3194> supplier) {
		this.field_12856 = supplier;
	}

	@Override
	public void method_17032(class_3568 arg) {
	}

	@Override
	public boolean method_12038() {
		return this.field_12847;
	}

	@Override
	public void method_12020(boolean bl) {
		this.field_12847 = bl;
		this.method_12008(true);
	}

	public static enum class_2819 {
		field_12860,
		field_12861,
		field_12859;
	}
}
