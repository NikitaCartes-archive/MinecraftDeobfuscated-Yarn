package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class class_7581 {
	static final Logger field_39696 = LogUtils.getLogger();
	final Executor field_39697;
	final TimeUnit field_39698;
	final class_7576 field_39699;

	public class_7581(Executor executor, TimeUnit timeUnit, class_7576 arg) {
		this.field_39697 = executor;
		this.field_39698 = timeUnit;
		this.field_39699 = arg;
	}

	public <T> class_7581.class_7586<T> method_44629(String string, Callable<T> callable, Duration duration, class_7587 arg) {
		long l = this.field_39698.convert(duration);
		if (l == 0L) {
			throw new IllegalArgumentException("Period of " + duration + " too short for selected resolution of " + this.field_39698);
		} else {
			return new class_7581.class_7586<>(string, callable, l, arg);
		}
	}

	public class_7581.class_7584 method_44628() {
		return new class_7581.class_7584();
	}

	@Environment(EnvType.CLIENT)
	static record class_7582<T>(Either<T, Exception> value, long time) {
	}

	@Environment(EnvType.CLIENT)
	class class_7583<T> {
		private final class_7581.class_7586<T> field_39701;
		private final Consumer<T> field_39702;
		private long field_39703 = -1L;

		class_7583(class_7581.class_7586<T> arg2, Consumer<T> consumer) {
			this.field_39701 = arg2;
			this.field_39702 = consumer;
		}

		void method_44631(long l) {
			this.field_39701.method_44639(l);
			this.method_44630();
		}

		void method_44630() {
			class_7581.class_7585<T> lv = this.field_39701.field_39712;
			if (lv != null && this.field_39703 < lv.time) {
				this.field_39702.accept(lv.value);
				this.field_39703 = lv.time;
			}
		}

		void method_44632() {
			class_7581.class_7585<T> lv = this.field_39701.field_39712;
			if (lv != null) {
				this.field_39702.accept(lv.value);
				this.field_39703 = lv.time;
			}
		}

		void method_44633() {
			this.field_39701.method_44638();
			this.field_39703 = -1L;
		}
	}

	@Environment(EnvType.CLIENT)
	public class class_7584 {
		private final List<class_7581.class_7583<?>> field_39705 = new ArrayList();

		public <T> void method_44635(class_7581.class_7586<T> arg, Consumer<T> consumer) {
			class_7581.class_7583<T> lv = class_7581.this.new class_7583<>(arg, consumer);
			this.field_39705.add(lv);
			lv.method_44630();
		}

		public void method_44634() {
			for (class_7581.class_7583<?> lv : this.field_39705) {
				lv.method_44632();
			}
		}

		public void method_44636() {
			for (class_7581.class_7583<?> lv : this.field_39705) {
				lv.method_44631(class_7581.this.field_39699.get(class_7581.this.field_39698));
			}
		}

		public void method_44637() {
			for (class_7581.class_7583<?> lv : this.field_39705) {
				lv.method_44633();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static record class_7585<T>(T value, long time) {
	}

	@Environment(EnvType.CLIENT)
	public class class_7586<T> {
		private final String field_39707;
		private final Callable<T> field_39708;
		private final long field_39709;
		private final class_7587 field_39710;
		@Nullable
		private CompletableFuture<class_7581.class_7582<T>> field_39711;
		@Nullable
		class_7581.class_7585<T> field_39712;
		private long field_39713 = -1L;

		class_7586(String string, Callable<T> callable, long l, class_7587 arg2) {
			this.field_39707 = string;
			this.field_39708 = callable;
			this.field_39709 = l;
			this.field_39710 = arg2;
		}

		void method_44639(long l) {
			if (this.field_39711 != null) {
				class_7581.class_7582<T> lv = (class_7581.class_7582<T>)this.field_39711.getNow(null);
				if (lv == null) {
					return;
				}

				this.field_39711 = null;
				long m = lv.time;
				lv.value().ifLeft(object -> {
					this.field_39712 = new class_7581.class_7585<>((T)object, m);
					this.field_39713 = m + this.field_39709 * this.field_39710.method_44643();
				}).ifRight(exception -> {
					long mx = this.field_39710.method_44645();
					class_7581.field_39696.warn("Failed to process task {}, will repeat after {} cycles", this.field_39707, mx, exception);
					this.field_39713 = m + this.field_39709 * mx;
				});
			}

			if (this.field_39713 <= l) {
				this.field_39711 = CompletableFuture.supplyAsync(() -> {
					try {
						T object = (T)this.field_39708.call();
						long lx = class_7581.this.field_39699.get(class_7581.this.field_39698);
						return new class_7581.class_7582<>(Either.left(object), lx);
					} catch (Exception var4x) {
						long lx = class_7581.this.field_39699.get(class_7581.this.field_39698);
						return new class_7581.class_7582(Either.right(var4x), lx);
					}
				}, class_7581.this.field_39697);
			}
		}

		void method_44638() {
			this.field_39711 = null;
			this.field_39712 = null;
			this.field_39713 = -1L;
		}
	}
}
