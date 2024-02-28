package net.minecraft.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class CreativeInventoryActionC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, CreativeInventoryActionC2SPacket> CODEC = Packet.createCodec(
		CreativeInventoryActionC2SPacket::write, CreativeInventoryActionC2SPacket::new
	);
	private final int slot;
	private final ItemStack stack;

	public CreativeInventoryActionC2SPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack.copy();
	}

	private CreativeInventoryActionC2SPacket(RegistryByteBuf buf) {
		this.slot = buf.readShort();
		this.stack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeShort(this.slot);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, this.stack);
	}

	@Override
	public PacketType<CreativeInventoryActionC2SPacket> getPacketId() {
		return PlayPackets.SET_CREATIVE_MODE_SLOT;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCreativeInventoryAction(this);
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getStack() {
		return this.stack;
	}
}
