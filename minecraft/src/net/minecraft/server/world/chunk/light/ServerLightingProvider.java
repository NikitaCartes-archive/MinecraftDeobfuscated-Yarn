package net.minecraft.server.world.chunk.light;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.class_2804;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkView;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.chunk.light.LightingView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLightingProvider extends LightingProvider implements LightingView, AutoCloseable {
	private static final Logger field_14020 = LogManager.getLogger();
	private final Queue<Runnable> field_14018 = Queues.<Runnable>newConcurrentLinkedQueue();
	private final Queue<Runnable> field_14016 = Queues.<Runnable>newConcurrentLinkedQueue();
	private final Queue<Runnable> field_14021 = Queues.<Runnable>newConcurrentLinkedQueue();
	private final BlockingQueue<Runnable> field_14014;
	private final ExecutorService field_14015;
	private final Consumer<Collection<Runnable>> field_14017;

	public ServerLightingProvider(ChunkView chunkView, Consumer<Collection<Runnable>> consumer, boolean bl) {
		super(chunkView, bl);
		this.field_14017 = consumer;
		this.field_14014 = new LinkedBlockingQueue();
		this.field_14015 = new ThreadPoolExecutor(
			1,
			1,
			0L,
			TimeUnit.MILLISECONDS,
			this.field_14014,
			new ThreadFactoryBuilder().setNameFormat("Light executor %s").setUncaughtExceptionHandler(new UncaughtExceptionLogger(field_14020)).build()
		);
	}

	public void method_14277() {
		if (!this.field_14021.isEmpty() || !this.field_14016.isEmpty() || !this.field_14018.isEmpty() || super.method_15561()) {
			Queue<Runnable> queue = new ArrayDeque(this.field_14021.size());

			Runnable runnable;
			while ((runnable = (Runnable)this.field_14021.poll()) != null) {
				queue.add(runnable);
			}

			List<Runnable> list = new ArrayList(this.field_14016.size());

			while ((runnable = (Runnable)this.field_14016.poll()) != null) {
				list.add(runnable);
			}

			while ((runnable = (Runnable)this.field_14018.poll()) != null) {
				this.field_14015.execute(runnable);
			}

			this.method_15563(Integer.MAX_VALUE, true, true);
			list.forEach(this.field_14015::execute);
			this.field_14015.execute(() -> this.field_14017.accept(queue));
		}
	}

	@Override
	public boolean method_15561() {
		return !this.field_14014.isEmpty() || !this.field_14018.isEmpty() || !this.field_14016.isEmpty() || !this.field_14021.isEmpty();
	}

	public void method_14272(Runnable runnable) {
		this.field_14021.add(runnable);
	}

	public void close() throws InterruptedException {
		this.field_14015.shutdown();
		if (!this.field_14015.awaitTermination(3L, TimeUnit.SECONDS)) {
			this.field_14015.shutdownNow();
		}
	}

	@Override
	public void queueLightCheck(BlockPos blockPos) {
		this.field_14016.add((Runnable)() -> super.queueLightCheck(blockPos));
	}

	@Override
	public void method_15560(BlockPos blockPos, int i) {
		this.field_14018.add((Runnable)() -> super.method_15560(blockPos, i));
	}

	@Override
	public int method_15563(int i, boolean bl, boolean bl2) {
		this.field_14015.execute(() -> super.method_15563(i, bl, bl2));
		return 0;
	}

	@Override
	public void method_15551(int i, int j, int k, boolean bl) {
		this.field_14018.add((Runnable)() -> super.method_15551(i, j, k, bl));
	}

	@Override
	public void method_15557(int i, int j, boolean bl) {
		this.field_14018.add((Runnable)() -> super.method_15557(i, j, bl));
	}

	@Override
	public void method_15558(LightType lightType, int i, int j, int k, class_2804 arg) {
		this.field_14018.add((Runnable)() -> super.method_15558(lightType, i, j, k, arg));
	}
}
