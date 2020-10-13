package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener> {
	private ItemStack book;
	private boolean signed;
	private int field_26897;

	public BookUpdateC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public BookUpdateC2SPacket(ItemStack book, boolean signed, int i) {
		this.book = book.copy();
		this.signed = signed;
		this.field_26897 = i;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.book = buf.readItemStack();
		this.signed = buf.readBoolean();
		this.field_26897 = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeItemStack(this.book);
		buf.writeBoolean(this.signed);
		buf.writeVarInt(this.field_26897);
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

	public int method_12235() {
		return this.field_26897;
	}
}
