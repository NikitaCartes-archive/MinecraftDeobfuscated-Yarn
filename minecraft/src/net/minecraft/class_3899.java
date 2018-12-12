package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3899 {
	private static final Logger field_17242 = LogManager.getLogger();
	public static final int field_17241 = ServerChunkManager.FULL_CHUNK_LEVEL + 2;
	private final List<Long2ObjectLinkedOpenHashMap<List<Runnable>>> field_17243 = (List<Long2ObjectLinkedOpenHashMap<List<Runnable>>>)IntStream.range(
			0, field_17241
		)
		.mapToObj(i -> new Long2ObjectLinkedOpenHashMap())
		.collect(Collectors.toList());
	private volatile int field_17244 = field_17241;
	private final AtomicInteger field_17245 = new AtomicInteger();
	private final AtomicInteger field_17246 = new AtomicInteger();
	private final String field_17247;

	public class_3899(String string) {
		this.field_17247 = string;
	}

	protected void method_17272(int i, ChunkPos chunkPos, int j) {
		if (i < field_17241) {
			Long2ObjectMap<List<Runnable>> long2ObjectMap = (Long2ObjectMap<List<Runnable>>)this.field_17243.get(i);
			List<Runnable> list = long2ObjectMap.remove(chunkPos.toLong());
			if (i == this.field_17244) {
				while (this.field_17244 < field_17241 && ((Long2ObjectLinkedOpenHashMap)this.field_17243.get(this.field_17244)).isEmpty()) {
					this.field_17244++;
				}
			}

			if (list != null && !list.isEmpty()) {
				((List)((Long2ObjectLinkedOpenHashMap)this.field_17243.get(j)).computeIfAbsent(chunkPos.toLong(), l -> Lists.newArrayList())).addAll(list);
				this.field_17244 = Math.min(this.field_17244, j);
			}
		}
	}

	protected void method_17274(Runnable runnable, ChunkPos chunkPos, int i) {
		((List)((Long2ObjectLinkedOpenHashMap)this.field_17243.get(i)).computeIfAbsent(chunkPos.toLong(), l -> Lists.newArrayList())).add(runnable);
		this.field_17244 = Math.min(this.field_17244, i);
		this.field_17245.getAndIncrement();
	}

	@Nullable
	public Runnable method_17270() {
		if (this.field_17244 >= field_17241) {
			return null;
		} else {
			int i = this.field_17244;
			Long2ObjectLinkedOpenHashMap<List<Runnable>> long2ObjectLinkedOpenHashMap = (Long2ObjectLinkedOpenHashMap<List<Runnable>>)this.field_17243.get(i);
			new ChunkPos(long2ObjectLinkedOpenHashMap.firstLongKey());
			List<Runnable> list = long2ObjectLinkedOpenHashMap.removeFirst();

			while (this.field_17244 < field_17241 && ((Long2ObjectLinkedOpenHashMap)this.field_17243.get(this.field_17244)).isEmpty()) {
				this.field_17244++;
			}

			this.field_17246.addAndGet(list.size());
			return () -> list.forEach(Runnable::run);
		}
	}

	protected void method_17276() {
		this.field_17245.set(0);
		this.field_17246.set(0);
	}

	protected int method_17278() {
		return this.field_17245.get();
	}

	protected int method_17279() {
		return this.field_17246.get();
	}

	public String toString() {
		return this.field_17247 + " " + this.field_17244 + "...";
	}
}
