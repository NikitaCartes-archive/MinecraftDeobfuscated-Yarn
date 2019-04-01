package net.minecraft;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4010 extends class_4014<class_4010.class_4046> {
	private static final Logger field_17918 = LogManager.getLogger();
	private final Stopwatch field_17919 = Stopwatch.createUnstarted();

	public class_4010(class_3300 arg, List<class_3302> list, Executor executor, Executor executor2, CompletableFuture<class_3902> completableFuture) {
		super(
			executor,
			executor2,
			arg,
			list,
			(argx, arg2, arg3, executor2x, executor3) -> {
				AtomicLong atomicLong = new AtomicLong();
				AtomicLong atomicLong2 = new AtomicLong();
				class_3533 lv = new class_3533(class_156.method_648(), () -> 0);
				class_3533 lv2 = new class_3533(class_156.method_648(), () -> 0);
				CompletableFuture<Void> completableFuturex = arg3.reload(argx, arg2, lv, lv2, runnable -> executor2x.execute(() -> {
						long l = class_156.method_648();
						runnable.run();
						atomicLong.addAndGet(class_156.method_648() - l);
					}), runnable -> executor3.execute(() -> {
						long l = class_156.method_648();
						runnable.run();
						atomicLong2.addAndGet(class_156.method_648() - l);
					}));
				return completableFuturex.thenApplyAsync(
					void_ -> new class_4010.class_4046(arg3.getClass().getSimpleName(), lv.method_16064(), lv2.method_16064(), atomicLong, atomicLong2), executor2
				);
			},
			completableFuture
		);
		this.field_17919.start();
		this.field_18043.thenAcceptAsync(this::method_18238, executor2);
	}

	private void method_18238(List<class_4010.class_4046> list) {
		this.field_17919.stop();
		int i = 0;
		field_17918.info("Resource reload finished after " + this.field_17919.elapsed(TimeUnit.MILLISECONDS) + " ms");

		for (class_4010.class_4046 lv : list) {
			class_3696 lv2 = lv.field_18038;
			class_3696 lv3 = lv.field_18039;
			int j = (int)((double)lv.field_18040.get() / 1000000.0);
			int k = (int)((double)lv.field_18041.get() / 1000000.0);
			int l = j + k;
			String string = lv.field_18037;
			field_17918.info(string + " took approximately " + l + " ms (" + j + " ms preparing, " + k + " ms applying)");
			String string2 = lv2.method_18052();
			if (string2.length() > 0) {
				field_17918.debug(string + " preparations:\n" + string2);
			}

			String string3 = lv3.method_18052();
			if (string3.length() > 0) {
				field_17918.debug(string + " reload:\n" + string3);
			}

			field_17918.info("----------");
			i += k;
		}

		field_17918.info("Total blocking time: " + i + " ms");
	}

	public static class class_4046 {
		private final String field_18037;
		private final class_3696 field_18038;
		private final class_3696 field_18039;
		private final AtomicLong field_18040;
		private final AtomicLong field_18041;

		private class_4046(String string, class_3696 arg, class_3696 arg2, AtomicLong atomicLong, AtomicLong atomicLong2) {
			this.field_18037 = string;
			this.field_18038 = arg;
			this.field_18039 = arg2;
			this.field_18040 = atomicLong;
			this.field_18041 = atomicLong2;
		}
	}
}
