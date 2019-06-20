package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_846 {
	private static final Logger field_4445 = LogManager.getLogger();
	private static final ThreadFactory field_4436 = new ThreadFactoryBuilder()
		.setNameFormat("Chunk Batcher %d")
		.setDaemon(true)
		.setUncaughtExceptionHandler(new class_140(field_4445))
		.build();
	private final int field_4442;
	private final List<Thread> field_4440 = Lists.<Thread>newArrayList();
	private final List<class_845> field_4444 = Lists.<class_845>newArrayList();
	private final PriorityBlockingQueue<class_842> field_4435 = Queues.newPriorityBlockingQueue();
	private final BlockingQueue<class_750> field_4438;
	private final class_286 field_4437 = new class_286();
	private final class_294 field_4441 = new class_294();
	private final Queue<class_846.class_847> field_4443 = Queues.<class_846.class_847>newPriorityQueue();
	private final class_845 field_4439;
	private class_243 field_18766 = class_243.field_1353;

	public class_846(boolean bl) {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 10485760 - 1);
		int j = Runtime.getRuntime().availableProcessors();
		int k = bl ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k * 2, i));
		this.field_4439 = new class_845(this, new class_750());
		List<class_750> list = Lists.<class_750>newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new class_750());
			}
		} catch (OutOfMemoryError var11) {
			field_4445.warn("Allocated only {}/{} buffers", list.size(), l);
			int n = list.size() * 2 / 3;

			for (int o = 0; o < n; o++) {
				list.remove(list.size() - 1);
			}

			System.gc();
		}

		this.field_4442 = list.size();
		this.field_4438 = Queues.<class_750>newArrayBlockingQueue(this.field_4442);
		this.field_4438.addAll(list);
		int m = Math.min(k, this.field_4442);
		if (m > 1) {
			for (int n = 0; n < m; n++) {
				class_845 lv = new class_845(this);
				Thread thread = field_4436.newThread(lv);
				thread.start();
				this.field_4444.add(lv);
				this.field_4440.add(thread);
			}
		}
	}

	public String method_3622() {
		return this.field_4440.isEmpty()
			? String.format("pC: %03d, single-threaded", this.field_4435.size())
			: String.format("pC: %03d, pU: %02d, aB: %02d", this.field_4435.size(), this.field_4443.size(), this.field_4438.size());
	}

	public void method_19419(class_243 arg) {
		this.field_18766 = arg;
	}

	public class_243 method_19420() {
		return this.field_18766;
	}

	public boolean method_3631(long l) {
		boolean bl = false;

		boolean bl2;
		do {
			bl2 = false;
			if (this.field_4440.isEmpty()) {
				class_842 lv = (class_842)this.field_4435.poll();
				if (lv != null) {
					try {
						this.field_4439.method_3615(lv);
						bl2 = true;
					} catch (InterruptedException var9) {
						field_4445.warn("Skipped task due to interrupt");
					}
				}
			}

			int i = 0;
			synchronized (this.field_4443) {
				while (i < 10) {
					class_846.class_847 lv2 = (class_846.class_847)this.field_4443.poll();
					if (lv2 == null) {
						break;
					}

					if (!lv2.field_4446.isDone()) {
						lv2.field_4446.run();
						bl2 = true;
						bl = true;
						i++;
					}
				}
			}
		} while (l != 0L && bl2 && l >= class_156.method_648());

		return bl;
	}

	public boolean method_3624(class_851 arg) {
		arg.method_3667().lock();

		boolean var4;
		try {
			class_842 lv = arg.method_3674();
			lv.method_3597(() -> this.field_4435.remove(lv));
			boolean bl = this.field_4435.offer(lv);
			if (!bl) {
				lv.method_3596();
			}

			var4 = bl;
		} finally {
			arg.method_3667().unlock();
		}

		return var4;
	}

	public boolean method_3627(class_851 arg) {
		arg.method_3667().lock();

		boolean var3;
		try {
			class_842 lv = arg.method_3674();

			try {
				this.field_4439.method_3615(lv);
			} catch (InterruptedException var7) {
			}

			var3 = true;
		} finally {
			arg.method_3667().unlock();
		}

		return var3;
	}

	public void method_3632() {
		this.method_3633();
		List<class_750> list = Lists.<class_750>newArrayList();

		while (list.size() != this.field_4442) {
			this.method_3631(Long.MAX_VALUE);

			try {
				list.add(this.method_3626());
			} catch (InterruptedException var3) {
			}
		}

		this.field_4438.addAll(list);
	}

	public void method_3625(class_750 arg) {
		this.field_4438.add(arg);
	}

	public class_750 method_3626() throws InterruptedException {
		return (class_750)this.field_4438.take();
	}

	public class_842 method_3629() throws InterruptedException {
		return (class_842)this.field_4435.take();
	}

	public boolean method_3620(class_851 arg) {
		arg.method_3667().lock();

		boolean var3;
		try {
			class_842 lv = arg.method_3669();
			if (lv == null) {
				return true;
			}

			lv.method_3597(() -> this.field_4435.remove(lv));
			var3 = this.field_4435.offer(lv);
		} finally {
			arg.method_3667().unlock();
		}

		return var3;
	}

	public ListenableFuture<Void> method_3635(class_1921 arg, class_287 arg2, class_851 arg3, class_849 arg4, double d) {
		if (class_310.method_1551().method_18854()) {
			if (GLX.useVbo()) {
				this.method_3621(arg2, arg3.method_3656(arg.ordinal()));
			} else {
				this.method_3623(arg2, ((class_848)arg3).method_3639(arg, arg4));
			}

			arg2.method_1331(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Void> listenableFutureTask = ListenableFutureTask.create(() -> this.method_3635(arg, arg2, arg3, arg4, d), null);
			synchronized (this.field_4443) {
				this.field_4443.add(new class_846.class_847(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void method_3623(class_287 arg, int i) {
		GlStateManager.newList(i, 4864);
		this.field_4437.method_1309(arg);
		GlStateManager.endList();
	}

	private void method_3621(class_287 arg, class_291 arg2) {
		this.field_4441.method_1372(arg2);
		this.field_4441.method_1309(arg);
	}

	public void method_3633() {
		while (!this.field_4435.isEmpty()) {
			class_842 lv = (class_842)this.field_4435.poll();
			if (lv != null) {
				lv.method_3596();
			}
		}
	}

	public boolean method_3630() {
		return this.field_4435.isEmpty() && this.field_4443.isEmpty();
	}

	public void method_3619() {
		this.method_3633();

		for (class_845 lv : this.field_4444) {
			lv.method_3611();
		}

		for (Thread thread : this.field_4440) {
			try {
				thread.interrupt();
				thread.join();
			} catch (InterruptedException var4) {
				field_4445.warn("Interrupted whilst waiting for worker to die", (Throwable)var4);
			}
		}

		this.field_4438.clear();
	}

	@Environment(EnvType.CLIENT)
	class class_847 implements Comparable<class_846.class_847> {
		private final ListenableFutureTask<Void> field_4446;
		private final double field_4447;

		public class_847(ListenableFutureTask<Void> listenableFutureTask, double d) {
			this.field_4446 = listenableFutureTask;
			this.field_4447 = d;
		}

		public int method_3638(class_846.class_847 arg) {
			return Doubles.compare(this.field_4447, arg.field_4447);
		}
	}
}
