package net.minecraft.network.listener;

public interface TickablePacketListener extends PacketListener {
	void tick();
}
