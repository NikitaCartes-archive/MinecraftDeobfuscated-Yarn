package net.minecraft.server.world;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * A listener to entity tracking within threaded anvil chunk storage.
 */
public interface EntityTrackingListener {
	ServerPlayerEntity getPlayer();

	void sendPacket(Packet<?> packet);
}
