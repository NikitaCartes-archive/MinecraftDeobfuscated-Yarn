package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.PacketByteBuf;

public interface Packet<T extends PacketListener> {
	void read(PacketByteBuf packetByteBuf) throws IOException;

	void write(PacketByteBuf packetByteBuf) throws IOException;

	void apply(T packetListener);

	default boolean isErrorFatal() {
		return false;
	}
}
