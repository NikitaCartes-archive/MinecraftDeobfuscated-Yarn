package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3898 extends class_3977 implements class_3193.class_3897 {
	private static final Logger field_17212 = LogManager.getLogger();
	public static final int field_18239 = 33 + class_2806.method_12155();
	private final Long2ObjectLinkedOpenHashMap<class_3193> field_17213 = new Long2ObjectLinkedOpenHashMap<>();
	private volatile Long2ObjectLinkedOpenHashMap<class_3193> field_17220 = this.field_17213.clone();
	private final Long2ObjectLinkedOpenHashMap<class_3193> field_18807 = new Long2ObjectLinkedOpenHashMap<>();
	private final LongSet field_18307 = new LongOpenHashSet();
	private final class_3218 field_17214;
	private final class_3227 field_17215;
	private final class_1255<Runnable> field_17216;
	private final class_2794<?> field_17218;
	private final Supplier<class_26> field_17705;
	private final class_4153 field_18808;
	private final LongSet field_17221 = new LongOpenHashSet();
	private boolean field_17222;
	private final class_3900 field_17223;
	private final class_3906<class_3900.class_3946<Runnable>> field_17224;
	private final class_3906<class_3900.class_3946<Runnable>> field_17226;
	private final class_3949 field_17442;
	private final class_3898.class_3216 field_17228;
	private final AtomicInteger field_17230 = new AtomicInteger();
	private final class_3485 field_17706;
	private final File field_17707;
	private final class_3210 field_18241 = new class_3210();
	private final Int2ObjectMap<class_3898.class_3208> field_18242 = new Int2ObjectOpenHashMap<>();
	private final Queue<Runnable> field_19343 = Queues.<Runnable>newConcurrentLinkedQueue();
	private int field_18243;

	public class_3898(
		class_3218 arg,
		File file,
		DataFixer dataFixer,
		class_3485 arg2,
		Executor executor,
		class_1255<Runnable> arg3,
		class_2823 arg4,
		class_2794<?> arg5,
		class_3949 arg6,
		Supplier<class_26> supplier,
		int i
	) {
		super(new File(arg.method_8597().method_12460().method_12488(file), "region"), dataFixer);
		this.field_17706 = arg2;
		this.field_17707 = arg.method_8597().method_12460().method_12488(file);
		this.field_17214 = arg;
		this.field_17218 = arg5;
		this.field_17216 = arg3;
		class_3846<Runnable> lv = class_3846.method_16902(executor, "worldgen");
		class_3846<Runnable> lv2 = class_3846.method_16902(arg3, "main");
		this.field_17442 = arg6;
		class_3846<Runnable> lv3 = class_3846.method_16902(executor, "light");
		this.field_17223 = new class_3900(ImmutableList.of(lv, lv2, lv3), executor, Integer.MAX_VALUE);
		this.field_17224 = this.field_17223.method_17622(lv, false);
		this.field_17226 = this.field_17223.method_17622(lv2, false);
		this.field_17215 = new class_3227(arg4, this, this.field_17214.method_8597().method_12451(), lv3, this.field_17223.method_17622(lv3, false));
		this.field_17228 = new class_3898.class_3216(executor, arg3);
		this.field_17705 = supplier;
		this.field_18808 = new class_4153(new File(this.field_17707, "poi"), dataFixer);
		this.method_17214(i);
	}

	private static double method_18704(class_1923 arg, class_1297 arg2) {
		double d = (double)(arg.field_9181 * 16 + 8);
		double e = (double)(arg.field_9180 * 16 + 8);
		double f = d - arg2.field_5987;
		double g = e - arg2.field_6035;
		return f * f + g * g;
	}

	private static int method_18719(class_1923 arg, class_3222 arg2, boolean bl) {
		int i;
		int j;
		if (bl) {
			class_4076 lv = arg2.method_14232();
			i = lv.method_18674();
			j = lv.method_18687();
		} else {
			i = class_3532.method_15357(arg2.field_5987 / 16.0);
			j = class_3532.method_15357(arg2.field_6035 / 16.0);
		}

		return method_18703(arg, i, j);
	}

	private static int method_18703(class_1923 arg, int i, int j) {
		int k = arg.field_9181 - i;
		int l = arg.field_9180 - j;
		return Math.max(Math.abs(k), Math.abs(l));
	}

	protected class_3227 method_17212() {
		return this.field_17215;
	}

	@Nullable
	protected class_3193 method_17255(long l) {
		return this.field_17213.get(l);
	}

	@Nullable
	protected class_3193 method_17216(long l) {
		return this.field_17220.get(l);
	}

	protected IntSupplier method_17604(long l) {
		return () -> {
			class_3193 lv = this.method_17216(l);
			return lv == null ? class_3899.field_17241 - 1 : Math.min(lv.method_17208(), class_3899.field_17241 - 1);
		};
	}

	@Environment(EnvType.CLIENT)
	public String method_17218(class_1923 arg) {
		class_3193 lv = this.method_17216(arg.method_8324());
		if (lv == null) {
			return "null";
		} else {
			String string = lv.method_14005() + "\n";
			class_2806 lv2 = lv.method_16141();
			class_2791 lv3 = lv.method_14010();
			if (lv2 != null) {
				string = string + "St: §" + lv2.method_16559() + lv2 + '§' + "r\n";
			}

			if (lv3 != null) {
				string = string + "Ch: §" + lv3.method_12009().method_16559() + lv3.method_12009() + '§' + "r\n";
			}

			class_3193.class_3194 lv4 = lv.method_13995();
			string = string + "§" + lv4.ordinal() + lv4;
			return string + '§' + "r";
		}
	}

	private CompletableFuture<Either<List<class_2791>, class_3193.class_3724>> method_17220(class_1923 arg, int i, IntFunction<class_2806> intFunction) {
		List<CompletableFuture<Either<class_2791, class_3193.class_3724>>> list = Lists.<CompletableFuture<Either<class_2791, class_3193.class_3724>>>newArrayList();
		int j = arg.field_9181;
		int k = arg.field_9180;

		for (int l = -i; l <= i; l++) {
			for (int m = -i; m <= i; m++) {
				int n = Math.max(Math.abs(m), Math.abs(l));
				final class_1923 lv = new class_1923(j + m, k + l);
				long o = lv.method_8324();
				class_3193 lv2 = this.method_17255(o);
				if (lv2 == null) {
					return CompletableFuture.completedFuture(Either.right(new class_3193.class_3724() {
						public String toString() {
							return "Unloaded " + lv.toString();
						}
					}));
				}

				class_2806 lv3 = (class_2806)intFunction.apply(n);
				CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = lv2.method_13993(lv3, this);
				list.add(completableFuture);
			}
		}

		CompletableFuture<List<Either<class_2791, class_3193.class_3724>>> completableFuture2 = class_156.method_652(list);
		return completableFuture2.thenApply(listx -> {
			List<class_2791> list2 = Lists.<class_2791>newArrayList();
			int l = 0;

			for (final Either<class_2791, class_3193.class_3724> either : listx) {
				Optional<class_2791> optional = either.left();
				if (!optional.isPresent()) {
					final int mx = l;
					return Either.right(new class_3193.class_3724() {
						public String toString() {
							return "Unloaded " + new class_1923(j + mx % (i * 2 + 1), k + mx / (i * 2 + 1)) + " " + ((class_3193.class_3724)either.right().get()).toString();
						}
					});
				}

				list2.add(optional.get());
				l++;
			}

			return Either.left(list2);
		});
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_17247(class_1923 arg) {
		return this.method_17220(arg, 2, i -> class_2806.field_12803)
			.thenApplyAsync(either -> either.mapLeft(list -> (class_2818)list.get(list.size() / 2)), this.field_17216);
	}

	@Nullable
	private class_3193 method_17217(long l, int i, @Nullable class_3193 arg, int j) {
		if (j > field_18239 && i > field_18239) {
			return arg;
		} else {
			if (arg != null) {
				arg.method_15890(i);
			}

			if (arg != null) {
				if (i > field_18239) {
					this.field_17221.add(l);
				} else {
					this.field_17221.remove(l);
				}
			}

			if (i <= field_18239 && arg == null) {
				arg = this.field_18807.remove(l);
				if (arg != null) {
					arg.method_15890(i);
				} else {
					arg = new class_3193(new class_1923(l), i, this.field_17215, this.field_17223, this);
				}

				this.field_17213.put(l, arg);
				this.field_17222 = true;
			}

			return arg;
		}
	}

	@Override
	public void close() throws IOException {
		this.field_17223.close();
		this.field_18808.close();
		super.close();
	}

	protected void method_17242(boolean bl) {
		if (bl) {
			List<class_3193> list = (List<class_3193>)this.field_17220
				.values()
				.stream()
				.filter(class_3193::method_20384)
				.peek(class_3193::method_20385)
				.collect(Collectors.toList());
			MutableBoolean mutableBoolean = new MutableBoolean();

			do {
				mutableBoolean.setFalse();
				list.stream().map(arg -> {
					CompletableFuture<class_2791> completableFuture;
					do {
						completableFuture = arg.method_14000();
						this.field_17216.method_18857(completableFuture::isDone);
					} while (completableFuture != arg.method_14000());

					return (class_2791)completableFuture.join();
				}).filter(arg -> arg instanceof class_2821 || arg instanceof class_2818).filter(this::method_17228).forEach(arg -> mutableBoolean.setTrue());
			} while (mutableBoolean.isTrue());

			this.method_20605(() -> true);
			field_17212.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.field_17707.getName());
		} else {
			this.field_17220.values().stream().filter(class_3193::method_20384).forEach(arg -> {
				class_2791 lv = (class_2791)arg.method_14000().getNow(null);
				if (lv instanceof class_2821 || lv instanceof class_2818) {
					this.method_17228(lv);
					arg.method_20385();
				}
			});
		}
	}

	protected void method_17233(BooleanSupplier booleanSupplier) {
		class_3695 lv = this.field_17214.method_16107();
		lv.method_15396("poi");
		this.field_18808.method_19290(booleanSupplier);
		lv.method_15405("chunk_unload");
		if (!this.field_17214.method_8458()) {
			this.method_20605(booleanSupplier);
		}

		lv.method_15407();
	}

	private void method_20605(BooleanSupplier booleanSupplier) {
		LongIterator longIterator = this.field_17221.iterator();

		for (int i = 0; longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_17221.size() > 2000); longIterator.remove()) {
			long l = longIterator.nextLong();
			class_3193 lv = this.field_17213.remove(l);
			if (lv != null) {
				this.field_18807.put(l, lv);
				this.field_17222 = true;
				i++;
				this.method_20458(l, lv);
			}
		}

		Runnable runnable;
		while (booleanSupplier.getAsBoolean() && (runnable = (Runnable)this.field_19343.poll()) != null) {
			runnable.run();
		}
	}

	private void method_20458(long l, class_3193 arg) {
		CompletableFuture<class_2791> completableFuture = arg.method_14000();
		completableFuture.thenAcceptAsync(arg2 -> {
			CompletableFuture<class_2791> completableFuture2 = arg.method_14000();
			if (completableFuture2 != completableFuture) {
				this.method_20458(l, arg);
			} else {
				if (this.field_18807.remove(l, arg) && arg2 != null) {
					if (arg2 instanceof class_2818) {
						((class_2818)arg2).method_12226(false);
					}

					this.method_17228(arg2);
					if (this.field_18307.remove(l) && arg2 instanceof class_2818) {
						class_2818 lv = (class_2818)arg2;
						this.field_17214.method_18764(lv);
					}

					this.field_17215.method_20386(arg2.method_12004());
					this.field_17215.method_17303();
					this.field_17442.method_17670(arg2.method_12004(), null);
				}
			}
		}, this.field_19343::add).whenComplete((void_, throwable) -> {
			if (throwable != null) {
				field_17212.error("Failed to save chunk " + arg.method_13994(), throwable);
			}
		});
	}

	protected boolean method_17244() {
		if (!this.field_17222) {
			return false;
		} else {
			this.field_17220 = this.field_17213.clone();
			this.field_17222 = false;
			return true;
		}
	}

	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_17236(class_3193 arg, class_2806 arg2) {
		class_1923 lv = arg.method_13994();
		if (arg2 == class_2806.field_12798) {
			return this.method_20619(lv);
		} else {
			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = arg.method_13993(arg2.method_16560(), this);
			return completableFuture.thenComposeAsync(either -> {
				Optional<class_2791> optional = either.left();
				if (!optional.isPresent()) {
					return CompletableFuture.completedFuture(either);
				} else {
					if (arg2 == class_2806.field_12805) {
						this.field_17228.method_17290(class_3230.field_19270, lv, 33 + class_2806.method_12175(class_2806.field_12795), lv);
					}

					class_2791 lvx = (class_2791)optional.get();
					if (lvx.method_12009().method_12165(arg2)) {
						CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuturex;
						if (arg2 == class_2806.field_12805) {
							completableFuturex = this.method_20617(arg, arg2);
						} else {
							completableFuturex = arg2.method_20612(this.field_17214, this.field_17706, this.field_17215, arg2xx -> this.method_17226(arg), lvx);
						}

						this.field_17442.method_17670(lv, arg2);
						return completableFuturex;
					} else {
						return this.method_20617(arg, arg2);
					}
				}
			}, this.field_17216);
		}
	}

	private CompletableFuture<Either<class_2791, class_3193.class_3724>> method_20619(class_1923 arg) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				class_2487 lv = this.method_17979(arg);
				if (lv != null) {
					boolean bl = lv.method_10573("Level", 10) && lv.method_10562("Level").method_10573("Status", 8);
					if (bl) {
						class_2791 lv2 = class_2852.method_12395(this.field_17214, this.field_17706, this.field_18808, arg, lv);
						lv2.method_12043(this.field_17214.method_8510());
						return Either.left(lv2);
					}

					field_17212.error("Chunk file at {} is missing level data, skipping", arg);
				}
			} catch (class_148 var5) {
				Throwable throwable = var5.getCause();
				if (!(throwable instanceof IOException)) {
					throw var5;
				}

				field_17212.error("Couldn't load chunk {}", arg, throwable);
			} catch (Exception var6) {
				field_17212.error("Couldn't load chunk {}", arg, var6);
			}

			return Either.left(new class_2839(arg, class_2843.field_12950));
		}, this.field_17216);
	}

	private CompletableFuture<Either<class_2791, class_3193.class_3724>> method_20617(class_3193 arg, class_2806 arg2) {
		class_1923 lv = arg.method_13994();
		CompletableFuture<Either<List<class_2791>, class_3193.class_3724>> completableFuture = this.method_17220(
			lv, arg2.method_12152(), i -> this.method_17229(arg2, i)
		);
		return completableFuture.thenComposeAsync(
			either -> (CompletableFuture)either.map(
					list -> {
						try {
							CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuturex = arg2.method_12154(
								this.field_17214, this.field_17218, this.field_17706, this.field_17215, arg2xxx -> this.method_17226(arg), list
							);
							this.field_17442.method_17670(lv, arg2);
							return completableFuturex;
						} catch (Exception var8) {
							class_128 lvx = class_128.method_560(var8, "Exception generating new chunk");
							class_129 lv2 = lvx.method_562("Chunk to be generated");
							lv2.method_578("Location", String.format("%d,%d", lv.field_9181, lv.field_9180));
							lv2.method_578("Position hash", class_1923.method_8331(lv.field_9181, lv.field_9180));
							lv2.method_578("Generator", this.field_17218);
							throw new class_148(lvx);
						}
					},
					arg2xx -> {
						this.method_20441(lv);
						return CompletableFuture.completedFuture(Either.right(arg2xx));
					}
				),
			runnable -> this.field_17224.method_16901(class_3900.method_17629(arg, runnable))
		);
	}

	protected void method_20441(class_1923 arg) {
		this.field_17216
			.method_18858(
				class_156.method_18839(
					() -> this.field_17228.method_20444(class_3230.field_19270, arg, 33 + class_2806.method_12175(class_2806.field_12795), arg),
					() -> "release light ticket " + arg
				)
			);
	}

	private class_2806 method_17229(class_2806 arg, int i) {
		class_2806 lv;
		if (i == 0) {
			lv = arg.method_16560();
		} else {
			lv = class_2806.method_12161(class_2806.method_12175(arg) + i);
		}

		return lv;
	}

	private CompletableFuture<Either<class_2791, class_3193.class_3724>> method_17226(class_3193 arg) {
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = arg.method_16146(class_2806.field_12803.method_16560());
		return completableFuture.thenApplyAsync(either -> {
			class_2806 lv = class_3193.method_14011(arg.method_14005());
			return !lv.method_12165(class_2806.field_12803) ? class_3193.field_16426 : either.mapLeft(arg2 -> {
				class_1923 lvx = arg.method_13994();
				class_2818 lv2;
				if (arg2 instanceof class_2821) {
					lv2 = ((class_2821)arg2).method_12240();
				} else {
					lv2 = new class_2818(this.field_17214, (class_2839)arg2);
					arg.method_20456(new class_2821(lv2));
				}

				lv2.method_12207(() -> class_3193.method_14008(arg.method_14005()));
				lv2.method_12206();
				if (this.field_18307.add(lvx.method_8324())) {
					lv2.method_12226(true);
					this.field_17214.method_8447(lv2.method_12214().values());
					List<class_1297> list = null;
					class_3509[] var6 = lv2.method_12215();
					int var7 = var6.length;

					for (int var8 = 0; var8 < var7; var8++) {
						for (class_1297 lv4 : var6[var8]) {
							if (!(lv4 instanceof class_1657) && !this.field_17214.method_18214(lv4)) {
								if (list == null) {
									list = Lists.<class_1297>newArrayList(lv4);
								} else {
									list.add(lv4);
								}
							}
						}
					}

					if (list != null) {
						list.forEach(lv2::method_12203);
					}
				}

				return lv2;
			});
		}, runnable -> this.field_17226.method_16901(class_3900.method_17626(runnable, arg.method_13994().method_8324(), arg::method_14005)));
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_17235(class_3193 arg) {
		class_1923 lv = arg.method_13994();
		CompletableFuture<Either<List<class_2791>, class_3193.class_3724>> completableFuture = this.method_17220(lv, 1, i -> class_2806.field_12803);
		CompletableFuture<Either<class_2818, class_3193.class_3724>> completableFuture2 = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
				class_2818 lvx = (class_2818)list.get(list.size() / 2);
				lvx.method_12221();
				return Either.left(lvx);
			}), runnable -> this.field_17226.method_16901(class_3900.method_17629(arg, runnable)));
		completableFuture2.thenAcceptAsync(either -> either.mapLeft(arg2 -> {
				this.field_17230.getAndIncrement();
				class_2596<?>[] lvs = new class_2596[2];
				this.method_17210(lv, false).forEach(arg2x -> this.method_18715(arg2x, lvs, arg2));
				return Either.left(arg2);
			}), runnable -> this.field_17226.method_16901(class_3900.method_17629(arg, runnable)));
		return completableFuture2;
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_20580(class_3193 arg) {
		return arg.method_13993(class_2806.field_12803, this).thenApplyAsync(either -> either.mapLeft(argx -> {
				class_2818 lv = (class_2818)argx;
				lv.method_20530();
				return lv;
			}), runnable -> this.field_17226.method_16901(class_3900.method_17629(arg, runnable)));
	}

	public int method_17253() {
		return this.field_17230.get();
	}

	private boolean method_17228(class_2791 arg) {
		this.field_18808.method_20436(arg.method_12004());
		if (!arg.method_12044()) {
			return false;
		} else {
			try {
				this.field_17214.method_8468();
			} catch (class_1939 var6) {
				field_17212.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)var6);
				return false;
			}

			arg.method_12043(this.field_17214.method_8510());
			arg.method_12008(false);
			class_1923 lv2 = arg.method_12004();

			try {
				class_2806 lv3 = arg.method_12009();
				if (lv3.method_12164() != class_2806.class_2808.field_12807) {
					class_2487 lv4 = this.method_17979(lv2);
					if (lv4 != null && class_2852.method_12377(lv4) == class_2806.class_2808.field_12807) {
						return false;
					}

					if (lv3 == class_2806.field_12798 && arg.method_12016().values().stream().noneMatch(class_3449::method_16657)) {
						return false;
					}
				}

				class_2487 lv4x = class_2852.method_12410(this.field_17214, arg);
				this.method_17910(lv2, lv4x);
				return true;
			} catch (Exception var5) {
				field_17212.error("Failed to save chunk {},{}", lv2.field_9181, lv2.field_9180, var5);
				return false;
			}
		}
	}

	protected void method_17214(int i) {
		int j = class_3532.method_15340(i + 1, 3, 33);
		if (j != this.field_18243) {
			int k = this.field_18243;
			this.field_18243 = j;
			this.field_17228.method_14049(this.field_18243);

			for (class_3193 lv : this.field_17213.values()) {
				class_1923 lv2 = lv.method_13994();
				class_2596<?>[] lvs = new class_2596[2];
				this.method_17210(lv2, false).forEach(arg2 -> {
					int jx = method_18719(lv2, arg2, true);
					boolean bl = jx <= k;
					boolean bl2 = jx <= this.field_18243;
					this.method_17241(arg2, lv2, lvs, bl, bl2);
				});
			}
		}
	}

	protected void method_17241(class_3222 arg, class_1923 arg2, class_2596<?>[] args, boolean bl, boolean bl2) {
		if (arg.field_6002 == this.field_17214) {
			if (bl2 && !bl) {
				class_3193 lv = this.method_17216(arg2.method_8324());
				if (lv != null) {
					class_2818 lv2 = lv.method_16144();
					if (lv2 != null) {
						this.method_18715(arg, args, lv2);
					}

					class_4209.method_19775(this.field_17214, arg2);
				}
			}

			if (!bl2 && bl) {
				arg.method_14246(arg2);
			}
		}
	}

	public int method_17260() {
		return this.field_17220.size();
	}

	protected class_3898.class_3216 method_17263() {
		return this.field_17228;
	}

	protected Iterable<class_3193> method_17264() {
		return Iterables.unmodifiableIterable(this.field_17220.values());
	}

	@Nullable
	private class_2487 method_17979(class_1923 arg) throws IOException {
		class_2487 lv = this.method_17911(arg);
		return lv == null ? null : this.method_17907(this.field_17214.method_8597().method_12460(), this.field_17705, lv);
	}

	boolean method_18724(class_1923 arg) {
		long l = arg.method_8324();
		return !this.field_17228.method_20800(l)
			? true
			: this.field_18241.method_14083(l).noneMatch(arg2 -> !arg2.method_7325() && method_18704(arg, arg2) < 16384.0);
	}

	private boolean method_18722(class_3222 arg) {
		return arg.method_7325() && !this.field_17214.method_8450().method_8355(class_1928.field_19402);
	}

	void method_18714(class_3222 arg, boolean bl) {
		boolean bl2 = this.method_18722(arg);
		boolean bl3 = this.field_18241.method_14082(arg);
		int i = class_3532.method_15357(arg.field_5987) >> 4;
		int j = class_3532.method_15357(arg.field_6035) >> 4;
		if (bl) {
			this.field_18241.method_14085(class_1923.method_8331(i, j), arg, bl2);
			this.method_20726(arg);
			if (!bl2) {
				this.field_17228.method_14048(class_4076.method_18680(arg), arg);
			}
		} else {
			class_4076 lv = arg.method_14232();
			this.field_18241.method_14084(lv.method_18692().method_8324(), arg);
			if (!bl3) {
				this.field_17228.method_14051(lv, arg);
			}
		}

		for (int k = i - this.field_18243; k <= i + this.field_18243; k++) {
			for (int l = j - this.field_18243; l <= j + this.field_18243; l++) {
				class_1923 lv2 = new class_1923(k, l);
				this.method_17241(arg, lv2, new class_2596[2], !bl, bl);
			}
		}
	}

	private class_4076 method_20726(class_3222 arg) {
		class_4076 lv = class_4076.method_18680(arg);
		arg.method_17668(lv);
		arg.field_13987.method_14364(new class_4282(lv.method_18674(), lv.method_18687()));
		return lv;
	}

	public void method_18713(class_3222 arg) {
		for (class_3898.class_3208 lv : this.field_18242.values()) {
			if (lv.field_18247 == arg) {
				lv.method_18729(this.field_17214.method_18456());
			} else {
				lv.method_18736(arg);
			}
		}

		int i = class_3532.method_15357(arg.field_5987) >> 4;
		int j = class_3532.method_15357(arg.field_6035) >> 4;
		class_4076 lv2 = arg.method_14232();
		class_4076 lv3 = class_4076.method_18680(arg);
		long l = lv2.method_18692().method_8324();
		long m = lv3.method_18692().method_8324();
		boolean bl = this.field_18241.method_14082(arg);
		boolean bl2 = this.method_18722(arg);
		boolean bl3 = lv2.method_18694() != lv3.method_18694();
		if (bl3 || bl != bl2) {
			this.method_20726(arg);
			if (!bl) {
				this.field_17228.method_14051(lv2, arg);
			}

			if (!bl2) {
				this.field_17228.method_14048(lv3, arg);
			}

			if (!bl && bl2) {
				this.field_18241.method_14086(arg);
			}

			if (bl && !bl2) {
				this.field_18241.method_14087(arg);
			}

			if (l != m) {
				this.field_18241.method_14081(l, m, arg);
			}
		}

		int k = lv2.method_18674();
		int n = lv2.method_18687();
		if (Math.abs(k - i) <= this.field_18243 * 2 && Math.abs(n - j) <= this.field_18243 * 2) {
			int o = Math.min(i, k) - this.field_18243;
			int p = Math.min(j, n) - this.field_18243;
			int q = Math.max(i, k) + this.field_18243;
			int r = Math.max(j, n) + this.field_18243;

			for (int s = o; s <= q; s++) {
				for (int t = p; t <= r; t++) {
					class_1923 lv4 = new class_1923(s, t);
					boolean bl4 = method_18703(lv4, k, n) <= this.field_18243;
					boolean bl5 = method_18703(lv4, i, j) <= this.field_18243;
					this.method_17241(arg, lv4, new class_2596[2], bl4, bl5);
				}
			}
		} else {
			for (int o = k - this.field_18243; o <= k + this.field_18243; o++) {
				for (int p = n - this.field_18243; p <= n + this.field_18243; p++) {
					class_1923 lv5 = new class_1923(o, p);
					boolean bl6 = true;
					boolean bl7 = false;
					this.method_17241(arg, lv5, new class_2596[2], true, false);
				}
			}

			for (int o = i - this.field_18243; o <= i + this.field_18243; o++) {
				for (int p = j - this.field_18243; p <= j + this.field_18243; p++) {
					class_1923 lv5 = new class_1923(o, p);
					boolean bl6 = false;
					boolean bl7 = true;
					this.method_17241(arg, lv5, new class_2596[2], false, true);
				}
			}
		}
	}

	@Override
	public Stream<class_3222> method_17210(class_1923 arg, boolean bl) {
		return this.field_18241.method_14083(arg.method_8324()).filter(arg2 -> {
			int i = method_18719(arg, arg2, true);
			return i > this.field_18243 ? false : !bl || i == this.field_18243;
		});
	}

	protected void method_18701(class_1297 arg) {
		if (!(arg instanceof class_1508)) {
			if (!(arg instanceof class_1538)) {
				class_1299<?> lv = arg.method_5864();
				int i = lv.method_18387() * 16;
				int j = lv.method_18388();
				if (this.field_18242.containsKey(arg.method_5628())) {
					throw new IllegalStateException("Entity is already tracked!");
				} else {
					class_3898.class_3208 lv2 = new class_3898.class_3208(arg, i, j, lv.method_18389());
					this.field_18242.put(arg.method_5628(), lv2);
					lv2.method_18729(this.field_17214.method_18456());
					if (arg instanceof class_3222) {
						class_3222 lv3 = (class_3222)arg;
						this.method_18714(lv3, true);

						for (class_3898.class_3208 lv4 : this.field_18242.values()) {
							if (lv4.field_18247 != lv3) {
								lv4.method_18736(lv3);
							}
						}
					}
				}
			}
		}
	}

	protected void method_18716(class_1297 arg) {
		if (arg instanceof class_3222) {
			class_3222 lv = (class_3222)arg;
			this.method_18714(lv, false);

			for (class_3898.class_3208 lv2 : this.field_18242.values()) {
				lv2.method_18733(lv);
			}
		}

		class_3898.class_3208 lv3 = this.field_18242.remove(arg.method_5628());
		if (lv3 != null) {
			lv3.method_18728();
		}
	}

	protected void method_18727() {
		List<class_3222> list = Lists.<class_3222>newArrayList();
		List<class_3222> list2 = this.field_17214.method_18456();

		for (class_3898.class_3208 lv : this.field_18242.values()) {
			class_4076 lv2 = lv.field_18249;
			class_4076 lv3 = class_4076.method_18680(lv.field_18247);
			if (!Objects.equals(lv2, lv3)) {
				lv.method_18729(list2);
				class_1297 lv4 = lv.field_18247;
				if (lv4 instanceof class_3222) {
					list.add((class_3222)lv4);
				}

				lv.field_18249 = lv3;
			}

			lv.field_18246.method_18756();
		}

		for (class_3898.class_3208 lv : this.field_18242.values()) {
			lv.method_18729(list);
		}
	}

	protected void method_18702(class_1297 arg, class_2596<?> arg2) {
		class_3898.class_3208 lv = this.field_18242.get(arg.method_5628());
		if (lv != null) {
			lv.method_18730(arg2);
		}
	}

	protected void method_18717(class_1297 arg, class_2596<?> arg2) {
		class_3898.class_3208 lv = this.field_18242.get(arg.method_5628());
		if (lv != null) {
			lv.method_18734(arg2);
		}
	}

	private void method_18715(class_3222 arg, class_2596<?>[] args, class_2818 arg2) {
		if (args[0] == null) {
			args[0] = new class_2672(arg2, 65535);
			args[1] = new class_2676(arg2.method_12004(), this.field_17215);
		}

		arg.method_14205(arg2.method_12004(), args[0], args[1]);
		class_4209.method_19775(this.field_17214, arg2.method_12004());
		List<class_1297> list = Lists.<class_1297>newArrayList();
		List<class_1297> list2 = Lists.<class_1297>newArrayList();

		for (class_3898.class_3208 lv : this.field_18242.values()) {
			class_1297 lv2 = lv.field_18247;
			if (lv2 != arg && lv2.field_6024 == arg2.method_12004().field_9181 && lv2.field_5980 == arg2.method_12004().field_9180) {
				lv.method_18736(arg);
				if (lv2 instanceof class_1308 && ((class_1308)lv2).method_5933() != null) {
					list.add(lv2);
				}

				if (!lv2.method_5685().isEmpty()) {
					list2.add(lv2);
				}
			}
		}

		if (!list.isEmpty()) {
			for (class_1297 lv3 : list) {
				arg.field_13987.method_14364(new class_2740(lv3, ((class_1308)lv3).method_5933()));
			}
		}

		if (!list2.isEmpty()) {
			for (class_1297 lv3 : list2) {
				arg.field_13987.method_14364(new class_2752(lv3));
			}
		}
	}

	protected class_4153 method_19488() {
		return this.field_18808;
	}

	public CompletableFuture<Void> method_20576(class_2818 arg) {
		return this.field_17216.method_20493(() -> arg.method_20471(this.field_17214));
	}

	class class_3208 {
		private final class_3231 field_18246;
		private final class_1297 field_18247;
		private final int field_18248;
		private class_4076 field_18249;
		private final Set<class_3222> field_18250 = Sets.<class_3222>newHashSet();

		public class_3208(class_1297 arg2, int i, int j, boolean bl) {
			this.field_18246 = new class_3231(class_3898.this.field_17214, arg2, j, bl, this::method_18730);
			this.field_18247 = arg2;
			this.field_18248 = i;
			this.field_18249 = class_4076.method_18680(arg2);
		}

		public boolean equals(Object object) {
			return object instanceof class_3898.class_3208 ? ((class_3898.class_3208)object).field_18247.method_5628() == this.field_18247.method_5628() : false;
		}

		public int hashCode() {
			return this.field_18247.method_5628();
		}

		public void method_18730(class_2596<?> arg) {
			for (class_3222 lv : this.field_18250) {
				lv.field_13987.method_14364(arg);
			}
		}

		public void method_18734(class_2596<?> arg) {
			this.method_18730(arg);
			if (this.field_18247 instanceof class_3222) {
				((class_3222)this.field_18247).field_13987.method_14364(arg);
			}
		}

		public void method_18728() {
			for (class_3222 lv : this.field_18250) {
				this.field_18246.method_14302(lv);
			}
		}

		public void method_18733(class_3222 arg) {
			if (this.field_18250.remove(arg)) {
				this.field_18246.method_14302(arg);
			}
		}

		public void method_18736(class_3222 arg) {
			if (arg != this.field_18247) {
				class_243 lv = new class_243(arg.field_5987, arg.field_6010, arg.field_6035).method_1020(this.field_18246.method_18759());
				int i = Math.min(this.field_18248, (class_3898.this.field_18243 - 1) * 16);
				boolean bl = lv.field_1352 >= (double)(-i)
					&& lv.field_1352 <= (double)i
					&& lv.field_1350 >= (double)(-i)
					&& lv.field_1350 <= (double)i
					&& this.field_18247.method_5680(arg);
				if (bl) {
					boolean bl2 = this.field_18247.field_5983;
					if (!bl2) {
						class_1923 lv2 = new class_1923(this.field_18247.field_6024, this.field_18247.field_5980);
						class_3193 lv3 = class_3898.this.method_17216(lv2.method_8324());
						if (lv3 != null && lv3.method_16144() != null) {
							bl2 = class_3898.method_18719(lv2, arg, false) <= class_3898.this.field_18243;
						}
					}

					if (bl2 && this.field_18250.add(arg)) {
						this.field_18246.method_18760(arg);
					}
				} else if (this.field_18250.remove(arg)) {
					this.field_18246.method_14302(arg);
				}
			}
		}

		public void method_18729(List<class_3222> list) {
			for (class_3222 lv : list) {
				this.method_18736(lv);
			}
		}
	}

	class class_3216 extends class_3204 {
		protected class_3216(Executor executor, Executor executor2) {
			super(executor, executor2);
		}

		@Override
		protected boolean method_14035(long l) {
			return class_3898.this.field_17221.contains(l);
		}

		@Nullable
		@Override
		protected class_3193 method_14038(long l) {
			return class_3898.this.method_17255(l);
		}

		@Nullable
		@Override
		protected class_3193 method_14053(long l, int i, @Nullable class_3193 arg, int j) {
			return class_3898.this.method_17217(l, i, arg, j);
		}
	}
}
