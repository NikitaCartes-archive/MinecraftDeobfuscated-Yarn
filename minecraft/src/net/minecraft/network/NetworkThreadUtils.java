package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ThreadTaskQueue;

public class NetworkThreadUtils {
	public static <T extends PacketListener> void method_11073(Packet<T> packet, T packetListener, ServerWorld serverWorld) throws OffThreadException {
		forceMainThread(packet, packetListener, serverWorld.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ThreadTaskQueue<?> threadTaskQueue) throws OffThreadException {
		if (!threadTaskQueue.method_18854()) {
			threadTaskQueue.execute(() -> packet.apply(packetListener));
			throw OffThreadException.INSTANCE;
		}
	}
}
