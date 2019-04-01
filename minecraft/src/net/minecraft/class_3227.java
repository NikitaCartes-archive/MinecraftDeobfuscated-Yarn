package net.minecraft;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3227 extends class_3568 implements AutoCloseable {
	private static final Logger field_14020 = LogManager.getLogger();
	private final class_3846<Runnable> field_17255;
	private final ObjectList<Pair<class_3227.class_3901, Runnable>> field_17256 = new ObjectArrayList<>();
	private final class_3898 field_17257;
	private final class_3906<class_3900.class_3946<Runnable>> field_17259;
	private volatile int field_17260 = 5;
	private final AtomicBoolean field_18812 = new AtomicBoolean();

	public class_3227(class_2823 arg, class_3898 arg2, boolean bl, class_3846<Runnable> arg3, class_3906<class_3900.class_3946<Runnable>> arg4) {
		super(arg, true, bl);
		this.field_17257 = arg2;
		this.field_17259 = arg4;
		this.field_17255 = arg3;
	}

	public void close() {
	}

	@Override
	public int method_15563(int i, boolean bl, boolean bl2) {
		throw new UnsupportedOperationException("Ran authomatically on a different thread!");
	}

	@Override
	public void method_15560(class_2338 arg, int i) {
		throw new UnsupportedOperationException("Ran authomatically on a different thread!");
	}

	@Override
	public void method_15559(class_2338 arg) {
		class_2338 lv = arg.method_10062();
		this.method_17308(
			arg.method_10263() >> 4,
			arg.method_10260() >> 4,
			class_3227.class_3901.field_17262,
			class_156.method_18839(() -> super.method_15559(lv), () -> "checkBlock " + arg)
		);
	}

	@Override
	public void method_15551(class_4076 arg, boolean bl) {
		this.method_17307(
			arg.method_10263(),
			arg.method_10260(),
			() -> 0,
			class_3227.class_3901.field_17261,
			class_156.method_18839(() -> super.method_15551(arg, bl), () -> "updateSectionStatus " + arg + " " + bl)
		);
	}

	@Override
	public void method_15557(class_1923 arg, boolean bl) {
		this.method_17308(
			arg.field_9181,
			arg.field_9180,
			class_3227.class_3901.field_17261,
			class_156.method_18839(() -> super.method_15557(arg, bl), () -> "suppresLight " + arg + " " + bl)
		);
	}

	@Override
	public void method_15558(class_1944 arg, class_4076 arg2, class_2804 arg3) {
		this.method_17308(
			arg2.method_10263(),
			arg2.method_10260(),
			class_3227.class_3901.field_17261,
			class_156.method_18839(() -> super.method_15558(arg, arg2, arg3), () -> "queueData " + arg2)
		);
	}

	private void method_17308(int i, int j, class_3227.class_3901 arg, Runnable runnable) {
		this.method_17307(i, j, this.field_17257.method_17604(class_1923.method_8331(i, j)), arg, runnable);
	}

	private void method_17307(int i, int j, IntSupplier intSupplier, class_3227.class_3901 arg, Runnable runnable) {
		this.field_17259.method_16901(class_3900.method_17626(() -> {
			this.field_17256.add(Pair.of(arg, runnable));
			if (this.field_17256.size() >= this.field_17260) {
				this.method_14277();
			}
		}, class_1923.method_8331(i, j), intSupplier));
	}

	public CompletableFuture<class_2791> method_17310(class_2791 arg, boolean bl) {
		class_1923 lv = arg.method_12004();
		this.method_17308(lv.field_9181, lv.field_9180, class_3227.class_3901.field_17261, class_156.method_18839(() -> {
			if (!bl) {
				super.method_15557(lv, false);
			}

			class_2826[] lvs = arg.method_12006();

			for (int i = 0; i < 16; i++) {
				class_2826 lvx = lvs[i];
				if (!class_2826.method_18090(lvx)) {
					super.method_15551(class_4076.method_18681(lv, i), false);
				}
			}

			if (!bl) {
				arg.method_12018().forEach(arg2x -> super.method_15560(arg2x, arg.method_8317(arg2x)));
			}

			arg.method_12020(true);
		}, () -> "lightChunk " + lv + " " + bl));
		return CompletableFuture.supplyAsync(() -> arg, runnable -> this.method_17308(lv.field_9181, lv.field_9180, class_3227.class_3901.field_17262, runnable));
	}

	public void method_17303() {
		if ((!this.field_17256.isEmpty() || super.method_15561()) && this.field_18812.compareAndSet(false, true)) {
			this.field_17255.method_16901(() -> {
				this.method_14277();
				this.field_18812.set(false);
			});
		}
	}

	private void method_14277() {
		int i = Math.min(this.field_17256.size(), this.field_17260);
		ObjectListIterator<Pair<class_3227.class_3901, Runnable>> objectListIterator = this.field_17256.iterator();

		int j;
		for (j = 0; objectListIterator.hasNext() && j < i; j++) {
			Pair<class_3227.class_3901, Runnable> pair = (Pair<class_3227.class_3901, Runnable>)objectListIterator.next();
			if (pair.getFirst() == class_3227.class_3901.field_17261) {
				pair.getSecond().run();
			}
		}

		objectListIterator.back(j);
		super.method_15563(Integer.MAX_VALUE, true, true);

		for (int var5 = 0; objectListIterator.hasNext() && var5 < i; var5++) {
			Pair<class_3227.class_3901, Runnable> pair = (Pair<class_3227.class_3901, Runnable>)objectListIterator.next();
			if (pair.getFirst() == class_3227.class_3901.field_17262) {
				pair.getSecond().run();
			}

			objectListIterator.remove();
		}
	}

	public void method_17304(int i) {
		this.field_17260 = i;
	}

	static enum class_3901 {
		field_17261,
		field_17262;
	}
}
