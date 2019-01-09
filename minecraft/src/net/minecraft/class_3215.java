package net.minecraft;

import com.google.common.collect.Queues;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3215 extends class_2802 implements class_3193.class_3897 {
	private static final int field_13920 = (int)Math.pow(17.0, 2.0);
	public static final int field_13922 = 33 + class_2806.method_12155();
	private static final List<class_2806> field_13934 = class_2806.method_16558();
	private final class_3204 field_17252;
	private final class_2794<?> field_13939;
	private final class_1937 field_13945;
	private final Thread field_17253;
	private final class_3227 field_13921;
	private final Queue<Runnable> field_13918 = Queues.<Runnable>newConcurrentLinkedQueue();
	private final class_3210 field_13930 = new class_3210();
	private final class_3898 field_17254;
	private int field_13942;
	private long field_13928;
	private boolean field_13929 = true;
	private boolean field_13941 = true;

	public class_3215(class_1937 arg, class_2858 arg2, Executor executor, class_2794<?> arg3, int i, class_3949 arg4) {
		this.field_13945 = arg;
		this.field_13939 = arg3;
		this.field_17253 = Thread.currentThread();
		this.field_17254 = new class_3898(executor, this, this.field_13918::add, arg2, this, this.method_16434(), this.method_12129(), arg4);
		this.field_13921 = this.field_17254.method_17212();
		this.field_17252 = this.field_17254.method_17263();
		this.method_14144(i);
	}

	public class_3227 method_17293() {
		return this.field_13921;
	}

	@Nullable
	private class_3193 method_14131(long l) {
		return this.field_17254.method_17216(l);
	}

	private static double method_14137(class_1923 arg, class_1297 arg2) {
		double d = (double)(arg.field_9181 * 16 + 8);
		double e = (double)(arg.field_9180 * 16 + 8);
		double f = d - arg2.field_5987;
		double g = e - arg2.field_6035;
		return f * f + g * g;
	}

	private static int method_17295(class_1923 arg, class_3222 arg2, boolean bl) {
		int i;
		int j;
		if (bl) {
			class_1923 lv = arg2.method_14232();
			i = lv.field_9181;
			j = lv.field_9180;
		} else {
			i = class_3532.method_15357(arg2.field_5987 / 16.0);
			j = class_3532.method_15357(arg2.field_6035 / 16.0);
		}

		return method_14125(arg, i, j);
	}

	public static int method_14141(class_1923 arg, class_1297 arg2) {
		return method_14125(arg, class_3532.method_15357(arg2.field_5987 / 16.0), class_3532.method_15357(arg2.field_6035 / 16.0));
	}

	private static int method_14125(class_1923 arg, int i, int j) {
		int k = arg.field_9181 - i;
		int l = arg.field_9180 - j;
		return Math.max(Math.abs(k), Math.abs(l));
	}

	public int method_17301() {
		return this.field_17254.method_17253();
	}

	@Nullable
	@Override
	public class_2791 method_12121(int i, int j, class_2806 arg, boolean bl) {
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = this.method_17299(i, j, arg, bl);
		return ((Either)completableFuture.join()).map(argx -> argx, argx -> {
			if (bl) {
				throw new IllegalStateException("Chunk not there when requested: " + argx);
			} else {
				return null;
			}
		});
	}

	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_17299(int i, int j, class_2806 arg, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.field_17253;
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture;
		if (bl2) {
			completableFuture = this.method_14134(i, j, arg, bl);

			while (!completableFuture.isDone()) {
				if (!this.method_17302()) {
					Thread.yield();
				}
			}
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.method_14134(i, j, arg, bl), this.field_13918::add)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<Either<class_2791, class_3193.class_3724>> method_14134(int i, int j, class_2806 arg, boolean bl) {
		class_1923 lv = new class_1923(i, j);
		long l = lv.method_8324();
		class_3193 lv2 = this.method_14131(l);
		int k = 33 + class_2806.method_12175(arg);
		if (bl && (lv2 == null || lv2.method_14005() > k)) {
			this.method_16155();
			lv2 = this.method_14131(l);
			if (lv2 == null || lv2.method_14005() > k) {
				this.field_17252.method_17290(class_3230.field_14032, lv, k, lv);
				this.method_16155();
				lv2 = this.method_14131(l);
			}

			if (lv2 == null || lv2.method_14005() > k) {
				throw new IllegalStateException("No chunk holder after ticket has been added");
			}
		}

		if (lv2 != null && lv2.method_14005() <= k) {
			return lv2.method_13997(arg);
		} else if (bl) {
			throw new IllegalStateException("No future after ticket has been added");
		} else {
			return class_3193.field_16430;
		}
	}

	@Override
	public boolean method_12123(int i, int j) {
		class_3193 lv = this.method_14131(new class_1923(i, j).method_8324());
		int k = 33 + class_2806.method_12175(class_2806.field_12803);
		return lv != null && lv.method_14005() <= k ? ((Either)lv.method_13997(class_2806.field_12803).getNow(class_3193.field_16426)).left().isPresent() : false;
	}

	@Override
	public class_1922 method_12246(int i, int j) {
		long l = class_1923.method_8331(i, j);
		class_3193 lv = this.method_14131(l);
		if (lv == null) {
			return null;
		} else {
			int k = field_13934.size() - 1;

			while (true) {
				class_2806 lv2 = (class_2806)field_13934.get(k);
				Optional<class_2791> optional = ((Either)lv.method_16146(lv2).getNow(class_3193.field_16426)).left();
				if (optional.isPresent()) {
					return (class_1922)optional.get();
				}

				if (lv2 == class_2806.field_12805.method_16560()) {
					return null;
				}

				k--;
			}
		}
	}

	public class_1937 method_16434() {
		return this.field_13945;
	}

	public boolean method_17302() {
		if (this.method_16155()) {
			return true;
		} else {
			Runnable runnable = (Runnable)this.field_13918.poll();
			if (runnable != null) {
				runnable.run();
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean method_16155() {
		if (this.field_17252.method_15892(this.field_17254)) {
			this.field_17254.method_17244();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_12125(class_1297 arg) {
		class_3193 lv = this.method_14131(class_1923.method_8331(class_3532.method_15357(arg.field_5987) >> 4, class_3532.method_15357(arg.field_6035) >> 4));
		if (lv == null) {
			return false;
		} else {
			Either<class_2818, class_3193.class_3724> either = (Either<class_2818, class_3193.class_3724>)lv.method_14003().getNow(class_3193.field_16427);
			return either.left().isPresent();
		}
	}

	public void method_17298(boolean bl) {
		this.field_17254.method_17242(bl);
	}

	@Override
	public void close() {
		this.field_13921.close();
		this.field_17254.close();
	}

	@Override
	public void method_12127(BooleanSupplier booleanSupplier) {
		this.field_13945.method_16107().method_15396("purge");
		this.field_17252.method_14045();
		this.method_16155();
		this.field_13945.method_16107().method_15405("light");
		this.field_13921.method_17303();
		this.field_13945.method_16107().method_15405("chunks");
		this.method_14161();
		this.field_13945.method_16107().method_15405("unload");
		this.field_17254.method_17233(booleanSupplier);
		this.field_13945.method_16107().method_15405("promote_chunks");
		this.field_13945.method_16107().method_15405("storage");
		this.field_17254.method_17250(booleanSupplier);
		this.field_13945.method_16107().method_15407();
	}

	private void method_14161() {
		long l = this.field_13945.method_8510();
		long m = l - this.field_13928;
		this.field_13928 = l;
		class_31 lv = this.field_13945.method_8401();
		boolean bl = lv.method_153() == class_1942.field_9266;
		boolean bl2 = this.field_13945.method_8450().method_8355("doMobSpawning");
		if (!bl) {
			this.field_13945.method_16107().method_15396("pollingChunks");
			int i = this.field_17252.method_14052();
			int j = this.field_13945.method_8450().method_8356("randomTickSpeed");
			class_2338 lv2 = this.field_13945.method_8395();
			boolean bl3 = lv.method_188() % 400L == 0L;
			class_1311[] lvs = class_1311.values();
			Object2IntMap<class_1311> object2IntMap = this.field_13945.method_17450();
			ObjectBidirectionalIterator<Entry<class_3193>> objectBidirectionalIterator = this.field_17254.method_17264();

			while (objectBidirectionalIterator.hasNext()) {
				Entry<class_3193> entry = (Entry<class_3193>)objectBidirectionalIterator.next();
				class_3193 lv3 = (class_3193)entry.getValue();
				class_2818 lv4 = lv3.method_16144();
				if (lv4 != null) {
					lv3.method_14006(lv4);
					class_1923 lv5 = lv3.method_13994();
					if (!this.field_13930.method_14083(lv5.method_8324()).noneMatch(arg2 -> method_14137(lv5, arg2) < 16384.0)) {
						lv4.method_12028(lv4.method_12033() + m);
						if (bl2 && (this.field_13929 || this.field_13941) && this.field_13945.method_8621().method_11951(lv4.method_12004())) {
							this.field_13945.method_16107().method_15396("spawner");

							for (class_1311 lv6 : lvs) {
								if ((!lv6.method_6136() || this.field_13941) && (lv6.method_6136() || this.field_13929) && (!lv6.method_6135() || bl3)) {
									int k = lv6.method_6134() * i / field_13920;
									if (object2IntMap.getInt(lv6) <= k) {
										class_1948.method_8663(lv6, this.field_13945, lv4, lv2);
									}
								}
							}

							this.field_13945.method_16107().method_15407();
						}

						this.field_13945.method_8462(lv4, j);
					}
				}
			}

			this.field_13945.method_16107().method_15407();
			if (bl2) {
				this.field_13939.method_12099(this.field_13945, this.field_13929, this.field_13941);
			}
		}
	}

	@Override
	public String method_12122() {
		return "ServerChunkCache: " + this.method_14151();
	}

	@Override
	public class_2794<?> method_12129() {
		return this.field_13939;
	}

	public int method_14151() {
		return this.field_17254.method_17260();
	}

	public void method_14128(class_2338 arg) {
		int i = arg.method_10263() >> 4;
		int j = arg.method_10260() >> 4;
		class_3193 lv = this.method_14131(class_1923.method_8331(i, j));
		if (lv != null) {
			lv.method_14002(arg.method_10263() & 15, arg.method_10264(), arg.method_10260() & 15);
		}
	}

	@Override
	public void method_12247(class_1944 arg, int i, int j, int k) {
		this.field_13918.add((Runnable)() -> {
			class_3193 lv = this.method_14131(class_1923.method_8331(i, k));
			if (lv != null) {
				lv.method_14012(arg, j);
			}
		});
	}

	public <T> void method_17297(class_3230<T> arg, class_1923 arg2, int i, T object) {
		this.field_17252.method_17291(arg, arg2, i, object);
	}

	public <T> void method_17300(class_3230<T> arg, class_1923 arg2, int i, T object) {
		this.field_17252.method_17292(arg, arg2, i, object);
	}

	@Override
	public void method_12124(class_1923 arg, boolean bl) {
		this.field_17252.method_14036(arg, bl);
	}

	private boolean method_14113(class_3222 arg) {
		return arg.method_7325() && !this.field_13945.method_8450().method_8355("spectatorsGenerateChunks");
	}

	public void method_14156(class_3222 arg) {
		this.method_14096(arg, true);
	}

	public void method_14116(class_3222 arg) {
		this.method_14096(arg, false);
	}

	private void method_14096(class_3222 arg, boolean bl) {
		boolean bl2 = this.method_14113(arg);
		boolean bl3 = this.field_13930.method_14082(arg);
		int i = class_3532.method_15357(arg.field_5987) >> 4;
		int j = class_3532.method_15357(arg.field_6035) >> 4;
		long l = class_1923.method_8331(i, j);
		if (bl) {
			this.field_13930.method_14085(l, arg, bl2);
			if (!bl2) {
				this.field_17252.method_14048(l, arg);
			}
		} else {
			long m = arg.method_14232().method_8324();
			this.field_13930.method_14084(m, arg);
			if (!bl2) {
				this.field_17252.method_14051(m, arg);
			}
		}

		for (int k = i - this.field_13942; k <= i + this.field_13942; k++) {
			for (int n = j - this.field_13942; n <= j + this.field_13942; n++) {
				class_1923 lv = new class_1923(k, n);
				this.field_17254.method_17241(arg, lv, new class_2596[2], !bl && !bl3, bl && !bl2);
			}
		}
	}

	public void method_14143(class_3222 arg) {
		int i = class_3532.method_15357(arg.field_5987) >> 4;
		int j = class_3532.method_15357(arg.field_6035) >> 4;
		int k = arg.method_14232().field_9181;
		int l = arg.method_14232().field_9180;
		long m = class_1923.method_8331(k, l);
		long n = class_1923.method_8331(i, j);
		boolean bl = this.field_13930.method_14082(arg);
		boolean bl2 = this.method_14113(arg);
		boolean bl3 = m != n;
		if (bl3 || bl != bl2) {
			if (!bl && bl2 || bl3) {
				this.field_17252.method_14051(m, arg);
			}

			if (bl && !bl2 || bl3) {
				this.field_17252.method_14048(n, arg);
			}

			if (!bl && bl2) {
				this.field_13930.method_14086(arg);
			}

			if (bl && !bl2) {
				this.field_13930.method_14087(arg);
			}

			if (bl3) {
				this.field_13930.method_14081(m, n, arg);
			}

			int o = Math.min(i, k) - this.field_13942;
			int p = Math.min(j, l) - this.field_13942;
			int q = Math.max(i, k) + this.field_13942;
			int r = Math.max(j, l) + this.field_13942;

			for (int s = o; s <= q; s++) {
				for (int t = p; t <= r; t++) {
					class_1923 lv = new class_1923(s, t);
					boolean bl4 = !bl && method_14125(lv, k, l) <= this.field_13942;
					boolean bl5 = !bl2 && method_14125(lv, i, j) <= this.field_13942;
					this.field_17254.method_17241(arg, lv, new class_2596[2], bl4, bl5);
				}
			}
		}
	}

	public void method_14144(int i) {
		int j = class_3532.method_15340(i + 1, 3, 33);
		if (j != this.field_13942) {
			this.field_17254.method_17214(this.field_13942, j);
			this.field_13942 = j;
			this.field_17252.method_14049(j);
		}
	}

	public boolean method_14154(class_3222 arg, int i, int j) {
		class_1923 lv = new class_1923(i, j);
		class_3193 lv2 = this.method_14131(lv.method_8324());
		if (lv2 == null) {
			return false;
		} else {
			return lv2.method_16144() != null ? method_17295(lv, arg, false) <= this.field_13942 : false;
		}
	}

	@Override
	public Stream<class_3222> method_17211(class_1923 arg, boolean bl, boolean bl2) {
		return this.field_13930.method_14083(arg.method_8324()).filter(arg2 -> {
			int i = method_17295(arg, arg2, bl2);
			return i > this.field_13942 ? false : !bl || i == this.field_13942;
		});
	}

	@Override
	public void method_12128(boolean bl, boolean bl2) {
		this.field_13929 = bl;
		this.field_13941 = bl2;
	}

	@Environment(EnvType.CLIENT)
	public String method_17294(class_1923 arg) {
		return this.field_17254.method_17218(arg);
	}
}
