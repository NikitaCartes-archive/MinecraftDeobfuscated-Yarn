package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;

public interface ServerLoginPacketListener extends ServerCrashSafePacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.LOGIN;
	}

	void onHello(LoginHelloC2SPacket packet);

	void onKey(LoginKeyC2SPacket packet);

	void onQueryResponse(LoginQueryResponseC2SPacket packet);

	void onEnterConfiguration(EnterConfigurationC2SPacket packet);
}
