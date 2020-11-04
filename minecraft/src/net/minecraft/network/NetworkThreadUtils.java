package net.minecraft.network;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.thread.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkThreadUtils {
	private static final Logger LOGGER = LogManager.getLogger();

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ServerWorld world) throws OffThreadException {
		forceMainThread(packet, listener, world.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ThreadExecutor<?> engine) throws OffThreadException {
		if (!engine.isOnThread()) {
			engine.execute(() -> {
				if (listener.getConnection().isOpen()) {
					packet.apply(listener);
				} else {
					LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
				}
			});
			throw OffThreadException.INSTANCE;
		}
	}
}
