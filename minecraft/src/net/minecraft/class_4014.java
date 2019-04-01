package net.minecraft;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_4014<S> implements class_4011 {
	protected final class_3300 field_17927;
	protected final CompletableFuture<class_3902> field_18042 = new CompletableFuture();
	protected final CompletableFuture<List<S>> field_18043;
	private final Set<class_3302> field_18044;
	private final int field_18045;
	private int field_18046;
	private int field_18047;
	private final AtomicInteger field_18048 = new AtomicInteger();
	private final AtomicInteger field_18049 = new AtomicInteger();

	public static class_4014<Void> method_18369(
		class_3300 arg, List<class_3302> list, Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture
	) {
		return new class_4014<>(
			executor,
			executor2,
			arg,
			list,
			(argx, arg2, arg3, executor2x, executor3) -> arg3.reload(argx, arg2, class_3694.field_16280, class_3694.field_16280, executor, executor3),
			completableFuture
		);
	}

	protected class_4014(
		Executor executor, Executor executor2, class_3300 arg, List<class_3302> list, class_4014.class_4047<S> arg2, CompletableFuture<class_3902> completableFuture
	) {
		this.field_17927 = arg;
		this.field_18045 = list.size();
		this.field_18048.incrementAndGet();
		completableFuture.thenRun(this.field_18049::incrementAndGet);
		List<CompletableFuture<S>> list2 = new ArrayList();
		CompletableFuture<?> completableFuture2 = completableFuture;
		this.field_18044 = Sets.<class_3302>newHashSet(list);

		for (final class_3302 lv : list) {
			final CompletableFuture<?> completableFuture3 = completableFuture2;
			CompletableFuture<S> completableFuture4 = arg2.create(new class_3302.class_4045() {
				@Override
				public <T> CompletableFuture<T> method_18352(T object) {
					executor2.execute(() -> {
						class_4014.this.field_18044.remove(lv);
						if (class_4014.this.field_18044.isEmpty()) {
							class_4014.this.field_18042.complete(class_3902.field_17274);
						}
					});
					return class_4014.this.field_18042.thenCombine(completableFuture3, (arg, object2) -> object);
				}
			}, arg, lv, runnable -> {
				this.field_18048.incrementAndGet();
				executor.execute(() -> {
					runnable.run();
					this.field_18049.incrementAndGet();
				});
			}, runnable -> {
				this.field_18046++;
				executor2.execute(() -> {
					runnable.run();
					this.field_18047++;
				});
			});
			list2.add(completableFuture4);
			completableFuture2 = completableFuture4;
		}

		this.field_18043 = class_156.method_652(list2);
	}

	@Override
	public CompletableFuture<class_3902> method_18364() {
		return this.field_18043.thenApply(list -> class_3902.field_17274);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_18229() {
		int i = this.field_18045 - this.field_18044.size();
		float f = (float)(this.field_18049.get() * 2 + this.field_18047 * 2 + i * 1);
		float g = (float)(this.field_18048.get() * 2 + this.field_18046 * 2 + this.field_18045 * 1);
		return f / g;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18786() {
		return this.field_18042.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18787() {
		return this.field_18043.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_18849() {
		if (this.field_18043.isCompletedExceptionally()) {
			this.field_18043.join();
		}
	}

	public interface class_4047<S> {
		CompletableFuture<S> create(class_3302.class_4045 arg, class_3300 arg2, class_3302 arg3, Executor executor, Executor executor2);
	}
}
