package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener> {
	private final ItemStack book;
	private final boolean signed;
	private final int slot;

	@Environment(EnvType.CLIENT)
	public BookUpdateC2SPacket(ItemStack book, boolean signed, int slot) {
		this.book = book.copy();
		this.signed = signed;
		this.slot = slot;
	}

	public BookUpdateC2SPacket(PacketByteBuf buf) {
		this.book = buf.readItemStack();
		this.signed = buf.readBoolean();
		this.slot = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeItemStack(this.book);
		buf.writeBoolean(this.signed);
		buf.writeVarInt(this.slot);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBookUpdate(this);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean wasSigned() {
		return this.signed;
	}

	public int getSlot() {
		return this.slot;
	}
}
