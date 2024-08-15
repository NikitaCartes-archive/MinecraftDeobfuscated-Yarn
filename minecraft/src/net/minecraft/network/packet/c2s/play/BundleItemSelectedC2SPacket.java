package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record BundleItemSelectedC2SPacket(int slotId, int selectedItemIndex) implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, BundleItemSelectedC2SPacket> CODEC = Packet.createCodec(
		BundleItemSelectedC2SPacket::write, BundleItemSelectedC2SPacket::new
	);

	private BundleItemSelectedC2SPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), buf.readVarInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.slotId);
		buf.writeVarInt(this.selectedItemIndex);
	}

	@Override
	public PacketType<BundleItemSelectedC2SPacket> getPacketId() {
		return PlayPackets.BUNDLE_ITEM_SELECTED;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBundleItemSelected(this);
	}
}
