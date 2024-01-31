package net.minecraft;

import java.util.EnumMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.DebugSampleSubscriptionC2SPacket;
import net.minecraft.util.DebugSampleType;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class class_9211 {
	public static final int TIMEOUT = 5000;
	private final ClientPlayNetworkHandler networkHandler;
	private final DebugHud debugHud;
	private final EnumMap<DebugSampleType, Long> lastTime;

	public class_9211(ClientPlayNetworkHandler handler, DebugHud hud) {
		this.debugHud = hud;
		this.networkHandler = handler;
		this.lastTime = new EnumMap(DebugSampleType.class);
	}

	public void method_56830() {
		if (this.debugHud.shouldRenderTickCharts()) {
			this.update(DebugSampleType.TICK_TIME);
		}
	}

	private void update(DebugSampleType type) {
		long l = Util.getMeasuringTimeMs();
		if (l > (Long)this.lastTime.getOrDefault(type, 0L) + 5000L) {
			this.networkHandler.sendPacket(new DebugSampleSubscriptionC2SPacket(type));
			this.lastTime.put(type, l);
		}
	}
}
