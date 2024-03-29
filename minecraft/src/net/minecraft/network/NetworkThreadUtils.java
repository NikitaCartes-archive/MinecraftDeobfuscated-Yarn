package net.minecraft.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.thread.ThreadExecutor;
import org.slf4j.Logger;

public class NetworkThreadUtils {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ServerWorld world) throws OffThreadException {
		forceMainThread(packet, listener, world.getServer());
	}

	public static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ThreadExecutor<?> engine) throws OffThreadException {
		if (!engine.isOnThread()) {
			engine.executeSync(() -> {
				if (listener.accepts(packet)) {
					try {
						packet.apply(listener);
					} catch (Exception var6) {
						if (var6 instanceof CrashException crashException && crashException.getCause() instanceof OutOfMemoryError || listener.shouldCrashOnException()) {
							if (var6 instanceof CrashException crashException2) {
								listener.fillCrashReport(crashException2.getReport());
								throw var6;
							}

							CrashReport crashReport = CrashReport.create(var6, "Main thread packet handler");
							listener.fillCrashReport(crashReport);
							throw new CrashException(crashReport);
						}

						LOGGER.error("Failed to handle packet {}, suppressing error", packet, var6);
					}
				} else {
					LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
				}
			});
			throw OffThreadException.INSTANCE;
		}
	}
}
