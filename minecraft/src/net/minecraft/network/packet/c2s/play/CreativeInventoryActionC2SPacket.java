package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record CreativeInventoryActionC2SPacket(short slot, ItemStack stack) implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, CreativeInventoryActionC2SPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.SHORT,
		CreativeInventoryActionC2SPacket::slot,
		ItemStack.createExtraValidatingPacketCodec(ItemStack.OPTIONAL_PACKET_CODEC),
		CreativeInventoryActionC2SPacket::stack,
		CreativeInventoryActionC2SPacket::new
	);

	public CreativeInventoryActionC2SPacket(int slot, ItemStack stack) {
		this((short)slot, stack);
	}

	@Override
	public PacketType<CreativeInventoryActionC2SPacket> getPacketId() {
		return PlayPackets.SET_CREATIVE_MODE_SLOT;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCreativeInventoryAction(this);
	}
}
