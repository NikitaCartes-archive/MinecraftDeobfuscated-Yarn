package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record SetCursorItemS2CPacket(ItemStack contents) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, SetCursorItemS2CPacket> CODEC = PacketCodec.tuple(
		ItemStack.OPTIONAL_PACKET_CODEC, SetCursorItemS2CPacket::contents, SetCursorItemS2CPacket::new
	);

	@Override
	public PacketType<SetCursorItemS2CPacket> getPacketId() {
		return PlayPackets.SET_CURSOR_ITEM;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onSetCursorItem(this);
	}
}
