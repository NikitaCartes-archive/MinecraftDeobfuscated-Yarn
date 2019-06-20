package net.minecraft;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3193 {
	public static final Either<class_2791, class_3193.class_3724> field_16426 = Either.right(class_3193.class_3724.field_16433);
	public static final CompletableFuture<Either<class_2791, class_3193.class_3724>> field_16430 = CompletableFuture.completedFuture(field_16426);
	public static final Either<class_2818, class_3193.class_3724> field_16427 = Either.right(class_3193.class_3724.field_16433);
	private static final CompletableFuture<Either<class_2818, class_3193.class_3724>> field_16429 = CompletableFuture.completedFuture(field_16427);
	private static final List<class_2806> field_13868 = class_2806.method_16558();
	private static final class_3193.class_3194[] field_13873 = class_3193.class_3194.values();
	private final AtomicReferenceArray<CompletableFuture<Either<class_2791, class_3193.class_3724>>> field_16425 = new AtomicReferenceArray(field_13868.size());
	private volatile CompletableFuture<Either<class_2818, class_3193.class_3724>> field_16431 = field_16429;
	private volatile CompletableFuture<Either<class_2818, class_3193.class_3724>> field_19333 = field_16429;
	private volatile CompletableFuture<Either<class_2818, class_3193.class_3724>> field_13865 = field_16429;
	private CompletableFuture<class_2791> field_16428 = CompletableFuture.completedFuture(null);
	private int field_16432;
	private int field_13862;
	private int field_17208;
	private final class_1923 field_13864;
	private final short[] field_13861 = new short[64];
	private int field_13874;
	private int field_13872;
	private int field_16209;
	private int field_13871;
	private int field_13870;
	private final class_3568 field_13863;
	private final class_3193.class_3896 field_17209;
	private final class_3193.class_3897 field_17210;
	private boolean field_19238;

	public class_3193(class_1923 arg, int i, class_3568 arg2, class_3193.class_3896 arg3, class_3193.class_3897 arg4) {
		this.field_13864 = arg;
		this.field_13863 = arg2;
		this.field_17209 = arg3;
		this.field_17210 = arg4;
		this.field_16432 = class_3898.field_18239 + 1;
		this.field_13862 = this.field_16432;
		this.field_17208 = this.field_16432;
		this.method_15890(i);
	}

	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_16146(class_2806 arg) {
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = (CompletableFuture<Either<class_2791, class_3193.class_3724>>)this.field_16425
			.get(arg.method_16559());
		return completableFuture == null ? field_16430 : completableFuture;
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_16145() {
		return this.field_19333;
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_14003() {
		return this.field_13865;
	}

	public CompletableFuture<Either<class_2818, class_3193.class_3724>> method_20725() {
		return this.field_16431;
	}

	@Nullable
	public class_2818 method_16144() {
		CompletableFuture<Either<class_2818, class_3193.class_3724>> completableFuture = this.method_16145();
		Either<class_2818, class_3193.class_3724> either = (Either<class_2818, class_3193.class_3724>)completableFuture.getNow(null);
		return either == null ? null : (class_2818)either.left().orElse(null);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2806 method_16141() {
		for (int i = field_13868.size() - 1; i >= 0; i--) {
			class_2806 lv = (class_2806)field_13868.get(i);
			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = this.method_16146(lv);
			if (((Either)completableFuture.getNow(field_16426)).left().isPresent()) {
				return lv;
			}
		}

		return null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public class_2791 method_14010() {
		for (int i = field_13868.size() - 1; i >= 0; i--) {
			class_2806 lv = (class_2806)field_13868.get(i);
			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = this.method_16146(lv);
			if (!completableFuture.isCompletedExceptionally()) {
				Optional<class_2791> optional = ((Either)completableFuture.getNow(field_16426)).left();
				if (optional.isPresent()) {
					return (class_2791)optional.get();
				}
			}
		}

		return null;
	}

	public CompletableFuture<class_2791> method_14000() {
		return this.field_16428;
	}

	public void method_14002(int i, int j, int k) {
		class_2818 lv = this.method_16144();
		if (lv != null) {
			this.field_13872 |= 1 << (j >> 4);
			if (this.field_13874 < 64) {
				short s = (short)(i << 12 | k << 8 | j);

				for (int l = 0; l < this.field_13874; l++) {
					if (this.field_13861[l] == s) {
						return;
					}
				}

				this.field_13861[this.field_13874++] = s;
			}
		}
	}

	public void method_14012(class_1944 arg, int i) {
		class_2818 lv = this.method_16144();
		if (lv != null) {
			lv.method_12008(true);
			if (arg == class_1944.field_9284) {
				this.field_13870 |= 1 << i - -1;
			} else {
				this.field_13871 |= 1 << i - -1;
			}
		}
	}

	public void method_14006(class_2818 arg) {
		if (this.field_13874 != 0 || this.field_13870 != 0 || this.field_13871 != 0) {
			class_1937 lv = arg.method_12200();
			if (this.field_13874 == 64) {
				this.field_16209 = -1;
			}

			if (this.field_13870 != 0 || this.field_13871 != 0) {
				this.method_13992(new class_2676(arg.method_12004(), this.field_13863, this.field_13870 & ~this.field_16209, this.field_13871 & ~this.field_16209), true);
				int i = this.field_13870 & this.field_16209;
				int j = this.field_13871 & this.field_16209;
				if (i != 0 || j != 0) {
					this.method_13992(new class_2676(arg.method_12004(), this.field_13863, i, j), false);
				}

				this.field_13870 = 0;
				this.field_13871 = 0;
				this.field_16209 = this.field_16209 & ~(this.field_13870 & this.field_13871);
			}

			if (this.field_13874 == 1) {
				int i = (this.field_13861[0] >> 12 & 15) + this.field_13864.field_9181 * 16;
				int j = this.field_13861[0] & 255;
				int k = (this.field_13861[0] >> 8 & 15) + this.field_13864.field_9180 * 16;
				class_2338 lv2 = new class_2338(i, j, k);
				this.method_13992(new class_2626(lv, lv2), false);
				if (lv.method_8320(lv2).method_11614().method_9570()) {
					this.method_14009(lv, lv2);
				}
			} else if (this.field_13874 == 64) {
				this.method_13992(new class_2672(arg, this.field_13872), false);
			} else if (this.field_13874 != 0) {
				this.method_13992(new class_2637(this.field_13874, this.field_13861, arg), false);

				for (int i = 0; i < this.field_13874; i++) {
					int j = (this.field_13861[i] >> 12 & 15) + this.field_13864.field_9181 * 16;
					int k = this.field_13861[i] & 255;
					int l = (this.field_13861[i] >> 8 & 15) + this.field_13864.field_9180 * 16;
					class_2338 lv3 = new class_2338(j, k, l);
					if (lv.method_8320(lv3).method_11614().method_9570()) {
						this.method_14009(lv, lv3);
					}
				}
			}

			this.field_13874 = 0;
			this.field_13872 = 0;
		}
	}

	private void method_14009(class_1937 arg, class_2338 arg2) {
		class_2586 lv = arg.method_8321(arg2);
		if (lv != null) {
			class_2622 lv2 = lv.method_16886();
			if (lv2 != null) {
				this.method_13992(lv2, false);
			}
		}
	}

	private void method_13992(class_2596<?> arg, boolean bl) {
		this.field_17210.method_17210(this.field_13864, bl).forEach(arg2 -> arg2.field_13987.method_14364(arg));
	}

	public CompletableFuture<Either<class_2791, class_3193.class_3724>> method_13993(class_2806 arg, class_3898 arg2) {
		int i = arg.method_16559();
		CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = (CompletableFuture<Either<class_2791, class_3193.class_3724>>)this.field_16425
			.get(i);
		if (completableFuture != null) {
			Either<class_2791, class_3193.class_3724> either = (Either<class_2791, class_3193.class_3724>)completableFuture.getNow(null);
			if (either == null || either.left().isPresent()) {
				return completableFuture;
			}
		}

		if (method_14011(this.field_13862).method_12165(arg)) {
			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture2 = arg2.method_17236(this, arg);
			this.method_16143(completableFuture2);
			this.field_16425.set(i, completableFuture2);
			return completableFuture2;
		} else {
			return completableFuture == null ? field_16430 : completableFuture;
		}
	}

	private void method_16143(CompletableFuture<? extends Either<? extends class_2791, class_3193.class_3724>> completableFuture) {
		this.field_16428 = this.field_16428.thenCombine(completableFuture, (arg, either) -> either.map(argx -> argx, arg2 -> arg));
	}

	@Environment(EnvType.CLIENT)
	public class_3193.class_3194 method_13995() {
		return method_14008(this.field_13862);
	}

	public class_1923 method_13994() {
		return this.field_13864;
	}

	public int method_14005() {
		return this.field_13862;
	}

	public int method_17208() {
		return this.field_17208;
	}

	private void method_17207(int i) {
		this.field_17208 = i;
	}

	public void method_15890(int i) {
		this.field_13862 = i;
	}

	protected void method_14007(class_3898 arg) {
		class_2806 lv = method_14011(this.field_16432);
		class_2806 lv2 = method_14011(this.field_13862);
		boolean bl = this.field_16432 <= class_3898.field_18239;
		boolean bl2 = this.field_13862 <= class_3898.field_18239;
		class_3193.class_3194 lv3 = method_14008(this.field_16432);
		class_3193.class_3194 lv4 = method_14008(this.field_13862);
		if (bl) {
			Either<class_2791, class_3193.class_3724> either = Either.right(new class_3193.class_3724() {
				public String toString() {
					return "Unloaded ticket level " + class_3193.this.field_13864.toString();
				}
			});

			for (int i = bl2 ? lv2.method_16559() + 1 : 0; i <= lv.method_16559(); i++) {
				CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = (CompletableFuture<Either<class_2791, class_3193.class_3724>>)this.field_16425
					.get(i);
				if (completableFuture != null) {
					completableFuture.complete(either);
				} else {
					this.field_16425.set(i, CompletableFuture.completedFuture(either));
				}
			}
		}

		boolean bl3 = lv3.method_14014(class_3193.class_3194.field_13876);
		boolean bl4 = lv4.method_14014(class_3193.class_3194.field_13876);
		this.field_19238 |= bl4;
		if (!bl3 && bl4) {
			this.field_16431 = arg.method_20580(this);
			this.method_16143(this.field_16431);
		}

		if (bl3 && !bl4) {
			CompletableFuture<Either<class_2818, class_3193.class_3724>> completableFuture = this.field_16431;
			this.field_16431 = field_16429;
			this.method_16143(completableFuture.thenApply(either -> either.ifLeft(arg::method_20576)));
		}

		boolean bl5 = lv3.method_14014(class_3193.class_3194.field_13875);
		boolean bl6 = lv4.method_14014(class_3193.class_3194.field_13875);
		if (!bl5 && bl6) {
			this.field_19333 = arg.method_17235(this);
			this.method_16143(this.field_19333);
		}

		if (bl5 && !bl6) {
			this.field_19333.complete(field_16427);
			this.field_19333 = field_16429;
		}

		boolean bl7 = lv3.method_14014(class_3193.class_3194.field_13877);
		boolean bl8 = lv4.method_14014(class_3193.class_3194.field_13877);
		if (!bl7 && bl8) {
			if (this.field_13865 != field_16429) {
				throw new IllegalStateException();
			}

			this.field_13865 = arg.method_17247(this.field_13864);
			this.method_16143(this.field_13865);
		}

		if (bl7 && !bl8) {
			this.field_13865.complete(field_16427);
			this.field_13865 = field_16429;
		}

		this.field_17209.method_17209(this.field_13864, this::method_17208, this.field_13862, this::method_17207);
		this.field_16432 = this.field_13862;
	}

	public static class_2806 method_14011(int i) {
		return i < 33 ? class_2806.field_12803 : class_2806.method_12161(i - 33);
	}

	public static class_3193.class_3194 method_14008(int i) {
		return field_13873[class_3532.method_15340(33 - i + 1, 0, field_13873.length - 1)];
	}

	public boolean method_20384() {
		return this.field_19238;
	}

	public void method_20385() {
		this.field_19238 = method_14008(this.field_13862).method_14014(class_3193.class_3194.field_13876);
	}

	public void method_20456(class_2821 arg) {
		for (int i = 0; i < this.field_16425.length(); i++) {
			CompletableFuture<Either<class_2791, class_3193.class_3724>> completableFuture = (CompletableFuture<Either<class_2791, class_3193.class_3724>>)this.field_16425
				.get(i);
			if (completableFuture != null) {
				Optional<class_2791> optional = ((Either)completableFuture.getNow(field_16426)).left();
				if (optional.isPresent() && optional.get() instanceof class_2839) {
					this.field_16425.set(i, CompletableFuture.completedFuture(Either.left(arg)));
				}
			}
		}

		this.method_16143(CompletableFuture.completedFuture(Either.left(arg.method_12240())));
	}

	public static enum class_3194 {
		field_19334,
		field_13876,
		field_13875,
		field_13877;

		public boolean method_14014(class_3193.class_3194 arg) {
			return this.ordinal() >= arg.ordinal();
		}
	}

	public interface class_3724 {
		class_3193.class_3724 field_16433 = new class_3193.class_3724() {
			public String toString() {
				return "UNLOADED";
			}
		};
	}

	public interface class_3896 {
		void method_17209(class_1923 arg, IntSupplier intSupplier, int i, IntConsumer intConsumer);
	}

	public interface class_3897 {
		Stream<class_3222> method_17210(class_1923 arg, boolean bl);
	}
}
