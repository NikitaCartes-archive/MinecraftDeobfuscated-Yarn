package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener> {
	private ItemStack book;
	private boolean signed;
	private Hand hand;

	public BookUpdateC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public BookUpdateC2SPacket(ItemStack book, boolean signed, Hand hand) {
		this.book = book.copy();
		this.signed = signed;
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.book = buf.readItemStack();
		this.signed = buf.readBoolean();
		this.hand = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeItemStack(this.book);
		buf.writeBoolean(this.signed);
		buf.writeEnumConstant(this.hand);
	}

	public void method_12236(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBookUpdate(this);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean wasSigned() {
		return this.signed;
	}

	public Hand getHand() {
		return this.hand;
	}
}
