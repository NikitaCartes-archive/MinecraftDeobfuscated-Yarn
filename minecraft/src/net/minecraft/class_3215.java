package net.minecraft;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3215 extends class_2802 {
	private static final int field_13920 = (int)Math.pow(17.0, 2.0);
	private static final List<class_2806> field_13934 = class_2806.method_16558();
	private final class_3204 field_17252;
	private final class_2794<?> field_13939;
	private final class_3218 field_13945;
	private final Thread field_17253;
	private final class_3227 field_13921;
	private final class_3215.class_4212 field_18809;
	public final class_3898 field_17254;
	private final class_26 field_17708;
	private long field_13928;
	private boolean field_13929 = true;
	private boolean field_13941 = true;
	private final long[] field_19335 = new long[4];
	private final class_2806[] field_19336 = new class_2806[4];
	private final class_2791[] field_19337 = new class_2791[4];

	public class_3215(
		class_3218 arg, File file, DataFixer dataFixer, class_3485 arg2, Executor executor, class_2794<?> arg3, int i, class_3949 arg4, Supplier<class_26> supplier
	) {
		this.field_13945 = arg;
		this.field_18809 = new class_3215.class_4212(arg);
		this.field_13939 = arg3;
		this.field_17253 = Thread.currentThread();
		File file2 = arg.method_8597().method_12460().method_12488(file);
		File file3 = new File(file2, "data");
		file3.mkdirs();
		this.field_17708 = new class_26(file3, dataFixer);
		this.field_17254 = new class_3898(arg, file, dataFixer, arg2, executor, this.field_18809, this, this.method_12129(), arg4, supplier, i);
		this.field_13921 = this.field_17254.method_17212();
		this.field_17252 = this.field_17254.method_17263();
		this.method_20587();
	}

	public class_3227 method_17293() {
		return this.field_13921;
	}

	@Nullable
	private class_3193 method_14131(long l) {
		return this.field_17254.method_17216(l);
	}

	public int method_17301() {
		return this.field_17254.method_17253();
	}

	@Nullable
	@Override
	public class_2791 method_12121(int i, int j, class_2806 arg, boolean bl) {
		if (Thread.currentThread() != this.field_17253) {
			return (class_2791)CompletableFuture.supplyAsync(() -> this.method_12121(i, j, arg, bl), this.field_18809).join();
		} else {
			long l = class_1923.method_8331(i, j);

			for (int k = 0; k < 4; k++) {
				if (l == this.field_19335[k] && arg == this.field_19336[k]) {
					class_2791 lv = this.field_19337[k];
					if (lv != null || !bl) {
						return lv;
					}
				}
			}

			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = this.method_14134(i, j, arg, bl);
			this.field_18809.method_18857(completableFuture::isDone);
			class_2791 lv = ((Either)completableFuture.join()).map(argx -> argx, argx -> {
				if (bl) {
					throw new IllegalStateException("Chunk not there when requested: " + argx);
				} else {
					return null;
				}
			});

			for (int m = 3; m > 0; m--) {
				this.field_19335[m] = this.field_19335[m - 1];
				this.field_19336[m] = this.field_19336[m - 1];
				this.field_19337[m] = this.field_19337[m - 1];
			}

			this.field_19335[0] = l;
			this.field_19336[0] = arg;
			this.field_19337[0] = lv;
			return lv;
		}
	}

	private void method_20587() {
		Arrays.fill(this.field_19335, class_1923.field_17348);
		Arrays.fill(this.field_19336, null);
		Arrays.fill(this.field_19337, null);
	}

	@Environment(EnvType.CLIENT)
	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_17299(int i, int j, class_2806 arg, boolean bl) {
		boolean bl2 = Thread.currentThread() == this.field_17253;
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture;
		if (bl2) {
			completableFuture = this.method_14134(i, j, arg, bl);
			this.field_18809.method_18857(completableFuture::isDone);
		} else {
			completableFuture = CompletableFuture.supplyAsync(() -> this.method_14134(i, j, arg, bl), this.field_18809)
				.thenCompose(completableFuturex -> completableFuturex);
		}

		return completableFuture;
	}

	private CompletableFuture<Either<class_2791, class_3193.class_3724>> method_14134(int i, int j, class_2806 arg, boolean bl) {
		class_1923 lv = new class_1923(i, j);
		long l = lv.method_8324();
		int k = 33 + class_2806.method_12175(arg);
		class_3193 lv2 = this.method_14131(l);
		if (bl) {
			this.field_17252.method_17290(class_3230.field_14032, lv, k, lv);
			if (this.method_18752(lv2, k)) {
				class_3695 lv3 = this.field_13945.method_16107();
				lv3.method_15396("chunkLoad");
				this.method_16155();
				lv2 = this.method_14131(l);
				lv3.method_15407();
				if (this.method_18752(lv2, k)) {
					throw new IllegalStateException("No chunk holder after ticket has been added");
				}
			}
		}

		return this.method_18752(lv2, k) ? class_3193.field_16430 : lv2.method_13993(arg, this.field_17254);
	}

	private boolean method_18752(@Nullable class_3193 arg, int i) {
		return arg == null || arg.method_14005() > i;
	}

	@Override
	public boolean method_12123(int i, int j) {
		class_3193 lv = this.method_14131(new class_1923(i, j).method_8324());
		int k = 33 + class_2806.method_12175(class_2806.field_12803);
		return !this.method_18752(lv, k);
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

	public boolean method_19492() {
		return this.field_18809.method_16075();
	}

	private boolean method_16155() {
		boolean bl = this.field_17252.method_15892(this.field_17254);
		boolean bl2 = this.field_17254.method_17244();
		if (!bl && !bl2) {
			return false;
		} else {
			this.method_20587();
			return true;
		}
	}

	@Override
	public boolean method_12125(class_1297 arg) {
		long l = class_1923.method_8331(class_3532.method_15357(arg.field_5987) >> 4, class_3532.method_15357(arg.field_6035) >> 4);
		return this.method_20585(l, class_3193::method_14003);
	}

	@Override
	public boolean method_20591(class_1923 arg) {
		return this.method_20585(arg.method_8324(), class_3193::method_14003);
	}

	@Override
	public boolean method_20529(class_2338 arg) {
		long l = class_1923.method_8331(arg.method_10263() >> 4, arg.method_10260() >> 4);
		return this.method_20585(l, class_3193::method_16145);
	}

	public boolean method_20727(class_1297 arg) {
		long l = class_1923.method_8331(class_3532.method_15357(arg.field_5987) >> 4, class_3532.method_15357(arg.field_6035) >> 4);
		return this.method_20585(l, class_3193::method_20725);
	}

	private boolean method_20585(long l, Function<class_3193, CompletableFuture<Either<class_2818, class_3193.class_3724>>> function) {
		class_3193 lv = this.method_14131(l);
		if (lv == null) {
			return false;
		} else {
			Either<class_2818, class_3193.class_3724> either = (Either<class_2818, class_3193.class_3724>)((CompletableFuture)function.apply(lv))
				.getNow(class_3193.field_16427);
			return either.left().isPresent();
		}
	}

	public void method_17298(boolean bl) {
		this.method_16155();
		this.field_17254.method_17242(bl);
	}

	@Override
	public void close() throws IOException {
		this.method_17298(true);
		this.field_13921.close();
		this.field_17254.close();
	}

	@Override
	public void method_12127(BooleanSupplier booleanSupplier) {
		this.field_13945.method_16107().method_15396("purge");
		this.field_17252.method_14045();
		this.method_16155();
		this.field_13945.method_16107().method_15405("chunks");
		this.method_14161();
		this.field_13945.method_16107().method_15405("unload");
		this.field_17254.method_17233(booleanSupplier);
		this.field_13945.method_16107().method_15407();
		this.method_20587();
	}

	private void method_14161() {
		long l = this.field_13945.method_8510();
		long m = l - this.field_13928;
		this.field_13928 = l;
		class_31 lv = this.field_13945.method_8401();
		boolean bl = lv.method_153() == class_1942.field_9266;
		boolean bl2 = this.field_13945.method_8450().method_8355(class_1928.field_19390);
		if (!bl) {
			this.field_13945.method_16107().method_15396("pollingChunks");
			int i = this.field_13945.method_8450().method_8356(class_1928.field_19399);
			class_2338 lv2 = this.field_13945.method_8395();
			boolean bl3 = lv.method_188() % 400L == 0L;
			this.field_13945.method_16107().method_15396("naturalSpawnCount");
			int j = this.field_17252.method_14052();
			class_1311[] lvs = class_1311.values();
			Object2IntMap<class_1311> object2IntMap = this.field_13945.method_18219();
			this.field_13945.method_16107().method_15407();
			this.field_17254
				.method_17264()
				.forEach(
					arg2 -> {
						Optional<class_2818> optional = ((Either)arg2.method_14003().getNow(class_3193.field_16427)).left();
						if (optional.isPresent()) {
							class_2818 lvx = (class_2818)optional.get();
							this.field_13945.method_16107().method_15396("broadcast");
							arg2.method_14006(lvx);
							this.field_13945.method_16107().method_15407();
							class_1923 lv2x = arg2.method_13994();
							if (!this.field_17254.method_18724(lv2x)) {
								lvx.method_12028(lvx.method_12033() + m);
								if (bl2 && (this.field_13929 || this.field_13941) && this.field_13945.method_8621().method_11951(lvx.method_12004())) {
									this.field_13945.method_16107().method_15396("spawner");

									for (class_1311 lv3 : lvs) {
										if (lv3 != class_1311.field_17715
											&& (!lv3.method_6136() || this.field_13941)
											&& (lv3.method_6136() || this.field_13929)
											&& (!lv3.method_6135() || bl3)) {
											int k = lv3.method_6134() * j / field_13920;
											if (object2IntMap.getInt(lv3) <= k) {
												class_1948.method_8663(lv3, this.field_13945, lvx, lv2);
											}
										}
									}

									this.field_13945.method_16107().method_15407();
								}

								this.field_13945.method_18203(lvx, i);
							}
						}
					}
				);
			this.field_13945.method_16107().method_15396("customSpawners");
			if (bl2) {
				this.field_13939.method_12099(this.field_13945, this.field_13929, this.field_13941);
			}

			this.field_13945.method_16107().method_15407();
			this.field_13945.method_16107().method_15407();
		}

		this.field_17254.method_18727();
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
	public void method_12247(class_1944 arg, class_4076 arg2) {
		this.field_18809.execute(() -> {
			class_3193 lv = this.method_14131(arg2.method_18692().method_8324());
			if (lv != null) {
				lv.method_14012(arg, arg2.method_18683());
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

	public void method_14096(class_3222 arg) {
		this.field_17254.method_18713(arg);
	}

	public void method_18753(class_1297 arg) {
		this.field_17254.method_18716(arg);
	}

	public void method_18755(class_1297 arg) {
		this.field_17254.method_18701(arg);
	}

	public void method_18751(class_1297 arg, class_2596<?> arg2) {
		this.field_17254.method_18717(arg, arg2);
	}

	public void method_18754(class_1297 arg, class_2596<?> arg2) {
		this.field_17254.method_18702(arg, arg2);
	}

	public void method_14144(int i) {
		this.field_17254.method_17214(i);
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

	public class_26 method_17981() {
		return this.field_17708;
	}

	public class_4153 method_19493() {
		return this.field_17254.method_19488();
	}

	final class class_4212 extends class_1255<Runnable> {
		private class_4212(class_1937 arg2) {
			super("Chunk source main thread executor for " + class_2378.field_11155.method_10221(arg2.method_8597().method_12460()));
		}

		@Override
		protected Runnable method_16211(Runnable runnable) {
			return runnable;
		}

		@Override
		protected boolean method_18856(Runnable runnable) {
			return true;
		}

		@Override
		protected boolean method_5384() {
			return true;
		}

		@Override
		protected Thread method_3777() {
			return class_3215.this.field_17253;
		}

		@Override
		protected boolean method_16075() {
			if (class_3215.this.method_16155()) {
				return true;
			} else {
				class_3215.this.field_13921.method_17303();
				return super.method_16075();
			}
		}
	}
}
