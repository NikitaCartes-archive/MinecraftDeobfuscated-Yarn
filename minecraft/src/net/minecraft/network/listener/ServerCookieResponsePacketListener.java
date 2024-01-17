package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;

public interface ServerCookieResponsePacketListener extends ServerCrashSafePacketListener {
	void onCookieResponse(CookieResponseC2SPacket packet);
}
