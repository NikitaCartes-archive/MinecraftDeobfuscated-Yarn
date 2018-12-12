package net.minecraft;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import net.minecraft.server.world.ServerChunkManagerEntry;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3900 implements AutoCloseable, ServerChunkManagerEntry.class_3896 {
	private static final Logger field_17248 = LogManager.getLogger();
	private final Map<class_3846<Runnable>, class_3899> field_17249;
	private final Set<class_3846<Runnable>> field_17250;
	private final class_3846<Pair<Integer, Runnable>> field_17251;

	public class_3900(List<class_3846<Runnable>> list, Executor executor) {
		this.field_17249 = (Map<class_3846<Runnable>, class_3899>)list.stream()
			.collect(Collectors.toMap(Function.identity(), arg -> new class_3899(arg.method_16898() + "_queue")));
		this.field_17250 = Sets.<class_3846<Runnable>>newHashSet(list);
		this.field_17251 = new class_3846<>(new class_3847.class_3848<>(3), executor, "sorter");
	}

	@Override
	public void method_17209(ChunkPos chunkPos, IntSupplier intSupplier, int i, IntConsumer intConsumer) {
		this.field_17251.method_16901(Pair.of(0, () -> {
			int j = intSupplier.getAsInt();
			this.field_17249.values().forEach(arg -> arg.method_17272(j, chunkPos, i));
			intConsumer.accept(i);
		}));
	}

	public void method_17284(class_3846<Runnable> arg, ServerChunkManagerEntry serverChunkManagerEntry, Runnable runnable) {
		this.method_17282(arg, runnable, serverChunkManagerEntry.getPos(), serverChunkManagerEntry::method_17208);
	}

	public void method_17282(class_3846<Runnable> arg, Runnable runnable, ChunkPos chunkPos, IntSupplier intSupplier) {
		this.field_17251.method_16901(Pair.of(1, () -> {
			class_3899 lv = this.method_17281(arg);
			int i = intSupplier.getAsInt();
			lv.method_17274(runnable, chunkPos, i);
			if (this.field_17250.remove(arg)) {
				this.method_17286(lv, arg);
			}
		}));
	}

	private void method_17286(class_3899 arg, class_3846<Runnable> arg2) {
		this.field_17251.method_16901(Pair.of(2, () -> {
			Runnable runnable = arg.method_17270();
			if (runnable == null) {
				this.field_17250.add(arg2);
			} else {
				arg2.method_16901(runnable);
				arg2.method_16901(() -> this.method_17286(arg, arg2));
			}
		}));
	}

	protected class_3899 method_17281(class_3846<Runnable> arg) {
		class_3899 lv = (class_3899)this.field_17249.get(arg);
		if (lv == null) {
			throw new IllegalArgumentException("No queue for: " + arg);
		} else {
			return lv;
		}
	}

	public void close() {
		this.field_17249.keySet().forEach(class_3846::close);
	}
}
