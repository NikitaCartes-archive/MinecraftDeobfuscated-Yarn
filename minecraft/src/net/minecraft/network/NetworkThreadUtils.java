package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkThreadUtils {
	private static final Logger field_20318 = LogManager.getLogger();

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ServerWorld serverWorld) throws OffThreadException {
		forceMainThread(packet, packetListener, serverWorld.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T packetListener, ThreadExecutor<?> threadExecutor) throws OffThreadException {
		if (!threadExecutor.isOnThread()) {
			threadExecutor.execute(() -> {
				if (packetListener.getConnection().isOpen()) {
					packet.apply(packetListener);
				} else {
					field_20318.debug("Ignoring packet due to disconnection: " + packet);
				}
			});
			throw OffThreadException.INSTANCE;
		}
	}
}
