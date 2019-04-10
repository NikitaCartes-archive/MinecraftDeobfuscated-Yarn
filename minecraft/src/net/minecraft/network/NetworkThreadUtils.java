package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ThreadExecutor;

public class NetworkThreadUtils {
	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ServerWorld serverWorld) throws OffThreadException {
		forceMainThread(packet, packetListener, serverWorld.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ThreadExecutor<?> threadExecutor) throws OffThreadException {
		if (!threadExecutor.isOnThread()) {
			threadExecutor.execute(() -> packet.apply(packetListener));
			throw OffThreadException.INSTANCE;
		}
	}
}
