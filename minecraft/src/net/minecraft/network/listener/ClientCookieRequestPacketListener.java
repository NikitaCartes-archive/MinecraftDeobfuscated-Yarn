package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;

public interface ClientCookieRequestPacketListener extends ClientPacketListener {
	void onCookieRequest(CookieRequestS2CPacket packet);
}
