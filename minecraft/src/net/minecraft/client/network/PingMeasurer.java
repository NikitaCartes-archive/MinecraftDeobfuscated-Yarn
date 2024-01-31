package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9191;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class PingMeasurer {
	private final ClientPlayNetworkHandler handler;
	private final class_9191 log;

	public PingMeasurer(ClientPlayNetworkHandler handler, class_9191 log) {
		this.handler = handler;
		this.log = log;
	}

	public void ping() {
		this.handler.sendPacket(new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
	}

	public void onPingResult(PingResultS2CPacket packet) {
		this.log.push(Util.getMeasuringTimeMs() - packet.startTime());
	}
}
