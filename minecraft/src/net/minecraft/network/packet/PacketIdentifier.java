package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public record PacketIdentifier<T extends Packet<?>>(NetworkSide side, Identifier id) {
	public String toString() {
		return this.side.getName() + "/" + this.id;
	}
}
