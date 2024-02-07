package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record PacketSample(String side, String protocolId, String packetId) {
	public static PacketSample fromEvent(RecordedEvent event) {
		return new PacketSample(event.getString("packetDirection"), event.getString("protocolId"), event.getString("packetId"));
	}
}
