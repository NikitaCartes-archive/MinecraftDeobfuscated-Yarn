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
	Logger field_51479 = LogUtils.getLogger();

	@Override
	default void method_59807(Packet packet, Exception exception) throws CrashException {
		field_51479.error("Failed to handle packet {}, suppressing error", packet, exception);
	}
}
