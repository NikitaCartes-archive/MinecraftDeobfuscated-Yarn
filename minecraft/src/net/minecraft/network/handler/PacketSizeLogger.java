package net.minecraft.network.handler;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_9191;

public class PacketSizeLogger {
	private final AtomicInteger packetSizeInBytes = new AtomicInteger();
	private final class_9191 log;

	public PacketSizeLogger(class_9191 log) {
		this.log = log;
	}

	public void increment(int bytes) {
		this.packetSizeInBytes.getAndAdd(bytes);
	}

	public void push() {
		this.log.push((long)this.packetSizeInBytes.getAndSet(0));
	}
}
