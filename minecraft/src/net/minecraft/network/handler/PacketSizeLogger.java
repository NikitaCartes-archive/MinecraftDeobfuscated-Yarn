package net.minecraft.network.handler;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.profiler.PerformanceLog;

public class PacketSizeLogger {
	private final AtomicInteger packetSizeInBytes = new AtomicInteger();
	private final PerformanceLog log;

	public PacketSizeLogger(PerformanceLog log) {
		this.log = log;
	}

	public void increment(int bytes) {
		this.packetSizeInBytes.getAndAdd(bytes);
	}

	public void push() {
		this.log.push((long)this.packetSizeInBytes.getAndSet(0));
	}
}
