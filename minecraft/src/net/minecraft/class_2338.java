package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class class_2338 extends class_2382 implements class_4213 {
	private static final Logger field_18789 = LogManager.getLogger();
	public static final class_2338 field_10980 = new class_2338(0, 0, 0);
	private static final int field_10978 = 1 + class_3532.method_15351(class_3532.method_15339(30000000));
	private static final int field_10977 = field_10978;
	private static final int field_10975 = 64 - field_10978 - field_10977;
	private static final long field_10976 = (1L << field_10978) - 1L;
	private static final long field_10974 = (1L << field_10975) - 1L;
	private static final long field_10973 = (1L << field_10977) - 1L;
	private static final int field_10983 = field_10975;
	private static final int field_10981 = field_10975 + field_10977;

	public class_2338(int i, int j, int k) {
		super(i, j, k);
	}

	public class_2338(double d, double e, double f) {
		super(d, e, f);
	}

	public class_2338(class_1297 arg) {
		this(arg.field_5987, arg.field_6010, arg.field_6035);
	}

	public class_2338(class_243 arg) {
		this(arg.field_1352, arg.field_1351, arg.field_1350);
	}

	public class_2338(class_2374 arg) {
		this(arg.method_10216(), arg.method_10214(), arg.method_10215());
	}

	public class_2338(class_2382 arg) {
		this(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public static <T> class_2338 method_19438(Dynamic<T> dynamic) {
		OfInt ofInt = dynamic.asIntStream().spliterator();
		int[] is = new int[3];
		if (ofInt.tryAdvance(i -> is[0] = i) && ofInt.tryAdvance(i -> is[1] = i)) {
			ofInt.tryAdvance(i -> is[2] = i);
		}

		return new class_2338(is[0], is[1], is[2]);
	}

	@Override
	public <T> T method_19508(DynamicOps<T> dynamicOps) {
		return dynamicOps.createIntList(IntStream.of(new int[]{this.method_10263(), this.method_10264(), this.method_10260()}));
	}

	public static long method_10060(long l, class_2350 arg) {
		return method_10096(l, arg.method_10148(), arg.method_10164(), arg.method_10165());
	}

	public static long method_10096(long l, int i, int j, int k) {
		return method_10064(method_10061(l) + i, method_10071(l) + j, method_10083(l) + k);
	}

	public static int method_10061(long l) {
		return (int)(l << 64 - field_10981 - field_10978 >> 64 - field_10978);
	}

	public static int method_10071(long l) {
		return (int)(l << 64 - field_10975 >> 64 - field_10975);
	}

	public static int method_10083(long l) {
		return (int)(l << 64 - field_10983 - field_10977 >> 64 - field_10977);
	}

	public static class_2338 method_10092(long l) {
		return new class_2338(method_10061(l), method_10071(l), method_10083(l));
	}

	public static long method_10064(int i, int j, int k) {
		long l = 0L;
		l |= ((long)i & field_10976) << field_10981;
		l |= ((long)j & field_10974) << 0;
		return l | ((long)k & field_10973) << field_10983;
	}

	public static long method_10091(long l) {
		return l & -16L;
	}

	public long method_10063() {
		return method_10064(this.method_10263(), this.method_10264(), this.method_10260());
	}

	public class_2338 method_10080(double d, double e, double f) {
		return d == 0.0 && e == 0.0 && f == 0.0
			? this
			: new class_2338((double)this.method_10263() + d, (double)this.method_10264() + e, (double)this.method_10260() + f);
	}

	public class_2338 method_10069(int i, int j, int k) {
		return i == 0 && j == 0 && k == 0 ? this : new class_2338(this.method_10263() + i, this.method_10264() + j, this.method_10260() + k);
	}

	public class_2338 method_10081(class_2382 arg) {
		return this.method_10069(arg.method_10263(), arg.method_10264(), arg.method_10260());
	}

	public class_2338 method_10059(class_2382 arg) {
		return this.method_10069(-arg.method_10263(), -arg.method_10264(), -arg.method_10260());
	}

	public class_2338 method_10084() {
		return this.method_10086(1);
	}

	public class_2338 method_10086(int i) {
		return this.method_10079(class_2350.field_11036, i);
	}

	public class_2338 method_10074() {
		return this.method_10087(1);
	}

	public class_2338 method_10087(int i) {
		return this.method_10079(class_2350.field_11033, i);
	}

	public class_2338 method_10095() {
		return this.method_10076(1);
	}

	public class_2338 method_10076(int i) {
		return this.method_10079(class_2350.field_11043, i);
	}

	public class_2338 method_10072() {
		return this.method_10077(1);
	}

	public class_2338 method_10077(int i) {
		return this.method_10079(class_2350.field_11035, i);
	}

	public class_2338 method_10067() {
		return this.method_10088(1);
	}

	public class_2338 method_10088(int i) {
		return this.method_10079(class_2350.field_11039, i);
	}

	public class_2338 method_10078() {
		return this.method_10089(1);
	}

	public class_2338 method_10089(int i) {
		return this.method_10079(class_2350.field_11034, i);
	}

	public class_2338 method_10093(class_2350 arg) {
		return this.method_10079(arg, 1);
	}

	public class_2338 method_10079(class_2350 arg, int i) {
		return i == 0
			? this
			: new class_2338(this.method_10263() + arg.method_10148() * i, this.method_10264() + arg.method_10164() * i, this.method_10260() + arg.method_10165() * i);
	}

	public class_2338 method_10070(class_2470 arg) {
		switch (arg) {
			case field_11467:
			default:
				return this;
			case field_11463:
				return new class_2338(-this.method_10260(), this.method_10264(), this.method_10263());
			case field_11464:
				return new class_2338(-this.method_10263(), this.method_10264(), -this.method_10260());
			case field_11465:
				return new class_2338(this.method_10260(), this.method_10264(), -this.method_10263());
		}
	}

	public class_2338 method_10075(class_2382 arg) {
		return new class_2338(
			this.method_10264() * arg.method_10260() - this.method_10260() * arg.method_10264(),
			this.method_10260() * arg.method_10263() - this.method_10263() * arg.method_10260(),
			this.method_10263() * arg.method_10264() - this.method_10264() * arg.method_10263()
		);
	}

	public class_2338 method_10062() {
		return this;
	}

	public static Iterable<class_2338> method_10097(class_2338 arg, class_2338 arg2) {
		return method_10094(
			Math.min(arg.method_10263(), arg2.method_10263()),
			Math.min(arg.method_10264(), arg2.method_10264()),
			Math.min(arg.method_10260(), arg2.method_10260()),
			Math.max(arg.method_10263(), arg2.method_10263()),
			Math.max(arg.method_10264(), arg2.method_10264()),
			Math.max(arg.method_10260(), arg2.method_10260())
		);
	}

	public static Stream<class_2338> method_20437(class_2338 arg, class_2338 arg2) {
		return method_17962(
			Math.min(arg.method_10263(), arg2.method_10263()),
			Math.min(arg.method_10264(), arg2.method_10264()),
			Math.min(arg.method_10260(), arg2.method_10260()),
			Math.max(arg.method_10263(), arg2.method_10263()),
			Math.max(arg.method_10264(), arg2.method_10264()),
			Math.max(arg.method_10260(), arg2.method_10260())
		);
	}

	public static Stream<class_2338> method_17962(int i, int j, int k, int l, int m, int n) {
		return StreamSupport.stream(new AbstractSpliterator<class_2338>((long)((l - i + 1) * (m - j + 1) * (n - k + 1)), 64) {
			final class_3980 field_17676 = new class_3980(i, j, k, l, m, n);
			final class_2338.class_2339 field_18231 = new class_2338.class_2339();

			public boolean tryAdvance(Consumer<? super class_2338> consumer) {
				if (this.field_17676.method_17963()) {
					consumer.accept(this.field_18231.method_10103(this.field_17676.method_18671(), this.field_17676.method_18672(), this.field_17676.method_18673()));
					return true;
				} else {
					return false;
				}
			}
		}, false);
	}

	public static Iterable<class_2338> method_10094(int i, int j, int k, int l, int m, int n) {
		return () -> new AbstractIterator<class_2338>() {
				final class_3980 field_17596 = new class_3980(i, j, k, l, m, n);
				final class_2338.class_2339 field_18232 = new class_2338.class_2339();

				protected class_2338 method_10106() {
					return (class_2338)(this.field_17596.method_17963()
						? this.field_18232.method_10103(this.field_17596.method_18671(), this.field_17596.method_18672(), this.field_17596.method_18673())
						: this.endOfData());
				}
			};
	}

	public static class class_2339 extends class_2338 {
		protected int field_10986;
		protected int field_10985;
		protected int field_10984;

		public class_2339() {
			this(0, 0, 0);
		}

		public class_2339(class_2338 arg) {
			this(arg.method_10263(), arg.method_10264(), arg.method_10260());
		}

		public class_2339(int i, int j, int k) {
			super(0, 0, 0);
			this.field_10986 = i;
			this.field_10985 = j;
			this.field_10984 = k;
		}

		public class_2339(double d, double e, double f) {
			this(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
		}

		@Override
		public class_2338 method_10080(double d, double e, double f) {
			return super.method_10080(d, e, f).method_10062();
		}

		@Override
		public class_2338 method_10069(int i, int j, int k) {
			return super.method_10069(i, j, k).method_10062();
		}

		@Override
		public class_2338 method_10079(class_2350 arg, int i) {
			return super.method_10079(arg, i).method_10062();
		}

		@Override
		public class_2338 method_10070(class_2470 arg) {
			return super.method_10070(arg).method_10062();
		}

		@Override
		public int method_10263() {
			return this.field_10986;
		}

		@Override
		public int method_10264() {
			return this.field_10985;
		}

		@Override
		public int method_10260() {
			return this.field_10984;
		}

		public class_2338.class_2339 method_10103(int i, int j, int k) {
			this.field_10986 = i;
			this.field_10985 = j;
			this.field_10984 = k;
			return this;
		}

		public class_2338.class_2339 method_10105(class_1297 arg) {
			return this.method_10102(arg.field_5987, arg.field_6010, arg.field_6035);
		}

		public class_2338.class_2339 method_10102(double d, double e, double f) {
			return this.method_10103(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
		}

		public class_2338.class_2339 method_10101(class_2382 arg) {
			return this.method_10103(arg.method_10263(), arg.method_10264(), arg.method_10260());
		}

		public class_2338.class_2339 method_16363(long l) {
			return this.method_10103(method_10061(l), method_10071(l), method_10083(l));
		}

		public class_2338.class_2339 method_17965(class_2335 arg, int i, int j, int k) {
			return this.method_10103(
				arg.method_10056(i, j, k, class_2350.class_2351.field_11048),
				arg.method_10056(i, j, k, class_2350.class_2351.field_11052),
				arg.method_10056(i, j, k, class_2350.class_2351.field_11051)
			);
		}

		public class_2338.class_2339 method_10098(class_2350 arg) {
			return this.method_10104(arg, 1);
		}

		public class_2338.class_2339 method_10104(class_2350 arg, int i) {
			return this.method_10103(this.field_10986 + arg.method_10148() * i, this.field_10985 + arg.method_10164() * i, this.field_10984 + arg.method_10165() * i);
		}

		public class_2338.class_2339 method_10100(int i, int j, int k) {
			return this.method_10103(this.field_10986 + i, this.field_10985 + j, this.field_10984 + k);
		}

		public void method_20787(int i) {
			this.field_10986 = i;
		}

		public void method_10099(int i) {
			this.field_10985 = i;
		}

		public void method_20788(int i) {
			this.field_10984 = i;
		}

		@Override
		public class_2338 method_10062() {
			return new class_2338(this);
		}
	}

	public static final class class_2340 extends class_2338.class_2339 implements AutoCloseable {
		private boolean field_11004;
		private static final List<class_2338.class_2340> field_11005 = Lists.<class_2338.class_2340>newArrayList();

		private class_2340(int i, int j, int k) {
			super(i, j, k);
		}

		public static class_2338.class_2340 method_10109() {
			return method_10111(0, 0, 0);
		}

		public static class_2338.class_2340 method_10117(class_1297 arg) {
			return method_10115(arg.field_5987, arg.field_6010, arg.field_6035);
		}

		public static class_2338.class_2340 method_10115(double d, double e, double f) {
			return method_10111(class_3532.method_15357(d), class_3532.method_15357(e), class_3532.method_15357(f));
		}

		public static class_2338.class_2340 method_10111(int i, int j, int k) {
			synchronized (field_11005) {
				if (!field_11005.isEmpty()) {
					class_2338.class_2340 lv = (class_2338.class_2340)field_11005.remove(field_11005.size() - 1);
					if (lv != null && lv.field_11004) {
						lv.field_11004 = false;
						lv.method_10113(i, j, k);
						return lv;
					}
				}
			}

			return new class_2338.class_2340(i, j, k);
		}

		public class_2338.class_2340 method_10113(int i, int j, int k) {
			return (class_2338.class_2340)super.method_10103(i, j, k);
		}

		public class_2338.class_2340 method_10110(class_1297 arg) {
			return (class_2338.class_2340)super.method_10105(arg);
		}

		public class_2338.class_2340 method_10112(double d, double e, double f) {
			return (class_2338.class_2340)super.method_10102(d, e, f);
		}

		public class_2338.class_2340 method_10114(class_2382 arg) {
			return (class_2338.class_2340)super.method_10101(arg);
		}

		public class_2338.class_2340 method_10118(class_2350 arg) {
			return (class_2338.class_2340)super.method_10098(arg);
		}

		public class_2338.class_2340 method_10116(class_2350 arg, int i) {
			return (class_2338.class_2340)super.method_10104(arg, i);
		}

		public class_2338.class_2340 method_10108(int i, int j, int k) {
			return (class_2338.class_2340)super.method_10100(i, j, k);
		}

		public void close() {
			synchronized (field_11005) {
				if (field_11005.size() < 100) {
					field_11005.add(this);
				}

				this.field_11004 = true;
			}
		}
	}
}
