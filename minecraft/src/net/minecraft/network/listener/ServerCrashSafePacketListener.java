package net.minecraft.network.listener;

import com.mojang.logging.LogUtils;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.crash.CrashException;
import org.slf4j.Logger;

/**
 * The interface for serverbound packet listeners.
 * 
 * @implNote Serverbound packet listeners log any uncaught exceptions
 * without crashing.
 */
public interface ServerCrashSafePacketListener extends ServerPacketListener {
	Logger LOGGER = LogUtils.getLogger();

	@Override
	default void onPacketException(Packet packet, Exception exception) throws CrashException {
		LOGGER.error("Failed to handle packet {}, suppressing error", packet, exception);
	}
}
