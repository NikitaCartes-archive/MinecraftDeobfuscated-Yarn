package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3898 implements AutoCloseable {
	private static final Logger field_17212 = LogManager.getLogger();
	private final Long2ObjectLinkedOpenHashMap<class_3193> field_17213 = new Long2ObjectLinkedOpenHashMap<>();
	private final class_1937 field_17214;
	private final class_3227 field_17215;
	private final Executor field_17216;
	private final class_3193.class_3897 field_17217;
	private final class_2794<?> field_17218;
	private volatile Long2ObjectLinkedOpenHashMap<class_3193> field_17220 = this.field_17213.clone();
	private final LongSet field_17221 = new LongOpenHashSet();
	private boolean field_17222;
	private final class_3900 field_17223;
	private final class_3906<class_3900.class_3946<Runnable>> field_17224;
	private final class_3906<class_3900.class_3946<Runnable>> field_17226;
	private final class_3949 field_17442;
	private final class_2858 field_17227;
	private final class_3898.class_3216 field_17228;
	private final AtomicInteger field_17230 = new AtomicInteger();

	public class_3898(
		Executor executor, class_3193.class_3897 arg, Executor executor2, class_2858 arg2, class_2823 arg3, class_1937 arg4, class_2794<?> arg5, class_3949 arg6
	) {
		this.field_17214 = arg4;
		this.field_17217 = arg;
		this.field_17218 = arg5;
		this.field_17216 = executor2;
		this.field_17227 = arg2;
		class_3846<Runnable> lv = class_3846.method_16902(executor, "worldgen");
		class_3846<Runnable> lv2 = class_3846.method_16902(executor2, "main");
		this.field_17442 = arg6;
		class_3846<Runnable> lv3 = class_3846.method_16902(executor, "light");
		this.field_17223 = new class_3900(ImmutableList.of(lv, lv2, lv3), executor, Integer.MAX_VALUE);
		this.field_17224 = this.field_17223.method_17622(lv, false);
		this.field_17226 = this.field_17223.method_17622(lv2, false);
		this.field_17215 = new class_3227(arg3, this, this.field_17214.method_8597().method_12451(), lv3, this.field_17223.method_17622(lv3, false));
		this.field_17228 = new class_3898.class_3216(executor, executor2);
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
		if (j > class_3215.field_13922 && i > class_3215.field_13922) {
			return arg;
		} else {
			if (arg != null) {
				arg.method_15890(i);
			}

			if (arg != null) {
				if (i > class_3215.field_13922) {
					this.field_17221.add(l);
				} else {
					this.field_17221.remove(l);
				}
			}

			if (i <= class_3215.field_13922 && arg == null) {
				arg = new class_3193(new class_1923(l), i, this.field_17215, this.field_17223, this.field_17217);
				this.field_17213.put(l, arg);
				this.field_17222 = true;
			}

			return arg;
		}
	}

	public void close() {
		this.field_17223.close();
		this.field_17220.values().forEach(arg -> {
			class_2791 lv = arg.method_14010();
			if (lv != null) {
				this.method_17228(lv, false);
			}
		});
		this.field_17227.close();
	}

	protected void method_17242(boolean bl) {
		for (class_3193 lv : this.field_17220.values()) {
			class_2818 lv2 = lv.method_16144();
			if (lv2 != null) {
				this.method_17228(lv2, false);
			}
		}

		if (bl) {
			this.field_17227.method_12413();
		}
	}

	protected void method_17233(BooleanSupplier booleanSupplier) {
		if (!this.field_17214.method_8458()) {
			LongIterator longIterator = this.field_17221.iterator();

			for (int i = 0; longIterator.hasNext() && (booleanSupplier.getAsBoolean() || i < 200 || this.field_17221.size() > 2000); longIterator.remove()) {
				long l = longIterator.nextLong();
				class_3193 lv = this.field_17213.remove(l);
				if (lv != null) {
					this.field_17222 = true;
					i++;
					CompletableFuture<class_2791> completableFuture = lv.method_14000();
					completableFuture.thenAcceptAsync(arg -> {
						if (arg != null) {
							this.method_17228(arg, true);

							for (int ix = 0; ix < 16; ix++) {
								this.field_17215.method_15551(arg.method_12004().field_9181, ix, arg.method_12004().field_9180, true);
							}

							this.field_17215.method_17303();
							this.field_17442.method_17670(arg.method_12004(), null);
						}
					}, this.field_17216);
				}
			}
		}
	}

	protected void method_17244() {
		if (this.field_17222) {
			this.field_17220 = this.field_17213.clone();
			this.field_17222 = false;
		}
	}

	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_17236(class_3193 arg, class_2806 arg2) {
		class_1923 lv = arg.method_13994();
		if (arg2 == class_2806.field_12798) {
			return CompletableFuture.supplyAsync(() -> {
				class_2791 lvx = this.field_17227.method_12411(this.field_17214, lv.field_9181, lv.field_9180);
				if (lvx != null) {
					lvx.method_12043(this.field_17214.method_8510());
					return Either.left(lvx);
				} else {
					return Either.left(new class_2839(lv, class_2843.field_12950));
				}
			}, this.field_17216);
		} else {
			CompletableFuture<Either<List<class_2791>, class_3193.class_3724>> completableFuture = this.method_17220(
				lv, arg2.method_12152(), i -> this.method_17229(arg2, i)
			);
			return completableFuture.thenComposeAsync(
				either -> (CompletableFuture)either.map(
						list -> {
							try {
								CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuturex = arg2.method_12154(
										this.field_17214, this.field_17218, this.field_17215, this::method_17226, list
									)
									.thenApply(Either::left);
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
						argxx -> CompletableFuture.completedFuture(Either.right(argxx))
					),
				runnable -> this.field_17224.method_16901(class_3900.method_17629(arg, runnable))
			);
		}
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

	public CompletableFuture<class_2791> method_17226(class_2791 arg) {
		class_1923 lv = arg.method_12004();
		class_3193 lv2 = this.method_17216(lv.method_8324());
		if (lv2 == null) {
			throw new IllegalStateException("No chunk holder while trying to convert to full chunk: " + lv);
		} else {
			return CompletableFuture.supplyAsync(() -> {
				class_2818 lvx;
				if (arg instanceof class_2821) {
					lvx = ((class_2821)arg).method_12240();
				} else {
					lvx = new class_2818(this.field_17214, (class_2839)arg, lv.field_9181, lv.field_9180);
				}

				lvx.method_12207(() -> class_3193.method_14008(lv2.method_14005()));
				lvx.method_12206();
				return lvx;
			}, runnable -> this.field_17226.method_16901(class_3900.method_17629(lv2, () -> this.field_17216.execute(runnable))));
		}
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_17235(class_3193 arg) {
		class_1923 lv = arg.method_13994();
		CompletableFuture<Either<List<class_2791>, class_3193.class_3724>> completableFuture = this.method_17220(lv, 1, i -> class_2806.field_12803);
		return completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
				final class_2818 lvx = (class_2818)list.get(list.size() / 2);
				if (!Objects.equals(lv, lvx.method_12004())) {
					throw new IllegalStateException();
				} else if (!lvx.method_12229()) {
					return Either.right(new class_3193.class_3724() {
						public String toString() {
							return "Not isLoaded " + lv.method_12004().toString();
						}
					});
				} else {
					lvx.method_12221();
					this.field_17230.getAndIncrement();
					class_2596<?>[] lvs = new class_2596[2];
					this.field_17217.method_17211(lv, false, true).forEach(arg3 -> {
						if (lvs[0] == null) {
							lvs[0] = new class_2672(lvx, 65535);
							lvs[1] = new class_2676(lvx.method_12004(), this.field_17215);
						}

						arg3.method_14205(lv, lvs[0], lvs[1]);
					});
					return Either.left(lvx);
				}
			}), runnable -> this.field_17226.method_16901(class_3900.method_17629(arg, () -> this.field_17216.execute(runnable))));
	}

	public int method_17253() {
		return this.field_17230.get();
	}

	private void method_17228(class_2791 arg, boolean bl) {
		try {
			if (bl && arg instanceof class_2818) {
				((class_2818)arg).method_12213();
			}

			if (!arg.method_12044()) {
				return;
			}

			arg.method_12043(this.field_17214.method_8510());
			this.field_17227.method_12410(this.field_17214, arg);
			arg.method_12008(false);
		} catch (IOException var4) {
			field_17212.error("Couldn't save chunk", (Throwable)var4);
		} catch (class_1939 var5) {
			field_17212.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)var5);
		}
	}

	protected void method_17214(int i, int j) {
		for (class_3193 lv : this.field_17213.values()) {
			class_1923 lv2 = lv.method_13994();
			class_2596<?>[] lvs = new class_2596[2];
			this.field_17217.method_17210(lv2, false).forEach(arg2 -> {
				int k = class_3215.method_14141(lv2, arg2);
				boolean bl = k <= i;
				boolean bl2 = k <= j;
				this.method_17241(arg2, lv2, lvs, bl, bl2);
			});
		}
	}

	protected void method_17241(class_3222 arg, class_1923 arg2, class_2596<?>[] args, boolean bl, boolean bl2) {
		if (arg.field_6002 == this.field_17214) {
			if (bl2 && !bl) {
				class_3193 lv = this.method_17216(arg2.method_8324());
				if (lv != null) {
					class_2818 lv2 = lv.method_16144();
					if (lv2 != null) {
						if (args[0] == null) {
							args[0] = new class_2672(lv2, 65535);
							args[1] = new class_2676(arg2, this.field_17215);
						}

						arg.method_14205(arg2, args[0], args[1]);
					}
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

	protected ObjectBidirectionalIterator<Entry<class_3193>> method_17264() {
		return this.field_17220.long2ObjectEntrySet().fastIterator();
	}

	public void method_17250(BooleanSupplier booleanSupplier) {
		boolean bl;
		do {
			bl = this.field_17227.method_12412();
		} while (bl && booleanSupplier.getAsBoolean());
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
