package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class CreativeInventoryActionC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int slot;
	private final ItemStack stack;

	@Environment(EnvType.CLIENT)
	public CreativeInventoryActionC2SPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack.copy();
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCreativeInventoryAction(this);
	}

	public CreativeInventoryActionC2SPacket(PacketByteBuf packetByteBuf) {
		this.slot = packetByteBuf.readShort();
		this.stack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}
}
