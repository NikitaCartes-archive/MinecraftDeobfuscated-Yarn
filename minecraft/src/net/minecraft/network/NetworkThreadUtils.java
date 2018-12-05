package net.minecraft.network;

import net.minecraft.class_2987;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ThreadTaskQueue;

public class NetworkThreadUtils {
	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ServerWorld serverWorld) throws class_2987 {
		forceMainThread(packet, packetListener, serverWorld.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ThreadTaskQueue<?> threadTaskQueue) throws class_2987 {
		if (!threadTaskQueue.isMainThread()) {
			threadTaskQueue.execute(() -> packet.apply(packetListener));
			throw class_2987.field_13400;
		}
	}
}
